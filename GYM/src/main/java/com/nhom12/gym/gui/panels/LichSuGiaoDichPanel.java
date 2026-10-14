package com.nhom12.gym.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class LichSuGiaoDichPanel extends JPanel {
    private JTextField txtSearch;
    private JComboBox<String> cbLoai;
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JButton btnTraCuu;
    private JButton btnPrintBill;
    private JButton btnExportExcel;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public LichSuGiaoDichPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("LỊCH SỬ GIAO DỊCH & HÓA ĐƠN ĐIỆN TỬ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Tra cứu toàn bộ hóa đơn bán hàng POS, đăng ký hội viên mới và gia hạn gói tập");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        // Filter Bar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel l1 = new JLabel("Mã HĐ / Khách Hàng:"); l1.setFont(fontLabel);
        filterPanel.add(l1);
        txtSearch = new JTextField(12); txtSearch.setFont(fontInput);
        filterPanel.add(txtSearch);

        JLabel l2 = new JLabel("Loại GD:"); l2.setFont(fontLabel);
        filterPanel.add(l2);
        cbLoai = new JComboBox<>(new String[]{"Tất cả", "Bán hàng POS", "Gia hạn Gói tập", "Đăng ký mới"});
        cbLoai.setFont(fontInput);
        filterPanel.add(cbLoai);

        JLabel l3 = new JLabel("Từ Ngày:"); l3.setFont(fontLabel);
        filterPanel.add(l3);
        txtTuNgay = new JTextField("01/10/2026", 8); txtTuNgay.setFont(fontInput);
        filterPanel.add(txtTuNgay);

        JLabel l4 = new JLabel("Đến Ngày:"); l4.setFont(fontLabel);
        filterPanel.add(l4);
        txtDenNgay = new JTextField(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()), 8); txtDenNgay.setFont(fontInput);
        filterPanel.add(txtDenNgay);

        btnTraCuu = new JButton("Tra Cứu Hóa Đơn");
        btnTraCuu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTraCuu.setBackground(new Color(37, 99, 235));
        btnTraCuu.setForeground(Color.WHITE);
        filterPanel.add(btnTraCuu);

        btnPrintBill = new JButton("In Lại Hóa Đơn");
        btnPrintBill.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrintBill.setBackground(new Color(16, 185, 129));
        btnPrintBill.setForeground(Color.WHITE);
        filterPanel.add(btnPrintBill);

        btnExportExcel = new JButton("Xuất Excel");
        btnExportExcel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExportExcel.setBackground(new Color(100, 116, 139));
        btnExportExcel.setForeground(Color.WHITE);
        filterPanel.add(btnExportExcel);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        String[] cols = new String[]{"Mã HĐ", "Thời Gian", "Khách Hàng", "Nội Dung Giao Dịch", "Nhân Viên Lập", "Tổng Tiền (VNĐ)", "Hình Thức"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(fontInput);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Đảm bảo hiển thị chuẩn tiếng Việt, tự động khôi phục lỗi ký tự '?' nếu có
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (value != null) {
                    setText(com.nhom12.gym.util.TextSanitizer.sanitize(value.toString()));
                }
                return c;
            }
        });

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 15));
        centerContainer.setOpaque(false);
        centerContainer.add(filterPanel, BorderLayout.NORTH);
        centerContainer.add(tablePanel, BorderLayout.CENTER);
        this.add(centerContainer, BorderLayout.CENTER);

        // Nạp dữ liệu hóa đơn thực tế từ HoaDonDAO
        loadDataFromDAO();

        // Lắng nghe sự kiện đồng bộ toàn hệ thống
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            if (type == com.nhom12.gym.service.DataSyncManager.DataType.HOA_DON ||
                type == com.nhom12.gym.service.DataSyncManager.DataType.ALL) {
                loadDataFromDAO();
            }
        });

        // Action Listeners
        btnTraCuu.addActionListener(e -> applyFilter());
        txtSearch.addActionListener(e -> applyFilter());
        cbLoai.addActionListener(e -> applyFilter());

        btnPrintBill.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trong danh sách để in lại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int mr = table.convertRowIndexToModel(sel);
            String code = model.getValueAt(mr, 0).toString();
            String time = model.getValueAt(mr, 1).toString();
            String customer = com.nhom12.gym.util.TextSanitizer.sanitize(model.getValueAt(mr, 2).toString());
            String type = com.nhom12.gym.util.TextSanitizer.sanitize(model.getValueAt(mr, 3).toString());
            String staff = com.nhom12.gym.util.TextSanitizer.sanitize(model.getValueAt(mr, 4).toString());
            String total = model.getValueAt(mr, 5).toString();
            String payType = com.nhom12.gym.util.TextSanitizer.sanitize(model.getValueAt(mr, 6).toString());

            double num = 0;
            double originalPrice = 0;
            double discount = 0;
            com.nhom12.gym.entity.HoaDon hd = com.nhom12.gym.dao.HoaDonDAO.getInstance().timTheoMa(code);
            if (hd != null) {
                num = hd.getThanhTien();
                originalPrice = hd.getTongTien() > 0 ? hd.getTongTien() : num;
                discount = hd.getGiamGia();
            } else {
                try {
                    num = Double.parseDouble(total.replaceAll("[^0-9]", ""));
                } catch (Exception ignored) {}
                originalPrice = num;
            }
            java.util.List<com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang> items = new java.util.ArrayList<>();
            items.add(new com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang(type, 1, originalPrice, num));
            Window parent = SwingUtilities.getWindowAncestor(this);
            com.nhom12.gym.gui.ThanhToanHoaDonDialog dialog = new com.nhom12.gym.gui.ThanhToanHoaDonDialog(
                    parent,
                    code,
                    customer,
                    type,
                    originalPrice,
                    discount,
                    num,
                    items,
                    null
            );
            dialog.setVisible(true);
        });

        btnExportExcel.addActionListener(e -> {
            com.nhom12.gym.util.ExcelExportHelper.exportTableToExcel(this, table, "LichSu_GiaoDich_FitLife");
        });
    }

    private void loadDataFromDAO() {
        model.setRowCount(0);
        java.util.List<com.nhom12.gym.entity.HoaDon> list = com.nhom12.gym.dao.HoaDonDAO.getInstance().layTatCa();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
        if (list != null && !list.isEmpty()) {
            for (com.nhom12.gym.entity.HoaDon hd : list) {
                String time = hd.getNgayLap() != null ? sdf.format(hd.getNgayLap()) : sdf.format(new java.util.Date());
                String cus = (hd.getTenHoiVien() != null && !hd.getTenHoiVien().isEmpty()) 
                        ? com.nhom12.gym.util.TextSanitizer.sanitize(hd.getTenHoiVien()) 
                        : "Khách vãng lai";
                String type = (hd.getLoaiGiaoDich() != null && !hd.getLoaiGiaoDich().isEmpty()) 
                        ? com.nhom12.gym.util.TextSanitizer.sanitize(hd.getLoaiGiaoDich()) 
                        : "Giao dịch FitLife";
                String staff = (hd.getNguoiLap() != null && !hd.getNguoiLap().isEmpty()) 
                        ? com.nhom12.gym.util.TextSanitizer.sanitize(hd.getNguoiLap()) 
                        : "letan1";
                String total = df.format(hd.getThanhTien());
                String payMethod = (hd.getHinhThucThanhToan() != null && !hd.getHinhThucThanhToan().isEmpty()) 
                        ? com.nhom12.gym.util.TextSanitizer.sanitize(hd.getHinhThucThanhToan()) 
                        : "Tiền mặt";
                model.addRow(new Object[]{
                        hd.getMaHoaDon(),
                        time,
                        cus,
                        type,
                        staff,
                        total,
                        payMethod
                });
            }
        }
    }

    private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        String type = cbLoai.getSelectedItem().toString();

        if (keyword.isEmpty() && "Tất cả".equalsIgnoreCase(type)) {
            sorter.setRowFilter(null);
        } else if (!keyword.isEmpty() && "Tất cả".equalsIgnoreCase(type)) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword));
        } else if (keyword.isEmpty() && !"Tất cả".equalsIgnoreCase(type)) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + type));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(
                    RowFilter.regexFilter("(?i)" + keyword),
                    RowFilter.regexFilter("(?i)" + type)
            )));
        }
    }
}
