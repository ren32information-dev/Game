package org.example;
import org.lwjgl.glfw.GLFW;

public class UIManager 
{
    private static final int P1_HP = 0; 
    private static final int P1_MAXHP = 1;     
    private static final int MAX_UI_ELEMENTS = 2; 
    private Renderer[] uiElements; 
    private Camera mainCamera; 
    
    private final float HP_BAR_Y = 14.0f; 
    private final float HP_BAR_MAX_X = -5.0f; // 假設背景條(-5.0f)是 HP 條的中心點

    // 獲取紅色 HP 條的初始最大寬度 (假設它在 initUI 中設定)
    final float MAX_HP_BAR_WIDTH = 4.0f; // 這裡我們假設最大寬度是 4.0f
    
    private Character playerCharacter;

    public UIManager(Camera camera,Character character) 
    {
        this.mainCamera = camera;
        this.playerCharacter = character; // 儲存角色引用
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
        uiElements[P1_HP].UIPos(HP_BAR_MAX_X, HP_BAR_Y, 0.0f); 
        uiElements[P1_HP].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_HP].UIColor(1.0f, 1.0f, 1.0f);
        
        // =================================================
        // 灰色HPバーの背景 (最大HP)
        // =================================================
        uiElements[P1_MAXHP].Init("UI/gray.png");  
        uiElements[P1_MAXHP].UIPos(HP_BAR_MAX_X, HP_BAR_Y, 0.0f); 
        uiElements[P1_MAXHP].UISize(MAX_HP_BAR_WIDTH, 0.4f, 1.0f);
        uiElements[P1_MAXHP].UIColor(1.0f, 1.0f, 1.0f); 
    }

    //===========================
    // 全てのUI要素を更新 (HPバーのロジックを完全に削除)
    //===========================
    public void update(float deltaTime)
    {
        
        if (playerCharacter != null) 
        {
            float ratio = playerCharacter.GetHPObject().getHealthRatio();
            float currentWidth = MAX_HP_BAR_WIDTH * ratio;
            float originalPosX = HP_BAR_MAX_X; 
            float widthDifference = MAX_HP_BAR_WIDTH - currentWidth;
            float newPosX = originalPosX - (widthDifference / 2.0f);
             
            uiElements[P1_HP].UISize(currentWidth, 0.4f, 1.0f);
            uiElements[P1_HP].UIPos(newPosX, HP_BAR_Y, 0.0f); 

            long windowID = GLFW.glfwGetCurrentContext();
            
            if (GLFW.glfwGetKey(windowID, GLFW.GLFW_KEY_B) == GLFW.GLFW_PRESS) 
            {
                currentWidth -= 0.5f;
            }

            playerCharacter.GetHPObject().update(); 
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
}