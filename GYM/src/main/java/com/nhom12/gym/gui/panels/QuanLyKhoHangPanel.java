package com.nhom12.gym.gui.panels;

import com.nhom12.gym.service.KhoHangService;
import com.nhom12.gym.util.ExcelExportHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Màn hình Quản Lý Kho Hàng & Sản Phẩm Đã Bán
 * - Tab 1: Quản lý Tồn Kho & Nhập Thêm Hàng Hóa (Tăng số lượng tồn kho)
 * - Tab 2: Danh Sách Sản Phẩm Đã Bán & Đồng Bộ Kho Hàng (Hiển thị ngày giờ bán, số lượng bán, doanh thu, hoàn trả lại kho)
 * - Đồng bộ logic thời gian thực với quầy POS.
 */
public class QuanLyKhoHangPanel extends JPanel implements KhoHangService.KhoHangListener {
    private final KhoHangService khoService = KhoHangService.getInstance();
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private JTabbedPane tabbedPane;

    // --- TAB 1: TỒN KHO & NHẬP HÀNG ---
    private JTextField txtMaSP;
    private JTextField txtTenSP;
    private JComboBox<String> cbDanhMuc;
    private JTextField txtSoLuong;
    private JTextField txtGiaBan;
    private JTable tableKho;
    private DefaultTableModel modelKho;
    private JLabel lblKpiTongMatHang;
    private JLabel lblKpiTongSoLuongTon;
    private JLabel lblKpiSapHetHang;

    // --- TAB 2: SẢN PHẨM ĐÃ BÁN ---
    private JTable tableDaBan;
    private DefaultTableModel modelDaBan;
    private JLabel lblKpiTongDaBan;
    private JLabel lblKpiDoanhThuDaBan;
    private JLabel lblKpiBanChayNhat;

