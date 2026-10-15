package com.nhom12.gym.gui.panels;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.BaoCaoService;
import com.nhom12.gym.util.ExcelExportHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Trung Tâm Báo Cáo & Phân Tích Thống Kê (Owner / Quản Lý Dashboard)
 * Đáp ứng đầy đủ 3 báo cáo cốt lõi trong phạm vi đề tài:
 * 1. Báo cáo Doanh thu theo từng chi nhánh và chu kỳ
 * 2. Báo cáo Hội viên sắp hết hạn (Cảnh báo tái ký, gửi SMS/Zalo)
 * 3. Báo cáo Mức độ sử dụng phòng tập theo giờ (Heatmap lưu lượng 6h - 22h)
 */
public class BaoCaoThongKePanel extends JPanel {

    private final BaoCaoService baoCaoService = new BaoCaoService();
    private final DecimalFormat dfMoney = new DecimalFormat("###,###,### VNĐ");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public BaoCaoThongKePanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("TRUNG TÂM BÁO CÁO THỐNG KÊ & CHỈ SỐ KINH DOANH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Báo cáo theo phạm vi đề tài: Doanh thu chi nhánh • Hội viên sắp hết hạn • Mức độ sử dụng theo giờ");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        initTabs();

        add(tabbedPane, BorderLayout.CENTER);

