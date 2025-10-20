package org.example;

/**
 * プレイヤー基底クラス
 * HP、入力管理、戦闘の基本機能を提供
 */
public class Player {
    protected int nPlayerNumber;
    //プレイヤー番号（1 or 2）
    protected InputManager pInputManager;
    //入力管理
    protected HitColliderBox pHurtBox;
    //被弾判定（やられ判定）
    
    protected int nHP;
    //体力
    protected int nMaxHP;
    //最大体力
    
    //コンストラクタ
    public Player(int nPlayerNumber, InputManager pInputManager) {
        this.nPlayerNumber = nPlayerNumber;
        this.pInputManager = pInputManager;
        
        // 被弾判定を設定（デフォルト）
        this.pHurtBox = new HitColliderBox(1.0f, 2.0f);
        
        // 体力初期化
        this.nMaxHP = 100;
        this.nHP = nMaxHP;
        
        System.out.println("[Player] プレイヤー" + nPlayerNumber + " を作成しました");
    }
    
    //更新処理（子クラスでオーバーライド可能）
    public void Update(float fDeltaTime) {
        // 基本的な更新処理（必要に応じて子クラスで拡張）
    }
    
    //プレイヤー番号を取得
    public int GetPlayerNumber() {
        return nPlayerNumber;
    }
    
    //入力マネージャーを取得
    public InputManager GetInputManager() {
        return pInputManager;
    }
    
    //被弾判定を取得
    public HitColliderBox GetHurtBox() {
        return pHurtBox;
    }
    
    //被弾判定を設定
    public void SetHurtBox(HitColliderBox pHurtBox) {
        this.pHurtBox = pHurtBox;
    }
    
    //体力を取得
    public int GetHP() {
        return nHP;
    }
    
    //最大体力を取得
    public int GetMaxHP() {
        return nMaxHP;
    }
    
    //ダメージを受ける
    public void TakeDamage(int nDamage) {
        nHP -= nDamage;
        if (nHP < 0) nHP = 0;
        System.out.println("[Player" + nPlayerNumber + "] ダメージ: " + nDamage + " (残りHP: " + nHP + ")");
    }
    
    //体力を回復
    public void Heal(int nHealAmount) {
        nHP += nHealAmount;
        if (nHP > nMaxHP) nHP = nMaxHP;
    }
    
    //生存しているか
    public boolean IsAlive() {
        return nHP > 0;
    }
}

