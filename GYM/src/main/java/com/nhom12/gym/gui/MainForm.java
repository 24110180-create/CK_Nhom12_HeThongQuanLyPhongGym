package com.nhom12.gym.gui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * MainForm - Màn hình giao diện chính (Alias tương thích theo mô hình chuẩn MVC)
 * Kế thừa trực tiếp từ MainDashboard.
 */
public class MainForm extends MainDashboard {

    public MainForm() {
        super();
    }

    public static void main(String[] args) {
        try {
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new MainForm().setVisible(true));
    }
}
