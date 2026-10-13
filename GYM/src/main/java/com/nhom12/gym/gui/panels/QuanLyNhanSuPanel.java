package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.ExcelExportHelper;
import com.nhom12.gym.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * TRUNG TÂM QUẢN TRỊ NHÂN SỰ & ĐIỀU HÀNH ĐỘI NGŨ CHUỖI DÀNH CHO CHỦ PHÒNG (ANH ĐỨC)
 * Thiết kế toàn diện phục vụ quản lý 4 nhân sự nòng cốt và đội ngũ HLV chuỗi 2 cơ sở:
 * - Lễ tân (Chị Linh), HLV (Anh Tuấn), Quản lý chi nhánh (Anh Sơn), Ban quản trị
 * - Thao tác nghiệp vụ: Thêm mới, Chỉnh sửa, Sa thải (Xóa), Khóa/Mở khóa, Reset mật khẩu (123456)
 * - Tự động đồng bộ với CSDL qua TaiKhoanDAO (chuẩn bảo mật mã hóa BCrypt)
 * - Tính năng sáng tạo: Thẻ KPI nhân sự, Lọc đa chiều, Xuất Excel bảng lương, Bảng trực ca, Tính thưởng KPI tự động
 */
public class QuanLyNhanSuPanel extends JPanel {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final DecimalFormat df = new DecimalFormat("#,### VNĐ");

    // Form inputs
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtFullName;
    private JTextField txtPhone;
    private JComboBox<String> cbRole;
    private JComboBox<String> cbBranch;
    private JComboBox<String> cbShift;
    private JTextField txtBaseSalary;
    private JTextField txtKpiBonus;
    private JComboBox<String> cbRating;
    private JComboBox<String> cbStatus;

    // Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnToggleLock;
    private JButton btnResetPassword;
    private JButton btnClear;

    // Filters & Tools
    private JTextField txtSearch;
    private JComboBox<String> cbFilterRole;
    private JComboBox<String> cbFilterBranch;
    private JComboBox<String> cbFilterStatus;
    private JButton btnExportExcel;
    private JButton btnShiftBoard;
    private JButton btnCalculateKPI;
    private JButton btnTransferBranch;
    private JButton btnViewProfile;

    // Table & Model
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    // KPI Summary Labels
    private JLabel lblKpiTotalStaff;
    private JLabel lblKpiTotalStaffSub;
    private JLabel lblKpiRoleComposition;
    private JLabel lblKpiRoleCompositionSub;
    private JLabel lblKpiPayroll;
    private JLabel lblKpiPayrollSub;
    private JLabel lblKpiStatusRate;
    private JLabel lblKpiStatusRateSub;

    // In-memory staff detail cache
    public static class StaffDetail {
        public String username;
        public String fullName;
        public String role;
        public String branch;
        public String shift;
        public String phone;
        public double baseSalary;
        public double kpiBonus;
        public String rating;
        public String status;

        public StaffDetail(String username, String fullName, String role, String branch, String shift,
                           String phone, double baseSalary, double kpiBonus, String rating, String status) {
            this.username = username;
            this.fullName = fullName;
            this.role = role;
            this.branch = branch;
            this.shift = shift;
            this.phone = phone;
            this.baseSalary = baseSalary;
            this.kpiBonus = kpiBonus;
            this.rating = rating;
            this.status = status;
        }

        public double getTotalIncome() {
            return baseSalary + kpiBonus;
        }
    }

    private final List<StaffDetail> staffList = new ArrayList<>();

