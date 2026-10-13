package com.nhom12.gym.gui;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiConsumer;

/**
 * Màn hình Đăng Ký Tài Khoản Đa Vai Trò (Multi-Role Registration Dialog)
 * Cho phép người dùng chọn vai trò (Hội viên, Lễ tân, HLV, Quản lý, Chủ phòng)
 * và tự tạo tài khoản mới để đăng nhập trực tiếp vào hệ thống FitLife Gym.
 */
public class DangKyTaiKhoanDialog extends JDialog {

    private final JTextField txtUsername = new JTextField();
    private final JTextField txtFullName = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JPasswordField txtConfirmPassword = new JPasswordField();
    private final JComboBox<String> cbBranch = new JComboBox<>(new String[]{
            "CN01 - Cơ sở 1 (Quận 1)",
            "CN02 - Cơ sở 2 (Tân Bình)",
            "Toàn hệ thống (VIP / Quản trị)"
    });

    private String selectedRole = "Hội viên";
    private final JPanel pnlRoleButtons = new JPanel(new GridLayout(1, 5, 8, 0));
    private final JButton[] roleButtons = new JButton[5];
    private final BiConsumer<String, String> onSuccessCallback;

    public DangKyTaiKhoanDialog(Frame parent, BiConsumer<String, String> onSuccessCallback) {
        super(parent, "ĐĂNG KÝ TÀI KHOẢN FITLIFE GYM - CHỌN VAI TRÒ", true);
        this.onSuccessCallback = onSuccessCallback;

        setSize(680, 620);
        setMinimumSize(new Dimension(640, 580));
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // 1. TOP HEADER BANNER (Slate 900)
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(15, 23, 42));
        pnlHeader.setBorder(new EmptyBorder(20, 28, 20, 28));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN HỆ THỐNG FITLIFE GYM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Lựa chọn vai trò của bạn và thiết lập thông tin đăng nhập để truy cập ứng dụng");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(148, 163, 184));

        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        pnlHeader.add(titleBox, BorderLayout.CENTER);
        root.add(pnlHeader, BorderLayout.NORTH);

        // 2. FORM BODY
        JPanel pnlBody = new JPanel();
        pnlBody.setLayout(new BoxLayout(pnlBody, BoxLayout.Y_AXIS));
        pnlBody.setOpaque(false);
        pnlBody.setBorder(new EmptyBorder(22, 32, 20, 32));

