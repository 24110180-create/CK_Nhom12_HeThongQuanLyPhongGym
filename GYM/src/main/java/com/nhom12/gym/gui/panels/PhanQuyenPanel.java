package com.nhom12.gym.gui.panels;

import com.nhom12.gym.service.PermissionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Giao diện Phân Quyền Truy Cập Hệ Thống Theo Vai Trò (RBAC)
 * Đã liên kết 100% với PermissionService và Dashboard:
 * - Thay đổi quyền sẽ lập tức ẩn/hiện chức năng tương ứng trên menu điều hành.
 * - Cho phép thêm nhóm vai trò mới, đặt lại mặc định, và lưu trữ vĩnh viễn.
 */
public class PhanQuyenPanel extends JPanel {

    private JTable roleTable;
    private DefaultTableModel roleModel;
    private JLabel lblRoleConfig;
    private JLabel lblStatus;

    private JCheckBox cbCheckIn;
    private JCheckBox cbHoiVien;
    private JCheckBox cbPOS;
    private JCheckBox cbLopHoc;
    private JCheckBox cbGoiTap;
    private JCheckBox cbBaoCao;
    private JCheckBox cbNhanSu;
    private JCheckBox cbXoa;

    private JButton btnSave;
    private JButton btnAddRole;
    private JButton btnDeleteRole;
    private JButton btnResetDefault;
    private JButton btnSelectAll;
    private JButton btnDeselectAll;

    private final PermissionService permService = PermissionService.getInstance();
    private boolean isUpdatingUI = false;

