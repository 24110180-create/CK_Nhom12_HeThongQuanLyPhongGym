package com.nhom12.gym.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Giao diện Đánh Giá & Góp Ý Dịch Vụ dành cho Hội Viên
 */
public class HoiVienGopYPanel extends JPanel {

    private final JTextArea txtContent;
    private final JComboBox<String> cbRating;
    private final JComboBox<String> cbCategory;
    private final DefaultTableModel historyModel;

    public HoiVienGopYPanel() {
        setLayout(new BorderLayout(18, 18));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. TOP HEADER
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("ĐÁNH GIÁ TRẢI NGHIỆM & GÓP Ý PHÒNG TẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Mọi ý kiến của bạn đều giúp FitLife hoàn thiện chất lượng dịch vụ mỗi ngày");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        topHeader.add(titleGroup, BorderLayout.WEST);

        add(topHeader, BorderLayout.NORTH);

        // 2. MAIN CENTER: 2 CỘT (Cột trái: Form gửi đánh giá mới; Cột phải: Lịch sử các phản hồi đã gửi)
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 25, 0));
        mainContent.setOpaque(false);

        // === CỘT TRÁI: FORM GỬI GÓP Ý ===
        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 22, 20, 22)
        ));

        JLabel lblFTitle = new JLabel("GỬI PHẢN HỒI MỚI");
        lblFTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFTitle.setForeground(new Color(15, 23, 42));
        lblFTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblFTitle);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 15)));

        // Chọn danh mục
        JLabel lblCat = new JLabel("Danh mục góp ý:");
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCat.setForeground(new Color(71, 85, 105));
        lblCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblCat);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 5)));

        cbCategory = new JComboBox<>(new String[]{
                "Cơ sở vật chất & Máy tập",
                "Vệ sinh phòng tập & Phòng tắm",
                "Chất lượng Huấn luyện viên (PT)",
                "Thái độ phục vụ của Lễ tân",
                "Góp ý mở thêm lớp học mới",
                "Khác"
        });
        cbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cbCategory.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(cbCategory);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 14)));

        // Mức độ hài lòng (sao)
        JLabel lblRate = new JLabel("Đánh giá độ hài lòng:");
        lblRate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRate.setForeground(new Color(71, 85, 105));
        lblRate.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblRate);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 5)));

        cbRating = new JComboBox<>(new String[]{
                "5 sao - Rất tuyệt vời",
                "4 sao - Tốt",
                "3 sao - Bình thường",
                "2 sao - Cần cải thiện",
                "1 sao - Không hài lòng"
        });
        cbRating.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbRating.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cbRating.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(cbRating);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 14)));

        // Nội dung chi tiết
        JLabel lblContent = new JLabel("Nội dung phản hồi chi tiết:");
        lblContent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContent.setForeground(new Color(71, 85, 105));
        lblContent.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblContent);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 5)));

        txtContent = new JTextArea(5, 20);
        txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        JScrollPane scrollTxt = new JScrollPane(txtContent);
        scrollTxt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        scrollTxt.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(scrollTxt);
        pnlForm.add(Box.createRigidArea(new Dimension(0, 18)));

        // Nút gửi
        JButton btnSubmit = new JButton("Gửi Phản Hồi Đến Ban Quản Lý");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(37, 99, 235));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSubmit.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(btnSubmit);

        mainContent.add(pnlForm);

        // === CỘT PHẢI: LỊCH SỬ CÁC GÓP Ý ĐÃ GỬI ===
        JPanel pnlHistory = new JPanel(new BorderLayout(0, 10));
        pnlHistory.setBackground(Color.WHITE);
        pnlHistory.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 22, 20, 22)
        ));

        JLabel lblHTitle = new JLabel("LỊCH SỬ CÁC PHẢN HỒI BẠN ĐÃ GỬI");
        lblHTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHTitle.setForeground(new Color(15, 23, 42));
        pnlHistory.add(lblHTitle, BorderLayout.NORTH);

        String[] cols = {"Ngày Gửi", "Danh Mục", "Đánh Giá", "Trạng Thái Xử Lý"};
        Object[][] data = {
                {"20/09/2026", "Cơ sở vật chất", "5 sao", "Đã tiếp nhận"},
                {"12/08/2026", "HLV (PT)", "5 sao", "Quản lý đã cảm ơn"},
                {"05/07/2026", "Vệ sinh phòng tập", "4 sao", "Đã xử lý & dọn dẹp"}
        };
        historyModel = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(historyModel);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlHistory.add(scrollTable, BorderLayout.CENTER);

        mainContent.add(pnlHistory);
        add(mainContent, BorderLayout.CENTER);

        // Event
        btnSubmit.addActionListener(e -> {
            String text = txtContent.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung góp ý!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cat = (String) cbCategory.getSelectedItem();
            String rate = (String) cbRating.getSelectedItem();
            String stars = rate.contains("5") ? "5 sao" : rate.contains("4") ? "4 sao" : "3 sao";
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            historyModel.insertRow(0, new Object[]{date, cat, stars, "Đang xử lý"});
            txtContent.setText("");

            JOptionPane.showMessageDialog(this,
                    "Cảm ơn bạn! Ban Quản lý FitLife đã tiếp nhận phản hồi và sẽ phản hồi sớm nhất.",
                    "Gửi Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