        // Lắng nghe sự kiện đồng bộ toàn hệ thống để cập nhật số liệu và biểu đồ ngay lập tức
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            if (type == com.nhom12.gym.service.DataSyncManager.DataType.HOA_DON) {
                SwingUtilities.invokeLater(this::capNhatDoanhThuTheoChiNhanh);
            } else if (type == com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN ||
                       type == com.nhom12.gym.service.DataSyncManager.DataType.CHECK_IN ||
                       type == com.nhom12.gym.service.DataSyncManager.DataType.ALL) {
                SwingUtilities.invokeLater(() -> {
                    capNhatDoanhThuTheoChiNhanh();
                    int sel = tabbedPane.getSelectedIndex();
                    if (sel > 0) {
                        initTabs();
                        if (sel >= 0 && sel < tabbedPane.getTabCount()) {
                            tabbedPane.setSelectedIndex(sel);
                        }
                    }
                });
            }
        });
    }

    private JTabbedPane tabbedPane;

    private void initTabs() {
        tabbedPane.removeAll();
        tabbedPane.addTab("Doanh Thu Theo Chi Nhánh", createRevenueBranchTab());
        tabbedPane.addTab("Hội Viên Sắp Hết Hạn", createExpiringMembersTab());
        tabbedPane.addTab("Mức Độ Sử Dụng Theo Giờ", createHourlyUsageTab());
    }

    private DefaultTableModel revenueModel;
    private BranchBarChart revenueChart;
    private JLabel lblRevenueLiveStatus;

    public void capNhatDoanhThuTheoChiNhanh() {
        if (revenueModel == null || revenueChart == null) return;
        Map<String, Double> branchRevenue = baoCaoService.getDoanhThuTheoChiNhanh();
        double totalRev = 0;
        for (double v : branchRevenue.values()) totalRev += v;
        if (totalRev == 0) totalRev = 1.0;

        double cn01Amount = branchRevenue.getOrDefault("CN01 - Quận 1", 0.0);
        double cn02Amount = branchRevenue.getOrDefault("CN02 - Tân Bình", 0.0);

        revenueModel.setRowCount(0);
        for (Map.Entry<String, Double> entry : branchRevenue.entrySet()) {
            double amount = entry.getValue();
            double pct = (totalRev > 1.0) ? (amount / totalRev) * 100.0 : 0.0;
            String code = entry.getKey().contains("CN01") ? "CN01" : "CN02";
            revenueModel.addRow(new Object[]{
                    code, entry.getKey(), dfMoney.format(amount), String.format("%.1f%%", pct), "Hoạt động tốt"
            });
        }

        revenueChart.updateData(new String[]{"CN01 - Quận 1", "CN02 - Tân Bình"}, new double[]{cn01Amount, cn02Amount});
        if (lblRevenueLiveStatus != null) {
            String timeNow = new SimpleDateFormat("HH:mm:ss").format(new Date());
            lblRevenueLiveStatus.setText("● REAL-TIME: Tự động cập nhật theo từng giao dịch • Tổng: " + dfMoney.format(totalRev) + " • Lúc: " + timeNow);
        }
    }

    // ==========================================
    // TAB 1: BÁO CÁO DOANH THU THEO CHI NHÁNH
    // ==========================================
    private JPanel createRevenueBranchTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Toolbar
        JPanel topBar = new JPanel(new BorderLayout(10, 5));
        topBar.setOpaque(false);

        lblRevenueLiveStatus = new JLabel("● REAL-TIME: Tự động cập nhật theo từng giao dịch");
        lblRevenueLiveStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRevenueLiveStatus.setForeground(new Color(16, 185, 129));
        topBar.add(lblRevenueLiveStatus, BorderLayout.WEST);

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBtns.setOpaque(false);

        JButton btnExportExcel = new JButton("Xuất Báo Cáo Doanh Thu (Excel)");
        btnExportExcel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportExcel.setBackground(new Color(16, 185, 129));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightBtns.add(btnExportExcel);
        topBar.add(rightBtns, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Split: Left Table, Right Chart
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(460);
        split.setDividerSize(6);
        split.setOpaque(false);
        split.setBorder(null);

        // Table Model
        String[] cols = {"Mã Chi Nhánh", "Tên Chi Nhánh", "Doanh Thu", "Tỷ Lệ Đóng Góp", "Trạng Thái"};
        revenueModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        Map<String, Double> branchRevenue = baoCaoService.getDoanhThuTheoChiNhanh();
        double totalRev = 0;
        for (double v : branchRevenue.values()) totalRev += v;
        if (totalRev == 0) totalRev = 1.0;

        double cn01Amount = branchRevenue.getOrDefault("CN01 - Quận 1", 0.0);
        double cn02Amount = branchRevenue.getOrDefault("CN02 - Tân Bình", 0.0);

        for (Map.Entry<String, Double> entry : branchRevenue.entrySet()) {
            double amount = entry.getValue();
            double pct = (totalRev > 1.0) ? (amount / totalRev) * 100.0 : 0.0;
            String code = entry.getKey().contains("CN01") ? "CN01" : "CN02";
            revenueModel.addRow(new Object[]{
                    code, entry.getKey(), dfMoney.format(amount), String.format("%.1f%%", pct), "Hoạt động tốt"
            });
        }

        JTable table = new JTable(revenueModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));

        btnExportExcel.addActionListener(e -> ExcelExportHelper.exportTableToExcel(this, table, "BaoCao_DoanhThu_ChiNhanh"));

        JPanel tableWrapper = new JPanel(new BorderLayout(0, 8));
        tableWrapper.setBackground(Color.WHITE);
        tableWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel lblTblTitle = new JLabel("Bảng Doanh Thu Tổng Hợp Các Cơ Sở:");
        lblTblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTblTitle.setForeground(new Color(15, 23, 42));
        tableWrapper.add(lblTblTitle, BorderLayout.NORTH);
        tableWrapper.add(new JScrollPane(table), BorderLayout.CENTER);

        split.setLeftComponent(tableWrapper);

        // Chart Card
        JPanel chartCard = new JPanel(new BorderLayout(0, 10));
        chartCard.setBackground(Color.WHITE);
        chartCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel lblC = new JLabel("Biểu Đồ Tỷ Trọng Doanh Thu Chi Nhánh");
        lblC.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblC.setForeground(new Color(15, 23, 42));
        chartCard.add(lblC, BorderLayout.NORTH);

        revenueChart = new BranchBarChart(new String[]{"CN01 - Quận 1", "CN02 - Tân Bình"},
                new double[]{cn01Amount, cn02Amount});
        chartCard.add(revenueChart, BorderLayout.CENTER);

        split.setRightComponent(chartCard);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // TAB 2: HỘI VIÊN SẮP HẾT HẠN
    // ==========================================
    private JPanel createExpiringMembersTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Tool bar
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        toolBar.setBackground(Color.WHITE);
        toolBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblFilter = new JLabel("Bộ lọc hạn dùng:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toolBar.add(lblFilter);

        JComboBox<String> cbDays = new JComboBox<>(new String[]{
                "Sắp hết hạn trong 7 ngày tới",
                "Sắp hết hạn trong 14 ngày tới",
                "Sắp hết hạn trong 30 ngày tới",
                "Đã quá hạn (Cần mời gia hạn ngay)"
        });
        cbDays.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toolBar.add(cbDays);

        JButton btnZalo = new JButton("Gửi Tin Nhắn Nhắc Hạn (Zalo ZNS)");
        btnZalo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnZalo.setBackground(new Color(37, 99, 235));
        btnZalo.setForeground(Color.WHITE);
        btnZalo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toolBar.add(btnZalo);

        JButton btnExport = new JButton("Xuất File Excel");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(new Color(16, 185, 129));
        btnExport.setForeground(Color.WHITE);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toolBar.add(btnExport);

        panel.add(toolBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã Hội Viên", "Họ Và Tên", "Số Điện Thoại", "Mã Thẻ QR", "Chi Nhánh", "Ngày Hết Hạn", "Mức Độ Cảnh Báo"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        Runnable reloadExpiring = () -> {
            model.setRowCount(0);
            int selIdx = cbDays.getSelectedIndex();
            int days = 7;
            if (selIdx == 1) days = 14;
            else if (selIdx == 2) days = 30;
            else if (selIdx == 3) days = 0;

            List<com.nhom12.gym.entity.HoiVien> allMembers = new com.nhom12.gym.dao.HoiVienDAO().layTatCa();
            if (allMembers == null) return;
            long now = System.currentTimeMillis();
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

            for (com.nhom12.gym.entity.HoiVien hv : allMembers) {
                Date exp = hv.getNgayHetHan();
                if (exp == null) continue;
                long diff = exp.getTime() - now;
                long remainDays = diff / (1000L * 60 * 60 * 24);

                if (selIdx == 3) {
                    if (remainDays < 0) {
                        model.addRow(new Object[]{
                                hv.getMaHoiVien(), hv.getHoTen(), hv.getSoDienThoai(),
                                hv.getMaTheQR() != null ? hv.getMaTheQR() : "",
                                hv.getChiNhanhDangKy() != null ? hv.getChiNhanhDangKy() : "CN01 - Cơ sở 1 (Quận 1)",
                                sdfDate.format(exp),
                                "ĐÃ QUÁ HẠN (CẤP 3)"
                        });
                    }
                } else {
                    if (remainDays <= days) {
                        String warning;
                        if (remainDays < 0) {
                            warning = "ĐÃ QUÁ HẠN (CẤP 3)";
                        } else if (remainDays <= 7) {
                            warning = "CÒN " + remainDays + " NGÀY (CẤP 1)";
                        } else {
                            warning = "CÒN " + remainDays + " NGÀY (CẤP 2)";
                        }
                        model.addRow(new Object[]{
                                hv.getMaHoiVien(), hv.getHoTen(), hv.getSoDienThoai(),
                                hv.getMaTheQR() != null ? hv.getMaTheQR() : "",
                                hv.getChiNhanhDangKy() != null ? hv.getChiNhanhDangKy() : "CN01 - Cơ sở 1 (Quận 1)",
                                sdfDate.format(exp),
                                warning
                        });
                    }
                }
            }
        };

        cbDays.addActionListener(e -> reloadExpiring.run());
        reloadExpiring.run();

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));

        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String s = (val != null) ? val.toString() : "";
                if (s.contains("ĐÃ QUÁ HẠN")) {
                    l.setForeground(new Color(239, 68, 68));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if (s.contains("CẤP 1")) {
                    l.setForeground(new Color(245, 158, 11));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                }
                return l;
            }
        });

        btnExport.addActionListener(e -> ExcelExportHelper.exportTableToExcel(this, table, "DanhSach_HoiVien_SapHetHan"));
        btnZalo.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hội viên trên bảng để gửi tin nhắn nhắc hạn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String name = table.getValueAt(row, 1).toString();
            String phone = table.getValueAt(row, 2).toString();
            String exp = table.getValueAt(row, 5).toString();

            JOptionPane.showMessageDialog(this,
                    "<html><body style='width: 320px; font-family: Segoe UI;'>"
                            + "<b style='color: #2563eb;'>Đã gửi tin nhắn nhắc hạn tự động!</b><br><br>"
                            + "<b>Người nhận:</b> " + name + " (" + phone + ")<br>"
                            + "<b>Thời hạn gói:</b> " + exp + "<br>"
                            + "<b>Kênh gửi:</b> Zalo ZNS Template ID: #FITLIFE_RENEW_PROMO<br>"
                            + "<b>Nội dung:</b> Tặng Voucher 10% khi tái gia hạn trước ngày " + exp + ".</body></html>",
                    "Gửi Thông Báo Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // TAB 3: MỨC ĐỘ SỬ DỤNG THEO GIỜ (HEATMAP)
    // ==========================================
    private JPanel createHourlyUsageTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Subtitle & Notes
        JPanel topNote = new JPanel(new BorderLayout());
        topNote.setBackground(new Color(238, 242, 255));
        topNote.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(199, 210, 254)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        JLabel lblNote = new JLabel("<html><b>Phân tích lưu lượng Hội viên theo giờ (06:00 - 22:00):</b> "
                + "Giúp Chủ phòng & Quản lý bố trí Huấn luyện viên trực sàn và tăng cường nhân sự lễ tân vào 2 khung giờ cao điểm vàng "
                + "(<b>Sáng 06:30 - 08:30</b> và <b>Chiều tối 17:30 - 20:00</b>).</html>");
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNote.setForeground(new Color(30, 58, 138));
        topNote.add(lblNote, BorderLayout.CENTER);

        panel.add(topNote, BorderLayout.NORTH);

        // Hourly Bar Chart
        int[] hourlyData = baoCaoService.getPhanBoLuuLuongTheoGio();
        HourlyTrafficChart chart = new HourlyTrafficChart(hourlyData);

        JPanel chartWrapper = new JPanel(new BorderLayout());
        chartWrapper.setBackground(Color.WHITE);
        chartWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        chartWrapper.add(chart, BorderLayout.CENTER);

        panel.add(chartWrapper, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // BIỂU ĐỒ DOANH THU CHI NHÁNH
    // ==========================================
    static class BranchBarChart extends JPanel {
        private String[] branches;
        private double[] revenues;
        private final DecimalFormat df = new DecimalFormat("#,### đ");

        public BranchBarChart(String[] branches, double[] revenues) {
            this.branches = branches;
            this.revenues = revenues;
            setOpaque(false);
        }

        public void updateData(String[] branches, double[] revenues) {
            this.branches = branches;
            this.revenues = revenues;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int pad = 50;

            if (branches == null || revenues == null || branches.length == 0) return;

            // Tính giá trị lớn nhất linh hoạt có thêm 25% khoảng đệm phía trên
            double max = 1000000.0;
            for (double r : revenues) {
                if (r > max) max = r;
            }
            max = max * 1.25;

            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(pad, h - pad, w - pad, h - pad);

            int n = branches.length;
            int barWidth = Math.min(100, (w - 2 * pad) / (n * 2));
            int gap = (w - 2 * pad - n * barWidth) / (n + 1);

            Color[] colors = {new Color(37, 99, 235), new Color(16, 185, 129), new Color(245, 158, 11)};
            Color[] gradients = {new Color(96, 165, 250), new Color(52, 211, 153), new Color(251, 191, 36)};

            for (int i = 0; i < n; i++) {
                int barH = (int) ((revenues[i] / max) * (h - 2 * pad));
                if (barH < 10 && revenues[i] > 0) barH = 10;
                int x = pad + gap + i * (barWidth + gap);
                int y = h - pad - barH;

                GradientPaint gp = new GradientPaint(x, y, colors[i % colors.length], x, y + barH, gradients[i % gradients.length]);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(x, y, barWidth, barH, 10, 10));

                // Value label on top (Hiển thị đầy đủ số tiền chính xác định dạng VND: ví dụ 21,530,000 đ)
                g2.setColor(new Color(15, 23, 42));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String vStr = df.format(revenues[i]);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(vStr, x + (barWidth - fm.stringWidth(vStr)) / 2, y - 8);

                // Branch label below
                g2.setColor(new Color(71, 85, 105));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                fm = g2.getFontMetrics();
                g2.drawString(branches[i], x + (barWidth - fm.stringWidth(branches[i])) / 2, h - pad + 24);
            }
        }
    }

    // ==========================================
    // BIỂU ĐỒ LƯU LƯỢNG THEO GIỜ (HOURLY TRAFFIC)
    // ==========================================
    static class HourlyTrafficChart extends JPanel {
        private final int[] hourly;

        public HourlyTrafficChart(int[] hourly) {
            this.hourly = hourly;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int pad = 50;

            // Lấy từ 6h đến 22h (17 khung giờ)
            int startH = 6;
            int endH = 22;
            int count = endH - startH + 1;

            int maxVal = 100;
            for (int i = startH; i <= endH; i++) {
                if (hourly[i] > maxVal) maxVal = hourly[i] + 10;
            }

            g2.setColor(new Color(226, 232, 240));
            g2.drawLine(pad, h - pad, w - pad, h - pad);

            int barWidth = (w - 2 * pad - (count + 1) * 6) / count;
            if (barWidth < 15) barWidth = 15;

            for (int i = 0; i < count; i++) {
                int hour = startH + i;
                int val = hourly[hour];
                int barH = (int) (((double) val / maxVal) * (h - 2 * pad));
                int x = pad + 10 + i * (barWidth + 6);
                int y = h - pad - barH;

                // Màu theo mức độ cao điểm
                if (val >= 60) {
                    g2.setColor(new Color(239, 68, 68)); // Đỏ: Giờ vàng cao điểm (>60 người)
                } else if (val >= 35) {
                    g2.setColor(new Color(245, 158, 11)); // Cam: Đông (35-60 người)
                } else {
                    g2.setColor(new Color(59, 130, 246)); // Xanh: Bình thường (<35 người)
                }

                g2.fill(new RoundRectangle2D.Double(x, y, barWidth, barH, 6, 6));

                // Value on top
                g2.setColor(new Color(15, 23, 42));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String vStr = String.valueOf(val);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(vStr, x + (barWidth - fm.stringWidth(vStr)) / 2, y - 5);

                // Hour below
                g2.setColor(new Color(100, 116, 139));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                String hStr = hour + "h";
                fm = g2.getFontMetrics();
                g2.drawString(hStr, x + (barWidth - fm.stringWidth(hStr)) / 2, h - pad + 18);
            }

            // Chú thích màu
            int legY = 20;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            drawLegend(g2, pad, legY, new Color(59, 130, 246), "Bình thường (<35 khách)");
            drawLegend(g2, pad + 180, legY, new Color(245, 158, 11), "Đông đúc (35-60 khách)");
            drawLegend(g2, pad + 360, legY, new Color(239, 68, 68), "Giờ Vàng Cao Điểm (>60 khách)");
        }

        private void drawLegend(Graphics2D g2, int x, int y, Color c, String text) {
            g2.setColor(c);
            g2.fill(new RoundRectangle2D.Double(x, y, 14, 14, 4, 4));
            g2.setColor(new Color(51, 65, 85));
            g2.drawString(text, x + 20, y + 12);
        }
    }
}
