package com.nhom12.gym.gui;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.gui.panels.*;
import com.nhom12.gym.util.Auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ỨNG DỤNG RIÊNG BIỆT CHO HỘI VIÊN FITLIFE GYM (FITLIFE MEMBER PORTAL)
 * Thiết kế chuẩn Sidebar Dọc (Vertical Menu Layout) bên trái:
 * 1. Thẻ Hội Viên Điện Tử & Mã QR Check-in Tự Động
 * 2. Đặt Chỗ Lớp Học Nhóm (Yoga, Zumba, Boxing...)
 * 3. Lịch Tập Huấn Luyện Viên Cá Nhân (PT 1:1)
 * 4. Theo Dõi Chỉ Số Cơ Thể InBody (Cơ, Mỡ, BMI)
 * 5. Quản Lý Gói Tập & Thanh Toán Gia Hạn VietQR
 * 6. Đánh Giá & Góp Ý Dịch Vụ
 */
public class HoiVienAppFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);
    private final List<SidebarMenuItem> sidebarMenuItems = new ArrayList<>();
    private JLabel lblWorkspaceTitle;
    private JLabel lblWorkspaceBreadcrumb;

    private static final String TAB_CARD_QR = "TAB_CARD_QR";
    private static final String TAB_CLASS = "TAB_CLASS";
    private static final String TAB_PT = "TAB_PT";
    private static final String TAB_INBODY = "TAB_INBODY";
    private static final String TAB_PACKAGE = "TAB_PACKAGE";
    private static final String TAB_FEEDBACK = "TAB_FEEDBACK";

    public static class SidebarMenuItem {
        public JButton button;
        public String title;
        public String subtitle;
        public String cardKey;
    }

    public HoiVienAppFrame() {
        if (Auth.vaiTro == null || Auth.vaiTro.trim().isEmpty()) {
            Auth.vaiTro = "Hội viên";
            Auth.hoTen = "Lê Anh Duy";
            Auth.tenDangNhap = "hoivien1";
            Auth.soDienThoai = "0974482749";
            Auth.chiNhanh = "CN01 - Cơ sở 1 (Quận 1)";
        }

        setTitle("FITLIFE MEMBER - CỔNG THÀNH VIÊN ĐIỆN TỬ (" + Auth.hoTen + ")");
        setSize(1350, 820);
        setMinimumSize(new Dimension(1050, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(248, 250, 252));

        Color accentColor = new Color(234, 179, 8); // Vàng Gold
        String memberName = (Auth.hoTen != null && !Auth.hoTen.isEmpty()) ? Auth.hoTen : "Lê Anh Duy";
        String memberPhone = (Auth.soDienThoai != null && !Auth.soDienThoai.isEmpty()) ? Auth.soDienThoai : "0974482749";

        // --- A. SIDEBAR DỌC BÊN TRÁI (275px) ---
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(15, 23, 42)); // Slate 900
        sidebar.setPreferredSize(new Dimension(275, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 41, 59)));

        // Sidebar Top: Thương hiệu & Hồ sơ Hội Viên
        JPanel sidebarTop = new JPanel();
        sidebarTop.setLayout(new BoxLayout(sidebarTop, BoxLayout.Y_AXIS));
        sidebarTop.setOpaque(false);
        sidebarTop.setBorder(new EmptyBorder(20, 18, 12, 18));

        JLabel lblBrand = new JLabel("FITLIFE MEMBER");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBadge = new JLabel("  VIP DIAMOND MEMBER  ");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setForeground(new Color(15, 23, 42));
        lblBadge.setBackground(accentColor);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        lblBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Box hồ sơ hội viên
        JPanel profileBox = new JPanel(new BorderLayout(12, 0));
        profileBox.setBackground(new Color(30, 41, 59));
        profileBox.setBorder(new EmptyBorder(10, 12, 10, 12));
        profileBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAvatar = new JLabel("HV", SwingConstants.CENTER);
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAvatar.setForeground(new Color(15, 23, 42));
        lblAvatar.setBackground(accentColor);
        lblAvatar.setOpaque(true);
        lblAvatar.setPreferredSize(new Dimension(38, 38));

        JPanel profileText = new JPanel(new GridLayout(3, 1, 0, 2));
        profileText.setOpaque(false);

        JLabel lblStaff = new JLabel(memberName);
        lblStaff.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStaff.setForeground(new Color(241, 245, 249));

        JLabel lblShift = new JLabel("Thành viên VIP • Kích hoạt");
        lblShift.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblShift.setForeground(new Color(52, 211, 153));

        JLabel lblPhone = new JLabel("SĐT: " + memberPhone);
        lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPhone.setForeground(new Color(148, 163, 184));

        profileText.add(lblStaff);
        profileText.add(lblShift);
        profileText.add(lblPhone);
        profileBox.add(lblAvatar, BorderLayout.WEST);
        profileBox.add(profileText, BorderLayout.CENTER);

        sidebarTop.add(lblBrand);
        sidebarTop.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebarTop.add(lblBadge);
        sidebarTop.add(Box.createRigidArea(new Dimension(0, 14)));
        sidebarTop.add(profileBox);
        sidebar.add(sidebarTop, BorderLayout.NORTH);

        // Sidebar Center: Menu cuộn dọc
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scrollMenu = new JScrollPane(menuContainer);
        scrollMenu.setBorder(null);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(14);
        sidebar.add(scrollMenu, BorderLayout.CENTER);

        // Thêm 6 mục menu dọc cho Hội viên
        addSidebarItem(menuContainer, "Thẻ VIP & Check-in QR", "Mã thẻ cá nhân & cửa flap barrier", TAB_CARD_QR, accentColor, true);
        addSidebarItem(menuContainer, "Đặt Chỗ Lớp Học", "Lớp Yoga, Zumba, Boxing", TAB_CLASS, accentColor, false);
        addSidebarItem(menuContainer, "Lịch Tập Với PT", "Số buổi kèm & kỷ lục tạ PR", TAB_PT, accentColor, false);
        addSidebarItem(menuContainer, "Chỉ Số Cơ Thể InBody", "Theo dõi cơ, mỡ, BMI cá nhân", TAB_INBODY, accentColor, false);
        addSidebarItem(menuContainer, "Gói Tập & Gia Hạn", "Hợp đồng & thanh toán VietQR", TAB_PACKAGE, accentColor, false);

        // Sidebar Bottom: Đổi mật khẩu & Đăng xuất
        JPanel sidebarBottom = new JPanel(new GridLayout(2, 1, 0, 8));
        sidebarBottom.setOpaque(false);
        sidebarBottom.setBorder(new EmptyBorder(12, 14, 16, 14));

        JButton btnChangePass = new JButton("Đổi Mật Khẩu");
        btnChangePass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnChangePass.setBackground(new Color(30, 41, 59));
        btnChangePass.setForeground(new Color(203, 213, 225));
        btnChangePass.setFocusPainted(false);
        btnChangePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangePass.addActionListener(e -> moDialogDoiMatKhau());

        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(30, 41, 59));
        btnLogout.setForeground(new Color(248, 113, 113));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(239, 68, 68), 1),
                new EmptyBorder(5, 12, 5, 12)
        ));
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

        sidebarBottom.add(btnChangePass);
        sidebarBottom.add(btnLogout);
        sidebar.add(sidebarBottom, BorderLayout.SOUTH);

        root.add(sidebar, BorderLayout.WEST);

        // --- B. MAIN WORKSPACE BÊN PHẢI ---
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(new Color(248, 250, 252));

        // Workspace Top Header
        JPanel workspaceHeader = new JPanel(new BorderLayout());
        workspaceHeader.setBackground(Color.WHITE);
        workspaceHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(12, 24, 12, 24)
        ));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setOpaque(false);
        lblWorkspaceTitle = new JLabel("THẺ VIP & CHECK-IN QR");
        lblWorkspaceTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWorkspaceTitle.setForeground(new Color(15, 23, 42));

        lblWorkspaceBreadcrumb = new JLabel("Cổng Hội Viên > Thẻ VIP & Check-in QR");
        lblWorkspaceBreadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblWorkspaceBreadcrumb.setForeground(new Color(100, 116, 139));

        headerLeft.add(lblWorkspaceTitle);
        headerLeft.add(lblWorkspaceBreadcrumb);
        workspaceHeader.add(headerLeft, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        String today = new SimpleDateFormat("EEEE, dd/MM/yyyy").format(new Date());
        JLabel lblDate = new JLabel(today);
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDate.setForeground(new Color(71, 85, 105));
        headerRight.add(lblDate);
        workspaceHeader.add(headerRight, BorderLayout.EAST);

        mainArea.add(workspaceHeader, BorderLayout.NORTH);

        // Content Area CardLayout
        contentArea.setOpaque(false);
        contentArea.add(new HoiVienTheDienTuPanel(), TAB_CARD_QR);
        contentArea.add(new HoiVienDatLopPanel(), TAB_CLASS);
        contentArea.add(new HoiVienLichPTPanel(), TAB_PT);
        contentArea.add(new HoiVienInBodyPanel(), TAB_INBODY);
        contentArea.add(new HoiVienGoiTapPanel(), TAB_PACKAGE);

        mainArea.add(contentArea, BorderLayout.CENTER);
        root.add(mainArea, BorderLayout.CENTER);

        setContentPane(root);
    }

    private void addSidebarItem(JPanel container, String title, String subtitle, String cardKey, Color accent, boolean isDefault) {
        SidebarMenuItem item = new SidebarMenuItem();
        item.title = title;
        item.subtitle = subtitle;
        item.cardKey = cardKey;

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (Boolean.TRUE.equals(getClientProperty("active"))) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(accent);
                    g2.fillRect(0, 0, 4, getHeight());
                    g2.dispose();
                }
            }
        };
        btn.setLayout(new BorderLayout(10, 0));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setPreferredSize(new Dimension(255, 46));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 1));
        textPanel.setOpaque(false);
        JLabel lblMain = new JLabel(title);
        lblMain.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMain.setForeground(new Color(226, 232, 240));

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(new Color(148, 163, 184));

        textPanel.add(lblMain);
        textPanel.add(lblSub);
        btn.add(textPanel, BorderLayout.CENTER);

        item.button = btn;
        sidebarMenuItems.add(item);

        btn.addActionListener(e -> selectSidebarItem(item, accent));

        java.awt.event.MouseAdapter ma = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                selectSidebarItem(item, accent);
            }
        };
        textPanel.addMouseListener(ma);
        lblMain.addMouseListener(ma);
        lblSub.addMouseListener(ma);

        container.add(btn);
        container.add(Box.createRigidArea(new Dimension(0, 3)));

        if (isDefault) {
            selectSidebarItem(item, accent);
        } else {
            setButtonState(btn, false);
        }
    }

    private void selectSidebarItem(SidebarMenuItem selectedItem, Color accent) {
        for (SidebarMenuItem item : sidebarMenuItems) {
            boolean isSel = (item == selectedItem);
            setButtonState(item.button, isSel);
        }

        lblWorkspaceTitle.setText(selectedItem.title.toUpperCase());
        lblWorkspaceBreadcrumb.setText("Cổng Hội Viên > " + selectedItem.title + " (" + selectedItem.subtitle + ")");
        cardLayout.show(contentArea, selectedItem.cardKey);
    }

    private void setButtonState(JButton btn, boolean active) {
        btn.putClientProperty("active", active);
        if (active) {
            btn.setBackground(new Color(30, 41, 59));
        } else {
            btn.setBackground(new Color(15, 23, 42));
        }
        btn.repaint();
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
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new HoiVienAppFrame().setVisible(true));
    }
}
