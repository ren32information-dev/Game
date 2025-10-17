package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class UI {

    // 外部からアクセスできるように
    private static ImagePanel panel;

    public static void createAndShowWindow() {
        JFrame frame = new JFrame("画像表示");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        try {
            BufferedImage image = ImageIO.read(new File("app/Image/GG.png"));
            panel = new ImagePanel(image);
            frame.add(panel);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "画像の読み込みに失敗しました");
        }

        frame.setVisible(true);
    }

    /** App側から操作するためのアクセサ */
    public static ImagePanel getPanel() {
        return panel;
    }

    // カスタムパネル：中央原点で描画
    public static class ImagePanel extends JPanel {
        private final BufferedImage image;
        private int imageX = 0;
        private int imageY = 0;
        private float scale = 1.0f;

        public ImagePanel(BufferedImage image) {
            this.image = image;
            setBackground(Color.BLACK);
        }

        public void setImagePosition(int x, int y) {
            this.imageX = x;
            this.imageY = y;
            repaint();
        }

        public void setImageScale(float scale) {
            this.scale = scale;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                int scaledWidth = (int) (image.getWidth() * scale);
                int scaledHeight = (int) (image.getHeight() * scale);

                int drawX = centerX + imageX - scaledWidth / 2;
                int drawY = centerY - imageY - scaledHeight / 2;

                g2d.drawImage(image, drawX, drawY, scaledWidth, scaledHeight, this);
            }
        }
    }
}