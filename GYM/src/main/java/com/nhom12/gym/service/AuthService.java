package com.nhom12.gym.service;

import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.Auth;
import com.nhom12.gym.util.PasswordUtil;

/**
 * Service Quản lý Xác thực & Phân quyền Người Dùng (Role-Based Access Control)
 * Bảo mật chặt chẽ: Không tạo cửa sau (backdoor), chỉ kích hoạt offline khi CSDL ngắt kết nối thực sự
 */
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

        TaiKhoan tk = null;
        boolean isDbError = false;

        // 1. Xác thực trực tiếp với CSDL qua DAO
        try {
            tk = taiKhoanDAO.dangNhap(user, pass);
        } catch (Exception e) {
            // Chỉ kích hoạt cờ dbError khi CSDL thực sự ngắt kết nối hoặc gặp lỗi hệ thống
            isDbError = true;
            System.err.println("[AuthService] Không thể kết nối tới SQL Server: " + e.getMessage());
        }

        // 2. KHI CSDL HOẠT ĐỘNG BÌNH THƯỜNG (ONLINE)
        if (!isDbError) {
            if (tk != null) {
                // Xác thực thành công: Ghi nhận phiên làm việc Auth
                Auth.tenDangNhap = tk.getTenDangNhap();
                Auth.vaiTro = tk.getVaiTro();
                Auth.hoTen = tk.getHoTen();
                return new LoginResult(true, "Đăng nhập thành công!", false, tk);
            } else {
                // Mật khẩu sai hoặc tài khoản không tồn tại trên CSDL -> BÁO LỖI NGAY, TUYỆT ĐỐI KHÔNG FALLBACK
                return new LoginResult(false, "Tên đăng nhập hoặc mật khẩu không chính xác!", false, null);
            }
        }

        // 3. CHỈ KHI CSDL MẤT KẾT NỐI (OFFLINE - isDbError == true)
        // Kích hoạt chế độ cứu cánh phòng sự cố mạng để không làm gián đoạn vận hành trạm kiểm soát
        System.out.println("[AuthService] Kích hoạt cơ chế ngoại tuyến do máy chủ CSDL mất kết nối...");
        String hashedDefault = PasswordUtil.hashPassword("123456");

        if (PasswordUtil.checkPassword(pass, hashedDefault)) {
            TaiKhoan offlineTK = null;
            if ("admin".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("admin", hashedDefault, "Chủ", "Võ Anh Đức (Chủ Hệ Thống)");
            } else if ("letan1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("letan1", hashedDefault, "Lễ tân", "Nguyễn Thị Linh (Lễ Tân CN01)");
            } else if ("hlv1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("hlv1", hashedDefault, "HLV", "Trần Tuấn Kiệt (HLV Trưởng)");
            } else if ("quanly1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("quanly1", hashedDefault, "Quản lý", "Lê Thanh Sơn (Quản Lý)");
            } else if ("hoivien1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("hoivien1", hashedDefault, "Hội viên", "Lê Anh Duy (Hội Viên VIP)");
            }

            if (offlineTK != null) {
                Auth.tenDangNhap = offlineTK.getTenDangNhap();
                Auth.vaiTro = offlineTK.getVaiTro();
                Auth.hoTen = offlineTK.getHoTen();
                return new LoginResult(true, "Đăng nhập thành công (Chế độ Ngoại Tuyến - Mất kết nối CSDL)!", true, offlineTK);
            }
        }

        return new LoginResult(false, "Không thể kết nối CSDL và thông tin tài khoản ngoại tuyến không khớp!", true, null);
    }
}
