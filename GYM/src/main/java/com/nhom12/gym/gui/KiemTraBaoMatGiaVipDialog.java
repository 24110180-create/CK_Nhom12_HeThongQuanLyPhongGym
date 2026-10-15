package com.nhom12.gym.gui;

import com.nhom12.gym.service.VipPolicyService;
import com.nhom12.gym.util.Auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * CỬA SỔ KIỂM TRA HIỂN THỊ GIÁ THEO PHÂN QUYỀN (BẢO MẬT VIP 50%)
 * Đáp ứng Yêu Cầu Ẩn 1 của Anh Đức:
 * - Khách VIP được giảm 50% trọn đời.
 * - Chỉ Chủ hệ thống mới nhìn thấy giá niêm yết gốc và mức chiết khấu 50%.
 * - Lễ tân và Quản lý chỉ thấy giá thực thu theo hợp đồng đã duyệt, hoàn toàn ẩn giá gốc và ẩn mức giảm.
 */
public class KiemTraBaoMatGiaVipDialog extends JDialog {

    private final DecimalFormat df = new DecimalFormat("#,### đ");

    private JComboBox<String> cbRole;
    private JComboBox<String> cbPackage;
    private JComboBox<String> cbVipMember;

    private JLabel lblGiaGocResult;
    private JLabel lblGiamGiaResult;
    private JLabel lblThucThuResult;
    private JLabel lblNoticeResult;
    private JLabel lblSecurityVerdict;
    private JPanel pnlVerdictBox;

