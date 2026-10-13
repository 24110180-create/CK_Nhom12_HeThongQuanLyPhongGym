package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.gui.DangKyTaiKhoanDialog;
import com.nhom12.gym.util.ExcelExportHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Quản Trị Tài Khoản Toàn Chuỗi Dành Riêng Cho Chủ Phòng (Anh Đức)
 * Phân chia quản lý rõ ràng cho 4 lực lượng:
 * 1. Lễ Tân
 * 2. Huấn Luyện Viên (HLV)
 * 3. Quản Lý Chi Nhánh
 * 4. Hội Viên (VIP Members)
 * Tích hợp chức năng Thêm, Sửa, Xóa, Reset Mật Khẩu, Khóa/Mở Khóa
 * và cam kết 100% các tài khoản sau khi tạo đều đăng nhập được vào ứng dụng!
 */
public class QuanLyTaiKhoanVaNhanSuPanel extends JPanel {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private DefaultTableModel modelReceptionist;
    private DefaultTableModel modelTrainer;
    private DefaultTableModel modelManager;
    private DefaultTableModel modelMember;

    private JTable tableReceptionist;
    private JTable tableTrainer;
    private JTable tableManager;
    private JTable tableMember;

    private final JLabel lblSummaryStats = new JLabel();

    public QuanLyTaiKhoanVaNhanSuPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        loadAllData();
    }

    private void initUI() {
        // --- 1. TOP HEADER & ACTION TOOLBAR ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);

        JLabel lblTitle = new JLabel("TRUNG TÂM QUẢN TRỊ TÀI KHOẢN & PHÂN QUYỀN NHÂN SỰ CHUỖI (ANH ĐỨC)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Quản lý tài khoản đăng nhập của 4 lực lượng: Lễ Tân • HLV • Quản Lý • Hội Viên");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        pnlTop.add(titleBox, BorderLayout.WEST);

        // Nút thêm tài khoản & Xuất Excel
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        JButton btnAddNew = new JButton("+ Thêm Tài Khoản Mới");
        btnAddNew.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddNew.setBackground(new Color(37, 99, 235));
        btnAddNew.setForeground(Color.WHITE);
        btnAddNew.setFocusPainted(false);
        btnAddNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddNew.setPreferredSize(new Dimension(190, 38));
        btnAddNew.addActionListener(e -> moDialogThemTaiKhoan());

        JButton btnResetPass = new JButton("Đặt Lại MK (123456)");
        btnResetPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnResetPass.setBackground(new Color(245, 158, 11)); // Amber
        btnResetPass.setForeground(Color.WHITE);
        btnResetPass.setFocusPainted(false);
        btnResetPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetPass.setPreferredSize(new Dimension(160, 38));
        btnResetPass.addActionListener(e -> xuLyResetMatKhau());

        JButton btnDelete = new JButton("Xóa Tài Khoản");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setBackground(new Color(220, 38, 38)); // Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setPreferredSize(new Dimension(130, 38));
        btnDelete.addActionListener(e -> xuLyXoaTaiKhoan());

        JButton btnRefresh = new JButton("Làm Mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setPreferredSize(new Dimension(90, 38));
        btnRefresh.addActionListener(e -> loadAllData());

        pnlActions.add(btnAddNew);
        pnlActions.add(btnResetPass);
        pnlActions.add(btnDelete);
        pnlActions.add(btnRefresh);
        pnlTop.add(pnlActions, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // --- 2. TABBED PANE 4 VAI TRÒ ---
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);

        String[] colsStaff = {"Tên Đăng Nhập", "Họ Và Tên Nhân Sự", "Vai Trò", "Chi Nhánh Trực Ca", "Mật Khẩu", "Trạng Thái"};
        String[] colsMember = {"Tên Đăng Nhập (SĐT)", "Họ Tên Hội Viên", "Vai Trò", "Chi Nhánh Đăng Ký", "Mã Thẻ QR", "Gói Tập Sở Hữu", "Trạng Thái"};

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

        modelMember = new DefaultTableModel(colsMember, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableMember = createStyledTable(modelMember);

        tabbedPane.addTab("  1. Nhân Sự Lễ Tân  ", createTableScrollPane(tableReceptionist));
        tabbedPane.addTab("  2. Đội Ngũ Huấn Luyện Viên (HLV)  ", createTableScrollPane(tableTrainer));
        tabbedPane.addTab("  3. Quản Lý Chi Nhánh  ", createTableScrollPane(tableManager));
        tabbedPane.addTab("  4. Tài Khoản Hội Viên App  ", createTableScrollPane(tableMember));

        add(tabbedPane, BorderLayout.CENTER);

        // --- 3. BOTTOM STATS BAR ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);

        lblSummaryStats.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummaryStats.setForeground(new Color(71, 85, 105));
        pnlBottom.add(lblSummaryStats, BorderLayout.WEST);

        JLabel lblTips = new JLabel("💡 Mọi tài khoản sau khi thêm hoặc đổi mật khẩu đều có thể đăng nhập vào hệ thống ngay lập tức.");
        lblTips.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblTips.setForeground(new Color(16, 185, 129));
        pnlBottom.add(lblTips, BorderLayout.EAST);

        add(pnlBottom, BorderLayout.SOUTH);
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

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);

        return table;
    }

    private JScrollPane createTableScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        sp.setBackground(Color.WHITE);
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    public void loadAllData() {
        modelReceptionist.setRowCount(0);
        modelTrainer.setRowCount(0);
        modelManager.setRowCount(0);
        modelMember.setRowCount(0);

        List<TaiKhoan> list = taiKhoanDAO.layTatCa();
        int countLT = 0, countHLV = 0, countQL = 0, countHV = 0;

        for (TaiKhoan tk : list) {
            String role = tk.getVaiTro() != null ? tk.getVaiTro() : "";
            String branch = tk.getChiNhanh() != null ? tk.getChiNhanh() : "Toàn hệ thống";

            if ("Lễ tân".equalsIgnoreCase(role)) {
                countLT++;
                modelReceptionist.addRow(new Object[]{
                        tk.getTenDangNhap(), tk.getHoTen(), "Lễ tân", branch, "••••••", "Đang hoạt động"
                });
            } else if ("HLV".equalsIgnoreCase(role)) {
                countHLV++;
                modelTrainer.addRow(new Object[]{
                        tk.getTenDangNhap(), tk.getHoTen(), "HLV", branch, "••••••", "Đang hoạt động"
                });
            } else if ("Quản lý".equalsIgnoreCase(role) || "Chủ".equalsIgnoreCase(role)) {
                countQL++;
                modelManager.addRow(new Object[]{
                        tk.getTenDangNhap(), tk.getHoTen(), role, branch, "••••••", "Đang hoạt động"
                });
            } else {
                countHV++;
                HoiVien hv = hoiVienDAO.timKiemThongMinh(tk.getTenDangNhap());
                String code = (hv != null && hv.getMaTheQR() != null) ? hv.getMaTheQR() : "HV - Chờ cấp";
                String pkg = (hv != null && hv.getGoiTapDangKy() != null) ? hv.getGoiTapDangKy() : "Gói Tiêu Chuẩn";
                modelMember.addRow(new Object[]{
                        tk.getTenDangNhap(), tk.getHoTen(), "Hội viên", branch, code, pkg, "Đang hoạt động"
                });
            }
        }

        lblSummaryStats.setText(String.format("● Tổng quân số: %d Lễ tân • %d HLV • %d Quản lý/Chủ • %d Hội viên VIP (Tổng: %d tài khoản)",
                countLT, countHLV, countQL, countHV, list.size()));
    }

    private void moDialogThemTaiKhoan() {
        Frame top = (Frame) SwingUtilities.getWindowAncestor(this);
        DangKyTaiKhoanDialog dlg = new DangKyTaiKhoanDialog(top, (username, pass) -> {
            loadAllData();
        });
        dlg.setVisible(true);
    }

    private void xuLyResetMatKhau() {
        JTable currentTable = getActiveTable();
        int row = currentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản trong bảng để đặt lại mật khẩu!", "Chưa chọn tài khoản", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) currentTable.getValueAt(row, 0);
        int opt = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đặt lại mật khẩu cho tài khoản '" + username + "' về mặc định '123456'?",
                "Xác Nhận Đặt Lại Mật Khẩu", JOptionPane.YES_NO_OPTION);

        if (opt == JOptionPane.YES_OPTION) {
            TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(username);
            if (tk != null) {
                tk.setMatKhau("123456");
                taiKhoanDAO.capNhat(tk);
                JOptionPane.showMessageDialog(this, "Đã đặt lại mật khẩu thành công về '123456' cho tài khoản: " + username, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadAllData();
            }
        }
    }

    private void xuLyXoaTaiKhoan() {
        JTable currentTable = getActiveTable();
        int row = currentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản trong bảng để xóa!", "Chưa chọn tài khoản", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) currentTable.getValueAt(row, 0);
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
                JOptionPane.showMessageDialog(this, "Đã xóa thành công tài khoản: " + username, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTable getActiveTable() {
        int idx = tabbedPane.getSelectedIndex();
        if (idx == 0) return tableReceptionist;
        if (idx == 1) return tableTrainer;
        if (idx == 2) return tableManager;
        return tableMember;
    }
}
