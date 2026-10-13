package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.DangKyLopDAO;
import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.LopHocDAO;
import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.LopHoc;
import com.nhom12.gym.util.Auth;
import com.nhom12.gym.util.ExcelExportHelper;
import com.nhom12.gym.util.SoundHelper;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Phân Hệ Quản Lý Lịch Lớp Học & Điểm Danh Hội Viên Dành Cho Huấn Luyện Viên (HLV)
 * Bố cục Master - Detail phân cấp trực quan:
 * - Master (Tầng trên): Danh sách các lớp học nhóm, phân công HLV, phòng tập, sĩ số.
 * - Detail (Tầng dưới): Danh sách học viên thực tế đăng ký lớp, công cụ ghi danh, hủy đăng ký và điểm danh trực tiếp.
 */
public class LichLopHocPanel extends JPanel {

    // DAOs
    private final LopHocDAO lopHocDAO = new LopHocDAO();
    private final DangKyLopDAO dangKyLopDAO = new DangKyLopDAO();
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();

    // Dữ liệu bộ nhớ màn hình
    private List<LopHoc> danhSachLop = new ArrayList<>();
    private LopHoc lopDangChon = null;
    private List<DangKyLop> danhSachHocVienLop = new ArrayList<>();

    // Component KPI Cards
    private JLabel lblCardTongLop;
    private JLabel lblCardTongHocVien;
    private JLabel lblCardDaDiemDanh;
    private JLabel lblCardLopDangChon;

    // Component Bảng Lớp Học (Master)
    private JTable tableLop;
    private DefaultTableModel modelLop;
    private TableRowSorter<DefaultTableModel> sorterLop;
    private JTextField txtSearchLop;
    private JComboBox<String> cbFilterBoMon;
    private JComboBox<String> cbFilterChiNhanh;

    // Component Bảng Học Viên & Điểm Danh (Detail)
    private JLabel lblDetailTitle;
    private JTable tableHocVien;
    private DefaultTableModel modelHocVien;
    private TableRowSorter<DefaultTableModel> sorterHocVien;
    private JTextField txtSearchHocVien;

    // Các nút thao tác Học Viên
    private JButton btnThemHocVien;
    private JButton btnHuyDangKy;
    private JButton btnDiemDanhCoMat;
    private JButton btnBaoVang;
    private JButton btnDiemDanhTatCa;
    private JButton btnXuatExcelHocVien;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public LichLopHocPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(new EmptyBorder(16, 20, 16, 20));
        this.setBackground(new Color(248, 250, 252));

