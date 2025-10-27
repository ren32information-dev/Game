package org.example;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureLoader {
    public static int loadTexture(String filePath) {
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);

        // 画像読み込み（RGBA）
        ByteBuffer image = STBImage.stbi_load(filePath, w, h, c, 4);
        if (image == null) {
            throw new RuntimeException("Failed to load texture: " + filePath);
        }

        int texID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w.get(0), h.get(0), 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

        STBImage.stbi_image_free(image);
        return texID;
    }
}