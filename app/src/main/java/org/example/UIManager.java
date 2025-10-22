package org.example;
import org.lwjgl.glfw.GLFW;

public class UIManager 
{
    //===P1===
    private static final int P1_HP = 0; 
    private static final int P1_MAXHP = 1;
    private static final int P1_Gauge = 2; 
    private static final int P1_MAXGauge = 3;
    private static final int P1_Gauge_Number = 4;
    //===P1===
    private static final int MAX_UI_ELEMENTS = 5; 
    
    private Renderer[] uiElements; 
    private Camera mainCamera; 
    
    private final float HP_BAR_MAX_X = -5.0f;       // -5.0fはHPX軸の中心
    private final float Gauge_BAR_MAX_X = -5.0f;    // -6.0fはGaugeX軸の中心
    private final float Gauge_BAR_MAX_Y = -3.5f;    // -6.0fはGaugeY軸の中心
    //===数字UV===
    private static final int NUMBER_OF_COLUMNS = 5;
    private static final int NUMBER_OF_ROWS = 5;  
    private static final float UV_UNIT_U = 1.0f / NUMBER_OF_COLUMNS; 
    private static final float UV_UNIT_V = 1.0f / NUMBER_OF_ROWS;
    //===数字UV===

    final float MAX_HP_BAR_WIDTH = 4.0f;        //最大HPの長さ
    final float MAX_Gauge_BAR_WIDTH = 3.0f;     //最大Gaugeの長さ
    
    private Character playerCharacter;

    public UIManager(Camera camera,Character character) 
    {
        this.mainCamera = camera;
        this.playerCharacter = character;
        this.uiElements = new Renderer[MAX_UI_ELEMENTS];
    }

    //===========================
    // 全てのUI要素を初期化
    //===========================
    public void initUI() 
    {
        for (int i = 0; i < MAX_UI_ELEMENTS; i++) 
        {
            uiElements[i] = new Renderer(mainCamera); 
        }

        // =================================================
        // 赤いHPバー (現在のHP)
        // =================================================
        uiElements[P1_HP].Init("UI/red.png");
        uiElements[P1_HP].UIPos(HP_BAR_MAX_X, 14.0f, 0.0f); 
        uiElements[P1_HP].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_HP].UIColor(1.0f, 1.0f, 1.0f);
        
