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
    private boolean bInputEnabled;
    //入力が有効かどうか
    
    //コンストラクタ
    public PlayerSlot(int nSlotNumber) {
        this.nSlotNumber = nSlotNumber;
        this.bOccupied = false;
        this.pInputManager = null;
        this.pCharacter = null;
        this.bInputEnabled = false;
    }
    
    //スロットにプレイヤーを接続
    public void Connect(InputManager pInputManager) {
        this.pInputManager = pInputManager;
        this.bOccupied = true;
        this.bInputEnabled = true; // 接続時に入力を有効化
        
        // キャラクターを作成（新しいコンストラクタを使用）
        float fStartX = (nSlotNumber == 1) ? -3.0f : 3.0f;
        this.pCharacter = new Character(nSlotNumber, pInputManager, fStartX, 0f, 0f);
        
        String sDeviceType = pInputManager.IsUsingGamepad() ? 
            "ゲームパッド(" + InputManager.GetGamepadName(pInputManager.GetGamepadId()) + ")" : 
            "キーボード";
        System.out.println("[接続] プレイヤー" + nSlotNumber + ": " + sDeviceType + " で接続しました");
    }
    
    //スロットにプレイヤーを接続（入力なし、表示のみ）
    public void ConnectDisplayOnly() {
        this.pInputManager = null;
        this.bOccupied = true;
        this.bInputEnabled = false; // 入力を無効化
        
        // キャラクターを作成（入力マネージャーなし）
        float fStartX = (nSlotNumber == 1) ? -3.0f : 3.0f;
        this.pCharacter = new Character(fStartX, 0f, 0f);
        
        // プレイヤー番号を設定
        this.pCharacter.SetPlayerNumber(nSlotNumber);
        
        System.out.println("[接続] プレイヤー" + nSlotNumber + ": 表示のみで作成しました（入力無効）");
    }
    
    //既存のキャラクターに入力マネージャーを接続
    public void AttachInputManager(InputManager pInputManager) {
        if (!bOccupied || pCharacter == null) {
            System.out.println("[警告] キャラクターが存在しません");
            return;
        }
        
        this.pInputManager = pInputManager;
        this.bInputEnabled = true;
        
        // Characterにも入力マネージャーを設定
        pCharacter.SetInputManager(pInputManager);
        
        String sDeviceType = pInputManager.IsUsingGamepad() ? 
            "ゲームパッド(" + InputManager.GetGamepadName(pInputManager.GetGamepadId()) + ")" : 
            "キーボード";
        System.out.println("[接続] プレイヤー" + nSlotNumber + ": " + sDeviceType + " で入力を有効化しました");
    }
    
    //スロットからプレイヤーを切断（入力のみ切断、表示は維持）
    public void Disconnect() {
        if (bOccupied && pInputManager != null) {
            String sDeviceType = pInputManager.IsUsingGamepad() ? 
                "ゲームパッド(" + InputManager.GetGamepadName(pInputManager.GetGamepadId()) + ")" : 
                "キーボード";
            System.out.println("[切断] プレイヤー" + nSlotNumber + ": " + sDeviceType + " の入力を切断しました（表示は維持）");
            
            this.pInputManager = null;
            this.bInputEnabled = false;
        }
    }
    
    //完全にスロットをクリア（表示も削除）
    public void ClearSlot() {
        if (bOccupied) {
            System.out.println("[削除] プレイヤー" + nSlotNumber + ": 完全に削除しました");
            
            this.pInputManager = null;
            this.bOccupied = false;
            this.bInputEnabled = false;
            this.pCharacter = null;
        }
    }
    
    //入力が有効か
    public boolean IsInputEnabled() {
        return bInputEnabled && pInputManager != null;
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