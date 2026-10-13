package com.nhom12.gym.service;

import com.nhom12.gym.entity.HoiVien;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử tự động (Unit Test) cho HoiVienService
 * Kiểm tra các ràng buộc nghiệp vụ lưu, tìm kiếm, và quản lý hội viên
 */
@DisplayName("Kiểm thử HoiVienService - Quản lý Hồ sơ Hội viên")
public class HoiVienServiceTest {

    private HoiVienService hoiVienService;

    @BeforeEach
    void setUp() {
        hoiVienService = new HoiVienService();
    }

    @Test
    @DisplayName("Kiểm tra lưu hội viên: Đối tượng null phải trả về thông báo lỗi")
    void testLuuHoiVien_NullObject() {
        String res = hoiVienService.luuHoiVien(null, false);
        assertTrue(res.startsWith("LỖI"), "Dữ liệu null phải báo lỗi");
    }

    @Test
    @DisplayName("Kiểm tra lưu hội viên: Họ tên để trống phải bị từ chối")
    void testLuuHoiVien_TrongHoTen() {
        HoiVien hv = new HoiVien();
        hv.setHoTen("   ");
        hv.setMaTheQR("QR_TEST_01");

        String res = hoiVienService.luuHoiVien(hv, false);
        assertTrue(res.contains("Tên không được để trống"), "Thiếu họ tên phải báo lỗi");
    }

    @Test
    @DisplayName("Kiểm tra lưu hội viên: Mã QR để trống phải bị từ chối")
    void testLuuHoiVien_TrongMaQR() {
        HoiVien hv = new HoiVien();
        hv.setHoTen("Nguyễn Văn Test");
        hv.setMaTheQR("");

        String res = hoiVienService.luuHoiVien(hv, false);
        assertTrue(res.contains("Mã QR không được để trống"), "Thiếu mã QR phải báo lỗi");
    }

    @Test
    @DisplayName("Kiểm tra lưu hội viên: Cảnh báo khi ngày hết hạn nằm trong quá khứ")
    void testLuuHoiVien_NgayHetHanQuaKhu() {
        HoiVien hv = new HoiVien();
        hv.setHoTen("Nguyễn Văn Test");
        hv.setMaTheQR("QR_TEST_QUA_KHU");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -10); // Quá khứ 10 ngày
        hv.setNgayHetHan(cal.getTime());

        String res = hoiVienService.luuHoiVien(hv, false);
        assertTrue(res.contains("CẢNH BÁO"), "Ngày hết hạn quá khứ phải trả về cảnh báo");
    }

    @Test
    @DisplayName("Kiểm tra lưu hội viên: Dữ liệu hợp lệ phải lưu thành công")
    void testLuuHoiVien_HopLe() {
        HoiVien hv = new HoiVien();
        hv.setHoTen("Lê Văn Thành");
        hv.setMaTheQR("QR_TEST_VALID_" + System.currentTimeMillis());
        hv.setChiNhanhDangKy("CN01 - Quận 1");
        hv.setTrangThai("Active");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 6); // Còn hạn 6 tháng
        hv.setNgayHetHan(cal.getTime());

        String res = hoiVienService.luuHoiVien(hv, false);
        assertTrue(res.contains("THÀNH CÔNG"), "Hội viên hợp lệ phải lưu thành công");
    }

    @Test
    @DisplayName("Kiểm tra tra cứu danh sách và tìm kiếm hội viên thông minh")
    void testTraCuuVaTimKiem() {
        List<HoiVien> ds = hoiVienService.layDanhSachHoiVien();
        assertNotNull(ds);
        assertFalse(ds.isEmpty(), "Danh sách hội viên không được rỗng");

        // Tìm theo mã QR có sẵn
        HoiVien found = hoiVienService.timTheoMaQR("QR001");
        assertNotNull(found, "Phải tìm thấy hội viên có mã QR001");
        assertTrue(found.getHoTen().contains("Duy") || found.getMaTheQR().equals("QR001"));
    }

    @Test
    @DisplayName("Kiểm tra đăng ký tài khoản hội viên mới và đăng nhập thành công vào App")
    void testDangKyTaiKhoanHoiVienMoiVaDangNhap() {
        String phone = "0989" + (System.currentTimeMillis() % 1000000);
        String pass = "memberPass123";
        String name = "Trần Hội Viên Mới";

        com.nhom12.gym.dao.TaiKhoanDAO tkDAO = new com.nhom12.gym.dao.TaiKhoanDAO();
        boolean saved = tkDAO.them(new com.nhom12.gym.entity.TaiKhoan(phone, pass, "Hội viên", name));
        assertTrue(saved, "Tài khoản hội viên mới phải được lưu thành công");

        AuthService authService = new AuthService();
        AuthService.LoginResult res = authService.dangNhap(phone, pass);
        assertTrue(res.success, "Hội viên mới tạo phải đăng nhập thành công");
        assertEquals("Hội viên", res.taiKhoan.getVaiTro(), "Vai trò phải là Hội viên");

        // Đăng nhập sai mật khẩu phải bị từ chối
        AuthService.LoginResult failRes = authService.dangNhap(phone, "wrongPassword");
        assertFalse(failRes.success, "Sai mật khẩu phải bị từ chối");
    }
}
