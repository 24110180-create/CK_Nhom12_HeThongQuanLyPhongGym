package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HuanLuyenVien;
import com.nhom12.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HuanLuyenVienDAO {
    private static final List<HuanLuyenVien> memoryList = new CopyOnWriteArrayList<>();

    static {
        memoryList.add(new HuanLuyenVien("HLV01", "Trần Tuấn Kiệt", "0912345678", "Bodybuilding / PT Tăng cơ", 6, "CN01 - Quận 1", "Ca chiều (14:00 - 22:00)", 4.9, "Đang làm việc"));
        memoryList.add(new HuanLuyenVien("HLV02", "Nguyễn Thu Thảo", "0987654321", "Yoga Thảo dược / Pilates", 5, "CN01 - Quận 1", "Ca sáng (06:00 - 14:00)", 5.0, "Đang làm việc"));
        memoryList.add(new HuanLuyenVien("HLV03", "Phan Thanh Sơn", "0903112233", "Kickboxing & HIIT Cardio", 4, "CN02 - Tân Bình", "Ca chiều (14:00 - 22:00)", 4.8, "Đang làm việc"));
        memoryList.add(new HuanLuyenVien("HLV04", "Lê Mai Hương", "0934556677", "Zumba Dance & Aerobic", 3, "CN02 - Tân Bình", "Ca sáng (06:00 - 14:00)", 4.9, "Đang làm việc"));
        memoryList.add(new HuanLuyenVien("HLV05", "Đặng Minh Trí", "0977889900", "CrossFit & Phục hồi chức năng", 7, "CN02 - Tân Bình", "Ca sáng (06:00 - 14:00)", 4.9, "Đang làm việc"));
    }

    public List<HuanLuyenVien> layTatCa() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                List<HuanLuyenVien> list = session.createQuery("FROM HuanLuyenVien ORDER BY maHLV ASC", HuanLuyenVien.class).list();
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

        return new ArrayList<>(memoryList);
    }

    public HuanLuyenVien timTheoMa(String maHLV) {
        if (maHLV == null) return null;
        for (HuanLuyenVien h : memoryList) {
            if (h.getMaHLV().equalsIgnoreCase(maHLV)) return h;
        }
        return null;
    }

    public boolean them(HuanLuyenVien hlv) {
        if (hlv == null) return false;
        memoryList.removeIf(item -> item.getMaHLV().equalsIgnoreCase(hlv.getMaHLV()));
        memoryList.add(hlv);

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                session.persist(hlv);
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

    public boolean capNhat(HuanLuyenVien hlv) {
        if (hlv == null) return false;
        for (int i = 0; i < memoryList.size(); i++) {
            if (memoryList.get(i).getMaHLV().equalsIgnoreCase(hlv.getMaHLV())) {
                memoryList.set(i, hlv);
                break;
            }
        }

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                session.merge(hlv);
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

    public boolean xoa(String maHLV) {
        memoryList.removeIf(item -> item.getMaHLV().equalsIgnoreCase(maHLV));

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                HuanLuyenVien h = session.get(HuanLuyenVien.class, maHLV);
                if (h != null) {
                    session.remove(h);
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
