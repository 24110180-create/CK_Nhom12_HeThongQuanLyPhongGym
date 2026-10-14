package com.nhom12.gym.gui.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

public class LichLopHocPanel extends JPanel {
    private JTextField txtMaLop;
    private JComboBox<String> cbTenLop;
    private JComboBox<String> cbHLV;
    private JComboBox<String> cbPhong;
    private JComboBox<String> cbGio;
    private JComboBox<String> cbThu;
    private JTextField txtMaxStudent;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnBook;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;

    public LichLopHocPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("LỊCH LỚP HỌC & ĐĂNG KÝ (YOGA, ZUMBA, PILATES)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Quản lý danh sách lớp học nhóm, phân công huấn luyện viên, kiểm soát sĩ số phòng tập");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(6);
        splitPane.setResizeWeight(0.36);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Form
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFormTitle = new JLabel("Thông Tin Lớp Học");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        int r = 0;
        // Class Code
        gbc.gridx = 0; gbc.gridy = r++;
        JLabel lCode = new JLabel("Mã Lớp Học (*):"); lCode.setFont(fontLabel);
        formGrid.add(lCode, gbc);
        gbc.gridy = r++;
        txtMaLop = new JTextField(); txtMaLop.setFont(fontInput);
        styleInput(txtMaLop);
        formGrid.add(txtMaLop, gbc);

        // Class Name
        gbc.gridy = r++;
        JLabel lName = new JLabel("Bộ Môn Lớp Học:"); lName.setFont(fontLabel);
        formGrid.add(lName, gbc);
        gbc.gridy = r++;
        cbTenLop = new JComboBox<>(new String[]{"Yoga Cổ Điển", "Zumba Dance", "Boxing & Kickfit", "Pilates Giảm Mỡ", "Cardio BodyCombat"});
        cbTenLop.setFont(fontInput);
        styleInput(cbTenLop);
        formGrid.add(cbTenLop, gbc);

        // Coach
        gbc.gridy = r++;
        JLabel lCoach = new JLabel("Huấn Luyện Viên:"); lCoach.setFont(fontLabel);
        formGrid.add(lCoach, gbc);
        gbc.gridy = r++;
        cbHLV = new JComboBox<>(new String[]{"HLV John Smith", "HLV Phạm Quốc Bảo", "HLV Anh Tuấn", "HLV Thu Hà"});
        cbHLV.setFont(fontInput);
        styleInput(cbHLV);
        formGrid.add(cbHLV, gbc);

        // Room
        gbc.gridy = r++;
        JLabel lRoom = new JLabel("Phòng Tập:"); lRoom.setFont(fontLabel);
        formGrid.add(lRoom, gbc);
        gbc.gridy = r++;
        cbPhong = new JComboBox<>(new String[]{"Phòng 01 (Yoga/Pilates)", "Phòng 02 (Zumba/Aerobic)", "Sàn Đấu Boxing", "Khu Functional"});
        cbPhong.setFont(fontInput);
        styleInput(cbPhong);
        formGrid.add(cbPhong, gbc);

        // Schedule & Days
        gbc.gridy = r++;
        JLabel lTime = new JLabel("Khung Giờ:"); lTime.setFont(fontLabel);
        formGrid.add(lTime, gbc);
        gbc.gridy = r++;
        cbGio = new JComboBox<>(new String[]{"07:00 - 08:30", "09:00 - 10:30", "15:00 - 16:30", "18:00 - 19:30", "19:45 - 21:00"});
        cbGio.setFont(fontInput);
        styleInput(cbGio);
        formGrid.add(cbGio, gbc);

        gbc.gridy = r++;
        JLabel lDay = new JLabel("Lịch Tập Trong Tuần:"); lDay.setFont(fontLabel);
        formGrid.add(lDay, gbc);
        gbc.gridy = r++;
        cbThu = new JComboBox<>(new String[]{"Thứ 2 - 4 - 6", "Thứ 3 - 5 - 7", "Thứ 7 & Chủ Nhật", "Cả tuần (T2 - T7)"});
        cbThu.setFont(fontInput);
        styleInput(cbThu);
        formGrid.add(cbThu, gbc);

