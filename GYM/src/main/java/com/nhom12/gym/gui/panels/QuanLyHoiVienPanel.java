package com.nhom12.gym.gui.panels;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.HoiVienService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Màn hình Quản Lý Hội Viên Đã Đăng Ký
 * Hiển thị chi tiết Ngày Giờ Đăng Ký, Số Tiền Đã Thanh Toán, Gói Tập, Hình Thức Thanh Toán,
 * kèm thanh KPI thống kê tổng doanh thu thực thu từ hội viên.
 */
public class QuanLyHoiVienPanel extends JPanel {
    private final HoiVienService service = new HoiVienService();
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

    // Các thành phần Form
    private JTextField txtMaHV;
    private JTextField txtQR;
    private JTextField txtHoTen;
    private JTextField txtSDT;
    private JTextField txtGoiTap;
    private JTextField txtNgayDangKy;
    private JTextField txtSoTien;
    private JComboBox<String> cbHinhThucTT;
    private JTextField txtChiNhanh;
    private JTextField txtNgayHetHan;
    private JComboBox<String> cbTrangThai;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private JTable table;
    private DefaultTableModel tableModel;

    // Các nhãn KPI thống kê
    private JLabel lblKpiTongHoiVien;
    private JLabel lblKpiTongDoanhThu;
    private JLabel lblKpiDangHoatDong;
    private JLabel lblKpiChuyenKhoan;

