package com.nhom12.gym.service;

import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.dao.HoiVienDAO;
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
                LoginResult validation = validateAccount(tk, false);
                if (validation != null) return validation;
                Auth.setSession(tk, false);
                return new LoginResult(true, "Đăng nhập thành công!", false, tk);
            } else {
                return new LoginResult(false, "Tên đăng nhập hoặc mật khẩu không chính xác!", false, null);
            }
        }

        // 3. CHỈ KHI CSDL MẤT KẾT NỐI (OFFLINE DEMO / EMERGENCY BUFFER)
        LOGGER.log(Level.INFO, "Kích hoạt cơ chế xác thực ngoại tuyến do mất kết nối CSDL...");

        // Kiểm tra trong bộ nhớ đệm tài khoản (hỗ trợ cả các tài khoản hội viên mới đăng ký)
        TaiKhoan cachedTK = taiKhoanDAO.timTheoTenDangNhap(user);
        if (cachedTK != null && "Đang hoạt động".equalsIgnoreCase(cachedTK.getTrangThaiTaiKhoan())
                && PasswordUtil.checkPassword(pass, cachedTK.getMatKhau())) {
            LoginResult validation = validateAccount(cachedTK, true);
            if (validation != null) return validation;
            Auth.setSession(cachedTK, true);
            return new LoginResult(true, "Đăng nhập thành công (Bộ nhớ đệm ngoại tuyến)!", true, cachedTK);
        }

        String hashedDefault = PasswordUtil.hashPassword("123456");
        if (PasswordUtil.checkPassword(pass, hashedDefault)) {
            TaiKhoan offlineTK = null;
            if ("admin".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("admin", hashedDefault, "Chủ", "Võ Anh Đức", "Toàn hệ thống");
            } else if ("letan1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("letan1", hashedDefault, "Lễ tân", "Nguyễn Thị Linh", "CN01 - Cơ sở 1 (Quận 1)");
            } else if ("letan2".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("letan2", hashedDefault, "Lễ tân", "Trần Thị Mai", "CN02 - Cơ sở 2 (Tân Bình)");
            } else if ("hlv1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("hlv1", hashedDefault, "HLV", "Trần Tuấn Kiệt", "CN01 - Cơ sở 1 (Quận 1)");
            } else if ("hlv2".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("hlv2", hashedDefault, "HLV", "Phạm Quốc Bảo", "CN02 - Cơ sở 2 (Tân Bình)");
            } else if ("hlv3".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("hlv3", hashedDefault, "HLV", "Nguyễn Thu Thảo", "CN01 - Cơ sở 1 (Quận 1)");
            } else if ("quanly1".equalsIgnoreCase(user)) {
                offlineTK = new TaiKhoan("quanly1", hashedDefault, "Quản lý", "Lê Thanh Sơn", "CN02 - Cơ sở 2 (Tân Bình)");
            }

            if (offlineTK != null) {
                Auth.setSession(offlineTK, true);
                return new LoginResult(true, "Đăng nhập thành công (Chế độ Ngoại Tuyến - Mất kết nối CSDL)!", true, offlineTK);
            }
        }

        return new LoginResult(false, "Không thể kết nối CSDL và tài khoản ngoại tuyến không khớp!", true, null);
    }

    private LoginResult validateAccount(TaiKhoan account, boolean offline) {
        if (!isKnownRole(account.getVaiTro())) {
            return new LoginResult(false, "Tài khoản chưa được gán vai trò hợp lệ. Vui lòng liên hệ quản trị viên.",
                    offline, null);
        }
        if ("Hội viên".equalsIgnoreCase(account.getVaiTro())) {
            if (account.getMaHoiVien() == null || account.getMaHoiVien() <= 0
                    || new HoiVienDAO().timTheoId(account.getMaHoiVien()) == null) {
                return new LoginResult(false,
                        "Tài khoản hội viên chưa được liên kết với hồ sơ hợp lệ. Vui lòng liên hệ lễ tân.",
                        offline, null);
            }
        }
        return null;
    }

    public boolean doiMatKhau(String tenDangNhap, String matKhauHienTai, String matKhauMoi) {
        if (!Auth.isLogin() || tenDangNhap == null || !tenDangNhap.equals(Auth.tenDangNhap)
                || matKhauHienTai == null || matKhauMoi == null || matKhauMoi.length() < 8
                || matKhauMoi.length() > 72 || !matKhauMoi.matches(".*[A-Za-z].*")
                || !matKhauMoi.matches(".*\\d.*")) {
            return false;
        }
        TaiKhoan account = taiKhoanDAO.timTheoTenDangNhap(tenDangNhap);
        if (account == null || !"Đang hoạt động".equalsIgnoreCase(account.getTrangThaiTaiKhoan())
                || !PasswordUtil.checkPassword(matKhauHienTai, account.getMatKhau())) {
            return false;
        }
        return taiKhoanDAO.capNhatMatKhau(tenDangNhap, matKhauMoi);
    }

    public static boolean isKnownRole(String role) {
        return "Chủ".equalsIgnoreCase(role) || "Quản lý".equalsIgnoreCase(role)
                || "Lễ tân".equalsIgnoreCase(role) || "HLV".equalsIgnoreCase(role)
                || "Hội viên".equalsIgnoreCase(role);
    }
}
