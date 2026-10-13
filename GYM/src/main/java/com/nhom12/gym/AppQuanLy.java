package com.nhom12.gym;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.gui.MainDashboard;
import com.nhom12.gym.util.Auth;
import javax.swing.*;

/**
 * ỨNG DỤNG KHỞI CHẠY ĐỘC LẬP: ĐIỀU HÀNH QUẢN LÝ CHI NHÁNH
 */
public class AppQuanLy {
    public static void main(String[] args) {
        try {
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            if (Auth.vaiTro == null || Auth.vaiTro.isEmpty()) {
                Auth.vaiTro = "Quản lý";
                Auth.hoTen = "Quản Lý Cơ Sở";
                Auth.chiNhanh = "CN02 - Tân Bình";
            }
            new MainDashboard("Quản lý").setVisible(true);
        });
    }
}
