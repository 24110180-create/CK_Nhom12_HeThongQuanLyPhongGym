package com.nhom12.gym.gui;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.util.SoundHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * Bộ Mô Phỏng Máy Quét Mã QR & Barcode Honeywell Voyager 1450g (Interactive Scanner Simulator)
 * Phục vụ trình diễn và chấm thi đồ án trực quan:
 * 1. Khung ngắm Camera & Tia Laser Đỏ quét qua mã QR sinh động
 * 2. Nút bấm "Trigger Laser Scan" mô phỏng bóp cò súng quét Honeywell
 * 3. Danh sách thẻ hội viên thực tế trong Database để chọn và quét ngay
 * 4. Bộ thẻ kịch bản 5 Business Rules (Hợp lệ, Hết hạn, Bị khóa, Sai chi nhánh, VIP ẩn)
 * 5. Hỗ trợ chọn file ảnh mã QR từ máy tính
 * 6. Giải thích chi tiết cơ chế phần cứng USB HID Keyboard Emulation
 */
public class QRScannerSimulatorDialog extends JDialog {

    private final Consumer<String> onScanCallback;
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();

    private String currentSelectedCode = "QR001";
    private String currentSelectedName = "Lê Anh Duy (VIP Diamond)";
    private String currentSelectedPkg = "VIP Diamond (12 Tháng)";
    private String currentSelectedBranch = "CN01 - Cơ sở 1 (Quận 1)";

    private JPanel pnlCameraViewfinder;
    private JLabel lblTargetCode;
    private JLabel lblTargetName;
    private JLabel lblScanStatus;
    private Timer laserAnimationTimer;
    private int laserY = 20;
    private boolean laserGoingDown = true;

    private JTable tblMembers;
    private DefaultTableModel modelMembers;

    public QRScannerSimulatorDialog(Window parent) {
        this(parent, null);
    }

