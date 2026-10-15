package com.nhom12.gym.dao;

import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.entity.LopHoc;
import com.nhom12.gym.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Data Access Object cho Thực thể Lớp Học (LopHoc)
 * Quản lý thời khóa biểu các lớp nhóm: Yoga, Zumba, Kickboxing, Pilates, HIIT Cardio
 */
public class LopHocDAO {
    private static final Logger LOGGER = Logger.getLogger(LopHocDAO.class.getName());
    private static final List<LopHoc> memoryClasses = new CopyOnWriteArrayList<>();
    private final DangKyLopDAO dangKyLopDAO = new DangKyLopDAO();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryClasses.isEmpty()) return;
        memoryClasses.addAll(com.nhom12.gym.service.SampleDataService.getDanhSachLopHocChuan());
    }

    public static void capNhatMemoryCache(List<LopHoc> list) {
        if (list != null && !list.isEmpty()) {
            memoryClasses.clear();
            memoryClasses.addAll(list);
        }
    }

    public List<LopHoc> layTatCa() {
        List<LopHoc> list = null;
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                list = session.createQuery("FROM LopHoc ORDER BY maLop ASC", LopHoc.class).list();
                if (list != null && !list.isEmpty()) {
                    memoryClasses.clear();
                    memoryClasses.addAll(list);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi truy vấn danh sách lớp từ CSDL, dùng cache: {0}", e.getMessage());
            }
        }
        if (list == null || list.isEmpty()) {
            list = new ArrayList<>(memoryClasses);
        }

        // Lọc bỏ triệt để các dữ liệu lớp học rác hoặc không hợp lệ (như 'freee') mà không gọi đệ quy
        list.removeIf(lh -> lh == null || (lh.getTenLop() != null && lh.getTenLop().toLowerCase().contains("freee")));
        memoryClasses.removeIf(lh -> lh == null || (lh.getTenLop() != null && lh.getTenLop().toLowerCase().contains("freee")));

        List<LopHoc> result = new ArrayList<>();
        // Đồng bộ sĩ số thực tế từ danh sách đăng ký
        for (LopHoc lh : list) {
            int actualCount = dangKyLopDAO.layDanhSachTheoLop(lh.getMaLop()).size();
            lh.setSiSoHienTai(actualCount);
            result.add(sanitizeLopHoc(lh));
        }
        return result;
    }

    private LopHoc sanitizeLopHoc(LopHoc lh) {
        if (lh == null) return null;
        lh.setTenLop(com.nhom12.gym.util.TextSanitizer.sanitize(lh.getTenLop()));
        lh.setBoMon(com.nhom12.gym.util.TextSanitizer.sanitize(lh.getBoMon()));
        lh.setHuanLuyenVien(com.nhom12.gym.util.TextSanitizer.sanitize(lh.getHuanLuyenVien()));
        lh.setPhongTap(com.nhom12.gym.util.TextSanitizer.sanitize(lh.getPhongTap()));
        lh.setChiNhanh(com.nhom12.gym.util.TextSanitizer.normalizeBranch(lh.getChiNhanh()));
        lh.setThoiGian(com.nhom12.gym.util.TextSanitizer.sanitize(lh.getThoiGian()));
        return lh;
    }

    public LopHoc timTheoMa(String maLop) {
        if (maLop == null || maLop.trim().isEmpty()) return null;
        String code = maLop.trim();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                LopHoc lh = session.get(LopHoc.class, code);
                if (lh != null) return sanitizeLopHoc(lh);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi tìm lớp theo mã ''{0}'': {1}", new Object[]{code, e.getMessage()});
            }
        }

        for (LopHoc lh : memoryClasses) {
            if (lh.getMaLop().equalsIgnoreCase(code)) return sanitizeLopHoc(lh);
        }
        return null;
    }

    public boolean them(LopHoc lop) {
        if (lop == null) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "CSDL offline - lưu lớp học vào bộ nhớ tạm");
            memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(lop.getMaLop()));
            memoryClasses.add(lop);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.persist(lop);
            tx.commit();

            memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(lop.getMaLop()));
            memoryClasses.add(lop);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.WARNING, "Lỗi thêm lớp học vào CSDL, dùng bộ nhớ tạm: " + lop.getMaLop(), e);
            memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(lop.getMaLop()));
            memoryClasses.add(lop);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        }
    }

    public boolean capNhat(LopHoc lop) {
        if (lop == null) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "CSDL offline - cập nhật lớp học trong bộ nhớ tạm");
            for (int i = 0; i < memoryClasses.size(); i++) {
                if (memoryClasses.get(i).getMaLop().equalsIgnoreCase(lop.getMaLop())) {
                    memoryClasses.set(i, lop);
                    break;
                }
            }
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.merge(lop);
            tx.commit();

            for (int i = 0; i < memoryClasses.size(); i++) {
                if (memoryClasses.get(i).getMaLop().equalsIgnoreCase(lop.getMaLop())) {
                    memoryClasses.set(i, lop);
                    break;
                }
            }
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.WARNING, "Lỗi cập nhật lớp học vào CSDL, dùng bộ nhớ tạm: " + lop.getMaLop(), e);
            for (int i = 0; i < memoryClasses.size(); i++) {
                if (memoryClasses.get(i).getMaLop().equalsIgnoreCase(lop.getMaLop())) {
                    memoryClasses.set(i, lop);
                    break;
                }
            }
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            return true;
        }
    }

    public boolean xoa(String maLop) {
        if (maLop == null || maLop.trim().isEmpty()) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "CSDL offline - xóa lớp học khỏi bộ nhớ tạm");
            boolean removed = memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(maLop.trim()));
            if (removed) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            }
            return removed;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            LopHoc lh = session.get(LopHoc.class, maLop.trim());
            if (lh != null) {
                session.remove(lh);
                tx.commit();
                memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(maLop.trim()));
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
                return true;
            }
            boolean removed = memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(maLop.trim()));
            if (removed) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            }
            return removed;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.WARNING, "Lỗi xóa lớp học trên CSDL, xóa khỏi bộ nhớ tạm: " + maLop, e);
            boolean removed = memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(maLop.trim()));
            if (removed) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.LOP_HOC);
            }
            return removed;
        }
    }

    // Các phương thức ủy quyền sang DangKyLopDAO đảm bảo tách bạch trách nhiệm
    public List<DangKyLop> layDanhSachHocVienCuaLop(String maLop) {
        return dangKyLopDAO.layDanhSachTheoLop(maLop);
    }

    public boolean dangKyVaoLop(DangKyLop dk) {
        if (dk == null) return false;
        if (dangKyLopDAO.daDangKy(dk.getMaLop(), dk.getMaHoiVien())) {
            return false;
        }
        boolean thanhCong = dangKyLopDAO.them(dk);
        if (thanhCong) {
            LopHoc lh = timTheoMa(dk.getMaLop());
            if (lh != null) {
                lh.setSiSoHienTai(lh.getSiSoHienTai() + 1);
                capNhat(lh);
            }
        }
        return thanhCong;
    }

    public boolean capNhatDiemDanh(int id, String trangThai) {
        return dangKyLopDAO.capNhatDiemDanh(id, trangThai);
    }

    public boolean huyDangKy(int idDangKy, String maLop) {
        boolean thanhCong = dangKyLopDAO.xoa(idDangKy);
        if (thanhCong && maLop != null) {
            LopHoc lh = timTheoMa(maLop);
            if (lh != null && lh.getSiSoHienTai() > 0) {
                lh.setSiSoHienTai(lh.getSiSoHienTai() - 1);
                capNhat(lh);
            }
        }
        return thanhCong;
    }

    public void diemDanhTatCa(String maLop, String trangThai) {
        if (maLop == null || maLop.trim().isEmpty()) return;
        dangKyLopDAO.diemDanhTatCa(maLop, trangThai);
    }
}
