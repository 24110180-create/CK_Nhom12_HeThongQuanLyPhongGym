package com.nhom12.gym.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Giao diện Theo Dõi Chỉ Số Cơ Thể InBody Của Tôi dành cho Hội Viên
 */
public class HoiVienInBodyPanel extends JPanel {

    public HoiVienInBodyPanel() {
        setLayout(new BorderLayout(18, 18));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("HỒ SƠ THỂ LỰC & CHỈ SỐ INBODY CỦA TÔI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Theo dõi chi tiết lượng cơ, tỷ lệ mỡ và tiến trình rèn luyện qua từng tháng");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        topHeader.add(titleGroup, BorderLayout.WEST);

        JButton btnBookInBody = new JButton("Đặt Lịch Đo InBody Định Kỳ");
        btnBookInBody.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBookInBody.setBackground(new Color(37, 99, 235));
        btnBookInBody.setForeground(Color.WHITE);
        btnBookInBody.setFocusPainted(false);
        btnBookInBody.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBookInBody.setPreferredSize(new Dimension(240, 40));
        topHeader.add(btnBookInBody, BorderLayout.EAST);

        add(topHeader, BorderLayout.NORTH);

        // 2. MAIN CENTER: BỐ CỤC 2 PHẦN (Phần trên: 4 thẻ chỉ số lớn + Đánh giá thể trạng; Phần dưới: Bảng lịch sử đo)
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);

        // ROW 1: 4 STAT CARDS
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        statsRow.add(createMetricCard("CÂN NẶNG HIỆN TẠI", "68.5 kg", "▼ Giảm 1.2 kg so với tháng trước", new Color(16, 185, 129)));
        statsRow.add(createMetricCard("TỶ LỆ MỠ (BODY FAT)", "14.8 %", "● Thể hình Chuẩn (Athletic)", new Color(37, 99, 235)));
        statsRow.add(createMetricCard("KHỐI LƯỢNG CƠ BẮP", "34.2 kg", "▲ Tăng 0.8 kg cơ nạc", new Color(245, 158, 11)));
        statsRow.add(createMetricCard("CHỈ SỐ BMI / BMR", "22.3 BMI", "● Trao đổi chất: 1,680 kcal", new Color(139, 92, 246)));

