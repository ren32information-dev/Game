package org.example;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Title {

    private int TitleLogoTextureId;
    private int PressAnyButtonTextureId;
    
    private final Matrix4f model = new Matrix4f();
    private final Matrix4f projection;
    private boolean PressAnyButtonVisible = true; // 点滅制御用

    int ScreenWidth = 800; //仮
    int sScreenHeight = 600; //仮

    public Title(int ScreenWidth, int ScreenHeight) {
        // 2D座標(0〜screenWidth, 0〜screenHeight)で描画する行列を設定
        this.projection = new Matrix4f().ortho2D(0, ScreenWidth, ScreenHeight, 0); 
    }

    public void init() {
        TitleLogoTextureId = TextureLoader.loadTexture("GG.png"); //画像ついかしてないので仮

        PressAnyButtonTextureId = TextureLoader.loadTexture("GG.png"); //画像ついかしてないので仮

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public void update(float deltaTime) {
        model.identity().translate(0.0f, 0.0f, 0.0f); 

        //0.5秒ごとに点滅
        if (System.currentTimeMillis() % 1000 < 500) { 
            PressAnyButtonVisible = true;
        } else {
            PressAnyButtonVisible = false;
        }
    }

    public void draw() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        Matrix4f mvp = new Matrix4f();
        projection.mul(model, mvp); 
        float[] mat = new float[16];
        mvp.get(mat);

        GL11.glPushMatrix();
        GL11.glLoadMatrixf(mat);

        drawTexture(TitleLogoTextureId, 100, 50, 600, 300); // (テクスチャID, x, y, 幅, 高さ) 800x600に合わせて調整 あとで変更

        if (PressAnyButtonVisible) {
            drawTexture(PressAnyButtonTextureId, 250, 450, 300, 50); //800x600に合わせて調整 あとで変更
        }
        
        GL11.glPopMatrix();
    }
    
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
        if (TitleLogoTextureId != 0) {
            GL11.glDeleteTextures(TitleLogoTextureId);
            TitleLogoTextureId = 0;
        }
        if (PressAnyButtonTextureId != 0) {
            GL11.glDeleteTextures(PressAnyButtonTextureId);
            PressAnyButtonTextureId = 0;
        }
    }
}