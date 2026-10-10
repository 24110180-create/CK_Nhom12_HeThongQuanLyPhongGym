package com.nhom12.gym.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Giao diện Lịch Tập Với Huấn Luyện Viên Cá Nhân (PT) & Kỷ Lục PR dành cho Hội Viên
 */
public class HoiVienLichPTPanel extends JPanel {

    private final DefaultTableModel prModel;
    private final JTable prTable;

    public HoiVienLichPTPanel() {
        setLayout(new BorderLayout(18, 18));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("LỊCH TẬP VỚI HLV CÁ NHÂN (1-ON-1 PT) & KỶ LỤC TẠ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Quản lý các buổi tập kèm riêng, liên hệ huấn luyện viên và nhật ký thành tích PR");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        topHeader.add(titleGroup, BorderLayout.WEST);

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topActions.setOpaque(false);

        JButton btnContactCoach = new JButton("Nhắn Tin Cho HLV");
        btnContactCoach.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnContactCoach.setBackground(new Color(16, 185, 129));
        btnContactCoach.setForeground(Color.WHITE);
        btnContactCoach.setFocusPainted(false);
        btnContactCoach.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnContactCoach.setPreferredSize(new Dimension(160, 40));

        JButton btnChangeSchedule = new JButton("Đổi Giờ Buổi Tập");
        btnChangeSchedule.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnChangeSchedule.setBackground(new Color(37, 99, 235));
        btnChangeSchedule.setForeground(Color.WHITE);
        btnChangeSchedule.setFocusPainted(false);
        btnChangeSchedule.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangeSchedule.setPreferredSize(new Dimension(160, 40));

        topActions.add(btnContactCoach);
        topActions.add(btnChangeSchedule);
        topHeader.add(topActions, BorderLayout.EAST);

        add(topHeader, BorderLayout.NORTH);

        // 2. MAIN CENTER: 2 CỘT (Cột trái: Thông tin HLV & Buổi tập; Cột phải: Kỷ lục PR & Tiến trình bài tập)
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 25, 0));
        mainContent.setOpaque(false);

        // === CỘT TRÁI: HỒ SƠ HLV & BUỔI HẸN ===
        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setOpaque(false);

        // Thẻ Huấn Luyện Viên
        JPanel coachCard = new JPanel(new BorderLayout(15, 12));
        coachCard.setBackground(Color.WHITE);
        coachCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // Avatar HLV
        JLabel lblCoachAvatar = new JLabel("HLV", SwingConstants.CENTER);
        lblCoachAvatar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCoachAvatar.setForeground(Color.WHITE);
        lblCoachAvatar.setBackground(new Color(245, 158, 11));
        lblCoachAvatar.setOpaque(true);
        lblCoachAvatar.setPreferredSize(new Dimension(65, 65));
        coachCard.add(lblCoachAvatar, BorderLayout.WEST);

        JPanel coachInfo = new JPanel(new GridLayout(4, 1, 0, 3));
        coachInfo.setOpaque(false);
        JLabel lblCoachName = new JLabel("TRẦN VĂN DŨNG");
        lblCoachName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCoachName.setForeground(new Color(15, 23, 42));

        JLabel lblCoachCert = new JLabel("Chứng chỉ Quốc tế NASM CPT • 6 năm kinh nghiệm");
        lblCoachCert.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCoachCert.setForeground(new Color(71, 85, 105));

        JLabel lblCoachRating = new JLabel("★★★★★ (4.95 / 5.0 • 120+ học viên đánh giá)");
        lblCoachRating.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCoachRating.setForeground(new Color(234, 179, 8));

        JLabel lblCoachPhone = new JLabel("Hotline / Zalo riêng: 0988.123.456");
        lblCoachPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCoachPhone.setForeground(new Color(37, 99, 235));

        coachInfo.add(lblCoachName);
        coachInfo.add(lblCoachCert);
        coachInfo.add(lblCoachRating);
        coachInfo.add(lblCoachPhone);
        coachCard.add(coachInfo, BorderLayout.CENTER);

        pnlLeft.add(coachCard);
        pnlLeft.add(Box.createRigidArea(new Dimension(0, 18)));

        // Hộp tiến độ số buổi PT
        JPanel pnlSessions = new JPanel(new GridLayout(3, 1, 0, 10));
        pnlSessions.setBackground(Color.WHITE);
        pnlSessions.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel rowSess1 = new JPanel(new BorderLayout());
        rowSess1.setOpaque(false);
        JLabel lblSessTitle = new JLabel("Số Buổi Đã Hoàn Thành:");
        lblSessTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblSessVal = new JLabel("16 / 24 Buổi (Còn 8 buổi)");
        lblSessVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSessVal.setForeground(new Color(16, 185, 129));
        rowSess1.add(lblSessTitle, BorderLayout.WEST);
        rowSess1.add(lblSessVal, BorderLayout.EAST);
        pnlSessions.add(rowSess1);

        JProgressBar pbSessions = new JProgressBar(0, 24);
        pbSessions.setValue(16);
        pbSessions.setForeground(new Color(16, 185, 129));
        pbSessions.setBackground(new Color(241, 245, 249));
        pbSessions.setBorderPainted(false);
        pbSessions.setPreferredSize(new Dimension(0, 10));
        pnlSessions.add(pbSessions);

        JPanel nextSession = new JPanel(new BorderLayout());
        nextSession.setOpaque(false);
        JLabel lblNextTitle = new JLabel("Buổi Hẹn Tiếp Theo:");
        lblNextTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNextTitle.setForeground(new Color(100, 116, 139));
        JLabel lblNextVal = new JLabel("Thứ Năm • 18:00 - 19:30 (Thân Trên)");
        lblNextVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNextVal.setForeground(new Color(37, 99, 235));
        nextSession.add(lblNextTitle, BorderLayout.WEST);
        nextSession.add(lblNextVal, BorderLayout.EAST);
        pnlSessions.add(nextSession);

        pnlLeft.add(pnlSessions);
        mainContent.add(pnlLeft);

        // === CỘT PHẢI: BẢNG KỶ LỤC TẠ PR (PERSONAL RECORDS) ===
        JPanel pnlRight = new JPanel(new BorderLayout(0, 12));
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel prHeader = new JPanel(new BorderLayout());
        prHeader.setOpaque(false);
        JLabel lblPrTitle = new JLabel("KỶ LỤC MỨC TẠ CÁ NHÂN (PERSONAL RECORDS - PR)");
        lblPrTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrTitle.setForeground(new Color(15, 23, 42));

        JButton btnAddPR = new JButton("+ Ghi Nhận Kỷ Lục Mới");
        btnAddPR.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddPR.setBackground(new Color(245, 158, 11));
        btnAddPR.setForeground(Color.WHITE);
        btnAddPR.setFocusPainted(false);
        btnAddPR.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddPR.setPreferredSize(new Dimension(170, 32));

        prHeader.add(lblPrTitle, BorderLayout.WEST);
        prHeader.add(btnAddPR, BorderLayout.EAST);
        pnlRight.add(prHeader, BorderLayout.NORTH);

        String[] cols = {"Bài Tập Trọng Điểm", "Mức Tạ (PR)", "Số Reps", "Ngày Thiết Lập", "Nhận Xét HLV"};
        Object[][] data = {
                {"Barbell Bench Press (Đẩy ngực)", "85 kg", "3 reps", "22/09/2026", "Form chuẩn, đẩy dứt khoát"},
                {"Barbell Back Squat (Gánh đùi)", "110 kg", "5 reps", "18/09/2026", "Xuống sâu song song sàn"},
                {"Conventional Deadlift", "130 kg", "2 reps", "12/09/2026", "Khóa lưng tốt, không võng"},
                {"Overhead Shoulder Press", "50 kg", "6 reps", "20/09/2026", "Ổn định vai, siết cơ bụng"},
                {"Pull-up (Hít xà đơn)", "Bodyweight", "15 reps", "24/09/2026", "Kỷ lục cá nhân tốt nhất"}
        };

        prModel = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        prTable = new JTable(prModel);
        prTable.setRowHeight(34);
        prTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        prTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        prTable.getTableHeader().setBackground(new Color(241, 245, 249));
        prTable.getTableHeader().setForeground(new Color(30, 41, 59));

        JScrollPane scrollPR = new JScrollPane(prTable);
        scrollPR.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlRight.add(scrollPR, BorderLayout.CENTER);

        mainContent.add(pnlRight);
        add(mainContent, BorderLayout.CENTER);

        // Events
        btnContactCoach.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "<html><body style='width: 280px; padding: 5px;'>" +
                    "<h3 style='color: #10b981; margin: 0;'>KẾT NỐI VỚI HLV THÀNH CÔNG</h3>" +
                    "<p>HLV Trần Văn Dũng đã nhận được thông báo kết nối từ bạn.</p>" +
                    "<p>Bạn cũng có thể gọi trực tiếp hoặc nhắn Zalo: <b>0988.123.456</b></p>" +
                    "</body></html>",
                    "Liên Hệ HLV", JOptionPane.INFORMATION_MESSAGE);
        });

        btnChangeSchedule.addActionListener(e -> {
            String[] times = {"Thứ Năm • 07:00 - 08:30 (Sáng)", "Thứ Năm • 19:30 - 21:00 (Tối)", "Thứ Sáu • 18:00 - 19:30 (Chiều tối)", "Thứ Bảy • 09:00 - 10:30 (Sáng)"};
            String choice = (String) JOptionPane.showInputDialog(this,
                    "Chọn khung giờ bạn muốn đổi ca tập:",
                    "Đổi Lịch Hẹn PT", JOptionPane.QUESTION_MESSAGE, null, times, times[0]);
            if (choice != null) {
                lblNextVal.setText(choice);
                JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu đổi lịch hẹn sang: " + choice + ".\nHLV đã phê duyệt lịch mới!", "Đổi Giờ Thành Công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnAddPR.addActionListener(e -> {
            JTextField txtEx = new JTextField(15);
            JTextField txtWeight = new JTextField(10);
            JTextField txtReps = new JTextField(10);

            JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
            form.add(new JLabel("Tên bài tập:"));
            form.add(txtEx);
            form.add(new JLabel("Mức tạ đạt được (kg):"));
            form.add(txtWeight);
            form.add(new JLabel("Số lần lặp (Reps):"));
            form.add(txtReps);

            int opt = JOptionPane.showConfirmDialog(this, form, "Ghi Nhận Kỷ Lục PR Mới", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                String ex = txtEx.getText().trim();
                String weight = txtWeight.getText().trim();
                String reps = txtReps.getText().trim();
                if (ex.isEmpty() || weight.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên bài tập và mức tạ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                prModel.insertRow(0, new Object[]{ex, weight + " kg", reps + " reps", "Hôm nay", "HLV xác nhận đạt chuẩn"});
                JOptionPane.showMessageDialog(this, "Chúc mừng! Kỷ lục mới đã được ghi nhận vào sổ tay thể lực!", "Chúc Mừng PR Mới", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}
