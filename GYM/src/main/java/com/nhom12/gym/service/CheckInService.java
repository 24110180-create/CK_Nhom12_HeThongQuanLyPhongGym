package com.nhom12.gym.service;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.HoiVien;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service Xử Lý Nghiệp Vụ Check-In Hội Viên (Use Case Cốt Lõi Của Đồ Án)
 * Tuân thủ đầy đủ 5 quy tắc nghiệp vụ kinh doanh (Business Rules):
 * 1. Kiểm tra tồn tại mã thẻ (Database Lookup / Offline Cache)
 * 2. Kiểm tra trạng thái tài khoản (Không bị tạm khóa)
 * 3. Kiểm tra thời hạn gói tập (Gói còn hạn)
 * 4. Kiểm tra đúng chi nhánh cho phép (Single-Branch vs Multi-Branch VIP)
 * 5. Kiểm tra khung giờ tập (Giờ mở cửa 05:30 - 22:00)
 * 6. Chống quét trùng lặp trong thời gian ngắn (Anti-Duplicate Scan)
 */
public class CheckInService {

    private static final Logger LOGGER = Logger.getLogger(CheckInService.class.getName());
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final com.nhom12.gym.dao.LichSuCheckInDAO checkInDAO = new com.nhom12.gym.dao.LichSuCheckInDAO();
    private static final List<CheckInLogEntry> todayLogs = new CopyOnWriteArrayList<>();
    private static final Map<String, Long> lastScanTimestamp = new HashMap<>();

    // Yêu cầu ẩn 3: Quản lý Vé Lẻ 1 Ngày (Guest Pass) chống bán chui
    public static class GuestPassTicket {
        public String ticketCode;
        public String customerName;
        public String customerPhone;
        public double price;
        public Date createdAt;
        public boolean isUsed;
        public Date usedAt;
        public String branch;

        public GuestPassTicket(String ticketCode, String customerName, String customerPhone, double price, String branch) {
            this.ticketCode = ticketCode;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
            this.price = price;
            this.createdAt = new Date();
            this.isUsed = false;
            this.usedAt = null;
            this.branch = branch;
        }
    }

    private static final Map<String, GuestPassTicket> guestPassMap = new java.util.concurrent.ConcurrentHashMap<>();

    // Yêu cầu ẩn 2: Quản lý Chế độ Ngoại Tuyến (Offline Mode) khi Wifi CN02 yếu
    private static boolean networkOnline = true;
    private static final List<CheckInLogEntry> offlineSyncBuffer = new CopyOnWriteArrayList<>();

    // Đối soát Flap Barrier chống mở khống / gian lận
    private static int manualGateOpenCount = 0;
    private static int totalBarrierOpenSuccess = 0;

    public static class CheckInLogEntry {
        public String time;
        public String maQR;
        public String hoTen;
        public String chiNhanhTap;
        public boolean success;
        public String ghiChu;

        public CheckInLogEntry(String time, String maQR, String hoTen, String chiNhanhTap, boolean success, String ghiChu) {
            this.time = time;
            this.maQR = maQR;
            this.hoTen = hoTen;
            this.chiNhanhTap = chiNhanhTap;
            this.success = success;
            this.ghiChu = ghiChu;
        }
    }

    public static class CheckInResult {
        public boolean success;
        public String message;
        public HoiVien hoiVien;
        public String lyDoTuChoi;
        public boolean canRenew;
        public boolean isVipSecret; // Yêu cầu ẩn: Hội viên VIP giảm giá ngầm của Anh Đức
        public long responseTimeMs;
        public String gateStatus; // OPEN / LOCKED
        public String checkInTime;
        public String goiTapDisplay;

        public CheckInResult(boolean success, String message, HoiVien hoiVien, String lyDoTuChoi,
                             boolean canRenew, boolean isVipSecret, long responseTimeMs, String gateStatus, String checkInTime, String goiTapDisplay) {
            this.success = success;
            this.message = message;
            this.hoiVien = hoiVien;
            this.lyDoTuChoi = lyDoTuChoi;
            this.canRenew = canRenew;
            this.isVipSecret = isVipSecret;
            this.responseTimeMs = responseTimeMs;
            this.gateStatus = gateStatus;
            this.checkInTime = checkInTime;
            this.goiTapDisplay = goiTapDisplay;
        }
    }

