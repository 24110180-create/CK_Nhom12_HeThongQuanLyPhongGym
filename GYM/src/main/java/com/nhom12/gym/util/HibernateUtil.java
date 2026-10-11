package com.nhom12.gym.util;

import com.nhom12.gym.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Quản lý khởi tạo duy nhất SessionFactory (Singleton Pattern)
 * Đăng ký đầy đủ 8 thực thể JPA đảm bảo không gặp lỗi 'Unable to locate persister'
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration cfg = new Configuration().configure();
            cfg.addAnnotatedClass(TaiKhoan.class);
            cfg.addAnnotatedClass(HoiVien.class);
            cfg.addAnnotatedClass(GoiTap.class);
            cfg.addAnnotatedClass(LopHoc.class);
            cfg.addAnnotatedClass(LichSuCheckIn.class);
            cfg.addAnnotatedClass(HoaDon.class);
            cfg.addAnnotatedClass(HuanLuyenVien.class);
            cfg.addAnnotatedClass(DangKyLop.class);
            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("[HibernateUtil] Lỗi khởi tạo SessionFactory: " + ex.getMessage());
            return null; // Trả về null an toàn để các DAO tự động kích hoạt bộ đệm dự phòng
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
