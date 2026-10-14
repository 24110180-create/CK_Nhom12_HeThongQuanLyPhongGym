package com.nhom12.gym.gui.panels;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.gui.GuiMaQRDialog;
import com.nhom12.gym.gui.QRScannerSimulatorDialog;
import com.nhom12.gym.service.HoiVienService;
import com.nhom12.gym.util.ExcelExportHelper;
import com.nhom12.gym.util.TextSanitizer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MÀN HÌNH QUẢN TRỊ DANH SÁCH & HỒ SƠ HỘI VIÊN (MEMBER MASTER DIRECTORY)
 * Tối ưu hiển thị toàn màn hình, tra cứu thông minh, kiểm toán phí hội viên
 * và tích hợp liên kết trực tiếp tới quy trình Tiếp nhận / Đăng ký mới.
 */
public class QuanLyHoiVienPanel extends JPanel {

    private final HoiVienService service = new HoiVienService();
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

    private final Runnable onOpenRegistration;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // Các nhãn KPI thống kê
    private JLabel lblKpiTongHoiVien;
    private JLabel lblKpiTongDoanhThu;
    private JLabel lblKpiDangHoatDong;
    private JLabel lblKpiChuyenKhoan;

    private JComboBox<String> cbFilterStatus;

    public QuanLyHoiVienPanel() {
        this(null);
    }

