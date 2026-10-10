package com.nhom12.gym.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QuanLyKhoHangPanel extends JPanel {
    private JTextField txtMaSP;
    private JTextField txtTenSP;
    private JComboBox<String> cbDanhMuc;
    private JTextField txtSoLuong;
    private JTextField txtGiaBan;
    private JTable table;
    private DefaultTableModel model;

    public QuanLyKhoHangPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("QUẢN LÝ KHO HÀNG & SẢN PHẨM QUẦY POS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        add(lblTitle, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(5);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // --- BÊN TRÁI: FORM NHẬP XUẤT KHO ---
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        TitledBorder borderLeft = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Thông Tin Hàng Hóa Nhập Kho "
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
        leftPanel.add(new JLabel("Mã Sản Phẩm:"), gbc);
        gbc.gridy = 1;
        txtMaSP = new JTextField("SP011");
        txtMaSP.setFont(fontInput);
        leftPanel.add(txtMaSP, gbc);

        gbc.gridy = 2;
        leftPanel.add(new JLabel("Tên Sản Phẩm:"), gbc);
        gbc.gridy = 3;
        txtTenSP = new JTextField();
        txtTenSP.setFont(fontInput);
        leftPanel.add(txtTenSP, gbc);

        gbc.gridy = 4;
        leftPanel.add(new JLabel("Danh Mục Hàng Hóa:"), gbc);
        gbc.gridy = 5;
        cbDanhMuc = new JComboBox<>(new String[]{"Nước uống giải khát", "Thực phẩm bổ sung dinh dưỡng", "Phụ kiện tập luyện", "Khác"});
        cbDanhMuc.setFont(fontInput);
        leftPanel.add(cbDanhMuc, gbc);

        gbc.gridy = 6;
        leftPanel.add(new JLabel("Số Lượng Tồn Kho:"), gbc);
        gbc.gridy = 7;
        txtSoLuong = new JTextField("50");
        txtSoLuong.setFont(fontInput);
        leftPanel.add(txtSoLuong, gbc);

        gbc.gridy = 8;
        leftPanel.add(new JLabel("Giá Bán Lẻ (VNĐ):"), gbc);
        gbc.gridy = 9;
        txtGiaBan = new JTextField("25000");
        txtGiaBan.setFont(fontInput);
        leftPanel.add(txtGiaBan, gbc);

        // Nút CRUD
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        JButton btnAdd = new JButton("Nhập Kho (Thêm)");
        btnAdd.setFont(fontLabel);
        btnAdd.setBackground(new Color(16, 185, 129));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnEdit = new JButton("Cập Nhật");
        btnEdit.setFont(fontLabel);
        btnEdit.setBackground(new Color(37, 99, 235));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDelete = new JButton("Xóa Hàng");
        btnDelete.setFont(fontLabel);
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnClear = new JButton("Làm Mới");
        btnClear.setFont(fontLabel);
        btnClear.setBackground(new Color(100, 116, 139));
        btnClear.setForeground(Color.WHITE);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gbc.gridy = 10;
        gbc.insets = new Insets(18, 10, 10, 10);
        leftPanel.add(btnPanel, gbc);

        gbc.gridy = 11;
        gbc.weighty = 1.0;
        leftPanel.add(Box.createVerticalGlue(), gbc);
        splitPane.setLeftComponent(leftPanel);

        // --- BÊN PHẢI: BẢNG TỒN KHO ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Tình Trạng Tồn Kho Thực Tế ",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        String[] cols = {"Mã SP", "Tên Sản Phẩm", "Danh Mục", "Tồn Kho", "Giá Bán"};
        Object[][] data = {
                {"SP001", "Nước khoáng Aquafina", "Nước uống giải khát", "150", "10,000 đ"},
                {"SP002", "Nước tăng lực Monster", "Nước uống giải khát", "45", "45,000 đ"},
                {"SP005", "Sữa pha sẵn Whey Protein", "Thực phẩm bổ sung dinh dưỡng", "28", "65,000 đ"},
                {"SP010", "Khăn tập Gym Microfiber", "Phụ kiện tập luyện", "30", "35,000 đ"},
                {"SP011", "Găng tay tập tạ da bò", "Phụ kiện tập luyện", "15", "120,000 đ"},
                {"SP012", "Chuối tươi nạp năng lượng", "Nước uống giải khát", "60", "15,000 đ"}
        };

        model = new DefaultTableModel(data, cols) {
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

        // Click bảng điền form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaSP.setText(model.getValueAt(row, 0).toString());
                txtTenSP.setText(model.getValueAt(row, 1).toString());
                cbDanhMuc.setSelectedItem(model.getValueAt(row, 2).toString());
                txtSoLuong.setText(model.getValueAt(row, 3).toString());
                txtGiaBan.setText(model.getValueAt(row, 4).toString().replace(" đ", "").replace(",", "").trim());
            }
        });

        // 1. NHẬP KHO (THÊM MỚI)
        btnAdd.addActionListener(e -> {
            String ma = txtMaSP.getText().trim();
            String ten = txtTenSP.getText().trim();
            String dm = cbDanhMuc.getSelectedItem().toString();
            String sl = txtSoLuong.getText().trim();
            String gia = txtGiaBan.getText().trim();

            if (ma.isEmpty() || ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã và Tên sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString().equalsIgnoreCase(ma)) {
                    JOptionPane.showMessageDialog(this, "Mã sản phẩm '" + ma + "' đã tồn tại trong kho!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            String giaHienThi = gia.contains("đ") ? gia : gia + " đ";
            model.addRow(new Object[]{ma, ten, dm, sl, giaHienThi});
            JOptionPane.showMessageDialog(this, "Đã nhập kho sản phẩm '" + ten + "' thành công!");
            btnClear.doClick();
        });

        // 2. CẬP NHẬT SẢN PHẨM
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một mặt hàng trên bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ma = txtMaSP.getText().trim();
            String ten = txtTenSP.getText().trim();
            String dm = cbDanhMuc.getSelectedItem().toString();
            String sl = txtSoLuong.getText().trim();
            String gia = txtGiaBan.getText().trim();
            String giaHienThi = gia.contains("đ") ? gia : gia + " đ";

            model.setValueAt(ma, row, 0);
            model.setValueAt(ten, row, 1);
            model.setValueAt(dm, row, 2);
            model.setValueAt(sl, row, 3);
            model.setValueAt(giaHienThi, row, 4);

            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin sản phẩm kho thành công!");
        });

        // 3. XÓA SẢN PHẨM
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ten = model.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa mặt hàng '" + ten + "' khỏi kho?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                btnClear.doClick();
                JOptionPane.showMessageDialog(this, "Đã xóa mặt hàng thành công!");
            }
        });

        // 4. LÀM MỚI FORM
        btnClear.addActionListener(e -> {
            txtMaSP.setText("SP" + String.format("%03d", model.getRowCount() + 1));
            txtTenSP.setText("");
            txtSoLuong.setText("50");
            txtGiaBan.setText("25000");
            cbDanhMuc.setSelectedIndex(0);
            table.clearSelection();
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
    }
}
