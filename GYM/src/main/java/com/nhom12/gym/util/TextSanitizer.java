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
        if (s.toLowerCase().contains("b?nh th?nh") || s.toLowerCase().contains("bình thạnh") || s.toLowerCase().contains("cn03") ||
            (s.toLowerCase().contains("cơ s") && s.contains("3"))) {
            return "CN03 - Cơ sở 3 (Bình Thạnh)";
        }
        if (s.toLowerCase().contains("cn01") || s.toLowerCase().contains("quận 1") || s.toLowerCase().contains("qu?n 1") ||
            (s.toLowerCase().contains("cơ s") && s.contains("1")) || s.toLowerCase().contains("gò vấp") || s.toLowerCase().contains("g? v")) {
            return "CN01 - Cơ sở 1 (Quận 1)";
        }
        if (s.toLowerCase().contains("cn02") || s.toLowerCase().contains("tân bình") || s.toLowerCase().contains("t?n b") ||
            (s.toLowerCase().contains("cơ s") && s.contains("2"))) {
            return "CN02 - Cơ sở 2 (Tân Bình)";
        }

        // 3. Khắc phục phương thức thanh toán
        if (s.equalsIgnoreCase("Chuyển khoản VietQR") || s.toLowerCase().contains("chuy?n kho?n") || s.equalsIgnoreCase("vietqr") || s.equalsIgnoreCase("chuyen khoan")) {
            return "Chuyển khoản VietQR";
        }
        if (s.equalsIgnoreCase("Tiền mặt") || s.toLowerCase().contains("ti?n m?t") || s.equalsIgnoreCase("tien mat")) {
            return "Tiền mặt";
        }
        if (s.equalsIgnoreCase("Quẹt thẻ POS") || s.toLowerCase().contains("qu?t th?") || s.equalsIgnoreCase("quet the")) {
            return "Quẹt thẻ POS";
        }

        // 4. Khắc phục chuỗi tổng quát chứa dấu '?' (Tên hội viên, lớp học, phòng tập, gói tập, HLV, hóa đơn)
        return restoreCorruptedVietnamese(s);
    }

    /**
     * Khôi phục các từ tiếng Việt thông dụng bị chuyển thành dấu '?' do lỗi encoding từ CSDL
     */
    public static String restoreCorruptedVietnamese(String text) {
        if (text == null || !text.contains("?")) {
            return text;
        }

        String res = text;
        // Giao dịch, POS, Hóa đơn & Bán hàng
        res = res.replace("Đăng k?", "Đăng ký");
        res = res.replace("đăng k?", "đăng ký");
        res = res.replace(" k?", " ký");
        res = res.replace("Khách L? Qu?y POS", "Khách Lẻ Quầy POS");
        res = res.replace("Khách L?", "Khách Lẻ");
        res = res.replace("khách l?", "khách lẻ");
        res = res.replace("L? Qu?y", "Lẻ Quầy");
        res = res.replace("l? qu?y", "lẻ quầy");
        res = res.replace("Qu?y POS", "Quầy POS");
        res = res.replace("qu?y POS", "quầy POS");
        res = res.replace("Qu?y", "Quầy");
        res = res.replace("qu?y", "quầy");
        res = res.replace("Thanh to?n mua h?ng", "Thanh toán mua hàng");
        res = res.replace("thanh to?n mua h?ng", "thanh toán mua hàng");
        res = res.replace("Thanh to?n", "Thanh toán");
        res = res.replace("thanh to?n", "thanh toán");
        res = res.replace("mua h?ng", "mua hàng");
        res = res.replace("Mua h?ng", "Mua hàng");
        res = res.replace("m?t h?ng", "mặt hàng");
        res = res.replace("M?t h?ng", "Mặt hàng");
        res = res.replace("B?n h?ng nư?c & th?c ph?m", "Bán hàng nước & thực phẩm");
        res = res.replace("b?n h?ng nư?c & th?c ph?m", "bán hàng nước & thực phẩm");
        res = res.replace("b?n h?ng", "bán hàng");
        res = res.replace("B?n h?ng", "Bán hàng");
        res = res.replace("B?n l?", "Bán lẻ");
        res = res.replace("b?n l?", "bán lẻ");
        res = res.replace("nư?c", "nước");
        res = res.replace("Nư?c", "Nước");
        res = res.replace("th?c ph?m", "thực phẩm");
        res = res.replace("Th?c ph?m", "Thực phẩm");
        res = res.replace("th?c", "thực");
        res = res.replace("Th?c", "Thực");
        res = res.replace("ph?m", "phẩm");
        res = res.replace("Ph?m", "Phẩm");
        res = res.replace("Chuy?n kho?n VietQR", "Chuyển khoản VietQR");
        res = res.replace("Chuy?n kho?n", "Chuyển khoản");
        res = res.replace("chuy?n kho?n", "chuyển khoản");
        res = res.replace("Chuy?n", "Chuyển");
        res = res.replace("chuy?n", "chuyển");
        res = res.replace("kho?n", "khoản");
        res = res.replace("Kho?n", "Khoản");
        res = res.replace("Ti?n m?t", "Tiền mặt");
        res = res.replace("ti?n m?t", "tiền mặt");
        res = res.replace("ti?n", "tiền");
        res = res.replace("Ti?n", "Tiền");
        res = res.replace("Qu?t th?", "Quẹt thẻ");
        res = res.replace("qu?t th?", "quẹt thẻ");
        res = res.replace("Qu?t", "Quẹt");
        res = res.replace("qu?t", "quẹt");
        res = res.replace("H?a đ?n", "Hóa đơn");
        res = res.replace("h?a đ?n", "hóa đơn");
        res = res.replace("Gia h?n", "Gia hạn");
        res = res.replace("gia h?n", "gia hạn");
        res = res.replace("Khách v?ng lai", "Khách vãng lai");
        res = res.replace("khách v?ng lai", "khách vãng lai");
        res = res.replace("v?ng lai", "vãng lai");
        res = res.replace("v?ng", "vãng");
        res = res.replace("V?ng", "Vãng");
        res = res.replace("D?ch v?", "Dịch vụ");
        res = res.replace("d?ch v?", "dịch vụ");
        res = res.replace("Khuy?n m?i", "Khuyến mãi");
        res = res.replace("khuy?n m?i", "khuyến mãi");
        res = res.replace("Gi?m gi?", "Giảm giá");
        res = res.replace("gi?m gi?", "giảm giá");
        res = res.replace("T?ng ti?n", "Tổng tiền");
        res = res.replace("t?ng ti?n", "tổng tiền");
        res = res.replace("Th?nh ti?n", "Thành tiền");
        res = res.replace("th?nh ti?n", "thành tiền");
        res = res.replace("N?i dung", "Nội dung");
        res = res.replace("n?i dung", "nội dung");
        res = res.replace("Ng?y l?p", "Ngày lập");
        res = res.replace("ng?y l?p", "ngày lập");
        res = res.replace("Tr?ng th?i", "Trạng thái");
        res = res.replace("tr?ng th?i", "trạng thái");
        res = res.replace("Đ? thanh to?n", "Đã thanh toán");
        res = res.replace("đ? thanh to?n", "đã thanh toán");
        res = res.replace("Chưa thanh to?n", "Chưa thanh toán");
        res = res.replace("chưa thanh to?n", "chưa thanh toán");
        res = res.replace("H?i viên", "Hội viên");
        res = res.replace("h?i viên", "hội viên");
        res = res.replace("H?i Viên", "Hội Viên");
        res = res.replace("Chi nh?nh", "Chi nhánh");
        res = res.replace("chi nh?nh", "chi nhánh");
        res = res.replace("Chi Nh?nh", "Chi Nhánh");
        res = res.replace("Nh?n viên", "Nhân viên");
        res = res.replace("nh?n viên", "nhân viên");
        res = res.replace("Nh?n Viên", "Nhân Viên");
        res = res.replace("T?i kho?n", "Tài khoản");
        res = res.replace("t?i kho?n", "tài khoản");
        res = res.replace("M?t kh?u", "Mật khẩu");
        res = res.replace("m?t kh?u", "mật khẩu");
        res = res.replace("th?ng", "tháng");
        res = res.replace("Th?ng", "Tháng");

        // Gói tập & dịch vụ
        res = res.replace("Tr?i Nghi?m", "Trải Nghiệm");
        res = res.replace("Tr?i nghi?m", "Trải nghiệm");
        res = res.replace("Kim C??ng", "Kim Cương");
        res = res.replace("Kim C?ng", "Kim Cương");
        res = res.replace("Tiêu Chu?n", "Tiêu Chuẩn");
        res = res.replace("Cơ B?n", "Cơ Bản");
        res = res.replace("Th?ng", "Tháng");

        // Lớp học & bộ môn
        res = res.replace("Kháng L?c", "Kháng Lực");
        res = res.replace("Kháng l?c", "Kháng lực");
        res = res.replace("Tr? Li?u", "Trị Liệu");
        res = res.replace("C?t S?ng", "Cột Sống");
        res = res.replace("Đ?t M?", "Đốt Mỡ");
        res = res.replace("T? V?", "Tự Vệ");
        res = res.replace("D?o Dai", "Dẻo Dai");
        res = res.replace("Thon G?n", "Thon Gọn");

        // Phòng tập & vị trí
        res = res.replace("T?ng 1", "Tầng 1");
        res = res.replace("T?ng 2", "Tầng 2");
        res = res.replace("T?ng 3", "Tầng 3");
        res = res.replace("T?ng", "Tầng");
        res = res.replace("V? Thu?t", "Võ Thuật");
        res = res.replace("Ph?ng", "Phòng");
        res = res.replace("Qu?n 1", "Quận 1");
        res = res.replace("Qu?n 2", "Quận 2");
        res = res.replace("Qu?n", "Quận");
        res = res.replace("Cơ s?", "Cơ sở");
        res = res.replace("B?nh Th?nh", "Bình Thạnh");

        // Họ và tên người (Hội viên, HLV, nhân viên)
        res = res.replace("Nguy?n", "Nguyễn");
        res = res.replace("Thu Th?o", "Thu Thảo");
        res = res.replace("Th?o", "Thảo");
        res = res.replace("Tr?n", "Trần");
        res = res.replace("Tu?n Ki?t", "Tuấn Kiệt");
        res = res.replace("Tu?n", "Tuấn");
        res = res.replace("Ki?t", "Kiệt");
        res = res.replace("Ho?ng", "Hoàng");
        res = res.replace("Huy?n", "Huyền");
        res = res.replace("D??ng", "Dương");
        res = res.replace("H??ng", "Hương");
        res = res.replace("Ph??ng", "Phương");
        res = res.replace("V?n", "Văn");
        res = res.replace("Th?", "Thị");
        res = res.replace("Qu?n", "Quân");
        res = res.replace("S?n", "Sơn");
        res = res.replace("Thành", "Thành");
        res = res.replace("Đ?c", "Đức");
        res = res.replace("Ph?t", "Phát");
        res = res.replace("B?o", "Bảo");
        res = res.replace("Lu?n", "Luận");

        // Điểm danh
        res = res.replace("Đã đi?m danh", "Đã điểm danh");
        res = res.replace("Chưa đi?m danh", "Chưa điểm danh");
        res = res.replace("Đi?m danh", "Điểm danh");
        res = res.replace("V?ng m?t", "Vắng mặt");
        res = res.replace("C? m?t", "Có mặt");
        res = res.replace("H?c viên", "Học viên");

        return res;
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
        if (b.contains("cn03") || b.contains("bình thạnh") || b.contains("b?nh th?nh") || (b.contains("cơ s") && b.contains("3")) || b.contains("cs3")) {
            return "CN03 - Cơ sở 3 (Bình Thạnh)";
        }
        if (b.contains("cn02") || b.contains("tân bình") || b.contains("t?n") || (b.contains("cơ s") && b.contains("2")) || b.contains("cs2")) {
            return "CN02 - Cơ sở 2 (Tân Bình)";
        }
        if (b.contains("cn01") || b.contains("quận 1") || b.contains("qu?n") || (b.contains("cơ s") && b.contains("1")) || b.contains("gò vấp") || b.contains("g? v") || b.contains("cs1")) {
            return "CN01 - Cơ sở 1 (Quận 1)";
        }

        return restoreCorruptedVietnamese(branch.trim());
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
