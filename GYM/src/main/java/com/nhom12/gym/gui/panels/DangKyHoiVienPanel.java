package com.nhom12.gym.gui.panels;

import com.nhom12.gym.gui.GuiMaQRDialog;

import com.nhom12.gym.dao.GoiTapDAO;
import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.GoiTap;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.TaiKhoan;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;

public class DangKyHoiVienPanel extends JPanel {
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final GoiTapDAO goiTapDAO = new GoiTapDAO();
    private final com.nhom12.gym.dao.TaiKhoanDAO taiKhoanDAO = new com.nhom12.gym.dao.TaiKhoanDAO();
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    private List<GoiTap> danhSachGoiTap = new ArrayList<>();
    private int currentMonths = 1;

    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtBirth;
    private JComboBox<String> cbGender;
    private JComboBox<String> cbBranch;

    private JComboBox<String> cbPackage;
    private JTextField txtStartDate;
    private JComboBox<String> cbTrainer;
    private JComboBox<String> cbPayment;
    private JTextField txtVoucher;
    private JButton btnApplyVoucher;
    private JCheckBox chkCreateAppAccount;

    private JLabel lblTotalCost;
    private long currentPrice = 500000;
    private int discountPercent = 0;

    // Card Preview Components
    private JLabel lblCardName;
    private JLabel lblCardCode;
    private JLabel lblCardPhone;
    private JLabel lblCardPackage;
    private JLabel lblCardExpire;
    private JLabel lblCardBranch;

