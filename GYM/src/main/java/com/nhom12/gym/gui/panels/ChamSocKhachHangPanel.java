package com.nhom12.gym.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class ChamSocKhachHangPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextArea txtMessage;
    private JButton btnSelectAll;
    private JButton btnSMS;
    private JButton btnEmail;
    private JButton btnRenewDirect;

    public ChamSocKhachHangPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("CHĂM SÓC KHÁCH HÀNG & MARKETING TỰ ĐỘNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Tự động lọc danh sách hội viên sắp hết hạn gói tập, gửi tin nhắn Zalo ZNS / SMS nhắc gia hạn");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(620);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Panel (Table)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblListTitle = new JLabel("Hội Viên Sắp Hết Hạn Gói Tập (Cần Nhắc Nhở)");
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblListTitle.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblListTitle, BorderLayout.NORTH);

        String[] cols = new String[]{"Mã HV", "Họ Tên", "Số Điện Thoại", "Ngày Hết Hạn", "Còn Lại"};
        Object[][] data = new Object[][]{
                {"HV015", "Trần Đại Quang", "0901 234 567", "03/10/2026", "3 ngày"},
                {"HV022", "Lê Thị Lan", "0987 654 321", "01/10/2026", "1 ngày"},
                {"HV041", "Phạm Trọng Thủy", "0912 233 445", "05/10/2026", "5 ngày"},
                {"HV056", "Võ Thị Hằng", "0933 445 566", "02/10/2026", "2 ngày"},
                {"HV078", "Đặng Văn Lâm", "0977 889 900", "04/10/2026", "4 ngày"}
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
        table.getTableHeader().setBackground(new Color(254, 242, 242));
        leftPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBottom.setOpaque(false);
        btnSelectAll = new JButton("Chọn Tất Cả Hội Viên");
        btnSelectAll.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSelectAll.setBackground(new Color(100, 116, 139));
        btnSelectAll.setForeground(Color.WHITE);

        btnRenewDirect = new JButton("Gia Hạn Ngay Cho Khách Đang Chọn");
        btnRenewDirect.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRenewDirect.setBackground(new Color(16, 185, 129));
        btnRenewDirect.setForeground(Color.WHITE);

        leftBottom.add(btnSelectAll);
        leftBottom.add(btnRenewDirect);
        leftPanel.add(leftBottom, BorderLayout.SOUTH);
        splitPane.setLeftComponent(leftPanel);

        // Right Panel (Compose)
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblCompose = new JLabel("Nội Dung Tin Nhắn (SMS / Zalo ZNS)");
        lblCompose.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCompose.setForeground(new Color(30, 41, 59));
        rightPanel.add(lblCompose, BorderLayout.NORTH);

        txtMessage = new JTextArea();
        txtMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        txtMessage.setText("FitLife Gym thông báo: Gói tập của quý khách sắp hết hạn trong vài ngày tới.\nHiện đang có ưu đãi GIẢM 20% khi gia hạn ngay hôm nay (Mã ưu đãi: SUMMER26).\nVui lòng liên hệ quầy Lễ tân hoặc Hotline 1900 1234 để được hỗ trợ.\nTrân trọng cảm ơn quý khách!");
        rightPanel.add(new JScrollPane(txtMessage), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);

        btnSMS = new JButton("Gửi Zalo / SMS Hàng Loạt");
        btnSMS.setBackground(new Color(37, 99, 235));
        btnSMS.setForeground(Color.WHITE);
        btnSMS.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnEmail = new JButton("Gửi Email Marketing");
        btnEmail.setBackground(new Color(139, 92, 246));
        btnEmail.setForeground(Color.WHITE);
        btnEmail.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnPanel.add(btnSMS);
        btnPanel.add(btnEmail);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Wire Action Listeners
        btnSelectAll.addActionListener(e -> {
            table.selectAll();
            JOptionPane.showMessageDialog(this, "Đã chọn toàn bộ " + model.getRowCount() + " hội viên trong danh sách!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSMS.addActionListener(e -> {
            int count = table.getSelectedRowCount();
            if (count == 0) count = model.getRowCount();
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận gửi tin nhắn SMS / Zalo nhắc hạn tới " + count + " hội viên?", "Xác nhận gửi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Hệ thống đã kết nối cổng Zalo ZNS / SMS Brandname thành công!\nĐã gửi thông báo tới " + count + " hội viên.", "Gửi Thành Công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnEmail.addActionListener(e -> {
            int count = table.getSelectedRowCount();
            if (count == 0) count = model.getRowCount();
            JOptionPane.showMessageDialog(this, "Đã gửi Email chiến dịch ưu đãi gia hạn tới " + count + " hộp thư hội viên thành công!", "Gửi Email Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        btnRenewDirect.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hội viên trong danh sách!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String name = model.getValueAt(sel, 1).toString();
            String code = model.getValueAt(sel, 0).toString();
            JOptionPane.showMessageDialog(this, "Đã chuyển yêu cầu gia hạn của hội viên [" + name + " - " + code + "] sang màn hình Gia Hạn Gói Tập!", "Chuyển Tiếp", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
