package org.example;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class InputManager {
    private Window pWindow;
    //ウィンドウオブジェクト
    private int nPlayerNumber;
    //プレイヤー番号（1 or 2）
    private boolean bUseGamepad;
    //ゲームパッドを使用するか
    private int nGamepadId;
    //ゲームパッドID（GLFW_JOYSTICK_1など）
    
    // キーボード設定（プレイヤー1）
    private int nKeyLeft1 = GLFW.GLFW_KEY_A;
    //プレイヤー1左キー
    private int nKeyRight1 = GLFW.GLFW_KEY_D;
    //プレイヤー1右キー
    private int nKeyJump1 = GLFW.GLFW_KEY_W;
    //プレイヤー1ジャンプキー
    private int nKeyAttack1 = GLFW.GLFW_KEY_J;
    //プレイヤー1攻撃キー
    private int nKeyGuard1 = GLFW.GLFW_KEY_K;
    //プレイヤー1ガードキー
    
    // キーボード設定（プレイヤー2）
    private int nKeyLeft2 = GLFW.GLFW_KEY_LEFT;
    //プレイヤー2左キー
    private int nKeyRight2 = GLFW.GLFW_KEY_RIGHT;
    //プレイヤー2右キー
    private int nKeyJump2 = GLFW.GLFW_KEY_UP;
    //プレイヤー2ジャンプキー
    private int nKeyAttack2 = GLFW.GLFW_KEY_KP_1;
    //プレイヤー2攻撃キー（テンキー1）
    private int nKeyGuard2 = GLFW.GLFW_KEY_KP_2;
    //プレイヤー2ガードキー（テンキー2）
    
    //コンストラクタ（キーボード用）
    public InputManager(Window pWindow, int nPlayerNumber) {
        this.pWindow = pWindow;
        this.nPlayerNumber = nPlayerNumber;
        this.bUseGamepad = false;
        this.nGamepadId = -1;
    }
    
    //コンストラクタ（ゲームパッド用）
    public InputManager(Window pWindow, int nPlayerNumber, int nGamepadId) {
        this.pWindow = pWindow;
        this.nPlayerNumber = nPlayerNumber;
        this.bUseGamepad = true;
        this.nGamepadId = nGamepadId;
    }
    
    //入力を取得
    public boolean GetInput(InputType eInputType) {
        if (bUseGamepad) {
            return GetGamepadInput(eInputType);
        } else {
            return GetKeyboardInput(eInputType);
        }
    }
    
    //キーボード入力を取得
    private boolean GetKeyboardInput(InputType eInputType) {
        if (nPlayerNumber == 1) {
            switch (eInputType) {
                case LEFT:
                    return pWindow.isKeyPressed(nKeyLeft1);
                case RIGHT:
                    return pWindow.isKeyPressed(nKeyRight1);
                case JUMP:
                    return pWindow.isKeyPressed(nKeyJump1);
                case ATTACK:
                    return pWindow.isKeyPressed(nKeyAttack1);
                case GUARD:
                    return pWindow.isKeyPressed(nKeyGuard1);
                default:
                    return false;
            }
        } else if (nPlayerNumber == 2) {
            switch (eInputType) {
                case LEFT:
                    return pWindow.isKeyPressed(nKeyLeft2);
                case RIGHT:
                    return pWindow.isKeyPressed(nKeyRight2);
                case JUMP:
                    return pWindow.isKeyPressed(nKeyJump2);
                case ATTACK:
                    return pWindow.isKeyPressed(nKeyAttack2);
                case GUARD:
                    return pWindow.isKeyPressed(nKeyGuard2);
                default:
                    return false;
            }
        }
        return false;
    }
    
    //ゲームパッド入力を取得
    private boolean GetGamepadInput(InputType eInputType) {
        // ゲームパッドが接続されているか確認
        if (!GLFW.glfwJoystickPresent(nGamepadId)) {
            return false;
        }
        
        // ゲームパッドがGamepad APIに対応しているか確認
        if (!GLFW.glfwJoystickIsGamepad(nGamepadId)) {
            return false;
        }
        
        GLFWGamepadState pState = GLFWGamepadState.create();
        if (!GLFW.glfwGetGamepadState(nGamepadId, pState)) {
            return false;
        }
        
        switch (eInputType) {
            case LEFT:
                // 左スティックまたは十字キー左
                return pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) < -0.5f || 
                       pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT) == GLFW.GLFW_PRESS;
            case RIGHT:
                // 右スティックまたは十字キー右
                return pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) > 0.5f || 
                       pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) == GLFW.GLFW_PRESS;
            case JUMP:
                // 上スティックまたは十字キー上
                return pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y) < -0.5f || 
                       pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP) == GLFW.GLFW_PRESS;
            case ATTACK:
                // Aボタン（Switch: B、PS: ×）
                return pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_A) == GLFW.GLFW_PRESS;
            case GUARD:
                // Bボタン（Switch: A、PS: ○）
                return pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_B) == GLFW.GLFW_PRESS;
            case DASH:
                // Xボタン（Switch: Y、PS: □）
                return pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_X) == GLFW.GLFW_PRESS;
            case SPECIAL:
                // Yボタン（Switch: X、PS: △）
                return pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_Y) == GLFW.GLFW_PRESS;
            default:
                return false;
        }
    }
    
    //ゲームパッドが接続されているか確認
    public static boolean IsGamepadConnected(int nGamepadId) {
        return GLFW.glfwJoystickPresent(nGamepadId) && GLFW.glfwJoystickIsGamepad(nGamepadId);
    }
    
    //接続されているゲームパッドの名前を取得
    public static String GetGamepadName(int nGamepadId) {
        if (IsGamepadConnected(nGamepadId)) {
            return GLFW.glfwGetGamepadName(nGamepadId);
        }
        return "Not Connected";
    }
    
    //プレイヤー番号を取得
    public int GetPlayerNumber() {
        return nPlayerNumber;
    }
    
    //ゲームパッドを使用しているか
    public boolean IsUsingGamepad() {
        return bUseGamepad;
    }
    
    //入力デバイスを切り替え（キーボード ⇔ ゲームパッド）
    public void SwitchInputDevice() {
        if (bUseGamepad) {
            // ゲームパッド → キーボード
            bUseGamepad = false;
            System.out.println("プレイヤー" + nPlayerNumber + ": キーボード入力に切り替え");
        } else {
            // キーボード → ゲームパッド（接続されている場合のみ）
            // デフォルトでJOYSTICK_1を使用
            if (nGamepadId == -1) {
                nGamepadId = GLFW.GLFW_JOYSTICK_1;
            }
            
            if (IsGamepadConnected(nGamepadId)) {
                bUseGamepad = true;
                System.out.println("プレイヤー" + nPlayerNumber + ": ゲームパッド入力に切り替え (" + GetGamepadName(nGamepadId) + ")");
            } else {
                System.out.println("プレイヤー" + nPlayerNumber + ": ゲームパッドが接続されていません");
            }
        }
    }
    
    //ゲームパッドIDを設定
    public void SetGamepadId(int nGamepadId) {
        this.nGamepadId = nGamepadId;
    }
    
    //ゲームパッドIDを取得
    public int GetGamepadId() {
        return nGamepadId;
    }
}

