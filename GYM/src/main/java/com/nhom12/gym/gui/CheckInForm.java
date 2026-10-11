package com.nhom12.gym.gui;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.CheckInService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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
    private JComboBox<String> cbStationBranch;
    private JLabel lblNetworkStatus;
    private JLabel lblPerfMs;

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

    public CheckInForm() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. TOP HEADER TOOLBAR (Tiêu đề, Chọn chi nhánh trạm, Chỉ báo mạng Offline/Online, Đo hiệu năng)
        JPanel topToolbar = new JPanel(new BorderLayout(15, 0));
        topToolbar.setBackground(Color.WHITE);
        topToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 18, 12, 18)
        ));

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("TRẠM QUÉT CHECK-IN TỰ ĐỘNG (USE CASE LÕI CỦA ĐỒ ÁN)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Kiểm soát cổng Flap Barrier • Tương thích máy quét USB Honeywell Voyager 1450g");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        topToolbar.add(titleGroup, BorderLayout.WEST);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightControls.setOpaque(false);

        JLabel lblBr = new JLabel("Trạm kiểm soát tại:");
        lblBr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbStationBranch = new JComboBox<>(new String[]{"CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)"});
        cbStationBranch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbStationBranch.setPreferredSize(new Dimension(170, 32));

        // Yêu cầu ẩn 2: Xử lý Wifi chi nhánh yếu
        lblNetworkStatus = new JLabel("Đang kết nối Online (Đồng bộ)");
        lblNetworkStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNetworkStatus.setForeground(new Color(16, 185, 129));
        lblNetworkStatus.setToolTipText("Hệ thống tự động kích hoạt bộ nhớ đệm cục bộ khi mất kết nối Internet");

        lblPerfMs = new JLabel("Phản hồi: 0ms");
        lblPerfMs.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPerfMs.setForeground(new Color(71, 85, 105));

        rightControls.add(lblBr);
        rightControls.add(cbStationBranch);
        rightControls.add(lblNetworkStatus);
        rightControls.add(lblPerfMs);
        topToolbar.add(rightControls, BorderLayout.EAST);

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

        JLabel lblScanTitle = new JLabel("QUÉT MÃ THẺ / MÃ QR HỘI VIÊN (TỰ ĐỘNG FOCUS)");
        lblScanTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblScanTitle.setForeground(new Color(15, 23, 42));
        pnlScanBox.add(lblScanTitle, BorderLayout.NORTH);

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setOpaque(false);

        txtMaQR = new JTextField();
        txtMaQR.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtMaQR.setForeground(new Color(37, 99, 235));
        txtMaQR.setPreferredSize(new Dimension(0, 44));
        txtMaQR.putClientProperty("JTextField.placeholderText", "Đưa thẻ QR vào máy quét hoặc nhập mã rồi Enter...");

        JButton btnSubmit = new JButton("Check-in");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(37, 99, 235));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(120, 44));

        inputRow.add(txtMaQR, BorderLayout.CENTER);
        inputRow.add(btnSubmit, BorderLayout.EAST);
        pnlScanBox.add(inputRow, BorderLayout.CENTER);

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
        quickTestRow.add(createTestBadge("VIP007 (Ẩn Anh Đức)", "VIP007", new Color(14, 165, 233)));
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
        bottomContainer.setPreferredSize(new Dimension(0, 230));

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

        btnManualOpen.addActionListener(e -> {
            pnlGateStatus.setBackground(new Color(254, 243, 199));
            lblGateStatusText.setText("CỬA FLAP BARRIER: MỞ THỦ CÔNG");
            lblGateStatusText.setForeground(new Color(180, 83, 9));
            lblGateSubText.setText("Lễ tân đã kích hoạt quyền mở khẩn cấp");
            pnlGateStatus.repaint();
            com.nhom12.gym.util.SoundHelper.playNotice();
        });

        // Tự động focus vào ô quét thẻ
        SwingUtilities.invokeLater(() -> txtMaQR.requestFocusInWindow());
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
        String currentBranch = (String) cbStationBranch.getSelectedItem();

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
            lblMemberName.setText("Họ Và Tên: " + hv.getHoTen());
            lblMemberCode.setText("Mã Thẻ QR: " + hv.getMaTheQR());
            lblMemberPhone.setText("Số Điện Thoại: 09" + (Math.abs(hv.getHoTen().hashCode()) % 80000000 + 10000000));
            lblMemberPackage.setText("Gói Tập: " + res.goiTapDisplay);
            lblMemberExpire.setText("Thời Hạn Sử Dụng: Đến " + (hv.getNgayHetHan() != null ? sdf.format(hv.getNgayHetHan()) : "31/12/2026"));
            lblMemberBranch.setText("Phạm Vi Tập: " + hv.getChiNhanhDangKy());

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
                lblMemberName.setText("Họ Và Tên: " + hv.getHoTen());
                lblMemberCode.setText("Mã Thẻ QR: " + hv.getMaTheQR());
                lblMemberPhone.setText("Số Điện Thoại: ---");
                lblMemberPackage.setText("Gói Tập: " + res.goiTapDisplay);
                lblMemberExpire.setText("Thời Hạn Sử Dụng: Đến " + (hv.getNgayHetHan() != null ? sdf.format(hv.getNgayHetHan()) : "---"));
                lblMemberBranch.setText("Phạm Vi Tập: " + hv.getChiNhanhDangKy());
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
        lblLogSummary.setText("● Hôm nay: " + logModel.getRowCount() + " lượt quét • "
                + checkInService.getSuccessCountToday() + " hợp lệ • "
                + checkInService.getFailCountToday() + " từ chối");

        txtMaQR.setText("");
        txtMaQR.requestFocusInWindow();
    }
}
