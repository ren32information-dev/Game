package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Character {
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
    
    public Character(float fX, float fY, float fZ) {
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
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 2.0f, 0.5f, 1.0f));
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 2.0f, -0.5f, 1.0f));
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 2.0f, 0.5f, -1.0f));
        this.pAllColliders.get("my").AddHitCollider(new HitColliderBox(1.0f, 2.0f, -0.5f, -1.0f));

        // HP オブジェクトを初期化
        this.pHP = new HP();

        // ゲージオブジェクトを初期化
        this.pGauge = new Gauge();
    }
    
    //画像を設定
    public void SetTexture(int nTextureId) {
        this.nTextureId = nTextureId;
    }
    
    //画像IDを取得
    public int GetTexture() {
        return nTextureId;
    }
    
    //更新処理
    public void Update(float fDeltaTime) {
        // 重力を適用
        fVelocityY += fGravity;
        fPositionY += fVelocityY;
        pHP.update();
        
        // 地面判定
        if (fPositionY <= fGroundLevel) {
            fPositionY = fGroundLevel;
            fVelocityY = 0;
            bIsGrounded = true;
        } else {
            bIsGrounded = false;
        }
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

    // ダメージが入る　デバッグ用
    public void DamageHP() {
        pHP.DamageHP(10);
    }

    // 回復が入る　デバッグ用
    public void HealHP() {
        pHP.HealHP(10);
    }   

    // 取得 HP オブジェクト
    public HP GetHP() {
        return pHP;
    }

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

        if (sTag1.equals("player") && sTag2.equals("player")) {
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
}

