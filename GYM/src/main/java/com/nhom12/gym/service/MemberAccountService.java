package com.nhom12.gym.service;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.TaiKhoanDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.TaiKhoan;
import com.nhom12.gym.util.Auth;

public class MemberAccountService {
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();

    public boolean capTaiKhoan(HoiVien member, String matKhauTam) {
        if (!coQuyenQuanLyTaiKhoan() || member == null || member.getMaHoiVien() <= 0
                || matKhauTam == null || matKhauTam.length() < 8 || matKhauTam.length() > 72
                || !matKhauTam.matches(".*[A-Za-z].*") || !matKhauTam.matches(".*\\d.*")
                || !dungChiNhanh(member.getChiNhanhDangKy())) {
            return false;
        }
        HoiVien persistedMember = hoiVienDAO.timTheoId(member.getMaHoiVien());
        if (persistedMember == null) return false;
        String phone = persistedMember.getSoDienThoai();
        if (phone == null || !phone.matches("0\\d{9}")) {
            return false;
        }

        TaiKhoan account = taiKhoanDAO.timTheoTenDangNhap(phone);
        if (account != null) {
            if (!"Hội viên".equalsIgnoreCase(account.getVaiTro())
                    || (account.getMaHoiVien() != null && account.getMaHoiVien() != persistedMember.getMaHoiVien())) {
                return false;
            }
            account.setMaHoiVien(persistedMember.getMaHoiVien());
            account.setHoTen(persistedMember.getHoTen());
            account.setChiNhanh(persistedMember.getChiNhanhDangKy());
            account.setTrangThaiTaiKhoan("Đang hoạt động");
            account.setMatKhau(matKhauTam);
            return taiKhoanDAO.capNhat(account);
        }

        TaiKhoan created = new TaiKhoan(phone, matKhauTam, "Hội viên",
                persistedMember.getHoTen(), persistedMember.getChiNhanhDangKy());
        created.setMaHoiVien(persistedMember.getMaHoiVien());
        created.setTrangThaiTaiKhoan("Đang hoạt động");
        return taiKhoanDAO.them(created);
    }

    public boolean datLaiMatKhau(String username, String matKhauTam) {
        TaiKhoan account = timTaiKhoanTrongPhamVi(username);
        return account != null && taiKhoanDAO.capNhatMatKhau(account.getTenDangNhap(), matKhauTam);
    }

    public boolean doiTrangThai(String username, String trangThai) {
        TaiKhoan account = timTaiKhoanTrongPhamVi(username);
        return account != null && taiKhoanDAO.doiTrangThai(account.getTenDangNhap(), trangThai);
    }

    private TaiKhoan timTaiKhoanTrongPhamVi(String username) {
        if (!coQuyenQuanLyTaiKhoan() || username == null) return null;
        TaiKhoan account = taiKhoanDAO.timTheoTenDangNhap(username);
        if (account == null || !"Hội viên".equalsIgnoreCase(account.getVaiTro())
                || account.getMaHoiVien() == null) {
            return null;
        }
        HoiVien member = hoiVienDAO.timTheoId(account.getMaHoiVien());
        if (member == null || !dungChiNhanh(member.getChiNhanhDangKy())) {
            return null;
        }
        return account;
    }

    private boolean coQuyenQuanLyTaiKhoan() {
        return PermissionService.getInstance().hasPermission(Auth.vaiTro, PermissionService.PERM_MEMBER_ACCOUNTS)
                && (Auth.isManager() || Auth.isBranchManager() || Auth.isReceptionist());
    }

    private boolean dungChiNhanh(String branch) {
        if (Auth.isManager()) return true;
        return branch != null && branch.equalsIgnoreCase(Auth.getChiNhanhHienTai());
    }
}
