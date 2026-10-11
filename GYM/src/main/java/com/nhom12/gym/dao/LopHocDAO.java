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
        memoryClasses.add(new LopHoc("LH01", "Yoga Trị Liệu Cột Sống", "Yoga", "Nguyễn Thu Thảo", "06:30 - 07:30 (T2-T4-T6)", "Studio 1 (Tầng 2)", 20, 18));
        memoryClasses.add(new LopHoc("LH02", "Zumba Cardio Đốt Mỡ", "Zumba", "Lê Mai Hương", "18:00 - 19:00 (T3-T5-T7)", "Studio 2 (Tầng 2)", 25, 22));
        memoryClasses.add(new LopHoc("LH03", "Kickboxing Tự Vệ Nâng Cao", "Boxing", "Phan Thanh Sơn", "19:15 - 20:15 (T2-T4-T6)", "Khu Võ Thuật Ring A", 15, 12));
        memoryClasses.add(new LopHoc("LH04", "HIIT Cardio Kháng Lực", "Cardio", "Trần Tuấn Kiệt", "07:00 - 08:00 (T3-T5)", "Khu Functional", 15, 10));
        memoryClasses.add(new LopHoc("LH05", "Pilates Dẻo Dai & Thon Gọn", "Pilates", "Nguyễn Thu Thảo", "17:30 - 18:30 (T2-T4-T6)", "Studio 1 (Tầng 2)", 18, 15));
    }

    public List<LopHoc> layTatCa() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                List<LopHoc> list = session.createQuery("FROM LopHoc ORDER BY maLop ASC", LopHoc.class).list();
                if (list != null && !list.isEmpty()) {
                    memoryClasses.clear();
                    memoryClasses.addAll(list);
                    return list;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi truy vấn danh sách lớp từ CSDL, dùng cache: {0}", e.getMessage());
            }
        }
        return new ArrayList<>(memoryClasses);
    }

    public LopHoc timTheoMa(String maLop) {
        if (maLop == null || maLop.trim().isEmpty()) return null;
        String code = maLop.trim();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                LopHoc lh = session.get(LopHoc.class, code);
                if (lh != null) return lh;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Lỗi tìm lớp theo mã ''{0}'': {1}", new Object[]{code, e.getMessage()});
            }
        }

        for (LopHoc lh : memoryClasses) {
            if (lh.getMaLop().equalsIgnoreCase(code)) return lh;
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
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi thêm lớp học vào CSDL: " + lop.getMaLop(), e);
            return false;
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
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật lớp học: " + lop.getMaLop(), e);
            return false;
        }
    }

    public boolean xoa(String maLop) {
        if (maLop == null || maLop.trim().isEmpty()) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            LOGGER.log(Level.WARNING, "CSDL offline - xóa lớp học khỏi bộ nhớ tạm");
            memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(maLop.trim()));
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            LopHoc lh = session.get(LopHoc.class, maLop.trim());
            if (lh != null) {
                session.remove(lh);
                tx.commit();
                memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(maLop.trim()));
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi xóa lớp học: " + maLop, e);
            return false;
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
}
