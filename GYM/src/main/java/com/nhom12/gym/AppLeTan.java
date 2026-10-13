package com.nhom12.gym;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.gui.MainDashboard;
import com.nhom12.gym.util.Auth;
import javax.swing.*;

/**
 * ỨNG DỤNG KHỞI CHẠY ĐỘC LẬP: TRẠM LỄ TÂN & BÁN HÀNG
 */
public class AppLeTan {
    public static void main(String[] args) {
        try {
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            if (Auth.vaiTro == null || Auth.vaiTro.isEmpty()) {
                Auth.vaiTro = "Lễ tân";
                Auth.hoTen = "Lễ Tân Trực Ca";
                Auth.chiNhanh = "CN01 - Quận 1";
            }
            new MainDashboard("Lễ tân").setVisible(true);
        });
    }
}
