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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Màn hình Quản Trị Tài Khoản & Phân Quyền Nhân Sự Nội Bộ Toàn Chuỗi (Chủ phòng Anh Đức).
 * Tích hợp toàn diện:
 * 1. Form Cột Trái: Hồ Sơ & Cấp Phát Tài Khoản Nhân Sự (Thêm mới, Cập nhật, Đặt lại MK 123456, Khóa/Mở khóa, Xóa).
 * 2. Cột Phải: Phân loại 4 tab vai trò nhân sự nòng cốt (Lễ Tân, HLV, Quản Lý, Chủ Phòng) kèm bộ lọc & ma trận quyền hạn.
 */
public class QuanLyTaiKhoanVaNhanSuPanel extends JPanel {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    // Form Components (Cột trái)
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtFullName;
    private JTextField txtPhone;
    private JComboBox<String> cbRole;
    private JComboBox<String> cbBranch;
    private JComboBox<String> cbStatus;
    private JLabel lblRolePermissionDesc;

    private JButton btnAddAccount;
    private JButton btnUpdateAccount;
    private JButton btnDeleteAccount;
    private JButton btnToggleLock;
    private JButton btnResetPassword;
    private JButton btnClearForm;

    // Table & Tabs (Cột phải)
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private DefaultTableModel modelReceptionist;
    private DefaultTableModel modelTrainer;
    private DefaultTableModel modelManager;
    private DefaultTableModel modelOwner;

    private JTable tableReceptionist;
    private JTable tableTrainer;
    private JTable tableManager;
    private JTable tableOwner;

    private JTextField txtSearch;
    private final JLabel lblSummaryStats = new JLabel();

    // Lưu trữ số điện thoại và trạng thái giả lập cho tài khoản nội bộ
    private final Map<String, String> phoneMap = new HashMap<>();
    private final Map<String, String> statusMap = new HashMap<>();

    public QuanLyTaiKhoanVaNhanSuPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(16, 20, 18, 20));

        initDefaultMetadata();
        initUI();
        loadAllData();

        // Lắng nghe sự kiện đồng bộ toàn hệ thống
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            if (type == com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU || type == com.nhom12.gym.service.DataSyncManager.DataType.ALL) {
                loadAllData();
            }
        });
    }

    private void initDefaultMetadata() {
        phoneMap.put("admin", "0909000001");
        phoneMap.put("letan1", "0912345601");
        phoneMap.put("letan2", "0912345602");
        phoneMap.put("hlv1", "0912345603");
        phoneMap.put("hlv2", "0912345604");
        phoneMap.put("hlv3", "0912345605");
        phoneMap.put("quanly1", "0912345606");

        statusMap.put("admin", "Đang hoạt động");
        statusMap.put("letan1", "Đang hoạt động");
        statusMap.put("letan2", "Đang hoạt động");
        statusMap.put("hlv1", "Đang hoạt động");
        statusMap.put("hlv2", "Đang hoạt động");
        statusMap.put("hlv3", "Đang hoạt động");
        statusMap.put("quanly1", "Đang hoạt động");
    }

    private void initUI() {
        // --- 1. TOP HEADER BANNER ---
        JPanel pnlTop = new JPanel(new BorderLayout(0, 4));
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(0, 0, 8, 0));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("TRUNG TÂM QUẢN TRỊ TÀI KHOẢN & PHÂN QUYỀN NHÂN SỰ CHUỖI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Cấp phát tài khoản, phân quyền đăng nhập, đổi mật khẩu và quản trị 4 vai trò: Lễ Tân • HLV • Quản Lý • Chủ Phòng");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        pnlTop.add(titleBox, BorderLayout.WEST);

        add(pnlTop, BorderLayout.NORTH);

        // --- 2. MAIN CENTER: 2 CỘT (CỘT TRÁI: FORM CẤP TÀI KHOẢN; CỘT PHẢI: TABS DANH SÁCH TÀI KHOẢN) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(createLeftAccountFormPanel());
        splitPane.setRightComponent(createRightAccountTablePanel());

        add(splitPane, BorderLayout.CENTER);

        // --- 3. BOTTOM STATS BAR ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(8, 0, 0, 0));

        lblSummaryStats.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummaryStats.setForeground(new Color(71, 85, 105));
        pnlBottom.add(lblSummaryStats, BorderLayout.WEST);

        JLabel lblTips = new JLabel("Mẹo: Mọi tài khoản sau khi cấp mới hoặc đặt lại mật khẩu đều có thể đăng nhập ngay vào hệ thống!");
        lblTips.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblTips.setForeground(new Color(16, 185, 129));
        pnlBottom.add(lblTips, BorderLayout.EAST);

        add(pnlBottom, BorderLayout.SOUTH);
    }

    private JPanel createLeftAccountFormPanel() {
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

        // 1. Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = r++;
        JLabel lUser = new JLabel("Tên Đăng Nhập / Mã Số (*):");
        lUser.setFont(fLabel);
        formGrid.add(lUser, gbc);

        gbc.gridy = r++;
        txtUsername = new JTextField();
        txtUsername.setFont(fInput);
        formGrid.add(txtUsername, gbc);

        // 2. Mật khẩu khởi tạo
        gbc.gridy = r++;
        JLabel lPass = new JLabel("Mật Khẩu Khởi Tạo:");
        lPass.setFont(fLabel);
        formGrid.add(lPass, gbc);

        gbc.gridy = r++;
        txtPassword = new JTextField("123456");
        txtPassword.setFont(fInput);
        formGrid.add(txtPassword, gbc);

        // 3. Họ và tên
        gbc.gridy = r++;
        JLabel lName = new JLabel("Họ Và Tên Nhân Sự (*):");
        lName.setFont(fLabel);
        formGrid.add(lName, gbc);

        gbc.gridy = r++;
        txtFullName = new JTextField();
        txtFullName.setFont(fInput);
        formGrid.add(txtFullName, gbc);

        // 4. Số điện thoại
        gbc.gridy = r++;
        JLabel lPhone = new JLabel("Số Điện Thoại Liên Hệ (*):");
        lPhone.setFont(fLabel);
        formGrid.add(lPhone, gbc);

        gbc.gridy = r++;
        txtPhone = new JTextField();
        txtPhone.setFont(fInput);
        formGrid.add(txtPhone, gbc);

        // 5. Vai trò phân hệ
        gbc.gridy = r++;
        JLabel lRole = new JLabel("Vai Trò Phân Hệ & Quyền Truy Cập:");
        lRole.setFont(fLabel);
        formGrid.add(lRole, gbc);

        gbc.gridy = r++;
        cbRole = new JComboBox<>(new String[]{"Lễ tân", "HLV", "Quản lý", "Chủ"});
        cbRole.setFont(fInput);
        formGrid.add(cbRole, gbc);

        // 6. Cơ sở làm việc
        gbc.gridy = r++;
        JLabel lBranch = new JLabel("Cơ Sở Trực Ca / Phụ Trách:");
        lBranch.setFont(fLabel);
        formGrid.add(lBranch, gbc);

        gbc.gridy = r++;
        cbBranch = new JComboBox<>(new String[]{
                "CN01 - Cơ sở 1 (Quận 1)",
                "CN02 - Cơ sở 2 (Tân Bình)",
                "Toàn hệ thống"
        });
        cbBranch.setFont(fInput);
        formGrid.add(cbBranch, gbc);

        // 7. Trạng thái tài khoản
        gbc.gridy = r++;
        JLabel lStatus = new JLabel("Trạng Thái Tài Khoản:");
        lStatus.setFont(fLabel);
        formGrid.add(lStatus, gbc);

        gbc.gridy = r++;
        cbStatus = new JComboBox<>(new String[]{"Đang hoạt động", "Tạm khóa"});
        cbStatus.setFont(fInput);
        formGrid.add(cbStatus, gbc);

        // 8. Khung mô tả phân quyền
        gbc.gridy = r++;
        lblRolePermissionDesc = new JLabel();
        lblRolePermissionDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRolePermissionDesc.setForeground(new Color(37, 99, 235));
        lblRolePermissionDesc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        lblRolePermissionDesc.setBackground(new Color(239, 246, 255));
        lblRolePermissionDesc.setOpaque(true);
        updateRoleDescription();
        formGrid.add(lblRolePermissionDesc, gbc);

        cbRole.addActionListener(e -> updateRoleDescription());

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        left.add(scrollForm, BorderLayout.CENTER);

        // Panel 6 nút bấm hành động (2 cột x 3 hàng)
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnAddAccount = new JButton("+ Cấp Tài Khoản");
        btnAddAccount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddAccount.setBackground(new Color(16, 185, 129));
        btnAddAccount.setForeground(Color.WHITE);
        btnAddAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnUpdateAccount = new JButton("Cập Nhật");
        btnUpdateAccount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdateAccount.setBackground(new Color(37, 99, 235));
        btnUpdateAccount.setForeground(Color.WHITE);
        btnUpdateAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDeleteAccount = new JButton("Xóa Tài Khoản");
        btnDeleteAccount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeleteAccount.setBackground(new Color(239, 68, 68));
        btnDeleteAccount.setForeground(Color.WHITE);
        btnDeleteAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnToggleLock = new JButton("Khóa / Mở Khóa");
        btnToggleLock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnToggleLock.setBackground(new Color(245, 158, 11));
        btnToggleLock.setForeground(Color.WHITE);
        btnToggleLock.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnResetPassword = new JButton("Reset MK (123456)");
        btnResetPassword.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnResetPassword.setBackground(new Color(139, 92, 246));
        btnResetPassword.setForeground(Color.WHITE);
        btnResetPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnClearForm = new JButton("Làm Mới Form");
        btnClearForm.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClearForm.setBackground(new Color(100, 116, 139));
        btnClearForm.setForeground(Color.WHITE);
        btnClearForm.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(btnAddAccount);
        btnPanel.add(btnUpdateAccount);
        btnPanel.add(btnDeleteAccount);
        btnPanel.add(btnToggleLock);
        btnPanel.add(btnResetPassword);
        btnPanel.add(btnClearForm);

        left.add(btnPanel, BorderLayout.SOUTH);

        // Sự kiện các nút
        btnAddAccount.addActionListener(e -> xuLyThemTaiKhoanTuForm());
        btnUpdateAccount.addActionListener(e -> xuLyCapNhatTaiKhoanTuForm());
        btnDeleteAccount.addActionListener(e -> xuLyXoaTaiKhoan());
        btnToggleLock.addActionListener(e -> xuLyKhoaMoKhoa());
        btnResetPassword.addActionListener(e -> xuLyResetMatKhauMacDinh());
        btnClearForm.addActionListener(e -> lamMoiForm());

        return left;
    }

    private void updateRoleDescription() {
        String r = (String) cbRole.getSelectedItem();
        if ("Lễ tân".equalsIgnoreCase(r)) {
            lblRolePermissionDesc.setText("<html><b>Quyền Lễ Tân:</b> Check-in Flap Barrier, Đăng ký hội viên, Bán gói tập, Thu tiền POS, Điểm danh lớp.</html>");
        } else if ("HLV".equalsIgnoreCase(r)) {
            lblRolePermissionDesc.setText("<html><b>Quyền HLV:</b> Xem lịch dạy cá nhân, Điểm danh lớp Yoga/Zumba, Theo dõi đo chỉ số InBody.</html>");
        } else if ("Quản lý".equalsIgnoreCase(r)) {
            lblRolePermissionDesc.setText("<html><b>Quyền Quản Lý:</b> Quản lý kho hàng, Hồ sơ hội viên, Phân ca làm việc, Kiểm toán doanh số cơ sở.</html>");
        } else {
            lblRolePermissionDesc.setText("<html><b>Quyền Chủ Phòng:</b> Toàn quyền tối cao quản trị tài chính, nhân sự, gói tập, phân quyền chuỗi.</html>");
        }
    }

    private JPanel createRightAccountTablePanel() {
        JPanel right = new JPanel(new BorderLayout(0, 10));
        right.setOpaque(false);

        // Toolbar tìm kiếm & chức năng phụ
        JPanel pnlToolbar = new JPanel(new BorderLayout(10, 0));
        pnlToolbar.setBackground(Color.WHITE);
        pnlToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pSearch.setOpaque(false);
        JLabel lSearch = new JLabel("Tìm tài khoản:");
        lSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập username hoặc họ tên...");
        pSearch.add(lSearch);
        pSearch.add(txtSearch);
        pnlToolbar.add(pSearch, BorderLayout.WEST);

        JPanel pActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pActions.setOpaque(false);

        JButton btnChangePassCustom = new JButton("Đổi Mật Khẩu Tùy Chỉnh");
        btnChangePassCustom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnChangePassCustom.setBackground(new Color(245, 158, 11));
        btnChangePassCustom.setForeground(Color.WHITE);
        btnChangePassCustom.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangePassCustom.addActionListener(e -> xuLyDoiMatKhauTuyChinh());

        JButton btnExportExcel = new JButton("Xuất Excel (.csv)");
        btnExportExcel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportExcel.setBackground(new Color(16, 185, 129));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportExcel.addActionListener(e -> xuLyXuatExcel());

        JButton btnRefresh = new JButton("Làm Mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadAllData());

        pActions.add(btnChangePassCustom);
        pActions.add(btnExportExcel);
        pActions.add(btnRefresh);
        pnlToolbar.add(pActions, BorderLayout.EAST);

        right.add(pnlToolbar, BorderLayout.NORTH);

        // TabbedPane 4 Vai Trò
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);

        String[] colsStaff = {"Tên Đăng Nhập", "Họ Và Tên Nhân Sự", "Vai Trò", "Chi Nhánh Trực Ca", "Số Điện Thoại", "Trạng Thái"};

        modelReceptionist = new DefaultTableModel(colsStaff, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableReceptionist = createStyledTable(modelReceptionist);

        modelTrainer = new DefaultTableModel(colsStaff, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableTrainer = createStyledTable(modelTrainer);

        modelManager = new DefaultTableModel(colsStaff, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableManager = createStyledTable(modelManager);

        modelOwner = new DefaultTableModel(colsStaff, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableOwner = createStyledTable(modelOwner);

        // Sự kiện click chọn dòng ở các bảng -> Tự động điền dữ liệu lên form bên trái
        setupTableSelectionListener(tableReceptionist);
        setupTableSelectionListener(tableTrainer);
        setupTableSelectionListener(tableManager);
        setupTableSelectionListener(tableOwner);

        tabbedPane.addTab("  1. Nhân Sự Lễ Tân (Chị Linh)  ", createTableScrollPane(tableReceptionist));
        tabbedPane.addTab("  2. Đội Ngũ Huấn Luyện Viên (HLV)  ", createTableScrollPane(tableTrainer));
        tabbedPane.addTab("  3. Quản Lý Chi Nhánh (Anh Sơn)  ", createTableScrollPane(tableManager));
        tabbedPane.addTab("  4. Ban Quản Trị / Chủ Phòng (Anh Đức)  ", createTableScrollPane(tableOwner));

        right.add(tabbedPane, BorderLayout.CENTER);

        // Lọc tức thì theo từ khóa tìm kiếm
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applySearchFilter(); }
            public void removeUpdate(DocumentEvent e) { applySearchFilter(); }
            public void changedUpdate(DocumentEvent e) { applySearchFilter(); }
        });

        return right;
    }

    private void setupTableSelectionListener(JTable t) {
        t.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = t.getSelectedRow();
                if (row >= 0) {
                    int modelRow = t.convertRowIndexToModel(row);
                    DefaultTableModel tm = (DefaultTableModel) t.getModel();
                    String username = (String) tm.getValueAt(modelRow, 0);
                    String hoTen = (String) tm.getValueAt(modelRow, 1);
                    String vaiTro = (String) tm.getValueAt(modelRow, 2);
                    String chiNhanh = (String) tm.getValueAt(modelRow, 3);
                    String sdt = (String) tm.getValueAt(modelRow, 4);
                    String trangThai = (String) tm.getValueAt(modelRow, 5);

                    txtUsername.setText(username);
                    txtUsername.setEnabled(false); // Khóa username khi đang xem tài khoản có sẵn
                    txtFullName.setText(hoTen);
                    txtPhone.setText(sdt);
                    cbRole.setSelectedItem(vaiTro);
                    cbBranch.setSelectedItem(chiNhanh);
                    cbStatus.setSelectedItem(trangThai);
                    txtPassword.setText("••••••");
                    txtPassword.setEnabled(false);
                }
            }
        });
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(15, 23, 42));
        table.setGridColor(new Color(241, 245, 249));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        return table;
    }

    private JScrollPane createTableScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        sp.setBackground(Color.WHITE);
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private void applySearchFilter() {
        String text = txtSearch.getText().trim();
        applyFilterToTable(tableReceptionist, text);
        applyFilterToTable(tableTrainer, text);
        applyFilterToTable(tableManager, text);
        applyFilterToTable(tableOwner, text);
    }

    @SuppressWarnings("unchecked")
    private void applyFilterToTable(JTable table, String text) {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        if (sorter != null) {
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }
    }

    public void loadAllData() {
        modelReceptionist.setRowCount(0);
        modelTrainer.setRowCount(0);
        modelManager.setRowCount(0);
        modelOwner.setRowCount(0);

        List<TaiKhoan> list = taiKhoanDAO.layTatCa();
        int countLT = 0, countHLV = 0, countQL = 0, countChu = 0;

        for (TaiKhoan tk : list) {
            String role = tk.getVaiTro() != null ? tk.getVaiTro() : "";
            String branch = tk.getChiNhanh() != null ? tk.getChiNhanh() : "Toàn hệ thống";
            String phone = phoneMap.getOrDefault(tk.getTenDangNhap(), "090" + (1000000 + Math.abs(tk.getTenDangNhap().hashCode() % 8999999)));
            String status = statusMap.getOrDefault(tk.getTenDangNhap(), "Đang hoạt động");

            Object[] row = new Object[]{
                    tk.getTenDangNhap(),
                    tk.getHoTen(),
                    role,
                    branch,
                    phone,
                    status
            };

            if ("Lễ tân".equalsIgnoreCase(role)) {
                countLT++;
                modelReceptionist.addRow(row);
            } else if ("HLV".equalsIgnoreCase(role)) {
                countHLV++;
                modelTrainer.addRow(row);
            } else if ("Quản lý".equalsIgnoreCase(role)) {
                countQL++;
                modelManager.addRow(row);
            } else if ("Chủ".equalsIgnoreCase(role)) {
                countChu++;
                modelOwner.addRow(row);
            }
        }

        lblSummaryStats.setText(String.format("Tổng quân số nội bộ: %d Lễ tân • %d HLV • %d Quản lý • %d Chủ phòng (Tổng: %d tài khoản)",
                countLT, countHLV, countQL, countChu, countLT + countHLV + countQL + countChu));
    }

    private void xuLyThemTaiKhoanTuForm() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String role = (String) cbRole.getSelectedItem();
        String branch = (String) cbBranch.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên Đăng Nhập / Mã Số!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ và Tên nhân sự!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtFullName.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Số Điện Thoại liên hệ!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtPhone.requestFocus();
            return;
        }

        // Kiểm tra trùng username
        if (taiKhoanDAO.timTheoTenDangNhap(username) != null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập '" + username + "' đã tồn tại trên hệ thống!", "Trùng Lặp", JOptionPane.ERROR_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty() || password.contains("••••")) {
            password = "123456";
        }

        TaiKhoan tk = new TaiKhoan(username, PasswordUtil.hashPassword(password), role, fullName, branch);
        taiKhoanDAO.them(tk);

        phoneMap.put(username, phone);
        statusMap.put(username, status);

        loadAllData();
        com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);

        JOptionPane.showMessageDialog(this,
                "<html><body>" +
                        "<h3>CẤP TÀI KHOẢN NHÂN SỰ THÀNH CÔNG!</h3>" +
                        "<p>Họ tên: <b>" + fullName + "</b> (" + role + ")</p>" +
                        "<p>Tài khoản: <b>" + username + "</b> | Mật khẩu: <b>" + password + "</b></p>" +
                        "<p>Cơ sở: <b>" + branch + "</b></p>" +
                        "<p style='color: green;'>Nhân viên có thể dùng tài khoản này để đăng nhập ngay!</p>" +
                        "</body></html>",
                "Cấp Tài Khoản Thành Công", JOptionPane.INFORMATION_MESSAGE);

        lamMoiForm();
    }

    private void xuLyCapNhatTaiKhoanTuForm() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản từ danh sách để cập nhật!", "Chưa chọn tài khoản", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String role = (String) cbRole.getSelectedItem();
        String branch = (String) cbBranch.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ và Tên nhân sự!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtFullName.requestFocus();
            return;
        }

        TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(username);
        if (tk == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản '" + username + "' trong CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tk.setHoTen(fullName);
        tk.setVaiTro(role);
        tk.setChiNhanh(branch);
        taiKhoanDAO.capNhat(tk);

        phoneMap.put(username, phone);
        statusMap.put(username, status);

        loadAllData();
        com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);

        JOptionPane.showMessageDialog(this, "Đã cập nhật thành công hồ sơ tài khoản: " + username, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyResetMatKhauMacDinh() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            JTable cur = getActiveTable();
            int r = cur.getSelectedRow();
            if (r >= 0) {
                username = (String) cur.getModel().getValueAt(cur.convertRowIndexToModel(r), 0);
            }
        }

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần đặt lại mật khẩu!", "Chưa chọn tài khoản", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đặt lại mật khẩu cho tài khoản [" + username + "] về mặc định: 123456 ?",
                "Xác Nhận Đặt Lại Mật Khẩu", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(username);
            if (tk != null) {
                tk.setMatKhau(PasswordUtil.hashPassword("123456"));
                taiKhoanDAO.capNhat(tk);
                JOptionPane.showMessageDialog(this, "Mật khẩu của tài khoản [" + username + "] đã được đặt lại thành: 123456", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadAllData();
            }
        }
    }

    private void xuLyKhoaMoKhoa() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            JTable cur = getActiveTable();
            int r = cur.getSelectedRow();
            if (r >= 0) {
                username = (String) cur.getModel().getValueAt(cur.convertRowIndexToModel(r), 0);
            }
        }

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần Khóa / Mở Khóa!", "Chưa chọn tài khoản", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Tuyệt đối không thể khóa tài khoản Chủ phòng tối cao (admin)!", "Cấm Thao Tác", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String curStatus = statusMap.getOrDefault(username, "Đang hoạt động");
        String nextStatus = "Đang hoạt động".equalsIgnoreCase(curStatus) ? "Tạm khóa" : "Đang hoạt động";
        statusMap.put(username, nextStatus);
        cbStatus.setSelectedItem(nextStatus);

        loadAllData();
        JOptionPane.showMessageDialog(this, "Đã chuyển trạng thái tài khoản [" + username + "] sang: " + nextStatus, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyXoaTaiKhoan() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            JTable cur = getActiveTable();
            int r = cur.getSelectedRow();
            if (r >= 0) {
                username = (String) cur.getModel().getValueAt(cur.convertRowIndexToModel(r), 0);
            }
        }

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản trong bảng để xóa!", "Chưa chọn tài khoản", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Không được phép xóa tài khoản quản trị tối cao của Chủ phòng (admin)!", "Từ chối", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa vĩnh viễn tài khoản '" + username + "' khỏi hệ thống?",
                "Xác Nhận Xóa Tài Khoản", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            boolean ok = taiKhoanDAO.xoa(username);
            if (ok) {
                phoneMap.remove(username);
                statusMap.remove(username);
                JOptionPane.showMessageDialog(this, "Đã xóa thành công tài khoản: " + username, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadAllData();
                lamMoiForm();
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lamMoiForm() {
        txtUsername.setText("");
        txtUsername.setEnabled(true);
        txtPassword.setText("123456");
        txtPassword.setEnabled(true);
        txtFullName.setText("");
        txtPhone.setText("");
        cbRole.setSelectedIndex(0);
        cbBranch.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        txtUsername.requestFocus();
    }

    private void xuLyDoiMatKhauTuyChinh() {
        JTable currentTable = getActiveTable();
        int row = currentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản trong bảng để đổi mật khẩu!", "Chưa chọn tài khoản", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) currentTable.getValueAt(row, 0);
        String hoTen = (String) currentTable.getValueAt(row, 1);
        String vaiTro = (String) currentTable.getValueAt(row, 2);

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đổi Mật Khẩu - " + username, true);
        dlg.setSize(450, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel pnl = new JPanel(new BorderLayout(0, 16));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 3));
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("ĐỔI MẬT KHẨU TÙY CHỈNH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("<html>Tài khoản: <b style='color:#2563eb;'>" + username + "</b> - " + hoTen + " (" + vaiTro + ")</html>");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSub);

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 8));
        form.setOpaque(false);

        JLabel l1 = new JLabel("Mật khẩu mới (*):");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JPasswordField txtNewPass = new JPasswordField();
        txtNewPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel l2 = new JLabel("Xác nhận mật khẩu mới (*):");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JPasswordField txtConfirmPass = new JPasswordField();
        txtConfirmPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        form.add(l1);
        form.add(txtNewPass);
        form.add(l2);
        form.add(txtConfirmPass);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtns.setOpaque(false);

        JButton btnDefault = new JButton("Đặt Mặc Định (123456)");
        btnDefault.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnDefault.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDefault.addActionListener(ev -> {
            txtNewPass.setText("123456");
            txtConfirmPass.setText("123456");
        });

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.addActionListener(ev -> dlg.dispose());

        JButton btnSave = new JButton("Lưu Mật Khẩu");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(ev -> {
            String pass1 = new String(txtNewPass.getPassword()).trim();
            String pass2 = new String(txtConfirmPass.getPassword()).trim();
            if (pass1.isEmpty() || pass2.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập đầy đủ mật khẩu mới!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (pass1.length() < 4) {
                JOptionPane.showMessageDialog(dlg, "Mật khẩu phải có độ dài tối thiểu 4 ký tự!", "Mật khẩu quá ngắn", JOptionPane.WARNING_MESSAGE);
                txtNewPass.requestFocus();
                return;
            }
            if (!pass1.equals(pass2)) {
                JOptionPane.showMessageDialog(dlg, "Mật khẩu xác nhận không khớp!", "Lỗi xác nhận", JOptionPane.WARNING_MESSAGE);
                txtConfirmPass.requestFocus();
                return;
            }

            TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(username);
            if (tk != null) {
                tk.setMatKhau(PasswordUtil.hashPassword(pass1));
                taiKhoanDAO.capNhat(tk);
                JOptionPane.showMessageDialog(dlg, "Đã cập nhật mật khẩu mới thành công cho tài khoản: " + username, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadAllData();
            } else {
                JOptionPane.showMessageDialog(dlg, "Không tìm thấy tài khoản để cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.add(btnDefault, BorderLayout.WEST);
        pnlBtns.add(btnCancel);
        pnlBtns.add(btnSave);
        bottomRow.add(pnlBtns, BorderLayout.EAST);

        pnl.add(pnlHeader, BorderLayout.NORTH);
        pnl.add(form, BorderLayout.CENTER);
        pnl.add(bottomRow, BorderLayout.SOUTH);

        dlg.setContentPane(pnl);
        dlg.setVisible(true);
    }

    private void xuLyXuatExcel() {
        JTable current = getActiveTable();
        String tabName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()).replaceAll("[^a-zA-Z0-9]", "_");
        ExcelExportHelper.exportTableToExcel(this, current, "DanhSach_TaiKhoan_" + tabName);
    }

    private JTable getActiveTable() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0) return tableReceptionist;
        if (idx == 1) return tableTrainer;
        if (idx == 2) return tableManager;
        return tableOwner;
    }
}
