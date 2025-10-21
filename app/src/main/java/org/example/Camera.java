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

    //格闘ゲーム用カメラ更新（2キャラクターの中間点を注視、距離に応じてズーム）
    public void UpdateFightingGameCamera(Character pChar1, Character pChar2) {
        // 2キャラクターの中間点を計算
        float fMidPointX = (pChar1.GetPositionX() + pChar2.GetPositionX()) / 2.0f;
        //中間点X座標
        float fMidPointY = (pChar1.GetPositionY() + pChar2.GetPositionY()) / 2.0f;
        //中間点Y座標
        float fMidPointZ = (pChar1.GetPositionZ() + pChar2.GetPositionZ()) / 2.0f;
        //中間点Z座標
        
        float fVerticalOffset = 3.0f;
        //カメラと注視点の縦軸オフセット（画面全体を上にシフト）
        
        // 2キャラクター間の距離を計算
        float fDistanceX = pChar1.GetPositionX() - pChar2.GetPositionX();
        //X軸の距離
        float fDistanceY = pChar1.GetPositionY() - pChar2.GetPositionY();
        //Y軸の距離
        float fDistance = (float) Math.sqrt(fDistanceX * fDistanceX + fDistanceY * fDistanceY);
        //キャラクター間の距離
        
        // 距離に応じてカメラのZ位置を調整（距離が遠いほど引く）
        float fMinCameraDistance = 10.0f;
        //最小カメラ距離（キャラが近い時）
        float fMaxCameraDistance = 20.0f;
        //最大カメラ距離（キャラが遠い時）
        float fDistanceThresholdMin = 2.0f;
        //距離判定の最小閾値
        float fDistanceThresholdMax = 10.0f;
        //距離判定の最大閾値
        
        // 距離を0.0～1.0に正規化
        float fNormalizedDistance = (fDistance - fDistanceThresholdMin) / (fDistanceThresholdMax - fDistanceThresholdMin);
        //正規化された距離
        fNormalizedDistance = Math.max(0.0f, Math.min(1.0f, fNormalizedDistance));
        //0.0～1.0にクランプ
        
        // カメラ距離を計算
        float fCameraDistance = fMinCameraDistance + (fMaxCameraDistance - fMinCameraDistance) * fNormalizedDistance;
        //現在のカメラ距離
        
        // カメラの位置を設定（中間点の後ろ、少し上から）
        float fCameraHeightOffset = 2.0f;
        //カメラの高さオフセット（注視点より少し上）
        setPosition(fMidPointX, fMidPointY + fVerticalOffset + fCameraHeightOffset, fMidPointZ + fCameraDistance);
        
        // 注視点を中間点＋縦軸オフセットに設定（画面を上げてUIと重ならないように）
        lookAt(fMidPointX, fMidPointY + fVerticalOffset, fMidPointZ);
    }

}