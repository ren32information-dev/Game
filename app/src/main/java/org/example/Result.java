package org.example;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Result {

    private int winTextureId; // 勝利メッセージ用のテクスチャID
    private int returnTextTextureId; // キャラクター選択に戻るメッセージ用のテクスチャID
    
    private final Matrix4f model = new Matrix4f();
    private final Matrix4f projection;
    
    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    
    public Result(int ScreenWidth, int ScreenHeight) {
        this.SCREEN_WIDTH = ScreenWidth;
        this.SCREEN_HEIGHT = ScreenHeight;
        
        // 2D座標(0〜screenWidth, 0〜screenHeight)で描画する行列を設定
        this.projection = new Matrix4f().ortho2D(0, ScreenWidth, ScreenHeight, 0); 
    }

    public void init() {
        // 画像は仮で "GG.png" を使用します。実際のゲームでは "WINNER.png" や "PressEnter.png" などに置き換えてください。
        // リザルト画面で勝利メッセージを表示するために、ここでは同じ画像を再利用します
        winTextureId = TextureLoader.loadTexture("Image/GG.png"); 
        returnTextTextureId = TextureLoader.loadTexture("Image/GG.png"); 

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        // デバッグ出力
        System.out.println("Result Screen initialized. Win Texture ID: " + winTextureId);
    }

    public void update(float deltaTime) {
        model.identity().translate(0.0f, 0.0f, 0.0f); 
        // リザルト画面は静的なため、点滅などの更新処理は現状なし
    }

    public void draw() {
        // 背景色を少し明るいグレー（または黒）にクリア
        GL11.glClearColor(0.2f, 0.2f, 0.2f, 1.0f); 
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Matrix4f mvp = new Matrix4f();
        projection.mul(model, mvp); 
        float[] mat = new float[16];
        mvp.get(mat);

        GL11.glPushMatrix();
        GL11.glLoadMatrixf(mat);

        // 1. 勝利メッセージの描画 (画面中央上部)
        int winWidth = 800;
        int winHeight = 250;
        int winX = (SCREEN_WIDTH - winWidth) / 2;
        int winY = 150;
        drawTexture(winTextureId, winX, winY, winWidth, winHeight); 

        // 2. キャラクター選択に戻るメッセージの描画 (画面下部)
        int returnWidth = 600;
        int returnHeight = 50;
        int returnX = (SCREEN_WIDTH - returnWidth) / 2;
        int returnY = SCREEN_HEIGHT - 100;
        drawTexture(returnTextTextureId, returnX, returnY, returnWidth, returnHeight); 
        
        GL11.glPopMatrix();
        
        GL11.glDisable(GL11.GL_BLEND); 
    }
    
    /**
     * テクスチャを指定された位置とサイズで描画する。
     */
    private void drawTexture(int textureId, int x, int y, int width, int height) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glColor3f(1.0f, 1.0f, 1.0f); 

        GL11.glBegin(GL11.GL_QUADS);
        
        // 頂点座標とUV座標の設定
        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(x, y, 0);             // 左上
        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(x + width, y, 0);     // 右上
        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(x + width, y + height, 0); // 右下
        GL11.glTexCoord2f(0, 1); GL11.glVertex3f(x, y + height, 0);    // 左下

        GL11.glEnd();
    }

    public void release() {
        if (winTextureId != 0) {
            GL11.glDeleteTextures(winTextureId);
            winTextureId = 0;
        }
        if (returnTextTextureId != 0) {
            GL11.glDeleteTextures(returnTextTextureId);
            returnTextTextureId = 0;
        }
    }
}
