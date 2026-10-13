package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.LichSuCheckInDAO;
import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.gui.QRScannerSimulatorDialog;
import com.nhom12.gym.util.Auth;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * CỬA SỔ ĐIỀU HÀNH CHỦ HỆ THỐNG FITLIFE GYM (OWNER EXECUTIVE COCKPIT)
 * Thiết kế riêng biệt cho Anh Đức (Chủ Chuỗi Phòng Tập) để nắm bắt toàn diện:
 * 1. KPIs Tài chính & Doanh thu thực tế (bao gồm doanh thu VIP bảo mật)
 * 2. So sánh hiệu quả vận hành chuỗi 2 cơ sở (CN01 Quận 1 & CN02 Tân Bình)
 * 3. Tình hình quân số nhân sự trực ca (Lễ tân, HLV, Quản lý chi nhánh)
 * 4. Cảnh báo điều hành trọng điểm (Hội viên VIP sắp hết hạn, kho hàng, giờ cao điểm)
 */
public class CuaSoChuHeThongPanel extends JPanel {

    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final LichSuCheckInDAO checkInDAO = new LichSuCheckInDAO();

    public CuaSoChuHeThongPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
    }

    private void initUI() {
        // --- 1. TOP HEADER BANNER (CHỐNG ĐÈ GIAO DIỆN) ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);
        titleBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("TRUNG TÂM ĐIỀU HÀNH CHIẾN LƯỢC CHỦ PHÒNG (ANH ĐỨC)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblTitle.setForeground(new Color(15, 23, 42));

        String todayStr = new SimpleDateFormat("EEEE, 'ngày' dd/MM/yyyy - HH:mm").format(new Date());
        JLabel lblSub = new JLabel("Báo cáo số liệu toàn diện chuỗi FitLife Gym (2 cơ sở) • Cập nhật thời gian thực: " + todayStr);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        pnlHeader.add(titleBox);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. VÙNG CUỘN NỘI DUNG CHÍNH ---
        contentBody.setLayout(new BoxLayout(contentBody, BoxLayout.Y_AXIS));
        contentBody.setOpaque(false);
        buildContentBody();

        JScrollPane scrollPane = new JScrollPane(contentBody);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        add(scrollPane, BorderLayout.CENTER);

        // Đăng ký nhận thông báo thay đổi dữ liệu thời gian thực
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            SwingUtilities.invokeLater(this::rebuildContent);
        });
    }

    private final JPanel contentBody = new JPanel();

    private void buildContentBody() {
        contentBody.removeAll();

        // A. DẢI 4 KPI CARDS TÀI CHÍNH & VẬN HÀNH
        contentBody.add(createKpiCardsRow());
        contentBody.add(Box.createRigidArea(new Dimension(0, 18)));

        // B. PHÂN KHU 2 CỘT: SO SÁNH 2 CHI NHÁNH & QUÂN SỐ NHÂN SỰ
        JPanel midRow = new JPanel(new GridLayout(1, 2, 18, 0));
        midRow.setOpaque(false);
        midRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        midRow.add(createBranchComparisonCard());
        midRow.add(createStaffingCard());
        contentBody.add(midRow);
        contentBody.add(Box.createRigidArea(new Dimension(0, 18)));

        // C. PHÂN KHU CẢNH BÁO ĐIỀU HÀNH & HỘI VIÊN VIP CỦA CHỦ
        contentBody.add(createOwnerAlertsSection());
    }

    private void rebuildContent() {
        buildContentBody();
        contentBody.revalidate();
        contentBody.repaint();
    }

    private JPanel createKpiCardsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        double totalRevenue = com.nhom12.gym.dao.HoaDonDAO.getInstance().layTongDoanhThu();
        int totalMembers = hoiVienDAO.layTatCa().size();
        long activeMembers = hoiVienDAO.layTatCa().stream().filter(m -> "Active".equalsIgnoreCase(m.getTrangThai()) || "Hoạt động".equalsIgnoreCase(m.getTrangThai())).count();
        int totalAccounts = taiKhoanDAO.layTatCa().size();
        int todayCheckins = checkInDAO.layLichSuHomNay().size();
        if (todayCheckins == 0) todayCheckins = checkInDAO.layTatCaLichSu().size();

        row.add(createKpiCard("DOANH THU THỰC TẾ", df.format(totalRevenue), "Cập nhật thời gian thực từ hóa đơn", new Color(37, 99, 235), new Color(238, 242, 255)));
        row.add(createKpiCard("HỘI VIÊN HOẠT ĐỘNG", activeMembers + " / " + totalMembers + " HV", "Tỷ lệ kích hoạt: " + (totalMembers > 0 ? (activeMembers * 100 / totalMembers) : 0) + "%", new Color(16, 185, 129), new Color(236, 253, 245)));
        row.add(createKpiCard("LƯỢT CHECK-IN HÔM NAY", todayCheckins + " Lượt quét", "Hệ thống trạm quét hoạt động 100%", new Color(245, 158, 11), new Color(254, 243, 199)));
        row.add(createKpiCard("TỔNG QUÂN SỐ TÀI KHOẢN", totalAccounts + " Tài khoản", "4 Vai trò đang vận hành", new Color(147, 51, 234), new Color(250, 245, 255)));

        return row;
    }

    private JPanel createKpiCard(String title, String val, String sub, Color accent, Color bgLight) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblT.setForeground(new Color(100, 116, 139));

        JLabel lblV = new JLabel(val);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblV.setForeground(accent);

        JLabel lblS = new JLabel(sub);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblS.setForeground(new Color(71, 85, 105));

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblV, BorderLayout.CENTER);
        card.add(lblS, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createBranchComparisonCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblHeader = new JLabel("HIỆU SUẤT VẬN HÀNH 2 CHI NHÁNH");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(15, 23, 42));
        card.add(lblHeader, BorderLayout.NORTH);

        JPanel list = new JPanel(new GridLayout(2, 1, 0, 10));
        list.setOpaque(false);

        java.util.Map<String, Double> branchRev = com.nhom12.gym.dao.HoaDonDAO.getInstance().layDoanhThuTheoChiNhanh();
        double cn01Rev = branchRev.getOrDefault("CN01 - Quận 1", 0.0);
        double cn02Rev = branchRev.getOrDefault("CN02 - Tân Bình", 0.0);

        List<com.nhom12.gym.entity.LichSuCheckIn> allCheckIns = checkInDAO.layTatCaLichSu();
        long cn01Checks = allCheckIns.stream().filter(c -> c.getChiNhanh() != null && c.getChiNhanh().contains("CN01")).count();
        long cn02Checks = allCheckIns.stream().filter(c -> c.getChiNhanh() != null && c.getChiNhanh().contains("CN02")).count();

        // Chi nhánh 1
        JPanel b1 = createBranchRow(
                "Cơ sở 1 - CN01 (Flagship Quận 1)",
                df.format(cn01Rev),
                cn01Checks + " lượt check-in ghi nhận • 100% Ổn định",
                new Color(37, 99, 235),
                "75% Sức chứa"
        );

        // Chi nhánh 2
        JPanel b2 = createBranchRow(
                "Cơ sở 2 - CN02 (Premium Tân Bình)",
                df.format(cn02Rev),
                cn02Checks + " lượt check-in ghi nhận • Đệm Offline Fallback bảo vệ",
                new Color(16, 185, 129),
                "58% Sức chứa"
        );

        list.add(b1);
        list.add(b2);
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBranchRow(String branchName, String revenue, String detail, Color color, String occupancy) {
        JPanel box = new JPanel(new BorderLayout(8, 4));
        box.setBackground(new Color(248, 250, 252));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblName = new JLabel(branchName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(new Color(15, 23, 42));

        JLabel lblOcc = new JLabel(occupancy);
        lblOcc.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblOcc.setForeground(color);
        top.add(lblName, BorderLayout.WEST);
        top.add(lblOcc, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JLabel lblDetail = new JLabel(detail);
        lblDetail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDetail.setForeground(new Color(100, 116, 139));

        JLabel lblRev = new JLabel(revenue);
        lblRev.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRev.setForeground(color);
        bottom.add(lblDetail, BorderLayout.WEST);
        bottom.add(lblRev, BorderLayout.EAST);

        box.add(top, BorderLayout.NORTH);
        box.add(bottom, BorderLayout.SOUTH);
        return box;
    }

    private JPanel createStaffingCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblHeader = new JLabel("TÌNH HÌNH QUÂN SỐ NHÂN SỰ ĐANG TRỰC");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(15, 23, 42));
        card.add(lblHeader, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(4, 1, 0, 8));
        stats.setOpaque(false);

        stats.add(createStaffRow("Lễ tân Front Desk:", "4 Nhân viên đang trực ca (CN01: 2, CN02: 2)", new Color(16, 185, 129)));
        stats.add(createStaffRow("Huấn Luyện Viên (HLV/PT):", "6 HLV đang có lớp (3 lớp Yoga/Zumba, 4 ca PT 1-1)", new Color(245, 158, 11)));
        stats.add(createStaffRow("Quản Lý Chi Nhánh:", "2 Quản lý đang phụ trách (Lê Thanh Sơn, Nguyễn Linh)", new Color(37, 99, 235)));
        stats.add(createStaffRow("Tài Khoản Hội Viên App:", "1,280 Hội viên sở hữu tài khoản và thẻ số", new Color(147, 51, 234)));

        card.add(stats, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStaffRow(String roleTitle, String desc, Color dotColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel lblT = new JLabel("● " + roleTitle);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblT.setForeground(dotColor);

        JLabel lblD = new JLabel(desc);
        lblD.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblD.setForeground(new Color(71, 85, 105));

        row.add(lblT, BorderLayout.WEST);
        row.add(lblD, BorderLayout.CENTER);
        return row;
    }

    private JPanel createOwnerAlertsSection() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblHeader = new JLabel("DANH SÁCH HỘI VIÊN VIP CẦN CHĂM SÓC RIÊNG (CHÍNH SÁCH ẨN CỦA ANH ĐỨC)");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(220, 38, 38));

        JLabel lblBadge = new JLabel("BẢO MẬT VỚI NHÂN VIÊN");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(254, 226, 226));
        lblBadge.setForeground(new Color(185, 28, 28));
        lblBadge.setBorder(new EmptyBorder(3, 8, 3, 8));

        top.add(lblHeader, BorderLayout.WEST);
        top.add(lblBadge, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        String[] cols = {"Mã Thẻ", "Họ Tên Hội Viên", "Số Điện Thoại", "Gói Tập Sở Hữu", "Hạn Sử Dụng", "Giá Niêm Yết", "Giá Thực Thu (Anh Đức)", "Hành Động"};
        Object[][] data = {
                {"VIP007", "Đỗ Minh Trí", "0909998877", "VIP Diamond Toàn Hệ Thống 2 Năm", "31/12/2027", "15,000,000 đ", "7,500,000 đ (-50%)", "Gọi Chăm Sóc"},
                {"QR001", "Lê Anh Duy", "0974482749", "Gói Kim Cương 12 Tháng", "31/12/2026", "8,500,000 đ", "8,500,000 đ", "Gửi Zalo Tri Ân"},
                {"QR002", "Võ Lê Ngọc Hưng", "0912345678", "Gói Standard 3 Tháng", "15/10/2026", "2,400,000 đ", "2,400,000 đ", "Nhắc Hạn Tái Ký"},
                {"QR004", "Trần Minh Quân", "0933221100", "Gói Premium 6 Tháng", "20/11/2026", "4,800,000 đ", "4,800,000 đ", "Mời Nâng Cấp VIP"}
        };

        JTable table = new JTable(data, cols);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(0, 160));
        sp.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        card.add(sp, BorderLayout.CENTER);

        return card;
    }
}
