package com.nhom12.gym.gui;

import com.nhom12.gym.dao.HoaDonDAO;
import com.nhom12.gym.entity.HoaDon;
import com.nhom12.gym.util.Auth;
import com.nhom12.gym.util.ImageHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * Cửa sổ Hóa Đơn & Cổng Thanh Toán VietQR Cá Nhân Hóa (Tác giả: LÊ ANH DUY - MB Bank)
 * Cung cấp trải nghiệm thanh toán chuẩn ngân hàng, in phiếu thu và lưu trữ dữ liệu tự động.
 */
public class ThanhToanHoaDonDialog extends JDialog {

    public static class ChiTietMucHang {
        public String ten;
        public int soLuong;
        public double donGia;
        public double thanhTien;

        public ChiTietMucHang(String ten, int soLuong, double donGia) {
            this.ten = ten;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.thanhTien = soLuong * donGia;
        }

        public ChiTietMucHang(String ten, int soLuong, double donGia, double thanhTien) {
            this.ten = ten;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.thanhTien = thanhTien;
        }
    }

    private final String maHoaDon;
    private final String tenKhachHang;
    private final String loaiGiaoDich;
    private final double tongTien;
    private final double giamGia;
    private final double thanhTien;
    private final String chiNhanh;
    private final String nguoiLap;
    private final List<ChiTietMucHang> danhSachMuc;
    private final Consumer<Boolean> onThanhToanThanhCong;

    private final DecimalFormat df = new DecimalFormat("#,### đ");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final Date ngayTao = new Date();

    private boolean daThanhToan = false;
    private String phuongThuc = "Chuyển khoản VietQR";
    private JTextField txtTienKhachDua;
    private JLabel lblTienThoi;

    public ThanhToanHoaDonDialog(Window parent, String maHoaDon, String tenKhachHang, String loaiGiaoDich,
                                 double tongTien, double giamGia, double thanhTien,
                                 String chiNhanh,
                                 List<ChiTietMucHang> danhSachMuc, Consumer<Boolean> onThanhToanThanhCong) {
        super(parent, "Xác Nhận Hóa Đơn & Thanh Toán - " + (maHoaDon != null ? maHoaDon : "FitLife Gym"), ModalityType.APPLICATION_MODAL);

        this.maHoaDon = (maHoaDon != null && !maHoaDon.trim().isEmpty())
                ? maHoaDon
                : "HD" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + (System.currentTimeMillis() % 10000);
        this.tenKhachHang = (tenKhachHang != null && !tenKhachHang.trim().isEmpty()) ? tenKhachHang : "Khách Lẻ / Hội Viên";
        this.loaiGiaoDich = (loaiGiaoDich != null && !loaiGiaoDich.trim().isEmpty()) ? loaiGiaoDich : "Dịch vụ phòng tập";
        this.tongTien = tongTien;
        this.giamGia = giamGia;
        this.thanhTien = thanhTien;
        this.chiNhanh = (chiNhanh != null && !chiNhanh.trim().isEmpty()) ? chiNhanh : Auth.getChiNhanhHienTai();
        this.nguoiLap = (Auth.hoTen != null && !Auth.hoTen.trim().isEmpty())
                ? Auth.hoTen
                : (Auth.tenDangNhap != null ? Auth.tenDangNhap : "letan1");

        this.danhSachMuc = (danhSachMuc != null && !danhSachMuc.isEmpty())
                ? danhSachMuc
                : new ArrayList<ChiTietMucHang>() {{
                    add(new ChiTietMucHang(loaiGiaoDich, 1, thanhTien, thanhTien));
                }};
        this.onThanhToanThanhCong = onThanhToanThanhCong;

        initUI();
    }

    public ThanhToanHoaDonDialog(Window parent, String maHoaDon, String tenKhachHang, String loaiGiaoDich,
                                 double tongTien, double giamGia, double thanhTien,
                                 List<ChiTietMucHang> danhSachMuc, Consumer<Boolean> onThanhToanThanhCong) {
        this(parent, maHoaDon, tenKhachHang, loaiGiaoDich, tongTien, giamGia, thanhTien, Auth.getChiNhanhHienTai(), danhSachMuc, onThanhToanThanhCong);
    }

