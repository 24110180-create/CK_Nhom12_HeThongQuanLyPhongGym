package com.nhom12.gym.gui.panels;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhom12.gym.util.ImageHelper;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class BanHangPOSPanel extends JPanel {
    private DefaultTableModel cartModel;
    private JTable cartTable;
    private JLabel lblTongTien;
    private double tongTien = 0;
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    public BanHangPOSPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(241, 245, 249));

        // BÊN TRÁI: DANH SÁCH MẶT HÀNG KÈM HÌNH ẢNH SẢN PHẨM THẬT
        JPanel productPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        productPanel.setOpaque(false);
        productPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Danh Mục Sản Phẩm (Click để thêm vào hóa đơn)",
                0, 0, new Font("Segoe UI", Font.BOLD, 15), new Color(30, 41, 59)
        ));

        productPanel.add(createProductCard("Nước Khoáng Tinh Khiết", 10000, "prod_water.jpg", new Color(14, 165, 233)));
        productPanel.add(createProductCard("Nước Tăng Lực Thể Thao", 45000, "prod_energy.jpg", new Color(34, 197, 94)));
        productPanel.add(createProductCard("Whey Protein Isolate", 65000, "prod_whey.jpg", new Color(168, 85, 247)));
        productPanel.add(createProductCard("Khăn Tập Microfiber", 35000, "prod_towel.jpg", new Color(234, 179, 8)));
        productPanel.add(createProductCard("Găng Tay Tập Tạ Da Bò", 120000, "prod_gloves.jpg", new Color(71, 85, 105)));
        productPanel.add(createProductCard("Chuối Tươi Nạp Năng Lượng", 15000, "prod_banana.jpg", new Color(249, 115, 22)));

        // BÊN PHẢI: GIỎ HÀNG & THANH TOÁN
        JPanel rightPanel = new JPanel(new BorderLayout(12, 12));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(460, 0));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblCartHeader = new JLabel("HÓA ĐƠN BÁN HÀNG TẠI QUẦY");
        lblCartHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCartHeader.setForeground(new Color(15, 23, 42));
        lblCartHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        rightPanel.add(lblCartHeader, BorderLayout.NORTH);

        String[] cols = {"Tên Sản Phẩm", "SL", "Đơn Giá", "Thành Tiền"};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(32);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartTable.getTableHeader().setBackground(new Color(241, 245, 249));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        cartTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollCart = new JScrollPane(cartTable);
        scrollCart.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        rightPanel.add(scrollCart, BorderLayout.CENTER);

        JPanel checkoutPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        checkoutPanel.setBackground(Color.WHITE);

        lblTongTien = new JLabel("Tổng cộng: 0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTongTien.setForeground(new Color(220, 38, 38));
        lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton btnPay = new JButton("XÁC NHẬN THANH TOÁN & IN BILL");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnPay.setBackground(new Color(16, 185, 129));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnClear = new JButton("Hủy Toàn Bộ Hóa Đơn");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClear.setBackground(new Color(241, 245, 249));
        btnClear.setForeground(new Color(71, 85, 105));
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnClear.addActionListener(e -> {
            cartModel.setRowCount(0);
            tongTien = 0;
            lblTongTien.setText("Tổng cộng: 0 đ");
        });

        btnPay.addActionListener(e -> {
            if (tongTien > 0) {
                JOptionPane.showMessageDialog(this,
                        "Thanh toán thành công: " + df.format(tongTien) + "\nĐang tiến hành in hóa đơn cho khách hàng...",
                        "Thanh toán thành công", JOptionPane.INFORMATION_MESSAGE);
                btnClear.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm vào giỏ trước khi thanh toán!");
            }
        });

        checkoutPanel.add(lblTongTien);
        checkoutPanel.add(btnPay);
        checkoutPanel.add(btnClear);
        rightPanel.add(checkoutPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productPanel, rightPanel);
        splitPane.setResizeWeight(0.62);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createProductCard(String name, double price, String imgName, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = ImageHelper.loadIcon(imgName, 110, 110);
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        if (icon != null) {
            lblImage.setIcon(icon);
        } else {
            lblImage.setText("Hình sản phẩm");
            lblImage.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblImage.setForeground(Color.GRAY);
        }
        card.add(lblImage, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        infoPanel.setOpaque(false);

        JLabel lblName = new JLabel(name, JLabel.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(new Color(30, 41, 59));

        JLabel lblPrice = new JLabel(df.format(price), JLabel.CENTER);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPrice.setForeground(new Color(220, 38, 38));

        infoPanel.add(lblName);
        infoPanel.add(lblPrice);
        card.add(infoPanel, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                themVaoGio(name, price);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accentColor, 2),
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

        return card;
    }

    private void themVaoGio(String name, double price) {
        boolean isExist = false;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if (cartModel.getValueAt(i, 0).equals(name)) {
                int oldQty = (int) cartModel.getValueAt(i, 1);
                cartModel.setValueAt(oldQty + 1, i, 1);
                cartModel.setValueAt(df.format((oldQty + 1) * price), i, 3);
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            cartModel.addRow(new Object[]{name, 1, df.format(price), df.format(price)});
        }
        tongTien += price;
        lblTongTien.setText("Tổng cộng: " + df.format(tongTien));
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
