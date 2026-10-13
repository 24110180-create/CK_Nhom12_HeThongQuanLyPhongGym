package com.nhom12.gym.gui;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.util.Auth;
import com.nhom12.gym.util.ImageHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginForm extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginForm() {
        setTitle("FitLife Gym Management - Đăng Nhập Hệ Thống");
        setSize(940, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainContainer = new JPanel(new GridLayout(1, 2));

        // 1. NỬA BÊN TRÁI: HÌNH ẢNH NỀN PHÒNG TẬP VÀ GIÀN TẠ CAO CẤP
        Image gymBg = ImageHelper.loadImage("gym_login_bg.jpg");
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (gymBg != null) {
                    g2d.drawImage(gymBg, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2d.setColor(new Color(24, 28, 36));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }

                GradientPaint overlay = new GradientPaint(
                        0, 0, new Color(10, 15, 25, 200),
                        0, getHeight(), new Color(5, 8, 15, 240)
                );
                g2d.setPaint(overlay);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);

        JLabel lblTag = new JLabel("HỆ THỐNG QUẢN LÝ THÔNG MINH");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTag.setForeground(new Color(56, 189, 248));

        JLabel lblBrand = new JLabel("FITLIFE GYM");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblSlogan = new JLabel("FITNESS & WELLNESS CENTER");
        lblSlogan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSlogan.setForeground(new Color(203, 213, 225));

        brandPanel.add(lblTag);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        brandPanel.add(lblBrand);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        brandPanel.add(lblSlogan);

        leftPanel.add(brandPanel, BorderLayout.NORTH);

        JPanel quotePanel = new JPanel();
        quotePanel.setLayout(new BoxLayout(quotePanel, BoxLayout.Y_AXIS));
        quotePanel.setOpaque(false);

        JLabel lblQuote = new JLabel("<html><i style='color:#e2e8f0;'>\"Đẳng cấp tạo nên phong độ. Hãy biến từng giọt mồ hôi thành kết quả vượt trội mỗi ngày.\"</i></html>");
        lblQuote.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblAuthor = new JLabel("Nhóm 12 - Hệ thống quản lý phòng GYM");
        lblAuthor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAuthor.setForeground(new Color(148, 163, 184));

        quotePanel.add(lblQuote);
        quotePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        quotePanel.add(lblAuthor);

        leftPanel.add(quotePanel, BorderLayout.SOUTH);

        // 2. NỬA BÊN PHẢI: FORM ĐĂNG NHẬP
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(248, 250, 252));
        rightPanel.setBorder(new EmptyBorder(45, 45, 40, 45));

        JLabel lblFormTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblFormTitle.setForeground(new Color(15, 23, 42));
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFormSub = new JLabel("Vui lòng nhập tài khoản và mật khẩu để tiếp tục");
        lblFormSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFormSub.setForeground(new Color(100, 116, 139));
        lblFormSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUserTitle = new JLabel("TÀI KHOẢN");
        lblUserTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserTitle.setForeground(new Color(71, 85, 105));
        lblUserTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUser = new JTextField(20);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUser.setPreferredSize(new Dimension(0, 42));
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUser.setText("");
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPassTitle = new JLabel("MẬT KHẨU");
        lblPassTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassTitle.setForeground(new Color(71, 85, 105));
        lblPassTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPass = new JPasswordField(20);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.setPreferredSize(new Dimension(0, 42));
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPass.setText("");
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = new JButton("ĐĂNG NHẬP NGAY");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(new Color(37, 99, 235));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 45));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(lblFormTitle);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        rightPanel.add(lblFormSub);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 22)));
        rightPanel.add(lblUserTitle);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        rightPanel.add(txtUser);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        rightPanel.add(lblPassTitle);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        rightPanel.add(txtPass);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(btnLogin);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton btnRegisterMember = new JButton("Khách hàng mới? Bấm vào đây để Đăng Ký Hội Viên Mới");
        btnRegisterMember.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegisterMember.setForeground(new Color(37, 99, 235));
        btnRegisterMember.setBorderPainted(false);
        btnRegisterMember.setContentAreaFilled(false);
        btnRegisterMember.setFocusPainted(false);
        btnRegisterMember.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegisterMember.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegisterMember.addActionListener(e -> {
            JDialog dlg = new JDialog(this, "Tiếp Nhận & Đăng Ký Hội Viên FitLife", true);
            dlg.setSize(1100, 720);
            dlg.setLocationRelativeTo(this);
            dlg.add(new com.nhom12.gym.gui.panels.DangKyHoiVienPanel(() -> dlg.dispose(), () -> dlg.dispose()));
            dlg.setVisible(true);
        });

        rightPanel.add(btnRegisterMember);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel lblSecNote = new JLabel("🔒 Hệ thống quản trị nội bộ. Tài khoản do Chủ phòng tập (Anh Đức) cấp phát.");
        lblSecNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSecNote.setForeground(new Color(148, 163, 184));
        lblSecNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(lblSecNote);

        mainContainer.add(leftPanel);
        mainContainer.add(rightPanel);
        setContentPane(mainContainer);

        btnLogin.addActionListener(e -> xuLyDangNhap());
        txtPass.addActionListener(e -> xuLyDangNhap());
        txtUser.addActionListener(e -> xuLyDangNhap());
    }

    private final com.nhom12.gym.service.AuthService authService = new com.nhom12.gym.service.AuthService();

    private void xuLyDangNhap() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        com.nhom12.gym.service.AuthService.LoginResult res = authService.dangNhap(user, pass);
        if (res.success) {
            // Kiểm tra phân quyền: Hội viên KHÔNG ĐƯỢC đăng nhập ứng dụng quản trị nội bộ
            if ("Hội viên".equalsIgnoreCase(Auth.vaiTro)) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ HỘI VIÊN (KHÁCH TẬP) KHÔNG ĐƯỢC ĐĂNG NHẬP VÀO ỨNG DỤNG NỘI BỘ NÀY!\n\n" +
                        "• Theo tài liệu phạm vi dự án FitLife Gym, App riêng cho hội viên nằm ngoài phạm vi triển khai (Out of Scope).\n" +
                        "• Hội viên chỉ tương tác gián tiếp: Vui lòng xuất trình mã QR hoặc thẻ tập tại quầy Lễ tân để check-in và nhờ lễ tân thao tác đăng ký gói / xếp lớp.",
                        "Thông Báo Phạm Vi Ứng Dụng",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String msg = "Xin chào " + Auth.vaiTro + " - " + Auth.hoTen + "!";
            if (res.isOffline) {
                msg += "\n\n⚠️ ĐANG CHẠY CHẾ ĐỘ OFFLINE – DỮ LIỆU ĐỆM GIẢ LẬP\n(Không có kết nối máy chủ SQL Server, hệ thống kích hoạt đệm ngoại tuyến).";
                JOptionPane.showMessageDialog(this, msg, "Cảnh Báo Chế Độ Ngoại Tuyến", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, msg, "Đăng Nhập Thành Công", JOptionPane.INFORMATION_MESSAGE);
            }
            this.dispose();
            new MainDashboard().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, res.message, "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
