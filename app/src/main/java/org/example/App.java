package org.example;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class App {
    public static void main(String[] args) {
        final int MAX_FPS = 60; // 最大FPS
        Window pWindow = new Window();
        //ウィンドウオブジェクト
        pWindow.create(1280, 720, "2D Fighting Game Prototype");

        // カメラ設定（2D格ゲー用に平行投影）
        Camera pCamera = new Camera();
        //カメラオブジェクト
        pCamera.setPerspective(false); // 平行投影に設定
        pCamera.setPosition(0, 5, 15);
        pCamera.lookAt(0, 5, 0);

        // キャラクターレンダラー作成
        CharacterRenderer pCharacterRenderer = new CharacterRenderer(pCamera);
        //キャラクターレンダラー
        pCharacterRenderer.Initialize("app/Image/St001.png");

        // ゲームパッドが接続されている場合の情報表示
        System.out.println("=== ゲームパッド検出 ===");
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_4; i++) {
            if (InputManager.IsGamepadConnected(i)) {
                System.out.println("ゲームパッド " + i + ": " + InputManager.GetGamepadName(i));
            }
        }

        // プレイヤースロット管理システムを作成
        PlayerSlotManager pSlotManager = new PlayerSlotManager(pWindow);
        //プレイヤースロット管理システム

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
                    
                    if (bLeftMove) pCharacter.MoveLeft(fDeltaTime);
                    if (bRightMove) pCharacter.MoveRight(fDeltaTime);
                    if (bJump) pCharacter.Jump();
                    
                    // キャラクター更新
                    pCharacter.Update(fDeltaTime);
                }
            }
            
            // 格闘ゲーム用カメラ更新と衝突判定（両プレイヤーが接続されている場合）
            PlayerSlot pSlot1 = pSlotManager.GetSlot(1);
            PlayerSlot pSlot2 = pSlotManager.GetSlot(2);
            if (pSlot1 != null && pSlot2 != null && pSlot1.IsOccupied() && pSlot2.IsOccupied()) {
                Character pChar1 = pSlot1.GetCharacter();
                Character pChar2 = pSlot2.GetCharacter();
                
                // カメラを2キャラクターの中間点に向ける（距離に応じてズーム）
                pCamera.UpdateFightingGameCamera(pChar1, pChar2);
                
                // プレイヤー同士の衝突判定
                if (CollisionManager.CheckCharacterCollision(pChar1, pChar2)) {
                    // 衝突を検出したことを表示
                    System.out.println("[衝突] プレイヤー同士が衝突しました！");
                    // TODO: ここに追加の処理を実装できます（ダメージ、押し出し、エフェクトなど）
                }
            }

            // 描画
            GL11.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // 各プレイヤーのキャラクターを描画
            for (PlayerSlot pSlot : pSlotManager.GetAllSlots()) {
                if (pSlot.IsOccupied()) {
                    pCharacterRenderer.DrawCharacter(pSlot.GetCharacter());
                }
            }

            pWindow.update();
        }

        pCharacterRenderer.Release();
        pWindow.destroy();
    }
}
