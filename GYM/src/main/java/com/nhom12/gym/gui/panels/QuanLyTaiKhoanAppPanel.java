package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.service.MemberAccountService;
import com.nhom12.gym.service.PermissionService;
import com.nhom12.gym.util.Auth;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class QuanLyTaiKhoanAppPanel extends JPanel {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final MemberAccountService accountService = new MemberAccountService();
    private final List<HoiVien> visibleMembers = new ArrayList<>();
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel countLabel = new JLabel();

    public QuanLyTaiKhoanAppPanel() {
        if (!PermissionService.getInstance().hasPermission(Auth.vaiTro, PermissionService.PERM_MEMBER_ACCOUNTS)
                || !(Auth.isManager() || Auth.isBranchManager() || Auth.isReceptionist())) {
            throw new SecurityException("Vai trò hiện tại không có quyền quản lý tài khoản hội viên.");
        }
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("QUẢN LÝ TÀI KHOẢN HỘI VIÊN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(new Color(15, 23, 42));
        JLabel subtitle = new JLabel("Cấp tài khoản từ hồ sơ hội viên hiện có; username là số điện thoại; mật khẩu được lưu dạng băm.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 116, 139));
        JPanel header = new JPanel(new BorderLayout(0, 5));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        JButton refresh = new JButton("Làm mới");
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        JButton issue = new JButton("Cấp / cấp lại tài khoản");
        JButton reset = new JButton("Đặt lại mật khẩu");
        JButton toggle = new JButton("Khóa / mở khóa");
        toolbar.add(issue);
        toolbar.add(reset);
        toolbar.add(toggle);
        toolbar.add(refresh);
        toolbar.add(countLabel);

        model = new DefaultTableModel(new Object[]{
                "Mã HV", "Họ tên hội viên", "Số điện thoại / username", "Chi nhánh", "Gói tập", "Tài khoản"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);
        content.add(toolbar, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        issue.addActionListener(e -> issueAccount());
        reset.addActionListener(e -> resetPassword());
        toggle.addActionListener(e -> toggleAccount());
        refresh.addActionListener(e -> loadMembers());
        loadMembers();
    }

    private void loadMembers() {
        visibleMembers.clear();
        model.setRowCount(0);
        for (HoiVien member : hoiVienDAO.layTatCa()) {
            if (!Auth.isManager() && !Auth.getChiNhanhHienTai().equalsIgnoreCase(member.getChiNhanhDangKy())) {
                continue;
            }
            visibleMembers.add(member);
            TaiKhoan account = memberAccount(member);
            String status = account == null ? "Chưa cấp tài khoản" : account.getTrangThaiTaiKhoan();
            model.addRow(new Object[]{
                    member.getMaHoiVien(), member.getHoTen(), member.getSoDienThoai(),
                    member.getChiNhanhDangKy(), member.getGoiTapDangKy(), status
            });
        }
        countLabel.setText("Tổng hồ sơ trong phạm vi: " + visibleMembers.size());
    }

    private TaiKhoan memberAccount(HoiVien member) {
        if (member.getSoDienThoai() == null) return null;
        TaiKhoan account = taiKhoanDAO.timTheoTenDangNhap(member.getSoDienThoai());
        if (account == null || !"Hội viên".equalsIgnoreCase(account.getVaiTro())) return null;
        return account.getMaHoiVien() != null && account.getMaHoiVien() == member.getMaHoiVien() ? account : null;
    }

    private HoiVien selectedMember() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= visibleMembers.size()) {
            JOptionPane.showMessageDialog(this, "Chọn hồ sơ hội viên trước.", "Chưa chọn hội viên",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return visibleMembers.get(row);
    }

    private void issueAccount() {
        HoiVien member = selectedMember();
        if (member == null) return;
        String phone = member.getSoDienThoai();
        if (phone == null || !phone.matches("0\\d{9}")) {
            JOptionPane.showMessageDialog(this, "Hồ sơ cần có số điện thoại hợp lệ gồm 10 chữ số, bắt đầu bằng 0.",
                    "Không thể cấp tài khoản", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TaiKhoan existing = memberAccount(member);
        if (existing != null && JOptionPane.showConfirmDialog(this,
                "Hội viên đã có tài khoản. Cấp lại sẽ mở khóa tài khoản và thay mật khẩu tạm. Tiếp tục?",
                "Cấp lại tài khoản", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        String temporaryPassword = generateTemporaryPassword();
        if (!accountService.capTaiKhoan(member, temporaryPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Không thể cấp tài khoản. Hãy kiểm tra quyền, chi nhánh, số điện thoại hoặc tài khoản username đã được dùng.",
                    "Cấp tài khoản thất bại", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadMembers();
        JOptionPane.showMessageDialog(this,
                "Đã cấp tài khoản cho " + member.getHoTen()
                        + "\nTên đăng nhập: " + phone
                        + "\nMật khẩu tạm: " + temporaryPassword
                        + "\nYêu cầu hội viên đổi mật khẩu sau khi đăng nhập.",
                "Tài khoản đã được cấp", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetPassword() {
        HoiVien member = selectedMember();
        if (member == null) return;
        TaiKhoan account = memberAccount(member);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Hội viên chưa có tài khoản. Dùng chức năng cấp tài khoản trước.",
                    "Chưa có tài khoản", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String temporaryPassword = generateTemporaryPassword();
        if (!accountService.datLaiMatKhau(account.getTenDangNhap(), temporaryPassword)) {
            JOptionPane.showMessageDialog(this, "Không thể đặt lại mật khẩu. Vui lòng thử lại.",
                    "Đặt lại thất bại", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Mật khẩu tạm mới của " + member.getHoTen() + ":\n"
                + temporaryPassword + "\nHãy chuyển riêng cho hội viên và yêu cầu đổi ngay.",
                "Đặt lại mật khẩu thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleAccount() {
        HoiVien member = selectedMember();
        if (member == null) return;
        TaiKhoan account = memberAccount(member);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Hội viên chưa có tài khoản.", "Chưa có tài khoản",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String newStatus = "Tạm khóa".equalsIgnoreCase(account.getTrangThaiTaiKhoan())
                ? "Đang hoạt động" : "Tạm khóa";
        if (JOptionPane.showConfirmDialog(this, "Chuyển tài khoản của " + member.getHoTen()
                + " sang trạng thái '" + newStatus + "'?", "Xác nhận trạng thái",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        if (!accountService.doiTrangThai(account.getTenDangNhap(), newStatus)) {
            JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái tài khoản.",
                    "Cập nhật thất bại", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadMembers();
    }

    private String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(12);
        password.append((char) ('A' + RANDOM.nextInt(26)));
        password.append((char) ('2' + RANDOM.nextInt(8)));
        while (password.length() < 12) {
            password.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        for (int i = password.length() - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }
        return password.toString();
    }
}
