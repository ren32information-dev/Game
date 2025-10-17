package org.example;

/**
 * 衝突判定を管理するクラス
 * シンプルな衝突判定のみを提供
 * 必要に応じて機能を追加・改造してください
 */
public class CollisionManager {
    
    /**
     * 2つのキャラクターが衝突しているかをチェック
     * @return 衝突している場合true
     */
    public static boolean CheckCharacterCollision(Character pChar1, Character pChar2) {
        HitColliderBox pBox1 = pChar1.GetHitBox();
        HitColliderBox pBox2 = pChar2.GetHitBox();
        
        if (pBox1 == null || pBox2 == null) {
            return false;
        }
        
        // 各ボックスの境界を取得
        float fLeft1 = pBox1.GetLeft(pChar1.GetPositionX());
        float fRight1 = pBox1.GetRight(pChar1.GetPositionX());
        float fBottom1 = pBox1.GetBottom(pChar1.GetPositionY());
        float fTop1 = pBox1.GetTop(pChar1.GetPositionY());
        
        float fLeft2 = pBox2.GetLeft(pChar2.GetPositionX());
        float fRight2 = pBox2.GetRight(pChar2.GetPositionX());
        float fBottom2 = pBox2.GetBottom(pChar2.GetPositionY());
        float fTop2 = pBox2.GetTop(pChar2.GetPositionY());
        
        // AABB（Axis-Aligned Bounding Box）衝突判定
        // X軸とY軸の両方で重なっていれば衝突
        boolean bOverlapX = fLeft1 < fRight2 && fRight1 > fLeft2;
        boolean bOverlapY = fBottom1 < fTop2 && fTop1 > fBottom2;
        
        return bOverlapX && bOverlapY;
    }
}

