package org.example;
import org.lwjgl.glfw.GLFW;

public class UIManager 
{
    ////===P1===
    //===status===
    private static final int P1_MAXHP = 1;
    private static final int P1_HP = 2; 
    private static final int P1_MAXGauge = 3;
    private static final int P1_Gauge = 4; 
    private static final int P1_Gauge_Number = 5;
    private static final int P1_HP_YELLOW = 22;
    //===status===

    //===Skill===
    private static final int P1_SKILL_ICON_1 = 13;
    private static final int P1_SKILL_ICON_2 = 14;
    private static final int P1_SKILL_ICON_3 = 15;
    private static final int P1_SKILL_COOLDOWN_OVERLAY_1 = 27; 
    private static final int P1_SKILL_COOLDOWN_OVERLAY_2 = 28;
    private static final int P1_SKILL_COOLDOWN_OVERLAY_3 = 29;
    private static final int P1_SKILL_OK_OVERLAY_1 = 33; 
    private static final int P1_SKILL_OK_OVERLAY_2 = 34;
    private static final int P1_SKILL_OK_OVERLAY_3 = 35;
    //===Skill===

    //===HitCount===
    private static final int P1_HIT_COUNT_DIGIT_ONES = 19; // 一の位 (個位)
    private static final int P1_HIT_COUNT_DIGIT_TENS = 20; // 十の位 (十位)
    private static final int P1_HIT_LABEL = 21; // 「HIT」の文字画像
    //===HitCount===
    ////===P1===

    //===タイマー===
    private static final int TIMER_DIGIT_TENS = 6;
    private static final int TIMER_DIGIT_ONES = 7;
    //===タイマー===

    ////===P2===
    //===status===
    private static final int P2_MAXHP = 8;
    private static final int P2_HP = 9; 
    private static final int P2_MAXGauge = 10;
    private static final int P2_Gauge = 11; 
    private static final int P2_Gauge_Number = 12;
    private static final int P2_HP_YELLOW = 23;
    //===status===

    //===Skill===
    private static final int P2_SKILL_ICON_1 = 16;
    private static final int P2_SKILL_ICON_2 = 17;
    private static final int P2_SKILL_ICON_3 = 18;
    private static final int P2_SKILL_COOLDOWN_OVERLAY_1 = 30; 
    private static final int P2_SKILL_COOLDOWN_OVERLAY_2 = 31;
    private static final int P2_SKILL_COOLDOWN_OVERLAY_3 = 32;
    private static final int P2_SKILL_OK_OVERLAY_1 = 36;
    private static final int P2_SKILL_OK_OVERLAY_2 = 37;
    private static final int P2_SKILL_OK_OVERLAY_3 = 38;
    //===Skill===
    //===HitCount===
    private static final int P2_HIT_COUNT_DIGIT_ONES = 24; // P2 一の位 (個位)
    private static final int P2_HIT_COUNT_DIGIT_TENS = 25; // P2 十の位 (十位)
    private static final int P2_HIT_LABEL = 26; // P2 「HIT」の文字画像
    //===HitCount===
    ////===P2===

    //===背景===
    private static final int BACKGROUND_IMAGE = 0;
    //===背景===
    
    private static final int MAX_UI_ELEMENTS = 39; //総イラスト数
    
    
    private Renderer[] uiElements; 
    private Camera mainCamera; 
    
    //===UIの座標定数===
    //// P1
    //HP Gauge
    private final float HP_BAR_MAX_X = -5.5f;       // -5.0fはHPX軸の中心
    private final float HP_BAR_MAX_Y = 14.0f;       // -5.0fはHPY軸の中心
    private final float Gauge_BAR_MAX_X = -5.5f;    // -6.0fはGaugeX軸の中心
    private final float Gauge_BAR_MAX_Y = -3.5f;    // -6.0fはGaugeY軸の中心
    //スキルアイコン
    private final float P1_SKILL_ICON_START_X = -8.0f;
    private final float P1_SKILL_ICON_Y = 12.5f;
    private final float P1_SKILL_ICON_SIZE = 0.7f;
    // スキルCD
    private final float SKILL_COOLDOWN_MAX_1 = 5.0f;
    private final float SKILL_COOLDOWN_MAX_2 = 8.0f;
    private final float SKILL_COOLDOWN_MAX_3 = 12.0f;
    // スキル今のCD
    private float fSkillCooldownCurrent1 = 0.0f;
    private float fSkillCooldownCurrent2 = 0.0f;
    private float fSkillCooldownCurrent3 = 0.0f;
    //Hit
    private final float P1_HIT_POS_X = -8.0f;
    private final float P1_HIT_POS_Y = 10.0f;