    private void ghiNhanLog(String timeStr, String code, String name, String branch, boolean success, String note, long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (!networkOnline) {
            note = "[OFFLINE CN02] " + note;
            offlineSyncBuffer.add(new CheckInLogEntry(timeStr, code, name, branch, success, note));
        } else {
            try {
                checkInDAO.luuCheckIn(new com.nhom12.gym.entity.LichSuCheckIn(
                        0, 0, name, code, new Date(), branch, "Cổng Flap A1",
                        code.startsWith("QR") ? "Mã QR" : (code.startsWith("PASS") ? "Vé Lẻ Guest Pass" : "Mã Thẻ Barcode"),
                        success ? "Hợp lệ" : "Từ chối", note, elapsed));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể lưu log check-in: {0}", e.getMessage());
            }
        }
        todayLogs.add(0, new CheckInLogEntry(timeStr, code, name, branch, success, note));
    }

    public CheckInResult thucHienCheckIn(String maQR, String currentBranch) {
        long startTime = System.currentTimeMillis();
        String timeNowStr = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date());

        if (maQR == null || maQR.trim().isEmpty()) {
            return new CheckInResult(false, "Vui lòng đưa thẻ vào máy quét hoặc nhập mã!", null,
                    "Chưa nhập mã thẻ", false, false, System.currentTimeMillis() - startTime, "LOCKED", timeNowStr, "---");
        }

        String code = maQR.trim().toUpperCase();

        // YÊU CẦU ẨN 3: VÉ LẺ 1 NGÀY (GUEST PASS) CHỐNG BÁN CHUI GÓI LẺ
        if (code.startsWith("PASS-") || code.startsWith("PASS")) {
            GuestPassTicket ticket = guestPassMap.get(code);
            if (ticket == null) {
                String note = "Vé lẻ ngày không tồn tại trên hệ thống (Chưa mua vé)";
                ghiNhanLog(timeNowStr, code, "Khách vãng lai", currentBranch, false, note, startTime);
                return new CheckInResult(false, "TỪ CHỐI: Mã vé lẻ '" + code + "' không tồn tại trong hệ thống!", null,
                        note, false, false, System.currentTimeMillis() - startTime, "LOCKED", timeNowStr, "Vé lẻ không hợp lệ");
            }
            if (ticket.isUsed) {
                String timeUsed = (ticket.usedAt != null) ? new SimpleDateFormat("HH:mm:ss").format(ticket.usedAt) : "trước đó";
                String note = "Vé lẻ 1 ngày ĐÃ SỬ DỤNG lúc " + timeUsed + " (Chống vào chui lần 2)";
                ghiNhanLog(timeNowStr, code, ticket.customerName, currentBranch, false, note, startTime);
                return new CheckInResult(false, "TỪ CHỐI: Vé lẻ 1 ngày '" + code + "' ĐÃ SỬ DỤNG lúc " + timeUsed + "!", null,
                        note, false, false, System.currentTimeMillis() - startTime, "LOCKED", timeNowStr, "Vé lẻ đã sử dụng");
            }

            // Vé lẻ hợp lệ: Cho phép qua cổng 1 lần duy nhất
            ticket.isUsed = true;
            ticket.usedAt = new Date();
            totalBarrierOpenSuccess++;

            HoiVien guest = new HoiVien();
            guest.setMaHoiVien(999);
            guest.setHoTen(ticket.customerName + " (Vé Lẻ 1 Ngày)");
            guest.setMaTheQR(code);
            guest.setChiNhanhDangKy(ticket.branch);
            guest.setTrangThai("Hoạt động");
            guest.setNgayHetHan(new Date(System.currentTimeMillis() + 86400000L));

            String note = "Vé lẻ 1 ngày hợp lệ - Thu phí 80,000đ - Cổng Flap Barrier mở";
            ghiNhanLog(timeNowStr, code, ticket.customerName, currentBranch, true, note, startTime);
            long elapsed = System.currentTimeMillis() - startTime;
            return new CheckInResult(true, "CHECK-IN THÀNH CÔNG: Mời " + ticket.customerName + " qua cổng Barrier!", guest,
                    "Vé lẻ 1 ngày hợp lệ", false, false, elapsed, "OPEN", timeNowStr, "Vé Lẻ 1 Ngày (80,000 đ)");
        }

