package org.example;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

/**
 * プレイヤースロット（1P、2P）を管理するクラス
 */
public class PlayerSlotManager {
    private Window pWindow;
    //ウィンドウオブジェクト
    private Camera pCamera;
    //カメラオブジェクト（境界チェック用）
    private PlayerSlot[] pSlots;
    //プレイヤースロット配列
    
    private CPUInputManager[] pCPUManagers;
    //CPU入力マネージャー配列（各プレイヤーごと）
    
    // 長押し検出用
    private static final float LONG_PRESS_DURATION = 1.0f;
    //長押し判定時間（秒）
    private float fKeyboardPressTime;
    //キーボードの押下時間
    private float[] fGamepadPressTime;
    //ゲームパッドの押下時間（最大4つ）
    
    // 前フレームのボタン状態
    private boolean bPrevKeyboardButton;
    //前フレームのキーボードボタン状態
    private boolean[] bPrevGamepadButton;
    //前フレームのゲームパッドボタン状態
    private boolean bPrevTabKey;
    //前フレームのTabキー状態
    
    // 長押し切断済みフラグ
    private boolean bKeyboardLongPressHandled;
    //キーボードの長押し切断を処理済みか
    private boolean[] bGamepadLongPressHandled;
    //ゲームパッドの長押し切断を処理済みか
    
    public PlayerSlotManager(Window pWindow) {
        this.pWindow = pWindow;
        this.pSlots = new PlayerSlot[2];
        this.pSlots[0] = new PlayerSlot(1);
        this.pSlots[1] = new PlayerSlot(2);
        
        this.pCPUManagers = new CPUInputManager[2];
        this.fKeyboardPressTime = 0f;
        this.fGamepadPressTime = new float[4];
        this.bPrevKeyboardButton = false;
        this.bPrevGamepadButton = new boolean[4];
        this.bPrevTabKey = false;
        this.bKeyboardLongPressHandled = false;
        this.bGamepadLongPressHandled = new boolean[4];
        
        System.out.println("=== プレイヤースロット管理システム起動 ===");
        System.out.println("接続方法:");
        System.out.println("  キーボード: Enterキー");
        System.out.println("  PS5コントローラー: オプションボタン");
        System.out.println("  Switchコントローラー: +ボタン");
        System.out.println("CPU切り替え: Tabキー");
        System.out.println("切断方法: 各ボタンを1秒長押し");
        System.out.println("========================================");
        
        // 自動的に2体のプレイヤーを作成（表示のみ）
        AutoConnectPlayers();
    }
    
    //カメラを設定（App.javaから呼び出す）
    public void SetCamera(Camera pCamera) {
        this.pCamera = pCamera;
        
        // 既存のキャラクターにもカメラを設定
        for (PlayerSlot pSlot : pSlots) {
            if (pSlot.IsOccupied()) {
                pSlot.GetCharacter().SetCamera(pCamera);
            }
        }
        System.out.println("[PlayerSlotManager] カメラを設定しました");
    }
    
    //自動的にプレイヤーを接続（表示のみ、入力は後で接続）
    private void AutoConnectPlayers() {
        // プレイヤー1を表示のみで作成
        pSlots[0].ConnectDisplayOnly();
        
        // プレイヤー2を表示のみで作成
        pSlots[1].ConnectDisplayOnly();
        
        // 相手キャラクターの設定
        if (pSlots[0].IsOccupied() && pSlots[1].IsOccupied()) {
            Character pChar1 = pSlots[0].GetCharacter();
            Character pChar2 = pSlots[1].GetCharacter();
            pChar1.SetOpponentCharacter(pChar2);
            pChar2.SetOpponentCharacter(pChar1);
            
            // カメラが設定されていればキャラクターにも設定
            if (pCamera != null) {
                pChar1.SetCamera(pCamera);
                pChar2.SetCamera(pCamera);
            }
        }
        
        System.out.println("[自動接続] 2体のプレイヤーを作成しました（表示のみ、入力待機中）");
        System.out.println("  - キーボードまたはコントローラーを接続して操作を開始してください");
    }
    