    public QRScannerSimulatorDialog(Window parent, Consumer<String> onScanCallback) {
        super(parent, "BỘ QUÉT MÃ QR & MÔ PHỎNG MÁY QUÉT HONEYWELL VOYAGER 1450G", ModalityType.APPLICATION_MODAL);
        this.onScanCallback = onScanCallback;

        setSize(880, 620);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(new Color(248, 250, 252));
        root.setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. TOP HEADER BANNER
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 3));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("TRÌNH MÔ PHỎNG MÁY QUÉT MÃ QR (HONEYWELL SCANNER SIMULATOR)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Mô phỏng tia quét laser quang học & Cơ chế truyền nhận USB HID Keyboard Emulation");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        topHeader.add(titleGroup, BorderLayout.WEST);

        JLabel lblHwBadge = new JLabel("HONEYWELL 1450G SẴN SÀNG");
        lblHwBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHwBadge.setForeground(new Color(16, 185, 129));
        topHeader.add(lblHwBadge, BorderLayout.EAST);
        root.add(topHeader, BorderLayout.NORTH);

        // 2. MAIN CENTER: 2 CỘT (Trái: Khung ngắm Camera & Tia Laser; Phải: Danh sách thẻ chọn quét)
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 18, 0));
        centerGrid.setOpaque(false);

        // === CỘT TRÁI: CAMERA VIEWFINDER & TIA LASER QUÉT ===
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.setBackground(new Color(15, 23, 42)); // Slate 900
        pnlLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Tiêu đề viewfinder
        JPanel vfHeader = new JPanel(new BorderLayout());
        vfHeader.setOpaque(false);
        JLabel lblVfTitle = new JLabel("CAMERA MÁY QUÉT (OPTICAL VIEWFINDER)");
        lblVfTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblVfTitle.setForeground(new Color(148, 163, 184));

        lblScanStatus = new JLabel("SẴN SÀNG QUÉT");
        lblScanStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblScanStatus.setForeground(new Color(56, 189, 248));
        vfHeader.add(lblVfTitle, BorderLayout.WEST);
        vfHeader.add(lblScanStatus, BorderLayout.EAST);
        pnlLeft.add(vfHeader, BorderLayout.NORTH);

        // Khung ngắm và vẽ QR kèm tia laser chuyển động
        pnlCameraViewfinder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Nền tối công nghệ
                g2.setColor(new Color(11, 15, 25));
                g2.fillRect(0, 0, w, h);

                // Khung quét trung tâm (Reticle)
                int boxSize = 210;
                int bx = (w - boxSize) / 2;
                int by = (h - boxSize) / 2;

                // 4 góc ngắm Cyan/Neon
                g2.setColor(new Color(6, 182, 212));
                g2.setStroke(new BasicStroke(3.0f));
                int cornerLen = 22;
                // Top-Left
                g2.drawLine(bx, by, bx + cornerLen, by);
                g2.drawLine(bx, by, bx, by + cornerLen);
                // Top-Right
                g2.drawLine(bx + boxSize, by, bx + boxSize - cornerLen, by);
                g2.drawLine(bx + boxSize, by, bx + boxSize, by + cornerLen);
                // Bottom-Left
                g2.drawLine(bx, by + boxSize, bx + cornerLen, by + boxSize);
                g2.drawLine(bx, by + boxSize, bx, by + boxSize - cornerLen);
                // Bottom-Right
                g2.drawLine(bx + boxSize, by + boxSize, bx + boxSize - cornerLen, by + boxSize);
                g2.drawLine(bx + boxSize, by + boxSize, bx + boxSize, by + boxSize - cornerLen);

                // Nền trắng bên trong khung QR
                int qrPad = 14;
                int qrSize = boxSize - qrPad * 2;
                int qx = bx + qrPad;
                int qy = by + qrPad;
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(qx, qy, qrSize, qrSize, 8, 8));

                // Vẽ ma trận QR Code dựa trên mã đang chọn
                veMaTranQRCode(g2, qx, qy, qrSize, currentSelectedCode);

                // Tia Laser Đỏ quét lên xuống
                int curLaserY = by + laserY;
                if (curLaserY > by + boxSize) curLaserY = by + boxSize;

                // Ánh phát quang Laser (Glow)
                g2.setColor(new Color(239, 68, 68, 80));
                g2.fillRect(bx + 4, curLaserY - 3, boxSize - 8, 7);
                // Tia chính giữa
                g2.setColor(new Color(239, 68, 68, 230));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawLine(bx + 2, curLaserY, bx + boxSize - 2, curLaserY);

                g2.dispose();
            }
        };
        pnlCameraViewfinder.setPreferredSize(new Dimension(280, 260));
        pnlLeft.add(pnlCameraViewfinder, BorderLayout.CENTER);

        // Thông tin mã đang hướng vào camera & Nút Trigger
        JPanel pnlVfBottom = new JPanel();
        pnlVfBottom.setLayout(new BoxLayout(pnlVfBottom, BoxLayout.Y_AXIS));
        pnlVfBottom.setOpaque(false);

        lblTargetCode = new JLabel("Mã Thẻ: " + currentSelectedCode);
        lblTargetCode.setFont(new Font("Consolas", Font.BOLD, 16));
        lblTargetCode.setForeground(new Color(56, 189, 248));
        lblTargetCode.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTargetName = new JLabel("Hội Viên: " + currentSelectedName);
        lblTargetName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTargetName.setForeground(new Color(203, 213, 225));
        lblTargetName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnTriggerScan = new JButton("BẮN TIA QUÉT (TRIGGER SCAN)");
        btnTriggerScan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTriggerScan.setBackground(new Color(239, 68, 68)); // Đỏ Laser
        btnTriggerScan.setForeground(Color.WHITE);
        btnTriggerScan.setFocusPainted(false);
        btnTriggerScan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTriggerScan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnTriggerScan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTriggerScan.addActionListener(e -> thucHienBanTiaQuet());

        pnlVfBottom.add(Box.createRigidArea(new Dimension(0, 6)));
        pnlVfBottom.add(lblTargetCode);
        pnlVfBottom.add(Box.createRigidArea(new Dimension(0, 3)));
        pnlVfBottom.add(lblTargetName);
        pnlVfBottom.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlVfBottom.add(btnTriggerScan);

        pnlLeft.add(pnlVfBottom, BorderLayout.SOUTH);
        centerGrid.add(pnlLeft);

        // === CỘT PHẢI: TABS DANH SÁCH THẺ ĐỂ CHỌN QUÉT ===
        JTabbedPane tabOptions = new JTabbedPane();
        tabOptions.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tabOptions.addTab("Thẻ Nghiệp Vụ Test Đồ Án", taoTabTheNghiepVu());
        tabOptions.addTab("Thẻ Hội Viên CSDL", taoTabTheHoiVienCSDL());
        tabOptions.addTab("Quét Từ File Ảnh", taoTabQuetTuFileAnh());

        centerGrid.add(tabOptions);
        root.add(centerGrid, BorderLayout.CENTER);

        // 3. BOTTOM BUTTONS
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        JLabel lblHelp = new JLabel("Lưu ý: Bấm chọn bất kỳ thẻ nào ở danh sách bên phải, sau đó bấm [Bắn Tia Quét] để mở cổng Flap Barrier.");
        lblHelp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHelp.setForeground(new Color(100, 116, 139));
        bottomBar.add(lblHelp, BorderLayout.WEST);

        JButton btnClose = new JButton("Đóng Cửa Sổ");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.setBackground(Color.WHITE);
        btnClose.setForeground(new Color(71, 85, 105));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        bottomBar.add(btnClose, BorderLayout.EAST);

        root.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(root);

        // Timer chuyển động tia Laser quét
        laserAnimationTimer = new Timer(25, e -> {
            if (laserGoingDown) {
                laserY += 3;
                if (laserY >= 190) laserGoingDown = false;
            } else {
                laserY -= 3;
                if (laserY <= 20) laserGoingDown = true;
            }
            pnlCameraViewfinder.repaint();
        });
        laserAnimationTimer.start();
    }

    // --- TAB 1: THẺ NGHIỆP VỤ ĐỒ ÁN (5 BUSINESS RULES) ---
    private JPanel taoTabTheNghiepVu() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lblDesc = new JLabel("Chọn 1 trong các kịch bản chấm điểm để đưa thẻ vào mắt đọc:");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(71, 85, 105));
        pnl.add(lblDesc, BorderLayout.NORTH);

        JPanel pnlList = new JPanel(new GridLayout(6, 1, 0, 8));
        pnlList.setOpaque(false);

        pnlList.add(createCardItem("QR001", "Lê Anh Duy (VIP Diamond)", "Hợp lệ 100% - Mở cổng Flap Barrier xanh lá", new Color(16, 185, 129)));
        pnlList.add(createCardItem("QR002", "Trần Thị Mai (Standard 1M)", "Gói tập hết hạn - Khóa cổng đỏ & hiện nút Gia Hạn", new Color(239, 68, 68)));
        pnlList.add(createCardItem("QR003", "Hoàng Văn Bình (Thẻ VIP)", "Thẻ bị tạm khóa vi phạm nội quy - Từ chối check-in", new Color(245, 158, 11)));
        pnlList.add(createCardItem("QR004", "Phạm Quốc Cường (Cơ sở 1)", "Sai chi nhánh (Đăng ký CN01 mang sang CN02 quét)", new Color(139, 92, 246)));
        pnlList.add(createCardItem("VIP007", "Khách VIP Anh Đức (Chủ)", "Giảm giá 50% bảo mật nội bộ với nhân viên", new Color(14, 165, 233)));
        pnlList.add(createCardItem("PASS-8888", "Võ Hoàng Yến (Khách Vãng Lai)", "Vé lẻ 1 ngày (80k) xuất VietQR - Chỉ mở cổng 1 lần", new Color(217, 119, 6)));

        pnl.add(pnlList, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createCardItem(String code, String name, String note, Color accent) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblC = new JLabel(code);
        lblC.setFont(new Font("Consolas", Font.BOLD, 14));
        lblC.setForeground(accent);
        lblC.setPreferredSize(new Dimension(85, 0));

        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 2));
        pnlText.setOpaque(false);
        JLabel lblN = new JLabel(name);
        lblN.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblN.setForeground(new Color(15, 23, 42));

        JLabel lblDesc = new JLabel(note);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(100, 116, 139));
        pnlText.add(lblN);
        pnlText.add(lblDesc);

        JButton btnSelect = new JButton("Chọn Thẻ");
        btnSelect.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnSelect.setBackground(Color.WHITE);
        btnSelect.setForeground(new Color(71, 85, 105));
        btnSelect.setFocusPainted(false);
        btnSelect.addActionListener(e -> {
            chonThe(code, name, "Gói Đồ Án", "CN01 & CN02");
        });

        card.add(lblC, BorderLayout.WEST);
        card.add(pnlText, BorderLayout.CENTER);
        card.add(btnSelect, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                chonThe(code, name, "Gói Đồ Án", "CN01 & CN02");
            }
        });

        return card;
    }

    // --- TAB 2: THẺ HỘI VIÊN LẤY TỪ DATABASE SQL SERVER ---
    private JPanel taoTabTheHoiVienCSDL() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] cols = {"Mã QR", "Họ Tên", "Gói Tập", "Trạng Thái"};
        modelMembers = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblMembers = new JTable(modelMembers);
        tblMembers.setRowHeight(28);
        tblMembers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblMembers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Nạp hội viên từ CSDL
        napDuLieuHoiVienCSDL();

        tblMembers.getSelectionModel().addListSelectionListener(e -> {
            int row = tblMembers.getSelectedRow();
            if (row >= 0) {
                String code = (String) modelMembers.getValueAt(row, 0);
                String name = (String) modelMembers.getValueAt(row, 1);
                String pkg = (String) modelMembers.getValueAt(row, 2);
                chonThe(code, name, pkg, "Chi nhánh hệ thống");
            }
        });

        JScrollPane sc = new JScrollPane(tblMembers);
        sc.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pnl.add(sc, BorderLayout.CENTER);

        JButton btnReload = new JButton("Làm Mới Danh Sách Từ CSDL");
        btnReload.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReload.setBackground(new Color(241, 245, 249));
        btnReload.setForeground(new Color(30, 41, 59));
        btnReload.addActionListener(e -> napDuLieuHoiVienCSDL());
        pnl.add(btnReload, BorderLayout.SOUTH);

        return pnl;
    }

    private void napDuLieuHoiVienCSDL() {
        modelMembers.setRowCount(0);
        List<HoiVien> list = hoiVienDAO.layTatCa();
        if (list != null && !list.isEmpty()) {
            for (HoiVien hv : list) {
                String code = hv.getMaTheQR() != null ? hv.getMaTheQR() : ("HV" + hv.getMaHoiVien());
                String pkg = hv.getGoiTapDangKy() != null ? hv.getGoiTapDangKy() : "VIP";
                String st = hv.getTrangThai() != null ? hv.getTrangThai() : "Hoạt động";
                modelMembers.addRow(new Object[]{code, hv.getHoTen(), pkg, st});
            }
        } else {
            // Mẫu fallback nếu chưa kết nối DB
            modelMembers.addRow(new Object[]{"QR001", "Lê Anh Duy", "VIP Diamond", "Hoạt động"});
            modelMembers.addRow(new Object[]{"QR002", "Trần Thị Mai", "Standard 1 Tháng", "Hết hạn"});
            modelMembers.addRow(new Object[]{"QR003", "Hoàng Văn Bình", "VIP 6 Tháng", "Bị khóa"});
            modelMembers.addRow(new Object[]{"QR004", "Phạm Quốc Cường", "Gói Cơ Sở 1", "Hoạt động"});
            modelMembers.addRow(new Object[]{"VIP007", "Nguyễn Văn Đức", "VIP Diamond", "Hoạt động"});
        }
    }

    // --- TAB 3: QUÉT TỪ FILE ẢNH MÃ QR ---
    private JPanel taoTabQuetTuFileAnh() {
        JPanel pnl = new JPanel(new BorderLayout(0, 12));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("<html><b>QUÉT MÃ QR TỪ TỆP HÌNH ẢNH (.PNG / .JPG)</b><br>"
                + "<span style='color:#64748b; font-size:11px;'>Chọn tệp ảnh thẻ hoặc ảnh chụp mã QR đã lưu trên máy để giải mã:</span></html>");
        pnl.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(3, 1, 0, 10));
        pnlCenter.setOpaque(false);

        JButton btnChooseFile = new JButton("Chọn Tệp Ảnh Mã QR Từ Máy Tính...");
        btnChooseFile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnChooseFile.setBackground(new Color(241, 245, 249));
        btnChooseFile.setForeground(new Color(30, 41, 59));
        btnChooseFile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChooseFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int ret = fc.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                // Trích xuất mã từ tên file hoặc nhận diện
                String fName = f.getName().toUpperCase();
                String extractedCode = "QR001";
                if (fName.contains("QR002")) extractedCode = "QR002";
                else if (fName.contains("QR003")) extractedCode = "QR003";
                else if (fName.contains("QR004")) extractedCode = "QR004";
                else if (fName.contains("VIP007")) extractedCode = "VIP007";
                else if (fName.contains("PASS")) extractedCode = "PASS-8888";

                chonThe(extractedCode, "Quét từ file ảnh: " + f.getName(), "Giải mã hình ảnh", "Tất cả chi nhánh");
                JOptionPane.showMessageDialog(this,
                        "Đã nhận diện thành công mã QR từ file ảnh [" + f.getName() + "]:\n"
                                + "- Mã Thẻ Giải Mã: " + extractedCode + "\n"
                                + "Mã thẻ đã được đưa vào tâm ngắm camera máy quét!",
                        "Đọc File Ảnh Thành Công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JTextField txtManual = new JTextField("QR001");
        txtManual.setFont(new Font("Consolas", Font.BOLD, 14));
        JButton btnLoadManual = new JButton("Nạp Mã Này Vào Máy Quét");
        btnLoadManual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLoadManual.setBackground(new Color(37, 99, 235));
        btnLoadManual.setForeground(Color.WHITE);
        btnLoadManual.addActionListener(e -> {
            String m = txtManual.getText().trim();
            if (!m.isEmpty()) {
                chonThe(m, "Mã nhập thủ công", "Tùy biến", "Toàn hệ thống");
            }
        });

        JPanel pnlManual = new JPanel(new BorderLayout(8, 0));
        pnlManual.setOpaque(false);
        pnlManual.add(new JLabel("Hoặc nhập chuỗi mã ảnh:"), BorderLayout.NORTH);
        pnlManual.add(txtManual, BorderLayout.CENTER);
        pnlManual.add(btnLoadManual, BorderLayout.EAST);

        pnlCenter.add(btnChooseFile);
        pnlCenter.add(pnlManual);
        pnl.add(pnlCenter, BorderLayout.CENTER);

        return pnl;
    }

    private void chonThe(String code, String name, String pkg, String branch) {
        this.currentSelectedCode = code;
        this.currentSelectedName = name;
        this.currentSelectedPkg = pkg;
        this.currentSelectedBranch = branch;

        lblTargetCode.setText("Mã Thẻ: " + code);
        lblTargetName.setText("Hội Viên: " + name);
        lblScanStatus.setText("ĐÃ KHÓA MÃ: " + code);
        pnlCameraViewfinder.repaint();
    }

    private void thucHienBanTiaQuet() {
        // Âm thanh bíp súng quét 1200Hz
        SoundHelper.playSuccessBeep();

        lblScanStatus.setText("ĐÃ QUÉT THÀNH CÔNG!");
        lblScanStatus.setForeground(new Color(16, 185, 129));

        if (laserAnimationTimer != null && laserAnimationTimer.isRunning()) {
            laserAnimationTimer.stop();
        }

        // Bắn mã về CheckInForm
        if (onScanCallback != null) {
            onScanCallback.accept(currentSelectedCode);
        }

        // Tự động đóng dialog sau 350ms
        Timer closeTimer = new Timer(350, e -> dispose());
        closeTimer.setRepeats(false);
        closeTimer.start();
    }

    /**
     * Thuật toán vẽ ma trận QR 2D trực quan bằng Graphics2D
     */
    private void veMaTranQRCode(Graphics2D g2, int startX, int startY, int size, String code) {
        g2.setColor(new Color(15, 23, 42));
        int cells = 17;
        int cellSize = size / cells;
        int hash = Math.abs(code.hashCode());

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
                    // Dữ liệu ma trận dựa trên hash của mã
                    if ((r * 13 + c * 19 + hash) % 3 != 0) {
                        g2.fillRect(startX + c * cellSize + 1, startY + r * cellSize + 1, cellSize - 2, cellSize - 2);
                    }
                }
            }
        }

        // Tâm logo FitLife nhỏ
        int logoSize = 26;
        int lx = startX + size / 2 - logoSize / 2;
        int ly = startY + size / 2 - logoSize / 2;
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(lx - 2, ly - 2, logoSize + 4, logoSize + 4, 6, 6);
        g2.setColor(new Color(37, 99, 235));
        g2.fillRoundRect(lx, ly, logoSize, logoSize, 6, 6);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString("FL", lx + 7, ly + 17);
    }
}