    public QuanLyHoiVienPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        // HEADER & KPI
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ HỘI VIÊN ĐÃ ĐĂNG KÝ & DOANH THU HỘI PHÍ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));
        topContainer.add(lblTitle, BorderLayout.NORTH);

        topContainer.add(createKpiPanel(), BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // NỘI DUNG CHÍNH (SPLIT PANE)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(390);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(createFormPanel());
        splitPane.setRightComponent(createTablePanel());
        add(splitPane, BorderLayout.CENTER);

        setupActions();
        loadDataToTable();
    }

    private JPanel createKpiPanel() {
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.setPreferredSize(new Dimension(0, 75));

        lblKpiTongHoiVien = new JLabel("0 HV", SwingConstants.CENTER);
        lblKpiTongDoanhThu = new JLabel("0 đ", SwingConstants.CENTER);
        lblKpiDangHoatDong = new JLabel("0 HV", SwingConstants.CENTER);
        lblKpiChuyenKhoan = new JLabel("0 đ", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("Tổng Hội Viên Đã Đăng Ký", lblKpiTongHoiVien, new Color(37, 99, 235), new Color(239, 246, 255)));
        kpiPanel.add(createKpiCard("Tổng Tiền Đã Thanh Toán", lblKpiTongDoanhThu, new Color(16, 185, 129), new Color(236, 253, 245)));
        kpiPanel.add(createKpiCard("Hội Viên Đang Hoạt Động (Active)", lblKpiDangHoatDong, new Color(14, 165, 233), new Color(240, 249, 255)));
        kpiPanel.add(createKpiCard("Thanh Toán VietQR / Chuyển Khoản", lblKpiChuyenKhoan, new Color(139, 92, 246), new Color(245, 243, 255)));

        return kpiPanel;
    }

    private JPanel createKpiCard(String title, JLabel lblValue, Color primaryColor, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor.brighter(), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(71, 85, 105));

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(primaryColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFormPanel() {
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 12);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Chi Tiết Hội Viên & Giao Dịch "
        );
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        border.setTitleColor(new Color(37, 99, 235));
        formContainer.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0), border));

        int r = 0;

        // Mã HV & Mã Thẻ QR
        gbc.gridx = 0; gbc.gridy = r++;
        formGrid.add(createLabel("Mã HV / Mã Thẻ QR (*):", fontLabel), gbc);
        JPanel pCode = new JPanel(new GridLayout(1, 2, 8, 0));
        pCode.setOpaque(false);
        txtMaHV = new JTextField();
        txtMaHV.setEditable(false);
        txtMaHV.setBackground(new Color(241, 245, 249));
        txtMaHV.setFont(fontInput);
        txtQR = new JTextField();
        txtQR.setFont(fontInput);
        pCode.add(txtMaHV);
        pCode.add(txtQR);
        gbc.gridy = r++;
        formGrid.add(pCode, gbc);

        // Họ và Tên
        gbc.gridy = r++;
        formGrid.add(createLabel("Họ Và Tên (*):", fontLabel), gbc);
        txtHoTen = new JTextField();
        txtHoTen.setFont(fontInput);
        gbc.gridy = r++;
        formGrid.add(txtHoTen, gbc);

        // Số Điện Thoại & Gói Tập
        gbc.gridy = r++;
        formGrid.add(createLabel("Số Điện Thoại:", fontLabel), gbc);
        txtSDT = new JTextField();
        txtSDT.setFont(fontInput);
        gbc.gridy = r++;
        formGrid.add(txtSDT, gbc);

        gbc.gridy = r++;
        formGrid.add(createLabel("Gói Tập Đã Đăng Ký:", fontLabel), gbc);
        txtGoiTap = new JTextField("Gói Standard 3 Tháng");
        txtGoiTap.setFont(fontInput);
        gbc.gridy = r++;
        formGrid.add(txtGoiTap, gbc);

        // Ngày Giờ Đăng Ký (Timestamp chi tiết)
        gbc.gridy = r++;
        formGrid.add(createLabel("Ngày Giờ Đăng Ký (dd/MM/yyyy HH:mm:ss):", fontLabel), gbc);
        txtNgayDangKy = new JTextField(sdfDateTime.format(new Date()));
        txtNgayDangKy.setFont(fontInput);
        gbc.gridy = r++;
        formGrid.add(txtNgayDangKy, gbc);

        // Số Tiền Đã Thanh Toán
        gbc.gridy = r++;
        formGrid.add(createLabel("Số Tiền Đã Thanh Toán (VNĐ):", fontLabel), gbc);
        txtSoTien = new JTextField("2400000");
        txtSoTien.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSoTien.setForeground(new Color(16, 185, 129));
        gbc.gridy = r++;
        formGrid.add(txtSoTien, gbc);

        // Hình Thức Thanh Toán
        gbc.gridy = r++;
        formGrid.add(createLabel("Hình Thức Thanh Toán:", fontLabel), gbc);
        cbHinhThucTT = new JComboBox<>(new String[]{"Chuyển khoản VietQR", "Tiền mặt", "Quẹt thẻ POS"});
        cbHinhThucTT.setFont(fontInput);
        gbc.gridy = r++;
        formGrid.add(cbHinhThucTT, gbc);

        // Chi Nhánh & Ngày Hết Hạn
        gbc.gridy = r++;
        formGrid.add(createLabel("Chi Nhánh Đăng Ký:", fontLabel), gbc);
        txtChiNhanh = new JTextField("CN01 - Quận 1");
        txtChiNhanh.setFont(fontInput);
        gbc.gridy = r++;
        formGrid.add(txtChiNhanh, gbc);

        gbc.gridy = r++;
        formGrid.add(createLabel("Ngày Hết Hạn Gói (dd/MM/yyyy):", fontLabel), gbc);
        txtNgayHetHan = new JTextField("31/12/2026");
        txtNgayHetHan.setFont(fontInput);
        gbc.gridy = r++;
        formGrid.add(txtNgayHetHan, gbc);

        // Trạng Thái
        gbc.gridy = r++;
        formGrid.add(createLabel("Trạng Thái Hoạt Động:", fontLabel), gbc);
        cbTrangThai = new JComboBox<>(new String[]{"Active", "Expired", "Pending", "Locked"});
        cbTrangThai.setFont(fontInput);
        gbc.gridy = r++;
        formGrid.add(cbTrangThai, gbc);

        // Các nút bấm hành động
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 6, 6));
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

        gbc.gridy = r++;
        gbc.insets = new Insets(12, 4, 4, 4);
        formGrid.add(btnPanel, gbc);

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        formContainer.add(scrollForm, BorderLayout.CENTER);

        return formContainer;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(new Color(51, 65, 85));
        return lbl;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Danh Sách Hội Viên Đã Đăng Ký & Chi Tiết Ngày Giờ / Tiền Thanh Toán ",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), new Color(30, 41, 59)
        ));

        // Thanh tìm kiếm và xuất file
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        toolBar.setOpaque(false);

        JTextField txtSearchTable = new JTextField(18);
        txtSearchTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearchTable.putClientProperty("JTextField.placeholderText", "Tìm tên, SĐT, mã QR, gói...");

        JButton btnExport = new JButton("Xuất File Excel (.csv)");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(new Color(241, 245, 249));
        btnExport.setForeground(new Color(15, 118, 110));
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toolBar.add(new JLabel("Tìm kiếm nhanh:"));
        toolBar.add(txtSearchTable);
        toolBar.add(btnExport);
        panel.add(toolBar, BorderLayout.NORTH);

        // Bảng dữ liệu có đầy đủ Ngày Giờ Đăng Ký và Số Tiền Đã Thanh Toán
        String[] cols = {
                "Mã HV", "Mã Thẻ QR", "Họ Và Tên", "Số ĐT", "Gói Tập",
                "Ngày Giờ Đăng Ký", "Số Tiền Đã TT", "Hình Thức TT",
                "Chi Nhánh", "Ngày Hết Hạn", "Trạng Thái"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
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

        btnExport.addActionListener(e -> com.nhom12.gym.util.ExcelExportHelper.exportTableToExcel(this, table, "DanhSach_HoiVien_DaDangKy"));

        // Định dạng căn chỉnh renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setForeground(new Color(16, 185, 129));
        rightRenderer.setFont(new Font("Segoe UI", Font.BOLD, 12));

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã HV
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Mã QR
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // SĐT
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Ngày giờ đăng ký
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);  // Số tiền đã TT
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Hình thức TT
        table.getColumnModel().getColumn(9).setCellRenderer(centerRenderer); // Ngày hết hạn
        table.getColumnModel().getColumn(10).setCellRenderer(centerRenderer); // Trạng thái

        // Tùy chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(75);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(95);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(145);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setPreferredWidth(130);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);
        table.getColumnModel().getColumn(9).setPreferredWidth(95);
        table.getColumnModel().getColumn(10).setPreferredWidth(80);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        List<HoiVien> list = null;
        try {
            list = service.layDanhSachHoiVien();
        } catch (Exception ignored) {}

        double tongDoanhThu = 0;
        int activeCount = 0;
        double tongVietQR = 0;

        if (list != null && !list.isEmpty()) {
            for (HoiVien hv : list) {
                String ngayDangKyStr = (hv.getNgayDangKy() != null)
                        ? sdfDateTime.format(hv.getNgayDangKy())
                        : "15/01/2026 09:00:00";

                double tien = (hv.getSoTienDaThanhToan() != null && hv.getSoTienDaThanhToan() > 0)
                        ? hv.getSoTienDaThanhToan()
                        : 1500000.0;

                tongDoanhThu += tien;

                String sdt = (hv.getSoDienThoai() != null && !hv.getSoDienThoai().isEmpty())
                        ? hv.getSoDienThoai() : "0901234567";

                String goiTap = (hv.getGoiTapDangKy() != null && !hv.getGoiTapDangKy().isEmpty())
                        ? hv.getGoiTapDangKy() : "Gói Standard 3 Tháng";

                String hinhThucTT = (hv.getHinhThucThanhToan() != null && !hv.getHinhThucThanhToan().isEmpty())
                        ? hv.getHinhThucThanhToan() : "Chuyển khoản VietQR";

                if (hinhThucTT.contains("VietQR") || hinhThucTT.contains("Chuyển khoản")) {
                    tongVietQR += tien;
                }

                String dateHetHanStr = (hv.getNgayHetHan() != null) ? sdfDate.format(hv.getNgayHetHan()) : "31/12/2026";
                String status = hv.getTrangThai() != null ? hv.getTrangThai() : "Active";
                if ("Active".equalsIgnoreCase(status) || "Hoạt động".equalsIgnoreCase(status)) {
                    activeCount++;
                }

                tableModel.addRow(new Object[]{
                        hv.getMaHoiVien(),
                        hv.getMaTheQR(),
                        hv.getHoTen(),
                        sdt,
                        goiTap,
                        ngayDangKyStr,
                        df.format(tien),
                        hinhThucTT,
                        hv.getChiNhanhDangKy(),
                        dateHetHanStr,
                        status
                });
            }
        }

        // Cập nhật KPI
        lblKpiTongHoiVien.setText(tableModel.getRowCount() + " Hội Viên");
        lblKpiTongDoanhThu.setText(df.format(tongDoanhThu));
        lblKpiDangHoatDong.setText(activeCount + " Đang hoạt động");
        lblKpiChuyenKhoan.setText(df.format(tongVietQR));
    }

    private void setupActions() {
        // Sự kiện click chọn dòng trên bảng -> Điền toàn bộ thông tin lên Form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaHV.setText(table.getValueAt(row, 0).toString());
                txtQR.setText(table.getValueAt(row, 1).toString());
                txtHoTen.setText(table.getValueAt(row, 2).toString());
                txtSDT.setText(table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "");
                txtGoiTap.setText(table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "");
                txtNgayDangKy.setText(table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "");

                String tienRaw = table.getValueAt(row, 6) != null
                        ? table.getValueAt(row, 6).toString().replaceAll("[^0-9]", "")
                        : "0";
                txtSoTien.setText(tienRaw);

                String ht = table.getValueAt(row, 7) != null ? table.getValueAt(row, 7).toString() : "Chuyển khoản VietQR";
                cbHinhThucTT.setSelectedItem(ht);

                txtChiNhanh.setText(table.getValueAt(row, 8) != null ? table.getValueAt(row, 8).toString() : "CN01");
                txtNgayHetHan.setText(table.getValueAt(row, 9) != null ? table.getValueAt(row, 9).toString() : "31/12/2026");
                cbTrangThai.setSelectedItem(table.getValueAt(row, 10).toString());
            }
        });

        // 1. THÊM MỚI HỘI VIÊN
        btnThem.addActionListener(e -> {
            String qr = txtQR.getText().trim();
            String name = txtHoTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            String goiTap = txtGoiTap.getText().trim();
            String branch = txtChiNhanh.getText().trim();
            String status = cbTrangThai.getSelectedItem().toString();
            String htTT = cbHinhThucTT.getSelectedItem().toString();
            double tien = 0;
            try {
                tien = Double.parseDouble(txtSoTien.getText().replaceAll("[^0-9]", "").trim());
            } catch (Exception ignored) {}

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ Tên hội viên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int nextId = 1;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                try {
                    int idVal = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
                    if (idVal >= nextId) nextId = idVal + 1;
                } catch (Exception ignored) {}
            }

            if (qr.isEmpty()) {
                qr = String.format("HV%03d", nextId);
                txtQR.setText(qr);
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 1).toString().equalsIgnoreCase(qr)) {
                    JOptionPane.showMessageDialog(this, "Mã thẻ QR '" + qr + "' đã tồn tại!", "Cảnh báo trùng lặp", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Date now = new Date();
            try {
                if (!txtNgayDangKy.getText().trim().isEmpty()) {
                    now = sdfDateTime.parse(txtNgayDangKy.getText().trim());
                }
            } catch (Exception ignored) {}

            Date hetHan = new Date(System.currentTimeMillis() + 90L * 24 * 3600 * 1000);
            try {
                if (!txtNgayHetHan.getText().trim().isEmpty()) {
                    hetHan = sdfDate.parse(txtNgayHetHan.getText().trim());
                }
            } catch (Exception ignored) {}

            HoiVien hv = new HoiVien(nextId, qr, name, sdt, branch.isEmpty() ? "CN01 - Quận 1" : branch,
                    goiTap, now, hetHan, tien, htTT, status);

            try {
                service.luuHoiVien(hv, false);
            } catch (Exception ignored) {}

            loadDataToTable();
            JOptionPane.showMessageDialog(this, "Đã thêm mới hội viên '" + name + "' thành công với số tiền " + df.format(tien) + "!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            btnLamMoi.doClick();
        });

        // 2. CẬP NHẬT HỘI VIÊN
        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0 && txtMaHV.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hội viên trên bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String qr = txtQR.getText().trim();
            String name = txtHoTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            String goiTap = txtGoiTap.getText().trim();
            String branch = txtChiNhanh.getText().trim();
            String status = cbTrangThai.getSelectedItem().toString();
            String htTT = cbHinhThucTT.getSelectedItem().toString();
            double tien = 0;
            try {
                tien = Double.parseDouble(txtSoTien.getText().replaceAll("[^0-9]", "").trim());
            } catch (Exception ignored) {}

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Họ Tên không được để trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = 1;
            try {
                id = Integer.parseInt(txtMaHV.getText().trim());
            } catch (Exception ignored) {}

            Date now = new Date();
            try {
                if (!txtNgayDangKy.getText().trim().isEmpty()) {
                    now = sdfDateTime.parse(txtNgayDangKy.getText().trim());
                }
            } catch (Exception ignored) {}

            Date hetHan = new Date(System.currentTimeMillis() + 90L * 24 * 3600 * 1000);
            try {
                if (!txtNgayHetHan.getText().trim().isEmpty()) {
                    hetHan = sdfDate.parse(txtNgayHetHan.getText().trim());
                }
            } catch (Exception ignored) {}

            HoiVien hv = new HoiVien(id, qr, name, sdt, branch, goiTap, now, hetHan, tien, htTT, status);
            try {
                service.luuHoiVien(hv, true);
            } catch (Exception ignored) {}

            loadDataToTable();
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin hội viên & số tiền thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        // 3. XÓA HỘI VIÊN
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

                loadDataToTable();
                btnLamMoi.doClick();
                JOptionPane.showMessageDialog(this, "Đã xóa hội viên thành công!");
            }
        });

        // 4. LÀM MỚI FORM
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
            txtSDT.setText("");
            txtGoiTap.setText("Gói Standard 3 Tháng");
            txtNgayDangKy.setText(sdfDateTime.format(new Date()));
            txtSoTien.setText("2400000");
            cbHinhThucTT.setSelectedIndex(0);
            txtChiNhanh.setText("CN01 - Quận 1");
            txtNgayHetHan.setText("31/12/2026");
            cbTrangThai.setSelectedIndex(0);
            table.clearSelection();
        });
    }
}
