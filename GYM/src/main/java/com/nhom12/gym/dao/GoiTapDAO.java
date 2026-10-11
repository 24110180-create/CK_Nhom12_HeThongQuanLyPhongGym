package com.nhom12.gym.dao;

import com.nhom12.gym.entity.GoiTap;
import com.nhom12.gym.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object cho Thực thể Gói Tập (GoiTap)
 * Hỗ trợ Observer Pattern tự động cập nhật danh sách gói tập sang các Panel liên quan
 */
public class GoiTapDAO {
    // Bộ nhớ đệm offline fallback thread-safe
    private static final List<GoiTap> memoryList = new CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryList.isEmpty()) return;
        memoryList.add(new GoiTap("GT01", "Gói 1 Tháng Tiêu Chuẩn", 1, 500000, "Toàn hệ thống", "Tập không giới hạn khung giờ, tặng 1 buổi PT định hướng", "Đang mở"));
        memoryList.add(new GoiTap("GT02", "Gói 3 Tháng Siêu Tiết Kiệm", 3, 1350000, "Toàn hệ thống", "Tặng bình lắc FitLife + Miễn phí đo InBody 2 tuần/lần", "Đang mở"));
        memoryList.add(new GoiTap("GT03", "Gói 6 Tháng Bứt Phá", 6, 2500000, "Toàn hệ thống", "Tặng áo tập FitLife + Tủ Locker cá nhân 6 tháng", "Đang mở"));
        memoryList.add(new GoiTap("GT04", "Gói 12 Tháng VIP Diamond", 12, 4500000, "Toàn hệ thống", "Full dịch vụ: Bể bơi, Xông hơi thảo dược, Miễn phí khăn & nước uống", "Đang mở"));
        memoryList.add(new GoiTap("GT05", "Gói PT Cá Nhân 1:1 (30 Buổi)", 3, 7500000, "Tại chi nhánh đăng ký", "Huấn luyện viên riêng thiết kế giáo án giảm mỡ/tăng cơ chuyên sâu", "Đang mở"));
        memoryList.add(new GoiTap("GT06", "Gói Sinh Viên Siêu Rẻ", 1, 350000, "Toàn hệ thống", "Khung giờ vàng 08:00 - 16:00 hằng ngày cho HSSV có thẻ", "Đang mở"));
    }

    public interface GoiTapChangeListener {
        void onGoiTapChanged();
    }

    private static final List<GoiTapChangeListener> changeListeners = new CopyOnWriteArrayList<>();

    public static void addChangeListener(GoiTapChangeListener listener) {
        if (listener != null && !changeListeners.contains(listener)) {
            changeListeners.add(listener);
        }
    }

    public static void removeChangeListener(GoiTapChangeListener listener) {
        changeListeners.remove(listener);
    }

    private static void notifyChange() {
        for (GoiTapChangeListener l : changeListeners) {
            try {
                l.onGoiTapChanged();
            } catch (Exception e) {
                System.err.println("[GoiTapDAO] Lỗi thông báo thay đổi gói tập: " + e.getMessage());
            }
        }
    }

    public List<GoiTap> layTatCa() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<GoiTap> list = session.createQuery("FROM GoiTap ORDER BY giaTien ASC", GoiTap.class).list();
            if (list != null && !list.isEmpty()) {
                memoryList.clear();
                memoryList.addAll(list);
                return list;
            }
        } catch (Exception e) {
            System.err.println("[GoiTapDAO] Lỗi truy vấn CSDL, sử dụng danh sách bộ nhớ đệm: " + e.getMessage());
        }
        return new ArrayList<>(memoryList);
    }

    public GoiTap timTheoMa(String maGoiTap) {
        if (maGoiTap == null || maGoiTap.trim().isEmpty()) return null;
        String code = maGoiTap.trim();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            GoiTap gt = session.get(GoiTap.class, code);
            if (gt != null) return gt;
        } catch (Exception e) {
            System.err.println("[GoiTapDAO] Lỗi tìm gói tập theo mã '" + code + "': " + e.getMessage());
        }

        for (GoiTap gt : memoryList) {
            if (gt.getMaGoiTap().equalsIgnoreCase(code)) {
                return gt;
            }
        }
        return null;
    }

    public GoiTap timTheoTen(String tenGoiTap) {
        if (tenGoiTap == null || tenGoiTap.trim().isEmpty()) return null;
        String t = tenGoiTap.trim().toLowerCase();
        List<GoiTap> all = layTatCa();
        for (GoiTap gt : all) {
            if (gt.getTenGoiTap() != null && gt.getTenGoiTap().toLowerCase().contains(t)) {
                return gt;
            }
        }
        return null;
    }

    public boolean them(GoiTap gt) {
        if (gt == null) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(gt);
            tx.commit();

            memoryList.removeIf(item -> item.getMaGoiTap().equalsIgnoreCase(gt.getMaGoiTap()));
            memoryList.add(gt);
            notifyChange();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[GoiTapDAO] Lỗi thêm gói tập: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhat(GoiTap gt) {
        if (gt == null) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(gt);
            tx.commit();

            for (int i = 0; i < memoryList.size(); i++) {
                if (memoryList.get(i).getMaGoiTap().equalsIgnoreCase(gt.getMaGoiTap())) {
                    memoryList.set(i, gt);
                    break;
                }
            }
            notifyChange();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[GoiTapDAO] Lỗi cập nhật gói tập: " + e.getMessage());
            return false;
        }
    }

    public boolean xoa(String maGoiTap) {
        if (maGoiTap == null || maGoiTap.trim().isEmpty()) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            GoiTap gt = session.get(GoiTap.class, maGoiTap.trim());
            if (gt != null) {
                session.remove(gt);
                tx.commit();
                memoryList.removeIf(item -> item.getMaGoiTap().equalsIgnoreCase(maGoiTap.trim()));
                notifyChange();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[GoiTapDAO] Lỗi xóa gói tập: " + e.getMessage());
            return false;
        }
    }
}
