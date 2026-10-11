package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.util.HibernateUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Data Access Object cho Thực thể Hội Viên (HoiVien)
 * Quản lý các thao tác CRUD và bộ nhớ đệm dự phòng khi mạng chi nhánh chập chờn
 */
public class HoiVienDAO {

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
            System.err.println("[HoiVienDAO] Không thể nạp danh sách hội viên mẫu: " + e.getMessage());
        }
    }

    /**
     * Lấy toàn bộ danh sách hội viên (Ưu tiên CSDL, fallback sang bộ nhớ đệm nếu mất kết nối)
     */
    public List<HoiVien> layTatCa() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<HoiVien> list = session.createQuery("FROM HoiVien ORDER BY maHoiVien DESC", HoiVien.class).list();
            if (list != null && !list.isEmpty()) {
                // Cập nhật bộ nhớ đệm đồng bộ với CSDL
                memoryCache.clear();
                memoryCache.addAll(list);
                return list;
            }
        } catch (Exception e) {
            System.err.println("[HoiVienDAO] CSDL chưa sẵn sàng, sử dụng bộ nhớ đệm: " + e.getMessage());
        }
        return new ArrayList<>(memoryCache);
    }

    /**
     * Thêm mới hội viên vào CSDL
     * @return true nếu lưu thành công, false nếu thất bại (không nuốt lỗi)
     */
    public boolean them(HoiVien hv) {
        if (hv == null) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(hv);
            tx.commit();

            // Lưu thành công CSDL thì đồng bộ vào memory cache
            memoryCache.removeIf(item -> item.getMaHoiVien() == hv.getMaHoiVien() || item.getMaTheQR().equalsIgnoreCase(hv.getMaTheQR()));
            memoryCache.add(0, hv);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[HoiVienDAO] Lỗi thêm hội viên vào CSDL: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin hội viên
     * @return true nếu cập nhật thành công, false nếu lỗi
     */
    public boolean capNhat(HoiVien hv) {
        if (hv == null) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(hv);
            tx.commit();

            // Đồng bộ bộ nhớ đệm
            for (int i = 0; i < memoryCache.size(); i++) {
                if (memoryCache.get(i).getMaHoiVien() == hv.getMaHoiVien()) {
                    memoryCache.set(i, hv);
                    break;
                }
            }
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[HoiVienDAO] Lỗi cập nhật hội viên: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa hội viên theo mã ID
     */
    public boolean xoa(int maHoiVien) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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
            System.err.println("[HoiVienDAO] Lỗi xóa hội viên ID " + maHoiVien + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm hội viên theo ID khóa chính
     */
    public HoiVien timTheoId(int maHoiVien) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            HoiVien hv = session.get(HoiVien.class, maHoiVien);
            if (hv != null) return hv;
        } catch (Exception e) {
            System.err.println("[HoiVienDAO] Lỗi tìm hội viên theo ID: " + e.getMessage());
        }

        // Fallback bộ nhớ đệm
        for (HoiVien hv : memoryCache) {
            if (hv.getMaHoiVien() == maHoiVien) return hv;
        }
        return null;
    }

    /**
     * Tìm hội viên theo Mã Thẻ QR (Dùng trong Use Case Check-in)
     */
    public HoiVien timTheoMaQR(String maQR) {
        if (maQR == null || maQR.trim().isEmpty()) return null;
        String q = maQR.trim();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HoiVien> query = session.createQuery(
                "FROM HoiVien v WHERE UPPER(v.maTheQR) = UPPER(:ma)", HoiVien.class
            );
            query.setParameter("ma", q);
            HoiVien hv = query.uniqueResult();
            if (hv != null) return hv;
        } catch (Exception e) {
            System.err.println("[HoiVienDAO] Lỗi tìm theo mã QR trong CSDL: " + e.getMessage());
        }

        // Fallback bộ nhớ đệm
        for (HoiVien hv : memoryCache) {
            if (hv.getMaTheQR() != null && hv.getMaTheQR().equalsIgnoreCase(q)) {
                return hv;
            }
        }
        return null;
    }

    /**
     * Tìm kiếm thông minh linh hoạt:
     * - Khớp theo mã thẻ QR
     * - Khớp theo mã số ID
     * - Khớp theo họ tên (chứa từ khóa)
     */
    public HoiVien timKiemThongMinh(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return null;
        String kw = keyword.trim();

        // 1. Khớp mã QR chính xác
        HoiVien hv = timTheoMaQR(kw);
        if (hv != null) return hv;

        // 2. Khớp theo ID số nguyên
        try {
            int id = Integer.parseInt(kw);
            hv = timTheoId(id);
            if (hv != null) return hv;
        } catch (NumberFormatException ignored) {
        }

        // 3. Khớp chuỗi số bên trong mã (ví dụ "HV01" -> 1)
        String digits = kw.replaceAll("\\D+", "");
        if (!digits.isEmpty()) {
            try {
                int id = Integer.parseInt(digits);
                hv = timTheoId(id);
                if (hv != null) return hv;
            } catch (NumberFormatException ignored) {
            }
        }

        // 4. Tìm kiếm trong danh sách tổng hợp
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