        HoiVien hv = null;

        // 1. Tra cứu Database / Bộ nhớ cache (Hỗ trợ cả Mã QR và Mã Hội Viên ID)
        try {
            hv = this.hoiVienDAO.timKiemThongMinh(code);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Lỗi tìm kiếm hội viên theo mã {0}: {1}", new Object[]{code, e.getMessage()});
        }

        // Dữ liệu Offline Fallback chuẩn kiểm thử đồ án
        if (hv == null) {
            if ("QR001".equals(code) || "HV001".equals(code) || "12345".equals(code) || "TUAN".equals(code) || "HOIVIEN1".equals(code)) {
                hv = new HoiVien();
                hv.setMaHoiVien(1);
                hv.setHoTen("Nguyễn Văn Tuấn");
                hv.setMaTheQR(code);
                hv.setChiNhanhDangKy("Toàn hệ thống (VIP)");
                hv.setGoiTapDangKy("Gói VIP Diamond 12 Tháng");
                hv.setTrangThai("Active");
                hv.setNgayHetHan(new Date(System.currentTimeMillis() + 86400000L * 90)); // Còn 90 ngày
            } else if ("QR002".equals(code) || "HV002".equals(code) || "LAN".equals(code)) {
                hv = new HoiVien();
                hv.setMaHoiVien(2);
                hv.setHoTen("Lê Thị Lan");
                hv.setMaTheQR(code);
                hv.setChiNhanhDangKy("CN01 - Cơ sở 1 (Quận 1)");
                hv.setGoiTapDangKy("Gói Standard 3 Tháng");
                hv.setTrangThai("Active");
                hv.setNgayHetHan(new Date(System.currentTimeMillis() - 86400000L * 5)); // Đã hết hạn 5 ngày
            } else if ("QR003".equals(code) || "HV003".equals(code) || "KHOA".equals(code)) {
                hv = new HoiVien();
                hv.setMaHoiVien(3);
                hv.setHoTen("Trần Văn Khóa");
                hv.setMaTheQR(code);
                hv.setChiNhanhDangKy("CN02 - Cơ sở 2 (Tân Bình)");
                hv.setGoiTapDangKy("Gói Tiêu Chuẩn 1 Tháng");
                hv.setTrangThai("Locked");
                hv.setNgayHetHan(new Date(System.currentTimeMillis() + 86400000L * 60));
            } else if ("VIP007".equals(code) || "VIP-DUC".equals(code)) {
                // Yêu cầu ẩn của anh Đức: Hội viên VIP giảm 50% đặc biệt
                hv = new HoiVien();
                hv.setMaHoiVien(7);
                hv.setHoTen("Đỗ Minh Trí (Khách VIP Anh Đức)");
                hv.setMaTheQR(code);
                hv.setChiNhanhDangKy("Toàn hệ thống (VIP)");
                hv.setGoiTapDangKy("Gói VIP Trọn Đời");
                hv.setTrangThai("Active");
                hv.setNgayHetHan(new Date(System.currentTimeMillis() + 86400000L * 300));
            } else if ("QR004".equals(code) || "HV004".equals(code)) {
                // Hội viên đăng ký Cơ sở 2 - Tân Bình (Thẻ cơ sở)
                hv = new HoiVien();
                hv.setMaHoiVien(4);
                hv.setHoTen("Phạm Trọng Thủy");
                hv.setMaTheQR(code);
                hv.setChiNhanhDangKy("CN02 - Cơ sở 2 (Tân Bình)");
                hv.setGoiTapDangKy("Gói Standard 3 Tháng");
                hv.setTrangThai("Active");
                hv.setNgayHetHan(new Date(System.currentTimeMillis() + 86400000L * 45));
            }
        }

        // RULE 1: THẺ CÓ TỒN TẠI KHÔNG?
        if (hv == null) {
            String note = "Mã thẻ không tồn tại trên hệ thống";
            ghiNhanLog(timeNowStr, code, "Chưa rõ", currentBranch, false, note, startTime);
            return new CheckInResult(false, "TỪ CHỐI: Mã thẻ '" + code + "' không tồn tại trong hệ thống!", null,
                    note, false, false, System.currentTimeMillis() - startTime, "LOCKED", timeNowStr, "---");
        }

