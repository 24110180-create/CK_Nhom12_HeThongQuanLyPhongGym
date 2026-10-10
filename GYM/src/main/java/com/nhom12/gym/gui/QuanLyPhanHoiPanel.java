package com.nhom12.gym.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class QuanLyPhanHoiPanel extends JPanel {
    private JTextField txtMaPH;
    private JTextField txtHoiVien;
    private JComboBox<String> cbRating;
    private JTextArea txtNoiDung;
    private JTextArea txtPhanHoiAdmin;
    private JComboBox<String> cbTrangThai;

    private JButton btnReply;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;

    public QuanLyPhanHoiPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(245, 247, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("QUẢN LÝ PHẢN HỒI & ĐÁNH GIÁ CHẤT LƯỢNG (CSKH)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        JLabel lblSubtitle = new JLabel("Lắng nghe ý kiến đóng góp, giải quyết khiếu nại và nâng cao trải nghiệm hội viên phòng tập");
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

        JLabel lblFormTitle = new JLabel("Chi Tiết Khiếu Nại / Góp Ý");
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
        JLabel lCode = new JLabel("Mã Phản Hồi:"); lCode.setFont(fontLabel);
        txtMaPH = new JTextField("PH01"); txtMaPH.setFont(fontInput);
        pCode.add(lCode, BorderLayout.NORTH); pCode.add(txtMaPH, BorderLayout.CENTER);

        JPanel pMember = new JPanel(new BorderLayout(0, 3)); pMember.setOpaque(false);
        JLabel lMember = new JLabel("Hội Viên Gửi:"); lMember.setFont(fontLabel);
        txtHoiVien = new JTextField("Nguyễn Văn Tuấn"); txtHoiVien.setFont(fontInput);
        pMember.add(lMember, BorderLayout.NORTH); pMember.add(txtHoiVien, BorderLayout.CENTER);

        rowCodeMember.add(pCode); rowCodeMember.add(pMember);
        gbc.gridx = 0; gbc.gridy = r++;
        formGrid.add(rowCodeMember, gbc);

        // Rating & Status
        JPanel rowRateStatus = new JPanel(new GridLayout(1, 2, 8, 0));
        rowRateStatus.setOpaque(false);
        JPanel pRate = new JPanel(new BorderLayout(0, 3)); pRate.setOpaque(false);
        JLabel lRate = new JLabel("Mức Độ Đánh Giá:"); lRate.setFont(fontLabel);
        cbRating = new JComboBox<>(new String[]{"5 Sao - Rất hài lòng", "4 Sao - Hài lòng", "3 Sao - Bình thường", "2 Sao - Chưa tốt", "1 Sao - Rất tệ"});
        cbRating.setFont(fontInput);
        pRate.add(lRate, BorderLayout.NORTH); pRate.add(cbRating, BorderLayout.CENTER);

        JPanel pStatus = new JPanel(new BorderLayout(0, 3)); pStatus.setOpaque(false);
        JLabel lStatus = new JLabel("Trạng Thái Xử Lý:"); lStatus.setFont(fontLabel);
        cbTrangThai = new JComboBox<>(new String[]{"Chờ xử lý", "Đang giải quyết", "Đã phản hồi khách"});
        cbTrangThai.setFont(fontInput);
        pStatus.add(lStatus, BorderLayout.NORTH); pStatus.add(cbTrangThai, BorderLayout.CENTER);

        rowRateStatus.add(pRate); rowRateStatus.add(pStatus);
        gbc.gridy = r++;
        formGrid.add(rowRateStatus, gbc);

        // Member content
        gbc.gridy = r++;
        JLabel lContent = new JLabel("Nội Dung Góp Ý Của Hội Viên:"); lContent.setFont(fontLabel);
        formGrid.add(lContent, gbc);
        gbc.gridy = r++;
        txtNoiDung = new JTextArea(3, 20);
        txtNoiDung.setFont(fontInput);
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        formGrid.add(new JScrollPane(txtNoiDung), gbc);

        // Admin reply
        gbc.gridy = r++;
        JLabel lAdmin = new JLabel("Câu Trả Lời Của Phòng Gym (Gửi SMS/App):"); lAdmin.setFont(fontLabel);
        formGrid.add(lAdmin, gbc);
        gbc.gridy = r++;
        txtPhanHoiAdmin = new JTextArea(3, 20);
        txtPhanHoiAdmin.setFont(fontInput);
        txtPhanHoiAdmin.setLineWrap(true);
        txtPhanHoiAdmin.setWrapStyleWord(true);
        txtPhanHoiAdmin.setText("Cảm ơn anh/chị đã đóng góp ý kiến. Ban quản lý đã tiếp nhận và sẽ khắc phục ngay trong 24h tới!");
        formGrid.add(new JScrollPane(txtPhanHoiAdmin), gbc);

        gbc.gridy = r++;
        gbc.weighty = 1.0;
        formGrid.add(Box.createVerticalGlue(), gbc);

        leftPanel.add(formGrid, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        btnReply = new JButton("Gửi Phản Hồi Cho Khách");
        btnReply.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReply.setBackground(new Color(16, 185, 129));
        btnReply.setForeground(Color.WHITE);

        btnUpdate = new JButton("Cập Nhật Trạng Thái");
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

        btnPanel.add(btnReply);
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

        // Search Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("Lọc phản hồi:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = new JTextField(18);
        txtSearch.setFont(fontInput);
        searchBar.add(lblSearch);
        searchBar.add(txtSearch);
        rightPanel.add(searchBar, BorderLayout.NORTH);

        String[] cols = new String[]{"Mã PH", "Hội Viên", "Đánh Giá", "Nội Dung Góp Ý", "Thời Gian", "Trạng Thái"};
        Object[][] data = new Object[][]{
                {"PH01", "Nguyễn Văn Tuấn", "5 Sao", "Phòng tập sạch sẽ, tạ đầy đủ, HLV rất nhiệt tình", "30/09/2026 10:15", "Đã phản hồi khách"},
                {"PH02", "Trần Thu Hà", "4 Sao", "Điều hòa phòng Yoga đôi khi hơi lạnh, mong quầy chỉnh lại", "29/09/2026 18:30", "Đã phản hồi khách"},
                {"PH03", "Lê Hoàng Phúc", "2 Sao", "Máy chạy bộ số 03 kêu cọt kẹt cần tra dầu gấp", "28/09/2026 15:45", "Đang giải quyết"},
                {"PH04", "Phạm Trọng Thủy", "5 Sao", "Gói VIP rất đáng tiền, tủ đồ thông minh tiện lợi", "27/09/2026 09:20", "Đã phản hồi khách"},
                {"PH05", "Võ Minh Đức", "3 Sao", "Giờ cao điểm chiều tối khá đông, cần thêm ghế bench", "26/09/2026 19:10", "Chờ xử lý"}
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
                txtMaPH.setText(model.getValueAt(mr, 0).toString());
                txtMaPH.setEditable(false);
                txtHoiVien.setText(model.getValueAt(mr, 1).toString());

                String rateStr = model.getValueAt(mr, 2).toString();
                if (rateStr.contains("5")) cbRating.setSelectedIndex(0);
                else if (rateStr.contains("4")) cbRating.setSelectedIndex(1);
                else if (rateStr.contains("3")) cbRating.setSelectedIndex(2);
                else if (rateStr.contains("2")) cbRating.setSelectedIndex(3);
                else cbRating.setSelectedIndex(4);

                txtNoiDung.setText(model.getValueAt(mr, 3).toString());
                cbTrangThai.setSelectedItem(model.getValueAt(mr, 5).toString());
            }
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // Wire Action Listeners
        btnReply.addActionListener(e -> actionTraLoi());
        btnUpdate.addActionListener(e -> actionCapNhat());
        btnDelete.addActionListener(e -> actionXoa());
        btnClear.addActionListener(e -> actionLamMoi());
    }

    private void actionTraLoi() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một ý kiến đóng góp trong bảng để gửi phản hồi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int mr = table.convertRowIndexToModel(sel);
        String member = model.getValueAt(mr, 1).toString();
        String replyText = txtPhanHoiAdmin.getText().trim();

        if (replyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung phản hồi của phòng gym!", "Thiếu nội dung", JOptionPane.WARNING_MESSAGE);
            return;
        }

        model.setValueAt("Đã phản hồi khách", mr, 5);
        cbTrangThai.setSelectedItem("Đã phản hồi khách");

        JOptionPane.showMessageDialog(this, "Đã gửi tin nhắn phản hồi thành công đến hội viên [" + member + "]!\nNội dung: " + replyText, "Gửi Phản Hồi Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionCapNhat() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ý kiến đóng góp để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int mr = table.convertRowIndexToModel(sel);

        model.setValueAt(txtHoiVien.getText().trim(), mr, 1);
        model.setValueAt(cbRating.getSelectedItem().toString().split(" - ")[0], mr, 2);
        model.setValueAt(txtNoiDung.getText().trim(), mr, 3);
        model.setValueAt(cbTrangThai.getSelectedItem().toString(), mr, 5);

        JOptionPane.showMessageDialog(this, "Cập nhật trạng thái phản hồi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionXoa() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phản hồi cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int mr = table.convertRowIndexToModel(sel);
        String code = model.getValueAt(mr, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa bản ghi phản hồi [" + code + "]?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(mr);
            actionLamMoi();
            JOptionPane.showMessageDialog(this, "Đã xóa phản hồi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actionLamMoi() {
        table.clearSelection();
        txtMaPH.setText("PH0" + (model.getRowCount() + 1));
        txtMaPH.setEditable(true);
        txtHoiVien.setText("");
        cbRating.setSelectedIndex(0);
        txtNoiDung.setText("");
        txtPhanHoiAdmin.setText("Cảm ơn anh/chị đã đóng góp ý kiến. Ban quản lý đã tiếp nhận và sẽ khắc phục ngay!");
        cbTrangThai.setSelectedIndex(0);
        txtSearch.setText("");
    }
}
