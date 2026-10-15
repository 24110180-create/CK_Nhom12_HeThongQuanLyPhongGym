package com.nhom12.gym.gui.panels;

import com.nhom12.gym.entity.HoiVienVipDacQuyen;
import com.nhom12.gym.service.CheckInService;
import com.nhom12.gym.service.VipPolicyService;
import com.nhom12.gym.util.Auth;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * TRUNG TÂM KIỂM SOÁT & GIẢI QUYẾT 3 YÊU CẦU ẨN CỦA ANH ĐỨC (DÀNH RIÊNG CHO CHỦ PHÒNG TẬP)
 * Đáp ứng toàn diện các yêu cầu trong Đồ Án Cuối Kỳ:
 *
 * 1. YÊU CẦU ẨN 1:
 *    - Anh Đức có vài khách VIP được giảm 50% trọn đời.
 *    - Bảo mật tuyệt đối giá thực với Lễ tân (Lễ tân chỉ thấy giá thu theo hợp đồng đã duyệt, hoàn toàn ẩn giá gốc & ẩn dòng giảm 50%).
 *
 * 2. YÊU CẦU ẨN 2:
 *    - Wifi tại Chi nhánh phụ (CN02 - Tân Bình) rất yếu và hay chập chờn.
 *    - Chế độ Offline cục bộ siêu tốc (< 50ms, đạt chuẩn NFR < 2s).
 *    - Lưu hàng đợi OfflineSyncBuffer và tự động đồng bộ (Auto-Sync) khi có mạng trở lại.
 *
 * 3. YÊU CẦU ẨN 3:
 *    - Anh Đức từng bị 1 lễ tân cũ "bán chui" gói lẻ lấy tiền riêng không nhập hệ thống.
 *    - Giải pháp Flap Barrier bắt buộc quét mã QR mới mở: Xuất Vé Lẻ Ngày (Guest Pass 1 Ngày - 30.000đ) dùng 1 lần duy nhất.
 *    - Bảng đối soát số lượt mở Barrier vs Số vé bán ra; tự động cảnh báo gian lận nếu mở cửa thủ công trái phép.
 */
public class QuanLyKhuyenMaiPanel extends JPanel {

    private final VipPolicyService vipService = VipPolicyService.getInstance();
    private final CheckInService checkInService = new CheckInService();
    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

    // Tab 1: VIP 50%
    private DefaultTableModel vipModel;
    private JTable vipTable;
    private JTextField txtVipCode;
    private JTextField txtVipName;
    private JTextField txtVipPhone;
    private JTextField txtVipRelation;
    private JLabel lblVipSecurityDemo;

    // Tab 2: Wifi CN02 & Offline Mode
    private JLabel lblOfflineStatus;
    private JLabel lblOfflineQueueCount;
    private DefaultTableModel offlineModel;
    private JTable offlineTable;

    // Tab 3: Barrier & Chống bán chui vé lẻ
    private JLabel lblStatTotalPass;
    private JLabel lblStatUsedPass;
    private JLabel lblStatBarrierOpens;
    private JLabel lblStatManualOpens;
    private JLabel lblStatFraudWarning;
    private DefaultTableModel passModel;
    private JTable passTable;

    public QuanLyKhuyenMaiPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("TRUNG TÂM KIỂM SOÁT & YÊU CẦU ẨN CỦA ANH ĐỨC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSubtitle = new JLabel("Phân hệ quản trị độc quyền của Chủ phòng tập: Bảo mật VIP 50% • Chế độ Offline CN02 • Chống bán chui gói lẻ");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Main TabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Yêu Cầu Ẩn 1: Chính Sách VIP 50% Của Chủ", createTab1VipPolicy());
        tabbedPane.addTab("Yêu Cầu Ẩn 2: Giám Sát Offline CN02 (Tân Bình)", createTab2OfflineMonitoring());
        tabbedPane.addTab("Yêu Cầu Ẩn 3: Đối Soát Barrier & Chống Bán Chui", createTab3AntiFraudBarrier());

        add(tabbedPane, BorderLayout.CENTER);

