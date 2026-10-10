package com.nhom12.gym.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class PhanQuyenPanel extends JPanel {
    private JTable roleTable;
    private DefaultTableModel roleModel;
    private JLabel lblRoleConfig;
    private JCheckBox cbCheckIn;
    private JCheckBox cbHoiVien;
    private JCheckBox cbPOS;
    private JCheckBox cbLopHoc;
    private JCheckBox cbGoiTap;
    private JCheckBox cbBaoCao;
    private JCheckBox cbNhanSu;
    private JCheckBox cbXoa;
    private JButton btnSave;

    public PhanQuyenPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("PHÂN QUYỀN TRUY CẬP HỆ THỐNG THEO VAI TRÒ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Giới hạn quyền xem doanh thu, xóa dữ liệu và phân tách tính năng giữa Lễ tân, HLV và Quản lý");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(360);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Panel (Roles)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblRolesTitle = new JLabel("Chọn Vai Trò / Nhóm Quyền");
        lblRolesTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRolesTitle.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblRolesTitle, BorderLayout.NORTH);

        String[] cols = new String[]{"Vai Trò Hệ Thống"};
        Object[][] data = new Object[][]{
                {"Quản trị viên (Chủ phòng tập)"},
                {"Quản lý Chi nhánh"},
                {"Lễ tân Ca Sáng"},
                {"Lễ tân Ca Chiều"},
                {"Huấn Luyện Viên (HLV)"}
        };
        roleModel = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        roleTable = new JTable(roleModel);
        roleTable.setRowHeight(42);
        roleTable.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleTable.setSelectionBackground(new Color(224, 231, 255));
        roleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(roleTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // Right Panel (Permissions)
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        lblRoleConfig = new JLabel("Cấu hình quyền hạn cho: Quản trị viên (Chủ phòng tập)");
        lblRoleConfig.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRoleConfig.setForeground(new Color(30, 41, 59));
        rightPanel.add(lblRoleConfig, BorderLayout.NORTH);

        JPanel checkboxPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        checkboxPanel.setBackground(Color.WHITE);
        checkboxPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font cbFont = new Font("Segoe UI", Font.PLAIN, 15);
        cbCheckIn = new JCheckBox("Quản lý Check-in & Quét mã", true);
        cbCheckIn.setFont(cbFont); cbCheckIn.setBackground(Color.WHITE);

        cbHoiVien = new JCheckBox("Quản lý Hội viên (Thêm/Sửa)", true);
        cbHoiVien.setFont(cbFont); cbHoiVien.setBackground(Color.WHITE);

        cbPOS = new JCheckBox("Bán hàng POS (Nước, Thực phẩm)", true);
        cbPOS.setFont(cbFont); cbPOS.setBackground(Color.WHITE);

        cbLopHoc = new JCheckBox("Đăng ký Lớp học Yoga/Zumba", true);
        cbLopHoc.setFont(cbFont); cbLopHoc.setBackground(Color.WHITE);

        cbGoiTap = new JCheckBox("Quản lý Gói Tập & Giá tiền", true);
        cbGoiTap.setFont(cbFont); cbGoiTap.setBackground(Color.WHITE);

        cbBaoCao = new JCheckBox("Xem Báo cáo Doanh Thu", true);
        cbBaoCao.setFont(cbFont); cbBaoCao.setBackground(Color.WHITE);

        cbNhanSu = new JCheckBox("Quản lý Nhân sự & Bảng Lương", true);
        cbNhanSu.setFont(cbFont); cbNhanSu.setBackground(Color.WHITE);

        cbXoa = new JCheckBox("Quyền XÓA Hóa đơn / Dữ liệu", true);
        cbXoa.setFont(cbFont); cbXoa.setForeground(new Color(239, 68, 68)); cbXoa.setBackground(Color.WHITE);

        checkboxPanel.add(cbCheckIn);
        checkboxPanel.add(cbHoiVien);
        checkboxPanel.add(cbPOS);
        checkboxPanel.add(cbLopHoc);
        checkboxPanel.add(cbGoiTap);
        checkboxPanel.add(cbBaoCao);
        checkboxPanel.add(cbNhanSu);
        checkboxPanel.add(cbXoa);
        rightPanel.add(checkboxPanel, BorderLayout.CENTER);

        JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botPanel.setOpaque(false);
        btnSave = new JButton("Lưu Cấu Hình Phân Quyền");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        botPanel.add(btnSave);
        rightPanel.add(botPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Listeners
        roleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && roleTable.getSelectedRow() != -1) {
                String role = roleModel.getValueAt(roleTable.getSelectedRow(), 0).toString();
                lblRoleConfig.setText("Cấu hình quyền hạn cho: " + role);

                if (role.contains("Chủ")) {
                    cbCheckIn.setSelected(true);
                    cbHoiVien.setSelected(true);
                    cbPOS.setSelected(true);
                    cbLopHoc.setSelected(true);
                    cbGoiTap.setSelected(true);
                    cbBaoCao.setSelected(true);
                    cbNhanSu.setSelected(true);
                    cbXoa.setSelected(true);
                } else if (role.contains("Quản lý")) {
                    cbCheckIn.setSelected(true);
                    cbHoiVien.setSelected(true);
                    cbPOS.setSelected(true);
                    cbLopHoc.setSelected(true);
                    cbGoiTap.setSelected(true);
                    cbBaoCao.setSelected(true);
                    cbNhanSu.setSelected(true);
                    cbXoa.setSelected(false);
                } else if (role.contains("Lễ tân")) {
                    cbCheckIn.setSelected(true);
                    cbHoiVien.setSelected(true);
                    cbPOS.setSelected(true);
                    cbLopHoc.setSelected(true);
                    cbGoiTap.setSelected(false);
                    cbBaoCao.setSelected(false);
                    cbNhanSu.setSelected(false);
                    cbXoa.setSelected(false);
                } else if (role.contains("HLV")) {
                    cbCheckIn.setSelected(true);
                    cbHoiVien.setSelected(false);
                    cbPOS.setSelected(false);
                    cbLopHoc.setSelected(true);
                    cbGoiTap.setSelected(false);
                    cbBaoCao.setSelected(false);
                    cbNhanSu.setSelected(false);
                    cbXoa.setSelected(false);
                }
            }
        });

        btnSave.addActionListener(e -> {
            int sel = roleTable.getSelectedRow();
            String roleName = sel != -1 ? roleModel.getValueAt(sel, 0).toString() : "Quản trị viên";
            JOptionPane.showMessageDialog(this, "Đã lưu và áp dụng phân quyền truy cập cho vai trò [" + roleName + "] thành công!\nCác tài khoản thuộc nhóm này sẽ có hiệu lực ngay trong phiên đăng nhập tiếp theo.", "Lưu Phân Quyền", JOptionPane.INFORMATION_MESSAGE);
        });

        roleTable.setRowSelectionInterval(0, 0);
    }
}
