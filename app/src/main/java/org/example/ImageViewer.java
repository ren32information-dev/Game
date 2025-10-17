package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageViewer {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageViewer().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        // フレーム作成
        JFrame frame = new JFrame("画像表示");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        try {
            // 読み込む画像のパス（png/jpg/jpeg対応）
            BufferedImage image = ImageIO.read(new File("app/Image/GG.png"));
            // 例: "images/photo.jpg" などでもOK

            // ラベルに貼り付け
            JLabel label = new JLabel(new ImageIcon(image));
            frame.add(label, BorderLayout.CENTER);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "画像の読み込みに失敗しました");
        }

        frame.setVisible(true);
    }
}