package com.nhom12.gym.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class QuanLyLockerPanel extends JPanel {
    private static class LockerInfo {
        int id;
        String name;
        int status; // 0: Trong, 1: Dang dung, 2: Bao tri
        String memberName;
        String timeAssigned;

        LockerInfo(int id, String name, int status, String memberName, String timeAssigned) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.memberName = memberName;
            this.timeAssigned = timeAssigned;
        }
    }

    private final LockerInfo[] lockers = new LockerInfo[20];
    private final JButton[] lockerButtons = new JButton[20];
    private int selectedIndex = -1;

    private JLabel lblLockerTitle;
    private JLabel lblTrangThai;
    private JLabel lblNguoiDung;
    private JLabel lblThoiGian;
    private JButton btnAction;
    private JButton btnMaintenance;
    private JButton btnEmergency;

    private JLabel lblStatTotal;
    private JLabel lblStatUsed;
    private JLabel lblStatFree;
    private JLabel lblStatMaintenance;

    public QuanLyLockerPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Initialize 20 Lockers data
        for (int i = 0; i < 20; i++) {
            String name = "Tủ " + String.format("%02d", i + 1);
            int status = (i == 3 || i == 6 || i == 11 || i == 15) ? 1 : (i == 18 ? 2 : 0);
            String member = (status == 1) ? ("Hội viên HV00" + (i + 1)) : "---";
            String time = (status == 1) ? "17:30 30/09/2026" : "---";
            lockers[i] = new LockerInfo(i + 1, name, status, member, time);
        }

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ SƠ ĐỒ TỦ ĐỒ (SMART LOCKER)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Theo dõi trạng thái thời gian thực, cấp phát và thu hồi chìa khóa tủ đồ thông minh");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        // Center Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(620);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Locker Grid (5 rows x 4 cols)
        JPanel leftPanel = new JPanel(new GridLayout(5, 4, 12, 12));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        for (int i = 0; i < 20; i++) {
            final int idx = i;
            JButton btn = new JButton(lockers[i].name);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            applyLockerButtonStyle(btn, lockers[i].status);

            btn.addActionListener(e -> selectLocker(idx));
            lockerButtons[i] = btn;
            leftPanel.add(btn);
        }
        splitPane.setLeftComponent(leftPanel);

        // Right Detail Panel
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        lblLockerTitle = new JLabel("Chưa chọn tủ nào");
        lblLockerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLockerTitle.setForeground(new Color(30, 41, 59));
        gbc.gridy = 0;
        rightPanel.add(lblLockerTitle, gbc);

        lblTrangThai = new JLabel("Trạng thái: Vui lòng click chọn tủ trên sơ đồ");
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTrangThai.setForeground(Color.GRAY);
        gbc.gridy = 1;
        rightPanel.add(lblTrangThai, gbc);

        lblNguoiDung = new JLabel("Hội viên sử dụng: ---");
        lblNguoiDung.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        gbc.gridy = 2;
        rightPanel.add(lblNguoiDung, gbc);

        lblThoiGian = new JLabel("Thời gian bàn giao: ---");
        lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblThoiGian.setForeground(Color.GRAY);
        gbc.gridy = 3;
        rightPanel.add(lblThoiGian, gbc);

        // Action Button: Cap phat or Tra tu
        btnAction = new JButton("Chọn Tủ Đồ Để Thao Tác");
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAction.setBackground(new Color(100, 116, 139));
        btnAction.setForeground(Color.WHITE);
        btnAction.setEnabled(false);
        gbc.gridy = 4;
        gbc.insets = new Insets(18, 10, 6, 10);
        rightPanel.add(btnAction, gbc);

        // Maintenance button
        btnMaintenance = new JButton("Chuyển Chế Độ Bảo Trì");
        btnMaintenance.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnMaintenance.setBackground(new Color(245, 158, 11));
        btnMaintenance.setForeground(Color.WHITE);
        btnMaintenance.setEnabled(false);
        gbc.gridy = 5;
        gbc.insets = new Insets(6, 10, 6, 10);
        rightPanel.add(btnMaintenance, gbc);

        // Emergency unlock button
        btnEmergency = new JButton("Mở Khóa Khẩn Cấp (Master PIN)");
        btnEmergency.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnEmergency.setEnabled(false);
        gbc.gridy = 6;
        gbc.insets = new Insets(6, 10, 6, 10);
        rightPanel.add(btnEmergency, gbc);

        gbc.gridy = 7;
        gbc.weighty = 1.0;
        rightPanel.add(Box.createVerticalGlue(), gbc);

        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Bottom Stats Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 8));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        lblStatTotal = new JLabel("Tổng số tủ: 20");
        lblStatTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblStatFree = new JLabel("Sẵn sàng: 15");
        lblStatFree.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatFree.setForeground(new Color(16, 185, 129));

        lblStatUsed = new JLabel("Đang dùng: 4");
        lblStatUsed.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatUsed.setForeground(new Color(239, 68, 68));

        lblStatMaintenance = new JLabel("Bảo trì: 1");
        lblStatMaintenance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatMaintenance.setForeground(new Color(245, 158, 11));

        bottomBar.add(lblStatTotal);
        bottomBar.add(lblStatFree);
        bottomBar.add(lblStatUsed);
        bottomBar.add(lblStatMaintenance);
        this.add(bottomBar, BorderLayout.SOUTH);

        // Wire Action Listeners
        btnAction.addActionListener(e -> actionMainButton());
        btnMaintenance.addActionListener(e -> actionToggleMaintenance());
        btnEmergency.addActionListener(e -> actionEmergencyUnlock());

        updateStats();
        // Select first locker by default
        selectLocker(0);
    }

    private void applyLockerButtonStyle(JButton btn, int status) {
        if (status == 1) { // In use
            btn.setBackground(new Color(239, 68, 68));
            btn.setForeground(Color.WHITE);
        } else if (status == 2) { // Maintenance
            btn.setBackground(new Color(245, 158, 11));
            btn.setForeground(Color.WHITE);
        } else { // Free
            btn.setBackground(new Color(16, 185, 129));
            btn.setForeground(Color.WHITE);
        }
    }

    private void selectLocker(int idx) {
        this.selectedIndex = idx;
        LockerInfo lk = lockers[idx];

        lblLockerTitle.setText(lk.name);
        btnAction.setEnabled(true);
        btnMaintenance.setEnabled(true);
        btnEmergency.setEnabled(true);

        if (lk.status == 0) { // Free
            lblTrangThai.setText("Trạng thái: Trống (Sẵn sàng cấp)");
            lblTrangThai.setForeground(new Color(16, 185, 129));
            lblNguoiDung.setText("Hội viên sử dụng: ---");
            lblThoiGian.setText("Thời gian: Sẵn sàng sử dụng");

            btnAction.setText("Cấp Phát Tủ Này");
            btnAction.setBackground(new Color(37, 99, 235));
            btnAction.setForeground(Color.WHITE);

            btnMaintenance.setText("Chuyển Sang Bảo Trì");
        } else if (lk.status == 1) { // In use
            lblTrangThai.setText("Trạng thái: Đang sử dụng");
            lblTrangThai.setForeground(new Color(239, 68, 68));
            lblNguoiDung.setText("Hội viên: " + lk.memberName);
            lblThoiGian.setText("Thời gian nhận: " + lk.timeAssigned);

            btnAction.setText("Trả Tủ Đồ & Mở Khóa");
            btnAction.setBackground(new Color(16, 185, 129));
            btnAction.setForeground(Color.WHITE);

            btnMaintenance.setText("Báo Hư Hỏng Khóa");
        } else { // Maintenance
            lblTrangThai.setText("Trạng thái: Đang bảo trì / Hư khóa");
            lblTrangThai.setForeground(new Color(245, 158, 11));
            lblNguoiDung.setText("Hội viên sử dụng: Khóa tạm ngừng");
            lblThoiGian.setText("Ghi chú: Kỹ thuật đang kiểm tra");

            btnAction.setText("Hoàn Tất Sửa Chữa");
            btnAction.setBackground(new Color(16, 185, 129));
            btnAction.setForeground(Color.WHITE);

            btnMaintenance.setText("Hủy Bảo Trì");
        }
    }

    private void actionMainButton() {
        if (selectedIndex == -1) return;
        LockerInfo lk = lockers[selectedIndex];

        if (lk.status == 0) { // Free -> Allocate
            String member = JOptionPane.showInputDialog(this, "Nhập Tên hoặc Mã Hội Viên nhận " + lk.name + ":", "Cấp phát tủ", JOptionPane.QUESTION_MESSAGE);
            if (member != null && !member.trim().isEmpty()) {
                lk.status = 1;
                lk.memberName = member.trim();
                lk.timeAssigned = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date());

                applyLockerButtonStyle(lockerButtons[selectedIndex], lk.status);
                selectLocker(selectedIndex);
                updateStats();
                JOptionPane.showMessageDialog(this, "Đã cấp phát " + lk.name + " cho [" + member.trim() + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (lk.status == 1) { // In use -> Return
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận nhận lại chìa khóa và mở khóa " + lk.name + " của [" + lk.memberName + "]?", "Thu hồi tủ đồ", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                lk.status = 0;
                lk.memberName = "---";
                lk.timeAssigned = "---";

                applyLockerButtonStyle(lockerButtons[selectedIndex], lk.status);
                selectLocker(selectedIndex);
                updateStats();
                JOptionPane.showMessageDialog(this, "Đã thu hồi " + lk.name + " thành công! Tủ đã sẵn sàng cho khách tiếp theo.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else { // Maintenance -> Free
            lk.status = 0;
            lk.memberName = "---";
            lk.timeAssigned = "---";
            applyLockerButtonStyle(lockerButtons[selectedIndex], lk.status);
            selectLocker(selectedIndex);
            updateStats();
            JOptionPane.showMessageDialog(this, lk.name + " đã kết thúc bảo trì và sẵn sàng sử dụng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actionToggleMaintenance() {
        if (selectedIndex == -1) return;
        LockerInfo lk = lockers[selectedIndex];

        if (lk.status != 2) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn muốn đánh dấu " + lk.name + " là Đang bảo trì / Hư hỏng?", "Bảo trì tủ", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                lk.status = 2;
                applyLockerButtonStyle(lockerButtons[selectedIndex], lk.status);
                selectLocker(selectedIndex);
                updateStats();
            }
        } else {
            lk.status = 0;
            applyLockerButtonStyle(lockerButtons[selectedIndex], lk.status);
            selectLocker(selectedIndex);
            updateStats();
        }
    }

    private void actionEmergencyUnlock() {
        if (selectedIndex == -1) return;
        LockerInfo lk = lockers[selectedIndex];
        JOptionPane.showMessageDialog(this, "Đã kích hoạt tín hiệu mở khóa từ xa cho " + lk.name + " thành công!\nChốt khóa điện tử đã nhả.", "Mở Khóa Khẩn Cấp", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStats() {
        int total = lockers.length;
        int used = 0;
        int free = 0;
        int maint = 0;

        for (LockerInfo lk : lockers) {
            if (lk.status == 1) used++;
            else if (lk.status == 2) maint++;
            else free++;
        }

        lblStatTotal.setText("Tổng số tủ: " + total);
        lblStatFree.setText("Sẵn sàng: " + free);
        lblStatUsed.setText("Đang dùng: " + used);
        lblStatMaintenance.setText("Bảo trì: " + maint);
    }
}
