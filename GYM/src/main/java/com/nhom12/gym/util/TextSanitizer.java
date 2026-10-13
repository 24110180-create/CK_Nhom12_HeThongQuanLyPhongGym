package com.nhom12.gym.util;

/**
 * Tiện ích Chuẩn Hóa Chuỗi Ký Tự Tiếng Việt & Khắc Phục Lỗi Encoding Dấu Hỏi Chấm (?)
 * Giúp khắc phục triệt để các trường hợp CSDL SQL Server bị lỗi font dấu hỏi chấm
 * như "Cơ s? 1 - G? V?p", "Ho?t đ?ng", "CN01 - Qu?n 1", "To?n h? th?ng".
 */
public class TextSanitizer {

    /**
     * Chuẩn hóa và làm sạch chuỗi hiển thị, khôi phục tiếng Việt chuẩn nếu bị lỗi dấu '?'
     */
    public static String sanitize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        String s = input.trim();

        // 1. Khắc phục trạng thái hoạt động
        if (s.equalsIgnoreCase("Active") || s.equalsIgnoreCase("Hoạt động") ||
            s.toLowerCase().contains("ho?t") || s.toLowerCase().contains("hoat")) {
            return "Active (Hoạt động)";
        }
        if (s.equalsIgnoreCase("Expired") || s.equalsIgnoreCase("Hết hạn") ||
            s.toLowerCase().contains("h?t") || s.toLowerCase().contains("het")) {
            return "Expired (Hết hạn)";
        }
        if (s.equalsIgnoreCase("Locked") || s.equalsIgnoreCase("Tạm khóa") ||
            s.toLowerCase().contains("kh?a") || s.toLowerCase().contains("khoa")) {
            return "Locked (Tạm khóa)";
        }

        // 2. Khắc phục chi nhánh cơ sở
        if (s.toLowerCase().contains("toàn hệ thống") || s.toLowerCase().contains("to?n") || s.toLowerCase().contains("vip")) {
            return "Toàn hệ thống (VIP)";
        }
        if (s.toLowerCase().contains("cn01") || s.toLowerCase().contains("quận 1") || s.toLowerCase().contains("qu?n") ||
            (s.toLowerCase().contains("cơ s") && s.contains("1")) || s.toLowerCase().contains("gò vấp") || s.toLowerCase().contains("g? v")) {
            return "CN01 - Cơ sở 1 (Quận 1)";
        }
        if (s.toLowerCase().contains("cn02") || s.toLowerCase().contains("tân bình") || s.toLowerCase().contains("t?n") ||
            (s.toLowerCase().contains("cơ s") && s.contains("2"))) {
            return "CN02 - Cơ sở 2 (Tân Bình)";
        }

        return s;
    }

    /**
     * Chuẩn hóa riêng tên Chi Nhánh để hiển thị đồng bộ trên toàn bộ hệ thống
     */
    public static String normalizeBranch(String branch) {
        if (branch == null || branch.trim().isEmpty()) {
            return "CN01 - Cơ sở 1 (Quận 1)";
        }
        String b = branch.trim().toLowerCase();

        if (b.contains("toàn hệ thống") || b.contains("to?n") || b.contains("vip") || b.contains("all")) {
            return "Toàn hệ thống (VIP)";
        }
        if (b.contains("cn02") || b.contains("tân bình") || b.contains("t?n") || (b.contains("cơ s") && b.contains("2")) || b.contains("cs2")) {
            return "CN02 - Cơ sở 2 (Tân Bình)";
        }
        if (b.contains("cn01") || b.contains("quận 1") || b.contains("qu?n") || (b.contains("cơ s") && b.contains("1")) || b.contains("gò vấp") || b.contains("g? v") || b.contains("cs1")) {
            return "CN01 - Cơ sở 1 (Quận 1)";
        }

        return branch.trim();
    }

    /**
     * So khớp chi nhánh thông minh giữa Hội Viên và Trạm Quét Check-in.
     * Giải quyết triệt để lỗi "Sai chi nhánh" do khác biệt cách đặt tên giữa CSDL và giao diện.
     *
     * @param memberBranch Chi nhánh đăng ký của hội viên
     * @param stationBranch Trạm quét hiện tại của phòng gym
     * @return true nếu được phép check-in vào trạm này
     */
    public static boolean isBranchMatching(String memberBranch, String stationBranch) {
        if (stationBranch == null || stationBranch.trim().isEmpty()) {
            return true;
        }
        String sLower = stationBranch.toLowerCase();
        // Trạm chọn "Toàn hệ thống" hoặc "Tất cả" -> luôn hợp lệ
        if (sLower.contains("tất cả") || sLower.contains("toàn hệ thống") || sLower.contains("all")) {
            return true;
        }

        if (memberBranch == null || memberBranch.trim().isEmpty()) {
            return true;
        }
        String mLower = memberBranch.toLowerCase();
        // Hội viên VIP / Toàn hệ thống -> được tập ở MỌI chi nhánh
        if (mLower.contains("toàn hệ thống") || mLower.contains("to?n") || mLower.contains("vip") || mLower.contains("all")) {
            return true;
        }

        // Nhận diện Cơ sở 1: CN01, Quận 1, Gò Vấp, CS1, Cơ sở 1, Cơ s? 1
        boolean stationIsCS1 = sLower.contains("cn01") || sLower.contains("cơ sở 1") || sLower.contains("quận 1") || sLower.contains("gò vấp") || sLower.contains("cs1") || (sLower.contains("cơ s") && sLower.contains("1"));
        boolean memberIsCS1  = mLower.contains("cn01") || mLower.contains("cơ sở 1") || mLower.contains("quận 1") || mLower.contains("gò vấp") || mLower.contains("cs1") || (mLower.contains("cơ s") && mLower.contains("1"));

        // Nhận diện Cơ sở 2: CN02, Tân Bình, CS2, Cơ sở 2, Cơ s? 2
        boolean stationIsCS2 = sLower.contains("cn02") || sLower.contains("cơ sở 2") || sLower.contains("tân bình") || sLower.contains("cs2") || (sLower.contains("cơ s") && sLower.contains("2"));
        boolean memberIsCS2  = mLower.contains("cn02") || mLower.contains("cơ sở 2") || mLower.contains("tân bình") || mLower.contains("cs2") || (mLower.contains("cơ s") && mLower.contains("2"));

        if (stationIsCS1 && memberIsCS1) {
            return true;
        }
        if (stationIsCS2 && memberIsCS2) {
            return true;
        }

        // So khớp trực tiếp hoặc contains chuỗi
        return mLower.contains(sLower) || sLower.contains(mLower);
    }
}
