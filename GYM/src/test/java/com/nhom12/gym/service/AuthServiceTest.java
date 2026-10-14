package com.nhom12.gym.service;

import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.TaiKhoan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {

    @Test
    @DisplayName("Tài khoản bị khóa không thể đăng nhập")
    void taiKhoanBiKhoaKhongDangNhapDuoc() {
        TaiKhoanDAO dao = new TaiKhoanDAO();
        String username = "locked-test-" + System.currentTimeMillis();
        assertTrue(dao.them(new TaiKhoan(username, "TestPassword123", "Lễ tân", "Tài khoản kiểm thử")));

        try {
            assertTrue(dao.doiTrangThai(username, "Tạm khóa"));
            AuthService.LoginResult result = new AuthService().dangNhap(username, "TestPassword123");
            assertFalse(result.success);
        } finally {
            dao.xoa(username);
        }
    }
}
