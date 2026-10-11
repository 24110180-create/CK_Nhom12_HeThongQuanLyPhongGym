package com.nhom12.gym.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class LichDayHLVPanel extends JPanel {
    private JTable classTable;
    private DefaultTableModel classModel;
    private JTable studentTable;
    private DefaultTableModel studentModel;
    private JButton btnDiemDanh;
    private JButton btnDiemDanhAll;
    private JLabel lblClassTitle;

    public LichDayHLVPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("LỊCH DẠY & ĐIỂM DANH LỚP HỌC (QUYỀN HUẤN LUYỆN VIÊN)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Theo dõi các ca dạy trong ngày, điểm danh sĩ số thực tế và đánh giá học viên tại lớp");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Panel (Class Schedule)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblLeftHeader = new JLabel("Lịch Dạy Hôm Nay Của Bạn");
        lblLeftHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLeftHeader.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblLeftHeader, BorderLayout.NORTH);

        String[] classCols = new String[]{"Ca Dạy", "Tên Lớp", "Phòng", "Sĩ Số"};
        Object[][] classData = new Object[][]{
                {"07:00 - 08:30", "Yoga Cơ Bản", "Phòng 01", "15/20"},
                {"09:00 - 10:30", "Zumba Dance", "Phòng 02", "18/25"},
                {"15:00 - 16:30", "Boxing PT 1-1", "Sàn Đấu", "1/1"},
                {"17:30 - 19:00", "Cardio Đốt Mỡ", "Phòng 01", "22/30"}
        };
        classModel = new DefaultTableModel(classData, classCols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        classTable = new JTable(classModel);
        classTable.setRowHeight(40);
        classTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        classTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        classTable.getTableHeader().setBackground(new Color(241, 245, 249));
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(classTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // Right Panel (Student Attendance)
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        lblClassTitle = new JLabel("Danh Sách Học Viên Lớp: Yoga Cơ Bản");
        lblClassTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblClassTitle.setForeground(new Color(30, 41, 59));
        rightPanel.add(lblClassTitle, BorderLayout.NORTH);

        String[] studentCols = new String[]{"Mã HV", "Tên Học Viên", "Trạng Thái Điểm Danh"};
        Object[][] studentData = new Object[][]{
                {"HV001", "Nguyễn Văn Tuấn", "Chưa điểm danh"},
                {"HV005", "Trần Thu Hà", "Đã có mặt ✔"},
                {"HV012", "Lê Hoàng Phúc", "Chưa điểm danh"},
                {"HV022", "Lê Thị Lan", "Đã có mặt ✔"},
                {"HV041", "Phạm Trọng Thủy", "Chưa điểm danh"}
        };
        studentModel = new DefaultTableModel(studentData, studentCols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        studentTable = new JTable(studentModel);
        studentTable.setRowHeight(38);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(new Color(241, 245, 249));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Action Buttons on Right Bottom
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setPreferredSize(new Dimension(0, 45));

        btnDiemDanh = new JButton("Điểm Danh Học Viên Đang Chọn");
        btnDiemDanh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDiemDanh.setBackground(new Color(16, 185, 129));
        btnDiemDanh.setForeground(Color.WHITE);

        btnDiemDanhAll = new JButton("Điểm Danh Có Mặt Tất Cả");
        btnDiemDanhAll.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDiemDanhAll.setBackground(new Color(37, 99, 235));
        btnDiemDanhAll.setForeground(Color.WHITE);

        btnPanel.add(btnDiemDanh);
        btnPanel.add(btnDiemDanhAll);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Action Listeners
        classTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && classTable.getSelectedRow() != -1) {
                String className = classModel.getValueAt(classTable.getSelectedRow(), 1).toString();
                lblClassTitle.setText("Danh Sách Học Viên Lớp: " + className);
            }
        });

        btnDiemDanh.addActionListener(e -> {
            int sel = studentTable.getSelectedRow();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên trong danh sách để điểm danh!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String cur = studentModel.getValueAt(sel, 2).toString();
            String nxt = cur.contains("Đã có mặt") ? "Chưa điểm danh" : "Đã có mặt ✔";
            studentModel.setValueAt(nxt, sel, 2);
            String name = studentModel.getValueAt(sel, 1).toString();
            JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái của học viên [" + name + "] sang: " + nxt, "Điểm Danh Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        btnDiemDanhAll.addActionListener(e -> {
            for (int i = 0; i < studentModel.getRowCount(); i++) {
                studentModel.setValueAt("Đã có mặt ✔", i, 2);
            }
            JOptionPane.showMessageDialog(this, "Đã điểm danh CÓ MẶT cho toàn bộ " + studentModel.getRowCount() + " học viên trong lớp!", "Điểm Danh Toàn Bộ", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
