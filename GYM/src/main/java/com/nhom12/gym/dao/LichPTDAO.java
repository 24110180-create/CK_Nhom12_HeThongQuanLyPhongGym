package com.nhom12.gym.dao;

import com.nhom12.gym.entity.LichPT;
import com.nhom12.gym.util.HibernateUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class LichPTDAO {
    private static final Logger LOGGER = Logger.getLogger(LichPTDAO.class.getName());
    private static final List<LichPT> memorySchedules = new CopyOnWriteArrayList<>();
    private static long nextMemoryId = 1;

    public List<LichPT> layTheoTuan(LocalDate ngayDauTuan, String chiNhanh, String maHLV) {
        if (ngayDauTuan == null) {
            return new ArrayList<>();
        }
        LocalDateTime start = ngayDauTuan.atStartOfDay();
        LocalDateTime end = ngayDauTuan.plusDays(7).atStartOfDay();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                StringBuilder hql = new StringBuilder("FROM LichPT p WHERE p.batDau >= :start AND p.batDau < :end");
                if (chiNhanh != null) hql.append(" AND p.chiNhanh = :branch");
                if (maHLV != null) hql.append(" AND p.maHLV = :trainer");
                hql.append(" ORDER BY p.batDau");
                var query = session.createQuery(hql.toString(), LichPT.class);
                query.setParameter("start", start);
                query.setParameter("end", end);
                if (chiNhanh != null) query.setParameter("branch", chiNhanh);
                if (maHLV != null) query.setParameter("trainer", maHLV);
                return query.list();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể tải lịch PT từ CSDL; dùng dữ liệu tạm trong phiên.", e);
            }
        }
        List<LichPT> result = new ArrayList<>();
        for (LichPT schedule : memorySchedules) {
            if (!schedule.getBatDau().isBefore(start) && schedule.getBatDau().isBefore(end)
                    && (chiNhanh == null || chiNhanh.equals(schedule.getChiNhanh()))
                    && (maHLV == null || maHLV.equalsIgnoreCase(schedule.getMaHLV()))) {
                result.add(schedule);
            }
        }
        result.sort(Comparator.comparing(LichPT::getBatDau));
        return result;
    }

    public boolean them(LichPT schedule) {
        if (!hopLe(schedule)) {
            return false;
        }
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            if (coXungDot(schedule, memorySchedules)) {
                return false;
            }
            synchronized (LichPTDAO.class) {
                schedule.setId(nextMemoryId++);
                memorySchedules.add(schedule);
            }
            return true;
        }
        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            List<LichPT> sameDay = session.createQuery(
                            "FROM LichPT p WHERE p.batDau >= :start AND p.batDau < :end "
                                    + "AND p.trangThai <> :cancelled",
                            LichPT.class)
                    .setParameter("start", schedule.getBatDau().toLocalDate().atStartOfDay())
                    .setParameter("end", schedule.getBatDau().toLocalDate().plusDays(1).atStartOfDay())
                    .setParameter("cancelled", LichPT.DA_HUY)
                    .list();
            if (coXungDot(schedule, sameDay) || coXungDot(schedule, memorySchedules)) {
                tx.rollback();
                return false;
            }
            session.persist(schedule);
            tx.commit();
            memorySchedules.add(schedule);
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Không thể lưu lịch PT của hội viên " + schedule.getMaHoiVien(), e);
            return false;
        }
    }

    public boolean capNhatTrangThai(long id, String trangThai, String maHLV, boolean coQuyenQuanLy) {
        if (!(LichPT.HOAN_THANH.equals(trangThai) || LichPT.DA_HUY.equals(trangThai))) {
            return false;
        }
        if (coQuyenQuanLy && !LichPT.DA_HUY.equals(trangThai)) {
            return false;
        }
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            for (LichPT schedule : memorySchedules) {
                if (schedule.getId() != null && schedule.getId() == id
                        && LichPT.DA_DAT.equals(schedule.getTrangThai())
                        && duocCapNhat(schedule, maHLV, coQuyenQuanLy)) {
                    schedule.setTrangThai(trangThai);
                    return true;
                }
            }
            return false;
        }
        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            LichPT schedule = session.get(LichPT.class, id);
            if (schedule == null || !duocCapNhat(schedule, maHLV, coQuyenQuanLy)
                    || !LichPT.DA_DAT.equals(schedule.getTrangThai())) {
                tx.rollback();
                return false;
            }
            schedule.setTrangThai(trangThai);
            tx.commit();
            for (LichPT memorySchedule : memorySchedules) {
                if (memorySchedule.getId() != null && memorySchedule.getId() == id) {
                    memorySchedule.setTrangThai(trangThai);
                }
            }
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Không thể cập nhật trạng thái lịch PT ID " + id, e);
            return false;
        }
    }

    public static boolean hopLe(LichPT schedule) {
        if (schedule == null || schedule.getMaHoiVien() <= 0 || isBlank(schedule.getTenHoiVien())
                || isBlank(schedule.getMaHLV()) || isBlank(schedule.getTenHLV())
                || isBlank(schedule.getChiNhanh()) || schedule.getBatDau() == null
                || !LichPT.DA_DAT.equals(schedule.getTrangThai())
                || (schedule.getGhiChu() != null && schedule.getGhiChu().length() > 500)
                || schedule.getThoiLuongPhut() < 30 || schedule.getThoiLuongPhut() > 90
                || schedule.getThoiLuongPhut() % 30 != 0) {
            return false;
        }
        LocalDateTime start = schedule.getBatDau();
        LocalDateTime end = start.plusMinutes(schedule.getThoiLuongPhut());
        return start.getHour() >= 6 && (end.getHour() < 22 || (end.getHour() == 22 && end.getMinute() == 0))
                && start.getMinute() % 30 == 0;
    }

    static boolean coXungDot(LichPT candidate, List<LichPT> existingSchedules) {
        if (candidate == null || candidate.getBatDau() == null) {
            return true;
        }
        LocalDateTime candidateEnd = candidate.getBatDau().plusMinutes(candidate.getThoiLuongPhut());
        for (LichPT existing : existingSchedules) {
            if (existing == null || LichPT.DA_HUY.equals(existing.getTrangThai())
                    || (candidate.getId() != null && candidate.getId().equals(existing.getId()))) {
                continue;
            }
            if (!existing.getBatDau().toLocalDate().equals(candidate.getBatDau().toLocalDate())) {
                continue;
            }
            boolean sameMember = existing.getMaHoiVien() == candidate.getMaHoiVien();
            boolean sameTrainer = existing.getMaHLV().equalsIgnoreCase(candidate.getMaHLV());
            if (!sameMember && !sameTrainer) {
                continue;
            }
            LocalDateTime existingEnd = existing.getBatDau().plusMinutes(existing.getThoiLuongPhut());
            if (candidate.getBatDau().isBefore(existingEnd) && existing.getBatDau().isBefore(candidateEnd)) {
                return true;
            }
        }
        return false;
    }

    private boolean duocCapNhat(LichPT schedule, String maHLV, boolean coQuyenQuanLy) {
        return coQuyenQuanLy || (maHLV != null && maHLV.equalsIgnoreCase(schedule.getMaHLV()));
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
