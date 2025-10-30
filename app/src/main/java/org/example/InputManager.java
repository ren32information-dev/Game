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
    private int nKeyLightAttack5 = GLFW.GLFW_KEY_Q;
    
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
                case LIGHTATTACK5:
                    return pWindow.isKeyPressed(nKeyLightAttack5);
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
    
    //ゲームパッド入力を取得（Switch純正、PS4コントローラー対応）
    private boolean GetGamepadInput(InputType eInputType) {
        // ゲームパッドが接続されているか確認
        if (!GLFW.glfwJoystickPresent(nGamepadId)) {
            return false;
        }
        
        // まずGamepad APIを試す（XInput互換コントローラー）
        if (GLFW.glfwJoystickIsGamepad(nGamepadId)) {
            GLFWGamepadState pState = GLFWGamepadState.create();
            if (GLFW.glfwGetGamepadState(nGamepadId, pState)) {
                // Gamepad APIで取得成功
                return GetGamepadInputStandard(pState, eInputType);
            }
        }
        
        // Gamepad APIが使えない場合はDirectInputモード（Switch純正、PS4など）
        return GetGamepadInputDirect(eInputType);
    }
    
    //標準Gamepad APIでの入力取得
    private boolean GetGamepadInputStandard(GLFWGamepadState pState, InputType eInputType) {
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
            case CROUCH:
                return pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y) > 0.5f || 
                       pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN) == GLFW.GLFW_PRESS;
            //case ATTACK:
                // Aボタン（Switch: B、PS: ×）
                //return pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_A) == GLFW.GLFW_PRESS;
            case GUARD:
                // Bボタン（Switch: A、PS: ○）
                return pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_B) == GLFW.GLFW_PRESS;
            case DASH:
                // Xボタン（Switch: Y、PS: □）
                //return pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_X) == GLFW.GLFW_PRESS;
            case SPECIAL:
                // Yボタン（Switch: X、PS: △）
                return pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_Y) == GLFW.GLFW_PRESS;
            case HEAVYATTACK5:
                // 強攻撃5ボタン
                return pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) < 0.5f &&
                       pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) > -0.5f &&
                       pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_A) == GLFW.GLFW_PRESS;
            case MEDIUMATTACK5:
                // 中攻撃5ボタン
                return pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) < 0.5f &&
                       pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) > -0.5f &&
                       pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_X) == GLFW.GLFW_PRESS;
            case LIGHTATTACK5:
                // 弱攻撃5ボタン
                return pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) < 0.5f &&
                       pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) > -0.5f &&
                       pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_Y) == GLFW.GLFW_PRESS;
            case LIGHTATTACK2:
                return pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) < 0.5f &&
                       pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) > -0.5f &&
                       pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y) > 0.5f &&
                       pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_Y) == GLFW.GLFW_PRESS;
            case HEAVYATTACK2:
                return pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) < 0.5f &&
                       pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) > -0.5f &&
                       pState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y) > 0.5f &&
                       pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_A) == GLFW.GLFW_PRESS;
            case MEDIUMATTACK6:
                return pState.buttons(GLFW.GLFW_GAMEPAD_BUTTON_X) == GLFW.GLFW_PRESS;
            default:
                return false;
        }
    }
    
    //DirectInputモードでの入力取得（Switch純正プロコン、PS4コントローラー対応）
    private boolean GetGamepadInputDirect(InputType eInputType) {
        // 生のボタン配列を取得
        ByteBuffer pButtons = GLFW.glfwGetJoystickButtons(nGamepadId);
        if (pButtons == null) {
            return false;
        }
        
        // 生のアナログスティック配列を取得
        FloatBuffer pAxes = GLFW.glfwGetJoystickAxes(nGamepadId);
        if (pAxes == null) {
            return false;
        }
        
        // Switch純正プロコン、PS4コントローラーのボタンマッピング
        switch (eInputType) {
            case LEFT:
                // 左スティック左（Axis 0が負の値）または十字キー左
                return (pAxes.capacity() > 0 && pAxes.get(0) < -0.5f) || 
                       (pButtons.capacity() > 13 && pButtons.get(13) == GLFW.GLFW_PRESS);
            case RIGHT:
                // 左スティック右（Axis 0が正の値）または十字キー右
                return (pAxes.capacity() > 0 && pAxes.get(0) > 0.5f) || 
                       (pButtons.capacity() > 14 && pButtons.get(14) == GLFW.GLFW_PRESS);
            case JUMP:
                // 左スティック上（Axis 1が負の値）または十字キー上
                return (pAxes.capacity() > 1 && pAxes.get(1) < -0.5f) || 
                       (pButtons.capacity() > 11 && pButtons.get(11) == GLFW.GLFW_PRESS);
            case ATTACK:
                // Aボタン（Switch: B = ボタン1、PS4: × = ボタン1）
                return pButtons.capacity() > 1 && pButtons.get(1) == GLFW.GLFW_PRESS;
            case GUARD:
                // Bボタン（Switch: A = ボタン0、PS4: ○ = ボタン2）
                return (pButtons.capacity() > 0 && pButtons.get(0) == GLFW.GLFW_PRESS) ||
                       (pButtons.capacity() > 2 && pButtons.get(2) == GLFW.GLFW_PRESS);
            case DASH:
                // Xボタン（Switch: Y = ボタン3、PS4: □ = ボタン0）
                return (pButtons.capacity() > 3 && pButtons.get(3) == GLFW.GLFW_PRESS) ||
                       (pButtons.capacity() > 0 && pButtons.get(0) == GLFW.GLFW_PRESS);
            case SPECIAL:
                // Yボタン（Switch: X = ボタン2、PS4: △ = ボタン3）
                return (pButtons.capacity() > 2 && pButtons.get(2) == GLFW.GLFW_PRESS) ||
                       (pButtons.capacity() > 3 && pButtons.get(3) == GLFW.GLFW_PRESS);
            default:
                return false;
        }
    }
    
    //ゲームパッドが接続されているか確認（Switch純正、PS4対応）
    public static boolean IsGamepadConnected(int nGamepadId) {
        // Joystickとして接続されていればOK（Gamepad API非対応でも検出）
        return GLFW.glfwJoystickPresent(nGamepadId);
    }
    
    //接続されているゲームパッドの名前を取得（Switch純正、PS4対応）
    public static String GetGamepadName(int nGamepadId) {
        if (!IsGamepadConnected(nGamepadId)) {
            return "Not Connected";
        }
        
        // まずGamepad APIの名前を試す
        String sGamepadName = GLFW.glfwGetGamepadName(nGamepadId);
        if (sGamepadName != null) {
            return sGamepadName;
        }
        
        // Gamepad APIで取得できない場合はJoystick名を使う（Switch純正、PS4など）
        String sJoystickName = GLFW.glfwGetJoystickName(nGamepadId);
        if (sJoystickName != null) {
            return sJoystickName;
        }
        
        return "Unknown Device";
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

