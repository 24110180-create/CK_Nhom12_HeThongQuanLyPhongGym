package com.nhom12.gym.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class QuanLyGoiTapPanel extends JPanel {
    private JTextField txtMaGoi;
    private JTextField txtTenGoi;
    private JTextField txtGiaTien;
    private JTextField txtThoiHan;
    private JComboBox<String> cbLoaiGoi;
    private JTable table;
    private DefaultTableModel model;
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    public QuanLyGoiTapPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("QUẢN LÝ GÓI TẬP & BẢNG GIÁ DỊCH VỤ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        add(lblTitle, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(5);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // --- BÊN TRÁI: FORM NHẬP LIỆU GÓI TẬP ---
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        TitledBorder borderLeft = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Cấu Hình Thông Tin Gói Tập "
        );
        borderLeft.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        borderLeft.setTitleColor(new Color(37, 99, 235));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0), borderLeft));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 13);

        gbc.gridx = 0; gbc.gridy = 0;
        leftPanel.add(new JLabel("Mã Gói Tập:"), gbc);
        gbc.gridy = 1;
        txtMaGoi = new JTextField("GT006");
        txtMaGoi.setFont(fontInput);
        leftPanel.add(txtMaGoi, gbc);

        gbc.gridy = 2;
        leftPanel.add(new JLabel("Tên Gói Tập:"), gbc);
        gbc.gridy = 3;
        txtTenGoi = new JTextField();
        txtTenGoi.setFont(fontInput);
        leftPanel.add(txtTenGoi, gbc);

        gbc.gridy = 4;
        leftPanel.add(new JLabel("Phân Loại Dịch Vụ:"), gbc);
        gbc.gridy = 5;
        cbLoaiGoi = new JComboBox<>(new String[]{"Gói Cơ Bản (Standard)", "Gói VIP (Có Sauna/Bể bơi)", "Gói PT (Kèm Huấn luyện viên)", "Gói Yoga / GroupX"});
        cbLoaiGoi.setFont(fontInput);
        leftPanel.add(cbLoaiGoi, gbc);

        gbc.gridy = 6;
        leftPanel.add(new JLabel("Giá Niêm Yết (VNĐ):"), gbc);
        gbc.gridy = 7;
        txtGiaTien = new JTextField();
        txtGiaTien.setFont(fontInput);
        leftPanel.add(txtGiaTien, gbc);

        gbc.gridy = 8;
        leftPanel.add(new JLabel("Thời Hạn Sử Dụng:"), gbc);
        gbc.gridy = 9;
        txtThoiHan = new JTextField("1 Tháng");
        txtThoiHan.setFont(fontInput);
        leftPanel.add(txtThoiHan, gbc);

        // Nút chức năng Thêm, Sửa, Xóa, Làm mới
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setOpaque(false);

        JButton btnThem = createBtn("Thêm Mới", new Color(16, 185, 129));
        JButton btnSua = createBtn("Cập Nhật", new Color(37, 99, 235));
        JButton btnXoa = createBtn("Xóa Gói", new Color(239, 68, 68));
        JButton btnLamMoi = createBtn("Làm Mới", new Color(100, 116, 139));

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        gbc.gridy = 10;
        gbc.insets = new Insets(18, 10, 10, 10);
        leftPanel.add(buttonPanel, gbc);

        gbc.gridy = 11;
        gbc.weighty = 1.0;
        leftPanel.add(Box.createVerticalGlue(), gbc);
        splitPane.setLeftComponent(leftPanel);

        // --- BÊN PHẢI: BẢNG DANH SÁCH GÓI TẬP ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Danh Sách Gói Tập Đang Mở Bán ",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topBar.setOpaque(false);
        JButton btnExport = new JButton("📊 Xuất Bảng Giá Excel (.csv)");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExport.setBackground(new Color(241, 245, 249));
        btnExport.setForeground(new Color(15, 118, 110));
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(e -> com.nhom12.gym.util.ExcelExportHelper.exportTableToExcel(this, table, "BangGia_GoiTap_FitLife"));
        topBar.add(btnExport);
        rightPanel.add(topBar, BorderLayout.NORTH);

        String[] columns = {"Mã Gói", "Tên Gói Tập", "Phân Loại", "Giá Tiền", "Thời Hạn"};
        Object[][] data = {
                {"GT001", "Gói Trải Nghiệm", "Gói Cơ Bản (Standard)", "350,000 đ", "1 Tháng"},
                {"GT002", "Gói Sinh Viên", "Gói Cơ Bản (Standard)", "750,000 đ", "3 Tháng"},
                {"VIP01", "VIP Thể Lực", "Gói VIP (Có Sauna/Bể bơi)", "2,500,000 đ", "6 Tháng"},
                {"VIP02", "VIP Hoàng Gia", "Gói VIP (Có Sauna/Bể bơi)", "4,500,000 đ", "12 Tháng"},
                {"PT001", "PT 1 Kèm 1 Chuyên Sâu", "Gói PT (Kèm Huấn luyện viên)", "5,000,000 đ", "1 Tháng"}
        };

        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);

        // Click bảng để điền dữ liệu lên form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaGoi.setText(model.getValueAt(row, 0).toString());
                txtTenGoi.setText(model.getValueAt(row, 1).toString());
                cbLoaiGoi.setSelectedItem(model.getValueAt(row, 2).toString());
                txtGiaTien.setText(model.getValueAt(row, 3).toString().replace(" đ", "").replace(",", "").trim());
                txtThoiHan.setText(model.getValueAt(row, 4).toString());
            }
        });

        // 1. THÊM MỚI GÓI TẬP
        btnThem.addActionListener(e -> {
            String ma = txtMaGoi.getText().trim();
            String ten = txtTenGoi.getText().trim();
            String loai = cbLoaiGoi.getSelectedItem().toString();
            String giaStr = txtGiaTien.getText().trim();
            String thoiHan = txtThoiHan.getText().trim();

            if (ma.isEmpty() || ten.isEmpty() || giaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã gói, Tên gói và Giá tiền!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra trùng mã
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString().equalsIgnoreCase(ma)) {
                    JOptionPane.showMessageDialog(this, "Mã gói tập '" + ma + "' đã tồn tại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            String formattedGia;
            try {
                double gia = Double.parseDouble(giaStr.replace(",", ""));
                formattedGia = df.format(gia);
            } catch (Exception ex) {
                formattedGia = giaStr + " đ";
            }

            model.addRow(new Object[]{ma, ten, loai, formattedGia, thoiHan});
            JOptionPane.showMessageDialog(this, "Đã thêm mới gói tập '" + ten + "' thành công!");
            btnLamMoi.doClick();
        });

        // 2. CẬP NHẬT GÓI TẬP
        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một gói tập trên bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ma = txtMaGoi.getText().trim();
            String ten = txtTenGoi.getText().trim();
            String loai = cbLoaiGoi.getSelectedItem().toString();
            String giaStr = txtGiaTien.getText().trim();
            String thoiHan = txtThoiHan.getText().trim();

            String formattedGia;
            try {
                double gia = Double.parseDouble(giaStr.replace(",", ""));
                formattedGia = df.format(gia);
            } catch (Exception ex) {
                formattedGia = giaStr + " đ";
            }

            model.setValueAt(ma, row, 0);
            model.setValueAt(ten, row, 1);
            model.setValueAt(loai, row, 2);
            model.setValueAt(formattedGia, row, 3);
            model.setValueAt(thoiHan, row, 4);

            JOptionPane.showMessageDialog(this, "Đã cập nhật gói tập thành công!");
        });

        // 3. XÓA GÓI TẬP
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn gói tập cần xóa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ten = model.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa gói tập '" + ten + "'?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                btnLamMoi.doClick();
                JOptionPane.showMessageDialog(this, "Đã xóa gói tập thành công!");
            }
        });

        // 4. LÀM MỚI FORM
        btnLamMoi.addActionListener(e -> {
            txtMaGoi.setText("GT" + String.format("%03d", model.getRowCount() + 1));
            txtTenGoi.setText("");
            txtGiaTien.setText("");
            txtThoiHan.setText("1 Tháng");
            cbLoaiGoi.setSelectedIndex(0);
            table.clearSelection();
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    private JButton createBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
