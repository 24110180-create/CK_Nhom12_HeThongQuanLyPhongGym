package com.nhom12.gym.service;

import com.nhom12.gym.dao.HoiVienVipDacQuyenDAO;
import com.nhom12.gym.entity.HoiVienVipDacQuyen;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service quản lý chính sách giá ưu đãi nội bộ, giới hạn hiển thị chi tiết giá theo vai trò đăng nhập
 * (Chủ xem đầy đủ, nhân viên chỉ xem giá cuối).
 */
public class VipPolicyService {

    // Lớp kế thừa nhằm duy trì tương thích ngược cho các màn hình giao diện
    public static class HoiVienVipDacQuyenLegacy extends HoiVienVipDacQuyen {
        public HoiVienVipDacQuyenLegacy() {
            super();
        }
        public HoiVienVipDacQuyenLegacy(String maTheQR, String hoTen, String soDienThoai, String moiQuanHe, double tiLeGiam, String ghiChuBaoMat) {
            super(maTheQR, hoTen, soDienThoai, moiQuanHe, tiLeGiam, ghiChuBaoMat);
        }
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
    private final HoiVienVipDacQuyenDAO vipDAO = new HoiVienVipDacQuyenDAO();
    private final List<HoiVienVipDacQuyen> danhSachVipDacQuyen = new CopyOnWriteArrayList<>();

    public static synchronized VipPolicyService getInstance() {
        if (instance == null) {
            instance = new VipPolicyService();
        }
        return instance;
    }

    private VipPolicyService() {
        napDanhSachVipTuCSDL();
    }

    private void napDanhSachVipTuCSDL() {
        danhSachVipDacQuyen.clear();
        List<HoiVienVipDacQuyen> listFromDb = vipDAO.layTatCa();
        if (listFromDb != null && !listFromDb.isEmpty()) {
            danhSachVipDacQuyen.addAll(listFromDb);
        } else {
            // Dữ liệu mẫu giả lập kỹ thuật dự phòng khi CSDL chưa có dữ liệu
            HoiVienVipDacQuyen v1 = new HoiVienVipDacQuyen("VIP007", "Nguyễn Văn A", "0901000001", "Khách VIP lâu năm", 50.0, "Chính sách ưu đãi nội bộ");
            HoiVienVipDacQuyen v2 = new HoiVienVipDacQuyen("VIP008", "Trần Thị B", "0902000002", "Đối tác chiến lược", 50.0, "Chính sách ưu đãi nội bộ");
            HoiVienVipDacQuyen v3 = new HoiVienVipDacQuyen("VIP009", "Lê Văn C", "0903000003", "Hội viên danh dự", 50.0, "Chính sách ưu đãi nội bộ");
            danhSachVipDacQuyen.add(v1);
            danhSachVipDacQuyen.add(v2);
            danhSachVipDacQuyen.add(v3);
        }
    }

    public List<HoiVienVipDacQuyen> layDanhSachVip() {
        return Collections.unmodifiableList(danhSachVipDacQuyen);
    }

    public boolean themVipDacQuyen(HoiVienVipDacQuyen vip) {
        if (vip == null || vip.getSoDienThoai() == null) return false;
        boolean ok = vipDAO.them(vip);
        if (ok) {
            danhSachVipDacQuyen.removeIf(v ->
                    (vip.getMaThe() != null && vip.getMaThe().equalsIgnoreCase(v.getMaThe())) ||
                    (vip.getSoDienThoai() != null && vip.getSoDienThoai().equalsIgnoreCase(v.getSoDienThoai()))
            );
            danhSachVipDacQuyen.add(vip);
        }
        return ok;
    }

    public boolean xoaVipDacQuyen(String sdtHoacMa) {
        if (sdtHoacMa == null || sdtHoacMa.trim().isEmpty()) return false;
        String key = sdtHoacMa.trim();
        boolean ok = vipDAO.xoa(key);
        if (ok) {
            danhSachVipDacQuyen.removeIf(v ->
                    v.getSoDienThoai().equalsIgnoreCase(key) ||
                    v.getMaThe().equalsIgnoreCase(key)
            );
        }
        return ok;
    }

    /**
     * Kiểm tra xem hội viên có thuộc danh sách áp dụng ưu đãi nội bộ hay không
     */
    public boolean laHoiVienVipDacQuyen(String sdt, String hoTen, String maQR) {
        for (HoiVienVipDacQuyen vip : danhSachVipDacQuyen) {
            if (sdt != null && !sdt.trim().isEmpty() && vip.getSoDienThoai() != null && vip.getSoDienThoai().equalsIgnoreCase(sdt.trim())) {
                return true;
            }
            if (maQR != null && !maQR.trim().isEmpty() && vip.getMaThe() != null && vip.getMaThe().equalsIgnoreCase(maQR.trim())) {
                return true;
            }
            if (hoTen != null && !hoTen.trim().isEmpty() && vip.getHoTen() != null && vip.getHoTen().equalsIgnoreCase(hoTen.trim())) {
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

        // Hội viên áp dụng ưu đãi nội bộ: Giảm 50%
        double giaSauGiam50 = giaGoc * 0.5;
        double soTienGiam = giaGoc * 0.5;

        boolean isChu = "Chủ".equalsIgnoreCase(currentRole) || "Quản trị viên (Chủ phòng tập)".equalsIgnoreCase(currentRole);

        if (isChu) {
            // Chủ đăng nhập: Xem đầy đủ giá niêm yết gốc, chiết khấu và số tiền thực thu
            return new ThongTinGiaHienThi(
                    true,
                    giaGoc,
                    giaSauGiam50,
                    soTienGiam,
                    50.0,
                    "Chính sách ưu đãi nội bộ (Giảm 50%)",
                    true
            );
        } else {
            // Lễ tân / nhân viên đăng nhập: Giới hạn hiển thị chi tiết, chỉ thấy số tiền thực thu
            return new ThongTinGiaHienThi(
                    true,
                    giaSauGiam50, // Hiển thị giá thực thu thay cho giá gốc
                    giaSauGiam50,
                    0,            // Ẩn chi tiết mức giảm
                    0,            // Ẩn tỷ lệ phần trăm giảm
                    "Hợp đồng ưu đãi nội bộ - Đã được phê duyệt",
                    false         // Giới hạn quyền xem chi tiết giá gốc
            );
        }
    }
}