    public DangKyHoiVienPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("TIẾP NHẬN & ĐĂNG KÝ HỘI VIÊN MỚI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Tạo hồ sơ hội viên mới, phân bổ gói tập, kích hoạt mã thẻ QR và cấp thẻ thành viên");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Registration Form
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFormTitle = new JLabel("Thông Tin Đăng Ký");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(30, 41, 59));
        formContainer.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        int r = 0;
        // Full Name
        gbc.gridx = 0; gbc.gridy = r++;
        JLabel l1 = new JLabel("Họ Và Tên (*):"); l1.setFont(fontLabel);
        formGrid.add(l1, gbc);
        gbc.gridy = r++;
        txtFullName = new JTextField(); txtFullName.setFont(fontInput);
        formGrid.add(txtFullName, gbc);

        // Phone & Gender (2 columns in one row)
        JPanel rowPhoneGender = new JPanel(new GridLayout(1, 2, 10, 0));
        rowPhoneGender.setOpaque(false);
        JPanel pPhone = new JPanel(new BorderLayout(0, 3)); pPhone.setOpaque(false);
        JLabel lPhone = new JLabel("Số Điện Thoại (*):"); lPhone.setFont(fontLabel);
        txtPhone = new JTextField(); txtPhone.setFont(fontInput);
        pPhone.add(lPhone, BorderLayout.NORTH); pPhone.add(txtPhone, BorderLayout.CENTER);

        JPanel pGender = new JPanel(new BorderLayout(0, 3)); pGender.setOpaque(false);
        JLabel lGender = new JLabel("Giới Tính:"); lGender.setFont(fontLabel);
        cbGender = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"}); cbGender.setFont(fontInput);
        pGender.add(lGender, BorderLayout.NORTH); pGender.add(cbGender, BorderLayout.CENTER);

        rowPhoneGender.add(pPhone); rowPhoneGender.add(pGender);
        gbc.gridy = r++;
        formGrid.add(rowPhoneGender, gbc);

        // Branch & Birthday
        JPanel rowBranchBirth = new JPanel(new GridLayout(1, 2, 10, 0));
        rowBranchBirth.setOpaque(false);
        JPanel pBranch = new JPanel(new BorderLayout(0, 3)); pBranch.setOpaque(false);
        JLabel lBranch = new JLabel("Chi Nhánh:"); lBranch.setFont(fontLabel);
        cbBranch = new JComboBox<>(new String[]{"CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)"}); cbBranch.setFont(fontInput);
        pBranch.add(lBranch, BorderLayout.NORTH); pBranch.add(cbBranch, BorderLayout.CENTER);

        JPanel pBirth = new JPanel(new BorderLayout(0, 3)); pBirth.setOpaque(false);
        JLabel lBirth = new JLabel("Ngày Sinh (dd/MM/yyyy):"); lBirth.setFont(fontLabel);
        txtBirth = new JTextField("15/05/2000"); txtBirth.setFont(fontInput);
        pBirth.add(lBirth, BorderLayout.NORTH); pBirth.add(txtBirth, BorderLayout.CENTER);

        rowBranchBirth.add(pBranch); rowBranchBirth.add(pBirth);
        gbc.gridy = r++;
        formGrid.add(rowBranchBirth, gbc);

        // Package Choice
        gbc.gridy = r++;
        JLabel lPkg = new JLabel("Chọn Gói Tập (*):"); lPkg.setFont(fontLabel);
        formGrid.add(lPkg, gbc);
        gbc.gridy = r++;
        cbPackage = new JComboBox<>();
        cbPackage.setFont(fontInput);
        formGrid.add(cbPackage, gbc);

        // Start Date & Trainer
        JPanel rowDatePT = new JPanel(new GridLayout(1, 2, 10, 0));
        rowDatePT.setOpaque(false);
        JPanel pDate = new JPanel(new BorderLayout(0, 3)); pDate.setOpaque(false);
        JLabel lDate = new JLabel("Ngày Bắt Đầu:"); lDate.setFont(fontLabel);
        txtStartDate = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date())); txtStartDate.setFont(fontInput);
        pDate.add(lDate, BorderLayout.NORTH); pDate.add(txtStartDate, BorderLayout.CENTER);

        JPanel pPT = new JPanel(new BorderLayout(0, 3)); pPT.setOpaque(false);
        JLabel lPT = new JLabel("HLV Kèm (Nếu có):"); lPT.setFont(fontLabel);
        cbTrainer = new JComboBox<>(new String[]{"Không kèm HLV", "HLV John Smith", "HLV Phạm Quốc Bảo", "HLV Anh Tuấn"}); cbTrainer.setFont(fontInput);
        pPT.add(lPT, BorderLayout.NORTH); pPT.add(cbTrainer, BorderLayout.CENTER);

        rowDatePT.add(pDate); rowDatePT.add(pPT);
        gbc.gridy = r++;
        formGrid.add(rowDatePT, gbc);

        // Payment & Voucher
        JPanel rowPayVoucher = new JPanel(new BorderLayout(10, 0));
        rowPayVoucher.setOpaque(false);
        JPanel pPay = new JPanel(new BorderLayout(0, 3)); pPay.setOpaque(false);
        JLabel lPay = new JLabel("Hình Thức Thanh Toán:"); lPay.setFont(fontLabel);
        cbPayment = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản (VietQR)", "Quẹt thẻ POS"}); cbPayment.setFont(fontInput);
        pPay.add(lPay, BorderLayout.NORTH); pPay.add(cbPayment, BorderLayout.CENTER);

        JPanel pVoucher = new JPanel(new BorderLayout(0, 3)); pVoucher.setOpaque(false);
        JLabel lVoucher = new JLabel("Mã Giảm Giá (Voucher):"); lVoucher.setFont(fontLabel);
        JPanel vSub = new JPanel(new BorderLayout(5, 0)); vSub.setOpaque(false);
        txtVoucher = new JTextField("SUMMER26"); txtVoucher.setFont(fontInput);
        btnApplyVoucher = new JButton("Áp Dụng");
        btnApplyVoucher.setBackground(new Color(245, 158, 11));
        btnApplyVoucher.setForeground(Color.WHITE);
        vSub.add(txtVoucher, BorderLayout.CENTER); vSub.add(btnApplyVoucher, BorderLayout.EAST);
        pVoucher.add(lVoucher, BorderLayout.NORTH); pVoucher.add(vSub, BorderLayout.CENTER);

        rowPayVoucher.add(pPay, BorderLayout.WEST);
        rowPayVoucher.add(pVoucher, BorderLayout.CENTER);
        gbc.gridy = r++;
        formGrid.add(rowPayVoucher, gbc);

        // Total Cost Bar
        JPanel totalBar = new JPanel(new BorderLayout());
        totalBar.setBackground(new Color(241, 245, 249));
        totalBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        JLabel lblTotalTitle = new JLabel("Tổng Tiền Cần Thanh Toán:");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalCost = new JLabel(df.format(currentPrice));
        lblTotalCost.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalCost.setForeground(new Color(16, 185, 129));
        totalBar.add(lblTotalTitle, BorderLayout.WEST);
        totalBar.add(lblTotalCost, BorderLayout.EAST);

        gbc.gridy = r++;
        gbc.insets = new Insets(10, 5, 5, 5);
        formGrid.add(totalBar, gbc);

        // Checkbox cấp tài khoản App
        chkCreateAppAccount = new JCheckBox("Tự động kích hoạt & cấp tài khoản FitLife App (User: SĐT, Mật khẩu: 123456)", true);
        chkCreateAppAccount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkCreateAppAccount.setForeground(new Color(37, 99, 235));
        chkCreateAppAccount.setOpaque(false);
        gbc.gridy = r++;
        gbc.insets = new Insets(4, 5, 10, 5);
        formGrid.add(chkCreateAppAccount, gbc);

        // Buttons
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnReset = new JButton("Làm Mới Form");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReset.setBackground(new Color(100, 116, 139));
        btnReset.setForeground(Color.WHITE);

        JButton btnSubmit = new JButton("Xác Nhận Đăng Ký");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(16, 185, 129));
        btnSubmit.setForeground(Color.WHITE);

        actionButtons.add(btnReset);
        actionButtons.add(btnSubmit);
        gbc.gridy = r++;
        formGrid.add(actionButtons, gbc);

        gbc.gridy = r++;
        gbc.weighty = 1.0;
        formGrid.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        formContainer.add(scrollForm, BorderLayout.CENTER);
        splitPane.setLeftComponent(formContainer);

        // Right Preview: Member Card Simulation
        JPanel rightContainer = new JPanel(new BorderLayout(15, 15));
        rightContainer.setBackground(Color.WHITE);
        rightContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblPreviewTitle = new JLabel("Xem Trước Thẻ Hội Viên");
        lblPreviewTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPreviewTitle.setForeground(new Color(30, 41, 59));
        rightContainer.add(lblPreviewTitle, BorderLayout.NORTH);

        // Visual Card Panel
        JPanel cardPreview = new JPanel(new BorderLayout());
        cardPreview.setBackground(new Color(30, 41, 59));
        cardPreview.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(15, 23, 42), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        cardPreview.setPreferredSize(new Dimension(380, 230));

        // Card Top: Brand & Code
        JPanel cardTop = new JPanel(new BorderLayout());
        cardTop.setOpaque(false);
        JLabel lblBrand = new JLabel("FITLIFE GYM & FITNESS");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBrand.setForeground(new Color(248, 250, 252));

        lblCardCode = new JLabel("HV046");
        lblCardCode.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCardCode.setForeground(new Color(16, 185, 129));
        cardTop.add(lblBrand, BorderLayout.WEST);
        cardTop.add(lblCardCode, BorderLayout.EAST);
        cardPreview.add(cardTop, BorderLayout.NORTH);

        // Card Center: Member Info & Mini QR Code badge
        JPanel cardCenter = new JPanel(new BorderLayout(10, 0));
        cardCenter.setOpaque(false);
        cardCenter.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel cardInfoText = new JPanel(new GridLayout(4, 1, 0, 4));
        cardInfoText.setOpaque(false);
        lblCardName = new JLabel("NGUYỄN VĂN A");
        lblCardName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCardName.setForeground(Color.WHITE);

        lblCardPhone = new JLabel("SĐT: 0901 234 567");
        lblCardPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCardPhone.setForeground(new Color(203, 213, 225));

        lblCardPackage = new JLabel("Gói: Sinh Viên 3 Tháng");
        lblCardPackage.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCardPackage.setForeground(new Color(251, 191, 36));

        lblCardExpire = new JLabel("Hạn sử dụng: 30/12/2026");
        lblCardExpire.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCardExpire.setForeground(new Color(148, 163, 184));

        cardInfoText.add(lblCardName);
        cardInfoText.add(lblCardPhone);
        cardInfoText.add(lblCardPackage);
        cardInfoText.add(lblCardExpire);
        cardCenter.add(cardInfoText, BorderLayout.CENTER);

        // Mini QR Graphic badge on card (Clickable)
        JPanel miniQR = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int sz = 74;
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, sz, sz, 8, 8);
                g2.setColor(new Color(15, 23, 42));
                int cells = 11;
                int cs = sz / cells;
                for (int r = 0; r < cells; r++) {
                    for (int c = 0; c < cells; c++) {
                        if ((r < 3 && c < 3) || (r < 3 && c >= cells - 3) || (r >= cells - 3 && c < 3) || (r * 7 + c * 13) % 3 != 0) {
                            g2.fillRect(c * cs + 1, r * cs + 1, cs - 1, cs - 1);
                        }
                    }
                }
            }
        };
        miniQR.setPreferredSize(new Dimension(75, 75));
        miniQR.setOpaque(false);
        miniQR.setCursor(new Cursor(Cursor.HAND_CURSOR));
        miniQR.setToolTipText("Bấm để phóng to và gửi mã QR qua Zalo / SMS");
        miniQR.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                moDialogGuiQR();
            }
        });
        cardCenter.add(miniQR, BorderLayout.EAST);
        cardPreview.add(cardCenter, BorderLayout.CENTER);

        // Card Bottom: Branch & Status badge
        JPanel cardBottom = new JPanel(new BorderLayout());
        cardBottom.setOpaque(false);
        lblCardBranch = new JLabel("Cơ sở 1 - Gò Vấp");
        lblCardBranch.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCardBranch.setForeground(new Color(148, 163, 184));

        JLabel lblBadge = new JLabel(" ● HOẠT ĐỘNG ");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(16, 185, 129));
        lblBadge.setForeground(Color.WHITE);
        cardBottom.add(lblCardBranch, BorderLayout.WEST);
        cardBottom.add(lblBadge, BorderLayout.EAST);
        cardPreview.add(cardBottom, BorderLayout.SOUTH);

        // Instructions and Print/Send buttons
        JPanel previewActions = new JPanel(new BorderLayout(0, 15));
        previewActions.setOpaque(false);
        previewActions.add(cardPreview, BorderLayout.NORTH);

        JLabel lblNote = new JLabel("<html><b>Mã thẻ QR</b> hiển thị trực quan ngay trên thẻ. Bạn có thể bấm gửi nhanh qua Zalo / SMS cho hội viên hoặc in thẻ cứng tại quầy.</html>");
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNote.setForeground(new Color(100, 116, 139));
        previewActions.add(lblNote, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlBtns.setOpaque(false);

        JButton btnSendQR = new JButton("Xuất & Gửi Mã QR");
        btnSendQR.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSendQR.setBackground(new Color(14, 116, 144));
        btnSendQR.setForeground(Color.WHITE);
        btnSendQR.setFocusPainted(false);
        btnSendQR.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSendQR.setPreferredSize(new Dimension(0, 42));
        btnSendQR.addActionListener(e -> moDialogGuiQR());

        JButton btnPrintCard = new JButton("In Thẻ Cứng");
        btnPrintCard.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrintCard.setBackground(new Color(37, 99, 235));
        btnPrintCard.setForeground(Color.WHITE);
        btnPrintCard.setFocusPainted(false);
        btnPrintCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrintCard.setPreferredSize(new Dimension(0, 42));
        btnPrintCard.addActionListener(e -> printCard());

        pnlBtns.add(btnSendQR);
        pnlBtns.add(btnPrintCard);
        previewActions.add(pnlBtns, BorderLayout.SOUTH);

        rightContainer.add(previewActions, BorderLayout.CENTER);
        splitPane.setRightComponent(rightContainer);
        this.add(splitPane, BorderLayout.CENTER);

        // Live calculation & card update listeners
        loadPackageList();
        cbPackage.addActionListener(e -> updateCalculations());
        btnApplyVoucher.addActionListener(e -> applyVoucher());
        btnReset.addActionListener(e -> resetForm());
        btnSubmit.addActionListener(e -> submitRegistration());
        btnPrintCard.addActionListener(e -> printCard());

        GoiTapDAO.addChangeListener(this::loadPackageList);
    }

    public void loadPackageList() {
        danhSachGoiTap = goiTapDAO.layTatCa();
        Object currentSelected = cbPackage.getSelectedItem();
        cbPackage.removeAllItems();
        if (danhSachGoiTap != null && !danhSachGoiTap.isEmpty()) {
            for (GoiTap gt : danhSachGoiTap) {
                cbPackage.addItem(gt.getTenGoiTap() + " (" + gt.getThoiHanThang() + " Tháng) - " + df.format(gt.getGiaTien()));
            }
            if (currentSelected != null) {
                cbPackage.setSelectedItem(currentSelected);
            }
            if (cbPackage.getSelectedIndex() < 0) {
                cbPackage.setSelectedIndex(0);
            }
        } else {
            cbPackage.addItem("Gói 1 Tháng Tiêu Chuẩn (1 Tháng) - 500,000 đ");
        }
        updateCalculations();
    }

    private void updateCalculations() {
        int idx = cbPackage.getSelectedIndex();
        String pkgTitle = "Gói Tiêu Chuẩn";
        if (danhSachGoiTap != null && idx >= 0 && idx < danhSachGoiTap.size()) {
            GoiTap selected = danhSachGoiTap.get(idx);
            currentPrice = (long) selected.getGiaTien();
            currentMonths = selected.getThoiHanThang();
            pkgTitle = selected.getTenGoiTap();
        } else {
            currentPrice = 500000;
            currentMonths = 1;
        }

        long finalPrice = currentPrice - (currentPrice * discountPercent / 100);
        lblTotalCost.setText(df.format(finalPrice));

        // Update card preview
        lblCardPackage.setText("Gói: " + pkgTitle);
        Calendar cal = Calendar.getInstance();
        try {
            if (txtStartDate != null && txtStartDate.getText() != null && !txtStartDate.getText().trim().isEmpty()) {
                cal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(txtStartDate.getText().trim()));
            }
        } catch (Exception ex) {
            cal.setTime(new Date());
        }
        cal.add(Calendar.MONTH, currentMonths);
        lblCardExpire.setText("Hạn sử dụng: " + new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()));
        if (cbBranch != null && cbBranch.getSelectedItem() != null) {
            lblCardBranch.setText(cbBranch.getSelectedItem().toString());
        }
    }

    private void applyVoucher() {
        String code = txtVoucher.getText().trim();
        if ("SUMMER26".equalsIgnoreCase(code)) {
            discountPercent = 20;
            JOptionPane.showMessageDialog(this, "Áp dụng thành công Voucher SUMMER26: Giảm 20%!", "Khuyến Mãi", JOptionPane.INFORMATION_MESSAGE);
        } else if ("NEWBIE50".equalsIgnoreCase(code)) {
            discountPercent = 50;
            JOptionPane.showMessageDialog(this, "Áp dụng thành công Voucher NEWBIE50: Giảm 50%!", "Khuyến Mãi", JOptionPane.INFORMATION_MESSAGE);
        } else if ("VIPMEMBER".equalsIgnoreCase(code)) {
            discountPercent = 30;
            JOptionPane.showMessageDialog(this, "Áp dụng thành công Voucher VIPMEMBER: Giảm 30%!", "Khuyến Mãi", JOptionPane.INFORMATION_MESSAGE);
        } else if (code.isEmpty()) {
            discountPercent = 0;
            JOptionPane.showMessageDialog(this, "Đã hủy áp dụng mã giảm giá.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            discountPercent = 0;
            JOptionPane.showMessageDialog(this, "Mã voucher không hợp lệ hoặc đã hết hạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        updateCalculations();
    }

    private void submitRegistration() {
        String name = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ Tên và Số Điện Thoại!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tự động tính số thứ tự ID kế tiếp và sinh mã định danh đồng bộ (ví dụ: HV007, HV013...)
        List<HoiVien> allMembers = hoiVienDAO.layTatCa();
        int maxId = 0;
        if (allMembers != null) {
            for (HoiVien m : allMembers) {
                if (m.getMaHoiVien() > maxId) maxId = m.getMaHoiVien();
            }
        }
        int nextId = maxId + 1;
        String code = String.format("HV%03d", nextId);
        int counter = nextId;
        while (hoiVienDAO.timTheoMaQR(code) != null) {
            counter++;
            code = String.format("HV%03d", counter);
        }

        lblCardName.setText(name.toUpperCase());
        lblCardCode.setText(code);
        lblCardPhone.setText("SĐT: " + phone);

        double finalPrice = currentPrice - (currentPrice * discountPercent / 100);
        String pkgName = cbPackage.getSelectedItem().toString();
        java.util.List<com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang> items = new java.util.ArrayList<>();
        items.add(new com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang("Đăng ký mới " + pkgName, 1, currentPrice, finalPrice));

        final String finalCode = code;
        final int finalNextId = nextId;

        Window topFrame = SwingUtilities.getWindowAncestor(this);
        com.nhom12.gym.gui.ThanhToanHoaDonDialog payDialog = new com.nhom12.gym.gui.ThanhToanHoaDonDialog(
                topFrame,
                null,
                name + " (" + code + ")",
                "Đăng ký " + pkgName,
                currentPrice,
                currentPrice * discountPercent / 100,
                finalPrice,
                items,
                success -> {
                    if (success) {
                        try {
                            HoiVien hv = new HoiVien();
                            hv.setMaHoiVien(finalNextId);
                            hv.setHoTen(name);
                            hv.setMaTheQR(finalCode);
                            hv.setSoDienThoai(phone);
                            hv.setChiNhanhDangKy(cbBranch.getSelectedItem().toString());
                            hv.setGoiTapDangKy(pkgName);
                            hv.setNgayDangKy(new Date()); // Lưu ngày giờ đăng ký chính xác
                            hv.setSoTienDaThanhToan(finalPrice); // Lưu số tiền đã thanh toán
                            hv.setHinhThucThanhToan(cbPayment.getSelectedItem().toString());
                            hv.setTrangThai("Hoạt động");
                            Calendar cal = Calendar.getInstance();
                            try {
                                if (txtStartDate != null && !txtStartDate.getText().trim().isEmpty()) {
                                    cal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(txtStartDate.getText().trim()));
                                }
                            } catch (Exception ex) {
                                cal.setTime(new Date());
                            }
                            cal.add(Calendar.MONTH, currentMonths);
                            hv.setNgayHetHan(cal.getTime());
                            hoiVienDAO.them(hv);
                        } catch (Exception ignored) {
                        }

                        if (chkCreateAppAccount != null && chkCreateAppAccount.isSelected()) {
                            try {
                                taiKhoanDAO.them(new TaiKhoan(phone, "123456", "Hội viên", name));
                            } catch (Exception ignored) {
                            }
                        }

                        Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
                        new GuiMaQRDialog(f, finalCode, name, phone, cbPackage.getSelectedItem().toString(), cbBranch.getSelectedItem().toString(), phone, "123456").setVisible(true);
                    }
                }
        );
        payDialog.setVisible(true);
    }

    private void moDialogGuiQR() {
        String name = txtFullName.getText().trim();
        if (name.isEmpty()) name = lblCardName.getText();
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty()) phone = "0901234567";
        String code = lblCardCode.getText();
        String pkg = cbPackage.getSelectedItem().toString();
        String br = cbBranch.getSelectedItem().toString();

        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        new GuiMaQRDialog(topFrame, code, name, phone, pkg, br, phone, "123456").setVisible(true);
    }

    private void printCard() {
        String name = txtFullName.getText().trim();
        if (name.isEmpty()) name = "Nguyễn Văn Tuấn";
        JOptionPane.showMessageDialog(this, "Đang kết nối máy in thẻ nhựa chuyên dụng...\nĐã gửi lệnh in thẻ cho hội viên: " + name + " (" + lblCardCode.getText() + ")", "In Thẻ Hội Viên", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetForm() {
        txtFullName.setText("");
        txtPhone.setText("");
        txtBirth.setText("15/05/2000");
        cbGender.setSelectedIndex(0);
        cbBranch.setSelectedIndex(0);
        cbPackage.setSelectedIndex(1);
        txtStartDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        cbTrainer.setSelectedIndex(0);
        cbPayment.setSelectedIndex(0);
        txtVoucher.setText("");
        discountPercent = 0;
        updateCalculations();
        lblCardName.setText("NGUYỄN VĂN A");
        lblCardPhone.setText("SĐT: 0901 234 567");
    }
}
