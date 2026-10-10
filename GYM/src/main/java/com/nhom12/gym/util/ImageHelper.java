package com.nhom12.gym.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ImageHelper {

    public static ImageIcon loadIcon(String resourceName, int width, int height) {
        try {
            BufferedImage img = null;
            InputStream is = ImageHelper.class.getResourceAsStream("/images/" + resourceName);
            if (is != null) {
                img = ImageIO.read(is);
            }

            if (img == null) {
                File f = new File("src/main/resources/images/" + resourceName);
                if (f.exists()) {
                    img = ImageIO.read(f);
                } else {
                    f = new File("GYM/src/main/resources/images/" + resourceName);
                    if (f.exists()) {
                        img = ImageIO.read(f);
                    }
                }
            }

            if (img != null) {
                Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            System.err.println("Không thể load ảnh: " + resourceName);
        }
        return null;
    }

    public static Image loadImage(String resourceName) {
        try {
            InputStream is = ImageHelper.class.getResourceAsStream("/images/" + resourceName);
            if (is != null) {
                return ImageIO.read(is);
            }
            File f = new File("src/main/resources/images/" + resourceName);
            if (f.exists()) return ImageIO.read(f);
            f = new File("GYM/src/main/resources/images/" + resourceName);
            if (f.exists()) return ImageIO.read(f);
        } catch (Exception ignored) {}
        return null;
    }
}
