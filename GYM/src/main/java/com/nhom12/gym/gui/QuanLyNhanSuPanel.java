package com.nhom12.gym.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class QuanLyNhanSuPanel extends JPanel {
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtFullName;
    private JComboBox<String> cbRole;
    private JTextField txtSalary;
    private JComboBox<String> cbStatus;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnToggleLock;
    private JButton btnDelete;
    private JButton btnClear;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;

    public QuanLyNhanSuPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN SỰ & TÀI KHOẢN HỆ THỐNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Quản lý danh sách nhân viên, cấp phát tài khoản, phân vai trò và tính lương");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        // Split Pane (Left Form, Right Table)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Form Panel
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFormTitle = new JLabel("Thông Tin Nhân Viên");
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
        // Username
        gbc.gridx = 0; gbc.gridy = row++;
        JLabel lblUser = new JLabel("Tên Đăng Nhập (*):");
        lblUser.setFont(fontLabel);
        formGrid.add(lblUser, gbc);

        gbc.gridy = row++;
        txtUsername = new JTextField();
        txtUsername.setFont(fontInput);
        formGrid.add(txtUsername, gbc);

        // Password
        gbc.gridy = row++;
        JLabel lblPass = new JLabel("Mật Khẩu:");
        lblPass.setFont(fontLabel);
        formGrid.add(lblPass, gbc);

        gbc.gridy = row++;
        txtPassword = new JTextField("123456");
        txtPassword.setFont(fontInput);
        formGrid.add(txtPassword, gbc);

        // Full Name
        gbc.gridy = row++;
        JLabel lblName = new JLabel("Họ Và Tên (*):");
        lblName.setFont(fontLabel);
        formGrid.add(lblName, gbc);

        gbc.gridy = row++;
        txtFullName = new JTextField();
        txtFullName.setFont(fontInput);
        formGrid.add(txtFullName, gbc);

        // Role
        gbc.gridy = row++;
        JLabel lblRole = new JLabel("Vai Trò / Chức Vụ:");
        lblRole.setFont(fontLabel);
        formGrid.add(lblRole, gbc);

        gbc.gridy = row++;
        cbRole = new JComboBox<>(new String[]{"Lễ tân", "HLV", "Quản lý", "Chủ"});
        cbRole.setFont(fontInput);
        formGrid.add(cbRole, gbc);

        // Salary
        gbc.gridy = row++;
        JLabel lblSalary = new JLabel("Lương Cơ Bản (VNĐ):");
        lblSalary.setFont(fontLabel);
        formGrid.add(lblSalary, gbc);

        gbc.gridy = row++;
        txtSalary = new JTextField("7,500,000");
        txtSalary.setFont(fontInput);
        formGrid.add(txtSalary, gbc);

        // Status
        gbc.gridy = row++;
        JLabel lblStatus = new JLabel("Trạng Thái Tài Khoản:");
        lblStatus.setFont(fontLabel);
        formGrid.add(lblStatus, gbc);

        gbc.gridy = row++;
        cbStatus = new JComboBox<>(new String[]{"Hoạt động", "Tạm khóa"});
        cbStatus.setFont(fontInput);
        formGrid.add(cbStatus, gbc);

        gbc.gridy = row++;
        gbc.weighty = 1.0;
        formGrid.add(Box.createVerticalGlue(), gbc);

        leftPanel.add(formGrid, BorderLayout.CENTER);

        // Buttons Panel on Left
        JPanel buttonGrid = new JPanel(new GridLayout(3, 2, 8, 8));
        buttonGrid.setOpaque(false);

        btnAdd = new JButton("Thêm Mới");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(new Color(16, 185, 129));
        btnAdd.setForeground(Color.WHITE);

        btnUpdate = new JButton("Cập Nhật");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdate.setBackground(new Color(37, 99, 235));
        btnUpdate.setForeground(Color.WHITE);

        btnToggleLock = new JButton("Khóa / Mở");
        btnToggleLock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnToggleLock.setBackground(new Color(245, 158, 11));
        btnToggleLock.setForeground(Color.WHITE);

        btnDelete = new JButton("Xóa");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);

        btnClear = new JButton("Làm Mới Form");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setBackground(new Color(100, 116, 139));
        btnClear.setForeground(Color.WHITE);

        buttonGrid.add(btnAdd);
        buttonGrid.add(btnUpdate);
        buttonGrid.add(btnToggleLock);
        buttonGrid.add(btnDelete);
        buttonGrid.add(btnClear);

        leftPanel.add(buttonGrid, BorderLayout.SOUTH);
        splitPane.setLeftComponent(leftPanel);

        // Right Table Panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Search Bar on Right Top
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm kiếm nhân sự:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = new JTextField(20);
        txtSearch.setFont(fontInput);
        searchBar.add(lblSearch);
        searchBar.add(txtSearch);
        rightPanel.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] cols = new String[]{"Tên Đăng Nhập", "Họ Tên Nhân Viên", "Vai Trò", "Lương Cơ Bản", "Trạng Thái"};
        Object[][] data = new Object[][]{
                {"admin", "Võ Anh Đức", "Chủ", "---", "Hoạt động"},
                {"letan1", "Nguyễn Văn Tuấn", "Lễ tân", "7,500,000", "Hoạt động"},
                {"letan2", "Trần Thị Mai", "Lễ tân", "7,000,000", "Tạm khóa"},
                {"hlv1", "John Smith (HLV)", "HLV", "15,000,000", "Hoạt động"},
                {"hlv2", "Phạm Quốc Bảo", "HLV", "12,000,000", "Hoạt động"},
                {"quanly1", "Lê Thanh Sơn", "Quản lý", "18,000,000", "Hoạt động"}
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
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Search filter listener
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Table Selection -> Form Populate
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                txtUsername.setText(model.getValueAt(modelRow, 0).toString());
                txtUsername.setEditable(false);
                txtFullName.setText(model.getValueAt(modelRow, 1).toString());
                cbRole.setSelectedItem(model.getValueAt(modelRow, 2).toString());
                txtSalary.setText(model.getValueAt(modelRow, 3).toString());
                cbStatus.setSelectedItem(model.getValueAt(modelRow, 4).toString());
            }
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Wire Action Listeners
        btnAdd.addActionListener(e -> actionThem());
        btnUpdate.addActionListener(e -> actionCapNhat());
        btnToggleLock.addActionListener(e -> actionKhoaMoKhoa());
        btnDelete.addActionListener(e -> actionXoa());
        btnClear.addActionListener(e -> actionLamMoi());
    }

    private void actionThem() {
        String username = txtUsername.getText().trim();
        String fullname = txtFullName.getText().trim();
        String role = cbRole.getSelectedItem().toString();
        String salary = txtSalary.getText().trim();
        String status = cbStatus.getSelectedItem().toString();

        if (username.isEmpty() || fullname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên Đăng Nhập và Họ Tên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check duplicate
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập '" + username + "' đã tồn tại! Vui lòng chọn tên khác.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        model.addRow(new Object[]{username, fullname, role, salary, status});
        JOptionPane.showMessageDialog(this, "Thêm nhân viên '" + fullname + "' thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        actionLamMoi();
    }

    private void actionCapNhat() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên trong danh sách để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);

        String fullname = txtFullName.getText().trim();
        String role = cbRole.getSelectedItem().toString();
        String salary = txtSalary.getText().trim();
        String status = cbStatus.getSelectedItem().toString();

        if (fullname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        model.setValueAt(fullname, modelRow, 1);
        model.setValueAt(role, modelRow, 2);
        model.setValueAt(salary, modelRow, 3);
        model.setValueAt(status, modelRow, 4);

        JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionKhoaMoKhoa() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần Khóa hoặc Mở Khóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        String currentStatus = model.getValueAt(modelRow, 4).toString();
        String newStatus = "Hoạt động".equalsIgnoreCase(currentStatus) ? "Tạm khóa" : "Hoạt động";
        model.setValueAt(newStatus, modelRow, 4);
        cbStatus.setSelectedItem(newStatus);

        JOptionPane.showMessageDialog(this, "Đã chuyển trạng thái nhân viên sang: " + newStatus, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionXoa() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        String username = model.getValueAt(modelRow, 0).toString();

        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản Quản trị viên tối cao (admin)!", "Cấm thao tác", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên [" + username + "] khỏi hệ thống?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(modelRow);
            actionLamMoi();
            JOptionPane.showMessageDialog(this, "Đã xóa nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actionLamMoi() {
        table.clearSelection();
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtPassword.setText("123456");
        txtFullName.setText("");
        cbRole.setSelectedIndex(0);
        txtSalary.setText("7,500,000");
        cbStatus.setSelectedIndex(0);
        txtSearch.setText("");
    }
}
