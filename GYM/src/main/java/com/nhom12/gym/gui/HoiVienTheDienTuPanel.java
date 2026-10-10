package com.nhom12.gym.gui;

import com.nhom12.gym.util.Auth;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Giao diện Thẻ Hội Viên Điện Tử & Mã QR Check-in dành riêng cho vai trò Hội Viên
 */
public class HoiVienTheDienTuPanel extends JPanel {

    private final JLabel lblQRStatus;
    private final JPanel pnlQRBox;
    private int checkInCount = 18;
    private final JLabel lblCheckInCount;

    public HoiVienTheDienTuPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. TOP HEADER BANNER
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("THẺ THÀNH VIÊN ĐIỆN TỬ & CHECK-IN QR");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Xuất trình thẻ điện tử hoặc quét mã QR cá nhân tại cửa tự động để vào tập");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        topHeader.add(titleGroup, BorderLayout.WEST);

        // Nút check-in mô phỏng ngay tại đây
        JButton btnSimulateCheckin = new JButton("Quét Check-in Cửa Tự Động (Simulation)");
        btnSimulateCheckin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimulateCheckin.setBackground(new Color(16, 185, 129));
        btnSimulateCheckin.setForeground(Color.WHITE);
        btnSimulateCheckin.setFocusPainted(false);
        btnSimulateCheckin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimulateCheckin.setPreferredSize(new Dimension(280, 42));
        topHeader.add(btnSimulateCheckin, BorderLayout.EAST);

        add(topHeader, BorderLayout.NORTH);

