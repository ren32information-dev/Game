package org.example;

/**
 * キャラクタークラス（Playerを継承）
 * 座標、移動、物理演算などの具体的な実装を提供
 */
public class Character extends Player {
    private float fPositionX;
    //X座標（左右位置）
    private float fPositionY;
    //Y座標（高さ）
    private float fPositionZ;
    //Z座標（奥行き）
    
    private float fVelocityY;
    //Y方向の速度（ジャンプ用）
    private float fMoveSpeed;
    //左右移動速度
    private float fJumpPower;
    //ジャンプ力
    private float fGravity;
    //重力
    
    private boolean bIsGrounded;
    //地面に接地しているか
    private float fGroundLevel;
    //地面の高さ
    
    private int nTextureId;
    //キャラクターの画像ID
    
    private HitColliderBox pHitBox;
    //当たり判定ボックス
    
    private CharacterState eCurrentState;
    //現在の状態
    private CharacterState ePreviousState;
    //前フレームの状態
    
    private InputCommand pInputCommand;
    //入力コマンド管理
    private float fDashSpeed;
    //ダッシュ速度
    private float fDashDuration;
    //ダッシュ持続時間
    private float fDashTimer;
    //ダッシュタイマー
    
    private Character pOpponentCharacter;
    //相手キャラクターへの参照
    
    //コンストラクタ
    public Character(int nPlayerNumber, InputManager pInputManager, float fX, float fY, float fZ) {
        // 親クラス（Player）のコンストラクタを呼び出し
        super(nPlayerNumber, pInputManager);
        
        this.fPositionX = fX;
        this.fPositionY = fY;
        this.fPositionZ = fZ;
        
        this.fVelocityY = 0f;
        this.fMoveSpeed = 5.0f;
        this.fJumpPower = 0.3f;
        this.fGravity = -0.015f;
        this.fGroundLevel = 0f;
        this.bIsGrounded = true;
        
        // デフォルトの当たり判定ボックスを作成（幅1.0、高さ2.0）
        this.pHitBox = new HitColliderBox(1.0f, 2.0f);
        
        // 初期状態は立ち状態
        this.eCurrentState = CharacterState.STAND;
        this.ePreviousState = CharacterState.STAND;
        
        // 入力コマンド管理を初期化
        this.pInputCommand = new InputCommand();
        
        // ダッシュパラメータ
        this.fDashSpeed = 15.0f; // 通常移動の3倍速
        this.fDashDuration = 0.3f; // 0.3秒間ダッシュ
        this.fDashTimer = 0f;
        
        this.pOpponentCharacter = null;
        
        System.out.println("[Character] キャラクターを作成しました (位置: " + fX + ", " + fY + ", " + fZ + ")");
    }
    
    //画像を設定
    public void SetTexture(int nTextureId) {
        this.nTextureId = nTextureId;
    }
    
    //画像IDを取得
    public int GetTexture() {
        return nTextureId;
    }
    
    //更新処理（オーバーライド）
    @Override
    public void Update(float fDeltaTime) {
        // 親クラスの更新処理を呼び出し
        super.Update(fDeltaTime);
        
        // 前フレームの状態を保存
        ePreviousState = eCurrentState;
        
        // 入力取得
        boolean bLeftMove = pInputManager.GetInput(InputType.LEFT);
        boolean bRightMove = pInputManager.GetInput(InputType.RIGHT);
        boolean bJump = pInputManager.GetInput(InputType.JUMP);
        boolean bGuard = pInputManager.GetInput(InputType.GUARD);
        boolean bUp = pInputManager.GetInput(InputType.JUMP); // 仮で上方向
        boolean bDown = false; // TODO: 下入力の実装
        
        // 現在のゲーム時刻を取得
        float fCurrentTime = (float) org.lwjgl.glfw.GLFW.glfwGetTime();
        
        // テンキー方向を取得（1P/2P側で反転）
        int nDirection = GetNumpadDirection(bLeftMove, bRightMove, bUp, bDown);
        
        // 入力コマンドバッファを更新
        pInputCommand.UpdateInput(nDirection, fCurrentTime);
        
        // ダッシュコマンド判定（66 or 44）
        boolean bDashForward = pInputCommand.CheckDashForward(fCurrentTime);
        boolean bDashBackward = pInputCommand.CheckDashBackward(fCurrentTime);
        
        // 相手との相対位置を考慮したダッシュ方向を計算
        boolean[] aDashDirections = CalculateDashDirections(bDashForward, bDashBackward);
        
        // 状態遷移の処理
        UpdateState(bLeftMove, bRightMove, bJump, bGuard, aDashDirections[0], aDashDirections[1], fDeltaTime);
        
        // 重力を適用
        fVelocityY += fGravity;
        fPositionY += fVelocityY;
        
        // 地面判定
        if (fPositionY <= fGroundLevel) {
            fPositionY = fGroundLevel;
            fVelocityY = 0;
            bIsGrounded = true;
        } else {
            bIsGrounded = false;
        }
        
        // 状態が変わったらログ出力
        if (eCurrentState != ePreviousState) {
            System.out.println("[Player" + nPlayerNumber + "] 状態遷移: " + ePreviousState + " → " + eCurrentState);
        }
    }
    
