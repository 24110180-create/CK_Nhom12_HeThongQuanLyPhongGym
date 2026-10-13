package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.DangKyLopDAO;
import com.nhom12.gym.dao.LopHocDAO;
import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.entity.LopHoc;
import com.nhom12.gym.service.DataSyncManager;
import com.nhom12.gym.util.Auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Giao diện Đặt Chỗ Lớp Học (Group Fitness) dành cho Hội Viên
 * Kết nối dữ liệu thực tế và tự động đồng bộ thời gian thực với HLV & Lễ tân
 */
public class HoiVienDatLopPanel extends JPanel {

    private final LopHocDAO lopHocDAO = new LopHocDAO();
    private final DangKyLopDAO dangKyLopDAO = new DangKyLopDAO();

    private final JTable table;
    private final DefaultTableModel model;
    private final JComboBox<String> cbFilter;
    private final Set<String> myBookedClasses = new HashSet<>();
    private final JLabel lblSummary;

    public HoiVienDatLopPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. TOP HEADER & FILTER
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGroup.setOpaque(false);
        JLabel lblTitle = new JLabel("ĐẶT CHỖ LỚP HỌC NHÓM (GROUP FITNESS)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Lựa chọn lớp học yêu thích trong tuần và giữ chỗ trước khi đến phòng tập");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        titleGroup.add(lblTitle);
        titleGroup.add(lblSub);
        topPanel.add(titleGroup, BorderLayout.WEST);

        // Filter Controls
        JPanel filterGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterGroup.setOpaque(false);

        JLabel lblF = new JLabel("Lọc bộ môn:");
        lblF.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFilter = new JComboBox<>(new String[]{"Tất cả bộ môn", "Yoga", "Zumba", "Boxing", "Cardio", "Pilates"});
        cbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFilter.setPreferredSize(new Dimension(170, 36));

        JButton btnMyClasses = new JButton("Lớp Của Tôi Đã Đặt");
        btnMyClasses.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnMyClasses.setBackground(new Color(241, 245, 249));
        btnMyClasses.setForeground(new Color(30, 41, 59));
        btnMyClasses.setFocusPainted(false);
        btnMyClasses.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMyClasses.setPreferredSize(new Dimension(160, 36));