    public PhanQuyenPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(0, 4));
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("PHÂN QUYỀN TRUY CẬP HỆ THỐNG THEO VAI TRÒ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Giới hạn quyền xem doanh thu, bán hàng POS, quản lý hội viên và phân tách tính năng giữa các vai trò điều hành");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(360);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // ================= CỘT TRÁI: DANH SÁCH VAI TRÒ =================
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblRolesTitle = new JLabel("Danh Sách Vai Trò / Nhóm Quyền");
        lblRolesTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRolesTitle.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblRolesTitle, BorderLayout.NORTH);

        roleModel = new DefaultTableModel(new String[]{"Vai Trò Hệ Thống"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        roleTable = new JTable(roleModel);
        roleTable.setRowHeight(40);
        roleTable.setFont(new Font("Segoe UI", Font.BOLD, 13));
        roleTable.setSelectionBackground(new Color(224, 231, 255));
        roleTable.setSelectionForeground(new Color(30, 41, 59));
        roleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(roleTable), BorderLayout.CENTER);

        // Cụm nút quản lý vai trò
        JPanel leftActions = new JPanel(new GridLayout(2, 1, 0, 6));
        leftActions.setOpaque(false);

        JPanel rowBtn1 = new JPanel(new GridLayout(1, 2, 6, 0));
        rowBtn1.setOpaque(false);
        btnAddRole = new JButton("+ Thêm Vai Trò");
        btnAddRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddRole.setBackground(new Color(241, 245, 249));
        btnAddRole.setForeground(new Color(30, 41, 59));
        btnAddRole.setFocusPainted(false);
        btnAddRole.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddRole.addActionListener(e -> themVaiTroMoi());

        btnDeleteRole = new JButton("🗑️ Xóa Vai Trò");
        btnDeleteRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDeleteRole.setBackground(new Color(241, 245, 249));
        btnDeleteRole.setForeground(new Color(220, 38, 38));
        btnDeleteRole.setFocusPainted(false);
        btnDeleteRole.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDeleteRole.addActionListener(e -> xoaVaiTroDangChon());
        rowBtn1.add(btnAddRole);
        rowBtn1.add(btnDeleteRole);

        btnResetDefault = new JButton("🔄 Khôi Phục Mặc Định Ban Đầu");
        btnResetDefault.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnResetDefault.setBackground(new Color(248, 250, 252));
        btnResetDefault.setForeground(new Color(71, 85, 105));
        btnResetDefault.setFocusPainted(false);
        btnResetDefault.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetDefault.addActionListener(e -> khoiPhucMacDinh());

        leftActions.add(rowBtn1);
        leftActions.add(btnResetDefault);
        leftPanel.add(leftActions, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // ================= CỘT PHẢI: CẤU HÌNH QUYỀN HẠN =================
        JPanel rightPanel = new JPanel(new BorderLayout(0, 12));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.setOpaque(false);

        lblRoleConfig = new JLabel("Cấu hình quyền hạn cho: Quản trị viên (Chủ phòng tập)");
        lblRoleConfig.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRoleConfig.setForeground(new Color(30, 41, 59));
        rightHeader.add(lblRoleConfig, BorderLayout.WEST);

        JPanel fastSelectGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        fastSelectGroup.setOpaque(false);
        btnSelectAll = new JButton("Chọn Tất Cả");
        btnSelectAll.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnSelectAll.setBackground(new Color(241, 245, 249));
        btnSelectAll.setFocusPainted(false);
        btnSelectAll.addActionListener(e -> setAllCheckboxes(true));

        btnDeselectAll = new JButton("Bỏ Chọn Hết");
        btnDeselectAll.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnDeselectAll.setBackground(new Color(241, 245, 249));
        btnDeselectAll.setFocusPainted(false);
        btnDeselectAll.addActionListener(e -> setAllCheckboxes(false));

        fastSelectGroup.add(btnSelectAll);
        fastSelectGroup.add(btnDeselectAll);
        rightHeader.add(fastSelectGroup, BorderLayout.EAST);
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        // Checkboxes
        JPanel checkboxPanel = new JPanel(new GridLayout(0, 2, 16, 18));
        checkboxPanel.setBackground(Color.WHITE);
        checkboxPanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        Font cbFont = new Font("Segoe UI", Font.PLAIN, 14);
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

        // Footer Right
        JPanel rightFooter = new JPanel(new BorderLayout());
        rightFooter.setOpaque(false);

        lblStatus = new JLabel("● Trạng thái: Dữ liệu quyền đang đồng bộ thời gian thực");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(16, 185, 129));
        rightFooter.add(lblStatus, BorderLayout.WEST);

        btnSave = new JButton("✓ Lưu Cấu Hình & Áp Dụng Ngay Lập Tức");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> luuCauHinhHienTai());
        rightFooter.add(btnSave, BorderLayout.EAST);

        rightPanel.add(rightFooter, BorderLayout.SOUTH);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Nạp dữ liệu vai trò ban đầu
        taiDanhSachVaiTro();

        // Listener khi chọn dòng trên bảng vai trò
        roleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && roleTable.getSelectedRow() != -1) {
                napQuyenTheoVaiTroDangChon();
            }
        });

        if (roleTable.getRowCount() > 0) {
            roleTable.setRowSelectionInterval(0, 0);
        }
    }

    private void taiDanhSachVaiTro() {
        roleModel.setRowCount(0);
        List<String> roles = permService.getAllRoles();
        for (String r : roles) {
            roleModel.addRow(new Object[]{r});
        }
    }

    private String getSelectedRoleName() {
        int sel = roleTable.getSelectedRow();
        if (sel != -1) {
            return roleModel.getValueAt(sel, 0).toString();
        }
        return "Quản trị viên (Chủ phòng tập)";
    }

    private void napQuyenTheoVaiTroDangChon() {
        isUpdatingUI = true;
        String role = getSelectedRoleName();
        lblRoleConfig.setText("Cấu hình quyền hạn cho: " + role);

        Set<String> perms = permService.getPermissions(role);
        cbCheckIn.setSelected(perms.contains(PermissionService.PERM_CHECKIN));
        cbHoiVien.setSelected(perms.contains(PermissionService.PERM_HOIVIEN));
        cbPOS.setSelected(perms.contains(PermissionService.PERM_POS));
        cbLopHoc.setSelected(perms.contains(PermissionService.PERM_LOPHOC));
        cbGoiTap.setSelected(perms.contains(PermissionService.PERM_GOITAP));
        cbBaoCao.setSelected(perms.contains(PermissionService.PERM_BAOCAO));
        cbNhanSu.setSelected(perms.contains(PermissionService.PERM_NHANSU));
        cbXoa.setSelected(perms.contains(PermissionService.PERM_XOA));

        lblStatus.setText("● Đang xem quyền của vai trò: [" + role + "]");
        lblStatus.setForeground(new Color(71, 85, 105));
        isUpdatingUI = false;
    }

    private void setAllCheckboxes(boolean selected) {
        cbCheckIn.setSelected(selected);
        cbHoiVien.setSelected(selected);
        cbPOS.setSelected(selected);
        cbLopHoc.setSelected(selected);
        cbGoiTap.setSelected(selected);
        cbBaoCao.setSelected(selected);
        cbNhanSu.setSelected(selected);
        cbXoa.setSelected(selected);
    }

    private void luuCauHinhHienTai() {
        String role = getSelectedRoleName();
        Set<String> perms = new HashSet<>();
        if (cbCheckIn.isSelected()) perms.add(PermissionService.PERM_CHECKIN);
        if (cbHoiVien.isSelected()) perms.add(PermissionService.PERM_HOIVIEN);
        if (cbPOS.isSelected()) perms.add(PermissionService.PERM_POS);
        if (cbLopHoc.isSelected()) perms.add(PermissionService.PERM_LOPHOC);
        if (cbGoiTap.isSelected()) perms.add(PermissionService.PERM_GOITAP);
        if (cbBaoCao.isSelected()) perms.add(PermissionService.PERM_BAOCAO);
        if (cbNhanSu.isSelected()) perms.add(PermissionService.PERM_NHANSU);
        if (cbXoa.isSelected()) perms.add(PermissionService.PERM_XOA);

        permService.setPermissions(role, perms);

        lblStatus.setText("✓ Đã lưu và áp dụng tức thì cho vai trò [" + role + "] (" + perms.size() + " quyền)!");
        lblStatus.setForeground(new Color(16, 185, 129));

        JOptionPane.showMessageDialog(this,
                "✓ Đã lưu cấu hình phân quyền cho vai trò [" + role + "] thành công!\n"
                        + "Hệ thống đã cập nhật tức thì. Khi người dùng thuộc vai trò này đăng nhập hoặc chuyển góc nhìn,\n"
                        + "các chức năng được cấp sẽ xuất hiện và các chức năng bị bỏ chọn sẽ ẩn đi.",
                "Lưu Phân Quyền Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void themVaiTroMoi() {
        String newRole = JOptionPane.showInputDialog(this,
                "Nhập tên vai trò / nhóm quyền mới cần thêm vào hệ thống:\n(Ví dụ: Kế toán thu ngân, Quản lý ca tối, Chuyên viên tư vấn)",
                "Thêm Nhóm Vai Trò", JOptionPane.QUESTION_MESSAGE);

        if (newRole != null && !newRole.trim().isEmpty()) {
            newRole = newRole.trim();
            for (int i = 0; i < roleModel.getRowCount(); i++) {
                if (roleModel.getValueAt(i, 0).toString().equalsIgnoreCase(newRole)) {
                    JOptionPane.showMessageDialog(this, "Vai trò '" + newRole + "' đã tồn tại trong danh sách!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Gán quyền cơ bản mặc định
            Set<String> base = new HashSet<>();
            base.add(PermissionService.PERM_CHECKIN);
            permService.addRole(newRole, base);

            taiDanhSachVaiTro();

            // Chọn vào dòng mới tạo
            for (int i = 0; i < roleModel.getRowCount(); i++) {
                if (roleModel.getValueAt(i, 0).toString().equalsIgnoreCase(newRole)) {
                    roleTable.setRowSelectionInterval(i, i);
                    break;
                }
            }

            JOptionPane.showMessageDialog(this, "Đã thêm vai trò '" + newRole + "' thành công!\nBạn có thể tích chọn các quyền tương ứng và bấm [Lưu Cấu Hình].", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xoaVaiTroDangChon() {
        int sel = roleTable.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vai trò cần xóa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String role = roleModel.getValueAt(sel, 0).toString();
        if (role.contains("Chủ") || role.contains("Quản lý Chi nhánh") || role.contains("Lễ tân Ca Sáng")) {
            JOptionPane.showMessageDialog(this, "Không thể xóa vai trò hệ thống mặc định cốt lõi: [" + role + "]!", "Bảo Vệ Hệ Thống", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cf = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhóm vai trò: [" + role + "] khỏi hệ thống?",
                "Xác Nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (cf == JOptionPane.YES_OPTION) {
            permService.removeRole(role);
            taiDanhSachVaiTro();
            if (roleTable.getRowCount() > 0) {
                roleTable.setRowSelectionInterval(0, 0);
            }
            JOptionPane.showMessageDialog(this, "Đã xóa vai trò [" + role + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void khoiPhucMacDinh() {
        int cf = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn khôi phục phân quyền của tất cả vai trò về cấu hình chuẩn ban đầu?",
                "Khôi Phục Mặc Định", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (cf == JOptionPane.YES_OPTION) {
            permService.resetToDefault();
            taiDanhSachVaiTro();
            if (roleTable.getRowCount() > 0) {
                roleTable.setRowSelectionInterval(0, 0);
            }
            JOptionPane.showMessageDialog(this, "Đã khôi phục toàn bộ phân quyền về mặc định thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
