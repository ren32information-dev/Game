package org.example;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class CharacterRenderer {
    private final Camera pCamera;
    //カメラオブジェクト
    private int nTextureId;
    //テクスチャID
    private float fWidth = 2.0f;
    //キャラクターの幅
    private float fHeight = 3.0f;
    //キャラクターの高さ

    public CharacterRenderer(Camera pCamera, String sImagePath) {
        this.pCamera = pCamera;
        Initialize(sImagePath);
    }

    //初期化（画像読み込み）
    public void Initialize(String sImagePath) {
        nTextureId = TextureLoader.loadTexture(sImagePath);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    //キャラクターを描画
    public void DrawCharacter(Character pCharacter) {
        float fX = pCharacter.GetPositionX();
        float fY = pCharacter.GetPositionY();
        float fZ = pCharacter.GetPositionZ();

        Matrix4f pModel = new Matrix4f().translate(fX, fY + fHeight/2, fZ);
        Matrix4f pMvp = new Matrix4f();
        pCamera.getProjectionMatrix().mul(pCamera.getViewMatrix(), pMvp).mul(pModel);
        
        float[] fMat = new float[16];
        pMvp.get(fMat);

        GL11.glPushMatrix();
        GL11.glLoadMatrixf(fMat);

        GL11.glColor3f(1.0f,1.0f,1.0f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, nTextureId);
        GL11.glBegin(GL11.GL_QUADS);

        // 前面（キャラクター画像）
        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(-fWidth/2,  fHeight/2, 0);
        GL11.glTexCoord2f(1, 0); GL11.glVertex3f( fWidth/2,  fHeight/2, 0);
        GL11.glTexCoord2f(1, 1); GL11.glVertex3f( fWidth/2, -fHeight/2, 0);
        GL11.glTexCoord2f(0, 1); GL11.glVertex3f(-fWidth/2, -fHeight/2, 0);

        GL11.glEnd();
        GL11.glPopMatrix();
    }

    //解放
    public void Release() {
        if (nTextureId != 0) {
            GL11.glDeleteTextures(nTextureId);
            nTextureId = 0;
        }
    }
}