    //// P2
    //HP Gauge
    private final float P2_HP_BAR_MAX_X = 5.5f;
    private final float P2_HP_BAR_MAX_Y = 14.0f; 
    private final float P2_Gauge_BAR_MAX_X = 5.5f; 
    private final float P2_Gauge_BAR_MAX_Y = -3.5f;
    //スキルアイコン
    private final float P2_SKILL_ICON_START_X = 8.0f;
    private final float P2_SKILL_ICON_Y = 12.5f;
    // P2 スキル今のCD
    private float fP2SkillCooldownCurrent1 = 0.0f;
    private float fP2SkillCooldownCurrent2 = 0.0f;
    private float fP2SkillCooldownCurrent3 = 0.0f;
    // P2 Hit
    private final float P2_HIT_POS_X = 8.0f; 
    private final float P2_HIT_POS_Y = 10.0f;
    float cameraX;
    float cameraY;

    //===UIの座標定数===

    //===数字UV===
    private static final int NUMBER_OF_COLUMNS = 5;
    private static final int NUMBER_OF_ROWS = 5;  
    private static final float UV_UNIT_U = 1.0f / NUMBER_OF_COLUMNS; 
    private static final float UV_UNIT_V = 1.0f / NUMBER_OF_ROWS;
    //===数字UV===

    private float fGameTimer = 9.0f; // タイマー

    final float MAX_HP_BAR_WIDTH = 4.0f;        //最大HPの長さ
    final float MAX_Gauge_BAR_WIDTH = 3.0f;     //最大Gaugeの長さ
    
    private Character player1Character; // P1 キャラクター
    private Character player2Character; // P2 キャラクター

    public UIManager(Camera camera,Character character1,Character character2) 
    {
        this.mainCamera = camera;
        this.player1Character = character1;
        this.player2Character = character2;
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
        uiElements[P1_HP].UIPos(HP_BAR_MAX_X, HP_BAR_MAX_Y, 0.0f); 
        uiElements[P1_HP].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_HP].UIColor(1.0f, 1.0f, 1.0f);
        uiElements[P1_HP].SetVisibility(true);

