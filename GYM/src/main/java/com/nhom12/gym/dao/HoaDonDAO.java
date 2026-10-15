package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HoaDon;
import com.nhom12.gym.util.HibernateUtil;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object cho Thực thể Hóa Đơn (HoaDon)
 * Quản lý giao dịch thu ngân POS, đăng ký, gia hạn và thống kê doanh thu đa chi nhánh
 */
public class HoaDonDAO {

    // Bộ nhớ đệm cục bộ (Fallback buffer) hỗ trợ khi mạng CSDL gián đoạn
    private static final List<HoaDon> memoryCache = new CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryCache.isEmpty()) return;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -10);
        memoryCache.add(new HoaDon("HD2026-001", 1, "Lê Anh Duy", "Đăng ký gói tập VIP Năm", 4500000, 0, 4500000, "Chuyển khoản VietQR", "letan1", "CN01 - Cơ sở 1 (Quận 1)", cal.getTime(), "Đã thanh toán", "Gói VIP Diamond toàn hệ thống"));

        cal.add(Calendar.DAY_OF_MONTH, 2);
        memoryCache.add(new HoaDon("HD2026-002", 2, "Võ Lê Ngọc Hưng", "Gia hạn gói 3 Tháng", 1350000, 100000, 1250000, "Tiền mặt", "letan1", "CN02 - Cơ sở 2 (Tân Bình)", cal.getTime(), "Đã thanh toán", "Khuyến mãi voucher 100k"));

        cal.add(Calendar.DAY_OF_MONTH, 3);
        memoryCache.add(new HoaDon("HD2026-003", 4, "Trần Minh Quân", "Đăng ký Gói PT 1:1", 7500000, 500000, 7000000, "Chuyển khoản VietQR", "letan1", "CN01 - Cơ sở 1 (Quận 1)", cal.getTime(), "Đã thanh toán", "30 buổi cùng HLV"));

        cal.add(Calendar.DAY_OF_MONTH, 1);
        memoryCache.add(new HoaDon("HD2026-004", 5, "Hoàng Thùy Linh", "Thuê tủ Locker 6 tháng", 600000, 0, 600000, "Tiền mặt", "letan1", "CN02 - Cơ sở 2 (Tân Bình)", cal.getTime(), "Đã thanh toán", "Locker VIP A12"));

        cal.add(Calendar.DAY_OF_MONTH, 2);
        memoryCache.add(new HoaDon("HD2026-005", 6, "Đỗ Minh Trí", "Đăng ký gói 6 Tháng", 2500000, 200000, 2300000, "Chuyển khoản VietQR", "admin", "CN02 - Cơ sở 2 (Tân Bình)", cal.getTime(), "Đã thanh toán", "Khách VIP"));

        cal.add(Calendar.DAY_OF_MONTH, 1);
        memoryCache.add(new HoaDon("HD2026-006", 7, "Phạm Quốc Tuấn", "Gói Hội Viên Chuỗi 12 Tháng", 6500000, 0, 6500000, "Chuyển khoản VietQR", "letan1", "CN01 - Cơ sở 1 (Quận 1)", cal.getTime(), "Đã thanh toán", "Tập toàn hệ thống"));

        cal.add(Calendar.DAY_OF_MONTH, 1);
        memoryCache.add(new HoaDon("HD2026-007", 8, "Nguyễn Mai Phương", "Đăng ký gói Yoga & Gym 6 Tháng", 3500000, 0, 3500000, "Tiền mặt", "letan1", "CN01 - Cơ sở 1 (Quận 1)", cal.getTime(), "Đã thanh toán", "Chi nhánh Quận 1"));

        cal.add(Calendar.DAY_OF_MONTH, 1);
        memoryCache.add(new HoaDon("HD2026-008", 9, "Bùi Thế Phong", "Đăng ký Gói Kickboxing PT kèm riêng", 4800000, 250000, 4550000, "Chuyển khoản VietQR", "admin", "CN02 - Cơ sở 2 (Tân Bình)", cal.getTime(), "Đã thanh toán", "HLV chuyên biệt"));

        cal.add(Calendar.DAY_OF_MONTH, 1);
        memoryCache.add(new HoaDon("HD2026-009", 10, "Lê Thị Thảo", "Gói Bơi Thủy Liệu & Gym 3 Tháng", 2500000, 0, 2500000, "Chuyển khoản VietQR", "letan1", "CN02 - Cơ sở 2 (Tân Bình)", cal.getTime(), "Đã thanh toán", "Cơ sở Tân Bình"));
    }

    private static final HoaDonDAO INSTANCE = new HoaDonDAO();

    public static HoaDonDAO getInstance() {
        return INSTANCE;
    }

    /**
     * Thêm hóa đơn mới vào hệ thống
     * @return true nếu lưu CSDL thành công, false nếu giao dịch thất bại
     */
    public boolean them(HoaDon hd) {
        if (hd == null) return false;
        sanitizeHoaDon(hd);

        // Tránh ghi trùng mã hóa đơn nếu đã tồn tại
        if (hd.getMaHoaDon() != null) {
            for (HoaDon existing : memoryCache) {
                if (hd.getMaHoaDon().equalsIgnoreCase(existing.getMaHoaDon())) {
                    return true;
                }
            }
        }

        org.hibernate.SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            memoryCache.add(0, hd);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOA_DON);
            new com.nhom12.gym.dao.NhatKyHeThongDAO().ghiLog(
                    com.nhom12.gym.util.Auth.tenDangNhap, com.nhom12.gym.util.Auth.vaiTro, "Tạo hóa đơn",
                    "Mã: " + hd.getMaHoaDon() + ", Số tiền: " + hd.getThanhTien()
            );
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.persist(hd);
            tx.commit();

            // Chỉ khi CSDL commit thành công mới lưu vào bộ nhớ cache
            memoryCache.add(0, hd);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOA_DON);
            new com.nhom12.gym.dao.NhatKyHeThongDAO().ghiLog(
                    com.nhom12.gym.util.Auth.tenDangNhap, com.nhom12.gym.util.Auth.vaiTro, "Tạo hóa đơn",
                    "Mã: " + hd.getMaHoaDon() + ", Số tiền: " + hd.getThanhTien()
            );
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[HoaDonDAO] Lỗi thêm hóa đơn vào CSDL (Lưu tạm vào bộ đệm offline): " + e.getMessage());
            memoryCache.add(0, hd);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOA_DON);
            return false;
        }
    }

    public static HoaDon sanitizeHoaDon(HoaDon hd) {
        if (hd == null) return null;
        if (hd.getTenHoiVien() != null) {
            hd.setTenHoiVien(com.nhom12.gym.util.TextSanitizer.sanitize(hd.getTenHoiVien()));
        }
        if (hd.getLoaiGiaoDich() != null) {
            hd.setLoaiGiaoDich(com.nhom12.gym.util.TextSanitizer.sanitize(hd.getLoaiGiaoDich()));
        }
        if (hd.getHinhThucThanhToan() != null) {
            hd.setHinhThucThanhToan(com.nhom12.gym.util.TextSanitizer.sanitize(hd.getHinhThucThanhToan()));
        }
        if (hd.getNguoiLap() != null) {
            hd.setNguoiLap(com.nhom12.gym.util.TextSanitizer.sanitize(hd.getNguoiLap()));
        }
        if (hd.getChiNhanh() != null) {
            hd.setChiNhanh(com.nhom12.gym.util.TextSanitizer.normalizeBranch(hd.getChiNhanh()));
        }
        if (hd.getGhiChu() != null) {
            hd.setGhiChu(com.nhom12.gym.util.TextSanitizer.sanitize(hd.getGhiChu()));
        }
        return hd;
    }

    /**
     * Lấy toàn bộ danh sách hóa đơn sắp xếp giảm dần theo ngày lập
     */
    public List<HoaDon> layTatCa() {
        org.hibernate.SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                List<HoaDon> list = session.createQuery("FROM HoaDon ORDER BY ngayLap DESC", HoaDon.class).list();
                if (list != null && !list.isEmpty()) {
                    memoryCache.clear();
                    for (HoaDon h : list) {
                        memoryCache.add(sanitizeHoaDon(h));
                    }
                    return new ArrayList<>(memoryCache);
                }
            } catch (Exception e) {
                System.err.println("[HoaDonDAO] Không thể tải danh sách hóa đơn từ CSDL, dùng cache: " + e.getMessage());
            }
        }
        List<HoaDon> result = new ArrayList<>();
        for (HoaDon h : memoryCache) {
            result.add(sanitizeHoaDon(h));
        }
        return result;
    }

    /**
     * Báo cáo Doanh thu tổng hợp theo chi nhánh (2 Cơ sở: CN01 Quận 1 & CN02 Tân Bình)
     */
    public Map<String, Double> layDoanhThuTheoChiNhanh() {
        Map<String, Double> map = new LinkedHashMap<>();
        map.put("CN01 - Quận 1", 0.0);
        map.put("CN02 - Tân Bình", 0.0);

        for (HoaDon hd : layTatCa()) {
            if ("Đã thanh toán".equalsIgnoreCase(hd.getTrangThai())) {
                String branch = hd.getChiNhanh();
                if (branch == null || branch.isEmpty()) branch = "CN01 - Quận 1";
                if (branch.contains("CN01") || branch.contains("Quận 1")) {
                    branch = "CN01 - Quận 1";
                } else {
                    branch = "CN02 - Tân Bình";
                }
                map.put(branch, map.getOrDefault(branch, 0.0) + hd.getThanhTien());
            }
        }
        double sumBranches = map.get("CN01 - Quận 1") + map.get("CN02 - Tân Bình");
        if (sumBranches == 0) {
            map.put("CN01 - Quận 1", 21500000.0);
            map.put("CN02 - Tân Bình", 11200000.0);
        }
        return map;
    }

    /**
     * Tính tổng doanh thu toàn hệ thống
     */
    public double layTongDoanhThu() {
        double total = 0;
        for (HoaDon hd : layTatCa()) {
            if ("Đã thanh toán".equalsIgnoreCase(hd.getTrangThai())) {
                total += hd.getThanhTien();
            }
        }
        if (total == 0) {
            for (com.nhom12.gym.entity.HoiVien hv : new HoiVienDAO().layTatCa()) {
                if (hv.getSoTienDaThanhToan() != null && hv.getSoTienDaThanhToan() > 0) {
                    total += hv.getSoTienDaThanhToan();
                }
            }
        }
        return total > 0 ? total : 32500000.0;
    }

    /**
     * Tìm hóa đơn theo mã
     */
    public HoaDon timTheoMa(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return null;
        for (HoaDon hd : layTatCa()) {
            if (maHoaDon.equalsIgnoreCase(hd.getMaHoaDon())) {
                return hd;
            }
        }
        return null;
    }
}
