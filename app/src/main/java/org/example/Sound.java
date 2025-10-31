package org.example;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Sound {

    // 静的変数：現在のBGM (バックグラウンドミュージック) の Clip インスタンスを保持し、再生状態を制御するために使用します。
    private static Clip backgroundMusicClip;

    /**
     * 指定されたWAVファイルを再生し、必要なフォーマット変換を行います。
     * このメソッドはBGM用として設定されており、自動的にループ再生されます。
     *
     * @param filePath WAVファイルのパス
     */
    public static void playWavWithConversion(String filePath) {
        try {
            File soundFile = new File(filePath);
            
            // ファイルの存在を確認
            if (!soundFile.exists()) {
                JOptionPane.showMessageDialog(null, "エラー：ファイルが見つかりません " + filePath);
                return;
            }
            
            // try-with-resources を使用して AudioInputStream が確実に閉じられるようにする
            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile)) {

                AudioFormat baseFormat = audioIn.getFormat();

                // ターゲットとなるデコードフォーマットを定義：PCM_SIGNED, 16ビット深度、基本フォーマットと同じサンプリングレートとチャンネル数
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2, // フレームサイズ = チャンネル数 * 2バイト (16/8)
                        baseFormat.getSampleRate(),
                        false
                );

                // デコードされたオーディオストリームを取得
                // try-with-resources を使用して AudioInputStream が確実に閉じられるようにする
                try (AudioInputStream decodedAudio = AudioSystem.getAudioInputStream(decodedFormat, audioIn)) {
                    
                    // 1. 古いBGMが再生中の場合は、まず停止してクローズする
                    if (backgroundMusicClip != null) {
                        if (backgroundMusicClip.isRunning()) {
                            backgroundMusicClip.stop();
                        }
                        backgroundMusicClip.close();
                    }

                    // 2. 新しい Clip を取得してオープンする
                    backgroundMusicClip = AudioSystem.getClip();
                    backgroundMusicClip.open(decodedAudio);

                    // 3. 無限ループ再生を設定
                    backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY); 

                    // 4. 再生を開始
                    backgroundMusicClip.start();
                    
                    System.out.println("BGM 再生開始: " + filePath);
                }
            }

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "サポートされていないオーディオフォーマットエラーです");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ファイル読み込みエラーまたはファイルが見つかりません");
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "オーディオ再生ラインが使用できません");
        }
    }
    
    /**
     * BGMを手動で停止し、システムリソースを解放します。
     * ゲーム終了時、シーン切り替え時、またはアプリケーションを閉じる際に呼び出す必要があります。
     */
    public static void stopBGM() {
        if (backgroundMusicClip != null) {
            if (backgroundMusicClip.isRunning()) {
                backgroundMusicClip.stop();
            }
            backgroundMusicClip.close();
            backgroundMusicClip = null; // 参照をクリア
            System.out.println("BGM を停止し、リソースを解放しました。");
        }
    }
    
    public static void playSFX(String filePath) {
        // try-catch ブロックは BGM のものとほぼ同じ
        try {
            File soundFile = new File(filePath);

            // ファイルの存在を確認
            if (!soundFile.exists()) {
                System.err.println("エラー：SFXファイルが見つかりません " + filePath);
                return;
            }
            
            // AudioInputStream をメソッドローカル変数として定義し、リスナー内で閉じる必要があるため、
            // ここでは try-with-resources を使用せず、finally または LineListener でクローズします。
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat baseFormat = audioIn.getFormat();

            // BGMと同じデコードフォーマットを設定
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            // デコードされたオーディオストリームを取得
            AudioInputStream decodedAudio = AudioSystem.getAudioInputStream(decodedFormat, audioIn);
            Clip clip = AudioSystem.getClip();
            clip.open(decodedAudio);

            // ★BGMと最も異なる点：LineListenerを設定し、再生終了時にリソースをクリーンアップする
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        decodedAudio.close();
                        audioIn.close();
                    } catch (IOException ignored) {
                        // クローズ失敗は無視しても通常問題ない
                    }
                    System.out.println("SFX 再生終了・リソース解放: " + filePath);
                }
            });
            
            // ループは設定せず、一度だけ再生
            clip.start();
            System.out.println("SFX 再生開始: " + filePath);


        } catch (UnsupportedAudioFileException e) {
            System.err.println("SFXエラー: サポートされていないオーディオフォーマット");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("SFXエラー: ファイル読み込みエラー");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("SFXエラー: オーディオ再生ラインが使用できません");
            e.printStackTrace();
        }
    }
    // ヒント：必要に応じて、短い効果音 (Sound Effects, SE) を再生するための別のメソッドを追加できます。
    // そのメソッドはループを必要とせず、LineListener内で Line をクローズできます。
}