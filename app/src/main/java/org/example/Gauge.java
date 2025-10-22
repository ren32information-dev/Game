package org.example;

public class Gauge {
    private float fCurrentGauge;
    //現在のゲージ値
    
    private float fMaxGauge = 7.0f;
    //最大ゲージ値

    private int nCurrentBars;
    // ゲージ本数

    //ゲージ初期化
    public Gauge() 
    {
        this.fCurrentGauge = 0.7f;
        this.nCurrentBars = 3;
    }   

    //ゲージを増加させるメソッド
    public void AddGauge(float amount) 
    {
        this.fCurrentGauge += amount;

        //ゲージ最大になるのチェック
        while (fCurrentGauge >= 1.0f && nCurrentBars < fMaxGauge) 
        {
            nCurrentBars++;
            fCurrentGauge -= 1.0f;

            if (nCurrentBars == fMaxGauge) 
            {
                nCurrentBars = (int)fMaxGauge; 
                break; 
            }
        }
    }

    //ゲージを減少させるメソッド
    public void SubtractGauge(float amount) 
    {
        this.fCurrentGauge -= amount;

        while (fCurrentGauge < 0.0f && nCurrentBars > 0) 
        {
            nCurrentBars--;
            fCurrentGauge += 1.0f; 
        }

        if (nCurrentBars == 0 && fCurrentGauge < 0.0f) {
            fCurrentGauge = 0.0f;
        }
    }

    //現在のゲージ値を取得するメソッド
    public float GetCurrentGauge() {
        return fCurrentGauge;
    }

    //最大ゲージ値を取得するメソッド
    public float GetMaxGauge() {
        return fMaxGauge;
    }

    public int GetCurrentBars() 
    {
        return nCurrentBars;
    }
}
