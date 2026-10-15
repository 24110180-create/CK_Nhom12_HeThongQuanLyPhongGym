package com.nhom12.gym.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Chuẩn hóa Ký tự Tiếng Việt & So Khớp Chi Nhánh Check-in")
class TextSanitizerTest {

    @Test
    @DisplayName("Khắc phục triệt để lỗi font dấu hỏi chấm (?) trong CSDL")
    void testSanitizeFontLoi() {
        assertEquals("Active (Hoạt động)", TextSanitizer.sanitize("Ho?t đ?ng"));
        assertEquals("Active (Hoạt động)", TextSanitizer.sanitize("Active"));
        assertEquals("Expired (Hết hạn)", TextSanitizer.sanitize("H?t h?n"));
        assertEquals("Locked (Tạm khóa)", TextSanitizer.sanitize("T?m kh?a"));

        assertEquals("CN01 - Cơ sở 1 (Quận 1)", TextSanitizer.sanitize("Cơ s? 1 - G? V?p"));
        assertEquals("CN01 - Cơ sở 1 (Quận 1)", TextSanitizer.sanitize("CN01 - Qu?n 1"));
        assertEquals("CN02 - Cơ sở 2 (Tân Bình)", TextSanitizer.sanitize("CN02 - T?n B?nh"));
        assertEquals("Toàn hệ thống (VIP)", TextSanitizer.sanitize("To?n h? th?ng"));

        // Kiểm tra các chuỗi thực tế từ giao diện
        assertEquals("Nguyễn Thu Thảo", TextSanitizer.sanitize("Nguy?n Thu Th?o"));
        assertEquals("Trần Tuấn Kiệt", TextSanitizer.sanitize("Tr?n Tu?n Ki?t"));
        assertEquals("HIIT Cardio Kháng Lực", TextSanitizer.sanitize("HIIT Cardio Kháng L?c"));
        assertEquals("Studio 1 (Tầng 2)", TextSanitizer.sanitize("Studio 1 (T?ng 2)"));
        assertEquals("Gói Trải Nghiệm 1 Tháng", TextSanitizer.sanitize("Gói Tr?i Nghi?m 1 Tháng"));
        assertEquals("Chuyển khoản VietQR", TextSanitizer.sanitize("Chuy?n kho?n (VietQR)"));
        assertEquals("CN03 - Cơ sở 3 (Bình Thạnh)", TextSanitizer.sanitize("Cơ s? 3 - B?nh Th?nh"));

        // Kiểm tra vai trò và nhật ký hệ thống (Audit Log)
        assertEquals("Chủ", TextSanitizer.sanitize("Ch?"));
        assertEquals("Chủ", TextSanitizer.sanitizeRole("Ch?"));
        assertEquals("Lễ tân", TextSanitizer.sanitizeRole("L? tân"));
        assertEquals("Tạo hóa đơn", TextSanitizer.sanitize("T?o hóa đơn"));
        assertEquals("Mã: HD-POS-1792067944669, Số tiền: 15000.0", 
                TextSanitizer.sanitize("M?: HD-POS-1792067944669, S? ti?n: 15000.0"));
    }

    @Test
    @DisplayName("So khớp chi nhánh thông minh - Giải quyết lỗi Check-in luôn bị từ chối")
    void testIsBranchMatchingThongMinh() {
        // 1. Cùng Cơ sở 1: dù viết khác kiểu hoặc dính lỗi font dấu '?' vẫn phải MATCH
        assertTrue(TextSanitizer.isBranchMatching("Cơ s? 1 - G? V?p", "CN01 - Cơ sở 1 (Quận 1)"),
                "Hội viên Cơ sở 1 (kể cả dính font lỗi) phải được check-in tại trạm Cơ sở 1");
        assertTrue(TextSanitizer.isBranchMatching("CN01 - Quận 1", "CN01 - Cơ sở 1 (Quận 1)"),
                "CN01 - Quận 1 phải khớp với trạm CN01 - Cơ sở 1 (Quận 1)");
        assertTrue(TextSanitizer.isBranchMatching("Cơ sở 1", "CN01 - Cơ sở 1 (Quận 1)"));

        // 2. Cùng Cơ sở 2:
        assertTrue(TextSanitizer.isBranchMatching("CN02 - Tân Bình", "CN02 - Cơ sở 2 (Tân Bình)"));
        assertTrue(TextSanitizer.isBranchMatching("Cơ s? 2 - T?n B?nh", "CN02 - Cơ sở 2 (Tân Bình)"));

        // 3. Khách VIP toàn hệ thống -> vào được mọi chi nhánh
        assertTrue(TextSanitizer.isBranchMatching("Toàn hệ thống (VIP)", "CN01 - Cơ sở 1 (Quận 1)"));
        assertTrue(TextSanitizer.isBranchMatching("To?n h? th?ng (VIP Diamond)", "CN02 - Cơ sở 2 (Tân Bình)"));

        // 4. Trạm chọn chế độ Toàn hệ thống -> chấp nhận mọi thẻ
        assertTrue(TextSanitizer.isBranchMatching("CN01 - Quận 1", "Toàn hệ thống (Tất cả trạm)"));

        // 5. Khác chi nhánh thực sự (CN01 quẹt tại trạm CN02) -> phải TỪ CHỐI
        assertFalse(TextSanitizer.isBranchMatching("CN01 - Quận 1", "CN02 - Cơ sở 2 (Tân Bình)"),
                "Thẻ cơ sở đơn lẻ CN01 quẹt tại trạm CN02 phải bị từ chối");
        assertFalse(TextSanitizer.isBranchMatching("CN02 - Tân Bình", "CN01 - Cơ sở 1 (Quận 1)"),
                "Thẻ cơ sở đơn lẻ CN02 quẹt tại trạm CN01 phải bị từ chối");
    }
}
