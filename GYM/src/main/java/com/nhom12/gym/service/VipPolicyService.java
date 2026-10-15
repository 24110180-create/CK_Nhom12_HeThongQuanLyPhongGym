package com.nhom12.gym.service;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.dao.HoiVienVipDacQuyenDAO;
import com.nhom12.gym.entity.HoiVien;
import com.nhom12.gym.entity.HoiVienVipDacQuyen;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
        // Tự động kiểm tra và đồng bộ các hội viên VIP vào danh sách hội viên chung
        for (HoiVienVipDacQuyen v : danhSachVipDacQuyen) {
            dongBoVaoDanhSachHoiVienChung(v);
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
            // Đồng bộ lập tức vào danh sách hội viên của cả 4 trạm (Chủ, Quản lý, Lễ tân, HLV)
            dongBoVaoDanhSachHoiVienChung(vip);
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
            DataSyncManager.fireDataChanged(DataSyncManager.DataType.HOI_VIEN);
            DataSyncManager.fireDataChanged(DataSyncManager.DataType.ALL);
        }
        return ok;
    }

    /**
     * Đồng bộ hội viên VIP vào danh sách chung HoiVienDAO để cả 4 trạm đều nhìn thấy
     */
    public void dongBoVaoDanhSachHoiVienChung(HoiVienVipDacQuyen vip) {
        if (vip == null) return;
        try {
            HoiVienDAO hvDAO = HoiVienDAO.getInstance();
            String code = vip.getMaThe() != null ? vip.getMaThe().trim() : "";
            String phone = vip.getSoDienThoai() != null ? vip.getSoDienThoai().trim() : "";
            String name = vip.getHoTen() != null ? vip.getHoTen().trim() : "Khách VIP";

            HoiVien existing = null;
            if (!code.isEmpty()) {
                existing = hvDAO.timTheoMaQR(code);
            }
            if (existing == null && !phone.isEmpty()) {
                existing = hvDAO.timKiemThongMinh(phone);
            }

            if (existing == null) {
                HoiVien newHv = new HoiVien();
                newHv.setMaTheQR(code.isEmpty() ? ("VIP" + (System.currentTimeMillis() % 10000)) : code);
                newHv.setHoTen(name);
                newHv.setSoDienThoai(phone);
                newHv.setChiNhanhDangKy("Toàn hệ thống");
                newHv.setGoiTapDangKy("Gói 12 Tháng VIP Diamond");
                newHv.setNgayDangKy(new Date());

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 12);
                newHv.setNgayHetHan(cal.getTime());
                newHv.setSoTienDaThanhToan(3000000.0);
                newHv.setHinhThucThanhToan("Chuyển khoản (VietQR)");
                newHv.setTrangThai("Active");

                hvDAO.them(newHv);
            } else {
                boolean changed = false;
                if (name != null && !name.isEmpty() && !name.equalsIgnoreCase(existing.getHoTen())) {
                    existing.setHoTen(name);
                    changed = true;
                }
                if (code != null && !code.isEmpty() && !code.equalsIgnoreCase(existing.getMaTheQR())) {
                    existing.setMaTheQR(code);
                    changed = true;
                }
                if (existing.getTrangThai() == null || !existing.getTrangThai().equalsIgnoreCase("Active")) {
                    existing.setTrangThai("Active");
                    changed = true;
                }
                if (changed) {
                    hvDAO.capNhat(existing);
                }
            }

            DataSyncManager.fireDataChanged(DataSyncManager.DataType.HOI_VIEN);
            DataSyncManager.fireDataChanged(DataSyncManager.DataType.ALL);
        } catch (Exception ex) {
            System.err.println("[VipPolicyService] Lỗi đồng bộ hội viên VIP vào danh sách chung: " + ex.getMessage());
        }
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
            // Chủ đăng nhập: Xem đầy đủ giá niêm yết gốc (ví dụ: 6M), chiết khấu VIP (-50%) và số tiền thực thu (3M)
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
            // Lễ tân / Quản lý đăng nhập: Hệ thống tự động áp dụng mức giảm 50% ngầm, hiển thị trực tiếp giá thực thu (3M) và thu đúng 3M
            // Hoàn toàn ẩn giá gốc niêm yết 6M, ẩn tỷ lệ -50%, ẩn mọi dấu hiệu chiết khấu để nhân viên chỉ đơn thuần tạo đơn và thu tiền
            return new ThongTinGiaHienThi(
                    true,
                    giaSauGiam50, // Hiển thị giá thực thu (đã giảm 50%) thay cho giá gốc
                    giaSauGiam50, // Thu đúng 3M
                    0,            // Ẩn chi tiết mức giảm
                    0,            // Ẩn tỷ lệ phần trăm giảm
                    "Hội phí tiêu chuẩn gói tập",
                    false         // Giới hạn quyền xem chi tiết giá gốc
            );
        }
    }
}
