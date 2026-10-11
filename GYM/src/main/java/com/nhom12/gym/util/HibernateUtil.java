package com.nhom12.gym.util;

import com.nhom12.gym.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Quản lý khởi tạo SessionFactory (Lazy Singleton Pattern với Thread-Safe)
 * Khi CSDL lỗi, trả về null an toàn mà không ném ExceptionInInitializerError,
 * cho phép các DAO kích hoạt cơ chế Offline Fallback Buffer trơn tru.
 */
public class HibernateUtil {
    private static volatile SessionFactory sessionFactory;

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
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

                sessionFactory = cfg.buildSessionFactory();
            } catch (Throwable ex) {
                System.err.println("[HibernateUtil] Không thể kết nối CSDL SQL Server: " + ex.getMessage());
                return null; // Để DAO tự kiểm tra null và fallback sang bộ nhớ đệm, không ném Error phá vỡ JVM
            }
        }
        return sessionFactory;
    }

    /**
     * Kiểm tra trạng thái kết nối tới máy chủ CSDL
     */
    public static boolean isConnected() {
        return getSessionFactory() != null;
    }
}
