package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.util.HibernateUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Data Access Object cho Thực thể Hội Viên (HoiVien)
 * Quản lý các thao tác CRUD và bộ nhớ đệm dự phòng khi mạng chi nhánh chập chờn
 */
public class HoiVienDAO {

    private static final HoiVienDAO INSTANCE = new HoiVienDAO();

    public static HoiVienDAO getInstance() {
        return INSTANCE;
    }

    private static final Logger LOGGER = Logger.getLogger(HoiVienDAO.class.getName());

    // Bộ nhớ đệm dự phòng (Memory Fallback Buffer) đồng bộ khi CSDL offline
    private static final List<HoiVien> memoryCache = new CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryCache.isEmpty()) return;
        try {
            memoryCache.addAll(com.nhom12.gym.service.SampleDataService.getDanhSachHoiVienChuan());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể nạp danh sách hội viên mẫu", e);
        }
    }

    public static void capNhatMemoryCache(List<HoiVien> list) {
        if (list != null && !list.isEmpty()) {
            memoryCache.clear();
            memoryCache.addAll(list);
        }
    }

    public List<HoiVien> layTatCa() {
        List<HoiVien> result = new ArrayList<>();
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                List<HoiVien> list = session.createQuery("FROM HoiVien WHERE maTheQR NOT LIKE 'QR_TEST_%' ORDER BY maHoiVien DESC", HoiVien.class).list();
                if (list != null && !list.isEmpty()) {
                    memoryCache.clear();
                    memoryCache.addAll(list);
                    for (HoiVien item : list) {
                        result.add(sanitizeHoiVien(item));
                    }
                    return result;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "CSDL chưa sẵn sàng, sử dụng bộ nhớ đệm: {0}", e.getMessage());
            }
        }
        for (HoiVien item : memoryCache) {
            if (item.getMaTheQR() != null && item.getMaTheQR().startsWith("QR_TEST_")) continue;
            result.add(sanitizeHoiVien(item));
        }
        return result;
    }

    public boolean them(HoiVien hv) {
        if (hv == null) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            // Offline mode: lưu vào memory cache
            if (hv.getMaHoiVien() <= 0) {
                int maxId = 0;
                for (HoiVien m : memoryCache) {
                    if (m.getMaHoiVien() > maxId) maxId = m.getMaHoiVien();
                }
                hv.setMaHoiVien(maxId + 1);
            }
            memoryCache.removeIf(item -> (item.getMaHoiVien() > 0 && item.getMaHoiVien() == hv.getMaHoiVien())
                    || (item.getMaTheQR() != null && item.getMaTheQR().equalsIgnoreCase(hv.getMaTheQR())));
            memoryCache.add(0, hv);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.ALL);
            new com.nhom12.gym.dao.NhatKyHeThongDAO().ghiLog(
                    com.nhom12.gym.util.Auth.tenDangNhap, com.nhom12.gym.util.Auth.vaiTro, "Thêm hội viên mới",
                    "Mã: " + hv.getMaTheQR() + ", Tên: " + hv.getHoTen()
            );
            return true;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            // Đảm bảo maHoiVien = 0 để IDENTITY generator của SQL Server tự sinh ID, tránh lỗi PersistentObjectException
            hv.setMaHoiVien(0);
            session.persist(hv);
            tx.commit();

            memoryCache.removeIf(item -> (item.getMaHoiVien() > 0 && item.getMaHoiVien() == hv.getMaHoiVien())
                    || (item.getMaTheQR() != null && item.getMaTheQR().equalsIgnoreCase(hv.getMaTheQR())));
            memoryCache.add(0, hv);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.ALL);
            new com.nhom12.gym.dao.NhatKyHeThongDAO().ghiLog(
                    com.nhom12.gym.util.Auth.tenDangNhap, com.nhom12.gym.util.Auth.vaiTro, "Thêm hội viên mới",
                    "Mã: " + hv.getMaTheQR() + ", Tên: " + hv.getHoTen()
            );
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi thêm hội viên vào CSDL: " + e.getMessage(), e);
            // Fallback an toàn vào memoryCache
            if (hv.getMaHoiVien() <= 0) {
                int maxId = 0;
                for (HoiVien m : memoryCache) {
                    if (m.getMaHoiVien() > maxId) maxId = m.getMaHoiVien();
                }
                hv.setMaHoiVien(maxId + 1);
            }
            memoryCache.removeIf(item -> (item.getMaHoiVien() > 0 && item.getMaHoiVien() == hv.getMaHoiVien())
                    || (item.getMaTheQR() != null && item.getMaTheQR().equalsIgnoreCase(hv.getMaTheQR())));
            memoryCache.add(0, hv);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.ALL);
            return true;
        }
    }

    public boolean capNhat(HoiVien hv) {
        if (hv == null) return false;

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            for (int i = 0; i < memoryCache.size(); i++) {
                if (memoryCache.get(i).getMaHoiVien() == hv.getMaHoiVien()) {
                    memoryCache.set(i, hv);
                    com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
                    return true;
                }
            }
            return false;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            session.merge(hv);
            tx.commit();

            for (int i = 0; i < memoryCache.size(); i++) {
                if (memoryCache.get(i).getMaHoiVien() == hv.getMaHoiVien()) {
                    memoryCache.set(i, hv);
                    break;
                }
            }
            com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi cập nhật hội viên", e);
            return false;
        }
    }

    public boolean xoa(int maHoiVien) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf == null) {
            boolean removed = memoryCache.removeIf(item -> item.getMaHoiVien() == maHoiVien);
            if (removed) {
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
                new com.nhom12.gym.dao.NhatKyHeThongDAO().ghiLog(
                        com.nhom12.gym.util.Auth.tenDangNhap, com.nhom12.gym.util.Auth.vaiTro, "Xóa hội viên",
                        "Mã hội viên: " + maHoiVien
                );
            }
            return removed;
        }

        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            HoiVien hv = session.get(HoiVien.class, maHoiVien);
            if (hv != null) {
                session.remove(hv);
                tx.commit();
                memoryCache.removeIf(item -> item.getMaHoiVien() == maHoiVien);
                com.nhom12.gym.service.DataSyncManager.fireDataChanged(com.nhom12.gym.service.DataSyncManager.DataType.HOI_VIEN);
                new com.nhom12.gym.dao.NhatKyHeThongDAO().ghiLog(
                        com.nhom12.gym.util.Auth.tenDangNhap, com.nhom12.gym.util.Auth.vaiTro, "Xóa hội viên",
                        "Mã hội viên: " + maHoiVien + " (" + hv.getHoTen() + ")"
                );
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            LOGGER.log(Level.SEVERE, "Lỗi xóa hội viên ID " + maHoiVien, e);
            return false;
        }
    }

    private HoiVien sanitizeHoiVien(HoiVien hv) {
        if (hv == null) return null;
        hv.setHoTen(com.nhom12.gym.util.TextSanitizer.sanitize(hv.getHoTen()));
        hv.setChiNhanhDangKy(com.nhom12.gym.util.TextSanitizer.normalizeBranch(hv.getChiNhanhDangKy()));
        if (hv.getTrangThai() != null) {
            hv.setTrangThai(com.nhom12.gym.util.TextSanitizer.sanitize(hv.getTrangThai()));
        }
        if (hv.getGoiTapDangKy() != null) {
            hv.setGoiTapDangKy(com.nhom12.gym.util.TextSanitizer.sanitize(hv.getGoiTapDangKy()));
        }
        if (hv.getHinhThucThanhToan() != null) {
            hv.setHinhThucThanhToan(com.nhom12.gym.util.TextSanitizer.sanitize(hv.getHinhThucThanhToan()));
        }
        return hv;
    }

    public HoiVien timTheoId(int maHoiVien) {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                HoiVien hv = session.get(HoiVien.class, maHoiVien);
                if (hv != null) return sanitizeHoiVien(hv);
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi tìm hội viên theo ID: {0}", e.getMessage());
            }
        }

        for (HoiVien hv : memoryCache) {
            if (hv.getMaHoiVien() == maHoiVien) return sanitizeHoiVien(hv);
        }
        return null;
    }

    public HoiVien timTheoMaQR(String maQR) {
        if (maQR == null || maQR.trim().isEmpty()) return null;
        String q = maQR.trim();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            try (Session session = sf.openSession()) {
                Query<HoiVien> query = session.createQuery(
                    "FROM HoiVien v WHERE UPPER(v.maTheQR) = UPPER(:ma)", HoiVien.class
                );
                query.setParameter("ma", q);
                HoiVien hv = query.uniqueResult();
                if (hv != null) return sanitizeHoiVien(hv);
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Lỗi tìm theo mã QR: {0}", e.getMessage());
            }
        }

        for (HoiVien hv : memoryCache) {
            if (hv.getMaTheQR() != null && hv.getMaTheQR().equalsIgnoreCase(q)) {
                return sanitizeHoiVien(hv);
            }
        }
        return null;
    }

    public HoiVien timKiemThongMinh(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return null;
        String kw = keyword.trim();

        // 1. Khớp mã QR chính xác
        HoiVien hv = timTheoMaQR(kw);
        if (hv != null) return sanitizeHoiVien(hv);

        // 2. Khớp ID số nguyên
        try {
            int id = Integer.parseInt(kw);
            hv = timTheoId(id);
            if (hv != null) return sanitizeHoiVien(hv);
        } catch (NumberFormatException ignored) {}

        // 3. Khớp số bóc tách từ mã dạng HV01, HV386
        String digits = kw.replaceAll("\\D+", "");
        if (!digits.isEmpty()) {
            try {
                int id = Integer.parseInt(digits);
                hv = timTheoId(id);
                if (hv != null) return sanitizeHoiVien(hv);
            } catch (NumberFormatException ignored) {}
        }

        // 4. Tìm kiếm theo Số Điện Thoại, Mã QR chứa, hoặc Họ Tên
        List<HoiVien> all = layTatCa();
        if (all != null) {
            for (HoiVien m : all) {
                if (m.getSoDienThoai() != null && m.getSoDienThoai().replaceAll("\\s+", "").contains(kw)) {
                    return sanitizeHoiVien(m);
                }
            }
            for (HoiVien m : all) {
                if (m.getMaTheQR() != null && m.getMaTheQR().equalsIgnoreCase(kw)) {
                    return sanitizeHoiVien(m);
                }
            }
            for (HoiVien m : all) {
                if (m.getHoTen() != null && m.getHoTen().toLowerCase().contains(kw.toLowerCase())) {
                    return sanitizeHoiVien(m);
                }
            }
        }

        return null;
    }

    /**
     * Lấy danh sách hội viên sắp hết hạn trong vòng soNgayToi ngày (tính chuẩn theo mốc 00:00:00 đầu ngày)
     * @param soNgayToi Số ngày tới (ví dụ: 7, 14, 30 ngày). Nếu soNgayToi < 0 sẽ lấy toàn bộ hội viên đã quá hạn.
     * @return Danh sách hội viên sắp hết hạn, sắp xếp ưu tiên từ khẩn cấp nhất đến ít khẩn cấp hơn
     */
    public List<HoiVien> layDanhSachSapHetHan(int soNgayToi) {
        List<HoiVien> all = layTatCa();
        List<HoiVien> res = new ArrayList<>();
        if (all == null) return res;

        java.util.Calendar todayCal = java.util.Calendar.getInstance();
        todayCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        todayCal.set(java.util.Calendar.MINUTE, 0);
        todayCal.set(java.util.Calendar.SECOND, 0);
        todayCal.set(java.util.Calendar.MILLISECOND, 0);
        long todayMs = todayCal.getTimeInMillis();

        for (HoiVien hv : all) {
            java.util.Date exp = hv.getNgayHetHan();
            if (exp == null) continue;

            java.util.Calendar expCal = java.util.Calendar.getInstance();
            expCal.setTime(exp);
            expCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            expCal.set(java.util.Calendar.MINUTE, 0);
            expCal.set(java.util.Calendar.SECOND, 0);
            expCal.set(java.util.Calendar.MILLISECOND, 0);
            long remainDays = (expCal.getTimeInMillis() - todayMs) / (1000L * 60 * 60 * 24);

            if (soNgayToi < 0) {
                if (remainDays < 0) {
                    res.add(sanitizeHoiVien(hv));
                }
            } else {
                if (remainDays <= soNgayToi) {
                    res.add(sanitizeHoiVien(hv));
                }
            }
        }

        // Sắp xếp ngày hết hạn tăng dần (những ai hết hạn trước / quá hạn xếp lên đầu để xử lý trước)
        res.sort((a, b) -> {
            if (a.getNgayHetHan() == null && b.getNgayHetHan() == null) return 0;
            if (a.getNgayHetHan() == null) return 1;
            if (b.getNgayHetHan() == null) return -1;
            return a.getNgayHetHan().compareTo(b.getNgayHetHan());
        });

        return res;
    }
}
