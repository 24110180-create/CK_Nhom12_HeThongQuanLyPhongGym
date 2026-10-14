package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.HoiVien;
import java.text.SimpleDateFormat;
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
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
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
        JPanel searchBar = new JPanel(new BorderLayout(0, 8));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel lSearch = new JLabel("Tra Cứu Mã HV / SĐT:");
        lSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBar.add(lSearch);
        txtSearch = new JTextField("HV001", 15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.putClientProperty("JComponent.roundRect", true);
        txtSearch.setPreferredSize(new Dimension(txtSearch.getPreferredSize().width, 38));

        btnSearch = new JButton("Tìm Kiếm Hồ Sơ");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(new Color(37, 99, 235));
        btnSearch.setForeground(Color.WHITE);
        styleActionButton(btnSearch);

        JPanel searchControls = new JPanel(new BorderLayout(10, 0));
        searchControls.setOpaque(false);
        searchControls.add(lSearch, BorderLayout.WEST);
        searchControls.add(txtSearch, BorderLayout.CENTER);
        searchControls.add(btnSearch, BorderLayout.EAST);

        btnRenew = new JButton("Gia Hạn Gói Này");
        btnRenew.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRenew.setBackground(new Color(16, 185, 129));
        btnRenew.setForeground(Color.WHITE);
        styleActionButton(btnRenew);

        btnLock = new JButton("Khóa Thẻ Tạm Thời");
        btnLock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLock.setBackground(new Color(245, 158, 11));
        btnLock.setForeground(Color.WHITE);
        styleActionButton(btnLock);

        btnPrintProfile = new JButton("In Hồ Sơ");
        btnPrintProfile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrintProfile.setBackground(new Color(100, 116, 139));
        btnPrintProfile.setForeground(Color.WHITE);
        styleActionButton(btnPrintProfile);

        JPanel searchActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchActions.setOpaque(false);
        searchActions.add(btnRenew);
        searchActions.add(btnLock);
        searchActions.add(btnPrintProfile);
        searchBar.add(searchControls, BorderLayout.NORTH);
        searchBar.add(searchActions, BorderLayout.SOUTH);

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
        lblAvatar.putClientProperty("JComponent.roundRect", true);
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
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(lblName, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JLabel lPhone = new JLabel("Mã Thẻ & Cơ Sở:"); lPhone.setFont(fLabel);
        infoPanel.add(lPhone, gbc);
        gbc.gridx = 2;
        lblPhone = new JLabel("HV001 (Toàn hệ thống)");
        lblPhone.setFont(fValue);
        gbc.weightx = 1.0;
        infoPanel.add(lblPhone, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JLabel lGoi = new JLabel("Gói Đang Tập:"); lGoi.setFont(fLabel);
        infoPanel.add(lGoi, gbc);
        gbc.gridx = 2;
        lblGoi = new JLabel("VIP Thể Lực (6 Tháng) - Hạn: 15/11/2026");
        lblGoi.setFont(fValue);
        gbc.weightx = 1.0;
        infoPanel.add(lblGoi, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JLabel lStatus = new JLabel("Tình Trạng Thẻ:"); lStatus.setFont(fLabel);
        infoPanel.add(lStatus, gbc);
        gbc.gridx = 2;
        lblStatus = new JLabel("Đang hoạt động (Còn 45 ngày)");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(new Color(16, 185, 129));
        lblStatus.setOpaque(true);
        lblStatus.setBackground(new Color(240, 253, 244));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        gbc.weightx = 1.0;
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
                {"30/09/2026", "18:15:22", "CN01 - Quận 1", "Yoga Cơ Bản", "Máy quét Lễ tân 1"},
                {"28/09/2026", "17:45:10", "CN01 - Quận 1", "---", "Máy quét Lễ tân 1"},
                {"26/09/2026", "18:00:05", "CN01 - Quận 1", "---", "Máy quét Lễ tân 2"},
                {"24/09/2026", "06:30:12", "CN02 - Tân Bình", "---", "Quét mã FaceID Cổng"}
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
        table.getTableHeader().setForeground(new Color(30, 41, 59));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable source, Object value, boolean selected, boolean focus, int row, int column) {
                java.awt.Component component = super.getTableCellRendererComponent(source, value, selected, focus, row, column);
                if (!selected) component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                setHorizontalAlignment(column < 3 ? JLabel.CENTER : JLabel.LEFT);
                return component;
            }
        });
        historyPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel mainCenter = new JPanel(new BorderLayout(0, 15));
        mainCenter.setOpaque(false);
        mainCenter.add(infoPanel, BorderLayout.NORTH);
        mainCenter.add(historyPanel, BorderLayout.CENTER);
        centerPanel.add(mainCenter, BorderLayout.CENTER);

        this.add(centerPanel, BorderLayout.CENTER);

        // Action Listeners
        java.awt.event.ActionListener searchAction = e -> {
            String q = txtSearch.getText().trim();
            if (q.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã HV, Mã Thẻ QR hoặc Tên hội viên để tra cứu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            HoiVien hv = hoiVienDAO.timKiemThongMinh(q);
            if (hv != null) {
                lblName.setText(hv.getHoTen());
                lblPhone.setText((hv.getMaTheQR() != null ? hv.getMaTheQR() : "HV" + hv.getMaHoiVien()) + " (" + (hv.getChiNhanhDangKy() != null ? hv.getChiNhanhDangKy() : "Toàn hệ thống") + ")");
                
                String goiText = "Hội Viên Gym";
                if (hv.getChiNhanhDangKy() != null && !hv.getChiNhanhDangKy().isEmpty()) {
                    goiText += " - Cơ sở: " + hv.getChiNhanhDangKy();
                }
                if (hv.getNgayHetHan() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    goiText += " - Hạn: " + sdf.format(hv.getNgayHetHan());
                }
                lblGoi.setText(goiText);

                if (hv.getNgayHetHan() != null) {
                    long diffMs = hv.getNgayHetHan().getTime() - System.currentTimeMillis();
                    long days = diffMs / (1000L * 60 * 60 * 24);
                    if (days < 0) {
                        lblStatus.setText("ĐÃ HẾT HẠN (Quá hạn " + Math.abs(days) + " ngày)");
                        lblStatus.setForeground(new Color(239, 68, 68));
                        lblStatus.setBackground(new Color(254, 242, 242));
                    } else if (days <= 7) {
                        lblStatus.setText("Sắp hết hạn (Còn " + days + " ngày)");
                        lblStatus.setForeground(new Color(245, 158, 11));
                        lblStatus.setBackground(new Color(255, 251, 235));
                    } else {
                        lblStatus.setText("Đang hoạt động (Còn " + days + " ngày)");
                        lblStatus.setForeground(new Color(16, 185, 129));
                        lblStatus.setBackground(new Color(240, 253, 244));
                    }
                } else {
                    lblStatus.setText(hv.getTrangThai() != null ? hv.getTrangThai() : "Đang hoạt động");
                    lblStatus.setForeground(new Color(16, 185, 129));
                    lblStatus.setBackground(new Color(240, 253, 244));
                }

                JOptionPane.showMessageDialog(this, "Đã tìm thấy hồ sơ hội viên: " + hv.getHoTen() + " [Mã: " + hv.getMaHoiVien() + " | Thẻ QR: " + hv.getMaTheQR() + "]", "Tra Cứu", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hội viên nào khớp với từ khóa: \"" + q + "\"!\nVui lòng kiểm tra lại danh sách hội viên.", "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
            }
        };

        btnSearch.addActionListener(searchAction);
        txtSearch.addActionListener(searchAction);

        btnRenew.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã chuyển yêu cầu gia hạn của hội viên [" + lblName.getText() + "] sang màn hình Gia Hạn Gói Tập!", "Chuyển Tiếp", JOptionPane.INFORMATION_MESSAGE);
        });

        btnLock.addActionListener(e -> {
            String cur = lblStatus.getText();
            if (cur.contains("Tạm khóa")) {
                lblStatus.setText("Đang hoạt động (Còn 45 ngày)");
                lblStatus.setForeground(new Color(16, 185, 129));
                lblStatus.setBackground(new Color(240, 253, 244));
                btnLock.setText("Khóa Thẻ Tạm Thời");
                JOptionPane.showMessageDialog(this, "Đã MỞ KHÓA thẻ hội viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                lblStatus.setText("Tạm khóa theo yêu cầu của khách");
                lblStatus.setForeground(new Color(239, 68, 68));
                lblStatus.setBackground(new Color(254, 242, 242));
                btnLock.setText("Mở Khóa Thẻ");
                JOptionPane.showMessageDialog(this, "Đã TẠM KHÓA thẻ hội viên thành công! Khách không thể check-in cho tới khi mở lại.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnPrintProfile.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đang kết nối máy in...\nĐã xuất và in hồ sơ chi tiết kèm lịch sử check-in của hội viên: " + lblName.getText(), "In Hồ Sơ", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void styleActionButton(JButton button) {
        Color normalColor = button.getBackground();
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setPreferredSize(new Dimension(Math.max(button.getPreferredSize().width, 130), 40));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) button.setBackground(normalColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }
}
