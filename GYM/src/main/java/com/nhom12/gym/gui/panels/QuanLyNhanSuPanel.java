package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.ExcelExportHelper;

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
 * Màn hình Quản Lý Nhân Sự, Chấm Công, Đánh Giá KPI & Bảng Lương Toàn Chuỗi (Chủ phòng Anh Đức).
 * Thiết kế mở rộng Full-Width chuyên nghiệp:
 * - Tập trung 100% vào nghiệp vụ nhân sự: Hồ sơ nhân viên, phân ca trực, duyệt lương, thưởng KPI, điều động chi nhánh.
 * - Form cấp phát tài khoản & mật khẩu đã được chuyển giao sang màn hình "Tài Khoản Nhân Sự & Phân Quyền".
 */
public class QuanLyNhanSuPanel extends JPanel {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final DecimalFormat df = new DecimalFormat("#,### VNĐ");

    // Filters & Action Buttons
    private JTextField txtSearch;
    private JComboBox<String> cbFilterRole;
    private JComboBox<String> cbFilterBranch;
    private JComboBox<String> cbFilterStatus;

    private JButton btnEditSalary;
    private JButton btnViewProfile;
    private JButton btnTransferBranch;
    private JButton btnShiftBoard;
    private JButton btnCalculateKPI;
    private JButton btnDeleteStaff;
    private JButton btnExportExcel;
    private JButton btnRefresh;

    // Table & Model (Full Width)
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

        public StaffDetail(String username, String fullName, String role, String branch, String shift, String phone, double baseSalary, double kpiBonus, String rating, String status) {
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
        setLayout(new BorderLayout(0, 14));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(16, 20, 18, 20));

        initUI();
        initDefaultStaffData();
        loadTableData();
        updateKpiMetrics();

