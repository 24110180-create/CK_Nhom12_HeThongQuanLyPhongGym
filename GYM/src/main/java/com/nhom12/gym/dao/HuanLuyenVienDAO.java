package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HuanLuyenVien;
import com.nhom12.gym.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object cho Thực thể Huấn Luyện Viên (HuanLuyenVien)
 */
public class HuanLuyenVienDAO {
    private static final List<HuanLuyenVien> memoryList = new CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryList.isEmpty()) return;
        memoryList.add(new HuanLuyenVien("HLV01", "Trần Tuấn Kiệt", "0912345678", "Bodybuilding / PT Tăng cơ", 6, "CN01 - Quận 1", "Ca chiều (14:00 - 22:00)", 4.9, "Đang làm việc"));
        memoryList.add(new HuanLuyenVien("HLV02", "Nguyễn Thu Thảo", "0987654321", "Yoga Thảo dược / Pilates", 5, "CN01 - Quận 1", "Ca sáng (06:00 - 14:00)", 5.0, "Đang làm việc"));
        memoryList.add(new HuanLuyenVien("HLV03", "Phan Thanh Sơn", "0903112233", "Kickboxing & HIIT Cardio", 4, "CN02 - Tân Bình", "Ca chiều (14:00 - 22:00)", 4.8, "Đang làm việc"));
        memoryList.add(new HuanLuyenVien("HLV04", "Lê Mai Hương", "0934556677", "Zumba Dance & Aerobic", 3, "CN02 - Tân Bình", "Ca sáng (06:00 - 14:00)", 4.9, "Đang làm việc"));
        memoryList.add(new HuanLuyenVien("HLV05", "Đặng Minh Trí", "0977889900", "CrossFit & Phục hồi chức năng", 7, "CN02 - Tân Bình", "Ca sáng (06:00 - 14:00)", 4.9, "Đang làm việc"));
    }

    public List<HuanLuyenVien> layTatCa() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<HuanLuyenVien> list = session.createQuery("FROM HuanLuyenVien ORDER BY maHLV ASC", HuanLuyenVien.class).list();
            if (list != null && !list.isEmpty()) {
                memoryList.clear();
                memoryList.addAll(list);
                return list;
            }
        } catch (Exception e) {
            System.err.println("[HuanLuyenVienDAO] Lỗi tải danh sách HLV từ CSDL: " + e.getMessage());
        }
        return new ArrayList<>(memoryList);
    }

    public HuanLuyenVien timTheoMa(String maHLV) {
        if (maHLV == null || maHLV.trim().isEmpty()) return null;
        String code = maHLV.trim();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            HuanLuyenVien h = session.get(HuanLuyenVien.class, code);
            if (h != null) return h;
        } catch (Exception e) {
            System.err.println("[HuanLuyenVienDAO] Lỗi tìm HLV theo mã: " + e.getMessage());
        }

        for (HuanLuyenVien h : memoryList) {
            if (h.getMaHLV().equalsIgnoreCase(code)) return h;
        }
        return null;
    }

    public boolean them(HuanLuyenVien hlv) {
        if (hlv == null) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(hlv);
            tx.commit();

            memoryList.removeIf(item -> item.getMaHLV().equalsIgnoreCase(hlv.getMaHLV()));
            memoryList.add(hlv);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[HuanLuyenVienDAO] Lỗi thêm HLV vào CSDL: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhat(HuanLuyenVien hlv) {
        if (hlv == null) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(hlv);
            tx.commit();

            for (int i = 0; i < memoryList.size(); i++) {
                if (memoryList.get(i).getMaHLV().equalsIgnoreCase(hlv.getMaHLV())) {
                    memoryList.set(i, hlv);
                    break;
                }
            }
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[HuanLuyenVienDAO] Lỗi cập nhật HLV: " + e.getMessage());
            return false;
        }
    }

    public boolean xoa(String maHLV) {
        if (maHLV == null || maHLV.trim().isEmpty()) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            HuanLuyenVien hlv = session.get(HuanLuyenVien.class, maHLV.trim());
            if (hlv != null) {
                session.remove(hlv);
                tx.commit();
                memoryList.removeIf(item -> item.getMaHLV().equalsIgnoreCase(maHLV.trim()));
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[HuanLuyenVienDAO] Lỗi xóa HLV: " + e.getMessage());
            return false;
        }
    }
}
