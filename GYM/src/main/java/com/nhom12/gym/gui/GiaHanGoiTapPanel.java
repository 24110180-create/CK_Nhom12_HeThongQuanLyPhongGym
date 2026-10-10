package com.nhom12.gym.gui;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.HoiVien;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GiaHanGoiTapPanel extends JPanel {
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    private JTextField txtSearch;
    private JButton btnSearch;
    private JLabel lblTen;
    private JLabel lblCurrentStatus;
    private JComboBox<String> cbGoi;
    private JTextField txtNgay;
    private JComboBox<String> cbThanhToan;
    private JTextField txtVoucher;
    private JButton btnApplyVoucher;
    private JLabel lblThanhTien;

    private JButton btnCancel;
    private JButton btnConfirm;

    private String currentMemberName = "Lê Thị Lan";
    private String currentMemberCode = "HV022";
    private long basePrice = 2500000;
    private int discountPercent = 0;

    public GiaHanGoiTapPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("GIA HẠN & NÂNG CẤP GÓI TẬP HỘI VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Tra cứu hội viên sắp hết hạn, chuyển đổi gói tập và xuất hóa đơn gia hạn");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblPrompt = new JLabel("Mã Hội Viên / SĐT / Tên:");
        lblPrompt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setText("HV022");

        btnSearch = new JButton("Tra Cứu Hội Viên");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(new Color(37, 99, 235));
        btnSearch.setForeground(Color.WHITE);

        searchPanel.add(lblPrompt);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        int r = 0;
        // Member Info Row
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel l1 = new JLabel("Hội Viên:"); l1.setFont(fontLabel);
        formPanel.add(l1, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        lblTen = new JLabel("Lê Thị Lan (Mã: HV022 - SĐT: 0987 654 321)");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTen.setForeground(new Color(30, 41, 59));
        formPanel.add(lblTen, gbc);

        // Current status row
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lStatus = new JLabel("Gói Hiện Tại:"); lStatus.setFont(fontLabel);
        formPanel.add(lStatus, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        lblCurrentStatus = new JLabel("Gói 1 Tháng Thử Nghiệm - Hết hạn vào ngày mai (01/10/2026)");
        lblCurrentStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCurrentStatus.setForeground(new Color(239, 68, 68));
        formPanel.add(lblCurrentStatus, gbc);

        // New Package Selection
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel l2 = new JLabel("Chọn Gói Gia Hạn:"); l2.setFont(fontLabel);
        formPanel.add(l2, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        cbGoi = new JComboBox<>(new String[]{
                "Gói Sinh Viên (3 Tháng) - 1.100.000 đ",
                "VIP Thể Lực (6 Tháng) - 2.500.000 đ",
                "Gói 1 Năm Toàn Diện (Tặng 1 tháng) - 4.000.000 đ",
                "Khóa Kèm Riêng PT 1-1 (20 Buổi) - 6.000.000 đ"
        });
        cbGoi.setSelectedIndex(1);
        cbGoi.setFont(fontInput);
        formPanel.add(cbGoi, gbc);

        // Start Date
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel l3 = new JLabel("Ngày Bắt Đầu Gia Hạn:"); l3.setFont(fontLabel);
        formPanel.add(l3, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        txtNgay = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        txtNgay.setFont(fontInput);
        formPanel.add(txtNgay, gbc);

        // Payment Method
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel l4 = new JLabel("Phương Thức Thanh Toán:"); l4.setFont(fontLabel);
        formPanel.add(l4, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        cbThanhToan = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản (VietQR)", "Quẹt thẻ POS"});
        cbThanhToan.setFont(fontInput);
        formPanel.add(cbThanhToan, gbc);

        // Voucher Coupon
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lVoucher = new JLabel("Mã Ưu Đãi / Voucher:"); lVoucher.setFont(fontLabel);
        formPanel.add(lVoucher, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        JPanel vSub = new JPanel(new BorderLayout(8, 0));
        vSub.setOpaque(false);
        txtVoucher = new JTextField("SUMMER26");
        txtVoucher.setFont(fontInput);
        btnApplyVoucher = new JButton("Áp Dụng Mã");
        btnApplyVoucher.setBackground(new Color(245, 158, 11));
        btnApplyVoucher.setForeground(Color.WHITE);
        vSub.add(txtVoucher, BorderLayout.CENTER);
        vSub.add(btnApplyVoucher, BorderLayout.EAST);
        formPanel.add(vSub, gbc);

        // Total Cost Display
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lTotal = new JLabel("Thành Tiền Sau Giảm:"); lTotal.setFont(fontLabel);
        formPanel.add(lTotal, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        lblThanhTien = new JLabel(df.format(basePrice));
        lblThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblThanhTien.setForeground(new Color(16, 185, 129));
        formPanel.add(lblThanhTien, gbc);

        // Action Buttons
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btnGroup.setOpaque(false);

        btnCancel = new JButton("Làm Mới");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBackground(new Color(100, 116, 139));
        btnCancel.setForeground(Color.WHITE);

        btnConfirm = new JButton("Chốt Gia Hạn & In Hóa Đơn");
        btnConfirm.setBackground(new Color(16, 185, 129));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnGroup.add(btnCancel);
        btnGroup.add(btnConfirm);
        formPanel.add(btnGroup, gbc);

        r++; gbc.gridy = r; gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);

        centerPanel.add(formPanel, BorderLayout.CENTER);
        this.add(centerPanel, BorderLayout.CENTER);

        // Action Listeners
        btnSearch.addActionListener(e -> actionSearch());
        txtSearch.addActionListener(e -> actionSearch());
        cbGoi.addActionListener(e -> updatePrice());
        btnApplyVoucher.addActionListener(e -> applyVoucher());
        btnCancel.addActionListener(e -> resetForm());
        btnConfirm.addActionListener(e -> confirmRenewal());

        updatePrice();
    }

    private void actionSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã hội viên, SĐT hoặc Họ tên để tra cứu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (keyword.equalsIgnoreCase("HV015") || keyword.contains("Quang")) {
            currentMemberName = "Trần Đại Quang";
            currentMemberCode = "HV015";
            lblTen.setText("Trần Đại Quang (Mã: HV015 - SĐT: 0901 234 567)");
            lblCurrentStatus.setText("Gói 6 Tháng - Hết hạn 03/10/2026 (Còn 3 ngày)");
        } else if (keyword.equalsIgnoreCase("HV041") || keyword.contains("Thủy")) {
            currentMemberName = "Phạm Trọng Thủy";
            currentMemberCode = "HV041";
            lblTen.setText("Phạm Trọng Thủy (Mã: HV041 - SĐT: 0912 233 445)");
            lblCurrentStatus.setText("Gói 3 Tháng - Hết hạn 05/10/2026 (Còn 5 ngày)");
        } else if (keyword.equalsIgnoreCase("HV001") || keyword.contains("Tuấn")) {
            currentMemberName = "Nguyễn Văn Tuấn";
            currentMemberCode = "HV001";
            lblTen.setText("Nguyễn Văn Tuấn (Mã: HV001 - SĐT: 0901 234 567)");
            lblCurrentStatus.setText("VIP Thể Lực - Hết hạn 15/11/2026 (Còn 45 ngày)");
        } else {
            currentMemberName = keyword;
            currentMemberCode = "HV" + (int)(Math.random() * 800 + 100);
            lblTen.setText(keyword + " (Mã: " + currentMemberCode + " - SĐT: 0988 xxx xxx)");
            lblCurrentStatus.setText("Gói Tiêu Chuẩn - Sắp hết hạn");
        }

        JOptionPane.showMessageDialog(this, "Đã tìm thấy thông tin hội viên: " + currentMemberName, "Tra Cứu Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updatePrice() {
        int idx = cbGoi.getSelectedIndex();
        switch (idx) {
            case 0: basePrice = 1100000; break;
            case 1: basePrice = 2500000; break;
            case 2: basePrice = 4000000; break;
            case 3: basePrice = 6000000; break;
        }

        long finalPrice = basePrice - (basePrice * discountPercent / 100);
        lblThanhTien.setText(df.format(finalPrice));
    }

    private void applyVoucher() {
        String code = txtVoucher.getText().trim();
        if ("SUMMER26".equalsIgnoreCase(code)) {
            discountPercent = 20;
            JOptionPane.showMessageDialog(this, "Áp dụng thành công Voucher SUMMER26: Giảm 20%!", "Khuyến Mãi", JOptionPane.INFORMATION_MESSAGE);
        } else if ("VIPMEMBER".equalsIgnoreCase(code)) {
            discountPercent = 30;
            JOptionPane.showMessageDialog(this, "Áp dụng thành công Voucher VIPMEMBER: Giảm 30%!", "Khuyến Mãi", JOptionPane.INFORMATION_MESSAGE);
        } else if (code.isEmpty()) {
            discountPercent = 0;
            JOptionPane.showMessageDialog(this, "Đã hủy áp dụng mã voucher.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            discountPercent = 0;
            JOptionPane.showMessageDialog(this, "Mã voucher không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        updatePrice();
    }

    private void confirmRenewal() {
        int months = 3;
        int idx = cbGoi.getSelectedIndex();
        if (idx == 0) months = 3;
        else if (idx == 1) months = 6;
        else if (idx == 2) months = 13;
        else if (idx == 3) months = 2;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, months);
        String newExpire = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());

        String invoice = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
                + "            HÓA ĐƠN GIA HẠN GÓI TẬP\n"
                + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
                + "Mã Hóa Đơn: HD" + System.currentTimeMillis() % 1000000 + "\n"
                + "Hội Viên: " + currentMemberName + " (" + currentMemberCode + ")\n"
                + "Gói Mới: " + cbGoi.getSelectedItem() + "\n"
                + "Ngày Bắt Đầu: " + txtNgay.getText().trim() + "\n"
                + "Hạn Mới Đến Ngày: " + newExpire + "\n"
                + "Hình Thức Thanh Toán: " + cbThanhToan.getSelectedItem() + "\n"
                + "Giảm Giá: " + discountPercent + "%\n"
                + "TỔNG THANH TOÁN: " + lblThanhTien.getText() + "\n"
                + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
                + "Đã in hóa đơn và gia hạn thành công trên hệ thống!";

        JOptionPane.showMessageDialog(this, invoice, "Gia Hạn Thành Công", JOptionPane.INFORMATION_MESSAGE);
        lblCurrentStatus.setText("Đang hoạt động - Hạn mới: " + newExpire);
        lblCurrentStatus.setForeground(new Color(16, 185, 129));
    }

    private void resetForm() {
        txtSearch.setText("");
        txtVoucher.setText("");
        discountPercent = 0;
        cbGoi.setSelectedIndex(0);
        cbThanhToan.setSelectedIndex(0);
        txtNgay.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        updatePrice();
    }
}
