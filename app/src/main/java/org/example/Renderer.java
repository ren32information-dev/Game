package org.example;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class Renderer 
{
    float SizeX, SizeY, SizeZ;
    float PosX, PosY, PosZ;
    float RtX, RtY, RtZ;
    float ColorR, ColorG, ColorB;

    public static final int MAX_UI = 4;
    
    private final Camera camera;

    private int textureId;

    private Matrix4f model = new Matrix4f();

    public Renderer(Camera camera) 
    {
        this.camera = camera;
    }

    //===========================
    // 初期化（画像読み込み）
    //===========================
    public void init() 
    {
        textureId = TextureLoader.loadTexture("UI/灰.png"); // ←画像を読み込む

        SizeX = 0;
        SizeY = 0;
        SizeZ = 0;
        PosX = 0; 
        PosY = 0;
        PosZ = 0;
        ColorR = 1.0f;
        ColorG = 1.0f;
        ColorB = 1.0f;
        RtX = 0.0f;
        RtY = 0.0f;
        RtZ = 0.0f;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    
    public void Init(String SUimageName) 
    {
        textureId = TextureLoader.loadTexture(SUimageName);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    //===========================
    // 更新処理
    //===========================
    public void update(float deltaTime) 
    {
    
    }

    //===========================
    // 描画
    //===========================

    public void draw() 
    {
        Matrix4f mvp = new Matrix4f();
        camera.getProjectionMatrix().mul(camera.getViewMatrix(), mvp).mul(model);
        float[] mat = new float[16];
        mvp.get(mat);

        GL11.glClearColor(ColorR, ColorG, ColorB, 1.0f);
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

        GL11.glEnd();
        GL11.glPopMatrix();
    }

   public void UISize(float x, float y, float z)
{
	SizeX = x;
	SizeY = y;
	SizeZ = z;
}

public void UIPos(float x, float y, float z)
{
	PosX = x;
	PosY = y;
	PosZ = z;
}

public void UIColor(float r, float g, float b)
{
	ColorR = r;
	ColorG = g;
	ColorB = b;
}

    //===========================
    // 解放
    //===========================
    public void release() 
    {
        if (textureId != 0) 
        {
            GL11.glDeleteTextures(textureId);
            textureId = 0;
        }
    }
}