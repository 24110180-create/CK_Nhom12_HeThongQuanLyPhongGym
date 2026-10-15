package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.GoiTapDAO;
import com.nhom12.gym.dao.HoaDonDAO;
import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.NhatKyHeThongDAO;
import com.nhom12.gym.entity.GoiTap;
import com.nhom12.gym.entity.HoaDon;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.DataSyncManager;
import com.nhom12.gym.service.VipPolicyService;
import com.nhom12.gym.util.Auth;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * GIAO DIỆN GIA HẠN & NÂNG CẤP GÓI TẬP HỘI VIÊN
 * Đảm bảo quy tắc bảo mật giá thực:
 * - CHỦ HỆ THỐNG: Nhìn thấy toàn bộ chi tiết Giá Gốc, Chiết Khấu VIP (-50%) và Thực Thu.
 * - LỄ TÂN / NHÂN VIÊN: ẨN HOÀN TOÀN Giá Gốc và Chiết Khấu 50%, chỉ thấy số tiền thực thu đã phê duyệt.
 */
public class GiaHanGoiTapPanel extends JPanel {
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final GoiTapDAO goiTapDAO = new GoiTapDAO();
    private final NhatKyHeThongDAO nhatKyDAO = new NhatKyHeThongDAO();
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private List<GoiTap> danhSachGoiTap = new ArrayList<>();

    // Controls
    private JTextField txtSearch;
    private JButton btnSearch;
    private JLabel lblRoleStatus;

    private JLabel lblTen;
    private JLabel lblCurrentStatus;
    private JComboBox<String> cbGoi;
    private JTextField txtNgay;
    private JComboBox<String> cbThanhToan;

    // Price Breakdown Components
    private JPanel pnlRowGiaGoc;
    private JLabel lblGiaGoc;
    private JPanel pnlRowGiamGia;
    private JLabel lblGiamGia;
    private JLabel lblVipPolicyNotice;
    private JLabel lblThanhTien;
    private JLabel lblSecurityBanner;

    private JButton btnCancel;
    private JButton btnConfirm;

    // Current State
    private HoiVien currentHoiVien = null;
    private long basePrice = 4500000;

    public GiaHanGoiTapPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        this.setBackground(new Color(245, 247, 250));

        initHeader();
        initCenter();