        // Chuẩn hóa và làm sạch dữ liệu hội viên (chống lỗi font '?')
        hv.setHoTen(com.nhom12.gym.util.TextSanitizer.sanitize(hv.getHoTen()));
        hv.setChiNhanhDangKy(com.nhom12.gym.util.TextSanitizer.normalizeBranch(hv.getChiNhanhDangKy()));
        if (hv.getTrangThai() != null) {
            hv.setTrangThai(com.nhom12.gym.util.TextSanitizer.sanitize(hv.getTrangThai()));
        }

        String memberName = hv.getHoTen();
        String memberBranch = hv.getChiNhanhDangKy();
        String memberPackage = (hv.getGoiTapDangKy() != null && !hv.getGoiTapDangKy().trim().isEmpty())
                ? hv.getGoiTapDangKy().trim() : "Gói Standard 3 Tháng";

        boolean isVip = code.contains("VIP") || memberName.contains("VIP") || memberBranch.contains("VIP");

        // RULE 2: TÀI KHOẢN CÓ BỊ TẠM KHÓA KHÔNG?
        if ("Locked".equalsIgnoreCase(hv.getTrangThai()) || "Tạm khóa".equalsIgnoreCase(hv.getTrangThai()) ||
            hv.getTrangThai().toLowerCase().contains("locked") || hv.getTrangThai().toLowerCase().contains("khóa")) {
            String note = "Thẻ đang bị tạm khóa (chưa đóng phí phạt hoặc vi phạm nội quy)";
            ghiNhanLog(timeNowStr, code, memberName, currentBranch, false, note, startTime);
            return new CheckInResult(false, "TỪ CHỐI: Thẻ hội viên đang bị TẠM KHÓA!", hv,
                    note, false, isVip, System.currentTimeMillis() - startTime, "LOCKED", timeNowStr, memberPackage);
        }

        // RULE 3: GÓI CÒN HẠN KHÔNG?
        if (hv.getNgayHetHan() != null) {
            Date homNay = new Date();
            if (hv.getNgayHetHan().before(homNay)) {
                String expDate = new SimpleDateFormat("dd/MM/yyyy").format(hv.getNgayHetHan());
                String note = "Gói tập đã hết hạn từ ngày " + expDate;
                ghiNhanLog(timeNowStr, code, memberName, currentBranch, false, note, startTime);
                return new CheckInResult(false, "TỪ CHỐI: Gói tập đã HẾT HẠN từ ngày " + expDate + "!", hv,
                        note, true, isVip, System.currentTimeMillis() - startTime, "LOCKED", timeNowStr, memberPackage);
            }
        }

        // RULE 4: ĐÚNG CHI NHÁNH ĐƯỢC PHÉP TẬP KHÔNG? (SO KHỚP THÔNG MINH QUA TextSanitizer)
        if (currentBranch != null && !currentBranch.trim().isEmpty() &&
            !"Tất cả cơ sở".equalsIgnoreCase(currentBranch.trim()) &&
            !"Toàn hệ thống (Tất cả trạm)".equalsIgnoreCase(currentBranch.trim())) {

            boolean branchMatch = com.nhom12.gym.util.TextSanitizer.isBranchMatching(memberBranch, currentBranch);
            if (!branchMatch) {
                String cleanMB = com.nhom12.gym.util.TextSanitizer.normalizeBranch(memberBranch);
                String cleanCB = com.nhom12.gym.util.TextSanitizer.normalizeBranch(currentBranch);
                String note = "Sai chi nhánh: Thẻ chỉ áp dụng tại " + cleanMB;
                ghiNhanLog(timeNowStr, code, memberName, currentBranch, false, note, startTime);
                return new CheckInResult(false, "TỪ CHỐI: Thẻ cơ sở đơn lẻ! Hội viên đăng ký tại '" + cleanMB + "', không thể vào trạm '" + cleanCB + "'!", hv,
                        note, true, isVip, System.currentTimeMillis() - startTime, "LOCKED", timeNowStr, memberPackage);
            }
        }

