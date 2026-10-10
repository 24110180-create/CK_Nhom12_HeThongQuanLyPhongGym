package com.nhom12.gym.dao;

import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.entity.LopHoc;
import com.nhom12.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LopHocDAO {
    private static final List<LopHoc> memoryClasses = new CopyOnWriteArrayList<>();
    private static final List<DangKyLop> memoryEnrollments = new CopyOnWriteArrayList<>();

    static {
        memoryClasses.add(new LopHoc("LH01", "Yoga Trị Liệu Cột Sống", "Yoga", "Nguyễn Thu Thảo", "06:30 - 07:30 (T2-T4-T6)", "Studio 1 (Tầng 2)", 20, 18));
        memoryClasses.add(new LopHoc("LH02", "Zumba Cardio Đốt Mỡ", "Zumba", "Lê Mai Hương", "18:00 - 19:00 (T3-T5-T7)", "Studio 2 (Tầng 2)", 25, 22));
        memoryClasses.add(new LopHoc("LH03", "Kickboxing Tự Vệ Nâng Cao", "Boxing", "Phan Thanh Sơn", "19:15 - 20:15 (T2-T4-T6)", "Khu Võ Thuật Ring A", 15, 12));
        memoryClasses.add(new LopHoc("LH04", "HIIT Cardio Kháng Lực", "Cardio", "Trần Tuấn Kiệt", "07:00 - 08:00 (T3-T5)", "Khu Functional", 15, 10));
        memoryClasses.add(new LopHoc("LH05", "Pilates Dẻo Dai & Thon Gọn", "Pilates", "Nguyễn Thu Thảo", "17:30 - 18:30 (T2-T4-T6)", "Studio 1 (Tầng 2)", 18, 15));

        // Mẫu học viên đăng ký điểm danh
        memoryEnrollments.add(new DangKyLop(1, "LH01", 1, "Lê Anh Duy", "0901234567", new Date(), "Đã điểm danh", "Đi học đúng giờ"));
        memoryEnrollments.add(new DangKyLop(2, "LH01", 2, "Võ Lê Ngọc Hưng", "0902345678", new Date(), "Đã điểm danh", "Mang thảm riêng"));
        memoryEnrollments.add(new DangKyLop(3, "LH01", 5, "Hoàng Thùy Linh", "0905678901", new Date(), "Chưa điểm danh", ""));
        memoryEnrollments.add(new DangKyLop(4, "LH02", 1, "Lê Anh Duy", "0901234567", new Date(), "Chưa điểm danh", ""));
        memoryEnrollments.add(new DangKyLop(5, "LH03", 4, "Trần Minh Quân", "0904567890", new Date(), "Đã điểm danh", "Đầy đủ găng tập"));
    }

    public List<LopHoc> layTatCa() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                List<LopHoc> list = session.createQuery("FROM LopHoc ORDER BY maLop ASC", LopHoc.class).list();
                if (list != null && !list.isEmpty()) {
                    session.close();
                    return list;
                }
            } catch (Throwable t) {
                if (session != null) {
                    try { session.close(); } catch (Throwable ignored) {}
                }
            }
        } catch (Exception ignored) {}

        return new ArrayList<>(memoryClasses);
    }

    public LopHoc timTheoMa(String maLop) {
        if (maLop == null) return null;
        for (LopHoc lh : memoryClasses) {
            if (lh.getMaLop().equalsIgnoreCase(maLop)) return lh;
        }
        return null;
    }

    public boolean them(LopHoc lop) {
        if (lop == null) return false;
        memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(lop.getMaLop()));
        memoryClasses.add(lop);

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                session.persist(lop);
                tx.commit();
                session.close();
                return true;
            } catch (Throwable t) {
                if (tx != null) tx.rollback();
                if (session != null) {
                    try { session.close(); } catch (Throwable ignored) {}
                }
            }
        } catch (Exception ignored) {}
        return true;
    }

    public boolean capNhat(LopHoc lop) {
        if (lop == null) return false;
        for (int i = 0; i < memoryClasses.size(); i++) {
            if (memoryClasses.get(i).getMaLop().equalsIgnoreCase(lop.getMaLop())) {
                memoryClasses.set(i, lop);
                break;
            }
        }

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                session.merge(lop);
                tx.commit();
                session.close();
                return true;
            } catch (Throwable t) {
                if (tx != null) tx.rollback();
                if (session != null) {
                    try { session.close(); } catch (Throwable ignored) {}
                }
            }
        } catch (Exception ignored) {}
        return true;
    }

    public boolean xoa(String maLop) {
        memoryClasses.removeIf(item -> item.getMaLop().equalsIgnoreCase(maLop));

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                LopHoc lh = session.get(LopHoc.class, maLop);
                if (lh != null) {
                    session.remove(lh);
                    tx.commit();
                }
                session.close();
                return true;
            } catch (Throwable t) {
                if (tx != null) tx.rollback();
                if (session != null) {
                    try { session.close(); } catch (Throwable ignored) {}
                }
            }
        } catch (Exception ignored) {}
        return true;
    }

    public List<DangKyLop> layDanhSachHocVienCuaLop(String maLop) {
        List<DangKyLop> result = new ArrayList<>();
        for (DangKyLop dk : memoryEnrollments) {
            if (dk.getMaLop().equalsIgnoreCase(maLop)) {
                result.add(dk);
            }
        }
        return result;
    }

    public boolean dangKyVaoLop(DangKyLop dk) {
        if (dk == null) return false;
        if (dk.getId() <= 0) {
            dk.setId(memoryEnrollments.size() + 1);
        }
        // Kiểm tra xem đã đăng ký chưa
        for (DangKyLop existing : memoryEnrollments) {
            if (existing.getMaLop().equalsIgnoreCase(dk.getMaLop()) && existing.getMaHoiVien() == dk.getMaHoiVien()) {
                return false; // Đã đăng ký
            }
        }
        memoryEnrollments.add(dk);

        // Tăng sĩ số hiện tại của lớp
        LopHoc lh = timTheoMa(dk.getMaLop());
        if (lh != null) {
            lh.setSiSoHienTai(lh.getSiSoHienTai() + 1);
            capNhat(lh);
        }
        return true;
    }

    public boolean capNhatDiemDanh(int id, String trangThai) {
        for (DangKyLop dk : memoryEnrollments) {
            if (dk.getId() == id) {
                dk.setTrangThaiDiemDanh(trangThai);
                return true;
            }
        }
        return false;
    }
}
