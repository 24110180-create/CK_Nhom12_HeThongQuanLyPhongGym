package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HoaDonDAO;
import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.LichSuCheckInDAO;
import com.nhom12.gym.entity.HoaDon;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.LichSuCheckIn;
import com.nhom12.gym.service.DataSyncManager;
import com.nhom12.gym.util.Auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ChiTietHoiVienPanel extends JPanel {
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final LichSuCheckInDAO checkInDAO = new LichSuCheckInDAO();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

    private JTextField txtSearch;
    private JButton btnSearch;
    private JLabel lblName;
    private JLabel lblPhone;
    private JLabel lblGoi;
    private JLabel lblStatus;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnRenew;
    private JButton btnLock;
    private JButton btnPrintProfile;

    private HoiVien currentMember;

    public ChiTietHoiVienPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("HỒ SƠ HỘI VIÊN & NHẬT KÝ CHECK-IN THỰC TẾ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Tra cứu toàn diện thông tin thẻ, lịch sử ra vào và trạng thái hoạt động");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Top Search Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblSearch = new JLabel("Nhập Mã HV / Mã QR / Tên:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBar.add(lblSearch);

        txtSearch = new JTextField(18);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchBar.add(txtSearch);

        btnSearch = new JButton("Tra Cứu Hồ Sơ");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBackground(new Color(37, 99, 235));
        btnSearch.setForeground(Color.WHITE);
        searchBar.add(btnSearch);

        btnRenew = new JButton("Gia Hạn Thẻ Tập");
        btnRenew.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRenew.setBackground(new Color(16, 185, 129));
        btnRenew.setForeground(Color.WHITE);

        btnLock = new JButton("Khóa Thẻ Tạm Thời");
        btnLock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLock.setBackground(new Color(239, 68, 68));
        btnLock.setForeground(Color.WHITE);

        if (!"HLV".equalsIgnoreCase(Auth.vaiTro)) {
            searchBar.add(btnRenew);
            searchBar.add(btnLock);
        }

        btnPrintProfile = new JButton("In Hồ Sơ Hội Viên");
        btnPrintProfile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrintProfile.setBackground(new Color(100, 116, 139));
        btnPrintProfile.setForeground(Color.WHITE);
        searchBar.add(btnPrintProfile);

        centerPanel.add(searchBar, BorderLayout.NORTH);

        // Info Summary Card
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        Font fLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fValue = new Font("Segoe UI", Font.PLAIN, 14);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lName = new JLabel("Họ Và Tên:"); lName.setFont(fLabel);
        infoPanel.add(lName, gbc);
        gbc.gridx = 1;
        lblName = new JLabel("Vui lòng nhập từ khóa tra cứu");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(new Color(30, 41, 59));
        infoPanel.add(lblName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lPhone = new JLabel("Mã Thẻ / SĐT:"); lPhone.setFont(fLabel);
        infoPanel.add(lPhone, gbc);
        gbc.gridx = 1;
        lblPhone = new JLabel("---");
        lblPhone.setFont(fValue);
        infoPanel.add(lblPhone, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        JLabel lGoi = new JLabel("Gói Tập Đang Dùng:"); lGoi.setFont(fLabel);
        infoPanel.add(lGoi, gbc);
        gbc.gridx = 3;
        lblGoi = new JLabel("---");
        lblGoi.setFont(fValue);
        infoPanel.add(lblGoi, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        JLabel lStatus = new JLabel("Tình Trạng Thẻ:"); lStatus.setFont(fLabel);
        infoPanel.add(lStatus, gbc);
        gbc.gridx = 3;
        lblStatus = new JLabel("---");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(new Color(100, 116, 139));
        infoPanel.add(lblStatus, gbc);

        // History Table
        JPanel historyPanel = new JPanel(new BorderLayout(0, 10));
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblHistTitle = new JLabel("Nhật Ký Quét Thẻ Check-in Thực Tế");
        lblHistTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHistTitle.setForeground(new Color(30, 41, 59));
        historyPanel.add(lblHistTitle, BorderLayout.NORTH);

        String[] cols = new String[]{"Ngày", "Giờ Check-in", "Chi Nhánh Quét", "Cổng / Thiết Bị", "Phương Thức", "Trạng Thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        historyPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel mainCenter = new JPanel(new BorderLayout(0, 15));
        mainCenter.setOpaque(false);
        mainCenter.add(infoPanel, BorderLayout.NORTH);
        mainCenter.add(historyPanel, BorderLayout.CENTER);
        centerPanel.add(mainCenter, BorderLayout.CENTER);

        this.add(centerPanel, BorderLayout.CENTER);

        // Action Listeners
        java.awt.event.ActionListener searchAction = e -> thucHienTraCuu();
        btnSearch.addActionListener(searchAction);
        txtSearch.addActionListener(searchAction);

        btnRenew.addActionListener(e -> xuLyGiaHan());
        btnLock.addActionListener(e -> xuLyKhoaMoThe());
        btnPrintProfile.addActionListener(e -> {
            if (currentMember == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng tra cứu hội viên trước khi in!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Đang kết nối máy in...\nĐã xuất và in hồ sơ chi tiết kèm lịch sử check-in của hội viên: " + currentMember.getHoTen(), "In Hồ Sơ", JOptionPane.INFORMATION_MESSAGE);
        });

        // Tự động tải hội viên đầu tiên làm mẫu
        List<HoiVien> all = hoiVienDAO.layTatCa();
        if (all != null && !all.isEmpty()) {
            hienThiHoiVien(all.get(0));
        }

        // Lắng nghe sự kiện đồng bộ toàn hệ thống
        DataSyncManager.addListener(type -> {
            if (type == DataSyncManager.DataType.HOI_VIEN || type == DataSyncManager.DataType.CHECK_IN || type == DataSyncManager.DataType.ALL) {
                if (currentMember != null) {
                    HoiVien refreshed = hoiVienDAO.timTheoId(currentMember.getMaHoiVien());
                    if (refreshed != null) {
                        hienThiHoiVien(refreshed);
                    }
                }
            }
        });
    }

    private void thucHienTraCuu() {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã HV, Mã Thẻ QR hoặc Tên hội viên để tra cứu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        HoiVien hv = hoiVienDAO.timKiemThongMinh(q);
        if (hv != null) {
            hienThiHoiVien(hv);
            JOptionPane.showMessageDialog(this, "Đã tìm thấy hồ sơ hội viên: " + hv.getHoTen() + " [Mã: " + hv.getMaHoiVien() + " | Thẻ QR: " + hv.getMaTheQR() + "]", "Tra Cứu", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hội viên nào khớp với từ khóa: \"" + q + "\"!\nVui lòng kiểm tra lại danh sách hội viên.", "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void hienThiHoiVien(HoiVien hv) {
        this.currentMember = hv;
        lblName.setText(hv.getHoTen());
        lblPhone.setText("QR: " + (hv.getMaTheQR() != null ? hv.getMaTheQR() : "HV" + hv.getMaHoiVien())
                + " • SĐT: " + (hv.getSoDienThoai() != null ? hv.getSoDienThoai() : "---")
                + " • " + (hv.getChiNhanhDangKy() != null ? hv.getChiNhanhDangKy() : "Toàn hệ thống"));

        String goiText = (hv.getGoiTapDangKy() != null ? hv.getGoiTapDangKy() : "Hội Viên Gym");
        if (hv.getNgayHetHan() != null) {
            goiText += " (Hạn: " + sdf.format(hv.getNgayHetHan()) + ")";
        }
        lblGoi.setText(goiText);

        boolean isLocked = hv.getTrangThai() != null && hv.getTrangThai().toLowerCase().contains("khóa");
        if (isLocked) {
            lblStatus.setText("Tạm khóa theo yêu cầu");
            lblStatus.setForeground(new Color(239, 68, 68));
            btnLock.setText("Mở Khóa Thẻ");
            btnLock.setBackground(new Color(16, 185, 129));
        } else {
            btnLock.setText("Khóa Thẻ Tạm Thời");
            btnLock.setBackground(new Color(239, 68, 68));
            if (hv.getNgayHetHan() != null) {
                long diffMs = hv.getNgayHetHan().getTime() - System.currentTimeMillis();
                long days = diffMs / (1000L * 60 * 60 * 24);
                if (days < 0) {
                    lblStatus.setText("ĐÃ HẾT HẠN (Quá hạn " + Math.abs(days) + " ngày)");
                    lblStatus.setForeground(new Color(239, 68, 68));
                } else if (days <= 7) {
                    lblStatus.setText("Sắp hết hạn (Còn " + days + " ngày)");
                    lblStatus.setForeground(new Color(245, 158, 11));
                } else {
                    lblStatus.setText("Đang hoạt động (Còn " + days + " ngày)");
                    lblStatus.setForeground(new Color(16, 185, 129));
                }
            } else {
                lblStatus.setText(hv.getTrangThai() != null ? hv.getTrangThai() : "Đang hoạt động");
                lblStatus.setForeground(new Color(16, 185, 129));
            }
        }

        // Tải lịch sử check-in thực tế từ LichSuCheckInDAO
        loadCheckInHistory(hv);
    }

    private void loadCheckInHistory(HoiVien hv) {
        model.setRowCount(0);
        List<LichSuCheckIn> logs = checkInDAO.timTheoMaTheQR(hv.getMaTheQR());
        if (logs.isEmpty() && hv.getMaHoiVien() > 0) {
            logs = checkInDAO.timTheoMaHoiVien(hv.getMaHoiVien());
        }

        if (logs != null && !logs.isEmpty()) {
            for (LichSuCheckIn log : logs) {
                String dStr = log.getThoiGian() != null ? sdf.format(log.getThoiGian()) : "---";
                String tStr = log.getThoiGian() != null ? sdfTime.format(log.getThoiGian()) : "---";
                model.addRow(new Object[]{
                        dStr,
                        tStr,
                        log.getChiNhanh() != null ? log.getChiNhanh() : "---",
                        log.getCuaRaVao() != null ? log.getCuaRaVao() : "Cổng chính",
                        log.getPhuongThuc() != null ? log.getPhuongThuc() : "Mã QR",
                        log.getTrangThai() != null ? log.getTrangThai() : "Hợp lệ"
                });
            }
        } else {
            model.addRow(new Object[]{
                    "---", "---", "Chưa có lượt quét thẻ nào được ghi nhận cho hội viên này", "---", "---", "Chờ quét"
            });
        }
    }

    private void xuLyKhoaMoThe() {
        if ("HLV".equalsIgnoreCase(Auth.vaiTro)) {
            JOptionPane.showMessageDialog(this, "Huấn luyện viên không có thẩm quyền khóa hoặc mở thẻ hội viên.\nVui lòng chuyển yêu cầu tới quầy Lễ tân hoặc Quản lý!", "Giới hạn quyền hạn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentMember == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tra cứu hội viên trước!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isCurrentlyLocked = currentMember.getTrangThai() != null && currentMember.getTrangThai().toLowerCase().contains("khóa");
        if (isCurrentlyLocked) {
            currentMember.setTrangThai("Hoạt động");
            hoiVienDAO.capNhat(currentMember);
            hienThiHoiVien(currentMember);
            JOptionPane.showMessageDialog(this, "Đã MỞ KHÓA thẻ hội viên [" + currentMember.getHoTen() + "] thành công!\nHội viên có thể check-in bình thường.", "Mở Khóa Thẻ", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận TẠM KHÓA thẻ của hội viên [" + currentMember.getHoTen() + "]?\nSau khi khóa, thẻ sẽ bị từ chối khi quét tại cổng check-in.",
                    "Xác Nhận Khóa Thẻ", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                currentMember.setTrangThai("Tạm khóa");
                hoiVienDAO.capNhat(currentMember);
                hienThiHoiVien(currentMember);
                JOptionPane.showMessageDialog(this, "Đã TẠM KHÓA thẻ hội viên thành công! Cổng check-in sẽ từ chối lượt quét.", "Khóa Thẻ", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void xuLyGiaHan() {
        if ("HLV".equalsIgnoreCase(Auth.vaiTro)) {
            JOptionPane.showMessageDialog(this, "Huấn luyện viên không phụ trách nghiệp vụ thu ngân hoặc gia hạn thẻ tập.\nVui lòng hướng dẫn hội viên tới quầy Lễ tân để thực hiện gia hạn và thanh toán!", "Giới hạn quyền hạn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentMember == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tra cứu hội viên trước khi gia hạn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] options = {"1 Tháng (500,000 đ)", "3 Tháng (1,350,000 đ)", "6 Tháng (2,500,000 đ)", "12 Tháng (4,500,000 đ)"};
        int sel = JOptionPane.showOptionDialog(this,
                "Chọn gói thời gian gia hạn cho hội viên: " + currentMember.getHoTen(),
                "Gia Hạn Thẻ Tập",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (sel < 0) return;

        int months = 1;
        double price = 500000.0;
        String pkg = "Gói 1 Tháng Tiêu Chuẩn";
        if (sel == 1) { months = 3; price = 1350000.0; pkg = "Gói 3 Tháng Tiết Kiệm"; }
        else if (sel == 2) { months = 6; price = 2500000.0; pkg = "Gói 6 Tháng Bứt Phá"; }
        else if (sel == 3) { months = 12; price = 4500000.0; pkg = "Gói 12 Tháng VIP Diamond"; }

        Calendar cal = Calendar.getInstance();
        if (currentMember.getNgayHetHan() != null && currentMember.getNgayHetHan().after(new Date())) {
            cal.setTime(currentMember.getNgayHetHan());
        } else {
            cal.setTime(new Date());
        }
        cal.add(Calendar.MONTH, months);
        Date newExp = cal.getTime();

        currentMember.setNgayHetHan(newExp);
        currentMember.setTrangThai("Hoạt động");
        currentMember.setSoTienDaThanhToan(price);
        hoiVienDAO.capNhat(currentMember);

        // Tạo hóa đơn
        String maHD = "HD" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        HoaDon hd = new HoaDon(
                maHD,
                currentMember.getMaHoiVien(),
                currentMember.getHoTen(),
                "Gia hạn " + pkg,
                price,
                0.0,
                price,
                "Tiền mặt",
                Auth.tenDangNhap != null ? Auth.tenDangNhap : "letan1",
                currentMember.getChiNhanhDangKy() != null ? currentMember.getChiNhanhDangKy() : "CN01 - Cơ sở 1 (Quận 1)",
                new Date(),
                "Đã thanh toán",
                "Gia hạn tại màn hình Chi tiết hội viên (Hạn mới: " + sdf.format(newExp) + ")"
        );
        HoaDonDAO.getInstance().them(hd);

        hienThiHoiVien(currentMember);

        JOptionPane.showMessageDialog(this,
                "GIA HẠN THÀNH CÔNG cho hội viên: " + currentMember.getHoTen() + "!\n"
                        + "• Hạn mới: " + sdf.format(newExp) + "\n"
                        + "• Hóa đơn " + maHD + " (Số tiền: " + String.format("%,.0f đ", price) + ") đã ghi nhận vào doanh thu.",
                "Gia Hạn Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }
}
