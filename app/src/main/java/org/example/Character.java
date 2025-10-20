package org.example;

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
    
    private boolean bIsGrounded;
    //地面に接地しているか
    private float fGroundLevel;
    //地面の高さ
    
    private int nTextureId;
    //キャラクターの画像ID
    
    private HitColliderBox pHitBox;
    //当たり判定ボックス
    
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
        
        // デフォルトの当たり判定ボックスを作成（幅1.0、高さ2.0）
        this.pHitBox = new HitColliderBox(1.0f, 2.0f);
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

    public void OnCollision(Character pOther) {
        // 衝突時の処理（必要に応じて実装）
        String sTag1 = pHitBox.GetTag();
        String sTag2 = pOther.GetHitBox().GetTag();

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

