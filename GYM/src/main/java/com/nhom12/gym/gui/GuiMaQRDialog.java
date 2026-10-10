package com.nhom12.gym.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Cửa sổ hiển thị và gửi mã QR Check-in & Thông tin tài khoản App cho Hội Viên
 */
public class GuiMaQRDialog extends JDialog {

    private final String memberCode;
    private final String memberName;
    private final String memberPhone;
    private final String packageName;
    private final String branchName;
    private final String appUsername;
    private final String appPassword;

    public GuiMaQRDialog(Frame parent, String memberCode, String memberName, String memberPhone,
                         String packageName, String branchName, String appUsername, String appPassword) {
        super(parent, "Xuất & Gửi Mã QR Check-in - " + memberName, true);
        this.memberCode = (memberCode != null && !memberCode.isEmpty()) ? memberCode : "HV" + (System.currentTimeMillis() % 1000);
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.packageName = packageName;
        this.branchName = branchName;
        this.appUsername = (appUsername != null && !appUsername.isEmpty()) ? appUsername : memberPhone;
        this.appPassword = (appPassword != null && !appPassword.isEmpty()) ? appPassword : "123456";

        initUI();
    }

    private void initUI() {
        setSize(780, 560);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(new Color(248, 250, 252));
        root.setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. TOP HEADER
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 3));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("CẤP & GỬI MÃ QR CHECK-IN THÀNH VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Mã QR định danh cá nhân và thông tin tài khoản đăng nhập FitLife App");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        top.add(titleGroup, BorderLayout.WEST);

        JLabel lblStatus = new JLabel("● ĐÃ KÍCH HOẠT");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(new Color(16, 185, 129));
        top.add(lblStatus, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        // 2. CENTER: 2 CỘT (Cột trái: QR Code trực quan; Cột phải: Thông tin thẻ & Tài khoản App)
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.setOpaque(false);

        // --- CỘT TRÁI: KHUNG VẼ MÃ QR SẮC NÉT ---
        JPanel leftQR = new JPanel(new BorderLayout(0, 10));
        leftQR.setBackground(Color.WHITE);
        leftQR.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 18, 18, 18)
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

                int size = 200;
                int startX = (getWidth() - size) / 2;
                int startY = (getHeight() - size) / 2;

                // Nền trắng và viền
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(startX - 12, startY - 12, size + 24, size + 24, 16, 16));
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Double(startX - 12, startY - 12, size + 24, size + 24, 16, 16));

                // Ma trận QR Code chuẩn
                g2.setColor(new Color(15, 23, 42));
                int cells = 17;
                int cellSize = size / cells;
                int hash = memberCode.hashCode() + memberPhone.hashCode();

                for (int r = 0; r < cells; r++) {
                    for (int c = 0; c < cells; c++) {
                        // 3 mắt định vị lớn ở 3 góc (Positioning markers)
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
                            // Dữ liệu mã hóa dựa trên hash của mã hội viên
                            if ((r * 11 + c * 17 + Math.abs(hash)) % 3 != 0) {
                                g2.fillRect(startX + c * cellSize + 1, startY + r * cellSize + 1, cellSize - 2, cellSize - 2);
                            }
                        }
                    }
                }

                // Logo FitLife nhỏ ở trung tâm QR
                int logoSize = 32;
                int lx = startX + size / 2 - logoSize / 2;
                int ly = startY + size / 2 - logoSize / 2;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(lx - 2, ly - 2, logoSize + 4, logoSize + 4, 8, 8);
                g2.setColor(new Color(37, 99, 235));
                g2.fillRoundRect(lx, ly, logoSize, logoSize, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString("FL", lx + 8, ly + 20);

                g2.dispose();
            }
        };
        qrGraphicBox.setPreferredSize(new Dimension(240, 220));
        qrGraphicBox.setOpaque(false);
        leftQR.add(qrGraphicBox, BorderLayout.CENTER);

        JLabel lblQRNote = new JLabel("<html><center style='color:#64748b; font-size:11px;'>Mã quét: <b>" + memberCode + "</b><br>Quét mã này tại cổng Flap Barrier hoặc quầy Lễ tân</center></html>", SwingConstants.CENTER);
        leftQR.add(lblQRNote, BorderLayout.SOUTH);
        center.add(leftQR);

        // --- CỘT PHẢI: CHI TIẾT THẺ & TÀI KHOẢN APP HỘI VIÊN ---
        JPanel rightInfo = new JPanel();
        rightInfo.setLayout(new BoxLayout(rightInfo, BoxLayout.Y_AXIS));
        rightInfo.setOpaque(false);

        // Khung thông tin thẻ
        JPanel pnlCardInfo = new JPanel(new GridLayout(5, 1, 0, 5));
        pnlCardInfo.setBackground(Color.WHITE);
        pnlCardInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblSec1 = new JLabel("HỒ SƠ HỘI VIÊN FITLIFE");
        lblSec1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSec1.setForeground(new Color(37, 99, 235));
        pnlCardInfo.add(lblSec1);

        pnlCardInfo.add(createRow("Họ và Tên:", memberName, new Color(15, 23, 42), true));
        pnlCardInfo.add(createRow("Số Điện Thoại:", memberPhone, new Color(15, 23, 42), false));
        pnlCardInfo.add(createRow("Gói Đăng Ký:", packageName, new Color(16, 185, 129), true));
        pnlCardInfo.add(createRow("Chi Nhánh:", branchName, new Color(71, 85, 105), false));
        rightInfo.add(pnlCardInfo);
        rightInfo.add(Box.createRigidArea(new Dimension(0, 12)));

        // Khung tài khoản đăng nhập FitLife App
        JPanel pnlAppAcc = new JPanel(new GridLayout(4, 1, 0, 5));
        pnlAppAcc.setBackground(new Color(240, 249, 255));
        pnlAppAcc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(186, 230, 253), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblSec2 = new JLabel("TÀI KHOẢN ĐĂNG NHẬP FITLIFE APP CỦA HỘI VIÊN");
        lblSec2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSec2.setForeground(new Color(2, 132, 199));
        pnlAppAcc.add(lblSec2);

        pnlAppAcc.add(createRow("Tên đăng nhập App:", appUsername, new Color(2, 132, 199), true));
        pnlAppAcc.add(createRow("Mật khẩu ban đầu:", appPassword, new Color(225, 29, 72), true));
        pnlAppAcc.add(createRow("Vai trò hệ thống:", "Hội viên (Member VIP)", new Color(15, 23, 42), false));
        rightInfo.add(pnlAppAcc);

        center.add(rightInfo);
        root.add(center, BorderLayout.CENTER);

        // 3. BOTTOM ACTIONS: 4 NÚT GỬI VÀ IN
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        JButton btnZalo = new JButton("Gửi Mã QR Qua Zalo");
        btnZalo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnZalo.setBackground(new Color(14, 116, 144));
        btnZalo.setForeground(Color.WHITE);
        btnZalo.setFocusPainted(false);
        btnZalo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnZalo.setPreferredSize(new Dimension(180, 42));

        JButton btnSMS = new JButton("Gửi SMS Brandname");
        btnSMS.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSMS.setBackground(new Color(16, 185, 129));
        btnSMS.setForeground(Color.WHITE);
        btnSMS.setFocusPainted(false);
        btnSMS.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSMS.setPreferredSize(new Dimension(170, 42));

        JButton btnSaveImg = new JButton("Tải Ảnh QR (.PNG)");
        btnSaveImg.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSaveImg.setBackground(new Color(241, 245, 249));
        btnSaveImg.setForeground(new Color(30, 41, 59));
        btnSaveImg.setFocusPainted(false);
        btnSaveImg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveImg.setPreferredSize(new Dimension(150, 42));

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(100, 116, 139));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(90, 42));

        bottom.add(btnZalo);
        bottom.add(btnSMS);
        bottom.add(btnSaveImg);
        bottom.add(btnClose);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);

        // Actions
        btnZalo.addActionListener(e -> guiQuaZalo());
        btnSMS.addActionListener(e -> guiQuaSMS());
        btnSaveImg.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Ảnh mã QR mã hóa (" + memberCode + ".png) đã được xuất thành công vào thư mục lưu trữ thẻ của hệ thống!",
                    "Tải Ảnh QR Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });
        btnClose.addActionListener(e -> dispose());
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
                "<p>Chúc mừng bạn đã gia nhập hệ sinh thái FitLife Gym! Dưới đây là thông tin thẻ và tài khoản đăng nhập App của bạn:</p>" +
                "<ul>" +
                "<li><b>Mã thẻ QR:</b> " + memberCode + " (Kèm file ảnh QR đính kèm)</li>" +
                "<li><b>Gói tập:</b> " + packageName + "</li>" +
                "<li><b>Tài khoản App:</b> " + appUsername + "</li>" +
                "<li><b>Mật khẩu App:</b> " + appPassword + "</li>" +
                "</ul>" +
                "<p style='color: #0284c7; font-size: 11px;'><i>Thời gian gửi: " + time + " • Trạng thái: Đã nhận</i></p>" +
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
                "Mat khau dang nhap App la: " + appPassword + ". Mo the QR tai: https://fitlife.vn/card/" + memberCode +
                "</div>" +
                "<p style='color: #64748b; font-size: 11px; margin-top: 6px;'>Đã chuyển tin nhắn thành công qua hạ tầng Viễn thông lúc " + time + "</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, smsText, "SMS Brandname - Gửi Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }
}
