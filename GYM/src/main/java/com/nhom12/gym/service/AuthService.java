package com.nhom12.gym.service;

import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.Auth;
import com.nhom12.gym.util.HibernateUtil;
import com.nhom12.gym.util.PasswordUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service Quản lý Xác thực & Phân quyền Người Dùng (Role-Based Access Control)
 * Bảo mật chặt chẽ: Tuyệt đối không tạo backdoor khi CSDL online.
 * Chỉ kích hoạt chế độ đệm ngoại tuyến (Offline Buffer) khi máy chủ CSDL mất kết nối thực sự.
 */
public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
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

        // 1. Xác thực trực tiếp với CSDL qua DAO nếu CSDL đang online
        if (HibernateUtil.isConnected()) {
            try {
                tk = taiKhoanDAO.dangNhap(user, pass);
            } catch (Exception e) {
                isDbError = true;
                LOGGER.log(Level.WARNING, "Lỗi kết nối CSDL khi xác thực người dùng: {0}", e.getMessage());
            }
        } else {
            isDbError = true;
            LOGGER.log(Level.WARNING, "Máy chủ CSDL hiện không hoạt động (Offline Mode).");
        }

        // 2. KHI CSDL HOẠT ĐỘNG BÌNH THƯỜNG (ONLINE)
        if (!isDbError) {
            if (tk != null) {
                // Xác thực thành công: Ghi nhận phiên làm việc Auth
                Auth.setSession(tk, false);
                return new LoginResult(true, "Đăng nhập thành công!", false, tk);
            } else {
                // Mật khẩu sai hoặc tài khoản không tồn tại trên CSDL -> BÁO LỖI NGAY, TUYỆT ĐỐI KHÔNG FALLBACK
                return new LoginResult(false, "Tên đăng nhập hoặc mật khẩu không chính xác!", false, null);
            }
        }

        // 3. CHỈ KHI CSDL MẤT KẾT NỐI (OFFLINE DEMO / EMERGENCY BUFFER)
        LOGGER.log(Level.INFO, "Kích hoạt cơ chế xác thực ngoại tuyến do mất kết nối CSDL...");
        String hashedDefault = PasswordUtil.hashPassword("123456");

        if (PasswordUtil.checkPassword(pass, hashedDefault)) {
            TaiKhoan offlineTK = null;
            if ("admin".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("admin", hashedDefault, "Chủ", "Võ Anh Đức (Chủ Hệ Thống)", "Toàn hệ thống");
            } else if ("letan1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("letan1", hashedDefault, "Lễ tân", "Nguyễn Thị Linh (Lễ Tân CN01)", "CN01 - Quận 1");
            } else if ("hlv1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("hlv1", hashedDefault, "HLV", "Trần Tuấn Kiệt (HLV Trưởng)", "CN01 - Quận 1");
            } else if ("quanly1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("quanly1", hashedDefault, "Quản lý", "Lê Thanh Sơn (Quản Lý Chi Nhánh)", "CN02 - Tân Bình");
            } else if ("hoivien1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("hoivien1", hashedDefault, "Hội viên", "Lê Anh Duy (Hội Viên VIP)", "CN01 - Quận 1");
            }

            if (offlineTK != null) {
                Auth.setSession(offlineTK, true);
                return new LoginResult(true, "Đăng nhập thành công (Chế độ Ngoại Tuyến - Mất kết nối CSDL)!", true, offlineTK);
            }
        }

        return new LoginResult(false, "Không thể kết nối CSDL và tài khoản ngoại tuyến không khớp!", true, null);
    }
}
