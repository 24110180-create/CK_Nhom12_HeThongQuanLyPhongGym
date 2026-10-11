package com.nhom12.gym.dao;

import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Data Access Object cho Thực thể Đăng Ký & Điểm Danh Lớp Học (DangKyLop)
 * Quản lý danh sách học viên đăng ký lớp yoga, zumba, boxing và trạng thái điểm danh
 */
public class DangKyLopDAO {

    // Bộ nhớ đệm dự phòng cho chức năng ngoại tuyến
    private static final List<DangKyLop> memoryCache = new CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryCache.isEmpty()) return;
        memoryCache.add(new DangKyLop(1, "LH01", 1, "Lê Anh Duy", "0901234567", new Date(), "Đã điểm danh", "Đi học đúng giờ"));
        memoryCache.add(new DangKyLop(2, "LH01", 2, "Võ Lê Ngọc Hưng", "0902345678", new Date(), "Đã điểm danh", "Mang thảm riêng"));
        memoryCache.add(new DangKyLop(3, "LH01", 5, "Hoàng Thùy Linh", "0905678901", new Date(), "Chưa điểm danh", ""));
        memoryCache.add(new DangKyLop(4, "LH02", 1, "Lê Anh Duy", "0901234567", new Date(), "Chưa điểm danh", ""));
        memoryCache.add(new DangKyLop(5, "LH03", 4, "Trần Minh Quân", "0904567890", new Date(), "Đã điểm danh", "Đầy đủ găng tập"));
    }

    /**
     * Lấy toàn bộ danh sách đăng ký lớp học
     */
    public List<DangKyLop> layTatCa() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<DangKyLop> list = session.createQuery("FROM DangKyLop ORDER BY id DESC", DangKyLop.class).list();
            if (list != null && !list.isEmpty()) {
                memoryCache.clear();
                memoryCache.addAll(list);
                return list;
            }
        } catch (Exception e) {
            System.err.println("[DangKyLopDAO] Lỗi tải danh sách từ CSDL, dùng cache: " + e.getMessage());
        }
        return new ArrayList<>(memoryCache);
    }

    /**
     * Lấy danh sách học viên của một lớp học cụ thể theo mã lớp
     */
    public List<DangKyLop> layDanhSachTheoLop(String maLop) {
        if (maLop == null || maLop.trim().isEmpty()) return new ArrayList<>();
        String code = maLop.trim();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DangKyLop> query = session.createQuery(
                "FROM DangKyLop WHERE UPPER(maLop) = UPPER(:ma) ORDER BY id ASC", DangKyLop.class
            );
            query.setParameter("ma", code);
            List<DangKyLop> list = query.list();
            if (list != null && !list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            System.err.println("[DangKyLopDAO] Lỗi lấy danh sách học viên theo lớp: " + e.getMessage());
        }

        // Fallback bộ nhớ đệm
        List<DangKyLop> result = new ArrayList<>();
        for (DangKyLop dk : memoryCache) {
            if (dk.getMaLop() != null && dk.getMaLop().equalsIgnoreCase(code)) {
                result.add(dk);
            }
        }
        return result;
    }

    /**
     * Kiểm tra xem hội viên đã đăng ký vào lớp này chưa
     */
    public boolean daDangKy(String maLop, int maHoiVien) {
        List<DangKyLop> list = layDanhSachTheoLop(maLop);
        for (DangKyLop dk : list) {
            if (dk.getMaHoiVien() == maHoiVien) {
                return true;
            }
        }
        return false;
    }

    /**
     * Đăng ký hội viên vào lớp học
     * @return true nếu lưu thành công, false nếu thất bại
     */
    public boolean them(DangKyLop dk) {
        if (dk == null) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(dk);
            tx.commit();

            memoryCache.add(dk);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[DangKyLopDAO] Lỗi thêm đăng ký lớp: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật trạng thái điểm danh cho một lượt đăng ký
     */
    public boolean capNhatDiemDanh(int id, String trangThai) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DangKyLop dk = session.get(DangKyLop.class, id);
            if (dk != null) {
                dk.setTrangThaiDiemDanh(trangThai);
                tx.commit();

                // Cập nhật memory cache
                for (DangKyLop item : memoryCache) {
                    if (item.getId() == id) {
                        item.setTrangThaiDiemDanh(trangThai);
                        break;
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[DangKyLopDAO] Lỗi cập nhật điểm danh ID " + id + ": " + e.getMessage());

            // Fallback memory cache khi CSDL offline
            for (DangKyLop item : memoryCache) {
                if (item.getId() == id) {
                    item.setTrangThaiDiemDanh(trangThai);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Xóa lượt đăng ký
     */
    public boolean xoa(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DangKyLop dk = session.get(DangKyLop.class, id);
            if (dk != null) {
                session.remove(dk);
                tx.commit();
                memoryCache.removeIf(item -> item.getId() == id);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[DangKyLopDAO] Lỗi xóa đăng ký lớp: " + e.getMessage());
            return false;
        }
    }
}
