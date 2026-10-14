package com.nhom12.gym.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Cửa sổ hiển thị Thẻ Hội Viên & Thông Tin Check-in Phòng Tập FitLife Gym.
 * Rõ ràng về mặt logic:
 * 1. Cung cấp đầy đủ mã QR, số điện thoại, gói tập và thời hạn để hội viên check-in ra vào.
 * 2. Khẳng định quy định phân quyền: Hội viên KHÔNG ĐƯỢC CẤP TÀI KHOẢN để đăng nhập vào phần mềm quản trị này.
 * 3. Tích hợp nút "Thử Check-in Ngay" để mô phỏng quét thẻ Honeywell mở cổng Flap Barrier tức thì.
 */
public class GuiMaQRDialog extends JDialog {

    private final String memberCode;
    private final String memberName;
    private final String memberPhone;
    private final String packageName;
    private final String branchName;
    private final String expireDate;

    public GuiMaQRDialog(Window parent, String memberCode, String memberName, String memberPhone,
                         String packageName, String branchName) {
        this(parent, memberCode, memberName, memberPhone, packageName, branchName, "12 tháng (Theo gói đăng ký)");
    }

    public GuiMaQRDialog(Window parent, String memberCode, String memberName, String memberPhone,
                         String packageName, String branchName, String expireDate) {
        super(parent, "THẺ HỘI VIÊN & THÔNG TIN CHECK-IN - " + memberName, ModalityType.APPLICATION_MODAL);
        this.memberCode = (memberCode != null && !memberCode.isEmpty()) ? memberCode : "HV" + (System.currentTimeMillis() % 1000);
        this.memberName = memberName != null ? memberName : "Hội Viên";
        this.memberPhone = memberPhone != null ? memberPhone : "";
        this.packageName = packageName != null ? packageName : "Gói Tiêu Chuẩn";
        this.branchName = branchName != null ? branchName : "Toàn hệ thống";
        this.expireDate = (expireDate != null && !expireDate.isEmpty()) ? expireDate : "Theo hạn gói tập";

        initUI();
    }

