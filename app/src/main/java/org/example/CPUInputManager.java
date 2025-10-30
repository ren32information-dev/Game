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
    private float fAttackRange;
    //攻撃が届く距離
    private float fDashRange;
    //ダッシュで一気に詰められる距離
    private float fLightAttackRange;
    //弱攻撃の最適距離
    private float fMediumAttackRange;
    //中攻撃の最適距離
    private float fHeavyAttackRange;
    //強攻撃の最適距離
    
    private AIAction eCurrentAction;
    //現在の行動
    private float fActionTimer;
    //行動タイマー
    
    private float fOpponentPrevX;
    //前フレームの相手X座標
    private float fOpponentPrevY;
    //前フレームの相手Y座標
    private CharacterState eOpponentPrevState;
    //前フレームの相手の状態
    
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
        this.fThinkInterval = 0.2f; // 0.2秒ごとに行動を更新（より早く反応）
        this.fIdealDistance = 4.5f; // 理想の間合い
        this.fCloseDistance = 2.5f; // 近距離
        this.fFarDistance = 7.0f; // 遠距離
        this.fAttackRange = 2.5f; // 攻撃が届く距離
        this.fDashRange = 5.0f; // ダッシュで一気に詰められる距離
        this.fLightAttackRange = 1.5f; // 弱攻撃：牽制用、短いリーチ
        this.fMediumAttackRange = 2.2f; // 中攻撃：中距離の隙狩り
        this.fHeavyAttackRange = 1.8f; // 強攻撃：近距離の確実な隙狩り
        this.eCurrentAction = AIAction.IDLE;
        this.fActionTimer = 0f;
        this.fOpponentPrevX = 0f;
        this.fOpponentPrevY = 0f;
        this.eOpponentPrevState = null;
        
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
            
            // 前フレームの情報を記録
            fOpponentPrevX = pOpponent.GetPositionX();
            fOpponentPrevY = pOpponent.GetPositionY();
            eOpponentPrevState = pOpponent.GetCurrentState();
        }
    }
    
    //行動を決定
    private void DecideAction() {
        float fDistance = Math.abs(pOpponent.GetPositionX() - pSelf.GetPositionX());
        CharacterState eOpponentState = pOpponent.GetCurrentState();
        float fOpponentY = pOpponent.GetPositionY();
        
        // 空中にいるかどうかを状態で判定（JUMP状態なら空中）
        boolean bSelfIsGrounded = (pSelf.GetCurrentState() != CharacterState.JUMP);
        boolean bOpponentIsGrounded = (eOpponentState != CharacterState.JUMP);
        
        // 相手の移動方向を判定
        float fOpponentMoveX = 0f;
        if (eOpponentPrevState != null) {
            fOpponentMoveX = pOpponent.GetPositionX() - fOpponentPrevX;
        }
        boolean bOpponentApproaching = (fOpponentMoveX * (pOpponent.GetPositionX() - pSelf.GetPositionX()) > 0);
        
        // 相手が攻撃中かどうか
        boolean bOpponentAttacking = IsAttackState(eOpponentState);
        
        // 相手がジャンプ中かどうか
        boolean bOpponentJumping = (eOpponentState == CharacterState.JUMP);
        
        // 相手がダッシュ中かどうか
        boolean bOpponentDashing = (eOpponentState == CharacterState.DASH);
        
        // 自分が空中にいる場合
        if (!bSelfIsGrounded) {
            // 相手が近く、接近してきている場合は空中ダッシュで距離を詰める
            if (fDistance < fDashRange && bOpponentApproaching && pRandom.nextFloat() < 0.5f) {
                eCurrentAction = AIAction.AIR_DASH;
                fActionTimer = 0f;
                return;
            }
            // それ以外は30%の確率で空中ダッシュ
            if (pRandom.nextFloat() < 0.3f) {
                eCurrentAction = AIAction.AIR_DASH;
                fActionTimer = 0f;
                return;
            }
            // 空中では入力を保持
            return;
        }
        
        // === 着地狩り判定 ===
        if (bOpponentJumping && fOpponentY < 4.0f && fDistance < 5.0f) {
            // 相手がジャンプ中で低い位置にいる（着地が近い）、かつ距離が近い
            // 着地地点に先回りして攻撃
            float fOpponentLandingX = pOpponent.GetPositionX() + fOpponentMoveX * 2.5f; // 着地地点を予測
            float fLandingDistance = Math.abs(fOpponentLandingX - pSelf.GetPositionX());
            
            if (fLandingDistance < fAttackRange) {
                // 攻撃範囲内なら最適な攻撃で着地狩り（ジャンプは確実な隙）
                eCurrentAction = ChooseOptimalAttack(fLandingDistance, true, true);
                if (eCurrentAction != AIAction.IDLE) {
                    fActionTimer = 0f;
                    return;
                }
            } else if (fLandingDistance < fDashRange) {
                // ダッシュ範囲内ならダッシュで着地地点に詰める（積極的に）
                if (pRandom.nextFloat() < 0.8f) {
                    eCurrentAction = AIAction.DASH_FORWARD; // 80%でダッシュ
                } else {
                    eCurrentAction = AIAction.MOVE_FORWARD; // 20%で歩き
                }
                fActionTimer = 0f;
                return;
            }
        }
        
        // === 相手が隙を晒している場合（攻撃中、ジャンプ中） ===
        if ((bOpponentAttacking || bOpponentJumping) && fDistance < fDashRange && fDistance > fAttackRange) {
            // 隙を突いてダッシュで一気に詰める（積極的に）
            if (pRandom.nextFloat() < 0.85f) {
                eCurrentAction = AIAction.DASH_FORWARD; // 85%でダッシュ
                fActionTimer = 0f;
                return;
            } else {
                eCurrentAction = AIAction.JUMP_FORWARD; // 15%でジャンプで詰める
                fActionTimer = 0f;
                return;
            }
        }
        
        // === 相手が接近してくる場合（ダッシュ、前ジャンプ） ===
        if (bOpponentApproaching && (bOpponentDashing || bOpponentJumping) && fDistance < fIdealDistance) {
            float fRand = pRandom.nextFloat();
            if (fRand < 0.35f) {
                // 後退して距離を取る
                eCurrentAction = AIAction.DASH_BACKWARD;
            } else if (fRand < 0.65f) {
                // 後ろジャンプで回避
                eCurrentAction = AIAction.JUMP_BACKWARD;
            } else {
                // 迎撃攻撃（接近してくるプレイヤーに対して、距離に応じた攻撃）
                if (fDistance < fAttackRange) {
                    // ダッシュやジャンプは隙なので強めの攻撃で迎撃
                    eCurrentAction = ChooseOptimalAttack(fDistance, true, bOpponentJumping);
                    if (eCurrentAction == AIAction.IDLE) {
                        eCurrentAction = AIAction.MOVE_BACKWARD;
                    }
                } else {
                    eCurrentAction = AIAction.MOVE_BACKWARD;
                }
            }
            fActionTimer = 0f;
            return;
        }
        
        // === 通常の間合い管理 ===
        if (fDistance < fCloseDistance) {
            // 超近距離：積極的に攻撃
            if (fDistance < fAttackRange) {
                // 攻撃範囲内なら攻撃（通常時：隙なし）
                float fRand = pRandom.nextFloat();
                if (fRand < 0.75f) {
                    // 75%で攻撃を選択（積極的に）
                    eCurrentAction = ChooseOptimalAttack(fDistance, false, false);
                    if (eCurrentAction == AIAction.IDLE) {
                        eCurrentAction = AIAction.MOVE_BACKWARD;
                    }
                } else {
                    eCurrentAction = AIAction.MOVE_BACKWARD; // たまに離脱
                }
            } else {
                // 攻撃が届かないなら離脱優先
                float fRand = pRandom.nextFloat();
                if (fRand < 0.5f) {
                    eCurrentAction = AIAction.MOVE_BACKWARD;
                } else if (fRand < 0.7f) {
                    eCurrentAction = AIAction.DASH_BACKWARD;
                } else if (fRand < 0.85f) {
                    eCurrentAction = AIAction.JUMP_BACKWARD;
                } else {
                    eCurrentAction = AIAction.CROUCH;
                }
            }
        } else if (fDistance > fFarDistance) {
            // 遠距離：基本移動、たまにジャンプやダッシュ
            float fRand = pRandom.nextFloat();
            if (fRand < 0.65f) {
                eCurrentAction = AIAction.MOVE_FORWARD; // 移動メイン
            } else if (fRand < 0.75f) {
                eCurrentAction = AIAction.JUMP_FORWARD; // たまにジャンプ
            } else {
                eCurrentAction = AIAction.DASH_FORWARD; // たまにダッシュ
            }
        } else if (fDistance > fIdealDistance) {
            // 中距離（理想の間合いより遠い）：基本移動、チャンスがあればダッシュ
            float fRand = pRandom.nextFloat();
            if (fRand < 0.6f) {
                eCurrentAction = AIAction.MOVE_FORWARD; // 移動メイン
            } else if (fRand < 0.7f) {
                eCurrentAction = AIAction.IDLE; // 様子見
            } else if (fRand < 0.85f) {
                eCurrentAction = AIAction.DASH_FORWARD; // チャンスを狙ってダッシュ
            } else {
                eCurrentAction = AIAction.JUMP_FORWARD; // たまにジャンプ
            }
        } else {
            // 理想の間合い：積極的に攻める
            // 攻撃が届く距離でのみ攻撃を選択
            if (fDistance < fAttackRange) {
                float fRand = pRandom.nextFloat();
                if (fRand < 0.65f) {
                    // 65%で攻撃（積極的に攻める）
                    eCurrentAction = ChooseOptimalAttack(fDistance, false, false);
                    if (eCurrentAction == AIAction.IDLE) {
                        eCurrentAction = AIAction.MOVE_FORWARD;
                    }
                } else if (fRand < 0.8f) {
                    eCurrentAction = AIAction.MOVE_FORWARD;
                } else if (fRand < 0.9f) {
                    eCurrentAction = AIAction.MOVE_BACKWARD;
                } else {
                    eCurrentAction = AIAction.IDLE; // 様子見
                }
            } else {
                // 攻撃が届かない距離では移動と様子見のみ
                float fRand = pRandom.nextFloat();
                if (fRand < 0.5f) {
                    eCurrentAction = AIAction.MOVE_FORWARD;
                } else if (fRand < 0.7f) {
                    eCurrentAction = AIAction.IDLE; // 様子見
                } else if (fRand < 0.8f) {
                    eCurrentAction = AIAction.MOVE_BACKWARD;
                } else if (fRand < 0.9f) {
                    eCurrentAction = AIAction.JUMP_VERTICAL;
                } else {
                    eCurrentAction = AIAction.CROUCH;
                }
            }
        }
        
        fActionTimer = 0f;
    }
    
    //攻撃状態かどうかを判定
    private boolean IsAttackState(CharacterState eState) {
        return eState == CharacterState.ATTACK ||
               eState == CharacterState.LIGHTATTACK5 ||
               eState == CharacterState.MEDIUMATTACK5 ||
               eState == CharacterState.HEAVYATTACK5 ||
               eState == CharacterState.LIGHTATTACK2 ||
               eState == CharacterState.MEDIUMATTACK2 ||
               eState == CharacterState.HEAVYATTACK2 ||
               eState == CharacterState.MEDIUMATTACK6 ||
               eState == CharacterState.HEAVYATTACK3;
    }
    
    //状況に応じた最適な攻撃を選択
    private AIAction ChooseOptimalAttack(float fDistance, boolean bOpponentVulnerable, boolean bOpponentJumping) {
        // プレイヤーがジャンプ中（確実なチャンス！積極的に攻める）
        if (bOpponentJumping) {
            if (fDistance <= fHeavyAttackRange) {
                // 近距離なら強攻撃で確実に狩る
                float fRand = pRandom.nextFloat();
                if (fRand < 0.7f) {
                    return AIAction.HEAVY_ATTACK; // 70%で強攻撃
                } else {
                    return AIAction.MEDIUM_ATTACK; // 30%で中攻撃
                }
            } else if (fDistance <= fMediumAttackRange) {
                // 中距離なら中攻撃で着地狩り
                float fRand = pRandom.nextFloat();
                if (fRand < 0.75f) {
                    return AIAction.MEDIUM_ATTACK; // 75%で中攻撃
                } else {
                    return AIAction.HEAVY_ATTACK; // 25%で強攻撃も狙う
                }
            } else if (fDistance <= fAttackRange) {
                // ギリギリ届く距離でも攻撃を出す
                return AIAction.MEDIUM_ATTACK; // 中攻撃でリーチを活かす
            }
            // 届かない距離ならIDLE（移動は別のロジックで処理される）
            return AIAction.IDLE;
        }
        
        // プレイヤーが大きな隙を晒している場合（攻撃中など）
        if (bOpponentVulnerable) {
            if (fDistance <= fHeavyAttackRange) {
                // 近距離で確実な隙なら強攻撃優先
                float fRand = pRandom.nextFloat();
                if (fRand < 0.8f) {
                    return AIAction.HEAVY_ATTACK; // 80%で強攻撃
                } else {
                    return AIAction.MEDIUM_ATTACK; // 20%で中攻撃
                }
            } else if (fDistance <= fMediumAttackRange) {
                // 中距離の隙なら中攻撃
                float fRand = pRandom.nextFloat();
                if (fRand < 0.85f) {
                    return AIAction.MEDIUM_ATTACK; // 85%で中攻撃
                } else {
                    return AIAction.HEAVY_ATTACK; // 15%で強攻撃も
                }
            } else if (fDistance <= fAttackRange) {
                // 遠めの距離でも隙なら攻撃を出す
                return AIAction.MEDIUM_ATTACK;
            }
        }
        
        // 通常時の攻撃選択（距離に応じた使い分け）
        if (fDistance <= fLightAttackRange) {
            // 弱攻撃範囲：牽制だが、たまに強めの攻撃も
            float fRand = pRandom.nextFloat();
            if (fRand < 0.5f) {
                return AIAction.LIGHT_ATTACK; // 50%で弱攻撃（牽制を減らした）
            } else if (fRand < 0.75f) {
                return AIAction.MEDIUM_ATTACK; // 25%で中攻撃
            } else {
                return AIAction.HEAVY_ATTACK; // 25%で強攻撃（リスクを取る）
            }
        } else if (fDistance <= fHeavyAttackRange) {
            // 強攻撃が届く範囲：中攻撃メイン
            float fRand = pRandom.nextFloat();
            if (fRand < 0.3f) {
                return AIAction.LIGHT_ATTACK; // 30%で牽制（減らした）
            } else if (fRand < 0.75f) {
                return AIAction.MEDIUM_ATTACK; // 45%で中攻撃
            } else {
                return AIAction.HEAVY_ATTACK; // 25%で強攻撃
            }
        } else if (fDistance <= fMediumAttackRange) {
            // 中攻撃範囲：中攻撃メイン
            float fRand = pRandom.nextFloat();
            if (fRand < 0.75f) {
                return AIAction.MEDIUM_ATTACK; // 75%で中攻撃（増やした）
            } else {
                return AIAction.LIGHT_ATTACK; // 25%で弱攻撃
            }
        } else if (fDistance <= fAttackRange) {
            // ギリギリ届く距離：中攻撃でリーチを活かす
            float fRand = pRandom.nextFloat();
            if (fRand < 0.6f) {
                return AIAction.MEDIUM_ATTACK; // 60%で中攻撃
            } else {
                return AIAction.LIGHT_ATTACK; // 40%で弱攻撃
            }
        }
        
        // 攻撃が届かない距離
        return AIAction.IDLE;
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
