package com.nhom12.gym.gui;

import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.Auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.BiConsumer;

/**
 * MÀN HÌNH ĐĂNG KÝ TÀI KHOẢN PHÂN HỆ 4 VAI TRÒ NHÂN SỰ NỘI BỘ
 * Tuân thủ phạm vi dự án FitLife Gym: Chỉ nhân sự nội bộ mới có tài khoản đăng nhập.
 * 1. Trạm Lễ Tân (Chị Linh) - Emerald #10B981
 * 2. Không Gian Huấn Luyện Viên (Anh Tuấn) - Amber #F59E0B
 * 3. Điều Hành Quản Lý Chi Nhánh (Anh Sơn) - Sky Blue #0EA5E9
 * 4. Ban Quản Trị / Chủ Phòng (Anh Đức) - Indigo #6366F1
 * Hội viên (Khách tập) tương tác gián tiếp tại quầy Lễ tân (Out of Scope app riêng).
 */
public class DangKyTaiKhoanDialog extends JDialog {

    public static class RoleDefinition {
        public final String roleKey;
        public final String title;
        public final String badge;
        public final String description;
        public final String appName;
        public final Color color;
        public final String initials;

        public RoleDefinition(String roleKey, String title, String badge, String description, String appName, Color color, String initials) {
            this.roleKey = roleKey;
            this.title = title;
            this.badge = badge;
            this.description = description;
            this.appName = appName;
            this.color = color;
            this.initials = initials;
        }
    }

    private static final RoleDefinition[] ROLES = new RoleDefinition[]{
            new RoleDefinition("Lễ tân", "TRẠM LỄ TÂN", "LỄ TÂN (CHỊ LINH)",
                    "Check-in < 2s, tạo hồ sơ, bán gói tập, thu tiền, xếp lớp Yoga/Zumba/Cardio", "App Trạm Lễ Tân", new Color(16, 185, 129), "LT"),
            new RoleDefinition("HLV", "TRẠM HLV", "HUẤN LUYỆN VIÊN (ANH TUẤN)",
                    "Xem/cập nhật lịch dạy cá nhân, điểm danh lớp học, theo dõi InBody & PT 1:1", "App Huấn Luyện Viên", new Color(245, 158, 11), "PT"),
            new RoleDefinition("Quản lý", "TRẠM QUẢN LÝ", "QUẢN LÝ CHI NHÁNH (ANH SƠN)",
                    "Xếp ca làm việc nhân viên, quản lý kho hàng, kiểm toán số liệu cơ sở phụ trách", "App Quản Lý Chi Nhánh", new Color(14, 165, 233), "QL"),
            new RoleDefinition("Chủ", "CHỦ PHÒNG TẬP", "CHỦ PHÒNG TẬP (ANH ĐỨC)",
                    "Báo cáo tổng hợp chuỗi 2 cơ sở, cảnh báo hết hạn 7 ngày, quản trị nhân sự, giá VIP ẩn 50%", "Bảng Điều Hành Trung Tâm", new Color(99, 102, 241), "AD")
    };

    private RoleDefinition selectedRole = ROLES[0]; // Mặc định Trạm Lễ Tân

    private final JPanel[] cardPanels = new JPanel[ROLES.length];
    private final JPanel pnlSelectedRoleBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
    private final JLabel lblSelectedRoleBanner = new JLabel();
    private final JButton btnSubmit = new JButton();

    private final JTextField txtFullName = new JTextField();
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JPasswordField txtConfirmPassword = new JPasswordField();
    private final JComboBox<String> cbBranch = new JComboBox<>(new String[]{
            "CN01 - Cơ sở 1 (Quận 1)",
            "CN02 - Cơ sở 2 (Tân Bình)",
            "Toàn hệ thống (Chủ chuỗi / Quản trị)"
    });

    private final Frame parentFrame;
    private final BiConsumer<String, String> onSuccessCallback;

    public DangKyTaiKhoanDialog(Frame parent, BiConsumer<String, String> onSuccessCallback) {
        super(parent, "ĐĂNG KÝ TÀI KHOẢN NHÂN SỰ NỘI BỘ FITLIFE GYM - 4 VAI TRÒ", true);
        this.parentFrame = parent;
        this.onSuccessCallback = onSuccessCallback;

        setSize(920, 720);
        setMinimumSize(new Dimension(860, 680));
        setLocationRelativeTo(parent);
        setResizable(true);

        initUI();
        updateRoleSelection();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(248, 250, 252));

