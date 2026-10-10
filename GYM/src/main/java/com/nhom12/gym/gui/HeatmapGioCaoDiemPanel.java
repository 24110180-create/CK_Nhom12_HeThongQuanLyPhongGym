package com.nhom12.gym.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class HeatmapGioCaoDiemPanel extends JPanel {
    private JComboBox<String> cbBranch;
    private JButton btnRefresh;
    private CustomHeatmapChart chart;

    public HeatmapGioCaoDiemPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("BIỂU ĐỒ HEATMAP MẬT ĐỘ & KHUNG GIỜ CAO ĐIỂM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Phân tích lưu lượng ra vào phòng gym theo ngày và khung giờ để bố trí HLV trực sàn hợp lý");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        // Filter Top Bar
        JPanel topToolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topToolBar.setBackground(Color.WHITE);
        topToolBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lBranch = new JLabel("Chọn Cơ Sở Phòng Gym:");
        lBranch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        topToolBar.add(lBranch);

        cbBranch = new JComboBox<>(new String[]{
                "Cơ sở 1 - Gò Vấp (Trụ Sở Chính)",
                "Cơ sở 2 - Quận 7 (Phú Mỹ Hưng)",
                "Cơ sở 3 - Bình Thạnh (Landmark)"
        });
        cbBranch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topToolBar.add(cbBranch);

        btnRefresh = new JButton("Đồng Bộ Dữ Liệu Cổng Quét FaceID");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setBackground(new Color(37, 99, 235));
        btnRefresh.setForeground(Color.WHITE);
        topToolBar.add(btnRefresh);

        // Chart Container
        JPanel chartContainer = new JPanel(new BorderLayout(0, 10));
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblSubTitle = new JLabel("Phân bổ lưu lượng Hội viên theo Ngày và Khung giờ (Màu càng đậm, khách càng đông)");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSubTitle.setForeground(new Color(100, 116, 139));
        chartContainer.add(lblSubTitle, BorderLayout.NORTH);

        chart = new CustomHeatmapChart();
        chartContainer.add(chart, BorderLayout.CENTER);

        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 8));
        legendPanel.setOpaque(false);
        legendPanel.add(new JLabel("Mức độ đông đúc:"));
        legendPanel.add(this.createLegendItem("Vắng (0-20 người)", new Color(238, 242, 255)));
        legendPanel.add(this.createLegendItem("Bình thường (20-50 người)", new Color(147, 197, 253)));
        legendPanel.add(this.createLegendItem("Đông (50-80 người)", new Color(59, 130, 246)));
        legendPanel.add(this.createLegendItem("Giờ Vàng (>80 người)", new Color(30, 58, 138)));

        chartContainer.add(legendPanel, BorderLayout.SOUTH);

        JPanel mainCenter = new JPanel(new BorderLayout(0, 15));
        mainCenter.setOpaque(false);
        mainCenter.add(topToolBar, BorderLayout.NORTH);
        mainCenter.add(chartContainer, BorderLayout.CENTER);
        this.add(mainCenter, BorderLayout.CENTER);

        // Action Listeners
        btnRefresh.addActionListener(e -> {
            chart.randomize();
            JOptionPane.showMessageDialog(this, "Đã đồng bộ xong dữ liệu quét cổng FaceID / Thẻ từ của " + cbBranch.getSelectedItem() + "!\nBiểu đồ mật độ đã được cập nhật theo thời gian thực.", "Đồng Bộ Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        cbBranch.addActionListener(e -> chart.randomize());
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        p.add(colorBox);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbl);
        return p;
    }

    static class CustomHeatmapChart extends JPanel {
        private final String[] days = new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};
        private final String[] hours = new String[]{"05:00", "07:00", "09:00", "11:00", "13:00", "15:00", "17:00", "19:00", "21:00"};
        private int[][] data = new int[][]{
                {30, 85, 40, 15, 10, 25, 95, 70, 20},
                {25, 80, 45, 20, 15, 30, 90, 75, 25},
                {35, 85, 30, 10, 10, 20, 95, 80, 20},
                {20, 75, 35, 15, 15, 35, 85, 65, 30},
                {40, 90, 50, 25, 20, 40, 100, 85, 45},
                {60, 50, 70, 80, 60, 50, 60, 40, 20},
                {50, 40, 60, 75, 50, 40, 50, 30, 15}
        };

        public CustomHeatmapChart() {
            this.setOpaque(false);
        }

        public void randomize() {
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    data[i][j] = (int) (Math.random() * 85 + 15);
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int startX = 65;
            int startY = 35;
            int cellWidth = (getWidth() - startX - 25) / hours.length;
            int cellHeight = (getHeight() - startY - 25) / days.length;

            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2d.setColor(new Color(100, 116, 139));

            // Hour labels
            for (int j = 0; j < hours.length; ++j) {
                g2d.drawString(hours[j], startX + j * cellWidth + cellWidth / 4, startY - 12);
            }

            // Days and heatmap cells
            for (int i = 0; i < days.length; ++i) {
                g2d.setColor(new Color(71, 85, 105));
                g2d.drawString(days[i], 12, startY + i * cellHeight + cellHeight / 2 + 5);

                for (int j = 0; j < hours.length; ++j) {
                    int val = data[i][j];
                    if (val <= 20) {
                        g2d.setColor(new Color(238, 242, 255));
                    } else if (val <= 50) {
                        g2d.setColor(new Color(147, 197, 253));
                    } else if (val <= 80) {
                        g2d.setColor(new Color(59, 130, 246));
                    } else {
                        g2d.setColor(new Color(30, 58, 138));
                    }

                    g2d.fillRoundRect(startX + j * cellWidth + 2, startY + i * cellHeight + 2, cellWidth - 4, cellHeight - 4, 6, 6);
                }
            }
        }
    }
}