        filterGroup.add(lblF);
        filterGroup.add(cbFilter);
        filterGroup.add(btnMyClasses);
        topPanel.add(filterGroup, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 2. DATA TABLE
        String[] cols = {"Mã Lớp", "Tên Lớp Học", "Bộ Môn", "Huấn Luyện Viên", "Thời Gian", "Phòng Tập", "Chỗ Đã Đặt", "Trạng Thái Của Bạn"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(30, 41, 59));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Highlight column Trạng thái
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String s = (val != null) ? val.toString() : "";
                if (s.contains("Đã giữ chỗ")) {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    l.setForeground(new Color(100, 116, 139));
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }
                return l;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. BOTTOM ACTIONS & INFO BAR
        JPanel bottomBar = new JPanel(new BorderLayout(15, 0));
        bottomBar.setOpaque(false);

        lblSummary = new JLabel("● Đang hiển thị lớp học khả dụng trong tuần");
        lblSummary.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSummary.setForeground(new Color(100, 116, 139));
        bottomBar.add(lblSummary, BorderLayout.WEST);

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionGroup.setOpaque(false);

        JButton btnBook = new JButton("Đặt Chỗ Lớp Được Chọn");
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBook.setBackground(new Color(37, 99, 235));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBook.setPreferredSize(new Dimension(200, 40));

        JButton btnCancel = new JButton("Hủy Đặt Chỗ");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBackground(new Color(239, 68, 68));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setPreferredSize(new Dimension(140, 40));

        actionGroup.add(btnBook);
        actionGroup.add(btnCancel);
        bottomBar.add(actionGroup, BorderLayout.EAST);

        add(bottomBar, BorderLayout.SOUTH);

        // Events
        btnBook.addActionListener(e -> xuLyDatCho());
        btnCancel.addActionListener(e -> xuLyHuyDatCho());

        cbFilter.addActionListener(e -> {
            String selected = (String) cbFilter.getSelectedItem();
            if ("Tất cả bộ môn".equals(selected)) {
                loadInitialData();
            } else {
                filterBySubject(selected);
            }
        });

        btnMyClasses.addActionListener(e -> {
            if (myBookedClasses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn chưa đăng ký lớp học nào trong tuần này!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            filterMyClasses();
        });

        loadInitialData();

        DataSyncManager.getInstance().addListener(evt -> {
            if (evt.isAffecting(DataSyncManager.DataType.LOP_HOC)) {
                loadInitialData();
            }
        });
    }

    private void xuLyDatCho() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học trên danh sách để đặt chỗ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String classId = table.getValueAt(row, 0).toString();
        String className = table.getValueAt(row, 1).toString();
        String status = table.getValueAt(row, 7).toString();

        if (myBookedClasses.contains(classId) || status.contains("Đã giữ chỗ")) {
            JOptionPane.showMessageDialog(this, "Bạn đã đăng ký giữ chỗ lớp này rồi!", "Đã đăng ký", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String hoTen = Auth.hoTen != null ? Auth.hoTen : "Lê Anh Duy";
        String sdt = Auth.soDienThoai != null ? Auth.soDienThoai : "0974482749";
        int myId = 1;

        DangKyLop dk = new DangKyLop(0, classId, myId, hoTen, sdt, new Date(), "Chưa điểm danh", "Đặt chỗ qua App Hội Viên");
        boolean ok = dangKyLopDAO.them(dk);
        if (ok) {
            myBookedClasses.add(classId);
            loadInitialData();

            JOptionPane.showMessageDialog(this,
                    "<html><body style='width: 280px; padding: 5px;'>" +
                    "<h3 style='color: #2563eb; margin: 0;'>ĐẶT CHỖ THÀNH CÔNG!</h3>" +
                    "<p>Lớp: <b>" + className + "</b></p>" +
                    "<p style='color: #10b981;'>Hệ thống đã tự động đồng bộ sang HLV và Lễ Tân phòng gym.</p>" +
                    "</body></html>",
                    "Xác Nhận Đặt Chỗ", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu đăng ký lớp vào hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyDatCho() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học muốn hủy đăng ký!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String classId = table.getValueAt(row, 0).toString();
        String status = table.getValueAt(row, 7).toString();

        if (!myBookedClasses.contains(classId) && !status.contains("Đã giữ chỗ")) {
            JOptionPane.showMessageDialog(this, "Bạn chưa đăng ký lớp này nên không thể hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn hủy đăng ký lớp " + table.getValueAt(row, 1) + "?",
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int myId = 1;
            dangKyLopDAO.xoaTheoLopVaHoiVien(classId, myId);
            myBookedClasses.remove(classId);
            loadInitialData();

            JOptionPane.showMessageDialog(this, "Đã hủy giữ chỗ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadInitialData() {
        model.setRowCount(0);
        myBookedClasses.clear();
        String hoTen = Auth.hoTen != null ? Auth.hoTen : "Lê Anh Duy";
        int myId = 1;

        List<LopHoc> list = lopHocDAO.layTatCa();
        if (list != null) {
            for (LopHoc lh : list) {
                boolean booked = dangKyLopDAO.daDangKy(lh.getMaLop(), myId);
                if (!booked) {
                    List<DangKyLop> dks = dangKyLopDAO.layDanhSachTheoLop(lh.getMaLop());
                    for (DangKyLop d : dks) {
                        if (d.getTenHoiVien() != null && d.getTenHoiVien().equalsIgnoreCase(hoTen)) {
                            booked = true;
                            break;
                        }
                    }
                }
                if (booked) {
                    myBookedClasses.add(lh.getMaLop());
                }
                model.addRow(new Object[]{
                        lh.getMaLop(),
                        lh.getTenLop(),
                        lh.getBoMon() != null ? lh.getBoMon() : "Gym",
                        lh.getHuanLuyenVien() != null ? lh.getHuanLuyenVien() : "HLV Chuyên Nghiệp",
                        lh.getThoiGian() != null ? lh.getThoiGian() : "08:00 - 09:00",
                        lh.getPhongTap() != null ? lh.getPhongTap() : "Studio 1",
                        lh.getSiSoHienTai() + "/" + lh.getSiSoToiDa(),
                        booked ? "✔ Đã giữ chỗ" : "Chưa đặt"
                });
            }
        }
        lblSummary.setText("● Đang hiển thị " + model.getRowCount() + " lớp học khả dụng trong tuần");
    }

    private void filterBySubject(String subject) {
        loadInitialData();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            String s = model.getValueAt(i, 2).toString();
            if (!s.toLowerCase().contains(subject.toLowerCase())) {
                model.removeRow(i);
            }
        }
        lblSummary.setText("● Đã lọc theo bộ môn: " + subject + " (" + model.getRowCount() + " lớp)");
    }

    private void filterMyClasses() {
        loadInitialData();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            String id = model.getValueAt(i, 0).toString();
            if (!myBookedClasses.contains(id)) {
                model.removeRow(i);
            }
        }
        lblSummary.setText("● Danh sách " + model.getRowCount() + " lớp bạn đã đăng ký");
    }
}
