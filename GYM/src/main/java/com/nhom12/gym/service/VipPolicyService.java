package com.nhom12.gym.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service Quản Lý Chính Sách VIP Đặc Quyền Của Chủ Phòng Tập (Anh Đức)
 * Giải quyết YÊU CẦU ẨN 1:
 * "Anh Đức có vài hội viên VIP được giảm 50%, không muốn nhân viên thấy giá thực,
 * yêu cầu này chỉ tiết lộ khi hỏi 'Có hội viên đặc biệt nào không?'"
 *
 * Cơ chế bảo mật giá thực:
 * - Đối với LỄ TÂN / NHÂN VIÊN: Hệ thống chỉ hiển thị số tiền thực thu (đã giảm 50%),
 *   hoàn toàn ẨN giá gốc và ẨN tỷ lệ giảm 50% (hiển thị như gói giá đặc biệt duyệt bởi Chủ).
 * - Đối với CHỦ PHÒNG TẬP: Hiển thị đầy đủ Giá niêm yết, Chiết khấu VIP nội bộ 50%, và Doanh thu thực tế.
 */
public class VipPolicyService {

    public static class HoiVienVipDacQuyen implements Serializable {
        private static final long serialVersionUID = 1L;
        private String maTheQR;
        private String hoTen;
        private String soDienThoai;
        private String moiQuanHe; // Bạn thân anh Đức, Cổ đông, Đối tác chiến lược
        private double tiLeGiam;  // Mặc định 50%
        private String ghiChuBaoMat;

        public HoiVienVipDacQuyen(String maTheQR, String hoTen, String soDienThoai, String moiQuanHe, double tiLeGiam, String ghiChuBaoMat) {
            this.maTheQR = maTheQR;
            this.hoTen = hoTen;
            this.soDienThoai = soDienThoai;
            this.moiQuanHe = moiQuanHe;
            this.tiLeGiam = tiLeGiam;
            this.ghiChuBaoMat = ghiChuBaoMat;
        }

        public String getMaTheQR() { return maTheQR; }
        public String getHoTen() { return hoTen; }
        public String getSoDienThoai() { return soDienThoai; }
        public String getMoiQuanHe() { return moiQuanHe; }
        public double getTiLeGiam() { return tiLeGiam; }
        public String getGhiChuBaoMat() { return ghiChuBaoMat; }
    }

    public static class ThongTinGiaHienThi {
        public boolean isVipDacQuyen;
        public double giaGoc;
        public double soTienThucThu;
        public double soTienGiam;
        public double phanTramGiam;
        public String moTaGiaHienThi;
        public boolean choPhepNhanVienThayGiaGoc;

        public ThongTinGiaHienThi(boolean isVipDacQuyen, double giaGoc, double soTienThucThu,
                                  double soTienGiam, double phanTramGiam, String moTaGiaHienThi,
                                  boolean choPhepNhanVienThayGiaGoc) {
            this.isVipDacQuyen = isVipDacQuyen;
            this.giaGoc = giaGoc;
            this.soTienThucThu = soTienThucThu;
            this.soTienGiam = soTienGiam;
            this.phanTramGiam = phanTramGiam;
            this.moTaGiaHienThi = moTaGiaHienThi;
            this.choPhepNhanVienThayGiaGoc = choPhepNhanVienThayGiaGoc;
        }
    }

    private static VipPolicyService instance;
    private final List<HoiVienVipDacQuyen> danhSachVipDacQuyen = new CopyOnWriteArrayList<>();

    public static synchronized VipPolicyService getInstance() {
        if (instance == null) {
            instance = new VipPolicyService();
        }
        return instance;
    }

    private VipPolicyService() {
        // Khởi tạo danh sách khách VIP đặc biệt của riêng anh Đức
        danhSachVipDacQuyen.add(new HoiVienVipDacQuyen("VIP007", "Đỗ Minh Trí", "0909998877", "Bạn thân từ thời đại học của Anh Đức", 50.0, "Giảm 50% trọn đời - Bí mật nội bộ Chủ"));
        danhSachVipDacQuyen.add(new HoiVienVipDacQuyen("QR001", "Lê Anh Duy", "0974482749", "Khách VIP danh dự của Chủ phòng tập", 50.0, "Áp dụng giá nội bộ 50% - Lễ tân không biết giá thực"));
        danhSachVipDacQuyen.add(new HoiVienVipDacQuyen("VIP008", "Nguyễn Tuấn Kiệt", "0912888999", "Đối tác nhượng quyền thiết bị gym", 50.0, "Chiết khấu bí mật 50% do Chủ duyệt"));
    }