        // =================================================
        // 灰色HPバーの背景 (最大HP)
        // =================================================
        uiElements[P1_MAXHP].Init("UI/gray.png");  
        uiElements[P1_MAXHP].UIPos(HP_BAR_MAX_X, 14.0f, 0.0f); 
        uiElements[P1_MAXHP].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_MAXHP].UIColor(1.0f, 1.0f, 1.0f); 

        // =================================================
        // 青色Gaugeバーの背景 (現在のGauge)
        // =================================================
        uiElements[P1_Gauge].Init("UI/blue.png");
        uiElements[P1_Gauge].UIPos(Gauge_BAR_MAX_X, Gauge_BAR_MAX_Y, 0.0f); 
        uiElements[P1_Gauge].UISize(MAX_Gauge_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_Gauge].UIColor(1.0f, 1.0f, 1.0f);


        // =================================================
        // 灰色Gaugeバーの背景 (最大Gauge)
        // =================================================
        uiElements[P1_MAXGauge].Init("UI/gray.png");  
        uiElements[P1_MAXGauge].UIPos(Gauge_BAR_MAX_X, Gauge_BAR_MAX_Y, 0.0f); 
        uiElements[P1_MAXGauge].UISize(MAX_Gauge_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_MAXGauge].UIColor(1.0f, 1.0f, 1.0f);
    
        // =================================================
        // Gauge本数 (最大Gauge)
        // =================================================
        uiElements[P1_Gauge_Number].Init("UI/number.png"); 
        uiElements[P1_Gauge_Number].UIPos(Gauge_BAR_MAX_X - 4.0f, Gauge_BAR_MAX_Y, 0.0f); 
        uiElements[P1_Gauge_Number].UISize(1.0f, 1.0f, 1.0f);
        uiElements[P1_Gauge_Number].UIColor(0.5f, 0.7f, 1.0f);
        
        DisplayNumber(P1_Gauge_Number, 2);
    }

    //===========================
    // 全てのUI要素を更新 (HPバーのロジックを完全に削除)
    //===========================
    public void update(float deltaTime)
    {
        
        //HP処理
        if (playerCharacter != null) 
        {
            float ratio = playerCharacter.GetHPObject().getHealthRatio();
            float currentWidth = MAX_HP_BAR_WIDTH * ratio;
            float originalPosX = HP_BAR_MAX_X; 
            float widthDifference = MAX_HP_BAR_WIDTH - currentWidth;
            float newPosX = originalPosX - widthDifference;
             
            uiElements[P1_HP].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P1_HP].UIPos(newPosX, 14.0f, 0.0f); 
        }
        
        /*
        //Gauge処理
        if (playerCharacter != null) 
        {
            float ratio = playerCharacter.GetGauge().GetCurrentGauge();
            float currentWidth = MAX_Gauge_BAR_WIDTH * ratio;
            float originalPosX = Gauge_BAR_MAX_X; 
            float widthDifference = MAX_Gauge_BAR_WIDTH - currentWidth;
            float newPosX = originalPosX - widthDifference;
            
            uiElements[P1_Gauge].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P1_Gauge].UIPos(newPosX, -3.5f, 0.0f); 
        }
        */

        if (playerCharacter != null) 
        {
            // ゲージインスタンスを取得 (GetGauge() が存在すると仮定)
            Gauge pGauge = playerCharacter.GetGauge();

            //ゲージバーの長さ (充填比率) を更新
            //GetCurrentGauge()は現在のゲージセルの充填比率 (0.0f〜1.0f) を返す
            float ratio = pGauge.GetCurrentGauge(); 
            float currentWidth = MAX_Gauge_BAR_WIDTH * ratio;
            
            // ゲージバーが幅 0 の時に正しい位置に配置されるように調整
            float originalPosX = Gauge_BAR_MAX_X; 
            float widthDifference = MAX_Gauge_BAR_WIDTH - currentWidth;
            float newPosX = originalPosX - (widthDifference / 2.0f); // 中心位置調整
            
            // 修正：ゲージバーが幅 0 の時に正しい位置に配置されるように調整
            newPosX = originalPosX - widthDifference;      
            newPosX = originalPosX - widthDifference;
            
            uiElements[P1_Gauge].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P1_Gauge].UIPos(newPosX, Gauge_BAR_MAX_Y, 0.0f); 

            //ゲージの数値 (エナジー格数) を更新
            int nBars = pGauge.GetCurrentBars();
            
            // 現在の整数ゲージ格数を表示
            // 最大が 7 なので、一桁の数字のみが必要です。
            DisplayNumber(P1_Gauge_Number, nBars);
        }

        for (Renderer renderer : uiElements) 
        {
            if (renderer != null) 
            {
                renderer.Update(deltaTime);
            }
        }

        //=====================DEBUG==========================
        long windowID = GLFW.glfwGetCurrentContext();
        
        if (GLFW.glfwGetKey(windowID, GLFW.GLFW_KEY_N) == GLFW.GLFW_PRESS) 
        {
            uiElements[P1_MAXHP].UIPos(-5.0f, 14.0f, 0.0f);
        }

        if (GLFW.glfwGetKey(windowID, GLFW.GLFW_KEY_M) == GLFW.GLFW_PRESS) 
        {
            uiElements[P1_MAXHP].UIPos(-5.0f, 14.0f, 0.0f);
        }
        //=====================DEBUG==========================
        if (GLFW.glfwGetKey(windowID, GLFW.GLFW_KEY_N) == GLFW.GLFW_PRESS) 
        {
             if (uiElements[P1_Gauge_Number] != null) 
             {
                DisplayNumber(P1_Gauge_Number, 1);
            }
        }
        if (GLFW.glfwGetKey(windowID, GLFW.GLFW_KEY_M) == GLFW.GLFW_PRESS) 
        {
            if (uiElements[P1_Gauge_Number] != null) 
            {
                DisplayNumber(P1_Gauge_Number, 9);
            }
        }

    }

    //===========================
    // 全てのUI要素を描画
    //===========================
    public void drawUI() 
    {
       for (Renderer renderer : uiElements) 
       {
           if (renderer != null) 
           {
               renderer.Draw();
           }
       }
    }
    
    //===========================
    // リソースを解放
    //===========================
    public void release() 
    {
        for (Renderer renderer : uiElements) 
        {
            if (renderer != null)
            {
                renderer.release();
            }
        }
    }
    
    //===========================
    // 外部から Renderer にアクセスするためのゲッター (必要に応じて追加)
    //===========================
    public Renderer getRenderer(int index) 
    {
        if (index >= 0 && index < MAX_UI_ELEMENTS) {
            return uiElements[index];
        }
        return null;
    }

    //===========================
    // プレイヤーキャラクターを設定
    //===========================
    public void SetPlayerCharacter(Character character) 
    {
        this.playerCharacter = character;
    }
    
    private void DisplayNumber(int elementIndex, int number) 
    {
       if (number < 0 || number > 9 || elementIndex >= MAX_UI_ELEMENTS) 
       {
            return;
        }

        Renderer numberRenderer = uiElements[elementIndex];
        
        int row = (number <= 4) ? 0 : 1;
        int col = (number <= 4) ? number : number - 5;

        float u_min = col * UV_UNIT_U;
        float u_max = u_min + UV_UNIT_U;

        float v_min = row * UV_UNIT_V;
        float v_max = v_min + UV_UNIT_V;
        
        numberRenderer.SetUIUV(u_min, v_min, u_max, v_max);
    }
}