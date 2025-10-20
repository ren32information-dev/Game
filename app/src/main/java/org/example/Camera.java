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

    private float speed = 5.0f;
    //移動速度（1秒あたりのユニット数）

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

    //カメラ移動（矢印キー用）
    public void MoveCamera(boolean bUpKey, boolean bDownKey, boolean bLeftKey, boolean bRightKey, float fDeltaTime) {
        Vector3f pForwardDirection = new Vector3f(target).sub(position).normalize();
        //前方向ベクトル
        Vector3f pRightDirection = new Vector3f(pForwardDirection).cross(up).normalize();
        //右方向ベクトル

        float fMoveAmount = speed * fDeltaTime;
        //移動量（デルタタイム考慮）

        if (bUpKey)      position.add(new Vector3f(pForwardDirection).mul(fMoveAmount));  // 上キー: 前進
        if (bDownKey)    position.sub(new Vector3f(pForwardDirection).mul(fMoveAmount));  // 下キー: 後退
        if (bRightKey)   position.add(new Vector3f(pRightDirection).mul(fMoveAmount));    // 右キー: 右移動
        if (bLeftKey)    position.sub(new Vector3f(pRightDirection).mul(fMoveAmount));    // 左キー: 左移動

        // ターゲットも一緒に動かす
        target.set(position).add(pForwardDirection);
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