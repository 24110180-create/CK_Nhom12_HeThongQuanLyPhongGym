package com.nhom12.gym.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Giao diện Quản Lý Gói Tập & Gia Hạn Trực Tuyến VietQR dành cho Hội Viên
 */
public class HoiVienGoiTapPanel extends JPanel {

    private final DefaultTableModel invoiceModel;

    public HoiVienGoiTapPanel() {
        setLayout(new BorderLayout(18, 18));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("GÓI TẬP CỦA TÔI & GIA HẠN TRỰC TUYẾN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Chi tiết hợp đồng hội viên, quyền lợi gói tập và gia hạn thẻ tích hợp mã VietQR");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        topHeader.add(titleGroup, BorderLayout.WEST);

        add(topHeader, BorderLayout.NORTH);

        // 2. MAIN CENTER: 2 CỘT (Cột trái: Thông tin hợp đồng hiện tại & Lịch sử; Cột phải: 3 gói gia hạn ưu đãi)
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 25, 0));
        mainContent.setOpaque(false);

        // === CỘT TRÁI: HỢP ĐỒNG HIỆN TẠI & LỊCH SỬ THANH TOÁN ===
        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setOpaque(false);

        // Khung hợp đồng đang dùng
        JPanel pnlContract = new JPanel(new GridLayout(5, 1, 0, 8));
        pnlContract.setBackground(Color.WHITE);
        pnlContract.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblContTitle = new JLabel("HỢP ĐỒNG THẺ TẬP ĐANG SỬ DỤNG");
        lblContTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblContTitle.setForeground(new Color(15, 23, 42));
        pnlContract.add(lblContTitle);

        pnlContract.add(createRow("Tên gói thành viên:", "VIP DIAMOND TOÀN HỆ THỐNG", new Color(37, 99, 235)));
        pnlContract.add(createRow("Thời hạn kích hoạt:", "01/01/2026 - 31/12/2026 (Còn 275 ngày)", new Color(16, 185, 129)));
        pnlContract.add(createRow("Phạm vi phòng tập:", "Tất cả chi nhánh FitLife trên toàn quốc", new Color(71, 85, 105)));
        pnlContract.add(createRow("Dịch vụ VIP miễn phí:", "Locker riêng, Khăn tập mỗi ngày, Xông hơi", new Color(245, 158, 11)));
        pnlLeft.add(pnlContract);
        pnlLeft.add(Box.createRigidArea(new Dimension(0, 18)));

        // Bảng lịch sử hóa đơn
        JPanel pnlInvoices = new JPanel(new BorderLayout(0, 8));
        pnlInvoices.setBackground(Color.WHITE);
        pnlInvoices.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblInvTitle = new JLabel("LỊCH SỬ GIAO DỊCH & HÓA ĐƠN ĐÃ THANH TOÁN");
        lblInvTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInvTitle.setForeground(new Color(15, 23, 42));
        pnlInvoices.add(lblInvTitle, BorderLayout.NORTH);

        String[] cols = {"Mã HĐ", "Nội Dung", "Số Tiền", "Ngày TT", "Hình Thức"};
        Object[][] data = {
                {"HD00821", "Gói VIP Diamond 12T", "7,500,000 đ", "01/01/2026", "VietQR"},
                {"HD00543", "Gói 24 Buổi PT Kèm", "12,000,000 đ", "15/01/2026", "Chuyển khoản"},
                {"HD00312", "Hộp Whey Gold 5Lbs", "1,850,000 đ", "10/02/2026", "Quẹt thẻ POS"}
        };
        invoiceModel = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable invTable = new JTable(invoiceModel);
        invTable.setRowHeight(32);
        invTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        invTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        invTable.getTableHeader().setBackground(new Color(241, 245, 249));

        JScrollPane scrollInv = new JScrollPane(invTable);
        scrollInv.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlInvoices.add(scrollInv, BorderLayout.CENTER);

        pnlLeft.add(pnlInvoices);
        mainContent.add(pnlLeft);

        // === CỘT PHẢI: 3 GÓI GIA HẠN ƯU ĐÃI ===
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setOpaque(false);

        JLabel lblRenewTitle = new JLabel("CÁC GÓI GIA HẠN ƯU ĐÃI DÀNH RIÊNG CHO HỘI VIÊN");
        lblRenewTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRenewTitle.setForeground(new Color(15, 23, 42));
        pnlRight.add(lblRenewTitle);
        pnlRight.add(Box.createRigidArea(new Dimension(0, 12)));

        pnlRight.add(createPackageCard("GÓI GIA HẠN 3 THÁNG", "2,400,000 đ",
                "Tặng thêm 01 tháng tập miễn phí • Miễn phí gửi xe & Locker",
                "2,400,000 đ", new Color(37, 99, 235)));
        pnlRight.add(Box.createRigidArea(new Dimension(0, 12)));

        pnlRight.add(createPackageCard("GÓI GIA HẠN 6 THÁNG (BÁN CHẠY)", "4,200,000 đ",
                "Tặng 02 tháng tập + 3 buổi PT riêng • Miễn phí Sauna xông hơi",
                "4,200,000 đ", new Color(16, 185, 129)));
        pnlRight.add(Box.createRigidArea(new Dimension(0, 12)));

        pnlRight.add(createPackageCard("GÓI VIP DIAMOND 12 THÁNG", "7,500,000 đ",
                "Tặng 03 tháng tập + 6 buổi PT + Bình giữ nhiệt & Khăn cao cấp",
                "7,500,000 đ", new Color(245, 158, 11)));

        mainContent.add(pnlRight);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createRow(String label, String val, Color valColor) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        JLabel lblL = new JLabel(label);
        lblL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblL.setForeground(new Color(100, 116, 139));

        JLabel lblV = new JLabel(val);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblV.setForeground(valColor);

        r.add(lblL, BorderLayout.WEST);
        r.add(lblV, BorderLayout.EAST);
        return r;
    }

    private JPanel createPackageCard(String name, String price, String desc, String rawPrice, Color badgeColor) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel left = new JPanel(new GridLayout(3, 1, 0, 3));
        left.setOpaque(false);
        JLabel lblN = new JLabel(name);
        lblN.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblN.setForeground(badgeColor);

        JLabel lblP = new JLabel(price);
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblP.setForeground(new Color(15, 23, 42));

        JLabel lblD = new JLabel("<html><span style='color:#64748b; font-size:11px;'>" + desc + "</span></html>");

        left.add(lblN);
        left.add(lblP);
        left.add(lblD);
        card.add(left, BorderLayout.CENTER);

        JButton btnPay = new JButton("Gia Hạn VietQR");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPay.setBackground(badgeColor);
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPay.setPreferredSize(new Dimension(135, 38));
        btnPay.addActionListener(e -> hienThiDialogVietQR(name, rawPrice));
        card.add(btnPay, BorderLayout.EAST);

        return card;
    }

    private void hienThiDialogVietQR(String packageName, String price) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thanh Toán VietQR - " + packageName, true);
        dialog.setSize(420, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblT = new JLabel("QUÉT MÃ VIETQR ĐỂ GIA HẠN GÓI");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblT.setForeground(new Color(15, 23, 42));
        lblT.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Hỗ trợ tất cả ngân hàng & ví điện tử qua chuẩn NAPAS 247");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // QR simulation box
        JPanel qrBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int sz = 160;
                int sx = (getWidth() - sz) / 2;
                int sy = 10;
                g2.setColor(new Color(15, 23, 42));
                g2.drawRect(sx, sy, sz, sz);
                g2.setColor(new Color(30, 41, 59));
                int cells = 16;
                int cs = sz / cells;
                for (int r = 0; r < cells; r++) {
                    for (int c = 0; c < cells; c++) {
                        if ((r < 3 && c < 3) || (r < 3 && c >= cells - 3) || (r >= cells - 3 && c < 3) || (r + c * 5) % 3 != 0) {
                            g2.fillRect(sx + c * cs + 1, sy + r * cs + 1, cs - 2, cs - 2);
                        }
                    }
                }
                g2.setColor(new Color(239, 68, 68));
                g2.fillRoundRect(sx + sz / 2 - 12, sy + sz / 2 - 12, 24, 24, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString("QR", sx + sz / 2 - 8, sy + sz / 2 + 5);
            }
        };
        qrBox.setPreferredSize(new Dimension(180, 180));
        qrBox.setMaximumSize(new Dimension(180, 180));
        qrBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrBox.setOpaque(false);

        JLabel lblBank = new JLabel("Ngân hàng: MBBank (Quân Đội)");
        lblBank.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBank.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAcc = new JLabel("STK: 9988 2244 6688  •  FITLIFE GYM JSC");
        lblAcc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAcc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAmount = new JLabel("Số tiền: " + price);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAmount.setForeground(new Color(16, 185, 129));
        lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblContent = new JLabel("Nội dung: FL88992 GIAHAN");
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblContent.setForeground(new Color(71, 85, 105));
        lblContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnConfirm = new JButton("Tôi Đã Chuyển Khoản Xong");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirm.setBackground(new Color(16, 185, 129));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConfirm.setPreferredSize(new Dimension(240, 42));
        btnConfirm.setMaximumSize(new Dimension(280, 42));

        btnConfirm.addActionListener(ev -> {
            dialog.dispose();
            invoiceModel.insertRow(0, new Object[]{"HD" + System.currentTimeMillis() % 100000, packageName, price, "Hôm nay", "VietQR Online"});
            JOptionPane.showMessageDialog(this,
                    "Hệ thống đã nhận diện giao dịch thành công!\nGói tập của bạn đã được gia hạn tự động.",
                    "Thanh Toán Hoàn Tất", JOptionPane.INFORMATION_MESSAGE);
        });

        pnl.add(lblT);
        pnl.add(Box.createRigidArea(new Dimension(0, 4)));
        pnl.add(lblSub);
        pnl.add(Box.createRigidArea(new Dimension(0, 15)));
        pnl.add(qrBox);
        pnl.add(Box.createRigidArea(new Dimension(0, 15)));
        pnl.add(lblBank);
        pnl.add(Box.createRigidArea(new Dimension(0, 3)));
        pnl.add(lblAcc);
        pnl.add(Box.createRigidArea(new Dimension(0, 3)));
        pnl.add(lblAmount);
        pnl.add(Box.createRigidArea(new Dimension(0, 3)));
        pnl.add(lblContent);
        pnl.add(Box.createRigidArea(new Dimension(0, 18)));
        pnl.add(btnConfirm);

        dialog.add(pnl, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}