    //テンキー方向を取得（1P/2P側で方向を反転）
    private int GetNumpadDirection(boolean bLeft, boolean bRight, boolean bUp, boolean bDown) {
        // 1P側は通常、2P側は左右反転
        boolean bIsPlayer1 = (nPlayerNumber == 1);
        
        // 左右を1P/2P側に応じて調整
        boolean bForward = bIsPlayer1 ? bRight : bLeft;
        boolean bBackward = bIsPlayer1 ? bLeft : bRight;
        
        // テンキー方向を計算
        if (bUp && bBackward) return 7;
        if (bUp && bForward) return 9;
        if (bUp) return 8;
        if (bDown && bBackward) return 1;
        if (bDown && bForward) return 3;
        if (bDown) return 2;
        if (bBackward) return 4;
        if (bForward) return 6;
        
        return 5; // ニュートラル
    }
    
    //相手との相対位置を考慮したダッシュ方向を計算
    private boolean[] CalculateDashDirections(boolean bDashForward, boolean bDashBackward) {
        boolean[] aDashDirections = {false, false}; // [0]=相手に向かって, [1]=相手から離れて
        
        // 相手プレイヤーを取得
        Character pOpponent = GetOpponentCharacter();
        if (pOpponent == null) {
            // 相手がいない場合は通常の方向
            aDashDirections[0] = bDashForward;
            aDashDirections[1] = bDashBackward;
            return aDashDirections;
        }
        
        // 相手との相対位置を計算
        float fDistanceToOpponent = pOpponent.GetPositionX() - this.fPositionX;
        boolean bIsFacingOpponent = (nPlayerNumber == 1) ? fDistanceToOpponent > 0 : fDistanceToOpponent < 0;
        
        if (bDashForward) {
            // 6コマンド（前ダッシュ）
            if (bIsFacingOpponent) {
                // 相手を向いている場合：相手に向かってダッシュ
                aDashDirections[0] = true;
            } else {
                // 相手に背を向けている場合：相手から離れてダッシュ
                aDashDirections[1] = true;
            }
        }
        
        if (bDashBackward) {
            // 4コマンド（後ダッシュ）
            if (bIsFacingOpponent) {
                // 相手を向いている場合：相手から離れてダッシュ
                aDashDirections[1] = true;
            } else {
                // 相手に背を向けている場合：相手に向かってダッシュ
                aDashDirections[0] = true;
            }
        }
        
        return aDashDirections;
    }
    
    //相手キャラクターを取得
    private Character GetOpponentCharacter() {
        return pOpponentCharacter;
    }
    
    //相手キャラクターを設定
    public void SetOpponentCharacter(Character pOpponent) {
        this.pOpponentCharacter = pOpponent;
    }
    
