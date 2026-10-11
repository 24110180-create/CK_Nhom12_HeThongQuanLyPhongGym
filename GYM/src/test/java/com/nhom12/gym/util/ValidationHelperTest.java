package com.nhom12.gym.util;

import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử tự động (Unit Test) cho ValidationHelper
 * Kiểm tra tính đúng đắn của toàn bộ các hàm kiểm tra dữ liệu đầu vào thuần
 */
@DisplayName("Kiểm thử ValidationHelper - Tiện ích kiểm tra dữ liệu")
public class ValidationHelperTest {

    @Test
    @DisplayName("Kiểm tra chuỗi rỗng: null, khoảng trắng, chuỗi có ký tự")
    void testIsRong() {
        assertTrue(ValidationHelper.isRong((String) null), "null phải coi là rỗng");
        assertTrue(ValidationHelper.isRong(""), "Chuỗi trống phải coi là rỗng");
        assertTrue(ValidationHelper.isRong("   "), "Chuỗi chỉ toàn dấu cách phải coi là rỗng");
        assertFalse(ValidationHelper.isRong("FitLife Gym"), "Chuỗi có nội dung không được coi là rỗng");
        assertFalse(ValidationHelper.isRong(" 123 "), "Chuỗi chứa số không được coi là rỗng");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0912345678", "0987654321", "0345678901", "0789123456", "0868123456", "0589123456"})
    @DisplayName("Kiểm tra số điện thoại Việt Nam hợp lệ (10 số, đầu số 03, 05, 07, 08, 09)")
    void testSoDienThoaiHopLe(String phone) {
        assertTrue(ValidationHelper.isSoDienThoaiHopLe(phone), "Số điện thoại " + phone + " phải hợp lệ");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1234567890", "0123456789", "091234567", "09123456789", "0912abc678", "null"})
    @DisplayName("Kiểm tra số điện thoại không hợp lệ (sai độ dài, sai đầu số, chứa chữ)")
    void testSoDienThoaiKhongHopLe(String phone) {
        assertFalse(ValidationHelper.isSoDienThoaiHopLe(phone), "Số điện thoại " + phone + " không được chấp nhận");
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin@fitlife.vn", "letan1@gym.com", "duyle.fitlife@gmail.com", "user.name+tag@sub.domain.co"})
    @DisplayName("Kiểm tra định dạng email hợp lệ chuẩn RFC")
    void testEmailHopLe(String email) {
        assertTrue(ValidationHelper.isEmail(email), "Email " + email + " phải hợp lệ");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "plainaddress", "missingatsign.com", "@missingusername.com", "user@.com", "user@domain.c"})
    @DisplayName("Kiểm tra định dạng email không hợp lệ")
    void testEmailKhongHopLe(String email) {
        assertFalse(ValidationHelper.isEmail(email), "Email " + email + " không được coi là hợp lệ");
    }

    @Test
    @DisplayName("Kiểm tra số nguyên dương và số thực dương")
    void testSoDuong() {
        assertTrue(ValidationHelper.isSoNguyenDuong("1"));
        assertTrue(ValidationHelper.isSoNguyenDuong("500000"));
        assertFalse(ValidationHelper.isSoNguyenDuong("0"));
        assertFalse(ValidationHelper.isSoNguyenDuong("-10"));
        assertFalse(ValidationHelper.isSoNguyenDuong("abc"));

        assertTrue(ValidationHelper.isSoThucDuong("0"));
        assertTrue(ValidationHelper.isSoThucDuong("1500000.50"));
        assertFalse(ValidationHelper.isSoThucDuong("-50.5"));
        assertFalse(ValidationHelper.isSoThucDuong("xyz"));
    }

    @Test
    @DisplayName("Kiểm tra định dạng ngày tháng nghiêm ngặt (Strict Lenient = false)")
    void testIsNgayHopLe() {
        assertTrue(ValidationHelper.isNgayHopLe("15/05/2000", "dd/MM/yyyy"));
        assertTrue(ValidationHelper.isNgayHopLe("29/02/2024", "dd/MM/yyyy"), "2024 là năm nhuận phải có ngày 29/02");
        assertFalse(ValidationHelper.isNgayHopLe("29/02/2023", "dd/MM/yyyy"), "2023 không nhuận, không thể có 29/02");
        assertFalse(ValidationHelper.isNgayHopLe("32/01/2024", "dd/MM/yyyy"), "Ngày 32 không tồn tại");
        assertFalse(ValidationHelper.isNgayHopLe("15/13/2024", "dd/MM/yyyy"), "Tháng 13 không tồn tại");
    }

    @Test
    @DisplayName("Kiểm tra tính tuổi và điều kiện đủ tuổi tham gia phòng gym")
    void testTinhTuoiVaDuTuoi() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -20); // 20 tuổi
        Date birthDate20 = cal.getTime();

        assertEquals(20, ValidationHelper.tinhTuoi(birthDate20));
        assertTrue(ValidationHelper.isDuTuoi(birthDate20, 15), "20 tuổi phải đủ điều kiện tối thiểu 15 tuổi");
        assertTrue(ValidationHelper.isDuTuoi(birthDate20, 18), "20 tuổi phải đủ điều kiện tối thiểu 18 tuổi");
        assertFalse(ValidationHelper.isDuTuoi(birthDate20, 25), "20 tuổi không đủ điều kiện tối thiểu 25 tuổi");

        // Ngày sinh tương lai
        Calendar futureCal = Calendar.getInstance();
        futureCal.add(Calendar.DAY_OF_MONTH, 10);
        assertFalse(ValidationHelper.isDuTuoi(futureCal.getTime(), 15), "Ngày sinh trong tương lai phải bị từ chối");
    }
}
