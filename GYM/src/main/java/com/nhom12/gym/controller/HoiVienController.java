package com.nhom12.gym.controller;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.service.HoiVienService;
import java.util.List;

/**
 * HoiVienController - Điều phối luồng nghiệp vụ Hội viên
 */
public class HoiVienController {
    private final HoiVienDAO hoiVienDAO;
    private final HoiVienService hoiVienService;

    public HoiVienController() {
        this.hoiVienDAO = new HoiVienDAO();
        this.hoiVienService = new HoiVienService();
    }

    public List<HoiVien> layDanhSachHoiVien() {
        return hoiVienService.layDanhSachHoiVien();
    }

    public String dangKyHoiVienMoi(HoiVien hv) {
        return hoiVienService.luuHoiVien(hv, false);
    }

    public HoiVien timKiemTheoMa(int id) {
        return hoiVienService.timTheoId(id);
    }

    public HoiVien timKiemTheoMaQR(String maQR) {
        return hoiVienService.timTheoMaQR(maQR);
    }
}