        // --- 1. TOP HEADER BANNER (Slate 900) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(15, 23, 42));
        pnlHeader.setBorder(new EmptyBorder(16, 28, 16, 28));

        JPanel headerText = new JPanel(new GridLayout(2, 1, 0, 4));
        headerText.setOpaque(false);

        JLabel lblBrand = new JLabel("FITLIFE GYM MANAGEMENT • ĐĂNG KÝ TÀI KHOẢN NHÂN SỰ NỘI BỘ");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Hệ thống phục vụ 4 vai trò nhân sự nội bộ theo phạm vi dự án. Bấm chọn vai trò của bạn để thiết lập tài khoản & khởi chạy:");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(148, 163, 184));

        headerText.add(lblBrand);
        headerText.add(lblSub);
        pnlHeader.add(headerText, BorderLayout.CENTER);
        root.add(pnlHeader, BorderLayout.NORTH);

        // --- 2. BODY CONTAINER (CHỌN ROLE + FORM NHẬP LIỆU) ---
        JPanel pnlBody = new JPanel();
        pnlBody.setLayout(new BoxLayout(pnlBody, BoxLayout.Y_AXIS));
        pnlBody.setOpaque(false);
        pnlBody.setBorder(new EmptyBorder(16, 24, 16, 24));

        // A. TIÊU ĐỀ BƯỚC 1: CHỌN 1 TRONG 4 VAI TRÒ
        JLabel lblStep1 = new JLabel("BƯỚC 1: BẤM CHỌN VAI TRÒ BẠN MUỐN ĐĂNG KÝ (*):");
        lblStep1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStep1.setForeground(new Color(30, 41, 59));
        lblStep1.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBody.add(lblStep1);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 8)));

        // B. LƯỚI 4 THẺ VAI TRÒ (4 BENTO ROLE CARDS - TĂNG CHIỀU CAO TRÁNH MẤT CHỮ)
        JPanel pnlRoleGrid = new JPanel(new GridLayout(1, 4, 10, 0));
        pnlRoleGrid.setOpaque(false);
        pnlRoleGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        pnlRoleGrid.setPreferredSize(new Dimension(860, 130));
        pnlRoleGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < ROLES.length; i++) {
            final RoleDefinition def = ROLES[i];

            JPanel card = new JPanel(new BorderLayout(8, 6));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.setOpaque(true);

            // Card Header: Initials Avatar + Badge
            JPanel cardTop = new JPanel(new BorderLayout(6, 0));
            cardTop.setOpaque(false);

            JLabel lblInit = new JLabel(def.initials, SwingConstants.CENTER);
            lblInit.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblInit.setForeground(Color.WHITE);
            lblInit.setBackground(def.color);
            lblInit.setOpaque(true);
            lblInit.setPreferredSize(new Dimension(28, 28));

            JLabel lblBadge = new JLabel(" " + def.badge + " ");
            lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lblBadge.setForeground(def.color);

            cardTop.add(lblInit, BorderLayout.WEST);
            cardTop.add(lblBadge, BorderLayout.CENTER);

            // Card Center: Role Title + Short Description (BoxLayout dọc thoáng)
            JPanel cardCenter = new JPanel();
            cardCenter.setLayout(new BoxLayout(cardCenter, BoxLayout.Y_AXIS));
            cardCenter.setOpaque(false);

            JLabel lblTitle = new JLabel(def.title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTitle.setForeground(new Color(15, 23, 42));
            lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblDesc = new JLabel("<html><span style='color: #64748b; font-size: 10px; line-height: 1.25;'>" + def.description + "</span></html>");
            lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

            cardCenter.add(Box.createRigidArea(new Dimension(0, 3)));
            cardCenter.add(lblTitle);
            cardCenter.add(Box.createRigidArea(new Dimension(0, 3)));
            cardCenter.add(lblDesc);

            card.add(cardTop, BorderLayout.NORTH);
            card.add(cardCenter, BorderLayout.CENTER);

            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedRole = def;
                    updateRoleSelection();
                }
            });

            cardPanels[i] = card;
            pnlRoleGrid.add(card);
        }

        pnlBody.add(pnlRoleGrid);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 12)));

        // C. BANNER THÔNG BÁO VAI TRÒ ĐANG CHỌN (DÙNG JPANEL VỚI NỀN OPAQUE ĐẶC, CHỐNG ĐÈ CHỮ)
        lblSelectedRoleBanner.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlSelectedRoleBanner.add(lblSelectedRoleBanner);
        pnlSelectedRoleBanner.setOpaque(true);
        pnlSelectedRoleBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pnlSelectedRoleBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBody.add(pnlSelectedRoleBanner);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 12)));

        // D. TIÊU ĐỀ BƯỚC 2: FORM NHẬP THÔNG TIN
        JLabel lblStep2 = new JLabel("BƯỚC 2: THIẾT LẬP THÔNG TIN ĐĂNG NHẬP VÀ CƠ SỞ (*):");
        lblStep2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStep2.setForeground(new Color(30, 41, 59));
        lblStep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBody.add(lblStep2);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 8)));

        // Form Fields (2 cột sạch sẽ)
        JPanel formContainer = new JPanel(new GridLayout(5, 2, 14, 10));
        formContainer.setOpaque(false);
        formContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 205));
        formContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 13);

        JLabel l1 = new JLabel("Họ Và Tên (*):"); l1.setFont(fontLabel);
        txtFullName.setFont(fontInput);
        formContainer.add(l1); formContainer.add(txtFullName);

        JLabel l2 = new JLabel("Số Điện Thoại / Tên Đăng Nhập (*):"); l2.setFont(fontLabel);
        txtUsername.setFont(fontInput);
        formContainer.add(l2); formContainer.add(txtUsername);

        JLabel l3 = new JLabel("Mật Khẩu (*):"); l3.setFont(fontLabel);
        txtPassword.setFont(fontInput);
        formContainer.add(l3); formContainer.add(txtPassword);

        JLabel l4 = new JLabel("Xác Nhận Mật Khẩu (*):"); l4.setFont(fontLabel);
        txtConfirmPassword.setFont(fontInput);
        formContainer.add(l4); formContainer.add(txtConfirmPassword);

        JLabel l5 = new JLabel("Chi Nhánh Phụ Trách / Tập Luyện (*):"); l5.setFont(fontLabel);
        cbBranch.setFont(fontInput);
        formContainer.add(l5); formContainer.add(cbBranch);

        pnlBody.add(formContainer);
        pnlBody.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblFootnote = new JLabel("<html><span style='color: #64748b; font-size: 11px;'>" +
                "• <b>Đăng ký xong có thể chọn mở trực tiếp giao diện độc lập của vai trò đó ngay tức thì.</b><br>" +
                "• Hội viên: Tự động cấp mã thẻ QR cá nhân và kích hoạt gói trải nghiệm thành viên VIP.<br>" +
                "• Nhân sự (Lễ tân, HLV, Quản lý): Sẵn sàng ca trực tại cơ sở phân công, quản lý và vận hành thông suốt.</span></html>");
        lblFootnote.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBody.add(lblFootnote);

        // BỌC BODY VÀO JSCROLLPANE ĐẢM BẢO KHÔNG BAO GIỜ BỊ ĐÈ GIAO DIỆN
        JScrollPane scroll = new JScrollPane(pnlBody);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scroll, BorderLayout.CENTER);

        // --- 3. BOTTOM ACTIONS BAR ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnCancel = new JButton("Hủy Bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addActionListener(e -> dispose());

        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(310, 38));
        btnSubmit.addActionListener(e -> xuLyDangKy());

        pnlBottom.add(btnCancel);
        pnlBottom.add(btnSubmit);
        root.add(pnlBottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void updateRoleSelection() {
        for (int i = 0; i < ROLES.length; i++) {
            RoleDefinition def = ROLES[i];
            JPanel card = cardPanels[i];
            boolean isSel = (def == selectedRole);

            if (isSel) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(def.color, 2),
                        new EmptyBorder(8, 10, 8, 10)
                ));
            } else {
                card.setBackground(new Color(241, 245, 249));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                        new EmptyBorder(9, 11, 9, 11)
                ));
            }
            card.repaint();
        }

        // Cập nhật banner với màu nền solid opaque (100% không xuyên thấu) để xóa triệt để chữ cũ
        Color bgSolid;
        if ("Lễ tân".equalsIgnoreCase(selectedRole.roleKey)) {
            bgSolid = new Color(236, 253, 245); // Emerald-50
        } else if ("HLV".equalsIgnoreCase(selectedRole.roleKey)) {
            bgSolid = new Color(254, 243, 199); // Amber-100
        } else if ("Quản lý".equalsIgnoreCase(selectedRole.roleKey)) {
            bgSolid = new Color(240, 249, 255); // Sky-50
        } else {
            bgSolid = new Color(238, 242, 255); // Indigo-50
        }

        pnlSelectedRoleBanner.setBackground(bgSolid);
        pnlSelectedRoleBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selectedRole.color, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        lblSelectedRoleBanner.setForeground(selectedRole.color.darker());
        lblSelectedRoleBanner.setText("● ĐANG CHỌN: " + selectedRole.title + " (" + selectedRole.badge + ") • Khởi chạy: " + selectedRole.appName);
        pnlSelectedRoleBanner.revalidate();
        pnlSelectedRoleBanner.repaint();

        btnSubmit.setBackground(selectedRole.color);
        btnSubmit.setText("Đăng Ký & Khởi Chạy " + selectedRole.title);

        // Gợi ý chi nhánh phù hợp
        if ("Chủ".equalsIgnoreCase(selectedRole.roleKey)) {
            cbBranch.setSelectedIndex(2); // Toàn hệ thống
        } else if ("Quản lý".equalsIgnoreCase(selectedRole.roleKey)) {
            cbBranch.setSelectedIndex(1); // Tân Bình
        } else {
            cbBranch.setSelectedIndex(0); // CN01
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
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập / Số điện thoại '" + username + "' đã tồn tại trên hệ thống!\n" +
                    "Vui lòng chọn tên đăng nhập khác hoặc sử dụng số điện thoại của bạn.",
                    "Trùng Tên Đăng Nhập", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        // Lưu tài khoản nhân sự mới
        TaiKhoan newAccount = new TaiKhoan(username, pass, selectedRole.roleKey, fullName, branch);
        boolean ok = tkDAO.them(newAccount);

        if (!ok) {
            JOptionPane.showMessageDialog(this, "Không thể tạo tài khoản. Vui lòng kiểm tra lại kết nối CSDL!", "Lỗi Lưu Trữ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hộp thoại tùy chọn sau đăng ký thành công
        String[] options = {"Khởi Chạy " + selectedRole.title + " Ngay", "Quay Lại Đăng Nhập"};
        int choice = JOptionPane.showOptionDialog(this,
                "<html><body style='width: 340px; padding: 4px;'>" +
                        "<h3 style='color: " + toHexColor(selectedRole.color) + "; margin: 0;'>ĐĂNG KÝ TÀI KHOẢN NHÂN SỰ THÀNH CÔNG!</h3>" +
                        "<p>Họ tên: <b>" + fullName + "</b></p>" +
                        "<p>Tài khoản: <b>" + username + "</b></p>" +
                        "<p>Vai trò phân hệ: <b>" + selectedRole.title + " (" + selectedRole.badge + ")</b></p>" +
                        "<p>Cơ sở hoạt động: <b>" + branch + "</b></p>" +
                        "<hr style='border: 0; border-top: 1px solid #e2e8f0; margin: 8px 0;'/>" +
                        "<p style='color: #0284c7; font-weight: bold;'>Bạn có muốn mở trực tiếp Ứng Dụng dành riêng cho " + selectedRole.title + " ngay bây giờ không?</p>" +
                        "</body></html>",
                "Đăng Ký Thành Công",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            // MỞ THẲNG ỨNG DỤNG CHO VAI TRÒ ĐÓ!
            Auth.setSession(newAccount, false);
            dispose();
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            SwingUtilities.invokeLater(() -> {
                new MainDashboard(selectedRole.roleKey).setVisible(true);
            });
        } else {
            // Quay về màn hình đăng nhập, điền sẵn thông tin
            if (onSuccessCallback != null) {
                onSuccessCallback.accept(username, pass);
            }
            dispose();
        }
    }

    private String toHexColor(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
