package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HuanLuyenVienDAO;
import com.nhom12.gym.dao.PhanCaLamViecDAO;
import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.HuanLuyenVien;
import com.nhom12.gym.entity.PhanCaLamViec;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.Auth;
import com.nhom12.gym.util.ExcelExportHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PhanCaLamViecPanel extends JPanel {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM");
    private static final String SANG = "SANG";
    private static final String CHIEU = "CHIEU";
    private static final int FIRST_DAY_COLUMN = 1;

    private final PhanCaLamViecDAO phanCaDAO = new PhanCaLamViecDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final HuanLuyenVienDAO huanLuyenVienDAO = new HuanLuyenVienDAO();
    private final Map<CellKey, List<StaffOption>> assignments = new LinkedHashMap<>();
    private final JComboBox<String> branchCombo = new JComboBox<>();
    private final JLabel weekLabel = new JLabel();
    private final DefaultTableModel model;
    private final JTable table;
    private LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    public PhanCaLamViecPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("LỊCH LÀM VIỆC VÀ PHÂN CA");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 41, 59));
        JLabel subtitle = new JLabel("Phân công lễ tân và HLV theo chi nhánh, ngày và ca làm việc");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 116, 139));
        JPanel header = new JPanel(new BorderLayout(0, 5));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(createColumns(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        configureTable();

        JButton previousWeek = new JButton("‹ Tuần trước");
        JButton nextWeek = new JButton("Tuần sau ›");
        JButton save = new JButton("Lưu phân ca");
        JButton export = new JButton("Xuất CSV");
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        toolbar.add(new JLabel("Chi nhánh:"));
        toolbar.add(branchCombo);
        toolbar.add(previousWeek);
        toolbar.add(weekLabel);
        toolbar.add(nextWeek);
        toolbar.add(save);
        toolbar.add(export);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel tip = new JLabel("Nhấp đúp vào một ngày để chọn một hoặc nhiều nhân viên cho ca đó.");
        tip.setForeground(new Color(100, 116, 139));
        center.add(tip, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        populateBranches();
        previousWeek.addActionListener(e -> changeWeek(-7));
        nextWeek.addActionListener(e -> changeWeek(7));
        branchCombo.addActionListener(e -> loadWeek());
        save.addActionListener(e -> saveWeek());
        export.addActionListener(e -> ExcelExportHelper.exportTableToExcel(this, table, "PhanCaLamViec"));
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                int row = table.rowAtPoint(event.getPoint());
                int column = table.columnAtPoint(event.getPoint());
                if (event.getClickCount() == 2 && row >= 0 && column >= FIRST_DAY_COLUMN) {
                    editCell(row, column);
                }
            }
        });
        loadWeek();
    }

    private Object[] createColumns() {
        Object[] columns = new Object[8];
        columns[0] = "Ca làm việc";
        for (int day = 0; day < 7; day++) {
            columns[day + 1] = dayName(day);
        }
        return columns;
    }

    private String dayName(int offset) {
        return new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"}[offset];
    }

    private void configureTable() {
        table.setRowHeight(68);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(37, 99, 235));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable target, Object value, boolean selected,
                                                            boolean focus, int row, int column) {
                Component component = super.getTableCellRendererComponent(target, value, selected, focus, row, column);
                setHorizontalAlignment(CENTER);
                setVerticalAlignment(CENTER);
                setToolTipText(value == null ? null : value.toString().replace('\n', ','));
                if (column == 0) {
                    component.setBackground(new Color(241, 245, 249));
                    component.setForeground(new Color(30, 41, 59));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    component.setBackground(selected ? new Color(219, 234, 254) : Color.WHITE);
                    component.setForeground(new Color(37, 99, 235));
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                return component;
            }
        });
    }

    private void populateBranches() {
        Set<String> branches = new LinkedHashSet<>();
        for (TaiKhoan account : taiKhoanDAO.layTatCa()) {
            if (("Lễ tân".equalsIgnoreCase(account.getVaiTro()) || "HLV".equalsIgnoreCase(account.getVaiTro()))
                    && isBranch(account.getChiNhanh())) {
                branches.add(account.getChiNhanh());
            }
        }
        for (HuanLuyenVien trainer : huanLuyenVienDAO.layTatCa()) {
            if (isBranch(trainer.getChiNhanh())) {
                branches.add(trainer.getChiNhanh());
            }
        }
        String activeBranch = Auth.getChiNhanhHienTai();
        branchCombo.removeAllItems();
        if (isBranch(activeBranch)) {
            branches.add(activeBranch);
        }
        branches.forEach(branchCombo::addItem);
        if (branchCombo.getItemCount() == 0) {
            branchCombo.addItem("CN01 - Cơ sở 1 (Quận 1)");
        }
        if (branches.contains(activeBranch)) {
            branchCombo.setSelectedItem(activeBranch);
        }
    }

    private boolean isBranch(String branch) {
        return branch != null && !branch.trim().isEmpty() && !"Toàn hệ thống".equalsIgnoreCase(branch.trim());
    }

    private void changeWeek(int days) {
        weekStart = weekStart.plusDays(days);
        loadWeek();
    }

    private void loadWeek() {
        if (model == null || branchCombo.getSelectedItem() == null) {
            return;
        }
        weekLabel.setText(weekStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " - " + weekStart.plusDays(6).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        updateColumnDates();
        assignments.clear();
        for (int row = 0; row < 4; row++) {
            model.setValueAt(rowLabel(row), row, 0);
            for (int day = 0; day < 7; day++) {
                model.setValueAt("", row, day + 1);
            }
        }
        for (PhanCaLamViec assignment : phanCaDAO.layTheoTuan(weekStart, selectedBranch())) {
            int row = rowFor(assignment);
            int day = (int) (assignment.getNgayLam().toEpochDay() - weekStart.toEpochDay());
            if (row >= 0 && day >= 0 && day < 7) {
                CellKey key = new CellKey(row, day + FIRST_DAY_COLUMN);
                assignments.computeIfAbsent(key, ignored -> new ArrayList<>())
                        .add(new StaffOption(assignment.getMaNhanVien(), assignment.getTenNhanVien(),
                                assignment.getVaiTro()));
            }
        }
        assignments.forEach((key, staff) -> model.setValueAt(displayStaff(staff), key.row, key.column));
    }

    private void updateColumnDates() {
        model.setColumnIdentifiers(createColumnsWithDates());
    }

    private Object[] createColumnsWithDates() {
        Object[] columns = createColumns();
        for (int day = 0; day < 7; day++) {
            columns[day + 1] = dayName(day) + " (" + weekStart.plusDays(day).format(DATE_FORMAT) + ")";
        }
        return columns;
    }

    private String rowLabel(int row) {
        if (row == 0) return "Lễ tân · Sáng (06:00–14:00)";
        if (row == 1) return "Lễ tân · Chiều (14:00–22:00)";
        if (row == 2) return "HLV sàn · Sáng (06:00–14:00)";
        return "HLV sàn · Chiều (14:00–22:00)";
    }

    private int rowFor(PhanCaLamViec assignment) {
        boolean trainer = "HLV".equalsIgnoreCase(assignment.getVaiTro());
        boolean morning = SANG.equals(assignment.getCaLam());
        return (trainer ? 2 : 0) + (morning ? 0 : 1);
    }

    private String selectedBranch() {
        Object selected = branchCombo.getSelectedItem();
        return selected == null ? "" : selected.toString();
    }

    private void editCell(int row, int column) {
        List<StaffOption> available = staffFor(row);
        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên đang làm việc thuộc chi nhánh này.",
                    "Không có nhân viên", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JList<StaffOption> staffList = new JList<>(available.toArray(new StaffOption[0]));
        staffList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        List<StaffOption> current = assignments.getOrDefault(new CellKey(row, column), new ArrayList<>());
        int[] selectedIndices = available.stream()
                .filter(current::contains)
                .mapToInt(available::indexOf)
                .toArray();
        staffList.setSelectedIndices(selectedIndices);
        JScrollPane scrollPane = new JScrollPane(staffList);
        scrollPane.setPreferredSize(new java.awt.Dimension(340, Math.min(280, Math.max(120, available.size() * 28))));
        int result = JOptionPane.showConfirmDialog(this, scrollPane,
                "Chọn nhân viên · " + rowLabel(row) + " · " + weekStart.plusDays(column - 1).format(DATE_FORMAT),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            CellKey key = new CellKey(row, column);
            List<StaffOption> selected = new ArrayList<>(staffList.getSelectedValuesList());
            if (selected.isEmpty()) {
                assignments.remove(key);
                model.setValueAt("", row, column);
            } else {
                assignments.put(key, selected);
                model.setValueAt(displayStaff(selected), row, column);
            }
        }
    }

    private List<StaffOption> staffFor(int row) {
        List<StaffOption> available = new ArrayList<>();
        boolean trainerRole = row >= 2;
        if (trainerRole) {
            for (HuanLuyenVien trainer : huanLuyenVienDAO.layTatCa()) {
                if (selectedBranch().equals(trainer.getChiNhanh())
                        && (trainer.getTrangThai() == null || trainer.getTrangThai().contains("làm việc"))) {
                    available.add(new StaffOption(trainer.getMaHLV(), trainer.getHoTen(), "HLV"));
                }
            }
        } else {
            for (TaiKhoan account : taiKhoanDAO.layTatCa()) {
                if ("Lễ tân".equalsIgnoreCase(account.getVaiTro()) && selectedBranch().equals(account.getChiNhanh())) {
                    available.add(new StaffOption(account.getTenDangNhap(), account.getHoTen(),
                            account.getVaiTro()));
                }
            }
        }
        return available;
    }

    private String displayStaff(List<StaffOption> staff) {
        return staff.stream().map(option -> option.name).reduce((left, right) -> left + "\n" + right).orElse("");
    }

    private void saveWeek() {
        List<PhanCaLamViec> toSave = new ArrayList<>();
        assignments.forEach((key, staffList) -> {
            LocalDate date = weekStart.plusDays(key.column - FIRST_DAY_COLUMN);
            String shift = key.row % 2 == 0 ? SANG : CHIEU;
            for (StaffOption staff : staffList) {
                toSave.add(new PhanCaLamViec(date, staff.code, staff.name, staff.role, selectedBranch(), shift));
            }
        });
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Lưu toàn bộ lịch phân ca từ " + weekLabel.getText() + " cho " + selectedBranch() + "?",
                "Xác nhận lưu lịch", JOptionPane.YES_NO_OPTION);
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }
        if (phanCaDAO.thayTheTuan(weekStart, selectedBranch(), toSave)) {
            JOptionPane.showMessageDialog(this, "Đã lưu lịch phân ca tuần này.", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể lưu lịch. Kiểm tra dữ liệu nhân viên, ca bị trùng hoặc kết nối cơ sở dữ liệu.",
                    "Lưu thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class CellKey {
        private final int row;
        private final int column;

        private CellKey(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof CellKey)) return false;
            CellKey key = (CellKey) other;
            return row == key.row && column == key.column;
        }

        @Override
        public int hashCode() {
            return 31 * row + column;
        }
    }

    private static final class StaffOption {
        private final String code;
        private final String name;
        private final String role;

        private StaffOption(String code, String name, String role) {
            this.code = code;
            this.name = name;
            this.role = role;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof StaffOption)) return false;
            StaffOption option = (StaffOption) other;
            return code.equalsIgnoreCase(option.code);
        }

        @Override
        public int hashCode() {
            return code.toLowerCase(Locale.ROOT).hashCode();
        }
    }
}
