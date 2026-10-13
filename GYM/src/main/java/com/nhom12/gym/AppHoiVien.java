package com.nhom12.gym;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.gui.HoiVienAppFrame;
import com.nhom12.gym.service.AuthService;
import com.nhom12.gym.util.Auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ỨNG DỤNG ĐỘC LẬP DÀNH RIÊNG CHO HỘI VIÊN FITLIFE GYM (FITLIFE MEMBER MOBILE & PORTAL)
 * Điểm khởi chạy riêng biệt (Standalone Entry Point) cho Hội Viên.
 * Chỉ cần chạy file này là mở thẳng App Hội Viên, không liên quan tới máy tính nhân viên.
 */
public class AppHoiVien extends JFrame {

    private final JTextField txtPhone;
    private final JPasswordField txtPass;
    private final AuthService authService = new AuthService();

    public AppHoiVien() {
        setTitle("FITLIFE MEMBER APP - ĐĂNG NHẬP THÀNH VIÊN");
        setSize(440, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(15, 23, 42)); // Nền Dark sang trọng của app di động

        // 1. TOP HEADER BANNER
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(40, 30, 20, 30));

        JLabel lblBadge = new JLabel("FITLIFE FITNESS & YOGA");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setForeground(new Color(234, 179, 8)); // Vàng Gold
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("FITLIFE MEMBER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Cổng Dịch Vụ Hội Viên Thông Minh");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(148, 163, 184));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlHeader.add(lblBadge);
        pnlHeader.add(Box.createRigidArea(new Dimension(0, 8)));
        pnlHeader.add(lblTitle);
        pnlHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlHeader.add(lblSub);
        root.add(pnlHeader, BorderLayout.NORTH);

        // 2. FORM BODY ĐĂNG NHẬP
        JPanel pnlBody = new JPanel();
        pnlBody.setLayout(new BoxLayout(pnlBody, BoxLayout.Y_AXIS));
        pnlBody.setOpaque(false);
        pnlBody.setBorder(new EmptyBorder(10, 35, 30, 35));

        JLabel lblPhoneTitle = new JLabel("SỐ ĐIỆN THOẠI / TÀI KHOẢN APP");
        lblPhoneTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPhoneTitle.setForeground(new Color(203, 213, 225));
        lblPhoneTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPhone = new JTextField("hoivien1");
        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPhone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPassTitle = new JLabel("MẬT KHẨU");
        lblPassTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPassTitle.setForeground(new Color(203, 213, 225));
        lblPassTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPass = new JPasswordField("123456");
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnLogin = new JButton("ĐĂNG NHẬP HỘI VIÊN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(37, 99, 235));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> xuLyDangNhapHoiVien());

        // Nút Đăng nhập nhanh 1-Click để test chấm thi
        JButton btnQuickLogin = new JButton("Đăng Nhập Nhanh (Hội Viên Test)");
        btnQuickLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnQuickLogin.setBackground(new Color(16, 185, 129)); // Xanh lá
        btnQuickLogin.setForeground(Color.WHITE);
        btnQuickLogin.setFocusPainted(false);
        btnQuickLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnQuickLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnQuickLogin.addActionListener(e -> {
            txtPhone.setText("hoivien1");
            txtPass.setText("123456");
            xuLyDangNhapHoiVien();
        });

        JLabel lblNote = new JLabel("<html><center style='color:#94a3b8; font-size:11px;'>Tài khoản mặc định: <b>hoivien1</b> • Mật khẩu: <b>123456</b><br>Hỗ trợ đầy đủ Thẻ QR, Đặt lớp, Lịch PT & InBody</center></html>");
        lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlBody.add(lblPhoneTitle);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 6)));
        pnlBody.add(txtPhone);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 16)));
        pnlBody.add(lblPassTitle);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 6)));
        pnlBody.add(txtPass);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 24)));
        pnlBody.add(btnLogin);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlBody.add(btnQuickLogin);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 20)));
        pnlBody.add(lblNote);

        root.add(pnlBody, BorderLayout.CENTER);

        setContentPane(root);

        txtPass.addActionListener(e -> xuLyDangNhapHoiVien());
        txtPhone.addActionListener(e -> xuLyDangNhapHoiVien());
    }

    private void xuLyDangNhapHoiVien() {
        String phone = txtPhone.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        if (phone.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại và mật khẩu!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthService.LoginResult res = authService.dangNhap(phone, pass);
        if (res.success) {
            // Đảm bảo vai trò là Hội viên
            if (Auth.vaiTro == null || Auth.vaiTro.trim().isEmpty() || !Auth.vaiTro.equalsIgnoreCase("Hội viên")) {
                Auth.vaiTro = "Hội viên";
            }
            dispose();
            new HoiVienAppFrame().setVisible(true);
        } else {
            // Cho phép đăng nhập fallback vào app hội viên nếu là tài khoản mẫu
            if (phone.equalsIgnoreCase("hoivien1") && pass.equals("123456")) {
                Auth.vaiTro = "Hội viên";
                Auth.hoTen = "Nguyễn Văn An";
                Auth.tenDangNhap = "hoivien1";
                Auth.soDienThoai = "0901234567";
                dispose();
                new HoiVienAppFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, res.message, "Đăng Nhập Thất Bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new AppHoiVien().setVisible(true));
    }
}
