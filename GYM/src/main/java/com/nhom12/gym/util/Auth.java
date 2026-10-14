package com.nhom12.gym.util;

import com.nhom12.gym.entity.TaiKhoan;

/**
 * Quản lý phiên đăng nhập người dùng (User Session Context)
 * Hỗ trợ Thread-Safe và tương thích ngược với toàn bộ các Panel giao diện
 */
public class Auth {
    public static volatile String tenDangNhap = null;
    public static volatile String soDienThoai = null;
    public static volatile String vaiTro = null;
    public static volatile String hoTen = null;
    public static volatile String chiNhanh = "Toàn hệ thống";
    public static volatile String chiNhanhDangChon = "Toàn hệ thống";
    public static volatile Integer maHoiVien = null;
    public static volatile boolean isOffline = false;
    private static volatile TaiKhoan currentUser = null;

    public static synchronized void setSession(TaiKhoan tk) {
        setSession(tk, false);
    }

    public static synchronized void setSession(TaiKhoan tk, boolean offline) {
        if (tk != null) {
            currentUser = tk;
            tenDangNhap = tk.getTenDangNhap();
            soDienThoai = tk.getTenDangNhap();
            vaiTro = tk.getVaiTro();
            hoTen = tk.getHoTen();
            chiNhanh = tk.getChiNhanh() != null ? tk.getChiNhanh() : "Toàn hệ thống";
            chiNhanhDangChon = chiNhanh;
            maHoiVien = tk.getMaHoiVien();
            isOffline = offline;
        } else {
            clear();
        }
    }

    public static TaiKhoan getCurrentUser() {
        return currentUser;
    }

    public static String getChiNhanhHienTai() {
        if (chiNhanhDangChon != null && !chiNhanhDangChon.trim().isEmpty()) {
            return chiNhanhDangChon;
        }
        return chiNhanh != null ? chiNhanh : "Toàn hệ thống";
    }

    public static boolean isToanHeThong() {
        String curr = getChiNhanhHienTai();
        return curr == null || curr.toLowerCase().contains("toàn hệ thống") || curr.toLowerCase().contains("tất cả");
    }

    public static boolean isLogin() {
        return tenDangNhap != null && !tenDangNhap.trim().isEmpty();
    }

    public static boolean isManager() {
        return isLogin() && "Chủ".equalsIgnoreCase(vaiTro);
    }

    public static boolean isReceptionist() {
        return isLogin() && "Lễ tân".equalsIgnoreCase(vaiTro);
    }

    public static boolean isTrainer() {
        return isLogin() && "HLV".equalsIgnoreCase(vaiTro);
    }

    public static boolean isBranchManager() {
        return isLogin() && "Quản lý".equalsIgnoreCase(vaiTro);
    }

    public static boolean isMember() {
        return isLogin() && "Hội viên".equalsIgnoreCase(vaiTro);
    }

    public static synchronized void clear() {
        currentUser = null;
        tenDangNhap = null;
        soDienThoai = null;
        vaiTro = null;
        hoTen = null;
        chiNhanh = "Toàn hệ thống";
        chiNhanhDangChon = "Toàn hệ thống";
        maHoiVien = null;
        isOffline = false;
    }
}
