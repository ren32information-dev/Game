package org.example;
import java.util.HashMap;

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
    public static void CheckCharacterCollision(Character pChar1, Character pChar2) {
        HashMap<String, HitColliderManager> pBox1 = pChar1.GetHitBoxs();
        HashMap<String, HitColliderManager> pBox2 = pChar2.GetHitBoxs();

        if (pBox1 == null || pBox2 == null) {
            return;
        }
        
        for(String manager1 : pBox1.keySet())
        {
            for(String manager2 : pBox2.keySet())
            {
                for (HitColliderBox box1 : pBox1.get(manager1).GetHitColliders()) {
                    for (HitColliderBox box2 : pBox2.get(manager2).GetHitColliders()) {
   
                        // 各ボックスの境界を取得
                        float fLeft1 = box1.GetLeft(pChar1.GetPositionX());
                        float fRight1 = box1.GetRight(pChar1.GetPositionX());
                        float fBottom1 = box1.GetBottom(pChar1.GetPositionY());
                        float fTop1 = box1.GetTop(pChar1.GetPositionY());

                        float fLeft2 = box2.GetLeft(pChar2.GetPositionX());
                        float fRight2 = box2.GetRight(pChar2.GetPositionX());
                        float fBottom2 = box2.GetBottom(pChar2.GetPositionY());
                        float fTop2 = box2.GetTop(pChar2.GetPositionY());

                        // AABB（Axis-Aligned Bounding Box）衝突判定
                        // X軸とY軸の両方で重なっていれば衝突
                        boolean bOverlapX = fLeft1 < fRight2 && fRight1 > fLeft2;
                        boolean bOverlapY = fBottom1 < fTop2 && fTop1 > fBottom2;

                        if(bOverlapX && bOverlapY) {

                            
                            pChar1.OnCollision(pChar2, manager1, manager2);
                            pChar2.OnCollision(pChar1, manager2, manager1);
                        }
                    }
                }
            }
        }
    }
}

