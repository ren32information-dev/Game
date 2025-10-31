package org.example;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import java.util.HashMap;
import java.util.ArrayList;
import org.lwjgl.glfw.GLFWGamepadState;
import java.nio.ByteBuffer;

public class App {


    private static Window pWindow;
    private static Camera pCamera;
    private static CharacterRenderer pCharacterRenderer;
    private static PlayerSlotManager pSlotManager;
    private static UIManager pUI;
    private static GameState CurrentState = GameState.TITLE;
    private static Title pTitleScreen;
    private static CharacterRendererManager pCharacterRendererManager;
    private static Result pResultScreen;

    //sound
    private static String bgmTitle;    // タイトル画面BGM
    private static String bgmGame;     // ゲーム中BGM
    private static String bgmResult;   // リザルト画面BGM


    //勝敗フラグ
    private static boolean bIsP1Winner = false;
    private static boolean bIsP2Winner = false;
    private static boolean bIsDraw = false;

    public static void setGameState(GameState newState) 
    {
    // 状態が切り替わる前に現在のBGMを停止
    Sound.stopBGM();
    CurrentState = newState;

    if (newState == GameState.TITLE) 
    {
        // タイトルBGMを再生
        Sound.playWavWithConversion(bgmTitle); 
    } 
    else if (newState == GameState.INGAME) 
    {
        // ゲーム中BGMを再生
        Sound.playWavWithConversion(bgmGame); 

        // 勝敗フラグをリセット
        bIsP1Winner = false;
        bIsP2Winner = false;
        bIsDraw = false;
    }
    else if (newState == GameState.RESULT) 
    {
        // リザルトBGMを再生
        Sound.playWavWithConversion(bgmResult);
    }
}

    public static void Init() {
        // 初期化コード（必要に応じて追加）
        pWindow = new Window();
        pWindow.create(1280, 720, "2D Fighting Game Prototype");

        // カメラ設定（2D格ゲー用に平行投影）
        pCamera = new Camera();
        // カメラオブジェクト
        pCamera.setPerspective(false); // 平行投影に設定
        pCamera.setPosition(0, 5, 15);
        pCamera.lookAt(0, 5, 0);
        
        pTitleScreen = new Title(1280, 720);
        pTitleScreen.init();
        
        // キャラクターレンダラー作成
        pCharacterRenderer = new CharacterRenderer(pCamera, "Image/St001.png"); 
        pCharacterRendererManager = new CharacterRendererManager(pCamera);   

        //sound
        bgmTitle = "Sound/TitleBGM.wav";
        bgmGame = "Sound/BattleBGM.wav";
        bgmResult = "Sound/EndingBGM.wav"; 

        // ゲームパッドが接続されている場合の情報表示
        System.out.println("=== ゲームパッド検出 ===");
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_4; i++) {
            if (InputManager.IsGamepadConnected(i)) {
                System.out.println("ゲームパッド " + i + ": " + InputManager.GetGamepadName(i));
            }
        }

        Sound.playWavWithConversion(bgmTitle); 

        // プレイヤースロット管理システムを作成
        pSlotManager = new PlayerSlotManager(pWindow);
        pSlotManager.SetCamera(pCamera); // カメラを設定（境界チェック用）
        PlayerSlot slot1 = pSlotManager.GetSlot(1);
        PlayerSlot slot2 = pSlotManager.GetSlot(2);

        if (slot1 != null) 
        {
            pUI = new UIManager(pCamera, slot1.GetCharacter(), slot2.GetCharacter());
        }
        else 
        {
           pUI = new UIManager(pCamera, null, null);
        }
        pUI.initUI();

