package org.example;

import org.lwjgl.glfw.GLFW; 

public class HP {
    
    private float maxHP = 100.0f; // 最大HP
    private float currentHP = 75.0f; // 當前HP
    private long windowHandle; // 視窗ID (用於檢查按鍵)
    
    // 構造函數：接受視窗ID
    public HP(long windowHandle) {
        this.windowHandle = windowHandle;
    }
    
    //===========================
    // 更新處理：檢查按鍵並修改HP值
    //===========================
    public void update() {
        // P 鍵被按下時
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_P) == GLFW.GLFW_PRESS) {
            // HP 減去 10.0f
            currentHP -= 10.0f;
            if (currentHP < 0) {
                currentHP = 0; // HP 不允許小於 0
            }
        }

        // O 鍵被按下時
        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_O) == GLFW.GLFW_PRESS) {
            // HP 回滿
            currentHP = maxHP;
        }
    }
    
    //===========================
    // Getter (供 UIManager 讀取)
    //===========================
    
    // 獲取當前 HP
    public float getCurrentHP() {
        return currentHP;
    }
    
    // 獲取最大 HP
    public float getMaxHP() {
        return maxHP;
    }
    
    // 獲取 HP 比例 (0.0 到 1.0)
    public float getHealthRatio() {
        if (maxHP <= 0) return 0.0f;
        return currentHP / maxHP;
    }
    
    // 供外部遊戲邏輯呼叫的設置血量方法 (可選)
    public void setCurrentHP(float newHP) {
        this.currentHP = newHP;
    }
}