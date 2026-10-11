package com.nhom12.gym.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class GhiChuTienDoPTPanel extends JPanel {
    private JTable tableHV;
    private DefaultTableModel modelHV;
    private JLabel lblTenHV;
    private JTextField txtNhomCo;
    private JTextField txtPR;
    private JTextArea txtNhanXet;
    private JButton btnSave;
    private JTable tableLog;
    private DefaultTableModel modelLog;

    public GhiChuTienDoPTPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("NHẬT KÝ & TIẾN ĐỘ TẬP LUYỆN (HLV KÈM 1-1)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Theo dõi số buổi còn lại, ghi chép mức tạ kỷ lục (PR) và nhật ký thể trạng học viên");
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

        // Left Panel (HV List)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblLeftTitle = new JLabel("Học Viên Đang Kèm (PT)");
        lblLeftTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLeftTitle.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblLeftTitle, BorderLayout.NORTH);

        String[] colsHV = new String[]{"Mã HV", "Tên Học Viên", "Buổi Còn"};
        Object[][] dataHV = new Object[][]{
                {"HV012", "Nguyễn Văn Tuấn", "15/20"},
                {"HV045", "Trần Thu Hà", "8/12"},
                {"HV088", "Lê Hoàng Phúc", "2/10"},
                {"HV099", "Phạm Trọng Thủy", "18/20"}
        };
        modelHV = new DefaultTableModel(dataHV, colsHV) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableHV = new JTable(modelHV);
        tableHV.setRowHeight(38);
        tableHV.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableHV.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHV.getTableHeader().setBackground(new Color(241, 245, 249));
        tableHV.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(tableHV), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // Right Panel (Log form + Log history)
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setOpaque(false);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lHV = new JLabel("Học Viên Đang Chọn:"); lHV.setFont(fontLabel);
        formPanel.add(lHV, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        lblTenHV = new JLabel("Nguyễn Văn Tuấn (Buổi 6/20)");
        lblTenHV.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTenHV.setForeground(new Color(37, 99, 235));
        formPanel.add(lblTenHV, gbc);

        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lMuscle = new JLabel("Nhóm Cơ Mục Tiêu:"); lMuscle.setFont(fontLabel);
        formPanel.add(lMuscle, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        txtNhomCo = new JTextField("Ngực - Vai - Tay sau (Push Day)");
        txtNhomCo.setFont(fontInput);
        formPanel.add(txtNhomCo, gbc);

        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lPR = new JLabel("Mức Tạ Kỷ Lục (PR):"); lPR.setFont(fontLabel);
        formPanel.add(lPR, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        txtPR = new JTextField("Bench Press 65kg (Tăng 5kg so với tuần trước)");
        txtPR.setFont(fontInput);
        formPanel.add(txtPR, gbc);

        r++; gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.25;
        JLabel lComment = new JLabel("Nhận Xét / Thể Trạng:"); lComment.setFont(fontLabel);
        formPanel.add(lComment, gbc);

        gbc.gridx = 1; gbc.weightx = 0.75;
        txtNhanXet = new JTextArea(2, 20);
        txtNhanXet.setFont(fontInput);
        txtNhanXet.setLineWrap(true);
        txtNhanXet.setWrapStyleWord(true);
        txtNhanXet.setText("Thể lực tốt, nhịp thở đều, form tạ chuẩn an toàn.");
        formPanel.add(new JScrollPane(txtNhanXet), gbc);

        r++; gbc.gridx = 1; gbc.gridy = r;
        btnSave = new JButton("Lưu Nhật Ký Buổi Tập Hôm Nay");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(16, 185, 129));
        btnSave.setForeground(Color.WHITE);
        formPanel.add(btnSave, gbc);

        rightPanel.add(formPanel, BorderLayout.NORTH);

        // History Table
        JPanel historyPanel = new JPanel(new BorderLayout(0, 10));
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblHistTitle = new JLabel("Lịch Sử Các Buổi Tập Trước Của Học Viên");
        lblHistTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHistTitle.setForeground(new Color(30, 41, 59));
        historyPanel.add(lblHistTitle, BorderLayout.NORTH);

        String[] colsLog = new String[]{"Ngày Tập", "Nhóm Cơ", "Mức Tạ PR", "Nhận Xét Của HLV", "HLV Ghi"};
        Object[][] dataLog = new Object[][]{
                {"28/09/2026", "Lưng - Xô", "Pull-up 8 reps", "Form kéo xô cải thiện rõ rệt", "John Smith"},
                {"26/09/2026", "Chân - Mông", "Squat 70kg", "Cơ đùi khỏe, cần giãn cơ sau buổi", "John Smith"},
                {"24/09/2026", "Ngực - Tay sau", "Bench Press 60kg", "Buổi tập hoàn thành đúng giáo án", "John Smith"}
        };
        modelLog = new DefaultTableModel(dataLog, colsLog) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableLog = new JTable(modelLog);
        tableLog.setRowHeight(38);
        tableLog.setFont(fontInput);
        tableLog.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableLog.getTableHeader().setBackground(new Color(241, 245, 249));
        historyPanel.add(new JScrollPane(tableLog), BorderLayout.CENTER);

        rightPanel.add(historyPanel, BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Listeners
        tableHV.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableHV.getSelectedRow() != -1) {
                int rSel = tableHV.getSelectedRow();
                String name = modelHV.getValueAt(rSel, 1).toString();
                String remaining = modelHV.getValueAt(rSel, 2).toString();
                lblTenHV.setText(name + " (Còn " + remaining + " buổi)");
            }
        });

        btnSave.addActionListener(e -> {
            String muscle = txtNhomCo.getText().trim();
            String pr = txtPR.getText().trim();
            String note = txtNhanXet.getText().trim();

            if (muscle.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập nhóm cơ mục tiêu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            modelLog.insertRow(0, new Object[]{dateStr, muscle, pr, note, "HLV Phụ Trách"});

            // Deduct session if possible
            int sel = tableHV.getSelectedRow();
            if (sel != -1) {
                String curRem = modelHV.getValueAt(sel, 2).toString();
                if (curRem.contains("/")) {
                    String[] parts = curRem.split("/");
                    try {
                        int rem = Integer.parseInt(parts[0]);
                        int total = Integer.parseInt(parts[1]);
                        if (rem > 0) rem--;
                        modelHV.setValueAt(rem + "/" + total, sel, 2);
                        lblTenHV.setText(modelHV.getValueAt(sel, 1).toString() + " (Còn " + rem + "/" + total + " buổi)");
                    } catch (Exception ignored) {}
                }
            }

            JOptionPane.showMessageDialog(this, "Đã lưu nhật ký buổi tập thành công và trừ 01 buổi tập của học viên!", "Ghi Nhận Thành Công", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
