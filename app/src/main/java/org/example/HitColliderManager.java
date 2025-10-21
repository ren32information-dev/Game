package org.example;

import java.lang.reflect.Array;
import java.util.ArrayList;

// 同じ種類の当たり判定ボックスをまとめて管理するクラス
// 被弾判定や攻撃判定を持つキャラクターに紐づけて使用する
public class HitColliderManager {

    ArrayList<HitColliderBox> pHitColliders;
    boolean bIsChecked = false;

    public HitColliderManager() {
        pHitColliders = new ArrayList<>();
    }
    //当たり判定ボックスを追加
    public void AddHitCollider(HitColliderBox pHitCollider) {
        pHitColliders.add(pHitCollider);
    }
    //当たり判定ボックスリストを取得
    public ArrayList<HitColliderBox> GetHitColliders() {
        return pHitColliders;
    }

    public boolean IsChecked() {
        return bIsChecked;
    }
}
