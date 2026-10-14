package com.nhom12.gym.service;

import com.nhom12.gym.dao.DangKyLopDAO;
import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.LopHocDAO;
import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.LopHoc;
import com.nhom12.gym.util.HibernateUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Service quản lý và đồng bộ Dữ Liệu Mẫu Chuẩn (Standard Seed Data) trên toàn bộ hệ thống FitLife.
 * Đảm bảo liên kết 100% giữa Hội viên (HoiVien), Lớp học (LopHoc) và Danh sách Điểm danh (DangKyLop).
 */
public class SampleDataService {

    private static final Logger LOGGER = Logger.getLogger(SampleDataService.class.getName());

    /**
     * Tạo danh sách 12 Hội Viên mẫu chuẩn với thông tin thực tế, gói tập đa dạng
     */
    public static List<HoiVien> getDanhSachHoiVienChuan() {
        List<HoiVien> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            list.add(new HoiVien(1, "QR001", "Lê Anh Duy", "0974482749", "CN01 - Cơ sở 1 (Quận 1)",
                    "Gói Kim Cương 12 Tháng", sdfTime.parse("2026-01-15 08:30:15"), sdf.parse("2026-12-31"),
                    8500000.0, "Chuyển khoản VietQR", "Active"));
            list.add(new HoiVien(2, "QR002", "Võ Lê Ngọc Hưng", "0912345678", "CN02 - Cơ sở 2 (Tân Bình)",
                    "Gói Standard 3 Tháng", sdfTime.parse("2026-07-15 14:15:20"), sdf.parse("2026-10-15"),
                    2400000.0, "Chuyển khoản VietQR", "Active"));
            list.add(new HoiVien(3, "QR003", "Võ Tấn Phát", "0988776655", "CN01 - Cơ sở 1 (Quận 1)",
                    "Gói Cơ Bản 1 Tháng", sdfTime.parse("2024-12-01 09:45:00"), sdf.parse("2025-01-01"),
                    900000.0, "Tiền mặt", "Expired"));
            list.add(new HoiVien(4, "QR004", "Trần Minh Quân", "0933221100", "CN01 - Cơ sở 1 (Quận 1)",
                    "Gói Premium 6 Tháng", sdfTime.parse("2026-05-20 16:20:45"), sdf.parse("2026-11-20"),
                    4800000.0, "Quẹt thẻ POS", "Active"));
            list.add(new HoiVien(5, "QR005", "Hoàng Thùy Linh", "0977112233", "CN02 - Cơ sở 2 (Tân Bình)",
                    "Gói Sinh Viên 3 Tháng", sdfTime.parse("2026-05-15 10:05:12"), sdf.parse("2026-08-15"),
                    1500000.0, "Chuyển khoản VietQR", "Active"));
            list.add(new HoiVien(6, "VIP007", "Đỗ Minh Trí", "0909998877", "Toàn hệ thống (VIP)",
                    "Gói VIP Toàn Hệ Thống 2 Năm", sdfTime.parse("2026-01-01 11:30:00"), sdf.parse("2027-12-31"),
                    15000000.0, "Chuyển khoản VietQR", "Active"));
            list.add(new HoiVien(7, "QR006", "Nguyễn Thu Hà", "0918223344", "CN01 - Cơ sở 1 (Quận 1)",
                    "Gói Standard 3 Tháng", sdfTime.parse("2026-06-01 09:00:00"), sdf.parse("2026-09-30"),
                    2400000.0, "Chuyển khoản VietQR", "Active"));
            list.add(new HoiVien(8, "QR007", "Phạm Quốc Bảo", "0903456789", "CN02 - Cơ sở 2 (Tân Bình)",
                    "Gói Premium 6 Tháng", sdfTime.parse("2026-04-10 15:30:00"), sdf.parse("2026-10-25"),
                    4800000.0, "Chuyển khoản VietQR", "Active"));
            list.add(new HoiVien(9, "QR008", "Đặng Mai Phương", "0989123456", "CN01 - Cơ sở 1 (Quận 1)",
                    "Gói Kim Cương 12 Tháng", sdfTime.parse("2026-02-14 10:00:00"), sdf.parse("2026-12-15"),
                    8500000.0, "Tiền mặt", "Active"));
            list.add(new HoiVien(10, "QR009", "Bùi Hoàng Nam", "0938556677", "CN02 - Cơ sở 2 (Tân Bình)",
                    "Gói Standard 3 Tháng", sdfTime.parse("2026-05-10 11:15:00"), sdf.parse("2026-11-10"),
                    2400000.0, "Chuyển khoản VietQR", "Active"));
            list.add(new HoiVien(11, "QR010", "Vũ Thị Kim Oanh", "0908778899", "CN01 - Cơ sở 1 (Quận 1)",
                    "Gói Sinh Viên 3 Tháng", sdfTime.parse("2026-04-01 16:45:00"), sdf.parse("2026-07-20"),
                    1500000.0, "Tiền mặt", "Active"));
            list.add(new HoiVien(12, "QR011", "Trương Đình Trọng", "0919334455", "CN02 - Cơ sở 2 (Tân Bình)",
                    "Gói Premium 6 Tháng", sdfTime.parse("2026-03-20 17:00:00"), sdf.parse("2026-12-05"),
                    4800000.0, "Quẹt thẻ POS", "Active"));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Lỗi tạo danh sách hội viên mẫu chuẩn", e);
        }
        return list;
    }

    /**
     * Tạo danh sách 5 Lớp Học chuẩn chỉ, thông tin chính xác không có dữ liệu rác
     */
    public static List<LopHoc> getDanhSachLopHocChuan() {
        List<LopHoc> list = new ArrayList<>();
        list.add(new LopHoc("LH01", "Yoga Trị Liệu Cột Sống", "Yoga", "Nguyễn Thu Thảo", "06:30 - 07:30 (T2-T4-T6)", "Studio 1 (Tầng 2)", "CN01 - Cơ sở 1 (Quận 1)", 20, 7));
        list.add(new LopHoc("LH02", "Zumba Cardio Đốt Mỡ", "Zumba", "Lê Mai Hương", "18:00 - 19:00 (T3-T5-T7)", "Studio 2 (Tầng 2)", "CN01 - Cơ sở 1 (Quận 1)", 25, 8));
        list.add(new LopHoc("LH03", "Kickboxing Tự Vệ Nâng Cao", "Boxing", "Phan Thanh Sơn", "19:15 - 20:15 (T2-T4-T6)", "Khu Võ Thuật Ring A", "CN02 - Cơ sở 2 (Tân Bình)", 15, 5));
        list.add(new LopHoc("LH04", "HIIT Cardio Kháng Lực", "Cardio", "Trần Tuấn Kiệt", "07:00 - 08:00 (T3-T5)", "Khu Functional", "CN02 - Cơ sở 2 (Tân Bình)", 15, 6));
        list.add(new LopHoc("LH05", "Pilates Dẻo Dai & Thon Gọn", "Pilates", "Nguyễn Thu Thảo", "17:30 - 18:30 (T2-T4-T6)", "Studio 1 (Tầng 2)", "CN01 - Cơ sở 1 (Quận 1)", 18, 6));
        return list;
    }

    /**
     * Tạo danh sách 32 Đăng Ký Lớp Học & Điểm Danh đồng bộ tuyệt đối với 12 Hội Viên
     */
    public static List<DangKyLop> getDanhSachDangKyChuan() {
        List<DangKyLop> list = new ArrayList<>();
        Date now = new Date();

        // 1. LỚP LH01 - YOGA TRỊ LIỆU CỘT SỐNG (7 học viên)
        list.add(new DangKyLop(1, "LH01", 1, "Lê Anh Duy", "0974482749", now, "Đã điểm danh", "Có mặt sớm 15p, chuẩn bị thảm"));
        list.add(new DangKyLop(2, "LH01", 2, "Võ Lê Ngọc Hưng", "0912345678", now, "Đã điểm danh", "Mang thảm cá nhân"));
        list.add(new DangKyLop(3, "LH01", 5, "Hoàng Thùy Linh", "0977112233", now, "Đã điểm danh", "Động tác uốn dẻo tốt"));
        list.add(new DangKyLop(4, "LH01", 7, "Nguyễn Thu Hà", "0918223344", now, "Chưa điểm danh", "Học viên mới buổi 2"));
        list.add(new DangKyLop(5, "LH01", 9, "Đặng Mai Phương", "0989123456", now, "Đã điểm danh", "Có mặt đúng giờ"));
        list.add(new DangKyLop(6, "LH01", 11, "Vũ Thị Kim Oanh", "0908778899", now, "Vắng mặt", "Bận việc công ty có phép"));
        list.add(new DangKyLop(7, "LH01", 6, "Đỗ Minh Trí", "0909998877", now, "Đã điểm danh", "Thẻ VIP Diamond"));

        // 2. LỚP LH02 - ZUMBA CARDIO ĐỐT MỠ (8 học viên)
        list.add(new DangKyLop(8, "LH02", 1, "Lê Anh Duy", "0974482749", now, "Đã điểm danh", "Rất năng nổ, nhiệt tình"));
        list.add(new DangKyLop(9, "LH02", 5, "Hoàng Thùy Linh", "0977112233", now, "Đã điểm danh", "Vũ đạo chuẩn nhịp"));
        list.add(new DangKyLop(10, "LH02", 7, "Nguyễn Thu Hà", "0918223344", now, "Đã điểm danh", "Đến sớm khởi động"));
        list.add(new DangKyLop(11, "LH02", 9, "Đặng Mai Phương", "0989123456", now, "Chưa điểm danh", "Đang di chuyển tới phòng"));
        list.add(new DangKyLop(12, "LH02", 11, "Vũ Thị Kim Oanh", "0908778899", now, "Đã điểm danh", "Có mặt đúng giờ"));
        list.add(new DangKyLop(13, "LH02", 2, "Võ Lê Ngọc Hưng", "0912345678", now, "Vắng mặt", "Đi công tác Hà Nội"));
        list.add(new DangKyLop(14, "LH02", 10, "Bùi Hoàng Nam", "0938556677", now, "Đã điểm danh", "Thể lực xung mãn"));
        list.add(new DangKyLop(15, "LH02", 6, "Đỗ Minh Trí", "0909998877", now, "Đã điểm danh", "Khách VIP tập trải nghiệm"));

        // 3. LỚP LH03 - KICKBOXING TỰ VỆ NÂNG CAO (5 học viên)
        list.add(new DangKyLop(16, "LH03", 4, "Trần Minh Quân", "0933221100", now, "Đã điểm danh", "Đầy đủ găng võ và băng quấn"));
        list.add(new DangKyLop(17, "LH03", 8, "Phạm Quốc Bảo", "0903456789", now, "Đã điểm danh", "Kỹ thuật ra đòn nhanh"));
        list.add(new DangKyLop(18, "LH03", 10, "Bùi Hoàng Nam", "0938556677", now, "Đã điểm danh", "Thể lực rất tốt"));
        list.add(new DangKyLop(19, "LH03", 12, "Trương Đình Trọng", "0919334455", now, "Chưa điểm danh", "Chuẩn bị vào sàn võ"));
        list.add(new DangKyLop(20, "LH03", 1, "Lê Anh Duy", "0974482749", now, "Vắng mặt", "Xin nghỉ do việc bận"));

        // 4. LỚP LH04 - HIIT CARDIO KHÁNG LỰC (6 học viên)
        list.add(new DangKyLop(21, "LH04", 4, "Trần Minh Quân", "0933221100", now, "Đã điểm danh", "Cardio cường độ cao"));
        list.add(new DangKyLop(22, "LH04", 2, "Võ Lê Ngọc Hưng", "0912345678", now, "Đã điểm danh", "Mang bình nước điện giải"));
        list.add(new DangKyLop(23, "LH04", 8, "Phạm Quốc Bảo", "0903456789", now, "Đã điểm danh", "Hoàn thành 100% giáo án"));
        list.add(new DangKyLop(24, "LH04", 10, "Bùi Hoàng Nam", "0938556677", now, "Chưa điểm danh", "Đăng ký ca sáng"));
        list.add(new DangKyLop(25, "LH04", 12, "Trương Đình Trọng", "0919334455", now, "Đã điểm danh", "Tập sức bền tốt"));
        list.add(new DangKyLop(26, "LH04", 5, "Hoàng Thùy Linh", "0977112233", now, "Vắng mặt", "Đau cơ xin nghỉ phục hồi"));

        // 5. LỚP LH05 - PILATES DẺO DAI & THON GỌN (6 học viên)
        list.add(new DangKyLop(27, "LH05", 1, "Lê Anh Duy", "0974482749", now, "Đã điểm danh", "Tập trên máy Reformer"));
        list.add(new DangKyLop(28, "LH05", 7, "Nguyễn Thu Hà", "0918223344", now, "Đã điểm danh", "Có mặt đúng giờ"));
        list.add(new DangKyLop(29, "LH05", 9, "Đặng Mai Phương", "0989123456", now, "Đã điểm danh", "Thân hình dẻo dai"));
        list.add(new DangKyLop(30, "LH05", 11, "Vũ Thị Kim Oanh", "0908778899", now, "Chưa điểm danh", "Đang trên đường tới"));
        list.add(new DangKyLop(31, "LH05", 5, "Hoàng Thùy Linh", "0977112233", now, "Đã điểm danh", "Tư thế chuẩn xác"));
        list.add(new DangKyLop(32, "LH05", 6, "Đỗ Minh Trí", "0909998877", now, "Vắng mặt", "Có việc bận báo trước"));

        return list;
    }

    /**
     * Nạp và đồng bộ dữ liệu mẫu chuẩn vào CSDL SQL Server và Bộ nhớ tạm hệ thống.
     * @param forceOverwrite nếu true, ghi đè triệt để làm sạch các bản ghi rác như 'freee'.
     */
    public static boolean napDuLieuMauDongBo(boolean forceOverwrite) {
        LOGGER.info("Bắt đầu nạp và đồng bộ dữ liệu mẫu chuẩn toàn hệ thống...");

        List<HoiVien> hoiViens = getDanhSachHoiVienChuan();
        List<LopHoc> lopHocs = getDanhSachLopHocChuan();
        List<DangKyLop> dangKyLops = getDanhSachDangKyChuan();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        if (sf != null) {
            Transaction tx = null;
            try (Session session = sf.openSession()) {
                tx = session.beginTransaction();

                // 1. Đồng bộ Hội Viên (merge nếu đã có mã QR)
                for (HoiVien hv : hoiViens) {
                    try {
                        HoiVien existing = session.createQuery("FROM HoiVien WHERE maTheQR = :qr", HoiVien.class)
                                .setParameter("qr", hv.getMaTheQR())
                                .uniqueResult();
                        if (existing != null) {
                            existing.setHoTen(hv.getHoTen());
                            existing.setSoDienThoai(hv.getSoDienThoai());
                            existing.setChiNhanhDangKy(hv.getChiNhanhDangKy());
                            existing.setGoiTapDangKy(hv.getGoiTapDangKy());
                            existing.setNgayDangKy(hv.getNgayDangKy());
                            existing.setNgayHetHan(hv.getNgayHetHan());
                            existing.setSoTienDaThanhToan(hv.getSoTienDaThanhToan());
                            existing.setHinhThucThanhToan(hv.getHinhThucThanhToan());
                            existing.setTrangThai(hv.getTrangThai());
                            session.merge(existing);
                        } else {
                            session.persist(hv);
                        }
                    } catch (Exception ex) {
                        LOGGER.log(Level.FINE, "Bỏ qua lỗi hội viên " + hv.getMaTheQR(), ex);
                    }
                }

                // 2. Đồng bộ 5 Lớp Học chuẩn (xóa triệt để lớp rác 'freee' trong CSDL)
                try {
                    session.createQuery("DELETE FROM LopHoc WHERE LOWER(tenLop) LIKE '%freee%'").executeUpdate();
                } catch (Exception ignored) {}
                for (LopHoc lh : lopHocs) {
                    session.merge(lh);
                }

                // 3. Đồng bộ Danh sách Đăng Ký & Điểm danh
                if (forceOverwrite) {
                    // Xóa các đăng ký cũ của 5 lớp chuẩn để nạp bộ mới sạch sẽ
                    try {
                        session.createQuery("DELETE FROM DangKyLop WHERE maLop IN ('LH01', 'LH02', 'LH03', 'LH04', 'LH05')").executeUpdate();
                    } catch (Exception ex) {
                        LOGGER.log(Level.FINE, "Lỗi xóa đăng ký cũ: {0}", ex.getMessage());
                    }

                    for (DangKyLop dk : dangKyLops) {
                        DangKyLop newDk = new DangKyLop(0, dk.getMaLop(), dk.getMaHoiVien(), dk.getTenHoiVien(),
                                dk.getSoDienThoai(), dk.getNgayDangKy(), dk.getTrangThaiDiemDanh(), dk.getGhiChu());
                        session.persist(newDk);
                    }
                } else {
                    // Kiểm tra nếu lớp chưa có học viên nào thì nạp bù
                    for (LopHoc lh : lopHocs) {
                        Long count = session.createQuery("SELECT COUNT(d.id) FROM DangKyLop d WHERE d.maLop = :ml", Long.class)
                                .setParameter("ml", lh.getMaLop())
                                .uniqueResult();
                        if (count == null || count == 0) {
                            for (DangKyLop dk : dangKyLops) {
                                if (dk.getMaLop().equalsIgnoreCase(lh.getMaLop())) {
                                    DangKyLop newDk = new DangKyLop(0, dk.getMaLop(), dk.getMaHoiVien(), dk.getTenHoiVien(),
                                            dk.getSoDienThoai(), dk.getNgayDangKy(), dk.getTrangThaiDiemDanh(), dk.getGhiChu());
                                    session.persist(newDk);
                                }
                            }
                        }
                    }
                }

                tx.commit();
                LOGGER.info("Đã đồng bộ thành công dữ liệu mẫu chuẩn xuống CSDL SQL Server!");
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                LOGGER.log(Level.WARNING, "Không thể lưu vào CSDL, tiến hành nạp vào bộ nhớ tạm: {0}", e.getMessage());
            }
        }

        // Cập nhật bộ nhớ đệm Memory Fallback
        HoiVienDAO.capNhatMemoryCache(hoiViens);
        LopHocDAO.capNhatMemoryCache(lopHocs);
        DangKyLopDAO.capNhatMemoryCache(dangKyLops);

        // Kích hoạt đồng bộ giao diện toàn hệ thống
        DataSyncManager.fireDataChanged(DataSyncManager.DataType.ALL);
        return true;
    }
}