        centerContainer.add(statsRow);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 18)));

        // ROW 2: COACH ADVICE & METERS (2 CỘT)
        JPanel midRow = new JPanel(new GridLayout(1, 2, 20, 0));
        midRow.setOpaque(false);
        midRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Khung đánh giá & lời khuyên HLV
        JPanel adviceBox = new JPanel(new BorderLayout(10, 8));
        adviceBox.setBackground(Color.WHITE);
        adviceBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JLabel lblAdviceTitle = new JLabel("LỜI KHUYÊN & ĐÁNH GIÁ TỪ HUẤN LUYỆN VIÊN (HLV DŨNG)");
        lblAdviceTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAdviceTitle.setForeground(new Color(15, 23, 42));

        JLabel lblAdviceBody = new JLabel("<html><body style='width: 380px; line-height: 1.4; color: #475569;'>" +
                "Tiến độ giảm mỡ và tăng cơ nạc rất khả quan! Hãy tiếp tục duy trì <b>4 buổi tập tạ/tuần</b>. " +
                "Tăng cường nạp <b>140g protein/ngày</b> (ức gà, trứng, whey) và ngủ đủ 7-8 tiếng để cơ bắp phục hồi tối đa." +
                "</body></html>");
        lblAdviceBody.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        adviceBox.add(lblAdviceTitle, BorderLayout.NORTH);
        adviceBox.add(lblAdviceBody, BorderLayout.CENTER);
        midRow.add(adviceBox);

        // Khung phân khúc thể trạng
        JPanel meterBox = new JPanel(new GridLayout(3, 1, 0, 6));
        meterBox.setBackground(Color.WHITE);
        meterBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 18, 12, 18)
        ));

        meterBox.add(createProgressMeter("Tỷ Lệ Mỡ (Body Fat)", 15, "14.8% (Mức chuẩn: 10 - 20%)", new Color(16, 185, 129)));
        meterBox.add(createProgressMeter("Khối Lượng Cơ Bắp (Muscle Mass)", 75, "34.2 kg (Mức cao vượt trội)", new Color(37, 99, 235)));
        meterBox.add(createProgressMeter("Mỡ Nội Tạng (Visceral Fat)", 20, "Cấp độ 3 (Cực kỳ an toàn & khỏe mạnh)", new Color(245, 158, 11)));

        midRow.add(meterBox);
        centerContainer.add(midRow);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 18)));

        // ROW 3: LỊCH SỬ CÁC LẦN ĐO INBODY
        JPanel historyContainer = new JPanel(new BorderLayout(0, 8));
        historyContainer.setOpaque(false);

        JLabel lblHisTitle = new JLabel("LỊCH SỬ CÁC LẦN ĐO INBODY GẦN NHẤT");
        lblHisTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHisTitle.setForeground(new Color(15, 23, 42));
        historyContainer.add(lblHisTitle, BorderLayout.NORTH);

        String[] cols = {"Đợt Đo", "Ngày Thực Hiện", "Cân Nặng", "Tỷ Lệ Mỡ (%)", "Cơ Bắp (kg)", "BMI", "HLV Đo & Tư Vấn", "Nhận Xét Tiến Độ"};
        Object[][] data = {
                {"Lần 4 (Mới nhất)", "25/09/2026", "68.5 kg", "14.8 %", "34.2 kg", "22.3", "Trần Văn Dũng", "Thể trạng chuẩn, cơ bắp săn chắc rõ rệt"},
                {"Lần 3", "25/08/2026", "69.7 kg", "16.2 %", "33.4 kg", "22.7", "Trần Văn Dũng", "Giảm mỡ tốt, sức bền cải thiện"},
                {"Lần 2", "25/07/2026", "71.0 kg", "17.9 %", "32.8 kg", "23.1", "Nguyễn Thu Hà", "Bắt đầu làm quen với tạ nặng"},
                {"Lần 1 (Ban đầu)", "25/06/2026", "72.5 kg", "19.5 %", "32.0 kg", "23.6", "Lê Duy Nam", "Bắt đầu chu kỳ giảm mỡ tăng cơ"}
        };

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(30, 41, 59));

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        historyContainer.add(scrollTable, BorderLayout.CENTER);

        centerContainer.add(historyContainer);
        add(centerContainer, BorderLayout.CENTER);

        // Event
        btnBookInBody.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "<html><body style='width: 280px; padding: 5px;'>" +
                    "<h3 style='color: #2563eb; margin: 0;'>ĐẶT LỊCH ĐO INBODY THÀNH CÔNG</h3>" +
                    "<p>Lịch đo tiếp theo: <b>Thứ Bảy, 08:30 sáng</b></p>" +
                    "<p>Người hỗ trợ: <b>HLV Trần Văn Dũng</b></p>" +
                    "<p style='color: #64748b;'>Lưu ý: Không ăn no và tránh uống nhiều nước trước khi đo 2 tiếng để có kết quả chính xác nhất.</p>" +
                    "</body></html>",
                    "Xác Nhận Lịch Đo", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private JPanel createMetricCard(String title, String val, String sub, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel content = new JPanel(new GridLayout(3, 1, 0, 2));
        content.setOpaque(false);

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblT.setForeground(new Color(100, 116, 139));

        JLabel lblV = new JLabel(val);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblV.setForeground(color);

        JLabel lblS = new JLabel(sub);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblS.setForeground(new Color(71, 85, 105));

        content.add(lblT);
        content.add(lblV);
        content.add(lblS);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createProgressMeter(String name, int percent, String statusText, Color color) {
        JPanel panel = new JPanel(new BorderLayout(8, 2));
        panel.setOpaque(false);

        JPanel topText = new JPanel(new BorderLayout());
        topText.setOpaque(false);
        JLabel lblN = new JLabel(name);
        lblN.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblN.setForeground(new Color(15, 23, 42));

        JLabel lblS = new JLabel(statusText);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblS.setForeground(color);

        topText.add(lblN, BorderLayout.WEST);
        topText.add(lblS, BorderLayout.EAST);
        panel.add(topText, BorderLayout.NORTH);

        JProgressBar pb = new JProgressBar(0, 100);
        pb.setValue(percent);
        pb.setForeground(color);
        pb.setBackground(new Color(241, 245, 249));
        pb.setBorderPainted(false);
        pb.setPreferredSize(new Dimension(0, 8));
        panel.add(pb, BorderLayout.CENTER);

        return panel;
    }
}
