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
        try {
            memoryCache.add(new HoiVien(1, "QR001", "Lê Anh Duy", "CN01 - Quận 1", sdf.parse("2026-12-31"), "Active"));
            memoryCache.add(new HoiVien(2, "QR002", "Võ Lê Ngọc Hưng", "CN02 - Tân Bình", sdf.parse("2026-10-15"), "Active"));
            memoryCache.add(new HoiVien(3, "QR003", "Võ Tấn Phát", "CN01 - Quận 1", sdf.parse("2025-01-01"), "Active"));
            memoryCache.add(new HoiVien(4, "QR004", "Trần Minh Quân", "CN01 - Quận 1", sdf.parse("2026-11-20"), "Active"));
            memoryCache.add(new HoiVien(5, "QR005", "Hoàng Thùy Linh", "CN02 - Tân Bình", sdf.parse("2026-08-15"), "Active"));
            memoryCache.add(new HoiVien(6, "VIP007", "Đỗ Minh Trí", "Toàn hệ thống (VIP)", sdf.parse("2027-12-31"), "Active"));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể nạp danh sách hội viên mẫu", e);
        }
    }

    public List<HoiVien> layTatCa() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            return new ArrayList<>(memoryCache);
        }

        try (Session session = sf.openSession()) {
            List<HoiVien> list = session.createQuery("FROM HoiVien ORDER BY maHoiVien DESC", HoiVien.class).list();
            if (list != null && !list.isEmpty()) {
                memoryCache.clear();
                memoryCache.addAll(list);
                return list;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "CSDL chưa sẵn sàng, sử dụng bộ nhớ đệm: {0}", e.getMessage());
        }
        return new ArrayList<>(memoryCache);
    }

    public boolean them(HoiVien hv) {
        if (hv == null) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            // Offline mode: lưu vào memory cache
            memoryCache.add(0, hv);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.persist(hv);
            tx.commit();

            memoryCache.removeIf(item -> item.getMaHoiVien() == hv.getMaHoiVien() || item.getMaTheQR().equalsIgnoreCase(hv.getMaTheQR()));
            memoryCache.add(0, hv);
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
            return memoryCache.removeIf(item -> item.getMaHoiVien() == maHoiVien);
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            HoiVien hv = session.get(HoiVien.class, maHoiVien);
            if (hv != null) {
                session.remove(hv);
                tx.commit();
                memoryCache.removeIf(item -> item.getMaHoiVien() == maHoiVien);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi xóa hội viên ID " + maHoiVien, e);
            return false;
        }
    }

    public HoiVien timTheoId(int maHoiVien) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                HoiVien hv = session.get(HoiVien.class, maHoiVien);
                if (hv != null) return hv;
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi tìm hội viên theo ID: {0}", e.getMessage());
            }
        }

        for (HoiVien hv : memoryCache) {
            if (hv.getMaHoiVien() == maHoiVien) return hv;
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
                if (hv != null) return hv;
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi tìm theo mã QR: {0}", e.getMessage());
            }
        }

        for (HoiVien hv : memoryCache) {
            if (hv.getMaTheQR() != null && hv.getMaTheQR().equalsIgnoreCase(q)) {
                return hv;
            }
        }
        return null;
    }

    public HoiVien timKiemThongMinh(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return null;
        String kw = keyword.trim();

        // 1. Khớp mã QR
        HoiVien hv = timTheoMaQR(kw);
        if (hv != null) return hv;

        // 2. Khớp ID số nguyên
        try {
            int id = Integer.parseInt(kw);
            hv = timTheoId(id);
            if (hv != null) return hv;
        } catch (NumberFormatException ignored) {}

        // 3. Khớp số bóc tách từ mã
        String digits = kw.replaceAll("\\D+", "");
        if (!digits.isEmpty()) {
            try {
                int id = Integer.parseInt(digits);
                hv = timTheoId(id);
                if (hv != null) return hv;
            } catch (NumberFormatException ignored) {}
        }

        // 4. Tìm kiếm họ tên
        List<HoiVien> all = layTatCa();
        if (all != null) {
            for (HoiVien m : all) {
                if (m.getMaTheQR() != null && m.getMaTheQR().equalsIgnoreCase(kw)) {
                    return m;
                }
            }
            for (HoiVien m : all) {
                if (m.getHoTen() != null && m.getHoTen().toLowerCase().contains(kw.toLowerCase())) {
                    return m;
                }
            }
        }

        return null;
    }
}