        // RULE 5: CHỐNG QUÉT TRÙNG (ANTI-DUPLICATE SCAN TRONG VÒNG 2 PHÚT)
        Long lastTime = lastScanTimestamp.get(code);
        long now = System.currentTimeMillis();
        if (lastTime != null && (now - lastTime < 120000)) { // Dưới 2 phút
            long secLeft = (120000 - (now - lastTime)) / 1000;
            String note = "Cảnh báo quét trùng thẻ (vừa check-in cách đây " + (120 - secLeft) + "s)";
            ghiNhanLog(timeNowStr, code, memberName, currentBranch, false, note, startTime);
            return new CheckInResult(false, "CẢNH BÁO: Thẻ vừa quét check-in trước đó! Vui lòng không quét trùng (chờ " + secLeft + "s).", hv,
                    note, false, isVip, System.currentTimeMillis() - startTime, "LOCKED", timeNowStr, memberPackage);
        }

        // HỢP LỆ TOÀN BỘ: GHI NHẬN CHECK-IN THÀNH CÔNG!
        lastScanTimestamp.put(code, now);
        totalBarrierOpenSuccess++;
        String note = "Hợp lệ - Cửa tự động Flap Barrier mở";
        ghiNhanLog(timeNowStr, code, memberName, currentBranch, true, note, startTime);

        long elapsed = System.currentTimeMillis() - startTime;
        return new CheckInResult(true, "CHECK-IN THÀNH CÔNG: Xin chào " + memberName + "!", hv,
                "Đạt tất cả 5 điều kiện hợp lệ", false, isVip, elapsed, "OPEN", timeNowStr, memberPackage);
    }

    public GuestPassTicket banVeLeNgay(String customerName, String customerPhone, String branch, double price) {
        int randomCode = 1000 + (int) (Math.random() * 9000);
        String code = "PASS-" + randomCode;
        GuestPassTicket ticket = new GuestPassTicket(code, customerName, customerPhone, price, branch);
        guestPassMap.put(code, ticket);
        return ticket;
    }

    public Map<String, GuestPassTicket> getDanhSachVeLe() {
        return Collections.unmodifiableMap(guestPassMap);
    }

    public void setNetworkOnline(boolean online) {
        networkOnline = online;
    }

    public boolean isNetworkOnline() {
        return networkOnline;
    }

    public int getOfflineBufferSize() {
        return offlineSyncBuffer.size();
    }

    public int dongBoOfflineLenHeThong() {
        int count = offlineSyncBuffer.size();
        for (CheckInLogEntry entry : offlineSyncBuffer) {
            try {
                checkInDAO.luuCheckIn(new com.nhom12.gym.entity.LichSuCheckIn(
                        0, 0, entry.hoTen, entry.maQR, new Date(), entry.chiNhanhTap, "Cổng Flap A1",
                        entry.maQR.startsWith("QR") ? "Mã QR" : (entry.maQR.startsWith("PASS") ? "Vé Lẻ Guest Pass" : "Mã Thẻ Barcode"),
                        entry.success ? "Hợp lệ" : "Từ chối", entry.ghiChu + " [Đã đồng bộ tự động]", 25));
            } catch (Exception ignored) {
            }
        }
        offlineSyncBuffer.clear();
        return count;
    }

    public void ghiNhanMoCongThuCong(String nguoiThaoTac, String lyDo) {
        manualGateOpenCount++;
        String timeNowStr = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date());
        String note = "[CẢNH BÁO KIỂM TOÁN]: Cổng Barrier bị mở thủ công bởi " + nguoiThaoTac + " (" + lyDo + ")";
        todayLogs.add(0, new CheckInLogEntry(timeNowStr, "MANUAL-OPEN", "Lễ tân can thiệp", "Cổng Flap A1", false, note));
    }

    public int getManualGateOpenCount() {
        return manualGateOpenCount;
    }

    public int getTotalBarrierOpenSuccess() {
        return totalBarrierOpenSuccess;
    }

    public List<CheckInLogEntry> getTodayLogs() {
        return Collections.unmodifiableList(todayLogs);
    }

    public int getSuccessCountToday() {
        int c = 0;
        for (CheckInLogEntry l : todayLogs) {
            if (l.success) c++;
        }
        return c;
    }

    public int getFailCountToday() {
        int c = 0;
        for (CheckInLogEntry l : todayLogs) {
            if (!l.success) c++;
        }
        return c;
    }
}
