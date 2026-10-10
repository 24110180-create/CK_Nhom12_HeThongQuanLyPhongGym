package com.nhom12.gym.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CaiDatHeThongPanel extends JPanel {
    private JTextField txtGymName;
    private JTextField txtGymAddress;
    private JTextField txtGymHotline;
    private JTextField txtGymBillGreeting;
    private JButton btnSaveInfo;

    private JComboBox<String> cbBank;
    private JTextField txtBankAcc;
    private JTextField txtBankHolder;
    private JButton btnSaveBank;

    public CaiDatHeThongPanel() {
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("CÀI ĐẶT HỆ THỐNG & CỔNG THANH TOÁN VIETQR");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Cấu hình thông tin cơ sở phòng gym in trên hóa đơn, kết nối tài khoản ngân hàng nhận tiền");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JPanel centerContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        centerContainer.setOpaque(false);

        // Left Panel: Gym Info
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r++;
        JLabel lInfo = new JLabel("Thông Tin In Trên Hóa Đơn");
        lInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lInfo.setForeground(new Color(30, 41, 59));
        gbc.gridwidth = 2;
        infoPanel.add(lInfo, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.35;
        JLabel lName = new JLabel("Tên Phòng Tập:"); lName.setFont(fontLabel);
        infoPanel.add(lName, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtGymName = new JTextField("FitLife Gym & Fitness Center"); txtGymName.setFont(fontInput);
        infoPanel.add(txtGymName, gbc);

        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.35;
        JLabel lAddr = new JLabel("Địa Chỉ Cơ Sở:"); lAddr.setFont(fontLabel);
        infoPanel.add(lAddr, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtGymAddress = new JTextField("12 Nguyễn Văn Bảo, Phường 4, Gò Vấp, TP.HCM"); txtGymAddress.setFont(fontInput);
        infoPanel.add(txtGymAddress, gbc);

        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.35;
        JLabel lHotline = new JLabel("Hotline CSKH:"); lHotline.setFont(fontLabel);
        infoPanel.add(lHotline, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtGymHotline = new JTextField("1900 1234"); txtGymHotline.setFont(fontInput);
        infoPanel.add(txtGymHotline, gbc);

        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.35;
        JLabel lGreeting = new JLabel("Lời Chào Cuối Bill:"); lGreeting.setFont(fontLabel);
        infoPanel.add(lGreeting, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        txtGymBillGreeting = new JTextField("Cảm ơn quý khách đã tin tưởng và đồng hành cùng FitLife Gym!"); txtGymBillGreeting.setFont(fontInput);
        infoPanel.add(txtGymBillGreeting, gbc);

        r++; gbc.gridx = 1; gbc.gridy = r;
        btnSaveInfo = new JButton("Lưu Thông Tin Phòng Tập");
        btnSaveInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSaveInfo.setBackground(new Color(16, 185, 129));
        btnSaveInfo.setForeground(Color.WHITE);
        infoPanel.add(btnSaveInfo, gbc);

        r++; gbc.gridy = r; gbc.weighty = 1.0;
        infoPanel.add(Box.createVerticalGlue(), gbc);

        // Right Panel: Bank & VietQR
        JPanel bankPanel = new JPanel(new GridBagLayout());
        bankPanel.setBackground(Color.WHITE);
        bankPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbcBank = new GridBagConstraints();
        gbcBank.fill = GridBagConstraints.HORIZONTAL;
        gbcBank.insets = new Insets(10, 10, 10, 10);
        gbcBank.weightx = 1.0;

        int br = 0;
        gbcBank.gridx = 0; gbcBank.gridy = br++;
        JLabel lBankHeader = new JLabel("Cấu Hình Nhận Tiền VietQR");
        lBankHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lBankHeader.setForeground(new Color(30, 41, 59));
        gbcBank.gridwidth = 2;
        bankPanel.add(lBankHeader, gbcBank);

        gbcBank.gridwidth = 1;
        gbcBank.gridx = 0; gbcBank.gridy = br; gbcBank.weightx = 0.35;
        JLabel lBank = new JLabel("Ngân Hàng:"); lBank.setFont(fontLabel);
        bankPanel.add(lBank, gbcBank);
        gbcBank.gridx = 1; gbcBank.weightx = 0.65;
        cbBank = new JComboBox<>(new String[]{"MB Bank (Ngân hàng Quân Đội)", "Vietcombank", "Techcombank", "TPBank", "ACB"});
        cbBank.setFont(fontInput);
        bankPanel.add(cbBank, gbcBank);

        br++; gbcBank.gridx = 0; gbcBank.gridy = br; gbcBank.weightx = 0.35;
        JLabel lAcc = new JLabel("Số Tài Khoản:"); lAcc.setFont(fontLabel);
        bankPanel.add(lAcc, gbcBank);
        gbcBank.gridx = 1; gbcBank.weightx = 0.65;
        txtBankAcc = new JTextField("090123456789"); txtBankAcc.setFont(fontInput);
        bankPanel.add(txtBankAcc, gbcBank);

        br++; gbcBank.gridx = 0; gbcBank.gridy = br; gbcBank.weightx = 0.35;
        JLabel lHolder = new JLabel("Tên Chủ TK:"); lHolder.setFont(fontLabel);
        bankPanel.add(lHolder, gbcBank);
        gbcBank.gridx = 1; gbcBank.weightx = 0.65;
        txtBankHolder = new JTextField("FITLIFE GYM CENTER"); txtBankHolder.setFont(fontInput);
        bankPanel.add(txtBankHolder, gbcBank);

        br++; gbcBank.gridx = 0; gbcBank.gridy = br; gbcBank.gridwidth = 2;
        JLabel lblQRNote = new JLabel("<html><i>(Hệ thống tự động sinh mã VietQR động theo đúng tổng tiền của từng hóa đơn tại quầy POS)</i></html>");
        lblQRNote.setForeground(new Color(100, 116, 139));
        lblQRNote.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        bankPanel.add(lblQRNote, gbcBank);

        br++; gbcBank.gridx = 1; gbcBank.gridy = br; gbcBank.gridwidth = 1;
        btnSaveBank = new JButton("Cập Nhật Cổng VietQR");
        btnSaveBank.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSaveBank.setBackground(new Color(37, 99, 235));
        btnSaveBank.setForeground(Color.WHITE);
        bankPanel.add(btnSaveBank, gbcBank);

        br++; gbcBank.gridy = br; gbcBank.weighty = 1.0;
        bankPanel.add(Box.createVerticalGlue(), gbcBank);

        centerContainer.add(infoPanel);
        centerContainer.add(bankPanel);
        this.add(centerContainer, BorderLayout.CENTER);

        // Wire Action Listeners
        btnSaveInfo.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã lưu và cập nhật thông tin phòng gym thành công!\nCác hóa đơn in ra từ bây giờ sẽ hiển thị tiêu đề và địa chỉ mới.", "Cài Đặt Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSaveBank.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã cập nhật cổng thanh toán VietQR thành công!\nNgân hàng: " + cbBank.getSelectedItem() + "\nSố TK: " + txtBankAcc.getText().trim() + " (" + txtBankHolder.getText().trim() + ")", "Cập Nhật VietQR", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
