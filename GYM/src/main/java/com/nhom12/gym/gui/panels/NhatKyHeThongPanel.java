package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.NhatKyHeThongDAO;
import com.nhom12.gym.entity.NhatKyHeThong;
import com.nhom12.gym.util.ExcelExportHelper;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Panel Nhật Ký Hệ Thống & Kiểm Toán Thao Tác (Audit Log)
 * Dành riêng cho vai trò Chủ phòng tập để giám sát toàn bộ hoạt động tạo/xóa hóa đơn,
 * thêm/xóa hội viên và tạo tài khoản của nhân sự, chống thất thoát doanh thu.
 */
public class NhatKyHeThongPanel extends JPanel {

    private final NhatKyHeThongDAO nhatKyDAO = new NhatKyHeThongDAO();
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

    private DefaultTableModel tableModel;
    private JTable tableLogs;
    private JTextField txtSearchUser;
    private JComboBox<String> cbActionFilter;
    private JComboBox<String> cbTimeFilter;
    private JLabel lblTotalRecords;

    public NhatKyHeThongPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("NHẬT KÝ THAO TÁC HỆ THỐNG (AUDIT LOG)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Giám sát và kiểm toán toàn bộ hoạt động tạo/xóa hóa đơn, hội viên và phân quyền nhân sự");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content Box
        JPanel contentCard = new JPanel(new BorderLayout(12, 12));
        contentCard.setBackground(Color.WHITE);
        contentCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Filter / Search Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setOpaque(false);

        JLabel lblSearch = new JLabel("Tên đăng nhập:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(new Color(51, 65, 85));

        txtSearchUser = new JTextField(12);
        txtSearchUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearchUser.putClientProperty("JTextField.placeholderText", "admin, letan1...");

        JLabel lblAction = new JLabel("Hành động:");
        lblAction.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAction.setForeground(new Color(51, 65, 85));

        cbActionFilter = new JComboBox<>(new String[]{
                "Tất cả hành động",
                "Tạo hóa đơn",
                "Thêm hội viên mới",
                "Xóa hội viên",
                "Tạo tài khoản"
        });
        cbActionFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblTime = new JLabel("Thời gian:");
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTime.setForeground(new Color(51, 65, 85));

        cbTimeFilter = new JComboBox<>(new String[]{
                "Tất cả thời gian",
                "Hôm nay",
                "7 ngày gần nhất",
                "30 ngày gần nhất"
        });
        cbTimeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnFilter = new JButton("Lọc Dữ Liệu");
        btnFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFilter.setBackground(new Color(37, 99, 235));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.addActionListener(e -> taiDuLieu());

        JButton btnReset = new JButton("Làm Mới");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReset.setBackground(new Color(241, 245, 249));
        btnReset.setForeground(new Color(51, 65, 85));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            txtSearchUser.setText("");
            cbActionFilter.setSelectedIndex(0);
            cbTimeFilter.setSelectedIndex(0);
            taiDuLieu();
        });

        JButton btnExportExcel = new JButton("Xuất Excel");
        btnExportExcel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportExcel.setBackground(new Color(16, 185, 129));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportExcel.addActionListener(e -> {
            ExcelExportHelper.exportTableToExcel(this, tableLogs, "Nhat_Ky_He_Thong");
        });

        toolbar.add(lblSearch);
        toolbar.add(txtSearchUser);
        toolbar.add(lblAction);
        toolbar.add(cbActionFilter);
        toolbar.add(lblTime);
        toolbar.add(cbTimeFilter);
        toolbar.add(btnFilter);
        toolbar.add(btnReset);
        toolbar.add(btnExportExcel);

        contentCard.add(toolbar, BorderLayout.NORTH);

        // Bảng dữ liệu log
        String[] cols = {"Mã Log", "Thời Gian", "Tên Đăng Nhập", "Vai Trò", "Hành Động", "Chi Tiết Thao Tác"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableLogs = new JTable(tableModel);
        tableLogs.setRowHeight(32);
        tableLogs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableLogs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableLogs.getTableHeader().setBackground(new Color(241, 245, 249));
        tableLogs.getTableHeader().setForeground(new Color(30, 41, 59));

        tableLogs.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableLogs.getColumnModel().getColumn(1).setPreferredWidth(140);
        tableLogs.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableLogs.getColumnModel().getColumn(3).setPreferredWidth(90);
        tableLogs.getColumnModel().getColumn(4).setPreferredWidth(130);
        tableLogs.getColumnModel().getColumn(5).setPreferredWidth(350);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableLogs.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableLogs.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tableLogs.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableLogs.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tableLogs);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        contentCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom status
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(8, 5, 0, 5));

        lblTotalRecords = new JLabel("Tổng số bản ghi: 0");
        lblTotalRecords.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalRecords.setForeground(new Color(100, 116, 139));
        bottomBar.add(lblTotalRecords, BorderLayout.WEST);

        contentCard.add(bottomBar, BorderLayout.SOUTH);
        add(contentCard, BorderLayout.CENTER);

        // Nạp dữ liệu ban đầu
        taiDuLieu();
    }

    private void taiDuLieu() {
        tableModel.setRowCount(0);
        String keyword = txtSearchUser.getText().trim();
        List<NhatKyHeThong> list;

        if (keyword.isEmpty()) {
            list = nhatKyDAO.layTatCa();
        } else {
            list = nhatKyDAO.timTheoTenDangNhap(keyword);
        }

        String selectedAction = (String) cbActionFilter.getSelectedItem();
        String selectedTime = (String) cbTimeFilter.getSelectedItem();

        Calendar cal = Calendar.getInstance();
        Date now = new Date();

        int count = 0;
        for (NhatKyHeThong log : list) {
            // Lọc theo hành động
            if (selectedAction != null && !"Tất cả hành động".equals(selectedAction)) {
                if (log.getHanhDong() == null || !log.getHanhDong().equalsIgnoreCase(selectedAction)) {
                    continue;
                }
            }

            // Lọc theo khoảng thời gian
            if (selectedTime != null && !"Tất cả thời gian".equals(selectedTime) && log.getThoiGian() != null) {
                long diffMs = now.getTime() - log.getThoiGian().getTime();
                long diffDays = diffMs / (1000L * 60 * 60 * 24);

                if ("Hôm nay".equals(selectedTime)) {
                    cal.setTime(now);
                    int todayYear = cal.get(Calendar.YEAR);
                    int todayDay = cal.get(Calendar.DAY_OF_YEAR);
                    cal.setTime(log.getThoiGian());
                    if (cal.get(Calendar.YEAR) != todayYear || cal.get(Calendar.DAY_OF_YEAR) != todayDay) {
                        continue;
                    }
                } else if ("7 ngày gần nhất".equals(selectedTime)) {
                    if (diffDays > 7) continue;
                } else if ("30 ngày gần nhất".equals(selectedTime)) {
                    if (diffDays > 30) continue;
                }
            }

            tableModel.addRow(new Object[]{
                    log.getMaNhatKy(),
                    log.getThoiGian() != null ? sdf.format(log.getThoiGian()) : "—",
                    log.getTenDangNhap(),
                    log.getVaiTro() != null ? log.getVaiTro() : "—",
                    log.getHanhDong(),
                    log.getChiTiet()
            });
            count++;
        }

        lblTotalRecords.setText("Tổng số bản ghi: " + count + " thao tác");
    }
}
