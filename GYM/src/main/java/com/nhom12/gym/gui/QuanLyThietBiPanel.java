package com.nhom12.gym.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class QuanLyThietBiPanel extends JPanel {
    private JTextField txtMaMay;
    private JTextField txtTenMay;
    private JComboBox<String> cbKhuVuc;
    private JTextField txtNgayMua;
    private JComboBox<String> cbTrangThai;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblCountActive;
    private JLabel lblCountBaoTri;
    private JLabel lblCountHong;

    public QuanLyThietBiPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("QUẢN LÝ THIẾT BỊ MÁY TẬP & BẢO TRÌ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        add(lblTitle, BorderLayout.NORTH);

        // 3 THẺ ĐẾM TRẠNG THÁI REALTIME TRÊN CÙNG
        JPanel topCards = new JPanel(new GridLayout(1, 3, 20, 0));
        topCards.setOpaque(false);
        topCards.setPreferredSize(new Dimension(0, 95));

        lblCountActive = new JLabel("45");
        lblCountBaoTri = new JLabel("3");
        lblCountHong = new JLabel("1");

        topCards.add(createStatusCard("Máy Hoạt Động Tốt", lblCountActive, new Color(16, 185, 129)));
        topCards.add(createStatusCard("Đang Bảo Dưỡng / Sửa", lblCountBaoTri, new Color(245, 158, 11)));
        topCards.add(createStatusCard("Báo Hỏng / Ngừng Dùng", lblCountHong, new Color(239, 68, 68)));

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(topCards, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(5);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // BÊN TRÁI: FORM NHẬP THIẾT BỊ
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        TitledBorder borderLeft = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Thông Tin Thiết Bị Máy "
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
        leftPanel.add(new JLabel("Mã Thiết Bị:"), gbc);
        gbc.gridy = 1;
        txtMaMay = new JTextField("TB016");
        txtMaMay.setFont(fontInput);
        leftPanel.add(txtMaMay, gbc);

        gbc.gridy = 2;
        leftPanel.add(new JLabel("Tên Thiết Bị / Máy Tập:"), gbc);
        gbc.gridy = 3;
        txtTenMay = new JTextField();
        txtTenMay.setFont(fontInput);
        leftPanel.add(txtTenMay, gbc);

        gbc.gridy = 4;
        leftPanel.add(new JLabel("Khu Vực Bố Trí:"), gbc);
        gbc.gridy = 5;
        cbKhuVuc = new JComboBox<>(new String[]{"Khu Cardio (Máy chạy/Xe đạp)", "Khu Free Weight (Tạ đơn/Giàn Smith)", "Khu Máy Khối Pin-Loaded", "Khu Giãn Cơ / Functional"});
        cbKhuVuc.setFont(fontInput);
        leftPanel.add(cbKhuVuc, gbc);

        gbc.gridy = 6;
        leftPanel.add(new JLabel("Ngày Đưa Vào Sử Dụng:"), gbc);
        gbc.gridy = 7;
        txtNgayMua = new JTextField("30/09/2026");
        txtNgayMua.setFont(fontInput);
        leftPanel.add(txtNgayMua, gbc);

        gbc.gridy = 8;
        leftPanel.add(new JLabel("Trạng Thái Hiện Tại:"), gbc);
        gbc.gridy = 9;
        cbTrangThai = new JComboBox<>(new String[]{"Bình thường", "Cần bảo dưỡng", "Đang sửa chữa", "Hỏng"});
        cbTrangThai.setFont(fontInput);
        leftPanel.add(cbTrangThai, gbc);

        // Nút chức năng
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        JButton btnAdd = new JButton("Thêm Máy");
        btnAdd.setFont(fontLabel);
        btnAdd.setBackground(new Color(16, 185, 129));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnEdit = new JButton("Cập Nhật");
        btnEdit.setFont(fontLabel);
        btnEdit.setBackground(new Color(37, 99, 235));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDelete = new JButton("Xóa Máy");
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
        gbc.insets = new Insets(16, 10, 10, 10);
        leftPanel.add(btnPanel, gbc);

        gbc.gridy = 11;
        JButton btnBaoTri = new JButton("Lập Phiếu Bảo Trì Thiết Bị");
        btnBaoTri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBaoTri.setBackground(new Color(245, 158, 11));
        btnBaoTri.setForeground(Color.WHITE);
        btnBaoTri.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftPanel.add(btnBaoTri, gbc);

        gbc.gridy = 12;
        gbc.weighty = 1.0;
        leftPanel.add(Box.createVerticalGlue(), gbc);
        splitPane.setLeftComponent(leftPanel);

        // BÊN PHẢI: BẢNG DANH SÁCH MÁY
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Danh Mục Thiết Bị Đang Vận Hành ",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        String[] cols = {"Mã Máy", "Tên Thiết Bị", "Khu Vực", "Ngày Mua", "Bảo Trì Gần Nhất", "Trạng Thái"};
        Object[][] data = {
                {"TB001", "Máy Chạy Bộ Treadmill Pro", "Khu Cardio (Máy chạy/Xe đạp)", "12/01/2025", "20/09/2026", "Bình thường"},
                {"TB002", "Xe Đạp Thể Lực Elip", "Khu Cardio (Máy chạy/Xe đạp)", "15/03/2025", "10/09/2026", "Bình thường"},
                {"TB008", "Giàn Tạ Đa Năng Smith", "Khu Free Weight (Tạ đơn/Giàn Smith)", "10/06/2025", "28/09/2026", "Cần bảo dưỡng"},
                {"TB015", "Máy Đẩy Ngực Ngồi Chest Press", "Khu Máy Khối Pin-Loaded", "22/11/2025", "01/09/2026", "Đang sửa chữa"}
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
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaMay.setText(model.getValueAt(row, 0).toString());
                txtTenMay.setText(model.getValueAt(row, 1).toString());
                cbKhuVuc.setSelectedItem(model.getValueAt(row, 2).toString());
                txtNgayMua.setText(model.getValueAt(row, 3).toString());
                cbTrangThai.setSelectedItem(model.getValueAt(row, 5).toString());
            }
        });

        // 1. THÊM MỚI
        btnAdd.addActionListener(e -> {
            String ma = txtMaMay.getText().trim();
            String ten = txtTenMay.getText().trim();
            String khu = cbKhuVuc.getSelectedItem().toString();
            String ngay = txtNgayMua.getText().trim();
            String status = cbTrangThai.getSelectedItem().toString();

            if (ma.isEmpty() || ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã máy và Tên thiết bị!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            model.addRow(new Object[]{ma, ten, khu, ngay, "Chưa có", status});
            updateCounts();
            JOptionPane.showMessageDialog(this, "Đã thêm mới thiết bị '" + ten + "' thành công!");
            btnClear.doClick();
        });

        // 2. CẬP NHẬT
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thiết bị trên bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ma = txtMaMay.getText().trim();
            String ten = txtTenMay.getText().trim();
            String khu = cbKhuVuc.getSelectedItem().toString();
            String ngay = txtNgayMua.getText().trim();
            String status = cbTrangThai.getSelectedItem().toString();

            model.setValueAt(ma, row, 0);
            model.setValueAt(ten, row, 1);
            model.setValueAt(khu, row, 2);
            model.setValueAt(ngay, row, 3);
            model.setValueAt(status, row, 5);

            updateCounts();
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin thiết bị thành công!");
        });

        // 3. XÓA
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thiết bị cần xóa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ten = model.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa thiết bị '" + ten + "'?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                updateCounts();
                btnClear.doClick();
                JOptionPane.showMessageDialog(this, "Đã xóa thiết bị thành công!");
            }
        });

        // 4. LẬP PHIẾU BẢO TRÌ
        btnBaoTri.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thiết bị cần lập phiếu bảo trì!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ten = model.getValueAt(row, 1).toString();
            model.setValueAt("Đang sửa chữa", row, 5);
            model.setValueAt("Hôm nay", row, 4);
            cbTrangThai.setSelectedItem("Đang sửa chữa");
            updateCounts();
            JOptionPane.showMessageDialog(this,
                    "Đã tạo phiếu bảo trì cho thiết bị: " + ten + "\nTrạng thái chuyển thành: Đang sửa chữa",
                    "Phiếu Bảo Trì", JOptionPane.INFORMATION_MESSAGE);
        });

        // 5. LÀM MỚI
        btnClear.addActionListener(e -> {
            txtMaMay.setText("TB" + String.format("%03d", model.getRowCount() + 1));
            txtTenMay.setText("");
            txtNgayMua.setText("30/09/2026");
            cbKhuVuc.setSelectedIndex(0);
            cbTrangThai.setSelectedIndex(0);
            table.clearSelection();
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        centerPanel.add(splitPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        updateCounts();
    }

    private void updateCounts() {
        int normal = 0, repair = 0, broken = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String st = model.getValueAt(i, 5).toString();
            if (st.equalsIgnoreCase("Bình thường")) normal++;
            else if (st.contains("sửa") || st.contains("bảo dưỡng")) repair++;
            else broken++;
        }
        lblCountActive.setText(String.valueOf(normal));
        lblCountBaoTri.setText(String.valueOf(repair));
        lblCountHong.setText(String.valueOf(broken));
    }

    private JPanel createStatusCard(String title, JLabel lblCount, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, color),
                new EmptyBorder(12, 16, 12, 16)
        ));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(71, 85, 105));

        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblCount.setForeground(color);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblCount, BorderLayout.CENTER);
        return card;
    }
}