    //状態遷移の処理
    private void UpdateState(boolean bLeftMove, boolean bRightMove, boolean bJump, boolean bGuard, 
                            boolean bDashForward, boolean bDashBackward, float fDeltaTime) {
        switch (eCurrentState) {
            case STAND:
                // 立ち状態からの遷移
                if (bDashForward || bDashBackward) {
                    // ダッシュ開始
                    ChangeState(CharacterState.DASH);
                    fDashTimer = fDashDuration;
                    System.out.println("[Player" + nPlayerNumber + "] ダッシュ開始！");
                } else if (bJump && bIsGrounded) {
                    ChangeState(CharacterState.JUMP);
                    Jump();
                } else if (bGuard) {
                    ChangeState(CharacterState.GUARD);
                } else if (bLeftMove || bRightMove) {
                    ChangeState(CharacterState.FRONT);
                    if (bLeftMove) MoveLeft(fDeltaTime);
                    if (bRightMove) MoveRight(fDeltaTime);
                }
                break;
                
            case FRONT:
                // 前進状態からの遷移
                if (bJump && bIsGrounded) {
                    ChangeState(CharacterState.JUMP);
                    Jump();
                } else if (bGuard) {
                    ChangeState(CharacterState.GUARD);
                } else if (bLeftMove || bRightMove) {
                    // 移動中
                    if (bLeftMove) MoveLeft(fDeltaTime);
                    if (bRightMove) MoveRight(fDeltaTime);
                } else {
                    // 移動入力がなくなったら立ち状態に
                    ChangeState(CharacterState.STAND);
                }
                break;
                
            case GUARD:
                // ガード状態からの遷移
                if (!bGuard) {
                    // ガード解除
                    ChangeState(CharacterState.STAND);
                }
                break;
                
            case CROUCH:
                // しゃがみ状態からの遷移
                // TODO: しゃがみ入力の実装後に追加
                ChangeState(CharacterState.STAND);
                break;
                
            case JUMP:
                // ジャンプ状態からの遷移
                if (bIsGrounded) {
                    // 着地したら立ち状態に
                    ChangeState(CharacterState.STAND);
                } else {
                    // 空中での移動
                    if (bLeftMove) MoveLeft(fDeltaTime);
                    if (bRightMove) MoveRight(fDeltaTime);
                }
                break;
                
            case DASH:
                // ダッシュ状態からの遷移
                fDashTimer -= fDeltaTime;
                
                if (fDashTimer > 0) {
                    // 相対方向でのダッシュ移動
                    Character pOpponent = GetOpponentCharacter();
                    if (pOpponent != null) {
                        // 相手との相対位置を計算
                        float fDistanceToOpponent = pOpponent.GetPositionX() - this.fPositionX;
                        boolean bIsFacingOpponent = (nPlayerNumber == 1) ? fDistanceToOpponent > 0 : fDistanceToOpponent < 0;
                        
                        if (bDashForward) {
                            // 相手に向かってダッシュ
                            if (bIsFacingOpponent) {
                                // 相手を向いている場合：相手に向かって
                                fPositionX += (nPlayerNumber == 1) ? fDashSpeed * fDeltaTime : -fDashSpeed * fDeltaTime;
                            } else {
                                // 相手に背を向けている場合：相手から離れて
                                fPositionX += (nPlayerNumber == 1) ? -fDashSpeed * fDeltaTime : fDashSpeed * fDeltaTime;
                            }
                        } else if (bDashBackward) {
                            // 相手から離れてダッシュ
                            if (bIsFacingOpponent) {
                                // 相手を向いている場合：相手から離れて
                                fPositionX += (nPlayerNumber == 1) ? -fDashSpeed * fDeltaTime : fDashSpeed * fDeltaTime;
                            } else {
                                // 相手に背を向けている場合：相手に向かって
                                fPositionX += (nPlayerNumber == 1) ? fDashSpeed * fDeltaTime : -fDashSpeed * fDeltaTime;
                            }
                        }
                    } else {
                        // 相手がいない場合は通常の方向
                        boolean bIsPlayer1 = (nPlayerNumber == 1);
                        if (bIsPlayer1) {
                            fPositionX += fDashSpeed * fDeltaTime;
                        } else {
                            fPositionX -= fDashSpeed * fDeltaTime;
                        }
                    }
                } else {
                    // ダッシュ終了
                    ChangeState(CharacterState.STAND);
                    System.out.println("[Player" + nPlayerNumber + "] ダッシュ終了");
                }
                break;
                
            case ATTACK:
                // 攻撃状態からの遷移
                // TODO: 攻撃の実装後に追加
                break;
                
            case DAMAGE:
                // 被ダメージ状態からの遷移
                // TODO: ダメージの実装後に追加
                break;
                
            case DOWN:
                // ダウン状態からの遷移
                // TODO: ダウンの実装後に追加
                break;
        }
    }
    
    //状態を変更
    private void ChangeState(CharacterState eNewState) {
        this.eCurrentState = eNewState;
    }
    
    //左に移動
    public void MoveLeft(float fDeltaTime) {
        fPositionX -= fMoveSpeed * fDeltaTime;
    }
    
    //右に移動
    public void MoveRight(float fDeltaTime) {
        fPositionX += fMoveSpeed * fDeltaTime;
    }
    
    //ジャンプ
    public void Jump() {
        if (bIsGrounded) {
            fVelocityY = fJumpPower;
            bIsGrounded = false;
        }
    }
    
    //X座標を取得
    public float GetPositionX() { 
        return fPositionX; 
    }
    
    //Y座標を取得
    public float GetPositionY() { 
        return fPositionY; 
    }
    
    //Z座標を取得
    public float GetPositionZ() { 
        return fPositionZ; 
    }
    
    //座標を設定
    public void SetPosition(float fX, float fY, float fZ) {
        this.fPositionX = fX;
        this.fPositionY = fY;
        this.fPositionZ = fZ;
    }
    
    //当たり判定ボックスを取得
    public HitColliderBox GetHitBox() {
        return pHitBox;
    }
    
    //当たり判定ボックスを設定
    public void SetHitBox(HitColliderBox pHitBox) {
        this.pHitBox = pHitBox;
    }
    
    //X座標を設定
    public void SetPositionX(float fX) {
        this.fPositionX = fX;
    }
    
    //Y座標を設定
    public void SetPositionY(float fY) {
        this.fPositionY = fY;
    }
    
    //現在の状態を取得
    public CharacterState GetCurrentState() {
        return eCurrentState;
    }
    
    //前の状態を取得
    public CharacterState GetPreviousState() {
        return ePreviousState;
    }
    
    //状態を強制的に設定（外部から状態を変更する場合）
    public void SetState(CharacterState eState) {
        this.ePreviousState = this.eCurrentState;
        this.eCurrentState = eState;
        System.out.println("[Player" + nPlayerNumber + "] 状態を強制変更: " + eState);
    }
}

