package com.nhom12.gym.gui.panels;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.service.KhoHangService;
import com.nhom12.gym.util.ImageHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.nhom12.gym.dao.HoaDonDAO;
import com.nhom12.gym.util.Auth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Quầy Bán Hàng POS (Point of Sale)
 * Đồng bộ thời gian thực với Kho Hàng (KhoHangService):
 * - Hiển thị số lượng tồn kho trên từng mặt hàng (Còn hàng / Sắp hết / Hết hàng).
 * - Chặn bán vượt quá tồn kho thực tế.
 * - Tự động GIẢM số lượng tồn kho & ghi nhận Sản Phẩm Đã Bán khi thanh toán thành công.
 */
public class BanHangPOSPanel extends JPanel implements KhoHangService.KhoHangListener {
    private static final Logger LOGGER = Logger.getLogger(BanHangPOSPanel.class.getName());

    private final KhoHangService khoService = KhoHangService.getInstance();
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    private JPanel productGridPanel;
    private DefaultTableModel cartModel;
    private JTable cartTable;
    private JLabel lblTongTien;
    private double tongTien = 0;

    // Lưu trữ mã sản phẩm tương ứng với từng hàng trong giỏ hàng
    private final Map<String, String> mapTenSangMaSP = new HashMap<>();

    public BanHangPOSPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(241, 245, 249));

        // Đăng ký lắng nghe biến động kho hàng từ mọi trạm
        khoService.addListener(this);

        // BÊN TRÁI: DANH SÁCH MẶT HÀNG KHO
        JPanel leftContainer = new JPanel(new BorderLayout(0, 10));
        leftContainer.setOpaque(false);

        JPanel headerLeft = new JPanel(new BorderLayout());
        headerLeft.setOpaque(false);
        JLabel lblLeftTitle = new JLabel("DANH MỤC HÀNG HÓA TỒN KHO (CLICK ĐỂ THÊM VÀO HÓA ĐƠN)");
        lblLeftTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLeftTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSyncStatus = new JLabel("● Đồng bộ kho thời gian thực");
        lblSyncStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSyncStatus.setForeground(new Color(16, 185, 129));

        headerLeft.add(lblLeftTitle, BorderLayout.WEST);
        headerLeft.add(lblSyncStatus, BorderLayout.EAST);
        leftContainer.add(headerLeft, BorderLayout.NORTH);

        productGridPanel = new JPanel(new GridLayout(0, 3, 14, 14));
        productGridPanel.setOpaque(false);

        JScrollPane scrollProduct = new JScrollPane(productGridPanel);
        scrollProduct.setBorder(null);
        scrollProduct.setOpaque(false);
        scrollProduct.getViewport().setOpaque(false);
        scrollProduct.getVerticalScrollBar().setUnitIncrement(16);
        leftContainer.add(scrollProduct, BorderLayout.CENTER);

        // BÊN PHẢI: GIỎ HÀNG & THANH TOÁN
        JPanel rightPanel = new JPanel(new BorderLayout(12, 12));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(460, 0));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblCartHeader = new JLabel("HÓA ĐƠN BÁN HÀNG TẠI QUẦY POS");
        lblCartHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCartHeader.setForeground(new Color(15, 23, 42));
        lblCartHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        rightPanel.add(lblCartHeader, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên Sản Phẩm", "SL", "Đơn Giá", "Thành Tiền"};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(32);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        cartTable.getTableHeader().setBackground(new Color(241, 245, 249));

        // Format cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        cartTable.getColumnModel().getColumn(0).setPreferredWidth(55);
        cartTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(45);
        cartTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        cartTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(95);
        cartTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        JScrollPane scrollCart = new JScrollPane(cartTable);
        scrollCart.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        rightPanel.add(scrollCart, BorderLayout.CENTER);

        // Thanh thao tác dưới giỏ hàng (Giảm / Tăng số lượng chuyên nghiệp)
        JPanel cartActionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        cartActionPanel.setOpaque(false);
        cartActionPanel.setPreferredSize(new Dimension(0, 36));

        JButton btnGiam = new JButton("Giảm Số Lượng (-1)");
        btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGiam.setBackground(new Color(255, 241, 242));
        btnGiam.setForeground(new Color(225, 29, 72));
        btnGiam.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 205, 211), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnGiam.setFocusPainted(false);
        btnGiam.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiam.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnGiam.setBackground(new Color(254, 226, 226));
                btnGiam.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(244, 63, 94), 1),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnGiam.setBackground(new Color(255, 241, 242));
                btnGiam.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(254, 205, 211), 1),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
        });

        JButton btnTang = new JButton("Tăng Số Lượng (+1)");
        btnTang.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTang.setBackground(new Color(236, 253, 245));
        btnTang.setForeground(new Color(5, 150, 105));
        btnTang.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(167, 243, 208), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnTang.setFocusPainted(false);
        btnTang.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTang.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnTang.setBackground(new Color(209, 250, 229));
                btnTang.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(16, 185, 129), 1),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnTang.setBackground(new Color(236, 253, 245));
                btnTang.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(167, 243, 208), 1),
                        BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
        });

        btnGiam.addActionListener(e -> giamSoLuongDongChon());
        btnTang.addActionListener(e -> tangSoLuongDongChon());
        cartActionPanel.add(btnGiam);
        cartActionPanel.add(btnTang);

        JPanel checkoutPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        checkoutPanel.setBackground(Color.WHITE);

        lblTongTien = new JLabel("Tổng cộng: 0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongTien.setForeground(new Color(220, 38, 38));
        lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton btnPay = new JButton("XÁC NHẬN THANH TOÁN (VIETQR / TIỀN MẶT)");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPay.setBackground(new Color(16, 185, 129));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnClear = new JButton("Hủy Toàn Bộ Hóa Đơn (Làm Mới Giỏ)");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(71, 85, 105));
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnClear.addActionListener(e -> {
            cartModel.setRowCount(0);
            tinhLaiTongTien();
        });

        // XỬ LÝ THANH TOÁN VÀ ĐỒNG BỘ GIẢM TỒN KHO
        btnPay.addActionListener(e -> {
            if (tongTien <= 0 || cartModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm vào giỏ trước khi thanh toán!", "Giỏ hàng rỗng", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Chuẩn bị danh sách món thanh toán
            List<com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang> itemsDialog = new ArrayList<>();
            List<KhoHangService.CartItem> itemsXuatKho = new ArrayList<>();

            for (int r = 0; r < cartModel.getRowCount(); r++) {
                String ma = cartModel.getValueAt(r, 0).toString();
                String ten = cartModel.getValueAt(r, 1).toString();
                int sl = Integer.parseInt(cartModel.getValueAt(r, 2).toString());
                double gia = parseTien(cartModel.getValueAt(r, 3).toString());
                double thanhTien = parseTien(cartModel.getValueAt(r, 4).toString());

                itemsDialog.add(new com.nhom12.gym.gui.ThanhToanHoaDonDialog.ChiTietMucHang(ten, sl, gia, thanhTien));
                itemsXuatKho.add(new KhoHangService.CartItem(ma, ten, sl, gia));
            }

            Window parent = SwingUtilities.getWindowAncestor(this);
            com.nhom12.gym.gui.ThanhToanHoaDonDialog dialog = new com.nhom12.gym.gui.ThanhToanHoaDonDialog(
                    parent,
                    null,
                    "Khách Lẻ Quầy POS",
                    "Thanh toán mua hàng quầy POS (" + cartModel.getRowCount() + " mặt hàng)",
                    tongTien,
                    0,
                    tongTien,
                    itemsDialog,
                    success -> {
                        if (success) {
                            cartModel.setRowCount(0);
                            tinhLaiTongTien();
                        }
                    },
                    invoice -> {
                        String staffName = (Auth.hoTen != null && !Auth.hoTen.isEmpty())
                                ? Auth.hoTen
                                : (Auth.tenDangNhap != null ? Auth.tenDangNhap : "Nhân viên");
                        String loi = khoService.xuatKhoBanHang(
                                itemsXuatKho, invoice.getHinhThucThanhToan(), staffName);
                        if (loi != null) {
                            return new com.nhom12.gym.gui.ThanhToanHoaDonDialog.PaymentResult(
                                    com.nhom12.gym.gui.ThanhToanHoaDonDialog.PaymentStatus.FAILED,
                                    "Chưa hoàn tất giao dịch: " + loi
                                            + "\n\nHệ thống chưa lưu hóa đơn thanh toán. Kiểm tra lại giỏ hàng và tồn kho trước khi xác nhận lại."
                            );
                        }

                        invoice.setMaHoiVien(0);
                        invoice.setGhiChu("Bán lẻ quầy POS: " + itemsXuatKho.size() + " mặt hàng");
                        try {
                            if (HoaDonDAO.getInstance().them(invoice)) {
                                return new com.nhom12.gym.gui.ThanhToanHoaDonDialog.PaymentResult(
                                        com.nhom12.gym.gui.ThanhToanHoaDonDialog.PaymentStatus.SUCCESS,
                                        "Đã thanh toán và lưu hóa đơn " + invoice.getMaHoaDon()
                                                + " vào cơ sở dữ liệu.\nTồn kho đã được cập nhật."
                                );
                            }
                        } catch (RuntimeException ex) {
                            LOGGER.log(Level.SEVERE, "Lỗi lưu hóa đơn POS " + invoice.getMaHoaDon(), ex);
                        }

                        return new com.nhom12.gym.gui.ThanhToanHoaDonDialog.PaymentResult(
                                com.nhom12.gym.gui.ThanhToanHoaDonDialog.PaymentStatus.BUFFERED,
                                "Giao dịch " + invoice.getMaHoaDon() + " đã được ghi vào bộ nhớ tạm và tồn kho đã cập nhật,"
                                        + " nhưng chưa xác nhận lưu bền vững vào cơ sở dữ liệu.\n"
                                        + "Không đóng ứng dụng cho đến khi kết nối lại và xác minh hóa đơn."
                        );
                    }
            );
            dialog.setVisible(true);
        });

        checkoutPanel.add(cartActionPanel);
        checkoutPanel.add(lblTongTien);
        checkoutPanel.add(btnPay);
        checkoutPanel.add(btnClear);
        rightPanel.add(checkoutPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, rightPanel);
        splitPane.setResizeWeight(0.64);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);

        // Nạp danh sách thẻ hàng hóa ban đầu
        loadProductCards();
    }

    /**
     * Nạp lại danh sách card sản phẩm từ KhoHangService
     */
    public void loadProductCards() {
        productGridPanel.removeAll();
        List<KhoHangService.SanPhamKho> list = khoService.layTatCaSanPhamKho();

        for (KhoHangService.SanPhamKho sp : list) {
            mapTenSangMaSP.put(sp.getTenSP(), sp.getMaSP());
            productGridPanel.add(createProductCard(sp));
        }

        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    private JPanel createProductCard(KhoHangService.SanPhamKho sp) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        int tonKho = sp.getSoLuongTon();
        boolean hetHang = (tonKho <= 0);

        // Header thẻ: Badge Tồn kho thông minh
        JPanel headerCard = new JPanel(new BorderLayout());
        headerCard.setOpaque(false);

        JLabel lblMaSP = new JLabel(sp.getMaSP());
        lblMaSP.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMaSP.setForeground(new Color(100, 116, 139));

        JLabel lblBadgeTon = new JLabel();
        lblBadgeTon.setOpaque(true);
        lblBadgeTon.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadgeTon.setBorder(new EmptyBorder(2, 6, 2, 6));

        if (hetHang) {
            lblBadgeTon.setText("HẾT HÀNG (Kho: 0)");
            lblBadgeTon.setBackground(new Color(254, 226, 226));
            lblBadgeTon.setForeground(new Color(220, 38, 38));
        } else if (tonKho <= 10) {
            lblBadgeTon.setText("Sắp hết: " + tonKho);
            lblBadgeTon.setBackground(new Color(254, 243, 199));
            lblBadgeTon.setForeground(new Color(180, 83, 9));
        } else {
            lblBadgeTon.setText("Tồn kho: " + tonKho);
            lblBadgeTon.setBackground(new Color(236, 253, 245));
            lblBadgeTon.setForeground(new Color(16, 185, 129));
        }

        headerCard.add(lblMaSP, BorderLayout.WEST);
        headerCard.add(lblBadgeTon, BorderLayout.EAST);
        card.add(headerCard, BorderLayout.NORTH);

        // Center thẻ: Ảnh sản phẩm
        ImageIcon icon = ImageHelper.loadIcon(sp.getHinhAnh(), 95, 95);
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        if (icon != null) {
            lblImage.setIcon(icon);
        } else {
            lblImage.setText(sp.getTenSP());
            lblImage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblImage.setForeground(Color.GRAY);
        }
        card.add(lblImage, BorderLayout.CENTER);

        // Footer thẻ: Tên và Giá bán
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        infoPanel.setOpaque(false);

        JLabel lblName = new JLabel(sp.getTenSP(), JLabel.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(hetHang ? new Color(148, 163, 184) : new Color(30, 41, 59));
        lblName.setToolTipText(sp.getTenSP());

        JLabel lblPrice = new JLabel(df.format(sp.getGiaBan()), JLabel.CENTER);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrice.setForeground(hetHang ? new Color(148, 163, 184) : new Color(220, 38, 38));

        infoPanel.add(lblName);
        infoPanel.add(lblPrice);
        card.add(infoPanel, BorderLayout.SOUTH);

        if (!hetHang) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    themVaoGio(sp);
                }
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(37, 99, 235), 2),
                            new EmptyBorder(9, 9, 9, 9)
                    ));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                            new EmptyBorder(10, 10, 10, 10)
                    ));
                }
            });
        } else {
            card.setBackground(new Color(248, 250, 252));
            card.setToolTipText("Mặt hàng này đã hết tồn kho trong kho chứa, vui lòng nhập thêm!");
        }

        return card;
    }

    /**
     * THÊM SẢN PHẨM VÀO GIỎ HÀNG - KIỂM SOÁT TỒN KHO NGHIÊM NGẶT
     */
    private void themVaoGio(KhoHangService.SanPhamKho sp) {
        int tonKho = sp.getSoLuongTon();
        if (tonKho <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Mặt hàng '" + sp.getTenSP() + "' đã hết trong kho chứa (Tồn kho: 0)!\nKhông thể bán.",
                    "Hết hàng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isExist = false;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if (cartModel.getValueAt(i, 0).equals(sp.getMaSP())) {
                int oldQty = Integer.parseInt(cartModel.getValueAt(i, 2).toString());
                if (oldQty + 1 > tonKho) {
                    JOptionPane.showMessageDialog(this,
                            "Trong kho hiện chỉ còn " + tonKho + " sản phẩm '" + sp.getTenSP() + "'!\nBạn không thể chọn vượt quá số lượng tồn kho.",
                            "Cảnh báo tồn kho", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int newQty = oldQty + 1;
                cartModel.setValueAt(newQty, i, 2);
                cartModel.setValueAt(df.format(newQty * sp.getGiaBan()), i, 4);
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            cartModel.addRow(new Object[]{
                    sp.getMaSP(),
                    sp.getTenSP(),
                    1,
                    df.format(sp.getGiaBan()),
                    df.format(sp.getGiaBan())
            });
        }

        tinhLaiTongTien();
    }

    private void tangSoLuongDongChon() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 sản phẩm trong giỏ hàng để tăng số lượng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String ma = cartModel.getValueAt(row, 0).toString();
        KhoHangService.SanPhamKho sp = khoService.timSanPhamTheoMa(ma);
        if (sp == null) return;

        int oldQty = Integer.parseInt(cartModel.getValueAt(row, 2).toString());
        if (oldQty + 1 > sp.getSoLuongTon()) {
            JOptionPane.showMessageDialog(this,
                    "Số lượng tồn kho của '" + sp.getTenSP() + "' chỉ còn " + sp.getSoLuongTon() + "!",
                    "Vượt tồn kho", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int newQty = oldQty + 1;
        cartModel.setValueAt(newQty, row, 2);
        cartModel.setValueAt(df.format(newQty * sp.getGiaBan()), row, 4);
        tinhLaiTongTien();
    }

    private void giamSoLuongDongChon() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 sản phẩm trong giỏ hàng để giảm số lượng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String ma = cartModel.getValueAt(row, 0).toString();
        KhoHangService.SanPhamKho sp = khoService.timSanPhamTheoMa(ma);
        if (sp == null) return;

        int oldQty = Integer.parseInt(cartModel.getValueAt(row, 2).toString());
        if (oldQty - 1 <= 0) {
            cartModel.removeRow(row);
        } else {
            int newQty = oldQty - 1;
            cartModel.setValueAt(newQty, row, 2);
            cartModel.setValueAt(df.format(newQty * sp.getGiaBan()), row, 4);
        }
        tinhLaiTongTien();
    }

    private void tinhLaiTongTien() {
        tongTien = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            double tt = parseTien(cartModel.getValueAt(i, 4).toString());
            tongTien += tt;
        }
        lblTongTien.setText("Tổng cộng: " + df.format(tongTien));
    }

    private double parseTien(String str) {
        try {
            return Double.parseDouble(str.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onKhoHangChanged() {
        SwingUtilities.invokeLater(this::loadProductCards);
    }

    public static void main(String[] args) {
        try {
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quầy Bán Hàng POS - FitLife Gym");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 680);
            frame.setLocationRelativeTo(null);
            frame.add(new BanHangPOSPanel());
            frame.setVisible(true);
        });
    }
}
