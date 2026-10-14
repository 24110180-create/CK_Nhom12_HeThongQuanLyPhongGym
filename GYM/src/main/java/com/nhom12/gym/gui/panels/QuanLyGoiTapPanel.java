package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.GoiTapDAO;
import com.nhom12.gym.entity.GoiTap;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Giao diện Quản Lý Danh Sách & Bảng Giá Gói Tập
 * Cho phép Chủ phòng/Quản lý chủ động đổi tên gói, giá tiền, thời hạn theo nhu cầu kinh doanh.
 * Tự động đồng bộ thời gian thực sang màn hình Đăng Ký và Gia Hạn Gói Tập.
 */
public class QuanLyGoiTapPanel extends JPanel {
    private final GoiTapDAO goiTapDAO = new GoiTapDAO();
    private JTextField txtMaGoi;
    private JTextField txtTenGoi;
    private JTextField txtGiaTien;
    private JTextField txtThoiHan;
    private JComboBox<String> cbLoaiGoi;
    private JComboBox<String> cbPhamVi;
    private JTextField txtMoTa;
    private JTable table;
    private DefaultTableModel model;
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    public QuanLyGoiTapPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ GÓI TẬP & CẤU HÌNH BẢNG GIÁ DỊCH VỤ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        JLabel lblSub = new JLabel("Chủ phòng tự do đặt tên gói, điều chỉnh giá và thời hạn - Hệ thống tự đồng bộ sang Menu Đăng Ký & Gia Hạn");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(6);
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
        gbc.insets = new Insets(5, 12, 5, 12);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 13);

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r++;
        JLabel lMa = new JLabel("Mã Gói Tập (*):"); lMa.setFont(fontLabel);
        leftPanel.add(lMa, gbc);
        gbc.gridy = r++;
        txtMaGoi = new JTextField("GT01");
        txtMaGoi.setFont(fontInput);
        leftPanel.add(txtMaGoi, gbc);

        gbc.gridy = r++;
        JLabel lTen = new JLabel("Tên Gói Tập (Tự đặt tên theo nhu cầu) (*):"); lTen.setFont(fontLabel);
        leftPanel.add(lTen, gbc);
        gbc.gridy = r++;
        txtTenGoi = new JTextField();
        txtTenGoi.setFont(fontInput);
        leftPanel.add(txtTenGoi, gbc);

        gbc.gridy = r++;
        JLabel lThoiHan = new JLabel("Thời Hạn Sử Dụng (Số Tháng) (*):"); lThoiHan.setFont(fontLabel);
        leftPanel.add(lThoiHan, gbc);
        gbc.gridy = r++;
        txtThoiHan = new JTextField("1");
        txtThoiHan.setFont(fontInput);
        leftPanel.add(txtThoiHan, gbc);

        gbc.gridy = r++;
        JLabel lGia = new JLabel("Giá Niêm Yết (VNĐ) (*):"); lGia.setFont(fontLabel);
        leftPanel.add(lGia, gbc);
        gbc.gridy = r++;
        txtGiaTien = new JTextField("500000");
        txtGiaTien.setFont(fontInput);
        leftPanel.add(txtGiaTien, gbc);

        gbc.gridy = r++;
        JLabel lLoai = new JLabel("Phân Loại Dịch Vụ:"); lLoai.setFont(fontLabel);
        leftPanel.add(lLoai, gbc);
        gbc.gridy = r++;
        cbLoaiGoi = new JComboBox<>(new String[]{"Gói Cơ Bản (Standard)", "Gói VIP (Có Sauna/Bể bơi)", "Gói PT (Kèm Huấn luyện viên)", "Gói Sinh Viên Siêu Rẻ", "Gói Yoga / GroupX"});
        cbLoaiGoi.setFont(fontInput);
        leftPanel.add(cbLoaiGoi, gbc);

        gbc.gridy = r++;
        JLabel lPhamVi = new JLabel("Phạm Vi Áp Dụng:"); lPhamVi.setFont(fontLabel);
        leftPanel.add(lPhamVi, gbc);
        gbc.gridy = r++;
        cbPhamVi = new JComboBox<>(new String[]{"Toàn hệ thống", "Tại chi nhánh đăng ký", "Khu VIP Diamond Landmark"});
        cbPhamVi.setFont(fontInput);
        leftPanel.add(cbPhamVi, gbc);

        gbc.gridy = r++;
        JLabel lMoTa = new JLabel("Mô Tả & Quyền Lợi Kèm Theo:"); lMoTa.setFont(fontLabel);
        leftPanel.add(lMoTa, gbc);
        gbc.gridy = r++;
        txtMoTa = new JTextField("Tập không giới hạn khung giờ");
        txtMoTa.setFont(fontInput);
        leftPanel.add(txtMoTa, gbc);

        // Nút chức năng Thêm, Sửa, Xóa, Làm mới
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setOpaque(false);

        JButton btnThem = createBtn("Thêm Gói Mới", new Color(16, 185, 129));
        JButton btnSua = createBtn("Lưu Cập Nhật", new Color(37, 99, 235));
        JButton btnXoa = createBtn("Xóa Gói", new Color(239, 68, 68));
        JButton btnLamMoi = createBtn("Làm Mới Form", new Color(100, 116, 139));

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        gbc.gridy = r++;
        gbc.insets = new Insets(14, 12, 10, 12);
        leftPanel.add(buttonPanel, gbc);

        gbc.gridy = r++;
        gbc.weighty = 1.0;
        leftPanel.add(Box.createVerticalGlue(), gbc);
        splitPane.setLeftComponent(leftPanel);

        // --- BÊN PHẢI: BẢNG DANH SÁCH GÓI TẬP ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Danh Sách Gói Tập Thực Tế Đang Mở Bán ",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topBar.setOpaque(false);
        JButton btnExport = new JButton("Xuất Bảng Giá Excel (.csv)");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExport.setBackground(new Color(241, 245, 249));
        btnExport.setForeground(new Color(15, 118, 110));
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(e -> com.nhom12.gym.util.ExcelExportHelper.exportTableToExcel(this, table, "BangGia_GoiTap_FitLife"));
        topBar.add(btnExport);
        rightPanel.add(topBar, BorderLayout.NORTH);

        String[] columns = {"Mã Gói", "Tên Gói Tập", "Thời Hạn", "Giá Niêm Yết", "Phân Loại / Phạm Vi", "Mô Tả Quyền Lợi", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
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
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(6).setCellRenderer(center);

        // Click bảng để điền dữ liệu lên form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaGoi.setText(model.getValueAt(row, 0).toString());
                txtTenGoi.setText(model.getValueAt(row, 1).toString());
                txtThoiHan.setText(model.getValueAt(row, 2).toString().replaceAll("\\D+", ""));
                txtGiaTien.setText(model.getValueAt(row, 3).toString().replace(" đ", "").replace(",", "").trim());
                String phamViVal = model.getValueAt(row, 4).toString();
                cbPhamVi.setSelectedItem(phamViVal);
                txtMoTa.setText(model.getValueAt(row, 5).toString());
            }
        });

        // Nạp dữ liệu ban đầu
        loadDataToTable();

        // 1. THÊM MỚI GÓI TẬP
        btnThem.addActionListener(e -> {
            String ma = txtMaGoi.getText().trim();
            String ten = txtTenGoi.getText().trim();
            String giaStr = txtGiaTien.getText().trim().replace(",", "");
            String thoiHanStr = txtThoiHan.getText().trim().replaceAll("\\D+", "");
            String phamVi = cbPhamVi.getSelectedItem().toString();
            String moTa = txtMoTa.getText().trim();

            if (ma.isEmpty() || ten.isEmpty() || giaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã gói, Tên gói và Giá tiền!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra trùng mã
            if (goiTapDAO.timTheoMa(ma) != null) {
                JOptionPane.showMessageDialog(this, "Mã gói tập '" + ma + "' đã tồn tại! Vui lòng chọn mã khác.", "Cảnh báo trùng mã", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double gia;
            try {
                gia = Double.parseDouble(giaStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Giá tiền không hợp lệ! Vui lòng chỉ nhập số.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int thoiHan = 1;
            try {
                thoiHan = Integer.parseInt(thoiHanStr);
                if (thoiHan <= 0) thoiHan = 1;
            } catch (Exception ignored) {}

            GoiTap gt = new GoiTap(ma, ten, thoiHan, gia, phamVi, moTa, "Đang mở");
            boolean ok = goiTapDAO.them(gt);
            if (ok) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Đã thêm gói tập '" + ten + "' thành công!\nCác menu đăng ký và gia hạn đã được cập nhật đồng bộ.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                btnLamMoi.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu gói tập vào cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 2. CẬP NHẬT GÓI TẬP
        btnSua.addActionListener(e -> {
            String ma = txtMaGoi.getText().trim();
            String ten = txtTenGoi.getText().trim();
            String giaStr = txtGiaTien.getText().trim().replace(",", "");
            String thoiHanStr = txtThoiHan.getText().trim().replaceAll("\\D+", "");
            String phamVi = cbPhamVi.getSelectedItem().toString();
            String moTa = txtMoTa.getText().trim();

            if (ma.isEmpty() || ten.isEmpty() || giaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một gói tập trên bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double gia;
            try {
                gia = Double.parseDouble(giaStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Giá tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int thoiHan = 1;
            try {
                thoiHan = Integer.parseInt(thoiHanStr);
                if (thoiHan <= 0) thoiHan = 1;
            } catch (Exception ignored) {}

            GoiTap gt = new GoiTap(ma, ten, thoiHan, gia, phamVi, moTa, "Đang mở");
            boolean ok = goiTapDAO.capNhat(gt);
            if (ok) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin gói tập '" + ten + "' thành công!\nCác màn hình khác đã được đồng bộ tự động.", "Cập Nhật Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật gói tập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 3. XÓA GÓI TẬP
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn gói tập cần xóa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ma = model.getValueAt(row, 0).toString();
            String ten = model.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa gói tập '" + ten + "' (" + ma + ")?\nThao tác này sẽ gỡ bỏ gói tập khỏi Menu Đăng ký và Gia hạn.",
                    "Xác nhận xóa gói tập", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                goiTapDAO.xoa(ma);
                loadDataToTable();
                btnLamMoi.doClick();
                JOptionPane.showMessageDialog(this, "Đã xóa gói tập '" + ten + "' thành công!", "Đã Xóa", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 4. LÀM MỚI FORM
        btnLamMoi.addActionListener(e -> {
            List<GoiTap> list = goiTapDAO.layTatCa();
            int nextIdx = list.size() + 1;
            txtMaGoi.setText(String.format("GT%02d", nextIdx));
            while (goiTapDAO.timTheoMa(txtMaGoi.getText()) != null) {
                nextIdx++;
                txtMaGoi.setText(String.format("GT%02d", nextIdx));
            }
            txtTenGoi.setText("");
            txtGiaTien.setText("500000");
            txtThoiHan.setText("1");
            txtMoTa.setText("Tập không giới hạn khung giờ");
            cbLoaiGoi.setSelectedIndex(0);
            cbPhamVi.setSelectedIndex(0);
            table.clearSelection();
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Đăng ký nhận thông báo thay đổi
        GoiTapDAO.addChangeListener(this::loadDataToTable);
    }

    public void loadDataToTable() {
        model.setRowCount(0);
        List<GoiTap> list = goiTapDAO.layTatCa();
        if (list != null) {
            for (GoiTap gt : list) {
                model.addRow(new Object[]{
                        gt.getMaGoiTap(),
                        gt.getTenGoiTap(),
                        gt.getThoiHanThang() + " Tháng",
                        df.format(gt.getGiaTien()),
                        gt.getPhamVi() != null ? gt.getPhamVi() : "Toàn hệ thống",
                        gt.getMoTa() != null ? gt.getMoTa() : "---",
                        gt.getTrangThai() != null ? gt.getTrangThai() : "Đang mở"
                });
            }
        }
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
