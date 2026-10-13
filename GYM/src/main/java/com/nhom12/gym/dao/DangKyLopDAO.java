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
        memoryCache.add(new DangKyLop(1, "LH01", 1, "Lê Anh Duy", "0974482749", new Date(), "Đã điểm danh", "Đi học đúng giờ"));
        memoryCache.add(new DangKyLop(2, "LH01", 2, "Võ Lê Ngọc Hưng", "0912345678", new Date(), "Đã điểm danh", "Mang thảm riêng"));
        memoryCache.add(new DangKyLop(3, "LH01", 5, "Hoàng Thùy Linh", "0977112233", new Date(), "Chưa điểm danh", ""));
        memoryCache.add(new DangKyLop(4, "LH02", 1, "Lê Anh Duy", "0974482749", new Date(), "Chưa điểm danh", ""));
        memoryCache.add(new DangKyLop(5, "LH03", 4, "Trần Minh Quân", "0933221100", new Date(), "Đã điểm danh", "Đầy đủ găng tập"));
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

    public List<DangKyLop> layTatCa() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            return new ArrayList<>(memoryCache);
        }

        try (Session session = sf.openSession()) {
            List<DangKyLop> list = session.createQuery("FROM DangKyLop ORDER BY id DESC", DangKyLop.class).list();
            if (list != null && !list.isEmpty()) {
                memoryCache.clear();
                memoryCache.addAll(list);
                return list;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể tải danh sách đăng ký lớp từ CSDL, dùng cache: {0}", e.getMessage());
        }
        return new ArrayList<>(memoryCache);
    }

    public List<DangKyLop> layDanhSachTheoLop(String maLop) {
        if (maLop == null || maLop.trim().isEmpty()) return new ArrayList<>();
        String code = maLop.trim();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                Query<DangKyLop> query = session.createQuery(
                    "FROM DangKyLop WHERE UPPER(maLop) = UPPER(:ma) ORDER BY id ASC", DangKyLop.class
                );
                query.setParameter("ma", code);
                List<DangKyLop> list = query.list();
                if (list != null && !list.isEmpty()) {
                    return list;
                }
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi lấy danh sách học viên lớp từ CSDL: {0}", e.getMessage());
            }
        }

        // Fallback bộ nhớ đệm
        List<DangKyLop> result = new ArrayList<>();
        for (DangKyLop dk : memoryCache) {
            if (dk.getMaLop() != null && dk.getMaLop().equalsIgnoreCase(code)) {
                result.add(dk);
            }
        }
        return result;
    }

    public boolean daDangKy(String maLop, int maHoiVien) {
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
            memoryCache.add(dk);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.persist(dk);
            tx.commit();

            memoryCache.add(dk);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi thêm đăng ký lớp vào CSDL", e);
            return false;
        }
    }

    public boolean capNhatDiemDanh(int id, String trangThai) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            for (DangKyLop item : memoryCache) {
                if (item.getId() == id) {
                    item.setTrangThaiDiemDanh(trangThai);
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
            if (dk != null) {
                dk.setTrangThaiDiemDanh(trangThai);
                session.merge(dk);
                tx.commit();

                // Cập nhật memory cache
                for (DangKyLop item : memoryCache) {
                    if (item.getId() == id) {
                        item.setTrangThaiDiemDanh(trangThai);
                        break;
                    }
                }
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật điểm danh vào CSDL", e);

            for (DangKyLop item : memoryCache) {
                if (item.getId() == id) {
                    item.setTrangThaiDiemDanh(trangThai);
                    com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                    return true;
                }
            }
            return false;
        }
    }

    public boolean capNhatGhiChu(int id, String ghiChu) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            for (DangKyLop item : memoryCache) {
                if (item.getId() == id) {
                    item.setGhiChu(ghiChu);
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
            if (dk != null) {
                dk.setGhiChu(ghiChu);
                session.merge(dk);
                tx.commit();

                for (DangKyLop item : memoryCache) {
                    if (item.getId() == id) {
                        item.setGhiChu(ghiChu);
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
                    item.setGhiChu(ghiChu);
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
            if (dk != null) {
                session.remove(dk);
                tx.commit();
                memoryCache.removeIf(item -> item.getId() == id);
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi xóa đăng ký lớp khỏi CSDL", e);
            return false;
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
