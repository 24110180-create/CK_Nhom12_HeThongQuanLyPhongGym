package com.nhom12.gym.dao;

import com.nhom12.gym.entity.GoiTap;
import com.nhom12.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GoiTapDAO {
    // Bộ nhớ đệm offline fallback thread-safe
    private static final List<GoiTap> memoryList = new CopyOnWriteArrayList<>();

    static {
        memoryList.add(new GoiTap("GT01", "Gói 1 Tháng Tiêu Chuẩn", 1, 500000, "Toàn hệ thống", "Tập không giới hạn khung giờ, tặng 1 buổi PT định hướng", "Đang mở"));
        memoryList.add(new GoiTap("GT02", "Gói 3 Tháng Siêu Tiết Kiệm", 3, 1350000, "Toàn hệ thống", "Tặng bình lắc FitLife + Miễn phí đo InBody 2 tuần/lần", "Đang mở"));
        memoryList.add(new GoiTap("GT03", "Gói 6 Tháng Bứt Phá", 6, 2500000, "Toàn hệ thống", "Tặng áo tập FitLife + Tủ Locker cá nhân 6 tháng", "Đang mở"));
        memoryList.add(new GoiTap("GT04", "Gói 12 Tháng VIP Diamond", 12, 4500000, "Toàn hệ thống", "Full dịch vụ: Bể bơi, Xông hơi thảo dược, Miễn phí khăn & nước uống", "Đang mở"));
        memoryList.add(new GoiTap("GT05", "Gói PT Cá Nhân 1:1 (30 Buổi)", 3, 7500000, "Tại chi nhánh đăng ký", "Huấn luyện viên riêng thiết kế giáo án giảm mỡ/tăng cơ chuyên sâu", "Đang mở"));
        memoryList.add(new GoiTap("GT06", "Gói Sinh Viên Siêu Rẻ", 1, 350000, "Toàn hệ thống", "Khung giờ vàng 08:00 - 16:00 hằng ngày cho HSSV có thẻ", "Đang mở"));
    }

    public List<GoiTap> layTatCa() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                List<GoiTap> list = session.createQuery("FROM GoiTap ORDER BY giaTien ASC", GoiTap.class).list();
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

        // Fallback danh sách in-memory
        return new ArrayList<>(memoryList);
    }

    public GoiTap timTheoMa(String maGoiTap) {
        if (maGoiTap == null) return null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                GoiTap gt = session.get(GoiTap.class, maGoiTap);
                session.close();
                if (gt != null) return gt;
            } catch (Throwable t) {
                if (session != null) {
                    try { session.close(); } catch (Throwable ignored) {}
                }
            }
        } catch (Exception ignored) {}

        for (GoiTap gt : memoryList) {
            if (gt.getMaGoiTap().equalsIgnoreCase(maGoiTap)) {
                return gt;
            }
        }
        return null;
    }

    public boolean them(GoiTap gt) {
        if (gt == null) return false;
        // Luôn cập nhật memory trước
        memoryList.removeIf(item -> item.getMaGoiTap().equalsIgnoreCase(gt.getMaGoiTap()));
        memoryList.add(gt);

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                session.persist(gt);
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

    public boolean capNhat(GoiTap gt) {
        if (gt == null) return false;
        // Cập nhật memory
        for (int i = 0; i < memoryList.size(); i++) {
            if (memoryList.get(i).getMaGoiTap().equalsIgnoreCase(gt.getMaGoiTap())) {
                memoryList.set(i, gt);
                break;
            }
        }

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                session.merge(gt);
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

    public boolean xoa(String maGoiTap) {
        memoryList.removeIf(item -> item.getMaGoiTap().equalsIgnoreCase(maGoiTap));

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                GoiTap gt = session.get(GoiTap.class, maGoiTap);
                if (gt != null) {
                    session.remove(gt);
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
}
