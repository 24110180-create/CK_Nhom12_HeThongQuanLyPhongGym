package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.DangKyLopDAO;
import com.nhom12.gym.dao.LopHocDAO;
import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.entity.LopHoc;
import com.nhom12.gym.service.DataSyncManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
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
    private final LopHocDAO lopHocDAO = new LopHocDAO();
    private final DangKyLopDAO dangKyLopDAO = new DangKyLopDAO();

    private JTable classTable;
    private DefaultTableModel classModel;
    private JTable studentTable;
    private DefaultTableModel studentModel;
    private JButton btnDiemDanh;
    private JButton btnDiemDanhAll;
    private JLabel lblClassTitle;

    private String currentMaLop = "";

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
        splitPane.setDividerLocation(540);
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

        JLabel lblLeftHeader = new JLabel("Danh Sách Các Ca Lớp Học Nhóm (Đang Vận Hành)");
        lblLeftHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLeftHeader.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblLeftHeader, BorderLayout.NORTH);

        String[] classCols = new String[]{"Mã Lớp", "Tên Lớp", "HLV Phụ Trách", "Lịch Học", "Sĩ Số"};
        classModel = new DefaultTableModel(classCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        classTable = new JTable(classModel);
        classTable.setRowHeight(40);
        classTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

        lblClassTitle = new JLabel("Danh Sách Học Viên Của Lớp");
        lblClassTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblClassTitle.setForeground(new Color(30, 41, 59));
        rightPanel.add(lblClassTitle, BorderLayout.NORTH);

        String[] studentCols = new String[]{"ID ĐK", "Mã HV", "Tên Học Viên", "Trạng Thái Điểm Danh"};
        studentModel = new DefaultTableModel(studentCols, 0) {
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
                int row = classTable.getSelectedRow();
                String maLop = classModel.getValueAt(row, 0).toString();
                String tenLop = classModel.getValueAt(row, 1).toString();
                currentMaLop = maLop;
                lblClassTitle.setText("Danh Sách Học Viên Lớp: " + tenLop + " (" + maLop + ")");
                loadStudentsForClass(maLop);
            }
        });

        btnDiemDanh.addActionListener(e -> xuLyDiemDanhChon());
        btnDiemDanhAll.addActionListener(e -> xuLyDiemDanhTatCa());

        // Tải dữ liệu ban đầu
        loadClasses();

        // Lắng nghe sự kiện đồng bộ toàn hệ thống
        DataSyncManager.addListener(type -> {
            if (type == DataSyncManager.DataType.LOP_HOC || type == DataSyncManager.DataType.ALL) {
                int selClass = classTable.getSelectedRow();
                loadClasses();
                if (selClass >= 0 && selClass < classTable.getRowCount()) {
                    classTable.setRowSelectionInterval(selClass, selClass);
                }
            }
        });
    }

    private void loadClasses() {
        classModel.setRowCount(0);
        List<LopHoc> classes = lopHocDAO.layTatCa();
        if (classes != null) {
            for (LopHoc lh : classes) {
                classModel.addRow(new Object[]{
                        lh.getMaLop(),
                        lh.getTenLop(),
                        lh.getHuanLuyenVien(),
                        lh.getThoiGian(),
                        lh.getSiSoHienTai() + " / " + lh.getSiSoToiDa()
                });
            }
        }
        if (classModel.getRowCount() > 0 && classTable.getSelectedRow() == -1) {
            classTable.setRowSelectionInterval(0, 0);
        }
    }

    private void loadStudentsForClass(String maLop) {
        studentModel.setRowCount(0);
        List<DangKyLop> list = dangKyLopDAO.layDanhSachTheoLop(maLop);
        if (list != null && !list.isEmpty()) {
            for (DangKyLop dk : list) {
                studentModel.addRow(new Object[]{
                        dk.getId(),
                        "HV" + dk.getMaHoiVien(),
                        dk.getTenHoiVien() != null ? dk.getTenHoiVien() : "Hội viên #" + dk.getMaHoiVien(),
                        dk.getTrangThaiDiemDanh() != null ? dk.getTrangThaiDiemDanh() : "Chưa điểm danh"
                });
            }
        } else {
            studentModel.addRow(new Object[]{
                    0, "---", "Chưa có học viên đăng ký lớp này", "---"
            });
        }
    }

    private void xuLyDiemDanhChon() {
        int sel = studentTable.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 học viên trong danh sách để điểm danh!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = studentModel.getValueAt(sel, 0);
        int id = (idObj instanceof Number) ? ((Number) idObj).intValue() : Integer.parseInt(idObj.toString());
        if (id <= 0) return;

        String cur = studentModel.getValueAt(sel, 3).toString();
        String nxt = cur.contains("Đã có mặt") ? "Chưa điểm danh" : "Đã có mặt ✔";

        dangKyLopDAO.capNhatDiemDanh(id, nxt);
        studentModel.setValueAt(nxt, sel, 3);
        String name = studentModel.getValueAt(sel, 2).toString();
        JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái điểm danh của học viên [" + name + "] sang: " + nxt, "Điểm Danh Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyDiemDanhTatCa() {
        if (studentModel.getRowCount() == 0) return;
        int count = 0;
        for (int i = 0; i < studentModel.getRowCount(); i++) {
            Object idObj = studentModel.getValueAt(i, 0);
            int id = (idObj instanceof Number) ? ((Number) idObj).intValue() : Integer.parseInt(idObj.toString());
            if (id > 0) {
                dangKyLopDAO.capNhatDiemDanh(id, "Đã có mặt ✔");
                studentModel.setValueAt("Đã có mặt ✔", i, 3);
                count++;
            }
        }
        JOptionPane.showMessageDialog(this, "Đã điểm danh CÓ MẶT cho toàn bộ " + count + " học viên trong lớp!", "Điểm Danh Toàn Bộ", JOptionPane.INFORMATION_MESSAGE);
    }
}
