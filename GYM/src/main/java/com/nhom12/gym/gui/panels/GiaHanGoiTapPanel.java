package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.GoiTapDAO;
import com.nhom12.gym.dao.HoaDonDAO;
import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.GoiTap;
import com.nhom12.gym.entity.HoaDon;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.VipPolicyService;
import com.nhom12.gym.util.Auth;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import javax.swing.SwingUtilities;
import java.awt.Insets;
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

public class GiaHanGoiTapPanel extends JPanel {
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final GoiTapDAO goiTapDAO = new GoiTapDAO();
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private List<GoiTap> danhSachGoiTap = new ArrayList<>();

    private JTextField txtSearch;
    private JButton btnSearch;
    private JLabel lblTen;
    private JLabel lblCurrentStatus;
    private JComboBox<String> cbGoi;
    private JTextField txtNgay;
    private JComboBox<String> cbThanhToan;
    private JLabel lblVipPolicyNotice;
    private JLabel lblThanhTien;

    private JButton btnCancel;
    private JButton btnConfirm;

    // Hội viên đang được chọn thực tế từ hệ thống
    private HoiVien currentHoiVien = null;
    private long basePrice = 2500000;

    public GiaHanGoiTapPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("GIA HẠN & NÂNG CẤP GÓI TẬP HỘI VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Tra cứu hội viên thực tế trong danh sách (theo Mã HV, Mã QR hoặc Tên) và xuất hóa đơn gia hạn");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblPrompt = new JLabel("Tra cứu Hội Viên (Mã HV / Mã QR / Họ Tên):");
        lblPrompt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.putClientProperty("JTextField.placeholderText", "Ví dụ: 12, HV861, QR001 hoặc Lê Anh Duy");

        btnSearch = new JButton("Tra Cứu");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(new Color(37, 99, 235));
        btnSearch.setForeground(Color.WHITE);