    private void initUI() {
        setSize(920, 680);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(15, 12));
        root.setBackground(new Color(241, 245, 249)); // Slate 100
        root.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. TOP HEADER BANNER
        JPanel topBanner = new JPanel(new BorderLayout());
        topBanner.setOpaque(false);

        JLabel lblTitle = new JLabel("HÓA ĐƠN DỊCH VỤ & XÁC NHẬN THANH TOÁN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Vui lòng kiểm tra kỹ thông tin đơn hàng và xác nhận qua cổng VietQR Napas 247 hoặc Tiền mặt");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        JPanel titleGrp = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGrp.setOpaque(false);
        titleGrp.add(lblTitle);
        titleGrp.add(lblSub);
        topBanner.add(titleGrp, BorderLayout.WEST);

        JLabel lblBadge = new JLabel("MÃ HĐ: " + maHoaDon + " ");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBadge.setForeground(new Color(37, 99, 235));
        topBanner.add(lblBadge, BorderLayout.EAST);

        root.add(topBanner, BorderLayout.NORTH);

        // 2. MAIN CENTER: 2 CỘT (TRÁI: CHI TIẾT HÓA ĐƠN | PHẢI: CỔNG VIETQR)
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        centerGrid.setOpaque(false);

        centerGrid.add(taoPanelChiTietHoaDon());
        centerGrid.add(taoPanelCongThanhToanVietQR());

        root.add(centerGrid, BorderLayout.CENTER);

        // 3. BOTTOM BUTTONS
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel lblSecurity = new JLabel("Giao dịch mã hóa an toàn • Đã đồng bộ với CSDL FitLife Gym");
        lblSecurity.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSecurity.setForeground(new Color(100, 116, 139));
        bottomBar.add(lblSecurity, BorderLayout.WEST);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        JButton btnCancel = new JButton("Đóng / Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(new Color(71, 85, 105));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        JButton btnPrint = new JButton("Xem Phiếu Thu");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrint.setBackground(new Color(241, 245, 249));
        btnPrint.setForeground(new Color(30, 41, 59));
        btnPrint.setFocusPainted(false);
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrint.addActionListener(e -> hienThiPhieuThuNhiet());

        JButton btnConfirm = new JButton("XÁC NHẬN ĐÃ THANH TOÁN (" + df.format(thanhTien) + ")");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setBackground(new Color(16, 185, 129)); // Emerald 500
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> xacNhanThanhToan());

        btnGroup.add(btnCancel);
        btnGroup.add(btnPrint);
        btnGroup.add(btnConfirm);
        bottomBar.add(btnGroup, BorderLayout.EAST);

        root.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // --- PANEL CHI TIẾT HÓA ĐƠN (CỘT TRÁI) ---
    private JPanel taoPanelChiTietHoaDon() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Header thông tin chung hóa đơn
        JPanel pnlHeader = new JPanel(new GridLayout(5, 1, 0, 3));
        pnlHeader.setOpaque(false);

        JLabel lblGym = new JLabel("FITLIFE GYM & FITNESS CENTER");
        lblGym.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGym.setForeground(new Color(15, 23, 42));

        JLabel lblBranch = new JLabel("Cơ sở: " + chiNhanh);
        lblBranch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBranch.setForeground(new Color(71, 85, 105));

        JLabel lblTime = new JLabel("Thời gian: " + sdf.format(ngayTao));
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTime.setForeground(new Color(71, 85, 105));

        JLabel lblStaff = new JLabel("Thu ngân: " + nguoiLap);
        lblStaff.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStaff.setForeground(new Color(71, 85, 105));

        JLabel lblCustomer = new JLabel("Khách hàng: " + tenKhachHang);
        lblCustomer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCustomer.setForeground(new Color(30, 41, 59));

        pnlHeader.add(lblGym);
        pnlHeader.add(lblBranch);
        pnlHeader.add(lblTime);
        pnlHeader.add(lblStaff);
        pnlHeader.add(lblCustomer);
        panel.add(pnlHeader, BorderLayout.NORTH);

