package com.nhom12.gym.dao;

import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.HibernateUtil;
import com.nhom12.gym.util.PasswordUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object cho Thực thể Tài Khoản (TaiKhoan)
 * Quản lý xác thực an toàn chuẩn bảo mật SHA-256 (không truyền plaintext vào SQL)
 */
public class TaiKhoanDAO {

    public List<TaiKhoan> layTatCa() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM TaiKhoan ORDER BY tenDangNhap ASC", TaiKhoan.class).list();
        } catch (Exception e) {
            System.err.println("[TaiKhoanDAO] Lỗi lấy danh sách tài khoản: " + e.getMessage());
            return null;
        }
    }

    public TaiKhoan timTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TaiKhoan.class, tenDangNhap.trim());
        } catch (Exception e) {
            System.err.println("[TaiKhoanDAO] Lỗi tìm tài khoản '" + tenDangNhap + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Xác thực đăng nhập an toàn:
     * - Tìm tài khoản theo TenDangNhap
     * - Đối soát mã băm mật khẩu bằng PasswordUtil.checkPassword()
     * - Tuyệt đối không truyền chuỗi mật khẩu thô vào mệnh đề WHERE của SQL/HQL
     * - Ném ngoại lệ khi mất kết nối CSDL để Service phân biệt với trường hợp gõ sai mật khẩu
     */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) throws Exception {
        if (tenDangNhap == null || matKhau == null) return null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TaiKhoan tk = session.get(TaiKhoan.class, tenDangNhap.trim());
            if (tk != null) {
                // Kiểm tra mật khẩu (so khớp SHA-256 an toàn)
                if (PasswordUtil.checkPassword(matKhau, tk.getMatKhau())) {
                    // Nếu tài khoản cũ chưa băm SHA-256, tự động nâng cấp băm trong CSDL
                    if (!PasswordUtil.isHashed(tk.getMatKhau())) {
                        tk.setMatKhau(PasswordUtil.hashPassword(matKhau));
                        capNhat(tk);
                    }
                    return tk;
                }
            }
            return null; // Tài khoản không tồn tại hoặc sai mật khẩu khi CSDL đang online
        } catch (Exception e) {
            System.err.println("[TaiKhoanDAO] Mất kết nối CSDL khi đăng nhập: " + e.getMessage());
            throw e; // Ném ngoại lệ để tầng Service nhận biết sự cố kết nối CSDL
        }
    }

    public boolean them(TaiKhoan tk) {
        if (tk == null) return false;
        if (tk.getMatKhau() != null && !PasswordUtil.isHashed(tk.getMatKhau())) {
            tk.setMatKhau(PasswordUtil.hashPassword(tk.getMatKhau()));
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[TaiKhoanDAO] Lỗi thêm tài khoản: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhat(TaiKhoan tk) {
        if (tk == null) return false;
        if (tk.getMatKhau() != null && !PasswordUtil.isHashed(tk.getMatKhau())) {
            tk.setMatKhau(PasswordUtil.hashPassword(tk.getMatKhau()));
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(tk);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[TaiKhoanDAO] Lỗi cập nhật tài khoản: " + e.getMessage());
            return false;
        }
    }

    public boolean xoa(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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
            System.err.println("[TaiKhoanDAO] Lỗi xóa tài khoản '" + tenDangNhap + "': " + e.getMessage());
            return false;
        }
    }
}