        searchPanel.add(lblPrompt);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        int r = 0;
        // Member Info Row
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel l1 = new JLabel("Hội Viên Đang Chọn:"); l1.setFont(fontLabel);
        formPanel.add(l1, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        lblTen = new JLabel("Đang tải dữ liệu hội viên...");
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTen.setForeground(new Color(30, 41, 59));
        formPanel.add(lblTen, gbc);

        // Current status row
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lStatus = new JLabel("Hạn Thẻ Hiện Tại:"); lStatus.setFont(fontLabel);
        formPanel.add(lStatus, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        lblCurrentStatus = new JLabel("---");
        lblCurrentStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCurrentStatus.setForeground(new Color(100, 116, 139));
        formPanel.add(lblCurrentStatus, gbc);

        // New Package Selection
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel l2 = new JLabel("Chọn Gói Gia Hạn:"); l2.setFont(fontLabel);
        formPanel.add(l2, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        cbGoi = new JComboBox<>();
        cbGoi.setFont(fontInput);
        formPanel.add(cbGoi, gbc);

        // Start Date
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel l3 = new JLabel("Ngày Bắt Đầu Gia Hạn:"); l3.setFont(fontLabel);
        formPanel.add(l3, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        txtNgay = new JTextField(sdf.format(new Date()));
        txtNgay.setFont(fontInput);
        formPanel.add(txtNgay, gbc);

        // Payment Method
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel l4 = new JLabel("Phương Thức Thanh Toán:"); l4.setFont(fontLabel);
        formPanel.add(l4, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        cbThanhToan = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản (VietQR)", "Quẹt thẻ POS"});
        cbThanhToan.setFont(fontInput);
        formPanel.add(cbThanhToan, gbc);

        // VIP Policy Notice (Yêu Cầu Ẩn 1: Bảo mật giá thực với Lễ tân)
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lPolicy = new JLabel("Chính Sách Khách Hàng:"); lPolicy.setFont(fontLabel);
        formPanel.add(lPolicy, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        lblVipPolicyNotice = new JLabel("Hội phí tiêu chuẩn niêm yết");
        lblVipPolicyNotice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVipPolicyNotice.setForeground(new Color(100, 116, 139));
        formPanel.add(lblVipPolicyNotice, gbc);

        // Total Cost Display
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lTotal = new JLabel("Thành Tiền Thanh Toán:"); lTotal.setFont(fontLabel);
        formPanel.add(lTotal, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        lblThanhTien = new JLabel(df.format(basePrice));
        lblThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblThanhTien.setForeground(new Color(16, 185, 129));
        formPanel.add(lblThanhTien, gbc);

        // Action Buttons
        r++; gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btnGroup.setOpaque(false);

        btnCancel = new JButton("Làm Mới");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBackground(new Color(100, 116, 139));
        btnCancel.setForeground(Color.WHITE);

        btnConfirm = new JButton("Chốt Gia Hạn & In Hóa Đơn");
        btnConfirm.setBackground(new Color(16, 185, 129));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnGroup.add(btnCancel);
        btnGroup.add(btnConfirm);
        formPanel.add(btnGroup, gbc);

        r++; gbc.gridy = r; gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);

        centerPanel.add(formPanel, BorderLayout.CENTER);
        this.add(centerPanel, BorderLayout.CENTER);

        // Action Listeners
        loadPackageList();
        btnSearch.addActionListener(e -> actionSearch());
        txtSearch.addActionListener(e -> actionSearch());
        cbGoi.addActionListener(e -> updatePrice());
        btnCancel.addActionListener(e -> resetForm());
        btnConfirm.addActionListener(e -> confirmRenewal());

        GoiTapDAO.addChangeListener(this::loadPackageList);
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            if (type == com.nhom12.gym.service.DataSyncManager.DataType.GOI_TAP || type == com.nhom12.gym.service.DataSyncManager.DataType.ALL) {
                loadPackageList();
            }
        });
        loadInitialMember();
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
            cbGoi.addItem("Gói 1 Tháng Tiêu Chuẩn (1 Tháng) - 500,000 đ");
        }
        updatePrice();
    }

    private void loadInitialMember() {
        List<HoiVien> list = hoiVienDAO.layTatCa();
        if (list != null && !list.isEmpty()) {
            displayMember(list.get(0));
            txtSearch.setText(list.get(0).getMaTheQR() != null ? list.get(0).getMaTheQR() : String.valueOf(list.get(0).getMaHoiVien()));
        } else {
            lblTen.setText("Chưa có hội viên trong hệ thống");
            lblCurrentStatus.setText("Vui lòng đăng ký hội viên mới trước.");
        }
    }

    private void displayMember(HoiVien hv) {
        if (hv == null) return;
        this.currentHoiVien = hv;

        String maHV = String.valueOf(hv.getMaHoiVien());
        String maQR = hv.getMaTheQR() != null ? hv.getMaTheQR() : ("HV" + maHV);
        String branch = hv.getChiNhanhDangKy() != null ? hv.getChiNhanhDangKy() : "CN01";

        lblTen.setText(hv.getHoTen() + " — Mã HV: " + maHV + " • Mã Thẻ QR: " + maQR + " (" + branch + ")");

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

    private void actionSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã hội viên (ví dụ: 12), Mã QR (ví dụ: HV861, QR001) hoặc Họ tên để tra cứu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tra cứu thực tế từ CSDL / DAO
        HoiVien found = hoiVienDAO.timKiemThongMinh(keyword);

        if (found != null) {
            displayMember(found);
            JOptionPane.showMessageDialog(this, 
                    "Đã tìm thấy hội viên: " + found.getHoTen() 
                    + "\nMã Hội Viên: " + found.getMaHoiVien() 
                    + "\nMã Thẻ QR: " + found.getMaTheQR() 
                    + "\nChi Nhánh: " + found.getChiNhanhDangKy(), 
                    "Tra Cứu Thành Công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                    "KHÔNG TÌM THẤY hội viên nào khớp với từ khóa: \"" + keyword + "\"!\n"
                    + "Vui lòng kiểm tra lại:\n"
                    + "• Mã Hội Viên (ví dụ: 1, 2, 12...)\n"
                    + "• Mã Thẻ QR (ví dụ: QR001, HV861...)\n"
                    + "• Hoặc Họ và Tên trong Danh Sách Hội Viên.", 
                    "Không Tìm Thấy", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updatePrice() {
        int idx = cbGoi.getSelectedIndex();
        if (danhSachGoiTap != null && idx >= 0 && idx < danhSachGoiTap.size()) {
            GoiTap selected = danhSachGoiTap.get(idx);
            basePrice = (long) selected.getGiaTien();
        } else {
            basePrice = 500000;
        }

        String sdt = currentHoiVien != null ? currentHoiVien.getSoDienThoai() : "";
        String hoTen = currentHoiVien != null ? currentHoiVien.getHoTen() : "";
        String maQR = currentHoiVien != null ? currentHoiVien.getMaTheQR() : "";
        String currentRole = Auth.vaiTro != null ? Auth.vaiTro : "Lễ tân";

        VipPolicyService.ThongTinGiaHienThi giaInfo = VipPolicyService.getInstance().tinhGiaTheoPhanQuyen(
                currentRole, sdt, hoTen, maQR, basePrice);

        lblThanhTien.setText(df.format(giaInfo.soTienThucThu));

        if (lblVipPolicyNotice != null) {
            if (giaInfo.isVipDacQuyen) {
                lblVipPolicyNotice.setText(giaInfo.moTaGiaHienThi);
                lblVipPolicyNotice.setForeground(new Color(16, 185, 129));
            } else {
                lblVipPolicyNotice.setText("Hội phí tiêu chuẩn niêm yết");
                lblVipPolicyNotice.setForeground(new Color(100, 116, 139));
            }
        }
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
        } else {
            months = 1;
        }

        // Tính ngày hết hạn mới: cộng dồn nếu còn hạn, hoặc từ hôm nay nếu đã quá hạn
        Calendar cal = Calendar.getInstance();
        if (currentHoiVien.getNgayHetHan() != null && currentHoiVien.getNgayHetHan().after(new Date())) {
            cal.setTime(currentHoiVien.getNgayHetHan());
        } else {
            cal.setTime(new Date());
        }
        cal.add(Calendar.MONTH, months);
        Date newExpireDate = cal.getTime();
        String newExpire = sdf.format(newExpireDate);

        // Cập nhật thực tế cho Hội Viên
        String sdt = currentHoiVien.getSoDienThoai();
        String hoTen = currentHoiVien.getHoTen();
        String maQR = currentHoiVien.getMaTheQR();
        String currentRole = Auth.vaiTro != null ? Auth.vaiTro : "Lễ tân";

        VipPolicyService.ThongTinGiaHienThi giaInfo = VipPolicyService.getInstance().tinhGiaTheoPhanQuyen(
                currentRole, sdt, hoTen, maQR, basePrice);

        double displayGiaGoc = giaInfo.giaGoc;
        double displayGiamGia = giaInfo.soTienGiam;
        double finalPrice = giaInfo.soTienThucThu;

        String maHD = "HD" + (System.currentTimeMillis() % 1000000);
        String pkgName = cbGoi.getSelectedItem() != null ? cbGoi.getSelectedItem().toString() : "Gói tập";

        java.util.List<com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang> items = new java.util.ArrayList<>();
        items.add(new com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang("Gia hạn " + pkgName + " (Hạn mới: " + newExpire + ")", 1, displayGiaGoc, finalPrice));

        Window parent = SwingUtilities.getWindowAncestor(this);
        com.nhom12.gym.gui.ThanhToanHoaDonDialog dialog = new com.nhom12.gym.gui.ThanhToanHoaDonDialog(
                parent,
                maHD,
                currentHoiVien.getHoTen() + " (" + currentHoiVien.getMaTheQR() + ")",
                "Gia hạn " + pkgName,
                displayGiaGoc,
                displayGiamGia,
                finalPrice,
                items,
                success -> {
                    if (success) {
                        currentHoiVien.setNgayHetHan(newExpireDate);
                        currentHoiVien.setTrangThai("Hoạt động");
                        currentHoiVien.setSoTienDaThanhToan(finalPrice);
                        hoiVienDAO.capNhat(currentHoiVien);

                        // Đồng bộ tạo Hóa đơn vào HoaDonDAO cho toàn hệ thống
                        com.nhom12.gym.entity.HoaDon hd = new com.nhom12.gym.entity.HoaDon(
                                maHD,
                                currentHoiVien.getMaHoiVien(),
                                currentHoiVien.getHoTen(),
                                "Gia hạn " + pkgName,
                                displayGiaGoc,
                                displayGiamGia,
                                finalPrice,
                                cbThanhToan.getSelectedItem() != null ? cbThanhToan.getSelectedItem().toString() : "Tiền mặt",
                                Auth.tenDangNhap != null ? Auth.tenDangNhap : "letan1",
                                currentHoiVien.getChiNhanhDangKy() != null ? currentHoiVien.getChiNhanhDangKy() : "CN01 - Cơ sở 1 (Quận 1)",
                                new Date(),
                                "Đã thanh toán",
                                "Gia hạn thẻ thành công (Hạn mới: " + newExpire + ")"
                        );
                        com.nhom12.gym.dao.HoaDonDAO.getInstance().them(hd);

                        displayMember(currentHoiVien);
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
        updatePrice();
        loadInitialMember();
    }
}