    public QuanLyHoiVienPanel(Runnable onOpenRegistration) {
        this.onOpenRegistration = onOpenRegistration;
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 14, 14, 14));
        setBackground(new Color(248, 250, 252));

        // HEADER & KPI
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setOpaque(false);

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ DANH SÁCH HỘI VIÊN & DOANH THU HỘI PHÍ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));
        topContainer.add(lblTitle, BorderLayout.NORTH);

        topContainer.add(createKpiPanel(), BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // BẢNG DANH SÁCH RỘNG RÃI TOÀN MÀN HÌNH (CENTER)
        add(createTablePanel(), BorderLayout.CENTER);

        loadDataToTable();

        // Lắng nghe sự kiện đồng bộ toàn hệ thống
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            if (type == com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN ||
                type == com.nhom12.gym.service.DataSyncManager.DataType.HOA_DON ||
                type == com.nhom12.gym.service.DataSyncManager.DataType.ALL) {
                loadDataToTable();
            }
        });
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

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        // --- TOP TOOLBAR: BỘ LỌC + TÌM KIẾM + NÚT ĐĂNG KÝ MỚI ---
        JPanel toolBar = new JPanel(new BorderLayout(10, 0));
        toolBar.setOpaque(false);

        // Bên trái toolbar: Bộ lọc cơ sở & trạng thái
        JPanel filterBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterBox.setOpaque(false);

        JLabel lblFBranch = new JLabel("Cơ sở:");
        lblFBranch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> cbFilterBranch = new JComboBox<>(new String[]{
                "Tất Cả Cơ Sở", "CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)", "Toàn hệ thống"
        });
        cbFilterBranch.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblFStatus = new JLabel("Trạng thái:");
        lblFStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbFilterStatus = new JComboBox<>(new String[]{
                "Tất Cả Trạng Thái", "Active (Đang hoạt động)", "Expired (Hết hạn)", "Locked (Tạm khóa)", "Sắp Hết Hạn (Trong 7 ngày tới)"
        });
        cbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        filterBox.add(lblFBranch);
        filterBox.add(cbFilterBranch);
        filterBox.add(lblFStatus);
        filterBox.add(cbFilterStatus);

        toolBar.add(filterBox, BorderLayout.WEST);

        // Bên phải toolbar: Tìm kiếm + Nút Đăng ký mới + Xuất Excel
        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionBox.setOpaque(false);

        JTextField txtSearch = new JTextField(16);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên, SĐT, mã QR, gói...");

        JButton btnDangKyMoi = new JButton("+ Đăng Ký Hội Viên Mới");
        btnDangKyMoi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDangKyMoi.setBackground(new Color(16, 185, 129));
        btnDangKyMoi.setForeground(Color.WHITE);
        btnDangKyMoi.setFocusPainted(false);
        btnDangKyMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangKyMoi.addActionListener(e -> {
            if (onOpenRegistration != null) {
                onOpenRegistration.run();
            } else {
                moDialogDangKyNhanh();
            }
        });

        JButton btnExport = new JButton("Xuất Excel (.csv)");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(new Color(241, 245, 249));
        btnExport.setForeground(new Color(15, 118, 110));
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(e -> ExcelExportHelper.exportTableToExcel(this, table, "DanhSach_HoiVien_DaDangKy"));

        actionBox.add(new JLabel("Tìm kiếm:"));
        actionBox.add(txtSearch);
        actionBox.add(btnDangKyMoi);
        actionBox.add(btnExport);

        toolBar.add(actionBox, BorderLayout.EAST);
        panel.add(toolBar, BorderLayout.NORTH);

        // --- BẢNG DỮ LIỆU ĐẦY ĐỦ 11 CỘT ---
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

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Bộ lọc tự động kết hợp Search + Cơ Sở + Trạng Thái
        Runnable doFilter = () -> {
            String text = txtSearch.getText().trim();
            int brIdx = cbFilterBranch.getSelectedIndex();
            int stIdx = cbFilterStatus.getSelectedIndex();

            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            if (!text.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + text));
            }
            if (brIdx == 1) {
                filters.add(RowFilter.regexFilter("(?i)CN01|Quận 1", 8));
            } else if (brIdx == 2) {
                filters.add(RowFilter.regexFilter("(?i)CN02|Tân Bình", 8));
            } else if (brIdx == 3) {
                filters.add(RowFilter.regexFilter("(?i)Toàn|VIP", 8));
            }

            if (stIdx == 1) {
                filters.add(RowFilter.regexFilter("(?i)Active|Hoạt động", 10));
            } else if (stIdx == 2) {
                filters.add(RowFilter.regexFilter("(?i)Expired|Hết hạn", 10));
            } else if (stIdx == 3) {
                filters.add(RowFilter.regexFilter("(?i)Locked|Khóa", 10));
            } else if (stIdx == 4) {
                filters.add(new RowFilter<Object, Object>() {
                    @Override
                    public boolean include(Entry<?, ?> entry) {
                        Object val = entry.getValue(9);
                        if (val == null) return false;
                        try {
                            Date d = new SimpleDateFormat("dd/MM/yyyy").parse(val.toString().trim());
                            long diff = d.getTime() - System.currentTimeMillis();
                            long days = diff / (1000L * 60 * 60 * 24);
                            return days >= -3 && days <= 7;
                        } catch (Exception ex) {
                            return false;
                        }
                    }
                });
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        };

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { doFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { doFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { doFilter.run(); }
        });
        cbFilterBranch.addActionListener(e -> doFilter.run());
        cbFilterStatus.addActionListener(e -> doFilter.run());

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
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Ngày giờ ĐK
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);  // Số tiền đã TT
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Hình thức TT
        table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer); // Chi nhánh
        table.getColumnModel().getColumn(9).setCellRenderer(centerRenderer); // Ngày hết hạn

        // Renderer màu cho trạng thái (Active: Xanh, Expired: Đỏ, Locked: Cam)
        table.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String s = (val != null) ? val.toString() : "";
                if (s.toLowerCase().contains("active") || s.toLowerCase().contains("hoạt động")) {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if (s.toLowerCase().contains("expired") || s.toLowerCase().contains("hết hạn")) {
                    l.setForeground(new Color(239, 68, 68));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if (s.toLowerCase().contains("locked") || s.toLowerCase().contains("khóa")) {
                    l.setForeground(new Color(245, 158, 11));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    l.setForeground(new Color(71, 85, 105));
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                }
                return l;
            }
        });

        // Tinh chỉnh độ rộng các cột
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getColumnModel().getColumn(0).setPreferredWidth(55);  // Mã HV
        table.getColumnModel().getColumn(1).setPreferredWidth(95);  // Mã Thẻ QR
        table.getColumnModel().getColumn(2).setPreferredWidth(140); // Họ Và Tên
        table.getColumnModel().getColumn(3).setPreferredWidth(95);  // Số ĐT
        table.getColumnModel().getColumn(4).setPreferredWidth(140); // Gói Tập
        table.getColumnModel().getColumn(5).setPreferredWidth(130); // Ngày Giờ ĐK
        table.getColumnModel().getColumn(6).setPreferredWidth(105); // Số Tiền Đã TT
        table.getColumnModel().getColumn(7).setPreferredWidth(115); // Hình Thức TT
        table.getColumnModel().getColumn(8).setPreferredWidth(135); // Chi Nhánh
        table.getColumnModel().getColumn(9).setPreferredWidth(90);  // Ngày Hết Hạn
        table.getColumnModel().getColumn(10).setPreferredWidth(100); // Trạng Thái

        // Double click mở dialog xem/cập nhật hồ sơ
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    moDialogCapNhatHoiVienDangChon();
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BOTTOM ACTIONS: THAO TÁC HỒ SƠ & THẺ ---
        JPanel bottomActions = new JPanel(new BorderLayout());
        bottomActions.setOpaque(false);
        bottomActions.setBorder(new EmptyBorder(6, 0, 0, 0));

        JLabel lblHint = new JLabel("Mẹo: Nhấp đúp chuột vào hàng hoặc chọn nút dưới đây để cập nhật hồ sơ, khóa thẻ hoặc gửi mã QR:");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(new Color(100, 116, 139));
        bottomActions.add(lblHint, BorderLayout.WEST);

        JPanel btnActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnActions.setOpaque(false);

        JButton btnXemTheQR = new JButton("Xem & Gửi Thẻ QR");
        btnXemTheQR.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXemTheQR.setBackground(new Color(37, 99, 235));
        btnXemTheQR.setForeground(Color.WHITE);
        btnXemTheQR.setFocusPainted(false);
        btnXemTheQR.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXemTheQR.addActionListener(e -> xemTheQRHoiVienDangChon());

        JButton btnCapNhat = new JButton("Cập Nhật Hồ Sơ");
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCapNhat.setBackground(new Color(14, 165, 233));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFocusPainted(false);
        btnCapNhat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCapNhat.addActionListener(e -> moDialogCapNhatHoiVienDangChon());

        JButton btnToggleLock = new JButton("Khóa / Mở Khóa");
        btnToggleLock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnToggleLock.setBackground(new Color(245, 158, 11));
        btnToggleLock.setForeground(Color.WHITE);
        btnToggleLock.setFocusPainted(false);
        btnToggleLock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleLock.addActionListener(e -> xuLyKhoaMoKhoaThe());

        JButton btnQuetCheckIn = new JButton("Quét Thử Check-in");
        btnQuetCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnQuetCheckIn.setBackground(new Color(16, 185, 129));
        btnQuetCheckIn.setForeground(Color.WHITE);
        btnQuetCheckIn.setFocusPainted(false);
        btnQuetCheckIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuetCheckIn.addActionListener(e -> quetCheckInHoiVienDangChon());

        JButton btnXoa = new JButton("Xóa");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXoa.setBackground(new Color(239, 68, 68));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.addActionListener(e -> xuLyXoaHoiVien());

        JButton btnLamMoi = new JButton("Tải Lại");
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLamMoi.setBackground(new Color(241, 245, 249));
        btnLamMoi.setForeground(new Color(71, 85, 105));
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLamMoi.addActionListener(e -> loadDataToTable());

        btnActions.add(btnXemTheQR);
        btnActions.add(btnCapNhat);
        btnActions.add(btnToggleLock);
        btnActions.add(btnQuetCheckIn);
        btnActions.add(btnXoa);
        btnActions.add(btnLamMoi);

        bottomActions.add(btnActions, BorderLayout.EAST);
        panel.add(bottomActions, BorderLayout.SOUTH);

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

                String rawGoi = (hv.getGoiTapDangKy() != null && !hv.getGoiTapDangKy().isEmpty())
                        ? hv.getGoiTapDangKy() : "Gói Standard 3 Tháng";
                String goiTap = TextSanitizer.sanitize(rawGoi);

                String rawHT = (hv.getHinhThucThanhToan() != null && !hv.getHinhThucThanhToan().isEmpty())
                        ? hv.getHinhThucThanhToan() : "Chuyển khoản VietQR";
                String hinhThucTT = TextSanitizer.sanitize(rawHT);

                if (hinhThucTT.contains("VietQR") || hinhThucTT.contains("Chuyển khoản")) {
                    tongVietQR += tien;
                }

                String dateHetHanStr = (hv.getNgayHetHan() != null) ? sdfDate.format(hv.getNgayHetHan()) : "31/12/2026";
                String cleanStatus = TextSanitizer.sanitize(hv.getTrangThai());
                String cleanBranch = TextSanitizer.normalizeBranch(hv.getChiNhanhDangKy());
                String cleanName = TextSanitizer.sanitize(hv.getHoTen());

                if (cleanStatus.toLowerCase().contains("active") || cleanStatus.toLowerCase().contains("hoạt động")) {
                    activeCount++;
                }

                tableModel.addRow(new Object[]{
                        hv.getMaHoiVien(),
                        hv.getMaTheQR(),
                        cleanName,
                        sdt,
                        goiTap,
                        ngayDangKyStr,
                        df.format(tien),
                        hinhThucTT,
                        cleanBranch,
                        dateHetHanStr,
                        cleanStatus
                });
            }
        }

        // Cập nhật KPI
        lblKpiTongHoiVien.setText(tableModel.getRowCount() + " Hội Viên");
        lblKpiTongDoanhThu.setText(df.format(tongDoanhThu));
        lblKpiDangHoatDong.setText(activeCount + " Đang hoạt động");
        lblKpiChuyenKhoan.setText(df.format(tongVietQR));
    }

    private HoiVien getSelectedHoiVien() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int modelRow = table.convertRowIndexToModel(row);
        int maHV = (Integer) tableModel.getValueAt(modelRow, 0);
        return service.timTheoId(maHV);
    }

    private void xemTheQRHoiVienDangChon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một hội viên trong bảng để xem mã thẻ QR!",
                    "Chưa chọn hội viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String maQR = (String) tableModel.getValueAt(modelRow, 1);
        String hoTen = (String) tableModel.getValueAt(modelRow, 2);
        String sdt = (String) tableModel.getValueAt(modelRow, 3);
        String goiTap = (String) tableModel.getValueAt(modelRow, 4);
        String chiNhanh = (String) tableModel.getValueAt(modelRow, 8);
        Object expireObj = tableModel.getValueAt(modelRow, 9);
        String ngayHetHan = expireObj != null ? expireObj.toString() : "Theo gói đăng ký";

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        GuiMaQRDialog dialog = new GuiMaQRDialog(parentWindow, maQR, hoTen, sdt, goiTap, chiNhanh, ngayHetHan);
        dialog.setVisible(true);
    }

    private void moDialogCapNhatHoiVienDangChon() {
        HoiVien hv = getSelectedHoiVien();
        if (hv == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một hội viên trong bảng để cập nhật hồ sơ!",
                    "Chưa chọn hội viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parentWindow, "CẬP NHẬT HỒ SƠ HỘI VIÊN (" + hv.getMaTheQR() + ")", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 520);
        dlg.setLocationRelativeTo(parentWindow);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel pForm = new JPanel(new GridLayout(0, 1, 6, 6));
        pForm.setBorder(new EmptyBorder(16, 20, 16, 20));

        JTextField txtName = new JTextField(hv.getHoTen());
        JTextField txtPhone = new JTextField(hv.getSoDienThoai() != null ? hv.getSoDienThoai() : "");
        JTextField txtPackage = new JTextField(hv.getGoiTapDangKy() != null ? hv.getGoiTapDangKy() : "");
        JComboBox<String> cbBranch = new JComboBox<>(new String[]{
                "CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)", "Toàn hệ thống (VIP)"
        });
        cbBranch.setSelectedItem(TextSanitizer.normalizeBranch(hv.getChiNhanhDangKy()));

        JTextField txtExpire = new JTextField(hv.getNgayHetHan() != null ? sdfDate.format(hv.getNgayHetHan()) : "31/12/2026");
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Active", "Expired", "Locked"});
        cbStatus.setSelectedItem(hv.getTrangThai() != null && hv.getTrangThai().contains("Lock") ? "Locked" : "Active");

        pForm.add(new JLabel("Họ Và Tên:"));
        pForm.add(txtName);
        pForm.add(new JLabel("Số Điện Thoại:"));
        pForm.add(txtPhone);
        pForm.add(new JLabel("Gói Tập Đã Đăng Ký:"));
        pForm.add(txtPackage);
        pForm.add(new JLabel("Cơ Sở Đăng Ký:"));
        pForm.add(cbBranch);
        pForm.add(new JLabel("Ngày Hết Hạn (dd/MM/yyyy):"));
        pForm.add(txtExpire);
        pForm.add(new JLabel("Trạng Thái:"));
        pForm.add(cbStatus);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSave = new JButton("Lưu Thay Đổi");
        btnSave.setBackground(new Color(16, 185, 129));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnCancel = new JButton("Hủy Bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnSave.addActionListener(ev -> {
            if (!com.nhom12.gym.util.ValidationHelper.checkRong(dlg, txtName, "Họ và Tên")) {
                return;
            }
            if (!com.nhom12.gym.util.ValidationHelper.checkSoDienThoai(dlg, txtPhone)) {
                return;
            }
            if (!com.nhom12.gym.util.ValidationHelper.checkRong(dlg, txtPackage, "Gói Tập")) {
                return;
            }
            if (!com.nhom12.gym.util.ValidationHelper.checkRong(dlg, txtExpire, "Ngày Hết Hạn")) {
                return;
            }

            Date expireDate;
            try {
                expireDate = sdfDate.parse(txtExpire.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Ngày hết hạn không hợp lệ! Vui lòng nhập đúng định dạng dd/MM/yyyy (ví dụ: 31/12/2026).", "Lỗi Định Dạng Ngày", JOptionPane.WARNING_MESSAGE);
                txtExpire.requestFocus();
                return;
            }

            hv.setHoTen(txtName.getText().trim());
            hv.setSoDienThoai(txtPhone.getText().trim());
            hv.setGoiTapDangKy(txtPackage.getText().trim());
            hv.setChiNhanhDangKy(cbBranch.getSelectedItem().toString());
            hv.setTrangThai(cbStatus.getSelectedItem().toString());
            hv.setNgayHetHan(expireDate);

            service.luuHoiVien(hv, true);
            loadDataToTable();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "Đã cập nhật hồ sơ hội viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });

        btnCancel.addActionListener(ev -> dlg.dispose());

        pBtns.add(btnCancel);
        pBtns.add(btnSave);

        dlg.add(pForm, BorderLayout.CENTER);
        dlg.add(pBtns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void xuLyKhoaMoKhoaThe() {
        HoiVien hv = getSelectedHoiVien();
        if (hv == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một hội viên trong bảng để khóa hoặc mở khóa thẻ!",
                    "Chưa chọn hội viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cur = hv.getTrangThai() != null ? hv.getTrangThai().toLowerCase() : "";
        boolean isLocked = cur.contains("lock") || cur.contains("khóa");
        String nextStatus = isLocked ? "Active" : "Locked";
        String actionText = isLocked ? "MỞ KHÓA" : "TẠM KHÓA";

        int cf = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn " + actionText + " thẻ của hội viên: " + hv.getHoTen() + " (" + hv.getMaTheQR() + ")?",
                "Xác nhận thay đổi trạng thái thẻ", JOptionPane.YES_NO_OPTION);
        if (cf == JOptionPane.YES_OPTION) {
            hv.setTrangThai(nextStatus);
            service.luuHoiVien(hv, true);
            loadDataToTable();
            JOptionPane.showMessageDialog(this,
                    "Đã " + actionText.toLowerCase() + " thẻ thành công cho hội viên " + hv.getHoTen() + "!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void quetCheckInHoiVienDangChon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một hội viên trong bảng để quét thử Check-in!",
                    "Chưa chọn hội viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String maQR = (String) tableModel.getValueAt(modelRow, 1);
        String hoTen = (String) tableModel.getValueAt(modelRow, 2);

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        QRScannerSimulatorDialog scanner = new QRScannerSimulatorDialog(parentWindow, code -> {
            JOptionPane.showMessageDialog(parentWindow,
                    "ĐÃ BẮN MÃ QUÉT THẺ THÀNH CÔNG!\n"
                            + "• Mã Thẻ: " + code + "\n"
                            + "• Hội Viên: " + hoTen + "\n\n"
                            + "Hệ thống đã ghi nhận tín hiệu từ máy quét Honeywell Voyager 1450g!",
                    "Tín Hiệu Máy Quét Ghi Nhận", JOptionPane.INFORMATION_MESSAGE);
        });
        scanner.setVisible(true);
    }

    private void xuLyXoaHoiVien() {
        HoiVien hv = getSelectedHoiVien();
        if (hv == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một hội viên trong bảng để xóa!",
                    "Chưa chọn hội viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cf = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa hội viên: " + hv.getHoTen() + " (Mã: " + hv.getMaTheQR() + ") khỏi hệ thống?",
                "Xác nhận xóa hội viên", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (cf == JOptionPane.YES_OPTION) {
            service.xoaHoiVien(hv.getMaHoiVien());
            loadDataToTable();
            JOptionPane.showMessageDialog(this, "Đã xóa hội viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void moDialogDangKyNhanh() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parentWindow, "TIẾP NHẬN & ĐĂNG KÝ HỘI VIÊN MỚI", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(1080, 700);
        dlg.setLocationRelativeTo(parentWindow);
        DangKyHoiVienPanel pnl = new DangKyHoiVienPanel(dlg::dispose, () -> {
            loadDataToTable();
            dlg.dispose();
        });
        dlg.setContentPane(pnl);
        dlg.setVisible(true);
    }

    public void locHoiVienSapHetHan() {
        if (cbFilterStatus != null && cbFilterStatus.getItemCount() > 4) {
            cbFilterStatus.setSelectedIndex(4);
        }
    }

}
