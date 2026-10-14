package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.DangKyLopDAO;
import com.nhom12.gym.dao.LopHocDAO;
import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.entity.LopHoc;
import com.nhom12.gym.service.DataSyncManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
    private JButton btnGhiChu;
    private JButton btnDiemDanhAll;
    private JLabel lblClassTitle;
    private JComboBox<String> cbFilterHLV;

    private String currentMaLop = "";

    public LichDayHLVPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        String hlvName = (com.nhom12.gym.util.Auth.hoTen != null && !com.nhom12.gym.util.Auth.hoTen.isEmpty())
                ? com.nhom12.gym.util.Auth.hoTen : "Trần Tuấn Kiệt";

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("LỊCH DẠY & ĐIỂM DANH LỚP HỌC (TRẠM HUẤN LUYỆN VIÊN)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("HLV Đang Đăng Nhập: " + hlvName + " • Quản lý sĩ số lớp, điểm danh và ghi chép nhận xét thể trạng học viên");
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

        JPanel leftTop = new JPanel(new BorderLayout(0, 6));
        leftTop.setOpaque(false);
        JLabel lblLeftHeader = new JLabel("Danh Sách Các Ca Lớp Học Nhóm (Đang Vận Hành)");
        lblLeftHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLeftHeader.setForeground(new Color(30, 41, 59));
        leftTop.add(lblLeftHeader, BorderLayout.NORTH);

        JPanel pFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pFilter.setOpaque(false);
        pFilter.add(new JLabel("Lọc theo HLV:"));
        cbFilterHLV = new JComboBox<>(new String[]{
                "Tất Cả Huấn Luyện Viên",
                "Trần Tuấn Kiệt",
                "Nguyễn Thu Thảo",
                "Phan Thanh Sơn",
                "Lê Mai Hương",
                "Phạm Quốc Bảo"
        });
        cbFilterHLV.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        // Tự động chọn theo tài khoản đăng nhập nếu là HLV
        for (int i = 0; i < cbFilterHLV.getItemCount(); i++) {
            if (cbFilterHLV.getItemAt(i).equalsIgnoreCase(hlvName)) {
                cbFilterHLV.setSelectedIndex(i);
                break;
            }
        }
        cbFilterHLV.addActionListener(e -> loadClasses());
        pFilter.add(cbFilterHLV);
        leftTop.add(pFilter, BorderLayout.SOUTH);
        leftPanel.add(leftTop, BorderLayout.NORTH);

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

        String[] studentCols = new String[]{"ID ĐK", "Mã HV", "Tên Học Viên", "Số ĐT", "Điểm Danh", "Ghi Chú Đánh Giá"};
        studentModel = new DefaultTableModel(studentCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        studentTable = new JTable(studentModel);
        studentTable.setRowHeight(38);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(new Color(241, 245, 249));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Action Buttons on Right Bottom
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setPreferredSize(new Dimension(0, 42));

        btnDiemDanh = new JButton("Điểm Danh Học Viên");
        btnDiemDanh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDiemDanh.setBackground(new Color(16, 185, 129));
        btnDiemDanh.setForeground(Color.WHITE);

        btnGhiChu = new JButton("Ghi Chú / Nhận Xét");
        btnGhiChu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGhiChu.setBackground(new Color(14, 165, 233));
        btnGhiChu.setForeground(Color.WHITE);

        btnDiemDanhAll = new JButton("Điểm Danh Tất Cả");
        btnDiemDanhAll.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDiemDanhAll.setBackground(new Color(37, 99, 235));
        btnDiemDanhAll.setForeground(Color.WHITE);

        btnPanel.add(btnDiemDanh);
        btnPanel.add(btnGhiChu);
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
                lblClassTitle.setText("Học Viên Lớp: " + tenLop + " (" + maLop + ")");
                loadStudentsForClass(maLop);
            }
        });

        btnDiemDanh.addActionListener(e -> xuLyDiemDanhChon());
        btnGhiChu.addActionListener(e -> xuLyGhiChuHocVien());
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
        String filterVal = (cbFilterHLV != null && cbFilterHLV.getSelectedItem() != null)
                ? cbFilterHLV.getSelectedItem().toString() : "Tất Cả Huấn Luyện Viên";

        if (classes != null) {
            for (LopHoc lh : classes) {
                if (!filterVal.startsWith("Tất Cả") && lh.getHuanLuyenVien() != null && !lh.getHuanLuyenVien().contains(filterVal)) {
                    continue;
                }
                classModel.addRow(new Object[]{
                        lh.getMaLop(),
                        lh.getTenLop(),
                        lh.getHuanLuyenVien(),
                        lh.getThoiGian(),
                        lh.getSiSoHienTai() + " / " + lh.getSiSoToiDa()
                });
            }
        }
        if (classModel.getRowCount() > 0) {
            classTable.setRowSelectionInterval(0, 0);
        } else {
            studentModel.setRowCount(0);
            lblClassTitle.setText("Không có lớp học nào phù hợp bộ lọc");
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
                        dk.getSoDienThoai() != null ? dk.getSoDienThoai() : "",
                        dk.getTrangThaiDiemDanh() != null ? dk.getTrangThaiDiemDanh() : "Chưa điểm danh",
                        dk.getGhiChu() != null ? dk.getGhiChu() : ""
                });
            }
        } else {
            studentModel.addRow(new Object[]{
                    0, "---", "Chưa có học viên đăng ký lớp này", "---", "---", "---"
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

        String cur = studentModel.getValueAt(sel, 4).toString();
        String nxt = cur.contains("Đã có mặt") || cur.contains("Đã điểm danh") ? "Chưa điểm danh" : "Đã có mặt";

        dangKyLopDAO.capNhatDiemDanh(id, nxt);
        studentModel.setValueAt(nxt, sel, 4);
        String name = studentModel.getValueAt(sel, 2).toString();
        DataSyncManager.fireDataChanged(DataSyncManager.DataType.LOP_HOC);
        JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái điểm danh của học viên [" + name + "] sang: " + nxt, "Điểm Danh Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuLyGhiChuHocVien() {
        int sel = studentTable.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 học viên trong danh sách để ghi chú!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idObj = studentModel.getValueAt(sel, 0);
        int id = (idObj instanceof Number) ? ((Number) idObj).intValue() : Integer.parseInt(idObj.toString());
        if (id <= 0) return;

        String curNote = studentModel.getValueAt(sel, 5) != null ? studentModel.getValueAt(sel, 5).toString() : "";
        String name = studentModel.getValueAt(sel, 2).toString();

        String newNote = JOptionPane.showInputDialog(this,
                "Nhập nhận xét / ghi chú tiến độ tập luyện cho học viên [" + name + "]:",
                curNote);

        if (newNote != null) {
            String trimmed = newNote.trim();
            if (trimmed.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nội dung ghi chú không được để trống!", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dangKyLopDAO.capNhatGhiChu(id, trimmed);
            studentModel.setValueAt(trimmed, sel, 5);
            JOptionPane.showMessageDialog(this, "Đã lưu nhận xét cho học viên [" + name + "]!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xuLyDiemDanhTatCa() {
        if (studentModel.getRowCount() == 0) return;
        int count = 0;
        for (int i = 0; i < studentModel.getRowCount(); i++) {
            Object idObj = studentModel.getValueAt(i, 0);
            int id = (idObj instanceof Number) ? ((Number) idObj).intValue() : Integer.parseInt(idObj.toString());
            if (id > 0) {
                dangKyLopDAO.capNhatDiemDanh(id, "Đã có mặt");
                studentModel.setValueAt("Đã có mặt", i, 4);
                count++;
            }
        }
        DataSyncManager.fireDataChanged(DataSyncManager.DataType.LOP_HOC);
        JOptionPane.showMessageDialog(this, "Đã điểm danh CÓ MẶT cho toàn bộ " + count + " học viên trong lớp!", "Điểm Danh Toàn Bộ", JOptionPane.INFORMATION_MESSAGE);
    }
}