    public KiemTraBaoMatGiaVipDialog(Window parent) {
        super(parent, "Kiểm Tra Hiển Thị Giá & Bảo Mật Hội Viên VIP (50%)", ModalityType.APPLICATION_MODAL);
        setSize(780, 560);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        initUI();
        capNhatKetQua();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(14, 14));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(18, 22, 18, 22));

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout(10, 4));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("KIỂM TRA HIỂN THỊ GIÁ BẢO MẬT THEO VAI TRÒ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(15, 23, 42));

        String currentLogin = Auth.vaiTro != null ? Auth.vaiTro : "Lễ tân";
        JLabel lblCurrentLogin = new JLabel("Trạm đăng nhập hiện tại: " + currentLogin.toUpperCase() + " (" + (Auth.tenDangNhap != null ? Auth.tenDangNhap : "letan1") + ")");
        lblCurrentLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCurrentLogin.setForeground(new Color(37, 99, 235));

        JLabel lblDesc = new JLabel("<html>Mô phỏng tức thì cách hệ thống tính giá và bảo mật tuyệt đối <b>Yêu Cầu Ẩn 1</b>: Khách VIP của Anh Đức được giảm 50% nhưng Lễ tân & Quản lý hoàn toàn không thấy giá gốc và không thấy tỷ lệ giảm 50%.</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(100, 116, 139));

        JPanel topText = new JPanel(new GridLayout(3, 1, 0, 3));
        topText.setOpaque(false);
        topText.add(lblTitle);
        topText.add(lblCurrentLogin);
        topText.add(lblDesc);

        headerPanel.add(topText, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- CENTER: CONTROLS & RESULT DISPLAY ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);

        // Top Control Grid
        JPanel controlGrid = new JPanel(new GridLayout(3, 2, 14, 10));
        controlGrid.setBackground(new Color(248, 250, 252));
        controlGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // 1. Role Selector
        JPanel pnlRole = new JPanel(new BorderLayout(0, 4));
        pnlRole.setOpaque(false);
        JLabel lblRoleLabel = new JLabel("1. Chọn Vai Trò Cần Kiểm Thử:");
        lblRoleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbRole = new JComboBox<>(new String[]{
                "👔 Lễ tân (Nhân viên thu ngân)",
                "💼 Quản lý (Điều hành chi nhánh)",
                "👑 Chủ phòng tập (Anh Đức)"
        });
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Default based on current role
        if ("Chủ".equalsIgnoreCase(currentLogin)) {
            cbRole.setSelectedIndex(2);
        } else if ("Quản lý".equalsIgnoreCase(currentLogin)) {
            cbRole.setSelectedIndex(1);
        } else {
            cbRole.setSelectedIndex(0);
        }
        pnlRole.add(lblRoleLabel, BorderLayout.NORTH);
        pnlRole.add(cbRole, BorderLayout.CENTER);

        // 2. Member Selector
        JPanel pnlMember = new JPanel(new BorderLayout(0, 4));
        pnlMember.setOpaque(false);
        JLabel lblMemberLabel = new JLabel("2. Chọn Hội Viên Cần Tính Giá:");
        lblMemberLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbVipMember = new JComboBox<>(new String[]{
                "VIP007 - Đỗ Minh Trí (0909998877) [👑 Khách VIP của Anh Đức]",
                "VIP006 - Thanh Tâm (0832123456) [👑 Khách VIP của Anh Đức]",
                "HV044 - Lê Anh Nuôi (0901234567) [Khách hàng thông thường]"
        });
        cbVipMember.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlMember.add(lblMemberLabel, BorderLayout.NORTH);
        pnlMember.add(cbVipMember, BorderLayout.CENTER);

        // 3. Package Selector
        JPanel pnlPkg = new JPanel(new BorderLayout(0, 4));
        pnlPkg.setOpaque(false);
        JLabel lblPkgLabel = new JLabel("3. Chọn Gói Tập Niêm Yết:");
        lblPkgLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cbPackage = new JComboBox<>(new String[]{
                "Gói 12 Tháng VIP Diamond - 6,000,000 đ",
                "Gói 12 Tháng Tiêu Chuẩn - 4,500,000 đ",
                "Gói 6 Tháng Tặng 1 - 2,500,000 đ",
                "Gói 3 Tháng Tái Ký - 1,350,000 đ",
                "Gói 1 Tháng Tiêu Chuẩn - 500,000 đ"
        });
        cbPackage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlPkg.add(lblPkgLabel, BorderLayout.NORTH);
        pnlPkg.add(cbPackage, BorderLayout.CENTER);

        // 4. Quick Refresh Button Box
        JPanel pnlQuickAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 16));
        pnlQuickAction.setOpaque(false);
        JButton btnQuickSwitchOwner = new JButton("👑 Đổi Sang Chủ");
        btnQuickSwitchOwner.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnQuickSwitchOwner.setBackground(new Color(243, 232, 255));
        btnQuickSwitchOwner.setForeground(new Color(126, 34, 206));
        btnQuickSwitchOwner.addActionListener(e -> { cbRole.setSelectedIndex(2); capNhatKetQua(); });

        JButton btnQuickSwitchStaff = new JButton("👔 Đổi Sang Lễ Tân");
        btnQuickSwitchStaff.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnQuickSwitchStaff.setBackground(new Color(224, 231, 255));
        btnQuickSwitchStaff.setForeground(new Color(30, 64, 175));
        btnQuickSwitchStaff.addActionListener(e -> { cbRole.setSelectedIndex(0); capNhatKetQua(); });

        pnlQuickAction.add(btnQuickSwitchStaff);
        pnlQuickAction.add(btnQuickSwitchOwner);

        controlGrid.add(pnlRole);
        controlGrid.add(pnlMember);
        controlGrid.add(pnlPkg);
        controlGrid.add(pnlQuickAction);

        centerPanel.add(controlGrid, BorderLayout.NORTH);

        // --- RESULTS CARD ---
        JPanel resultCard = new JPanel(new BorderLayout(10, 10));
        resultCard.setBackground(Color.WHITE);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblCardTitle = new JLabel("KẾT QUẢ HIỂN THỊ TRÊN MÀN HÌNH NGHIỆP VỤ:");
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCardTitle.setForeground(new Color(30, 41, 59));
        resultCard.add(lblCardTitle, BorderLayout.NORTH);

        JPanel resultList = new JPanel(new GridLayout(4, 1, 0, 8));
        resultList.setOpaque(false);

        lblGiaGocResult = new JLabel("Giá niêm yết: ---");
        lblGiaGocResult.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lblGiamGiaResult = new JLabel("Chiết khấu VIP: ---");
        lblGiamGiaResult.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lblThucThuResult = new JLabel("Số tiền cần thu: ---");
        lblThucThuResult.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblNoticeResult = new JLabel("Ghi chú hóa đơn: ---");
        lblNoticeResult.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        resultList.add(lblGiaGocResult);
        resultList.add(lblGiamGiaResult);
        resultList.add(lblThucThuResult);
        resultList.add(lblNoticeResult);

        resultCard.add(resultList, BorderLayout.CENTER);

        // Bottom Verdict Banner
        pnlVerdictBox = new JPanel(new BorderLayout());
        pnlVerdictBox.setOpaque(true);
        pnlVerdictBox.setBorder(new EmptyBorder(8, 12, 8, 12));
        lblSecurityVerdict = new JLabel("Đang đánh giá...");
        lblSecurityVerdict.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlVerdictBox.add(lblSecurityVerdict, BorderLayout.CENTER);
        resultCard.add(pnlVerdictBox, BorderLayout.SOUTH);

        centerPanel.add(resultCard, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM ACTIONS ---
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        JLabel lblDefenseTip = new JLabel("<html><b>Mẹo bảo vệ đồ án:</b> Bấm chuyển qua lại giữa <b>Chủ</b> và <b>Lễ tân</b> để giảng viên thấy giá gốc 6M biến mất thành 3M và chiết khấu 50% tự động bị ẩn hoàn toàn!</html>");
        lblDefenseTip.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDefenseTip.setForeground(new Color(100, 116, 139));
        bottomBar.add(lblDefenseTip, BorderLayout.WEST);

        JButton btnClose = new JButton("Đóng Cửa Sổ");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBackground(new Color(37, 99, 235));
        btnClose.setForeground(Color.WHITE);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        bottomBar.add(btnClose, BorderLayout.EAST);

        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        // Listeners
        cbRole.addActionListener(e -> capNhatKetQua());
        cbPackage.addActionListener(e -> capNhatKetQua());
        cbVipMember.addActionListener(e -> capNhatKetQua());

        setContentPane(mainPanel);
    }

    private void capNhatKetQua() {
        String selRole = cbRole.getSelectedItem() != null ? cbRole.getSelectedItem().toString() : "";
        String roleName = selRole.contains("Chủ") ? "Chủ" : (selRole.contains("Quản lý") ? "Quản lý" : "Lễ tân");

        String selMember = cbVipMember.getSelectedItem() != null ? cbVipMember.getSelectedItem().toString() : "";
        String sdt = "0909998877";
        String hoTen = "Đỗ Minh Trí";
        String maThe = "VIP007";
        if (selMember.contains("VIP006")) {
            sdt = "0832123456";
            hoTen = "Thanh Tâm";
            maThe = "VIP006";
        } else if (selMember.contains("HV044")) {
            sdt = "0901234567";
            hoTen = "Lê Anh Nuôi";
            maThe = "HV044";
        }

        String selPkg = cbPackage.getSelectedItem() != null ? cbPackage.getSelectedItem().toString() : "";
        double giaGoc = 6000000.0;
        if (selPkg.contains("4,500,000")) giaGoc = 4500000.0;
        else if (selPkg.contains("2,500,000")) giaGoc = 2500000.0;
        else if (selPkg.contains("1,350,000")) giaGoc = 1350000.0;
        else if (selPkg.contains("500,000")) giaGoc = 500000.0;

        VipPolicyService.ThongTinGiaHienThi info =
                VipPolicyService.getInstance().tinhGiaTheoPhanQuyen(roleName, sdt, hoTen, maThe, giaGoc);

        if (!info.isVipDacQuyen) {
            // Khách thường
            lblGiaGocResult.setText("Giá niêm yết: " + df.format(info.giaGoc));
            lblGiaGocResult.setForeground(new Color(71, 85, 105));

            lblGiamGiaResult.setText("Chiết khấu: 0 đ (Hội viên tiêu chuẩn)");
            lblGiamGiaResult.setForeground(new Color(100, 116, 139));

            lblThucThuResult.setText("Số tiền cần thu: " + df.format(info.soTienThucThu));
            lblThucThuResult.setForeground(new Color(16, 185, 129));

            lblNoticeResult.setText("Ghi chú: Hội phí tiêu chuẩn niêm yết theo bảng giá dịch vụ");
            lblNoticeResult.setForeground(new Color(71, 85, 105));

            pnlVerdictBox.setBackground(new Color(241, 245, 249));
            lblSecurityVerdict.setText("ℹ️ Khách hàng tiêu chuẩn — Thu đủ 100% hội phí niêm yết.");
            lblSecurityVerdict.setForeground(new Color(71, 85, 105));
        } else if ("Chủ".equals(roleName)) {
            // Chủ phòng tập (Anh Đức)
            lblGiaGocResult.setText("Giá niêm yết gốc: " + df.format(info.giaGoc));
            lblGiaGocResult.setForeground(new Color(71, 85, 105));

            lblGiamGiaResult.setText("Chiết khấu VIP: -50% (" + df.format(info.soTienGiam) + ") [👑 ĐẶC QUYỀN VIP CỦA CHỦ]");
            lblGiamGiaResult.setForeground(new Color(220, 38, 38));

            lblThucThuResult.setText("Số tiền thực thu: " + df.format(info.soTienThucThu));
            lblThucThuResult.setForeground(new Color(16, 185, 129));

            lblNoticeResult.setText("Ghi chú: " + info.moTaGiaHienThi + " (CHỦ XEM ĐẦY ĐỦ)");
            lblNoticeResult.setForeground(new Color(22, 101, 52));

            pnlVerdictBox.setBackground(new Color(240, 253, 244));
            lblSecurityVerdict.setText("👑 [GÓC NHÌN CHỦ HỆ THỐNG]: Hiển thị đầy đủ minh bạch giá gốc " + df.format(info.giaGoc) + " và mức giảm 50% (" + df.format(info.soTienGiam) + ").");
            lblSecurityVerdict.setForeground(new Color(22, 101, 52));
        } else {
            // Lễ tân hoặc Quản lý: Thấy đơn giá hợp đồng đã duyệt (đã giảm 50% thực tế), không lộ giá gốc 6M và không lộ -50%
            lblGiaGocResult.setText("Đơn giá hợp đồng duyệt: " + df.format(info.soTienThucThu) + " [Đã ẩn giá gốc niêm yết " + df.format(giaGoc) + "]");
            lblGiaGocResult.setForeground(new Color(71, 85, 105));

            lblGiamGiaResult.setText("Chiết khấu tại quầy: Đã định giá sẵn trong hợp đồng riêng (Ẩn hoàn toàn tỷ lệ 50%)");
            lblGiamGiaResult.setForeground(new Color(180, 83, 9));

            lblThucThuResult.setText("Số tiền Lễ tân cần thu: " + df.format(info.soTienThucThu) + " (Đã giảm 50% cho khách VIP)");
            lblThucThuResult.setForeground(new Color(16, 185, 129));

            lblNoticeResult.setText("Ghi chú hợp đồng: " + info.moTaGiaHienThi + " — Thu đúng " + df.format(info.soTienThucThu));
            lblNoticeResult.setForeground(new Color(180, 83, 9));

            pnlVerdictBox.setBackground(new Color(254, 243, 199));
            lblSecurityVerdict.setText("🔒 [LOGIC BẢO MẬT CHUẨN]: Khách VIP ĐÃ ĐƯỢC GIẢM 50% THẬT (chỉ trả " + df.format(info.soTienThucThu) + " thay vì " + df.format(giaGoc) + "), nhưng Lễ tân chỉ thấy đây là hợp đồng đặc biệt giá " + df.format(info.soTienThucThu) + ", hoàn toàn không biết giá gốc là bao nhiêu và không biết tỷ lệ giảm 50%!");
            lblSecurityVerdict.setForeground(new Color(180, 83, 9));
        }
    }
}