        // Listeners & Data loading
        loadPackageList();
        GoiTapDAO.addChangeListener(this::loadPackageList);
        DataSyncManager.addListener(type -> {
            if (type == DataSyncManager.DataType.GOI_TAP || type == DataSyncManager.DataType.ALL) {
                loadPackageList();
            }
        });
        loadInitialMember();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 6));
        headerPanel.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        JLabel lblTitle = new JLabel("GIA HẠN & NÂNG CẤP GÓI TẬP HỘI VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSubtitle = new JLabel("Tra cứu hội viên thực tế, gia hạn thời hạn thẻ tập & xuất hóa đơn thanh toán");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        titleBox.add(lblTitle);
        titleBox.add(lblSubtitle);

        // Góc phải Header: Hiển thị quyền đăng nhập hiện tại của người dùng
        JPanel roleToolBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        roleToolBox.setOpaque(false);

        String currentLoginRole = Auth.vaiTro != null ? Auth.vaiTro : "Lễ tân";
        boolean isMaster = "Chủ".equalsIgnoreCase(currentLoginRole);

        lblRoleStatus = new JLabel("Đăng nhập: " + (isMaster ? "👑 CHỦ PHÒNG TẬP" : "👔 " + currentLoginRole.toUpperCase()));
        lblRoleStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRoleStatus.setOpaque(true);
        lblRoleStatus.setBackground(isMaster ? new Color(243, 232, 255) : new Color(224, 231, 255));
        lblRoleStatus.setForeground(isMaster ? new Color(126, 34, 206) : new Color(30, 64, 175));
        lblRoleStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isMaster ? new Color(192, 132, 252) : new Color(147, 197, 253), 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        roleToolBox.add(lblRoleStatus);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(roleToolBox, BorderLayout.EAST);
        this.add(headerPanel, BorderLayout.NORTH);
    }

    private void initCenter() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);

        // --- 1. SEARCH PANEL ---
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));

        JPanel searchLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchLeft.setOpaque(false);
        JLabel lblPrompt = new JLabel("Tra cứu Hội Viên:");
        lblPrompt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.putClientProperty("JTextField.placeholderText", "Ví dụ: VIP007, 6, QR001, Đỗ Minh Trí...");

        btnSearch = new JButton("Tra Cứu");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(new Color(37, 99, 235));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchLeft.add(lblPrompt);
        searchLeft.add(txtSearch);
        searchLeft.add(btnSearch);

        searchPanel.add(searchLeft, BorderLayout.WEST);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // --- 2. FORM BODY PANEL ---
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 22, 16, 22)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        int r = 0;
        // Member Info Row
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.22;
        JLabel l1 = new JLabel("Hội Viên Đang Chọn:"); l1.setFont(fontLabel);
        formContainer.add(l1, gbc);

        gbc.gridx = 1; gbc.weightx = 0.78;
        lblTen = new JLabel("Đang tải dữ liệu hội viên...");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTen.setForeground(new Color(30, 41, 59));
        formContainer.add(lblTen, gbc);

        // Expiration Status
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.22;
        JLabel lStatus = new JLabel("Hạn Thẻ Hiện Tại:"); lStatus.setFont(fontLabel);
        formContainer.add(lStatus, gbc);

        gbc.gridx = 1; gbc.weightx = 0.78;
        lblCurrentStatus = new JLabel("---");
        lblCurrentStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCurrentStatus.setForeground(new Color(100, 116, 139));
        formContainer.add(lblCurrentStatus, gbc);

        // Package Choice
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.22;
        JLabel l2 = new JLabel("Chọn Gói Gia Hạn:"); l2.setFont(fontLabel);
        formContainer.add(l2, gbc);

        gbc.gridx = 1; gbc.weightx = 0.78;
        cbGoi = new JComboBox<>();
        cbGoi.setFont(fontInput);
        formContainer.add(cbGoi, gbc);

        // Start Date
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.22;
        JLabel l3 = new JLabel("Ngày Bắt Đầu Gia Hạn:"); l3.setFont(fontLabel);
        formContainer.add(l3, gbc);

        gbc.gridx = 1; gbc.weightx = 0.78;
        txtNgay = new JTextField(sdf.format(new Date()));
        txtNgay.setFont(fontInput);
        formContainer.add(txtNgay, gbc);

        // Payment Method
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.22;
        JLabel l4 = new JLabel("Phương Thức Thanh Toán:"); l4.setFont(fontLabel);
        formContainer.add(l4, gbc);

        gbc.gridx = 1; gbc.weightx = 0.78;
        cbThanhToan = new JComboBox<>(new String[]{"Chuyển khoản (VietQR)", "Tiền mặt", "Quẹt thẻ POS"});
        cbThanhToan.setFont(fontInput);
        formContainer.add(cbThanhToan, gbc);

        // --- 3. PRICE & SECURITY BREAKDOWN CARD (YÊU CẦU CỐT LÕI) ---
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        JPanel pnlPriceSecurityBox = taoPanelBangGiaVaBaoMat();
        formContainer.add(pnlPriceSecurityBox, gbc);

        // Action Buttons Row
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btnGroup.setOpaque(false);

        btnCancel = new JButton("Làm Mới Form");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBackground(new Color(100, 116, 139));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnConfirm = new JButton("Chốt Gia Hạn & Xuất Hóa Đơn (VietQR)");
        btnConfirm.setBackground(new Color(16, 185, 129));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGroup.add(btnCancel);
        btnGroup.add(btnConfirm);
        formContainer.add(btnGroup, gbc);

        r++; gbc.gridy = r; gbc.weighty = 1.0;
        formContainer.add(Box.createVerticalGlue(), gbc);

        centerPanel.add(formContainer, BorderLayout.CENTER);
        this.add(centerPanel, BorderLayout.CENTER);

        // Wire Actions
        btnSearch.addActionListener(e -> actionSearch());
        txtSearch.addActionListener(e -> actionSearch());
        cbGoi.addActionListener(e -> updatePrice());
        btnCancel.addActionListener(e -> resetForm());
        btnConfirm.addActionListener(e -> confirmRenewal());
    }

    private JPanel taoPanelBangGiaVaBaoMat() {
        JPanel box = new JPanel(new BorderLayout(0, 10));
        box.setBackground(new Color(248, 250, 252));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel details = new JPanel(new GridLayout(0, 1, 0, 8));
        details.setOpaque(false);

        // 1. Dòng Giá Niêm Yết Chuẩn (Giá Gốc)
        pnlRowGiaGoc = new JPanel(new BorderLayout());
        pnlRowGiaGoc.setOpaque(false);
        JLabel lblGiaGocTitle = new JLabel("Giá Niêm Yết Chuẩn Của Gói:");
        lblGiaGocTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblGiaGocTitle.setForeground(new Color(71, 85, 105));
        lblGiaGoc = new JLabel(df.format(basePrice));
        lblGiaGoc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGiaGoc.setForeground(new Color(71, 85, 105));
        pnlRowGiaGoc.add(lblGiaGocTitle, BorderLayout.WEST);
        pnlRowGiaGoc.add(lblGiaGoc, BorderLayout.EAST);

        // 2. Dòng Chiết Khấu VIP Đặc Quyền
        pnlRowGiamGia = new JPanel(new BorderLayout());
        pnlRowGiamGia.setOpaque(false);
        JLabel lblGiamGiaTitle = new JLabel("Chiết Khấu VIP Đặc Quyền (Giảm 50%):");
        lblGiamGiaTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblGiamGiaTitle.setForeground(new Color(220, 38, 38));
        lblGiamGia = new JLabel("- 0 đ");
        lblGiamGia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGiamGia.setForeground(new Color(220, 38, 38));
        pnlRowGiamGia.add(lblGiamGiaTitle, BorderLayout.WEST);
        pnlRowGiamGia.add(lblGiamGia, BorderLayout.EAST);

        // 3. Dòng Chính Sách Khách Hàng
        JPanel pnlRowPolicy = new JPanel(new BorderLayout());
        pnlRowPolicy.setOpaque(false);
        JLabel lblPolTitle = new JLabel("Chính Sách Khách Hàng:");
        lblPolTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPolTitle.setForeground(new Color(71, 85, 105));
        lblVipPolicyNotice = new JLabel("Hội phí tiêu chuẩn niêm yết");
        lblVipPolicyNotice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVipPolicyNotice.setForeground(new Color(100, 116, 139));
        pnlRowPolicy.add(lblPolTitle, BorderLayout.WEST);
        pnlRowPolicy.add(lblVipPolicyNotice, BorderLayout.EAST);

        // 4. Dòng Thành Tiền Cần Thu (Thực thu)
        JPanel pnlRowThanhTien = new JPanel(new BorderLayout());
        pnlRowThanhTien.setOpaque(false);
        JLabel lblTotalTitle = new JLabel("SỐ TIỀN CẦN THANH TOÁN (THỰC THU):");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalTitle.setForeground(new Color(15, 23, 42));
        lblThanhTien = new JLabel(df.format(basePrice));
        lblThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblThanhTien.setForeground(new Color(16, 185, 129));
        pnlRowThanhTien.add(lblTotalTitle, BorderLayout.WEST);
        pnlRowThanhTien.add(lblThanhTien, BorderLayout.EAST);

        details.add(pnlRowGiaGoc);
        details.add(pnlRowGiamGia);
        details.add(pnlRowPolicy);
        details.add(pnlRowThanhTien);

        // 5. Banner giải thích bảo mật theo quyền
        lblSecurityBanner = new JLabel("Đang tải chính sách bảo mật...");
        lblSecurityBanner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSecurityBanner.setOpaque(true);
        lblSecurityBanner.setBorder(new EmptyBorder(6, 10, 6, 10));

        box.add(details, BorderLayout.CENTER);
        box.add(lblSecurityBanner, BorderLayout.SOUTH);

        return box;
    }

    private String getEffectiveRole() {
        return Auth.vaiTro != null ? Auth.vaiTro : "Lễ tân";
    }

    public void loadPackageList() {
        danhSachGoiTap = goiTapDAO.layTatCa();
        Object currentSelected = cbGoi.getSelectedItem();
        cbGoi.removeAllItems();
        if (danhSachGoiTap != null && !danhSachGoiTap.isEmpty()) {
            for (GoiTap gt : danhSachGoiTap) {
                cbGoi.addItem(gt.getTenGoiTap() + " (" + gt.getThoiHanThang() + " Tháng) - " + df.format(gt.getGiaTien()));
            }
            if (currentSelected != null) {
                cbGoi.setSelectedItem(currentSelected);
            }
            if (cbGoi.getSelectedIndex() < 0) {
                cbGoi.setSelectedIndex(0);
            }
        } else {
            cbGoi.addItem("Gói 12 Tháng VIP Diamond (12 Tháng) - 4,500,000 đ");
            cbGoi.addItem("Gói 1 Tháng Tiêu Chuẩn (1 Tháng) - 500,000 đ");
        }
        updatePrice();
    }

    private void loadInitialMember() {
        List<HoiVien> list = hoiVienDAO.layTatCa();
        if (list != null && !list.isEmpty()) {
            // Mặc định nạp thử hội viên VIP007 nếu có trong danh sách
            HoiVien defaultVip = null;
            for (HoiVien hv : list) {
                if ("VIP007".equalsIgnoreCase(hv.getMaTheQR())) {
                    defaultVip = hv;
                    break;
                }
            }
            if (defaultVip != null) {
                displayMember(defaultVip);
                txtSearch.setText(defaultVip.getMaTheQR());
            } else {
                displayMember(list.get(0));
                txtSearch.setText(list.get(0).getMaTheQR() != null ? list.get(0).getMaTheQR() : String.valueOf(list.get(0).getMaHoiVien()));
            }
        } else {
            lblTen.setText("Chưa có hội viên trong hệ thống");
            lblCurrentStatus.setText("Vui lòng nạp dữ liệu mẫu hoặc đăng ký hội viên trước.");
        }
    }

    public void selectQuickMember(String keyword) {
        txtSearch.setText(keyword);
        actionSearch();
    }

    private void displayMember(HoiVien hv) {
        if (hv == null) return;
        this.currentHoiVien = hv;

        String maHV = String.valueOf(hv.getMaHoiVien());
        String maQR = hv.getMaTheQR() != null ? hv.getMaTheQR() : ("HV" + maHV);
        String branch = hv.getChiNhanhDangKy() != null ? hv.getChiNhanhDangKy() : "CN01";

        boolean isMasterUser = "Chủ".equalsIgnoreCase(Auth.vaiTro);
        boolean isVip = VipPolicyService.getInstance().laHoiVienVipDacQuyen(hv.getSoDienThoai(), hv.getHoTen(), maQR);
        String vipBadge = (isVip && isMasterUser) ? " [⭐ HỘI VIÊN VIP ĐẶC QUYỀN]" : "";

        lblTen.setText("<html>" + hv.getHoTen() + "<span style='color:#7c3aed;font-weight:bold;'>" + vipBadge + "</span>"
                + " — Mã HV: " + maHV + " • Mã Thẻ: " + maQR + " (" + branch + ")</html>");

        if (hv.getNgayHetHan() != null) {
            String expStr = sdf.format(hv.getNgayHetHan());
            boolean isExpired = hv.getNgayHetHan().before(new Date());
            if (isExpired) {
                long diff = (System.currentTimeMillis() - hv.getNgayHetHan().getTime()) / (1000 * 60 * 60 * 24);
                lblCurrentStatus.setText("ĐÃ HẾT HẠN vào ngày " + expStr + " (Đã quá hạn " + diff + " ngày — Cần gia hạn ngay!)");
                lblCurrentStatus.setForeground(new Color(239, 68, 68));
            } else {
                long diff = (hv.getNgayHetHan().getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
                lblCurrentStatus.setText("Đang hoạt động — Hạn thẻ đến ngày: " + expStr + " (Còn " + diff + " ngày)");
                lblCurrentStatus.setForeground(new Color(16, 185, 129));
            }
        } else {
            lblCurrentStatus.setText("Chưa kích hoạt hạn thẻ");
            lblCurrentStatus.setForeground(new Color(245, 158, 11));
        }

        updatePrice();
    }

    public void chonHoiVienDeGiaHan(HoiVien hv) {
        if (hv == null) return;
        displayMember(hv);
        if (txtSearch != null) {
            txtSearch.setText(hv.getMaTheQR() != null ? hv.getMaTheQR() : String.valueOf(hv.getMaHoiVien()));
        }
    }

    private void actionSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã hội viên (ví dụ: 6), Mã Thẻ (VIP007) hoặc Họ tên để tra cứu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        HoiVien found = hoiVienDAO.timKiemThongMinh(keyword);
        if (found != null) {
            displayMember(found);
        } else {
            JOptionPane.showMessageDialog(this,
                    "KHÔNG TÌM THẤY hội viên nào khớp với từ khóa: \"" + keyword + "\"!\n"
                            + "Vui lòng kiểm tra lại Mã Hội Viên (ví dụ: 6) hoặc Mã Thẻ (VIP007).",
                    "Không Tìm Thấy", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updatePrice() {
        int idx = cbGoi.getSelectedIndex();
        if (danhSachGoiTap != null && idx >= 0 && idx < danhSachGoiTap.size()) {
            GoiTap selected = danhSachGoiTap.get(idx);
            basePrice = (long) selected.getGiaTien();
        } else {
            basePrice = 4500000;
        }

        String sdt = currentHoiVien != null ? currentHoiVien.getSoDienThoai() : "";
        String hoTen = currentHoiVien != null ? currentHoiVien.getHoTen() : "";
        String maQR = currentHoiVien != null ? currentHoiVien.getMaTheQR() : "";
        String currentRole = getEffectiveRole();

        VipPolicyService.ThongTinGiaHienThi giaInfo = VipPolicyService.getInstance().tinhGiaTheoPhanQuyen(
                currentRole, sdt, hoTen, maQR, basePrice);

        boolean isChu = "Chủ".equalsIgnoreCase(currentRole)
                || currentRole.toLowerCase().contains("chủ")
                || currentRole.toLowerCase().contains("admin");

        if (giaInfo.isVipDacQuyen) {
            if (isChu) {
                // CHỦ HỆ THỐNG: HIỂN THỊ ĐẦY ĐỦ MINH BẠCH GIÁ GỐC VÀ CHIẾT KHẤU 50%
                pnlRowGiaGoc.setVisible(true);
                lblGiaGoc.setText(df.format(giaInfo.giaGoc));
                lblGiaGoc.setForeground(new Color(71, 85, 105));

                pnlRowGiamGia.setVisible(true);
                lblGiamGia.setText("- 50% (" + df.format(giaInfo.soTienGiam) + ") [👑 ĐẶC QUYỀN VIP CỦA CHỦ]");
                lblGiamGia.setForeground(new Color(220, 38, 38));

                lblVipPolicyNotice.setText("👑 VIP ĐẶC QUYỀN CỦA CHỦ PHÒNG - ÁP DỤNG GIẢM 50% TRỰC TIẾP");
                lblVipPolicyNotice.setForeground(new Color(16, 185, 129));

                lblSecurityBanner.setVisible(true);
                lblSecurityBanner.setText("<html>👑 <b>ĐẶC QUYỀN VIP CỦA CHỦ:</b> Đang hiển thị chi tiết giá gốc và mức giảm 50%. Thông tin này được bảo mật và ẩn đối với Lễ tân & Quản lý.</html>");
                lblSecurityBanner.setBackground(new Color(240, 253, 244));
                lblSecurityBanner.setForeground(new Color(22, 101, 52));
                lblSecurityBanner.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(187, 247, 208), 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            } else {
                // LỄ TÂN / QUẢN LÝ: HỆ THỐNG TỰ ĐỘNG BIẾT VÀ TÍNH ĐÚNG MỨC GIÁ, ẨN TOÀN BỘ DẤU VẾT 50%
                pnlRowGiaGoc.setVisible(false); // Ẩn hoàn toàn dòng giá gốc 6M
                pnlRowGiamGia.setVisible(false); // Ẩn hoàn toàn dòng giảm giá -50%

                lblVipPolicyNotice.setText("Hội phí tiêu chuẩn gói tập");
                lblVipPolicyNotice.setForeground(new Color(100, 116, 139));

                lblSecurityBanner.setVisible(false); // ẨN HOÀN TOÀN BANNER BẢO MẬT KHỎI MÀN HÌNH LỄ TÂN & QUẢN LÝ
            }
        } else {
            // HỘI VIÊN TIÊU CHUẨN (KHÔNG PHẢI VIP)
            pnlRowGiaGoc.setVisible(isChu);
            if (isChu) {
                lblGiaGoc.setText(df.format(basePrice));
                lblGiaGoc.setForeground(new Color(71, 85, 105));
            }

            pnlRowGiamGia.setVisible(false);

            lblVipPolicyNotice.setText("Hội phí tiêu chuẩn niêm yết");
            lblVipPolicyNotice.setForeground(new Color(100, 116, 139));

            lblSecurityBanner.setVisible(false);
        }

        lblThanhTien.setText(df.format(giaInfo.soTienThucThu));

        // Revalidate layout to accommodate hidden/shown rows smoothly
        pnlRowGiaGoc.revalidate();
        pnlRowGiaGoc.repaint();
        pnlRowGiamGia.revalidate();
        pnlRowGiamGia.repaint();
    }

    private void confirmRenewal() {
        if (currentHoiVien == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tra cứu và chọn một hội viên thực tế trước khi gia hạn!", "Chưa chọn hội viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int months = 1;
        String packageName = "Gói Tiêu Chuẩn";
        int idx = cbGoi.getSelectedIndex();
        if (danhSachGoiTap != null && idx >= 0 && idx < danhSachGoiTap.size()) {
            GoiTap selected = danhSachGoiTap.get(idx);
            months = selected.getThoiHanThang();
            packageName = selected.getTenGoiTap();
        }

        // Tính ngày hết hạn mới: cộng dồn nếu còn hạn, hoặc cộng từ hôm nay nếu đã quá hạn
        Calendar cal = Calendar.getInstance();
        if (currentHoiVien.getNgayHetHan() != null && currentHoiVien.getNgayHetHan().after(new Date())) {
            cal.setTime(currentHoiVien.getNgayHetHan());
        } else {
            cal.setTime(new Date());
        }
        cal.add(Calendar.MONTH, months);
        Date newExpireDate = cal.getTime();
        String newExpire = sdf.format(newExpireDate);

        String sdt = currentHoiVien.getSoDienThoai();
        String hoTen = currentHoiVien.getHoTen();
        String maQR = currentHoiVien.getMaTheQR();
        String effectiveRole = getEffectiveRole();

        boolean isChu = "Chủ".equalsIgnoreCase(effectiveRole)
                || effectiveRole.toLowerCase().contains("chủ")
                || effectiveRole.toLowerCase().contains("admin");

        VipPolicyService.ThongTinGiaHienThi giaInfo = VipPolicyService.getInstance().tinhGiaTheoPhanQuyen(
                effectiveRole, sdt, hoTen, maQR, basePrice);

        // QUY TẮC BẢO MẬT HÓA ĐƠN:
        // Chủ: Xem đầy đủ Giá Gốc và Chiết Khấu VIP (-50%)
        // Lễ tân: Giá gốc = Thực thu, Giảm giá = 0, hoàn toàn không thấy mức giảm 50%
        double displayGiaGoc = isChu ? giaInfo.giaGoc : giaInfo.soTienThucThu;
        double displayGiamGia = isChu ? giaInfo.soTienGiam : 0.0;
        double finalPrice = giaInfo.soTienThucThu;

        String maHD = "HD" + (System.currentTimeMillis() % 1000000);
        String pkgName = cbGoi.getSelectedItem() != null ? cbGoi.getSelectedItem().toString() : "Gói tập";

        String itemTitle = (isChu && giaInfo.isVipDacQuyen)
                ? "Gia hạn " + pkgName + " (Hạn mới: " + newExpire + ") [Đặc quyền VIP 50%]"
                : "Gia hạn " + pkgName + " (Hạn mới: " + newExpire + ")";

        java.util.List<com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang> items = new java.util.ArrayList<>();
        items.add(new com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang(itemTitle, 1, displayGiaGoc, finalPrice));

        Window parent = SwingUtilities.getWindowAncestor(this);
        com.nhom12.gym.gui.ThanhToanHoaDonDialog dialog = new com.nhom12.gym.gui.ThanhToanHoaDonDialog(
                parent,
                maHD,
                currentHoiVien.getHoTen() + " (" + (currentHoiVien.getMaTheQR() != null ? currentHoiVien.getMaTheQR() : ("HV" + currentHoiVien.getMaHoiVien())) + ")",
                "Gia hạn " + pkgName,
                displayGiaGoc,
                displayGiamGia,
                finalPrice,
                currentHoiVien.getChiNhanhDangKy() != null ? currentHoiVien.getChiNhanhDangKy() : Auth.getChiNhanhHienTai(),
                items,
                success -> {
                    if (success) {
                        currentHoiVien.setNgayHetHan(newExpireDate);
                        currentHoiVien.setTrangThai("Active");
                        currentHoiVien.setSoTienDaThanhToan(finalPrice);
                        hoiVienDAO.capNhat(currentHoiVien);

                        // Tạo và lưu hóa đơn thực tế
                        HoaDon hd = new HoaDon(
                                maHD,
                                currentHoiVien.getMaHoiVien(),
                                currentHoiVien.getHoTen(),
                                "Gia hạn " + pkgName,
                                displayGiaGoc,
                                displayGiamGia,
                                finalPrice,
                                cbThanhToan.getSelectedItem() != null ? cbThanhToan.getSelectedItem().toString() : "Chuyển khoản (VietQR)",
                                Auth.tenDangNhap != null ? Auth.tenDangNhap : "letan1",
                                currentHoiVien.getChiNhanhDangKy() != null ? currentHoiVien.getChiNhanhDangKy() : "CN01 - Cơ sở 1 (Quận 1)",
                                new Date(),
                                "Đã thanh toán",
                                "Gia hạn thẻ thành công (Hạn mới: " + newExpire + ")"
                        );
                        hoaDonDAO.them(hd);

                        // Ghi vết kiểm toán (Audit Trail)
                        String auditDetail = "Gia hạn gói [" + pkgName + "] cho HV: " + currentHoiVien.getHoTen()
                                + " (" + currentHoiVien.getMaTheQR() + ") - Thực thu: " + df.format(finalPrice)
                                + (giaInfo.isVipDacQuyen ? " [Áp dụng VIP Ưu đãi nội bộ]" : "");
                        nhatKyDAO.ghiLog(
                                Auth.tenDangNhap != null ? Auth.tenDangNhap : "letan1",
                                effectiveRole,
                                "GIA_HAN_HOI_VIEN",
                                auditDetail
                        );

                        // Kích hoạt đồng bộ thời gian thực đến toàn bộ Dashboard & Biểu đồ doanh thu
                        DataSyncManager.fireDataChanged(DataSyncManager.DataType.ALL);

                        displayMember(currentHoiVien);

                        JOptionPane.showMessageDialog(this,
                                "GIA HẠN THÀNH CÔNG!\n"
                                        + "• Hội viên: " + currentHoiVien.getHoTen() + "\n"
                                        + "• Hạn thẻ mới: " + newExpire + "\n"
                                        + "• Số tiền thanh toán: " + df.format(finalPrice) + "\n"
                                        + "• Mã hóa đơn: " + maHD + " (Doanh thu đã được cập nhật liên tục)",
                                "Gia Hạn Hoàn Tất", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
        );
        dialog.setVisible(true);
    }

    private void resetForm() {
        txtSearch.setText("");
        cbGoi.setSelectedIndex(0);
        cbThanhToan.setSelectedIndex(0);
        txtNgay.setText(sdf.format(new Date()));
        loadInitialMember();
    }
}
