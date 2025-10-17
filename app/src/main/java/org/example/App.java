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

        // キャラクター作成
        Character pCharacter = new Character(-3f, 0f, 0f);
        //キャラクターオブジェクト
        CharacterRenderer pCharacterRenderer = new CharacterRenderer(pCamera);
        //キャラクターレンダラー
        pCharacterRenderer.Initialize("app/Image/St001.png");

        // 入力マネージャー作成（プレイヤー1: キーボード）
        InputManager pInputManager1 = new InputManager(pWindow, 1);
        //プレイヤー1の入力マネージャー

        // ゲームパッドが接続されている場合の情報表示
        System.out.println("=== ゲームパッド検出 ===");
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_4; i++) {
            if (InputManager.IsGamepadConnected(i)) {
                System.out.println("ゲームパッド " + i + ": " + InputManager.GetGamepadName(i));
            }
        }

        double dLastTime = org.lwjgl.glfw.GLFW.glfwGetTime();
        //前フレームの時刻
        
        boolean bPreviousEnterKey = false;
        //前フレームのEnterキー状態

        while (!pWindow.shouldClose()) {
            double dCurrentTime = org.lwjgl.glfw.GLFW.glfwGetTime();
            //現在の時刻
            float fDeltaTime = (float) (dCurrentTime - dLastTime);
            //デルタタイム
            
            // FPS制限
            if(fDeltaTime < 1.0f / MAX_FPS) continue;
            
            dLastTime = dCurrentTime;

            // 入力デバイス切り替え（Enterキー）
            boolean bCurrentEnterKey = pWindow.isKeyPressed(GLFW.GLFW_KEY_ENTER);
            //現在のEnterキー状態
            if (bCurrentEnterKey && !bPreviousEnterKey) {
                // Enterキーが押された瞬間
                pInputManager1.SwitchInputDevice();
            }
            bPreviousEnterKey = bCurrentEnterKey;

            // キャラクター操作（InputManager経由）
            boolean bLeftMove = pInputManager1.GetInput(InputType.LEFT);
            //左移動入力
            boolean bRightMove = pInputManager1.GetInput(InputType.RIGHT);
            //右移動入力
            boolean bJump = pInputManager1.GetInput(InputType.JUMP);
            //ジャンプ入力

            if (bLeftMove) pCharacter.MoveLeft(fDeltaTime);
            if (bRightMove) pCharacter.MoveRight(fDeltaTime);
            if (bJump) pCharacter.Jump();

            // カメラ操作（矢印キー）
            boolean bUpKey = pWindow.isKeyPressed(GLFW.GLFW_KEY_UP);
            //上キー
            boolean bDownKey = pWindow.isKeyPressed(GLFW.GLFW_KEY_DOWN);
            //下キー
            boolean bLeftKey = pWindow.isKeyPressed(GLFW.GLFW_KEY_LEFT);
            //左キー
            boolean bRightKey = pWindow.isKeyPressed(GLFW.GLFW_KEY_RIGHT);
            //右キー

            pCamera.MoveCamera(bUpKey, bDownKey, bLeftKey, bRightKey, fDeltaTime);

            // 更新
            pCharacter.Update(fDeltaTime);

            // 描画
            GL11.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            pCharacterRenderer.DrawCharacter(pCharacter);

            pWindow.update();
        }

        pCharacterRenderer.Release();
        pWindow.destroy();
    }
}