package com.nhom12.gym.dao;

import com.nhom12.gym.entity.ChiNhanh;
import com.nhom12.gym.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Data Access Object cho Thực thể Chi Nhánh (ChiNhanhDAO)
 * Quản lý danh mục các cơ sở trong chuỗi FitLife Gym, hỗ trợ cơ chế Bộ nhớ đệm kép (Dual Cache)
 */
public class ChiNhanhDAO {

    private static final Logger LOGGER = Logger.getLogger(ChiNhanhDAO.class.getName());
    private static final List<ChiNhanh> memoryCache = new CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryCache.isEmpty()) return;
        memoryCache.add(new ChiNhanh(
                "CN01",
                "FitLife Flagship - Quận 1",
                "123 Nguyễn Thị Minh Khai, P. Bến Thành, Quận 1, TP.HCM",
                "028 3822 1234",
                "Lê Thanh Sơn",
                180,
                "Đang hoạt động"
        ));
        memoryCache.add(new ChiNhanh(
                "CN02",
                "FitLife Premium - Tân Bình",
                "45 Hoàng Hoa Thám, Phường 13, Quận Tân Bình, TP.HCM",
                "028 3810 5678",
                "Phan Hoàng Long",
                120,
                "Đang hoạt động"
        ));
    }

    public List<ChiNhanh> layTatCa() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            return new ArrayList<>(memoryCache);
        }

        try (Session session = sf.openSession()) {
            List<ChiNhanh> list = session.createQuery("FROM ChiNhanh ORDER BY maChiNhanh ASC", ChiNhanh.class).list();
            if (list != null && !list.isEmpty()) {
                memoryCache.clear();
                memoryCache.addAll(list);
                return list;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "CSDL chưa sẵn sàng, sử dụng bộ nhớ đệm chi nhánh: {0}", e.getMessage());
        }
        return new ArrayList<>(memoryCache);
    }

    public ChiNhanh timTheoMa(String maChiNhanh) {
        if (maChiNhanh == null || maChiNhanh.trim().isEmpty()) return null;
        String code = maChiNhanh.trim();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                ChiNhanh cn = session.get(ChiNhanh.class, code);
                if (cn != null) return cn;
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi tìm chi nhánh theo mã: {0}", e.getMessage());
            }
        }

        for (ChiNhanh cn : memoryCache) {
            if (cn.getMaChiNhanh().equalsIgnoreCase(code) || code.contains(cn.getMaChiNhanh())) {
                return cn;
            }
        }
        return null;
    }

    public boolean luuHoacCapNhat(ChiNhanh cn) {
        if (cn == null) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            memoryCache.removeIf(item -> item.getMaChiNhanh().equalsIgnoreCase(cn.getMaChiNhanh()));
            memoryCache.add(cn);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.ALL);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.merge(cn);
            tx.commit();

            memoryCache.removeIf(item -> item.getMaChiNhanh().equalsIgnoreCase(cn.getMaChiNhanh()));
            memoryCache.add(cn);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.ALL);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi lưu chi nhánh vào CSDL", e);
            return false;
        }
    }
}
