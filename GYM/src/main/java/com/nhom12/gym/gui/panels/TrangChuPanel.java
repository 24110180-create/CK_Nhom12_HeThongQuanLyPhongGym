package com.nhom12.gym.gui.panels;

import com.nhom12.gym.gui.MainDashboard;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TrangChuPanel extends JPanel {

    public TrangChuPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setBackground(new Color(248, 250, 252));

        // 1. Tiêu đề trang
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("BẢNG TỔNG QUAN VẬN HÀNH PHÒNG TẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Cập nhật số liệu vận hành và lượt khách check-in theo thời gian thực");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Khu vực Thẻ Thống kê (Grid 3 thẻ cao cấp)
        widgetPanel.setLayout(new GridLayout(1, 3, 20, 0));
        widgetPanel.setOpaque(false);
        updateWidgets();

        add(widgetPanel, BorderLayout.CENTER);

        // Lắng nghe sự kiện đồng bộ toàn hệ thống
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            SwingUtilities.invokeLater(this::updateWidgets);
        });

        // 3. Khu vực Thao tác nhanh & Giờ mở cửa
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
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
        JButton btnBaoCao = createQuickButton("Mở Báo Cáo Doanh Thu");

        quickActionPanel.add(btnDangKy);
        quickActionPanel.add(btnGiaHan);
        quickActionPanel.add(btnBaoCao);

        JPanel noticeCard = new JPanel(new BorderLayout());
        noticeCard.setBackground(new Color(254, 243, 199));
        noticeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 158, 11), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblNotice = new JLabel("<html><b>THÔNG BÁO BAN QUẢN LÝ:</b> Lịch bảo dưỡng giàn tạ máy Smith và máy chạy bộ khu B diễn ra vào 22:00 tối nay. Vui lòng nhắc nhở hội viên hoàn thành bài tập đúng giờ.</html>");
        lblNotice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNotice.setForeground(new Color(146, 64, 14));
        noticeCard.add(lblNotice, BorderLayout.CENTER);

        bottomPanel.add(quickActionPanel, BorderLayout.CENTER);
        bottomPanel.add(noticeCard, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        btnDangKy.addActionListener(e -> navigateToModule("Quản Lý Hội Viên", 1));
        btnGiaHan.addActionListener(e -> navigateToModule("Gia Hạn Gói Tập", -1));
        btnBaoCao.addActionListener(e -> navigateToModule("Báo Cáo Doanh Thu", -1));
    }

    private void navigateToModule(String moduleTitle, int tabIndex) {
        MainDashboard dashboard = MainDashboard.getInstance();
        if (dashboard == null) {
            JOptionPane.showMessageDialog(this,
                    "Không thể mở chức năng vì bảng điều khiển chưa sẵn sàng.",
                    "Không thể điều hướng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean opened = tabIndex >= 0
                ? dashboard.navigateTo(moduleTitle, tabIndex)
                : dashboard.navigateTo(moduleTitle);
        if (!opened) {
            JOptionPane.showMessageDialog(this,
                    "Vai trò hiện tại không được cấp quyền truy cập chức năng \"" + moduleTitle + "\".",
                    "Không đủ quyền", JOptionPane.WARNING_MESSAGE);
        }
    }

    private final JPanel widgetPanel = new JPanel();

    private void updateWidgets() {
        widgetPanel.removeAll();

        java.text.DecimalFormat df = new java.text.DecimalFormat("#,### đ");
        int totalMembers = new com.nhom12.gym.dao.HoiVienDAO().layTatCa().size();
        long activeMembers = new com.nhom12.gym.dao.HoiVienDAO().layTatCa().stream().filter(m -> "Active".equalsIgnoreCase(m.getTrangThai()) || "Hoạt động".equalsIgnoreCase(m.getTrangThai())).count();
        int todayCheckins = (int) new com.nhom12.gym.dao.LichSuCheckInDAO()
                .layLichSuHomNay()
                .stream()
                .filter(log -> "Hợp lệ".equalsIgnoreCase(log.getTrangThai()))
                .count();
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
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(300, 160));

        JLabel lblTag = new JLabel(" " + tag + " ");
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTag.setForeground(Color.WHITE);
        lblTag.setBackground(new Color(255, 255, 255, 60));
        lblTag.setOpaque(true);
        lblTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(255, 255, 255, 210));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(mainValue);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(Color.WHITE);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(subText);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(255, 255, 255, 190));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTag);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblValue);
        card.add(Box.createVerticalGlue());
        card.add(lblSub);

        return card;
    }

    private JButton createQuickButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(170, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        return btn;
    }
}
