package org.example;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Result {

    private int p1WinTextureId;
    private int p2WinTextureId;
    private int drawTextureId;
    private int continueTextureId;
    
    private int currentResultTextureId;

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
        p1WinTextureId = TextureLoader.loadTexture("Image/1PWin.png");//1P勝ち
        p2WinTextureId = TextureLoader.loadTexture("Image/2PWin.png");//2P勝ち
        drawTextureId = TextureLoader.loadTexture("Image/Draw.png");//引き分け
        continueTextureId = TextureLoader.loadTexture("Image/GG.png");//続ける

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        // デバッグ出力
        System.out.println("Result Screen initialized. P1 Win Texture ID: " + p1WinTextureId);
    }
    
    public void setResult(boolean isP1Winner, boolean isP2Winner, boolean isDraw) {
        if (isDraw) {
            currentResultTextureId = drawTextureId;
        } else if (isP1Winner) {
            currentResultTextureId = p1WinTextureId;
        } else if (isP2Winner) {
            currentResultTextureId = p2WinTextureId;
        } else {
            // 勝敗が未定の場合はDRAWをデフォルトとしておく
            currentResultTextureId = drawTextureId; 
        }
    }


    public void update(float deltaTime) {
        model.identity().translate(0.0f, 0.0f, 0.0f); 
    }

    public void draw() {
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

        if (currentResultTextureId != 0) {
            // X=0, Y=0, 幅=画面幅, 高さ=画面高さ で画面いっぱいに描画
            drawTexture(currentResultTextureId, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT); 
        }
        
        GL11.glPopMatrix();
        
        GL11.glDisable(GL11.GL_BLEND); 
    }
    
    private void drawTexture(int textureId, int x, int y, int width, int height) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glColor3f(1.0f, 1.0f, 1.0f); 

        GL11.glBegin(GL11.GL_QUADS);
        
        // 頂点座標とUV座標の設定
        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(x, y, 0); // 左上
        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(x + width, y, 0); // 右上
        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(x + width, y + height, 0); // 右下
        GL11.glTexCoord2f(0, 1); GL11.glVertex3f(x, y + height, 0);// 左下

        GL11.glEnd();
    }

    public void release() {
        if (p1WinTextureId != 0) {
            GL11.glDeleteTextures(p1WinTextureId);
            p1WinTextureId = 0;
        }
        if (p2WinTextureId != 0) {
            GL11.glDeleteTextures(p2WinTextureId);
            p2WinTextureId = 0;
        }
        if (drawTextureId != 0) {
            GL11.glDeleteTextures(drawTextureId);
            drawTextureId = 0;
        }
        if (continueTextureId != 0) {
             GL11.glDeleteTextures(continueTextureId);
             continueTextureId = 0;
        }
    }
}