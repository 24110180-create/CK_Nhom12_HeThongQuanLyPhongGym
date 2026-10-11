package com.nhom12.gym.service;

import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.Auth;

public class AuthService {
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public static class LoginResult {
        public boolean success;
        public String message;
        public boolean isOffline;
        public TaiKhoan taiKhoan;

        public LoginResult(boolean success, String message, boolean isOffline, TaiKhoan taiKhoan) {
            this.success = success;
            this.message = message;
            this.isOffline = isOffline;
            this.taiKhoan = taiKhoan;
        }
    }

    public LoginResult dangNhap(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty() || matKhau == null || matKhau.trim().isEmpty()) {
            return new LoginResult(false, "Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!", false, null);
        }

        String user = tenDangNhap.trim();
        String pass = matKhau.trim();

        // 1. Thử kết nối cơ sở dữ liệu
        try {
            TaiKhoan tk = taiKhoanDAO.dangNhap(user, pass);
            if (tk != null) {
                Auth.tenDangNhap = tk.getTenDangNhap();
                Auth.vaiTro = tk.getVaiTro();
                Auth.hoTen = tk.getHoTen();
                return new LoginResult(true, "Đăng nhập thành công!", false, tk);
            }
        } catch (Exception ignored) {
        }

        // 2. Fallback offline cho cả 5 vai trò để kiểm thử (Mật khẩu '123456' băm chuẩn SHA-256)
        if ("123456".equals(pass)) {
            String hashedDefault = com.nhom12.gym.util.PasswordUtil.hashPassword("123456");
            TaiKhoan offlineTK = null;
            if ("admin".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("admin", hashedDefault, "Chủ", "Võ Anh Đức");
            } else if ("letan1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("letan1", hashedDefault, "Lễ tân", "Chị Linh (Lễ Tân)");
            } else if ("hlv1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("hlv1", hashedDefault, "HLV", "John Smith (HLV)");
            } else if ("quanly1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("quanly1", hashedDefault, "Quản lý", "Lê Thanh Sơn (Quản Lý)");
            } else if ("hoivien1".equalsIgnoreCase(user) || "hoivien".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("hoivien1", hashedDefault, "Hội viên", "Nguyễn Văn An (Hội Viên VIP)");
            }

            if (offlineTK != null) {
                Auth.tenDangNhap = offlineTK.getTenDangNhap();
                Auth.vaiTro = offlineTK.getVaiTro();
                Auth.hoTen = offlineTK.getHoTen();
                return new LoginResult(true, "Đăng nhập thành công (Chế độ Offline)!", true, offlineTK);
            }
        }

        return new LoginResult(false, "Sai tài khoản hoặc mật khẩu! Vui lòng thử lại.", false, null);
    }
}
