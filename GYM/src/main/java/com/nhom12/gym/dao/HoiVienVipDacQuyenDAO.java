package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HoiVienVipDacQuyen;
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
 * Data Access Object cho Hội viên VIP đặc quyền (HoiVienVipDacQuyenDAO)
 * Quản lý lưu trữ CSDL bền vững và hỗ trợ bộ đệm ngoại tuyến (Offline Fallback).
 */
public class HoiVienVipDacQuyenDAO {

    private static final Logger LOGGER = Logger.getLogger(HoiVienVipDacQuyenDAO.class.getName());
    private static final List<HoiVienVipDacQuyen> memoryCache = new CopyOnWriteArrayList<>();

    public List<HoiVienVipDacQuyen> layTatCa() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                List<HoiVienVipDacQuyen> list = session.createQuery(
                        "FROM HoiVienVipDacQuyen ORDER BY maThe ASC", HoiVienVipDacQuyen.class).list();
                if (list != null && !list.isEmpty()) {
                    memoryCache.clear();
                    memoryCache.addAll(list);
                    return new ArrayList<>(list);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể tải danh sách VIP từ CSDL, dùng bộ nhớ đệm: {0}", e.getMessage());
            }
        }
        return new ArrayList<>(memoryCache);
    }

    public boolean them(HoiVienVipDacQuyen vip) {
        if (vip == null || vip.getMaThe() == null || vip.getMaThe().trim().isEmpty()) {
            return false;
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            // Chế độ ngoại tuyến: lưu tạm vào bộ nhớ đệm
            memoryCache.removeIf(v -> v.getMaThe().equalsIgnoreCase(vip.getMaThe())
                    || (vip.getSoDienThoai() != null && v.getSoDienThoai() != null && v.getSoDienThoai().equalsIgnoreCase(vip.getSoDienThoai())));
            memoryCache.add(vip);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.merge(vip);
            tx.commit();

            memoryCache.removeIf(v -> v.getMaThe().equalsIgnoreCase(vip.getMaThe())
                    || (vip.getSoDienThoai() != null && v.getSoDienThoai() != null && v.getSoDienThoai().equalsIgnoreCase(vip.getSoDienThoai())));
            memoryCache.add(vip);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi thêm hội viên VIP vào CSDL: " + vip.getMaThe(), e);
            return false;
        }
    }

    public boolean xoa(String maTheHoacSdt) {
        if (maTheHoacSdt == null || maTheHoacSdt.trim().isEmpty()) {
            return false;
        }
        String key = maTheHoacSdt.trim();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            // Chế độ ngoại tuyến: xóa khỏi bộ nhớ đệm
            return memoryCache.removeIf(v -> v.getMaThe().equalsIgnoreCase(key)
                    || (v.getSoDienThoai() != null && v.getSoDienThoai().equalsIgnoreCase(key)));
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            HoiVienVipDacQuyen vip = session.get(HoiVienVipDacQuyen.class, key);
            if (vip == null) {
                List<HoiVienVipDacQuyen> list = session.createQuery(
                        "FROM HoiVienVipDacQuyen WHERE soDienThoai = :phone", HoiVienVipDacQuyen.class)
                        .setParameter("phone", key).list();
                if (list != null && !list.isEmpty()) {
                    vip = list.get(0);
                }
            }
            if (vip != null) {
                session.remove(vip);
                tx.commit();
                memoryCache.removeIf(v -> v.getMaThe().equalsIgnoreCase(key)
                        || (v.getSoDienThoai() != null && v.getSoDienThoai().equalsIgnoreCase(key)));
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi xóa hội viên VIP khỏi CSDL: " + key, e);
            return false;
        }
    }
}
