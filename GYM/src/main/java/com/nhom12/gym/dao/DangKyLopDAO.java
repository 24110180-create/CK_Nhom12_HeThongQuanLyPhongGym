package com.nhom12.gym.dao;

import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Data Access Object cho Thực thể Đăng Ký & Điểm Danh Lớp Học (DangKyLop)
 * Sử dụng Hibernate ORM lưu trữ bền vững xuống bảng DangKyLop trong CSDL
 */
public class DangKyLopDAO {

    private static final Logger LOGGER = Logger.getLogger(DangKyLopDAO.class.getName());

    // Bộ nhớ đệm dự phòng cho chức năng ngoại tuyến
    private static final List<DangKyLop> memoryCache = new CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryCache.isEmpty()) return;
        memoryCache.addAll(com.nhom12.gym.service.SampleDataService.getDanhSachDangKyChuan());
    }

    public static void capNhatMemoryCache(List<DangKyLop> list) {
        if (list != null && !list.isEmpty()) {
            memoryCache.clear();
            memoryCache.addAll(list);
        }
    }

    public List<DangKyLop> layDanhSachTheoHoiVien(int maHoiVien) {
        List<DangKyLop> all = layTatCa();
        List<DangKyLop> res = new ArrayList<>();
        for (DangKyLop dk : all) {
            if (dk.getMaHoiVien() == maHoiVien) {
                res.add(dk);
            }
        }
        return res;
    }

    private DangKyLop sanitizeDangKyLop(DangKyLop dk) {
        if (dk == null) return null;
        dk.setTenHoiVien(com.nhom12.gym.util.TextSanitizer.sanitize(dk.getTenHoiVien()));
        dk.setTrangThaiDiemDanh(com.nhom12.gym.util.TextSanitizer.sanitize(dk.getTrangThaiDiemDanh()));
        if (dk.getGhiChu() != null) {
            dk.setGhiChu(com.nhom12.gym.util.TextSanitizer.sanitize(dk.getGhiChu()));
        }
        return dk;
    }

    public List<DangKyLop> layTatCa() {
        List<DangKyLop> result = new ArrayList<>();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            for (DangKyLop dk : memoryCache) {
                result.add(sanitizeDangKyLop(dk));
            }
            return result;
        }

        try (Session session = sf.openSession()) {
            List<DangKyLop> list = session.createQuery("FROM DangKyLop ORDER BY id DESC", DangKyLop.class).list();
            if (list != null && !list.isEmpty()) {
                for (DangKyLop dk : list) {
                    result.add(sanitizeDangKyLop(dk));
                }
                // Đồng bộ thêm các bản ghi bộ nhớ tạm chưa có trong CSDL
                for (DangKyLop dk : memoryCache) {
                    boolean daCo = result.stream().anyMatch(r ->
                        r.getMaLop() != null && r.getMaLop().equalsIgnoreCase(dk.getMaLop()) && r.getMaHoiVien() == dk.getMaHoiVien()
                    );
                    if (!daCo) {
                        result.add(sanitizeDangKyLop(dk));
                    }
                }
                memoryCache.clear();
                memoryCache.addAll(result);
                return result;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể tải danh sách đăng ký lớp từ CSDL, dùng cache: {0}", e.getMessage());
        }
        for (DangKyLop dk : memoryCache) {
            result.add(sanitizeDangKyLop(dk));
        }
        return result;
    }

    public List<DangKyLop> layDanhSachTheoLop(String maLop) {
        if (maLop == null || maLop.trim().isEmpty()) return new ArrayList<>();
        String code = maLop.trim();
        List<DangKyLop> result = new ArrayList<>();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                Query<DangKyLop> query = session.createQuery(
                    "FROM DangKyLop WHERE UPPER(maLop) = UPPER(:ma) ORDER BY id ASC", DangKyLop.class
                );
                query.setParameter("ma", code);
                List<DangKyLop> list = query.list();
                if (list != null && !list.isEmpty()) {
                    for (DangKyLop dk : list) {
                        result.add(sanitizeDangKyLop(dk));
                    }
                    // Đồng bộ thêm các bản ghi vừa ghi danh trong bộ nhớ tạm nếu chưa có
                    for (DangKyLop dk : memoryCache) {
                        if (dk.getMaLop() != null && dk.getMaLop().equalsIgnoreCase(code)) {
                            boolean daCo = result.stream().anyMatch(r -> r.getMaHoiVien() == dk.getMaHoiVien());
                            if (!daCo) {
                                result.add(sanitizeDangKyLop(dk));
                            }
                        }
                    }
                    return result;
                } else {
                    // Nếu lớp chưa có học viên nào trong CSDL, tự động nạp học viên chuẩn vào CSDL
                    List<DangKyLop> samples = new ArrayList<>();
                    for (DangKyLop dk : memoryCache) {
                        if (dk.getMaLop() != null && dk.getMaLop().equalsIgnoreCase(code)) {
                            samples.add(dk);
                        }
                    }
                    if (samples.isEmpty()) {
                        for (DangKyLop dk : com.nhom12.gym.service.SampleDataService.getDanhSachDangKyChuan()) {
                            if (dk.getMaLop() != null && dk.getMaLop().equalsIgnoreCase(code)) {
                                samples.add(dk);
                            }
                        }
                    }
                    if (!samples.isEmpty()) {
                        Transaction tx = session.beginTransaction();
                        for (DangKyLop s : samples) {
                            DangKyLop newDk = new DangKyLop(0, s.getMaLop(), s.getMaHoiVien(), s.getTenHoiVien(),
                                    s.getSoDienThoai(), s.getNgayDangKy(), s.getTrangThaiDiemDanh(), s.getGhiChu());
                            session.persist(newDk);
                            s.setId(newDk.getId());
                            result.add(sanitizeDangKyLop(newDk));
                        }
                        tx.commit();
                        for (DangKyLop r : result) {
                            memoryCache.removeIf(m -> m.getMaLop() != null && m.getMaLop().equalsIgnoreCase(r.getMaLop()) && m.getMaHoiVien() == r.getMaHoiVien());
                            memoryCache.add(r);
                        }
                        return result;
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi lấy danh sách học viên lớp từ CSDL: {0}", e.getMessage());
            }
        }

        // Fallback bộ nhớ đệm
        for (DangKyLop dk : memoryCache) {
            if (dk.getMaLop() != null && dk.getMaLop().equalsIgnoreCase(code)) {
                boolean daCo = result.stream().anyMatch(r -> r.getMaHoiVien() == dk.getMaHoiVien());
                if (!daCo) {
                    result.add(sanitizeDangKyLop(dk));
                }
            }
        }
        return result;
    }

    public boolean daDangKy(String maLop, int maHoiVien) {
        if (maLop == null || maHoiVien <= 0) return false;
        List<DangKyLop> list = layDanhSachTheoLop(maLop);
        for (DangKyLop dk : list) {
            if (dk.getMaHoiVien() == maHoiVien) {
                return true;
            }
        }
        return false;
    }

    public boolean them(DangKyLop dk) {
        if (dk == null) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            if (dk.getId() <= 0) dk.setId(memoryCache.size() + 1);
            memoryCache.removeIf(item -> item.getMaLop().equalsIgnoreCase(dk.getMaLop()) && item.getMaHoiVien() == dk.getMaHoiVien());
            memoryCache.add(dk);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.persist(dk);
            tx.commit();

            memoryCache.removeIf(item -> item.getMaLop().equalsIgnoreCase(dk.getMaLop()) && item.getMaHoiVien() == dk.getMaHoiVien());
            memoryCache.add(dk);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.WARNING, "Lỗi thêm đăng ký lớp vào CSDL, lưu vào bộ nhớ tạm", e);
            if (dk.getId() <= 0) dk.setId(memoryCache.size() + 1);
            memoryCache.removeIf(item -> item.getMaLop().equalsIgnoreCase(dk.getMaLop()) && item.getMaHoiVien() == dk.getMaHoiVien());
            memoryCache.add(dk);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        }
    }

    public boolean capNhatDiemDanh(int id, String trangThai) {
        String cleanStatus = com.nhom12.gym.util.TextSanitizer.sanitize(trangThai);
        if (cleanStatus == null || cleanStatus.trim().isEmpty()) {
            cleanStatus = "Chưa điểm danh";
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            for (DangKyLop item : memoryCache) {
                if (item.getId() == id) {
                    item.setTrangThaiDiemDanh(cleanStatus);
                    com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                    return true;
                }
            }
            return false;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            DangKyLop dk = session.get(DangKyLop.class, id);
            if (dk == null) {
                // Nếu ID chưa có trong DB (ID ảo từ cache), tìm trong memoryCache
                DangKyLop mem = memoryCache.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
                if (mem != null) {
                    Query<DangKyLop> q = session.createQuery(
                        "FROM DangKyLop WHERE UPPER(maLop) = UPPER(:ml) AND maHoiVien = :mhv", DangKyLop.class);
                    q.setParameter("ml", mem.getMaLop());
                    q.setParameter("mhv", mem.getMaHoiVien());
                    List<DangKyLop> existingList = q.list();
                    if (!existingList.isEmpty()) {
                        dk = existingList.get(0);
                    } else {
                        dk = new DangKyLop(0, mem.getMaLop(), mem.getMaHoiVien(), mem.getTenHoiVien(),
                                mem.getSoDienThoai(), mem.getNgayDangKy(), cleanStatus, mem.getGhiChu());
                        session.persist(dk);
                    }
                }
            }

            if (dk != null) {
                dk.setTrangThaiDiemDanh(cleanStatus);
                session.merge(dk);
                tx.commit();

                // Cập nhật memory cache
                final int finalId = dk.getId();
                for (DangKyLop item : memoryCache) {
                    if (item.getId() == id || (item.getMaLop().equalsIgnoreCase(dk.getMaLop()) && item.getMaHoiVien() == dk.getMaHoiVien())) {
                        item.setId(finalId);
                        item.setTrangThaiDiemDanh(cleanStatus);
                        break;
                    }
                }
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật điểm danh vào CSDL, lưu bộ nhớ tạm", e);

            for (DangKyLop item : memoryCache) {
                if (item.getId() == id) {
                    item.setTrangThaiDiemDanh(cleanStatus);
                    com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                    return true;
                }
            }
            return false;
        }
    }

    public boolean diemDanhTatCa(String maLop, String trangThai) {
        if (maLop == null || maLop.trim().isEmpty()) return false;
        String cleanStatus = com.nhom12.gym.util.TextSanitizer.sanitize(trangThai);
        if (cleanStatus == null || cleanStatus.trim().isEmpty()) {
            cleanStatus = "Đã điểm danh";
        }
        String code = maLop.trim();

        // 1. Cập nhật memory cache
        for (DangKyLop item : memoryCache) {
            if (item.getMaLop() != null && item.getMaLop().equalsIgnoreCase(code)) {
                item.setTrangThaiDiemDanh(cleanStatus);
            }
        }

        // 2. Cập nhật CSDL
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            Transaction tx = null;
            try (Session session = sf.openSession()) {
                tx = session.beginTransaction();
                int updated = session.createQuery(
                    "UPDATE DangKyLop SET trangThaiDiemDanh = :tt WHERE UPPER(maLop) = UPPER(:ml)"
                ).setParameter("tt", cleanStatus).setParameter("ml", code).executeUpdate();

                if (updated == 0) {
                    for (DangKyLop item : memoryCache) {
                        if (item.getMaLop() != null && item.getMaLop().equalsIgnoreCase(code)) {
                            DangKyLop newDk = new DangKyLop(0, item.getMaLop(), item.getMaHoiVien(), item.getTenHoiVien(),
                                    item.getSoDienThoai(), item.getNgayDangKy(), cleanStatus, item.getGhiChu());
                            session.persist(newDk);
                            item.setId(newDk.getId());
                        }
                    }
                }
                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                LOGGER.log(Level.WARNING, "Lỗi điểm danh tất cả trong CSDL: {0}", e.getMessage());
            }
        }

        com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
        return true;
    }

    public boolean capNhatGhiChu(int id, String ghiChu) {
        String cleanNote = com.nhom12.gym.util.TextSanitizer.sanitize(ghiChu);
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            for (DangKyLop item : memoryCache) {
                if (item.getId() == id) {
                    item.setGhiChu(cleanNote);
                    com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                    return true;
                }
            }
            return false;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            DangKyLop dk = session.get(DangKyLop.class, id);
            if (dk == null) {
                DangKyLop mem = memoryCache.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
                if (mem != null) {
                    Query<DangKyLop> q = session.createQuery(
                        "FROM DangKyLop WHERE UPPER(maLop) = UPPER(:ml) AND maHoiVien = :mhv", DangKyLop.class);
                    q.setParameter("ml", mem.getMaLop());
                    q.setParameter("mhv", mem.getMaHoiVien());
                    List<DangKyLop> existingList = q.list();
                    if (!existingList.isEmpty()) {
                        dk = existingList.get(0);
                    } else {
                        dk = new DangKyLop(0, mem.getMaLop(), mem.getMaHoiVien(), mem.getTenHoiVien(),
                                mem.getSoDienThoai(), mem.getNgayDangKy(), mem.getTrangThaiDiemDanh(), cleanNote);
                        session.persist(dk);
                    }
                }
            }

            if (dk != null) {
                dk.setGhiChu(cleanNote);
                session.merge(dk);
                tx.commit();

                final int finalId = dk.getId();
                for (DangKyLop item : memoryCache) {
                    if (item.getId() == id || (item.getMaLop().equalsIgnoreCase(dk.getMaLop()) && item.getMaHoiVien() == dk.getMaHoiVien())) {
                        item.setId(finalId);
                        item.setGhiChu(cleanNote);
                        break;
                    }
                }
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật ghi chú đăng ký lớp", e);
            for (DangKyLop item : memoryCache) {
                if (item.getId() == id) {
                    item.setGhiChu(cleanNote);
                    com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                    return true;
                }
            }
            return false;
        }
    }

    public boolean xoa(int id) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            boolean removed = memoryCache.removeIf(item -> item.getId() == id);
            if (removed) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            }
            return removed;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            DangKyLop dk = session.get(DangKyLop.class, id);
            if (dk == null) {
                DangKyLop mem = memoryCache.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
                if (mem != null) {
                    Query<DangKyLop> q = session.createQuery(
                        "FROM DangKyLop WHERE UPPER(maLop) = UPPER(:ml) AND maHoiVien = :mhv", DangKyLop.class);
                    q.setParameter("ml", mem.getMaLop());
                    q.setParameter("mhv", mem.getMaHoiVien());
                    List<DangKyLop> list = q.list();
                    if (!list.isEmpty()) {
                        dk = list.get(0);
                    }
                }
            }

            if (dk != null) {
                session.remove(dk);
            }
            tx.commit();

            final int targetId = (dk != null) ? dk.getId() : id;
            memoryCache.removeIf(item -> item.getId() == id || item.getId() == targetId);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.WARNING, "Lỗi xóa đăng ký lớp khỏi CSDL: " + id, e);
            boolean removed = memoryCache.removeIf(item -> item.getId() == id);
            if (removed) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            }
            return removed;
        }
    }

    public boolean xoaTheoLopVaHoiVien(String maLop, int maHoiVien) {
        List<DangKyLop> list = layDanhSachTheoLop(maLop);
        for (DangKyLop dk : list) {
            if (dk.getMaHoiVien() == maHoiVien) {
                return xoa(dk.getId());
            }
        }
        return false;
    }
}
