package com.nhom12.gym.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class TheoDoiChiSoPanel extends JPanel {
    private final DecimalFormat df = new DecimalFormat("#0.0");

    private JTextField txtMaDo;
    private JComboBox<String> cbHoiVien;
    private JTextField txtChieuCao;
    private JTextField txtCanNang;
    private JTextField txtCo;
    private JTextField txtMo;
    private JLabel lblBMI;
    private JLabel lblDanhGia;
    private JTextField txtGhiChu;

    private JButton btnCalc;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnExport;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;

    public TheoDoiChiSoPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("THEO DÕI CHỈ SỐ CƠ THỂ & INBODY (BODY COMPOSITION)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Đo đạc cân nặng, phân tích khối lượng cơ, tỷ lệ mỡ và thiết lập lộ trình luyện tập cho hội viên");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(420);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left Form
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFormTitle = new JLabel("Phiếu Đo InBody");
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
        // Code & Member
        JPanel rowCodeMember = new JPanel(new GridLayout(1, 2, 8, 0));
        rowCodeMember.setOpaque(false);
        JPanel pCode = new JPanel(new BorderLayout(0, 3)); pCode.setOpaque(false);
        JLabel lCode = new JLabel("Mã Đo:"); lCode.setFont(fontLabel);
        txtMaDo = new JTextField("IB01"); txtMaDo.setFont(fontInput);
        pCode.add(lCode, BorderLayout.NORTH); pCode.add(txtMaDo, BorderLayout.CENTER);

        JPanel pMember = new JPanel(new BorderLayout(0, 3)); pMember.setOpaque(false);
        JLabel lMember = new JLabel("Hội Viên:"); lMember.setFont(fontLabel);
        cbHoiVien = new JComboBox<>();
        loadMembersToCombo();
        cbHoiVien.setFont(fontInput);
        pMember.add(lMember, BorderLayout.NORTH); pMember.add(cbHoiVien, BorderLayout.CENTER);

        rowCodeMember.add(pCode); rowCodeMember.add(pMember);
        gbc.gridx = 0; gbc.gridy = r++;
        formGrid.add(rowCodeMember, gbc);

        // Height & Weight
        JPanel rowHW = new JPanel(new GridLayout(1, 2, 8, 0));
        rowHW.setOpaque(false);
        JPanel pH = new JPanel(new BorderLayout(0, 3)); pH.setOpaque(false);
        JLabel lH = new JLabel("Chiều Cao (cm):"); lH.setFont(fontLabel);
        txtChieuCao = new JTextField("175"); txtChieuCao.setFont(fontInput);
        pH.add(lH, BorderLayout.NORTH); pH.add(txtChieuCao, BorderLayout.CENTER);

        JPanel pW = new JPanel(new BorderLayout(0, 3)); pW.setOpaque(false);
        JLabel lW = new JLabel("Cân Nặng (kg):"); lW.setFont(fontLabel);
        txtCanNang = new JTextField("72.5"); txtCanNang.setFont(fontInput);
        pW.add(lW, BorderLayout.NORTH); pW.add(txtCanNang, BorderLayout.CENTER);

        rowHW.add(pH); rowHW.add(pW);
        gbc.gridy = r++;
        formGrid.add(rowHW, gbc);

        // Muscle & Fat
        JPanel rowMF = new JPanel(new GridLayout(1, 2, 8, 0));
        rowMF.setOpaque(false);
        JPanel pM = new JPanel(new BorderLayout(0, 3)); pM.setOpaque(false);
        JLabel lM = new JLabel("Khối Lượng Cơ (kg):"); lM.setFont(fontLabel);
        txtCo = new JTextField("34.2"); txtCo.setFont(fontInput);
        pM.add(lM, BorderLayout.NORTH); pM.add(txtCo, BorderLayout.CENTER);

        JPanel pF = new JPanel(new BorderLayout(0, 3)); pF.setOpaque(false);
        JLabel lF = new JLabel("Tỷ Lệ Mỡ (%):"); lF.setFont(fontLabel);
        txtMo = new JTextField("18.5"); txtMo.setFont(fontInput);
        pF.add(lF, BorderLayout.NORTH); pF.add(txtMo, BorderLayout.CENTER);

        rowMF.add(pM); rowMF.add(pF);
        gbc.gridy = r++;
        formGrid.add(rowMF, gbc);

        // Button Calculate BMI
        btnCalc = new JButton("Tự Động Tính BMI & Đánh Giá Thể Trạng");
        btnCalc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCalc.setBackground(new Color(245, 158, 11));
        btnCalc.setForeground(Color.WHITE);
        gbc.gridy = r++;
        gbc.insets = new Insets(8, 4, 8, 4);
        formGrid.add(btnCalc, gbc);

        // Result Box
        JPanel resultBox = new JPanel(new GridLayout(2, 1, 0, 4));
        resultBox.setBackground(new Color(241, 245, 249));
        resultBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        lblBMI = new JLabel("Chỉ số BMI: 23.7 (Lý tưởng)");
        lblBMI.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBMI.setForeground(new Color(30, 41, 59));

        lblDanhGia = new JLabel("Thể trạng: Cân đối, tỷ lệ cơ nạc tốt");
        lblDanhGia.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDanhGia.setForeground(new Color(16, 185, 129));

        resultBox.add(lblBMI);
        resultBox.add(lblDanhGia);
        gbc.gridy = r++;
        formGrid.add(resultBox, gbc);

        // Notes
        gbc.gridy = r++;
        JLabel lNote = new JLabel("Lời Khuyên Của PT:"); lNote.setFont(fontLabel);
        formGrid.add(lNote, gbc);
        gbc.gridy = r++;
        txtGhiChu = new JTextField("Duy trì lịch tập 4 buổi/tuần, tăng thêm tạ Compound");
        txtGhiChu.setFont(fontInput);
        formGrid.add(txtGhiChu, gbc);

        gbc.gridy = r++;
        gbc.weighty = 1.0;
        formGrid.add(Box.createVerticalGlue(), gbc);

        leftPanel.add(formGrid, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = new JButton("Lưu Chỉ Số Mới");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(new Color(16, 185, 129));
        btnAdd.setForeground(Color.WHITE);

        btnUpdate = new JButton("Cập Nhật");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdate.setBackground(new Color(37, 99, 235));
        btnUpdate.setForeground(Color.WHITE);

        btnDelete = new JButton("Xóa Bản Ghi");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);

        btnClear = new JButton("Làm Mới Form");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setBackground(new Color(100, 116, 139));
        btnClear.setForeground(Color.WHITE);

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

        // Search Bar & Export on Right Top
        JPanel rightTop = new JPanel(new BorderLayout());
        rightTop.setOpaque(false);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("Lọc bản đo:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = new JTextField(15);
        txtSearch.setFont(fontInput);
        pSearch.add(lblSearch);
        pSearch.add(txtSearch);
        rightTop.add(pSearch, BorderLayout.WEST);

        btnExport = new JButton("In Phiếu Kết Quả InBody");
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExport.setBackground(new Color(37, 99, 235));
        btnExport.setForeground(Color.WHITE);
        rightTop.add(btnExport, BorderLayout.EAST);
        rightPanel.add(rightTop, BorderLayout.NORTH);

        String[] cols = new String[]{"Mã Đo", "Hội Viên", "Ngày Đo", "C.Cao (cm)", "C.Nặng (kg)", "Cơ (kg)", "Mỡ (%)", "BMI", "Đánh Giá"};
        Object[][] data = new Object[][]{
                {"IB01", "Lê Anh Duy", "30/09/2026", "175", "72.5", "34.2", "18.5%", "23.7", "Cân đối"},
                {"IB02", "Võ Lê Ngọc Hưng", "28/09/2026", "172", "68.0", "32.5", "17.0%", "23.0", "Chuẩn mẫu"},
                {"IB03", "Trần Minh Quân", "25/09/2026", "180", "82.0", "38.0", "19.5%", "25.3", "Vận động viên"},
                {"IB04", "Võ Tấn Phát", "20/09/2026", "168", "65.0", "29.0", "20.0%", "23.0", "Cân đối"}
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Search filter
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        // Table Selection -> Populate form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int mr = table.convertRowIndexToModel(table.getSelectedRow());
                txtMaDo.setText(model.getValueAt(mr, 0).toString());
                txtMaDo.setEditable(false);

                String mem = model.getValueAt(mr, 1).toString();
                for (int i = 0; i < cbHoiVien.getItemCount(); i++) {
                    if (cbHoiVien.getItemAt(i).contains(mem)) {
                        cbHoiVien.setSelectedIndex(i);
                        break;
                    }
                }

                txtChieuCao.setText(model.getValueAt(mr, 3).toString());
                txtCanNang.setText(model.getValueAt(mr, 4).toString());
                txtCo.setText(model.getValueAt(mr, 5).toString());
                txtMo.setText(model.getValueAt(mr, 6).toString().replace("%", ""));
                calculateBMI();
            }
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Action Listeners
        btnCalc.addActionListener(e -> calculateBMI());
        btnAdd.addActionListener(e -> actionThem());
        btnUpdate.addActionListener(e -> actionCapNhat());
        btnDelete.addActionListener(e -> actionXoa());
        btnClear.addActionListener(e -> actionLamMoi());
        btnExport.addActionListener(e -> actionInPhieu());
        
        com.nhom12.gym.service.DataSyncManager.addListener(type -> {
            if (type == com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN || type == com.nhom12.gym.service.DataSyncManager.DataType.ALL) {
                loadMembersToCombo();
            }
        });
    }

    private void loadMembersToCombo() {
        if (cbHoiVien == null) return;
        Object cur = cbHoiVien.getSelectedItem();
        cbHoiVien.removeAllItems();
        java.util.List<com.nhom12.gym.entity.HoiVien> list = new com.nhom12.gym.dao.HoiVienDAO().layTatCa();
        if (list != null) {
            for (com.nhom12.gym.entity.HoiVien hv : list) {
                cbHoiVien.addItem(hv.getHoTen() + " (" + (hv.getMaTheQR() != null ? hv.getMaTheQR() : "HV" + hv.getMaHoiVien()) + ")");
            }
        }
        if (cur != null) cbHoiVien.setSelectedItem(cur);
    }

    private boolean calculateBMI() {
        try {
            if (!com.nhom12.gym.util.ValidationHelper.checkSoDuong(this, txtChieuCao, "Chiều cao (cm)")) return false;
            if (!com.nhom12.gym.util.ValidationHelper.checkSoDuong(this, txtCanNang, "Cân nặng (kg)")) return false;

            double h = Double.parseDouble(txtChieuCao.getText().trim()) / 100.0;
            double w = Double.parseDouble(txtCanNang.getText().trim());
            double bmi = w / (h * h);

            String status;
            Color col;
            if (bmi < 18.5) {
                status = "Thiếu cân (Cần tăng cơ nạc)";
                col = new Color(245, 158, 11);
            } else if (bmi <= 24.9) {
                status = "Cân đối, tỷ lệ cơ thể lý tưởng";
                col = new Color(16, 185, 129);
            } else if (bmi <= 29.9) {
                status = "Thừa cân (Nên tập Cardio đốt mỡ)";
                col = new Color(239, 68, 68);
            } else {
                status = "Béo phì độ 1 (Cần kiểm soát calo)";
                col = new Color(220, 38, 38);
            }

            lblBMI.setText("Chỉ số BMI: " + df.format(bmi));
            lblDanhGia.setText("Thể trạng: " + status);
            lblDanhGia.setForeground(col);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Chiều cao (cm) và Cân nặng (kg) hợp lệ!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private void actionThem() {
        if (!com.nhom12.gym.util.ValidationHelper.checkRong(this, txtMaDo, "Mã Đo InBody")) {
            return;
        }
        if (cbHoiVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hội viên cần đo InBody!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!com.nhom12.gym.util.ValidationHelper.checkSoDuong(this, txtCo, "Khối lượng cơ (kg)")) {
            return;
        }
        if (!com.nhom12.gym.util.ValidationHelper.checkSoDuong(this, txtMo, "Tỷ lệ mỡ (%)")) {
            return;
        }
        if (!calculateBMI()) {
            return;
        }

        String code = txtMaDo.getText().trim().toUpperCase();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equalsIgnoreCase(code)) {
                JOptionPane.showMessageDialog(this, "Mã đo '" + code + "' đã tồn tại!", "Trùng mã", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String member = cbHoiVien.getSelectedItem().toString().split(" \\(")[0];
        String bmiStr = lblBMI.getText().replace("Chỉ số BMI: ", "");
        String statusStr = lblDanhGia.getText().replace("Thể trạng: ", "").split(" \\(")[0];

        model.addRow(new Object[]{
                code,
                member,
                dateStr,
                txtChieuCao.getText().trim(),
                txtCanNang.getText().trim(),
                txtCo.getText().trim(),
                txtMo.getText().trim() + "%",
                bmiStr,
                statusStr
        });

        JOptionPane.showMessageDialog(this, "Lưu kết quả đo InBody cho [" + member + "] thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        actionLamMoi();
    }

    private void actionCapNhat() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bản đo trong bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cbHoiVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hội viên!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!com.nhom12.gym.util.ValidationHelper.checkSoDuong(this, txtCo, "Khối lượng cơ (kg)")) {
            return;
        }
        if (!com.nhom12.gym.util.ValidationHelper.checkSoDuong(this, txtMo, "Tỷ lệ mỡ (%)")) {
            return;
        }
        if (!calculateBMI()) {
            return;
        }

        int mr = table.convertRowIndexToModel(sel);
        String member = cbHoiVien.getSelectedItem().toString().split(" \\(")[0];
        String bmiStr = lblBMI.getText().replace("Chỉ số BMI: ", "");
        String statusStr = lblDanhGia.getText().replace("Thể trạng: ", "").split(" \\(")[0];

        model.setValueAt(member, mr, 1);
        model.setValueAt(txtChieuCao.getText().trim(), mr, 3);
        model.setValueAt(txtCanNang.getText().trim(), mr, 4);
        model.setValueAt(txtCo.getText().trim(), mr, 5);
        model.setValueAt(txtMo.getText().trim() + "%", mr, 6);
        model.setValueAt(bmiStr, mr, 7);
        model.setValueAt(statusStr, mr, 8);

        JOptionPane.showMessageDialog(this, "Cập nhật bản ghi đo InBody thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionXoa() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bản ghi đo cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int mr = table.convertRowIndexToModel(sel);
        String code = model.getValueAt(mr, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa bản đo [" + code + "]?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(mr);
            actionLamMoi();
            JOptionPane.showMessageDialog(this, "Đã xóa bản đo thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actionLamMoi() {
        table.clearSelection();
        txtMaDo.setText("IB0" + (model.getRowCount() + 1));
        txtMaDo.setEditable(true);
        cbHoiVien.setSelectedIndex(0);
        txtChieuCao.setText("175");
        txtCanNang.setText("72.5");
        txtCo.setText("34.2");
        txtMo.setText("18.5");
        txtGhiChu.setText("Duy trì tập 4 buổi/tuần, tăng thêm tạ Compound");
        calculateBMI();
        txtSearch.setText("");
    }

    private void actionInPhieu() {
        int sel = table.getSelectedRow();
        String member = sel != -1 ? model.getValueAt(table.convertRowIndexToModel(sel), 1).toString() : cbHoiVien.getSelectedItem().toString().split(" \\(")[0];

        String report = "==========================================\n"
                + "       PHIẾU KẾT QUẢ PHÂN TÍCH INBODY\n"
                + "==========================================\n"
                + "Hội Viên: " + member + "\n"
                + "Ngày Đo: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "\n"
                + "Chiều Cao: " + txtChieuCao.getText() + " cm | Cân Nặng: " + txtCanNang.getText() + " kg\n"
                + "Khối Lượng Cơ Xương (SMM): " + txtCo.getText() + " kg\n"
                + "Tỷ Lệ Mỡ Cơ Thể (PBF): " + txtMo.getText() + "%\n"
                + lblBMI.getText() + "\n"
                + lblDanhGia.getText() + "\n"
                + "Lời Khuyên HLV: " + txtGhiChu.getText() + "\n"
                + "==========================================\n"
                + "Đã xuất phiếu phân tích InBody và cập nhật vào hồ sơ theo dõi hội viên thành công!";
        JOptionPane.showMessageDialog(this, report, "Xuất Phiếu InBody", JOptionPane.INFORMATION_MESSAGE);
    }
}
