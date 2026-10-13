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
        topToolBar.add(cbWeek);

        btnExport = new JButton("Xuất File Excel Bảng Phân Ca");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExport.setBackground(new Color(16, 185, 129));
        btnExport.setForeground(Color.WHITE);
        topToolBar.add(btnExport);

        btnSave = new JButton("Lưu Bảng Phân Ca");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        topToolBar.add(btnSave);

        centerPanel.add(topToolBar, BorderLayout.NORTH);

        // Schedule Table
        String[] cols = new String[]{"Ca Làm Việc", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};
        Object[][] data = new Object[][]{
                {"Ca Sáng (06:00 - 14:00)", "Nguyễn Thị Linh", "Nguyễn Thị Linh", "Nguyễn Thị Linh", "Trần Thị Mai", "Nguyễn Thị Linh", "Trần Thị Mai", "Nghỉ Luân Phiên"},
                {"Ca Chiều (14:00 - 22:00)", "Trần Thị Mai", "Trần Thị Mai", "Nguyễn Thị Linh", "Nguyễn Thị Linh", "Trần Thị Mai", "Nguyễn Thị Linh", "Nguyễn Thị Linh"},
                {"HLV Trực Sàn (Theo Ca)", "Trần Tuấn Kiệt", "Nguyễn Thu Thảo", "Phan Thanh Sơn", "Lê Mai Hương", "Phạm Quốc Bảo", "Cả 2 HLV", "Trần Tuấn Kiệt"}
        };
        model = new DefaultTableModel(data, cols);
        table = new JTable(model);
        table.setRowHeight(90);
        table.setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(37, 99, 235));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(254, 240, 138));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                if (column == 0) {
                    setBackground(new Color(241, 245, 249));
                    setForeground(new Color(30, 41, 59));
                } else {
                    setBackground(isSelected ? new Color(254, 240, 138) : Color.WHITE);
                    if (value != null && value.toString().contains("Nghỉ")) {
                        setForeground(new Color(239, 68, 68));
                    } else {
                        setForeground(new Color(37, 99, 235));
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
}