        pResultScreen = new Result(1280, 720);
        pResultScreen.init();
    }

    public static void Uninit() {
        // 解放コード（必要に応じて追加）
        if (pWindow != null) {
            pWindow.destroy();
            pWindow = null;
        }
        if (pCharacterRenderer != null) {
            pCharacterRenderer.Release();
            pCharacterRenderer = null;
        }
        if (pUI != null) {
            pUI.release();
            pUI = null;
        }
        if (pTitleScreen != null) {
            pTitleScreen.release();
            pTitleScreen = null;
        }
        if (pCharacterRendererManager != null) {
            pCharacterRendererManager.Uninit();
            pCharacterRendererManager = null;
        }
         if (pResultScreen != null) {
            pResultScreen.release();
            pResultScreen = null;
        }
    }

    public static void Update(float fDeltaTime) 
    {

        // 更新コード（必要に応じて追加）
        // プレイヤースロット管理の更新（接続/切断の検出）
        pSlotManager.Update(fDeltaTime);

        //タイトル
        if(CurrentState == GameState.TITLE)
        {
            pTitleScreen.update(fDeltaTime);
            if (pWindow.isKeyPressed(GLFW.GLFW_KEY_ENTER)|| isAnyGamepadButtonPressed()) 
            {
                setGameState(GameState.PLAYGUIDE);
            } 
            else if (pWindow.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) 
            {
                GLFW.glfwSetWindowShouldClose(pWindow.getHandle(), true);
            }
        }

        //プレイガイド
        else if (CurrentState == GameState.PLAYGUIDE) 
        {
            //デバッグ用にCキーでINGAMEへ
            if (pWindow.isKeyPressed(GLFW.GLFW_KEY_ENTER)|| isAnyGamepadButtonPressed()) 
            {
                 setGameState(GameState.INGAME);
            }
        }
        
        //ゲーム中
        else if (CurrentState == GameState.INGAME) 
        {
            // デバッグ用にNキーでリザルト画面へ遷移
            if (pWindow.isKeyPressed(GLFW.GLFW_KEY_N)) {
                // デバッグ用にP1勝利を設定して遷移
                pResultScreen.setResult(true, false, false); 
                setGameState(GameState.RESULT);
                return; // 以降の処理をスキップ
            }
             // 各プレイヤーの入力処理と更新
            for (PlayerSlot pSlot : pSlotManager.GetAllSlots()) 
            {
                if (pSlot.IsOccupied()) 
                {
                     Character pCharacter = pSlot.GetCharacter();
                    
                    // 入力が有効な場合のみ手動操作を処理
                    if (pSlot.IsInputEnabled())
                    {
                        InputManager pInputManager = pSlot.GetInputManager();
                        
                        // キャラクター操作
                        boolean bLeftMove = pInputManager.GetInput(InputType.LEFT);
                        boolean bRightMove = pInputManager.GetInput(InputType.RIGHT);
                        boolean bJump = pInputManager.GetInput(InputType.JUMP);
                        boolean bDamage = pInputManager.GetInput(InputType.LEFT);   // デバッグ用
                        boolean bHeal = pInputManager.GetInput(InputType.RIGHT);   // デバッグ用
                        

                    }
                    
                    // キャラクター更新（入力無効でも物理演算などは動く）
                    pCharacter.Update(fDeltaTime);
                }
            }
            
            // プレイヤー同士の衝突判定
            PlayerSlot pSlot1 = pSlotManager.GetSlot(1);
            PlayerSlot pSlot2 = pSlotManager.GetSlot(2);
            if (pSlot1 != null && pSlot2 != null && pSlot1.IsOccupied() && pSlot2.IsOccupied()) 
            {
                Character pChar1 = pSlot1.GetCharacter();
                Character pChar2 = pSlot2.GetCharacter();
                
                // 衝突判定
                CollisionManager.CheckCharacterCollision(pChar1, pChar2);
                
                // カメラを2キャラクターの中間点に向ける（距離に応じてズーム）
                pCamera.UpdateFightingGameCamera(pChar1, pChar2);

                // 勝敗判定

                boolean bP1Dead = pChar1.IsDie();
                boolean bP2Dead = pChar2.IsDie();

                if (bP1Dead || bP2Dead)
                {
                    if(bP1Dead)
                    {
                        bIsP2Winner = true;
                    }
                    else if(bP2Dead)
                    {
                        bIsP1Winner = true;
                    }
                    else
                    {
                        bIsDraw = true;
                    }
                    pResultScreen.setResult(bIsP1Winner, bIsP2Winner, bIsDraw);
                    setGameState(GameState.RESULT);
                }
            }
            
            // UI更新
            if (pSlot1 != null && pSlot1.IsOccupied()) {
                Character playerCharacter = pSlot1.GetCharacter();
                pUI.SetPlayerCharacter(playerCharacter);
            }
            pUI.update(fDeltaTime);
        } 

        //リザルト画面
        else if (CurrentState == GameState.RESULT) 
        {
            //リザルト画面の更新処理
        
            // 任意のキーが押されたら CHARACTERSELECT へ戻る
            if (pWindow.isKeyPressed(GLFW.GLFW_KEY_ENTER)|| isAnyGamepadButtonPressed()) 
            {
                setGameState(GameState.PLAYGUIDE);
            }
        }
    }

    public static void Draw() {
        // 描画コード（必要に応じて追加）
        // 描画
        GL11.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // タイトル画面の描画
        if (CurrentState == GameState.TITLE) 
        {
            pTitleScreen.draw();
        }

        // プレイガイド画面の描画
        else if (CurrentState == GameState.PLAYGUIDE)
        {
             //操作説明の画像を表示
        }

        // ゲーム中の描画
        else if (CurrentState == GameState.INGAME) 
        {

            // 描画
            GL11.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            
            pUI.drawUI();
            //各プレイヤーのキャラクターを描画
            for (PlayerSlot pSlot : pSlotManager.GetAllSlots()) {
                if (pSlot.IsOccupied()) {
                    pCharacterRendererManager.DrawCharacter(pSlot.GetCharacter(), pSlot.GetCharacter().GetTexture());
                }
            }

            PlayerSlot pSlot1 = pSlotManager.GetSlot(1);
            PlayerSlot pSlot2 = pSlotManager.GetSlot(2);
            if (pSlot1 != null && pSlot2 != null && pSlot1.IsOccupied() && pSlot2.IsOccupied()) {
                Character pChar1 = pSlot1.GetCharacter();
                Character pChar2 = pSlot2.GetCharacter();

                //カメラを2キャラクターの中間点に向ける（距離に応じてズーム）
                pCamera.UpdateFightingGameCamera(pChar1, pChar2);
            }
        }
        // Result画面の描画
        else if (CurrentState == GameState.RESULT) {
            if (pResultScreen != null) {
                pResultScreen.draw();
            }
        }
        pWindow.update();
    }
    public static void main(String[] args) {
        final int MAX_FPS = 60; // 最大FPS

        Init();

        double dLastTime = org.lwjgl.glfw.GLFW.glfwGetTime();
        //前フレームの時刻

        while (!pWindow.shouldClose()) {
            double dCurrentTime = org.lwjgl.glfw.GLFW.glfwGetTime();
            //現在の時刻
            float fDeltaTime = (float) (dCurrentTime - dLastTime);
            //デルタタイム
            
            // FPS制限
            if(fDeltaTime < 1.0f / MAX_FPS) continue;
            
            dLastTime = dCurrentTime;

            Update(fDeltaTime);
            Draw();

            

            //pWindow.update();
        }
        // 解放

        Uninit();
    }

    // コントローラーの決定ボタン（●ボタン）が押されたかチェック
    private static boolean isAnyGamepadButtonPressed() {
        // 接続されている全てのコントローラーをチェック（JOYSTICK_1〜4）
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_4; i++) {
            if (GLFW.glfwJoystickPresent(i)) {
                // Gamepad API対応コントローラー（XInput、一部のコントローラー）
                if (GLFW.glfwJoystickIsGamepad(i)) {
                    GLFWGamepadState state = GLFWGamepadState.create();
                    if (GLFW.glfwGetGamepadState(i, state)) {
                        // Bボタン（Switch: A、PS: ○、Xbox: B）
                        if (state.buttons(GLFW.GLFW_GAMEPAD_BUTTON_B) == GLFW.GLFW_PRESS) {
                            return true;
                        }
                        // Aボタン（Switch: B、PS: ×、Xbox: A）
                        if (state.buttons(GLFW.GLFW_GAMEPAD_BUTTON_A) == GLFW.GLFW_PRESS) {
                            return true;
                        }
                        // STARTボタンも追加
                        if (state.buttons(GLFW.GLFW_GAMEPAD_BUTTON_START) == GLFW.GLFW_PRESS) {
                            return true;
                        }
                    }
                } else {
                    // DirectInput（Switch純正、PS4など）
                    ByteBuffer buttons = GLFW.glfwGetJoystickButtons(i);
                    if (buttons != null && buttons.capacity() > 0) {
                        // ボタン0（通常は決定ボタン）
                        if (buttons.get(0) == GLFW.GLFW_PRESS) {
                            return true;
                        }
                        // ボタン1
                        if (buttons.capacity() > 1 && buttons.get(1) == GLFW.GLFW_PRESS) {
                            return true;
                        }
                        // ボタン9（STARTボタン、Switch純正プロコン）
                        if (buttons.capacity() > 9 && buttons.get(9) == GLFW.GLFW_PRESS) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

   
}