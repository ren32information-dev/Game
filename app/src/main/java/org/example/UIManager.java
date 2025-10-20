package org.example;

// 移除 import org.lwjgl.glfw.GLFW; (因為現在交給 HealthController 處理)

public class UIManager {
    // 註解：UI 要素の数を必要に応じて調整できます
    private static final int MAX_UI_ELEMENTS = 10; 
    private Renderer[] uiElements; // 複数の Renderer オブジェクトを格納する配列
    private Camera mainCamera; 

    // 新增：HealthController オブジェクト（HPデータを取得するため）
    private final HP Hp; 

    // ============== HP バーのパラメータ (幾何サイズと位置のみ) ==============
    private final float MAX_BAR_WIDTH = 4.0f; // HP バーの最大幅 (SizeX)
    private final float BAR_HEIGHT = 0.4f; // HP バーの高さ (SizeY)
    private final float BAR_CENTER_X = 0.0f; // HP バーの X 軸中心位置
    private final float BAR_CENTER_Y = 3.5f; // HP バーの Y 軸位置 (画面上部)
    // ==========================================

    /**
     * 構造関数。
     * @param camera メインカメラ
     * @param controller HPデータを管理する
     */
    public UIManager(Camera camera, HP controller) {
        this.mainCamera = camera;
        this.uiElements = new Renderer[MAX_UI_ELEMENTS];
        this.Hp = controller; // コントローラーを保存
    }

    //===========================
    // 全てのUI要素を初期化
    //===========================
    public void initUI() {
        // Renderer インスタンスを作成
        for (int i = 0; i < MAX_UI_ELEMENTS; i++) {
            uiElements[i] = new Renderer(mainCamera); 
        }

        // =================================================
        // UI 要素 [0]: 赤いHPバー (現在のHP)
        // =================================================
        uiElements[0].Init("UI/赤.png"); 
        // 紋理が白/灰色の場合に、赤く表示させるために色を設定
        uiElements[0].UIColor(1.0f, 0.0f, 0.0f); 

        // =================================================
        // UI 要素 [1]: 灰色HPバーの背景 (最大HP)
        // =================================================
        uiElements[1].Init("UI/灰.png"); 
        uiElements[1].UIColor(0.5f, 0.5f, 0.5f); // 灰色に設定
        
        // 最大 HP バー (灰色): サイズと位置は固定
        uiElements[1].UIPos(BAR_CENTER_X, BAR_CENTER_Y, 0.0f);
        uiElements[1].UISize(MAX_BAR_WIDTH, BAR_HEIGHT, 1.0f); 

        // 例：UI 要素 [2]: キャラクター画像
        uiElements[2].Init("UI/青.png"); 
        uiElements[2].UIPos(-2.0f, 0.0f, 0.0f);
        uiElements[2].UISize(1.5f, 1.5f, 1.0f);
    }

    //===========================
    // 全てのUI要素を更新 (主にHPバーのサイズと位置の調整)
    //===========================
    public void update(float deltaTime) {

        // 1. HP から現在の HP 割合を取得
        float currentRatio = Hp.getHealthRatio();

        // 2. 赤い HP バーの現在の幅を計算: (最大幅 * HP 割合)
        float currentBarWidth = MAX_BAR_WIDTH * currentRatio;
        
        // 3. 右側を固定するために、中心点の新しい X 座標を計算
        // X 軸の位移量 = (最大幅 - 現在の幅) / 2
        float xOffset = (MAX_BAR_WIDTH - currentBarWidth) / 2.0f;
        
        // 赤い HP バーの中心 X 座標 = 最大 HP バー中心 X 座標 - 位移量
        float currentBarX = BAR_CENTER_X - xOffset; 
        
        // 4. 新しいサイズと位置を赤い HP バーに適用
        uiElements[0].UIPos(currentBarX, BAR_CENTER_Y, 0.0f);
        uiElements[0].UISize(currentBarWidth, BAR_HEIGHT, 1.0f);
        
        
        // 全 Renderer の Update メソッドを呼び出す
        for (Renderer renderer : uiElements) {
            if (renderer != null) {
                renderer.Update(deltaTime);
            }
        }
    }

    //===========================
    // 全てのUI要素を描画
    //===========================
    public void drawUI() {
        // UI 要素 [1] (灰色/最大HP) を先に描画して背景にする
        if (uiElements[1] != null) {
            uiElements[1].Draw();
        }
        
        // UI 要素 [0] (赤色/現在HP) をその上に描画する
        if (uiElements[0] != null) {
            uiElements[0].Draw();
        }
        
        // 他の UI 要素を描画
        for (int i = 2; i < MAX_UI_ELEMENTS; i++) {
            if (uiElements[i] != null) {
                uiElements[i].Draw();
            }
        }
    }
    
    //===========================
    // リソースを解放
    //===========================
    public void release() {
        for (Renderer renderer : uiElements) 
        {
            if (renderer != null) {
                renderer.release();
            }
        }
    }
}