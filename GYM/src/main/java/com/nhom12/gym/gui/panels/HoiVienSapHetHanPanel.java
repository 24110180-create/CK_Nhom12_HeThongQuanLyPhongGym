package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.DataSyncManager;
import com.nhom12.gym.util.ExcelExportHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * TRUNG TÂM QUẢN LÝ HỘI VIÊN SẮP HẾT HẠN & TÁI KÝ HỢP ĐỒNG FITLIFE
 * Chuyển giao từ Trang Chủ sang Quản Lý Hội Viên & Lớp Học nhằm đảm bảo tính
 * tập trung, khoa học và chuyên nghiệp của quy trình chăm sóc khách hàng.
 */
public class HoiVienSapHetHanPanel extends JPanel {

    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private JComboBox<String> cbDaysFilter;
    private JComboBox<String> cbBranchFilter;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private JLabel lblCountCritical;
    private JLabel lblCountWarning;
    private JLabel lblCountExpired;

    public HoiVienSapHetHanPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(14, 16, 16, 16));

        initUI();
        loadData();

        DataSyncManager.addListener(type -> {
            if (type == DataSyncManager.DataType.HOI_VIEN || type == DataSyncManager.DataType.ALL) {
                loadData();
            }
        });
    }

    private void initUI() {
        // --- 1. HEADER & KPI SUMMARY ---
        JPanel pTop = new JPanel(new BorderLayout(0, 10));
        pTop.setOpaque(false);

        JPanel pTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        pTitle.setOpaque(false);

        JLabel lblTitle = new JLabel("TRUNG TÂM THEO DÕI HỘI VIÊN SẮP HẾT HẠN & TÁI KÝ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Phát hiện sớm hội viên chuẩn bị hết hạn thẻ tập • Triển khai chính sách ưu đãi tái ký kịp thời");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        pTitle.add(lblTitle);
        pTitle.add(lblSub);
        pTop.add(pTitle, BorderLayout.NORTH);

        // 3 Mini Alert Metric Cards
        JPanel pMetrics = new JPanel(new GridLayout(1, 3, 12, 0));
        pMetrics.setOpaque(false);

        lblCountCritical = new JLabel("0 Hội viên", SwingConstants.CENTER);
        lblCountWarning = new JLabel("0 Hội viên", SwingConstants.CENTER);
        lblCountExpired = new JLabel("0 Hội viên", SwingConstants.CENTER);

        pMetrics.add(createMiniCard("🔴 Khẩn cấp (≤ 3 ngày / Hôm nay)", lblCountCritical, new Color(220, 38, 38), new Color(254, 242, 242)));
        pMetrics.add(createMiniCard("🟡 Cần chăm sóc (≤ 7 ngày tới)", lblCountWarning, new Color(217, 119, 6), new Color(254, 243, 199)));
        pMetrics.add(createMiniCard("⚠️ Đã quá hạn (Mời gia hạn lại)", lblCountExpired, new Color(100, 116, 139), new Color(241, 245, 249)));

        pTop.add(pMetrics, BorderLayout.SOUTH);
        add(pTop, BorderLayout.NORTH);

        // --- 2. CENTER: TOOLBAR + TABLE ---
        JPanel pCenter = new JPanel(new BorderLayout(0, 8));
        pCenter.setBackground(Color.WHITE);
        pCenter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Toolbar
        JPanel pToolbar = new JPanel(new BorderLayout(10, 0));
        pToolbar.setOpaque(false);

        JPanel pFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pFilters.setOpaque(false);

        JLabel lDays = new JLabel("Kỳ hạn:");
        lDays.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbDaysFilter = new JComboBox<>(new String[]{
                "Sắp hết hạn trong 7 ngày tới (Ưu tiên)",
                "Sắp hết hạn trong 14 ngày tới",
                "Sắp hết hạn trong 30 ngày tới",
                "Đã quá hạn sử dụng",
                "Tất cả hội viên có hạn dùng"
        });
        cbDaysFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lBr = new JLabel("Cơ sở:");
        lBr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbBranchFilter = new JComboBox<>(new String[]{
                "Tất cả cơ sở",
                "CN01 - Cơ sở 1 (Quận 1)",
                "CN02 - Cơ sở 2 (Tân Bình)",
                "Toàn hệ thống"
        });
        cbBranchFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pFilters.add(lDays);
        pFilters.add(cbDaysFilter);
        pFilters.add(lBr);
        pFilters.add(cbBranchFilter);
        pToolbar.add(pFilters, BorderLayout.WEST);

        JPanel pActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pActions.setOpaque(false);

        JButton btnZalo = new JButton("📱 Gửi Nhắc Hạn (Zalo ZNS / SMS)");
        btnZalo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnZalo.setBackground(new Color(37, 99, 235));
        btnZalo.setForeground(Color.WHITE);
        btnZalo.setFocusPainted(false);
        btnZalo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnRenew = new JButton("💳 Gia Hạn Gói Cho Hội Viên");
        btnRenew.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRenew.setBackground(new Color(16, 185, 129));
        btnRenew.setForeground(Color.WHITE);
        btnRenew.setFocusPainted(false);
        btnRenew.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnExport = new JButton("📥 Xuất Excel");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(new Color(100, 116, 139));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pActions.add(btnZalo);
        pActions.add(btnRenew);
        pActions.add(btnExport);
        pToolbar.add(pActions, BorderLayout.EAST);

        pCenter.add(pToolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {
                "Mã HV", "Mã Thẻ QR", "Họ Và Tên", "Số Điện Thoại",
                "Gói Tập Đang Ký", "Chi Nhánh", "Ngày Hết Hạn", "Thời Hạn Còn Lại", "Mức Độ Cảnh Báo"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Render màu theo mức độ cảnh báo
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(new Color(15, 23, 42));

                    if (col == 8 && value != null) {
                        String st = value.toString();
                        if (st.contains("KHẨN CẤP") || st.contains("HÔM NAY")) {
                            c.setForeground(new Color(220, 38, 38));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if (st.contains("SẮP HẾT HẠN") || st.contains("CẤP 1")) {
                            c.setForeground(new Color(217, 119, 6));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(new Color(100, 116, 139));
                        }
                    }
                }
                return c;
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    moDialogGiaHanHoiVien();
                }
            }
        });

        pCenter.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pCenter, BorderLayout.CENTER);

        // Listeners
        cbDaysFilter.addActionListener(e -> loadData());
        cbBranchFilter.addActionListener(e -> loadData());

        btnZalo.addActionListener(e -> xuLyGuiTinNhanZalo());
        btnRenew.addActionListener(e -> moDialogGiaHanHoiVien());
        btnExport.addActionListener(e -> ExcelExportHelper.exportTableToExcel(this, table, "BaoCao_HoiVien_SapHetHan"));
    }

    private JPanel createMiniCard(String title, JLabel lblVal, Color fg, Color bg) {
        JPanel card = new JPanel(new BorderLayout(4, 2));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lT = new JLabel(title);
        lT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lT.setForeground(fg);

        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblVal.setForeground(fg);

        card.add(lT, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        return card;
    }

    public void loadData() {
        model.setRowCount(0);
        List<HoiVien> all = hoiVienDAO.layTatCa();
        if (all == null) return;

        int daysFilterIdx = cbDaysFilter.getSelectedIndex();
        int maxDays = 7;
        if (daysFilterIdx == 1) maxDays = 14;
        else if (daysFilterIdx == 2) maxDays = 30;
        else if (daysFilterIdx == 3) maxDays = 0; // Quá hạn
        else if (daysFilterIdx == 4) maxDays = 365;

        String branchFilter = (String) cbBranchFilter.getSelectedItem();
        long now = System.currentTimeMillis();

        int critical = 0;
        int warning = 0;
        int expired = 0;

        for (HoiVien hv : all) {
            Date exp = hv.getNgayHetHan();
            if (exp == null) continue;

            // Lọc cơ sở
            if (branchFilter != null && !branchFilter.startsWith("Tất cả")) {
                if (hv.getChiNhanhDangKy() != null && !hv.getChiNhanhDangKy().contains(branchFilter.substring(0, 4))) {
                    continue;
                }
            }

            long diff = exp.getTime() - now;
            long remainDays = diff / (1000L * 60 * 60 * 24);

            if (remainDays < 0) expired++;
            else if (remainDays <= 3) critical++;
            else if (remainDays <= 7) warning++;

            boolean match = false;
            if (daysFilterIdx == 3) {
                match = (remainDays < 0);
            } else if (daysFilterIdx == 4) {
                match = true;
            } else {
                match = (remainDays <= maxDays);
            }

            if (match) {
                String remainText;
                String alertLevel;
                if (remainDays < 0) {
                    remainText = "Quá hạn " + Math.abs(remainDays) + " ngày";
                    alertLevel = "🔴 ĐÃ QUÁ HẠN (CẤP 3)";
                } else if (remainDays == 0) {
                    remainText = "Hết hạn hôm nay!";
                    alertLevel = "🔴 KHẨN CẤP: HÔM NAY";
                } else if (remainDays <= 3) {
                    remainText = "Còn " + remainDays + " ngày";
                    alertLevel = "🔴 KHẨN CẤP (CẤP 1)";
                } else if (remainDays <= 7) {
                    remainText = "Còn " + remainDays + " ngày";
                    alertLevel = "🟡 SẮP HẾT HẠN (7 NGÀY)";
                } else {
                    remainText = "Còn " + remainDays + " ngày";
                    alertLevel = "🟢 TRONG HẠN ĐỊNH";
                }

                model.addRow(new Object[]{
                        hv.getMaHoiVien(),
                        hv.getMaTheQR() != null ? hv.getMaTheQR() : ("HV" + hv.getMaHoiVien()),
                        hv.getHoTen(),
                        hv.getSoDienThoai() != null ? hv.getSoDienThoai() : "--",
                        hv.getGoiTapDangKy() != null ? hv.getGoiTapDangKy() : "Gói Tiêu Chuẩn",
                        hv.getChiNhanhDangKy() != null ? hv.getChiNhanhDangKy() : "CN01 - Quận 1",
                        sdf.format(exp),
                        remainText,
                        alertLevel
                });
            }
        }

        lblCountCritical.setText(critical + " Hội viên");
        lblCountWarning.setText(warning + " Hội viên");
        lblCountExpired.setText(expired + " Hội viên");
    }

    private void moDialogGiaHanHoiVien() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hội viên trong bảng để gia hạn!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(sel);
        int maHV = (int) model.getValueAt(modelRow, 0);
        String hoTen = (String) model.getValueAt(modelRow, 2);

        HoiVien hv = null;
        for (HoiVien h : hoiVienDAO.layTatCa()) {
            if (h.getMaHoiVien() == maHV) {
                hv = h;
                break;
            }
        }
        if (hv == null) return;

        String[] options = {
                "Gia hạn 1 Tháng (500,000 VNĐ)",
                "Gia hạn 3 Tháng (1,350,000 VNĐ - Giảm 10%)",
                "Gia hạn 6 Tháng (2,500,000 VNĐ - Tặng 1 tháng)",
                "Gia hạn 12 Tháng VIP (4,500,000 VNĐ - Giảm 25%)"
        };

        int choice = JOptionPane.showOptionDialog(this,
                "Chọn gói thời gian gia hạn tiếp theo cho hội viên [" + hoTen + "]:",
                "Gia Hạn Thẻ Tập FitLife",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (choice < 0) return;

        Calendar cal = Calendar.getInstance();
        Date curExp = hv.getNgayHetHan();
        if (curExp != null && curExp.after(new Date())) {
            cal.setTime(curExp);
        } else {
            cal.setTime(new Date());
        }

        double fee = 500000;
        String pkg = "Gói 1 Tháng";
        if (choice == 0) {
            cal.add(Calendar.MONTH, 1);
            fee = 500000;
            pkg = "Gói 1 Tháng Tiêu Chuẩn";
        } else if (choice == 1) {
            cal.add(Calendar.MONTH, 3);
            fee = 1350000;
            pkg = "Gói 3 Tháng Tái Ký";
        } else if (choice == 2) {
            cal.add(Calendar.MONTH, 7); // 6 + 1 tang
            fee = 2500000;
            pkg = "Gói 6 Tháng Tặng 1";
        } else if (choice == 3) {
            cal.add(Calendar.MONTH, 12);
            fee = 4500000;
            pkg = "Gói VIP Tái Ký 1 Năm";
        }

        Date newExp = cal.getTime();
        hv.setNgayHetHan(newExp);
        hv.setTrangThai("Active");
        hv.setGoiTapDangKy(pkg);
        if (hv.getSoTienDaThanhToan() != null) {
            hv.setSoTienDaThanhToan(hv.getSoTienDaThanhToan() + fee);
        } else {
            hv.setSoTienDaThanhToan(fee);
        }

        hoiVienDAO.capNhat(hv);
        DataSyncManager.fireDataChanged(DataSyncManager.DataType.HOI_VIEN);
        loadData();

        JOptionPane.showMessageDialog(this,
                "<html><body>" +
                        "<h3 style='color: #10B981;'>GIA HẠN THÀNH CÔNG!</h3>" +
                        "<p>Hội viên: <b>" + hoTen + "</b></p>" +
                        "<p>Gói gia hạn: <b>" + pkg + "</b></p>" +
                        "<p>Hạn sử dụng mới: <b>" + sdf.format(newExp) + "</b></p>" +
                        "</body></html>",
                "Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyGuiTinNhanZalo() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hội viên để gửi tin nhắn nhắc hạn!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(sel);
        String hoTen = (String) model.getValueAt(modelRow, 2);
        String sdt = (String) model.getValueAt(modelRow, 3);
        String han = (String) model.getValueAt(modelRow, 6);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận gửi tin nhắn Zalo ZNS / SMS tự động nhắc tái ký thẻ tập tới:\n" +
                        "• Hội viên: " + hoTen + "\n" +
                        "• Số điện thoại: " + sdt + "\n" +
                        "• Hạn thẻ: " + han + "\n\n" +
                        "Nội dung: 'FitLife Gym kinh chao quy khach " + hoTen + ", the tap cua ban se het han vao " + han + ". Nhanh tay tai ky trong tuan nay de nhan uu dai giam 15%!'",
                "Gửi Thông Báo Tự Động", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Đã gửi tin nhắn nhắc tái ký thành công qua Cổng Zalo ZNS tới hội viên [" + hoTen + " - " + sdt + "]!",
                    "Gửi Thành Công", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
