package com.nhom12.gym.dao;

import com.nhom12.gym.entity.NhatKyHeThong;
import com.nhom12.gym.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Data Access Object cho Nhật Ký Hệ Thống (NhatKyHeThongDAO)
 * Quản lý ghi nhật ký kiểm toán và tra cứu lịch sử thao tác của các vai trò trong hệ thống.
 */
public class NhatKyHeThongDAO {

    private static final Logger LOGGER = Logger.getLogger(NhatKyHeThongDAO.class.getName());
    private static final List<NhatKyHeThong> memoryCache = new CopyOnWriteArrayList<>();

    public boolean ghiLog(String tenDangNhap, String vaiTro, String hanhDong, String chiTiet) {
        String u = (tenDangNhap != null && !tenDangNhap.trim().isEmpty()) ? tenDangNhap.trim() : "he_thong";
        String r = (vaiTro != null) ? vaiTro.trim() : "Hệ thống";
        String h = (hanhDong != null) ? hanhDong.trim() : "Thao tác";
        String c = (chiTiet != null) ? chiTiet.trim() : "";

        NhatKyHeThong log = new NhatKyHeThong(u, r, h, c);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            memoryCache.add(0, log);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.persist(log);
            tx.commit();
            memoryCache.add(0, log);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.WARNING, "Lỗi ghi nhật ký thao tác vào CSDL: " + e.getMessage());
            return false;
        }
    }

    public List<NhatKyHeThong> layTatCa() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                List<NhatKyHeThong> list = session.createQuery(
                        "FROM NhatKyHeThong ORDER BY thoiGian DESC", NhatKyHeThong.class).list();
                if (list != null && !list.isEmpty()) {
                    memoryCache.clear();
                    memoryCache.addAll(list);
                    return new ArrayList<>(list);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể tải nhật ký từ CSDL, dùng bộ nhớ đệm: {0}", e.getMessage());
            }
        }
        return new ArrayList<>(memoryCache);
    }

    public List<NhatKyHeThong> timTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return layTatCa();
        }
        String u = tenDangNhap.trim().toLowerCase();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                List<NhatKyHeThong> list = session.createQuery(
                        "FROM NhatKyHeThong WHERE LOWER(tenDangNhap) LIKE :user ORDER BY thoiGian DESC", NhatKyHeThong.class)
                        .setParameter("user", "%" + u + "%")
                        .list();
                if (list != null) {
                    return list;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi tìm nhật ký theo tên đăng nhập: {0}", e.getMessage());
            }
        }

        List<NhatKyHeThong> result = new ArrayList<>();
        for (NhatKyHeThong log : memoryCache) {
            if (log.getTenDangNhap() != null && log.getTenDangNhap().toLowerCase().contains(u)) {
                result.add(log);
            }
        }
        return result;
    }
}
