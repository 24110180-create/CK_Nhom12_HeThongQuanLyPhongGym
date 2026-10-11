package com.nhom12.gym.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class QuanLyKhuyenMaiPanel extends JPanel {
    private JTextField txtCode;
    private JTextField txtDiscount;
    private JTextField txtExpireDate;
    private JTextField txtMaxUsage;
    private JComboBox<String> cbStatus;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnLock;
    private JButton btnDelete;
    private JButton btnClear;

    private JTable table;
    private DefaultTableModel model;

    public QuanLyKhuyenMaiPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ VOUCHER & MÃ KHUYẾN MÃI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(194, 65, 12));
        JLabel lblSubtitle = new JLabel("Tạo mã giảm giá, thiết lập hạn sử dụng và phân bổ chiến dịch marketing");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Panel (Form)
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFormTitle = new JLabel("Chi Tiết Mã Ưu Đãi");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        int row = 0;
        // Code
        gbc.gridx = 0; gbc.gridy = row++;
        JLabel lbl1 = new JLabel("Mã Voucher (*):");
        lbl1.setFont(fontLabel);
        formGrid.add(lbl1, gbc);

        gbc.gridy = row++;
        txtCode = new JTextField();
        txtCode.setFont(fontInput);
        formGrid.add(txtCode, gbc);

        // Discount
        gbc.gridy = row++;
        JLabel lbl2 = new JLabel("Mức Giảm Giá (*):");
        lbl2.setFont(fontLabel);
        formGrid.add(lbl2, gbc);

        gbc.gridy = row++;
        txtDiscount = new JTextField("20%");
        txtDiscount.setFont(fontInput);
        formGrid.add(txtDiscount, gbc);

        // Expire
        gbc.gridy = row++;
        JLabel lbl3 = new JLabel("Ngày Hết Hạn (dd/MM/yyyy):");
        lbl3.setFont(fontLabel);
        formGrid.add(lbl3, gbc);

        gbc.gridy = row++;
        txtExpireDate = new JTextField("31/12/2026");
        txtExpireDate.setFont(fontInput);
        formGrid.add(txtExpireDate, gbc);

        // Max usage
        gbc.gridy = row++;
        JLabel lbl4 = new JLabel("Giới Hạn Lượt Dùng:");
        lbl4.setFont(fontLabel);
        formGrid.add(lbl4, gbc);

        gbc.gridy = row++;
        txtMaxUsage = new JTextField("100");
        txtMaxUsage.setFont(fontInput);
        formGrid.add(txtMaxUsage, gbc);

        // Status
        gbc.gridy = row++;
        JLabel lbl5 = new JLabel("Trạng Thái Voucher:");
        lbl5.setFont(fontLabel);
        formGrid.add(lbl5, gbc);

        gbc.gridy = row++;
        cbStatus = new JComboBox<>(new String[]{"Đang mở", "Tạm khóa", "Hết hạn"});
        cbStatus.setFont(fontInput);
        formGrid.add(cbStatus, gbc);

        gbc.gridy = row++;
        gbc.weighty = 1.0;
        formGrid.add(Box.createVerticalGlue(), gbc);

        leftPanel.add(formGrid, BorderLayout.CENTER);

        // Button Grid
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = new JButton("Phát Hành");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(new Color(16, 185, 129));
        btnAdd.setForeground(Color.WHITE);

        btnUpdate = new JButton("Cập Nhật");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdate.setBackground(new Color(37, 99, 235));
        btnUpdate.setForeground(Color.WHITE);

        btnLock = new JButton("Khóa / Mở");
        btnLock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLock.setBackground(new Color(245, 158, 11));
        btnLock.setForeground(Color.WHITE);

        btnDelete = new JButton("Xóa");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);

        btnClear = new JButton("Làm Mới");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setBackground(new Color(100, 116, 139));
        btnClear.setForeground(Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnLock);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        leftPanel.add(btnPanel, BorderLayout.SOUTH);
        splitPane.setLeftComponent(leftPanel);

        // Right Panel (Table)
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTableTitle = new JLabel("Danh Sách Voucher Hiện Có");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(new Color(30, 41, 59));
        rightPanel.add(lblTableTitle, BorderLayout.NORTH);

        String[] cols = new String[]{"Mã Code", "Giảm Giá", "Hạn Sử Dụng", "Đã Dùng", "Trạng Thái"};
        Object[][] data = new Object[][]{
                {"NEWBIE50", "50%", "Không giới hạn", "45/∞", "Đang mở"},
                {"SUMMER26", "20%", "31/10/2026", "12/100", "Đang mở"},
                {"FLASH100K", "100,000 đ", "15/09/2026", "50/50", "Hết hạn"},
                {"VIPMEMBER", "30%", "31/12/2026", "8/50", "Đang mở"},
                {"TET2027", "25%", "28/02/2027", "0/200", "Tạm khóa"}
        };

        model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(fontInput);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Row Selection -> Populate form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                txtCode.setText(model.getValueAt(r, 0).toString());
                txtCode.setEditable(false);
                txtDiscount.setText(model.getValueAt(r, 1).toString());
                txtExpireDate.setText(model.getValueAt(r, 2).toString());

                String usage = model.getValueAt(r, 3).toString();
                if (usage.contains("/")) {
                    txtMaxUsage.setText(usage.substring(usage.indexOf("/") + 1));
                }
                cbStatus.setSelectedItem(model.getValueAt(r, 4).toString());
            }
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Action Listeners
        btnAdd.addActionListener(e -> actionThem());
        btnUpdate.addActionListener(e -> actionCapNhat());
        btnLock.addActionListener(e -> actionKhoa());
        btnDelete.addActionListener(e -> actionXoa());
        btnClear.addActionListener(e -> actionLamMoi());
    }

    private void actionThem() {
        String code = txtCode.getText().trim().toUpperCase();
        String discount = txtDiscount.getText().trim();
        String expire = txtExpireDate.getText().trim();
        String max = txtMaxUsage.getText().trim();
        String status = cbStatus.getSelectedItem().toString();

        if (code.isEmpty() || discount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Voucher và Mức giảm giá!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equalsIgnoreCase(code)) {
                JOptionPane.showMessageDialog(this, "Mã Voucher '" + code + "' đã tồn tại!", "Trùng mã", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String usage = "0/" + (max.isEmpty() ? "100" : max);
        model.addRow(new Object[]{code, discount, expire.isEmpty() ? "Không giới hạn" : expire, usage, status});
        JOptionPane.showMessageDialog(this, "Phát hành mã Voucher [" + code + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        actionLamMoi();
    }

    private void actionCapNhat() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Voucher trong bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String discount = txtDiscount.getText().trim();
        String expire = txtExpireDate.getText().trim();
        String max = txtMaxUsage.getText().trim();
        String status = cbStatus.getSelectedItem().toString();

        if (discount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mức giảm giá không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        model.setValueAt(discount, r, 1);
        model.setValueAt(expire, r, 2);
        String oldUsage = model.getValueAt(r, 3).toString();
        String usedCount = oldUsage.contains("/") ? oldUsage.substring(0, oldUsage.indexOf("/")) : "0";
        model.setValueAt(usedCount + "/" + (max.isEmpty() ? "100" : max), r, 3);
        model.setValueAt(status, r, 4);

        JOptionPane.showMessageDialog(this, "Cập nhật thông tin Voucher thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionKhoa() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Voucher cần Khóa hoặc Mở lại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cur = model.getValueAt(r, 4).toString();
        String nxt = "Đang mở".equalsIgnoreCase(cur) ? "Tạm khóa" : "Đang mở";
        model.setValueAt(nxt, r, 4);
        cbStatus.setSelectedItem(nxt);

        JOptionPane.showMessageDialog(this, "Đã chuyển trạng thái Voucher sang: " + nxt, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionXoa() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Voucher cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = model.getValueAt(r, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa mã giảm giá [" + code + "]?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(r);
            actionLamMoi();
            JOptionPane.showMessageDialog(this, "Đã xóa Voucher thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actionLamMoi() {
        table.clearSelection();
        txtCode.setText("");
        txtCode.setEditable(true);
        txtDiscount.setText("20%");
        txtExpireDate.setText("31/12/2026");
        txtMaxUsage.setText("100");
        cbStatus.setSelectedIndex(0);
    }
}
