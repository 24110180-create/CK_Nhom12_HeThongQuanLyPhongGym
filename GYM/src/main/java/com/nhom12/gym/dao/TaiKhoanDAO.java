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

    public List<TaiKhoan> layTatCa() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "Không có kết nối CSDL khi lấy danh sách tài khoản");
            return Collections.emptyList();
        }
        try (Session session = sf.openSession()) {
            return session.createQuery("FROM TaiKhoan ORDER BY tenDangNhap ASC", TaiKhoan.class).list();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách tài khoản", e);
            return Collections.emptyList();
        }
    }

    public TaiKhoan timTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) return null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "Không có kết nối CSDL khi tìm tài khoản: {0}", tenDangNhap);
            return null;
        }
        try (Session session = sf.openSession()) {
            return session.get(TaiKhoan.class, tenDangNhap.trim());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm tài khoản '" + tenDangNhap + "'", e);
            return null;
        }
    }

    /**
     * Xác thực đăng nhập an toàn:
     * - Tìm tài khoản theo TenDangNhap
     * - Đối soát mã băm mật khẩu bằng PasswordUtil.checkPassword() (BCrypt / SHA-256)
     * - Tuyệt đối không truyền chuỗi mật khẩu thô vào mệnh đề WHERE của SQL/HQL
     * - Ném ngoại lệ khi mất kết nối CSDL để Service phân biệt với trường hợp gõ sai mật khẩu
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
            return null; // Tài khoản không tồn tại hoặc sai mật khẩu khi CSDL đang online
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi kết nối CSDL khi đăng nhập", e);
            throw e; // Ném ngoại lệ để tầng Service nhận biết sự cố kết nối CSDL
        }
    }

    public boolean them(TaiKhoan tk) {
        if (tk == null) return false;
        if (tk.getMatKhau() != null && !PasswordUtil.isHashed(tk.getMatKhau())) {
            tk.setMatKhau(PasswordUtil.hashPassword(tk.getMatKhau()));
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "Không có kết nối CSDL khi thêm tài khoản");
            return false;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.persist(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi thêm tài khoản '" + tk.getTenDangNhap() + "'", e);
            return false;
        }
    }

    public boolean capNhat(TaiKhoan tk) {
        if (tk == null) return false;
        if (tk.getMatKhau() != null && !PasswordUtil.isHashed(tk.getMatKhau())) {
            tk.setMatKhau(PasswordUtil.hashPassword(tk.getMatKhau()));
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "Không có kết nối CSDL khi cập nhật tài khoản");
            return false;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.merge(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật tài khoản '" + tk.getTenDangNhap() + "'", e);
            return false;
        }
    }

    public boolean xoa(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "Không có kết nối CSDL khi xóa tài khoản");
            return false;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            TaiKhoan tk = session.get(TaiKhoan.class, tenDangNhap.trim());
            if (tk != null) {
                session.remove(tk);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi xóa tài khoản '" + tenDangNhap + "'", e);
            return false;
        }
    }
}
