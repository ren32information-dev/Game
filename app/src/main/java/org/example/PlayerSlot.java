package org.example;

/**
 * プレイヤースロット（1Pまたは2P）を表すクラス
 */
public class PlayerSlot {
    private int nSlotNumber;
    //スロット番号（1 or 2）
    private InputManager pInputManager;
    //入力マネージャー
    private boolean bOccupied;
    //スロットが占有されているか
    private Character pCharacter;
    //キャラクター
    
    public PlayerSlot(int nSlotNumber) {
        this.nSlotNumber = nSlotNumber;
        this.bOccupied = false;
        this.pInputManager = null;
        this.pCharacter = null;
    }
    
    //スロットにプレイヤーを接続
    public void Connect(InputManager pInputManager) {
        this.pInputManager = pInputManager;
        this.bOccupied = true;
        
        // キャラクターを作成
        float fStartX = (nSlotNumber == 1) ? -3.0f : 3.0f;
        this.pCharacter = new Character(fStartX, 0f, 0f);
        
        String sDeviceType = pInputManager.IsUsingGamepad() ? 
            "ゲームパッド(" + InputManager.GetGamepadName(pInputManager.GetGamepadId()) + ")" : 
            "キーボード";
        System.out.println("[接続] プレイヤー" + nSlotNumber + ": " + sDeviceType + " で接続しました");
    }
    
    //スロットからプレイヤーを切断
    public void Disconnect() {
        if (bOccupied) {
            String sDeviceType = pInputManager.IsUsingGamepad() ? 
                "ゲームパッド(" + InputManager.GetGamepadName(pInputManager.GetGamepadId()) + ")" : 
                "キーボード";
            System.out.println("[切断] プレイヤー" + nSlotNumber + ": " + sDeviceType + " を切断しました");
            
            this.pInputManager = null;
            this.bOccupied = false;
            this.pCharacter = null;
        }
    }
    
    //スロット番号を取得
    public int GetSlotNumber() {
        return nSlotNumber;
    }
    
    //占有されているか
    public boolean IsOccupied() {
        return bOccupied;
    }
    
    //入力マネージャーを取得
    public InputManager GetInputManager() {
        return pInputManager;
    }
    
    //キャラクターを取得
    public Character GetCharacter() {
        return pCharacter;
    }
}

