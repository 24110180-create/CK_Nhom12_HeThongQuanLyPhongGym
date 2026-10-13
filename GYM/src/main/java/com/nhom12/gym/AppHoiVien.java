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

        // Dòng liên kết Đăng ký tài khoản
        JButton btnRegister = new JButton("Bạn chưa có tài khoản?Đăng kí ngay tại đây!");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegister.setForeground(new Color(56, 189, 248)); // Sky Blue sáng
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(e -> {
            new com.nhom12.gym.gui.DangKyTaiKhoanDialog(this, (u, p) -> {
                txtPhone.setText(u);
                txtPass.setText(p);
            }).setVisible(true);
        });

        pnlBody.add(lblPhoneTitle);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 6)));
        pnlBody.add(txtPhone);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 16)));
        pnlBody.add(lblPassTitle);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 6)));
        pnlBody.add(txtPass);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 20)));
        pnlBody.add(btnLogin);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 14)));
        pnlBody.add(btnRegister);

        root.add(pnlBody, BorderLayout.CENTER);

        setContentPane(root);

        txtPass.addActionListener(e -> xuLyDangNhapHoiVien());
        txtPhone.addActionListener(e -> xuLyDangNhapHoiVien());
    }

    private void moDialogDangKyApp() {
        JDialog dlg = new JDialog(this, "Đăng Ký Thành Viên FitLife App Mới", true);
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

        JButton btnSubmit = new JButton("Hoàn Tất Đăng Ký");
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
                JOptionPane.showMessageDialog(dlg, "Số điện thoại không hợp lệ! Vui lòng nhập đúng 10 chữ số thuộc các đầu số nhà mạng Việt Nam.", "Lỗi Định Dạng", JOptionPane.WARNING_MESSAGE);
                tfPhone.requestFocus();
                return;
            }

            String code = "HV" + (System.currentTimeMillis() % 1000);

            // Lưu tài khoản & hồ sơ hội viên
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

            txtPhone.setText(phone);
            txtPass.setText(pass);
            dlg.dispose();

            new com.nhom12.gym.gui.GuiMaQRDialog(this, code, name, phone, "Gói Đăng Ký Trực Tuyến", (String) cbBr.getSelectedItem(), phone, pass).setVisible(true);

            // Tự động kích hoạt đăng nhập
            xuLyDangNhapHoiVien();
        });

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottomDlg, BorderLayout.SOUTH);
        dlg.setVisible(true);
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
            if (Auth.soDienThoai == null || Auth.soDienThoai.isEmpty()) {
                Auth.soDienThoai = phone;
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
