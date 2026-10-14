package com.nhom12.gym.util;

import com.nhom12.gym.entity.*;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Quản lý khởi tạo SessionFactory (Lazy Singleton Pattern với Thread-Safe)
 * - Tự động nạp cấu hình bảo mật từ file database.properties riêng biệt
 * - Hỗ trợ Connection Pool (HikariCP / built-in pool) chống leak connection
 * - Khi CSDL lỗi, trả về null an toàn cho cơ chế Offline Fallback Buffer
 */
public class HibernateUtil {
    private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class.getName());
    private static volatile SessionFactory sessionFactory;
    private static volatile boolean initAttempted = false;

    private static Properties loadDatabaseProperties() {
        Properties props = new Properties();
        // 1. Thử đọc từ database.properties trong classpath
        try (InputStream is = HibernateUtil.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (is != null) {
                props.load(is);
                return props;
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Không thể đọc database.properties: {0}", e.getMessage());
        }

        // 2. Thử đọc từ database.properties.example nếu chưa tạo database.properties
        try (InputStream is = HibernateUtil.class.getClassLoader().getResourceAsStream("database.properties.example")) {
            if (is != null) {
                props.load(is);
                return props;
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Không thể đọc database.properties.example: {0}", e.getMessage());
        }

        return props;
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory != null) {
            return sessionFactory;
        }
        if (initAttempted) {
            return null; // Đã thử khởi tạo trước đó và CSDL offline, trả về null tức thì để DAO dùng buffer không làm treo UI
        }
        initAttempted = true;
        try {
            Configuration cfg = new Configuration().configure();
            cfg.addAnnotatedClass(ChiNhanh.class);
            cfg.addAnnotatedClass(TaiKhoan.class);
            cfg.addAnnotatedClass(HoiVien.class);
            cfg.addAnnotatedClass(GoiTap.class);
            cfg.addAnnotatedClass(LopHoc.class);
            cfg.addAnnotatedClass(LichSuCheckIn.class);
            cfg.addAnnotatedClass(HoaDon.class);
            cfg.addAnnotatedClass(HoiVienVipDacQuyen.class);
            cfg.addAnnotatedClass(NhatKyHeThong.class);
            cfg.addAnnotatedClass(HuanLuyenVien.class);
            cfg.addAnnotatedClass(DangKyLop.class);

            // Nạp cấu hình từ database.properties (hoặc ghi đè từ biến môi trường)
            Properties dbProps = loadDatabaseProperties();
            String url = System.getenv("DB_URL");
            if (url == null) url = dbProps.getProperty("db.url");
            if (url != null && !url.trim().isEmpty()) {
                cfg.setProperty("hibernate.connection.url", url.trim());
            }

            String username = System.getenv("DB_USERNAME");
            if (username == null) username = dbProps.getProperty("db.username");
            if (username != null && !username.trim().isEmpty() && !username.contains("YOUR_")) {
                cfg.setProperty("hibernate.connection.username", username.trim());
            }

            String password = System.getenv("DB_PASSWORD");
            if (password == null) password = dbProps.getProperty("db.password");
            if (password != null && !password.trim().isEmpty() && !password.contains("YOUR_")) {
                cfg.setProperty("hibernate.connection.password", password.trim());
            }

            // Cấu hình Connection Pool (Tối ưu số lượng kết nối đồng thời và chống leak connection)
            String poolSize = dbProps.getProperty("hibernate.connection.pool_size");
            if (poolSize != null && !poolSize.trim().isEmpty()) {
                cfg.setProperty("hibernate.connection.pool_size", poolSize.trim());
            } else {
                cfg.setProperty("hibernate.connection.pool_size", "10");
            }

            sessionFactory = cfg.buildSessionFactory();
        } catch (Throwable ex) {
            LOGGER.log(Level.WARNING, "Không thể kết nối CSDL SQL Server (Hệ thống chuyển sang chế độ Offline Buffer cục bộ): {0}", ex.getMessage());
            return null; // Trả về null an toàn cho DAO fallback sang buffer
        }
        return sessionFactory;
    }

    public static synchronized void resetConnectionAttempt() {
        initAttempted = false;
    }

    /**
     * Kiểm tra trạng thái kết nối tới máy chủ CSDL
     */
    public static boolean isConnected() {
        return getSessionFactory() != null;
    }
}