        // Lắng nghe sự kiện đồng bộ toàn hệ thống
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            if (type == com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU || type == com.nhom12.gym.service.DataSyncManager.DataType.ALL) {
                initDefaultStaffData();
                loadTableData();
                updateKpiMetrics();
            }
        });
    }

    private void initUI() {
        // --- 1. TOP HEADER BANNER ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);
        titleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("TRUNG TÂM QUẢN TRỊ NHÂN SỰ & BẢNG LƯƠNG CHUỖI PHÒNG TẬP (ANH ĐỨC)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Giám sát quân số 4 lực lượng nhân sự chuỗi 2 cơ sở • Bảng tính lương, thưởng KPI, hiệu suất & điều phối ca trực");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        pnlHeader.add(titleBox);
        pnlHeader.add(Box.createRigidArea(new Dimension(0, 12)));

        // --- 2. HÀNG 4 THẺ KPI TỔNG HỢP ---
        pnlHeader.add(createKpiCardsPanel());
        pnlHeader.add(Box.createRigidArea(new Dimension(0, 12)));

        // --- 3. THANH TÌM KIẾM & BỘ LỌC ĐA CHIỀU (FILTER TOOLBAR) ---
        pnlHeader.add(createFilterToolbar());
        add(pnlHeader, BorderLayout.NORTH);

        // --- 4. TRUNG TÂM: BẢNG NHÂN SỰ & BẢNG LƯƠNG TOÀN DIỆN (FULL WIDTH) ---
        add(createMainTablePanel(), BorderLayout.CENTER);
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

        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(180, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập tên nhân sự, mã NV...");

        JLabel lblFRole = new JLabel("Vai trò:");
        lblFRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFRole.setForeground(new Color(71, 85, 105));

        cbFilterRole = new JComboBox<>(new String[]{"Tất cả vai trò", "Lễ tân", "HLV", "Quản lý", "Chủ"});
        cbFilterRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFilterRole.setPreferredSize(new Dimension(130, 32));

        JLabel lblFBranch = new JLabel("Cơ sở:");
        lblFBranch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFBranch.setForeground(new Color(71, 85, 105));

        cbFilterBranch = new JComboBox<>(new String[]{"Tất cả cơ sở", "CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)", "Toàn hệ thống"});
        cbFilterBranch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFilterBranch.setPreferredSize(new Dimension(150, 32));

        JLabel lblFStatus = new JLabel("Trạng thái:");
        lblFStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFStatus.setForeground(new Color(71, 85, 105));

        cbFilterStatus = new JComboBox<>(new String[]{"Tất cả", "Đang làm việc", "Tạm khóa"});
        cbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFilterStatus.setPreferredSize(new Dimension(120, 32));

        pRow1.add(lblSearch);
        pRow1.add(txtSearch);
        pRow1.add(lblFRole);
        pRow1.add(cbFilterRole);
        pRow1.add(lblFBranch);
        pRow1.add(cbFilterBranch);
        pRow1.add(lblFStatus);
        pRow1.add(cbFilterStatus);

        // Hàng 2: Bộ công cụ điều hành chuyên sâu cho Quản lý & Chủ phòng
        JPanel pRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pRow2.setOpaque(false);
        pRow2.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnEditSalary = new JButton("✏️ Điều Chỉnh Lương & Ca Trực");
        btnEditSalary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEditSalary.setBackground(new Color(37, 99, 235));
        btnEditSalary.setForeground(Color.WHITE);
        btnEditSalary.setFocusPainted(false);
        btnEditSalary.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditSalary.setPreferredSize(new Dimension(215, 32));

        btnViewProfile = new JButton("📋 Hồ Sơ Chi Tiết");
        btnViewProfile.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnViewProfile.setBackground(new Color(79, 70, 229));
        btnViewProfile.setForeground(Color.WHITE);
        btnViewProfile.setFocusPainted(false);
        btnViewProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewProfile.setPreferredSize(new Dimension(135, 32));

        btnTransferBranch = new JButton("🏢 Điều Động Chi Nhánh");
        btnTransferBranch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTransferBranch.setBackground(new Color(99, 102, 241));
        btnTransferBranch.setForeground(Color.WHITE);
        btnTransferBranch.setFocusPainted(false);
        btnTransferBranch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTransferBranch.setPreferredSize(new Dimension(175, 32));

        btnShiftBoard = new JButton("📅 Lịch Trực & Ca Làm");
        btnShiftBoard.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnShiftBoard.setBackground(new Color(14, 165, 233));
        btnShiftBoard.setForeground(Color.WHITE);
        btnShiftBoard.setFocusPainted(false);
        btnShiftBoard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShiftBoard.setPreferredSize(new Dimension(165, 32));

        btnCalculateKPI = new JButton("💵 Tính Thưởng KPI");
        btnCalculateKPI.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalculateKPI.setBackground(new Color(245, 158, 11));
        btnCalculateKPI.setForeground(Color.WHITE);
        btnCalculateKPI.setFocusPainted(false);
        btnCalculateKPI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalculateKPI.setPreferredSize(new Dimension(145, 32));

        btnDeleteStaff = new JButton("🗑️ Xóa Nhân Sự");
        btnDeleteStaff.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeleteStaff.setBackground(new Color(239, 68, 68));
        btnDeleteStaff.setForeground(Color.WHITE);
        btnDeleteStaff.setFocusPainted(false);
        btnDeleteStaff.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDeleteStaff.setPreferredSize(new Dimension(130, 32));

        btnExportExcel = new JButton("📊 Xuất Bảng Lương (.csv)");
        btnExportExcel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportExcel.setBackground(new Color(16, 185, 129));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setFocusPainted(false);
        btnExportExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportExcel.setPreferredSize(new Dimension(185, 32));

        btnRefresh = new JButton("🔄 Làm Mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setPreferredSize(new Dimension(105, 32));

        pRow2.add(btnEditSalary);
        pRow2.add(btnViewProfile);
        pRow2.add(btnTransferBranch);
        pRow2.add(btnShiftBoard);
        pRow2.add(btnCalculateKPI);
        pRow2.add(btnDeleteStaff);
        pRow2.add(btnExportExcel);
        pRow2.add(btnRefresh);

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

        btnEditSalary.addActionListener(e -> moDialogDieuChinhLuong());
        btnViewProfile.addActionListener(e -> moDialogHoSoChiTiet());
        btnTransferBranch.addActionListener(e -> moDialogDieuDongChiNhanh());
        btnShiftBoard.addActionListener(e -> moDialogLichTrucHomNay());
        btnCalculateKPI.addActionListener(e -> moDialogTinhThuongKPI());
        btnDeleteStaff.addActionListener(e -> xuLyXoaNhanSu());
        btnExportExcel.addActionListener(e -> xuLyXuatExcel());
        btnRefresh.addActionListener(e -> {
            initDefaultStaffData();
            loadTableData();
            updateKpiMetrics();
        });

        return bar;
    }

    private JPanel createMainTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
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

        // Thiết lập chiều rộng cột trải rộng toàn màn hình
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        table.getColumnModel().getColumn(7).setPreferredWidth(120);
        table.getColumnModel().getColumn(8).setPreferredWidth(130);
        table.getColumnModel().getColumn(9).setPreferredWidth(110);
        table.getColumnModel().getColumn(10).setPreferredWidth(110);

        // Custom Cell Renderer tô màu badge theo Vai trò, Tiền tệ & Trạng thái
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(new Color(15, 23, 42));

                    // Căn giữa các cột mã, vai trò, SĐT, đánh giá, trạng thái
                    if (col == 0 || col == 2 || col == 5 || col == 9 || col == 10) {
                        setHorizontalAlignment(SwingConstants.CENTER);
                    } else if (col == 6 || col == 7 || col == 8) {
                        setHorizontalAlignment(SwingConstants.RIGHT);
                    } else {
                        setHorizontalAlignment(SwingConstants.LEFT);
                    }

                    // Cột Vai trò (Col 2)
                    if (col == 2 && value != null) {
                        String r = value.toString();
                        if ("Chủ".equalsIgnoreCase(r)) c.setForeground(new Color(99, 102, 241)); // Indigo
                        else if ("Lễ tân".equalsIgnoreCase(r)) c.setForeground(new Color(16, 185, 129)); // Emerald
                        else if ("HLV".equalsIgnoreCase(r)) c.setForeground(new Color(217, 119, 6)); // Amber
                        else if ("Quản lý".equalsIgnoreCase(r)) c.setForeground(new Color(14, 165, 233)); // Sky
                    }

                    // Cột Tổng Thu Nhập (Col 8)
                    if (col == 8 && value != null) {
                        c.setForeground(new Color(37, 99, 235));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
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

        // Click đúp vào dòng -> Mở cửa sổ điều chỉnh Lương & Ca trực
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    moDialogDieuChinhLuong();
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
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

        // Đồng bộ dữ liệu thực tế từ TaiKhoanDAO
        try {
            List<TaiKhoan> dbAccounts = taiKhoanDAO.layTatCa();
            if (dbAccounts != null) {
                for (TaiKhoan tk : dbAccounts) {
                    if (tk == null || tk.getTenDangNhap() == null) continue;
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
                        String phoneVal = tk.getTenDangNhap().matches("^0[35789][0-9]{8}$")
                                ? tk.getTenDangNhap()
                                : String.format("09%08d", Math.abs(tk.getTenDangNhap().hashCode()) % 100000000);
                        staffList.add(new StaffDetail(tk.getTenDangNhap(), fn, r, b, "Ca xoay linh hoạt", phoneVal, base, bonus, "5.0 ⭐ (Xuất sắc)", "Đang làm việc"));
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
        lblKpiPayrollSub.setText("Bình quân: " + (total > 0 ? df.format(totalPayroll / total) : "0 VNĐ") + " / người");

        double activeRate = total > 0 ? ((total - locked) * 100.0 / total) : 100;
        lblKpiStatusRate.setText(String.format("%.0f%% Đang trực", activeRate));
        lblKpiStatusRateSub.setText(locked == 0 ? "100% Sẵn sàng phục vụ khách" : "Có " + locked + " nhân sự tạm khóa");
    }

    private void applyFilters() {
        String keyword = txtSearch.getText().trim();
        String roleFilter = (String) cbFilterRole.getSelectedItem();
        String branchFilter = (String) cbFilterBranch.getSelectedItem();
        String statusFilter = (String) cbFilterStatus.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!keyword.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + keyword, 0, 1, 5));
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

    private void moDialogDieuChinhLuong() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân sự trên bảng để điều chỉnh Lương & Ca trực!", "Chưa chọn nhân sự", JOptionPane.WARNING_MESSAGE);
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

        if (staff == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ của nhân sự này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "ĐIỀU CHỈNH LƯƠNG & CA TRỰC - " + staff.fullName.toUpperCase(), true);
        dlg.setSize(520, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel pnl = new JPanel(new BorderLayout(0, 14));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel pHeader = new JPanel(new GridLayout(2, 1, 0, 3));
        pHeader.setOpaque(false);
        JLabel lblH = new JLabel("ĐIỀU CHỈNH CHẾ ĐỘ ĐÃI NGỘ & CA LÀM VIỆC");
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblH.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Nhân sự: " + staff.fullName + " (" + staff.username + " - " + staff.role + ")");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pHeader.add(lblH);
        pHeader.add(lblSub);

        JPanel pForm = new JPanel(new GridLayout(6, 2, 12, 10));
        pForm.setOpaque(false);

        Font fL = new Font("Segoe UI", Font.BOLD, 12);
        Font fI = new Font("Segoe UI", Font.PLAIN, 13);

        JTextField txtBase = new JTextField(String.format("%.0f", staff.baseSalary));
        txtBase.setFont(fI);

        JTextField txtBonus = new JTextField(String.format("%.0f", staff.kpiBonus));
        txtBonus.setFont(fI);

        JComboBox<String> cbShiftEdit = new JComboBox<>(new String[]{
                "Ca sáng (06:00 - 14:00)",
                "Ca chiều (14:00 - 22:00)",
                "Ca hành chính (08:00 - 17:00)",
                "Ca xoay linh hoạt"
        });
        cbShiftEdit.setFont(fI);
        cbShiftEdit.setSelectedItem(staff.shift);

        JComboBox<String> cbBranchEdit = new JComboBox<>(new String[]{
                "CN01 - Cơ sở 1 (Quận 1)",
                "CN02 - Cơ sở 2 (Tân Bình)",
                "Toàn hệ thống"
        });
        cbBranchEdit.setFont(fI);
        cbBranchEdit.setSelectedItem(staff.branch);

        JComboBox<String> cbRatingEdit = new JComboBox<>(new String[]{
                "5.0 ⭐ (Xuất sắc)", "4.8 ⭐ (Tốt)", "4.5 ⭐ (Khá)", "4.0 ⭐ (Đạt)"
        });
        cbRatingEdit.setFont(fI);
        cbRatingEdit.setSelectedItem(staff.rating);

        JComboBox<String> cbStatusEdit = new JComboBox<>(new String[]{
                "Đang làm việc", "Tạm khóa"
        });
        cbStatusEdit.setFont(fI);
        cbStatusEdit.setSelectedItem(staff.status);

        JLabel l1 = new JLabel("Lương Cơ Bản (VNĐ):"); l1.setFont(fL);
        JLabel l2 = new JLabel("Thưởng KPI / Hoa Hồng (VNĐ):"); l2.setFont(fL);
        JLabel l3 = new JLabel("Ca Làm Việc:"); l3.setFont(fL);
        JLabel l4 = new JLabel("Cơ Sở Công Tác:"); l4.setFont(fL);
        JLabel l5 = new JLabel("Đánh Giá Hiệu Suất:"); l5.setFont(fL);
        JLabel l6 = new JLabel("Trạng Thái Hoạt Động:"); l6.setFont(fL);

        pForm.add(l1); pForm.add(txtBase);
        pForm.add(l2); pForm.add(txtBonus);
        pForm.add(l3); pForm.add(cbShiftEdit);
        pForm.add(l4); pForm.add(cbBranchEdit);
        pForm.add(l5); pForm.add(cbRatingEdit);
        pForm.add(l6); pForm.add(cbStatusEdit);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pBtns.setOpaque(false);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.addActionListener(e -> dlg.dispose());

        final StaffDetail staffFinal = staff;
        JButton btnSave = new JButton("💾 Lưu Thay Đổi");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> {
            try {
                String baseStr = txtBase.getText().trim().replaceAll("[^0-9]", "");
                String bonusStr = txtBonus.getText().trim().replaceAll("[^0-9]", "");
                if (baseStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Vui lòng nhập Lương Cơ Bản!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double newBase = Double.parseDouble(baseStr);
                double newBonus = bonusStr.isEmpty() ? 0 : Double.parseDouble(bonusStr);

                staffFinal.baseSalary = newBase;
                staffFinal.kpiBonus = newBonus;
                staffFinal.shift = (String) cbShiftEdit.getSelectedItem();
                staffFinal.branch = (String) cbBranchEdit.getSelectedItem();
                staffFinal.rating = (String) cbRatingEdit.getSelectedItem();
                staffFinal.status = (String) cbStatusEdit.getSelectedItem();

                // Cập nhật CSDL TaiKhoan nếu có đổi chi nhánh
                TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(staffFinal.username);
                if (tk != null) {
                    tk.setChiNhanh(staffFinal.branch);
                    taiKhoanDAO.capNhat(tk);
                }

                loadTableData();
                updateKpiMetrics();
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);

                JOptionPane.showMessageDialog(dlg, "Đã cập nhật chế độ đãi ngộ & ca trực cho nhân sự " + staffFinal.fullName + " thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Dữ liệu nhập vào không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pBtns.add(btnCancel);
        pBtns.add(btnSave);

        pnl.add(pHeader, BorderLayout.NORTH);
        pnl.add(pForm, BorderLayout.CENTER);
        pnl.add(pBtns, BorderLayout.SOUTH);

        dlg.setContentPane(pnl);
        dlg.setVisible(true);
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
            JOptionPane.showMessageDialog(this, "Tuyệt đối không thể xóa hồ sơ quản trị của Chủ phòng (admin)!", "Cấm Thao Tác", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhân sự [" + username + "] khỏi danh sách bảng lương chuỗi?",
                "Xác Nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            staffList.removeIf(s -> s.username.equalsIgnoreCase(username));
            taiKhoanDAO.xoa(username);
            loadTableData();
            updateKpiMetrics();
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
            JOptionPane.showMessageDialog(this, "Đã xóa nhân viên [" + username + "] thành công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xuLyXuatExcel() {
        ExcelExportHelper.exportTableToExcel(this, table, "BangLuong_NhanSu_ToanChuoi");
    }

    private void moDialogLichTrucHomNay() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "LỊCH TRỰC & ĐỘ PHỦ CA NHÂN SỰ HÔM NAY", true);
        dlg.setSize(750, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));
        ((JPanel) dlg.getContentPane()).setBorder(new EmptyBorder(18, 20, 18, 20));
        dlg.getContentPane().setBackground(Color.WHITE);

        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);

        JLabel lblH = new JLabel("BẢNG PHÂN CA TRỰC 2 CƠ SỞ THEO THỜI GIAN THỰC");
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblH.setForeground(new Color(15, 23, 42));
        pnl.add(lblH, BorderLayout.NORTH);

        String[] cols = {"Mã NV", "Họ Và Tên", "Vai Trò", "Cơ Sở", "Ca Trực Hôm Nay", "Trạng Thái"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0);
        for (StaffDetail s : staffList) {
            tm.addRow(new Object[]{s.username, s.fullName, s.role, s.branch, s.shift, s.status});
        }
        JTable t = new JTable(tm);
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
        if (staffList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Danh sách nhân sự hiện đang trống!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int selected = table.getSelectedRow();
        int initialIndex = 0;
        if (selected != -1) {
            int modelRow = table.convertRowIndexToModel(selected);
            String u = (String) model.getValueAt(modelRow, 0);
            for (int i = 0; i < staffList.size(); i++) {
                if (staffList.get(i).username.equalsIgnoreCase(u)) {
                    initialIndex = i;
                    break;
                }
            }
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "ĐIỀU ĐỘNG & BIỆT PHÁI NHÂN SỰ LIÊN CHI NHÁNH (ANH ĐỨC)", true);
        dlg.setSize(580, 480);
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

        JPanel pForm = new JPanel(new GridLayout(4, 2, 10, 14));
        pForm.setOpaque(false);

        JLabel lStaff = new JLabel("Chọn nhân sự:");
        lStaff.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String[] staffNames = new String[staffList.size()];
        for (int i = 0; i < staffList.size(); i++) {
            staffNames[i] = staffList.get(i).fullName + " (" + staffList.get(i).username + " - " + staffList.get(i).role + ")";
        }
        JComboBox<String> cbStaffPicker = new JComboBox<>(staffNames);
        cbStaffPicker.setSelectedIndex(initialIndex);

        JLabel lCurBranch = new JLabel("Cơ sở hiện tại:");
        lCurBranch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lblCurVal = new JLabel(staffList.get(initialIndex).branch);
        lblCurVal.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        JLabel lNewBranch = new JLabel("Cơ sở chuyển đến (*):");
        lNewBranch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> cbNewBranch = new JComboBox<>(new String[]{"CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)", "Toàn hệ thống"});
        if (staffList.get(initialIndex).branch.contains("CN01")) cbNewBranch.setSelectedIndex(1);
        else cbNewBranch.setSelectedIndex(0);

        JLabel lNewShift = new JLabel("Phân bổ ca trực mới:");
        lNewShift.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> cbNewShift = new JComboBox<>(new String[]{
                "Ca sáng (06:00 - 14:00)",
                "Ca chiều (14:00 - 22:00)",
                "Ca hành chính (08:00 - 17:00)",
                "Ca xoay linh hoạt"
        });
        cbNewShift.setSelectedItem(staffList.get(initialIndex).shift);

        cbStaffPicker.addActionListener(e -> {
            int idx = cbStaffPicker.getSelectedIndex();
            if (idx >= 0 && idx < staffList.size()) {
                lblCurVal.setText(staffList.get(idx).branch);
                if (staffList.get(idx).branch.contains("CN01")) cbNewBranch.setSelectedIndex(1);
                else cbNewBranch.setSelectedIndex(0);
                cbNewShift.setSelectedItem(staffList.get(idx).shift);
            }
        });

        pForm.add(lStaff); pForm.add(cbStaffPicker);
        pForm.add(lCurBranch); pForm.add(lblCurVal);
        pForm.add(lNewBranch); pForm.add(cbNewBranch);
        pForm.add(lNewShift); pForm.add(cbNewShift);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pBtns.setOpaque(false);
        JButton btnCancel = new JButton("Hủy Lệnh");
        btnCancel.addActionListener(e -> dlg.dispose());

        JButton btnConfirm = new JButton("Ban Hành Lệnh Điều Động");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConfirm.setBackground(new Color(99, 102, 241));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.addActionListener(e -> {
            int idx = cbStaffPicker.getSelectedIndex();
            String nb = (String) cbNewBranch.getSelectedItem();
            String ns = (String) cbNewShift.getSelectedItem();

            StaffDetail s = staffList.get(idx);
            s.branch = nb;
            s.shift = ns;

            TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(s.username);
            if (tk != null) {
                tk.setChiNhanh(nb);
                taiKhoanDAO.capNhat(tk);
            }

            loadTableData();
            updateKpiMetrics();
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);

            JOptionPane.showMessageDialog(dlg, "Đã ban hành lệnh điều động: " + s.fullName + " chuyển sang công tác tại [" + nb + "]", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });

        pBtns.add(btnCancel);
        pBtns.add(btnConfirm);

        dlg.add(pHead, BorderLayout.NORTH);
        dlg.add(pForm, BorderLayout.CENTER);
        dlg.add(pBtns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void moDialogHoSoChiTiet() {
        if (staffList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Danh sách nhân sự hiện đang trống!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int selected = table.getSelectedRow();
        int initialIndex = 0;
        if (selected != -1) {
            int modelRow = table.convertRowIndexToModel(selected);
            String u = (String) model.getValueAt(modelRow, 0);
            for (int i = 0; i < staffList.size(); i++) {
                if (staffList.get(i).username.equalsIgnoreCase(u)) {
                    initialIndex = i;
                    break;
                }
            }
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "HỒ SƠ NHÂN SỰ CHI TIẾT", true);
        dlg.setSize(620, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));
        ((JPanel) dlg.getContentPane()).setBorder(new EmptyBorder(18, 22, 18, 22));
        dlg.getContentPane().setBackground(Color.WHITE);

        JPanel pHeaderContainer = new JPanel(new BorderLayout(0, 12));
        pHeaderContainer.setOpaque(false);

        JPanel pSelectRow = new JPanel(new BorderLayout(8, 0));
        pSelectRow.setOpaque(false);
        JLabel lSelect = new JLabel("Xem hồ sơ nhân sự:");
        lSelect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lSelect.setForeground(new Color(71, 85, 105));
        pSelectRow.add(lSelect, BorderLayout.WEST);

        String[] staffNames = new String[staffList.size()];
        for (int i = 0; i < staffList.size(); i++) {
            StaffDetail s = staffList.get(i);
            staffNames[i] = s.fullName + " (" + s.username + " - " + s.role + ")";
        }
        JComboBox<String> cbChooser = new JComboBox<>(staffNames);
        cbChooser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbChooser.setSelectedIndex(initialIndex);
        pSelectRow.add(cbChooser, BorderLayout.CENTER);
        pHeaderContainer.add(pSelectRow, BorderLayout.NORTH);

        JPanel pTop = new JPanel(new BorderLayout(15, 0));
        pTop.setOpaque(false);
        pTop.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(0, 0, 12, 0)
        ));

        JLabel lblAv = new JLabel("", SwingConstants.CENTER);
        lblAv.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAv.setForeground(Color.WHITE);
        lblAv.setOpaque(true);
        lblAv.setPreferredSize(new Dimension(52, 52));

        JPanel pName = new JPanel(new GridLayout(2, 1, 0, 3));
        pName.setOpaque(false);
        JLabel lName = new JLabel("");
        lName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lName.setForeground(new Color(15, 23, 42));
        JLabel lRoleSub = new JLabel("");
        lRoleSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lRoleSub.setForeground(new Color(100, 116, 139));
        pName.add(lName);
        pName.add(lRoleSub);

        pTop.add(lblAv, BorderLayout.WEST);
        pTop.add(pName, BorderLayout.CENTER);
        pHeaderContainer.add(pTop, BorderLayout.CENTER);
        dlg.add(pHeaderContainer, BorderLayout.NORTH);

        JPanel pDetails = new JPanel(new GridLayout(6, 2, 12, 12));
        pDetails.setOpaque(false);

        JLabel vPhone = new JLabel();
        JLabel vBranch = new JLabel();
        JLabel vShift = new JLabel();
        JLabel vSalary = new JLabel();
        JLabel vKpi = new JLabel();
        JLabel vTotal = new JLabel();
        JLabel vRating = new JLabel();
        JLabel vStatus = new JLabel();
        JLabel vContract = new JLabel("Hợp đồng chính thức (Không thời hạn)");
        JLabel vKpiDesc = new JLabel("Đạt 115% chỉ tiêu doanh số tháng");

        Font fDL = new Font("Segoe UI", Font.BOLD, 12);
        Font fDV = new Font("Segoe UI", Font.PLAIN, 13);
        JLabel ld1 = new JLabel("Điện thoại liên hệ:"); ld1.setFont(fDL); vPhone.setFont(fDV);
        JLabel ld2 = new JLabel("Cơ sở công tác:"); ld2.setFont(fDL); vBranch.setFont(fDV);
        JLabel ld3 = new JLabel("Ca làm việc cố định:"); ld3.setFont(fDL); vShift.setFont(fDV);
        JLabel ld4 = new JLabel("Lương cơ bản:"); ld4.setFont(fDL); vSalary.setFont(fDV);
        JLabel ld5 = new JLabel("Thưởng KPI / Hoa hồng:"); ld5.setFont(fDL); vKpi.setFont(fDV);
        JLabel ld6 = new JLabel("Tổng thu nhập hàng tháng:"); ld6.setFont(fDL); vTotal.setFont(new Font("Segoe UI", Font.BOLD, 14)); vTotal.setForeground(new Color(37, 99, 235));
        JLabel ld7 = new JLabel("Đánh giá hiệu suất:"); ld7.setFont(fDL); vRating.setFont(fDV);
        JLabel ld8 = new JLabel("Trạng thái công tác:"); ld8.setFont(fDL); vStatus.setFont(fDV);
        JLabel ld9 = new JLabel("Loại hợp đồng:"); ld9.setFont(fDL); vContract.setFont(fDV);
        JLabel ld10 = new JLabel("Ghi chú hiệu suất:"); ld10.setFont(fDL); vKpiDesc.setFont(fDV);

        pDetails.add(ld1); pDetails.add(vPhone);
        pDetails.add(ld2); pDetails.add(vBranch);
        pDetails.add(ld3); pDetails.add(vShift);
        pDetails.add(ld4); pDetails.add(vSalary);
        pDetails.add(ld5); pDetails.add(vKpi);
        pDetails.add(ld6); pDetails.add(vTotal);
        pDetails.add(ld7); pDetails.add(vRating);
        pDetails.add(ld8); pDetails.add(vStatus);
        pDetails.add(ld9); pDetails.add(vContract);
        pDetails.add(ld10); pDetails.add(vKpiDesc);

        dlg.add(pDetails, BorderLayout.CENTER);

        Runnable updateView = () -> {
            int idx = cbChooser.getSelectedIndex();
            if (idx >= 0 && idx < staffList.size()) {
                StaffDetail s = staffList.get(idx);
                lName.setText(s.fullName);
                lRoleSub.setText("Mã NV: " + s.username + " • Vai trò: " + s.role + " • " + s.branch);

                String initials = s.fullName.length() > 0 ? s.fullName.substring(0, 1).toUpperCase() : "NV";
                lblAv.setText(initials);
                if ("Chủ".equalsIgnoreCase(s.role)) lblAv.setBackground(new Color(99, 102, 241));
                else if ("Lễ tân".equalsIgnoreCase(s.role)) lblAv.setBackground(new Color(16, 185, 129));
                else if ("HLV".equalsIgnoreCase(s.role)) lblAv.setBackground(new Color(245, 158, 11));
                else lblAv.setBackground(new Color(14, 165, 233));

                vPhone.setText(s.phone);
                vBranch.setText(s.branch);
                vShift.setText(s.shift);
                vSalary.setText(df.format(s.baseSalary));
                vKpi.setText(df.format(s.kpiBonus));
                vTotal.setText(df.format(s.getTotalIncome()));
                vRating.setText(s.rating);
                vStatus.setText(s.status);
            }
        };

        cbChooser.addActionListener(e -> updateView.run());
        updateView.run();

        JPanel pFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pFooter.setOpaque(false);
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dlg.dispose());
        pFooter.add(btnClose);
        dlg.add(pFooter, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void moDialogTinhThuongKPI() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "CÔNG CỤ TÍNH THƯỞNG KPI & HOA HỒNG (DOANH THU & HIỆU SUẤT)", true);
        dlg.setSize(600, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));
        ((JPanel) dlg.getContentPane()).setBorder(new EmptyBorder(20, 24, 20, 24));
        dlg.getContentPane().setBackground(Color.WHITE);

        JPanel pHead = new JPanel(new GridLayout(2, 1, 0, 3));
        pHead.setOpaque(false);
        JLabel lblH = new JLabel("BỘ QUY TẮC TÍNH THƯỞNG KPI HỆ THỐNG FITLIFE");
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblH.setForeground(new Color(15, 23, 42));
        JLabel lblSub = new JLabel("Tự động áp dụng tỷ lệ hoa hồng doanh số: HLV (15% gói PT) • Lễ tân (2% tổng bill đăng ký)");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pHead.add(lblH);
        pHead.add(lblSub);

        JPanel pBody = new JPanel(new GridLayout(4, 2, 12, 14));
        pBody.setOpaque(false);

        JLabel lPolicy = new JLabel("Chọn chính sách thưởng tháng:");
        lPolicy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> cbPolicy = new JComboBox<>(new String[]{
                "Tháng 10/2026: Tăng tốc doanh số (+10% Bonus cho HLV)",
                "Chính sách chuẩn: Đạt 100% KPI = 1,500,000 VNĐ / LT",
                "Chính sách thi đua: Top 1 Doanh số nhận thêm 2,000,000 VNĐ"
        });

        JLabel lTarget = new JLabel("Mức độ hoàn thành chỉ tiêu:");
        lTarget.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> cbTarget = new JComboBox<>(new String[]{
                "Vượt mức chỉ tiêu (110% - 130%)",
                "Đạt chỉ tiêu (100%)",
                "Gần đạt chỉ tiêu (85% - 99%)"
        });

        pBody.add(lPolicy); pBody.add(cbPolicy);
        pBody.add(lTarget); pBody.add(cbTarget);

        dlg.add(pHead, BorderLayout.NORTH);
        dlg.add(pBody, BorderLayout.CENTER);

        JPanel pFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pFooter.setOpaque(false);
        JButton btnClose = new JButton("Hủy");
        btnClose.addActionListener(e -> dlg.dispose());

        JButton btnApply = new JButton("Duyệt & Cập Nhật Thưởng Toàn Đội");
        btnApply.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnApply.setBackground(new Color(245, 158, 11));
        btnApply.setForeground(Color.WHITE);
        btnApply.addActionListener(e -> {
            for (StaffDetail s : staffList) {
                if ("HLV".equalsIgnoreCase(s.role)) {
                    s.kpiBonus = 4500000;
                } else if ("Lễ tân".equalsIgnoreCase(s.role)) {
                    s.kpiBonus = 1800000;
                }
            }
            loadTableData();
            updateKpiMetrics();
            JOptionPane.showMessageDialog(dlg, "Đã duyệt và áp dụng chính sách thưởng KPI mới vào Bảng lương toàn chuỗi!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });

        pFooter.add(btnClose);
        pFooter.add(btnApply);
        dlg.add(pFooter, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }
}
