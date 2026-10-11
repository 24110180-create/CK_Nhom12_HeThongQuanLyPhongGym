package com.nhom12.gym.gui;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.util.Auth;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class MainDashboard extends JFrame {

    private String currentRole;
    private JPanel rootPanel;

    // Workstation components for non-Owner roles (or Owner preview)
    private JPanel workspaceContainer;
    private JLabel lblWorkspaceTitle;
    private JLabel lblWorkspaceBreadcrumb;
    private final List<SidebarMenuItem> sidebarMenuItems = new ArrayList<>();

    // =========================================================================
    // DỮ LIỆU ĐIỀU HÀNH 5 TRỤ CỘT CHO GÓC NHÌN CHỦ PHÒNG (EXECUTIVE NAVIGATION)
    // =========================================================================
    public static class OwnerModuleItem {
        public String title;
        public String subtitle;
        public Supplier<JPanel> supplier;

        public OwnerModuleItem(String title, String subtitle, Supplier<JPanel> supplier) {
            this.title = title;
            this.subtitle = subtitle;
            this.supplier = supplier;
        }
    }

    public static class OwnerPillar {
        public String name;
        public Color accentColor;
        public List<OwnerModuleItem> items = new ArrayList<>();

        public OwnerPillar(String name, Color accentColor) {
            this.name = name;
            this.accentColor = accentColor;
        }
    }

    private final List<OwnerPillar> ownerPillars = new ArrayList<>();
    private int activeOwnerPillarIdx = 0;
    private OwnerModuleItem activeOwnerModule;
    private JPanel ownerWorkspaceContainer;
    private JPanel pnlOwnerPillarsBar;
    private JPanel pnlOwnerSubRibbon;

    public MainDashboard() {
        this.currentRole = (Auth.vaiTro != null && !Auth.vaiTro.trim().isEmpty()) ? Auth.vaiTro : "Chủ";
        setTitle("FitLife Gym Management - " + getRoleDisplayName(currentRole));
        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initOwnerPillars();
        initLayout();
    }

    private void initOwnerPillars() {
        if (!ownerPillars.isEmpty()) return;

        // Trụ cột 0: Báo Cáo & Thống Kê
        OwnerPillar p0 = new OwnerPillar("Báo Cáo & Thống Kê", new Color(99, 102, 241));
        p0.items.add(new OwnerModuleItem("Báo Cáo Doanh Thu", "Doanh thu đa chi nhánh (2 cơ sở) & hội viên hết hạn", () -> new BaoCaoThongKePanel()));
        p0.items.add(new OwnerModuleItem("Phân Tích Giờ Cao Điểm", "Phân tích mật độ tập luyện 6h-22h", () -> new HeatmapGioCaoDiemPanel()));
        p0.items.add(new OwnerModuleItem("Tổng Quan Sức Chứa", "KPIs tổng hợp & tỷ lệ lấp đầy", () -> new TrangChuPanel()));
        p0.items.add(new OwnerModuleItem("Lịch Sử Giao Dịch", "Nhật ký hóa đơn & dòng tiền", () -> new LichSuGiaoDichPanel()));
        ownerPillars.add(p0);

        // Trụ cột 1: Vận Hành Phòng Tập
        OwnerPillar p1 = new OwnerPillar("Vận Hành Phòng Tập", new Color(16, 185, 129));
        p1.items.add(new OwnerModuleItem("Trạm Quét Check-in", "Honeywell Scanner & Cửa Flap Barrier", () -> new CheckInForm()));
        p1.items.add(new OwnerModuleItem("Danh Sách Hội Viên", "Hồ sơ cá nhân & thẻ tập", () -> new QuanLyHoiVienPanel()));
        p1.items.add(new OwnerModuleItem("Đăng Ký Hội Viên Mới", "Tạo hợp đồng & cấp mã QR", () -> new DangKyHoiVienPanel()));
        p1.items.add(new OwnerModuleItem("Gia Hạn Gói Tập", "Nâng cấp gói & gia hạn thẻ", () -> new GiaHanGoiTapPanel()));
        p1.items.add(new OwnerModuleItem("Quản Lý Gói Tập", "Cấu hình bảng giá dịch vụ", () -> new QuanLyGoiTapPanel()));
        p1.items.add(new OwnerModuleItem("Lịch Lớp Học", "Lớp Yoga, Zumba, Boxing", () -> new LichLopHocPanel()));
        ownerPillars.add(p1);

        // Trụ cột 2: Kinh Doanh & Dịch Vụ
        OwnerPillar p2 = new OwnerPillar("Kinh Doanh & Dịch Vụ", new Color(14, 165, 233));
        p2.items.add(new OwnerModuleItem("Quầy Bán Hàng POS", "Bán nước giải khát, whey, đồ tập", () -> new BanHangPOSPanel()));
        p2.items.add(new OwnerModuleItem("Quản Lý Kho Hàng", "Kiểm kê xuất nhập tồn kho", () -> new QuanLyKhoHangPanel()));
        p2.items.add(new OwnerModuleItem("Quản Lý Tủ Locker", "Tủ đồ cá nhân thông minh", () -> new QuanLyLockerPanel()));
        p2.items.add(new OwnerModuleItem("Quản Lý Khuyến Mãi", "Mã khuyến mãi & chiết khấu", () -> new QuanLyKhuyenMaiPanel()));
        p2.items.add(new OwnerModuleItem("Chăm Sóc Khách Hàng", "Nhắc hạn tự động Zalo/SMS", () -> new ChamSocKhachHangPanel()));
        p2.items.add(new OwnerModuleItem("Khảo Sát & Phản Hồi", "Khảo sát ý kiến hội viên", () -> new QuanLyPhanHoiPanel()));
        ownerPillars.add(p2);

        // Trụ cột 3: Huấn Luyện Viên
        OwnerPillar p3 = new OwnerPillar("Huấn Luyện Viên", new Color(245, 158, 11));
        p3.items.add(new OwnerModuleItem("Theo Dõi InBody", "Mỡ cơ, trọng lượng, BMR", () -> new TheoDoiChiSoPanel()));
        p3.items.add(new OwnerModuleItem("Lịch Dạy Huấn Luyện Viên", "Lịch phân ca huấn luyện viên", () -> new LichDayHLVPanel()));
        p3.items.add(new OwnerModuleItem("Sổ Tay Tiến Độ PT", "Giáo án tập luyện cá nhân 1:1", () -> new GhiChuTienDoPTPanel()));
        p3.items.add(new OwnerModuleItem("Hồ Sơ Hội Viên", "Lịch sử tập luyện cá nhân", () -> new ChiTietHoiVienPanel()));
        ownerPillars.add(p3);

        // Trụ cột 4: Quản Trị Hệ Thống
        OwnerPillar p4 = new OwnerPillar("Quản Trị Hệ Thống", new Color(236, 72, 153));
        p4.items.add(new OwnerModuleItem("Quản Lý Nhân Sự", "Danh sách HLV & nhân viên", () -> new QuanLyNhanSuPanel()));
        p4.items.add(new OwnerModuleItem("Tài Khoản App Hội Viên", "Cấp & quản lý tài khoản app", () -> new QuanLyTaiKhoanAppPanel()));
        p4.items.add(new OwnerModuleItem("Quản Lý Thiết Bị", "Bảo dưỡng máy móc & giàn tạ", () -> new QuanLyThietBiPanel()));
        p4.items.add(new OwnerModuleItem("Phân Ca Làm Việc", "Xếp lịch làm việc theo tuần", () -> new PhanCaLamViecPanel()));
        p4.items.add(new OwnerModuleItem("Phân Quyền Người Dùng", "Quản trị quyền 5 vai trò", () -> new PhanQuyenPanel()));
        p4.items.add(new OwnerModuleItem("Cài Đặt Chung", "Thông tin phòng & đa cơ sở", () -> new CaiDatHeThongPanel()));
        ownerPillars.add(p4);

        // Mặc định nạp Báo Cáo Doanh Thu
        activeOwnerModule = p0.items.get(0);
    }

    private String getRoleDisplayName(String role) {
        if ("Chủ".equalsIgnoreCase(role)) return "Quản Trị Chủ Hệ Thống";
        if ("Lễ tân".equalsIgnoreCase(role)) return "Trạm Vận Hành Lễ Tân";
        if ("HLV".equalsIgnoreCase(role)) return "Không Gian Huấn Luyện Viên";
        if ("Quản lý".equalsIgnoreCase(role)) return "Quản Lý Vận Hành Chi Nhánh";
        if ("Hội viên".equalsIgnoreCase(role)) return "Cổng Dịch Vụ Hội Viên";
        return "Quản Trị Hệ Thống";
    }

    private void initLayout() {
        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(248, 250, 252));

        if ("Chủ".equalsIgnoreCase(currentRole)) {
            buildOwnerMasterHubLayout();
        } else {
            buildStaffWorkstationLayout(currentRole);
        }

        setContentPane(rootPanel);
        revalidate();
        repaint();
    }

    // =========================================================================
    // 1. GIAO DIỆN CHỦ: EXECUTIVE DUAL-TIER NAVIGATION & EMBEDDED WORKSPACE
    // =========================================================================
    private void buildOwnerMasterHubLayout() {
        rootPanel.removeAll();

        // Container cho 2 tầng điều hướng trên cùng
        JPanel topHeader = new JPanel();
        topHeader.setLayout(new BoxLayout(topHeader, BoxLayout.Y_AXIS));
        topHeader.setOpaque(false);

        // --- A. THANH ĐIỀU HÀNH CẤP CAO (TOP NAVBAR - TẦNG 1) ---
        JPanel navBar = new JPanel(new BorderLayout(15, 0));
        navBar.setBackground(new Color(15, 23, 42)); // Dark Slate #0F172A
        navBar.setPreferredSize(new Dimension(0, 52));
        navBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        navBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 41, 59)),
                new EmptyBorder(0, 20, 0, 20)
        ));

        // Trái: Tên thương hiệu & phân hệ
        JPanel brandGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandGroup.setOpaque(false);

        JLabel lblLogo = new JLabel("FITLIFE GYM");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblDivider = new JLabel("|");
        lblDivider.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDivider.setForeground(new Color(71, 85, 105));

        JLabel lblSubBrand = new JLabel("Quản Trị Chủ Hệ Thống");
        lblSubBrand.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubBrand.setForeground(new Color(148, 163, 184));

        brandGroup.add(lblLogo);
        brandGroup.add(lblDivider);
        brandGroup.add(lblSubBrand);
        navBar.add(brandGroup, BorderLayout.WEST);

        // Giữa: 5 Trụ Cột Điều Hành (Pillar Tabs)
        pnlOwnerPillarsBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        pnlOwnerPillarsBar.setOpaque(false);
        rebuildPillarsBar();
        navBar.add(pnlOwnerPillarsBar, BorderLayout.CENTER);

        // Phải: Chuyển vai trò, thông tin người dùng và nút đăng xuất
        JPanel userGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userGroup.setOpaque(false);

        JLabel lblSwitch = new JLabel("Góc nhìn:");
        lblSwitch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSwitch.setForeground(new Color(148, 163, 184));

        JComboBox<String> cbViewMode = new JComboBox<>(new String[]{
                "Chủ Phòng",
                "Trạm Lễ Tân",
                "Trạm Huấn Luyện Viên",
                "Trạm Quản Lý",
                "Giao Diện Hội Viên"
        });
        cbViewMode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbViewMode.setBackground(new Color(30, 41, 59));
        cbViewMode.setForeground(Color.WHITE);
        cbViewMode.setPreferredSize(new Dimension(160, 32));
        cbViewMode.addActionListener(e -> {
            int sel = cbViewMode.getSelectedIndex();
            if (sel == 1) buildStaffWorkstationLayout("Lễ tân");
            else if (sel == 2) buildStaffWorkstationLayout("HLV");
            else if (sel == 3) buildStaffWorkstationLayout("Quản lý");
            else if (sel == 4) buildStaffWorkstationLayout("Hội viên");
            else buildOwnerMasterHubLayout();
            revalidate();
            repaint();
        });

        JLabel lblUser = new JLabel(Auth.hoTen != null ? Auth.hoTen : "Anh Đức");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(241, 245, 249));

        JButton btnLogout = createLogoutButton();

        userGroup.add(lblSwitch);
        userGroup.add(cbViewMode);
        userGroup.add(lblUser);
        userGroup.add(btnLogout);
        navBar.add(userGroup, BorderLayout.EAST);

        // --- B. THANH CHỨC NĂNG CHI TIẾT (SUB-NAVIGATION - TẦNG 2) ---
        pnlOwnerSubRibbon = new JPanel(new BorderLayout(10, 0));
        pnlOwnerSubRibbon.setBackground(Color.WHITE);
        pnlOwnerSubRibbon.setPreferredSize(new Dimension(0, 44));
        pnlOwnerSubRibbon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        pnlOwnerSubRibbon.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(0, 16, 0, 16)
        ));

        rebuildSubRibbon();

        topHeader.add(navBar);
        topHeader.add(pnlOwnerSubRibbon);
        rootPanel.add(topHeader, BorderLayout.NORTH);

        // --- C. KHÔNG GIAN LÀM VIỆC TRỰC TIẾP (EMBEDDED WORKSPACE) ---
        ownerWorkspaceContainer = new JPanel(new BorderLayout());
        ownerWorkspaceContainer.setOpaque(false);
        rootPanel.add(ownerWorkspaceContainer, BorderLayout.CENTER);

        // Nạp mô-đun đang chọn
        if (activeOwnerModule != null) {
            loadOwnerModule(activeOwnerModule);
        }
    }

    private void rebuildPillarsBar() {
        pnlOwnerPillarsBar.removeAll();

        for (int i = 0; i < ownerPillars.size(); i++) {
            final int idx = i;
            OwnerPillar p = ownerPillars.get(i);
            boolean isActive = (i == activeOwnerPillarIdx);

            JButton btnPillar = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (isActive) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setColor(new Color(56, 189, 248)); // Sky Blue accent indicator
                        g2.fillRect(0, getHeight() - 3, getWidth(), 3);
                        g2.dispose();
                    }
                }
            };
            btnPillar.setText(p.name);
            btnPillar.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 13));
            btnPillar.setForeground(isActive ? Color.WHITE : new Color(148, 163, 184));
            btnPillar.setBackground(isActive ? new Color(30, 41, 59) : new Color(15, 23, 42));
            btnPillar.setFocusPainted(false);
            btnPillar.setBorder(new EmptyBorder(0, 14, 0, 14));
            btnPillar.setPreferredSize(new Dimension(btnPillar.getPreferredSize().width, 52));
            btnPillar.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnPillar.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!isActive) {
                        btnPillar.setForeground(new Color(241, 245, 249));
                        btnPillar.setBackground(new Color(24, 34, 53));
                    }
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!isActive) {
                        btnPillar.setForeground(new Color(148, 163, 184));
                        btnPillar.setBackground(new Color(15, 23, 42));
                    }
                }
            });

            btnPillar.addActionListener(e -> {
                activeOwnerPillarIdx = idx;
                rebuildPillarsBar();
                rebuildSubRibbon();
                if (!p.items.isEmpty()) {
                    loadOwnerModule(p.items.get(0));
                }
                pnlOwnerPillarsBar.revalidate();
                pnlOwnerPillarsBar.repaint();
            });

            pnlOwnerPillarsBar.add(btnPillar);
        }
    }

    private void rebuildSubRibbon() {
        pnlOwnerSubRibbon.removeAll();

        if (activeOwnerPillarIdx < 0 || activeOwnerPillarIdx >= ownerPillars.size()) return;
        OwnerPillar currentPillar = ownerPillars.get(activeOwnerPillarIdx);

        // Trái: Danh sách module text buttons
        JPanel pnlPills = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        pnlPills.setOpaque(false);

        for (OwnerModuleItem item : currentPillar.items) {
            boolean isSel = (item == activeOwnerModule);

            JButton pill = new JButton(item.title);
            pill.setFont(new Font("Segoe UI", isSel ? Font.BOLD : Font.PLAIN, 12));
            pill.setFocusPainted(false);
            pill.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (isSel) {
                pill.setBackground(new Color(241, 245, 249));
                pill.setForeground(new Color(15, 23, 42));
                pill.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                        new EmptyBorder(5, 12, 5, 12)
                ));
            } else {
                pill.setBackground(Color.WHITE);
                pill.setForeground(new Color(100, 116, 139));
                pill.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(241, 245, 249), 1),
                        new EmptyBorder(5, 12, 5, 12)
                ));
            }

            pill.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!isSel) {
                        pill.setBackground(new Color(248, 250, 252));
                        pill.setForeground(new Color(15, 23, 42));
                        pill.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                                new EmptyBorder(5, 12, 5, 12)
                        ));
                    }
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!isSel) {
                        pill.setBackground(Color.WHITE);
                        pill.setForeground(new Color(100, 116, 139));
                        pill.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(241, 245, 249), 1),
                                new EmptyBorder(5, 12, 5, 12)
                        ));
                    }
                }
            });

            pill.addActionListener(e -> {
                loadOwnerModule(item);
                rebuildSubRibbon();
            });

            pnlPills.add(pill);
        }

        JScrollPane scrollPills = new JScrollPane(pnlPills);
        scrollPills.setBorder(null);
        scrollPills.setOpaque(false);
        scrollPills.getViewport().setOpaque(false);
        scrollPills.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPills.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pnlOwnerSubRibbon.add(scrollPills, BorderLayout.CENTER);

        // Phải: Tiện ích mở cửa sổ riêng và làm mới
        JPanel pnlRightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        pnlRightActions.setOpaque(false);

        JButton btnPopout = new JButton("Mở Cửa Sổ Riêng");
        btnPopout.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnPopout.setBackground(new Color(248, 250, 252));
        btnPopout.setForeground(new Color(71, 85, 105));
        btnPopout.setFocusPainted(false);
        btnPopout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPopout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btnPopout.addActionListener(e -> {
            if (activeOwnerModule != null) {
                openModule(activeOwnerModule.title, activeOwnerModule.supplier);
            }
        });

        JButton btnRefresh = new JButton("Làm Mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRefresh.setBackground(new Color(248, 250, 252));
        btnRefresh.setForeground(new Color(71, 85, 105));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btnRefresh.addActionListener(e -> loadOwnerModule(activeOwnerModule));

        pnlRightActions.add(btnPopout);
        pnlRightActions.add(btnRefresh);
        pnlOwnerSubRibbon.add(pnlRightActions, BorderLayout.EAST);

        pnlOwnerSubRibbon.revalidate();
        pnlOwnerSubRibbon.repaint();
    }

    private void loadOwnerModule(OwnerModuleItem item) {
        if (item == null) return;
        this.activeOwnerModule = item;

        try {
            JPanel panel = item.supplier.get();
            ownerWorkspaceContainer.removeAll();
            ownerWorkspaceContainer.add(panel, BorderLayout.CENTER);
            ownerWorkspaceContainer.revalidate();
            ownerWorkspaceContainer.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải mô-đun: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // =========================================================================
    // 2. GIAO DIỆN 3 ROLE: TRẠM LÀM VIỆC CHUYÊN BIỆT (SIDEBAR + EMBEDDED WORKSPACE)
    // (Không dàn ngang quá đà, sắp xếp hợp lý, thao tác trực tiếp trên màn hình)
    // =========================================================================
    private void buildStaffWorkstationLayout(String role) {
        rootPanel.removeAll();
        sidebarMenuItems.clear();

        Color roleColor;
        String roleBadgeText;
        String avatarInitials;
        String staffName = (Auth.hoTen != null && !Auth.hoTen.isEmpty()) ? Auth.hoTen : "Người Dùng";
        String statusText = "Đang trực ca • Sẵn sàng";

        if ("Lễ tân".equalsIgnoreCase(role)) {
            roleColor = new Color(16, 185, 129);
            roleBadgeText = "TRẠM LỄ TÂN & BÁN HÀNG";
            avatarInitials = "LT";
        } else if ("HLV".equalsIgnoreCase(role)) {
            roleColor = new Color(245, 158, 11);
            roleBadgeText = "KHÔNG GIAN HUẤN LUYỆN VIÊN";
            avatarInitials = "PT";
        } else if ("Hội viên".equalsIgnoreCase(role)) {
            roleColor = new Color(14, 165, 233);
            roleBadgeText = "CỔNG DỊCH VỤ HỘI VIÊN VIP";
            avatarInitials = "HV";
            statusText = "Thành viên VIP • Đang kích hoạt";
            if (Auth.hoTen == null || Auth.hoTen.isEmpty()) {
                staffName = "Nguyễn Văn An";
            }
        } else { // Quản lý
            roleColor = new Color(99, 102, 241);
            roleBadgeText = "ĐIỀU HÀNH QUẢN LÝ CHI NHÁNH";
            avatarInitials = "QL";
        }

        // --- A. SIDEBAR BÊN TRÁI (270px) ---
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setPreferredSize(new Dimension(275, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 41, 59)));

        // Sidebar Top: Brand & Staff Card
        JPanel sidebarTop = new JPanel();
        sidebarTop.setLayout(new BoxLayout(sidebarTop, BoxLayout.Y_AXIS));
        sidebarTop.setOpaque(false);
        sidebarTop.setBorder(new EmptyBorder(20, 20, 15, 20));

        JLabel lblBrand = new JLabel("FITLIFE GYM");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBadge = new JLabel("  " + roleBadgeText + "  ");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setBackground(roleColor);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        lblBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Staff profile box
        JPanel profileBox = new JPanel(new BorderLayout(12, 0));
        profileBox.setBackground(new Color(30, 41, 59));
        profileBox.setBorder(new EmptyBorder(10, 12, 10, 12));
        profileBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAvatar = new JLabel(avatarInitials, SwingConstants.CENTER);
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAvatar.setForeground(Color.WHITE);
        lblAvatar.setBackground(roleColor);
        lblAvatar.setOpaque(true);
        lblAvatar.setPreferredSize(new Dimension(38, 38));

        JPanel profileText = new JPanel(new GridLayout(2, 1, 0, 2));
        profileText.setOpaque(false);
        JLabel lblStaff = new JLabel(staffName);
        lblStaff.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStaff.setForeground(new Color(241, 245, 249));

        JLabel lblShift = new JLabel(statusText);
        lblShift.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblShift.setForeground(new Color(52, 211, 153));

        profileText.add(lblStaff);
        profileText.add(lblShift);
        profileBox.add(lblAvatar, BorderLayout.WEST);
        profileBox.add(profileText, BorderLayout.CENTER);

        sidebarTop.add(lblBrand);
        sidebarTop.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebarTop.add(lblBadge);
        sidebarTop.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarTop.add(profileBox);
        sidebar.add(sidebarTop, BorderLayout.NORTH);

        // Sidebar Center: Navigation Menu Items
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollMenu = new JScrollPane(menuContainer);
        scrollMenu.setBorder(null);
        scrollMenu.setOpaque(false);
        scrollMenu.getViewport().setOpaque(false);
        scrollMenu.getVerticalScrollBar().setUnitIncrement(14);
        sidebar.add(scrollMenu, BorderLayout.CENTER);

        // Sidebar Bottom: Logout & Switch back for Owner
        JPanel sidebarBottom = new JPanel(new GridLayout(0, 1, 0, 8));
        sidebarBottom.setOpaque(false);
        sidebarBottom.setBorder(new EmptyBorder(15, 15, 18, 15));

        if ("Chủ".equalsIgnoreCase(Auth.vaiTro)) {
            JButton btnBackHub = new JButton("<< Về Tổng Quan Của Chủ");
            btnBackHub.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnBackHub.setBackground(new Color(30, 41, 59));
            btnBackHub.setForeground(new Color(56, 189, 248));
            btnBackHub.setFocusPainted(false);
            btnBackHub.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnBackHub.addActionListener(e -> {
                buildOwnerMasterHubLayout();
                revalidate();
                repaint();
            });
            sidebarBottom.add(btnBackHub);
        }

        JButton btnLogout = createLogoutButton();
        btnLogout.setPreferredSize(new Dimension(0, 38));
        sidebarBottom.add(btnLogout);
        sidebar.add(sidebarBottom, BorderLayout.SOUTH);

        rootPanel.add(sidebar, BorderLayout.WEST);

        // --- B. WORKSPACE BÊN PHẢI (MAIN EMBEDDED VIEWPORT) ---
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(new Color(248, 250, 252));

        // Workspace Top Header
        JPanel workspaceHeader = new JPanel(new BorderLayout());
        workspaceHeader.setBackground(Color.WHITE);
        workspaceHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(14, 25, 14, 25)
        ));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setOpaque(false);
        lblWorkspaceTitle = new JLabel("TIÊU ĐỀ MÔ-ĐUN");
        lblWorkspaceTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblWorkspaceTitle.setForeground(new Color(15, 23, 42));

        lblWorkspaceBreadcrumb = new JLabel("Trang nghiệp vụ");
        lblWorkspaceBreadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblWorkspaceBreadcrumb.setForeground(new Color(100, 116, 139));

        headerLeft.add(lblWorkspaceTitle);
        headerLeft.add(lblWorkspaceBreadcrumb);
        workspaceHeader.add(headerLeft, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        headerRight.setOpaque(false);
        String today = new SimpleDateFormat("EEEE, dd/MM/yyyy").format(new Date());
        JLabel lblDate = new JLabel("Hôm nay: " + today);
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDate.setForeground(new Color(71, 85, 105));
        headerRight.add(lblDate);
        workspaceHeader.add(headerRight, BorderLayout.EAST);

        mainArea.add(workspaceHeader, BorderLayout.NORTH);

        // Workspace Dynamic Center Container
        workspaceContainer = new JPanel(new BorderLayout());
        workspaceContainer.setOpaque(false);
        mainArea.add(workspaceContainer, BorderLayout.CENTER);

        rootPanel.add(mainArea, BorderLayout.CENTER);

        // --- C. THIẾT LẬP MENU ĐẶC THÙ CHO TỪNG ROLE ---
        if ("Lễ tân".equalsIgnoreCase(role)) {
            setupReceptionistMenu(menuContainer, roleColor);
        } else if ("HLV".equalsIgnoreCase(role)) {
            setupTrainerMenu(menuContainer, roleColor);
        } else if ("Hội viên".equalsIgnoreCase(role)) {
            setupMemberMenu(menuContainer, roleColor);
        } else {
            setupManagerMenu(menuContainer, roleColor);
        }
    }

    // Menu Lễ Tân
    private void setupReceptionistMenu(JPanel menuContainer, Color accent) {
        addSidebarItem(menuContainer, "Quét Thẻ & Check-in", "Check-in hội viên ra vào", () -> new CheckInForm(), accent, true);
        addSidebarItem(menuContainer, "Quầy Bán Hàng POS", "Bán nước, whey & phụ kiện", () -> new BanHangPOSPanel(), accent, false);
        addSidebarItem(menuContainer, "Đăng Ký Hội Viên Mới", "Tạo thẻ và hợp đồng mới", () -> new DangKyHoiVienPanel(), accent, false);
        addSidebarItem(menuContainer, "Gia Hạn Gói Tập", "Nâng cấp và gia hạn thẻ", () -> new GiaHanGoiTapPanel(), accent, false);
        addSidebarItem(menuContainer, "Quản Lý Gói Tập", "Bảng giá & cấu hình gói tập", () -> new QuanLyGoiTapPanel(), accent, false);
        addSidebarItem(menuContainer, "Tài Khoản App Hội Viên", "Cấp & mở khóa tài khoản app", () -> new QuanLyTaiKhoanAppPanel(), accent, false);
        addSidebarItem(menuContainer, "Quản Lý Tủ Đồ Locker", "Sơ đồ 20 tủ đồ thông minh", () -> new QuanLyLockerPanel(), accent, false);
        addSidebarItem(menuContainer, "Lịch Lớp Học", "Lớp Yoga, Zumba, Boxing", () -> new LichLopHocPanel(), accent, false);
        addSidebarItem(menuContainer, "Chăm Sóc Khách Hàng", "Gửi SMS & Zalo nhắc hạn", () -> new ChamSocKhachHangPanel(), accent, false);
        addSidebarItem(menuContainer, "Hồ Sơ & Lịch Sử HV", "Hồ sơ chi tiết & lượt quét", () -> new ChiTietHoiVienPanel(), accent, false);
        addSidebarItem(menuContainer, "Danh Sách Hội Viên", "Quản lý danh bạ hội viên", () -> new QuanLyHoiVienPanel(), accent, false);
    }

    // Menu Huấn Luyện Viên (HLV)
    private void setupTrainerMenu(JPanel menuContainer, Color accent) {
        addSidebarItem(menuContainer, "Lịch Dạy & Điểm Danh", "Ca dạy hôm nay & sĩ số", () -> new LichDayHLVPanel(), accent, true);
        addSidebarItem(menuContainer, "Sổ Tay Tiến Độ PT", "Nhật ký mức tạ & số buổi", () -> new GhiChuTienDoPTPanel(), accent, false);
        addSidebarItem(menuContainer, "Đo Đạc Chỉ Số InBody", "Phân tích cơ, mỡ & BMI", () -> new TheoDoiChiSoPanel(), accent, false);
        addSidebarItem(menuContainer, "Hồ Sơ Học Viên Kèm", "Hồ sơ học viên cá nhân", () -> new ChiTietHoiVienPanel(), accent, false);
        addSidebarItem(menuContainer, "Lịch Lớp Học Nhóm", "Thời khóa biểu các lớp", () -> new LichLopHocPanel(), accent, false);
    }

    // Menu Hội Viên VIP
    private void setupMemberMenu(JPanel menuContainer, Color accent) {
        addSidebarItem(menuContainer, "Thẻ VIP & Check-in QR", "Mã thẻ cá nhân & cửa tự động", () -> new HoiVienTheDienTuPanel(), accent, true);
        addSidebarItem(menuContainer, "Đặt Chỗ Lớp Học", "Lớp Yoga, Zumba, Boxing", () -> new HoiVienDatLopPanel(), accent, false);
        addSidebarItem(menuContainer, "Chỉ Số Cơ Thể InBody", "Theo dõi cơ, mỡ, BMI cá nhân", () -> new HoiVienInBodyPanel(), accent, false);
        addSidebarItem(menuContainer, "Lịch Tập Với HLV (PT)", "Số buổi kèm & kỷ lục tạ PR", () -> new HoiVienLichPTPanel(), accent, false);
        addSidebarItem(menuContainer, "Gói Tập & Gia Hạn", "Hợp đồng & thanh toán VietQR", () -> new HoiVienGoiTapPanel(), accent, false);
        addSidebarItem(menuContainer, "Đánh Giá & Góp Ý", "Chấm điểm dịch vụ & gửi ý kiến", () -> new HoiVienGopYPanel(), accent, false);
    }

    // Menu Quản Lý Chi Nhánh
    private void setupManagerMenu(JPanel menuContainer, Color accent) {
        addSidebarItem(menuContainer, "Tổng Quan Vận Hành", "Sức chứa & doanh số ngày", () -> new TrangChuPanel(), accent, true);
        addSidebarItem(menuContainer, "Báo Cáo Doanh Thu", "Doanh thu tuần, tháng, quý", () -> new BaoCaoThongKePanel(), accent, false);
        addSidebarItem(menuContainer, "Quản Lý Gói Tập", "Bảng giá & cấu hình gói tập", () -> new QuanLyGoiTapPanel(), accent, false);
        addSidebarItem(menuContainer, "Mật Độ Giờ Cao Điểm", "Heatmap lưu lượng phòng gym", () -> new HeatmapGioCaoDiemPanel(), accent, false);
        addSidebarItem(menuContainer, "Tài Khoản App Hội Viên", "Quản lý truy cập app hội viên", () -> new QuanLyTaiKhoanAppPanel(), accent, false);
        addSidebarItem(menuContainer, "Lịch Trực & Phân Ca", "Xếp ca làm việc nhân viên", () -> new PhanCaLamViecPanel(), accent, false);
        addSidebarItem(menuContainer, "Quản Lý Nhân Sự", "Danh sách HLV & Lễ tân", () -> new QuanLyNhanSuPanel(), accent, false);
        addSidebarItem(menuContainer, "Quản Lý Kho Hàng", "Tồn kho thực phẩm & đồ uống", () -> new QuanLyKhoHangPanel(), accent, false);
        addSidebarItem(menuContainer, "Quản Lý Thiết Bị Máy", "Bảo trì giàn tạ & máy chạy", () -> new QuanLyThietBiPanel(), accent, false);
        addSidebarItem(menuContainer, "Lịch Sử Giao Dịch", "Nhật ký hóa đơn toàn viện", () -> new LichSuGiaoDichPanel(), accent, false);
        addSidebarItem(menuContainer, "Đánh Giá & Phản Hồi", "Góp ý từ hội viên", () -> new QuanLyPhanHoiPanel(), accent, false);
    }

    private static class SidebarMenuItem {
        JButton button;
        String title;
        String subtitle;
        Supplier<JPanel> supplier;
        Color accent;
    }

    private void addSidebarItem(JPanel container, String title, String subtitle, Supplier<JPanel> supplier, Color accent, boolean isDefault) {
        SidebarMenuItem item = new SidebarMenuItem();
        item.title = title;
        item.subtitle = subtitle;
        item.supplier = supplier;
        item.accent = accent;

        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (Boolean.TRUE.equals(getClientProperty("active"))) {
                    g.setColor(accent);
                    g.fillRect(0, 0, 4, getHeight());
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
        lblMain.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMain.setForeground(new Color(226, 232, 240));

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblSub.setForeground(new Color(148, 163, 184));

        textPanel.add(lblMain);
        textPanel.add(lblSub);
        btn.add(textPanel, BorderLayout.CENTER);

        item.button = btn;
        sidebarMenuItems.add(item);

        btn.addActionListener(e -> selectSidebarItem(item));

        // Click on children also triggers
        java.awt.event.MouseAdapter ma = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                selectSidebarItem(item);
            }
        };
        textPanel.addMouseListener(ma);
        lblMain.addMouseListener(ma);
        lblSub.addMouseListener(ma);

        container.add(btn);
        container.add(Box.createRigidArea(new Dimension(0, 3)));

        if (isDefault) {
            selectSidebarItem(item);
        } else {
            setButtonState(btn, false, accent);
        }
    }

    private void selectSidebarItem(SidebarMenuItem selectedItem) {
        for (SidebarMenuItem item : sidebarMenuItems) {
            boolean isSel = (item == selectedItem);
            setButtonState(item.button, isSel, selectedItem.accent);
        }

        lblWorkspaceTitle.setText(selectedItem.title.toUpperCase());
        lblWorkspaceBreadcrumb.setText("Nghiệp vụ " + currentRole + " > " + selectedItem.title + " (" + selectedItem.subtitle + ")");

        // Load panel into workspace directly
        try {
            JPanel panel = selectedItem.supplier.get();
            workspaceContainer.removeAll();
            workspaceContainer.add(panel, BorderLayout.CENTER);
            workspaceContainer.revalidate();
            workspaceContainer.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải mô-đun: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void setButtonState(JButton btn, boolean active, Color accent) {
        btn.putClientProperty("active", active);
        if (active) {
            btn.setBackground(new Color(30, 41, 59));
        } else {
            btn.setBackground(new Color(15, 23, 42));
        }
        btn.repaint();
    }



    private void openModule(String title, Supplier<JPanel> panelSupplier) {
        try {
            JPanel panel = panelSupplier.get();
            JDialog dialog = new JDialog(MainDashboard.this, title, false);
            dialog.setSize(1180, 750);
            dialog.setLocationRelativeTo(MainDashboard.this);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setContentPane(panel);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainDashboard.this,
                    "Lỗi tải mô-đun: " + ex.getMessage(),
                    "Thông báo", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JButton createLogoutButton() {
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
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Auth.clear();
                this.dispose();
                new LoginForm().setVisible(true);
            }
        });
        return btnLogout;
    }

    public static void main(String[] args) {
        try {
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
