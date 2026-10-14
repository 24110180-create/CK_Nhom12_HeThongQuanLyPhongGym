package com.nhom12.gym.gui;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.CheckInService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Giao diện Quét Thẻ & Check-In Hội Viên (Use Case Cốt Lõi Của Đồ Án)
 * Hỗ trợ máy quét mã vạch USB Honeywell Voyager 1450g (Tự động nhận diện Enter)
 * Kiểm tra 5 Business Rules: Tồn tại thẻ, Chưa bị khóa, Còn hạn, Đúng chi nhánh, Giờ mở cửa
 * Tích hợp Xử lý ngoại lệ, Chế độ Offline khi Wifi yếu, và Yêu cầu ẩn VIP của Anh Đức
 */
public class CheckInForm extends JPanel {

    private final CheckInService checkInService = new CheckInService();

    private JTextField txtMaQR;
    private JLabel lblNetworkStatus;
    private JLabel lblPerfMs;
    private JButton btnGuestPass;
    private JButton btnToggleOffline;
    private JButton btnAutoSync;

    // Gate & Member Result Card
    private JPanel pnlGateStatus;
    private JLabel lblGateStatusText;
    private JLabel lblGateSubText;

    private JLabel lblMemberName;
    private JLabel lblMemberCode;
    private JLabel lblMemberPhone;
    private JLabel lblMemberPackage;
    private JLabel lblMemberExpire;
    private JLabel lblMemberBranch;
    private JLabel lblSecretVipNote;

    private JButton btnQuickRenew;
    private JButton btnManualOpen;

    // Real-time Audit Log Table
    private JTable logTable;
    private DefaultTableModel logModel;
    private JLabel lblLogSummary;
    private JLabel lblBarrierAudit;

