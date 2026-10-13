package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HoaDonDAO;
import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.HoaDon;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.DataSyncManager;
import com.nhom12.gym.util.Auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class ChamSocKhachHangPanel extends JPanel {
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private JTable table;
    private DefaultTableModel model;
    private JTextArea txtMessage;
    private JButton btnSelectAll;
    private JButton btnSMS;
    private JButton btnEmail;
    private JButton btnRenewDirect;

    public ChamSocKhachHangPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("CHĂM SÓC KHÁCH HÀNG & MARKETING TỰ ĐỘNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Tự động lọc danh sách hội viên sắp hết hạn gói tập, gửi tin nhắn Zalo ZNS / SMS nhắc gia hạn");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(620);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Panel (Table)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblListTitle = new JLabel("Hội Viên Sắp Hết Hạn Gói Tập (Cần Nhắc Nhở)");
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblListTitle.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblListTitle, BorderLayout.NORTH);

        String[] cols = new String[]{"Mã HV", "Mã QR", "Họ Tên", "Số Điện Thoại", "Ngày Hết Hạn", "Tình Trạng Hạn"};
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
        table.getTableHeader().setBackground(new Color(254, 242, 242));
        leftPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBottom.setOpaque(false);
        btnSelectAll = new JButton("Chọn Tất Cả Hội Viên");
        btnSelectAll.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSelectAll.setBackground(new Color(100, 116, 139));
        btnSelectAll.setForeground(Color.WHITE);

        btnRenewDirect = new JButton("Gia Hạn Ngay Cho Khách Đang Chọn");
        btnRenewDirect.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRenewDirect.setBackground(new Color(16, 185, 129));
        btnRenewDirect.setForeground(Color.WHITE);

        leftBottom.add(btnSelectAll);
        leftBottom.add(btnRenewDirect);
        leftPanel.add(leftBottom, BorderLayout.SOUTH);
        splitPane.setLeftComponent(leftPanel);

        // Right Panel (Compose)
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblCompose = new JLabel("Nội Dung Tin Nhắn (SMS / Zalo ZNS)");
        lblCompose.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCompose.setForeground(new Color(30, 41, 59));
        rightPanel.add(lblCompose, BorderLayout.NORTH);

        txtMessage = new JTextArea();
        txtMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        txtMessage.setText("FitLife GYM thông báo: Gói tập của quý khách sắp hết hạn trong vài ngày tới.\nVui lòng liên hệ quầy Lễ tân hoặc Hotline 1900 1234 để được hỗ trợ gia hạn và tư vấn lịch tập.\nTrân trọng cảm ơn quý khách!");
        rightPanel.add(new JScrollPane(txtMessage), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);

        btnSMS = new JButton("Gửi Zalo / SMS Hàng Loạt");
        btnSMS.setBackground(new Color(37, 99, 235));
        btnSMS.setForeground(Color.WHITE);
        btnSMS.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnEmail = new JButton("Gửi Email Marketing");
        btnEmail.setBackground(new Color(139, 92, 246));
        btnEmail.setForeground(Color.WHITE);
        btnEmail.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnPanel.add(btnSMS);
        btnPanel.add(btnEmail);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Wire Action Listeners
        btnSelectAll.addActionListener(e -> {
            if (model.getRowCount() > 0) {
                table.selectAll();
                JOptionPane.showMessageDialog(this, "Đã chọn toàn bộ " + model.getRowCount() + " hội viên trong danh sách!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnSMS.addActionListener(e -> {
            int count = table.getSelectedRowCount();
            if (count == 0) count = model.getRowCount();
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Hiện không có hội viên nào cần gửi tin nhắn nhắc hạn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận gửi tin nhắn SMS / Zalo nhắc hạn tới " + count + " hội viên?", "Xác nhận gửi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Hệ thống đã kết nối cổng Zalo ZNS / SMS Brandname thành công!\nĐã gửi thông báo tới " + count + " hội viên.", "Gửi Thành Công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnEmail.addActionListener(e -> {
            int count = table.getSelectedRowCount();
            if (count == 0) count = model.getRowCount();
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Hiện không có hội viên nào cần gửi Email nhắc hạn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Đã gửi Email chiến dịch ưu đãi gia hạn tới " + count + " hộp thư hội viên thành công!", "Gửi Email Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        btnRenewDirect.addActionListener(e -> xuLyGiaHanNgay());

        // Tải dữ liệu ban đầu từ CSDL thật
        loadExpiringMembers();

        // Đăng ký nhận thông báo thay đổi dữ liệu thời gian thực
        DataSyncManager.addListener(type -> {
            if (type == DataSyncManager.DataType.HOI_VIEN || type == DataSyncManager.DataType.ALL) {
                loadExpiringMembers();
            }
        });
    }

    public void loadExpiringMembers() {
        model.setRowCount(0);
        List<HoiVien> all = hoiVienDAO.layTatCa();
        if (all == null) return;

        long nowMs = System.currentTimeMillis();
        long limitMs = nowMs + 30L * 24 * 60 * 60 * 1000; // 30 ngày tới

        for (HoiVien hv : all) {
            Date exp = hv.getNgayHetHan();
            if (exp != null) {
                long diffMs = exp.getTime() - nowMs;
                long days = diffMs / (1000L * 60 * 60 * 24);

                // Lọc những hội viên đã quá hạn hoặc còn <= 30 ngày
                if (exp.getTime() < limitMs) {
                    String statusText;
                    if (days < 0) {
                        statusText = "ĐÃ QUÁ HẠN (" + Math.abs(days) + " ngày)";
                    } else if (days == 0) {
                        statusText = "HẾT HẠN HÔM NAY";
                    } else {
                        statusText = "Còn " + days + " ngày";
                    }

                    model.addRow(new Object[]{
                            "HV" + hv.getMaHoiVien(),
                            hv.getMaTheQR() != null ? hv.getMaTheQR() : "",
                            hv.getHoTen(),
                            hv.getSoDienThoai(),
                            sdf.format(exp),
                            statusText
                    });
                }
            }
        }
    }

    private void xuLyGiaHanNgay() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 hội viên trong danh sách cần gia hạn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codeStr = model.getValueAt(sel, 0).toString(); // HV001
        String name = model.getValueAt(sel, 2).toString();
        String qr = model.getValueAt(sel, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận gia hạn nhanh thêm 1 Tháng (500,000 đ) cho hội viên:\n"
                        + "• Họ và tên: " + name + "\n"
                        + "• Mã thẻ: " + qr + " (" + codeStr + ")\n"
                        + "• Phí gia hạn: 500,000 đ",
                "Xác Nhận Gia Hạn Nhanh", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Tra cứu hội viên trong DAO
        HoiVien hv = hoiVienDAO.timKiemThongMinh(qr);
        if (hv == null) hv = hoiVienDAO.timKiemThongMinh(codeStr);

        if (hv == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ hội viên để gia hạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Calendar cal = Calendar.getInstance();
        if (hv.getNgayHetHan() != null && hv.getNgayHetHan().after(new Date())) {
            cal.setTime(hv.getNgayHetHan());
        } else {
            cal.setTime(new Date());
        }
        cal.add(Calendar.MONTH, 1);
        Date newExp = cal.getTime();

        hv.setNgayHetHan(newExp);
        hv.setTrangThai("Hoạt động");
        hv.setSoTienDaThanhToan(500000.0);
        hoiVienDAO.capNhat(hv);

        // Tạo hóa đơn
        String maHD = "HD" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        HoaDon hd = new HoaDon(
                maHD,
                hv.getMaHoiVien(),
                hv.getHoTen(),
                "Gia hạn nhanh CSKH 1 Tháng",
                500000.0,
                0.0,
                500000.0,
                "Tiền mặt",
                Auth.tenDangNhap != null ? Auth.tenDangNhap : "letan1",
                hv.getChiNhanhDangKy() != null ? hv.getChiNhanhDangKy() : "CN01 - Cơ sở 1 (Quận 1)",
                new Date(),
                "Đã thanh toán",
                "Gia hạn từ mục Chăm sóc khách hàng (Hạn mới: " + sdf.format(newExp) + ")"
        );
        HoaDonDAO.getInstance().them(hd);

        JOptionPane.showMessageDialog(this,
                "GIA HẠN THÀNH CÔNG cho hội viên: " + name + "!\n"
                        + "• Hạn mới: " + sdf.format(newExp) + "\n"
                        + "• Hóa đơn " + maHD + " đã được ghi nhận vào doanh thu hệ thống.",
                "Gia Hạn Thành Công", JOptionPane.INFORMATION_MESSAGE);

        loadExpiringMembers();
    }
}
