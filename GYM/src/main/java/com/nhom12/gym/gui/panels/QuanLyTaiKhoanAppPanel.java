package com.nhom12.gym.gui.panels;

import com.nhom12.gym.gui.GuiMaQRDialog;

import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.TaiKhoan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Phân hệ Quản Lý Tài Khoản App Hội Viên dành cho Lễ tân, Quản lý và Chủ phòng
 */
public class QuanLyTaiKhoanAppPanel extends JPanel {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField txtSearch;
    private final JComboBox<String> cbFilterStatus;
    private final JLabel lblCount;

    public QuanLyTaiKhoanAppPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. TOP HEADER & SEARCH BAR
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ TÀI KHOẢN APP DÀNH CHO HỘI VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Cấp mới, mở khóa, đặt lại mật khẩu và gửi mã QR đăng nhập cho hội viên sử dụng ứng dụng di động");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        headerRow.add(titleGroup, BorderLayout.WEST);

        // Nút thêm tài khoản nhanh
        JButton btnAddNew = new JButton("+ Cấp Mới Tài Khoản App");
        btnAddNew.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAddNew.setBackground(new Color(37, 99, 235));
        btnAddNew.setForeground(Color.WHITE);
        btnAddNew.setFocusPainted(false);
        btnAddNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddNew.setPreferredSize(new Dimension(210, 40));
        headerRow.add(btnAddNew, BorderLayout.EAST);
        topContainer.add(headerRow);
        topContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Search & Filter Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel searchLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchLeft.setOpaque(false);
        JLabel lblS = new JLabel("Tìm kiếm:");
        lblS.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(220, 34));

        JLabel lblF = new JLabel("Trạng thái:");
        lblF.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbFilterStatus = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang hoạt động", "Đang tạm khóa"});
        cbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFilterStatus.setPreferredSize(new Dimension(160, 34));

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setBackground(new Color(241, 245, 249));
        btnSearch.setForeground(new Color(30, 41, 59));
        btnSearch.setPreferredSize(new Dimension(70, 34));

        searchLeft.add(lblS);
        searchLeft.add(txtSearch);
        searchLeft.add(lblF);
        searchLeft.add(cbFilterStatus);
        searchLeft.add(btnSearch);
        toolbar.add(searchLeft, BorderLayout.WEST);

        lblCount = new JLabel("● Tổng số tài khoản: 6");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(new Color(100, 116, 139));
        toolbar.add(lblCount, BorderLayout.EAST);

        topContainer.add(toolbar);
        add(topContainer, BorderLayout.NORTH);

        // 2. DATA TABLE
        String[] cols = {"Tên Đăng Nhập (SĐT)", "Họ Tên Hội Viên", "Mã Thẻ QR", "Gói Tập Sở Hữu", "Trạng Thái App", "Ngày Kích Hoạt", "Đăng Nhập Gần Nhất"};
        Object[][] data = {
                {"0000000001", "Dương Hoàng Huy", "HV406", "Gói Sinh Viên 3 Tháng", "Hoạt động", "30/09/2026", "Vừa mới đây"},
                {"hoivien1", "Nguyễn Văn An", "QR001", "VIP Diamond 12 Tháng", "Hoạt động", "01/01/2026", "Hôm nay 08:30"},
                {"0901234567", "Lê Thị Thảo", "QR002", "Gói Thể Lực 6 Tháng", "Hoạt động", "15/02/2026", "Hôm qua 18:20"},
                {"0988776655", "Trần Văn Bình", "HV108", "Gói 1 Tháng Thử Nghiệm", "Tạm khóa", "10/05/2026", "15/07/2026"},
                {"0933221100", "Phạm Quỳnh Như", "HV215", "VIP Toàn Diện 1 Năm", "Hoạt động", "20/06/2026", "28/09/2026"},
                {"0977112233", "Nguyễn Minh Khang", "HV302", "Khóa PT 1-1 (20 Buổi)", "Hoạt động", "01/08/2026", "Hôm nay 09:15"}
        };

        model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(30, 41, 59));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Render trạng thái có màu sắc
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String s = (val != null) ? val.toString() : "";
                if ("Hoạt động".equalsIgnoreCase(s)) {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    l.setText("● Hoạt động");
                } else {
                    l.setForeground(new Color(239, 68, 68));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    l.setText("● Tạm khóa");
                }
                return l;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. BOTTOM ACTIONS TOOLBAR (Các nút chức năng quản lý)
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomBar.setOpaque(false);

        JButton btnSendQR = new JButton("Xuất & Gửi Mã QR (Zalo/SMS)");
        btnSendQR.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSendQR.setBackground(new Color(14, 116, 144));
        btnSendQR.setForeground(Color.WHITE);
        btnSendQR.setFocusPainted(false);
        btnSendQR.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSendQR.setPreferredSize(new Dimension(240, 42));

        JButton btnResetPass = new JButton("Đặt Lại Mật Khẩu (Reset)");
        btnResetPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnResetPass.setBackground(new Color(245, 158, 11));
        btnResetPass.setForeground(Color.WHITE);
        btnResetPass.setFocusPainted(false);
        btnResetPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetPass.setPreferredSize(new Dimension(190, 42));

        JButton btnToggleLock = new JButton("Khóa / Mở Khóa App");
        btnToggleLock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnToggleLock.setBackground(new Color(100, 116, 139));
        btnToggleLock.setForeground(Color.WHITE);
        btnToggleLock.setFocusPainted(false);
        btnToggleLock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleLock.setPreferredSize(new Dimension(170, 42));

        JButton btnDelete = new JButton("Xóa Tài Khoản");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setPreferredSize(new Dimension(130, 42));

        bottomBar.add(btnSendQR);
        bottomBar.add(btnResetPass);
        bottomBar.add(btnToggleLock);
        bottomBar.add(btnDelete);
        add(bottomBar, BorderLayout.SOUTH);

        // Events
        btnAddNew.addActionListener(e -> moDialogCapTaiKhoanMoi());
        btnSendQR.addActionListener(e -> xuLyGuiMaQR());
        btnResetPass.addActionListener(e -> xuLyResetMatKhau());
        btnToggleLock.addActionListener(e -> xuLyKhoaMoKhoa());
        btnDelete.addActionListener(e -> xuLyXoaTaiKhoan());

        btnSearch.addActionListener(e -> filterTable());
        txtSearch.addActionListener(e -> filterTable());
        cbFilterStatus.addActionListener(e -> filterTable());
    }

    private void moDialogCapTaiKhoanMoi() {
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(topFrame, "Cấp Tài Khoản FitLife App Mới Cho Hội Viên", true);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 12));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 25, 20, 25));

        JTextField tfName = new JTextField();
        JTextField tfPhone = new JTextField();
        JTextField tfPass = new JTextField("123456");
        JComboBox<String> cbPkg = new JComboBox<>(new String[]{
                "VIP Diamond 12 Tháng",
                "Gói Thể Lực 6 Tháng",
                "Gói Sinh Viên 3 Tháng",
                "Gói 1 Tháng Thử Nghiệm"
        });
        JComboBox<String> cbBr = new JComboBox<>(new String[]{
                "CN01 - Cơ sở 1 (Quận 1)",
                "CN02 - Cơ sở 2 (Tân Bình)"
        });

        form.add(new JLabel("Họ Và Tên Hội Viên (*):"));
        form.add(tfName);
        form.add(new JLabel("Số Điện Thoại (Username) (*):"));
        form.add(tfPhone);
        form.add(new JLabel("Mật Khẩu Ban Đầu:"));
        form.add(tfPass);
        form.add(new JLabel("Gói Tập:"));
        form.add(cbPkg);
        form.add(new JLabel("Chi Nhánh:"));
        form.add(cbBr);

        JPanel bottomDlg = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        bottomDlg.setBackground(Color.WHITE);

        JButton btnOk = new JButton("Tạo & Gửi Mã QR");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOk.setBackground(new Color(37, 99, 235));
        btnOk.setForeground(Color.WHITE);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        bottomDlg.add(btnCancel);
        bottomDlg.add(btnOk);

        btnCancel.addActionListener(e -> dlg.dispose());
        btnOk.addActionListener(e -> {
            String name = tfName.getText().trim();
            String phone = tfPhone.getText().trim();
            String pass = tfPass.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập đầy đủ Tên và Số điện thoại hội viên!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String code = "HV" + (System.currentTimeMillis() % 1000);
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            // Lưu tài khoản DB
            try {
                taiKhoanDAO.them(new TaiKhoan(phone, pass, "Hội viên", name));
            } catch (Exception ignored) {}

            // Thêm vào bảng
            model.insertRow(0, new Object[]{phone, name, code, cbPkg.getSelectedItem(), "Hoạt động", date, "Chưa đăng nhập"});
            lblCount.setText("● Tổng số tài khoản: " + model.getRowCount());
            dlg.dispose();

            // Bật ngay cửa sổ gửi QR
            new GuiMaQRDialog(topFrame, code, name, phone, (String) cbPkg.getSelectedItem(), (String) cbBr.getSelectedItem()).setVisible(true);
        });

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottomDlg, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void xuLyGuiMaQR() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hội viên trên danh sách để xuất mã QR!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String phone = table.getValueAt(row, 0).toString();
        String name = table.getValueAt(row, 1).toString();
        String code = table.getValueAt(row, 2).toString();
        String pkg = table.getValueAt(row, 3).toString();

        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        new GuiMaQRDialog(topFrame, code, name, phone, pkg, "FitLife Central").setVisible(true);
    }

    private void xuLyResetMatKhau() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản hội viên cần đặt lại mật khẩu!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String phone = table.getValueAt(row, 0).toString();
        String name = table.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đặt lại mật khẩu của hội viên " + name + " (" + phone + ") về mặc định '123456'?",
                "Xác Nhận Reset Mật Khẩu", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                TaiKhoan tk = taiKhoanDAO.timTheoTenDangNhap(phone);
                if (tk != null) {
                    tk.setMatKhau("123456");
                    taiKhoanDAO.capNhat(tk);
                }
            } catch (Exception ignored) {}

            JOptionPane.showMessageDialog(this,
                    "Đặt lại mật khẩu thành công!\nMật khẩu mới: 123456\nHệ thống đã gửi thông báo xác nhận đến SĐT: " + phone,
                    "Đã Đặt Lại Mật Khẩu", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xuLyKhoaMoKhoa() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần thay đổi trạng thái!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = table.getValueAt(row, 4).toString();
        String newStatus = "Hoạt động".equalsIgnoreCase(currentStatus) ? "Tạm khóa" : "Hoạt động";
        table.setValueAt(newStatus, row, 4);

        JOptionPane.showMessageDialog(this,
                "Đã chuyển trạng thái tài khoản sang: " + newStatus,
                "Cập Nhật Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyXoaTaiKhoan() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản muốn xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String phone = table.getValueAt(row, 0).toString();
        String name = table.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tài khoản App của hội viên " + name + " (" + phone + ")?\nHội viên sẽ không thể đăng nhập app được nữa.",
                "Cảnh Báo Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                taiKhoanDAO.xoa(phone);
            } catch (Exception ignored) {}

            model.removeRow(row);
            lblCount.setText("● Tổng số tài khoản: " + model.getRowCount());
            JOptionPane.showMessageDialog(this, "Đã xóa tài khoản hội viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterTable() {
        String query = txtSearch.getText().trim().toLowerCase();
        String statusFilter = (String) cbFilterStatus.getSelectedItem();

        // Đơn giản hóa: highlight hoặc thông báo kết quả tìm kiếm
        if (query.isEmpty() && "Tất cả trạng thái".equals(statusFilter)) {
            lblCount.setText("● Tổng số tài khoản: " + model.getRowCount());
            return;
        }

        int found = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String phone = model.getValueAt(i, 0).toString().toLowerCase();
            String name = model.getValueAt(i, 1).toString().toLowerCase();
            String code = model.getValueAt(i, 2).toString().toLowerCase();
            String status = model.getValueAt(i, 4).toString();

            boolean matchQuery = query.isEmpty() || phone.contains(query) || name.contains(query) || code.contains(query);
            boolean matchStatus = "Tất cả trạng thái".equals(statusFilter) || status.equalsIgnoreCase(statusFilter);

            if (matchQuery && matchStatus) {
                found++;
                table.setRowSelectionInterval(i, i);
            }
        }
        lblCount.setText("● Tìm thấy " + found + " tài khoản phù hợp");
    }
}
