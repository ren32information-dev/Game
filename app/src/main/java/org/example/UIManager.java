package org.example;

public class UIManager {
    // 註解：UI 要素の数を必要に応じて調整できます
    private static final int MAX_UI_ELEMENTS = 10; 
    private Renderer[] uiElements; // 複数の Renderer オブジェクトを格納する配列
    private Camera mainCamera; 

    // 構造関数：Camera のみを受け取る（HPコントローラーへの依存を排除）
    public UIManager(Camera camera) {
        this.mainCamera = camera;
        this.uiElements = new Renderer[MAX_UI_ELEMENTS];
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
        // UI 要素 [0]: 赤いHPバー (現在のHP) - 固定の初期値
        // =================================================
        uiElements[0].Init("UI/red.png"); 
        // 暫定的な初期位置とサイズを設定 (後に外部から変更されることを想定)
        uiElements[0].UIPos(0.0f, 3.5f, 0.0f); 
        uiElements[0].UISize(4.0f, 0.4f, 1.0f);


        // =================================================
        // UI 要素 [1]: 灰色HPバーの背景 (最大HP) - 固定
        // =================================================
        uiElements[1].Init("UI/gray.png");      
        // 最大 HP バー (灰色): サイズと位置は固定
        uiElements[1].UIPos(0.0f, 3.5f, 0.0f);
        uiElements[1].UISize(4.0f, 0.4f, 1.0f); 

        // 例：UI 要素 [2]: キャラクター画像
        uiElements[2].Init("UI/blue.png"); 
        uiElements[2].UIPos(-2.0f, 0.0f, 0.0f);
        uiElements[2].UISize(1.5f, 1.5f, 1.0f);
    }

    //===========================
    // 全てのUI要素を更新 (HPバーのロジックを完全に削除)
    //===========================
    public void update(float deltaTime) {
        
        // 【重要】: ここから HP バーの計算ロジックを完全に削除しました。
        // 赤いHPバーの位置やサイズを更新したい場合は、
        // 外部のクラス（例: App.java や別の Manager クラス）から
        // uiElements[0].UIPos(...) や uiElements[0].UISize(...) を呼び出す必要があります。

        
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
    
    //===========================
    // 外部から Renderer にアクセスするためのゲッター (必要に応じて追加)
    //===========================
    public Renderer getRenderer(int index) {
        if (index >= 0 && index < MAX_UI_ELEMENTS) {
            return uiElements[index];
        }
        return null;
    }
}