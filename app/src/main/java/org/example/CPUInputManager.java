package org.example;

import java.util.Random;

/**
 * CPU用の入力マネージャー（InputManagerを継承）
 * AIの判断に基づいた入力を返す
 */
public class CPUInputManager extends InputManager {
    private Character pSelf;
    //自分のキャラクター
    private Character pOpponent;
    //相手のキャラクター
    
    private Random pRandom;
    //ランダム生成器
    
    private float fThinkTimer;
    //思考タイマー（定期的に行動を更新）
    private float fThinkInterval;
    //思考間隔（秒）
    
    private float fIdealDistance;
    //理想の間合い
    private float fCloseDistance;
    //近距離の閾値
    private float fFarDistance;
    //遠距離の閾値
    
    private AIAction eCurrentAction;
    //現在の行動
    private float fActionTimer;
    //行動タイマー
    
    private enum AIAction {
        IDLE,           // 待機
        MOVE_FORWARD,   // 前進
        MOVE_BACKWARD,  // 後退
        JUMP_FORWARD,   // 前ジャンプ
        JUMP_BACKWARD,  // 後ろジャンプ
        JUMP_VERTICAL,  // 垂直ジャンプ
        DASH_FORWARD,   // 前ダッシュ
        DASH_BACKWARD,  // 後ダッシュ
        AIR_DASH,       // 空中ダッシュ
        CROUCH,         // しゃがみ
        LIGHT_ATTACK,   // 弱攻撃
        MEDIUM_ATTACK,  // 中攻撃
        HEAVY_ATTACK    // 強攻撃
    }
    
    //コンストラクタ
    public CPUInputManager(Window pWindow, int nPlayerNumber) {
        super(pWindow, nPlayerNumber);
        this.pRandom = new Random();
        this.fThinkTimer = 0f;
        this.fThinkInterval = 0.3f; // 0.3秒ごとに行動を更新
        this.fIdealDistance = 5.0f; // 理想の間合い
        this.fCloseDistance = 3.0f; // 近距離
        this.fFarDistance = 8.0f; // 遠距離
        this.eCurrentAction = AIAction.IDLE;
        this.fActionTimer = 0f;
        
        System.out.println("[CPU] プレイヤー" + nPlayerNumber + "のCPU制御を開始");
    }
    
    //自分のキャラクターを設定
    public void SetSelfCharacter(Character pCharacter) {
        this.pSelf = pCharacter;
    }
    
    //相手のキャラクターを設定
    public void SetOpponentCharacter(Character pCharacter) {
        this.pOpponent = pCharacter;
    }
    
    //CPU思考の更新（毎フレーム呼ぶ）
    public void Update(float fDeltaTime) {
        if (pSelf == null || pOpponent == null) {
            return;
        }
        
        fThinkTimer += fDeltaTime;
        fActionTimer += fDeltaTime;
        
        // 定期的に行動を決定
        if (fThinkTimer >= fThinkInterval) {
            fThinkTimer = 0f;
            DecideAction();
        }
    }
    
    //行動を決定
    private void DecideAction() {
        float fDistance = Math.abs(pOpponent.GetPositionX() - pSelf.GetPositionX());
        // 空中にいるかどうかを状態で判定（JUMP状態なら空中）
        boolean bIsGrounded = (pSelf.GetCurrentState() != CharacterState.JUMP);
        
        // 空中にいる場合
        if (!bIsGrounded) {
            // 30%の確率で空中ダッシュ
            if (pRandom.nextFloat() < 0.3f) {
                eCurrentAction = AIAction.AIR_DASH;
                fActionTimer = 0f;
                return;
            }
            // 空中では入力を保持
            return;
        }
        
        // 距離に応じた行動選択
        if (fDistance < fCloseDistance) {
            // 近距離：後退、攻撃、ジャンプなど
            float fRand = pRandom.nextFloat();
            if (fRand < 0.25f) {
                eCurrentAction = AIAction.MOVE_BACKWARD;
            } else if (fRand < 0.4f) {
                eCurrentAction = AIAction.LIGHT_ATTACK; // 弱攻撃
            } else if (fRand < 0.5f) {
                eCurrentAction = AIAction.JUMP_BACKWARD; // ジャンプ頻度削減
            } else if (fRand < 0.7f) {
                eCurrentAction = AIAction.DASH_BACKWARD;
            } else if (fRand < 0.85f) {
                eCurrentAction = AIAction.MEDIUM_ATTACK; // 中攻撃
            } else {
                eCurrentAction = AIAction.CROUCH;
            }
        } else if (fDistance > fFarDistance) {
            // 遠距離：前進、ダッシュ（ジャンプ頻度削減）
            float fRand = pRandom.nextFloat();
            if (fRand < 0.55f) {
                eCurrentAction = AIAction.MOVE_FORWARD;
            } else if (fRand < 0.65f) {
                eCurrentAction = AIAction.JUMP_FORWARD; // ジャンプ頻度削減（10%）
            } else {
                eCurrentAction = AIAction.DASH_FORWARD;
            }
        } else {
            // 理想の間合い：攻撃中心、様々な行動
            float fRand = pRandom.nextFloat();
            if (fRand < 0.2f) {
                eCurrentAction = AIAction.LIGHT_ATTACK; // 弱攻撃
            } else if (fRand < 0.35f) {
                eCurrentAction = AIAction.MEDIUM_ATTACK; // 中攻撃
            } else if (fRand < 0.45f) {
                eCurrentAction = AIAction.HEAVY_ATTACK; // 強攻撃
            } else if (fRand < 0.55f) {
                eCurrentAction = AIAction.IDLE;
            } else if (fRand < 0.65f) {
                eCurrentAction = AIAction.MOVE_FORWARD;
            } else if (fRand < 0.7f) {
                eCurrentAction = AIAction.MOVE_BACKWARD;
            } else if (fRand < 0.75f) {
                eCurrentAction = AIAction.JUMP_VERTICAL; // ジャンプ頻度削減（5%）
            } else if (fRand < 0.8f) {
                eCurrentAction = AIAction.CROUCH;
            } else if (fRand < 0.9f) {
                eCurrentAction = AIAction.DASH_FORWARD;
            } else {
                eCurrentAction = AIAction.DASH_BACKWARD;
            }
        }
        
        fActionTimer = 0f;
    }
    