    public List<HoiVienVipDacQuyen> layDanhSachVip() {
        return Collections.unmodifiableList(danhSachVipDacQuyen);
    }

    public boolean themVipDacQuyen(HoiVienVipDacQuyen vip) {
        if (vip == null || vip.getSoDienThoai() == null) return false;
        danhSachVipDacQuyen.add(vip);
        return true;
    }

    public boolean xoaVipDacQuyen(String sdtHoacMa) {
        return danhSachVipDacQuyen.removeIf(v ->
                v.getSoDienThoai().equalsIgnoreCase(sdtHoacMa) ||
                v.getMaTheQR().equalsIgnoreCase(sdtHoacMa));
    }

    /**
     * Kiểm tra xem hội viên có thuộc danh sách VIP đặc quyền bí mật của Anh Đức không
     */
    public boolean laHoiVienVipDacQuyen(String sdt, String hoTen, String maQR) {
        for (HoiVienVipDacQuyen vip : danhSachVipDacQuyen) {
            if (sdt != null && !sdt.trim().isEmpty() && vip.getSoDienThoai().equalsIgnoreCase(sdt.trim())) {
                return true;
            }
            if (maQR != null && !maQR.trim().isEmpty() && vip.getMaTheQR().equalsIgnoreCase(maQR.trim())) {
                return true;
            }
            if (hoTen != null && !hoTen.trim().isEmpty() && vip.getHoTen().equalsIgnoreCase(hoTen.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * TÍNH TOÁN GIÁ VÀ ÁP DỤNG QUY TẮC BẢO MẬT GIÁ THỰC THEO ROLE
     * @param currentRole Vai trò người dùng đang thao tác ("Chủ", "Lễ tân", "Quản lý")
     * @param sdt Số điện thoại
     * @param hoTen Tên khách
     * @param maQR Mã thẻ
     * @param giaGoc Giá niêm yết chuẩn của gói tập
     */
    public ThongTinGiaHienThi tinhGiaTheoPhanQuyen(String currentRole, String sdt, String hoTen, String maQR, double giaGoc) {
        boolean isVip = laHoiVienVipDacQuyen(sdt, hoTen, maQR);

        if (!isVip) {
            // Hội viên thông thường: Không có giảm giá
            return new ThongTinGiaHienThi(false, giaGoc, giaGoc, 0, 0, "Giá niêm yết tiêu chuẩn", true);
        }

        // HỘI VIÊN VIP ĐẶC QUYỀN CỦA ANH ĐỨC: GIẢM 50%
        double giaSauGiam50 = giaGoc * 0.5;
        double soTienGiam = giaGoc * 0.5;

        boolean isChu = "Chủ".equalsIgnoreCase(currentRole) || "Quản trị viên (Chủ phòng tập)".equalsIgnoreCase(currentRole);

        if (isChu) {
            // CHỦ ĐĂNG NHẬP: Thấy đầy đủ giá gốc, chiết khấu 50% và thực thu
            return new ThongTinGiaHienThi(
                    true,
                    giaGoc,
                    giaSauGiam50,
                    soTienGiam,
                    50.0,
                    "Chính sách VIP của Anh Đức (Giảm 50% nội bộ)",
                    true
            );
        } else {
            // LỄ TÂN / NHÂN VIÊN ĐĂNG NHẬP: HOÀN TOÀN ẨN GIÁ GỐC VÀ ẨN TỶ LỆ 50%!
            // Chỉ thấy số tiền thu thực tế như là một hợp đồng đặc biệt duyệt bởi Chủ
            return new ThongTinGiaHienThi(
                    true,
                    giaSauGiam50, // Tráo giá gốc thành giá thực thu để nhân viên không biết giá thực
                    giaSauGiam50,
                    0,            // Ẩn số tiền giảm (hiển thị 0đ chiết khấu)
                    0,            // Ẩn phần trăm giảm
                    "Hợp đồng VIP đặc quyền - Đã được Chủ phòng tập phê duyệt",
                    false         // Chặn không cho nhân viên thấy giá thực
            );
        }
    }
}