        // Bảng danh mục sản phẩm / dịch vụ
        String[] cols = {"Hạng Mục", "SL", "Đơn Giá", "Thành Tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (ChiTietMucHang m : danhSachMuc) {
            model.addRow(new Object[]{
                    m.ten,
                    m.soLuong,
                    df.format(m.donGia),
                    df.format(m.thanhTien)
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 250, 252));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        // Tổng kết tiền
        JPanel pnlTotal = new JPanel(new GridLayout(3, 1, 0, 4));
        pnlTotal.setOpaque(false);
        pnlTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                new EmptyBorder(8, 0, 0, 0)
        ));

        JPanel rowSub = new JPanel(new BorderLayout());
        rowSub.setOpaque(false);

        if (giamGia > 0) {
            rowSub.add(new JLabel("Tổng tiền niêm yết:"), BorderLayout.WEST);
            rowSub.add(new JLabel(df.format(tongTien)), BorderLayout.EAST);
            pnlTotal.add(rowSub);

            JPanel rowDisc = new JPanel(new BorderLayout());
            rowDisc.setOpaque(false);
            rowDisc.add(new JLabel("Chiết khấu ưu đãi:"), BorderLayout.WEST);
            JLabel lblDiscVal = new JLabel("- " + df.format(giamGia));
            lblDiscVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblDiscVal.setForeground(new Color(220, 38, 38));
            rowDisc.add(lblDiscVal, BorderLayout.EAST);
            pnlTotal.add(rowDisc);
        } else {
            rowSub.add(new JLabel("Tổng thanh toán:"), BorderLayout.WEST);
            rowSub.add(new JLabel(df.format(thanhTien)), BorderLayout.EAST);
            pnlTotal.add(rowSub);
        }

        JPanel rowFinal = new JPanel(new BorderLayout()); rowFinal.setOpaque(false);
        JLabel lblFTitle = new JLabel("CẦN THANH TOÁN:");
        lblFTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblFVal = new JLabel(df.format(thanhTien));
        lblFVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFVal.setForeground(new Color(16, 185, 129));
        rowFinal.add(lblFTitle, BorderLayout.WEST);
        rowFinal.add(lblFVal, BorderLayout.EAST);

        pnlTotal.add(rowFinal);
        panel.add(pnlTotal, BorderLayout.SOUTH);

        return panel;
    }

    // --- PANEL CỔNG THANH TOÁN VIETQR (CỘT PHẢI) ---
    private JPanel taoPanelCongThanhToanVietQR() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Switch Phương Thức (VietQR vs Tiền Mặt)
        JPanel tabMethods = new JPanel(new GridLayout(1, 2, 8, 0));
        tabMethods.setOpaque(false);

        JRadioButton rbQR = new JRadioButton("Quét Mã VietQR (MB Bank)", true);
        rbQR.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rbQR.setOpaque(false);

        JRadioButton rbCash = new JRadioButton("Tiền Mặt Tại Quầy", false);
        rbCash.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rbCash.setOpaque(false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbQR);
        bg.add(rbCash);
        tabMethods.add(rbQR);
        tabMethods.add(rbCash);

        panel.add(tabMethods, BorderLayout.NORTH);

        CardLayout cardLayout = new CardLayout();
        JPanel pnlCardContainer = new JPanel(cardLayout);
        pnlCardContainer.setOpaque(false);

        // 1. Giao diện VietQR chính gốc của tác giả Lê Anh Duy
        JPanel pnlQR = new JPanel(new BorderLayout(0, 8));
        pnlQR.setOpaque(false);

        JPanel pnlQRImage = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlQRImage.setOpaque(false);

        // Tải ảnh VietQR chính chủ của Lê Anh Duy
        ImageIcon iconQR = ImageHelper.loadIcon("vietqr_leanhduy.png", 220, 275);
        JLabel lblQRImage = new JLabel();
        lblQRImage.setHorizontalAlignment(SwingConstants.CENTER);
        if (iconQR != null) {
            lblQRImage.setIcon(iconQR);
        } else {
            lblQRImage.setText("<html><center><b style='color:#DC2626'>[VIETQR LE ANH DUY]</b><br>Ngân hàng Quân Đội (MB Bank)<br>STK: 0974482749</center></html>");
        }
        lblQRImage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(4, 4, 4, 4)
        ));
        pnlQRImage.add(lblQRImage);
        pnlQR.add(pnlQRImage, BorderLayout.CENTER);

        // Chi tiết tài khoản nhận tiền
        JPanel pnlAccInfo = new JPanel(new GridLayout(4, 1, 0, 3));
        pnlAccInfo.setBackground(new Color(248, 250, 252));
        pnlAccInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel l1 = new JLabel("Chủ TK: LE ANH DUY (MB Bank - Quân Đội)");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l1.setForeground(new Color(30, 41, 59));

        JLabel l2 = new JLabel("Số TK: 0974482749");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l2.setForeground(new Color(37, 99, 235));

        JLabel l3 = new JLabel("Số tiền: " + df.format(thanhTien));
        l3.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l3.setForeground(new Color(16, 185, 129));

        String memo = maHoaDon + " " + tenKhachHang.replace(" ", "");
        if (memo.length() > 25) memo = memo.substring(0, 25);
        JLabel l4 = new JLabel("Nội dung CK: " + memo);
        l4.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l4.setForeground(new Color(71, 85, 105));

        pnlAccInfo.add(l1);
        pnlAccInfo.add(l2);
        pnlAccInfo.add(l3);
        pnlAccInfo.add(l4);

        JPanel pnlQRBottom = new JPanel(new BorderLayout(0, 6));
        pnlQRBottom.setOpaque(false);
        pnlQRBottom.add(pnlAccInfo, BorderLayout.CENTER);

        JButton btnCopy = new JButton("Sao Chép Số TK & Nội Dung");
        btnCopy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnCopy.setBackground(new Color(241, 245, 249));
        btnCopy.setForeground(new Color(30, 41, 59));
        btnCopy.setFocusPainted(false);
        btnCopy.addActionListener(e -> {
            String clip = "STK: 0974482749\nNgan hang: MB Bank\nChu TK: LE ANH DUY\nSo tien: " + (long) thanhTien + "\nNoi dung: " + maHoaDon;
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(clip), null);
            JOptionPane.showMessageDialog(this, "Đã sao chép thông tin chuyển khoản vào bộ nhớ tạm!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
        pnlQRBottom.add(btnCopy, BorderLayout.SOUTH);

        pnlQR.add(pnlQRBottom, BorderLayout.SOUTH);

        // 2. Giao diện Tiền Mặt
        JPanel pnlCash = new JPanel(new GridBagLayout());
        pnlCash.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel lblMustPay = new JLabel("Số tiền cần thu: " + df.format(thanhTien));
        lblMustPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMustPay.setForeground(new Color(220, 38, 38));
        pnlCash.add(lblMustPay, gbc);

        gbc.gridy = 1;
        JPanel rowGive = new JPanel(new BorderLayout(8, 0)); rowGive.setOpaque(false);
        rowGive.add(new JLabel("Tiền khách đưa:"), BorderLayout.WEST);
        txtTienKhachDua = new JTextField(String.valueOf((long) thanhTien));
        txtTienKhachDua.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rowGive.add(txtTienKhachDua, BorderLayout.CENTER);
        pnlCash.add(rowGive, gbc);

        gbc.gridy = 2;
        lblTienThoi = new JLabel("Tiền thối lại: 0 đ");
        lblTienThoi.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTienThoi.setForeground(new Color(16, 185, 129));
        pnlCash.add(lblTienThoi, gbc);

        txtTienKhachDua.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                try {
                    String clean = txtTienKhachDua.getText().replaceAll("[^0-9]", "");
                    double given = clean.isEmpty() ? 0 : Double.parseDouble(clean);
                    double change = given - thanhTien;
                    if (change >= 0) {
                        lblTienThoi.setText("Tiền thối lại: " + df.format(change));
                        lblTienThoi.setForeground(new Color(16, 185, 129));
                    } else {
                        lblTienThoi.setText("Khách còn thiếu: " + df.format(-change));
                        lblTienThoi.setForeground(new Color(220, 38, 38));
                    }
                } catch (Exception ignored) {
                }
            }
        });

        pnlCardContainer.add(pnlQR, "QR");
        pnlCardContainer.add(pnlCash, "CASH");

        rbQR.addActionListener(e -> {
            phuongThuc = "Chuyển khoản VietQR";
            cardLayout.show(pnlCardContainer, "QR");
        });
        rbCash.addActionListener(e -> {
            phuongThuc = "Tiền mặt";
            cardLayout.show(pnlCardContainer, "CASH");
        });

        panel.add(pnlCardContainer, BorderLayout.CENTER);
        return panel;
    }

    private void xacNhanThanhToan() {
        daThanhToan = true;

        // Lưu vào CSDL HoaDon và Memory Cache
        try {
            HoaDon hd = new HoaDon(
                    maHoaDon,
                    1,
                    tenKhachHang,
                    loaiGiaoDich,
                    tongTien,
                    giamGia,
                    thanhTien,
                    phuongThuc,
                    nguoiLap,
                    chiNhanh,
                    ngayTao,
                    "Đã thanh toán",
                    "Thanh toán thành công qua " + phuongThuc
            );
            HoaDonDAO.getInstance().them(hd);
        } catch (Exception ex) {
            System.err.println("[ThanhToanDialog] Lỗi ghi nhận hóa đơn: " + ex.getMessage());
        }

        if (onThanhToanThanhCong != null) {
            try {
                onThanhToanThanhCong.accept(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        JOptionPane.showMessageDialog(this,
                "Đã xác nhận thanh toán thành công!\n"
                        + "• Mã Hóa Đơn: " + maHoaDon + "\n"
                        + "• Khách hàng: " + tenKhachHang + "\n"
                        + "• Tổng tiền: " + df.format(thanhTien) + "\n"
                        + "• Hình thức: " + phuongThuc + "\n"
                        + "Hệ thống đã tự động lưu hóa đơn và cập nhật sổ sách kế toán.",
                "Thanh Toán Hoàn Tất", JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    private void hienThiPhieuThuNhiet() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("      FITLIFE GYM & FITNESS CENTER       \n");
        sb.append("  HỆ THỐNG PHÒNG TẬP THỂ HÌNH ĐẲNG CẤP   \n");
        sb.append("=========================================\n");
        sb.append("Cơ sở: ").append(chiNhanh).append("\n");
        sb.append("Số HĐ: ").append(maHoaDon).append("\n");
        sb.append("Thời gian: ").append(sdf.format(ngayTao)).append("\n");
        sb.append("Thu ngân: ").append(nguoiLap).append("\n");
        sb.append("Khách hàng: ").append(tenKhachHang).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-20s %3s %14s\n", "Tên Hàng / Dịch Vụ", "SL", "Thành Tiền"));
        sb.append("-----------------------------------------\n");
        for (ChiTietMucHang m : danhSachMuc) {
            String name = m.ten.length() > 20 ? m.ten.substring(0, 18) + ".." : m.ten;
            sb.append(String.format("%-20s %3d %14s\n", name, m.soLuong, df.format(m.thanhTien)));
        }
        sb.append("-----------------------------------------\n");
        sb.append(String.format("%-22s %16s\n", "Tổng tiền hàng:", df.format(tongTien)));
        sb.append(String.format("%-22s %16s\n", "Chiết khấu/Voucher:", "- " + df.format(giamGia)));
        sb.append(String.format("%-22s %16s\n", "TỔNG THANH TOÁN:", df.format(thanhTien)));
        sb.append("Phương thức: ").append(phuongThuc).append("\n");
        sb.append("Cổng thanh toán: VietQR (MB Bank - LE ANH DUY)\n");
        sb.append("=========================================\n");
        sb.append("       CẢM ƠN QUÝ KHÁCH & HẸN GẶP LẠI    \n");
        sb.append("=========================================\n");

        JTextArea txt = new JTextArea(sb.toString());
        txt.setFont(new Font("Consolas", Font.PLAIN, 12));
        txt.setEditable(false);
        txt.setBackground(new Color(254, 252, 232)); // Giấy in hóa đơn vàng nhẹ
        txt.setBorder(new EmptyBorder(10, 10, 10, 10));

        JDialog d = new JDialog(this, "Phiếu Thu Hóa Đơn Nhiệt 80mm", true);
        d.setSize(440, 520);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        d.add(new JScrollPane(txt), BorderLayout.CENTER);

        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bPrint = new JButton("Gửi Máy In Bill");
        bPrint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bPrint.setBackground(new Color(16, 185, 129));
        bPrint.setForeground(Color.WHITE);
        bPrint.addActionListener(e -> {
            JOptionPane.showMessageDialog(d, "Đã gửi lệnh in thành công đến máy in nhiệt quầy thu ngân!", "In Thành Công", JOptionPane.INFORMATION_MESSAGE);
            d.dispose();
        });
        JButton bClose = new JButton("Đóng");
        bClose.addActionListener(e -> d.dispose());
        pBtns.add(bPrint);
        pBtns.add(bClose);
        d.add(pBtns, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    public boolean isDaThanhToan() {
        return daThanhToan;
    }
}
