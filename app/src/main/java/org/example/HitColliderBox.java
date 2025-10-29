package org.example;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

/**
 * 当たり判定ボックス（矩形の衝突判定）
 */
public class HitColliderBox {
    private float fWidth;
    //幅
    private float fHeight;
    //高さ
    private float fOffsetX;
    //キャラクターの中心からのX方向オフセット
    private float fOffsetY;
    //キャラクターの中心からのY方向オフセット
    private Renderer debugRenderer;

    
    public HitColliderBox(float fWidth, float fHeight) {
        this.fWidth = fWidth;
        this.fHeight = fHeight;
        this.fOffsetX = 0f;
        this.fOffsetY = 0f;
    }
    
    public HitColliderBox(float fWidth, float fHeight, float fOffsetX, float fOffsetY) {
        this.fWidth = fWidth;
        this.fHeight = fHeight;
        this.fOffsetX = fOffsetX;
        this.fOffsetY = fOffsetY;
    }
    
    //幅を取得
    public float GetWidth() {
        return fWidth;
    }
    
    //高さを取得
    public float GetHeight() {
        return fHeight;
    }
    
    //X方向オフセットを取得
    public float GetOffsetX() {
        return fOffsetX;
    }
    
    //Y方向オフセットを取得
    public float GetOffsetY() {
        return fOffsetY;
    }
    
    //幅を設定
    public void SetWidth(float fWidth) {
        this.fWidth = fWidth;
    }
    
    //高さを設定
    public void SetHeight(float fHeight) {
        this.fHeight = fHeight;
    }
    
    //オフセットを設定
    public void SetOffset(float fOffsetX, float fOffsetY) {
        this.fOffsetX = fOffsetX;
        this.fOffsetY = fOffsetY;
    }
    
    /**
     * 指定された位置での当たり判定の左端を取得
     */
    public float GetLeft(float fCenterX) {
        return fCenterX + fOffsetX - fWidth / 2.0f;
    }
    
    /**
     * 指定された位置での当たり判定の右端を取得
     */
    public float GetRight(float fCenterX) {
        return fCenterX + fOffsetX + fWidth / 2.0f;
    }
    
    /**
     * 指定された位置での当たり判定の下端を取得
     */
    public float GetBottom(float fCenterY) {
        return fCenterY + fOffsetY - fHeight / 2.0f;
    }
    
    /**
     * 指定された位置での当たり判定の上端を取得
     */
    public float GetTop(float fCenterY) {
        return fCenterY + fOffsetY + fHeight / 2.0f;
    }
    
    /**
     * 指定された位置での当たり判定の中心Xを取得
     */
    public float GetCenterX(float fCharacterX) {
        return fCharacterX + fOffsetX;
    }
    
    /**
     * 指定された位置での当たり判定の中心Yを取得
     */
    public float GetCenterY(float fCharacterY) {
        return fCharacterY + fOffsetY;
    }

    /*
     * 描画処理（デバッグ用）
     */
    public void Draw(Camera pCamera, float fCharacterX, float fCharacterY) {
        if (debugRenderer == null) {
            
            debugRenderer = new Renderer(pCamera);
            debugRenderer.Init("UI/gray.png");
            
        }

        debugRenderer.UISize(fWidth / 2, fHeight / 2, 1.0f);
        debugRenderer.UIColor(1.0f, 1.0f, 1.0f);
        
        float fPosX = GetCenterX(fCharacterX);
        float fPosY = GetCenterY(fCharacterY);

        debugRenderer.UIPos(fPosX, fPosY, 0.0f);
        debugRenderer.Draw();
    }
}