        khoiTaoGiaoDien();
        taiDuLieuLopHoc();
    }

    private void khoiTaoGiaoDien() {
        // 1. Header & KPI Cards
        JPanel pTop = new JPanel(new BorderLayout(0, 12));
        pTop.setOpaque(false);

        // Header Title
        JPanel pTitle = new JPanel(new BorderLayout(5, 5));
        pTitle.setOpaque(false);

        JLabel lblTitle = new JLabel("LỊCH LỚP HỌC & ĐIỂM DANH HỘI VIÊN (HUẤN LUYỆN VIÊN)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Quản lý thời khóa biểu lớp nhóm Yoga, Zumba, Boxing • Ghi danh & Điểm danh học viên theo thời gian thực");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        pTitle.add(lblTitle, BorderLayout.NORTH);
        pTitle.add(lblSubtitle, BorderLayout.SOUTH);
        pTop.add(pTitle, BorderLayout.NORTH);

        // KPI Badges Bar
        JPanel pCards = new JPanel(new GridLayout(1, 4, 12, 0));
        pCards.setOpaque(false);

        lblCardTongLop = taoKpiCard(pCards, "TỔNG SỐ LỚP HỌC", "0 Lớp", new Color(37, 99, 235), new Color(239, 246, 255));
        lblCardTongHocVien = taoKpiCard(pCards, "TỔNG LƯỢT ĐĂNG KÝ", "0 Học viên", new Color(16, 185, 129), new Color(236, 253, 245));
        lblCardDaDiemDanh = taoKpiCard(pCards, "ĐÃ ĐIỂM DANH HÔM NAY", "0 / 0", new Color(217, 119, 6), new Color(254, 243, 199));
        lblCardLopDangChon = taoKpiCard(pCards, "LỚP ĐANG CHỌN", "Chưa chọn lớp", new Color(100, 116, 139), new Color(241, 245, 249));

        pTop.add(pCards, BorderLayout.SOUTH);
        this.add(pTop, BorderLayout.NORTH);

        // 2. Thân chính: Chuyển sang JTabbedPane 2 Tab thoáng đãng, khoa học (không bị rối chồng chéo 2 bảng)
        mainTabs = new JTabbedPane();
        mainTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainTabs.setBackground(Color.WHITE);

        // Tab 1: Danh Sách & Lịch Các Lớp Học
        JPanel pMaster = taoPanelMasterLop();
        mainTabs.addTab("  1. Thời Khóa Biểu & Danh Sách Lớp Học Nhóm  ", pMaster);

        // Tab 2: Danh Sách Học Viên & Điểm Danh Trực Tiếp
        JPanel pDetail = taoPanelDetailHocVien();
        mainTabs.addTab("  2. Điểm Danh Học Viên Theo Lớp  ", pDetail);

        this.add(mainTabs, BorderLayout.CENTER);
    }

    private JTabbedPane mainTabs;

    private JLabel taoKpiCard(JPanel parent, String title, String value, Color accentColor, Color bgCard) {
        JPanel card = new JPanel(new BorderLayout(5, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lTitle.setForeground(new Color(100, 116, 139));

        JLabel lVal = new JLabel(value);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lVal.setForeground(accentColor);

        card.add(lTitle, BorderLayout.NORTH);
        card.add(lVal, BorderLayout.CENTER);
        parent.add(card);
        return lVal;
    }

    // =========================================================================
    // 1. TẦNG TRÊN: MASTER LỚP HỌC
    // =========================================================================
    private JPanel taoPanelMasterLop() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Toolbar 2 tầng (chống tràn và đè các nút trên màn hình nhỏ)
        JPanel pTool = new JPanel();
        pTool.setLayout(new BoxLayout(pTool, BoxLayout.Y_AXIS));
        pTool.setOpaque(false);

        // Trái: Tìm kiếm & Lọc
        JPanel pFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pFilter.setOpaque(false);
        pFilter.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lSearch = new JLabel("Tìm kiếm lớp:");
        lSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lSearch.setForeground(new Color(51, 65, 85));

        txtSearchLop = new JTextField(12);
        txtSearchLop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearchLop.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterMaster(); }
            public void removeUpdate(DocumentEvent e) { filterMaster(); }
            public void changedUpdate(DocumentEvent e) { filterMaster(); }
        });

        JLabel lChiNhanh = new JLabel("Cơ sở:");
        lChiNhanh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lChiNhanh.setForeground(new Color(51, 65, 85));

        cbFilterChiNhanh = new JComboBox<>(new String[]{"Tất Cả Cơ Sở", "CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)"});
        cbFilterChiNhanh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String curBranch = Auth.getChiNhanhHienTai();
        if (curBranch.contains("CN01")) {
            cbFilterChiNhanh.setSelectedIndex(1);
        } else if (curBranch.contains("CN02")) {
            cbFilterChiNhanh.setSelectedIndex(2);
        } else {
            cbFilterChiNhanh.setSelectedIndex(0);
        }
        cbFilterChiNhanh.addActionListener(e -> filterMaster());

        JLabel lBoMon = new JLabel("Bộ môn:");
        lBoMon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lBoMon.setForeground(new Color(51, 65, 85));

        cbFilterBoMon = new JComboBox<>(new String[]{"Tất Cả Bộ Môn", "Yoga", "Zumba", "Boxing", "Cardio", "Pilates"});
        cbFilterBoMon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFilterBoMon.addActionListener(e -> filterMaster());

        pFilter.add(lSearch);
        pFilter.add(txtSearchLop);
        pFilter.add(lChiNhanh);
        pFilter.add(cbFilterChiNhanh);
        pFilter.add(lBoMon);
        pFilter.add(cbFilterBoMon);
        pTool.add(pFilter);
        pTool.add(Box.createRigidArea(new Dimension(0, 6)));

        // Hàng nút Thêm, Sửa, Xóa, Làm mới, Xuất Excel (căn trái thoáng đãng)
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pBtns.setOpaque(false);
        pBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnThemLop = taoButtonFlat("+ Thêm Lớp Mới", new Color(16, 185, 129), Color.WHITE);
        btnThemLop.addActionListener(e -> moDialogThemSuaLop(null));

        JButton btnSuaLop = taoButtonFlat("Cập Nhật Lớp", new Color(37, 99, 235), Color.WHITE);
        btnSuaLop.addActionListener(e -> {
            if (lopDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học trong bảng để chỉnh sửa!", "Chưa chọn lớp", JOptionPane.WARNING_MESSAGE);
                return;
            }
            moDialogThemSuaLop(lopDangChon);
        });

        JButton btnXoaLop = taoButtonFlat("Xóa Lớp", new Color(239, 68, 68), Color.WHITE);
        btnXoaLop.addActionListener(e -> actionXoaLop());

        JButton btnRefresh = taoButtonFlat("Làm Mới", new Color(100, 116, 139), Color.WHITE);
        btnRefresh.addActionListener(e -> taiDuLieuLopHoc());

        JButton btnXuatExcelLop = taoButtonFlat("Xuất Excel Lịch Lớp", new Color(15, 118, 110), Color.WHITE);
        btnXuatExcelLop.addActionListener(e -> ExcelExportHelper.exportTableToExcel(this, tableLop, "LichLopHoc_FitLife"));

        JButton btnDiemDanh = taoButtonFlat("Điểm Danh Lớp Này ->", new Color(147, 51, 234), Color.WHITE);
        btnDiemDanh.addActionListener(e -> {
            if (lopDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học trong bảng để điểm danh!", "Chưa chọn lớp", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (mainTabs != null) mainTabs.setSelectedIndex(1);
        });

        pBtns.add(btnThemLop);
        pBtns.add(btnSuaLop);
        pBtns.add(btnXoaLop);
        pBtns.add(btnDiemDanh);
        pBtns.add(btnRefresh);
        pBtns.add(btnXuatExcelLop);
        pTool.add(pBtns);

        panel.add(pTool, BorderLayout.NORTH);

        // Bảng Lớp Học
        String[] cols = {"Mã Lớp", "Cơ Sở", "Bộ Môn", "Tên Lớp Học", "Huấn Luyện Viên", "Phòng Tập", "Khung Giờ & Lịch Tập", "Sĩ Số", "Tình Trạng Chỗ"};
        modelLop = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableLop = new JTable(modelLop);
        tableLop.setRowHeight(34);
        tableLop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableLop.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableLop.getTableHeader().setBackground(new Color(241, 245, 249));
        tableLop.getTableHeader().setForeground(new Color(30, 41, 59));
        tableLop.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorterLop = new TableRowSorter<>(modelLop);
        tableLop.setRowSorter(sorterLop);

        // Căn giữa các cột Mã, Cơ Sở, Bộ Môn, Phòng, Sĩ Số, Tình trạng
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        tableLop.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tableLop.getColumnModel().getColumn(0).setPreferredWidth(70);
        tableLop.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        tableLop.getColumnModel().getColumn(1).setPreferredWidth(130);
        tableLop.getColumnModel().getColumn(2).setCellRenderer(centerRender);
        tableLop.getColumnModel().getColumn(2).setPreferredWidth(85);
        tableLop.getColumnModel().getColumn(3).setPreferredWidth(160);
        tableLop.getColumnModel().getColumn(4).setPreferredWidth(130);
        tableLop.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableLop.getColumnModel().getColumn(6).setPreferredWidth(155);
        tableLop.getColumnModel().getColumn(7).setCellRenderer(centerRender);
        tableLop.getColumnModel().getColumn(7).setPreferredWidth(75);

        // Renderer Badge Tình Trạng Chỗ (Col 8)
        tableLop.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String val = (value != null) ? value.toString() : "";
                if ("ĐẦY LỚP".equalsIgnoreCase(val)) {
                    l.setForeground(new Color(220, 38, 38));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                }
                return l;
            }
        });
        tableLop.getColumnModel().getColumn(8).setPreferredWidth(100);

        // Sự kiện chọn dòng lớp học -> Nạp danh sách học viên
        tableLop.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableLop.getSelectedRow();
                if (row != -1) {
                    int mr = tableLop.convertRowIndexToModel(row);
                    String maLop = modelLop.getValueAt(mr, 0).toString();
                    lopDangChon = lopHocDAO.timTheoMa(maLop);
                    taiDanhSachHocVienCuaLop(lopDangChon);
                } else {
                    lopDangChon = null;
                    xoaTrangBangHocVien();
                }
            }
        });

        tableLop.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tableLop.getSelectedRow() != -1 && mainTabs != null) {
                    mainTabs.setSelectedIndex(1);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tableLop);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 2. TẦNG DƯỚI: DETAIL HỌC VIÊN & ĐIỂM DANH
    // =========================================================================
    private JPanel taoPanelDetailHocVien() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Header & Toolbar
        JPanel pTop = new JPanel(new BorderLayout(10, 8));
        pTop.setOpaque(false);

        // Tiêu đề Detail
        lblDetailTitle = new JLabel("DANH SÁCH HỌC VIÊN ĐĂNG KÝ (Vui lòng chọn một lớp học ở bảng trên)");
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDetailTitle.setForeground(new Color(30, 41, 59));
        pTop.add(lblDetailTitle, BorderLayout.NORTH);

        // Hàng điều khiển 2 tầng chống đè
        JPanel pActions = new JPanel();
        pActions.setLayout(new BoxLayout(pActions, BoxLayout.Y_AXIS));
        pActions.setOpaque(false);
        pActions.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tìm kiếm học viên trong lớp
        JPanel pSearchHV = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pSearchHV.setOpaque(false);
        pSearchHV.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lSearch = new JLabel("Lọc học viên:");
        lSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lSearch.setForeground(new Color(51, 65, 85));
        txtSearchHocVien = new JTextField(12);
        txtSearchHocVien.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearchHocVien.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterDetail(); }
            public void removeUpdate(DocumentEvent e) { filterDetail(); }
            public void changedUpdate(DocumentEvent e) { filterDetail(); }
        });
        pSearchHV.add(lSearch);
        pSearchHV.add(txtSearchHocVien);
        pActions.add(pSearchHV);
        pActions.add(Box.createRigidArea(new Dimension(0, 6)));

        // Bộ nút hành động học viên: Thêm, Hủy, Điểm danh, Báo vắng, Điểm danh tất cả, Xuất Excel (căn trái thoáng đãng)
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pBtns.setOpaque(false);
        pBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnThemHocVien = taoButtonFlat("+ Ghi Danh Học Viên", new Color(16, 185, 129), Color.WHITE);
        btnThemHocVien.addActionListener(e -> moDialogGhiDanhHocVien());

        btnHuyDangKy = taoButtonFlat("Hủy Đăng Ký", new Color(239, 68, 68), Color.WHITE);
        btnHuyDangKy.addActionListener(e -> actionHuyDangKyHocVien());

        btnDiemDanhCoMat = taoButtonFlat("Điểm Danh (Có Mặt)", new Color(5, 150, 105), Color.WHITE);
        btnDiemDanhCoMat.addActionListener(e -> actionCapNhatDiemDanh("Đã điểm danh"));

        btnBaoVang = taoButtonFlat("Báo Vắng", new Color(220, 38, 38), Color.WHITE);
        btnBaoVang.addActionListener(e -> actionCapNhatDiemDanh("Vắng mặt"));

        btnDiemDanhTatCa = taoButtonFlat("Điểm Danh Tất Cả", new Color(37, 99, 235), Color.WHITE);
        btnDiemDanhTatCa.addActionListener(e -> actionDiemDanhTatCa());

        btnXuatExcelHocVien = taoButtonFlat("Xuất Excel Điểm Danh", new Color(15, 118, 110), Color.WHITE);
        btnXuatExcelHocVien.addActionListener(e -> {
            if (lopDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trước khi xuất file điểm danh!");
                return;
            }
            ExcelExportHelper.exportTableToExcel(this, tableHocVien, "DiemDanh_" + lopDangChon.getMaLop());
        });

        JButton btnBackMaster = taoButtonFlat("<- Xem Lịch Các Lớp", new Color(100, 116, 139), Color.WHITE);
        btnBackMaster.addActionListener(e -> {
            if (mainTabs != null) mainTabs.setSelectedIndex(0);
        });

        pBtns.add(btnBackMaster);
        pBtns.add(btnThemHocVien);
        pBtns.add(btnHuyDangKy);
        pBtns.add(btnDiemDanhCoMat);
        pBtns.add(btnBaoVang);
        pBtns.add(btnDiemDanhTatCa);
        pBtns.add(btnXuatExcelHocVien);
        pActions.add(pBtns);

        pTop.add(pActions, BorderLayout.SOUTH);
        panel.add(pTop, BorderLayout.NORTH);

        // Bảng Học Viên
        String[] cols = {"ID ĐK", "Mã HV", "Họ Và Tên Học Viên", "Số Điện Thoại", "Ngày Đăng Ký", "Trạng Thái Điểm Danh", "Ghi Chú Đăng Ký"};
        modelHocVien = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableHocVien = new JTable(modelHocVien);
        tableHocVien.setRowHeight(32);
        tableHocVien.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableHocVien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHocVien.getTableHeader().setBackground(new Color(241, 245, 249));
        tableHocVien.getTableHeader().setForeground(new Color(30, 41, 59));
        tableHocVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorterHocVien = new TableRowSorter<>(modelHocVien);
        tableHocVien.setRowSorter(sorterHocVien);

        // Căn lề & Kích thước cột
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);

        tableHocVien.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        tableHocVien.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableHocVien.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        tableHocVien.getColumnModel().getColumn(1).setPreferredWidth(70);
        tableHocVien.getColumnModel().getColumn(2).setPreferredWidth(170);
        tableHocVien.getColumnModel().getColumn(3).setCellRenderer(centerRender);
        tableHocVien.getColumnModel().getColumn(3).setPreferredWidth(110);
        tableHocVien.getColumnModel().getColumn(4).setCellRenderer(centerRender);
        tableHocVien.getColumnModel().getColumn(4).setPreferredWidth(130);

        // Renderer màu cho Trạng Thái Điểm Danh
        tableHocVien.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String val = (value != null) ? value.toString() : "";
                if ("Đã điểm danh".equalsIgnoreCase(val)) {
                    l.setForeground(new Color(5, 150, 105));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("Vắng mặt".equalsIgnoreCase(val)) {
                    l.setForeground(new Color(220, 38, 38));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    l.setForeground(new Color(180, 83, 9));
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }
                return l;
            }
        });
        tableHocVien.getColumnModel().getColumn(5).setPreferredWidth(130);
        tableHocVien.getColumnModel().getColumn(6).setPreferredWidth(180);

        JScrollPane scroll = new JScrollPane(tableHocVien);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        capNhatTrangThaiNutDetail(false);
        return panel;
    }

    private void capNhatTrangThaiNutDetail(boolean enabled) {
        btnThemHocVien.setEnabled(enabled);
        btnHuyDangKy.setEnabled(enabled);
        btnDiemDanhCoMat.setEnabled(enabled);
        btnBaoVang.setEnabled(enabled);
        btnDiemDanhTatCa.setEnabled(enabled);
        btnXuatExcelHocVien.setEnabled(enabled);
    }

    // =========================================================================
    // 3. XỬ LÝ DỮ LIỆU & ĐỒNG BỘ
    // =========================================================================
    private void taiDuLieuLopHoc() {
        danhSachLop = lopHocDAO.layTatCa();
        modelLop.setRowCount(0);

        int tongLop = danhSachLop.size();
        int tongHocVien = 0;
        int tongDaDiemDanh = 0;

        for (LopHoc lh : danhSachLop) {
            int cur = lh.getSiSoHienTai();
            int max = lh.getSiSoToiDa();
            tongHocVien += cur;

            String tinhTrang = (cur >= max) ? "ĐẦY LỚP" : "CÒN CHỖ (" + (max - cur) + ")";
            String branchName = (lh.getChiNhanh() != null) ? lh.getChiNhanh() : "CN01 - Cơ sở 1 (Quận 1)";
            modelLop.addRow(new Object[]{
                    lh.getMaLop(),
                    branchName,
                    lh.getBoMon() != null ? lh.getBoMon() : "Khác",
                    lh.getTenLop(),
                    lh.getHuanLuyenVien(),
                    lh.getPhongTap(),
                    lh.getThoiGian(),
                    cur + "/" + max,
                    tinhTrang
            });
        }

        // Đếm số học viên đã điểm danh trên toàn hệ thống
        List<DangKyLop> tatCaDK = dangKyLopDAO.layTatCa();
        for (DangKyLop dk : tatCaDK) {
            if ("Đã điểm danh".equalsIgnoreCase(dk.getTrangThaiDiemDanh())) {
                tongDaDiemDanh++;
            }
        }

        lblCardTongLop.setText(tongLop + " Lớp");
        lblCardTongHocVien.setText(tongHocVien + " Học viên");
        lblCardDaDiemDanh.setText(tongDaDiemDanh + " / " + tatCaDK.size());

        if (lopDangChon != null) {
            // Chọn lại lớp cũ nếu còn tồn tại
            boolean timThay = false;
            for (int i = 0; i < tableLop.getRowCount(); i++) {
                int mr = tableLop.convertRowIndexToModel(i);
                if (modelLop.getValueAt(mr, 0).toString().equalsIgnoreCase(lopDangChon.getMaLop())) {
                    tableLop.setRowSelectionInterval(i, i);
                    timThay = true;
                    break;
                }
            }
            if (!timThay) {
                lopDangChon = null;
                xoaTrangBangHocVien();
            }
        } else {
            // Tự động chọn dòng đầu tiên nếu có
            if (tableLop.getRowCount() > 0) {
                tableLop.setRowSelectionInterval(0, 0);
            } else {
                xoaTrangBangHocVien();
            }
        }
    }

    private void taiDanhSachHocVienCuaLop(LopHoc lop) {
        if (lop == null) {
            xoaTrangBangHocVien();
            return;
        }

        danhSachHocVienLop = lopHocDAO.layDanhSachHocVienCuaLop(lop.getMaLop());
        modelHocVien.setRowCount(0);

        int daDiemDanhCuaLop = 0;
        for (DangKyLop dk : danhSachHocVienLop) {
            if ("Đã điểm danh".equalsIgnoreCase(dk.getTrangThaiDiemDanh())) {
                daDiemDanhCuaLop++;
            }
            String ngayStr = (dk.getNgayDangKy() != null) ? dateFormat.format(dk.getNgayDangKy()) : "";
            modelHocVien.addRow(new Object[]{
                    dk.getId(),
                    "HV" + String.format("%02d", dk.getMaHoiVien()),
                    dk.getTenHoiVien(),
                    dk.getSoDienThoai(),
                    ngayStr,
                    dk.getTrangThaiDiemDanh(),
                    dk.getGhiChu() != null ? dk.getGhiChu() : ""
            });
        }

        int cur = danhSachHocVienLop.size();
        int max = lop.getSiSoToiDa();
        lblDetailTitle.setText("HỌC VIÊN LỚP: " + lop.getTenLop().toUpperCase() + " (" + lop.getMaLop() + ") • SĨ SỐ: " + cur + "/" + max + " • ĐIỂM DANH: " + daDiemDanhCuaLop + "/" + cur);
        lblCardLopDangChon.setText(lop.getTenLop() + " (" + cur + "/" + max + ")");
        capNhatTrangThaiNutDetail(true);
    }

    private void xoaTrangBangHocVien() {
        modelHocVien.setRowCount(0);
        lblDetailTitle.setText("DANH SÁCH HỌC VIÊN ĐĂNG KÝ (Vui lòng chọn một lớp học ở bảng trên)");
        lblCardLopDangChon.setText("Chưa chọn lớp");
        capNhatTrangThaiNutDetail(false);
    }

    private void filterMaster() {
        String keyword = txtSearchLop.getText().trim().toLowerCase();
        String selectedBoMon = (String) cbFilterBoMon.getSelectedItem();
        String selectedBranch = (cbFilterChiNhanh != null) ? (String) cbFilterChiNhanh.getSelectedItem() : "Tất Cả Cơ Sở";

        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!keyword.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + keyword));
        }
        if (selectedBranch != null && !selectedBranch.startsWith("Tất Cả")) {
            String branchCode = selectedBranch.contains("CN01") ? "CN01" : (selectedBranch.contains("CN02") ? "CN02" : selectedBranch);
            filters.add(RowFilter.regexFilter("(?i)" + branchCode, 1));
        }
        if (selectedBoMon != null && !selectedBoMon.startsWith("Tất Cả")) {
            filters.add(RowFilter.regexFilter("(?i)" + selectedBoMon, 2));
        }

        if (filters.isEmpty()) {
            sorterLop.setRowFilter(null);
        } else {
            sorterLop.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void filterDetail() {
        String text = txtSearchHocVien.getText().trim();
        if (text.isEmpty()) {
            sorterHocVien.setRowFilter(null);
        } else {
            sorterHocVien.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    // =========================================================================
    // 4. CÁC HÀNH ĐỘNG NGHIỆP VỤ (THÊM / BỚT / CHỌN / ĐIỂM DANH)
    // =========================================================================
    private void actionXoaLop() {
        if (lopDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int countHV = danhSachHocVienLop.size();
        String msg = "Bạn có chắc chắn muốn hủy và xóa lớp [" + lopDangChon.getTenLop() + " (" + lopDangChon.getMaLop() + ")]?";
        if (countHV > 0) {
            msg += "\nCảnh báo: Lớp hiện đang có " + countHV + " học viên đã đăng ký!";
        }

        int confirm = JOptionPane.showConfirmDialog(this, msg, "Xác nhận xóa lớp học", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = lopHocDAO.xoa(lopDangChon.getMaLop());
            if (ok) {
                SoundHelper.playSuccessBeep();
                JOptionPane.showMessageDialog(this, "Đã xóa lớp học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                lopDangChon = null;
                taiDuLieuLopHoc();
            } else {
                SoundHelper.playErrorBeep();
                JOptionPane.showMessageDialog(this, "Không thể xóa lớp học. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actionHuyDangKyHocVien() {
        if (lopDangChon == null) return;
        int row = tableHocVien.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên trong bảng cần hủy đăng ký!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mr = tableHocVien.convertRowIndexToModel(row);
        int idDangKy = (int) modelHocVien.getValueAt(mr, 0);
        String tenHV = modelHocVien.getValueAt(mr, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy đăng ký lớp cho học viên [" + tenHV + "] không?",
                "Xác nhận hủy đăng ký", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = lopHocDAO.huyDangKy(idDangKy, lopDangChon.getMaLop());
            if (ok) {
                SoundHelper.playSuccessBeep();
                taiDuLieuLopHoc();
                JOptionPane.showMessageDialog(this, "Đã hủy đăng ký học viên [" + tenHV + "] khỏi lớp!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                SoundHelper.playErrorBeep();
                JOptionPane.showMessageDialog(this, "Hủy đăng ký thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actionCapNhatDiemDanh(String trangThai) {
        if (lopDangChon == null) return;
        int row = tableHocVien.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên trong bảng để cập nhật điểm danh!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mr = tableHocVien.convertRowIndexToModel(row);
        int idDangKy = (int) modelHocVien.getValueAt(mr, 0);
        String tenHV = modelHocVien.getValueAt(mr, 2).toString();

        boolean ok = lopHocDAO.capNhatDiemDanh(idDangKy, trangThai);
        if (ok) {
            SoundHelper.playSuccessBeep();
            modelHocVien.setValueAt(trangThai, mr, 5);
            taiDuLieuLopHoc(); // Đồng bộ lại đếm KPI
        } else {
            SoundHelper.playErrorBeep();
            JOptionPane.showMessageDialog(this, "Cập nhật điểm danh thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actionDiemDanhTatCa() {
        if (lopDangChon == null || danhSachHocVienLop.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lớp học chưa có học viên nào để điểm danh!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận điểm danh CÓ MẶT cho toàn bộ " + danhSachHocVienLop.size() + " học viên lớp [" + lopDangChon.getTenLop() + "]?",
                "Điểm danh tất cả", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            lopHocDAO.diemDanhTatCa(lopDangChon.getMaLop(), "Đã điểm danh");
            SoundHelper.playSuccessBeep();
            taiDuLieuLopHoc();
            JOptionPane.showMessageDialog(this, "Đã điểm danh thành công cho toàn bộ học viên trong lớp!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =========================================================================
    // 5. DIALOG THÊM / SỬA LỚP HỌC (CHUẨN CHỈ, KHÔNG CÓ SẠN)
    // =========================================================================
    private void moDialogThemSuaLop(LopHoc lopCanSua) {
        boolean isEdit = (lopCanSua != null);
        String title = isEdit ? "CẬP NHẬT LỚP HỌC: " + lopCanSua.getMaLop() : "THÊM LỚP HỌC MỚI";

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dlg.setSize(520, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(15, 15));
        ((JPanel) dlg.getContentPane()).setBorder(new EmptyBorder(20, 24, 20, 24));
        dlg.getContentPane().setBackground(Color.WHITE);

        // Header Form
        JPanel pHead = new JPanel(new BorderLayout(0, 4));
        pHead.setOpaque(false);
        JLabel lblH = new JLabel(title);
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblH.setForeground(new Color(15, 23, 42));
        JLabel lblSub = new JLabel(isEdit ? "Điều chỉnh phân công giáo viên, phòng và sĩ số lớp" : "Tạo mới lịch lớp nhóm và xếp lịch phòng tập");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pHead.add(lblH, BorderLayout.NORTH);
        pHead.add(lblSub, BorderLayout.SOUTH);
        dlg.add(pHead, BorderLayout.NORTH);

        // Form Fields
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.weightx = 1.0;

        Font fontL = new Font("Segoe UI", Font.BOLD, 12);
        Font fontI = new Font("Segoe UI", Font.PLAIN, 13);

        int r = 0;
        // Mã Lớp
        gbc.gridy = r++;
        JLabel lMa = new JLabel("Mã Lớp Học (*):"); lMa.setFont(fontL);
        pForm.add(lMa, gbc);
        gbc.gridy = r++;
        JTextField txtMa = new JTextField(); txtMa.setFont(fontI);
        if (isEdit) {
            txtMa.setText(lopCanSua.getMaLop());
            txtMa.setEditable(false);
            txtMa.setBackground(new Color(241, 245, 249));
        } else {
            // Gợi ý mã lớp tiếp theo
            txtMa.setText("LH" + String.format("%02d", danhSachLop.size() + 1));
        }
        pForm.add(txtMa, gbc);

        // Bộ Môn
        gbc.gridy = r++;
        JLabel lBoMon = new JLabel("Bộ Môn:"); lBoMon.setFont(fontL);
        pForm.add(lBoMon, gbc);
        gbc.gridy = r++;
        JComboBox<String> cbBoMon = new JComboBox<>(new String[]{"Yoga", "Zumba", "Boxing", "Cardio", "Pilates"});
        cbBoMon.setFont(fontI);
        if (isEdit && lopCanSua.getBoMon() != null) {
            cbBoMon.setSelectedItem(lopCanSua.getBoMon());
        }
        pForm.add(cbBoMon, gbc);

        // Tên Lớp
        gbc.gridy = r++;
        JLabel lTen = new JLabel("Tên Lớp Học (*):"); lTen.setFont(fontL);
        pForm.add(lTen, gbc);
        gbc.gridy = r++;
        JTextField txtTen = new JTextField(); txtTen.setFont(fontI);
        if (isEdit) txtTen.setText(lopCanSua.getTenLop());
        pForm.add(txtTen, gbc);

        // Huấn Luyện Viên
        gbc.gridy = r++;
        JLabel lHLV = new JLabel("Huấn Luyện Viên Phụ Trách:"); lHLV.setFont(fontL);
        pForm.add(lHLV, gbc);
        gbc.gridy = r++;
        JComboBox<String> cbHLV = new JComboBox<>(new String[]{
                "Nguyễn Thu Thảo", "Lê Mai Hương", "Phan Thanh Sơn",
                "Trần Tuấn Kiệt", "HLV John Smith", "HLV Phạm Quốc Bảo"
        });
        cbHLV.setFont(fontI);
        if (isEdit && lopCanSua.getHuanLuyenVien() != null) {
            cbHLV.setSelectedItem(lopCanSua.getHuanLuyenVien());
        }
        pForm.add(cbHLV, gbc);

        // Phòng Tập
        gbc.gridy = r++;
        JLabel lPhong = new JLabel("Phòng Tập:"); lPhong.setFont(fontL);
        pForm.add(lPhong, gbc);
        gbc.gridy = r++;
        JComboBox<String> cbPhong = new JComboBox<>(new String[]{
                "Studio 1 (Tầng 2)", "Studio 2 (Tầng 2)", "Khu Võ Thuật Ring A",
                "Khu Functional", "Phòng 01 (Yoga/Pilates)"
        });
        cbPhong.setFont(fontI);
        if (isEdit && lopCanSua.getPhongTap() != null) {
            cbPhong.setSelectedItem(lopCanSua.getPhongTap());
        }
        pForm.add(cbPhong, gbc);

        // Khung Giờ & Thứ
        gbc.gridy = r++;
        JLabel lTime = new JLabel("Lịch Tập & Khung Giờ:"); lTime.setFont(fontL);
        pForm.add(lTime, gbc);
        gbc.gridy = r++;
        JComboBox<String> cbTime = new JComboBox<>(new String[]{
                "06:30 - 07:30 (T2-T4-T6)", "07:00 - 08:00 (T3-T5)",
                "17:30 - 18:30 (T2-T4-T6)", "18:00 - 19:00 (T3-T5-T7)",
                "19:15 - 20:15 (T2-T4-T6)", "08:00 - 09:30 (T7 & CN)"
        });
        cbTime.setEditable(true);
        cbTime.setFont(fontI);
        if (isEdit && lopCanSua.getThoiGian() != null) {
            cbTime.setSelectedItem(lopCanSua.getThoiGian());
        }
        pForm.add(cbTime, gbc);

        // Cơ Sở
        gbc.gridy = r++;
        JLabel lCN = new JLabel("Cơ Sở Trực Thuộc (*):"); lCN.setFont(fontL);
        pForm.add(lCN, gbc);
        gbc.gridy = r++;
        JComboBox<String> cbCN = new JComboBox<>(new String[]{"CN01 - Cơ sở 1 (Quận 1)", "CN02 - Cơ sở 2 (Tân Bình)"});
        cbCN.setFont(fontI);
        if (isEdit && lopCanSua.getChiNhanh() != null) {
            cbCN.setSelectedItem(lopCanSua.getChiNhanh().contains("CN02") ? "CN02 - Cơ sở 2 (Tân Bình)" : "CN01 - Cơ sở 1 (Quận 1)");
        } else if (Auth.getChiNhanhHienTai().contains("CN02")) {
            cbCN.setSelectedIndex(1);
        }
        pForm.add(cbCN, gbc);

        // Sĩ Số Tối Đa
        gbc.gridy = r++;
        JLabel lMax = new JLabel("Sĩ Số Tối Đa (*):"); lMax.setFont(fontL);
        pForm.add(lMax, gbc);
        gbc.gridy = r++;
        JTextField txtMax = new JTextField(isEdit ? String.valueOf(lopCanSua.getSiSoToiDa()) : "20");
        txtMax.setFont(fontI);
        pForm.add(txtMax, gbc);

        dlg.add(pForm, BorderLayout.CENTER);

        // Buttons
        JPanel pBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pBot.setOpaque(false);

        JButton btnSave = taoButtonFlat(isEdit ? "Cập Nhật" : "Lưu Lớp Mới", new Color(16, 185, 129), Color.WHITE);
        btnSave.addActionListener(e -> {
            String ma = txtMa.getText().trim().toUpperCase();
            String ten = txtTen.getText().trim();
            String boMon = (String) cbBoMon.getSelectedItem();
            String hlv = (String) cbHLV.getSelectedItem();
            String phong = (String) cbPhong.getSelectedItem();
            String time = (String) cbTime.getSelectedItem();
            String branch = (String) cbCN.getSelectedItem();
            String maxStr = txtMax.getText().trim();

            if (ma.isEmpty() || ten.isEmpty() || maxStr.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng nhập đầy đủ các trường bắt buộc (*)", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int maxSiSo;
            try {
                maxSiSo = Integer.parseInt(maxStr);
                if (maxSiSo <= 0 || maxSiSo > 100) {
                    JOptionPane.showMessageDialog(dlg, "Sĩ số tối đa phải nằm trong khoảng từ 1 đến 100!", "Không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Sĩ số tối đa phải là số nguyên dương!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isEdit) {
                // Kiểm tra trùng mã
                if (lopHocDAO.timTheoMa(ma) != null) {
                    JOptionPane.showMessageDialog(dlg, "Mã lớp [" + ma + "] đã tồn tại trong hệ thống!", "Trùng mã", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LopHoc lopMoi = new LopHoc(ma, ten, boMon, hlv, time, phong, branch, maxSiSo, 0);
                boolean ok = lopHocDAO.them(lopMoi);
                if (ok) {
                    SoundHelper.playSuccessBeep();
                    lopDangChon = lopMoi;
                    taiDuLieuLopHoc();
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, "Đã thêm mới lớp học [" + ten + "] thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    SoundHelper.playErrorBeep();
                    JOptionPane.showMessageDialog(dlg, "Không thể lưu lớp học mới vào cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                lopCanSua.setTenLop(ten);
                lopCanSua.setChiNhanh(branch);
                lopCanSua.setBoMon(boMon);
                lopCanSua.setHuanLuyenVien(hlv);
                lopCanSua.setPhongTap(phong);
                lopCanSua.setThoiGian(time);
                lopCanSua.setSiSoToiDa(maxSiSo);

                boolean ok = lopHocDAO.capNhat(lopCanSua);
                if (ok) {
                    SoundHelper.playSuccessBeep();
                    taiDuLieuLopHoc();
                    dlg.dispose();
                    JOptionPane.showMessageDialog(this, "Cập nhật lớp học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    SoundHelper.playErrorBeep();
                    JOptionPane.showMessageDialog(dlg, "Cập nhật lớp học thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnCancel = taoButtonFlat("Hủy Bỏ", new Color(148, 163, 184), Color.WHITE);
        btnCancel.addActionListener(e -> dlg.dispose());

        pBot.add(btnSave);
        pBot.add(btnCancel);
        dlg.add(pBot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    // =========================================================================
    // 6. DIALOG GHI DANH HỌC VIÊN (CHỌN TỪ HOIVIENDAP THẬT, CỰC XỊN)
    // =========================================================================
    private void moDialogGhiDanhHocVien() {
        if (lopDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học ở bảng trên trước khi ghi danh học viên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cur = danhSachHocVienLop.size();
        int max = lopDangChon.getSiSoToiDa();
        if (cur >= max) {
            JOptionPane.showMessageDialog(this,
                    "Lớp học [" + lopDangChon.getTenLop() + "] đã đạt sĩ số tối đa (" + max + " học viên)!\nVui lòng tăng sĩ số lớp hoặc chọn lớp khác.",
                    "Lớp Đã Đầy Chỗ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ghi Danh Hội Viên Vào Lớp: " + lopDangChon.getTenLop(), true);
        dlg.setSize(780, 500);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(14, 14));
        ((JPanel) dlg.getContentPane()).setBorder(new EmptyBorder(16, 18, 16, 18));
        dlg.getContentPane().setBackground(Color.WHITE);

        // Header Dialog
        JPanel pHead = new JPanel(new BorderLayout(0, 4));
        pHead.setOpaque(false);

        JLabel lblH = new JLabel("GHI DANH HỘI VIÊN: " + lopDangChon.getTenLop().toUpperCase() + " (" + lopDangChon.getMaLop() + ")");
        lblH.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblH.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Sĩ số hiện tại: " + cur + "/" + max + " • Còn lại: " + (max - cur) + " suất • HLV: " + lopDangChon.getHuanLuyenVien());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        pHead.add(lblH, BorderLayout.NORTH);
        pHead.add(lblSub, BorderLayout.SOUTH);
        dlg.add(pHead, BorderLayout.NORTH);

        // Thân Dialog: Ô tìm kiếm + Bảng Hội Viên
        JPanel pCenter = new JPanel(new BorderLayout(0, 10));
        pCenter.setOpaque(false);

        // Thanh tìm kiếm nhanh hội viên
        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pSearch.setOpaque(false);
        JLabel lSearch = new JLabel("Tìm hội viên (Tên, SĐT, Mã QR):");
        lSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField txtSearchHV = new JTextField(20);
        txtSearchHV.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pSearch.add(lSearch);
        pSearch.add(txtSearchHV);
        pCenter.add(pSearch, BorderLayout.NORTH);

        // Bảng Danh Sách Hội Viên
        String[] hvCols = {"Mã HV", "Mã QR", "Họ Và Tên", "Số Điện Thoại", "Gói Tập Đang Ký", "Chi Nhánh", "Trạng Thái"};
        DefaultTableModel hvModel = new DefaultTableModel(hvCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        List<HoiVien> tatCaHoiVien = hoiVienDAO.layTatCa();
        for (HoiVien hv : tatCaHoiVien) {
            hvModel.addRow(new Object[]{
                    hv.getMaHoiVien(),
                    hv.getMaTheQR(),
                    hv.getHoTen(),
                    hv.getSoDienThoai(),
                    hv.getGoiTapDangKy() != null ? hv.getGoiTapDangKy() : "Gói Tiêu Chuẩn",
                    hv.getChiNhanhDangKy() != null ? hv.getChiNhanhDangKy() : "CN01 - Quận 1",
                    hv.getTrangThai() != null ? hv.getTrangThai() : "Active"
            });
        }

        JTable tableHV = new JTable(hvModel);
        tableHV.setRowHeight(30);
        tableHV.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableHV.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHV.getTableHeader().setBackground(new Color(241, 245, 249));
        tableHV.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<DefaultTableModel> hvSorter = new TableRowSorter<>(hvModel);
        tableHV.setRowSorter(hvSorter);

        txtSearchHV.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String t = txtSearchHV.getText().trim();
                if (t.isEmpty()) hvSorter.setRowFilter(null);
                else hvSorter.setRowFilter(RowFilter.regexFilter("(?i)" + t));
            }
        });

        JScrollPane hvScroll = new JScrollPane(tableHV);
        hvScroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pCenter.add(hvScroll, BorderLayout.CENTER);

        // Ghi chú đăng ký
        JPanel pNote = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pNote.setOpaque(false);
        JLabel lNote = new JLabel("Ghi chú kèm theo:");
        lNote.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField txtGhiChu = new JTextField("Đăng ký lớp thường xuyên", 35);
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pNote.add(lNote);
        pNote.add(txtGhiChu);
        pCenter.add(pNote, BorderLayout.SOUTH);

        dlg.add(pCenter, BorderLayout.CENTER);

        // Buttons
        JPanel pBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pBot.setOpaque(false);

        JButton btnConfirm = taoButtonFlat("Xác Nhận Ghi Danh", new Color(16, 185, 129), Color.WHITE);
        btnConfirm.addActionListener(e -> {
            int sel = tableHV.getSelectedRow();
            if (sel == -1) {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn một hội viên trong bảng để ghi danh!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int mr = tableHV.convertRowIndexToModel(sel);
            int maHV = (int) hvModel.getValueAt(mr, 0);
            String tenHV = hvModel.getValueAt(mr, 2).toString();
            String sdt = hvModel.getValueAt(mr, 3).toString();
            String ghiChu = txtGhiChu.getText().trim();

            // Kiểm tra xem hội viên này đã có trong lớp chưa
            if (dangKyLopDAO.daDangKy(lopDangChon.getMaLop(), maHV)) {
                JOptionPane.showMessageDialog(dlg,
                        "Hội viên [" + tenHV + "] đã có tên trong danh sách đăng ký của lớp này rồi!",
                        "Đã Đăng Ký", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tạo đối tượng đăng ký
            DangKyLop dk = new DangKyLop(0, lopDangChon.getMaLop(), maHV, tenHV, sdt, new Date(), "Chưa điểm danh", ghiChu);
            boolean ok = lopHocDAO.dangKyVaoLop(dk);
            if (ok) {
                SoundHelper.playSuccessBeep();
                taiDuLieuLopHoc();
                dlg.dispose();
                JOptionPane.showMessageDialog(this,
                        "Đã ghi danh thành công hội viên [" + tenHV + "] vào lớp " + lopDangChon.getTenLop() + "!",
                        "Ghi Danh Thành Công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                SoundHelper.playErrorBeep();
                JOptionPane.showMessageDialog(dlg, "Ghi danh thất bại. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnClose = taoButtonFlat("Đóng", new Color(148, 163, 184), Color.WHITE);
        btnClose.addActionListener(e -> dlg.dispose());

        pBot.add(btnConfirm);
        pBot.add(btnClose);
        dlg.add(pBot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    // =========================================================================
    // TIỆN ÍCH TẠO NÚT PHẲNG HIỆN ĐẠI (KHÔNG CÓ ICON EMOJI VỠ FONT)
    // =========================================================================
    private JButton taoButtonFlat(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