    //更新処理（毎フレーム呼び出す）
    public void Update(float fDeltaTime) {
        // キーボードのEnterキーをチェック
        CheckKeyboardConnection(fDeltaTime);
        
        // TabキーでCPU切り替え
        CheckCPUToggle();
        
        // ゲームパッドのボタンをチェック
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_4; i++) {
            if (InputManager.IsGamepadConnected(i)) {
                CheckGamepadConnection(i, fDeltaTime);
            }
        }
        
        // CPUの思考を更新
        UpdateCPUs(fDeltaTime);
    }
    
    //キーボードの接続/切断をチェック（Enterキーでトグル）
    private void CheckKeyboardConnection(float fDeltaTime) {
        boolean bCurrentButton = pWindow.isKeyPressed(GLFW.GLFW_KEY_ENTER);
        
        // ボタンが押された瞬間を検出（前フレーム：離されていた、今フレーム：押されている）
        if (bCurrentButton && !bPrevKeyboardButton) {
            // Enterキーが押された瞬間
            
            // P1がキーボードで接続されているか確認
            boolean bP1KeyboardConnected = false;
            if (pSlots[0].IsOccupied() && pSlots[0].GetInputManager() != null && !pSlots[0].GetInputManager().IsUsingGamepad()) {
                bP1KeyboardConnected = true;
            }
            
            if (bP1KeyboardConnected) {
                // 既にP1にキーボードが接続されている → 切断
                pSlots[0].Disconnect();
                UpdateOpponentReferences();
                System.out.println("[トグル] P1のキーボード入力を切断しました");
            } else {
                // P1にキーボードが接続されていない → 接続を試みる
                TryConnectKeyboard();
            }
        }
        
        bPrevKeyboardButton = bCurrentButton;
    }
    
    //ゲームパッドの接続/切断をチェック
    private void CheckGamepadConnection(int nGamepadId, float fDeltaTime) {
        int nIndex = nGamepadId - GLFW.GLFW_JOYSTICK_1;
        
        GLFWGamepadState pState = GLFWGamepadState.create();
        if (!GLFW.glfwGetGamepadState(nGamepadId, pState)) {
            return;
        }
        
        // PS5のオプションボタン = GLFW_GAMEPAD_BUTTON_BACK
        // Switchの+ボタン = GLFW_GAMEPAD_BUTTON_START
        // 両方チェック（どちらかが押されていればOK）
        boolean bCurrentButton = 
            pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_START) == GLFW.GLFW_PRESS ||
            pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_BACK) == GLFW.GLFW_PRESS;
        
        if (bCurrentButton) {
            // ボタンが押されている
            fGamepadPressTime[nIndex] += fDeltaTime;
            
            // 長押し判定（1秒以上）
            if (fGamepadPressTime[nIndex] >= LONG_PRESS_DURATION && !bGamepadLongPressHandled[nIndex]) {
                // 長押しで切断
                System.out.println("[デバッグ] ゲームパッド長押し検出: " + String.format("%.2f", fGamepadPressTime[nIndex]) + "秒");
                DisconnectGamepad(nGamepadId);
                bGamepadLongPressHandled[nIndex] = true;
            }
        } else {
            // ボタンが離された
            if (bPrevGamepadButton[nIndex] && fGamepadPressTime[nIndex] < LONG_PRESS_DURATION) {
                // 短押しで接続
                TryConnectGamepad(nGamepadId);
            }
            fGamepadPressTime[nIndex] = 0f;
            bGamepadLongPressHandled[nIndex] = false;
        }
        
        bPrevGamepadButton[nIndex] = bCurrentButton;
    }
    
    //キーボードで接続を試みる
    private void TryConnectKeyboard() {
        // すでにキーボードが接続されているか確認
        for (PlayerSlot pSlot : pSlots) {
            if (pSlot.IsOccupied() && pSlot.GetInputManager() != null && !pSlot.GetInputManager().IsUsingGamepad()) {
                System.out.println("[警告] キーボードは既に接続されています");
                return;
            }
        }
        
        // 入力が無効なスロットを探す（表示のみのプレイヤー）
        for (PlayerSlot pSlot : pSlots) {
            if (pSlot.IsOccupied() && !pSlot.IsInputEnabled()) {
                InputManager pInputManager = new InputManager(pWindow, pSlot.GetSlotNumber());
                pSlot.AttachInputManager(pInputManager);
                
                // カメラが設定されていればキャラクターにも設定
                if (pCamera != null) {
                    pSlot.GetCharacter().SetCamera(pCamera);
                }
                
                // 相手キャラクターの設定を更新
                UpdateOpponentReferences();
                return;
            }
        }
        
        // 空いているスロットを探す
        for (PlayerSlot pSlot : pSlots) {
            if (!pSlot.IsOccupied()) {
                InputManager pInputManager = new InputManager(pWindow, pSlot.GetSlotNumber());
                pSlot.Connect(pInputManager);
                
                // カメラが設定されていればキャラクターにも設定
                if (pCamera != null) {
                    pSlot.GetCharacter().SetCamera(pCamera);
                }
                
                // 相手キャラクターの設定を更新
                UpdateOpponentReferences();
                return;
            }
        }
        
        // スロットがいっぱい
        System.out.println("[警告] プレイヤーがいっぱいです（キーボード）");
    }
    
    //ゲームパッドで接続を試みる
    private void TryConnectGamepad(int nGamepadId) {
        // このゲームパッドが既に接続されているか確認
        for (PlayerSlot pSlot : pSlots) {
            if (pSlot.IsOccupied() && pSlot.GetInputManager() != null && pSlot.GetInputManager().IsUsingGamepad()) {
                if (pSlot.GetInputManager().GetGamepadId() == nGamepadId) {
                    System.out.println("[警告] このゲームパッドは既に接続されています");
                    return;
                }
            }
        }
        
        // 入力が無効なスロットを探す（表示のみのプレイヤー）
        for (PlayerSlot pSlot : pSlots) {
            if (pSlot.IsOccupied() && !pSlot.IsInputEnabled()) {
                InputManager pInputManager = new InputManager(pWindow, pSlot.GetSlotNumber(), nGamepadId);
                pSlot.AttachInputManager(pInputManager);
                
                // カメラが設定されていればキャラクターにも設定
                if (pCamera != null) {
                    pSlot.GetCharacter().SetCamera(pCamera);
                }
                
                // 相手キャラクターの設定を更新
                UpdateOpponentReferences();
                return;
            }
        }
        
        // 空いているスロットを探す
        for (PlayerSlot pSlot : pSlots) {
            if (!pSlot.IsOccupied()) {
                InputManager pInputManager = new InputManager(pWindow, pSlot.GetSlotNumber(), nGamepadId);
                pSlot.Connect(pInputManager);
                
                // カメラが設定されていればキャラクターにも設定
                if (pCamera != null) {
                    pSlot.GetCharacter().SetCamera(pCamera);
                }
                
                // 相手キャラクターの設定を更新
                UpdateOpponentReferences();
                return;
            }
        }
        
        // スロットがいっぱい
        System.out.println("[警告] プレイヤーがいっぱいです（" + InputManager.GetGamepadName(nGamepadId) + "）");
    }
    
    //キーボードを切断
    private void DisconnectKeyboard() {
        for (PlayerSlot pSlot : pSlots) {
            if (pSlot.IsOccupied() && pSlot.GetInputManager() != null && !pSlot.GetInputManager().IsUsingGamepad()) {
                pSlot.Disconnect();
                
                // 相手キャラクターの設定を更新（プレイヤーは表示のみで残る）
                UpdateOpponentReferences();
                return;
            }
        }
    }
    
    //ゲームパッドを切断
    private void DisconnectGamepad(int nGamepadId) {
        for (PlayerSlot pSlot : pSlots) {
            if (pSlot.IsOccupied() && pSlot.GetInputManager() != null && pSlot.GetInputManager().IsUsingGamepad()) {
                if (pSlot.GetInputManager().GetGamepadId() == nGamepadId) {
                    pSlot.Disconnect();
                    
                    // 相手キャラクターの設定を更新（プレイヤーは表示のみで残る）
                    UpdateOpponentReferences();
                    return;
                }
            }
        }
    }
    
    //スロットを取得
    public PlayerSlot GetSlot(int nSlotNumber) {
        if (nSlotNumber >= 1 && nSlotNumber <= 2) {
            return pSlots[nSlotNumber - 1];
        }
        return null;
    }
    
    //すべてのスロットを取得
    public PlayerSlot[] GetAllSlots() {
        return pSlots;
    }
    
    //相手キャラクターへの参照を更新
    private void UpdateOpponentReferences() {
        if (pSlots[0].IsOccupied() && pSlots[1].IsOccupied()) {
            Character pChar1 = pSlots[0].GetCharacter();
            Character pChar2 = pSlots[1].GetCharacter();
            pChar1.SetOpponentCharacter(pChar2);
            pChar2.SetOpponentCharacter(pChar1);
        } else {
            // どちらかが切断されている場合は参照をnullにする
            if (pSlots[0].IsOccupied()) {
                pSlots[0].GetCharacter().SetOpponentCharacter(null);
            }
            if (pSlots[1].IsOccupied()) {
                pSlots[1].GetCharacter().SetOpponentCharacter(null);
            }
        }
    }
    
    //Tabキーでのプレイヤー→CPU切り替えをチェック
    private void CheckCPUToggle() {
        boolean bCurrentTabKey = pWindow.isKeyPressed(GLFW.GLFW_KEY_TAB);
        
        // Tabキーが押された瞬間を検出
        if (bCurrentTabKey && !bPrevTabKey) {
            // 空いているスロット（入力が無効なスロット）を探してCPUに切り替え
            boolean bToggled = false;
            
            for (int i = 0; i < pSlots.length; i++) {
                PlayerSlot pSlot = pSlots[i];
                
                // スロットが占有されていない場合はスキップ
                if (!pSlot.IsOccupied()) {
                    continue;
                }
                
                // 既にCPUの場合は人間に戻す（空きに戻す）
                if (pCPUManagers[i] != null) {
                    System.out.println("[CPU→空き] プレイヤー" + (i + 1) + "のCPU制御を解除しました（入力待機状態）");
                    pSlot.Disconnect();
                    pCPUManagers[i] = null;
                    bToggled = true;
                    break;
                }
                
                // 入力が有効な場合はスキップ（人間が操作中）
                if (pSlot.IsInputEnabled()) {
                    continue;
                }
                
                // 空いている（入力が無効）場合はCPUに切り替え
                CPUInputManager pCPU = new CPUInputManager(pWindow, pSlot.GetSlotNumber());
                pCPU.SetSelfCharacter(pSlot.GetCharacter());
                
                // 相手キャラクターを設定
                int nOpponentIndex = (i == 0) ? 1 : 0;
                if (pSlots[nOpponentIndex].IsOccupied()) {
                    pCPU.SetOpponentCharacter(pSlots[nOpponentIndex].GetCharacter());
                }
                
                // CPUマネージャーをスロットに設定
                pSlot.AttachInputManager(pCPU);
                pCPUManagers[i] = pCPU;
                
                System.out.println("[空き→CPU] プレイヤー" + (i + 1) + "をCPU制御に切り替えました");
                bToggled = true;
                break;
            }
            
            if (!bToggled) {
                System.out.println("[CPU切り替え] 空いているプレイヤースロットがありません（両方とも人間またはCPU操作中）");
            }
        }
        
        bPrevTabKey = bCurrentTabKey;
    }
    
    //CPUの思考を更新
    private void UpdateCPUs(float fDeltaTime) {
        for (int i = 0; i < pCPUManagers.length; i++) {
            if (pCPUManagers[i] != null) {
                pCPUManagers[i].Update(fDeltaTime);
            }
        }
    }
}

