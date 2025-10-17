package org.example;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class App {
    
    public static void main(String[] args) {
        final int MAX_FPS = 60; // 最大FPS
        Window window = new Window();
        window.create(800, 600, "3D Texture Cube");

        Camera camera = new Camera();
        camera.setPerspective(true);
        camera.setPosition(0, 2, 6);
        camera.lookAt(0, 0, 0);

        Renderer renderer = new Renderer(camera);
        renderer.init();

        double lastTime = org.lwjgl.glfw.GLFW.glfwGetTime();

        boolean forward, backward, left, right;


        Renderer renderer1 = new Renderer(camera);
            renderer1.init();
        Renderer renderer2 = new Renderer(camera);
            renderer2.init();
            Renderer renderer3 = new Renderer(camera);
            renderer3.init();
            Renderer renderer4 = new Renderer(camera);
            renderer4.init();
            Renderer renderer5 = new Renderer(camera); 
            renderer5.init();
            Renderer renderer6 = new Renderer(camera);
            renderer6.init();

        while (!window.shouldClose()) {
            double currentTime = org.lwjgl.glfw.GLFW.glfwGetTime();
            float delta = (float) (currentTime - lastTime);
            if(delta <= 1.0f / MAX_FPS) continue; // FPS制限

            lastTime = currentTime;

            // キー入力
            forward = window.isKeyPressed(GLFW.GLFW_KEY_W);
            backward = window.isKeyPressed(GLFW.GLFW_KEY_S);
            left = window.isKeyPressed(GLFW.GLFW_KEY_A);
            right = window.isKeyPressed(GLFW.GLFW_KEY_D);

            camera.moveCamera(forward, backward, left, right);

            // Update / Draw
            renderer1.update(delta);
            renderer1.draw();

            renderer2.update(delta);
            renderer2.draw();
            renderer3.update(delta);
            renderer3.draw();
            renderer4.update(delta);
            renderer4.draw();
            renderer5.update(delta);
            renderer5.draw();
            renderer6.update(delta);
            renderer6.draw();
            
            //renderer1.setPosition(1, 1, 1);

            window.update();
        }

        renderer.release();
        window.destroy();
    }
}