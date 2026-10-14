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
    }

    public List<TaiKhoan> layTatCa() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                List<TaiKhoan> list = session.createQuery("FROM TaiKhoan ORDER BY tenDangNhap ASC", TaiKhoan.class).list();
                memoryCache.clear();
                if (list != null) memoryCache.addAll(list);
                return list == null ? Collections.emptyList() : new java.util.ArrayList<>(list);
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
                return session.get(TaiKhoan.class, q);
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
     * - Tìm tài khoản theo TenDangNhap trong CSDL
     * - Đối soát mã băm mật khẩu bằng PasswordUtil.checkPassword() (BCrypt / SHA-256)
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
                if (laTaiKhoanHoatDong(tk) && PasswordUtil.checkPassword(matKhau, tk.getMatKhau())) {
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
        if (!duLieuTaiKhoanHopLe(tk)) return false;
        if (tk.getMatKhau() != null && !PasswordUtil.isHashed(tk.getMatKhau())) {
            tk.setMatKhau(PasswordUtil.hashPassword(tk.getMatKhau()));
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            TaiKhoan existing = timTrongBoNho(tk.getTenDangNhap());
            if (existing != null) return false;
            memoryCache.add(tk);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            if (session.get(TaiKhoan.class, tk.getTenDangNhap()) != null) {
                tx.rollback();
                return false;
            }
            session.persist(tk);
            tx.commit();
            memoryCache.removeIf(item -> item.getTenDangNhap().equalsIgnoreCase(tk.getTenDangNhap()));
            memoryCache.add(tk);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Không thể lưu tài khoản vào CSDL: " + tk.getTenDangNhap(), e);
            return false;
        }
    }

    public boolean capNhat(TaiKhoan tk) {
        if (!duLieuTaiKhoanHopLe(tk)) return false;
        if (tk.getMatKhau() != null && !PasswordUtil.isHashed(tk.getMatKhau())) {
            tk.setMatKhau(PasswordUtil.hashPassword(tk.getMatKhau()));
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            TaiKhoan existing = timTrongBoNho(tk.getTenDangNhap());
            if (existing == null) return false;
            memoryCache.removeIf(item -> item.getTenDangNhap().equalsIgnoreCase(tk.getTenDangNhap()));
            memoryCache.add(tk);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.merge(tk);
            tx.commit();
            memoryCache.removeIf(item -> item.getTenDangNhap().equalsIgnoreCase(tk.getTenDangNhap()));
            memoryCache.add(tk);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật tài khoản trong CSDL: " + tk.getTenDangNhap(), e);
            return false;
        }
    }

    public boolean capNhatMatKhau(String tenDangNhap, String matKhauMoi) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()
                || matKhauMoi == null || matKhauMoi.length() < 8) {
            return false;
        }
        TaiKhoan tk = timTheoTenDangNhap(tenDangNhap);
        if (tk == null) return false;
        tk.setMatKhau(PasswordUtil.hashPassword(matKhauMoi));
        return capNhat(tk);
    }

    public boolean doiTrangThai(String tenDangNhap, String trangThai) {
        if (!"Đang hoạt động".equals(trangThai) && !"Tạm khóa".equals(trangThai)) return false;
        TaiKhoan tk = timTheoTenDangNhap(tenDangNhap);
        if (tk == null) return false;
        tk.setTrangThaiTaiKhoan(trangThai);
        return capNhat(tk);
    }

    private TaiKhoan timTrongBoNho(String tenDangNhap) {
        for (TaiKhoan item : memoryCache) {
            if (item.getTenDangNhap().equalsIgnoreCase(tenDangNhap)) return item;
        }
        return null;
    }

    private boolean laTaiKhoanHoatDong(TaiKhoan tk) {
        return tk != null && "Đang hoạt động".equalsIgnoreCase(tk.getTrangThaiTaiKhoan());
    }

    private boolean duLieuTaiKhoanHopLe(TaiKhoan tk) {
        return tk != null && tk.getTenDangNhap() != null && !tk.getTenDangNhap().trim().isEmpty()
                && tk.getMatKhau() != null && !tk.getMatKhau().isEmpty()
                && tk.getVaiTro() != null && !tk.getVaiTro().trim().isEmpty()
                && tk.getHoTen() != null && !tk.getHoTen().trim().isEmpty()
                && ("Đang hoạt động".equals(tk.getTrangThaiTaiKhoan())
                || "Tạm khóa".equals(tk.getTrangThaiTaiKhoan()));
    }

    public boolean xoa(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) return false;
        String q = tenDangNhap.trim();
        boolean removedFromMemory = memoryCache.removeIf(item -> item.getTenDangNhap().equalsIgnoreCase(q));

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            if (removedFromMemory) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
            }
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            TaiKhoan tk = session.get(TaiKhoan.class, q);
            if (tk != null) {
                session.remove(tk);
                tx.commit();
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
                return true;
            }
            if (removedFromMemory) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi xóa tài khoản '" + q + "'", e);
            if (removedFromMemory) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.NHAN_SU);
                return true;
            }
            return false;
        }
    }
}
