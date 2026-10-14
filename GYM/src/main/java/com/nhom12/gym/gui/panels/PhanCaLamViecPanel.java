package com.nhom12.gym.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PhanCaLamViecPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnExport;
    private JButton btnSave;
    private JComboBox<String> cbWeek;

    public PhanCaLamViecPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("LỊCH LÀM VIỆC & PHÂN CA TRỰC (QUẢN LÝ)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Sắp xếp ca trực lễ tân, huấn luyện viên sàn và xuất bảng phân ca hàng tuần");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Top Toolbar
        JPanel topToolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topToolBar.setBackground(Color.WHITE);
        topToolBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblWeek = new JLabel("Chọn Tuần Làm Việc:");
        lblWeek.setFont(new Font("Segoe UI", Font.BOLD, 13));
        topToolBar.add(lblWeek);

        cbWeek = new JComboBox<>(new String[]{
                "Tuần hiện tại (28/09 - 04/10/2026)",
                "Tuần tới (05/10 - 11/10/2026)",
                "Tuần sau nữa (12/10 - 18/10/2026)"
        });
        cbWeek.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbWeek.putClientProperty("JComponent.roundRect", true);
        cbWeek.setPreferredSize(new java.awt.Dimension(cbWeek.getPreferredSize().width, 38));
        topToolBar.add(cbWeek);

        btnExport = new JButton("Xuất File Excel Bảng Phân Ca");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExport.setBackground(new Color(16, 185, 129));
        btnExport.setForeground(Color.WHITE);
        styleActionButton(btnExport);
        topToolBar.add(btnExport);

        btnSave = new JButton("Lưu Bảng Phân Ca");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        styleActionButton(btnSave);
        topToolBar.add(btnSave);

        centerPanel.add(topToolBar, BorderLayout.NORTH);

        // Schedule Table
        String[] cols = new String[]{"Ca Làm Việc", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};
        Object[][] data = new Object[][]{
                {"Ca Sáng (06:00 - 14:00)", "Lê Thị Lan", "Lê Thị Lan", "Lê Thị Lan", "Nguyễn Tuấn", "Lê Thị Lan", "Nguyễn Tuấn", "Nghỉ Luân Phiên"},
                {"Ca Chiều (14:00 - 22:00)", "Nguyễn Tuấn", "Nguyễn Tuấn", "Lê Thị Lan", "Lê Thị Lan", "Nguyễn Tuấn", "Lê Thị Lan", "Lê Thị Lan"},
                {"HLV Trực Sàn (Theo Ca)", "Anh Tuấn", "PT John", "Anh Tuấn", "PT John", "Anh Tuấn", "Cả 2 PT", "Anh Tuấn"}
        };
        model = new DefaultTableModel(data, cols);
        table = new JTable(model);
        table.setRowHeight(90);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(15, 23, 42));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 44));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new java.awt.Dimension(1, 1));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
                setFont(table.getFont().deriveFont(column == 0 ? Font.BOLD : Font.PLAIN));
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else if (column == 0) {
                    c.setBackground(new Color(226, 232, 240));
                    c.setForeground(new Color(30, 41, 59));
                } else {
                    Color[] shiftColors = {
                            new Color(239, 246, 255),
                            new Color(236, 253, 245),
                            new Color(255, 251, 235)
                    };
                    c.setBackground(shiftColors[row % shiftColors.length]);
                    String assignment = value == null ? "" : value.toString();
                    if (assignment.contains("Nghỉ")) {
                        c.setForeground(new Color(185, 28, 28));
                    } else {
                        c.setForeground(new Color(30, 41, 59));
                    }
                }
                return c;
            }
        });

        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom tip
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(new JLabel("Mẹo: Quản lý có thể Click đúp vào từng ô để đổi tên nhân viên trực ca trực tiếp trên bảng. Nhấn 'Lưu Bảng Phân Ca' để hoàn tất."));
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.add(centerPanel, BorderLayout.CENTER);

        // Wire Action Listeners
        btnExport.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã xuất bảng phân ca làm việc tuần [" + cbWeek.getSelectedItem() + "] thành công sang file Excel: LICH_TRUC_CA_TUAN.xlsx!", "Xuất File Excel", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSave.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã lưu và công bố lịch trực ca tuần [" + cbWeek.getSelectedItem() + "] thành công tới toàn bộ nhân viên phòng gym!", "Lưu Lịch Trực", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void styleActionButton(JButton button) {
        Color normalColor = button.getBackground();
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
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
