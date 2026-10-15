package com.nhom12.gym.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TrangChuPanel extends JPanel {

    private final JPanel widgetPanel = new JPanel();
    private DashboardLiveRevenueChart revenueChart;
    private JLabel lblLiveStatus;
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    public TrangChuPanel() {
        setLayout(new BorderLayout(20, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(248, 250, 252));

        // 1. Tiêu đề trang
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("BẢNG TỔNG QUAN VẬN HÀNH PHÒNG TẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Cập nhật số liệu vận hành và lượt khách check-in theo thời gian thực");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Khu vực Trung Tâm: KPI Widgets (Top) + Biểu Đồ Doanh Thu Live (Center)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        widgetPanel.setLayout(new GridLayout(1, 3, 16, 0));
        widgetPanel.setOpaque(false);
        centerPanel.add(widgetPanel, BorderLayout.NORTH);

        JPanel chartCard = createLiveRevenueChartCard();
        centerPanel.add(chartCard, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 3. Khu vực Thao tác nhanh & Giờ mở cửa
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 12));
        bottomPanel.setOpaque(false);

        JPanel quickActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        quickActionPanel.setOpaque(false);
        quickActionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                " Phím Tắt Thao Tác Nhanh ", 0, 0,
                new Font("Segoe UI", Font.BOLD, 13), new Color(71, 85, 105)
        ));

        JButton btnDangKy = createQuickButton("Đăng Ký Hội Viên");
        JButton btnGiaHan = createQuickButton("Gia Hạn Thẻ Tập");
        JButton btnBaoCao = createQuickButton("Xuất Báo Cáo Nhanh");

        quickActionPanel.add(btnDangKy);
        quickActionPanel.add(btnGiaHan);
        quickActionPanel.add(btnBaoCao);

        JPanel noticeCard = new JPanel(new BorderLayout());
        noticeCard.setBackground(new Color(254, 243, 199));
        noticeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 158, 11), 1),
                new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel lblNotice = new JLabel("<html><b>THÔNG BÁO BAN QUẢN LÝ:</b> Lịch bảo dưỡng giàn tạ máy Smith và máy chạy bộ khu B diễn ra vào 22:00 tối nay. Vui lòng nhắc nhở hội viên hoàn thành bài tập đúng giờ.</html>");
        lblNotice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNotice.setForeground(new Color(146, 64, 14));
        noticeCard.add(lblNotice, BorderLayout.CENTER);

        bottomPanel.add(quickActionPanel, BorderLayout.CENTER);
        bottomPanel.add(noticeCard, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Wire Action Listeners for Quick Action Buttons
        btnDangKy.addActionListener(e -> {
            if (com.nhom12.gym.gui.MainDashboard.getInstance() != null) {
                com.nhom12.gym.gui.MainDashboard.getInstance().navigateTo("Quản Lý Hội Viên", 1);
            } else {
                JOptionPane.showMessageDialog(this, "Chuyển hướng đến màn hình: ĐĂNG KÝ HỘI VIÊN MỚI", "Thao Tác Nhanh", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnGiaHan.addActionListener(e -> {
            if (com.nhom12.gym.gui.MainDashboard.getInstance() != null) {
                com.nhom12.gym.gui.MainDashboard.getInstance().navigateTo("Quản Lý Hội Viên", 3);
            } else {
                JOptionPane.showMessageDialog(this, "Chuyển hướng đến màn hình: GIA HẠN GÓI TẬP", "Thao Tác Nhanh", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnBaoCao.addActionListener(e -> {
            if (com.nhom12.gym.gui.MainDashboard.getInstance() != null) {
                com.nhom12.gym.gui.MainDashboard.getInstance().navigateTo("Báo Cáo Doanh Thu");
            } else {
                JOptionPane.showMessageDialog(this, "Chuyển hướng đến màn hình: BÁO CÁO DOANH THU", "Thao Tác Nhanh", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        updateWidgets();

        // Lắng nghe sự kiện đồng bộ toàn hệ thống để cập nhật biểu đồ & số liệu liên tục
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            SwingUtilities.invokeLater(this::updateWidgets);
        });
    }

    private JPanel createLiveRevenueChartCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel lblTitle = new JLabel("BIỂU ĐỒ DOANH THU & HIỆU SUẤT TÀI CHÍNH THỜI GIAN THỰC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(15, 23, 42));

        lblLiveStatus = new JLabel("● LIVE: Tự động cập nhật liên tục khi có giao dịch mới");
        lblLiveStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLiveStatus.setForeground(new Color(16, 185, 129));

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(lblLiveStatus, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        revenueChart = new DashboardLiveRevenueChart();
        revenueChart.setPreferredSize(new Dimension(0, 190));
        card.add(revenueChart, BorderLayout.CENTER);

        JLabel lblAuditNote = new JLabel("Tự động đồng bộ thời gian thực từ mọi giao dịch phát sinh (Đăng ký mới, Gia hạn, Vé lẻ 30,000 đ, Bán POS quầy bar)");
        lblAuditNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAuditNote.setForeground(new Color(100, 116, 139));
        card.add(lblAuditNote, BorderLayout.SOUTH);

        return card;
    }

    private void updateWidgets() {
        widgetPanel.removeAll();

        List<com.nhom12.gym.entity.HoiVien> listHV = new com.nhom12.gym.dao.HoiVienDAO().layTatCa();
        int totalMembers = listHV.size();
        long activeMembers = listHV.stream().filter(m -> {
            if (m.getTrangThai() == null) return true;
            String st = m.getTrangThai().trim().toLowerCase();
            return !st.contains("khóa") && !st.contains("lock") && !st.contains("hết hạn") && !st.contains("expired");
        }).count();
        int todayCheckins = new com.nhom12.gym.dao.LichSuCheckInDAO().layLichSuHomNay().size();
        if (todayCheckins == 0) todayCheckins = new com.nhom12.gym.dao.LichSuCheckInDAO().layTatCaLichSu().size();
        double totalRev = com.nhom12.gym.dao.HoaDonDAO.getInstance().layTongDoanhThu();

        widgetPanel.add(createWidgetCard(
                "HỘI VIÊN HOẠT ĐỘNG", activeMembers + " / " + totalMembers, "Tỷ lệ kích hoạt: " + (totalMembers > 0 ? (activeMembers * 100 / totalMembers) : 0) + "%",
                new Color(239, 68, 68), new Color(249, 115, 22), "REALTIME"
        ));

        widgetPanel.add(createWidgetCard(
                "CHECK-IN HÔM NAY", String.valueOf(todayCheckins), "Lượt quét thẻ hợp lệ",
                new Color(13, 148, 136), new Color(16, 185, 129), "HỘI VIÊN"
        ));

        widgetPanel.add(createWidgetCard(
                "DOANH THU THỰC TẾ", df.format(totalRev), "Ghi nhận từ hóa đơn toàn hệ thống",
                new Color(37, 99, 235), new Color(99, 102, 241), "HÓA ĐƠN"
        ));

        widgetPanel.revalidate();
        widgetPanel.repaint();

        // Cập nhật biểu đồ doanh thu thời gian thực
        Map<String, Double> branchRev = com.nhom12.gym.dao.HoaDonDAO.getInstance().layDoanhThuTheoChiNhanh();
        double cn01 = branchRev.getOrDefault("CN01 - Quận 1", 0.0);
        double cn02 = branchRev.getOrDefault("CN02 - Tân Bình", 0.0);
        if (revenueChart != null) {
            revenueChart.updateData(cn01, cn02, totalRev);
        }
        if (lblLiveStatus != null) {
            String timeNow = new SimpleDateFormat("HH:mm:ss").format(new Date());
            lblLiveStatus.setText("● LIVE: Đã cập nhật giao dịch lúc " + timeNow + " (Real-time)");
        }
    }

    private JPanel createWidgetCard(String title, String mainValue, String subText, Color colorStart, Color colorEnd, String tag) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, colorStart, getWidth(), getHeight(), colorEnd);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(300, 130));

        JLabel lblTag = new JLabel(" " + tag + " ");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTag.setForeground(Color.WHITE);
        lblTag.setBackground(new Color(255, 255, 255, 60));
        lblTag.setOpaque(true);
        lblTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(255, 255, 255, 210));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(mainValue);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(Color.WHITE);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(subText);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(255, 255, 255, 190));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTag);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(lblValue);
        card.add(Box.createVerticalGlue());
        card.add(lblSub);

        return card;
    }

    private JButton createQuickButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        return btn;
    }

    // =========================================================================
    // BIỂU ĐỒ DOANH THU THỜI GIAN THỰC (REAL-TIME DASHBOARD BAR CHART)
    // =========================================================================
    static class DashboardLiveRevenueChart extends JPanel {
        private String[] labels = {"CN01 - Quận 1", "CN02 - Tân Bình", "Toàn Hệ Thống"};
        private double[] values = {21500000.0, 11200000.0, 32700000.0};
        private final DecimalFormat dfMoney = new DecimalFormat("#,### đ");

        public DashboardLiveRevenueChart() {
            setOpaque(false);
        }

        public void updateData(double cn01, double cn02, double total) {
            this.values = new double[]{cn01, cn02, total};
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int pad = 42;

            double max = 1000000.0;
            for (double v : values) if (v > max) max = v;
            max = max * 1.25;

            // Đường trục tọa độ
            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(pad, h - pad, w - pad, h - pad);

            int n = labels.length;
            int barWidth = Math.min(115, (w - 2 * pad) / (n * 2));
            int gap = (w - 2 * pad - n * barWidth) / (n + 1);

            Color[][] palettes = {
                    {new Color(37, 99, 235), new Color(96, 165, 250)},   // Blue CN01
                    {new Color(16, 185, 129), new Color(52, 211, 153)}, // Green CN02
                    {new Color(147, 51, 234), new Color(192, 132, 252)} // Purple Total
            };

            for (int i = 0; i < n; i++) {
                int barH = (int) ((values[i] / max) * (h - 2 * pad));
                if (barH < 10 && values[i] > 0) barH = 10;
                int x = pad + gap + i * (barWidth + gap);
                int y = h - pad - barH;

                GradientPaint gp = new GradientPaint(x, y, palettes[i][0], x, y + barH, palettes[i][1]);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(x, y, barWidth, barH, 10, 10));

                // Giá trị số tiền trên đỉnh cột (Exact VND formatting)
                g2.setColor(new Color(15, 23, 42));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String vStr = dfMoney.format(values[i]);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(vStr, x + (barWidth - fm.stringWidth(vStr)) / 2, y - 8);

                // Tên cột bên dưới
                g2.setColor(new Color(71, 85, 105));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                fm = g2.getFontMetrics();
                g2.drawString(labels[i], x + (barWidth - fm.stringWidth(labels[i])) / 2, h - pad + 22);
            }
        }
    }
}