    //入力を取得（オーバーライド）
    @Override
    public boolean GetInput(InputType eInputType) {
        if (pSelf == null || pOpponent == null) {
            return false;
        }
        
        // 相手が左右どちらにいるか判定
        boolean bOpponentOnRight = (pOpponent.GetPositionX() - pSelf.GetPositionX()) > 0;
        // 空中にいるかどうかを状態で判定（JUMP状態なら空中）
        boolean bIsGrounded = (pSelf.GetCurrentState() != CharacterState.JUMP);
        
        switch (eCurrentAction) {
            case IDLE:
                return false;
                
            case MOVE_FORWARD:
                if (eInputType == InputType.LEFT && !bOpponentOnRight) return true;
                if (eInputType == InputType.RIGHT && bOpponentOnRight) return true;
                return false;
                
            case MOVE_BACKWARD:
                if (eInputType == InputType.LEFT && bOpponentOnRight) return true;
                if (eInputType == InputType.RIGHT && !bOpponentOnRight) return true;
                return false;
                
            case JUMP_FORWARD:
                // 前ジャンプ（9方向）
                if (eInputType == InputType.JUMP) return true;
                if (bIsGrounded) {
                    if (eInputType == InputType.LEFT && !bOpponentOnRight) return true;
                    if (eInputType == InputType.RIGHT && bOpponentOnRight) return true;
                }
                return false;
                
            case JUMP_BACKWARD:
                // 後ろジャンプ（7方向）
                if (eInputType == InputType.JUMP) return true;
                if (bIsGrounded) {
                    if (eInputType == InputType.LEFT && bOpponentOnRight) return true;
                    if (eInputType == InputType.RIGHT && !bOpponentOnRight) return true;
                }
                return false;
                
            case JUMP_VERTICAL:
                // 垂直ジャンプ（8方向）
                if (eInputType == InputType.JUMP) return true;
                return false;
                
            case DASH_FORWARD:
                // 前ダッシュ（66コマンド）
                if (eInputType == InputType.LEFT && !bOpponentOnRight) return true;
                if (eInputType == InputType.RIGHT && bOpponentOnRight) return true;
                return false;
                
            case DASH_BACKWARD:
                // 後ダッシュ（44コマンド）
                if (eInputType == InputType.LEFT && bOpponentOnRight) return true;
                if (eInputType == InputType.RIGHT && !bOpponentOnRight) return true;
                return false;
                
            case AIR_DASH:
                // 空中ダッシュ
                if (eInputType == InputType.LEFT && !bOpponentOnRight) return true;
                if (eInputType == InputType.RIGHT && bOpponentOnRight) return true;
                return false;
                
            case CROUCH:
                // しゃがみ（下方向）
                if (eInputType == InputType.CROUCH) return true;
                return false;
                
            case LIGHT_ATTACK:
                // 弱攻撃
                if (eInputType == InputType.LIGHTATTACK5) return true;
                return false;
                
            case MEDIUM_ATTACK:
                // 中攻撃
                if (eInputType == InputType.MEDIUMATTACK5) return true;
                return false;
                
            case HEAVY_ATTACK:
                // 強攻撃
                if (eInputType == InputType.HEAVYATTACK5) return true;
                return false;
                
            default:
                return false;
        }
    }
}
