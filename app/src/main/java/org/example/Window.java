package org.example;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Window {
    private long pWindow;

    public void create(int width, int height, String title) {
        if (!GLFW.glfwInit())
            throw new IllegalStateException("GLFW init failed");

        pWindow = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        if (pWindow == 0)
            throw new RuntimeException("Failed to create window");

        GLFW.glfwMakeContextCurrent(pWindow);
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GLFW.glfwShowWindow(pWindow);
    }

    public long getHandle() {
        return pWindow;
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(pWindow);
    }

    public void update() {
        GLFW.glfwSwapBuffers(pWindow);
        GLFW.glfwPollEvents();
    }

    public void destroy() {
        GLFW.glfwDestroyWindow(pWindow);
        GLFW.glfwTerminate();
    }

    // キー入力チェック用メソッドを追加
    public boolean isKeyPressed(int key) {
        return GLFW.glfwGetKey(pWindow, key) == GLFW.GLFW_PRESS;
    }
}