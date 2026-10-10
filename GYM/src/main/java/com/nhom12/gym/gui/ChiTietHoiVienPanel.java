package com.nhom12.gym.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ChiTietHoiVienPanel extends JPanel {
    private JTextField txtSearch;
    private JButton btnSearch;
    private JLabel lblName;
    private JLabel lblPhone;
    private JLabel lblGoi;
    private JLabel lblStatus;
    private JButton btnRenew;
    private JButton btnLock;
    private JButton btnPrintProfile;

    private JTable table;
    private DefaultTableModel model;

    public ChiTietHoiVienPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("HỒ SƠ CHI TIẾT & LỊCH SỬ HOẠT ĐỘNG HỘI VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Tra cứu hồ sơ khách hàng, thời hạn thẻ tập và nhật ký quét mã check-in qua cổng an ninh");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Search Bar Top
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lSearch = new JLabel("Tra Cứu Mã HV / SĐT:");
        lSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBar.add(lSearch);
        txtSearch = new JTextField("HV001", 15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchBar.add(txtSearch);

        btnSearch = new JButton("Tìm Kiếm Hồ Sơ");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(new Color(37, 99, 235));
        btnSearch.setForeground(Color.WHITE);
        searchBar.add(btnSearch);

        btnRenew = new JButton("Gia Hạn Gói Này");
        btnRenew.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRenew.setBackground(new Color(16, 185, 129));
        btnRenew.setForeground(Color.WHITE);
        searchBar.add(btnRenew);

        btnLock = new JButton("Khóa Thẻ Tạm Thời");
        btnLock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLock.setBackground(new Color(245, 158, 11));
        btnLock.setForeground(Color.WHITE);
        searchBar.add(btnLock);

        btnPrintProfile = new JButton("In Hồ Sơ");
        btnPrintProfile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrintProfile.setBackground(new Color(100, 116, 139));
        btnPrintProfile.setForeground(Color.WHITE);
        searchBar.add(btnPrintProfile);

        centerPanel.add(searchBar, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 15, 6, 15);

        // Member Avatar Icon Box
        JLabel lblAvatar = new JLabel("HV", JLabel.CENTER);
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblAvatar.setForeground(new Color(37, 99, 235));
        lblAvatar.setPreferredSize(new Dimension(85, 85));
        lblAvatar.setBackground(new Color(238, 242, 255));
        lblAvatar.setOpaque(true);
        lblAvatar.setBorder(BorderFactory.createLineBorder(new Color(199, 210, 254), 2));
        gbc.gridheight = 4;
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(lblAvatar, gbc);

        gbc.gridheight = 1;
        Font fLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fValue = new Font("Segoe UI", Font.PLAIN, 15);

        gbc.gridx = 1; gbc.gridy = 0;
        JLabel lName = new JLabel("Họ Và Tên:"); lName.setFont(fLabel);
        infoPanel.add(lName, gbc);
        gbc.gridx = 2;
        lblName = new JLabel("Nguyễn Văn Tuấn");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(new Color(30, 41, 59));
        infoPanel.add(lblName, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JLabel lPhone = new JLabel("Số Điện Thoại:"); lPhone.setFont(fLabel);
        infoPanel.add(lPhone, gbc);
        gbc.gridx = 2;
        lblPhone = new JLabel("0901 234 567");
        lblPhone.setFont(fValue);
        infoPanel.add(lblPhone, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JLabel lGoi = new JLabel("Gói Đang Tập:"); lGoi.setFont(fLabel);
        infoPanel.add(lGoi, gbc);
        gbc.gridx = 2;
        lblGoi = new JLabel("VIP Thể Lực (6 Tháng) - Hạn: 15/11/2026");
        lblGoi.setFont(fValue);
        infoPanel.add(lblGoi, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JLabel lStatus = new JLabel("Tình Trạng Thẻ:"); lStatus.setFont(fLabel);
        infoPanel.add(lStatus, gbc);
        gbc.gridx = 2;
        lblStatus = new JLabel("Đang hoạt động (Còn 45 ngày)");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(new Color(16, 185, 129));
        infoPanel.add(lblStatus, gbc);

        // History Table
        JPanel historyPanel = new JPanel(new BorderLayout(0, 10));
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblHistTitle = new JLabel("Nhật Ký Quét Thẻ Check-in & Sử Dụng Dịch Vụ");
        lblHistTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHistTitle.setForeground(new Color(30, 41, 59));
        historyPanel.add(lblHistTitle, BorderLayout.NORTH);

        String[] cols = new String[]{"Ngày", "Giờ Check-in", "Chi Nhánh", "Lớp Học (Nếu Có)", "Thiết Bị Quét"};
        Object[][] data = new Object[][]{
                {"30/09/2026", "18:15:22", "Cơ sở 1 - Gò Vấp", "Yoga Cơ Bản", "Máy quét Lễ tân 1"},
                {"28/09/2026", "17:45:10", "Cơ sở 1 - Gò Vấp", "---", "Máy quét Lễ tân 1"},
                {"26/09/2026", "18:00:05", "Cơ sở 1 - Gò Vấp", "---", "Máy quét Lễ tân 2"},
                {"24/09/2026", "06:30:12", "Cơ sở 2 - Quận 7", "---", "Quét mã FaceID Cổng"}
        };
        model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        historyPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel mainCenter = new JPanel(new BorderLayout(0, 15));
        mainCenter.setOpaque(false);
        mainCenter.add(infoPanel, BorderLayout.NORTH);
        mainCenter.add(historyPanel, BorderLayout.CENTER);
        centerPanel.add(mainCenter, BorderLayout.CENTER);

        this.add(centerPanel, BorderLayout.CENTER);

        // Action Listeners
        btnSearch.addActionListener(e -> {
            String q = txtSearch.getText().trim();
            if (q.equalsIgnoreCase("HV022") || q.contains("Lan")) {
                lblName.setText("Lê Thị Lan");
                lblPhone.setText("0987 654 321");
                lblGoi.setText("Gói 1 Tháng Thử Nghiệm - Hạn: 01/10/2026");
                lblStatus.setText("Sắp hết hạn (Còn 1 ngày)");
                lblStatus.setForeground(new Color(239, 68, 68));
            } else {
                lblName.setText("Nguyễn Văn Tuấn");
                lblPhone.setText("0901 234 567");
                lblGoi.setText("VIP Thể Lực (6 Tháng) - Hạn: 15/11/2026");
                lblStatus.setText("Đang hoạt động (Còn 45 ngày)");
                lblStatus.setForeground(new Color(16, 185, 129));
            }
            JOptionPane.showMessageDialog(this, "Đã tải xong hồ sơ hội viên: " + lblName.getText(), "Tra Cứu", JOptionPane.INFORMATION_MESSAGE);
        });

        btnRenew.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã chuyển yêu cầu gia hạn của hội viên [" + lblName.getText() + "] sang màn hình Gia Hạn Gói Tập!", "Chuyển Tiếp", JOptionPane.INFORMATION_MESSAGE);
        });

        btnLock.addActionListener(e -> {
            String cur = lblStatus.getText();
            if (cur.contains("Tạm khóa")) {
                lblStatus.setText("Đang hoạt động (Còn 45 ngày)");
                lblStatus.setForeground(new Color(16, 185, 129));
                btnLock.setText("Khóa Thẻ Tạm Thời");
                JOptionPane.showMessageDialog(this, "Đã MỞ KHÓA thẻ hội viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                lblStatus.setText("Tạm khóa theo yêu cầu của khách");
                lblStatus.setForeground(new Color(239, 68, 68));
                btnLock.setText("Mở Khóa Thẻ");
                JOptionPane.showMessageDialog(this, "Đã TẠM KHÓA thẻ hội viên thành công! Khách không thể check-in cho tới khi mở lại.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnPrintProfile.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đang kết nối máy in...\nĐã xuất và in hồ sơ chi tiết kèm lịch sử check-in của hội viên: " + lblName.getText(), "In Hồ Sơ", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
