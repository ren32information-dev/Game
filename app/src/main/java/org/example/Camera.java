package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f position = new Vector3f(0, 0, 3);
    private final Vector3f target = new Vector3f(0, 0, 0);
    private final Vector3f up = new Vector3f(0, 1, 0);
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();

    private boolean isPerspective = true;
    private float fov = (float) Math.toRadians(60.0f);
    private float aspect = 16f / 9f;
    private float near = 0.1f;
    private float far = 1000f;

    private float speed = 0.1f; // 移動速度

    public void setPerspective(boolean perspective) {
        isPerspective = perspective;
    }

    public void setAspect(float width, float height) {
        aspect = width / height;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void lookAt(float x, float y, float z) {
        target.set(x, y, z);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix.identity().lookAt(position, target, up);
    }

    public Matrix4f getProjectionMatrix() {
        if (isPerspective)
            projectionMatrix.identity().perspective(fov, aspect, near, far);
        else
            projectionMatrix.identity().ortho(-10, 10, -10, 10, near, far);
        return projectionMatrix;
    }

    //カメラ移動
    public void moveCamera(boolean forward, boolean backward, boolean left, boolean right) {
        Vector3f forwardDir = new Vector3f(target).sub(position).normalize(); // 前方向ベクトル
        Vector3f rightDir = new Vector3f(forwardDir).cross(up).normalize();   // 右方向ベクトル

        if (forward)  position.add(new Vector3f(forwardDir).mul(speed));
        if (backward) position.sub(new Vector3f(forwardDir).mul(speed));
        if (right)   position.add(new Vector3f(rightDir).mul(speed));
        if (left)    position.sub(new Vector3f(rightDir).mul(speed));

        // ターゲットも一緒に動かす
        target.set(position).add(forwardDir);
    }
    // カメラの周りでターゲットを回す
    public void rotateTargetAroundCamera(float angle, float radius) {
        // カメラの位置を中心にターゲットを回転
        float x = (float) (position.x + radius * Math.cos(angle));
        float z = (float) (position.z + radius * Math.sin(angle));
        target.set(x, target.y, z);

        // 必要に応じてカメラの方向を更新
        lookAt(target.x, target.y, target.z);
    }

}