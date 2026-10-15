package com.nhom12.gym.gui.panels;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.DataSyncManager;
import com.nhom12.gym.service.SampleDataService;
import com.nhom12.gym.util.SoundHelper;
import com.nhom12.gym.util.TextSanitizer;
import com.nhom12.gym.util.ValidationHelper;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Phân Hệ Theo Dõi InBody, Chỉ Số Cơ Thể & Thể Lực Dành Cho Huấn Luyện Viên (HLV)
 * Hỗ trợ tra cứu nhanh hội viên, tự động hiển thị đầy đủ thông tin hồ sơ ra các ô,
 * cho phép chỉnh sửa chỉ số đo đạc, tự động tính BMI và cập nhật tức thì.
 */
public class TheoDoiChiSoPanel extends JPanel {

    private final DecimalFormat df = new DecimalFormat("#0.0");
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();

    // Dữ liệu bộ nhớ hội viên
    private List<HoiVien> danhSachHoiVien = new ArrayList<>();
    private boolean isSyncingFromTable = false;
    private boolean isSyncingMember = false;

    // Component Tìm Kiếm & Chọn Hội Viên
    private JTextField txtSearchMember;
    private JComboBox<String> cbHoiVien;

    // Các ô hiển thị thông tin hội viên đầy đủ chi tiết
    private JTextField txtHvMa;
    private JTextField txtHvTen;
    private JTextField txtHvPhone;
    private JTextField txtHvGoiTap;
    private JTextField txtHvChiNhanh;
    private JTextField txtHvTrangThai;

    // Các ô nhập và chỉnh sửa chỉ số InBody
    private JTextField txtMaDo;
    private JTextField txtNgayDo;
    private JTextField txtChieuCao;
    private JTextField txtCanNang;
    private JTextField txtCo;
    private JTextField txtMo;
    private JLabel lblBMI;
    private JLabel lblDanhGia;
    private JTextField txtGhiChu;

    // Các nút thao tác
    private JButton btnCalc;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnExport;

    // Bảng danh sách bản đo bên phải
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearchTable;

    public TheoDoiChiSoPanel() {
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(new EmptyBorder(16, 20, 16, 20));
        this.setBackground(new Color(245, 247, 250));

        khoiTaoGiaoDien();
        napDanhSachHoiVienVaoCombo();

        // Tự động chọn dòng đầu tiên trong bảng nếu có dữ liệu
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        // Đăng ký đồng bộ dữ liệu
        DataSyncManager.addListener(type -> {
            if (type == DataSyncManager.DataType.HOI_VIEN || type == DataSyncManager.DataType.ALL) {
                napDanhSachHoiVienVaoCombo();
            }
        });
    }

    private void khoiTaoGiaoDien() {
        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("THEO DÕI CHỈ SỐ CƠ THỂ & INBODY (BODY COMPOSITION)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSubtitle = new JLabel("Tra cứu hồ sơ hội viên, đo đạc phân tích cơ - mỡ - BMI và cập nhật lộ trình luyện tập trực tiếp");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        // 2. Bố cục Master - Detail dạng SplitPane ngang
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(470);
        splitPane.setDividerSize(6);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // =====================================================================
        // PANEL TRÁI: FORM THÔNG TIN HỘI VIÊN & CHỈ SỐ INBODY
        // =====================================================================
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Tiêu đề form
        JLabel lblFormTitle = new JLabel("Phiếu Đo InBody & Hồ Sơ Thể Lực");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(30, 41, 59));
        leftPanel.add(lblFormTitle, BorderLayout.NORTH);

        // Form Container với GridBagLayout
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 3, 4, 3);
        gbc.weightx = 1.0;

