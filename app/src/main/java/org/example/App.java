package org.example;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 * メインアプリケーションクラス
 * ゲームの初期化、メインループ、終了処理を管理
 */
public class App {
    private static Window pWindow;
    //ウィンドウ
    private static Camera pCamera;
    //カメラ
    private static CharacterRenderer pCharacterRenderer;
    //キャラクター描画
    private static PlayerSlotManager pSlotManager;
    //プレイヤースロット管理

    //初期化処理
    public static void Init() {
        // ウィンドウ作成
        pWindow = new Window();
        pWindow.create(1280, 720, "2D Fighting Game Prototype");

        // カメラ設定（2D格ゲー用に平行投影）
        pCamera = new Camera();
        pCamera.setPerspective(false);
        pCamera.setPosition(0, 5, 15);
        pCamera.lookAt(0, 5, 0);

        // キャラクターレンダラー作成
        pCharacterRenderer = new CharacterRenderer(pCamera);
        pCharacterRenderer.Initialize("Image/St001.png");

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

    //終了処理
    public static void Uninit() {
        if (pWindow != null) {
            pWindow.destroy();
            pWindow = null;
        }
        if (pCharacterRenderer != null) {
            pCharacterRenderer.Release();
            pCharacterRenderer = null;
        }
    }

    //更新処理
    public static void Update(float fDeltaTime) {
        // プレイヤースロット管理の更新（接続/切断の検出）
        pSlotManager.Update(fDeltaTime);

               // 各プレイヤーの更新
               for (PlayerSlot pSlot : pSlotManager.GetAllSlots()) {
                   if (pSlot.IsOccupied()) {
                       Character pCharacter = pSlot.GetCharacter();
                       //CharacterはPlayerを継承しているので、Player型としても扱える
                       pCharacter.Update(fDeltaTime);
                   }
               }
               
        // 両プレイヤーが存在する場合の処理
        PlayerSlot pSlot1 = pSlotManager.GetSlot(1);
        PlayerSlot pSlot2 = pSlotManager.GetSlot(2);
        if (pSlot1 != null && pSlot2 != null && pSlot1.IsOccupied() && pSlot2.IsOccupied()) {
            Character pChar1 = pSlot1.GetCharacter();
            Character pChar2 = pSlot2.GetCharacter();
            
            // 相互に相手を設定
            pChar1.SetOpponentCharacter(pChar2);
            pChar2.SetOpponentCharacter(pChar1);
            
            // カメラ更新（2キャラクターの中間点を注視）
            pCamera.UpdateFightingGameCamera(pChar1, pChar2);
            
            // プレイヤー同士の衝突判定
            if (CollisionManager.CheckCharacterCollision(pChar1, pChar2)) {
                System.out.println("[衝突] プレイヤー同士が衝突しました！");
                // TODO: ここに追加の処理を実装できます（ダメージ、押し出し、エフェクトなど）
            }
        }
    }

    //描画処理
    public static void Draw() {
        GL11.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // 各プレイヤーのキャラクターを描画
        for (PlayerSlot pSlot : pSlotManager.GetAllSlots()) {
            if (pSlot.IsOccupied()) {
                Character pCharacter = pSlot.GetCharacter();
                pCharacterRenderer.DrawCharacter(pCharacter);
            }
        }

        pWindow.update();
    }
    //メイン関数
    public static void main(String[] args) {
        final int MAX_FPS = 60;
        //最大FPS
        
        // 初期化
        Init();

        double dLastTime = GLFW.glfwGetTime();
        //前フレームの時刻

        // メインループ
        while (!pWindow.shouldClose()) {
            double dCurrentTime = GLFW.glfwGetTime();
            //現在の時刻
            float fDeltaTime = (float) (dCurrentTime - dLastTime);
            //デルタタイム
            
            // FPS制限
            if(fDeltaTime < 1.0f / MAX_FPS) continue;
            
            dLastTime = dCurrentTime;

            // 更新と描画
            Update(fDeltaTime);
            Draw();
        }

        // 終了処理
        Uninit();
    }
}