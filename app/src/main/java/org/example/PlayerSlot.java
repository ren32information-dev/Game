package org.example;

/**
 * プレイヤースロット（1Pまたは2P）を表すクラス
 * 接続/切断の管理のみを担当
 */
public class PlayerSlot {
    private int nSlotNumber;
    //スロット番号（1 or 2）
    private boolean bOccupied;
    //スロットが占有されているか
    private Player pPlayer;
    //プレイヤーオブジェクト
    
    //コンストラクタ
    public PlayerSlot(int nSlotNumber) {
        this.nSlotNumber = nSlotNumber;
        this.bOccupied = false;
        this.pPlayer = null;
    }
    
    //スロットにプレイヤーを接続
    public void Connect(InputManager pInputManager) {
        this.bOccupied = true;
        
        // キャラクター（Playerの子クラス）を作成
        // 1Pは左側、2Pは右側に配置
        float fStartX = (nSlotNumber == 1) ? -3.0f : 3.0f;
        this.pPlayer = new Character(nSlotNumber, pInputManager, fStartX, 0f, 0f);
        
        String sDeviceType = pInputManager.IsUsingGamepad() ? 
            "ゲームパッド(" + InputManager.GetGamepadName(pInputManager.GetGamepadId()) + ")" : 
            "キーボード";
        System.out.println("[接続] プレイヤー" + nSlotNumber + ": " + sDeviceType + " で接続しました");
    }
    
    //スロットからプレイヤーを切断
    public void Disconnect() {
        if (bOccupied) {
            String sDeviceType = pPlayer.GetInputManager().IsUsingGamepad() ? 
                "ゲームパッド(" + InputManager.GetGamepadName(pPlayer.GetInputManager().GetGamepadId()) + ")" : 
                "キーボード";
            System.out.println("[切断] プレイヤー" + nSlotNumber + ": " + sDeviceType + " を切断しました");
            
            this.bOccupied = false;
            this.pPlayer = null;
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
    
    //プレイヤーを取得
    public Player GetPlayer() {
        return pPlayer;
    }
    
    //入力マネージャーを取得（互換性のため残す）
    public InputManager GetInputManager() {
        return pPlayer != null ? pPlayer.GetInputManager() : null;
    }
    
    //キャラクターを取得（PlayerはCharacterを継承しているのでキャストして返す）
    public Character GetCharacter() {
        return (Character) pPlayer;
    }
}

