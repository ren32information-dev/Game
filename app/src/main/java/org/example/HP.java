package org.example;

import org.lwjgl.glfw.GLFW; 

public class HP {
    
    private float maxHP = 100.0f; // 最大HP
    private float currentHP = 100.0f; // 當前HP
    
    // 構造函數：接受視窗ID
    public HP() {
    }
    
    //===========================
    // 更新處理：檢查按鍵並修改HP值
    //===========================
    public void update() {
        currentHP = Math.max(currentHP, 0);
        currentHP = Math.min(currentHP, maxHP);
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

    // ダメージを与える
    public void DamageHP(float damage) {
        currentHP = Math.max(currentHP - damage, 0);
    }

    // HPを回復する（デバッグ用）
    public void HealHP(float heal) {
        currentHP = Math.min(currentHP + heal, maxHP);
    }

    // 死んだかどうか判定
    public boolean IsDieHP() {
        return currentHP <= 0;
    }
}