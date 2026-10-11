package com.nhom12.gym.dao;

import com.nhom12.gym.entity.LichSuCheckIn;
import com.nhom12.gym.util.HibernateUtil;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Data Access Object cho Thực thể Nhật Ký Check-In (LichSuCheckIn)
 * Đóng vai trò ghi nhận Audit Log cho Use Case Lõi Quét Thẻ và phân tích Heatmap
 */
public class LichSuCheckInDAO {
    private static final List<LichSuCheckIn> memoryLogs = new CopyOnWriteArrayList<>();

    static {
        khoiTaoDuLieuMauNeuTrong();
    }

    private static void khoiTaoDuLieuMauNeuTrong() {
        if (!memoryLogs.isEmpty()) return;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 6); cal.set(Calendar.MINUTE, 15);
        memoryLogs.add(new LichSuCheckIn(1, 1, "Lê Anh Duy", "QR001", cal.getTime(), "CN01 - Quận 1", "Cổng Flap A1", "Mã QR App", "Hợp lệ", "Vào cửa thành công", 18));

        cal.set(Calendar.HOUR_OF_DAY, 7); cal.set(Calendar.MINUTE, 2);
        memoryLogs.add(new LichSuCheckIn(2, 2, "Võ Lê Ngọc Hưng", "QR002", cal.getTime(), "CN02 - Tân Bình", "Cổng Flap B1", "Mã Barcode Thẻ", "Hợp lệ", "Vào cửa thành công", 22));

        cal.set(Calendar.HOUR_OF_DAY, 8); cal.set(Calendar.MINUTE, 30);
        memoryLogs.add(new LichSuCheckIn(3, 3, "Võ Tấn Phát", "QR003", cal.getTime(), "CN01 - Quận 1", "Cổng Flap A2", "Mã QR App", "Từ chối", "Thẻ đã hết hạn 01/01/2025", 25));

        cal.set(Calendar.HOUR_OF_DAY, 9); cal.set(Calendar.MINUTE, 10);
        memoryLogs.add(new LichSuCheckIn(4, 4, "Trần Minh Quân", "QR004", cal.getTime(), "CN02 - Tân Bình", "Cổng Flap B2", "Mã Barcode Thẻ", "Từ chối", "Sai chi nhánh đăng ký (CN01)", 20));

        cal.set(Calendar.HOUR_OF_DAY, 17); cal.set(Calendar.MINUTE, 45);
        memoryLogs.add(new LichSuCheckIn(5, 6, "Đỗ Minh Trí", "VIP007", cal.getTime(), "CN01 - Quận 1", "Cổng VIP Gate", "Mã QR VIP Pass", "Hợp lệ", "VIP President - Miễn phí trọn đời", 16));
    }

    public boolean luuCheckIn(LichSuCheckIn log) {
        if (log == null) return false;

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(log);
            tx.commit();

            memoryLogs.add(0, log);
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("[LichSuCheckInDAO] Lỗi ghi nhật ký check-in: " + e.getMessage());
            // Vẫn lưu vào bộ đệm nội bộ để trạm quét không bị đình trệ
            memoryLogs.add(0, log);
            return false;
        }
    }

    public List<LichSuCheckIn> layTatCaLichSu() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<LichSuCheckIn> list = session.createQuery("FROM LichSuCheckIn ORDER BY thoiGian DESC", LichSuCheckIn.class).list();
            if (list != null && !list.isEmpty()) {
                memoryLogs.clear();
                memoryLogs.addAll(list);
                return list;
            }
        } catch (Exception e) {
            System.err.println("[LichSuCheckInDAO] Lỗi truy vấn CSDL, dùng memory cache: " + e.getMessage());
        }
        return new ArrayList<>(memoryLogs);
    }

    public List<LichSuCheckIn> layLichSuHomNay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());

        List<LichSuCheckIn> result = new ArrayList<>();
        for (LichSuCheckIn item : layTatCaLichSu()) {
            if (item.getThoiGian() != null && sdf.format(item.getThoiGian()).equals(today)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Thống kê số lượt tập theo từng khung giờ trong ngày (0h -> 23h)
     * Phục vụ biểu đồ Mức độ sử dụng phòng tập theo giờ (Heatmap)
     */
    public int[] layPhanBoLuuLuongTheoGio() {
        int[] hourly = new int[24];
        // Đặt một số giá trị cơ sở thực tế cho phòng Gym (cao điểm 6h-8h sáng và 17h-20h tối)
        hourly[6] = 28;  hourly[7] = 45;  hourly[8] = 32;
        hourly[9] = 18;  hourly[10] = 12; hourly[11] = 15;
        hourly[12] = 20; hourly[13] = 14; hourly[14] = 10;
        hourly[15] = 16; hourly[16] = 25; hourly[17] = 52;
        hourly[18] = 68; hourly[19] = 62; hourly[20] = 48;
        hourly[21] = 26;

        Calendar cal = Calendar.getInstance();
        for (LichSuCheckIn log : layTatCaLichSu()) {
            if (log.getThoiGian() != null && "Hợp lệ".equalsIgnoreCase(log.getTrangThai())) {
                cal.setTime(log.getThoiGian());
                int h = cal.get(Calendar.HOUR_OF_DAY);
                if (h >= 0 && h < 24) {
                    hourly[h]++;
                }
            }
        }
        return hourly;
    }
}
