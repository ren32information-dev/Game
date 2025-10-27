package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * キャラクタークラス（Playerを継承）
 * 座標、移動、物理演算、状態遷移、ダッシュなどの具体的な実装を提供
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
    private HP pHP;

    private boolean bIsGrounded;
    //地面に接地しているか
    private float fGroundLevel;
    //地面の高さ
    
    private int nTextureId;
    //キャラクターの画像ID

    private HashMap<String, HitColliderManager> pAllColliders;
    // 被弾判定、攻撃判定、投げ判定などを管理するマップ

    private Gauge pGauge;
    //ゲージオブジェクト
    
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
    private float fDashCooldownTimer;
    //ダッシュのクールダウンタイマー（後隙）
    private float fDashCooldownDuration;
    //ダッシュのクールダウン時間（30フレーム ≈ 0.5秒）
    private float fAnimationTimer;
    //アニメーションのフレーム調整用のタイマー
    private boolean fWalkloop;
    // 歩行ループフラグ
    
    private Character pOpponentCharacter;
    //相手キャラクターへの参照
    
    private Camera pCamera;
    //カメラへの参照（境界チェック用）
    
    private boolean bIsFacingRight;
    //右を向いているか（true=右向き、false=左向き）
    
    private JumpType eJumpType;
    //ジャンプの種類（バックジャンプ、垂直ジャンプ、前ジャンプ）
    private int nJumpPreparationFrames;
    //ジャンプ予備動作フレーム数
    private static final int JUMP_PREPARATION_FRAMES = 3;
    //ジャンプ予備動作の必要フレーム数（3フレーム待機、4フレーム目にジャンプ）
    private float fJumpDirectionX;
    //ジャンプ時の水平方向速度
    
    private boolean bAirDashUsed;
    //空中ダッシュを使用済みか
    private boolean bIsAirDashing;
    //現在空中ダッシュ中か
    private float fAirDashTimer;
    //空中ダッシュタイマー
    private float fAirDashSpeed;
    //空中ダッシュ速度
    private float fAirDashDuration;
    //空中ダッシュ持続時間
    private float fAirDashDirectionX;
    //空中ダッシュの方向
    
    //ジャンプの種類を定義
    private enum JumpType {
        NONE,
        //ジャンプしていない
        BACK_JUMP,
        //バックジャンプ（7方向）
        VERTICAL_JUMP,
        //垂直ジャンプ（8方向）
        FORWARD_JUMP
        //前ジャンプ（9方向）
    }
    
    //コンストラクタ（プレイヤー番号と入力マネージャーを受け取る）
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

        // 当たり判定マネージャーを初期化
        this.pAllColliders = new HashMap<>();
        this.pAllColliders.put("my", new HitColliderManager());         // 自身の当たり判定
        this.pAllColliders.put("attack", new HitColliderManager());     // 攻撃判定
        this.pAllColliders.put("throw", new HitColliderManager());      // 投げ判定

        // 自身の当たり判定を追加
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 1.0f, 0.5f, 0.5f));
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 1.0f, -0.5f, 0.5f));
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 1.0f, 0.5f, -0.5f));
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 1.0f, -0.5f, -0.5f));

        // HP オブジェクトを初期化
        this.pHP = new HP();

        // ゲージオブジェクトを初期化
        this.pGauge = new Gauge();
        
        // 初期状態は立ち状態
        this.eCurrentState = CharacterState.STAND;
        this.ePreviousState = CharacterState.STAND;
        
        // 入力コマンド管理を初期化
        this.pInputCommand = new InputCommand();

        // アニメーションのフレーム調整用のタイマー
        this.fAnimationTimer = 0f;
        this.fWalkloop = false;
        
        // ダッシュパラメータ
        this.fDashSpeed = 15.0f; // 通常移動の3倍速
        this.fDashDuration = 0.3f; // 0.3秒間ダッシュ
        this.fDashTimer = 0f;
        this.fDashCooldownTimer = 0f; // クールダウンタイマー初期化
        this.fDashCooldownDuration = 0.25f; // 15フレーム（60FPS想定で0.25秒）
        
        this.pOpponentCharacter = null;
        this.pCamera = null;
        
        // 向きの初期化（1Pは右向き、2Pは左向き）
        this.bIsFacingRight = (nPlayerNumber == 1);
        
        // ジャンプパラメータ
        this.eJumpType = JumpType.NONE;
        this.nJumpPreparationFrames = 0;
        this.fJumpDirectionX = 0f;
        
        // 空中ダッシュパラメータ（地上ダッシュの半分の距離 2.25 = 15.0 × 0.15）
        this.bAirDashUsed = false;
        this.bIsAirDashing = false;
        this.fAirDashTimer = 0f;
        this.fAirDashSpeed = 15.0f; // 地上ダッシュと同じ速度
        this.fAirDashDuration = 0.15f; // 地上ダッシュの半分の時間（距離半減）
        this.fAirDashDirectionX = 0f;
        
        System.out.println("[Character] キャラクターを作成しました (位置: " + fX + ", " + fY + ", " + fZ + ")");
    }
    
    //コンストラクタ（互換性のため、古い形式も残す）
    public Character(float fX, float fY, float fZ) {
        // デフォルトの値でPlayerコンストラクタを呼び出す
        super(1, null);
        
        this.fPositionX = fX;
        this.fPositionY = fY;
        this.fPositionZ = fZ;
        
        this.fVelocityY = 0f;
        this.fMoveSpeed = 5.0f;
        this.fJumpPower = 0.3f;
        this.fGravity = -0.015f;
        this.fGroundLevel = 0f;
        this.bIsGrounded = true;

        // 当たり判定マネージャーを初期化
        this.pAllColliders = new HashMap<>();
        this.pAllColliders.put("my", new HitColliderManager());         // 自身の当たり判定
        this.pAllColliders.put("attack", new HitColliderManager());     // 攻撃判定
        this.pAllColliders.put("throw", new HitColliderManager());      // 投げ判定

        // 自身の当たり判定を追加
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 1.5f, 0.5f, 0.8f)); // 右下
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 1.5f, -0.5f, 0.8f));   // 左下
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 1.5f, 0.5f, 2.2f));   // 右上
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 1.5f, -0.5f, 2.2f));  // 左上

        // HP オブジェクトを初期化
        this.pHP = new HP();

        // ゲージオブジェクトを初期化
        this.pGauge = new Gauge();
        
        // 初期状態は立ち状態
        this.eCurrentState = CharacterState.STAND;
        this.ePreviousState = CharacterState.STAND;
        
        // 入力コマンド管理を初期化
        this.pInputCommand = new InputCommand();

        // アニメーションのフレーム調整用のタイマー
        this.fAnimationTimer = 0f;
        this.fWalkloop = false;

        // ダッシュパラメータ
        this.fDashSpeed = 15.0f;
        this.fDashDuration = 0.3f;
        this.fDashTimer = 0f;
        this.fDashCooldownTimer = 0f; // クールダウンタイマー初期化
        this.fDashCooldownDuration = 0.25f; // 15フレーム（60FPS想定で0.25秒）
        
        this.pOpponentCharacter = null;
        this.pCamera = null;
        
        // 向きの初期化（デフォルトは右向き）
        this.bIsFacingRight = true;
        
        // ジャンプパラメータ
        this.eJumpType = JumpType.NONE;
        this.nJumpPreparationFrames = 0;
        this.fJumpDirectionX = 0f;
        
        // 空中ダッシュパラメータ
        this.bAirDashUsed = false;
        this.bIsAirDashing = false;
        this.fAirDashTimer = 0f;
        this.fAirDashSpeed = 15.0f;
        this.fAirDashDuration = 0.15f; // 地上ダッシュの半分の時間
        this.fAirDashDirectionX = 0f;
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
        
        // HPとゲージの更新
        pHP.update();
        
        // ダッシュクールダウンタイマーを減算
        if (fDashCooldownTimer > 0) {
            fDashCooldownTimer -= fDeltaTime;
            if (fDashCooldownTimer < 0) {
                fDashCooldownTimer = 0;
            }
        }
        
        // 前フレームの状態を保存
        ePreviousState = eCurrentState;
        
        // 入力マネージャーがnullの場合は状態遷移をスキップ
        if (pInputManager != null) {
            // 入力取得
            boolean bLeftMove = pInputManager.GetInput(InputType.LEFT);
            boolean bRightMove = pInputManager.GetInput(InputType.RIGHT);
            boolean bJump = pInputManager.GetInput(InputType.JUMP);
            boolean bGuard = pInputManager.GetInput(InputType.GUARD);
            boolean bUp = pInputManager.GetInput(InputType.JUMP); // 上方向
            boolean bHeavyAttack5 = pInputManager.GetInput(InputType.HEAVYATTACK5);
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
            
            // ジャンプの種類を判定（7,8,9方向）
            JumpType eRequestedJumpType = JumpType.NONE;
            if (nDirection == 7) {
                if(bIsFacingRight == false) {
                    eRequestedJumpType = JumpType.FORWARD_JUMP;
                } else {
                    eRequestedJumpType = JumpType.BACK_JUMP;
                }
            } else if (nDirection == 8) {
                eRequestedJumpType = JumpType.VERTICAL_JUMP;
            } else if (nDirection == 9) {
                if(bIsFacingRight == true) {
                    eRequestedJumpType = JumpType.FORWARD_JUMP;
                } else {
                    eRequestedJumpType = JumpType.BACK_JUMP;
                }
            }
            
            // 状態遷移の処理
            UpdateState(bLeftMove, bRightMove, bJump, bGuard, aDashDirections[0], aDashDirections[1], eRequestedJumpType, fDeltaTime);
        }
        
        // 重力を適用（空中ダッシュ中は無効化）
        if (!bIsAirDashing) {
            fVelocityY += fGravity;
            fPositionY += fVelocityY;
        }
        // 空中ダッシュ中はY座標を維持（真横移動のみ）
        
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
        
        // カメラの視野範囲外に出ないように座標を制限
        ClampPositionToCameraBounds();
        
        // 振り向き処理（地上にいる時のみ）
        UpdateFacingDirection();
    }
    
    //左に移動（地上のみ、空中では無効。ジャンプ予備動作中は移動可能）
    public void MoveLeft(float fDeltaTime) {
        // 空中にいる場合のみ移動禁止（地上でのジャンプ予備動作中は移動可能）
        if (!bIsGrounded) {
            return;
        }
        fPositionX -= fMoveSpeed * fDeltaTime;
    }
    
    //右に移動（地上のみ、空中では無効。ジャンプ予備動作中は移動可能）
    public void MoveRight(float fDeltaTime) {
        // 空中にいる場合のみ移動禁止（地上でのジャンプ予備動作中は移動可能）
        if (!bIsGrounded) {
            return;
        }
        fPositionX += fMoveSpeed * fDeltaTime;
    }
    
    //ジャンプ（格闘ゲーム式ジャンプに切り替えたため無効化）
    public void Jump() {
        // 格闘ゲーム式のジャンプはUpdate()内で7,8,9方向入力により処理される
        // この古いメソッドは呼ばれても何もしない
        return;
    }
    
    //格闘ゲーム式ジャンプを実行（7,8,9方向）
    private void ExecuteJump() {
        if (!bIsGrounded) {
            return; // 既に空中にいる場合は何もしない
        }
        
        // 垂直速度を設定
        fVelocityY = fJumpPower;
        bIsGrounded = false;
        
        // ジャンプの種類に応じて水平速度を設定
        float fHorizontalJumpSpeed = 3.0f; // 水平方向のジャンプ速度
        Character pOpponent = GetOpponentCharacter();
        
        switch (eJumpType) {
            case BACK_JUMP:
                // バックジャンプ：相手から離れる方向
                System.out.println("[Player]" + pOpponent.GetPlayerNumber() + "]後ろジャンプ");
                nTextureId = 4;
                if(bIsFacingRight) {
                    fJumpDirectionX = -fHorizontalJumpSpeed; // 右向きなら左方向
                } else {
                    fJumpDirectionX = fHorizontalJumpSpeed; // 左向きなら右方向
                }
                break;
                
            case VERTICAL_JUMP:
                // 垂直ジャンプ：水平移動なし
                System.out.println("[Player" + pOpponent.GetPlayerNumber() + "]垂直ジャンプ");
                nTextureId = 2;
                fJumpDirectionX = 0f;
                break;
                
            case FORWARD_JUMP:
                // 前ジャンプ：相手に向かう方向
                System.out.println("[Player]" + pOpponent.GetPlayerNumber() + "]前ジャンプ");
                nTextureId = 3;
                if(bIsFacingRight) {
                    fJumpDirectionX = fHorizontalJumpSpeed; // 右向きなら右方向
                } else {
                    fJumpDirectionX = -fHorizontalJumpSpeed; // 左向きなら左方向
                }
                break;
                
            default:
                fJumpDirectionX = 0f;
                break;
        }
    }
    
    //空中ダッシュを開始
    private void StartAirDash(boolean bDashForward, boolean bDashBackward) {
        // 空中ダッシュ使用済みフラグを立てる
        bAirDashUsed = true;
        bIsAirDashing = true;
        fAirDashTimer = fAirDashDuration;
        
        // ジャンプの上昇速度をリセット（ふわっとするのを防ぐ）
        fVelocityY = 0f;
        
        // 斜めジャンプの水平成分をリセット（斜め方向に落ちないようにする）
        fJumpDirectionX = 0f;
        
        // 相手プレイヤーを取得
        Character pOpponent = GetOpponentCharacter();
        
        if (pOpponent != null) {
            // 相手との相対位置を計算
            float fDistanceToOpponent = pOpponent.GetPositionX() - this.fPositionX;
            boolean bIsFacingOpponent = (nPlayerNumber == 1) ? fDistanceToOpponent > 0 : fDistanceToOpponent < 0;
            
            if (bDashForward) {
                // 前方向（相手に向かって）空中ダッシュ
                if (bIsFacingOpponent) {
                    //fAirDashDirectionX = (nPlayerNumber == 1) ? fAirDashSpeed : -fAirDashSpeed;
                    fAirDashDirectionX = fAirDashSpeed;
                } else {
                    fAirDashDirectionX = -fAirDashSpeed;
                }
                System.out.println("[Player" + nPlayerNumber + "] 空中前ダッシュ開始！");
            } else if (bDashBackward) {
                // 後方向（相手から離れて）空中ダッシュ
                if (bIsFacingOpponent) {
                    fAirDashDirectionX = -fAirDashSpeed;
                } else {
                    fAirDashDirectionX = fAirDashSpeed;
                }
                System.out.println("[Player" + nPlayerNumber + "] 空中後ダッシュ開始！");
            }
        } else {
            // 相手がいない場合は通常の方向
            if (bDashForward) {
                fAirDashDirectionX = (nPlayerNumber == 1) ? fAirDashSpeed : -fAirDashSpeed;
            } else if (bDashBackward) {
                fAirDashDirectionX = (nPlayerNumber == 1) ? -fAirDashSpeed : fAirDashSpeed;
            }
            System.out.println("[Player" + nPlayerNumber + "] 空中ダッシュ開始！");
        }
    }

    // ダメージが入る　デバッグ用
    public void DamageHP() {
        pHP.DamageHP(10);
    }

    // 回復が入る　デバッグ用
    public void HealHP() {
        pHP.HealHP(10);
    }   

    //HPオブジェクトを取得
    public HP GetHPObject() {
        return pHP;
    }

    //ゲージオブジェクトを取得
    public Gauge GetGauge() {
        return pGauge;
    }
    
    //X座標を取得
    public float GetPositionX() { return fPositionX; }
    //Y座標を取得
    public float GetPositionY() { return fPositionY; }
    //Z座標を取得
    public float GetPositionZ() { return fPositionZ; }
    
    //座標を設定
    public void SetPosition(float fX, float fY, float fZ) {
        this.fPositionX = fX;
        this.fPositionY = fY;
        this.fPositionZ = fZ;
    }
    
    //当たり判定ボックスを取得
    public HashMap<String, HitColliderManager> GetHitBoxs() {
        return pAllColliders;
    }
    
    //当たり判定ボックスを設定
    public void SetHitBox(String sKey, HitColliderBox pHitBox) {
        this.pAllColliders.get(sKey).AddHitCollider(pHitBox);
    }
    
    //X座標を設定
    public void SetPositionX(float fX) {
        this.fPositionX = fX;
    }
    
    //Y座標を設定
    public void SetPositionY(float fY) {
        this.fPositionY = fY;
    }

    public void OnCollision(Character pOther, String sTag1, String sTag2) {
        // 衝突時の処理（必要に応じて実装）

        if (sTag1.equals("my") && sTag2.equals("my")) {
            System.out.println("[衝突処理] プレイヤー同士が衝突しました！");
            // 追加の処理をここに実装できます（例：ダメージ、押し出し、エフェクトなど）
        }

        if(sTag1.equals("player") && sTag2.equals("attack")) {
            System.out.println("[衝突処理] プレイヤーが攻撃に当たりました！");
            // 追加の処理をここに実装できます（例：ダメージ、エフェクトなど）
        }

        if(sTag1.equals("attack") && sTag2.equals("player")) {
            System.out.println("[衝突処理] 攻撃がプレイヤーに当たりました！");
            // 追加の処理をここに実装できます（例：ダメージ、エフェクトなど）
        }
    }
    
    //テンキー方向を取得（1P/2P側で方向を反転）
    private int GetNumpadDirection(boolean bLeft, boolean bRight, boolean bUp, boolean bDown) {
        // 1P側は通常、2P側は左右反転
        
        // 左右を1P/2P側に応じて調整
        //boolean bForward = bIsPlayer1 ? bRight : bLeft;
        //boolean bBackward = bIsPlayer1 ? bLeft : bRight;

        boolean bForward = bRight;
        boolean bBackward = bLeft;
        
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
    
    //入力マネージャーを設定（後から接続する場合）
    public void SetInputManager(InputManager pInputManager) {
        this.pInputManager = pInputManager;
        System.out.println("[Character] プレイヤー" + nPlayerNumber + " に入力マネージャーを設定しました");
    }
    
    //プレイヤー番号を設定（後から設定する場合）
    public void SetPlayerNumber(int nPlayerNumber) {
        this.nPlayerNumber = nPlayerNumber;
        System.out.println("[Character] プレイヤー番号を " + nPlayerNumber + " に設定しました");
    }
    
    //カメラを設定（境界チェック用）
    public void SetCamera(Camera pCamera) {
        this.pCamera = pCamera;
    }
    
    //向きを取得（描画用）
    public boolean GetIsFacingRight() {
        return bIsFacingRight;
    }
    
    //振り向き処理（地上にいる時のみ相手の方を向く）
    private void UpdateFacingDirection() {
        // 空中にいる場合は振り向かない
        if (!bIsGrounded) {
            return;
        }
        
        // 相手がいない場合は何もしない
        Character pOpponent = GetOpponentCharacter();
        if (pOpponent == null) {
            return;
        }
        
        // 相手との相対位置を計算
        float fDistanceToOpponent = pOpponent.GetPositionX() - this.fPositionX;
        //相手までの距離（正=右、負=左）
        
        boolean bShouldFaceRight = fDistanceToOpponent > 0;
        //相手が右にいる場合は右を向くべき
        
        // 向きが変わった場合のみログ出力
        if (bIsFacingRight != bShouldFaceRight) {
            bIsFacingRight = bShouldFaceRight;
            String sDirection = bIsFacingRight ? "右" : "左";
            System.out.println("[Player" + nPlayerNumber + "] " + sDirection + "を向きました");
        }
    }
    
    //X座標を境界内にクランプ（カメラの視野範囲外に出ないようにする）
    private void ClampPositionToCameraBounds() {
        if (pCamera == null) {
            return; // カメラが設定されていない場合は何もしない
        }
        
        float fLeftBound = pCamera.GetViewportLeft();
        //カメラの左端
        float fRightBound = pCamera.GetViewportRight();
        //カメラの右端
        
        // X座標を境界内にクランプ
        if (fPositionX < fLeftBound) {
            fPositionX = fLeftBound;
        } else if (fPositionX > fRightBound) {
            fPositionX = fRightBound;
        }
    }
    
    //状態遷移の処理
    private void UpdateState(boolean bLeftMove, boolean bRightMove, boolean bJump, boolean bGuard, 
                            boolean bDashForward, boolean bDashBackward, JumpType eRequestedJumpType, float fDeltaTime) {
        switch (eCurrentState) {
            case STAND:
                nTextureId = 0; // 立ち状態のテクスチャIDを設定
                // 立ち状態からの遷移
                if ((bDashForward || bDashBackward) && fDashCooldownTimer <= 0) {
                    // ダッシュ開始（クールダウンが終わっている場合のみ）
                    ChangeState(CharacterState.DASH);
                    fDashTimer = fDashDuration;
                    System.out.println("[Player" + nPlayerNumber + "] ダッシュ開始！");
                } else if (eRequestedJumpType != JumpType.NONE && bIsGrounded) {
                    // ジャンプ予備動作開始（7,8,9方向の入力を検出）
                    eJumpType = eRequestedJumpType;
                    nJumpPreparationFrames = 0;
                    ChangeState(CharacterState.JUMP);
                    System.out.println("[Player" + nPlayerNumber + "] ジャンプ予備動作開始: " + eJumpType);
                } else if (bGuard) {
                    ChangeState(CharacterState.GUARD);
                } else if (bLeftMove || bRightMove) {
                    ChangeState(CharacterState.FRONT);
                    // 移動処理はApp.javaから呼ばれるので、ここでは状態変更のみ
                }
                break;
                
            case FRONT:
                // 前進状態からの遷移
                fAnimationTimer += fDeltaTime;
                if(fAnimationTimer >= 0.2f)
                {
                    fAnimationTimer = 0f;
                    nTextureId++;
                }
                nTextureId = nTextureId % 2; // 前進状態のテクスチャIDを設定（0と1を交互に切り替え）
                if ((bDashForward || bDashBackward) && fDashCooldownTimer <= 0) {
                    // ダッシュ開始（クールダウンが終わっている場合のみ）
                    ChangeState(CharacterState.DASH);
                    fDashTimer = fDashDuration;
                    System.out.println("[Player" + nPlayerNumber + "] ダッシュ開始！");
                } else if (eRequestedJumpType != JumpType.NONE && bIsGrounded) {
                    // ジャンプ予備動作開始
                    eJumpType = eRequestedJumpType;
                    nJumpPreparationFrames = 0;
                    ChangeState(CharacterState.JUMP);
                    System.out.println("[Player" + nPlayerNumber + "] ジャンプ予備動作開始: " + eJumpType);
                } else if (bGuard) {
                    ChangeState(CharacterState.GUARD);
                } else if (bLeftMove || bRightMove) {
                    // 移動中（実際の移動処理はApp.javaから呼ばれる）
                    // 状態はFRONTのまま維持
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
                // ジャンプ状態の処理
                if (nJumpPreparationFrames < JUMP_PREPARATION_FRAMES) {
                    nTextureId = 1;
                    // 予備動作中（1～3フレーム目、地上で待機）
                    nJumpPreparationFrames++;
                    
                    if (nJumpPreparationFrames >= JUMP_PREPARATION_FRAMES) {
                        // 3フレーム経過したら次のフレームでジャンプ実行の準備
                        System.out.println("[Player" + nPlayerNumber + "] 予備動作完了（次フレームでジャンプ実行）");
                        nTextureId = 0;
                    }
                } else if (nJumpPreparationFrames == JUMP_PREPARATION_FRAMES && bIsGrounded) {
                    // 4フレーム目でジャンプ実行
                    nTextureId = 2; // ジャンプ中のテクスチャIDを設定
                    ExecuteJump();
                    nJumpPreparationFrames++; // カウントを進めて実行済みフラグ化
                    System.out.println("[Player" + nPlayerNumber + "] ジャンプ実行！ 種類: " + eJumpType);
                } else if (!bIsGrounded) {
                    // 空中にいる場合
                    
                    // 空中ダッシュの処理
                    if (bIsAirDashing) {
                        // 空中ダッシュ中
                        fAirDashTimer -= fDeltaTime;
                        
                        if (fAirDashTimer > 0) {
                            // 空中ダッシュ方向に移動
                            fPositionX += fAirDashDirectionX * fDeltaTime;
                        } else {
                            // 空中ダッシュ終了、慣性を残す
                            bIsAirDashing = false;
                            // 空中ダッシュの方向に慣性を残す（速度は約30%）
                            float fInertiaSpeed = 2.0f; // 慣性による移動速度
                            if (fAirDashDirectionX > 0) {
                                fJumpDirectionX = fInertiaSpeed; // 右方向への慣性
                            } else if (fAirDashDirectionX < 0) {
                                fJumpDirectionX = -fInertiaSpeed; // 左方向への慣性
                            }
                            System.out.println("[Player" + nPlayerNumber + "] 空中ダッシュ終了（慣性: " + fJumpDirectionX + "）");
                        }
                    } else if (!bAirDashUsed && (bDashForward || bDashBackward)) {
                        // 空中ダッシュ開始（未使用かつダッシュコマンド入力）
                        StartAirDash(bDashForward, bDashBackward);
                    } else if (bAirDashUsed) {
                        // 空中ダッシュ使用済み（慣性による移動を適用）
                        if (fJumpDirectionX != 0f) {
                            fPositionX += fJumpDirectionX * fDeltaTime;
                        }
                    } else {
                        // 通常のジャンプ移動（空中ダッシュ未使用時）
                        if (eJumpType == JumpType.BACK_JUMP) {
                            // バックジャンプ：相手から離れる方向
                            fPositionX += fJumpDirectionX * fDeltaTime;
                        } else if (eJumpType == JumpType.FORWARD_JUMP) {
                            // 前ジャンプ：相手に向かう方向
                            fPositionX += fJumpDirectionX * fDeltaTime;
                        }
                        // VERTICAL_JUMPの場合は水平移動なし
                    }
                } else if (bIsGrounded && nJumpPreparationFrames > JUMP_PREPARATION_FRAMES) {
                    // 着地した（予備動作完了後、空中から地上に戻った）
                    nTextureId = 5; // 着地後のテクスチャIDを設定
                    ChangeState(CharacterState.STAND);
                    eJumpType = JumpType.NONE;
                    nJumpPreparationFrames = 0;
                    fJumpDirectionX = 0f;
                    
                    // 空中ダッシュフラグをリセット
                    bAirDashUsed = false;
                    bIsAirDashing = false;
                    fAirDashTimer = 0f;
                    fAirDashDirectionX = 0f;
                    
                    System.out.println("[Player" + nPlayerNumber + "] 着地しました");
                }
                break;
                
            case DASH:
                // ダッシュ状態からの遷移
                fDashTimer -= fDeltaTime;
                this.fAnimationTimer += fDeltaTime;
                if (this.fAnimationTimer >= 0.1f) {
                    this.fAnimationTimer = 0f;

                    if(nTextureId == 3)
                    {
                        this.fWalkloop = true;
                    }    

                    if(nTextureId == 1)
                    {
                        this.fWalkloop = false;
                    }

                    if(this.fWalkloop == false)
                    {
                        nTextureId++;
                    }
                    else
                    {
                        nTextureId--;
                    }
                                    
                }
                
                
                if (fDashTimer > 0) {
                    // 相対方向でのダッシュ移動
                    Character pOpponent = GetOpponentCharacter();
                    if (pOpponent != null) {
                        // 相手との相対位置を計算
                        float fDistanceToOpponent = pOpponent.GetPositionX() - this.fPositionX;
                        boolean bIsFacingOpponent = fDistanceToOpponent > 0;

                        if(pOpponent.GetPlayerNumber() == 1)
                        {
                            bIsFacingOpponent = !bIsFacingOpponent;
                        }
                        
                        if (bDashForward) {
                            // 相手に向かってダッシュ
                            if (bIsFacingOpponent) {
                                // 相手を向いている場合：相手に向かって
                                fPositionX += fDashSpeed * fDeltaTime;
                                System.out.println("1");
                            } else {
                                // 相手に背を向けている場合：相手から離れて
                                fPositionX += -fDashSpeed * fDeltaTime;
                                System.out.println("2");
                            }
                        } else if (bDashBackward) {
                            // 相手から離れてダッシュ
                            if (bIsFacingOpponent) {
                                // 相手を向いている場合：相手から離れて
                                fPositionX += -fDashSpeed * fDeltaTime;
                                System.out.println("3");
                            } else {
                                // 相手に背を向けている場合：相手に向かって
                                fPositionX += fDashSpeed * fDeltaTime;
                                System.out.println("4");
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
                    fDashCooldownTimer = fDashCooldownDuration; // クールダウン開始
                    ChangeState(CharacterState.STAND);
                    System.out.println("[Player" + nPlayerNumber + "] ダッシュ終了（クールダウン開始: " + fDashCooldownDuration + "秒）");
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
        this.fAnimationTimer = 0f; // アニメーションタイマーをリセット
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

