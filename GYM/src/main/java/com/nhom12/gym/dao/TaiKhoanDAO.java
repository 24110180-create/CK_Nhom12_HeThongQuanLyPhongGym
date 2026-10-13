package com.nhom12.gym.dao;

import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.HibernateUtil;
import com.nhom12.gym.util.PasswordUtil;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Data Access Object cho Thực thể Tài Khoản (TaiKhoan)
 * Quản lý xác thực an toàn chuẩn bảo mật BCrypt & SHA-256 (không so sánh thô trong SQL)
 */
public class TaiKhoanDAO {
    private static final Logger LOGGER = Logger.getLogger(TaiKhoanDAO.class.getName());

    // Bộ nhớ đệm tài khoản dự phòng đa luồng an toàn (Memory Fallback Buffer)
    private static final List<TaiKhoan> memoryCache = new java.util.concurrent.CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryCache.isEmpty()) return;
        String hashed123456 = PasswordUtil.hashPassword("123456");
        memoryCache.add(new TaiKhoan("admin", hashed123456, "Chủ", "Võ Anh Đức", "Toàn hệ thống"));
        memoryCache.add(new TaiKhoan("letan1", hashed123456, "Lễ tân", "Nguyễn Thị Linh", "CN01 - Cơ sở 1 (Quận 1)"));
        memoryCache.add(new TaiKhoan("letan2", hashed123456, "Lễ tân", "Trần Thị Mai", "CN02 - Cơ sở 2 (Tân Bình)"));
        memoryCache.add(new TaiKhoan("hlv1", hashed123456, "HLV", "Trần Tuấn Kiệt", "CN01 - Cơ sở 1 (Quận 1)"));
        memoryCache.add(new TaiKhoan("hlv2", hashed123456, "HLV", "Phạm Quốc Bảo", "CN02 - Cơ sở 2 (Tân Bình)"));
        memoryCache.add(new TaiKhoan("hlv3", hashed123456, "HLV", "Nguyễn Thu Thảo", "CN01 - Cơ sở 1 (Quận 1)"));
        memoryCache.add(new TaiKhoan("quanly1", hashed123456, "Quản lý", "Lê Thanh Sơn", "CN02 - Cơ sở 2 (Tân Bình)"));
        memoryCache.add(new TaiKhoan("hoivien1", hashed123456, "Hội viên", "Lê Anh Duy", "CN01 - Cơ sở 1 (Quận 1)"));
        memoryCache.add(new TaiKhoan("hoivien2", hashed123456, "Hội viên", "Đặng Thu Thảo", "CN02 - Cơ sở 2 (Tân Bình)"));
    }

    public List<TaiKhoan> layTatCa() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                List<TaiKhoan> list = session.createQuery("FROM TaiKhoan ORDER BY tenDangNhap ASC", TaiKhoan.class).list();
                if (list != null && !list.isEmpty()) {
                    for (TaiKhoan dbTk : list) {
                        memoryCache.removeIf(item -> item.getTenDangNhap().equalsIgnoreCase(dbTk.getTenDangNhap()));
                        memoryCache.add(dbTk);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi lấy danh sách tài khoản từ CSDL, dùng bộ nhớ đệm: {0}", e.getMessage());
            }
        }
        khoiTaoDuLieuMauNeuTrong();
        return new java.util.ArrayList<>(memoryCache);
    }

    public TaiKhoan timTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) return null;
        String q = tenDangNhap.trim();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                TaiKhoan tk = session.get(TaiKhoan.class, q);
                if (tk != null) return tk;
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi tìm tài khoản qua CSDL: {0}", e.getMessage());
            }
        }
        for (TaiKhoan tk : memoryCache) {
            if (tk.getTenDangNhap().equalsIgnoreCase(q)) return tk;
        }
        return null;
    }

    /**
     * Xác thực đăng nhập an toàn:
     * - Tìm tài khoản theo TenDangNhap trong CSDL và bộ nhớ đệm
     * - Đối soát mã băm mật khẩu bằng PasswordUtil.checkPassword() (BCrypt / SHA-256)
     * - Tự động đồng bộ vào CSDL nếu tài khoản hợp lệ từ memoryCache
     */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) throws Exception {
        if (tenDangNhap == null || matKhau == null) return null;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "Mất kết nối CSDL khi đăng nhập tài khoản: {0}", tenDangNhap);
            throw new SQLException("Không thể kết nối đến máy chủ CSDL SQL Server (Offline Mode)");
        }

        try (Session session = sf.openSession()) {
            TaiKhoan tk = session.get(TaiKhoan.class, tenDangNhap.trim());

            // Nếu CSDL chưa có tài khoản (ví dụ quanly1), tìm trong memoryCache
            if (tk == null) {
                for (TaiKhoan mem : memoryCache) {
                    if (mem.getTenDangNhap().equalsIgnoreCase(tenDangNhap.trim())) {
                        tk = mem;
                        // Tự động persist vào CSDL để lưu trữ bền vững
                        Transaction tx = null;
                        try {
                            tx = session.beginTransaction();
                            session.persist(new TaiKhoan(mem.getTenDangNhap(), mem.getMatKhau(), mem.getVaiTro(), mem.getHoTen(), mem.getChiNhanh()));
                            tx.commit();
                        } catch (Exception ex) {
                            if (tx != null) tx.rollback();
                        }
                        break;
                    }
                }
            }

            if (tk != null) {
                // Kiểm tra mật khẩu (đối soát BCrypt / SHA-256 an toàn)
                if (PasswordUtil.checkPassword(matKhau, tk.getMatKhau())) {
                    // Nếu tài khoản cũ chưa băm BCrypt, tự động nâng cấp mã băm BCrypt trong CSDL
                    String storedHash = tk.getMatKhau();
                    if (storedHash == null || (!storedHash.startsWith("$2a$") && !storedHash.startsWith("$2b$") && !storedHash.startsWith("$2y$"))) {
                        tk.setMatKhau(PasswordUtil.hashPassword(matKhau));
                        capNhat(tk);
                    }
                    return tk;
                }
            }
            return null; // Tài khoản không tồn tại hoặc sai mật khẩu
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi kết nối CSDL khi đăng nhập", e);
            throw e;
        }
    }

    public boolean them(TaiKhoan tk) {
        if (tk == null) return false;
        if (tk.getMatKhau() != null && !PasswordUtil.isHashed(tk.getMatKhau())) {
            tk.setMatKhau(PasswordUtil.hashPassword(tk.getMatKhau()));
        }

        memoryCache.removeIf(item -> item.getTenDangNhap().equalsIgnoreCase(tk.getTenDangNhap()));
        memoryCache.add(tk);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.persist(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.WARNING, "Không thể lưu tài khoản vào CSDL, đã lưu vào bộ nhớ đệm: {0}", e.getMessage());
            return true; // Vẫn trả về true vì đã lưu thành công vào memoryCache
        }
    }

    public boolean capNhat(TaiKhoan tk) {
        if (tk == null) return false;
        if (tk.getMatKhau() != null && !PasswordUtil.isHashed(tk.getMatKhau())) {
            tk.setMatKhau(PasswordUtil.hashPassword(tk.getMatKhau()));
        }

        for (int i = 0; i < memoryCache.size(); i++) {
            if (memoryCache.get(i).getTenDangNhap().equalsIgnoreCase(tk.getTenDangNhap())) {
                memoryCache.set(i, tk);
                break;
            }
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.merge(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.WARNING, "Lỗi cập nhật CSDL, đã cập nhật bộ nhớ đệm: {0}", e.getMessage());
            return true;
        }
    }

    public boolean xoa(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) return false;
        String q = tenDangNhap.trim();
        memoryCache.removeIf(item -> item.getTenDangNhap().equalsIgnoreCase(q));

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            TaiKhoan tk = session.get(TaiKhoan.class, q);
            if (tk != null) {
                session.remove(tk);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi xóa tài khoản '" + q + "'", e);
            return false;
        }
    }
}
