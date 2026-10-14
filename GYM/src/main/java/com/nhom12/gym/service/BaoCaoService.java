package com.nhom12.gym.service;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.HoaDonDAO;
import com.nhom12.gym.dao.LichSuCheckInDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.LichSuCheckIn;

import java.util.*;

public class BaoCaoService {
    private final HoiVienDAO hoiVienDAO = new HoiVienDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final LichSuCheckInDAO checkInDAO = new LichSuCheckInDAO();

    /**
     * Báo cáo 1: Doanh thu theo từng chi nhánh
     */
    public Map<String, Double> getDoanhThuTheoChiNhanh() {
        return hoaDonDAO.layDoanhThuTheoChiNhanh();
    }

    /**
     * Báo cáo 2: Danh sách hội viên sắp hết hạn trong vòng N ngày tới
     */
    public List<HoiVien> getHoiVienSapHetHan(int soNgayToi) {
        return hoiVienDAO.layDanhSachSapHetHan(soNgayToi);
    }

    /**
     * Báo cáo 3: Mức độ sử dụng phòng tập theo giờ (Heatmap 6h - 22h)
     */
    public int[] getPhanBoLuuLuongTheoGio() {
        return checkInDAO.layPhanBoLuuLuongTheoGio();
    }

    /**
     * Thống kê tổng hợp số liệu cho Dashboard
     */
    public Map<String, Object> getThongKeTongQuan() {
        Map<String, Object> stats = new HashMap<>();

        List<HoiVien> members = hoiVienDAO.layTatCa();
        int totalMembers = (members != null) ? members.size() : 0;
        long activeMembers = (members != null) ? members.stream().filter(m -> "Active".equalsIgnoreCase(m.getTrangThai())).count() : 0;

        double totalRevenue = hoaDonDAO.layTongDoanhThu();
        List<LichSuCheckIn> todayLogs = checkInDAO.layLichSuHomNay();
        int todayCheckins = todayLogs.size();
        long validCheckins = todayLogs.stream().filter(l -> "Hợp lệ".equalsIgnoreCase(l.getTrangThai())).count();

        stats.put("totalMembers", totalMembers);
        stats.put("activeMembers", activeMembers);
        stats.put("totalRevenue", totalRevenue);
        stats.put("todayCheckins", todayCheckins);
        stats.put("validCheckins", validCheckins);
        stats.put("deniedCheckins", todayCheckins - validCheckins);

        return stats;
    }
}
