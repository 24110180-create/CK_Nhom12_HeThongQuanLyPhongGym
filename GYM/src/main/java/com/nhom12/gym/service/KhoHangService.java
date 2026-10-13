package com.nhom12.gym.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service Quản Lý Kho Hàng & Sản Phẩm Đã Bán
 * Đồng bộ thời gian thực giữa Quản Lý Kho Hàng và Quầy Bán Hàng POS:
 * - Khi bán hàng tại POS -> Tự động GIẢM số lượng tồn kho & ghi nhận Sản Phẩm Đã Bán.
 * - Khi nhập kho thêm -> Tự động TĂNG số lượng tồn kho & đồng bộ sang POS.
 * - Khi hoàn trả hàng -> Tự động TĂNG LẠI số lượng tồn kho hợp logic.
 */
public class KhoHangService {

    // Interface lắng nghe thay đổi tồn kho & đơn hàng
    public interface KhoHangListener {
        void onKhoHangChanged();
    }

    // Thực thể Sản phẩm trong kho
    public static class SanPhamKho implements Serializable {
        private static final long serialVersionUID = 1L;
        private String maSP;
        private String tenSP;
        private String danhMuc;
        private int soLuongTon;
        private double giaBan;
        private String hinhAnh;

        public SanPhamKho() {}

        public SanPhamKho(String maSP, String tenSP, String danhMuc, int soLuongTon, double giaBan, String hinhAnh) {
            this.maSP = maSP;
            this.tenSP = tenSP;
            this.danhMuc = danhMuc;
            this.soLuongTon = soLuongTon;
            this.giaBan = giaBan;
            this.hinhAnh = hinhAnh;
        }

        public String getMaSP() { return maSP; }
        public void setMaSP(String maSP) { this.maSP = maSP; }

        public String getTenSP() { return tenSP; }
        public void setTenSP(String tenSP) { this.tenSP = tenSP; }

        public String getDanhMuc() { return danhMuc; }
        public void setDanhMuc(String danhMuc) { this.danhMuc = danhMuc; }

        public int getSoLuongTon() { return soLuongTon; }
        public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

        public double getGiaBan() { return giaBan; }
        public void setGiaBan(double giaBan) { this.giaBan = giaBan; }

        public String getHinhAnh() { return hinhAnh; }
        public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
    }

    // Thực thể Sản phẩm kho đã bán (Nhật ký xuất kho POS)
    public static class SanPhamDaBan implements Serializable {
        private static final long serialVersionUID = 1L;
        private String maGiaoDich;
        private String maSP;
        private String tenSP;
        private String danhMuc;
        private int soLuong;
        private double donGia;
        private double thanhTien;
        private Date thoiGian;
        private String phuongThucTT;
        private String thuNgan;
        private String trangThai; // "Đã bán (Đã trừ kho)", "Đã hoàn trả (+X vào kho)"

        public SanPhamDaBan() {}

        public SanPhamDaBan(String maGiaoDich, String maSP, String tenSP, String danhMuc,
                           int soLuong, double donGia, double thanhTien, Date thoiGian,
                           String phuongThucTT, String thuNgan, String trangThai) {
            this.maGiaoDich = maGiaoDich;
            this.maSP = maSP;
            this.tenSP = tenSP;
            this.danhMuc = danhMuc;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.thanhTien = thanhTien;
            this.thoiGian = thoiGian;
            this.phuongThucTT = phuongThucTT;
            this.thuNgan = thuNgan;
            this.trangThai = trangThai;
        }

        public String getMaGiaoDich() { return maGiaoDich; }
        public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }

        public String getMaSP() { return maSP; }
        public void setMaSP(String maSP) { this.maSP = maSP; }

        public String getTenSP() { return tenSP; }
        public void setTenSP(String tenSP) { this.tenSP = tenSP; }

        public String getDanhMuc() { return danhMuc; }
        public void setDanhMuc(String danhMuc) { this.danhMuc = danhMuc; }

        public int getSoLuong() { return soLuong; }
        public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

        public double getDonGia() { return donGia; }
        public void setDonGia(double donGia) { this.donGia = donGia; }

        public double getThanhTien() { return thanhTien; }
        public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

        public Date getThoiGian() { return thoiGian; }
        public void setThoiGian(Date thoiGian) { this.thoiGian = thoiGian; }