        // Max Students
        gbc.gridy = r++;
        JLabel lMax = new JLabel("Sĩ Số Tối Đa:"); lMax.setFont(fontLabel);
        formGrid.add(lMax, gbc);
        gbc.gridy = r++;
        txtMaxStudent = new JTextField("25");
        txtMaxStudent.setFont(fontInput);
        styleInput(txtMaxStudent);
        formGrid.add(txtMaxStudent, gbc);

        gbc.gridy = r++;
        gbc.weighty = 1.0;
        formGrid.add(Box.createVerticalGlue(), gbc);

        leftPanel.add(formGrid, BorderLayout.CENTER);

        // Form Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = new JButton("Thêm Lớp Mới");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(new Color(16, 185, 129));
        btnAdd.setForeground(Color.WHITE);
        styleActionButton(btnAdd);

        btnUpdate = new JButton("Cập Nhật");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdate.setBackground(new Color(37, 99, 235));
        btnUpdate.setForeground(Color.WHITE);
        styleActionButton(btnUpdate);

        btnDelete = new JButton("Hủy Lớp");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);
        styleActionButton(btnDelete);

        btnClear = new JButton("Làm Mới");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setBackground(new Color(100, 116, 139));
        btnClear.setForeground(Color.WHITE);
        styleActionButton(btnClear);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // Right Table
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Search Bar & Book Button on Right Top
        JPanel rightTop = new JPanel(new BorderLayout());
        rightTop.setOpaque(false);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("Lọc lớp học:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = new JTextField(15);
        txtSearch.setFont(fontInput);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tên lớp, HLV hoặc khung giờ...");
        styleInput(txtSearch);
        pSearch.add(lblSearch);
        pSearch.add(txtSearch);
        rightTop.add(pSearch, BorderLayout.WEST);

        JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pButtons.setOpaque(false);

        btnBook = new JButton("+ Ghi Danh Học Viên");
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBook.setBackground(new Color(16, 185, 129));
        btnBook.setForeground(Color.WHITE);
        btnBook.setCursor(new Cursor(Cursor.HAND_CURSOR));
        styleActionButton(btnBook);

        JButton btnDiemDanh = new JButton("📋 Danh Sách Điểm Danh");
        btnDiemDanh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDiemDanh.setBackground(new Color(37, 99, 235));
        btnDiemDanh.setForeground(Color.WHITE);
        btnDiemDanh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        styleActionButton(btnDiemDanh);
        btnDiemDanh.addActionListener(e -> moDialogDiemDanh());

        JButton btnExport = new JButton("📊 Xuất Excel");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExport.setBackground(new Color(241, 245, 249));
        btnExport.setForeground(new Color(15, 118, 110));
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        styleActionButton(btnExport);
        btnExport.addActionListener(e -> com.nhom12.gym.util.ExcelExportHelper.exportTableToExcel(this, table, "LichLopHoc_FitLife"));

        pButtons.add(btnBook);
        pButtons.add(btnDiemDanh);
        pButtons.add(btnExport);
        rightTop.add(pButtons, BorderLayout.EAST);
        rightPanel.add(rightTop, BorderLayout.NORTH);

        String[] cols = new String[]{"Mã Lớp", "Tên Lớp", "HLV Phụ Trách", "Phòng", "Khung Giờ", "Lịch Học", "Sĩ Số"};
        Object[][] data = new Object[][]{
                {"LOP01", "Yoga Cổ Điển", "HLV Thu Hà", "Phòng 01", "07:00 - 08:30", "Thứ 2 - 4 - 6", "15/20"},
                {"LOP02", "Zumba Dance", "HLV John Smith", "Phòng 02", "09:00 - 10:30", "Thứ 3 - 5 - 7", "18/25"},
                {"LOP03", "Boxing & Kickfit", "HLV Phạm Quốc Bảo", "Sàn Đấu Boxing", "15:00 - 16:30", "Thứ 2 - 4 - 6", "8/12"},
                {"LOP04", "Cardio BodyCombat", "HLV Anh Tuấn", "Phòng 01", "18:00 - 19:30", "Cả tuần (T2 - T7)", "22/30"},
                {"LOP05", "Pilates Giảm Mỡ", "HLV Thu Hà", "Phòng 01", "19:45 - 21:00", "Thứ 3 - 5 - 7", "10/15"}
        };

        model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(fontInput);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        styleDataTable(table, 1, 2);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Filter search
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
        });

        // Table Selection -> Populate form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int mr = table.convertRowIndexToModel(table.getSelectedRow());
                txtMaLop.setText(model.getValueAt(mr, 0).toString());
                txtMaLop.setEditable(false);
                cbTenLop.setSelectedItem(model.getValueAt(mr, 1).toString());
                cbHLV.setSelectedItem(model.getValueAt(mr, 2).toString());
                cbPhong.setSelectedItem(model.getValueAt(mr, 3).toString());
                cbGio.setSelectedItem(model.getValueAt(mr, 4).toString());
                cbThu.setSelectedItem(model.getValueAt(mr, 5).toString());

                String cur = model.getValueAt(mr, 6).toString();
                if (cur.contains("/")) {
                    txtMaxStudent.setText(cur.substring(cur.indexOf("/") + 1));
                }
            }
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Wire Action Listeners
        btnAdd.addActionListener(e -> actionThem());
        btnUpdate.addActionListener(e -> actionCapNhat());
        btnDelete.addActionListener(e -> actionXoa());
        btnClear.addActionListener(e -> actionLamMoi());
        btnBook.addActionListener(e -> actionDangKyHocVien());
    }

    private void actionThem() {
        String code = txtMaLop.getText().trim().toUpperCase();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Lớp Học!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equalsIgnoreCase(code)) {
                JOptionPane.showMessageDialog(this, "Mã lớp '" + code + "' đã tồn tại!", "Trùng mã", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String max = txtMaxStudent.getText().trim();
        if (max.isEmpty()) max = "20";

        model.addRow(new Object[]{
                code,
                cbTenLop.getSelectedItem().toString(),
                cbHLV.getSelectedItem().toString(),
                cbPhong.getSelectedItem().toString(),
                cbGio.getSelectedItem().toString(),
                cbThu.getSelectedItem().toString(),
                "0/" + max
        });

        JOptionPane.showMessageDialog(this, "Thêm lớp học [" + code + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        actionLamMoi();
    }

    private void actionCapNhat() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trong bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int mr = table.convertRowIndexToModel(sel);

        String max = txtMaxStudent.getText().trim();
        if (max.isEmpty()) max = "20";

        String oldSiSo = model.getValueAt(mr, 6).toString();
        String currentJoined = oldSiSo.contains("/") ? oldSiSo.substring(0, oldSiSo.indexOf("/")) : "0";

        model.setValueAt(cbTenLop.getSelectedItem().toString(), mr, 1);
        model.setValueAt(cbHLV.getSelectedItem().toString(), mr, 2);
        model.setValueAt(cbPhong.getSelectedItem().toString(), mr, 3);
        model.setValueAt(cbGio.getSelectedItem().toString(), mr, 4);
        model.setValueAt(cbThu.getSelectedItem().toString(), mr, 5);
        model.setValueAt(currentJoined + "/" + max, mr, 6);

        JOptionPane.showMessageDialog(this, "Cập nhật thông tin lớp học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionXoa() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học cần hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int mr = table.convertRowIndexToModel(sel);
        String code = model.getValueAt(mr, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn hủy và xóa lớp [" + code + "]?", "Xác nhận hủy lớp", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(mr);
            actionLamMoi();
            JOptionPane.showMessageDialog(this, "Đã xóa lớp học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actionLamMoi() {
        table.clearSelection();
        txtMaLop.setText("");
        txtMaLop.setEditable(true);
        cbTenLop.setSelectedIndex(0);
        cbHLV.setSelectedIndex(0);
        cbPhong.setSelectedIndex(0);
        cbGio.setSelectedIndex(0);
        cbThu.setSelectedIndex(0);
        txtMaxStudent.setText("25");
        txtSearch.setText("");
    }

    private void actionDangKyHocVien() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học trong bảng để ghi danh học viên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int mr = table.convertRowIndexToModel(sel);
        String className = model.getValueAt(mr, 1).toString();
        String currentSiSo = model.getValueAt(mr, 6).toString();

        String member = JOptionPane.showInputDialog(this, "Nhập Tên hoặc Mã Hội Viên đăng ký tham gia lớp [" + className + "]:", "Ghi Danh Lớp Học", JOptionPane.QUESTION_MESSAGE);
        if (member != null && !member.trim().isEmpty()) {
            int cur = 0;
            int max = 25;
            if (currentSiSo.contains("/")) {
                String[] parts = currentSiSo.split("/");
                try {
                    cur = Integer.parseInt(parts[0]);
                    max = Integer.parseInt(parts[1]);
                } catch (Exception ignored) {}
            }
            if (cur >= max) {
                JOptionPane.showMessageDialog(this, "Lớp học đã đạt sĩ số tối đa (" + max + " người). Vui lòng chọn lớp khác!", "Lớp Đã Đầy", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cur++;
            model.setValueAt(cur + "/" + max, mr, 6);
            JOptionPane.showMessageDialog(this, "Đã ghi danh thành công hội viên [" + member.trim() + "] vào lớp " + className + "!\nSĩ số hiện tại: " + cur + "/" + max, "Ghi Danh Thành Công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void moDialogDiemDanh() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học trên bảng để mở danh sách điểm danh!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int mr = table.convertRowIndexToModel(sel);
        String maLop = model.getValueAt(mr, 0).toString();
        String tenLop = model.getValueAt(mr, 1).toString();
        String hlv = model.getValueAt(mr, 2).toString();
        String time = model.getValueAt(mr, 4).toString();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Danh Sách Điểm Danh - " + tenLop, true);
        dlg.setSize(750, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(12, 12));
        ((JPanel) dlg.getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel pHeader = new JPanel(new BorderLayout());
        pHeader.setOpaque(false);
        JLabel lblT = new JLabel("ĐIỂM DANH LỚP: " + tenLop.toUpperCase() + " (" + maLop + ")");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblT.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("HLV: " + hlv + " • Khung giờ: " + time + " • Lớp học theo lịch FitLife");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));
        pHeader.add(lblT, BorderLayout.NORTH);
        pHeader.add(lblSub, BorderLayout.SOUTH);
        dlg.add(pHeader, BorderLayout.NORTH);

        // Table
        String[] cols = {"Mã HV", "Họ Và Tên", "Số Điện Thoại", "Trạng Thái Điểm Danh", "Ghi Chú"};
        DefaultTableModel tModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // Dữ liệu học viên mẫu của lớp
        tModel.addRow(new Object[]{"HV01", "Lê Anh Duy", "0901234567", "ĐÃ ĐIỂM DANH", "Đến sớm 10 phút"});
        tModel.addRow(new Object[]{"HV02", "Võ Lê Ngọc Hưng", "0902345678", "ĐÃ ĐIỂM DANH", "Trang phục đúng quy định"});
        tModel.addRow(new Object[]{"HV04", "Trần Minh Quân", "0904567890", "CHƯA ĐIỂM DANH", ""});
        tModel.addRow(new Object[]{"HV05", "Hoàng Thùy Linh", "0905678901", "CHƯA ĐIỂM DANH", ""});
        tModel.addRow(new Object[]{"HV08", "Đặng Gia Huy", "0908901234", "VẮNG MẶT", "Báo nghỉ có phép"});

        JTable tbl = new JTable(tModel);
        tbl.setRowHeight(30);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl.getTableHeader().setBackground(new Color(241, 245, 249));
        styleDataTable(tbl, 1, 4);

        tbl.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, hasFocus, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBackground(isSel ? t.getSelectionBackground() : (r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
                l.setOpaque(true);
                String s = (val != null) ? val.toString() : "";
                if (isSel) {
                    l.setForeground(t.getSelectionForeground());
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if ("ĐÃ ĐIỂM DANH".equalsIgnoreCase(s)) {
                    l.setForeground(new Color(16, 185, 129));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if ("VẮNG MẶT".equalsIgnoreCase(s)) {
                    l.setForeground(new Color(239, 68, 68));
                    l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else {
                    l.setForeground(new Color(100, 116, 139));
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                }
                return l;
            }
        });

        dlg.add(new JScrollPane(tbl), BorderLayout.CENTER);

        // Buttons
        JPanel pBot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pBot.setOpaque(false);

        JButton btnCoMat = new JButton("✔ Có Mặt (Điểm Danh)");
        btnCoMat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCoMat.setBackground(new Color(16, 185, 129));
        btnCoMat.setForeground(Color.WHITE);
        styleActionButton(btnCoMat);
        btnCoMat.addActionListener(e -> {
            int r = tbl.getSelectedRow();
            if (r >= 0) {
                tModel.setValueAt("ĐÃ ĐIỂM DANH", r, 3);
                com.nhom12.gym.util.SoundHelper.playSuccessBeep();
            } else {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn học viên cần điểm danh!");
            }
        });

        JButton btnVang = new JButton("✖ Báo Vắng");
        btnVang.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVang.setBackground(new Color(239, 68, 68));
        btnVang.setForeground(Color.WHITE);
        styleActionButton(btnVang);
        btnVang.addActionListener(e -> {
            int r = tbl.getSelectedRow();
            if (r >= 0) {
                tModel.setValueAt("VẮNG MẶT", r, 3);
            } else {
                JOptionPane.showMessageDialog(dlg, "Vui lòng chọn học viên cần báo vắng!");
            }
        });

        JButton btnXuatEx = new JButton("📊 Xuất File Excel");
        btnXuatEx.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXuatEx.setBackground(new Color(241, 245, 249));
        btnXuatEx.setForeground(new Color(15, 118, 110));
        styleActionButton(btnXuatEx);
        btnXuatEx.addActionListener(e -> com.nhom12.gym.util.ExcelExportHelper.exportTableToExcel(dlg, tbl, "DiemDanh_" + maLop));

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        styleActionButton(btnClose);
        btnClose.addActionListener(e -> dlg.dispose());

        pBot.add(btnCoMat);
        pBot.add(btnVang);
        pBot.add(btnXuatEx);
        pBot.add(btnClose);
        dlg.add(pBot, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void styleDataTable(JTable dataTable, int... leftAlignedColumns) {
        dataTable.getTableHeader().setForeground(new Color(30, 41, 59));
        dataTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        dataTable.setShowVerticalLines(false);
        dataTable.setGridColor(new Color(226, 232, 240));
        dataTable.setSelectionBackground(new Color(219, 234, 254));
        dataTable.setSelectionForeground(new Color(15, 23, 42));
        dataTable.setFillsViewportHeight(true);
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable source, Object value, boolean selected, boolean focus, int row, int column) {
                Component component = super.getTableCellRendererComponent(source, value, selected, focus, row, column);
                if (!selected) component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                boolean leftAligned = false;
                for (int leftColumn : leftAlignedColumns) {
                    if (column == leftColumn) leftAligned = true;
                }
                setHorizontalAlignment(leftAligned ? SwingConstants.LEFT : SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return component;
            }
        });
    }

    private void styleInput(JComponent component) {
        component.putClientProperty("JComponent.roundRect", true);
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, 36));
    }

    private void styleActionButton(JButton button) {
        Color normalColor = button.getBackground();
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) button.setBackground(normalColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }
}
