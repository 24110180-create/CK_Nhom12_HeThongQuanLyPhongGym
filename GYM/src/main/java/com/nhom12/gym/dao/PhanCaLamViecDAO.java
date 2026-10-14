package com.nhom12.gym.dao;

import com.nhom12.gym.entity.PhanCaLamViec;
import com.nhom12.gym.util.HibernateUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhanCaLamViecDAO {
    private static final Logger LOGGER = Logger.getLogger(PhanCaLamViecDAO.class.getName());
    private static final List<PhanCaLamViec> memoryAssignments = new ArrayList<>();

    public List<PhanCaLamViec> layTheoTuan(LocalDate ngayDauTuan, String chiNhanh) {
        if (ngayDauTuan == null || chiNhanh == null || chiNhanh.trim().isEmpty()) {
            return new ArrayList<>();
        }
        LocalDate ngayCuoiTuan = ngayDauTuan.plusDays(6);
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                return session.createQuery(
                                "FROM PhanCaLamViec p WHERE p.ngayLam BETWEEN :start AND :end "
                                        + "AND p.chiNhanh = :branch ORDER BY p.ngayLam, p.caLam, p.tenNhanVien",
                                PhanCaLamViec.class)
                        .setParameter("start", ngayDauTuan)
                        .setParameter("end", ngayCuoiTuan)
                        .setParameter("branch", chiNhanh)
                        .list();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể tải lịch phân ca từ CSDL; dùng dữ liệu tạm trong phiên.", e);
            }
        }
        synchronized (memoryAssignments) {
            List<PhanCaLamViec> result = new ArrayList<>();
            for (PhanCaLamViec assignment : memoryAssignments) {
                if (chiNhanh.equals(assignment.getChiNhanh())
                        && !assignment.getNgayLam().isBefore(ngayDauTuan)
                        && !assignment.getNgayLam().isAfter(ngayCuoiTuan)) {
                    result.add(assignment);
                }
            }
            return result;
        }
    }

    public boolean thayTheTuan(LocalDate ngayDauTuan, String chiNhanh, List<PhanCaLamViec> assignments) {
        if (!hopLe(ngayDauTuan, chiNhanh, assignments)) {
            return false;
        }
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            synchronized (memoryAssignments) {
                memoryAssignments.removeIf(item -> chiNhanh.equals(item.getChiNhanh())
                        && !item.getNgayLam().isBefore(ngayDauTuan)
                        && !item.getNgayLam().isAfter(ngayDauTuan.plusDays(6)));
                memoryAssignments.addAll(assignments);
            }
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            List<PhanCaLamViec> conflicts = session.createQuery(
                            "FROM PhanCaLamViec p WHERE p.ngayLam BETWEEN :start AND :end "
                                    + "AND p.chiNhanh <> :branch",
                            PhanCaLamViec.class)
                    .setParameter("start", ngayDauTuan)
                    .setParameter("end", ngayDauTuan.plusDays(6))
                    .setParameter("branch", chiNhanh)
                    .list();
            if (coTrungCaKhacChiNhanh(assignments, conflicts)) {
                tx.rollback();
                return false;
            }
            session.createMutationQuery(
                            "DELETE FROM PhanCaLamViec p WHERE p.ngayLam BETWEEN :start AND :end "
                                    + "AND p.chiNhanh = :branch")
                    .setParameter("start", ngayDauTuan)
                    .setParameter("end", ngayDauTuan.plusDays(6))
                    .setParameter("branch", chiNhanh)
                    .executeUpdate();
            assignments.forEach(session::persist);
            tx.commit();
            synchronized (memoryAssignments) {
                memoryAssignments.removeIf(item -> chiNhanh.equals(item.getChiNhanh())
                        && !item.getNgayLam().isBefore(ngayDauTuan)
                        && !item.getNgayLam().isAfter(ngayDauTuan.plusDays(6)));
                memoryAssignments.addAll(assignments);
            }
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Không thể lưu lịch phân ca tuần bắt đầu " + ngayDauTuan, e);
            return false;
        }
    }

    static boolean hopLe(LocalDate ngayDauTuan, String chiNhanh, List<PhanCaLamViec> assignments) {
        if (ngayDauTuan == null || chiNhanh == null || chiNhanh.trim().isEmpty() || assignments == null) {
            return false;
        }
        LocalDate end = ngayDauTuan.plusDays(6);
        for (int i = 0; i < assignments.size(); i++) {
            PhanCaLamViec item = assignments.get(i);
            if (item == null || item.getNgayLam() == null || item.getMaNhanVien() == null
                    || item.getMaNhanVien().trim().isEmpty() || item.getTenNhanVien() == null
                    || item.getTenNhanVien().trim().isEmpty() || item.getVaiTro() == null
                    || !("Lễ tân".equalsIgnoreCase(item.getVaiTro()) || "HLV".equalsIgnoreCase(item.getVaiTro()))
                    || item.getChiNhanh() == null || !chiNhanh.equals(item.getChiNhanh())
                    || item.getNgayLam().isBefore(ngayDauTuan) || item.getNgayLam().isAfter(end)
                    || !("SANG".equals(item.getCaLam()) || "CHIEU".equals(item.getCaLam()))) {
                return false;
            }
            for (int j = 0; j < i; j++) {
                PhanCaLamViec previous = assignments.get(j);
                if (sameSlot(item, previous)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean coTrungCaKhacChiNhanh(List<PhanCaLamViec> proposed, List<PhanCaLamViec> existing) {
        for (PhanCaLamViec item : proposed) {
            for (PhanCaLamViec other : existing) {
                if (sameSlot(item, other)) {
                    return true;
                }
            }
        }
        synchronized (memoryAssignments) {
            for (PhanCaLamViec item : proposed) {
                for (PhanCaLamViec other : memoryAssignments) {
                    if (!item.getChiNhanh().equals(other.getChiNhanh()) && sameSlot(item, other)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean sameSlot(PhanCaLamViec left, PhanCaLamViec right) {
        return left.getNgayLam().equals(right.getNgayLam())
                && left.getCaLam().equals(right.getCaLam())
                && left.getMaNhanVien().equalsIgnoreCase(right.getMaNhanVien());
    }
}
