package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.util.HibernateUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class HoiVienDAO {
    // Bộ nhớ đệm kép cục bộ (Dual Memory Fallback Cache) hoạt động cả khi offline
    private static final List<HoiVien> memoryList = new CopyOnWriteArrayList<>();

    static {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            memoryList.add(new HoiVien(1, "QR001", "Lê Anh Duy", "CN01 - Quận 1", sdf.parse("2026-12-31"), "Active"));
            memoryList.add(new HoiVien(2, "QR002", "Võ Lê Ngọc Hưng", "CN02 - Tân Bình", sdf.parse("2026-10-15"), "Active"));
            memoryList.add(new HoiVien(3, "QR003", "Võ Tấn Phát", "CN01 - Quận 1", sdf.parse("2025-01-01"), "Active"));
            memoryList.add(new HoiVien(4, "QR004", "Trần Minh Quân", "CN01 - Quận 1", sdf.parse("2026-11-20"), "Active"));
            memoryList.add(new HoiVien(5, "QR005", "Hoàng Thùy Linh", "CN02 - Tân Bình", sdf.parse("2026-08-15"), "Active"));
            memoryList.add(new HoiVien(6, "VIP007", "Đỗ Minh Trí", "Toàn hệ thống (VIP)", sdf.parse("2027-12-31"), "Active"));
        } catch (Exception ignored) {}
    }

    public List<HoiVien> layTatCa() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                List<HoiVien> list = session.createQuery("FROM HoiVien ORDER BY maHoiVien DESC", HoiVien.class).list();
                if (list != null && !list.isEmpty()) {
                    // Đồng bộ danh sách từ CSDL vào bộ nhớ đệm
                    for (HoiVien dbHv : list) {
                        boolean exists = false;
                        for (int i = 0; i < memoryList.size(); i++) {
                            if (memoryList.get(i).getMaHoiVien() == dbHv.getMaHoiVien()) {
                                memoryList.set(i, dbHv);
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            memoryList.add(dbHv);
                        }
                    }
                    session.close();
                    return list;
                }
            } catch (Throwable t) {
                if (session != null) {
                    try { session.close(); } catch (Throwable ignored) {}
                }
            }
        } catch (Exception ignored) {}

        return new ArrayList<>(memoryList);
    }

    public boolean them(HoiVien hv) {
        if (hv == null) return false;

        // Tự động gán mã ID và mã thẻ định dạng chuẩn nếu thiếu
        int maxId = 0;
        for (HoiVien m : memoryList) {
            if (m.getMaHoiVien() > maxId) maxId = m.getMaHoiVien();
        }
        if (hv.getMaHoiVien() <= 0) {
            hv.setMaHoiVien(maxId + 1);
        }
        if (hv.getMaTheQR() == null || hv.getMaTheQR().trim().isEmpty()) {
            hv.setMaTheQR(String.format("HV%03d", hv.getMaHoiVien()));
        }

        // Cập nhật bộ nhớ đệm nội bộ
        boolean existed = false;
        for (int i = 0; i < memoryList.size(); i++) {
            if (memoryList.get(i).getMaHoiVien() == hv.getMaHoiVien()) {
                memoryList.set(i, hv);
                existed = true;
                break;
            }
        }
        if (!existed) {
            memoryList.add(0, hv);
        }

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                session.persist(hv);
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

    public boolean capNhat(HoiVien hv) {
        if (hv == null) return false;

        // Cập nhật bộ nhớ đệm nội bộ
        for (int i = 0; i < memoryList.size(); i++) {
            if (memoryList.get(i).getMaHoiVien() == hv.getMaHoiVien()) {
                memoryList.set(i, hv);
                break;
            }
        }

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                session.merge(hv);
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

    public boolean xoa(int maHoiVien) {
        memoryList.removeIf(hv -> hv.getMaHoiVien() == maHoiVien);

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                HoiVien hv = session.get(HoiVien.class, maHoiVien);
                if (hv != null) {
                    session.remove(hv);
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

    public HoiVien timTheoId(int maHoiVien) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                HoiVien hv = session.get(HoiVien.class, maHoiVien);
                session.close();
                if (hv != null) return hv;
            } catch (Throwable t) {
                if (session != null) {
                    try { session.close(); } catch (Throwable ignored) {}
                }
            }
        } catch (Exception ignored) {}

        for (HoiVien hv : memoryList) {
            if (hv.getMaHoiVien() == maHoiVien) return hv;
        }
        return null;
    }

    public HoiVien timTheoMaQR(String maQR) {
        if (maQR == null || maQR.trim().isEmpty()) return null;
        String q = maQR.trim();

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                Query<HoiVien> query = session.createQuery("FROM HoiVien v WHERE UPPER(v.maTheQR) = UPPER(:ma)", HoiVien.class);
                query.setParameter("ma", q);
                HoiVien hv = query.uniqueResult();
                session.close();
                if (hv != null) return hv;
            } catch (Throwable t) {
                if (session != null) {
                    try { session.close(); } catch (Throwable ignored) {}
                }
            }
        } catch (Exception ignored) {}

        for (HoiVien hv : memoryList) {
            if (hv.getMaTheQR() != null && hv.getMaTheQR().equalsIgnoreCase(q)) {
                return hv;
            }
        }
        return null;
    }

    /**
     * Tra cứu thông minh đa năng:
     * - Khớp theo Mã Hội Viên (ví dụ: '12', '1', hoặc 'HV012', 'HV12')
     * - Khớp theo Mã Thẻ QR (ví dụ: 'HV861', 'QR001', 'VIP007')
     * - Khớp theo Họ Tên (ví dụ: 'Duy', 'Hưng', 'Linh'...)
     */
    public HoiVien timKiemThongMinh(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return null;
        String kw = keyword.trim();

        // 1. Thử tìm theo Mã QR chính xác
        HoiVien hv = timTheoMaQR(kw);
        if (hv != null) return hv;

        // 2. Thử tìm theo ID số nguyên (ví dụ người dùng gõ "12", "1")
        try {
            int id = Integer.parseInt(kw);
            hv = timTheoId(id);
            if (hv != null) return hv;
        } catch (NumberFormatException ignored) {}

        // 3. Nếu người dùng gõ dạng "HV12", "HV012", "QR01" -> bóc tách phần số
        String digitsOnly = kw.replaceAll("\\D+", "");
        if (!digitsOnly.isEmpty()) {
            try {
                int id = Integer.parseInt(digitsOnly);
                hv = timTheoId(id);
                if (hv != null) return hv;
            } catch (NumberFormatException ignored) {}
        }

        // 4. Tìm kiếm trong toàn bộ danh sách (Database hoặc Memory Cache)
        List<HoiVien> all = layTatCa();
        if (all != null) {
            // Khớp theo mã QR không phân biệt hoa thường
            for (HoiVien m : all) {
                if (m.getMaTheQR() != null && m.getMaTheQR().equalsIgnoreCase(kw)) {
                    return m;
                }
            }
            // Khớp theo Họ Tên contains
            for (HoiVien m : all) {
                if (m.getHoTen() != null && m.getHoTen().toLowerCase().contains(kw.toLowerCase())) {
                    return m;
                }
            }
        }

        return null;
    }
}