        // PHẦN 1: CHỌN VAI TRÒ (ROLE SELECTOR)
        JLabel lblRolePrompt = new JLabel("BƯỚC 1: BẤM CHỌN VAI TRÒ ĐĂNG KÝ (*):");
        lblRolePrompt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRolePrompt.setForeground(new Color(30, 41, 59));
        lblRolePrompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBody.add(lblRolePrompt);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 8)));

        pnlRoleButtons.setOpaque(false);
        pnlRoleButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnlRoleButtons.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] roles = {"Hội viên", "Lễ tân", "HLV", "Quản lý", "Chủ"};
        String[] displayNames = {"Hội Viên", "Lễ Tân", "HLV / PT", "Quản Lý", "Chủ Phòng"};

        for (int i = 0; i < roles.length; i++) {
            final String role = roles[i];
            JButton btn = new JButton(displayNames[i]);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            roleButtons[i] = btn;

            if (role.equals(selectedRole)) {
                setRoleButtonSelected(btn, true);
            } else {
                setRoleButtonSelected(btn, false);
            }

            btn.addActionListener(e -> {
                selectedRole = role;
                for (int j = 0; j < roles.length; j++) {
                    setRoleButtonSelected(roleButtons[j], roles[j].equals(selectedRole));
                }
            });
            pnlRoleButtons.add(btn);
        }
        pnlBody.add(pnlRoleButtons);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 20)));

        // PHẦN 2: THÔNG TIN ĐĂNG NHẬP
        JLabel lblInfoPrompt = new JLabel("BƯỚC 2: NHẬP THÔNG TIN TÀI KHOẢN VÀ MẬT KHẨU (*):");
        lblInfoPrompt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblInfoPrompt.setForeground(new Color(30, 41, 59));
        lblInfoPrompt.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBody.add(lblInfoPrompt);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel formGrid = new JPanel(new GridLayout(5, 2, 16, 12));
        formGrid.setOpaque(false);
        formGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));
        formGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 13);

        JLabel l1 = new JLabel("Họ Và Tên (*):"); l1.setFont(fontLabel);
        txtFullName.setFont(fontInput);
        formGrid.add(l1); formGrid.add(txtFullName);

        JLabel l2 = new JLabel("Tên Đăng Nhập / SĐT (*):"); l2.setFont(fontLabel);
        txtUsername.setFont(fontInput);
        formGrid.add(l2); formGrid.add(txtUsername);

        JLabel l3 = new JLabel("Mật Khẩu (*):"); l3.setFont(fontLabel);
        txtPassword.setFont(fontInput);
        formGrid.add(l3); formGrid.add(txtPassword);

        JLabel l4 = new JLabel("Xác Nhận Mật Khẩu (*):"); l4.setFont(fontLabel);
        txtConfirmPassword.setFont(fontInput);
        formGrid.add(l4); formGrid.add(txtConfirmPassword);

        JLabel l5 = new JLabel("Chi Nhánh Phụ Trách / Tập:"); l5.setFont(fontLabel);
        cbBranch.setFont(fontInput);
        formGrid.add(l5); formGrid.add(cbBranch);

        pnlBody.add(formGrid);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 14)));

        JLabel lblNotice = new JLabel("<html><span style='color: #64748b; font-size: 11px;'>" +
                "• Hội viên: Tự động cấp mã thẻ QR và gói trải nghiệm 1 tháng.<br>" +
                "• Nhân viên (Lễ tân, HLV, Quản lý): Sẵn sàng ca trực tại chi nhánh đã chọn.<br>" +
                "• Tài khoản đăng ký xong có thể đăng nhập ngay vào ứng dụng của vai trò đó.</span></html>");
        lblNotice.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBody.add(lblNotice);

        root.add(pnlBody, BorderLayout.CENTER);

        // 3. BOTTOM BUTTONS BAR
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 15));
        pnlBottom.setBackground(new Color(248, 250, 252));
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnCancel = new JButton("Hủy Bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> dispose());

        JButton btnSubmit = new JButton("HOÀN TẤT ĐĂNG KÝ");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(37, 99, 235)); // Indigo / Blue 600
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(190, 38));
        btnSubmit.addActionListener(e -> xuLyDangKy());

        pnlBottom.add(btnCancel);
        pnlBottom.add(btnSubmit);
        root.add(pnlBottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void setRoleButtonSelected(JButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(new Color(37, 99, 235));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(29, 78, 216), 2),
                    new EmptyBorder(8, 6, 8, 6)
            ));
        } else {
            btn.setBackground(new Color(241, 245, 249));
            btn.setForeground(new Color(51, 65, 85));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    new EmptyBorder(8, 6, 8, 6)
            ));
        }
    }

    private void xuLyDangKy() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String confirmPass = new String(txtConfirmPassword.getPassword()).trim();
        String branch = (String) cbBranch.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các thông tin bắt buộc (*)", "Thiếu Thông Tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không trùng khớp!", "Lỗi Mật Khẩu", JOptionPane.WARNING_MESSAGE);
            txtConfirmPassword.requestFocus();
            return;
        }

        if (pass.length() < 4) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có độ dài tối thiểu 4 ký tự!", "Mật khẩu quá ngắn", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        TaiKhoanDAO tkDAO = new TaiKhoanDAO();
        if (tkDAO.timTheoTenDangNhap(username) != null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập '" + username + "' đã tồn tại trên hệ thống!\nVui lòng chọn tên đăng nhập khác hoặc số điện thoại của bạn.", "Trùng Tên Đăng Nhập", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        // Lưu tài khoản mới
        TaiKhoan newAccount = new TaiKhoan(username, pass, selectedRole, fullName, branch);
        boolean ok = tkDAO.them(newAccount);

        if (!ok) {
            JOptionPane.showMessageDialog(this, "Không thể tạo tài khoản. Vui lòng thử lại!", "Lỗi Lưu Trữ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Nếu là Hội Viên: Tự động khởi tạo thêm hồ sơ HoiVien và mã thẻ QR
        String memberCode = "HV" + (System.currentTimeMillis() % 10000);
        if ("Hội viên".equalsIgnoreCase(selectedRole)) {
            try {
                HoiVienDAO hvDAO = new HoiVienDAO();
                HoiVien hv = new HoiVien();
                hv.setHoTen(fullName);
                hv.setSoDienThoai(username);
                hv.setMaTheQR(memberCode);
                hv.setChiNhanhDangKy(branch);
                hv.setGoiTapDangKy("Gói Trải Nghiệm 1 Tháng");
                hv.setNgayDangKy(new Date());
                hv.setSoTienDaThanhToan(500000.0);
                hv.setHinhThucThanhToan("Chuyển khoản (VietQR)");
                hv.setTrangThai("Hoạt động");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 1);
                hv.setNgayHetHan(cal.getTime());
                hvDAO.them(hv);
            } catch (Exception ignored) {}
        }

        JOptionPane.showMessageDialog(this,
                "<html><body style='width: 320px; padding: 5px;'>" +
                "<h3 style='color: #10b981; margin: 0;'>ĐĂNG KÝ TÀI KHOẢN THÀNH CÔNG!</h3>" +
                "<p>Họ tên: <b>" + fullName + "</b></p>" +
                "<p>Tên đăng nhập: <b>" + username + "</b></p>" +
                "<p>Vai trò: <b>" + selectedRole + "</b></p>" +
                "<p>Chi nhánh: <b>" + branch + "</b></p>" +
                ("Hội viên".equalsIgnoreCase(selectedRole) ? "<p>Mã thẻ QR cá nhân: <b>" + memberCode + "</b></p>" : "") +
                "<p style='color: #3b82f6;'>Bạn có thể sử dụng tài khoản này để đăng nhập ngay vào ứng dụng!</p>" +
                "</body></html>",
                "Đăng Ký Thành Công", JOptionPane.INFORMATION_MESSAGE);

        if (onSuccessCallback != null) {
            onSuccessCallback.accept(username, pass);
        }

        dispose();
    }
}
