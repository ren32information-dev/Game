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
        
        this.fKeyboardPressTime = 0f;
        this.fGamepadPressTime = new float[4];
        this.bPrevKeyboardButton = false;
        this.bPrevGamepadButton = new boolean[4];
        this.bKeyboardLongPressHandled = false;
        this.bGamepadLongPressHandled = new boolean[4];
        
        System.out.println("=== プレイヤースロット管理システム起動 ===");
        System.out.println("接続方法:");
        System.out.println("  キーボード: Enterキー");
        System.out.println("  PS5コントローラー: オプションボタン");
        System.out.println("  Switchコントローラー: +ボタン");
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
        
        // ゲームパッドのボタンをチェック
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_4; i++) {
            if (InputManager.IsGamepadConnected(i)) {
                CheckGamepadConnection(i, fDeltaTime);
            }
        }
    }
    
    //キーボードの接続/切断をチェック
    private void CheckKeyboardConnection(float fDeltaTime) {
        boolean bCurrentButton = pWindow.isKeyPressed(GLFW.GLFW_KEY_ENTER);
        
        if (bCurrentButton) {
            // ボタンが押されている
            fKeyboardPressTime += fDeltaTime;
            
            // 長押し判定（1秒以上）
            if (fKeyboardPressTime >= LONG_PRESS_DURATION && !bKeyboardLongPressHandled) {
                // 長押しで切断
                System.out.println("[デバッグ] キーボード長押し検出: " + String.format("%.2f", fKeyboardPressTime) + "秒");
                DisconnectKeyboard();
                bKeyboardLongPressHandled = true;
            }
        } else {
            // ボタンが離された
            if (bPrevKeyboardButton && fKeyboardPressTime < LONG_PRESS_DURATION) {
                // 短押しで接続
                TryConnectKeyboard();
            }
            fKeyboardPressTime = 0f;
            bKeyboardLongPressHandled = false;
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
}