        public String getPhuongThucTT() { return phuongThucTT; }
        public void setPhuongThucTT(String phuongThucTT) { this.phuongThucTT = phuongThucTT; }

        public String getThuNgan() { return thuNgan; }
        public void setThuNgan(String thuNgan) { this.thuNgan = thuNgan; }

        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    }

    // Item giỏ hàng khi bán tại POS
    public static class CartItem {
        private String maSP;
        private String tenSP;
        private int soLuong;
        private double donGia;

        public CartItem(String maSP, String tenSP, int soLuong, double donGia) {
            this.maSP = maSP;
            this.tenSP = tenSP;
            this.soLuong = soLuong;
            this.donGia = donGia;
        }

        public String getMaSP() { return maSP; }
        public String getTenSP() { return tenSP; }
        public int getSoLuong() { return soLuong; }
        public double getDonGia() { return donGia; }
    }

    // Singleton Pattern
    private static KhoHangService instance;
    private final List<SanPhamKho> danhSachKho = new CopyOnWriteArrayList<>();
    private final List<SanPhamDaBan> danhSachDaBan = new CopyOnWriteArrayList<>();
    private final List<KhoHangListener> listeners = new CopyOnWriteArrayList<>();
    private int maGiaoDichCounter = 100;

    public static synchronized KhoHangService getInstance() {
        if (instance == null) {
            instance = new KhoHangService();
        }
        return instance;
    }

    private KhoHangService() {
        khoiTaoDuLieuKhoMau();
    }

    private void khoiTaoDuLieuKhoMau() {
        // 1. Khởi tạo danh sách tồn kho ban đầu
        danhSachKho.add(new SanPhamKho("SP001", "Nước Khoáng Tinh Khiết Aquafina", "Nước uống giải khát", 150, 10000, "prod_water.jpg"));
        danhSachKho.add(new SanPhamKho("SP002", "Nước Tăng Lực Thể Thao Monster", "Nước uống giải khát", 45, 45000, "prod_energy.jpg"));
        danhSachKho.add(new SanPhamKho("SP005", "Whey Protein Isolate Pha Sẵn", "Thực phẩm bổ sung dinh dưỡng", 28, 65000, "prod_whey.jpg"));
        danhSachKho.add(new SanPhamKho("SP010", "Khăn Tập Gym Microfiber Thấm Hút", "Phụ kiện tập luyện", 30, 35000, "prod_towel.jpg"));
        danhSachKho.add(new SanPhamKho("SP011", "Găng Tay Tập Tạ Da Bò Cao Cấp", "Phụ kiện tập luyện", 15, 120000, "prod_gloves.jpg"));
        danhSachKho.add(new SanPhamKho("SP012", "Chuối Tươi Nạp Năng Lượng Pre-Workout", "Nước uống giải khát", 60, 15000, "prod_banana.jpg"));

        // 2. Khởi tạo lịch sử các sản phẩm đã bán mẫu (kèm ngày giờ chính xác)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            danhSachDaBan.add(new SanPhamDaBan("POS-20261013-001", "SP001", "Nước Khoáng Tinh Khiết Aquafina", "Nước uống giải khát",
                    2, 10000, 20000, sdf.parse("2026-10-13 08:15:20"), "Chuyển khoản VietQR", "Lê Anh Duy (Admin)", "Đã bán (Đã trừ kho)"));
            danhSachDaBan.add(new SanPhamDaBan("POS-20261013-002", "SP005", "Whey Protein Isolate Pha Sẵn", "Thực phẩm bổ sung dinh dưỡng",
                    1, 65000, 65000, sdf.parse("2026-10-13 09:30:45"), "Chuyển khoản VietQR", "Thu ngân quầy sáng", "Đã bán (Đã trừ kho)"));
            danhSachDaBan.add(new SanPhamDaBan("POS-20261013-003", "SP002", "Nước Tăng Lực Thể Thao Monster", "Nước uống giải khát",
                    3, 45000, 135000, sdf.parse("2026-10-13 10:45:10"), "Tiền mặt", "Thu ngân quầy sáng", "Đã bán (Đã trừ kho)"));
            danhSachDaBan.add(new SanPhamDaBan("POS-20261013-004", "SP011", "Găng Tay Tập Tạ Da Bò Cao Cấp", "Phụ kiện tập luyện",
                    1, 120000, 120000, sdf.parse("2026-10-13 11:20:00"), "Quẹt thẻ POS", "Lê Anh Duy (Admin)", "Đã bán (Đã trừ kho)"));
            danhSachDaBan.add(new SanPhamDaBan("POS-20261013-005", "SP012", "Chuối Tươi Nạp Năng Lượng Pre-Workout", "Nước uống giải khát",
                    4, 15000, 60000, sdf.parse("2026-10-13 14:05:30"), "Chuyển khoản VietQR", "Thu ngân quầy chiều", "Đã bán (Đã trừ kho)"));
            danhSachDaBan.add(new SanPhamDaBan("POS-20261012-088", "SP010", "Khăn Tập Gym Microfiber Thấm Hút", "Phụ kiện tập luyện",
                    2, 35000, 70000, sdf.parse("2026-10-12 17:50:15"), "Tiền mặt", "Thu ngân quầy chiều", "Đã bán (Đã trừ kho)"));
        } catch (Exception ignored) {}
    }

    public List<SanPhamKho> layTatCaSanPhamKho() {
        return new ArrayList<>(danhSachKho);
    }

    public List<SanPhamDaBan> layTatCaSanPhamDaBan() {
        return new ArrayList<>(danhSachDaBan);
    }

    public SanPhamKho timSanPhamTheoMa(String maSP) {
        if (maSP == null) return null;
        for (SanPhamKho sp : danhSachKho) {
            if (sp.getMaSP().equalsIgnoreCase(maSP.trim())) return sp;
        }
        return null;
    }

    public SanPhamKho timSanPhamTheoTen(String tenSP) {
        if (tenSP == null) return null;
        for (SanPhamKho sp : danhSachKho) {
            if (sp.getTenSP().equalsIgnoreCase(tenSP.trim())) return sp;
        }
        // Tìm gần đúng nếu không khớp tuyệt đối
        for (SanPhamKho sp : danhSachKho) {
            if (sp.getTenSP().toLowerCase().contains(tenSP.trim().toLowerCase()) ||
                tenSP.trim().toLowerCase().contains(sp.getTenSP().toLowerCase())) {
                return sp;
            }
        }
        return null;
    }

    /**
     * BÁN HÀNG TẠI QUẦY POS -> TỰ ĐỘNG GIẢM TỒN KHO & GHI NHẬN SẢN PHẨM ĐÃ BÁN
     * @return null nếu thành công, chuỗi thông báo lỗi nếu thất bại (ví dụ không đủ hàng tồn kho)
     */
    public synchronized String xuatKhoBanHang(List<CartItem> cartItems, String phuongThucTT, String thuNgan) {
        if (cartItems == null || cartItems.isEmpty()) {
            return "Giỏ hàng rỗng, không thể xuất kho!";
        }

        // BƯỚC 1: Kiểm tra xem tất cả mặt hàng có đủ tồn kho không
        for (CartItem item : cartItems) {
            SanPhamKho sp = timSanPhamTheoMa(item.getMaSP());
            if (sp == null) {
                sp = timSanPhamTheoTen(item.getTenSP());
            }
            if (sp == null) {
                return "Sản phẩm '" + item.getTenSP() + "' không tồn tại trong kho chứa!";
            }
            if (sp.getSoLuongTon() < item.getSoLuong()) {
                return "Sản phẩm '" + sp.getTenSP() + "' chỉ còn " + sp.getSoLuongTon() + " trong kho, không đủ xuất " + item.getSoLuong() + "!";
            }
        }

        // BƯỚC 2: Thực hiện trừ tồn kho & ghi nhận danh sách đã bán
        SimpleDateFormat sdfOrder = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String maGD = "POS-" + sdfOrder.format(new Date()) + "-" + (++maGiaoDichCounter);
        Date now = new Date();

        for (CartItem item : cartItems) {
            SanPhamKho sp = timSanPhamTheoMa(item.getMaSP());
            if (sp == null) sp = timSanPhamTheoTen(item.getTenSP());

            // LOGIC GIẢM TỒN KHO CHÍNH XÁC:
            int tonCu = sp.getSoLuongTon();
            int tonMoi = tonCu - item.getSoLuong();
            sp.setSoLuongTon(Math.max(0, tonMoi));

            // Thêm vào danh mục sản phẩm đã bán:
            SanPhamDaBan daBan = new SanPhamDaBan(
                    maGD,
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getDanhMuc(),
                    item.getSoLuong(),
                    sp.getGiaBan(),
                    item.getSoLuong() * sp.getGiaBan(),
                    now,
                    (phuongThucTT != null && !phuongThucTT.isEmpty()) ? phuongThucTT : "Chuyển khoản VietQR",
                    (thuNgan != null && !thuNgan.isEmpty()) ? thuNgan : "Thu ngân FitLife POS",
                    "Đã bán (Đã trừ kho)"
            );
            danhSachDaBan.add(0, daBan);
        }

        // BƯỚC 3: Đồng bộ thông báo realtime sang toàn bộ các màn hình
        notifyListeners();
        return null; // Thành công
    }

    /**
     * NHẬP HÀNG THÊM VÀO KHO -> TỰ ĐỘNG TĂNG SỐ LƯỢNG TỒN KHO
     */
    public synchronized boolean nhapHangVaoKho(String maSP, int soLuongThem, double giaMoi) {
        SanPhamKho sp = timSanPhamTheoMa(maSP);
        if (sp == null) return false;

        // LOGIC TĂNG TỒN KHO KHI NHẬP HÀNG:
        int tonCu = sp.getSoLuongTon();
        sp.setSoLuongTon(tonCu + Math.max(0, soLuongThem));
        if (giaMoi > 0) {
            sp.setGiaBan(giaMoi);
        }

        notifyListeners();
        return true;
    }

    /**
     * THÊM SẢN PHẨM MỚI VÀO KHO
     */
    public synchronized boolean themSanPhamMoi(SanPhamKho sp) {
        if (sp == null || sp.getMaSP() == null || sp.getMaSP().trim().isEmpty()) return false;
        if (timSanPhamTheoMa(sp.getMaSP()) != null) return false;

        danhSachKho.add(sp);
        notifyListeners();
        return true;
    }

    /**
     * CẬP NHẬT THÔNG TIN SẢN PHẨM KHO
     */
    public synchronized boolean capNhatSanPham(SanPhamKho spMoi) {
        if (spMoi == null) return false;
        for (int i = 0; i < danhSachKho.size(); i++) {
            if (danhSachKho.get(i).getMaSP().equalsIgnoreCase(spMoi.getMaSP())) {
                danhSachKho.set(i, spMoi);
                notifyListeners();
                return true;
            }
        }
        return false;
    }

    /**
     * XÓA MẶT HÀNG KHỎI KHO
     */
    public synchronized boolean xoaSanPham(String maSP) {
        boolean removed = danhSachKho.removeIf(sp -> sp.getMaSP().equalsIgnoreCase(maSP));
        if (removed) {
            notifyListeners();
        }
        return removed;
    }

    /**
     * HOÀN TRẢ HÀNG ĐÃ BÁN VỀ KHO -> TỰ ĐỘNG TĂNG LẠI SỐ LƯỢNG VÀO KHO CHỨA
     */
    public synchronized boolean hoanTraHangVeKho(String maGiaoDich, String maSP, int soLuongHoanTra, String lyDo) {
        SanPhamDaBan target = null;
        for (SanPhamDaBan db : danhSachDaBan) {
            if (db.getMaGiaoDich().equalsIgnoreCase(maGiaoDich) && db.getMaSP().equalsIgnoreCase(maSP)) {
                target = db;
                break;
            }
        }
        if (target == null) return false;

        SanPhamKho sp = timSanPhamTheoMa(maSP);
        if (sp != null) {
            // LOGIC TĂNG LẠI TỒN KHO HỢP LÝ:
            sp.setSoLuongTon(sp.getSoLuongTon() + soLuongHoanTra);
        }

        target.setTrangThai("Đã hoàn trả (+" + soLuongHoanTra + " vào kho) - " + lyDo);
        notifyListeners();
        return true;
    }

    // Cơ chế Observer: Đăng ký & Thông báo
    public void addListener(KhoHangListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(KhoHangListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (KhoHangListener l : listeners) {
            try {
                l.onKhoHangChanged();
            } catch (Exception ignored) {}
        }
    }
}
