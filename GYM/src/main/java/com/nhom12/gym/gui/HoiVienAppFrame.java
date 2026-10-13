package com.nhom12.gym.gui;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.gui.panels.*;
import com.nhom12.gym.util.Auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Ứng Dụng Riêng Biệt Dành Riêng Cho Hội Viên FitLife Gym (FitLife Member Portal)
 * Tách biệt hoàn toàn với hệ thống quản trị nội bộ của nhân viên:
 * 1. Thẻ Hội Viên Điện Tử & Mã QR Check-in Tự Động
 * 2. Đặt Chỗ Lớp Học Nhóm (Group Fitness: Yoga, Zumba, Boxing...)
 * 3. Lịch Tập Huấn Luyện Viên Cá Nhân (PT 1:1)
 * 4. Theo Dõi Chỉ Số Cơ Thể InBody (Cơ, Mỡ, BMI, BMR)
 * 5. Quản Lý Gói Tập & Thanh Toán Gia Hạn VietQR
 * 6. Đánh Giá & Góp Ý Chất Lượng Dịch Vụ
 */
public class HoiVienAppFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);
    private final JPanel pnlTabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    private JButton btnActiveTab = null;

    private static final String TAB_CARD_QR = "TAB_CARD_QR";
    private static final String TAB_CLASS = "TAB_CLASS";
    private static final String TAB_PT = "TAB_PT";
    private static final String TAB_INBODY = "TAB_INBODY";
    private static final String TAB_PACKAGE = "TAB_PACKAGE";
    private static final String TAB_FEEDBACK = "TAB_FEEDBACK";

    public HoiVienAppFrame() {
        // Thiết lập vai trò hội viên mặc định nếu chưa đăng nhập
        if (Auth.vaiTro == null || Auth.vaiTro.trim().isEmpty()) {
            Auth.vaiTro = "Hội viên";
            Auth.hoTen = "Nguyễn Văn An";
            Auth.tenDangNhap = "hoivien1";
            Auth.soDienThoai = "0901234567";
        }

        setTitle("FITLIFE MEMBER - CỔNG THÀNH VIÊN ĐIỆN TỬ (" + Auth.hoTen + ")");
        setSize(1180, 780);
        setMinimumSize(new Dimension(1000, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(248, 250, 252));

        // 1. TOP HEADER BRAND & MEMBER PROFILE
        JPanel topHeader = new JPanel(new BorderLayout(15, 0));
        topHeader.setBackground(new Color(15, 23, 42)); // Slate 900 Đẳng Cấp
        topHeader.setPreferredSize(new Dimension(0, 62));
        topHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 41, 59)),
                new EmptyBorder(0, 24, 0, 24)
        ));

        // Trái: Thương hiệu FitLife Member App
        JPanel brandLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 14));
        brandLeft.setOpaque(false);

        JLabel lblBrand = new JLabel("FITLIFE MEMBER");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblDivider = new JLabel("|");
        lblDivider.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDivider.setForeground(new Color(71, 85, 105));

        JLabel lblSub = new JLabel("Cổng Thành Viên Thông Minh");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(148, 163, 184));

        JLabel lblVipBadge = new JLabel(" VIP DIAMOND ");
        lblVipBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblVipBadge.setOpaque(true);
        lblVipBadge.setBackground(new Color(234, 179, 8)); // Vàng Gold
        lblVipBadge.setForeground(new Color(15, 23, 42));
        lblVipBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

        brandLeft.add(lblBrand);
        brandLeft.add(lblDivider);
        brandLeft.add(lblSub);
        brandLeft.add(lblVipBadge);
        topHeader.add(brandLeft, BorderLayout.WEST);

        // Phải: Thông tin hội viên & Nút Đăng xuất
        JPanel userRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        userRight.setOpaque(false);

        String memberName = (Auth.hoTen != null && !Auth.hoTen.isEmpty()) ? Auth.hoTen : "Hội Viên VIP";
        JLabel lblUserGreeting = new JLabel("Xin chào, " + memberName);
        lblUserGreeting.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserGreeting.setForeground(new Color(241, 245, 249));

        JButton btnChangePass = new JButton("Đổi Mật Khẩu");
        btnChangePass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnChangePass.setBackground(new Color(30, 41, 59));
        btnChangePass.setForeground(new Color(203, 213, 225));
        btnChangePass.setFocusPainted(false);
        btnChangePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangePass.addActionListener(e -> moDialogDoiMatKhau());

        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 38, 38));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất khỏi Ứng dụng Hội viên FitLife?",
                    "Xác Nhận Đăng Xuất", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                Auth.clear();
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        userRight.add(lblUserGreeting);
        userRight.add(btnChangePass);
        userRight.add(btnLogout);
        topHeader.add(userRight, BorderLayout.EAST);

        // 2. SUB NAVIGATION BAR (THANH TAB CHUYÊN DỤNG CHO HỘI VIÊN)
        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setBackground(Color.WHITE);
        navContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        pnlTabBar.setOpaque(false);
        pnlTabBar.setBorder(new EmptyBorder(4, 16, 4, 16));

        themTabButton("Thẻ VIP & Check-in QR", TAB_CARD_QR, true);
        themTabButton("Đặt Chỗ Lớp Học", TAB_CLASS, false);
        themTabButton("Lịch Tập Với PT", TAB_PT, false);
        themTabButton("Chỉ Số Cơ Thể InBody", TAB_INBODY, false);
        themTabButton("Gói Tập & Gia Hạn", TAB_PACKAGE, false);
        themTabButton("Đánh Giá & Góp Ý", TAB_FEEDBACK, false);

        navContainer.add(pnlTabBar, BorderLayout.CENTER);

        // Gom Top Header và Nav Container vào Top Panel
        JPanel topGroup = new JPanel(new BorderLayout());
        topGroup.add(topHeader, BorderLayout.NORTH);
        topGroup.add(navContainer, BorderLayout.SOUTH);
        root.add(topGroup, BorderLayout.NORTH);

        // 3. MAIN CENTER CONTENT AREA (CARD LAYOUT CHỨA 6 PANEL ĐẶC THÙ)
        contentArea.setOpaque(false);
        contentArea.add(new HoiVienTheDienTuPanel(), TAB_CARD_QR);
        contentArea.add(new HoiVienDatLopPanel(), TAB_CLASS);
        contentArea.add(new HoiVienLichPTPanel(), TAB_PT);
        contentArea.add(new HoiVienInBodyPanel(), TAB_INBODY);
        contentArea.add(new HoiVienGoiTapPanel(), TAB_PACKAGE);
        contentArea.add(new HoiVienGopYPanel(), TAB_FEEDBACK);

        root.add(contentArea, BorderLayout.CENTER);

        setContentPane(root);
    }

    private void themTabButton(String title, String cardKey, boolean isDefault) {
        JButton btn = new JButton(title);
        btn.setFont(new Font("Segoe UI", isDefault ? Font.BOLD : Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isDefault) {
            btnActiveTab = btn;
            setButtonActiveStyle(btn);
        } else {
            setButtonInactiveStyle(btn);
        }

        btn.addActionListener(e -> {
            if (btnActiveTab != null) {
                setButtonInactiveStyle(btnActiveTab);
            }
            btnActiveTab = btn;
            setButtonActiveStyle(btn);
            cardLayout.show(contentArea, cardKey);
        });

        pnlTabBar.add(btn);
    }

    private void setButtonActiveStyle(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(238, 242, 255)); // Indigo nhạt
        btn.setForeground(new Color(67, 56, 202));   // Indigo 700
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(199, 210, 254), 1),
                new EmptyBorder(6, 16, 6, 16)
        ));
    }

    private void setButtonInactiveStyle(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(71, 85, 105));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1),
                new EmptyBorder(6, 16, 6, 16)
        ));
    }

    private void moDialogDoiMatKhau() {
        JPasswordField pfOld = new JPasswordField();
        JPasswordField pfNew = new JPasswordField();
        JPasswordField pfConfirm = new JPasswordField();

        JPanel pnl = new JPanel(new GridLayout(3, 2, 8, 12));
        pnl.add(new JLabel("Mật khẩu hiện tại:"));
        pnl.add(pfOld);
        pnl.add(new JLabel("Mật khẩu mới:"));
        pnl.add(pfNew);
        pnl.add(new JLabel("Xác nhận mật khẩu mới:"));
        pnl.add(pfConfirm);

        int opt = JOptionPane.showConfirmDialog(this, pnl,
                "Đổi Mật Khẩu Tài Khoản Hội Viên", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            String passNew = new String(pfNew.getPassword()).trim();
            String passConfirm = new String(pfConfirm.getPassword()).trim();
            if (passNew.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới không được để trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!passNew.equals(passConfirm)) {
                JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu mới không trùng khớp!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Đã đổi mật khẩu thành công! Vui lòng ghi nhớ mật khẩu mới của bạn.",
                    "Thành Công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new HoiVienAppFrame().setVisible(true));
    }
}
