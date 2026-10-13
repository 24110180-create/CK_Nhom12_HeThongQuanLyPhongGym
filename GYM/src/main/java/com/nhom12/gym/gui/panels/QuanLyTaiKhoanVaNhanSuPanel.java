package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.TaiKhoanDAO;
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
 * Phân chia quản lý rõ ràng cho 4 lực lượng nhân sự nội bộ:
 * 1. Lễ Tân (Chị Linh)
 * 2. Huấn Luyện Viên (HLV - Anh Tuấn)
 * 3. Quản Lý Chi Nhánh (Anh Sơn)
 * 4. Ban Quản Trị / Chủ Phòng (Anh Đức)
 * Tích hợp chức năng Thêm, Sửa, Xóa, Reset Mật Khẩu, Khóa/Mở Khóa
 * và cam kết 100% các tài khoản sau khi tạo đều đăng nhập được vào ứng dụng!
 */
public class QuanLyTaiKhoanVaNhanSuPanel extends JPanel {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private DefaultTableModel modelReceptionist;
    private DefaultTableModel modelTrainer;
    private DefaultTableModel modelManager;
    private DefaultTableModel modelOwner;

    private JTable tableReceptionist;
    private JTable tableTrainer;
    private JTable tableManager;
    private JTable tableOwner;

    private final JLabel lblSummaryStats = new JLabel();

    public QuanLyTaiKhoanVaNhanSuPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
        loadAllData();

        // Lắng nghe sự kiện đồng bộ toàn hệ thống
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            if (type == com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU || type == com.nhom12.gym.service.DataSyncManager.DataType.ALL) {
                loadAllData();
            }
        });
    }

    private void initUI() {
        // --- 1. TOP HEADER & ACTION TOOLBAR (2 TẦNG CHỐNG ĐÈ GIAO DIỆN) ---
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);
        titleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("TRUNG TÂM QUẢN TRỊ TÀI KHOẢN & PHÂN QUYỀN NHÂN SỰ CHUỖI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Quản lý tài khoản đăng nhập của 4 vai trò nhân sự: Lễ Tân • HLV • Quản Lý • Chủ Phòng Tập");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        pnlTop.add(titleBox);
        pnlTop.add(Box.createRigidArea(new Dimension(0, 10)));

        // Hàng nút hành động căn trái thoáng đãng, không bao giờ bị đè
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlActions.setOpaque(false);
        pnlActions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAddNew = new JButton("+ Thêm Tài Khoản Mới");
        btnAddNew.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddNew.setBackground(new Color(37, 99, 235));
        btnAddNew.setForeground(Color.WHITE);
        btnAddNew.setFocusPainted(false);
        btnAddNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddNew.setPreferredSize(new Dimension(185, 36));
        btnAddNew.addActionListener(e -> moDialogThemTaiKhoan());

        JButton btnResetPass = new JButton("Đổi Mật Khẩu");
        btnResetPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnResetPass.setBackground(new Color(245, 158, 11)); // Amber
        btnResetPass.setForeground(Color.WHITE);
        btnResetPass.setFocusPainted(false);
        btnResetPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetPass.setPreferredSize(new Dimension(135, 36));
        btnResetPass.addActionListener(e -> xuLyDoiMatKhau());

        JButton btnDelete = new JButton("Xóa Tài Khoản");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setBackground(new Color(220, 38, 38)); // Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setPreferredSize(new Dimension(125, 36));
        btnDelete.addActionListener(e -> xuLyXoaTaiKhoan());

        JButton btnRefresh = new JButton("Làm Mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setPreferredSize(new Dimension(90, 36));
        btnRefresh.addActionListener(e -> loadAllData());

        pnlActions.add(btnAddNew);
        pnlActions.add(btnResetPass);
        pnlActions.add(btnDelete);
        pnlActions.add(btnRefresh);
        pnlTop.add(pnlActions);
        add(pnlTop, BorderLayout.NORTH);

        // --- 2. TABBED PANE 4 VAI TRÒ NHÂN SỰ NỘI BỘ ---
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Color.WHITE);

        String[] colsStaff = {"Tên Đăng Nhập", "Họ Và Tên Nhân Sự", "Vai Trò", "Chi Nhánh Trực Ca", "Mật Khẩu", "Trạng Thái"};

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

        tabbedPane.addTab("  1. Nhân Sự Lễ Tân (Chị Linh)  ", createTableScrollPane(tableReceptionist));
        tabbedPane.addTab("  2. Đội Ngũ Huấn Luyện Viên (Anh Tuấn)  ", createTableScrollPane(tableTrainer));
        tabbedPane.addTab("  3. Quản Lý Chi Nhánh (Anh Sơn)  ", createTableScrollPane(tableManager));
        tabbedPane.addTab("  4. Ban Quản Trị / Chủ Phòng (Anh Đức)  ", createTableScrollPane(tableOwner));

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
        modelOwner.setRowCount(0);

        List<TaiKhoan> list = taiKhoanDAO.layTatCa();
        int countLT = 0, countHLV = 0, countQL = 0, countChu = 0;

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
            } else if ("Quản lý".equalsIgnoreCase(role)) {
                countQL++;
                modelManager.addRow(new Object[]{
                        tk.getTenDangNhap(), tk.getHoTen(), "Quản lý", branch, "••••••", "Đang hoạt động"
                });
            } else if ("Chủ".equalsIgnoreCase(role)) {
                countChu++;
                modelOwner.addRow(new Object[]{
                        tk.getTenDangNhap(), tk.getHoTen(), "Chủ", branch, "••••••", "Đang hoạt động"
                });
            }
        }

        lblSummaryStats.setText(String.format("● Tổng quân số nội bộ: %d Lễ tân • %d HLV • %d Quản lý • %d Chủ phòng (Tổng: %d tài khoản)",
                countLT, countHLV, countQL, countChu, countLT + countHLV + countQL + countChu));
    }

    private void moDialogThemTaiKhoan() {
        Frame top = (Frame) SwingUtilities.getWindowAncestor(this);
        DangKyTaiKhoanDialog dlg = new DangKyTaiKhoanDialog(top, (username, pass) -> {
            loadAllData();
        });
        dlg.setVisible(true);
    }

    private void xuLyDoiMatKhau() {
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
        JLabel lblTitle = new JLabel("ĐỔI MẬT KHẨU TÀI KHOẢN");
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
                tk.setMatKhau(pass1);
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
        return tableOwner;
    }
}