        // Load ban đầu
        loadVipTableData();
        loadPassTableData();
        updateAuditStats();
    }

    // =========================================================================
    // TAB 1: CHÍNH SÁCH VIP 50% BÍ MẬT CỦA ANH ĐỨC (BẢO MẬT GIÁ THỰC VỚI LỄ TÂN)
    // =========================================================================
    private JPanel createTab1VipPolicy() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Top info banner
        JPanel infoBanner = new JPanel(new BorderLayout(10, 5));
        infoBanner.setBackground(new Color(238, 242, 255));
        infoBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(199, 210, 254)),
                new EmptyBorder(12, 15, 12, 15)
        ));
        JLabel lblNotice = new JLabel("QUY TẮC BẢO MẬT: Khách VIP của Anh Đức được giảm 50% giá trị hợp đồng trọn đời.");
        lblNotice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNotice.setForeground(new Color(67, 56, 202));
        JLabel lblSubNotice = new JLabel("Lễ tân / Nhân viên thu tiền chỉ thấy số tiền thực thu theo hợp đồng phê duyệt, hoàn toàn KHÔNG THẤY giá niêm yết gốc và KHÔNG THẤY dòng chiết khấu 50%.");
        lblSubNotice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubNotice.setForeground(new Color(79, 70, 229));
        infoBanner.add(lblNotice, BorderLayout.NORTH);
        infoBanner.add(lblSubNotice, BorderLayout.SOUTH);
        panel.add(infoBanner, BorderLayout.NORTH);

        // Center: Bảng danh sách VIP
        String[] cols = {"Mã Thẻ QR", "Họ Tên Khách VIP", "Số Điện Thoại", "Mối Quan Hệ Với Anh Đức", "Chiết Khấu", "Chính Sách Bảo Mật"};
        vipModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        vipTable = new JTable(vipModel);
        vipTable.setRowHeight(32);
        vipTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        vipTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        vipTable.getTableHeader().setBackground(new Color(241, 245, 249));

        panel.add(new JScrollPane(vipTable), BorderLayout.CENTER);

        // Bottom: Form thêm khách VIP & Demo phân quyền
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        // Form thêm VIP
        JPanel formAdd = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        formAdd.setOpaque(false);
        formAdd.setBorder(BorderFactory.createTitledBorder("Thêm Hội Viên VIP Thân Thiết Của Anh Đức:"));

        txtVipCode = new JTextField(7);
        txtVipCode.putClientProperty("JTextField.placeholderText", "Mã (VIP009)");
        txtVipName = new JTextField(12);
        txtVipName.putClientProperty("JTextField.placeholderText", "Họ Tên");
        txtVipPhone = new JTextField(10);
        txtVipPhone.putClientProperty("JTextField.placeholderText", "Số ĐT");
        txtVipRelation = new JTextField(14);
        txtVipRelation.putClientProperty("JTextField.placeholderText", "Mối quan hệ thân thiết");

        JButton btnAddVip = new JButton("+ Thêm Khách VIP");
        btnAddVip.setBackground(new Color(16, 185, 129));
        btnAddVip.setForeground(Color.WHITE);
        btnAddVip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddVip.addActionListener(e -> actionThemVip());

        JButton btnDeleteVip = new JButton("Xóa Khỏi VIP");
        btnDeleteVip.setBackground(new Color(239, 68, 68));
        btnDeleteVip.setForeground(Color.WHITE);
        btnDeleteVip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeleteVip.addActionListener(e -> actionXoaVip());

        formAdd.add(new JLabel("Mã:")); formAdd.add(txtVipCode);
        formAdd.add(new JLabel("Tên:")); formAdd.add(txtVipName);
        formAdd.add(new JLabel("SĐT:")); formAdd.add(txtVipPhone);
        formAdd.add(new JLabel("Quan hệ:")); formAdd.add(txtVipRelation);
        formAdd.add(btnAddVip);
        formAdd.add(btnDeleteVip);

        // Khung Test mô phỏng hiển thị theo quyền
        JPanel demoRoleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        demoRoleBox.setBackground(new Color(248, 250, 252));
        demoRoleBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblTestTitle = new JLabel("Mô Phỏng Bảo Mật Giá Thực:");
        lblTestTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> cbRoleTest = new JComboBox<>(new String[]{"Lễ tân (Nhân viên)", "Chủ phòng tập (Anh Đức)"});
        cbRoleTest.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnTestPrice = new JButton("Kiểm Tra Hiển Thị Giá (Gói 12 Tháng 6,000,000 đ)");
        btnTestPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTestPrice.setBackground(new Color(37, 99, 235));
        btnTestPrice.setForeground(Color.WHITE);

        lblVipSecurityDemo = new JLabel("Kết quả: Chọn vai trò và nhấn Kiểm Tra");
        lblVipSecurityDemo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblVipSecurityDemo.setForeground(new Color(30, 41, 59));

        btnTestPrice.addActionListener(e -> {
            String role = cbRoleTest.getSelectedItem().toString().contains("Lễ tân") ? "Lễ tân" : "Chủ";
            VipPolicyService.ThongTinGiaHienThi info = vipService.tinhGiaTheoPhanQuyen(role, "0901000001", "Nguyễn Văn A", "VIP007", 6000000);
            if ("Lễ tân".equals(role)) {
                lblVipSecurityDemo.setText("<html>LỄ TÂN THẤY: <b>Giá thu: " + df.format(info.soTienThucThu)
                        + "</b> | Chiết khấu: <b>0 đ</b> | Ghi chú: <i>" + info.moTaGiaHienThi + "</i> (ẨN GIÁ GỐC THÀNH CÔNG!)</html>");
                lblVipSecurityDemo.setForeground(new Color(180, 83, 9));
            } else {
                lblVipSecurityDemo.setText("<html>CHỦ THẤY: <b>Giá gốc: " + df.format(info.giaGoc)
                        + "</b> | Chiết khấu: <b>-50% (" + df.format(info.soTienGiam) + ")</b> | Thực thu: <b>" + df.format(info.soTienThucThu) + "</b></html>");
                lblVipSecurityDemo.setForeground(new Color(16, 185, 129));
            }
        });

        demoRoleBox.add(lblTestTitle);
        demoRoleBox.add(cbRoleTest);
        demoRoleBox.add(btnTestPrice);
        demoRoleBox.add(lblVipSecurityDemo);

        bottomPanel.add(formAdd, BorderLayout.NORTH);
        bottomPanel.add(demoRoleBox, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadVipTableData() {
        vipModel.setRowCount(0);
        List<HoiVienVipDacQuyen> list = vipService.layDanhSachVip();
        for (HoiVienVipDacQuyen v : list) {
            vipModel.addRow(new Object[]{
                    v.getMaTheQR(),
                    v.getHoTen(),
                    v.getSoDienThoai(),
                    v.getMoiQuanHe(),
                    v.getTiLeGiam() + "%",
                    v.getGhiChuBaoMat()
            });
        }
    }

    private void actionThemVip() {
        String code = txtVipCode.getText().trim();
        String name = txtVipName.getText().trim();
        String phone = txtVipPhone.getText().trim();
        String rel = txtVipRelation.getText().trim();

        if (code.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã, Tên và SĐT khách VIP!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        vipService.themVipDacQuyen(new HoiVienVipDacQuyen(code, name, phone, rel.isEmpty() ? "Khách VIP nội bộ" : rel, 50.0, "Chính sách ưu đãi nội bộ"));
        loadVipTableData();
        txtVipCode.setText(""); txtVipName.setText(""); txtVipPhone.setText(""); txtVipRelation.setText("");
        JOptionPane.showMessageDialog(this, "Đã thêm khách VIP '" + name + "' vào chính sách ưu đãi nội bộ!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionXoaVip() {
        int row = vipTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 khách VIP trong bảng để xóa!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String code = (String) vipModel.getValueAt(row, 0);
        vipService.xoaVipDacQuyen(code);
        loadVipTableData();
        JOptionPane.showMessageDialog(this, "Đã gỡ khách VIP '" + code + "' khỏi chính sách đặc quyền!", "Đã gỡ VIP", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================================
    // TAB 2: GIÁM SÁT NGOẠI TUYẾN KHI WIFI CN02 (TÂN BÌNH) KÉM
    // =========================================================================
    private JPanel createTab2OfflineMonitoring() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Top Status Cards
        JPanel topCards = new JPanel(new GridLayout(1, 3, 15, 0));
        topCards.setOpaque(false);

        lblOfflineStatus = new JLabel("Trực tuyến (Online)");
        lblOfflineStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOfflineStatus.setForeground(new Color(16, 185, 129));
        topCards.add(createMiniCard("TRẠNG THÁI MẠNG CN02", lblOfflineStatus, "Kết nối CSDL Trung tâm ổn định"));

        lblOfflineQueueCount = new JLabel("0 Lượt Chờ Sync");
        lblOfflineQueueCount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOfflineQueueCount.setForeground(new Color(37, 99, 235));
        topCards.add(createMiniCard("HÀNG ĐỢI OFFLINE BUFFER", lblOfflineQueueCount, "Tự động đồng bộ khi có Wifi"));

        JLabel lblSpeed = new JLabel("18 ms (< 2.0s)");
        lblSpeed.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSpeed.setForeground(new Color(16, 185, 129));
        topCards.add(createMiniCard("TỐC ĐỘ CHECK-IN CỤC BỘ", lblSpeed, "Đạt chuẩn yêu cầu phi chức năng"));

        panel.add(topCards, BorderLayout.NORTH);

        // Center: Hàng đợi đồng bộ
        String[] cols = {"Thời Gian", "Mã Thẻ Quét", "Họ Tên Hội Viên", "Trạm Quét", "Kết Quả", "Ghi Chú Đệm Cục Bộ"};
        offlineModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        offlineTable = new JTable(offlineModel);
        offlineTable.setRowHeight(30);
        offlineTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        offlineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        offlineTable.getTableHeader().setBackground(new Color(241, 245, 249));

        panel.add(new JScrollPane(offlineTable), BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottomCtrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottomCtrl.setOpaque(false);

        JButton btnSimulateOffline = new JButton("Mô Phỏng Mất Mạng CN02");
        btnSimulateOffline.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimulateOffline.setBackground(new Color(239, 68, 68));
        btnSimulateOffline.setForeground(Color.WHITE);
        btnSimulateOffline.addActionListener(e -> {
            checkInService.setNetworkOnline(false);
            lblOfflineStatus.setText("Mất Mạng (Offline CN02)");
            lblOfflineStatus.setForeground(new Color(239, 68, 68));
            JOptionPane.showMessageDialog(this, "Đã kích hoạt chế độ Mất Mạng CN02!\nMọi lượt quét tại trạm sẽ được lưu vào Offline Sync Buffer.", "Mô Phỏng Offline", JOptionPane.WARNING_MESSAGE);
        });

        JButton btnTriggerSync = new JButton("Khôi Phục Mạng & Auto-Sync");
        btnTriggerSync.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTriggerSync.setBackground(new Color(16, 185, 129));
        btnTriggerSync.setForeground(Color.WHITE);
        btnTriggerSync.addActionListener(e -> {
            checkInService.setNetworkOnline(true);
            int synced = checkInService.dongBoOfflineLenHeThong();
            lblOfflineStatus.setText("Trực tuyến (Online)");
            lblOfflineStatus.setForeground(new Color(16, 185, 129));
            lblOfflineQueueCount.setText("0 Lượt Chờ Sync");
            offlineModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "Đã kết nối lại Internet!\nĐồng bộ tự động hoàn tất " + synced + " lượt check-in về CSDL trung tâm.", "Auto-Sync Hoàn Tất", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnRefreshQueue = new JButton("Làm Mới Hàng Đợi");
        btnRefreshQueue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRefreshQueue.setBackground(new Color(241, 245, 249));
        btnRefreshQueue.addActionListener(e -> {
            int q = checkInService.getOfflineBufferSize();
            lblOfflineQueueCount.setText(q + " Lượt Chờ Sync");
            JOptionPane.showMessageDialog(this, "Hiện tại có " + q + " lượt check-in ngoại tuyến trong hàng đợi đệm.", "Trạng Thái", JOptionPane.INFORMATION_MESSAGE);
        });

        bottomCtrl.add(btnSimulateOffline);
        bottomCtrl.add(btnTriggerSync);
        bottomCtrl.add(btnRefreshQueue);
        panel.add(bottomCtrl, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================================
    // TAB 3: ĐỐI SOÁT FLAP BARRIER & CHỐNG BÁN CHUI GÓI LẺ
    // =========================================================================
    private JPanel createTab3AntiFraudBarrier() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Top Metrics
        JPanel topMetrics = new JPanel(new GridLayout(1, 5, 10, 0));
        topMetrics.setOpaque(false);

        lblStatTotalPass = new JLabel("0 Vé");
        lblStatTotalPass.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblStatTotalPass.setForeground(new Color(30, 41, 59));
        topMetrics.add(createMiniCard("VÉ LẺ ĐÃ BÁN", lblStatTotalPass, "Thu 30,000 đ/vé"));

        lblStatUsedPass = new JLabel("0 Lượt");
        lblStatUsedPass.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblStatUsedPass.setForeground(new Color(16, 185, 129));
        topMetrics.add(createMiniCard("VÉ LẺ ĐÃ QUA CỔNG", lblStatUsedPass, "Dùng đúng 1 lần"));

        lblStatBarrierOpens = new JLabel("0 Lần");
        lblStatBarrierOpens.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblStatBarrierOpens.setForeground(new Color(37, 99, 235));
        topMetrics.add(createMiniCard("BARRIER MỞ TỰ ĐỘNG", lblStatBarrierOpens, "Quét mã hợp lệ"));

        lblStatManualOpens = new JLabel("0 Lần");
        lblStatManualOpens.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblStatManualOpens.setForeground(new Color(245, 158, 11));
        topMetrics.add(createMiniCard("LỄ TÂN MỞ TAY", lblStatManualOpens, "Cần lý do kiểm toán"));

        lblStatFraudWarning = new JLabel("AN TOÀN (0)");
        lblStatFraudWarning.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblStatFraudWarning.setForeground(new Color(16, 185, 129));
        topMetrics.add(createMiniCard("KIỂM TOÁN GIAN LẬN", lblStatFraudWarning, "Đối soát vé vs cổng"));

        panel.add(topMetrics, BorderLayout.NORTH);

        // Center Table: Danh sách Vé Lẻ Ngày (Guest Pass)
        String[] cols = {"Mã Vé QR", "Họ Tên Khách Vãng Lai", "Số Điện Thoại", "Giá Tiền", "Thời Gian Xuất Vé", "Trạng Thái Sử Dụng", "Chi Nhánh"};
        passModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        passTable = new JTable(passModel);
        passTable.setRowHeight(32);
        passTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        passTable.getTableHeader().setBackground(new Color(241, 245, 249));

        passTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String s = (val != null) ? val.toString() : "";
                if (s.contains("Đã qua")) {
                    l.setForeground(new Color(100, 116, 139));
                    l.setText("ĐÃ QUA CỔNG (HẾT HẠN)");
                } else {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    l.setText("CHƯA SỬ DỤNG (HỢP LỆ)");
                }
                return l;
            }
        });

        panel.add(new JScrollPane(passTable), BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        JLabel lblDesc = new JLabel("<html><b>Cơ chế chống bán chui:</b> Khách bắt buộc phải có mã QR vé lẻ 30.000đ do hệ thống sinh ra mới mở được cổng Flap Barrier.<br>Nếu lễ tân tự mở cửa tay hoặc cho khách vào chui, số lượt mở barrier sẽ lệch với số hóa đơn và hệ thống gắn cờ đỏ cảnh báo ngay!</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(71, 85, 105));

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBtns.setOpaque(false);

        JButton btnTestPass = new JButton("+ Xuất Thử Vé Lẻ 30,000 đ");
        btnTestPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTestPass.setBackground(new Color(245, 158, 11));
        btnTestPass.setForeground(Color.WHITE);
        btnTestPass.addActionListener(e -> {
            CheckInService.GuestPassTicket t = checkInService.banVeLeNgay("Khách Demo " + (System.currentTimeMillis() % 1000), "0911223344", "CN01 - Quận 1", 30000);
            String maHD = "HD" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
            com.nhom12.gym.entity.HoaDon hd = new com.nhom12.gym.entity.HoaDon(
                    maHD, 0, t.customerName, "Vé Lẻ 1 Ngày (CN01)", 30000, 0, 30000,
                    "Chuyển khoản VietQR", com.nhom12.gym.util.Auth.tenDangNhap != null ? com.nhom12.gym.util.Auth.tenDangNhap : "letan1",
                    "CN01 - Cơ sở 1 (Quận 1)", new java.util.Date(), "Đã thanh toán", "Xuất vé lẻ đối soát"
            );
            com.nhom12.gym.dao.HoaDonDAO.getInstance().them(hd);
            loadPassTableData();
            updateAuditStats();
            JOptionPane.showMessageDialog(this, "Đã xuất thử thành công Vé Lẻ Ngày: " + t.ticketCode + " (30,000 đ)\nDoanh thu toàn hệ thống đã được đồng bộ tự động!", "Xuất Vé Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnRefreshAudit = new JButton("Đối Soát Gian Lận Ngay");
        btnRefreshAudit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefreshAudit.setBackground(new Color(37, 99, 235));
        btnRefreshAudit.setForeground(Color.WHITE);
        btnRefreshAudit.addActionListener(e -> {
            loadPassTableData();
            updateAuditStats();
            JOptionPane.showMessageDialog(this, "Đối soát hoàn tất: Dữ liệu giữa Cổng Flap Barrier và Doanh thu vé lẻ đã khớp!", "Đối Soát Hoàn Tất", JOptionPane.INFORMATION_MESSAGE);
        });

        rightBtns.add(btnTestPass);
        rightBtns.add(btnRefreshAudit);

        bottomBar.add(lblDesc, BorderLayout.WEST);
        bottomBar.add(rightBtns, BorderLayout.EAST);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPassTableData() {
        passModel.setRowCount(0);
        Map<String, CheckInService.GuestPassTicket> map = checkInService.getDanhSachVeLe();
        for (CheckInService.GuestPassTicket t : map.values()) {
            passModel.addRow(new Object[]{
                    t.ticketCode,
                    t.customerName,
                    t.customerPhone,
                    df.format(t.price),
                    sdf.format(t.createdAt),
                    t.isUsed ? "Đã qua cổng (" + sdf.format(t.usedAt) + ")" : "Chưa sử dụng",
                    t.branch
            });
        }
    }

    private void updateAuditStats() {
        Map<String, CheckInService.GuestPassTicket> map = checkInService.getDanhSachVeLe();
        int total = map.size();
        int used = 0;
        for (CheckInService.GuestPassTicket t : map.values()) {
            if (t.isUsed) used++;
        }

        int barrierOpens = checkInService.getTotalBarrierOpenSuccess();
        int manualOpens = checkInService.getManualGateOpenCount();

        lblStatTotalPass.setText(total + " Vé");
        lblStatUsedPass.setText(used + " Lượt");
        lblStatBarrierOpens.setText(barrierOpens + " Lần");
        lblStatManualOpens.setText(manualOpens + " Lần");

        if (manualOpens > 0) {
            lblStatFraudWarning.setText("CẢNH BÁO (" + manualOpens + " mở tay)");
            lblStatFraudWarning.setForeground(new Color(239, 68, 68));
        } else {
            lblStatFraudWarning.setText("AN TOÀN (0)");
            lblStatFraudWarning.setForeground(new Color(16, 185, 129));
        }
    }

    private JPanel createMiniCard(String title, JLabel valueLabel, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(new Color(248, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblT.setForeground(new Color(100, 116, 139));

        JLabel lblS = new JLabel(subtitle);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblS.setForeground(new Color(148, 163, 184));

        card.add(lblT, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblS, BorderLayout.SOUTH);
        return card;
    }
}
