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
        rightPanel.add(Box.createRigidArea(new Dimension(0, 22)));
        rightPanel.add(btnLogin);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Nút liên kết Đăng ký tài khoản
        JButton btnRegisterApp = new JButton("Bạn chưa có tài khoản? Đăng kí ngay tại đây!");
        btnRegisterApp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegisterApp.setForeground(new Color(2, 132, 199));
        btnRegisterApp.setBorderPainted(false);
        btnRegisterApp.setContentAreaFilled(false);
        btnRegisterApp.setFocusPainted(false);
        btnRegisterApp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegisterApp.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegisterApp.addActionListener(e -> moDialogDangKyAppHoiVien());
        rightPanel.add(btnRegisterApp);

        mainContainer.add(leftPanel);
        mainContainer.add(rightPanel);
        setContentPane(mainContainer);

        btnLogin.addActionListener(e -> xuLyDangNhap());
        txtPass.addActionListener(e -> xuLyDangNhap());
        txtUser.addActionListener(e -> xuLyDangNhap());
    }

    private void moDialogDangKyAppHoiVien() {
        JDialog dlg = new JDialog(this, "Đăng Ký Tài Khoản FitLife App Dành Cho Hội Viên", true);
        dlg.setSize(480, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 14));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 25, 20, 25));

        JTextField tfName = new JTextField();
        JTextField tfPhone = new JTextField();
        JPasswordField tfPass = new JPasswordField();
        JComboBox<String> cbBr = new JComboBox<>(new String[]{
                "CN01 - Cơ sở 1 (Quận 1)",
                "CN02 - Cơ sở 2 (Tân Bình)"
        });

        form.add(new JLabel("Họ Và Tên (*):"));
        form.add(tfName);
        form.add(new JLabel("Số Điện Thoại (*):"));
        form.add(tfPhone);
        form.add(new JLabel("Mật Khẩu Đăng Nhập (*):"));
        form.add(tfPass);
        form.add(new JLabel("Chi Nhánh Đăng Ký:"));
        form.add(cbBr);

        JPanel bottomDlg = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        bottomDlg.setBackground(Color.WHITE);

        JButton btnSubmit = new JButton("Đăng Ký Ngay");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(37, 99, 235));
        btnSubmit.setForeground(Color.WHITE);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        bottomDlg.add(btnCancel);
        bottomDlg.add(btnSubmit);

        btnCancel.addActionListener(ev -> dlg.dispose());
        btnSubmit.addActionListener(ev -> {
            String name = tfName.getText().trim();
            String phone = tfPhone.getText().trim();
            String pass = new String(tfPass.getPassword()).trim();
            if (name.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập đầy đủ thông tin để đăng ký tài khoản!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!phone.matches("^0(3|5|7|8|9)[0-9]{8}$")) {
                JOptionPane.showMessageDialog(dlg, "Số điện thoại không hợp lệ! Vui lòng nhập đúng 10 chữ số thuộc các đầu số nhà mạng Việt Nam (03, 05, 07, 08, 09).", "Lỗi Định Dạng", JOptionPane.WARNING_MESSAGE);
                tfPhone.requestFocus();
                return;
            }

            String code = "HV" + (System.currentTimeMillis() % 1000);

            // Lưu tài khoản Hội viên vào hệ thống
            try {
                com.nhom12.gym.dao.TaiKhoanDAO dao = new com.nhom12.gym.dao.TaiKhoanDAO();
                dao.them(new com.nhom12.gym.entity.TaiKhoan(phone, pass, "Hội viên", name));
            } catch (Exception ignored) {}

            try {
                com.nhom12.gym.dao.HoiVienDAO hvDAO = new com.nhom12.gym.dao.HoiVienDAO();
                com.nhom12.gym.entity.HoiVien hv = new com.nhom12.gym.entity.HoiVien();
                hv.setHoTen(name);
                hv.setSoDienThoai(phone);
                hv.setMaTheQR(code);
                hv.setChiNhanhDangKy((String) cbBr.getSelectedItem());
                hv.setGoiTapDangKy("Gói Trải Nghiệm 1 Tháng");
                hv.setNgayDangKy(new java.util.Date());
                hv.setSoTienDaThanhToan(500000.0);
                hv.setHinhThucThanhToan("Chuyển khoản (VietQR)");
                hv.setTrangThai("Hoạt động");
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.add(java.util.Calendar.MONTH, 1);
                hv.setNgayHetHan(c.getTime());
                hvDAO.them(hv);
            } catch (Exception ignored) {}

            txtUser.setText(phone);
            txtPass.setText(pass);
            dlg.dispose();

            // Hiển thị ngay cửa sổ mã QR và thông tin tài khoản
            new GuiMaQRDialog(this, code, name, phone, "Gói Đăng Ký Trực Tuyến", (String) cbBr.getSelectedItem(), phone, pass).setVisible(true);
        });

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottomDlg, BorderLayout.SOUTH);
        dlg.setVisible(true);
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
            String msg = "Xin chào " + Auth.vaiTro + " - " + Auth.hoTen + "!";
            if (res.isOffline) {
                msg += "\n\n⚠️ ĐANG CHẠY CHẾ ĐỘ OFFLINE – DỮ LIỆU ĐỆM GIẢ LẬP\n(Không có kết nối máy chủ SQL Server, hệ thống kích hoạt đệm ngoại tuyến).";
                JOptionPane.showMessageDialog(this, msg, "Cảnh Báo Chế Độ Ngoại Tuyến", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, msg, "Đăng Nhập Thành Công", JOptionPane.INFORMATION_MESSAGE);
            }
            this.dispose();
            if ("Hội viên".equalsIgnoreCase(Auth.vaiTro)) {
                new HoiVienAppFrame().setVisible(true);
            } else {
                new MainDashboard().setVisible(true);
            }
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
