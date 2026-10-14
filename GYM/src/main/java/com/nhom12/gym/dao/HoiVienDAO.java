package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.util.HibernateUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Data Access Object cho Thực thể Hội Viên (HoiVien)
 * Quản lý các thao tác CRUD và bộ nhớ đệm dự phòng khi mạng chi nhánh chập chờn
 */
public class HoiVienDAO {

    private static final Logger LOGGER = Logger.getLogger(HoiVienDAO.class.getName());

    // Bộ nhớ đệm dự phòng (Memory Fallback Buffer) đồng bộ khi CSDL offline
    private static final List<HoiVien> memoryCache = new CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryCache.isEmpty()) return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            memoryCache.add(new HoiVien(1, "QR001", "Lê Anh Duy", "0974482749", "CN01 - Cơ sở 1 (Quận 1)",
                    "Gói Kim Cương 12 Tháng", sdfTime.parse("2026-01-15 08:30:15"), sdf.parse("2026-12-31"),
                    8500000.0, "Chuyển khoản VietQR", "Active"));
            memoryCache.add(new HoiVien(2, "QR002", "Võ Lê Ngọc Hưng", "0912345678", "CN02 - Cơ sở 2 (Tân Bình)",
                    "Gói Standard 3 Tháng", sdfTime.parse("2026-07-15 14:15:20"), sdf.parse("2026-10-15"),
                    2400000.0, "Chuyển khoản VietQR", "Active"));
            memoryCache.add(new HoiVien(3, "QR003", "Võ Tấn Phát", "0988776655", "CN01 - Cơ sở 1 (Quận 1)",
                    "Gói Cơ Bản 1 Tháng", sdfTime.parse("2024-12-01 09:45:00"), sdf.parse("2025-01-01"),
                    900000.0, "Tiền mặt", "Expired"));
            memoryCache.add(new HoiVien(4, "QR004", "Trần Minh Quân", "0933221100", "CN01 - Cơ sở 1 (Quận 1)",
                    "Gói Premium 6 Tháng", sdfTime.parse("2026-05-20 16:20:45"), sdf.parse("2026-11-20"),
                    4800000.0, "Quẹt thẻ POS", "Active"));
            memoryCache.add(new HoiVien(5, "QR005", "Hoàng Thùy Linh", "0977112233", "CN02 - Cơ sở 2 (Tân Bình)",
                    "Gói Sinh Viên 3 Tháng", sdfTime.parse("2026-05-15 10:05:12"), sdf.parse("2026-08-15"),
                    1500000.0, "Chuyển khoản VietQR", "Active"));
            memoryCache.add(new HoiVien(6, "VIP007", "Đỗ Minh Trí", "0909998877", "Toàn hệ thống (VIP)",
                    "Gói VIP Toàn Hệ Thống 2 Năm", sdfTime.parse("2026-01-01 11:30:00"), sdf.parse("2027-12-31"),
                    15000000.0, "Chuyển khoản VietQR", "Active"));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể nạp danh sách hội viên mẫu", e);
        }
    }

    public List<HoiVien> layTatCa() {
        List<HoiVien> result = new ArrayList<>();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                List<HoiVien> list = session.createQuery("FROM HoiVien WHERE maTheQR NOT LIKE 'QR_TEST_%' ORDER BY maHoiVien DESC", HoiVien.class).list();
                if (list != null && !list.isEmpty()) {
                    memoryCache.clear();
                    memoryCache.addAll(list);
                    for (HoiVien item : list) {
                        result.add(sanitizeHoiVien(item));
                    }
                    return result;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "CSDL chưa sẵn sàng, sử dụng bộ nhớ đệm: {0}", e.getMessage());
            }
        }
        for (HoiVien item : memoryCache) {
            if (item.getMaTheQR() != null && item.getMaTheQR().startsWith("QR_TEST_")) continue;
            result.add(sanitizeHoiVien(item));
        }
        return result;
    }

    public boolean them(HoiVien hv) {
        if (hv == null) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            // Offline mode: lưu vào memory cache
            memoryCache.add(0, hv);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.persist(hv);
            tx.commit();

            memoryCache.removeIf(item -> item.getMaHoiVien() == hv.getMaHoiVien() || item.getMaTheQR().equalsIgnoreCase(hv.getMaTheQR()));
            memoryCache.add(0, hv);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi thêm hội viên vào CSDL", e);
            return false;
        }
    }

    public boolean capNhat(HoiVien hv) {
        if (hv == null) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            for (int i = 0; i < memoryCache.size(); i++) {
                if (memoryCache.get(i).getMaHoiVien() == hv.getMaHoiVien()) {
                    memoryCache.set(i, hv);
                    com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
                    return true;
                }
            }
            return false;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.merge(hv);
            tx.commit();

            for (int i = 0; i < memoryCache.size(); i++) {
                if (memoryCache.get(i).getMaHoiVien() == hv.getMaHoiVien()) {
                    memoryCache.set(i, hv);
                    break;
                }
            }
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật hội viên", e);
            return false;
        }
    }

    public boolean xoa(int maHoiVien) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            boolean removed = memoryCache.removeIf(item -> item.getMaHoiVien() == maHoiVien);
            if (removed) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
            }
            return removed;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            HoiVien hv = session.get(HoiVien.class, maHoiVien);
            if (hv != null) {
                session.remove(hv);
                tx.commit();
                memoryCache.removeIf(item -> item.getMaHoiVien() == maHoiVien);
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi xóa hội viên ID " + maHoiVien, e);
            return false;
        }
    }

    private HoiVien sanitizeHoiVien(HoiVien hv) {
        if (hv == null) return null;
        hv.setHoTen(com.nhom12.gym.util.TextSanitizer.sanitize(hv.getHoTen()));
        hv.setChiNhanhDangKy(com.nhom12.gym.util.TextSanitizer.normalizeBranch(hv.getChiNhanhDangKy()));
        if (hv.getTrangThai() != null) {
            hv.setTrangThai(com.nhom12.gym.util.TextSanitizer.sanitize(hv.getTrangThai()));
        }
        return hv;
    }

    public HoiVien timTheoId(int maHoiVien) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                HoiVien hv = session.get(HoiVien.class, maHoiVien);
                if (hv != null) return sanitizeHoiVien(hv);
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi tìm hội viên theo ID: {0}", e.getMessage());
            }
        }

        for (HoiVien hv : memoryCache) {
            if (hv.getMaHoiVien() == maHoiVien) return sanitizeHoiVien(hv);
        }
        return null;
    }

    public HoiVien timTheoMaQR(String maQR) {
        if (maQR == null || maQR.trim().isEmpty()) return null;
        String q = maQR.trim();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                Query<HoiVien> query = session.createQuery(
                    "FROM HoiVien v WHERE UPPER(v.maTheQR) = UPPER(:ma)", HoiVien.class
                );
                query.setParameter("ma", q);
                HoiVien hv = query.uniqueResult();
                if (hv != null) return sanitizeHoiVien(hv);
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi tìm theo mã QR: {0}", e.getMessage());
            }
        }

        for (HoiVien hv : memoryCache) {
            if (hv.getMaTheQR() != null && hv.getMaTheQR().equalsIgnoreCase(q)) {
                return sanitizeHoiVien(hv);
            }
        }
        return null;
    }

    public HoiVien timKiemThongMinh(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return null;
        String kw = keyword.trim();

        // 1. Khớp mã QR chính xác
        HoiVien hv = timTheoMaQR(kw);
        if (hv != null) return sanitizeHoiVien(hv);

        // 2. Khớp ID số nguyên
        try {
            int id = Integer.parseInt(kw);
            hv = timTheoId(id);
            if (hv != null) return sanitizeHoiVien(hv);
        } catch (NumberFormatException ignored) {}

        // 3. Khớp số bóc tách từ mã dạng HV01, HV386
        String digits = kw.replaceAll("\\D+", "");
        if (!digits.isEmpty()) {
            try {
                int id = Integer.parseInt(digits);
                hv = timTheoId(id);
                if (hv != null) return sanitizeHoiVien(hv);
            } catch (NumberFormatException ignored) {}
        }

        // 4. Tìm kiếm theo Số Điện Thoại, Mã QR chứa, hoặc Họ Tên
        List<HoiVien> all = layTatCa();
        if (all != null) {
            for (HoiVien m : all) {
                if (m.getSoDienThoai() != null && m.getSoDienThoai().replaceAll("\\s+", "").contains(kw)) {
                    return sanitizeHoiVien(m);
                }
            }
            for (HoiVien m : all) {
                if (m.getMaTheQR() != null && m.getMaTheQR().equalsIgnoreCase(kw)) {
                    return sanitizeHoiVien(m);
                }
            }
            for (HoiVien m : all) {
                if (m.getHoTen() != null && m.getHoTen().toLowerCase().contains(kw.toLowerCase())) {
                    return sanitizeHoiVien(m);
                }
            }
        }

        return null;
    }
}
