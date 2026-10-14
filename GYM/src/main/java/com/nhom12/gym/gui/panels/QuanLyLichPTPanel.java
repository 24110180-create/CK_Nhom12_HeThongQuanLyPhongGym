package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.HuanLuyenVienDAO;
import com.nhom12.gym.dao.LichPTDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.HuanLuyenVien;
import com.nhom12.gym.entity.LichPT;
import com.nhom12.gym.util.Auth;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class QuanLyLichPTPanel extends JPanel {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final LichPTDAO lichPTDAO = new LichPTDAO();
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final HuanLuyenVienDAO huanLuyenVienDAO = new HuanLuyenVienDAO();
    private final JComboBox<String> branchCombo = new JComboBox<>();
    private final JLabel weekLabel = new JLabel();
    private final JLabel messageLabel = new JLabel(" ");
    private final DefaultTableModel model;
    private final JTable table;
    private final List<LichPT> visibleSchedules = new ArrayList<>();
    private LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    public QuanLyLichPTPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("LỊCH HUẤN LUYỆN CÁ NHÂN (PT)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 41, 59));
        JLabel subtitle = new JLabel("Lễ tân và quản lý đặt lịch cho hội viên; HLV theo dõi lịch được giao và cập nhật kết quả buổi tập");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 116, 139));
        JPanel header = new JPanel(new BorderLayout(0, 5));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        String[] columns = {"Ngày", "Thời gian", "Mã HV", "Hội viên", "Số điện thoại", "HLV", "Chi nhánh", "Trạng thái", "Ghi chú"};
        model = new DefaultTableModel(columns, 0) {
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

        JButton previous = new JButton("‹ Tuần trước");
        JButton next = new JButton("Tuần sau ›");
        JButton create = new JButton("Đặt lịch PT");
        JButton refresh = new JButton("Làm mới");
        JButton complete = new JButton("Đánh dấu hoàn thành");
        JButton cancel = new JButton("Hủy buổi tập");
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        toolbar.add(new JLabel("Chi nhánh:"));
        toolbar.add(branchCombo);
        toolbar.add(previous);
        toolbar.add(weekLabel);
        toolbar.add(next);
        toolbar.add(refresh);
        toolbar.add(create);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JPanel statusActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        statusActions.setOpaque(false);
        statusActions.add(complete);
        statusActions.add(cancel);
        bottom.add(statusActions, BorderLayout.WEST);
        bottom.add(messageLabel, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);
        content.add(toolbar, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);

        boolean manager = hasSchedulingPermission();
        create.setVisible(manager);
        complete.setVisible(Auth.isTrainer());
        cancel.setVisible(Auth.isTrainer() || manager);
        populateBranches();
        branchCombo.addActionListener(e -> loadWeek());
        previous.addActionListener(e -> changeWeek(-7));
        next.addActionListener(e -> changeWeek(7));
        refresh.addActionListener(e -> loadWeek());
        create.addActionListener(e -> openCreateDialog());
        complete.addActionListener(e -> updateSelectedStatus(LichPT.HOAN_THANH));
        cancel.addActionListener(e -> updateSelectedStatus(LichPT.DA_HUY));
        loadWeek();
    }

    private boolean hasSchedulingPermission() {
        return Auth.isManager() || Auth.isBranchManager() || Auth.isReceptionist();
    }

    private void populateBranches() {
        List<String> branches = new ArrayList<>();
        for (HuanLuyenVien trainer : huanLuyenVienDAO.layTatCa()) {
            if (trainer.getChiNhanh() != null && !branches.contains(trainer.getChiNhanh())) {
                branches.add(trainer.getChiNhanh());
            }
        }
        String current = Auth.getChiNhanhHienTai();
        branchCombo.removeAllItems();
        if (hasSchedulingPermission() && Auth.isManager()) {
            branches.forEach(branchCombo::addItem);
        } else {
            branchCombo.addItem(current);
            branchCombo.setEnabled(false);
        }
        if (branchCombo.getItemCount() == 0) {
            branchCombo.addItem(current);
        }
        if (branches.contains(current)) {
            branchCombo.setSelectedItem(current);
        }
    }

    private void changeWeek(int days) {
        weekStart = weekStart.plusDays(days);
        loadWeek();
    }

    private String selectedBranch() {
        Object selected = branchCombo.getSelectedItem();
        return selected == null ? Auth.getChiNhanhHienTai() : selected.toString();
    }

    private String trainerCodeForCurrentUser() {
        if (!Auth.isTrainer()) return null;
        String currentName = Auth.hoTen == null ? "" : Auth.hoTen.trim();
        for (HuanLuyenVien trainer : huanLuyenVienDAO.layTatCa()) {
            if (!currentName.isEmpty() && trainer.getHoTen().trim().equalsIgnoreCase(currentName)) {
                return trainer.getMaHLV();
            }
        }
        String login = Auth.tenDangNhap == null ? "" : Auth.tenDangNhap.trim();
        if (login.matches("(?i)hlv\\d+")) {
            String digits = login.substring(3);
            try {
                return "HLV" + String.format(Locale.ROOT, "%02d", Integer.parseInt(digits));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private void loadWeek() {
        if (model == null || branchCombo.getSelectedItem() == null) {
            return;
        }
        weekLabel.setText(weekStart.format(DATE_TIME) + " - " + weekStart.plusDays(6).format(DATE_TIME));
        messageLabel.setText(" ");
        String trainerCode = trainerCodeForCurrentUser();
        if (Auth.isTrainer() && trainerCode == null) {
            visibleSchedules.clear();
            model.setRowCount(0);
            messageLabel.setText("Không tìm thấy hồ sơ HLV tương ứng với tài khoản đang đăng nhập; không thể hiển thị lịch.");
            return;
        }
        List<LichPT> schedules = lichPTDAO.layTheoTuan(weekStart,
                Auth.isManager() ? selectedBranch() : selectedBranch(), trainerCode);
        visibleSchedules.clear();
        visibleSchedules.addAll(schedules);
        model.setRowCount(0);
        for (LichPT schedule : schedules) {
            LocalDateTime end = schedule.getBatDau().plusMinutes(schedule.getThoiLuongPhut());
            model.addRow(new Object[]{
                    schedule.getBatDau().format(DATE_TIME),
                    schedule.getBatDau().format(TIME) + "–" + end.format(TIME) + " (" + schedule.getThoiLuongPhut() + "p)",
                    schedule.getMaHoiVien(),
                    schedule.getTenHoiVien(),
                    schedule.getSoDienThoaiHoiVien(),
                    schedule.getTenHLV(),
                    schedule.getChiNhanh(),
                    schedule.getTrangThai(),
                    schedule.getGhiChu()
            });
        }
    }

    private void openCreateDialog() {
        List<HoiVien> members = hoiVienDAO.layTatCa().stream()
                .filter(member -> selectedBranch().equals(member.getChiNhanhDangKy()))
                .collect(java.util.stream.Collectors.toList());
        List<HuanLuyenVien> trainers = huanLuyenVienDAO.layTatCa().stream()
                .filter(trainer -> selectedBranch().equals(trainer.getChiNhanh())
                        && (trainer.getTrangThai() == null || trainer.getTrangThai().contains("làm việc")))
                .collect(java.util.stream.Collectors.toList());
        if (members.isEmpty() || trainers.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    members.isEmpty() ? "Không tìm thấy hội viên thuộc chi nhánh này."
                            : "Không tìm thấy HLV đang làm việc thuộc chi nhánh này.",
                    "Không thể đặt lịch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<MemberOption> memberCombo = new JComboBox<>(members.stream().map(MemberOption::new)
                .toArray(MemberOption[]::new));
        JComboBox<TrainerOption> trainerCombo = new JComboBox<>(trainers.stream().map(TrainerOption::new)
                .toArray(TrainerOption[]::new));
        JTextField dateField = new JTextField(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE), 12);
        JTextField timeField = new JTextField("10:00", 7);
        JComboBox<Integer> durationCombo = new JComboBox<>(new Integer[]{30, 60, 90});
        JTextField notesField = new JTextField(22);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
        addFormRow(form, 0, "Hội viên:", memberCombo);
        addFormRow(form, 1, "Huấn luyện viên:", trainerCombo);
        addFormRow(form, 2, "Ngày (yyyy-MM-dd):", dateField);
        addFormRow(form, 3, "Giờ bắt đầu (HH:mm):", timeField);
        addFormRow(form, 4, "Thời lượng (phút):", durationCombo);
        addFormRow(form, 5, "Ghi chú:", notesField);
        int result = JOptionPane.showConfirmDialog(this, form, "Đặt lịch PT · " + selectedBranch(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            LocalDateTime startsAt = LocalDateTime.of(LocalDate.parse(dateField.getText().trim()),
                    LocalTime.parse(timeField.getText().trim()));
            if (startsAt.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Không thể đặt lịch trong quá khứ.");
            }
            MemberOption member = (MemberOption) memberCombo.getSelectedItem();
            TrainerOption trainer = (TrainerOption) trainerCombo.getSelectedItem();
            int duration = (Integer) durationCombo.getSelectedItem();
            LichPT schedule = new LichPT(member.member.getMaHoiVien(), member.member.getHoTen(),
                    member.member.getSoDienThoai(), trainer.trainer.getMaHLV(), trainer.trainer.getHoTen(),
                    selectedBranch(), startsAt, duration, LichPT.DA_DAT, notesField.getText().trim(),
                    Auth.tenDangNhap);
            if (!LichPTDAO.hopLe(schedule)) {
                throw new IllegalArgumentException("Chọn giờ bắt đầu trong khung 06:00–21:00, theo mốc 30 phút; "
                        + "thời lượng 30–90 phút và phải kết thúc trước 22:00.");
            }
            if (!lichPTDAO.them(schedule)) {
                throw new IllegalStateException("Khung giờ bị trùng với lịch khác của HLV hoặc hội viên, hoặc không thể lưu.");
            }
            loadWeek();
            JOptionPane.showMessageDialog(this, "Đã đặt lịch PT cho " + member.member.getHoTen() + ".",
                    "Đặt lịch thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày hoặc giờ không đúng định dạng. Dùng yyyy-MM-dd và HH:mm.",
                    "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Không thể đặt lịch", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addFormRow(JPanel form, int row, String label, JComponent component) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 0;
        constraints.gridy = row;
        form.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        form.add(component, constraints);
    }

    private void updateSelectedStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= visibleSchedules.size()) {
            JOptionPane.showMessageDialog(this, "Chọn một buổi PT trong danh sách trước.",
                    "Chưa chọn lịch", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        LichPT selected = visibleSchedules.get(row);
        if (!LichPT.DA_DAT.equals(selected.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Chỉ buổi đang ở trạng thái '" + LichPT.DA_DAT + "' mới được cập nhật.",
                    "Không thể cập nhật", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Xác nhận " + status.toLowerCase(Locale.ROOT) + " buổi PT của " + selected.getTenHoiVien() + "?",
                "Cập nhật lịch PT", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        boolean updated = lichPTDAO.capNhatTrangThai(selected.getId(), status, trainerCodeForCurrentUser(),
                hasSchedulingPermission());
        if (!updated) {
            JOptionPane.showMessageDialog(this,
                    "Không thể cập nhật lịch này. Hãy kiểm tra quyền và trạng thái hiện tại của buổi tập.",
                    "Cập nhật thất bại", JOptionPane.ERROR_MESSAGE);
            return;
        }
        loadWeek();
    }

    private static final class MemberOption {
        private final HoiVien member;

        private MemberOption(HoiVien member) {
            this.member = member;
        }

        @Override
        public String toString() {
            return member.getHoTen() + " · #" + member.getMaHoiVien() + " · " + member.getSoDienThoai();
        }
    }

    private static final class TrainerOption {
        private final HuanLuyenVien trainer;

        private TrainerOption(HuanLuyenVien trainer) {
            this.trainer = trainer;
        }

        @Override
        public String toString() {
            return trainer.getHoTen() + " · " + trainer.getMaHLV();
        }
    }
}
