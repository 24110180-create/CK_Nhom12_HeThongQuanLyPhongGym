package com.nhom12.gym.gui;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.gui.panels.*;
import com.nhom12.gym.service.PermissionService;
import com.nhom12.gym.util.Auth;
import com.nhom12.gym.util.HibernateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * BẢNG ĐIỀU HÀNH TRUNG TÂM FITLIFE GYM (FITLIFE COMMAND CENTER)
 * Thiết kế chuẩn Sidebar Dọc (Vertical Menu Layout) bên trái cho tất cả các vai trò:
 * 1. Chủ Phòng: 10 mô-đun cấp cao (Tầm nhìn chiến lược, ra quyết định, không thao tác tay chân)
 * 2. Lễ Tân: 8 mô-đun thao tác trực tiếp tại quầy
 * 3. Huấn Luyện Viên: 4 mô-đun chuyên môn PT & InBody
 * 4. Quản Lý Chi Nhánh: 13 mô-đun (8 quầy + 3 cơ sở + 2 báo cáo khóa theo chi nhánh phụ trách)
 * 5. Hội Viên VIP: 5 mô-đun cổng dịch vụ cá nhân
 * Dropdown "Góc nhìn" trên Header cho phép chuyển đổi tức thời giữa các trạm để kiểm tra & demo.
 */
public class MainDashboard extends JFrame {

    private String currentRole;
    private JPanel rootPanel;

    private JPanel workspaceContainer;
    private JLabel lblWorkspaceTitle;
    private JLabel lblWorkspaceBreadcrumb;
    private final List<SidebarMenuItem> sidebarMenuItems = new ArrayList<>();
    private SidebarMenuItem currentSelectedItem;
    private JLabel lblSidebarBranch;

    public static class SidebarMenuItem {
        public JButton button;
        public String title;
        public String subtitle;
        public Supplier<JPanel> supplier;
        public Color accent;
    }

    private static MainDashboard instance;

    public static MainDashboard getInstance() {
        return instance;
    }

    public void navigateTo(String moduleTitle) {
        navigateTo(moduleTitle, -1);
    }

    public void navigateTo(String moduleTitle, int tabIndex) {
        if (moduleTitle == null) return;
        for (SidebarMenuItem item : sidebarMenuItems) {
            if (item.title != null && item.title.toLowerCase().contains(moduleTitle.toLowerCase())) {
                selectSidebarItem(item);
                if (tabIndex >= 0) {
                    SwingUtilities.invokeLater(() -> {
                        for (Component comp : workspaceContainer.getComponents()) {
                            if (comp instanceof QuanLyHoiVienHubPanel) {
                                ((QuanLyHoiVienHubPanel) comp).selectTab(tabIndex);
                            } else if (comp instanceof JScrollPane) {
                                Component view = ((JScrollPane) comp).getViewport().getView();
                                if (view instanceof QuanLyHoiVienHubPanel) {
                                    ((QuanLyHoiVienHubPanel) view).selectTab(tabIndex);
                                }
                            }
                        }
                    });
                }
                return;
            }
        }
    }

    public MainDashboard() {
        this((Auth.vaiTro != null && !Auth.vaiTro.trim().isEmpty()) ? Auth.vaiTro : "Chủ");
    }

    public MainDashboard(String initialRole) {
        instance = this;
        this.currentRole = (initialRole != null && !initialRole.trim().isEmpty()) ? initialRole : "Chủ";
        setTitle("FitLife Gym Management - " + getRoleDisplayName(currentRole));
        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initLayout();

        // Lắng nghe thay đổi phân quyền thời gian thực
        PermissionService.getInstance().addChangeListener(() -> {
            SwingUtilities.invokeLater(() -> {
                buildVerticalWorkstationLayout(currentRole);
                revalidate();
                repaint();
            });
        });
    }