        // 2. MAIN CENTER: 2 CỘT (Cột trái: Thẻ VIP Gold/Dark, Cột phải: Mã QR Code & Thống kê cá nhân)
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 25, 0));
        mainContent.setOpaque(false);

        // === CỘT TRÁI: THẺ VIP THÀNH VIÊN ===
        JPanel pnlCardContainer = new JPanel();
        pnlCardContainer.setLayout(new BoxLayout(pnlCardContainer, BoxLayout.Y_AXIS));
        pnlCardContainer.setOpaque(false);

        // Component thẻ phong cách sang trọng VIP
        String memberName = (Auth.hoTen != null && !Auth.hoTen.isEmpty()) ? Auth.hoTen : "Nguyễn Văn An";
        JPanel vipCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Gradient nền thẻ sang trọng (Dark Slate & Deep Indigo)
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 23, 42),
                        w, h, new Color(30, 58, 138)
                );
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 24, 24));

                // Vân viền cong trang trí
                g2.setColor(new Color(255, 255, 255, 25));
                g2.drawOval(w - 180, -60, 260, 260);
                g2.drawOval(w - 120, -20, 200, 200);

                // Viền mạ vàng / bạch kim sang trọng
                g2.setColor(new Color(234, 179, 8, 160));
                g2.setStroke(new BasicStroke(1.8f));
                g2.draw(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, 24, 24));

                // Chip thẻ điện tử giả lập
                g2.setColor(new Color(234, 179, 8, 220));
                g2.fillRoundRect(35, 75, 48, 36, 8, 8);
                g2.setColor(new Color(161, 98, 7));
                g2.drawRoundRect(35, 75, 48, 36, 8, 8);
                g2.drawLine(35, 93, 83, 93);
                g2.drawLine(59, 75, 59, 111);

                g2.dispose();
            }
        };
        vipCard.setLayout(null);
        vipCard.setPreferredSize(new Dimension(460, 270));
        vipCard.setMaximumSize(new Dimension(520, 270));
        vipCard.setOpaque(false);

        JLabel lblCardLogo = new JLabel("FITLIFE GYM");
        lblCardLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblCardLogo.setForeground(Color.WHITE);
        lblCardLogo.setBounds(35, 25, 180, 30);
        vipCard.add(lblCardLogo);

        JLabel lblVipBadge = new JLabel("DIAMOND VIP");
        lblVipBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblVipBadge.setForeground(new Color(254, 240, 138));
        lblVipBadge.setBounds(340, 28, 120, 24);
        vipCard.add(lblVipBadge);

        JLabel lblCardNumber = new JLabel("FL - 8899 - 2026 - VIP");
        lblCardNumber.setFont(new Font("Consolas", Font.BOLD, 20));
        lblCardNumber.setForeground(new Color(241, 245, 249));
        lblCardNumber.setBounds(35, 130, 350, 30);
        vipCard.add(lblCardNumber);

        JLabel lblHolderTitle = new JLabel("CHỦ THẺ / MEMBER");
        lblHolderTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHolderTitle.setForeground(new Color(148, 163, 184));
        lblHolderTitle.setBounds(35, 180, 150, 16);
        vipCard.add(lblHolderTitle);

        JLabel lblHolderName = new JLabel(memberName.toUpperCase());
        lblHolderName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHolderName.setForeground(Color.WHITE);
        lblHolderName.setBounds(35, 200, 250, 24);
        vipCard.add(lblHolderName);

        JLabel lblExpiryTitle = new JLabel("HẾT HẠN / EXPIRES");
        lblExpiryTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblExpiryTitle.setForeground(new Color(148, 163, 184));
        lblExpiryTitle.setBounds(310, 180, 130, 16);
        vipCard.add(lblExpiryTitle);

        JLabel lblExpiryDate = new JLabel("31/12/2026");
        lblExpiryDate.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblExpiryDate.setForeground(new Color(254, 240, 138));
        lblExpiryDate.setBounds(310, 200, 130, 24);
        vipCard.add(lblExpiryDate);

        pnlCardContainer.add(vipCard);
        pnlCardContainer.add(Box.createRigidArea(new Dimension(0, 18)));

        // Thông tin quyền lợi gói tập
        JPanel pnlBenefits = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlBenefits.setBackground(Color.WHITE);
        pnlBenefits.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        pnlBenefits.add(createBenefitRow("Gói tập hiện tại:", "VIP Diamond Toàn Hệ Thống (12 Tháng)", new Color(37, 99, 235)));
        pnlBenefits.add(createBenefitRow("Chi nhánh đăng ký:", "FitLife Landmark 81 - Tầng 3", new Color(15, 23, 42)));
        pnlBenefits.add(createBenefitRow("Đặc quyền kèm theo:", "Xông hơi Sauna, Miễn phí Locker, Khăn tập", new Color(16, 185, 129)));

        pnlCardContainer.add(pnlBenefits);
        mainContent.add(pnlCardContainer);

        // === CỘT PHẢI: MÃ QR CODE CÁ NHÂN & THỐNG KÊ LƯỢT TẬP ===
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setOpaque(false);

        // Khung QR Code
        JPanel pnlQRCard = new JPanel(new BorderLayout(15, 15));
        pnlQRCard.setBackground(Color.WHITE);
        pnlQRCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Header QR Box
        JPanel qrHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        qrHeader.setOpaque(false);
        JLabel lblQRTitle = new JLabel("MÃ QR CHECK-IN CỬA TỰ ĐỘNG");
        lblQRTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblQRTitle.setForeground(new Color(15, 23, 42));

        lblQRStatus = new JLabel("● Mã bảo mật thời gian thực • Sẵn sàng quét");
        lblQRStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblQRStatus.setForeground(new Color(16, 185, 129));
        qrHeader.add(lblQRTitle);
        qrHeader.add(lblQRStatus);
        pnlQRCard.add(qrHeader, BorderLayout.NORTH);

        // QR Code Graphic Display
        pnlQRBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 180;
                int startX = (getWidth() - size) / 2;
                int startY = (getHeight() - size) / 2;

                g2.setColor(Color.WHITE);
                g2.fillRect(startX - 10, startY - 10, size + 20, size + 20);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRect(startX - 10, startY - 10, size + 20, size + 20);

                // Vẽ ma trận QR cách điệu hiện đại
                g2.setColor(new Color(15, 23, 42));
                int cells = 15;
                int cellSize = size / cells;
                for (int r = 0; r < cells; r++) {
                    for (int c = 0; c < cells; c++) {
                        // 3 mắt định vị ở 3 góc
                        boolean isCorner = (r < 4 && c < 4) || (r < 4 && c >= cells - 4) || (r >= cells - 4 && c < 4);
                        if (isCorner) {
                            if (r == 0 || r == 3 || c == 0 || c == 3 ||
                                (r < 4 && (c == cells - 1 || c == cells - 4)) ||
                                (r >= cells - 4 && (c == 0 || c == 3))) {
                                g2.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                            } else if (r == 1 && c == 1 || r == 1 && c == cells - 2 || r == cells - 2 && c == 1) {
                                g2.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                            }
                        } else {
                            // Dữ liệu mã hóa giả lập
                            if ((r * 7 + c * 13 + (int)(System.currentTimeMillis() / 60000)) % 3 != 0) {
                                g2.fillRect(startX + c * cellSize + 1, startY + r * cellSize + 1, cellSize - 2, cellSize - 2);
                            }
                        }
                    }
                }

                // Tâm logo FitLife nhỏ
                g2.setColor(new Color(37, 99, 235));
                g2.fillRoundRect(startX + size / 2 - 14, startY + size / 2 - 14, 28, 28, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString("FL", startX + size / 2 - 7, startY + size / 2 + 5);

                g2.dispose();
            }
        };
        pnlQRBox.setPreferredSize(new Dimension(220, 200));
        pnlQRBox.setOpaque(false);
        pnlQRCard.add(pnlQRBox, BorderLayout.CENTER);

        // Nút bấm tương tác QR
        JPanel qrActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        qrActions.setOpaque(false);

        JButton btnRefreshQR = new JButton("Làm Mới Mã QR");
        btnRefreshQR.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefreshQR.setBackground(new Color(241, 245, 249));
        btnRefreshQR.setForeground(new Color(30, 41, 59));
        btnRefreshQR.setFocusPainted(false);
        btnRefreshQR.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefreshQR.addActionListener(e -> {
            pnlQRBox.repaint();
            lblQRStatus.setText("● Đã làm mới mã QR bảo mật lúc " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        });

        JButton btnDownloadQR = new JButton("Lưu Mã Về Điện Thoại");
        btnDownloadQR.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDownloadQR.setBackground(new Color(37, 99, 235));
        btnDownloadQR.setForeground(Color.WHITE);
        btnDownloadQR.setFocusPainted(false);
        btnDownloadQR.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDownloadQR.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Mã QR định danh cá nhân đã được đồng bộ vào ứng dụng di động FitLife Mobile của bạn!",
                    "Tải QR Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        qrActions.add(btnRefreshQR);
        qrActions.add(btnDownloadQR);
        pnlQRCard.add(qrActions, BorderLayout.SOUTH);

        pnlRight.add(pnlQRCard);
        pnlRight.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3 Thẻ thống kê nhanh
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 12, 0));
        statsRow.setOpaque(false);

        JPanel stat1 = createStatBox("Số Buổi Đã Tập Tháng Này", "18 buổi", new Color(16, 185, 129));
        lblCheckInCount = (JLabel) ((JPanel) stat1.getComponent(0)).getComponent(1);

        JPanel stat2 = createStatBox("Điểm Thưởng Hội Viên", "1,250 pts", new Color(245, 158, 11));

        statsRow.add(stat1);
        statsRow.add(stat2);
        pnlRight.add(statsRow);

        mainContent.add(pnlRight);
        add(mainContent, BorderLayout.CENTER);

        // Sự kiện mô phỏng Check-in
        btnSimulateCheckin.addActionListener(e -> {
            checkInCount++;
            lblCheckInCount.setText(checkInCount + " buổi");
            pnlQRBox.repaint();
            JOptionPane.showMessageDialog(this,
                    "<html><body style='width: 280px; padding: 5px;'>" +
                    "<h3 style='color: #10b981; margin: 0;'>CHECK-IN THÀNH CÔNG!</h3>" +
                    "<p>Xin chào: <b>" + memberName + "</b></p>" +
                    "<p>Gói tập: <b>VIP Diamond</b> (Hạn: 31/12/2026)</p>" +
                    "<p>Cửa tự động Flap Barrier đã mở. Chúc bạn có một buổi tập tràn đầy năng lượng!</p>" +
                    "</body></html>",
                    "Cửa Vào FitLife Đã Mở", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private JPanel createBenefitRow(String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        JLabel lblL = new JLabel(label);
        lblL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblL.setForeground(new Color(100, 116, 139));

        JLabel lblV = new JLabel(value);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblV.setForeground(valueColor);

        row.add(lblL, BorderLayout.WEST);
        row.add(lblV, BorderLayout.EAST);
        return row;
    }

    private JPanel createStatBox(String title, String val, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 4));
        content.setOpaque(false);

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblT.setForeground(new Color(100, 116, 139));

        JLabel lblV = new JLabel(val);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblV.setForeground(color);

        content.add(lblT);
        content.add(lblV);
        card.add(content, BorderLayout.CENTER);
        return card;
    }
}
