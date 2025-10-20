package org.example;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Renderer {
    private final Camera camera;
    private int textureId;
    private Matrix4f model = new Matrix4f();

    public Renderer(Camera camera) {
        this.camera = camera;
    }

    //===========================
    // 初期化（画像読み込み）
    //===========================
    public void init() {
        textureId = TextureLoader.loadTexture("灰.png"); // ←画像を読み込む
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    //===========================
    // 更新処理（移動など）
    //===========================
    public void update(float deltaTime) {
        // 例：時間経過で回転させる
        /*float t = (System.currentTimeMillis() % 10000L) / 1000.0f;
        model.identity()
             .translate(0.0f, 0.0f, -5.0f)
             .rotateY(t);
        */
    }

    //===========================
    // 描画
    //===========================
    public void draw() {
        Matrix4f mvp = new Matrix4f();
        camera.getProjectionMatrix().mul(camera.getViewMatrix(), mvp).mul(model);
        float[] mat = new float[16];
        mvp.get(mat);

        GL11.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glPushMatrix();
        GL11.glLoadMatrixf(mat);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glBegin(GL11.GL_QUADS);

        //===========================
        // 前面 (UV付き)
        //===========================
        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(1, 1,  0);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3f( -1, 1,  0);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3f( -1,  -1,  0);
        GL11.glTexCoord2f(0, 1); GL11.glVertex3f(1,  -1,  0);

        //===========================
        // 背面
        //===========================
        //GL11.glTexCoord2f(0, 0); GL11.glVertex3f( 1, -1, -1);
        //GL11.glTexCoord2f(1, 0); GL11.glVertex3f(-1, -1, -1);
        //GL11.glTexCoord2f(1, 1); GL11.glVertex3f(-1,  1, -1);
        //GL11.glTexCoord2f(0, 1); GL11.glVertex3f( 1,  1, -1);

        GL11.glEnd();
        GL11.glPopMatrix();
    }

    //===========================
    // 解放
    //===========================
    public void release() {
        if (textureId != 0) {
            GL11.glDeleteTextures(textureId);
            textureId = 0;
        }
    }
}