    private String getRoleDisplayName(String role) {
        if ("Chủ".equalsIgnoreCase(role)) return "Quản Trị Chủ Hệ Thống";
        if ("Lễ tân".equalsIgnoreCase(role)) return "Trạm Vận Hành Lễ Tân";
        if ("HLV".equalsIgnoreCase(role)) return "Không Gian Huấn Luyện Viên";
        if ("Quản lý".equalsIgnoreCase(role)) return "Quản Lý Vận Hành Chi Nhánh";
        if ("Hội viên".equalsIgnoreCase(role)) return "Cổng Dịch Vụ Hội Viên VIP";
        return "Quản Trị Hệ Thống";
    }

    private void initLayout() {
        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(248, 250, 252));
        setContentPane(rootPanel);

        buildVerticalWorkstationLayout(currentRole);

        revalidate();
        repaint();
    }

    private JPanel taoBannerCanhBaoOffline() {
        if (!Auth.isOffline && HibernateUtil.isConnected()) {
            return null;
        }
        JPanel banner = new JPanel(new BorderLayout(15, 0));
        banner.setBackground(new Color(220, 38, 38)); // Đỏ cảnh báo #DC2626
        banner.setBorder(new EmptyBorder(7, 20, 7, 20));
        banner.setPreferredSize(new Dimension(0, 36));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel lblMsg = new JLabel("⚠️  CẢNH BÁO: ĐANG CHẠY CHẾ ĐỘ OFFLINE – DỮ LIỆU ĐANG ĐƯỢC ĐỆM TẠM THỜI (MÁY CHỦ SQL SERVER MẤT KẾT NỐI)");
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMsg.setForeground(Color.WHITE);

        JLabel lblBadge = new JLabel("● OFFLINE BUFFER ACTIVE");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(new Color(254, 226, 226));

        banner.add(lblMsg, BorderLayout.CENTER);
        banner.add(lblBadge, BorderLayout.EAST);
        return banner;
    }

    // =========================================================================
    // GIAO DIỆN CHUẨN MENU DỌC (VERTICAL SIDEBAR + EMBEDDED WORKSPACE)
    // Áp dụng đồng bộ cho tất cả các vai trò (Chủ, Lễ tân, HLV, Quản lý, Hội viên)
    // =========================================================================
    public void buildVerticalWorkstationLayout(String role) {
        this.currentRole = role;
        setTitle("FitLife Gym Management - " + getRoleDisplayName(role));

        rootPanel.removeAll();
        sidebarMenuItems.clear();

        Color roleColor;
        String roleBadgeText;
        String avatarInitials;
        String staffName;
        String statusText;

        String rawName = (Auth.hoTen != null && !Auth.hoTen.trim().isEmpty()) ? Auth.hoTen : ((Auth.tenDangNhap != null) ? Auth.tenDangNhap : "Nhân Sự");
        staffName = rawName;
        avatarInitials = tinhAvatarInitials(rawName);

        String activeUser = (Auth.tenDangNhap != null) ? Auth.tenDangNhap : "nhansu";

        if ("Chủ".equalsIgnoreCase(role)) {
            roleColor = new Color(99, 102, 241); // Indigo
            roleBadgeText = "CHỦ PHÒNG TẬP";
            statusText = "Điều Hành Chuỗi • @" + activeUser;
        } else if ("Lễ tân".equalsIgnoreCase(role)) {
            roleColor = new Color(16, 185, 129); // Emerald
            roleBadgeText = "TRẠM LỄ TÂN";
            statusText = "Đang trực ca • @" + activeUser;
        } else if ("HLV".equalsIgnoreCase(role)) {
            roleColor = new Color(245, 158, 11); // Amber
            roleBadgeText = "HUẤN LUYỆN VIÊN";
            statusText = "HLV Sàn & PT • @" + activeUser;
        } else { // Quản lý
            roleColor = new Color(14, 165, 233); // Sky Blue
            roleBadgeText = "QUẢN LÝ CHI NHÁNH";
            statusText = "Quản lý cơ sở • @" + activeUser;
        }

        // --- A. SIDEBAR DỌC BÊN TRÁI (275px) ---
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(15, 23, 42)); // Slate 900
        sidebar.setPreferredSize(new Dimension(275, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 41, 59)));

        // Sidebar Top: Thương hiệu & Hồ sơ người dùng
        JPanel sidebarTop = new JPanel();
        sidebarTop.setLayout(new BoxLayout(sidebarTop, BoxLayout.Y_AXIS));
        sidebarTop.setOpaque(false);
        sidebarTop.setBorder(new EmptyBorder(20, 18, 12, 18));

        JLabel lblBrand = new JLabel("FITLIFE GYM");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBadge = new JLabel("  " + roleBadgeText + "  ");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setBackground(roleColor);
        lblBadge.setOpaque(true);
        lblBadge.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        lblBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Box hồ sơ nhân sự / người dùng
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

        JPanel profileText = new JPanel(new GridLayout(3, 1, 0, 2));
        profileText.setOpaque(false);

        JLabel lblStaff = new JLabel(staffName);
        lblStaff.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStaff.setForeground(new Color(241, 245, 249));

        JLabel lblShift = new JLabel(statusText);
        lblShift.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblShift.setForeground(new Color(52, 211, 153));

        String staffBranch = Auth.getChiNhanhHienTai();
        lblSidebarBranch = new JLabel("Cơ sở: " + staffBranch);
        lblSidebarBranch.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSidebarBranch.setForeground(new Color(148, 163, 184));

        profileText.add(lblStaff);
        profileText.add(lblShift);
        profileText.add(lblSidebarBranch);
        profileBox.add(lblAvatar, BorderLayout.WEST);
        profileBox.add(profileText, BorderLayout.CENTER);

        sidebarTop.add(lblBrand);
        sidebarTop.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebarTop.add(lblBadge);
        sidebarTop.add(Box.createRigidArea(new Dimension(0, 14)));
        sidebarTop.add(profileBox);
        sidebar.add(sidebarTop, BorderLayout.NORTH);

        // Sidebar Center: Menu cuộn dọc (Vertical Menu List)
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

        // Sidebar Bottom: Nút Đăng xuất
        JPanel sidebarBottom = new JPanel(new GridLayout(0, 1, 0, 8));
        sidebarBottom.setOpaque(false);
        sidebarBottom.setBorder(new EmptyBorder(12, 14, 16, 14));

        JButton btnLogout = createLogoutButton();
        btnLogout.setPreferredSize(new Dimension(0, 36));
        sidebarBottom.add(btnLogout);
        sidebar.add(sidebarBottom, BorderLayout.SOUTH);

        rootPanel.add(sidebar, BorderLayout.WEST);

        // --- B. MAIN WORKSPACE BÊN PHẢI (HEADER + WORKSPACE VIEWPORT) ---
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(new Color(248, 250, 252));

        // Workspace Top Header: Gọn gàng, thoáng đãng, chống đè text
        JPanel workspaceHeader = new JPanel(new BorderLayout(15, 0));
        workspaceHeader.setBackground(Color.WHITE);
        workspaceHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(12, 24, 12, 24)
        ));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1, 0, 2));
        headerLeft.setOpaque(false);
        lblWorkspaceTitle = new JLabel("TIÊU ĐỀ MÔ-ĐUN");
        lblWorkspaceTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWorkspaceTitle.setForeground(new Color(15, 23, 42));

        lblWorkspaceBreadcrumb = new JLabel("Trang nghiệp vụ");
        lblWorkspaceBreadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblWorkspaceBreadcrumb.setForeground(new Color(100, 116, 139));

        headerLeft.add(lblWorkspaceTitle);
        headerLeft.add(lblWorkspaceBreadcrumb);
        workspaceHeader.add(headerLeft, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        // Nút Đăng ký hội viên nhanh
        JButton btnQuickRegister = new JButton("+ Đăng Ký Hội Viên");
        btnQuickRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnQuickRegister.setBackground(new Color(37, 99, 235));
        btnQuickRegister.setForeground(Color.WHITE);
        btnQuickRegister.setFocusPainted(false);
        btnQuickRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickRegister.setPreferredSize(new Dimension(165, 32));
        btnQuickRegister.addActionListener(e -> {
            navigateTo("Quản Lý Hội Viên", 1);
        });

        // Nút Quét mã check-in
        JButton btnQuickCheckIn = new JButton("Quét QR Check-in");
        btnQuickCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnQuickCheckIn.setBackground(new Color(16, 185, 129));
        btnQuickCheckIn.setForeground(Color.WHITE);
        btnQuickCheckIn.setFocusPainted(false);
        btnQuickCheckIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickCheckIn.setPreferredSize(new Dimension(140, 32));
        btnQuickCheckIn.addActionListener(e -> {
            Window top = SwingUtilities.getWindowAncestor(this);
            new QRScannerSimulatorDialog(top).setVisible(true);
        });

        // Bỏ nút đăng ký hội viên ở header đối với role Quản lý, Lễ tân và HLV
        boolean hideQuickRegister = "HLV".equalsIgnoreCase(role)
                || "Lễ tân".equalsIgnoreCase(role)
                || "Quản lý".equalsIgnoreCase(role);
        if (!hideQuickRegister) {
            headerRight.add(btnQuickRegister);
        }

        // Dropdown chuyển góc nhìn (CHỈ DÀNH RIÊNG CHO CHỦ PHÒNG TẬP)
        boolean isRealOwner = "Chủ".equalsIgnoreCase(Auth.vaiTro);
        if (isRealOwner) {
            JLabel lblGoc = new JLabel("Góc nhìn:");
            lblGoc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblGoc.setForeground(new Color(100, 116, 139));

            JComboBox<String> cbStaffView = new JComboBox<>(new String[]{
                    "Chủ Phòng Tập",
                    "Trạm Lễ Tân",
                    "Trạm HLV",
                    "Trạm Quản Lý"
            });
            cbStaffView.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            cbStaffView.setPreferredSize(new Dimension(140, 30));

            if ("Lễ tân".equalsIgnoreCase(role)) cbStaffView.setSelectedIndex(1);
            else if ("HLV".equalsIgnoreCase(role)) cbStaffView.setSelectedIndex(2);
            else if ("Quản lý".equalsIgnoreCase(role)) cbStaffView.setSelectedIndex(3);
            else cbStaffView.setSelectedIndex(0);

            cbStaffView.addActionListener(e -> {
                int s = cbStaffView.getSelectedIndex();
                if (s == 1) buildVerticalWorkstationLayout("Lễ tân");
                else if (s == 2) buildVerticalWorkstationLayout("HLV");
                else if (s == 3) buildVerticalWorkstationLayout("Quản lý");
                else buildVerticalWorkstationLayout("Chủ");
                revalidate();
                repaint();
            });

            // Bộ lọc chi nhánh dành cho Chủ phòng
            JLabel lblBranchHead = new JLabel("Cơ sở:");
            lblBranchHead.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblBranchHead.setForeground(new Color(100, 116, 139));

            JComboBox<String> cbBranchSelector = new JComboBox<>(new String[]{
                    "Toàn hệ thống",
                    "CN01 - Cơ sở 1 (Quận 1)",
                    "CN02 - Cơ sở 2 (Tân Bình)"
            });
            cbBranchSelector.setFont(new Font("Segoe UI", Font.BOLD, 11));
            cbBranchSelector.setPreferredSize(new Dimension(175, 30));
            if (Auth.chiNhanhDangChon != null) {
                if (Auth.chiNhanhDangChon.contains("CN01")) cbBranchSelector.setSelectedIndex(1);
                else if (Auth.chiNhanhDangChon.contains("CN02")) cbBranchSelector.setSelectedIndex(2);
                else cbBranchSelector.setSelectedIndex(0);
            }
            cbBranchSelector.addActionListener(e -> {
                String selected = (String) cbBranchSelector.getSelectedItem();
                Auth.chiNhanhDangChon = selected;
                if (lblSidebarBranch != null) {
                    lblSidebarBranch.setText("Cơ sở: " + Auth.getChiNhanhHienTai());
                }
                if (currentSelectedItem != null) {
                    selectSidebarItem(currentSelectedItem);
                }
            });

            headerRight.add(btnQuickCheckIn);
            headerRight.add(lblGoc);
            headerRight.add(cbStaffView);
            headerRight.add(lblBranchHead);
            headerRight.add(cbBranchSelector);
        } else {
            // Đối với nhân sự (Lễ tân, HLV, Quản lý): Cố định chi nhánh công tác, không có dropdown đổi góc nhìn
            JLabel lblStaffBranchBadge = new JLabel(" Cơ sở: " + Auth.getChiNhanhHienTai() + " ");
            lblStaffBranchBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblStaffBranchBadge.setForeground(new Color(30, 41, 59));
            lblStaffBranchBadge.setBackground(new Color(241, 245, 249));
            lblStaffBranchBadge.setOpaque(true);
            lblStaffBranchBadge.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    new EmptyBorder(4, 8, 4, 8)
            ));

            headerRight.add(btnQuickCheckIn);
            headerRight.add(lblStaffBranchBadge);
        }

        String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        JLabel lblDate = new JLabel("Ngày: " + today);
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDate.setForeground(new Color(100, 116, 139));
        headerRight.add(lblDate);
        workspaceHeader.add(headerRight, BorderLayout.EAST);

        mainArea.add(workspaceHeader, BorderLayout.NORTH);

        // Workspace Dynamic Center Container
        workspaceContainer = new JPanel(new BorderLayout());
        workspaceContainer.setOpaque(false);
        mainArea.add(workspaceContainer, BorderLayout.CENTER);

        rootPanel.add(mainArea, BorderLayout.CENTER);

        JPanel offlineBanner = taoBannerCanhBaoOffline();
        if (offlineBanner != null) {
            rootPanel.add(offlineBanner, BorderLayout.NORTH);
        }

        // --- C. NẠP DANH SÁCH MENU DỌC CHO TỪNG ROLE ---
        if ("Chủ".equalsIgnoreCase(role)) {
            setupOwnerMenu(menuContainer, roleColor);
        } else if ("Lễ tân".equalsIgnoreCase(role)) {
            setupReceptionistMenu(menuContainer, roleColor);
        } else if ("HLV".equalsIgnoreCase(role)) {
            setupTrainerMenu(menuContainer, roleColor);
        } else {
            setupManagerMenu(menuContainer, roleColor);
        }
    }

    // =========================================================================
    // 1. MENU CHỦ PHÒNG (10 MÔ-ĐUN CẤP CAO - TẦM NHÌN CHIẾN LƯỢC)
    // =========================================================================
    // =========================================================================
    // 1. MENU CHỦ PHÒNG (10 MÔ-ĐUN CẤP CAO - TẦM NHÌN CHIẾN LƯỢC)
    // =========================================================================
    private void setupOwnerMenu(JPanel menuContainer, Color accent) {
        addSidebarCategoryHeader(menuContainer, "Tổng Quan & Báo Cáo");
        addSidebarItem(menuContainer, "Trang Chủ (KPI Tổng Hợp)", "Cửa sổ điều hành chiến lược của Anh Đức", () -> new CuaSoChuHeThongPanel(), accent, true);
        addSidebarItem(menuContainer, "Báo Cáo Doanh Thu", "Ra quyết định tài chính & doanh thu chuỗi", () -> new BaoCaoThongKePanel(), accent, false);
        addSidebarItem(menuContainer, "Phân Tích Giờ Cao Điểm", "Quyết định xếp ca & mở rộng phòng 6h-22h", () -> new HeatmapGioCaoDiemPanel(), accent, false);
        addSidebarItem(menuContainer, "Lịch Sử Giao Dịch", "Kiểm toán, đối soát dòng tiền & hóa đơn", () -> new LichSuGiaoDichPanel(), accent, false);

        addSidebarCategoryHeader(menuContainer, "Khách Hàng & Dịch Vụ");
        addSidebarItem(menuContainer, "Quản Lý Hội Viên", "Hồ sơ, thẻ QR, tiếp nhận mới & tái ký gói tập", () -> new QuanLyHoiVienHubPanel(), accent, false);
        addSidebarItem(menuContainer, "Lịch Lớp Học & Điểm Danh", "Thời khóa biểu lớp nhóm & điểm danh học viên", () -> new LichLopHocPanel(), accent, false);

        addSidebarCategoryHeader(menuContainer, "Chiến Lược Kinh Doanh");
        addSidebarItem(menuContainer, "Quản Lý Gói Tập & Giá Tiền", "Quyết định chính sách giá & bảng giá dịch vụ", () -> new QuanLyGoiTapPanel(), accent, false);
        addSidebarItem(menuContainer, "Quản Lý Khuyến Mãi", "Quyết định marketing, gói VIP ẩn 50% & chống bán chui", () -> new QuanLyKhuyenMaiPanel(), accent, false);

        addSidebarCategoryHeader(menuContainer, "Con Người & Hệ Thống");
        addSidebarItem(menuContainer, "Quản Lý Nhân Sự & Bảng Lương", "Danh sách HLV, nhân viên & bảng lương hệ thống", () -> new QuanLyNhanSuPanel(), accent, false);
        addSidebarItem(menuContainer, "Lịch Làm Việc & Phân Ca HLV", "Quản lý ca trực, phân công lớp và lịch làm việc HLV", () -> new PhanCaLamViecPanel(), accent, false);
        addSidebarItem(menuContainer, "Tài Khoản Nhân Sự & Phân Quyền", "Quản trị tài khoản 4 vai trò nhân sự nội bộ", () -> new QuanLyTaiKhoanVaNhanSuPanel(), accent, false);
        addSidebarItem(menuContainer, "Cài Đặt Chung", "Cấu hình chuỗi phòng tập & hệ thống", () -> new CaiDatHeThongPanel(), accent, false);
    }

    // =========================================================================
    // 2. MENU LỄ TÂN (THAO TÁC TRỰC TIẾP TẠI QUẦY)
    // =========================================================================
    private void setupReceptionistMenu(JPanel menuContainer, Color accent) {
        addSidebarCategoryHeader(menuContainer, "Nghiệp Vụ Hội Viên");
        addSidebarItem(menuContainer, "Quét Thẻ & Check-in", "Check-in hội viên & cửa flap barrier", () -> new CheckInForm(), accent, true);
        addSidebarItem(menuContainer, "Quản Lý Hội Viên", "Hồ sơ thẻ QR, tiếp nhận mới & gia hạn gói tập", () -> new QuanLyHoiVienHubPanel(), accent, false);

        addSidebarCategoryHeader(menuContainer, "Dịch Vụ & Lớp Học");
        addSidebarItem(menuContainer, "Lịch Lớp Học & Điểm Danh", "Thời khóa biểu lớp nhóm & điểm danh học viên", () -> new LichLopHocPanel(), accent, false);
        addSidebarItem(menuContainer, "Quầy Bán Hàng POS", "Bán nước giải khát, whey & đồ tập", () -> new BanHangPOSPanel(), accent, false);
    }

    // =========================================================================
    // 3. MENU HUẤN LUYỆN VIÊN (CHUYÊN MÔN HLV & PT)
    // =========================================================================
    private void setupTrainerMenu(JPanel menuContainer, Color accent) {
        addSidebarItem(menuContainer, "Theo Dõi InBody & Thể Lực", "Phân tích cơ, mỡ, BMI & thể trạng", () -> new TheoDoiChiSoPanel(), accent, true);
        addSidebarItem(menuContainer, "Lịch Lớp Học & Điểm Danh", "Thời khóa biểu lớp nhóm & điểm danh học viên", () -> new LichLopHocPanel(), accent, false);
        addSidebarItem(menuContainer, "Sổ Tay Tiến Độ PT", "Giáo án tập luyện 1:1 & kỷ lục tạ cá nhân", () -> new GhiChuTienDoPTPanel(), accent, false);
        addSidebarItem(menuContainer, "Hồ Sơ Học Viên Phụ Trách", "Chi tiết lịch sử tập & mục tiêu học viên", () -> new ChiTietHoiVienPanel(), accent, false);
    }

    // =========================================================================
    // 4. MENU QUẢN LÝ (ĐIỀU HÀNH CHI NHÁNH & SỐ LIỆU CƠ SỞ)
    // =========================================================================
    private void setupManagerMenu(JPanel menuContainer, Color accent) {
        addSidebarCategoryHeader(menuContainer, "Vận Hành Quầy & Dịch Vụ");
        addSidebarItem(menuContainer, "Quét Thẻ & Check-in", "Check-in hội viên tại quầy", () -> new CheckInForm(), accent, true);
        addSidebarItem(menuContainer, "Quản Lý Hội Viên", "Hồ sơ hội viên, tiếp nhận đăng ký & tái ký", () -> new QuanLyHoiVienHubPanel(), accent, false);
        addSidebarItem(menuContainer, "Lịch Lớp Học & Điểm Danh", "Điều phối lớp nhóm & điểm danh học viên", () -> new LichLopHocPanel(), accent, false);
        addSidebarItem(menuContainer, "Quầy Bán Hàng POS", "Thu ngân quầy bar & sản phẩm", () -> new BanHangPOSPanel(), accent, false);

        addSidebarCategoryHeader(menuContainer, "Điều Hành Cơ Sở");
        addSidebarItem(menuContainer, "Quản Lý Kho Hàng", "Kiểm kê xuất nhập tồn kho cơ sở", () -> new QuanLyKhoHangPanel(), accent, false);
        addSidebarItem(menuContainer, "Phân Ca Làm Việc", "Xếp lịch làm việc nhân viên theo tuần", () -> new PhanCaLamViecPanel(), accent, false);

        addSidebarCategoryHeader(menuContainer, "Báo Cáo Chi Nhánh");
        addSidebarItem(menuContainer, "Phân Tích Giờ Cao Điểm", "Mật độ khách 6h-22h (Chi nhánh)", () -> new HeatmapGioCaoDiemPanel(), accent, false);
        addSidebarItem(menuContainer, "Báo Cáo Doanh Thu Chi Nhánh", "Doanh thu & tài chính chi nhánh", () -> new BaoCaoThongKePanel(), accent, false);
    }



    private void addSidebarCategoryHeader(JPanel container, String categoryTitle) {
        JLabel lblCat = new JLabel(categoryTitle.toUpperCase());
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblCat.setForeground(new Color(100, 116, 139));
        lblCat.setBorder(new EmptyBorder(12, 14, 4, 14));
        lblCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblCat);
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

        btn.addActionListener(e -> selectSidebarItem(item));

        // Click trên textPanel và các label con cũng kích hoạt
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
        this.currentSelectedItem = selectedItem;
        for (SidebarMenuItem item : sidebarMenuItems) {
            boolean isSel = (item == selectedItem);
            setButtonState(item.button, isSel, selectedItem.accent);
        }

        lblWorkspaceTitle.setText(selectedItem.title.toUpperCase());
        lblWorkspaceBreadcrumb.setText("Hệ thống > " + currentRole + " > " + selectedItem.title + " (" + selectedItem.subtitle + ")");

        try {
            JPanel panel = selectedItem.supplier.get();
            JScrollPane workspaceScroll = new JScrollPane(panel);
            workspaceScroll.setBorder(null);
            workspaceScroll.setOpaque(false);
            workspaceScroll.getViewport().setOpaque(false);
            workspaceScroll.getVerticalScrollBar().setUnitIncrement(16);
            workspaceScroll.getHorizontalScrollBar().setUnitIncrement(16);

            workspaceContainer.removeAll();
            workspaceContainer.add(workspaceScroll, BorderLayout.CENTER);
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

    private String tinhAvatarInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "AD";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        String first = parts[0].substring(0, 1).toUpperCase();
        String last = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return first + last;
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
