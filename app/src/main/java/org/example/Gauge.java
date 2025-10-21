package org.example;

public class Gauge {
    private float fCurrentGauge = 0.0f;
    //現在のゲージ値
    private float fMaxGauge = 7.0f;
    //最大ゲージ値

    //ゲージを増加させるメソッド
    public void AddGauge(float amount) {
        fCurrentGauge = Math.min(fCurrentGauge + amount, fMaxGauge);
    }

    //ゲージを減少させるメソッド
    public void SubtractGauge(float amount) {
        fCurrentGauge = Math.max(fCurrentGauge - amount, 0.0f);
    }

    //現在のゲージ値を取得するメソッド
    public float GetCurrentGauge() {
        return fCurrentGauge;
    }

    //最大ゲージ値を取得するメソッド
    public float GetMaxGauge() {
        return fMaxGauge;
    }
}
