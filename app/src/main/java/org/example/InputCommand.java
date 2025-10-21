package org.example;

/**
 * 入力コマンドバッファ管理クラス
 * 格ゲーの方向入力履歴を管理
 */
public class InputCommand {
    private static final int BUFFER_SIZE = 10;
    //入力バッファサイズ
    private static final float COMMAND_TIME_WINDOW = 0.3f;
    //コマンド判定の時間窓（秒）
    
    private int[] nDirectionBuffer;
    //方向入力の履歴バッファ
    private float[] fTimeBuffer;
    //各入力の時刻バッファ
    private int nBufferIndex;
    //現在のバッファインデックス
    private int nPreviousDirection;
    //前フレームの方向
    
    //コンストラクタ
    public InputCommand() {
        this.nDirectionBuffer = new int[BUFFER_SIZE];
        this.fTimeBuffer = new float[BUFFER_SIZE];
        this.nBufferIndex = 0;
        this.nPreviousDirection = 5; // ニュートラル
        
        // バッファを初期化
        for (int i = 0; i < BUFFER_SIZE; i++) {
            nDirectionBuffer[i] = 5;
            fTimeBuffer[i] = -999f;
        }
    }
    
    //方向入力を更新
    public void UpdateInput(int nCurrentDirection, float fCurrentTime) {
        // 方向が変わった時だけバッファに追加
        if (nCurrentDirection != nPreviousDirection) {
            nBufferIndex = (nBufferIndex + 1) % BUFFER_SIZE;
            nDirectionBuffer[nBufferIndex] = nCurrentDirection;
            fTimeBuffer[nBufferIndex] = fCurrentTime;
            nPreviousDirection = nCurrentDirection;
        }
    }
    
    //66コマンド（前ダッシュ）を判定
    public boolean CheckDashForward(float fCurrentTime) {
        return CheckDoubleDirection(6, fCurrentTime);
    }
    
    //44コマンド（後ダッシュ）を判定
    public boolean CheckDashBackward(float fCurrentTime) {
        return CheckDoubleDirection(4, fCurrentTime);
    }
    
    //同じ方向を2回連続で入力したかチェック
    private boolean CheckDoubleDirection(int nDirection, float fCurrentTime) {
        int nCount = 0;
        float fFirstInputTime = -999f;
        
        // バッファを新しい順に走査
        for (int i = 0; i < BUFFER_SIZE; i++) {
            int nIndex = (nBufferIndex - i + BUFFER_SIZE) % BUFFER_SIZE;
            float fInputTime = fTimeBuffer[nIndex];
            
            // 時間窓外の入力は無視
            if (fCurrentTime - fInputTime > COMMAND_TIME_WINDOW) {
                break;
            }
            
            // 指定方向の入力をカウント
            if (nDirectionBuffer[nIndex] == nDirection) {
                nCount++;
                if (nCount == 1) {
                    fFirstInputTime = fInputTime;
                }
                if (nCount >= 2) {
                    // 2回目の入力が時間内にあればtrue
                    return (fCurrentTime - fFirstInputTime) <= COMMAND_TIME_WINDOW;
                }
            }
        }
        
        return false;
    }
    
    //バッファをクリア
    public void Clear() {
        for (int i = 0; i < BUFFER_SIZE; i++) {
            nDirectionBuffer[i] = 5;
            fTimeBuffer[i] = -999f;
        }
        nBufferIndex = 0;
        nPreviousDirection = 5;
    }
    
    //デバッグ用：バッファの内容を表示
    public void PrintBuffer() {
        System.out.print("[InputBuffer] ");
        for (int i = 0; i < 5; i++) {
            int nIndex = (nBufferIndex - i + BUFFER_SIZE) % BUFFER_SIZE;
            System.out.print(nDirectionBuffer[nIndex] + " ");
        }
        System.out.println();
    }
}

