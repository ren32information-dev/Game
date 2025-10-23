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
    
    //===タイマー===
    private static final int TIMER_DIGIT_TENS = 5;
    private static final int TIMER_DIGIT_ONES = 6;
    //===タイマー===

    // ===P2===
    private static final int P2_HP = 7; 
    private static final int P2_MAXHP = 8;
    private static final int P2_Gauge = 9; 
    private static final int P2_MAXGauge = 10;
    private static final int P2_Gauge_Number = 11;
    // ===P2===

    //===P1Skill===
    private static final int P1_SKILL_ICON_1 = 12;
    private static final int P1_SKILL_ICON_2 = 13;
    private static final int P1_SKILL_ICON_3 = 14;
    //===P1Skill===

    private static final int MAX_UI_ELEMENTS = 15; 
    
    private Renderer[] uiElements; 
    private Camera mainCamera; 
    
    //// P1 UI の座標定数
    private final float HP_BAR_MAX_X = -5.0f;       // -5.0fはHPX軸の中心
    private final float Gauge_BAR_MAX_X = -5.0f;    // -6.0fはGaugeX軸の中心
    private final float Gauge_BAR_MAX_Y = -3.5f;    // -6.0fはGaugeY軸の中心
    //スキルアイコン
    private final float P1_SKILL_ICON_START_X = -8.0f;
    private final float P1_SKILL_ICON_Y = 12.5f;
    private final float P1_SKILL_ICON_SIZE = 0.8f;
    // スキルCD
    private final float SKILL_COOLDOWN_MAX_1 = 5.0f;
    private final float SKILL_COOLDOWN_MAX_2 = 8.0f;
    private final float SKILL_COOLDOWN_MAX_3 = 12.0f;
    // スキル今のCD
    private float fSkillCooldownCurrent1 = 0.0f;
    private float fSkillCooldownCurrent2 = 0.0f;
    private float fSkillCooldownCurrent3 = 0.0f;
    
    //// P2 UI の座標定数
    private final float P2_HP_BAR_MAX_X = 5.0f; 
    private final float P2_Gauge_BAR_MAX_X = 5.0f;



    //===数字UV===
    private static final int NUMBER_OF_COLUMNS = 5;
    private static final int NUMBER_OF_ROWS = 5;  
    private static final float UV_UNIT_U = 1.0f / NUMBER_OF_COLUMNS; 
    private static final float UV_UNIT_V = 1.0f / NUMBER_OF_ROWS;
    //===数字UV===

    private float fGameTimer = 99.0f; // タイマー

    final float MAX_HP_BAR_WIDTH = 4.0f;        //最大HPの長さ
    final float MAX_Gauge_BAR_WIDTH = 3.0f;     //最大Gaugeの長さ
    
    private Character player1Character; // P1 キャラクター
    private Character player2Character; // P2 キャラクター

    public UIManager(Camera camera,Character character) 
    {
        this.mainCamera = camera;
        this.player1Character = character;
        //this.player2Character = character;
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
        // P1赤いHPバー (現在のHP)
        // =================================================
        uiElements[P1_HP].Init("UI/red.png");
        uiElements[P1_HP].UIPos(HP_BAR_MAX_X, 14.0f, 0.0f); 
        uiElements[P1_HP].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_HP].UIColor(1.0f, 1.0f, 1.0f);
        
        // =================================================
        // P1灰色HPバーの背景 (最大HP)
        // =================================================
        uiElements[P1_MAXHP].Init("UI/gray.png");  
        uiElements[P1_MAXHP].UIPos(HP_BAR_MAX_X, 14.0f, 0.0f); 
        uiElements[P1_MAXHP].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_MAXHP].UIColor(1.0f, 1.0f, 1.0f); 

        // =================================================
        // P1青色Gaugeバーの背景 (現在のGauge)
        // =================================================
        uiElements[P1_Gauge].Init("UI/blue.png");
        uiElements[P1_Gauge].UIPos(Gauge_BAR_MAX_X, Gauge_BAR_MAX_Y, 0.0f); 
        uiElements[P1_Gauge].UISize(MAX_Gauge_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_Gauge].UIColor(1.0f, 1.0f, 1.0f);


        // =================================================
        // P1灰色Gaugeバーの背景 (最大Gauge)
        // =================================================
        uiElements[P1_MAXGauge].Init("UI/gray.png");  
        uiElements[P1_MAXGauge].UIPos(Gauge_BAR_MAX_X, Gauge_BAR_MAX_Y, 0.0f); 
        uiElements[P1_MAXGauge].UISize(MAX_Gauge_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_MAXGauge].UIColor(1.0f, 1.0f, 1.0f);
    
        // =================================================
        // P1Gauge本数 (最大Gauge)
        // =================================================
        uiElements[P1_Gauge_Number].Init("UI/number.png"); 
        uiElements[P1_Gauge_Number].UIPos(Gauge_BAR_MAX_X - 4.0f, Gauge_BAR_MAX_Y, 0.0f); 
        uiElements[P1_Gauge_Number].UISize(1.0f, 1.0f, 1.0f);
        uiElements[P1_Gauge_Number].UIColor(0.5f, 0.7f, 1.0f);
        
        DisplayNumber(P1_Gauge_Number, 2);

        //Skill1
        uiElements[P1_SKILL_ICON_1].Init("UI/watermelon.png");
        uiElements[P1_SKILL_ICON_1].UIPos(P1_SKILL_ICON_START_X, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_ICON_1].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_ICON_1].UIColor(1.0f, 1.0f, 1.0f);
        
        //Skill2
        uiElements[P1_SKILL_ICON_2].Init("UI/cherry.png");
        uiElements[P1_SKILL_ICON_2].UIPos(P1_SKILL_ICON_START_X + 1.5f, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_ICON_2].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_ICON_2].UIColor(1.0f, 1.0f, 1.0f);
        
        //Skill3
        uiElements[P1_SKILL_ICON_3].Init("UI/bell.png");
        uiElements[P1_SKILL_ICON_3].UIPos(P1_SKILL_ICON_START_X + 3.0f, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_ICON_3].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_ICON_3].UIColor(1.0f, 1.0f, 1.0f);
        //===========================================================================================

        // =================================================
        // P2 HPバーの背景 (最大HP)
        // =================================================
        uiElements[P2_MAXHP].Init("UI/gray.png");
        uiElements[P2_MAXHP].UIPos(P2_HP_BAR_MAX_X, 14.0f, 0.0f); 
        uiElements[P2_MAXHP].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P2_MAXHP].UIColor(1.0f, 1.0f, 1.0f); 
        
        // =================================================
        // P2 赤いHPバー (現在のHP)
        // =================================================
        uiElements[P2_HP].Init("UI/red.png");
        uiElements[P2_HP].UIPos(P2_HP_BAR_MAX_X, 14.0f, 0.0f); 
        uiElements[P2_HP].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P2_HP].UIColor(1.0f, 1.0f, 1.0f);

        // =================================================
        // P2 灰色Gaugeバーの背景 (最大Gauge)
        // =================================================
        uiElements[P2_MAXGauge].Init("UI/gray.png");
        uiElements[P2_MAXGauge].UIPos(P2_Gauge_BAR_MAX_X, Gauge_BAR_MAX_Y, 0.0f); 
        uiElements[P2_MAXGauge].UISize(MAX_Gauge_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P2_MAXGauge].UIColor(1.0f, 1.0f, 1.0f);

        // =================================================
        // P2 青色Gaugeバー (現在のGauge)
        // =================================================
        uiElements[P2_Gauge].Init("UI/blue.png");
        uiElements[P2_Gauge].UIPos(P2_Gauge_BAR_MAX_X, Gauge_BAR_MAX_Y, 0.0f); 
        uiElements[P2_Gauge].UISize(MAX_Gauge_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P2_Gauge].UIColor(1.0f, 1.0f, 1.0f); 

        // =================================================
        // P2 Gauge本数 (数字)
        // =================================================
        uiElements[P2_Gauge_Number].Init("UI/number.png"); 
        uiElements[P2_Gauge_Number].UIPos(P2_Gauge_BAR_MAX_X + 4.0f, Gauge_BAR_MAX_Y, 0.0f); 
        uiElements[P2_Gauge_Number].UISize(1.0f, 1.0f, 1.0f);
        uiElements[P2_Gauge_Number].UIColor(0.5f, 0.7f, 1.0f);
        
        DisplayNumber(P2_Gauge_Number, 2);

        // =================================================
        // 十の位
        // =================================================
        uiElements[TIMER_DIGIT_TENS].Init("UI/number.png"); 
        uiElements[TIMER_DIGIT_TENS].UISize(0.8f, 0.8f, 1.0f); 
        uiElements[TIMER_DIGIT_TENS].UIPos(-0.6f, 14.0f, 0.0f); 
        uiElements[TIMER_DIGIT_TENS].UIColor(1.0f, 1.0f, 1.0f);

        // =================================================
        // 一の位
        // =================================================
        uiElements[TIMER_DIGIT_ONES].Init("UI/number.png"); 
        uiElements[TIMER_DIGIT_ONES].UISize(0.8f, 0.8f, 1.0f); 
        uiElements[TIMER_DIGIT_ONES].UIPos(0.6f, 14.0f, 0.0f); 
        uiElements[TIMER_DIGIT_ONES].UIColor(1.0f, 1.0f, 1.0f);

        //最初は99
        DisplayNumber(TIMER_DIGIT_TENS, 9);
        DisplayNumber(TIMER_DIGIT_ONES, 9);
    }

    //===========================
    // 全てのUI要素を更新 (HPバーのロジックを完全に削除)
    //===========================
    public void update(float deltaTime)
    {
        ////===P1===
        //HP処理
        if (player1Character != null) 
        {
            float ratio = player1Character.GetHPObject().getHealthRatio();
            float currentWidth = MAX_HP_BAR_WIDTH * ratio;
            float originalPosX = HP_BAR_MAX_X; 
            float widthDifference = MAX_HP_BAR_WIDTH - currentWidth;
            float newPosX = originalPosX - widthDifference;
             
            uiElements[P1_HP].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P1_HP].UIPos(newPosX, 14.0f, 0.0f); 
        }

        //Gauge処理
        if (player1Character != null) 
        {
            // ゲージインスタンスを取得 (GetGauge() が存在すると仮定)
            Gauge pGauge = player1Character.GetGauge();

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

        //Skill1CD
        if (fSkillCooldownCurrent1 > 0.0f) 
        {
            fSkillCooldownCurrent1 -= deltaTime;
            if (fSkillCooldownCurrent1 < 0.0f) 
            {
                fSkillCooldownCurrent1 = 0.0f;
            }
            uiElements[P1_SKILL_ICON_1].UIColor(0.5f, 0.5f, 0.5f);
        }
        else 
        {
            uiElements[P1_SKILL_ICON_1].UIColor(1.0f, 1.0f, 1.0f);
        }
        
        //Skill2CD
        if (fSkillCooldownCurrent2 > 0.0f) 
        {
            fSkillCooldownCurrent2 -= deltaTime;
            if (fSkillCooldownCurrent2 < 0.0f) 
            {
                fSkillCooldownCurrent2 = 0.0f;
            }
            uiElements[P1_SKILL_ICON_2].UIColor(0.5f, 0.5f, 0.5f);
        }
        else 
        {
            uiElements[P1_SKILL_ICON_2].UIColor(1.0f, 1.0f, 1.0f);
        }

        //Skill3CD
        if (fSkillCooldownCurrent3 > 0.0f) 
        {
            fSkillCooldownCurrent3 -= deltaTime;
            if (fSkillCooldownCurrent3 < 0.0f) 
            {
                fSkillCooldownCurrent3 = 0.0f;
            }
            uiElements[P1_SKILL_ICON_3].UIColor(0.5f, 0.5f, 0.5f);
        }
        else 
        {
            uiElements[P1_SKILL_ICON_3].UIColor(1.0f, 1.0f, 1.0f);
        }
        ////===P1===

        ///////////////P2の処理今仮にP1のを使ってる
        ////===P2===
        //HP
        if (player1Character != null) 
        {
            float ratio = player1Character.GetHPObject().getHealthRatio();
            float currentWidth = MAX_HP_BAR_WIDTH * ratio;
            float originalPosX = P2_HP_BAR_MAX_X; 
            float widthDifference = MAX_HP_BAR_WIDTH - currentWidth;
            float newPosX = originalPosX + widthDifference; // 右寄せで縮小
            
            uiElements[P2_HP].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P2_HP].UIPos(newPosX, 14.0f, 0.0f); 
        }

        //Gauge
        if (player1Character != null) 
        {
            // ゲージインスタンスを取得 
            Gauge pGauge = player1Character.GetGauge();

            // ゲージバーの長さ (充填比率) を更新
            float ratio = pGauge.GetCurrentGauge(); 
            float currentWidth = MAX_Gauge_BAR_WIDTH * ratio;
            
            float originalPosX = P2_Gauge_BAR_MAX_X; 
            float widthDifference = MAX_Gauge_BAR_WIDTH - currentWidth;
            // P2 は右寄せで縮小
            float newPosX = originalPosX + widthDifference; 
            
            uiElements[P2_Gauge].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P2_Gauge].UIPos(newPosX, Gauge_BAR_MAX_Y, 0.0f); 

            // ゲージの数値 (エナジー格数) を更新
            int nBars = pGauge.GetCurrentBars();
            
            // 現在の整数ゲージ格数を表示
            DisplayNumber(P2_Gauge_Number, nBars);
        }
        ////===P2===
        
        // =================================================
        // タイマー処理
        // =================================================
        if (fGameTimer > 0.0f) 
        {
            fGameTimer -= deltaTime; 

            if (fGameTimer < 0.0f) 
            {
                fGameTimer = 0.0f;
            }

            int seconds = (int) Math.floor(fGameTimer); //今の秒数取得
        
            int tensDigit = seconds / 10;   // 十の位
            int onesDigit = seconds % 10;   // 一の位
        
            // 十の位を表示
            DisplayNumber(TIMER_DIGIT_TENS, tensDigit);
            // 一の位を表示
            DisplayNumber(TIMER_DIGIT_ONES, onesDigit);
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
            if (fSkillCooldownCurrent1 == 0.0f)
            {
            fSkillCooldownCurrent1 = SKILL_COOLDOWN_MAX_1;
            }
        }

        if (GLFW.glfwGetKey(windowID, GLFW.GLFW_KEY_M) == GLFW.GLFW_PRESS) 
        {
            if (fSkillCooldownCurrent2 == 0.0f)
            {
            fSkillCooldownCurrent2 = SKILL_COOLDOWN_MAX_2;
            }
        }

        if (GLFW.glfwGetKey(windowID, GLFW.GLFW_KEY_B) == GLFW.GLFW_PRESS) 
        {
            if (fSkillCooldownCurrent3 == 0.0f)
            {
            fSkillCooldownCurrent3 = SKILL_COOLDOWN_MAX_3;
            }
        }
        //=====================DEBUG==========================
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
        this.player1Character = character;
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