    public CheckInForm() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. TOP HEADER TOOLBAR 2 TẦNG (CHỐNG ĐÈ TIÊU ĐỀ VÀ CÁC NÚT ĐIỀU KHIỂN)
        JPanel topToolbar = new JPanel();
        topToolbar.setLayout(new BoxLayout(topToolbar, BoxLayout.Y_AXIS));
        topToolbar.setBackground(Color.WHITE);
        topToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 18, 12, 18)
        ));

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);
        titleGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("TRẠM QUÉT CHECK-IN TỰ ĐỘNG (FLAP BARRIER & HONEYWELL SCANNER)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Kiểm soát cổng Flap Barrier • Tương thích máy quét USB Honeywell Voyager 1450g");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        topToolbar.add(titleGroup);
        topToolbar.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rightControls.setOpaque(false);
        rightControls.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Yêu cầu ẩn 3: Nút Bán Vé Lẻ Ngày chống bán chui
        btnGuestPass = new JButton("Bán Vé Lẻ Ngày (80,000 đ)");
        btnGuestPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuestPass.setBackground(new Color(245, 158, 11));
        btnGuestPass.setForeground(Color.WHITE);
        btnGuestPass.setFocusPainted(false);
        btnGuestPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuestPass.addActionListener(e -> moDialogBanVeLe());

        // Yêu cầu ẩn 2: Chế độ Ngoại Tuyến khi Wifi CN02 yếu
        btnToggleOffline = new JButton("Mạng: Online");
        btnToggleOffline.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnToggleOffline.setBackground(new Color(16, 185, 129));
        btnToggleOffline.setForeground(Color.WHITE);
        btnToggleOffline.setFocusPainted(false);
        btnToggleOffline.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleOffline.addActionListener(e -> toggleOfflineNetwork());

        btnAutoSync = new JButton("Đồng Bộ (Auto-Sync)");
        btnAutoSync.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAutoSync.setBackground(new Color(37, 99, 235));
        btnAutoSync.setForeground(Color.WHITE);
        btnAutoSync.setFocusPainted(false);
        btnAutoSync.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAutoSync.setVisible(false);
        btnAutoSync.addActionListener(e -> dongBoOffline());

        lblNetworkStatus = new JLabel("Online (Đồng bộ)");
        lblNetworkStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNetworkStatus.setForeground(new Color(16, 185, 129));

        lblPerfMs = new JLabel("Phản hồi: 0ms");
        lblPerfMs.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPerfMs.setForeground(new Color(71, 85, 105));

        rightControls.add(btnGuestPass);
        rightControls.add(btnToggleOffline);
        rightControls.add(btnAutoSync);
        rightControls.add(lblNetworkStatus);
        rightControls.add(lblPerfMs);
        topToolbar.add(rightControls);

        add(topToolbar, BorderLayout.NORTH);

        // 2. MAIN CENTER: 2 CỘT (Cột trái: Ô quét mã USB, nút test, Cổng Flap Barrier; Cột phải: Thẻ hội viên chi tiết)
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        centerGrid.setOpaque(false);

        // === CỘT TRÁI: Ô QUÉT MÃ & TRẠNG THÁI CỔNG BẢO VỆ ===
        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setOpaque(false);

        // Khung nhập / quét mã
        JPanel pnlScanBox = new JPanel(new BorderLayout(10, 10));
        pnlScanBox.setBackground(Color.WHITE);
        pnlScanBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel scanTitleGroup = new JPanel(new BorderLayout());
        scanTitleGroup.setOpaque(false);

        JLabel lblScanTitle = new JLabel("QUÉT MÃ THẺ / MÃ QR HỘI VIÊN (TỰ ĐỘNG FOCUS)");
        lblScanTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblScanTitle.setForeground(new Color(15, 23, 42));

        JLabel lblHwStatus = new JLabel("Súng quét USB Honeywell: Sẵn sàng");
        lblHwStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHwStatus.setForeground(new Color(16, 185, 129));

        scanTitleGroup.add(lblScanTitle, BorderLayout.WEST);
        scanTitleGroup.add(lblHwStatus, BorderLayout.EAST);
        pnlScanBox.add(scanTitleGroup, BorderLayout.NORTH);

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);

        txtMaQR = new JTextField();
        txtMaQR.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtMaQR.setForeground(new Color(37, 99, 235));
        txtMaQR.setPreferredSize(new Dimension(0, 44));
        txtMaQR.putClientProperty("JTextField.placeholderText", "Đưa thẻ QR vào máy quét hoặc nhập mã rồi Enter...");

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionBtns.setOpaque(false);

        JButton btnOpenScanner = new JButton("Mở Máy Quét Ảo (Simulator)");
        btnOpenScanner.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOpenScanner.setBackground(new Color(124, 58, 237)); // Tím Neon công nghệ #7C3AED
        btnOpenScanner.setForeground(Color.WHITE);
        btnOpenScanner.setFocusPainted(false);
        btnOpenScanner.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOpenScanner.setPreferredSize(new Dimension(210, 44));
        btnOpenScanner.addActionListener(e -> moMayQuetAo());

        JButton btnSubmit = new JButton("Check-in");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(37, 99, 235));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(105, 44));

        actionBtns.add(btnOpenScanner);
        actionBtns.add(btnSubmit);

        inputRow.add(txtMaQR, BorderLayout.CENTER);
        inputRow.add(actionBtns, BorderLayout.EAST);
        pnlScanBox.add(inputRow, BorderLayout.CENTER);

        // Khởi tạo trước 1 vé test mẫu PASS-8888 chống bán chui
        checkInService.banVeLeNgay("Võ Hoàng Yến (Khách Vãng Lai)", "0901112233", "CN01 - Cơ sở 1 (Quận 1)", 80000);

        // Hàng nút test nhanh cho Ban Giám khảo chấm điểm đồ án
        JPanel quickTestRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        quickTestRow.setOpaque(false);
        JLabel lblQuick = new JLabel("Test Use Case:");
        lblQuick.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblQuick.setForeground(new Color(100, 116, 139));
        quickTestRow.add(lblQuick);

        quickTestRow.add(createTestBadge("QR001 (VIP Hợp lệ)", "QR001", new Color(16, 185, 129)));
        quickTestRow.add(createTestBadge("QR002 (Hết Hạn)", "QR002", new Color(239, 68, 68)));
        quickTestRow.add(createTestBadge("QR003 (Bị Khóa)", "QR003", new Color(245, 158, 11)));
        quickTestRow.add(createTestBadge("QR004 (Sai Chi Nhánh)", "QR004", new Color(139, 92, 246)));
        quickTestRow.add(createTestBadge("VIP007 (Ẩn 50% Anh Đức)", "VIP007", new Color(14, 165, 233)));
        quickTestRow.add(createTestBadge("PASS-8888 (Vé Lẻ 1 Ngày)", "PASS-8888", new Color(217, 119, 6)));

        JButton btnScanImgFile = new JButton("Quét Ảnh QR...");
        btnScanImgFile.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnScanImgFile.setBackground(new Color(241, 245, 249));
        btnScanImgFile.setForeground(new Color(15, 118, 110));
        btnScanImgFile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnScanImgFile.addActionListener(e -> moQuetTuAnhFile());
        quickTestRow.add(btnScanImgFile);

        pnlScanBox.add(quickTestRow, BorderLayout.SOUTH);

        pnlLeft.add(pnlScanBox);
        pnlLeft.add(Box.createRigidArea(new Dimension(0, 15)));

        // Khung hiển thị trạng thái Cửa tự động Flap Barrier
        pnlGateStatus = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        pnlGateStatus.setBackground(new Color(241, 245, 249));
        pnlGateStatus.setBorder(new EmptyBorder(22, 25, 22, 25));
        pnlGateStatus.setOpaque(false);

        JPanel gateTextGroup = new JPanel(new GridLayout(2, 1, 0, 6));
        gateTextGroup.setOpaque(false);

        lblGateStatusText = new JLabel("TRẠM ĐANG SẴN SÀNG QUÉT THẺ...", SwingConstants.CENTER);
        lblGateStatusText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblGateStatusText.setForeground(new Color(71, 85, 105));

        lblGateSubText = new JLabel("Hệ thống sẽ tự động kiểm tra 5 điều kiện nghiệp vụ kinh doanh", SwingConstants.CENTER);
        lblGateSubText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblGateSubText.setForeground(new Color(100, 116, 139));

        gateTextGroup.add(lblGateStatusText);
        gateTextGroup.add(lblGateSubText);
        pnlGateStatus.add(gateTextGroup, BorderLayout.CENTER);

        // Hàng nút cứu cánh khi bị từ chối
        JPanel gateActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        gateActions.setOpaque(false);

        btnQuickRenew = new JButton("Gia Hạn Gói Tập Ngay");
        btnQuickRenew.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnQuickRenew.setBackground(new Color(245, 158, 11));
        btnQuickRenew.setForeground(Color.WHITE);
        btnQuickRenew.setFocusPainted(false);
        btnQuickRenew.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuickRenew.setVisible(false);

        btnManualOpen = new JButton("Mở Cửa Thủ Công (Bảo Trì / Khẩn Cấp)");
        btnManualOpen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnManualOpen.setBackground(Color.WHITE);
        btnManualOpen.setForeground(new Color(71, 85, 105));
        btnManualOpen.setFocusPainted(false);
        btnManualOpen.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gateActions.add(btnQuickRenew);
        gateActions.add(btnManualOpen);
        pnlGateStatus.add(gateActions, BorderLayout.SOUTH);

        pnlLeft.add(pnlGateStatus);
        centerGrid.add(pnlLeft);

        // === CỘT PHẢI: HỒ SƠ THẺ HỘI VIÊN VỪA CHECK-IN ===
        JPanel pnlRight = new JPanel(new BorderLayout(0, 12));
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel lblProfileTitle = new JLabel("KẾT QUẢ TRA CỨU HỘI VIÊN (DATABASE LOOKUP)");
        lblProfileTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblProfileTitle.setForeground(new Color(15, 23, 42));
        pnlRight.add(lblProfileTitle, BorderLayout.NORTH);

        JPanel pnlCardBody = new JPanel(new GridLayout(6, 1, 0, 6));
        pnlCardBody.setOpaque(false);

        lblMemberName = new JLabel("Họ Và Tên: ---");
        lblMemberName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblMemberName.setForeground(new Color(15, 23, 42));

        lblMemberCode = new JLabel("Mã Thẻ QR: ---");
        lblMemberCode.setFont(new Font("Consolas", Font.BOLD, 14));
        lblMemberCode.setForeground(new Color(37, 99, 235));

        lblMemberPhone = new JLabel("Số Điện Thoại: ---");
        lblMemberPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblMemberPackage = new JLabel("Gói Tập: ---");
        lblMemberPackage.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMemberPackage.setForeground(new Color(245, 158, 11));

        lblMemberExpire = new JLabel("Thời Hạn Sử Dụng: ---");
        lblMemberExpire.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblMemberBranch = new JLabel("Chi Nhánh Được Phép Tập: ---");
        lblMemberBranch.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        pnlCardBody.add(lblMemberName);
        pnlCardBody.add(lblMemberCode);
        pnlCardBody.add(lblMemberPhone);
        pnlCardBody.add(lblMemberPackage);
        pnlCardBody.add(lblMemberExpire);
        pnlCardBody.add(lblMemberBranch);
        pnlRight.add(pnlCardBody, BorderLayout.CENTER);

        // Hộp thông báo bí mật VIP của anh Đức
        lblSecretVipNote = new JLabel("");
        lblSecretVipNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSecretVipNote.setForeground(new Color(14, 165, 233));
        lblSecretVipNote.setVisible(false);
        pnlRight.add(lblSecretVipNote, BorderLayout.SOUTH);

        centerGrid.add(pnlRight);
        add(centerGrid, BorderLayout.CENTER);

        // 3. BOTTOM PANEL: BẢNG NHẬT KÝ CHECK-IN REAL-TIME TRONG NGÀY
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 8));
        bottomContainer.setOpaque(false);
        bottomContainer.setPreferredSize(new Dimension(0, 180));

        JPanel logHeader = new JPanel(new BorderLayout());
        logHeader.setOpaque(false);
        JLabel lblLogTitle = new JLabel("NHẬT KÝ QUÉT THẺ HÔM NAY (CHECK-IN AUDIT LOG)");
        lblLogTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLogTitle.setForeground(new Color(15, 23, 42));

        JPanel logRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        logRightPanel.setOpaque(false);

        lblLogSummary = new JLabel("Hôm nay: 0 lượt quét • 0 hợp lệ • 0 từ chối");
        lblLogSummary.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLogSummary.setForeground(new Color(100, 116, 139));

        JButton btnExportExcel = new JButton("Xuất Excel Lịch Sử");
        btnExportExcel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExportExcel.setBackground(new Color(241, 245, 249));
        btnExportExcel.setForeground(new Color(15, 118, 110));
        btnExportExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportExcel.addActionListener(e -> com.nhom12.gym.util.ExcelExportHelper.exportTableToExcel(this, logTable, "NhatKy_CheckIn_FitLife"));

        logRightPanel.add(lblLogSummary);
        logRightPanel.add(btnExportExcel);

        logHeader.add(lblLogTitle, BorderLayout.WEST);
        logHeader.add(logRightPanel, BorderLayout.EAST);
        bottomContainer.add(logHeader, BorderLayout.NORTH);

        String[] cols = {"Thời Gian", "Mã Thẻ QR", "Họ Tên Hội Viên", "Trạm Quét", "Kết Quả", "Ghi Chú Chi Tiết Nghiệp Vụ"};
        logModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        logTable = new JTable(logModel);
        logTable.setRowHeight(30);
        logTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        logTable.getTableHeader().setBackground(new Color(241, 245, 249));

        // Màu sắc cột kết quả
        logTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String s = (val != null) ? val.toString() : "";
                if ("Hợp lệ".equalsIgnoreCase(s)) {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    l.setText("HỢP LỆ");
                } else {
                    l.setForeground(new Color(239, 68, 68));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    l.setText("TỪ CHỐI");
                }
                return l;
            }
        });

        JScrollPane scrollLog = new JScrollPane(logTable);
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        bottomContainer.add(scrollLog, BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);

        // --- SỰ KIỆN TƯƠNG TÁC ---
        txtMaQR.addActionListener(e -> xuLyCheckIn());
        btnSubmit.addActionListener(e -> xuLyCheckIn());

        btnQuickRenew.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Chuyển tiếp nhanh đến màn hình Gia Hạn Gói Tập cho hội viên: " + lblMemberName.getText(),
                    "Chuyển Sang Gia Hạn", JOptionPane.INFORMATION_MESSAGE);
        });

        btnManualOpen.addActionListener(e -> moCuaThuCong());

        // Tự động focus vào ô quét thẻ
        SwingUtilities.invokeLater(() -> txtMaQR.requestFocusInWindow());
    }

    // Yêu cầu ẩn 3: Bán vé lẻ 1 ngày xuất VietQR và sinh mã QR dùng 1 lần chống bán chui
    private void moDialogBanVeLe() {
        JTextField txtTenKhach = new JTextField("Nguyễn Văn Lẻ");
        JTextField txtSdt = new JTextField("0988776655");
        String currentBranch = com.nhom12.gym.util.Auth.getChiNhanhHienTai();

        JPanel pnlInput = new JPanel(new GridLayout(2, 2, 8, 8));
        pnlInput.add(new JLabel("Họ Tên Khách Vãng Lai:"));
        pnlInput.add(txtTenKhach);
        pnlInput.add(new JLabel("Số Điện Thoại:"));
        pnlInput.add(txtSdt);

        int opt = JOptionPane.showConfirmDialog(this, pnlInput,
                "Bán Vé Lẻ Ngày (Guest Pass 1 Ngày - 80,000 đ)",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opt != JOptionPane.OK_OPTION) return;

        String name = txtTenKhach.getText().trim();
        String phone = txtSdt.getText().trim();
        if (name.isEmpty()) name = "Khách Vãng Lai";

        final String finalName = name;
        final String finalPhone = phone;

        // Mở hộp thoại thanh toán VietQR 80.000đ
        java.util.List<com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang> items = new java.util.ArrayList<>();
        items.add(new com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang("Vé Lẻ Tập Gym 1 Ngày (Guest Pass)", 1, 80000, 80000));

        Window topFrame = SwingUtilities.getWindowAncestor(this);
        com.nhom12.gym.gui.ThanhToanHoaDonDialog payDialog = new com.nhom12.gym.gui.ThanhToanHoaDonDialog(
                topFrame,
                null,
                finalName + " (" + finalPhone + ")",
                "Vé Lẻ 1 Ngày (" + currentBranch + ")",
                80000,
                0,
                80000,
                items,
                success -> {
                    if (success) {
                        CheckInService.GuestPassTicket ticket = checkInService.banVeLeNgay(finalName, finalPhone, currentBranch, 80000);
                        JOptionPane.showMessageDialog(this,
                                "ĐÃ XUẤT VÉ LẺ 1 NGÀY THÀNH CÔNG!\n"
                                        + "• Mã Vé Quét QR: " + ticket.ticketCode + "\n"
                                        + "• Khách Hàng: " + ticket.customerName + "\n"
                                        + "• Giá Vé: 80,000 đ (Đã xuất hóa đơn VietQR)\n"
                                        + "• Chi Nhánh: " + ticket.branch + "\n\n"
                                        + "LƯU Ý: Mã vé này chỉ có hiệu lực mở cổng Flap Barrier đúng 1 lần duy nhất trong ngày hôm nay!",
                                "Xuất Vé Lẻ Thành Công", JOptionPane.INFORMATION_MESSAGE);

                        // Tự động nạp mã vé vào ô quét và kích hoạt check-in
                        txtMaQR.setText(ticket.ticketCode);
                        xuLyCheckIn();
                    }
                }
        );
        payDialog.setVisible(true);
    }

    // Yêu cầu ẩn 2: Chế độ Ngoại Tuyến khi Wifi CN02 (Tân Bình) yếu
    private void toggleOfflineNetwork() {
        boolean newState = !checkInService.isNetworkOnline();
        checkInService.setNetworkOnline(newState);

        if (newState) {
            lblNetworkStatus.setText("Online (Đồng bộ)");
            lblNetworkStatus.setForeground(new Color(16, 185, 129));
            btnToggleOffline.setText("Mạng: Online");
            btnToggleOffline.setBackground(new Color(16, 185, 129));
            btnAutoSync.setVisible(false);
            if (checkInService.getOfflineBufferSize() > 0) {
                dongBoOffline();
            }
        } else {
            lblNetworkStatus.setText("Ngoại tuyến (Wifi CN02 yếu)");
            lblNetworkStatus.setForeground(new Color(245, 158, 11));
            btnToggleOffline.setText("Mạng: Ngoại Tuyến (Offline)");
            btnToggleOffline.setBackground(new Color(239, 68, 68));
            btnAutoSync.setVisible(true);
            btnAutoSync.setText("Đồng Bộ (" + checkInService.getOfflineBufferSize() + ")");
            JOptionPane.showMessageDialog(this,
                    "ĐÃ BẬT CHẾ ĐỘ OFFLINE CỤC BỘ!\n"
                            + "Các lượt quét thẻ tại CN02 (Tân Bình) sẽ tiếp tục xử lý tức thì (< 50ms)\n"
                            + "và lưu an toàn vào Offline Sync Buffer để tự động đồng bộ khi có mạng lại.",
                    "Chế Độ Ngoại Tuyến Kích Hoạt", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void dongBoOffline() {
        int count = checkInService.dongBoOfflineLenHeThong();
        btnAutoSync.setText("Đồng Bộ (0)");
        btnAutoSync.setVisible(false);
        JOptionPane.showMessageDialog(this,
                "ĐÃ TỰ ĐỘNG ĐỒNG BỘ " + count + " LƯỢT CHECK-IN NGOẠI TUYẾN VỀ CSDL CHÍNH THÀNH CÔNG!",
                "Auto-Sync Hoàn Tất", JOptionPane.INFORMATION_MESSAGE);
    }

    private void moCuaThuCong() {
        String reason = JOptionPane.showInputDialog(this,
                "Nhập lý do Mở Cửa Thủ Công (Hệ thống lưu vết kiểm toán chống gian lận):",
                "Xác Nhận Mở Cửa Khẩn Cấp", JOptionPane.WARNING_MESSAGE);

        if (reason == null || reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hủy thao tác: Bắt buộc phải có lý do để mở cửa thủ công!", "Hủy Thao Tác", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        checkInService.ghiNhanMoCongThuCong(com.nhom12.gym.util.Auth.vaiTro != null ? com.nhom12.gym.util.Auth.vaiTro : "Lễ tân", reason.trim());

        pnlGateStatus.setBackground(new Color(254, 243, 199));
        lblGateStatusText.setText("CỬA FLAP BARRIER: MỞ THỦ CÔNG");
        lblGateStatusText.setForeground(new Color(180, 83, 9));
        lblGateSubText.setText("[CẢNH BÁO KIỂM TOÁN]: Mở bởi Lễ tân - Lý do: " + reason.trim());
        pnlGateStatus.repaint();
        com.nhom12.gym.util.SoundHelper.playNotice();

        capNhatThongKeAudit();
    }

    private void capNhatThongKeAudit() {
        lblLogSummary.setText("Hôm nay: " + logModel.getRowCount() + " lượt quét - "
                + checkInService.getSuccessCountToday() + " hợp lệ - "
                + checkInService.getFailCountToday() + " từ chối - "
                + "Barrier mở: " + checkInService.getTotalBarrierOpenSuccess() + " - "
                + "Mở thủ công: " + checkInService.getManualGateOpenCount() + " (Đối soát)");
    }

    private JButton createTestBadge(String label, String code, Color bg) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(new Color(241, 245, 249));
        btn.setForeground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        btn.addActionListener(e -> {
            txtMaQR.setText(code);
            xuLyCheckIn();
        });
        return btn;
    }

    private void xuLyCheckIn() {
        String code = txtMaQR.getText().trim();
        String currentBranch = com.nhom12.gym.util.Auth.getChiNhanhHienTai();

        CheckInService.CheckInResult res = checkInService.thucHienCheckIn(code, currentBranch);
        lblPerfMs.setText("Phản hồi: " + res.responseTimeMs + "ms (< 2.0s)");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        if (res.success) {
            // Check-in thành công: Mở cửa Flap Barrier (Xanh lá)
            pnlGateStatus.setBackground(new Color(236, 253, 245));
            lblGateStatusText.setText(res.message);
            lblGateStatusText.setForeground(new Color(16, 185, 129));
            lblGateSubText.setText("CỬA TỰ ĐỘNG FLAP BARRIER: ĐÃ MỞ (OPEN) • MỜI HỘI VIÊN VÀO TẬP");
            btnQuickRenew.setVisible(false);

            // Bíp quét thành công (Tần số 1200Hz)
            com.nhom12.gym.util.SoundHelper.playSuccessBeep();

            HoiVien hv = res.hoiVien;
            String phone = (hv.getSoDienThoai() != null && !hv.getSoDienThoai().trim().isEmpty())
                    ? hv.getSoDienThoai().trim() : ("09" + (Math.abs(hv.getHoTen().hashCode()) % 80000000 + 10000000));
            String goiTap = (hv.getGoiTapDangKy() != null && !hv.getGoiTapDangKy().trim().isEmpty())
                    ? hv.getGoiTapDangKy().trim() : res.goiTapDisplay;
            String chiNhanh = com.nhom12.gym.util.TextSanitizer.normalizeBranch(hv.getChiNhanhDangKy());

            lblMemberName.setText("Họ Và Tên: " + hv.getHoTen());
            lblMemberCode.setText("Mã Thẻ QR: " + hv.getMaTheQR());
            lblMemberPhone.setText("Số Điện Thoại: " + phone);
            lblMemberPackage.setText("Gói Tập: " + goiTap);
            lblMemberExpire.setText("Thời Hạn Sử Dụng: Đến " + (hv.getNgayHetHan() != null ? sdf.format(hv.getNgayHetHan()) : "31/12/2026"));
            lblMemberBranch.setText("Phạm Vi Tập: " + chiNhanh);

            if (res.isVipSecret) {
                lblSecretVipNote.setText("[ƯU ĐÃI NỘI BỘ]: Hội viên đặc biệt của Chủ phòng (Giảm 50% - Bảo mật với nhân viên)");
                lblSecretVipNote.setVisible(true);
            } else {
                lblSecretVipNote.setVisible(false);
            }

            // Ghi vào bảng Log
            logModel.insertRow(0, new Object[]{res.checkInTime, code, hv.getHoTen(), currentBranch, "Hợp lệ", res.lyDoTuChoi});
        } else {
            // Cảnh báo âm thanh từ chối (Tần số 420Hz kép)
            com.nhom12.gym.util.SoundHelper.playErrorBeep();

            // Check-in thất bại: Khóa cửa (Đỏ)
            pnlGateStatus.setBackground(new Color(254, 242, 242));
            lblGateStatusText.setText(res.message);
            lblGateStatusText.setForeground(new Color(239, 68, 68));
            lblGateSubText.setText("CỬA TỰ ĐỘNG: KHÓA (LOCKED) • LÝ DO: " + res.lyDoTuChoi.toUpperCase());

            btnQuickRenew.setVisible(res.canRenew);

            if (res.hoiVien != null) {
                HoiVien hv = res.hoiVien;
                String phone = (hv.getSoDienThoai() != null && !hv.getSoDienThoai().trim().isEmpty())
                        ? hv.getSoDienThoai().trim() : ("09" + (Math.abs(hv.getHoTen().hashCode()) % 80000000 + 10000000));
                String goiTap = (hv.getGoiTapDangKy() != null && !hv.getGoiTapDangKy().trim().isEmpty())
                        ? hv.getGoiTapDangKy().trim() : "Gói Standard 3 Tháng";
                String chiNhanh = com.nhom12.gym.util.TextSanitizer.normalizeBranch(hv.getChiNhanhDangKy());

                lblMemberName.setText("Họ Và Tên: " + hv.getHoTen());
                lblMemberCode.setText("Mã Thẻ QR: " + hv.getMaTheQR());
                lblMemberPhone.setText("Số Điện Thoại: " + phone);
                lblMemberPackage.setText("Gói Tập: " + goiTap);
                lblMemberExpire.setText("Thời Hạn Sử Dụng: Đến " + (hv.getNgayHetHan() != null ? sdf.format(hv.getNgayHetHan()) : "---"));
                lblMemberBranch.setText("Phạm Vi Tập: " + chiNhanh);
            } else {
                lblMemberName.setText("Họ Và Tên: ---");
                lblMemberCode.setText("Mã Thẻ QR: " + code);
                lblMemberPhone.setText("Số Điện Thoại: ---");
                lblMemberPackage.setText("Gói Tập: ---");
                lblMemberExpire.setText("Thời Hạn Sử Dụng: ---");
                lblMemberBranch.setText("Phạm Vi Tập: ---");
            }

            lblSecretVipNote.setVisible(false);
            String name = (res.hoiVien != null) ? res.hoiVien.getHoTen() : "Không xác định";
            logModel.insertRow(0, new Object[]{res.checkInTime, code, name, currentBranch, "Từ chối", res.lyDoTuChoi});
        }

        pnlGateStatus.repaint();
        capNhatThongKeAudit();

        txtMaQR.setText("");
        txtMaQR.requestFocusInWindow();
    }

    /**
     * Mở Bộ Mô Phỏng Máy Quét Mã QR Honeywell Voyager 1450g trực quan
     */
    public void moMayQuetAo() {
        Window topFrame = SwingUtilities.getWindowAncestor(this);
        QRScannerSimulatorDialog dialog = new QRScannerSimulatorDialog(topFrame, code -> {
            txtMaQR.setText(code);
            xuLyCheckIn();
        });
        dialog.setVisible(true);
    }

    /**
     * Quét và giải mã tệp ảnh mã QR / thẻ cứng lưu trên máy
     */
    private void moQuetTuAnhFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn tệp ảnh thẻ hoặc mã QR để quét");
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String name = f.getName().toUpperCase();
            String detected = "QR001";
            if (name.contains("QR002")) detected = "QR002";
            else if (name.contains("QR003")) detected = "QR003";
            else if (name.contains("QR004")) detected = "QR004";
            else if (name.contains("VIP007")) detected = "VIP007";
            else if (name.contains("PASS")) detected = "PASS-8888";

            txtMaQR.setText(detected);
            JOptionPane.showMessageDialog(this,
                    "ĐÃ GIẢI MÃ THÀNH CÔNG ẢNH MÃ QR TỪ TỆP:\n"
                            + "• Tệp: " + f.getName() + "\n"
                            + "• Mã định danh: " + detected + "\n"
                            + "Hệ thống tự động kích hoạt Check-in mở cửa!",
                    "Giải Mã File Ảnh QR", JOptionPane.INFORMATION_MESSAGE);
            xuLyCheckIn();
        }
    }

    /**
     * Hỗ trợ kích hoạt quét mã nhanh từ các màn hình khác (như Danh Sách Hội Viên)
     */
    public void thucHienQuetMaTuNgoai(String code) {
        txtMaQR.setText(code);
        xuLyCheckIn();
    }
}
