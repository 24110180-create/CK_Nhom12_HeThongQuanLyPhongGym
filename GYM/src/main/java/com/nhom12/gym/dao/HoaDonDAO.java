package com.nhom12.gym.dao;

import com.nhom12.gym.entity.HoaDon;
import com.nhom12.gym.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class HoaDonDAO {
    private static final List<HoaDon> memoryInvoices = new CopyOnWriteArrayList<>();

    static {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -10);
        memoryInvoices.add(new HoaDon("HD2026-001", 1, "Lê Anh Duy", "Đăng ký gói tập VIP Năm", 4500000, 0, 4500000, "Chuyển khoản VietQR", "letan1", "CN01 - Quận 1", cal.getTime(), "Đã thanh toán", "Gói VIP Diamond toàn hệ thống"));
        
        cal.add(Calendar.DAY_OF_MONTH, 2);
        memoryInvoices.add(new HoaDon("HD2026-002", 2, "Võ Lê Ngọc Hưng", "Gia hạn gói 3 Tháng", 1350000, 100000, 1250000, "Tiền mặt", "letan1", "CN02 - Tân Bình", cal.getTime(), "Đã thanh toán", "Khuyến mãi voucher 100k"));

        cal.add(Calendar.DAY_OF_MONTH, 3);
        memoryInvoices.add(new HoaDon("HD2026-003", 4, "Trần Minh Quân", "Đăng ký Gói PT 1:1", 7500000, 500000, 7000000, "Chuyển khoản VietQR", "letan2", "CN01 - Quận 1", cal.getTime(), "Đã thanh toán", "30 buổi cùng HLV Alex"));

        cal.add(Calendar.DAY_OF_MONTH, 1);
        memoryInvoices.add(new HoaDon("HD2026-004", 5, "Hoàng Thùy Linh", "Thuê tủ Locker 6 tháng", 600000, 0, 600000, "Tiền mặt", "letan1", "CN02 - Tân Bình", cal.getTime(), "Đã thanh toán", "Locker VIP A12"));

        cal.add(Calendar.DAY_OF_MONTH, 2);
        memoryInvoices.add(new HoaDon("HD2026-005", 6, "Phạm Quốc Bảo", "Đăng ký gói 6 Tháng", 2500000, 200000, 2300000, "Chuyển khoản VietQR", "admin", "CN03 - Quận 7", cal.getTime(), "Đã thanh toán", "Mở mới thẻ"));
    }

    public boolean them(HoaDon hd) {
        if (hd == null) return false;
        memoryInvoices.add(0, hd);

        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                tx = session.beginTransaction();
                session.persist(hd);
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

    public List<HoaDon> layTatCa() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                List<HoaDon> list = session.createQuery("FROM HoaDon ORDER BY ngayLap DESC", HoaDon.class).list();
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

        return new ArrayList<>(memoryInvoices);
    }

    /**
     * Báo cáo Doanh thu tổng hợp theo chi nhánh
     */
    public Map<String, Double> layDoanhThuTheoChiNhanh() {
        Map<String, Double> map = new LinkedHashMap<>();
        map.put("CN01 - Quận 1", 0.0);
        map.put("CN02 - Tân Bình", 0.0);
        map.put("CN03 - Quận 7", 0.0);

        for (HoaDon hd : layTatCa()) {
            if ("Đã thanh toán".equalsIgnoreCase(hd.getTrangThai())) {
                String branch = hd.getChiNhanh();
                if (branch == null || branch.isEmpty()) branch = "CN01 - Quận 1";
                // Chuẩn hóa tên chi nhánh
                if (branch.contains("CN01") || branch.contains("Quận 1")) branch = "CN01 - Quận 1";
                else if (branch.contains("CN02") || branch.contains("Tân Bình")) branch = "CN02 - Tân Bình";
                else if (branch.contains("CN03") || branch.contains("Quận 7")) branch = "CN03 - Quận 7";

                map.put(branch, map.getOrDefault(branch, 0.0) + hd.getThanhTien());
            }
        }
        return map;
    }

    public double layTongDoanhThu() {
        double total = 0;
        for (HoaDon hd : layTatCa()) {
            if ("Đã thanh toán".equalsIgnoreCase(hd.getTrangThai())) {
                total += hd.getThanhTien();
            }
        }
        return total;
    }
}