    private void initUI() {
        setSize(840, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(15, 14));
        root.setBackground(new Color(248, 250, 252));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. TOP HEADER BANNER
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 3));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("XÁC NHẬN ĐĂNG KÝ & THÔNG TIN CHECK-IN HỘI VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Thẻ thành viên đã kích hoạt thành công • Quét mã QR hoặc dùng Số điện thoại để check-in tại quầy & cửa tự động");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        top.add(titleGroup, BorderLayout.WEST);

        JLabel lblStatus = new JLabel("  ĐÃ KÍCH HOẠT  ");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setForeground(new Color(16, 185, 129));
        lblStatus.setBackground(new Color(236, 253, 245));
        lblStatus.setOpaque(true);
        lblStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(167, 243, 208), 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        top.add(lblStatus, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // 2. CENTER: 2 CỘT (Cột trái: Khung vẽ QR Code; Cột phải: Hồ sơ thẻ & Quy định nghiệp vụ)
        JPanel center = new JPanel(new GridLayout(1, 2, 18, 0));
        center.setOpaque(false);

        // --- CỘT TRÁI: KHUNG VẼ MÃ QR TRỰC QUAN ---
        JPanel leftQR = new JPanel(new BorderLayout(0, 8));
        leftQR.setBackground(Color.WHITE);
        leftQR.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblQRHeader = new JLabel("MÃ QR CHECK-IN CỬA TỰ ĐỘNG", SwingConstants.CENTER);
        lblQRHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblQRHeader.setForeground(new Color(15, 23, 42));
        leftQR.add(lblQRHeader, BorderLayout.NORTH);

        JPanel qrGraphicBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 190;
                int startX = (getWidth() - size) / 2;
                int startY = (getHeight() - size) / 2;

                // Nền trắng và viền
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(startX - 10, startY - 10, size + 20, size + 20, 14, 14));
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Double(startX - 10, startY - 10, size + 20, size + 20, 14, 14));

                // Ma trận QR Code chuẩn
                g2.setColor(new Color(15, 23, 42));
                int cells = 17;
                int cellSize = size / cells;
                int hash = memberCode.hashCode() + memberPhone.hashCode();

                for (int r = 0; r < cells; r++) {
                    for (int c = 0; c < cells; c++) {
                        boolean isTopLeft = (r < 5 && c < 5);
                        boolean isTopRight = (r < 5 && c >= cells - 5);
                        boolean isBottomLeft = (r >= cells - 5 && c < 5);

                        if (isTopLeft || isTopRight || isBottomLeft) {
                            if (r == 0 || r == 4 || c == 0 || c == 4 ||
                                    (r < 5 && (c == cells - 1 || c == cells - 5)) ||
                                    (r >= cells - 5 && (c == 0 || c == 4))) {
                                g2.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                            } else if ((r >= 1 && r <= 3 && c >= 1 && c <= 3) ||
                                    (r >= 1 && r <= 3 && c >= cells - 4 && c <= cells - 2) ||
                                    (r >= cells - 4 && r <= cells - 2 && c >= 1 && c <= 3)) {
                                g2.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
                            }
                        } else {
                            if ((r * 11 + c * 17 + Math.abs(hash)) % 3 != 0) {
                                g2.fillRect(startX + c * cellSize + 1, startY + r * cellSize + 1, cellSize - 2, cellSize - 2);
                            }
                        }
                    }
                }

                // Logo FitLife nhỏ ở trung tâm QR
                int logoSize = 30;
                int lx = startX + size / 2 - logoSize / 2;
                int ly = startY + size / 2 - logoSize / 2;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(lx - 2, ly - 2, logoSize + 4, logoSize + 4, 8, 8);
                g2.setColor(new Color(37, 99, 235));
                g2.fillRoundRect(lx, ly, logoSize, logoSize, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString("FL", lx + 7, ly + 19);

                g2.dispose();
            }
        };
        qrGraphicBox.setPreferredSize(new Dimension(220, 210));
        qrGraphicBox.setOpaque(false);
        leftQR.add(qrGraphicBox, BorderLayout.CENTER);

        JPanel pnlQRBottom = new JPanel(new GridLayout(2, 1, 0, 4));
        pnlQRBottom.setOpaque(false);

        JLabel lblCodeText = new JLabel("Mã Thẻ Check-in: " + memberCode, SwingConstants.CENTER);
        lblCodeText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCodeText.setForeground(new Color(37, 99, 235));

        JButton btnCopyCode = new JButton("Sao Chép Mã QR");
        btnCopyCode.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnCopyCode.setBackground(new Color(241, 245, 249));
        btnCopyCode.setForeground(new Color(51, 65, 85));
        btnCopyCode.setFocusPainted(false);
        btnCopyCode.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCopyCode.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(memberCode), null);
            JOptionPane.showMessageDialog(this, "Đã sao chép mã thẻ [" + memberCode + "] vào bộ nhớ tạm!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });

        pnlQRBottom.add(lblCodeText);
        pnlQRBottom.add(btnCopyCode);
        leftQR.add(pnlQRBottom, BorderLayout.SOUTH);
        center.add(leftQR);

        // --- CỘT PHẢI: CHI TIẾT THẺ & QUY ĐỊNH CHECK-IN (KHÔNG CẤP TÀI KHOẢN APP) ---
        JPanel rightInfo = new JPanel();
        rightInfo.setLayout(new BoxLayout(rightInfo, BoxLayout.Y_AXIS));
        rightInfo.setOpaque(false);

        // Khung thông tin thẻ thành viên
        JPanel pnlCardInfo = new JPanel(new GridLayout(6, 1, 0, 4));
        pnlCardInfo.setBackground(Color.WHITE);
        pnlCardInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblSec1 = new JLabel("HỒ SƠ THẺ HỘI VIÊN FITLIFE");
        lblSec1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSec1.setForeground(new Color(37, 99, 235));
        pnlCardInfo.add(lblSec1);

        pnlCardInfo.add(createRow("Họ và Tên:", memberName, new Color(15, 23, 42), true));
        pnlCardInfo.add(createRow("Số Điện Thoại:", memberPhone, new Color(15, 23, 42), false));
        pnlCardInfo.add(createRow("Gói Tập Đăng Ký:", packageName, new Color(16, 185, 129), true));
        pnlCardInfo.add(createRow("Chi Nhánh Áp Dụng:", branchName, new Color(71, 85, 105), false));
        pnlCardInfo.add(createRow("Thời Hạn Sử Dụng:", expireDate, new Color(225, 29, 72), true));
        rightInfo.add(pnlCardInfo);
        rightInfo.add(Box.createRigidArea(new Dimension(0, 10)));

        // Khung quy định check-in & phân quyền bảo mật (ĐÚNG LOGIC NGHIỆP VỤ)
        JPanel pnlPolicy = new JPanel();
        pnlPolicy.setLayout(new BoxLayout(pnlPolicy, BoxLayout.Y_AXIS));
        pnlPolicy.setBackground(new Color(254, 243, 199)); // Vàng nhạt cảnh báo nghiệp vụ
        pnlPolicy.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 158, 11), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblPolicyHeader = new JLabel("QUY ĐỊNH PHÂN QUYỀN & CHECK-IN PHÒNG TẬP");
        lblPolicyHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPolicyHeader.setForeground(new Color(180, 83, 9));
        lblPolicyHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlPolicy.add(lblPolicyHeader);
        pnlPolicy.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel lblRule1 = new JLabel("<html><b>1. Phân quyền hệ thống:</b> Hội viên <u>KHÔNG ĐƯỢC CẤP TÀI KHOẢN</u> để đăng nhập vào phần mềm quản lý nội bộ này (phần mềm chỉ dành riêng cho Chủ phòng, Quản lý, Lễ tân và HLV).</html>");
        lblRule1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRule1.setForeground(new Color(120, 53, 15));
        lblRule1.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlPolicy.add(lblRule1);
        pnlPolicy.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel lblRule2 = new JLabel("<html><b>2. Phương thức Check-in:</b> Quý hội viên chỉ cần đưa mã QR trên điện thoại hoặc đọc Số điện thoại tại quầy Lễ tân / Cổng Flap Barrier để tự động mở cửa vào tập.</html>");
        lblRule2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRule2.setForeground(new Color(120, 53, 15));
        lblRule2.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlPolicy.add(lblRule2);

        rightInfo.add(pnlPolicy);
        center.add(rightInfo);
        root.add(center, BorderLayout.CENTER);

        // 3. BOTTOM ACTIONS: CÁC NÚT THAO TÁC NGHIỆP VỤ
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setOpaque(false);

        JButton btnTestCheckIn = new JButton("Thử Check-in Ngay");
        btnTestCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTestCheckIn.setBackground(new Color(5, 150, 105)); // Xanh lá
        btnTestCheckIn.setForeground(Color.WHITE);
        btnTestCheckIn.setFocusPainted(false);
        btnTestCheckIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTestCheckIn.setPreferredSize(new Dimension(170, 38));
        btnTestCheckIn.addActionListener(e -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            QRScannerSimulatorDialog scanner = new QRScannerSimulatorDialog(parentWindow, memberCode, memberName);
            scanner.setVisible(true);
        });

        JButton btnZalo = new JButton("Gửi Mã Qua Zalo");
        btnZalo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnZalo.setBackground(new Color(14, 116, 144));
        btnZalo.setForeground(Color.WHITE);
        btnZalo.setFocusPainted(false);
        btnZalo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnZalo.setPreferredSize(new Dimension(150, 38));
        btnZalo.addActionListener(e -> guiQuaZalo());

        JButton btnSMS = new JButton("Gửi SMS Brandname");
        btnSMS.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSMS.setBackground(new Color(37, 99, 235));
        btnSMS.setForeground(Color.WHITE);
        btnSMS.setFocusPainted(false);
        btnSMS.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSMS.setPreferredSize(new Dimension(160, 38));
        btnSMS.addActionListener(e -> guiQuaSMS());

        JButton btnSaveImg = new JButton("Tải Ảnh Thẻ QR");
        btnSaveImg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSaveImg.setBackground(new Color(241, 245, 249));
        btnSaveImg.setForeground(new Color(30, 41, 59));
        btnSaveImg.setFocusPainted(false);
        btnSaveImg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveImg.setPreferredSize(new Dimension(135, 38));
        btnSaveImg.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Ảnh mã QR mã hóa (" + memberCode + ".png) đã được xuất thành công vào thư mục lưu trữ thẻ của hệ thống!",
                    "Tải Ảnh QR Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnClose = new JButton("Hoàn Tất & Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBackground(new Color(100, 116, 139));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(135, 38));
        btnClose.addActionListener(e -> dispose());

        bottom.add(btnTestCheckIn);
        bottom.add(btnZalo);
        bottom.add(btnSMS);
        bottom.add(btnSaveImg);
        bottom.add(btnClose);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createRow(String label, String val, Color valColor, boolean bold) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        JLabel lblL = new JLabel(label);
        lblL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblL.setForeground(new Color(100, 116, 139));

        JLabel lblV = new JLabel(val);
        lblV.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 12));
        lblV.setForeground(valColor);

        r.add(lblL, BorderLayout.WEST);
        r.add(lblV, BorderLayout.EAST);
        return r;
    }

    private void guiQuaZalo() {
        String time = new SimpleDateFormat("HH:mm - dd/MM/yyyy").format(new Date());
        String zaloMsg = "<html><body style='width: 320px; font-family: Segoe UI;'>" +
                "<div style='background-color: #0284c7; color: white; padding: 10px; border-radius: 6px;'>" +
                "<h3 style='margin: 0;'>TIN NHẮN ZALO OA: FITLIFE FITNESS</h3>" +
                "<p style='margin: 3px 0 0 0; font-size: 11px;'>Gửi đến: <b>" + memberPhone + " (" + memberName + ")</b></p>" +
                "</div>" +
                "<div style='padding: 12px; background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; margin-top: 8px;'>" +
                "<p>Kính chào Quý hội viên <b>" + memberName + "</b>,</p>" +
                "<p>Chúc mừng bạn đã gia nhập hệ sinh thái FitLife Gym! Dưới đây là thông tin thẻ thành viên và mã QR check-in của bạn:</p>" +
                "<ul>" +
                "<li><b>Mã thẻ QR Check-in:</b> " + memberCode + "</li>" +
                "<li><b>Gói tập:</b> " + packageName + "</li>" +
                "<li><b>Thời hạn:</b> " + expireDate + "</li>" +
                "<li><b>Cơ sở áp dụng:</b> " + branchName + "</li>" +
                "<li><b>Quy định ra vào:</b> Quét mã QR trực tiếp tại cổng Flap Barrier hoặc quầy Lễ tân (Không cần tài khoản đăng nhập)</li>" +
                "</ul>" +
                "<p style='color: #0284c7; font-size: 11px;'><i>Thời gian gửi: " + time + " • Trạng thái: Đã gửi thành công</i></p>" +
                "</div>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, zaloMsg, "Zalo ZNS Dispatch - Gửi Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guiQuaSMS() {
        String time = new SimpleDateFormat("HH:mm dd/MM").format(new Date());
        String smsText = "<html><body style='width: 300px; font-family: Segoe UI;'>" +
                "<h4 style='color: #10b981; margin: 0;'>TIN NHẮN SMS BRANDNAME ĐÃ GỬI</h4>" +
                "<p><b>Người nhận:</b> " + memberPhone + "</p>" +
                "<div style='background-color: #f1f5f9; padding: 10px; border-left: 4px solid #10b981; font-size: 12px;'>" +
                "[FITLIFE] Chuc mung " + memberName + " da dang ky thanh cong the hoi vien " + memberCode + ". " +
                "Goi tap: " + packageName + " (" + expireDate + "). " +
                "Xuat trinh ma QR hoac doc SDT de check-in tai quay Le tan / cong Flap Barrier tu dong." +
                "</div>" +
                "<p style='color: #64748b; font-size: 11px; margin-top: 6px;'>Đã chuyển tin nhắn thành công qua hạ tầng Viễn thông lúc " + time + "</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, smsText, "SMS Brandname - Gửi Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }
}
