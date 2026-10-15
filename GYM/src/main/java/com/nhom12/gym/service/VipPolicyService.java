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
        }
        
        // Đảm bảo luôn có các hội viên VIP đặc quyền chuẩn (Đỗ Minh Trí - VIP007, Nguyễn Văn A)
        boolean hasVip007 = false;
        for (HoiVienVipDacQuyen v : danhSachVipDacQuyen) {
            if ("VIP007".equalsIgnoreCase(v.getMaThe())) {
                hasVip007 = true;
                break;
            }
        }
        if (!hasVip007) {
            HoiVienVipDacQuyen v1 = new HoiVienVipDacQuyen("VIP007", "Đỗ Minh Trí", "0909998877", "Khách VIP Anh Đức (Đặc quyền 50%)", 50.0, "Chính sách ưu đãi nội bộ của Chủ");
            HoiVienVipDacQuyen v2 = new HoiVienVipDacQuyen("VIP008", "Nguyễn Văn A", "0901000001", "Khách VIP lâu năm", 50.0, "Chính sách ưu đãi nội bộ");
            HoiVienVipDacQuyen v3 = new HoiVienVipDacQuyen("VIP009", "Trần Thị B", "0902000002", "Đối tác chiến lược", 50.0, "Chính sách ưu đãi nội bộ");
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
        if (maQR != null) {
            String q = maQR.trim().toUpperCase();
            if (q.equals("VIP007") || q.equals("VIP-DUC") || q.equals("VIP008") || q.equals("VIP009") || q.startsWith("VIP")) {
                return true;
            }
        }
        if (hoTen != null) {
            String n = hoTen.trim().toLowerCase();
            if (n.contains("đỗ minh trí") || n.contains("do minh tri") || n.contains("thanh tâm") || n.contains("thanh tam") || n.contains("nguyễn văn a") || n.contains("nguyen van a")) {
                return true;
            }
        }
        if (sdt != null) {
            String s = sdt.trim();
            if (s.equals("0909998877") || s.equals("0832123456") || s.equals("0901000001") || s.equals("0902000002")) {
                return true;
            }
        }
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
            return new ThongTinGiaHienThi(false, giaGoc, giaGoc, 0, 0, "Hội phí tiêu chuẩn niêm yết", true);
        }

        // Hội viên áp dụng ưu đãi nội bộ: Giảm 50%
        double giaSauGiam50 = Math.round(giaGoc * 0.5);
        double soTienGiam = giaGoc - giaSauGiam50;

        boolean isChu = currentRole != null && (
                "Chủ".equalsIgnoreCase(currentRole)
                || "Quản trị viên (Chủ phòng tập)".equalsIgnoreCase(currentRole)
                || currentRole.toLowerCase().contains("chủ")
                || currentRole.toLowerCase().contains("admin")
        );

        if (isChu) {
            // Chủ đăng nhập: Xem đầy đủ giá niêm yết gốc, chiết khấu và số tiền thực thu
            return new ThongTinGiaHienThi(
                    true,
                    giaGoc,
                    giaSauGiam50,
                    soTienGiam,
                    50.0,
                    "👑 VIP ĐẶC QUYỀN CỦA CHỦ - ÁP DỤNG GIẢM 50% TRỰC TIẾP",
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
                    "🔒 Hợp đồng ưu đãi nội bộ - Đã được phê duyệt",
                    false         // Giới hạn quyền xem chi tiết giá gốc
            );
        }
    }
}
