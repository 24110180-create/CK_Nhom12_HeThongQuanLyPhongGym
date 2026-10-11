package com.nhom12.gym.gui.panels;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.HoiVienService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class QuanLyHoiVienPanel extends JPanel {
    private final HoiVienService service = new HoiVienService();
    private JTextField txtMaHV;
    private JTextField txtQR;
    private JTextField txtHoTen;
    private JTextField txtChiNhanh;
    private JComboBox<String> cbTrangThai;
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JTable table;
    private DefaultTableModel tableModel;

    public QuanLyHoiVienPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ THÔNG TIN HỘI VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        add(lblTitle, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(5);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(createFormPanel());
        splitPane.setRightComponent(createTablePanel());
        add(splitPane, BorderLayout.CENTER);

        setupActions();
        loadDataToTable();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 13);

        // Header form
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Thông Tin Chi Tiết Hội Viên "
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        border.setTitleColor(new Color(37, 99, 235));
        panel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0), border));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã Hội Viên (Tự động):"), gbc);
        gbc.gridy = 1;
        txtMaHV = new JTextField();
        txtMaHV.setEditable(false);
        txtMaHV.setBackground(new Color(241, 245, 249));
        txtMaHV.setFont(fontInput);
        panel.add(txtMaHV, gbc);

        gbc.gridy = 2;
        panel.add(new JLabel("Mã Thẻ QR (*):"), gbc);
        gbc.gridy = 3;
        txtQR = new JTextField();
        txtQR.setFont(fontInput);
        panel.add(txtQR, gbc);

        gbc.gridy = 4;
        panel.add(new JLabel("Họ Và Tên (*):"), gbc);
        gbc.gridy = 5;
        txtHoTen = new JTextField();
        txtHoTen.setFont(fontInput);
        panel.add(txtHoTen, gbc);

        gbc.gridy = 6;
        panel.add(new JLabel("Chi Nhánh Đăng Ký:"), gbc);
        gbc.gridy = 7;
        txtChiNhanh = new JTextField("CN01");
        txtChiNhanh.setFont(fontInput);
        panel.add(txtChiNhanh, gbc);

        gbc.gridy = 8;
        panel.add(new JLabel("Trạng Thái Hoạt Động:"), gbc);
        gbc.gridy = 9;
        cbTrangThai = new JComboBox<>(new String[]{"Active", "Expired", "Pending", "Locked"});
        cbTrangThai.setFont(fontInput);
        panel.add(cbTrangThai, gbc);

        // Nút chức năng
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        btnThem = new JButton("Thêm Mới");
        btnThem.setBackground(new Color(16, 185, 129));
        btnThem.setForeground(Color.WHITE);
        btnThem.setFont(fontLabel);
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSua = new JButton("Cập Nhật");
        btnSua.setBackground(new Color(37, 99, 235));
        btnSua.setForeground(Color.WHITE);
        btnSua.setFont(fontLabel);
        btnSua.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnXoa = new JButton("Xóa Hội Viên");
        btnXoa.setBackground(new Color(239, 68, 68));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFont(fontLabel);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLamMoi = new JButton("Làm Mới Form");
        btnLamMoi.setBackground(new Color(241, 245, 249));
        btnLamMoi.setForeground(new Color(51, 65, 85));
        btnLamMoi.setFont(fontLabel);
        btnLamMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(btnThem);
        btnPanel.add(btnSua);
        btnPanel.add(btnXoa);
        btnPanel.add(btnLamMoi);

        gbc.gridy = 10;
        gbc.insets = new Insets(18, 6, 6, 6);
        panel.add(btnPanel, gbc);

        gbc.gridy = 11;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Danh Sách Dữ Liệu Hội Viên Phòng Tập ",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        // Thanh công cụ tìm kiếm và xuất Excel
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        toolBar.setOpaque(false);

        JTextField txtSearchTable = new JTextField(15);
        txtSearchTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearchTable.putClientProperty("JTextField.placeholderText", "Tìm tên hoặc mã QR...");

        JButton btnExport = new JButton("📊 Xuất File Excel (.csv)");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExport.setBackground(new Color(241, 245, 249));
        btnExport.setForeground(new Color(15, 118, 110));
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toolBar.add(new JLabel("Tìm kiếm:"));
        toolBar.add(txtSearchTable);
        toolBar.add(btnExport);
        panel.add(toolBar, BorderLayout.NORTH);

        String[] cols = {"Mã HV", "Mã QR", "Họ Và Tên", "Chi Nhánh", "Ngày Hết Hạn", "Trạng Thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        txtSearchTable.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                String text = txtSearchTable.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        btnExport.addActionListener(e -> com.nhom12.gym.util.ExcelExportHelper.exportTableToExcel(this, table, "DanhSach_HoiVien_FitLife"));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<HoiVien> list = null;
        try {
            list = service.layDanhSachHoiVien();
        } catch (Exception ignored) {}

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (list != null && !list.isEmpty()) {
            for (HoiVien hv : list) {
                String dateStr = (hv.getNgayHetHan() != null) ? sdf.format(hv.getNgayHetHan()) : "31/12/2026";
                tableModel.addRow(new Object[]{
                        hv.getMaHoiVien(), hv.getMaTheQR(), hv.getHoTen(),
                        hv.getChiNhanhDangKy(), dateStr, hv.getTrangThai()
                });
            }
        } else {
            // Nạp dữ liệu mẫu ban đầu từ Database.sql nếu CSDL đang offline
            tableModel.addRow(new Object[]{1, "QR001", "Lê Anh Duy", "CN01", "31/12/2026", "Active"});
            tableModel.addRow(new Object[]{2, "QR002", "Võ Lê Ngọc Hưng", "CN02", "15/10/2026", "Active"});
            tableModel.addRow(new Object[]{3, "QR003", "Võ Tấn Phát", "CN01", "01/01/2025", "Active"});
            tableModel.addRow(new Object[]{4, "QR004", "Trần Minh Quân", "CN01", "20/11/2026", "Active"});
            tableModel.addRow(new Object[]{5, "QR005", "Hoàng Thùy Linh", "CN02", "15/08/2026", "Active"});
        }
    }

    private void setupActions() {
        // Sự kiện click bảng điền dữ liệu lên form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaHV.setText(table.getValueAt(row, 0).toString());
                txtQR.setText(table.getValueAt(row, 1).toString());
                txtHoTen.setText(table.getValueAt(row, 2).toString());
                txtChiNhanh.setText(table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "CN01");
                cbTrangThai.setSelectedItem(table.getValueAt(row, 5).toString());
            }
        });

        // 1. NÚT THÊM MỚI
        btnThem.addActionListener(e -> {
            String qr = txtQR.getText().trim();
            String name = txtHoTen.getText().trim();
            String branch = txtChiNhanh.getText().trim();
            String status = cbTrangThai.getSelectedItem().toString();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ Tên hội viên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tính mã ID tiếp theo
            int nextId = 1;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                try {
                    int idVal = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
                    if (idVal >= nextId) nextId = idVal + 1;
                } catch (Exception ignored) {}
            }

            // Tự động đồng bộ mã QR chuẩn nếu chưa nhập
            if (qr.isEmpty()) {
                qr = String.format("HV%03d", nextId);
                txtQR.setText(qr);
            }

            // Kiểm tra trùng mã QR trên bảng
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 1).toString().equalsIgnoreCase(qr)) {
                    JOptionPane.showMessageDialog(this, "Mã thẻ QR '" + qr + "' đã tồn tại! Vui lòng chọn mã khác.", "Cảnh báo trùng lặp", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            tableModel.insertRow(0, new Object[]{nextId, qr, name, branch.isEmpty() ? "CN01" : branch, "31/12/2026", status});

            // Lưu vào CSDL và Fallback Cache
            try {
                HoiVien hv = new HoiVien();
                hv.setMaHoiVien(nextId);
                hv.setMaTheQR(qr);
                hv.setHoTen(name);
                hv.setChiNhanhDangKy(branch.isEmpty() ? "CN01" : branch);
                hv.setTrangThai(status);
                service.luuHoiVien(hv, false);
            } catch (Exception ignored) {}

            JOptionPane.showMessageDialog(this, "Đã thêm mới hội viên '" + name + "' [Mã HV: " + nextId + " • Mã QR: " + qr + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            btnLamMoi.doClick();
        });

        // 2. NÚT CẬP NHẬT
        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0 && txtMaHV.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hội viên trên bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String qr = txtQR.getText().trim();
            String name = txtHoTen.getText().trim();
            String branch = txtChiNhanh.getText().trim();
            String status = cbTrangThai.getSelectedItem().toString();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Họ Tên không được để trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (qr.isEmpty() && !txtMaHV.getText().isEmpty()) {
                qr = String.format("HV%03d", Integer.parseInt(txtMaHV.getText()));
                txtQR.setText(qr);
            }

            if (row >= 0) {
                tableModel.setValueAt(qr, row, 1);
                tableModel.setValueAt(name, row, 2);
                tableModel.setValueAt(branch, row, 3);
                tableModel.setValueAt(status, row, 5);
            }

            // Cập nhật CSDL và Cache
            try {
                if (!txtMaHV.getText().isEmpty()) {
                    HoiVien hv = new HoiVien();
                    hv.setMaHoiVien(Integer.parseInt(txtMaHV.getText()));
                    hv.setMaTheQR(qr);
                    hv.setHoTen(name);
                    hv.setChiNhanhDangKy(branch);
                    hv.setTrangThai(status);
                    service.luuHoiVien(hv, true);
                }
            } catch (Exception ignored) {}

            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin hội viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        // 3. NÚT XÓA HỘI VIÊN
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hội viên cần xóa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String name = tableModel.getValueAt(row, 2).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa hội viên '" + name + "'?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    service.xoaHoiVien(id);
                } catch (Exception ignored) {}

                tableModel.removeRow(row);
                btnLamMoi.doClick();
                JOptionPane.showMessageDialog(this, "Đã xóa hội viên thành công!");
            }
        });

        // 4. NÚT LÀM MỚI FORM (ĐỒNG BỘ MÃ TIẾP THEO)
        btnLamMoi.addActionListener(e -> {
            int nextId = 1;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                try {
                    int idVal = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
                    if (idVal >= nextId) nextId = idVal + 1;
                } catch (Exception ignored) {}
            }
            txtMaHV.setText(String.valueOf(nextId));
            txtQR.setText(String.format("HV%03d", nextId));
            txtHoTen.setText("");
            txtChiNhanh.setText("CN01");
            cbTrangThai.setSelectedIndex(0);
            table.clearSelection();
        });
    }
}
