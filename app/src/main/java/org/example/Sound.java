package org.example;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Sound {

    public static void playWavWithConversion(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

            AudioFormat baseFormat = audioIn.getFormat();

            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            AudioInputStream decodedAudio = AudioSystem.getAudioInputStream(decodedFormat, audioIn);

            Clip clip = AudioSystem.getClip();
            clip.open(decodedAudio);
            clip.start();

            // 再生完了を待機（ブロッキングしたくない場合は別スレッドでOK）
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        decodedAudio.close();
                        audioIn.close();
                    } catch (IOException ignored) {}
                }
            });

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "サポートされていない音声形式です");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ファイルが見つからない、または読み込み失敗");
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "音声再生ラインが使用できません");
        }
    }
}