    public QuanLyNhanSuPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(18, 22, 20, 22));

        initUI();
        initDefaultStaffData();
        loadTableData();
        updateKpiMetrics();
    }

    private void initUI() {
        // --- 1. TOP HEADER BANNER ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);
        titleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("TRUNG TÂM QUẢN TRỊ NHÂN SỰ & ĐIỀU HÀNH ĐỘI NGŨ (ANH ĐỨC)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Quản lý 4 nhân sự nòng cốt & đội ngũ HLV chuỗi 2 cơ sở • Cấp phát tài khoản, phân ca trực, duyệt lương & thưởng KPI");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        pnlHeader.add(titleBox);
        pnlHeader.add(Box.createRigidArea(new Dimension(0, 14)));

        // --- 2. HÀNG 4 THẺ KPI TỔNG HỢP ---
        pnlHeader.add(createKpiCardsPanel());
        pnlHeader.add(Box.createRigidArea(new Dimension(0, 14)));

        // --- 3. THANH TÌM KIẾM & BỘ LỌC ĐA CHIỀU (FILTER TOOLBAR) ---
        pnlHeader.add(createFilterToolbar());
        add(pnlHeader, BorderLayout.NORTH);

        // --- 4. TRUNG TÂM: SPLIT PANE (FORM BÊN TRÁI, TABLE BÊN PHẢI) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(390);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(createLeftFormPanel());
        splitPane.setRightComponent(createRightTablePanel());

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createKpiCardsPanel() {
        JPanel pnl = new JPanel(new GridLayout(1, 4, 14, 0));
        pnl.setOpaque(false);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Card 1: Tổng nhân sự
        lblKpiTotalStaff = new JLabel("0 Nhân sự");
        lblKpiTotalStaffSub = new JLabel("CN01: 0 • CN02: 0 • Trụ sở: 0");
        pnl.add(createSingleKpiCard("👥 TỔNG NHÂN LỰC CHUỖI", lblKpiTotalStaff, lblKpiTotalStaffSub, new Color(99, 102, 241)));

        // Card 2: Lực lượng
        lblKpiRoleComposition = new JLabel("0 LT • 0 HLV");
        lblKpiRoleCompositionSub = new JLabel("0 Quản Lý • 1 Chủ");
        pnl.add(createSingleKpiCard("🏢 CƠ CẤU VẬN HÀNH", lblKpiRoleComposition, lblKpiRoleCompositionSub, new Color(16, 185, 129)));

        // Card 3: Quỹ lương
        lblKpiPayroll = new JLabel("0 VNĐ");
        lblKpiPayrollSub = new JLabel("Lương cơ bản + Thưởng KPI");
        pnl.add(createSingleKpiCard("💰 DỰ TOÁN QUỸ LƯƠNG", lblKpiPayroll, lblKpiPayrollSub, new Color(245, 158, 11)));

        // Card 4: Trạng thái ca trực
        lblKpiStatusRate = new JLabel("100% Hoạt động");
        lblKpiStatusRateSub = new JLabel("Sẵn sàng phục vụ khách");
        pnl.add(createSingleKpiCard("⚡ TRẠNG THÁI CA TRỰC", lblKpiStatusRate, lblKpiStatusRateSub, new Color(14, 165, 233)));

        return pnl;
    }

    private JPanel createSingleKpiCard(String title, JLabel lblValue, JLabel lblSub, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblT.setForeground(accent);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(new Color(15, 23, 42));

        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(100, 116, 139));

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 2));
        content.setOpaque(false);
        content.add(lblValue);
        content.add(lblSub);

        card.add(lblT, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFilterToolbar() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(8, 12, 8, 12)
        ));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hàng 1: Tìm kiếm & Các bộ lọc đa chiều
        JPanel pRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        pRow1.setOpaque(false);
        pRow1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(new Color(71, 85, 105));

        txtSearch = new JTextField(14);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(170, 32));

        JLabel lblFRole = new JLabel("Vai trò:");
        lblFRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFRole.setForeground(new Color(71, 85, 105));

        cbFilterRole = new JComboBox<>(new String[]{"Tất cả vai trò", "Lễ tân", "HLV", "Quản lý", "Chủ"});
        cbFilterRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFilterRole.setPreferredSize(new Dimension(125, 32));

        JLabel lblFBranch = new JLabel("Cơ sở:");
        lblFBranch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFBranch.setForeground(new Color(71, 85, 105));

        cbFilterBranch = new JComboBox<>(new String[]{"Tất cả cơ sở", "CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)", "Toàn hệ thống"});
        cbFilterBranch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFilterBranch.setPreferredSize(new Dimension(145, 32));

        JLabel lblFStatus = new JLabel("Trạng thái:");
        lblFStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFStatus.setForeground(new Color(71, 85, 105));

        cbFilterStatus = new JComboBox<>(new String[]{"Tất cả", "Đang làm việc", "Tạm khóa"});
        cbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFilterStatus.setPreferredSize(new Dimension(115, 32));

        pRow1.add(lblSearch);
        pRow1.add(txtSearch);
        pRow1.add(lblFRole);
        pRow1.add(cbFilterRole);
        pRow1.add(lblFBranch);
        pRow1.add(cbFilterBranch);
        pRow1.add(lblFStatus);
        pRow1.add(cbFilterStatus);

        // Hàng 2: Bộ công cụ điều hành chuyên sâu cho Chủ phòng (Anh Đức)
        JPanel pRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pRow2.setOpaque(false);
        pRow2.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnShiftBoard = new JButton("Lịch Trực & Độ Phủ Ca");
        btnShiftBoard.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnShiftBoard.setBackground(new Color(14, 165, 233));
        btnShiftBoard.setForeground(Color.WHITE);
        btnShiftBoard.setFocusPainted(false);
        btnShiftBoard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShiftBoard.setPreferredSize(new Dimension(165, 32));

        btnTransferBranch = new JButton("Điều Động Chi Nhánh");
        btnTransferBranch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTransferBranch.setBackground(new Color(99, 102, 241));
        btnTransferBranch.setForeground(Color.WHITE);
        btnTransferBranch.setFocusPainted(false);
        btnTransferBranch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTransferBranch.setPreferredSize(new Dimension(165, 32));

        btnCalculateKPI = new JButton("Tính Thưởng KPI");
        btnCalculateKPI.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalculateKPI.setBackground(new Color(245, 158, 11));
        btnCalculateKPI.setForeground(Color.WHITE);
        btnCalculateKPI.setFocusPainted(false);
        btnCalculateKPI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalculateKPI.setPreferredSize(new Dimension(140, 32));

        btnViewProfile = new JButton("Hồ Sơ Chi Tiết");
        btnViewProfile.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnViewProfile.setBackground(new Color(79, 70, 229));
        btnViewProfile.setForeground(Color.WHITE);
        btnViewProfile.setFocusPainted(false);
        btnViewProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewProfile.setPreferredSize(new Dimension(125, 32));

        btnExportExcel = new JButton("Xuất Excel");
        btnExportExcel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportExcel.setBackground(new Color(16, 185, 129));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setFocusPainted(false);
        btnExportExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportExcel.setPreferredSize(new Dimension(105, 32));

        pRow2.add(btnShiftBoard);
        pRow2.add(btnTransferBranch);
        pRow2.add(btnCalculateKPI);
        pRow2.add(btnViewProfile);
        pRow2.add(btnExportExcel);

        bar.add(pRow1);
        bar.add(Box.createRigidArea(new Dimension(0, 4)));
        bar.add(pRow2);

        // Listeners lọc dữ liệu tức thì
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        };
        txtSearch.getDocument().addDocumentListener(dl);
        cbFilterRole.addActionListener(e -> applyFilters());
        cbFilterBranch.addActionListener(e -> applyFilters());
        cbFilterStatus.addActionListener(e -> applyFilters());

        btnExportExcel.addActionListener(e -> xuLyXuatExcel());
        btnShiftBoard.addActionListener(e -> moDialogLichTrucHomNay());
        btnTransferBranch.addActionListener(e -> moDialogDieuDongChiNhanh());
        btnCalculateKPI.addActionListener(e -> moDialogTinhThuongKPI());
        btnViewProfile.addActionListener(e -> moDialogHoSoChiTiet());

        return bar;
    }

    private JPanel createLeftFormPanel() {
        JPanel left = new JPanel(new BorderLayout(0, 10));
        left.setBackground(Color.WHITE);
        left.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblFormTitle = new JLabel("HỒ SƠ & CẤP TÀI KHOẢN NHÂN SỰ");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFormTitle.setForeground(new Color(15, 23, 42));
        left.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 2, 4, 2);
        gbc.weightx = 1.0;

        Font fLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fInput = new Font("Segoe UI", Font.PLAIN, 13);

        int r = 0;

        // Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = r++;
        JLabel lUser = new JLabel("Tên Đăng Nhập / Mã Số (*):");
        lUser.setFont(fLabel);
        formGrid.add(lUser, gbc);

        gbc.gridy = r++;
        txtUsername = new JTextField();
        txtUsername.setFont(fInput);
        formGrid.add(txtUsername, gbc);

        // Mật khẩu
        gbc.gridy = r++;
        JLabel lPass = new JLabel("Mật Khẩu Khởi Tạo:");
        lPass.setFont(fLabel);
        formGrid.add(lPass, gbc);

        gbc.gridy = r++;
        txtPassword = new JTextField("123456");
        txtPassword.setFont(fInput);
        formGrid.add(txtPassword, gbc);

        // Họ tên
        gbc.gridy = r++;
        JLabel lName = new JLabel("Họ Và Tên Nhân Sự (*):");
        lName.setFont(fLabel);
        formGrid.add(lName, gbc);

        gbc.gridy = r++;
        txtFullName = new JTextField();
        txtFullName.setFont(fInput);
        formGrid.add(txtFullName, gbc);

        // Số điện thoại
        gbc.gridy = r++;
        JLabel lPhone = new JLabel("Số Điện Thoại Liên Hệ (*):");
        lPhone.setFont(fLabel);
        formGrid.add(lPhone, gbc);

        gbc.gridy = r++;
        txtPhone = new JTextField();
        txtPhone.setFont(fInput);
        formGrid.add(txtPhone, gbc);

        // Vai trò
        gbc.gridy = r++;
        JLabel lRole = new JLabel("Vai Trò Phân Hệ:");
        lRole.setFont(fLabel);
        formGrid.add(lRole, gbc);

        gbc.gridy = r++;
        cbRole = new JComboBox<>(new String[]{"Lễ tân", "HLV", "Quản lý", "Chủ"});
        cbRole.setFont(fInput);
        formGrid.add(cbRole, gbc);

        // Cơ sở làm việc
        gbc.gridy = r++;
        JLabel lBranch = new JLabel("Cơ Sở Làm Việc:");
        lBranch.setFont(fLabel);
        formGrid.add(lBranch, gbc);

        gbc.gridy = r++;
        cbBranch = new JComboBox<>(new String[]{"CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)", "Toàn hệ thống"});
        cbBranch.setFont(fInput);
        formGrid.add(cbBranch, gbc);

        // Ca trực
        gbc.gridy = r++;
        JLabel lShift = new JLabel("Ca Làm Việc:");
        lShift.setFont(fLabel);
        formGrid.add(lShift, gbc);

        gbc.gridy = r++;
        cbShift = new JComboBox<>(new String[]{
                "Ca sáng (06:00 - 14:00)",
                "Ca chiều (14:00 - 22:00)",
                "Ca hành chính (08:00 - 17:00)",
                "Ca xoay linh hoạt"
        });
        cbShift.setFont(fInput);
        formGrid.add(cbShift, gbc);

        // Lương cơ bản
        gbc.gridy = r++;
        JLabel lSalary = new JLabel("Lương Cơ Bản (VNĐ):");
        lSalary.setFont(fLabel);
        formGrid.add(lSalary, gbc);

        gbc.gridy = r++;
        txtBaseSalary = new JTextField("7,500,000");
        txtBaseSalary.setFont(fInput);
        formGrid.add(txtBaseSalary, gbc);

        // Thưởng KPI
        gbc.gridy = r++;
        JLabel lKpi = new JLabel("Thưởng KPI / Hoa Hồng PT (VNĐ):");
        lKpi.setFont(fLabel);
        formGrid.add(lKpi, gbc);

        gbc.gridy = r++;
        txtKpiBonus = new JTextField("1,500,000");
        txtKpiBonus.setFont(fInput);
        formGrid.add(txtKpiBonus, gbc);

        // Đánh giá sao & Trạng thái
        gbc.gridy = r++;
        JLabel lStatus = new JLabel("Đánh Giá & Trạng Thái:");
        lStatus.setFont(fLabel);
        formGrid.add(lStatus, gbc);

        gbc.gridy = r++;
        JPanel subRow = new JPanel(new GridLayout(1, 2, 8, 0));
        subRow.setOpaque(false);
        cbRating = new JComboBox<>(new String[]{"5.0 ⭐ (Xuất sắc)", "4.8 ⭐ (Tốt)", "4.5 ⭐ (Khá)", "4.0 ⭐ (Đạt)"});
        cbRating.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbStatus = new JComboBox<>(new String[]{"Đang làm việc", "Tạm khóa"});
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subRow.add(cbRating);
        subRow.add(cbStatus);
        formGrid.add(subRow, gbc);

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        left.add(scrollForm, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnAdd = new JButton("+ Thêm Nhân Sự");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setBackground(new Color(16, 185, 129));
        btnAdd.setForeground(Color.WHITE);

        btnUpdate = new JButton("✏️ Cập Nhật");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdate.setBackground(new Color(37, 99, 235));
        btnUpdate.setForeground(Color.WHITE);

        btnDelete = new JButton("🗑️ Xóa / Sa Thải");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);

        btnToggleLock = new JButton("🔒 Khóa / Mở Khóa");
        btnToggleLock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnToggleLock.setBackground(new Color(245, 158, 11));
        btnToggleLock.setForeground(Color.WHITE);

        btnResetPassword = new JButton("🔑 Đặt Lại MK (123456)");
        btnResetPassword.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnResetPassword.setBackground(new Color(139, 92, 246));
        btnResetPassword.setForeground(Color.WHITE);

        btnClear = new JButton("🔄 Làm Mới Form");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClear.setBackground(new Color(100, 116, 139));
        btnClear.setForeground(Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnToggleLock);
        btnPanel.add(btnResetPassword);
        btnPanel.add(btnClear);

        left.add(btnPanel, BorderLayout.SOUTH);

        // Listeners
        btnAdd.addActionListener(e -> xuLyThemNhanSu());
        btnUpdate.addActionListener(e -> xuLyCapNhatNhanSu());
        btnDelete.addActionListener(e -> xuLyXoaNhanSu());
        btnToggleLock.addActionListener(e -> xuLyKhoaMoKhoa());
        btnResetPassword.addActionListener(e -> xuLyResetMatKhau());
        btnClear.addActionListener(e -> xuLyLamMoiForm());

        return left;
    }

    private JPanel createRightTablePanel() {
        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        String[] cols = new String[]{
                "Tên Đăng Nhập", "Họ Và Tên", "Vai Trò", "Cơ Sở",
                "Ca Trực", "Số ĐT", "Lương Cơ Bản", "Thưởng KPI", "Tổng Thu Nhập", "Đánh Giá", "Trạng Thái"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Custom Cell Renderer tô màu badge theo Vai trò & Trạng thái
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(new Color(15, 23, 42));

                    // Cột Vai trò (Col 2)
                    if (col == 2 && value != null) {
                        String r = value.toString();
                        if ("Chủ".equalsIgnoreCase(r)) c.setForeground(new Color(99, 102, 241)); // Indigo
                        else if ("Lễ tân".equalsIgnoreCase(r)) c.setForeground(new Color(16, 185, 129)); // Emerald
                        else if ("HLV".equalsIgnoreCase(r)) c.setForeground(new Color(217, 119, 6)); // Amber
                        else if ("Quản lý".equalsIgnoreCase(r)) c.setForeground(new Color(14, 165, 233)); // Sky
                    }

                    // Cột Trạng thái (Col 10)
                    if (col == 10 && value != null) {
                        String st = value.toString();
                        if ("Tạm khóa".equalsIgnoreCase(st)) {
                            c.setForeground(new Color(220, 38, 38)); // Đỏ
                        } else {
                            c.setForeground(new Color(16, 185, 129)); // Xanh
                        }
                    }
                }
                return c;
            }
        });

        // Click hàng -> fill lên Form bên trái
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                txtUsername.setText(model.getValueAt(modelRow, 0).toString());
                txtUsername.setEditable(false);
                txtFullName.setText(model.getValueAt(modelRow, 1).toString());
                cbRole.setSelectedItem(model.getValueAt(modelRow, 2).toString());
                cbBranch.setSelectedItem(model.getValueAt(modelRow, 3).toString());
                cbShift.setSelectedItem(model.getValueAt(modelRow, 4).toString());
                txtPhone.setText(model.getValueAt(modelRow, 5).toString());
                txtBaseSalary.setText(model.getValueAt(modelRow, 6).toString());
                txtKpiBonus.setText(model.getValueAt(modelRow, 7).toString());
                cbRating.setSelectedItem(model.getValueAt(modelRow, 9).toString());
                cbStatus.setSelectedItem(model.getValueAt(modelRow, 10).toString());
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    moDialogHoSoChiTiet();
                }
            }
        });

        right.add(new JScrollPane(table), BorderLayout.CENTER);
        return right;
    }

    private void initDefaultStaffData() {
        staffList.clear();

        // Nạp dữ liệu nền tảng chuẩn mực cho chuỗi 2 cơ sở
        staffList.add(new StaffDetail("admin", "Võ Anh Đức", "Chủ", "Toàn hệ thống", "Ca hành chính (08:00 - 17:00)", "0909000001", 30000000, 10000000, "5.0 ⭐ (Xuất sắc)", "Đang làm việc"));
        staffList.add(new StaffDetail("letan1", "Nguyễn Thị Linh", "Lễ tân", "CN01 - Cơ sở 1 (Quận 1)", "Ca sáng (06:00 - 14:00)", "0912345601", 7500000, 1500000, "5.0 ⭐ (Xuất sắc)", "Đang làm việc"));
        staffList.add(new StaffDetail("letan2", "Trần Thị Mai", "Lễ tân", "CN02 - Cơ sở 2 (Tân Bình)", "Ca chiều (14:00 - 22:00)", "0912345602", 7500000, 1200000, "4.8 ⭐ (Tốt)", "Đang làm việc"));
        staffList.add(new StaffDetail("hlv1", "Trần Tuấn Kiệt", "HLV", "CN01 - Cơ sở 1 (Quận 1)", "Ca chiều (14:00 - 22:00)", "0912345603", 15000000, 4500000, "4.9 ⭐ (Xuất sắc)", "Đang làm việc"));
        staffList.add(new StaffDetail("hlv2", "Phạm Quốc Bảo", "HLV", "CN02 - Cơ sở 2 (Tân Bình)", "Ca sáng (06:00 - 14:00)", "0912345604", 14000000, 3800000, "4.8 ⭐ (Tốt)", "Đang làm việc"));
        staffList.add(new StaffDetail("hlv3", "Nguyễn Thu Thảo", "HLV", "CN01 - Cơ sở 1 (Quận 1)", "Ca sáng (06:00 - 14:00)", "0912345605", 13000000, 3200000, "5.0 ⭐ (Xuất sắc)", "Đang làm việc"));
        staffList.add(new StaffDetail("quanly1", "Lê Thanh Sơn", "Quản lý", "CN02 - Cơ sở 2 (Tân Bình)", "Ca hành chính (08:00 - 17:00)", "0912345606", 18000000, 4000000, "4.9 ⭐ (Xuất sắc)", "Đang làm việc"));

        // Đồng bộ dữ liệu thực tế từ TaiKhoanDAO (bao gồm các tài khoản mới được tạo bởi Anh Đức)
        try {
            List<TaiKhoan> dbAccounts = taiKhoanDAO.layTatCa();
            if (dbAccounts != null) {
                for (TaiKhoan tk : dbAccounts) {
                    if (tk == null || tk.getTenDangNhap() == null) continue;
                    // Bỏ qua tài khoản hội viên, trung tâm nhân sự chỉ quản lý nội bộ
                    if ("Hội viên".equalsIgnoreCase(tk.getVaiTro())) continue;

                    boolean exists = false;
                    for (StaffDetail s : staffList) {
                        if (s.username.equalsIgnoreCase(tk.getTenDangNhap())) {
                            exists = true;
                            if (tk.getHoTen() != null && !tk.getHoTen().trim().isEmpty()) s.fullName = tk.getHoTen();
                            if (tk.getVaiTro() != null && !tk.getVaiTro().trim().isEmpty()) s.role = tk.getVaiTro();
                            if (tk.getChiNhanh() != null && !tk.getChiNhanh().trim().isEmpty()) s.branch = tk.getChiNhanh();
                            break;
                        }
                    }
                    if (!exists) {
                        String r = (tk.getVaiTro() != null) ? tk.getVaiTro() : "Nhân viên";
                        String b = (tk.getChiNhanh() != null) ? tk.getChiNhanh() : "CN01 - Cơ sở 1 (Quận 1)";
                        String fn = (tk.getHoTen() != null && !tk.getHoTen().trim().isEmpty()) ? tk.getHoTen() : tk.getTenDangNhap();
                        double base = "HLV".equalsIgnoreCase(r) ? 14000000 : ("Quản lý".equalsIgnoreCase(r) ? 18000000 : 7500000);
                        double bonus = "HLV".equalsIgnoreCase(r) ? 3500000 : 1500000;
                        staffList.add(new StaffDetail(tk.getTenDangNhap(), fn, r, b, "Ca xoay linh hoạt", "0909112233", base, bonus, "5.0 ⭐ (Xuất sắc)", "Đang làm việc"));
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void loadTableData() {
        model.setRowCount(0);
        for (StaffDetail s : staffList) {
            model.addRow(new Object[]{
                    s.username,
                    s.fullName,
                    s.role,
                    s.branch,
                    s.shift,
                    s.phone,
                    df.format(s.baseSalary),
                    df.format(s.kpiBonus),
                    df.format(s.getTotalIncome()),
                    s.rating,
                    s.status
            });
        }
    }

    private void updateKpiMetrics() {
        int total = staffList.size();
        int cn1 = 0, cn2 = 0, hq = 0;
        int lt = 0, hlv = 0, ql = 0, chu = 0;
        double totalPayroll = 0;
        int locked = 0;

        for (StaffDetail s : staffList) {
            if (s.branch.contains("CN01")) cn1++;
            else if (s.branch.contains("CN02")) cn2++;
            else hq++;

            if ("Lễ tân".equalsIgnoreCase(s.role)) lt++;
            else if ("HLV".equalsIgnoreCase(s.role)) hlv++;
            else if ("Quản lý".equalsIgnoreCase(s.role)) ql++;
            else chu++;

            totalPayroll += s.getTotalIncome();
            if ("Tạm khóa".equalsIgnoreCase(s.status)) locked++;
        }

        lblKpiTotalStaff.setText(total + " Nhân sự");
        lblKpiTotalStaffSub.setText("CN01: " + cn1 + " • CN02: " + cn2 + " • Trụ sở: " + hq);

        lblKpiRoleComposition.setText(lt + " LT • " + hlv + " HLV");
        lblKpiRoleCompositionSub.setText(ql + " Quản Lý • " + chu + " Chủ Phòng");

        lblKpiPayroll.setText(df.format(totalPayroll));
        lblKpiPayrollSub.setText("Đã gồm lương cứng & KPI");

        int activeRate = total > 0 ? (int) Math.round(((double) (total - locked) / total) * 100) : 100;
        lblKpiStatusRate.setText(activeRate + "% Sẵn sàng");
        lblKpiStatusRateSub.setText(locked == 0 ? "Đầy đủ nhân lực trực ca" : ("Có " + locked + " nhân sự tạm khóa"));
    }

    private void applyFilters() {
        String text = txtSearch.getText().trim();
        String roleFilter = (String) cbFilterRole.getSelectedItem();
        String branchFilter = (String) cbFilterBranch.getSelectedItem();
        String statusFilter = (String) cbFilterStatus.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + text, 0, 1, 5)); // Tìm theo user, tên, SĐT
        }

        if (roleFilter != null && !roleFilter.startsWith("Tất cả")) {
            filters.add(RowFilter.regexFilter("^" + roleFilter + "$", 2));
        }

        if (branchFilter != null && !branchFilter.startsWith("Tất cả")) {
            filters.add(RowFilter.regexFilter(branchFilter, 3));
        }

        if (statusFilter != null && !statusFilter.startsWith("Tất cả")) {
            filters.add(RowFilter.regexFilter("^" + statusFilter + "$", 10));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void xuLyThemNhanSu() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String role = (String) cbRole.getSelectedItem();
        String branch = (String) cbBranch.getSelectedItem();
        String shift = (String) cbShift.getSelectedItem();
        String salaryStr = txtBaseSalary.getText().trim().replaceAll("[^0-9]", "");
        String kpiStr = txtKpiBonus.getText().trim().replaceAll("[^0-9]", "");
        String rating = (String) cbRating.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();

        if (username.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên đăng nhập, Họ tên và Số điện thoại!", "Thiếu Thông Tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            password = "123456";
        }

        // Kiểm tra trùng username
        for (StaffDetail s : staffList) {
            if (s.username.equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập '" + username + "' đã tồn tại trên hệ thống!", "Trùng Lặp", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        double baseSalary = salaryStr.isEmpty() ? 7500000 : Double.parseDouble(salaryStr);
        double kpiBonus = kpiStr.isEmpty() ? 1500000 : Double.parseDouble(kpiStr);

        StaffDetail newStaff = new StaffDetail(username, fullName, role, branch, shift, phone, baseSalary, kpiBonus, rating, status);
        staffList.add(newStaff);

        // ĐỒNG BỘ LƯU VÀO CSDL TAIKHOANDAO ĐỂ NHÂN VIÊN CÓ THỂ ĐĂNG NHẬP NGAY!
        TaiKhoan tk = new TaiKhoan(username, PasswordUtil.hashPassword(password), role, fullName, branch);
        taiKhoanDAO.them(tk);

        loadTableData();
        updateKpiMetrics();
        xuLyLamMoiForm();

        JOptionPane.showMessageDialog(this,
                "<html><body>" +
                        "<h3>THÊM NHÂN SỰ & CẤP TÀI KHOẢN THÀNH CÔNG!</h3>" +
                        "<p>Họ tên: <b>" + fullName + "</b> (" + role + ")</p>" +
                        "<p>Tài khoản: <b>" + username + "</b> | Mật khẩu: <b>" + password + "</b></p>" +
                        "<p>Cơ sở: <b>" + branch + "</b> | Ca trực: <b>" + shift + "</b></p>" +
                        "<p style='color: green;'>✅ Nhân viên có thể đăng nhập ngay vào hệ thống!</p>" +
                        "</body></html>",
                "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyCapNhatNhanSu() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên trong bảng danh sách để cập nhật!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        String username = (String) model.getValueAt(modelRow, 0);

        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String role = (String) cbRole.getSelectedItem();
        String branch = (String) cbBranch.getSelectedItem();
        String shift = (String) cbShift.getSelectedItem();
        String salaryStr = txtBaseSalary.getText().trim().replaceAll("[^0-9]", "");
        String kpiStr = txtKpiBonus.getText().trim().replaceAll("[^0-9]", "");
        String rating = (String) cbRating.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();

        if (fullName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên và số điện thoại không được để trống!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double baseSalary = salaryStr.isEmpty() ? 7500000 : Double.parseDouble(salaryStr);
        double kpiBonus = kpiStr.isEmpty() ? 1500000 : Double.parseDouble(kpiStr);

        for (StaffDetail s : staffList) {
            if (s.username.equalsIgnoreCase(username)) {
                s.fullName = fullName;
                s.phone = phone;
                s.role = role;
                s.branch = branch;
                s.shift = shift;
                s.baseSalary = baseSalary;
                s.kpiBonus = kpiBonus;
                s.rating = rating;
                s.status = status;
                break;
            }
        }

        // Cập nhật CSDL
        TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(username);
        if (tk != null) {
            tk.setHoTen(fullName);
            tk.setVaiTro(role);
            tk.setChiNhanh(branch);
            taiKhoanDAO.capNhat(tk);
        }

        loadTableData();
        updateKpiMetrics();
        JOptionPane.showMessageDialog(this, "Đã cập nhật thành công hồ sơ nhân sự: " + fullName, "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyXoaNhanSu() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự cần xóa / sa thải!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        String username = (String) model.getValueAt(modelRow, 0);

        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Tuyệt đối không thể xóa tài khoản Quản trị tối cao của Chủ phòng (admin)!", "Cấm Thao Tác", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhân sự [" + username + "] khỏi toàn bộ chuỗi phòng tập?",
                "Xác Nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            staffList.removeIf(s -> s.username.equalsIgnoreCase(username));
            taiKhoanDAO.xoa(username);
            loadTableData();
            updateKpiMetrics();
            xuLyLamMoiForm();
            JOptionPane.showMessageDialog(this, "Đã xóa nhân viên [" + username + "] thành công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xuLyKhoaMoKhoa() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự cần Khóa hoặc Mở Khóa!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        String username = (String) model.getValueAt(modelRow, 0);

        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Không thể khóa tài khoản của Chủ phòng!", "Cấm Thao Tác", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (StaffDetail s : staffList) {
            if (s.username.equalsIgnoreCase(username)) {
                if ("Đang làm việc".equalsIgnoreCase(s.status)) {
                    s.status = "Tạm khóa";
                } else {
                    s.status = "Đang làm việc";
                }
                cbStatus.setSelectedItem(s.status);
                JOptionPane.showMessageDialog(this, "Đã chuyển trạng thái của " + s.fullName + " sang: [" + s.status + "]", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }

        loadTableData();
        updateKpiMetrics();
    }

    private void xuLyResetMatKhau() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản nhân sự cần đặt lại mật khẩu!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        String username = (String) model.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đặt lại mật khẩu tài khoản [" + username + "] về mặc định: '123456'?",
                "Xác Nhận Đặt Lại Mật Khẩu", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(username);
            if (tk != null) {
                tk.setMatKhau(PasswordUtil.hashPassword("123456"));
                taiKhoanDAO.capNhat(tk);
            }
            JOptionPane.showMessageDialog(this,
                    "Đã đặt lại mật khẩu của [" + username + "] thành công về: 123456!",
                    "Thành Công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xuLyLamMoiForm() {
        table.clearSelection();
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtPassword.setText("123456");
        txtFullName.setText("");
        txtPhone.setText("");
        cbRole.setSelectedIndex(0);
        cbBranch.setSelectedIndex(0);
        cbShift.setSelectedIndex(0);
        txtBaseSalary.setText("7,500,000");
        txtKpiBonus.setText("1,500,000");
        cbRating.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
    }

    private void xuLyXuatExcel() {
        ExcelExportHelper.exportTableToExcel(this, table, "BangLuong_NhanSu_FitLife");
    }

    private void moDialogLichTrucHomNay() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "BẢNG ĐIỀU PHỐI & KIỂM TRA ĐỘ PHỦ CA TRỰC CHUỖI (ANH ĐỨC)", true);
        dlg.setSize(820, 530);
        dlg.setLocationRelativeTo(this);

        JPanel pnl = new JPanel(new BorderLayout(10, 14));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Phân tích độ phủ ca trực
        int cn1Morning = 0, cn1Afternoon = 0;
        int cn2Morning = 0, cn2Afternoon = 0;

        for (StaffDetail s : staffList) {
            if ("Tạm khóa".equalsIgnoreCase(s.status)) continue;
            boolean isCN1 = s.branch.contains("CN01");
            boolean isCN2 = s.branch.contains("CN02");
            boolean isMorning = s.shift.toLowerCase().contains("sáng");
            boolean isAfternoon = s.shift.toLowerCase().contains("chiều");

            if (isCN1) {
                if (isMorning) cn1Morning++;
                if (isAfternoon) cn1Afternoon++;
            }
            if (isCN2) {
                if (isMorning) cn2Morning++;
                if (isAfternoon) cn2Afternoon++;
            }
        }

        JPanel pHeader = new JPanel(new BorderLayout(0, 8));
        pHeader.setOpaque(false);

        JLabel title = new JLabel("BẢNG ĐIỀU PHỐI & ĐỘ PHỦ CA TRỰC TOÀN CHUỖI (ANH ĐỨC)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(new Color(15, 23, 42));

        boolean coverageOk = (cn1Morning > 0 && cn1Afternoon > 0 && cn2Morning > 0 && cn2Afternoon > 0);
        String alertText = coverageOk ?
                "🟢 HỆ THỐNG AN TOÀN: CN01 (" + cn1Morning + " sáng / " + cn1Afternoon + " chiều) • CN02 (" + cn2Morning + " sáng / " + cn2Afternoon + " chiều) • Đầy đủ nhân lực trực" :
                "⚠️ CẢNH BÁO CA TRỰC: Có ca thiếu nhân lực trực! Chủ phòng cần điều động thêm nhân sự.";

        JLabel lblAlert = new JLabel(alertText);
        lblAlert.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAlert.setOpaque(true);
        lblAlert.setBackground(coverageOk ? new Color(236, 253, 245) : new Color(254, 243, 199));
        lblAlert.setForeground(coverageOk ? new Color(16, 185, 129) : new Color(217, 119, 6));
        lblAlert.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(coverageOk ? new Color(16, 185, 129) : new Color(217, 119, 6), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        pHeader.add(title, BorderLayout.NORTH);
        pHeader.add(lblAlert, BorderLayout.SOUTH);
        pnl.add(pHeader, BorderLayout.NORTH);

        String[] cols = {"Cơ Sở", "Ca Làm Việc", "Nhân Sự Trực", "Vai Trò", "Số ĐT", "Tình Trạng"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (StaffDetail s : staffList) {
            String sttBadge = "Tạm khóa".equalsIgnoreCase(s.status) ? "🔴 Tạm khóa" :
                             (s.shift.toLowerCase().contains("sáng") ? "🟢 Đang trực ca" : "🟡 Ca tiếp theo");
            m.addRow(new Object[]{
                    s.branch,
                    s.shift,
                    s.fullName,
                    s.role,
                    s.phone,
                    sttBadge
            });
        }

        JTable t = new JTable(m);
        t.setRowHeight(34);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(241, 245, 249));

        pnl.add(new JScrollPane(t), BorderLayout.CENTER);

        JPanel pFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pFooter.setOpaque(false);
        JButton btnClose = new JButton("Đóng Cửa Sổ");
        btnClose.addActionListener(e -> dlg.dispose());
        pFooter.add(btnClose);
        pnl.add(pFooter, BorderLayout.SOUTH);

        dlg.setContentPane(pnl);
        dlg.setVisible(true);
    }

    private void moDialogDieuDongChiNhanh() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân sự cần điều động chi nhánh!", "Chưa chọn nhân sự", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        String username = (String) model.getValueAt(modelRow, 0);

        StaffDetail staff = null;
        for (StaffDetail s : staffList) {
            if (s.username.equalsIgnoreCase(username)) {
                staff = s;
                break;
            }
        }
        if (staff == null) return;

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "ĐIỀU ĐỘNG & BIỆT PHÁI NHÂN SỰ LIÊN CHI NHÁNH (ANH ĐỨC)", true);
        dlg.setSize(520, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));
        ((JPanel) dlg.getContentPane()).setBorder(new EmptyBorder(20, 24, 20, 24));
        dlg.getContentPane().setBackground(Color.WHITE);

        JPanel pHead = new JPanel(new BorderLayout(0, 4));
        pHead.setOpaque(false);
        JLabel lblH = new JLabel("LỆNH ĐIỀU ĐỘNG CÔNG TÁC CHI NHÁNH");
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblH.setForeground(new Color(15, 23, 42));
        JLabel lblSub = new JLabel("Điều phối nhân sự giữa CN01 (Quận 1) và CN02 (Tân Bình) • Quyền hạn Chủ phòng");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pHead.add(lblH, BorderLayout.NORTH);
        pHead.add(lblSub, BorderLayout.SOUTH);
        dlg.add(pHead, BorderLayout.NORTH);

        JPanel pForm = new JPanel(new GridLayout(5, 2, 10, 12));
        pForm.setOpaque(false);

        pForm.add(new JLabel("Nhân sự điều động:"));
        JLabel lblStaffName = new JLabel("<html><b>" + staff.fullName + "</b> (" + staff.username + " - " + staff.role + ")</html>");
        pForm.add(lblStaffName);

        pForm.add(new JLabel("Cơ sở hiện tại:"));
        JLabel lblCurrentBranch = new JLabel("<html><font color='#0284c7'><b>" + staff.branch + "</b></font> (" + staff.shift + ")</html>");
        pForm.add(lblCurrentBranch);

        pForm.add(new JLabel("Cơ sở điều động đến (*):"));
        JComboBox<String> cbNewBranch = new JComboBox<>(new String[]{"CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)", "Toàn hệ thống"});
        if (staff.branch.contains("CN01")) cbNewBranch.setSelectedIndex(1);
        else if (staff.branch.contains("CN02")) cbNewBranch.setSelectedIndex(0);
        else cbNewBranch.setSelectedIndex(0);
        pForm.add(cbNewBranch);

        pForm.add(new JLabel("Ca làm việc mới (*):"));
        JComboBox<String> cbNewShift = new JComboBox<>(new String[]{
                "Ca sáng (06:00 - 14:00)",
                "Ca chiều (14:00 - 22:00)",
                "Ca hành chính (08:00 - 17:00)",
                "Ca xoay linh hoạt"
        });
        cbNewShift.setSelectedItem(staff.shift);
        pForm.add(cbNewShift);

        pForm.add(new JLabel("Lý do điều động / Ghi chú:"));
        JTextField txtReason = new JTextField("Điều phối tăng cường nhân lực theo kế hoạch chuỗi của Anh Đức");
        pForm.add(txtReason);

        dlg.add(pForm, BorderLayout.CENTER);

        JPanel pBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pBot.setOpaque(false);
        JButton btnConfirm = new JButton("Xác Nhận Điều Động");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConfirm.setBackground(new Color(16, 185, 129));
        btnConfirm.setForeground(Color.WHITE);

        StaffDetail finalStaff = staff;
        btnConfirm.addActionListener(e -> {
            String newBranch = (String) cbNewBranch.getSelectedItem();
            String newShift = (String) cbNewShift.getSelectedItem();
            String reason = txtReason.getText().trim();

            finalStaff.branch = newBranch;
            finalStaff.shift = newShift;

            // Cập nhật CSDL
            TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(finalStaff.username);
            if (tk != null) {
                tk.setChiNhanh(newBranch);
                taiKhoanDAO.capNhat(tk);
            }

            loadTableData();
            updateKpiMetrics();
            dlg.dispose();
            JOptionPane.showMessageDialog(this,
                    "<html><body>" +
                            "<h3 style='color: #10B981;'>ĐIỀU ĐỘNG NHÂN SỰ THÀNH CÔNG!</h3>" +
                            "<p>Nhân sự: <b>" + finalStaff.fullName + "</b> (" + finalStaff.username + ")</p>" +
                            "<p>Cơ sở mới: <b>" + newBranch + "</b></p>" +
                            "<p>Ca trực: <b>" + newShift + "</b></p>" +
                            "<p>Lý do: <i>" + reason + "</i></p>" +
                            "</body></html>",
                    "Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnClose = new JButton("Hủy Bỏ");
        btnClose.addActionListener(e -> dlg.dispose());

        pBot.add(btnConfirm);
        pBot.add(btnClose);
        dlg.add(pBot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void moDialogHoSoChiTiet() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự trong danh sách để xem hồ sơ chi tiết!", "Chưa chọn nhân sự", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        String username = (String) model.getValueAt(modelRow, 0);

        StaffDetail staff = null;
        for (StaffDetail s : staffList) {
            if (s.username.equalsIgnoreCase(username)) {
                staff = s;
                break;
            }
        }
        if (staff == null) return;

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "HỒ SƠ NHÂN SỰ CHI TIẾT - " + staff.fullName, true);
        dlg.setSize(580, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));
        ((JPanel) dlg.getContentPane()).setBorder(new EmptyBorder(20, 24, 20, 24));
        dlg.getContentPane().setBackground(Color.WHITE);

        // Header Avatar & Badge
        JPanel pTop = new JPanel(new BorderLayout(15, 0));
        pTop.setOpaque(false);
        pTop.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(0, 0, 14, 0)
        ));

        String initials = "AD";
        if (staff.fullName != null && !staff.fullName.trim().isEmpty()) {
            String[] parts = staff.fullName.trim().split("\\s+");
            if (parts.length == 1) initials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            else initials = parts[0].substring(0, 1).toUpperCase() + parts[parts.length - 1].substring(0, 1).toUpperCase();
        }

        JLabel lblAv = new JLabel(initials, SwingConstants.CENTER);
        lblAv.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAv.setForeground(Color.WHITE);
        lblAv.setOpaque(true);
        Color avBg = "Chủ".equalsIgnoreCase(staff.role) ? new Color(99, 102, 241) :
                     ("Lễ tân".equalsIgnoreCase(staff.role) ? new Color(16, 185, 129) :
                     ("HLV".equalsIgnoreCase(staff.role) ? new Color(245, 158, 11) : new Color(14, 165, 233)));
        lblAv.setBackground(avBg);
        lblAv.setPreferredSize(new Dimension(50, 50));

        JPanel pName = new JPanel(new GridLayout(2, 1, 0, 3));
        pName.setOpaque(false);
        JLabel lName = new JLabel(staff.fullName);
        lName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lName.setForeground(new Color(15, 23, 42));

        JLabel lRoleSub = new JLabel("Vai trò: " + staff.role + " • Tài khoản: @" + staff.username + " • Trạng thái: " + staff.status);
        lRoleSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lRoleSub.setForeground(new Color(100, 116, 139));
        pName.add(lName);
        pName.add(lRoleSub);

        pTop.add(lblAv, BorderLayout.WEST);
        pTop.add(pName, BorderLayout.CENTER);
        dlg.add(pTop, BorderLayout.NORTH);

        // Center Details Grid
        JPanel pCenter = new JPanel(new GridLayout(7, 2, 10, 8));
        pCenter.setOpaque(false);

        addProfileField(pCenter, "Cơ Sở Công Tác:", staff.branch);
        addProfileField(pCenter, "Ca Trực Phân Công:", staff.shift);
        addProfileField(pCenter, "Số Điện Thoại Liên Hệ:", staff.phone);
        addProfileField(pCenter, "Đánh Giá Hiệu Suất:", staff.rating);
        addProfileField(pCenter, "Lương Cứng Định Mức:", df.format(staff.baseSalary));
        addProfileField(pCenter, "Thưởng KPI / Hoa Hồng:", df.format(staff.kpiBonus));
        addProfileField(pCenter, "Tổng Thu Nhập Tháng:", df.format(staff.getTotalIncome()));

        dlg.add(pCenter, BorderLayout.CENTER);

        JPanel pBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pBot.setOpaque(false);

        JButton btnClose = new JButton("Đóng Cửa Sổ");
        btnClose.addActionListener(e -> dlg.dispose());

        pBot.add(btnClose);
        dlg.add(pBot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void addProfileField(JPanel parent, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(71, 85, 105));

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        v.setForeground(new Color(15, 23, 42));

        parent.add(l);
        parent.add(v);
    }

    private void moDialogTinhThuongKPI() {
        int count = 0;
        double totalBonus = 0;
        for (StaffDetail s : staffList) {
            if (s.rating.contains("5.0") || s.rating.contains("4.9") || s.rating.contains("4.8")) {
                s.kpiBonus += 500000;
                totalBonus += 500000;
                count++;
            }
        }
        loadTableData();
        updateKpiMetrics();

        JOptionPane.showMessageDialog(this,
                "<html><body>" +
                        "<h3 style='color: #10B981;'>HOÀN TẤT DUYỆT THƯỞNG KPI HIỆU SUẤT!</h3>" +
                        "<p>Đã duyệt thưởng thêm <b>+500,000 VNĐ</b> cho <b>" + count + "</b> nhân sự đạt đánh giá xuất sắc (>= 4.8⭐).</p>" +
                        "<p>Tổng ngân sách khen thưởng: <b>" + df.format(totalBonus) + "</b></p>" +
                        "</body></html>",
                "Khen Thưởng KPI", JOptionPane.INFORMATION_MESSAGE);
    }
}
