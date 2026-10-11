package com.nhom12.gym.controller;

import com.nhom12.gym.service.AuthService;
import com.nhom12.gym.service.AuthService.LoginResult;

/**
 * AuthController - Điều phối luồng Đăng nhập & Xác thực hệ thống
 */
public class AuthController {
    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    public LoginResult xuLyDangNhap(String tenDangNhap, String matKhau) {
        return authService.dangNhap(tenDangNhap, matKhau);
    }
}