    public QuanLyKhoHangPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 250, 252));

        // Đăng ký lắng nghe thay đổi tồn kho từ toàn bộ hệ thống
        khoService.addListener(this);

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ KHO HÀNG & THEO DÕI SẢN PHẨM ĐÃ BÁN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Đồng bộ tăng giảm logic với Quầy Bán Hàng POS • Cập nhật tự động thời gian thực");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane chính
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("📦 TỒN KHO & NHẬP HÀNG", createTabTonKho());
        tabbedPane.addTab("📊 SẢN PHẨM ĐÃ BÁN & ĐỒNG BỘ KHO", createTabDaBan());

        add(tabbedPane, BorderLayout.CENTER);

        // Nạp dữ liệu ban đầu
        loadDataKho();
        loadDataDaBan();
    }

    // ==========================================
    // TAB 1: QUẢN LÝ TỒN KHO VÀ NHẬP HÀNG
    // ==========================================
    private JPanel createTabTonKho() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // KPI Tồn kho
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.setPreferredSize(new Dimension(0, 68));

        lblKpiTongMatHang = new JLabel("0 Mặt Hàng", SwingConstants.CENTER);
        lblKpiTongSoLuongTon = new JLabel("0 Sản Phẩm", SwingConstants.CENTER);
        lblKpiSapHetHang = new JLabel("0 Cảnh Báo", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("📦 Tổng Danh Mục Mặt Hàng", lblKpiTongMatHang, new Color(37, 99, 235), new Color(239, 246, 255)));
        kpiPanel.add(createKpiCard("📈 Tổng Số Lượng Tồn Trong Kho", lblKpiTongSoLuongTon, new Color(16, 185, 129), new Color(236, 253, 245)));
        kpiPanel.add(createKpiCard("⚠️ Mặt Hàng Sắp Hết / Hết Kho", lblKpiSapHetHang, new Color(239, 68, 68), new Color(254, 242, 242)));
        panel.add(kpiPanel, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(380);
        split.setDividerSize(6);
        split.setOpaque(false);
        split.setBorder(null);

        // Form bên trái
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        TitledBorder borderLeft = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Quản Lý Hàng Hóa & Nhập Kho Thêm "
        );
        borderLeft.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        borderLeft.setTitleColor(new Color(37, 99, 235));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0), borderLeft));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 12);

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r++;
        leftPanel.add(new JLabel("Mã Sản Phẩm (*):"), gbc);
        gbc.gridy = r++;
        txtMaSP = new JTextField("SP013");
        txtMaSP.setFont(fontInput);
        leftPanel.add(txtMaSP, gbc);

        gbc.gridy = r++;
        leftPanel.add(new JLabel("Tên Sản Phẩm (*):"), gbc);
        gbc.gridy = r++;
        txtTenSP = new JTextField();
        txtTenSP.setFont(fontInput);
        leftPanel.add(txtTenSP, gbc);

        gbc.gridy = r++;
        leftPanel.add(new JLabel("Danh Mục Hàng Hóa:"), gbc);
        gbc.gridy = r++;
        cbDanhMuc = new JComboBox<>(new String[]{
                "Nước uống giải khát",
                "Thực phẩm bổ sung dinh dưỡng",
                "Phụ kiện tập luyện",
                "Đồ bảo hộ & Trang phục",
                "Khác"
        });
        cbDanhMuc.setFont(fontInput);
        leftPanel.add(cbDanhMuc, gbc);

        gbc.gridy = r++;
        leftPanel.add(new JLabel("Số Lượng Nhập / Tồn Kho Hiện Tại (*):"), gbc);
        gbc.gridy = r++;
        txtSoLuong = new JTextField("50");
        txtSoLuong.setFont(fontInput);
        leftPanel.add(txtSoLuong, gbc);

        gbc.gridy = r++;
        leftPanel.add(new JLabel("Giá Bán Lẻ (VNĐ):"), gbc);
        gbc.gridy = r++;
        txtGiaBan = new JTextField("25000");
        txtGiaBan.setFont(fontInput);
        leftPanel.add(txtGiaBan, gbc);

        // Nút Thao tác Tồn Kho
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 6, 6));
        btnPanel.setOpaque(false);

        JButton btnNhapKhoThem = new JButton("📦 Nhập Thêm (+SL)");
        btnNhapKhoThem.setFont(fontLabel);
        btnNhapKhoThem.setBackground(new Color(16, 185, 129));
        btnNhapKhoThem.setForeground(Color.WHITE);
        btnNhapKhoThem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnThemMoi = new JButton("➕ Thêm Hàng Mới");
        btnThemMoi.setFont(fontLabel);
        btnThemMoi.setBackground(new Color(14, 165, 233));
        btnThemMoi.setForeground(Color.WHITE);
        btnThemMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCapNhat = new JButton("✏️ Cập Nhật");
        btnCapNhat.setFont(fontLabel);
        btnCapNhat.setBackground(new Color(37, 99, 235));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnXoa = new JButton("🗑️ Xóa Hàng");
        btnXoa.setFont(fontLabel);
        btnXoa.setBackground(new Color(239, 68, 68));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnLamMoi = new JButton("🔄 Làm Mới Form");
        btnLamMoi.setFont(fontLabel);
        btnLamMoi.setBackground(new Color(241, 245, 249));
        btnLamMoi.setForeground(new Color(71, 85, 105));
        btnLamMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnChuyenTabBan = new JButton("📊 Xem Hàng Đã Bán ➔");
        btnChuyenTabBan.setFont(fontLabel);
        btnChuyenTabBan.setBackground(new Color(245, 243, 255));
        btnChuyenTabBan.setForeground(new Color(124, 58, 237));
        btnChuyenTabBan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChuyenTabBan.addActionListener(e -> tabbedPane.setSelectedIndex(1));

        btnPanel.add(btnNhapKhoThem);
        btnPanel.add(btnThemMoi);
        btnPanel.add(btnCapNhat);
        btnPanel.add(btnXoa);
        btnPanel.add(btnLamMoi);
        btnPanel.add(btnChuyenTabBan);

        gbc.gridy = r++;
        gbc.insets = new Insets(14, 8, 8, 8);
        leftPanel.add(btnPanel, gbc);

        gbc.gridy = r++;
        gbc.weighty = 1.0;
        leftPanel.add(Box.createVerticalGlue(), gbc);
        split.setLeftComponent(leftPanel);

        // Bảng bên phải: Tình trạng tồn kho thực tế
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Tình Trạng Tồn Kho Thực Tế Trong Kho Chứa ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)
        ));

        // Tool bar tìm kiếm và xuất CSV
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        toolBar.setOpaque(false);
        JTextField txtSearchKho = new JTextField(16);
        txtSearchKho.setFont(fontInput);
        txtSearchKho.putClientProperty("JTextField.placeholderText", "Tìm mã hoặc tên sản phẩm...");

        JButton btnExportKho = new JButton("📊 Xuất Danh Mục Kho (.csv)");
        btnExportKho.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnExportKho.setBackground(new Color(241, 245, 249));
        btnExportKho.setForeground(new Color(15, 118, 110));
        btnExportKho.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toolBar.add(new JLabel("Tìm kiếm:"));
        toolBar.add(txtSearchKho);
        toolBar.add(btnExportKho);
        rightPanel.add(toolBar, BorderLayout.NORTH);

        String[] colsKho = {"Mã SP", "Tên Sản Phẩm", "Danh Mục", "Số Lượng Tồn", "Giá Bán Lẻ", "Trạng Thái Kho"};
        modelKho = new DefaultTableModel(colsKho, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableKho = new JTable(modelKho);
        tableKho.setRowHeight(32);
        tableKho.setFont(fontInput);
        tableKho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableKho.getTableHeader().setBackground(new Color(241, 245, 249));

        javax.swing.table.TableRowSorter<DefaultTableModel> sorterKho = new javax.swing.table.TableRowSorter<>(modelKho);
        tableKho.setRowSorter(sorterKho);

        txtSearchKho.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                String t = txtSearchKho.getText().trim();
                sorterKho.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        btnExportKho.addActionListener(e -> ExcelExportHelper.exportTableToExcel(this, tableKho, "TonKho_FitLife_Gym"));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);

        tableKho.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableKho.getColumnModel().getColumn(0).setCellRenderer(center);
        tableKho.getColumnModel().getColumn(1).setPreferredWidth(190);
        tableKho.getColumnModel().getColumn(2).setPreferredWidth(140);
        tableKho.getColumnModel().getColumn(3).setPreferredWidth(85);
        tableKho.getColumnModel().getColumn(3).setCellRenderer(center);
        tableKho.getColumnModel().getColumn(4).setPreferredWidth(95);
        tableKho.getColumnModel().getColumn(4).setCellRenderer(right);
        tableKho.getColumnModel().getColumn(5).setPreferredWidth(110);
        tableKho.getColumnModel().getColumn(5).setCellRenderer(center);

        tableKho.getSelectionModel().addListSelectionListener(e -> {
            int row = tableKho.getSelectedRow();
            if (row >= 0) {
                txtMaSP.setText(tableKho.getValueAt(row, 0).toString());
                txtTenSP.setText(tableKho.getValueAt(row, 1).toString());
                cbDanhMuc.setSelectedItem(tableKho.getValueAt(row, 2).toString());
                txtSoLuong.setText(tableKho.getValueAt(row, 3).toString());
                txtGiaBan.setText(tableKho.getValueAt(row, 4).toString().replaceAll("[^0-9]", ""));
            }
        });

        // 1. NÚT NHẬP KHO THÊM (+ SỐ LƯỢNG)
        btnNhapKhoThem.addActionListener(e -> {
            String ma = txtMaSP.getText().trim();
            if (ma.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm trên bảng hoặc nhập Mã SP cần nhập thêm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            KhoHangService.SanPhamKho sp = khoService.timSanPhamTheoMa(ma);
            if (sp == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy mã sản phẩm '" + ma + "' trong kho. Hãy bấm 'Thêm Hàng Mới' nếu đây là mặt hàng mới!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String slStr = JOptionPane.showInputDialog(this,
                    "Nhập số lượng bổ sung thêm vào kho cho sản phẩm:\n[" + sp.getMaSP() + "] " + sp.getTenSP() + "\n(Tồn hiện tại: " + sp.getSoLuongTon() + "):",
                    "Nhập Kho Thêm", JOptionPane.QUESTION_MESSAGE);

            if (slStr != null && !slStr.trim().isEmpty()) {
                try {
                    int slThem = Integer.parseInt(slStr.trim());
                    if (slThem <= 0) {
                        JOptionPane.showMessageDialog(this, "Số lượng nhập thêm phải lớn hơn 0!");
                        return;
                    }

                    // TĂNG TỒN KHO LOGIC HỢP LÝ:
                    khoService.nhapHangVaoKho(sp.getMaSP(), slThem, sp.getGiaBan());
                    JOptionPane.showMessageDialog(this,
                            "Đã nhập kho thêm +" + slThem + " " + sp.getTenSP() + " thành công!\nTồn kho mới: " + sp.getSoLuongTon(),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ!");
                }
            }
        });

        // 2. NÚT THÊM HÀNG MỚI
        btnThemMoi.addActionListener(e -> {
            String ma = txtMaSP.getText().trim();
            String ten = txtTenSP.getText().trim();
            String dm = cbDanhMuc.getSelectedItem().toString();
            int sl = 0;
            double gia = 0;
            try {
                sl = Integer.parseInt(txtSoLuong.getText().trim());
                gia = Double.parseDouble(txtGiaBan.getText().replaceAll("[^0-9]", "").trim());
            } catch (Exception ignored) {}

            if (ma.isEmpty() || ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã và Tên sản phẩm không được để trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (khoService.timSanPhamTheoMa(ma) != null) {
                JOptionPane.showMessageDialog(this, "Mã sản phẩm '" + ma + "' đã tồn tại! Vui lòng chọn mã khác hoặc bấm 'Nhập Kho Thêm'.", "Trùng lặp", JOptionPane.WARNING_MESSAGE);
                return;
            }

            KhoHangService.SanPhamKho spMoi = new KhoHangService.SanPhamKho(ma, ten, dm, sl, gia, "prod_water.jpg");
            khoService.themSanPhamMoi(spMoi);
            JOptionPane.showMessageDialog(this, "Đã thêm mặt hàng mới '" + ten + "' vào kho thành công!");
            btnLamMoi.doClick();
        });

        // 3. NÚT CẬP NHẬT
        btnCapNhat.addActionListener(e -> {
            String ma = txtMaSP.getText().trim();
            String ten = txtTenSP.getText().trim();
            String dm = cbDanhMuc.getSelectedItem().toString();
            int sl = 0;
            double gia = 0;
            try {
                sl = Integer.parseInt(txtSoLuong.getText().trim());
                gia = Double.parseDouble(txtGiaBan.getText().replaceAll("[^0-9]", "").trim());
            } catch (Exception ignored) {}

            if (ma.isEmpty() || ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hoặc nhập đầy đủ thông tin để cập nhật!");
                return;
            }

            KhoHangService.SanPhamKho sp = khoService.timSanPhamTheoMa(ma);
            if (sp == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy mặt hàng với mã '" + ma + "'!");
                return;
            }

            sp.setTenSP(ten);
            sp.setDanhMuc(dm);
            sp.setSoLuongTon(sl);
            sp.setGiaBan(gia);

            khoService.capNhatSanPham(sp);
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin mặt hàng thành công!");
        });

        // 4. NÚT XÓA HÀNG
        btnXoa.addActionListener(e -> {
            String ma = txtMaSP.getText().trim();
            if (ma.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn mặt hàng cần xóa trên bảng!");
                return;
            }
            int cf = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa mặt hàng [" + ma + "] khỏi kho?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (cf == JOptionPane.YES_OPTION) {
                khoService.xoaSanPham(ma);
                btnLamMoi.doClick();
                JOptionPane.showMessageDialog(this, "Đã xóa mặt hàng khỏi kho thành công!");
            }
        });

        // 5. NÚT LÀM MỚI FORM
        btnLamMoi.addActionListener(e -> {
            txtMaSP.setText("SP" + String.format("%03d", modelKho.getRowCount() + 1));
            txtTenSP.setText("");
            txtSoLuong.setText("50");
            txtGiaBan.setText("25000");
            cbDanhMuc.setSelectedIndex(0);
            tableKho.clearSelection();
        });

        rightPanel.add(new JScrollPane(tableKho), BorderLayout.CENTER);
        split.setRightComponent(rightPanel);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // TAB 2: SẢN PHẨM ĐÃ BÁN & NHẬT KÝ ĐỒNG BỘ XUẤT KHO
    // ==========================================
    private JPanel createTabDaBan() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // KPI Bán Hàng Kho
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        kpiPanel.setOpaque(false);
        kpiPanel.setPreferredSize(new Dimension(0, 68));

        lblKpiTongDaBan = new JLabel("0 Món", SwingConstants.CENTER);
        lblKpiDoanhThuDaBan = new JLabel("0 đ", SwingConstants.CENTER);
        lblKpiBanChayNhat = new JLabel("Chưa có", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("🛒 Tổng Số Lượng Đã Bán Tại Quầy", lblKpiTongDaBan, new Color(139, 92, 246), new Color(245, 243, 255)));
        kpiPanel.add(createKpiCard("💰 Tổng Doanh Thu Bán Hàng Kho", lblKpiDoanhThuDaBan, new Color(16, 185, 129), new Color(236, 253, 245)));
        kpiPanel.add(createKpiCard("🌟 Mặt Hàng Bán Chạy Nhất", lblKpiBanChayNhat, new Color(245, 158, 11), new Color(254, 252, 232)));
        panel.add(kpiPanel, BorderLayout.NORTH);

        // Bảng dữ liệu sản phẩm đã bán
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " Danh Sách Sản Phẩm Kho Hàng Đã Bán & Nhật Ký Xuất Kho POS (Có Ngày Giờ Chi Tiết) ",
                0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)
        ));

        // Thanh công cụ
        JPanel toolBar = new JPanel(new BorderLayout());
        toolBar.setOpaque(false);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        leftActions.setOpaque(false);

        JButton btnHoanTra = new JButton("🔄 Hoàn Trả Hàng (Tăng Lại Vào Kho)");
        btnHoanTra.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHoanTra.setBackground(new Color(239, 246, 255));
        btnHoanTra.setForeground(new Color(37, 99, 235));
        btnHoanTra.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnLamMoiDaBan = new JButton("🔄 Làm Mới");
        btnLamMoiDaBan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLamMoiDaBan.setBackground(new Color(241, 245, 249));
        btnLamMoiDaBan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        leftActions.add(btnHoanTra);
        leftActions.add(btnLamMoiDaBan);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        rightActions.setOpaque(false);

        JTextField txtSearchDaBan = new JTextField(18);
        txtSearchDaBan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearchDaBan.putClientProperty("JTextField.placeholderText", "Tìm mã GD, tên SP, ngày giờ...");

        JButton btnExportDaBan = new JButton("📊 Xuất Báo Cáo (.csv)");
        btnExportDaBan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportDaBan.setBackground(new Color(241, 245, 249));
        btnExportDaBan.setForeground(new Color(15, 118, 110));
        btnExportDaBan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightActions.add(new JLabel("Tìm kiếm:"));
        rightActions.add(txtSearchDaBan);
        rightActions.add(btnExportDaBan);

        toolBar.add(leftActions, BorderLayout.WEST);
        toolBar.add(rightActions, BorderLayout.EAST);
        tableContainer.add(toolBar, BorderLayout.NORTH);

        String[] colsDaBan = {
                "Mã Giao Dịch", "Mã SP", "Tên Sản Phẩm", "Số Lượng Bán",
                "Đơn Giá", "Thành Tiền", "Ngày Giờ Bán", "Hình Thức TT",
                "Thu Ngân / Kênh", "Trạng Thái Kho"
        };

        modelDaBan = new DefaultTableModel(colsDaBan, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableDaBan = new JTable(modelDaBan);
        tableDaBan.setRowHeight(32);
        tableDaBan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableDaBan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableDaBan.getTableHeader().setBackground(new Color(241, 245, 249));

        javax.swing.table.TableRowSorter<DefaultTableModel> sorterDaBan = new javax.swing.table.TableRowSorter<>(modelDaBan);
        tableDaBan.setRowSorter(sorterDaBan);

        txtSearchDaBan.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                String t = txtSearchDaBan.getText().trim();
                sorterDaBan.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        btnExportDaBan.addActionListener(e -> ExcelExportHelper.exportTableToExcel(this, tableDaBan, "BaoCao_SanPhamKho_DaBan"));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer rightGreen = new DefaultTableCellRenderer();
        rightGreen.setHorizontalAlignment(JLabel.RIGHT);
        rightGreen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rightGreen.setForeground(new Color(16, 185, 129));

        tableDaBan.getColumnModel().getColumn(0).setPreferredWidth(125);
        tableDaBan.getColumnModel().getColumn(0).setCellRenderer(center);
        tableDaBan.getColumnModel().getColumn(1).setPreferredWidth(55);
        tableDaBan.getColumnModel().getColumn(1).setCellRenderer(center);
        tableDaBan.getColumnModel().getColumn(2).setPreferredWidth(180);
        tableDaBan.getColumnModel().getColumn(3).setPreferredWidth(80);
        tableDaBan.getColumnModel().getColumn(3).setCellRenderer(center);
        tableDaBan.getColumnModel().getColumn(4).setPreferredWidth(85);
        tableDaBan.getColumnModel().getColumn(4).setCellRenderer(right);
        tableDaBan.getColumnModel().getColumn(5).setPreferredWidth(95);
        tableDaBan.getColumnModel().getColumn(5).setCellRenderer(rightGreen);
        tableDaBan.getColumnModel().getColumn(6).setPreferredWidth(135);
        tableDaBan.getColumnModel().getColumn(6).setCellRenderer(center);
        tableDaBan.getColumnModel().getColumn(7).setPreferredWidth(125);
        tableDaBan.getColumnModel().getColumn(7).setCellRenderer(center);
        tableDaBan.getColumnModel().getColumn(8).setPreferredWidth(120);
        tableDaBan.getColumnModel().getColumn(9).setPreferredWidth(150);

        // THAO TÁC HOÀN TRẢ HÀNG VỀ KHO (TỰ ĐỘNG TĂNG LẠI TỒN KHO)
        btnHoanTra.addActionListener(e -> {
            int row = tableDaBan.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 mục hàng đã bán trên bảng để tiến hành hoàn trả về kho!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maGD = tableDaBan.getValueAt(row, 0).toString();
            String maSP = tableDaBan.getValueAt(row, 1).toString();
            String tenSP = tableDaBan.getValueAt(row, 2).toString();
            int sl = Integer.parseInt(tableDaBan.getValueAt(row, 3).toString());
            String trangThai = tableDaBan.getValueAt(row, 9).toString();

            if (trangThai.contains("hoàn trả")) {
                JOptionPane.showMessageDialog(this, "Mục hàng này đã được hoàn trả về kho trước đó rồi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String input = JOptionPane.showInputDialog(this,
                    "Nhập số lượng muốn hoàn trả lại vào kho cho món:\n" + tenSP + " (Số lượng đã bán: " + sl + "):",
                    sl);

            if (input != null && !input.trim().isEmpty()) {
                try {
                    int slHoan = Integer.parseInt(input.trim());
                    if (slHoan <= 0 || slHoan > sl) {
                        JOptionPane.showMessageDialog(this, "Số lượng hoàn trả không hợp lệ (Phải từ 1 đến " + sl + ")!");
                        return;
                    }

                    String lyDo = JOptionPane.showInputDialog(this, "Nhập lý do hoàn trả:", "Khách đổi ý");
                    if (lyDo == null || lyDo.trim().isEmpty()) lyDo = "Khách đổi trả tại quầy";

                    // THỰC HIỆN LOGIC TĂNG LẠI TỒN KHO:
                    boolean ok = khoService.hoanTraHangVeKho(maGD, maSP, slHoan, lyDo);
                    if (ok) {
                        JOptionPane.showMessageDialog(this,
                                "Đã hoàn trả +" + slHoan + " " + tenSP + " về kho chứa thành công!\nSố lượng tồn kho đã được tự động TĂNG LẠI.",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
                }
            }
        });

        btnLamMoiDaBan.addActionListener(e -> {
            loadDataDaBan();
            loadDataKho();
        });

        tableContainer.add(new JScrollPane(tableDaBan), BorderLayout.CENTER);
        panel.add(tableContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createKpiCard(String title, JLabel lblValue, Color primaryColor, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryColor.brighter(), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(71, 85, 105));

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValue.setForeground(primaryColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    /**
     * Nạp dữ liệu bảng Tồn Kho & tính toán KPI
     */
    public void loadDataKho() {
        modelKho.setRowCount(0);
        List<KhoHangService.SanPhamKho> list = khoService.layTatCaSanPhamKho();

        int tongSL = 0;
        int sapHet = 0;

        for (KhoHangService.SanPhamKho sp : list) {
            tongSL += sp.getSoLuongTon();
            String tt;
            if (sp.getSoLuongTon() <= 0) {
                tt = "❌ Hết hàng (0)";
                sapHet++;
            } else if (sp.getSoLuongTon() <= 10) {
                tt = "⚠️ Sắp hết (" + sp.getSoLuongTon() + ")";
                sapHet++;
            } else {
                tt = "✅ Đủ hàng (" + sp.getSoLuongTon() + ")";
            }

            modelKho.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getDanhMuc(),
                    sp.getSoLuongTon(),
                    df.format(sp.getGiaBan()),
                    tt
            });
        }

        lblKpiTongMatHang.setText(list.size() + " Mặt Hàng");
        lblKpiTongSoLuongTon.setText(tongSL + " Sản Phẩm");
        lblKpiSapHetHang.setText(sapHet + " Cảnh Báo Cần Nhập");
    }

    /**
     * Nạp dữ liệu bảng Sản Phẩm Đã Bán & tính toán KPI
     */
    public void loadDataDaBan() {
        modelDaBan.setRowCount(0);
        List<KhoHangService.SanPhamDaBan> list = khoService.layTatCaSanPhamDaBan();

        int tongSLBan = 0;
        double tongDoanhThu = 0;
        java.util.Map<String, Integer> mapBanChay = new java.util.HashMap<>();

        for (KhoHangService.SanPhamDaBan db : list) {
            tongSLBan += db.getSoLuong();
            tongDoanhThu += db.getThanhTien();
            mapBanChay.put(db.getTenSP(), mapBanChay.getOrDefault(db.getTenSP(), 0) + db.getSoLuong());

            String timeStr = (db.getThoiGian() != null) ? sdfDateTime.format(db.getThoiGian()) : "13/10/2026 10:00:00";

            modelDaBan.addRow(new Object[]{
                    db.getMaGiaoDich(),
                    db.getMaSP(),
                    db.getTenSP(),
                    db.getSoLuong(),
                    df.format(db.getDonGia()),
                    df.format(db.getThanhTien()),
                    timeStr,
                    db.getPhuongThucTT(),
                    db.getThuNgan(),
                    db.getTrangThai()
            });
        }

        // Tìm món bán chạy nhất
        String topItem = "Chưa có";
        int maxSl = 0;
        for (java.util.Map.Entry<String, Integer> entry : mapBanChay.entrySet()) {
            if (entry.getValue() > maxSl) {
                maxSl = entry.getValue();
                topItem = entry.getKey() + " (" + maxSl + ")";
            }
        }

        lblKpiTongDaBan.setText(tongSLBan + " Món Đã Xuất");
        lblKpiDoanhThuDaBan.setText(df.format(tongDoanhThu));
        lblKpiBanChayNhat.setText(topItem);
    }

    @Override
    public void onKhoHangChanged() {
        SwingUtilities.invokeLater(() -> {
            loadDataKho();
            loadDataDaBan();
        });
    }
}