        // =================================================
        // P1黄色HPバー (HP<40)
        // =================================================
        uiElements[P1_HP_YELLOW].Init("UI/yellow.png"); 
        uiElements[P1_HP_YELLOW].UIPos(HP_BAR_MAX_X, HP_BAR_MAX_Y, 0.0f); 
        uiElements[P1_HP_YELLOW].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_HP_YELLOW].UIColor(1.0f, 1.0f, 1.0f); 
        uiElements[P1_HP_YELLOW].SetVisibility(false);

        // =================================================
        // P1灰色HPバーの背景 (最大HP)
        // =================================================
        uiElements[P1_MAXHP].Init("UI/gray.png");  
        uiElements[P1_MAXHP].UIPos(HP_BAR_MAX_X, HP_BAR_MAX_Y, 0.0f); 
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
        uiElements[P1_SKILL_ICON_1].Init("UI/SpGauge001.png");
        uiElements[P1_SKILL_ICON_1].UIPos(P1_SKILL_ICON_START_X, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_ICON_1].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_ICON_1].UIColor(1.0f, 1.0f, 1.0f);
        
        //Skill2
        uiElements[P1_SKILL_ICON_2].Init("UI/SpGauge002.png");
        uiElements[P1_SKILL_ICON_2].UIPos(P1_SKILL_ICON_START_X + 1.5f, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_ICON_2].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_ICON_2].UIColor(1.0f, 1.0f, 1.0f);
        
        //Skill3
        uiElements[P1_SKILL_ICON_3].Init("UI/SpGauge002.png");
        uiElements[P1_SKILL_ICON_3].UIPos(P1_SKILL_ICON_START_X + 3.0f, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_ICON_3].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_ICON_3].UIColor(1.0f, 1.0f, 1.0f);
        // Skill1 Overlay
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_1].Init("UI/Black50%.png");
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_1].UIPos(P1_SKILL_ICON_START_X, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_1].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_1].SetVisibility(false); //
        
        // Skill2 Overlay
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_2].Init("UI/Black50%.png");
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_2].UIPos(P1_SKILL_ICON_START_X + 1.5f, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_2].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_2].SetVisibility(false); 

        // Skill3 Overlay
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_3].Init("UI/Black50%.png");
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_3].UIPos(P1_SKILL_ICON_START_X + 3.0f, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_3].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_COOLDOWN_OVERLAY_3].SetVisibility(false);
        
        // P1 Skill OK Overlay 1
        uiElements[P1_SKILL_OK_OVERLAY_1].Init("UI/GaugeOK.png");
        uiElements[P1_SKILL_OK_OVERLAY_1].UIPos(P1_SKILL_ICON_START_X, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_OK_OVERLAY_1].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_OK_OVERLAY_1].SetVisibility(false);

        // P1 Skill OK Overlay 2
        uiElements[P1_SKILL_OK_OVERLAY_2].Init("UI/GaugeOK.png");
        uiElements[P1_SKILL_OK_OVERLAY_2].UIPos(P1_SKILL_ICON_START_X + 1.5f, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_OK_OVERLAY_2].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_OK_OVERLAY_2].SetVisibility(false);

        // P1 Skill OK Overlay 3
        uiElements[P1_SKILL_OK_OVERLAY_3].Init("UI/GaugeOK.png");
        uiElements[P1_SKILL_OK_OVERLAY_3].UIPos(P1_SKILL_ICON_START_X + 3.0f, P1_SKILL_ICON_Y, 0.0f); 
        uiElements[P1_SKILL_OK_OVERLAY_3].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P1_SKILL_OK_OVERLAY_3].SetVisibility(false);

        // =================================================
        // Hit Count Label 「HIT」の文字
        // =================================================
        uiElements[P1_HIT_LABEL].Init("UI/Hit.png"); 
        uiElements[P1_HIT_LABEL].UIPos(P1_HIT_POS_X + cameraX, P1_HIT_POS_Y + cameraY, 0.0f);
        uiElements[P1_HIT_LABEL].UISize(2.0f, 1.4f, 1.0f);
        uiElements[P1_HIT_LABEL].SetVisibility(false);

        // =================================================
        // Hit Count - 十の位
        // =================================================
        uiElements[P1_HIT_COUNT_DIGIT_TENS].Init("UI/number.png"); 
        uiElements[P1_HIT_COUNT_DIGIT_TENS].UISize(0.6f, 0.6f, 1.0f); 
        uiElements[P1_HIT_COUNT_DIGIT_TENS].UIPos(P1_HIT_POS_X + 2.0f + cameraX, P1_HIT_POS_Y + cameraY, 0.0f);
        uiElements[P1_HIT_COUNT_DIGIT_TENS].SetVisibility(false);

        // =================================================
        // Hit Count - 一の位
        // =================================================
        uiElements[P1_HIT_COUNT_DIGIT_ONES].Init("UI/number.png"); 
        uiElements[P1_HIT_COUNT_DIGIT_ONES].UISize(0.6f, 0.6f, 1.0f); 
        uiElements[P1_HIT_COUNT_DIGIT_ONES].UIPos(P1_HIT_POS_X + 3.0f + cameraX, P1_HIT_POS_Y + cameraY, 0.0f);
        uiElements[P1_HIT_COUNT_DIGIT_ONES].SetVisibility(false);
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
        uiElements[P2_HP].SetVisibility(true);

        // =================================================
        // P2黄色HPバー (HP<40)
        // =================================================
        uiElements[P2_HP_YELLOW].Init("UI/yellow.png"); 
        uiElements[P2_HP_YELLOW].UIPos(P2_HP_BAR_MAX_X, 14.0f, 0.0f); 
        uiElements[P2_HP_YELLOW].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P2_HP_YELLOW].UIColor(1.0f, 1.0f, 1.0f);
        uiElements[P2_HP_YELLOW].SetVisibility(false);

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
     
        // P2 Skill1
        uiElements[P2_SKILL_ICON_1].Init("UI/SpGauge001.png"); 
        uiElements[P2_SKILL_ICON_1].UIPos(P2_SKILL_ICON_START_X, P2_SKILL_ICON_Y, 0.0f); 
        uiElements[P2_SKILL_ICON_1].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P2_SKILL_ICON_1].UIColor(1.0f, 1.0f, 1.0f);
        
        // P2 Skill2
        uiElements[P2_SKILL_ICON_2].Init("UI/SpGauge002.png"); 
        uiElements[P2_SKILL_ICON_2].UIPos(P2_SKILL_ICON_START_X - 1.5f, P2_SKILL_ICON_Y, 0.0f); // X座標を左に移動
        uiElements[P2_SKILL_ICON_2].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P2_SKILL_ICON_2].UIColor(1.0f, 1.0f, 1.0f);
        
        // P2 Skill3
        uiElements[P2_SKILL_ICON_3].Init("UI/SpGauge002.png"); 
        uiElements[P2_SKILL_ICON_3].UIPos(P2_SKILL_ICON_START_X - 3.0f, P2_SKILL_ICON_Y, 0.0f); // X座標をさらに左に移動
        uiElements[P2_SKILL_ICON_3].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P2_SKILL_ICON_3].UIColor(1.0f, 1.0f, 1.0f);

        // P2 Skill1 Overlay
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_1].Init("UI/Black50%.png");
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_1].UIPos(P2_SKILL_ICON_START_X, P2_SKILL_ICON_Y, 0.0f); 
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_1].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_1].SetVisibility(false);
        
        // P2 Skill2 Overlay
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_2].Init("UI/Black50%.png");
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_2].UIPos(P2_SKILL_ICON_START_X - 1.5f, P2_SKILL_ICON_Y, 0.0f); 
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_2].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_2].SetVisibility(false); 

        // P2 Skill3 Overlay
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_3].Init("UI/Black50%.png");
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_3].UIPos(P2_SKILL_ICON_START_X - 3.0f, P2_SKILL_ICON_Y, 0.0f); 
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_3].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P2_SKILL_COOLDOWN_OVERLAY_3].SetVisibility(false);

        // P2 Skill OK Overlay 1
        uiElements[P2_SKILL_OK_OVERLAY_1].Init("UI/GaugeOK.png");
        uiElements[P2_SKILL_OK_OVERLAY_1].UIPos(P2_SKILL_ICON_START_X, P2_SKILL_ICON_Y, 0.0f); 
        uiElements[P2_SKILL_OK_OVERLAY_1].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P2_SKILL_OK_OVERLAY_1].SetVisibility(false);

        // P2 Skill OK Overlay 2
        uiElements[P2_SKILL_OK_OVERLAY_2].Init("UI/GaugeOK.png");
        uiElements[P2_SKILL_OK_OVERLAY_2].UIPos(P2_SKILL_ICON_START_X - 1.5f, P2_SKILL_ICON_Y, 0.0f); 
        uiElements[P2_SKILL_OK_OVERLAY_2].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P2_SKILL_OK_OVERLAY_2].SetVisibility(false);

        // P2 Skill OK Overlay 3
        uiElements[P2_SKILL_OK_OVERLAY_3].Init("UI/GaugeOK.png");
        uiElements[P2_SKILL_OK_OVERLAY_3].UIPos(P2_SKILL_ICON_START_X - 3.0f, P2_SKILL_ICON_Y, 0.0f); 
        uiElements[P2_SKILL_OK_OVERLAY_3].UISize(P1_SKILL_ICON_SIZE, P1_SKILL_ICON_SIZE, 1.0f);
        uiElements[P2_SKILL_OK_OVERLAY_3].SetVisibility(false);

        // =================================================
        // P2 Hit Count Label 「HIT」の文字
        // =================================================
        uiElements[P2_HIT_LABEL].Init("UI/Hit.png"); 
        uiElements[P2_HIT_LABEL].UIPos(P2_HIT_POS_X + cameraX, P2_HIT_POS_Y + cameraY, 0.0f);
        uiElements[P2_HIT_LABEL].UISize(2.0f, 1.4f, 1.0f);
        uiElements[P2_HIT_LABEL].SetVisibility(false);
    
        // =================================================
        // P2 Hit Count - 十の位
        // =================================================
        uiElements[P2_HIT_COUNT_DIGIT_TENS].Init("UI/number.png"); 
        uiElements[P2_HIT_COUNT_DIGIT_TENS].UISize(0.6f, 0.6f, 1.0f); 
        uiElements[P2_HIT_COUNT_DIGIT_TENS].UIPos(P2_HIT_POS_X - 2.0f + cameraX, P2_HIT_POS_Y + cameraY, 0.0f); // X位置調整
        uiElements[P2_HIT_COUNT_DIGIT_TENS].SetVisibility(false);

        // =================================================
        // P2 Hit Count - 一の位
        // =================================================
         uiElements[P2_HIT_COUNT_DIGIT_ONES].Init("UI/number.png"); 
        uiElements[P2_HIT_COUNT_DIGIT_ONES].UISize(0.6f, 0.6f, 1.0f); 
        uiElements[P2_HIT_COUNT_DIGIT_ONES].UIPos(P2_HIT_POS_X - 3.0f + cameraX, P2_HIT_POS_Y + cameraY, 0.0f); // X位置調整
        uiElements[P2_HIT_COUNT_DIGIT_ONES].SetVisibility(false);

        ////タイマー
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


        // =================================================
        // 背景
        // =================================================
            
        uiElements[BACKGROUND_IMAGE].Init("UI/BackGround.png"); 
        uiElements[BACKGROUND_IMAGE].UIPos(0.0f, 6.0f, -1.0f); 
        uiElements[BACKGROUND_IMAGE].UISize(18.0f, 12.96f, 1.0f);
        uiElements[BACKGROUND_IMAGE].UIColor(1.0f, 1.0f, 1.0f);
        uiElements[BACKGROUND_IMAGE].SetUIUV(1.0f, 0.0f, 0.0f, 1.0f);

    }

    //===========================
    // 全てのUI要素を更新 (HPバーのロジックを完全に削除)
    //===========================
    public void update(float deltaTime)
    {
        // === カメラの現在の X 座標を取得 ===
        cameraX = mainCamera.GetPosX();
        cameraY = mainCamera.GetPosY() - 7.0f;
        
        ////===P1===
        //HP処理
        float p1HpPosX = HP_BAR_MAX_X + cameraX;
        float p1HpPosY = HP_BAR_MAX_Y + cameraY;
        uiElements[P1_MAXHP].UIPos(p1HpPosX, p1HpPosY, 0.0f);
        
        if (player1Character != null) 
        {
            float ratio = player1Character.GetHPObject().getHealthRatio();
            float currentHP = player1Character.GetHPObject().getCurrentHP();
            
            float currentWidth = MAX_HP_BAR_WIDTH * ratio;
            float originalPosX = HP_BAR_MAX_X + cameraX; 
            float widthDifference = MAX_HP_BAR_WIDTH - currentWidth;
            float newPosX = originalPosX - widthDifference;
            float newPosY = HP_BAR_MAX_Y + cameraY;

            // 調整 P1_HP_YELLOW 的位置和大小，使其與 P1_HP 相同
            uiElements[P1_HP_YELLOW].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P1_HP_YELLOW].UIPos(newPosX, newPosY, 0.0f); 

            // 調整 P1_HP (紅色) 的位置和大小
            uiElements[P1_HP].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P1_HP].UIPos(newPosX, newPosY, 0.0f); 


            // 【P1 HP 顏色切換邏輯】
            if (currentHP < 40.0f) 
            {
                uiElements[P1_HP].SetVisibility(false);        // 隱藏紅色 HP
                uiElements[P1_HP_YELLOW].SetVisibility(true);  // 顯示黃色 HP
            }
            else 
            {
                uiElements[P1_HP].SetVisibility(true);         // 顯示紅色 HP
                uiElements[P1_HP_YELLOW].SetVisibility(false); // 隱藏黃色 HP
            }
        }

        //Gauge処理

        float p1MaxGaugePosX = Gauge_BAR_MAX_X + cameraX;
        float p1MaxGaugePosY = Gauge_BAR_MAX_Y + cameraY;
        uiElements[P1_MAXGauge].UIPos(p1MaxGaugePosX, p1MaxGaugePosY, 0.0f); 

        float p1GaugeNumberPosX = (Gauge_BAR_MAX_X - 4.0f) + cameraX; 
        float p1GaugeNumberPosY = Gauge_BAR_MAX_Y + cameraY;
        uiElements[P1_Gauge_Number].UIPos(p1GaugeNumberPosX, p1GaugeNumberPosY, 0.0f);

        if (player1Character != null) 
        {
            // ゲージインスタンスを取得 (GetGauge() が存在すると仮定)
            Gauge pGauge = player1Character.GetGauge();

            //ゲージバーの長さ (充填比率) を更新
            //GetCurrentGauge()は現在のゲージセルの充填比率 (0.0f〜1.0f) を返す
            float ratio = pGauge.GetCurrentGauge(); 
            float currentWidth = MAX_Gauge_BAR_WIDTH * ratio;
            
            // ゲージバーが幅 0 の時に正しい位置に配置されるように調整
            float originalPosX = Gauge_BAR_MAX_X + cameraX;
            float widthDifference = MAX_Gauge_BAR_WIDTH - currentWidth;
            float newPosX = originalPosX - (widthDifference / 2.0f); // 中心位置調整
            
            // 修正：ゲージバーが幅 0 の時に正しい位置に配置されるように調整
            newPosX = originalPosX - widthDifference;      
            float newPosY = Gauge_BAR_MAX_Y + cameraY;
            
            uiElements[P1_Gauge].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P1_Gauge].UIPos(newPosX, newPosY, 0.0f); 

            //ゲージの数値 (エナジー格数) を更新
            int nBars = pGauge.GetCurrentBars();
            
            // 現在の整数ゲージ格数を表示
            // 最大が 7 なので、一桁の数字のみが必要です。
            DisplayNumber(P1_Gauge_Number, nBars);
        }
        //Skill
        float p1Skill1X = P1_SKILL_ICON_START_X + cameraX;
        float p1Skill2X = P1_SKILL_ICON_START_X + 1.5f + cameraX;
        float p1Skill3X = P1_SKILL_ICON_START_X + 3.0f + cameraX;
        float p1SkillY = P1_SKILL_ICON_Y + cameraY;
        
        uiElements[P1_SKILL_ICON_1].UIPos(p1Skill1X, p1SkillY, 0.0f); 
        uiElements[P1_SKILL_ICON_2].UIPos(p1Skill2X, p1SkillY, 0.0f); 
        uiElements[P1_SKILL_ICON_3].UIPos(p1Skill3X, p1SkillY, 0.0f);

        uiElements[P1_SKILL_OK_OVERLAY_1].UIPos(p1Skill1X, p1SkillY, 0.0f);
        uiElements[P1_SKILL_OK_OVERLAY_2].UIPos(p1Skill2X, p1SkillY, 0.0f);
        uiElements[P1_SKILL_OK_OVERLAY_3].UIPos(p1Skill3X, p1SkillY, 0.0f); 
        // Skill1CD
        if (fSkillCooldownCurrent1 > 0.0f) 
        {
            fSkillCooldownCurrent1 -= deltaTime;
            if (fSkillCooldownCurrent1 < 0.0f) 
            {
                fSkillCooldownCurrent1 = 0.0f;
            }
        }

        UpdateSkillCooldown(
            P1_SKILL_COOLDOWN_OVERLAY_1, 
            fSkillCooldownCurrent1, 
            SKILL_COOLDOWN_MAX_1, 
            p1Skill1X, 
            p1SkillY
        );

        // Skill2CD
        if (fSkillCooldownCurrent2 > 0.0f) 
        {
            fSkillCooldownCurrent2 -= deltaTime;
            if (fSkillCooldownCurrent2 < 0.0f) 
            {
                fSkillCooldownCurrent2 = 0.0f;
            }
        }
        
        UpdateSkillCooldown(
            P1_SKILL_COOLDOWN_OVERLAY_2, 
            fSkillCooldownCurrent2, 
            SKILL_COOLDOWN_MAX_2, 
            p1Skill2X, 
            p1SkillY
        );
        
        // Skill3CD
        if (fSkillCooldownCurrent3 > 0.0f) 
        {
            fSkillCooldownCurrent3 -= deltaTime;
            if (fSkillCooldownCurrent3 < 0.0f) 
            {
                fSkillCooldownCurrent3 = 0.0f;
            }
        }

        UpdateSkillCooldown(
            P1_SKILL_COOLDOWN_OVERLAY_3, 
            fSkillCooldownCurrent3, 
            SKILL_COOLDOWN_MAX_3, 
            p1Skill3X, 
            p1SkillY
        );

        // =================================================
        // Hit Count 處理
        // =================================================
        if (player1Character != null) 
        {
            int hitCount;

            hitCount = player1Character.ComboCount(); 

            if (hitCount >= 1) 
            {
                // Hit>1
                uiElements[P1_HIT_LABEL].SetVisibility(true);
                
                uiElements[P1_HIT_LABEL].UIPos(P1_HIT_POS_X + cameraX, 
                P1_HIT_POS_Y + cameraY, 0.0f);

                DisplayMultiDigitNumber(hitCount);
                
                uiElements[P1_HIT_COUNT_DIGIT_TENS].UIPos(P1_HIT_POS_X + 2.0f +
                 cameraX, P1_HIT_POS_Y + cameraY, 0.0f);
                uiElements[P1_HIT_COUNT_DIGIT_ONES].UIPos(P1_HIT_POS_X + 3.0f + 
                cameraX, P1_HIT_POS_Y + cameraY, 0.0f);

                if(hitCount <= 9)
                {
                    uiElements[P1_HIT_COUNT_DIGIT_ONES].SetVisibility(true);
                    uiElements[P1_HIT_COUNT_DIGIT_TENS].SetVisibility(false);
                }
                else
                {
                    uiElements[P1_HIT_COUNT_DIGIT_ONES].SetVisibility(true);
                    uiElements[P1_HIT_COUNT_DIGIT_TENS].SetVisibility(true);
                }
            } 
            else 
            {
                // Hit<1
                uiElements[P1_HIT_LABEL].SetVisibility(false);
                uiElements[P1_HIT_COUNT_DIGIT_ONES].SetVisibility(false);
                uiElements[P1_HIT_COUNT_DIGIT_TENS].SetVisibility(false);
            }
        }
        ////===P1===

        ////===P2===
        //HP
        float p2HpPosX = P2_HP_BAR_MAX_X + cameraX;
        float p2HpPosY = P2_HP_BAR_MAX_Y + cameraY;
        uiElements[P2_MAXHP].UIPos(p2HpPosX, p2HpPosY, 0.0f); // 背景も更新
        
        if (player2Character != null) 
        {
            float ratio = player2Character.GetHPObject().getHealthRatio();
            float currentHP = player2Character.GetHPObject().getCurrentHP();
            float currentWidth = MAX_HP_BAR_WIDTH * ratio;
            float originalPosX = P2_HP_BAR_MAX_X + cameraX; 
            float widthDifference = MAX_HP_BAR_WIDTH - currentWidth;
            float newPosX = originalPosX + widthDifference; // 右寄せで縮小
            
            uiElements[P2_HP].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P2_HP].UIPos(newPosX, p2HpPosY, 0.0f); 
            
            uiElements[P2_HP_YELLOW].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P2_HP_YELLOW].UIPos(newPosX, p2HpPosY, 0.0f);

            if (currentHP < 40.0f) 
            {
                uiElements[P2_HP].SetVisibility(false);        // 隱藏紅色 HP
                uiElements[P2_HP_YELLOW].SetVisibility(true);  // 顯示黃色 HP
            }
            else 
            {
                uiElements[P2_HP].SetVisibility(true);         // 顯示紅色 HP
                uiElements[P2_HP_YELLOW].SetVisibility(false); // 隱藏黃色 HP
            }
        }       


        float p2MaxGaugePosX = P2_Gauge_BAR_MAX_X + cameraX;
        float p2MaxGaugePosY = P2_Gauge_BAR_MAX_Y + cameraY;
        uiElements[P2_MAXGauge].UIPos(p2MaxGaugePosX, p2MaxGaugePosY, 0.0f);
        
        float p2GaugeNumberPosX = (P2_Gauge_BAR_MAX_X + 4.0f) + cameraX; 
        float p2GaugeNumberPosY = P2_Gauge_BAR_MAX_Y + cameraY; 
        uiElements[P2_Gauge_Number].UIPos(p2GaugeNumberPosX, p2GaugeNumberPosY, 0.0f);
        
        //Gauge
        if (player2Character != null) 
        {
            // ゲージインスタンスを取得 
            Gauge pGauge = player2Character.GetGauge();
            
            // ゲージバーの長さ (充填比率) を更新
            float ratio = pGauge.GetCurrentGauge(); 
            float currentWidth = MAX_Gauge_BAR_WIDTH * ratio;
            
            float originalPosX = P2_Gauge_BAR_MAX_X + cameraX; 
            float widthDifference = MAX_Gauge_BAR_WIDTH - currentWidth;
            // P2 は右寄せで縮小
            float newPosX = originalPosX + widthDifference; 
            float newPosY = P2_Gauge_BAR_MAX_Y + cameraY; 
            
            uiElements[P2_Gauge].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P2_Gauge].UIPos(newPosX, newPosY, 0.0f); 
            
            // ゲージの数値 (エナジー格数) を更新
            int nBars = pGauge.GetCurrentBars();
            
            // 現在の整数ゲージ格数を表示
            DisplayNumber(P2_Gauge_Number, nBars);
        }

        // =================================================
        // P2 Skill Icon の位置更新
        // =================================================
        float p2Skill1X = P2_SKILL_ICON_START_X + cameraX;
        float p2Skill2X = P2_SKILL_ICON_START_X - 1.5f + cameraX;
        float p2Skill3X = P2_SKILL_ICON_START_X - 3.0f + cameraX;
        float p2SkillY = P2_SKILL_ICON_Y + cameraY;

        uiElements[P2_SKILL_ICON_1].UIPos(p2Skill1X, p2SkillY, 0.0f); 
        uiElements[P2_SKILL_ICON_2].UIPos(p2Skill2X, p2SkillY, 0.0f); 
        uiElements[P2_SKILL_ICON_3].UIPos(p2Skill3X, p2SkillY, 0.0f);
        
        uiElements[P2_SKILL_OK_OVERLAY_1].UIPos(p2Skill1X, p2SkillY, 0.0f);
        uiElements[P2_SKILL_OK_OVERLAY_2].UIPos(p2Skill2X, p2SkillY, 0.0f);
        uiElements[P2_SKILL_OK_OVERLAY_3].UIPos(p2Skill3X, p2SkillY, 0.0f); 

        // Skill1CD
        if (fP2SkillCooldownCurrent1 > 0.0f) 
        {
            fP2SkillCooldownCurrent1 -= deltaTime;
            if (fP2SkillCooldownCurrent1 < 0.0f) 
            {
                fP2SkillCooldownCurrent1 = 0.0f;
            }
        }

        UpdateSkillCooldown(
            P2_SKILL_COOLDOWN_OVERLAY_1, 
            fP2SkillCooldownCurrent1, 
            SKILL_COOLDOWN_MAX_1, 
            p2Skill1X, 
            p2SkillY
        );

        // Skill2CD
        if (fP2SkillCooldownCurrent2 > 0.0f) 
        {
            fP2SkillCooldownCurrent2 -= deltaTime;
            if (fP2SkillCooldownCurrent2 < 0.0f) 
            {
                fP2SkillCooldownCurrent2 = 0.0f;
            }
        }
        
        UpdateSkillCooldown(
            P2_SKILL_COOLDOWN_OVERLAY_2, 
            fP2SkillCooldownCurrent2, 
            SKILL_COOLDOWN_MAX_2, 
            p2Skill2X, 
            p2SkillY
        );
        
        // Skill3CD
        if (fP2SkillCooldownCurrent3 > 0.0f) 
        {
            fP2SkillCooldownCurrent3 -= deltaTime;
            if (fP2SkillCooldownCurrent3 < 0.0f) 
            {
                fP2SkillCooldownCurrent3 = 0.0f;
            }
        }

        UpdateSkillCooldown(
            P2_SKILL_COOLDOWN_OVERLAY_3, 
            fP2SkillCooldownCurrent3, 
            SKILL_COOLDOWN_MAX_3, 
            p2Skill3X, 
            p2SkillY
        );

        // =================================================
        // Hit Count 處理
        // =================================================
        // =================================================
        if (player2Character != null) 
        {
            int hitCount;
            hitCount = player2Character.ComboCount();

            if (hitCount >= 1) 
            {
                // Hit>1
                uiElements[P2_HIT_LABEL].SetVisibility(true);

                // P2 Hit Label 位置更新
                uiElements[P2_HIT_LABEL].UIPos(P2_HIT_POS_X + cameraX, P2_HIT_POS_Y + cameraY, 0.0f);

                DisplayP2MultiDigitNumber(hitCount);
                
                uiElements[P2_HIT_COUNT_DIGIT_TENS].UIPos(P2_HIT_POS_X - 3.0f + cameraX, P2_HIT_POS_Y + cameraY, 0.0f);
                uiElements[P2_HIT_COUNT_DIGIT_ONES].UIPos(P2_HIT_POS_X - 2.0f + cameraX, P2_HIT_POS_Y + cameraY, 0.0f);
                
                if(hitCount <= 9)
                {
                    uiElements[P2_HIT_COUNT_DIGIT_ONES].SetVisibility(true);
                    uiElements[P2_HIT_COUNT_DIGIT_TENS].SetVisibility(false);
                }
                else
                {
                    uiElements[P2_HIT_COUNT_DIGIT_ONES].SetVisibility(true);
                    uiElements[P2_HIT_COUNT_DIGIT_TENS].SetVisibility(true);
                }
            } 
            else 
            {
                // Hit<1
                uiElements[P2_HIT_LABEL].SetVisibility(false);
                uiElements[P2_HIT_COUNT_DIGIT_ONES].SetVisibility(false);
                uiElements[P2_HIT_COUNT_DIGIT_TENS].SetVisibility(false);
            }

        }
        ////===P2===
        
        
        // =================================================
        // タイマー処理
        // =================================================
        uiElements[TIMER_DIGIT_TENS].UIPos(-0.6f + cameraX, 14.0f + cameraY, 0.0f); 
        uiElements[TIMER_DIGIT_ONES].UIPos(0.6f + cameraX, 14.0f + cameraY, 0.0f);
        
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
        else
        {
            DisplayNumber(TIMER_DIGIT_TENS, 0);
            DisplayNumber(TIMER_DIGIT_ONES, 0);
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
            if (fP2SkillCooldownCurrent1 == 0.0f)
            {
                fP2SkillCooldownCurrent1 = SKILL_COOLDOWN_MAX_1;
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
    
    //number.png
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

    //===========================
    // P1複数桁の数字を表示
    //===========================
    private void DisplayMultiDigitNumber(int number) 
    {
        int tensDigit = number / 10; // 十の位
        int onesDigit = number % 10; // 一の位

        if (tensDigit > 0) 
        {
            DisplayNumber(P1_HIT_COUNT_DIGIT_TENS, tensDigit);
        } 

        DisplayNumber(P1_HIT_COUNT_DIGIT_ONES, onesDigit);
    }
    //===========================
    // P2複数桁の数字を表示
    //===========================
    private void DisplayP2MultiDigitNumber(int number) 
    {
        int tensDigit = number / 10; // 十の位
        int onesDigit = number % 10; // 一の位

        if (tensDigit > 0) 
        {
            DisplayNumber(P2_HIT_COUNT_DIGIT_TENS, tensDigit);
        } 

        DisplayNumber(P2_HIT_COUNT_DIGIT_ONES, onesDigit);
    }

    //===========================
    //スキルCD
    //===========================
    private void UpdateSkillCooldown(int overlayID, float currentCD,
     float maxCD, float iconX, float iconY) 
    {
        Renderer overlay = uiElements[overlayID];
        
        int okOverlayID = -1;
        if (overlayID == P1_SKILL_COOLDOWN_OVERLAY_1) okOverlayID = P1_SKILL_OK_OVERLAY_1;
        else if (overlayID == P1_SKILL_COOLDOWN_OVERLAY_2) okOverlayID = P1_SKILL_OK_OVERLAY_2;
        else if (overlayID == P1_SKILL_COOLDOWN_OVERLAY_3) okOverlayID = P1_SKILL_OK_OVERLAY_3;
        else if (overlayID == P2_SKILL_COOLDOWN_OVERLAY_1) okOverlayID = P2_SKILL_OK_OVERLAY_1;
        else if (overlayID == P2_SKILL_COOLDOWN_OVERLAY_2) okOverlayID = P2_SKILL_OK_OVERLAY_2;
        else if (overlayID == P2_SKILL_COOLDOWN_OVERLAY_3) okOverlayID = P2_SKILL_OK_OVERLAY_3;

        Renderer okOverlay = (okOverlayID != -1) ? uiElements[okOverlayID] : null;

        if (currentCD <= 0.0f) 
        {
            overlay.SetVisibility(false);
            if (okOverlay != null) 
            {
                okOverlay.SetVisibility(true);
            }
            uiElements[overlayID - 14].UIColor(1.0f, 1.0f, 1.0f); 
            return;
        }

        if (okOverlay != null) 
        {
            okOverlay.SetVisibility(false);
        }

        overlay.SetVisibility(true);

        float recoveryRatio = 1.0f - (currentCD / maxCD); 

        float v = 1.0f - recoveryRatio; 
                                    
        overlay.SetUIUV(0.0f, 0.0f, 1.0f, v); 
        
        float iconSize = P1_SKILL_ICON_SIZE;
        float currentHeight = iconSize * v; 
        float newOverlayY = iconY + (iconSize / 2.0f) -
         (currentHeight / 2.5f) + (recoveryRatio / 3.0f); 
        
        overlay.UISize(iconSize, currentHeight, 1.0f); 
        overlay.UIPos(iconX, newOverlayY, 0.0f);        
    }
}