        Font fontSection = new Font("Segoe UI", Font.BOLD, 13);
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 13);

        int r = 0;

        // --- PHẦN 1: TÌM KIẾM & CHỌN HỘI VIÊN ---
        gbc.gridx = 0;
        gbc.gridy = r++;
        JLabel lblSecSearch = new JLabel("1. TÌM KIẾM & CHỌN HỘI VIÊN");
        lblSecSearch.setFont(fontSection);
        lblSecSearch.setForeground(new Color(37, 99, 235));
        formGrid.add(lblSecSearch, gbc);

        // Ô tìm kiếm nhanh hội viên
        gbc.gridy = r++;
        JPanel pSearchRow = new JPanel(new BorderLayout(6, 0));
        pSearchRow.setOpaque(false);
        JLabel lSearchHV = new JLabel("Tìm nhanh (Tên/SĐT/Mã):");
        lSearchHV.setFont(fontLabel);
        txtSearchMember = new JTextField();
        txtSearchMember.setFont(fontInput);
        txtSearchMember.setToolTipText("Gõ tên, số điện thoại hoặc mã hội viên để tìm kiếm tức thì");
        pSearchRow.add(lSearchHV, BorderLayout.NORTH);
        pSearchRow.add(txtSearchMember, BorderLayout.CENTER);
        formGrid.add(pSearchRow, gbc);

        // ComboBox Hội Viên
        gbc.gridy = r++;
        JPanel pComboRow = new JPanel(new BorderLayout(6, 0));
        pComboRow.setOpaque(false);
        JLabel lMember = new JLabel("Hội Viên Đang Chọn:");
        lMember.setFont(fontLabel);
        cbHoiVien = new JComboBox<>();
        cbHoiVien.setFont(fontInput);
        cbHoiVien.setMaximumRowCount(10);
        pComboRow.add(lMember, BorderLayout.NORTH);
        pComboRow.add(cbHoiVien, BorderLayout.CENTER);
        formGrid.add(pComboRow, gbc);

        // --- PHẦN 2: THÔNG TIN HỘI VIÊN ĐẦY ĐỦ CHI TIẾT ---
        gbc.gridy = r++;
        gbc.insets = new Insets(10, 3, 4, 3);
        JLabel lblSecHVInfo = new JLabel("2. THÔNG TIN HỘI VIÊN CHI TIẾT");
        lblSecHVInfo.setFont(fontSection);
        lblSecHVInfo.setForeground(new Color(5, 150, 105));
        formGrid.add(lblSecHVInfo, gbc);
        gbc.insets = new Insets(3, 3, 3, 3);

        // Hàng chi tiết 1: Mã HV & Họ Tên
        JPanel rowHv1 = new JPanel(new GridLayout(1, 2, 8, 0));
        rowHv1.setOpaque(false);

        JPanel pHvMa = new JPanel(new BorderLayout(0, 2)); pHvMa.setOpaque(false);
        JLabel lHvMa = new JLabel("Mã Hội Viên / QR:"); lHvMa.setFont(fontLabel);
        txtHvMa = taoTextFieldReadOnly();
        pHvMa.add(lHvMa, BorderLayout.NORTH); pHvMa.add(txtHvMa, BorderLayout.CENTER);

        JPanel pHvTen = new JPanel(new BorderLayout(0, 2)); pHvTen.setOpaque(false);
        JLabel lHvTen = new JLabel("Họ Và Tên:"); lHvTen.setFont(fontLabel);
        txtHvTen = taoTextFieldReadOnly();
        pHvTen.add(lHvTen, BorderLayout.NORTH); pHvTen.add(txtHvTen, BorderLayout.CENTER);

        rowHv1.add(pHvMa); rowHv1.add(pHvTen);
        gbc.gridy = r++;
        formGrid.add(rowHv1, gbc);

        // Hàng chi tiết 2: Số điện thoại & Gói tập
        JPanel rowHv2 = new JPanel(new GridLayout(1, 2, 8, 0));
        rowHv2.setOpaque(false);

        JPanel pHvPhone = new JPanel(new BorderLayout(0, 2)); pHvPhone.setOpaque(false);
        JLabel lHvPhone = new JLabel("Số Điện Thoại:"); lHvPhone.setFont(fontLabel);
        txtHvPhone = taoTextFieldReadOnly();
        pHvPhone.add(lHvPhone, BorderLayout.NORTH); pHvPhone.add(txtHvPhone, BorderLayout.CENTER);

        JPanel pHvGoi = new JPanel(new BorderLayout(0, 2)); pHvGoi.setOpaque(false);
        JLabel lHvGoi = new JLabel("Gói Tập Đang Ký:"); lHvGoi.setFont(fontLabel);
        txtHvGoiTap = taoTextFieldReadOnly();
        pHvGoi.add(lHvGoi, BorderLayout.NORTH); pHvGoi.add(txtHvGoiTap, BorderLayout.CENTER);

        rowHv2.add(pHvPhone); rowHv2.add(pHvGoi);
        gbc.gridy = r++;
        formGrid.add(rowHv2, gbc);

        // Hàng chi tiết 3: Chi nhánh & Trạng thái thẻ
        JPanel rowHv3 = new JPanel(new GridLayout(1, 2, 8, 0));
        rowHv3.setOpaque(false);

        JPanel pHvBranch = new JPanel(new BorderLayout(0, 2)); pHvBranch.setOpaque(false);
        JLabel lHvBranch = new JLabel("Chi Nhánh:"); lHvBranch.setFont(fontLabel);
        txtHvChiNhanh = taoTextFieldReadOnly();
        pHvBranch.add(lHvBranch, BorderLayout.NORTH); pHvBranch.add(txtHvChiNhanh, BorderLayout.CENTER);

        JPanel pHvStatus = new JPanel(new BorderLayout(0, 2)); pHvStatus.setOpaque(false);
        JLabel lHvStatus = new JLabel("Trạng Thái Thẻ:"); lHvStatus.setFont(fontLabel);
        txtHvTrangThai = taoTextFieldReadOnly();
        pHvStatus.add(lHvStatus, BorderLayout.NORTH); pHvStatus.add(txtHvTrangThai, BorderLayout.CENTER);

        rowHv3.add(pHvBranch); rowHv3.add(pHvStatus);
        gbc.gridy = r++;
        formGrid.add(rowHv3, gbc);

        // --- PHẦN 3: CHỈ SỐ INBODY (HLV CÓ THỂ CHỈNH SỬA) ---
        gbc.gridy = r++;
        gbc.insets = new Insets(10, 3, 4, 3);
        JLabel lblSecInBody = new JLabel("3. CHỈ SỐ ĐO INBODY & THỂ TRẠNG (CHỈNH SỬA TẠI ĐÂY)");
        lblSecInBody.setFont(fontSection);
        lblSecInBody.setForeground(new Color(217, 119, 6));
        formGrid.add(lblSecInBody, gbc);
        gbc.insets = new Insets(3, 3, 3, 3);

        // Hàng Mã Đo & Ngày Đo
        JPanel rowCodeDate = new JPanel(new GridLayout(1, 2, 8, 0));
        rowCodeDate.setOpaque(false);

        JPanel pCode = new JPanel(new BorderLayout(0, 2)); pCode.setOpaque(false);
        JLabel lCode = new JLabel("Mã Đo:"); lCode.setFont(fontLabel);
        txtMaDo = new JTextField("IB01"); txtMaDo.setFont(fontInput);
        pCode.add(lCode, BorderLayout.NORTH); pCode.add(txtMaDo, BorderLayout.CENTER);

        JPanel pDate = new JPanel(new BorderLayout(0, 2)); pDate.setOpaque(false);
        JLabel lDate = new JLabel("Ngày Đo:"); lDate.setFont(fontLabel);
        txtNgayDo = new JTextField(sdfDate.format(new Date())); txtNgayDo.setFont(fontInput);
        pDate.add(lDate, BorderLayout.NORTH); pDate.add(txtNgayDo, BorderLayout.CENTER);

        rowCodeDate.add(pCode); rowCodeDate.add(pDate);
        gbc.gridy = r++;
        formGrid.add(rowCodeDate, gbc);

        // Chiều cao & Cân nặng
        JPanel rowHW = new JPanel(new GridLayout(1, 2, 8, 0));
        rowHW.setOpaque(false);

        JPanel pH = new JPanel(new BorderLayout(0, 2)); pH.setOpaque(false);
        JLabel lH = new JLabel("Chiều Cao (cm):"); lH.setFont(fontLabel);
        txtChieuCao = new JTextField("175"); txtChieuCao.setFont(fontInput);
        pH.add(lH, BorderLayout.NORTH); pH.add(txtChieuCao, BorderLayout.CENTER);

        JPanel pW = new JPanel(new BorderLayout(0, 2)); pW.setOpaque(false);
        JLabel lW = new JLabel("Cân Nặng (kg):"); lW.setFont(fontLabel);
        txtCanNang = new JTextField("72.5"); txtCanNang.setFont(fontInput);
        pW.add(lW, BorderLayout.NORTH); pW.add(txtCanNang, BorderLayout.CENTER);

        rowHW.add(pH); rowHW.add(pW);
        gbc.gridy = r++;
        formGrid.add(rowHW, gbc);

        // Khối lượng cơ & Tỷ lệ mỡ
        JPanel rowMF = new JPanel(new GridLayout(1, 2, 8, 0));
        rowMF.setOpaque(false);

        JPanel pM = new JPanel(new BorderLayout(0, 2)); pM.setOpaque(false);
        JLabel lM = new JLabel("Khối Lượng Cơ (kg):"); lM.setFont(fontLabel);
        txtCo = new JTextField("34.2"); txtCo.setFont(fontInput);
        pM.add(lM, BorderLayout.NORTH); pM.add(txtCo, BorderLayout.CENTER);

        JPanel pF = new JPanel(new BorderLayout(0, 2)); pF.setOpaque(false);
        JLabel lF = new JLabel("Tỷ Lệ Mỡ (%):"); lF.setFont(fontLabel);
        txtMo = new JTextField("18.5"); txtMo.setFont(fontInput);
        pF.add(lF, BorderLayout.NORTH); pF.add(txtMo, BorderLayout.CENTER);

        rowMF.add(pM); rowMF.add(pF);
        gbc.gridy = r++;
        formGrid.add(rowMF, gbc);

        // Tự động tính toán BMI tức thì khi HLV thay đổi chiều cao hoặc cân nặng
        DocumentListener bmiRealtimeListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tinhBmiRealtime(); }
            public void removeUpdate(DocumentEvent e) { tinhBmiRealtime(); }
            public void changedUpdate(DocumentEvent e) { tinhBmiRealtime(); }
            private void tinhBmiRealtime() {
                try {
                    String hStr = txtChieuCao.getText().trim();
                    String wStr = txtCanNang.getText().trim();
                    if (hStr.isEmpty() || wStr.isEmpty()) return;
                    double h = Double.parseDouble(hStr) / 100.0;
                    double w = Double.parseDouble(wStr);
                    if (h > 0.5 && h < 2.5 && w > 20 && w < 300) {
                        double bmi = w / (h * h);
                        capNhatKetQuaBMI(bmi);
                    }
                } catch (Exception ignored) {}
            }
        };
        txtChieuCao.getDocument().addDocumentListener(bmiRealtimeListener);
        txtCanNang.getDocument().addDocumentListener(bmiRealtimeListener);

        // Nút Tính BMI thủ công
        btnCalc = new JButton("Tự Động Tính BMI & Đánh Giá Thể Trạng");
        btnCalc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalc.setBackground(new Color(245, 158, 11));
        btnCalc.setForeground(Color.WHITE);
        btnCalc.setFocusPainted(false);
        btnCalc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = r++;
        gbc.insets = new Insets(6, 3, 6, 3);
        formGrid.add(btnCalc, gbc);
        gbc.insets = new Insets(3, 3, 3, 3);

        // Khung kết quả BMI
        JPanel resultBox = new JPanel(new GridLayout(2, 1, 0, 4));
        resultBox.setBackground(new Color(241, 245, 249));
        resultBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(6, 12, 6, 12)
        ));
        lblBMI = new JLabel("Chỉ số BMI: 23.7");
        lblBMI.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBMI.setForeground(new Color(30, 41, 59));

        lblDanhGia = new JLabel("Thể trạng: Cân đối, tỷ lệ cơ thể lý tưởng");
        lblDanhGia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDanhGia.setForeground(new Color(16, 185, 129));

        resultBox.add(lblBMI);
        resultBox.add(lblDanhGia);
        gbc.gridy = r++;
        formGrid.add(resultBox, gbc);

        // Lời khuyên của PT
        gbc.gridy = r++;
        JLabel lNote = new JLabel("Lời Khuyên Của PT:");
        lNote.setFont(fontLabel);
        formGrid.add(lNote, gbc);

        gbc.gridy = r++;
        txtGhiChu = new JTextField("Duy trì tập 4 buổi/tuần, tăng thêm tạ Compound");
        txtGhiChu.setFont(fontInput);
        formGrid.add(txtGhiChu, gbc);

        // Cuộn form nếu màn hình nhỏ
        JScrollPane scrollForm = new JScrollPane(formGrid);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        scrollForm.getVerticalScrollBar().setUnitIncrement(14);
        leftPanel.add(scrollForm, BorderLayout.CENTER);

        // 4 Nút hành động phía dưới
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        btnAdd = taoButtonAction("Lưu Chỉ Số Mới", new Color(16, 185, 129));
        btnUpdate = taoButtonAction("Cập Nhật", new Color(37, 99, 235));
        btnDelete = taoButtonAction("Xóa Bản Ghi", new Color(239, 68, 68));
        btnClear = taoButtonAction("Làm Mới Form", new Color(100, 116, 139));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // =====================================================================
        // PANEL PHẢI: BẢNG LỊCH SỬ ĐO INBODY
        // =====================================================================
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Thanh tìm kiếm bảng và xuất in
        JPanel rightTop = new JPanel(new BorderLayout());
        rightTop.setOpaque(false);

        JPanel pSearchTable = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pSearchTable.setOpaque(false);
        JLabel lblSearchT = new JLabel("Lọc bản đo:");
        lblSearchT.setFont(fontLabel);
        txtSearchTable = new JTextField(15);
        txtSearchTable.setFont(fontInput);
        pSearchTable.add(lblSearchT);
        pSearchTable.add(txtSearchTable);
        rightTop.add(pSearchTable, BorderLayout.WEST);

        btnExport = taoButtonAction("In Phiếu Kết Quả InBody", new Color(37, 99, 235));
        rightTop.add(btnExport, BorderLayout.EAST);
        rightPanel.add(rightTop, BorderLayout.NORTH);

        // Cấu trúc bảng 10 cột có lưu kèm Lời Khuyên PT
        String[] cols = new String[]{
                "Mã Đo", "Hội Viên", "Ngày Đo", "C.Cao (cm)", "C.Nặng (kg)",
                "Cơ (kg)", "Mỡ (%)", "BMI", "Đánh Giá", "Lời Khuyên PT"
        };
        Object[][] data = new Object[][]{
                {"IB01", "Lê Anh Duy", "30/09/2026", "175", "72.5", "34.2", "18.5%", "23.7", "Cân đối", "Duy trì tập 4 buổi/tuần, tăng thêm tạ Compound"},
                {"IB02", "Võ Lê Ngọc Hưng", "28/09/2026", "172", "68.0", "32.5", "17.0%", "23.0", "Chuẩn mẫu", "Tập trung bổ sung đạm và siết cơ ngực"},
                {"IB03", "Trần Minh Quân", "25/09/2026", "180", "82.0", "38.0", "19.5%", "25.3", "Vận động viên", "Cường độ tập cao, chú ý giãn cơ sau buổi tập"},
                {"IB04", "Võ Tấn Phát", "20/09/2026", "168", "65.0", "29.0", "20.0%", "23.0", "Cân đối", "Tăng cường bài tập cardio để giảm thêm 2% mỡ"}
        };

        model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(fontInput);
        table.getTableHeader().setFont(fontLabel);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Căn giữa các cột số liệu
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{0, 2, 3, 4, 5, 6, 7, 8}) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Độ rộng cột tối ưu
        table.getColumnModel().getColumn(0).setPreferredWidth(65);
        table.getColumnModel().getColumn(1).setPreferredWidth(125);
        table.getColumnModel().getColumn(2).setPreferredWidth(85);
        table.getColumnModel().getColumn(3).setPreferredWidth(75);
        table.getColumnModel().getColumn(4).setPreferredWidth(75);
        table.getColumnModel().getColumn(5).setPreferredWidth(65);
        table.getColumnModel().getColumn(6).setPreferredWidth(65);
        table.getColumnModel().getColumn(7).setPreferredWidth(55);
        table.getColumnModel().getColumn(8).setPreferredWidth(110);
        table.getColumnModel().getColumn(9).setPreferredWidth(180);

        // Lọc nhanh dữ liệu bảng
        txtSearchTable.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { locBang(); }
            public void removeUpdate(DocumentEvent e) { locBang(); }
            public void changedUpdate(DocumentEvent e) { locBang(); }
            private void locBang() {
                String text = txtSearchTable.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        // Click chọn dòng trong bảng -> Đồng bộ toàn bộ dữ liệu lên form & thông tin hội viên
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int mr = table.convertRowIndexToModel(table.getSelectedRow());
                napDuLieuTuBangVaoForm(mr);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (table.getSelectedRow() != -1) {
                    int mr = table.convertRowIndexToModel(table.getSelectedRow());
                    napDuLieuTuBangVaoForm(mr);
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        rightPanel.add(scrollTable, BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);
        this.add(splitPane, BorderLayout.CENTER);

        // =====================================================================
        // SỰ KIỆN TÌM KIẾM HỘI VIÊN & CHỌN COMBOBOX
        // =====================================================================
        txtSearchMember.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { timKiemHoiVienNhanh(); }
            public void removeUpdate(DocumentEvent e) { timKiemHoiVienNhanh(); }
            public void changedUpdate(DocumentEvent e) { timKiemHoiVienNhanh(); }
        });

        cbHoiVien.addActionListener(e -> {
            if (isSyncingFromTable || isSyncingMember) return;
            xuLyChonHoiVienTuCombo();
        });

        // Các nút thao tác
        btnCalc.addActionListener(e -> calculateBMI());
        btnAdd.addActionListener(e -> actionThem());
        btnUpdate.addActionListener(e -> actionCapNhat());
        btnDelete.addActionListener(e -> actionXoa());
        btnClear.addActionListener(e -> actionLamMoi());
        btnExport.addActionListener(e -> actionInPhieu());
    }

    private JTextField taoTextFieldReadOnly() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setEditable(false);
        tf.setBackground(new Color(248, 250, 252));
        tf.setForeground(new Color(15, 23, 42));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(3, 6, 3, 6)
        ));
        return tf;
    }

    private JButton taoButtonAction(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // NẠP DỮ LIỆU & ĐỒNG BỘ THÔNG TIN HỘI VIÊN RA CÁC Ô
    // =========================================================================
    private void napDanhSachHoiVienVaoCombo() {
        if (cbHoiVien == null) return;
        danhSachHoiVien = hoiVienDAO.layTatCa();
        if (danhSachHoiVien == null || danhSachHoiVien.isEmpty()) {
            danhSachHoiVien = SampleDataService.getDanhSachHoiVienChuan();
        }

        isSyncingMember = true;
        try {
            cbHoiVien.removeAllItems();
            for (HoiVien hv : danhSachHoiVien) {
                String maCode = (hv.getMaTheQR() != null && !hv.getMaTheQR().trim().isEmpty())
                        ? hv.getMaTheQR()
                        : ("HV" + String.format("%02d", hv.getMaHoiVien()));
                cbHoiVien.addItem(TextSanitizer.sanitize(hv.getHoTen()) + " (" + maCode + ")");
            }
        } finally {
            isSyncingMember = false;
        }

        if (cbHoiVien.getItemCount() > 0 && !danhSachHoiVien.isEmpty()) {
            hienThiThongTinHoiVienRaCacO(danhSachHoiVien.get(0));
        }
    }

    private void timKiemHoiVienNhanh() {
        String query = txtSearchMember.getText().trim().toLowerCase();
        if (query.isEmpty()) return;

        for (int i = 0; i < danhSachHoiVien.size(); i++) {
            HoiVien hv = danhSachHoiVien.get(i);
            String hoTen = (hv.getHoTen() != null) ? hv.getHoTen().toLowerCase() : "";
            String phone = (hv.getSoDienThoai() != null) ? hv.getSoDienThoai() : "";
            String qr = (hv.getMaTheQR() != null) ? hv.getMaTheQR().toLowerCase() : "";
            String ma = "hv" + hv.getMaHoiVien();

            if (hoTen.contains(query) || phone.contains(query) || qr.contains(query) || ma.equalsIgnoreCase(query)) {
                if (i < cbHoiVien.getItemCount() && cbHoiVien.getSelectedIndex() != i) {
                    isSyncingMember = true;
                    try {
                        cbHoiVien.setSelectedIndex(i);
                    } finally {
                        isSyncingMember = false;
                    }
                }
                hienThiThongTinHoiVienRaCacO(hv);
                dongBoChiSoTheoTenHoiVien(hv.getHoTen());
                break;
            }
        }
    }

    private void xuLyChonHoiVienTuCombo() {
        int idx = cbHoiVien.getSelectedIndex();
        if (idx >= 0 && idx < danhSachHoiVien.size()) {
            HoiVien hv = danhSachHoiVien.get(idx);
            hienThiThongTinHoiVienRaCacO(hv);
            dongBoChiSoTheoTenHoiVien(hv.getHoTen());
        }
    }

    /**
     * Tự động hiển thị đầy đủ thông tin hội viên ra các ô chi tiết
     */
    private void hienThiThongTinHoiVienRaCacO(HoiVien hv) {
        if (hv == null) {
            txtHvMa.setText("");
            txtHvTen.setText("");
            txtHvPhone.setText("");
            txtHvGoiTap.setText("");
            txtHvChiNhanh.setText("");
            txtHvTrangThai.setText("");
            return;
        }

        String maCode = (hv.getMaTheQR() != null && !hv.getMaTheQR().trim().isEmpty())
                ? hv.getMaTheQR()
                : ("HV" + String.format("%02d", hv.getMaHoiVien()));
        txtHvMa.setText(maCode + " (ID: " + hv.getMaHoiVien() + ")");
        txtHvTen.setText(TextSanitizer.sanitize(hv.getHoTen()));
        txtHvPhone.setText(hv.getSoDienThoai() != null ? hv.getSoDienThoai() : "Chưa cập nhật");
        txtHvGoiTap.setText(TextSanitizer.sanitize(hv.getGoiTapDangKy() != null ? hv.getGoiTapDangKy() : "Gói Tiêu Chuẩn"));
        txtHvChiNhanh.setText(TextSanitizer.normalizeBranch(hv.getChiNhanhDangKy()));

        String hsd = (hv.getNgayHetHan() != null) ? sdfDate.format(hv.getNgayHetHan()) : "Vô thời hạn";
        String status = TextSanitizer.sanitize(hv.getTrangThai() != null ? hv.getTrangThai() : "Hoạt động");
        txtHvTrangThai.setText(status + " (HSD: " + hsd + ")");
    }

    /**
     * Khi tìm kiếm hoặc chọn một hội viên, tự động đồng bộ chỉ số đo của hội viên đó ra các ô
     */
    private void dongBoChiSoTheoTenHoiVien(String tenHoiVien) {
        if (tenHoiVien == null || tenHoiVien.trim().isEmpty()) return;
        String tenChuan = tenHoiVien.trim();

        // Tìm trong bảng xem hội viên này đã có bản đo nào chưa
        for (int r = 0; r < model.getRowCount(); r++) {
            String memInTable = model.getValueAt(r, 1).toString().trim();
            if (memInTable.equalsIgnoreCase(tenChuan) || tenChuan.contains(memInTable) || memInTable.contains(tenChuan)) {
                int viewRow = table.convertRowIndexToView(r);
                if (viewRow != -1) {
                    table.setRowSelectionInterval(viewRow, viewRow);
                    table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
                }
                napDuLieuTuBangVaoForm(r);
                return;
            }
        }

        // Nếu hội viên chưa từng đo InBody: tự động tạo mã đo mới và nạp ngày hiện tại
        table.clearSelection();
        txtMaDo.setText("IB0" + (model.getRowCount() + 1));
        txtNgayDo.setText(sdfDate.format(new Date()));
        txtGhiChu.setText("Thiết lập lộ trình thể lực và mục tiêu tập luyện cho hội viên");
        calculateBMI();
    }

    /**
     * Khi click chọn dòng trong bảng, nạp toàn bộ số liệu vào các ô và chọn hội viên tương ứng
     */
    private void napDuLieuTuBangVaoForm(int mr) {
        if (mr < 0 || mr >= model.getRowCount()) return;

        txtMaDo.setText(model.getValueAt(mr, 0).toString());
        String memName = model.getValueAt(mr, 1).toString();
        txtNgayDo.setText(model.getValueAt(mr, 2).toString());
        txtChieuCao.setText(model.getValueAt(mr, 3).toString());
        txtCanNang.setText(model.getValueAt(mr, 4).toString());
        txtCo.setText(model.getValueAt(mr, 5).toString());
        txtMo.setText(model.getValueAt(mr, 6).toString().replace("%", ""));

        if (model.getColumnCount() > 9 && model.getValueAt(mr, 9) != null) {
            txtGhiChu.setText(model.getValueAt(mr, 9).toString());
        }

        calculateBMI();

        // Tự động tìm và chọn hội viên trong danh sách để điền các ô thông tin hội viên
        isSyncingFromTable = true;
        try {
            for (int i = 0; i < danhSachHoiVien.size(); i++) {
                HoiVien hv = danhSachHoiVien.get(i);
                String hvTen = hv.getHoTen();
                if (hvTen.equalsIgnoreCase(memName) || memName.contains(hvTen) || hvTen.contains(memName)) {
                    if (i < cbHoiVien.getItemCount()) {
                        cbHoiVien.setSelectedIndex(i);
                    }
                    hienThiThongTinHoiVienRaCacO(hv);
                    break;
                }
            }
        } finally {
            isSyncingFromTable = false;
        }
    }

    // =========================================================================
    // CÁC HÀNH ĐỘNG NGHIỆP VỤ (TÍNH TOÁN, THÊM, CẬP NHẬT, XÓA, LÀM MỚI, IN PHIẾU)
    // =========================================================================
    private boolean calculateBMI() {
        try {
            if (!ValidationHelper.checkSoDuong(this, txtChieuCao, "Chiều cao (cm)")) return false;
            if (!ValidationHelper.checkSoDuong(this, txtCanNang, "Cân nặng (kg)")) return false;

            double h = Double.parseDouble(txtChieuCao.getText().trim()) / 100.0;
            double w = Double.parseDouble(txtCanNang.getText().trim());
            double bmi = w / (h * h);
            capNhatKetQuaBMI(bmi);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Chiều cao (cm) và Cân nặng (kg) hợp lệ!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private void capNhatKetQuaBMI(double bmi) {
        String status;
        Color col;
        if (bmi < 18.5) {
            status = "Thiếu cân (Cần tăng cường cơ nạc)";
            col = new Color(245, 158, 11);
        } else if (bmi <= 24.9) {
            status = "Cân đối, tỷ lệ cơ thể lý tưởng";
            col = new Color(16, 185, 129);
        } else if (bmi <= 29.9) {
            status = "Thừa cân (Nên tập Cardio đốt mỡ)";
            col = new Color(239, 68, 68);
        } else {
            status = "Béo phì (Cần kiểm soát chế độ calo)";
            col = new Color(220, 38, 38);
        }

        lblBMI.setText("Chỉ số BMI: " + df.format(bmi));
        lblDanhGia.setText("Thể trạng: " + status);
        lblDanhGia.setForeground(col);
    }

    /**
     * HLV cập nhật chỉ số đo: Cập nhật dòng đang chọn HOẶC bản đo khớp mã đo / tên hội viên
     */
    private void actionCapNhat() {
        if (!ValidationHelper.checkSoDuong(this, txtChieuCao, "Chiều cao (cm)")) return;
        if (!ValidationHelper.checkSoDuong(this, txtCanNang, "Cân nặng (kg)")) return;
        if (!ValidationHelper.checkSoDuong(this, txtCo, "Khối lượng cơ (kg)")) return;
        if (!ValidationHelper.checkSoDuong(this, txtMo, "Tỷ lệ mỡ (%)")) return;
        if (!calculateBMI()) return;

        int sel = table.getSelectedRow();
        int mr = -1;

        if (sel != -1) {
            mr = table.convertRowIndexToModel(sel);
        } else {
            // Nếu chưa chọn dòng trên bảng, tự động tìm theo Mã Đo
            String code = txtMaDo.getText().trim();
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString().equalsIgnoreCase(code)) {
                    mr = i;
                    break;
                }
            }
            // Nếu vẫn chưa thấy, tìm theo tên hội viên đang chọn
            if (mr == -1 && cbHoiVien.getSelectedItem() != null) {
                String memberName = cbHoiVien.getSelectedItem().toString().split(" \\(")[0].trim();
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 1).toString().equalsIgnoreCase(memberName)) {
                        mr = i;
                        break;
                    }
                }
            }
        }

        if (mr == -1) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Không tìm thấy bản đo cũ của hội viên này trong danh sách.\nBạn có muốn lưu các chỉ số này thành Bản Đo Mới không?",
                    "Bản Đo Chưa Tồn Tại", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                actionThem();
            }
            return;
        }

        String member = (cbHoiVien.getSelectedItem() != null)
                ? cbHoiVien.getSelectedItem().toString().split(" \\(")[0].trim()
                : model.getValueAt(mr, 1).toString();
        String dateStr = txtNgayDo.getText().trim().isEmpty() ? sdfDate.format(new Date()) : txtNgayDo.getText().trim();
        String bmiStr = lblBMI.getText().replace("Chỉ số BMI: ", "").trim();
        String statusStr = lblDanhGia.getText().replace("Thể trạng: ", "").trim();
        String ghiChu = txtGhiChu.getText().trim();

        // Cập nhật toàn bộ chỉ số vào model
        model.setValueAt(txtMaDo.getText().trim(), mr, 0);
        model.setValueAt(member, mr, 1);
        model.setValueAt(dateStr, mr, 2);
        model.setValueAt(txtChieuCao.getText().trim(), mr, 3);
        model.setValueAt(txtCanNang.getText().trim(), mr, 4);
        model.setValueAt(txtCo.getText().trim(), mr, 5);
        model.setValueAt(txtMo.getText().trim().replace("%", "") + "%", mr, 6);
        model.setValueAt(bmiStr, mr, 7);
        model.setValueAt(statusStr, mr, 8);
        if (model.getColumnCount() > 9) {
            model.setValueAt(ghiChu, mr, 9);
        }

        // Chọn lại và cuộn đến dòng vừa cập nhật
        int viewRow = table.convertRowIndexToView(mr);
        if (viewRow != -1) {
            table.setRowSelectionInterval(viewRow, viewRow);
            table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
        }

        SoundHelper.playSuccessBeep();
        JOptionPane.showMessageDialog(this,
                "Đã cập nhật chỉ số InBody thành công cho hội viên [" + member + "]!\n"
                + "• Chiều cao: " + txtChieuCao.getText().trim() + " cm | Cân nặng: " + txtCanNang.getText().trim() + " kg\n"
                + "• Khối lượng cơ: " + txtCo.getText().trim() + " kg | Tỷ lệ mỡ: " + txtMo.getText().trim() + "%\n"
                + "• " + lblBMI.getText() + " (" + statusStr + ")",
                "Cập Nhật Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionThem() {
        if (!ValidationHelper.checkRong(this, txtMaDo, "Mã Đo InBody")) return;
        if (cbHoiVien.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hội viên cần đo InBody!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!ValidationHelper.checkSoDuong(this, txtChieuCao, "Chiều cao (cm)")) return;
        if (!ValidationHelper.checkSoDuong(this, txtCanNang, "Cân nặng (kg)")) return;
        if (!ValidationHelper.checkSoDuong(this, txtCo, "Khối lượng cơ (kg)")) return;
        if (!ValidationHelper.checkSoDuong(this, txtMo, "Tỷ lệ mỡ (%)")) return;
        if (!calculateBMI()) return;

        String code = txtMaDo.getText().trim().toUpperCase();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equalsIgnoreCase(code)) {
                JOptionPane.showMessageDialog(this,
                        "Mã đo '" + code + "' đã tồn tại trong danh sách!\nNếu muốn điều chỉnh số đo của bản này, vui lòng bấm nút 'Cập Nhật'.",
                        "Trùng Mã Đo", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String member = cbHoiVien.getSelectedItem().toString().split(" \\(")[0].trim();
        String dateStr = txtNgayDo.getText().trim().isEmpty() ? sdfDate.format(new Date()) : txtNgayDo.getText().trim();
        String bmiStr = lblBMI.getText().replace("Chỉ số BMI: ", "").trim();
        String statusStr = lblDanhGia.getText().replace("Thể trạng: ", "").trim();
        String ghiChu = txtGhiChu.getText().trim();

        model.addRow(new Object[]{
                code,
                member,
                dateStr,
                txtChieuCao.getText().trim(),
                txtCanNang.getText().trim(),
                txtCo.getText().trim(),
                txtMo.getText().trim().replace("%", "") + "%",
                bmiStr,
                statusStr,
                ghiChu
        });

        int newRow = model.getRowCount() - 1;
        int viewRow = table.convertRowIndexToView(newRow);
        if (viewRow != -1) {
            table.setRowSelectionInterval(viewRow, viewRow);
            table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
        }

        SoundHelper.playSuccessBeep();
        JOptionPane.showMessageDialog(this, "Đã lưu bản đo InBody mới [" + code + "] cho hội viên [" + member + "] thành công!", "Lưu Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actionXoa() {
        int sel = table.getSelectedRow();
        if (sel == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bản ghi đo trong bảng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mr = table.convertRowIndexToModel(sel);
        String code = model.getValueAt(mr, 0).toString();
        String member = model.getValueAt(mr, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa bản đo [" + code + "] của hội viên [" + member + "]?",
                "Xác Nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(mr);
            actionLamMoi();
            SoundHelper.playSuccessBeep();
            JOptionPane.showMessageDialog(this, "Đã xóa bản đo thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actionLamMoi() {
        table.clearSelection();
        txtMaDo.setText("IB0" + (model.getRowCount() + 1));
        txtNgayDo.setText(sdfDate.format(new Date()));
        txtSearchMember.setText("");
        if (cbHoiVien.getItemCount() > 0) {
            cbHoiVien.setSelectedIndex(0);
        }
        txtChieuCao.setText("175");
        txtCanNang.setText("70.0");
        txtCo.setText("33.0");
        txtMo.setText("18.0");
        txtGhiChu.setText("Duy trì tập 4 buổi/tuần, tăng thêm tạ Compound");
        calculateBMI();
        txtSearchTable.setText("");
    }

    private void actionInPhieu() {
        int sel = table.getSelectedRow();
        String member = sel != -1
                ? model.getValueAt(table.convertRowIndexToModel(sel), 1).toString()
                : (cbHoiVien.getSelectedItem() != null ? cbHoiVien.getSelectedItem().toString().split(" \\(")[0] : "Hội viên");

        String report = "========================================================\n"
                + "       PHIẾU KẾT QUẢ PHÂN TÍCH INBODY & THỂ TRẠNG\n"
                + "========================================================\n"
                + "Hội Viên: " + member + " | Mã/QR: " + txtHvMa.getText() + "\n"
                + "Số Điện Thoại: " + txtHvPhone.getText() + "\n"
                + "Gói Tập: " + txtHvGoiTap.getText() + "\n"
                + "Chi Nhánh: " + txtHvChiNhanh.getText() + " | Trạng Thái: " + txtHvTrangThai.getText() + "\n"
                + "--------------------------------------------------------\n"
                + "Ngày Đo: " + txtNgayDo.getText() + " (Mã bản đo: " + txtMaDo.getText() + ")\n"
                + "Chiều Cao: " + txtChieuCao.getText() + " cm | Cân Nặng: " + txtCanNang.getText() + " kg\n"
                + "Khối Lượng Cơ Xương (SMM): " + txtCo.getText() + " kg\n"
                + "Tỷ Lệ Mỡ Cơ Thể (PBF): " + txtMo.getText() + "%\n"
                + lblBMI.getText() + "\n"
                + lblDanhGia.getText() + "\n"
                + "Lời Khuyên Của PT: " + txtGhiChu.getText() + "\n"
                + "========================================================\n"
                + "Đã lưu kết quả phân tích InBody vào hồ sơ thể lực của hội viên thành công!";
        JOptionPane.showMessageDialog(this, report, "Phiếu InBody & Thể Lực", JOptionPane.INFORMATION_MESSAGE);
    }
}
