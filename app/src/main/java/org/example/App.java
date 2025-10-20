package org.example;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class App {


    private static Window pWindow;
    private static Camera pCamera;
    private static CharacterRenderer pCharacterRenderer;
    private static PlayerSlotManager pSlotManager;
    private static UIManager pUI;

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

        // キャラクターレンダラー作成
        pCharacterRenderer = new CharacterRenderer(pCamera);

        pUI = new UIManager(pCamera);
        // キャラクターレンダラー
        pCharacterRenderer.Initialize("Image/St001.png");

        pUI.initUI();

                // ゲームパッドが接続されている場合の情報表示
        System.out.println("=== ゲームパッド検出 ===");
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_4; i++) {
            if (InputManager.IsGamepadConnected(i)) {
                System.out.println("ゲームパッド " + i + ": " + InputManager.GetGamepadName(i));
            }
        }

        // プレイヤースロット管理システムを作成
        pSlotManager = new PlayerSlotManager(pWindow);
        
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
    }

    public static void Update(float fDeltaTime) {
        // 更新コード（必要に応じて追加）
        // プレイヤースロット管理の更新（接続/切断の検出）
        pSlotManager.Update(fDeltaTime);

        // 各プレイヤーの入力処理と更新
        for (PlayerSlot pSlot : pSlotManager.GetAllSlots()) {
           if (pSlot.IsOccupied()) {
                InputManager pInputManager = pSlot.GetInputManager();
                Character pCharacter = pSlot.GetCharacter();
                    
                // キャラクター操作
                boolean bLeftMove = pInputManager.GetInput(InputType.LEFT);
                boolean bRightMove = pInputManager.GetInput(InputType.RIGHT);
                boolean bJump = pInputManager.GetInput(InputType.JUMP);
                boolean bDamage = pInputManager.GetInput(InputType.LEFT);   // デバッグ用
                boolean bHeal = pInputManager.GetInput(InputType.RIGHT);   // デバッグ用
                    
                if (bLeftMove) pCharacter.MoveLeft(fDeltaTime);
                if (bRightMove) pCharacter.MoveRight(fDeltaTime);
                if (bJump) pCharacter.Jump();
                if (bDamage) pCharacter.DamageHP(); // デバッグ用
                if (bHeal) pCharacter.HealHP();     // デバッグ用
                    
                // キャラクター更新
                pCharacter.Update(fDeltaTime);
            }
        }
            
        // プレイヤー同士の衝突判定
        // ここで衝突時の処理を追加してください（例：ダメージ、押し出し、エフェクトなど）
        PlayerSlot pSlot1 = pSlotManager.GetSlot(1);
        PlayerSlot pSlot2 = pSlotManager.GetSlot(2);
        if (pSlot1 != null && pSlot2 != null && pSlot1.IsOccupied() && pSlot2.IsOccupied()) {
            Character pChar1 = pSlot1.GetCharacter();
            Character pChar2 = pSlot2.GetCharacter();
                
            // 衝突判定
            if (CollisionManager.CheckCharacterCollision(pChar1, pChar2)) {
                pChar1.OnCollision(pChar2);
                pChar2.OnCollision(pChar1);
            }
        }

        pUI.update(fDeltaTime);
    }

    public static void Draw() {
        // 描画コード（必要に応じて追加）
        // 描画
        GL11.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // 各プレイヤーのキャラクターを描画
        for (PlayerSlot pSlot : pSlotManager.GetAllSlots()) 
        {
            if (pSlot.IsOccupied()) {
                pCharacterRenderer.DrawCharacter(pSlot.GetCharacter());
            }
        }

        pUI.drawUI();

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

            
        }
        // 解放

        Uninit();
    }
}