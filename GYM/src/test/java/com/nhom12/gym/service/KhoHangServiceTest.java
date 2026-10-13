package com.nhom12.gym.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Nghiệp vụ Đồng bộ Kho Hàng & Quầy POS (KhoHangService)")
public class KhoHangServiceTest {

    private KhoHangService service;

    @BeforeEach
    void setUp() {
        service = KhoHangService.getInstance();
    }

    @Test
    @DisplayName("Kiểm tra danh mục kho khởi tạo có dữ liệu đầy đủ")
    void testKhoiTaoDuLieuKho() {
        List<KhoHangService.SanPhamKho> listKho = service.layTatCaSanPhamKho();
        assertNotNull(listKho);
        assertFalse(listKho.isEmpty());

        List<KhoHangService.SanPhamDaBan> listDaBan = service.layTatCaSanPhamDaBan();
        assertNotNull(listDaBan);
        assertFalse(listDaBan.isEmpty());
    }

    @Test
    @DisplayName("Logic GIẢM KHO khi bán hàng POS thành công")
    void testXuatKhoKhiBanHangGiamTonKho() {
        KhoHangService.SanPhamKho sp = service.timSanPhamTheoMa("SP001");
        assertNotNull(sp);
        int tonTruocKhiBan = sp.getSoLuongTon();
        assertTrue(tonTruocKhiBan >= 2);

        List<KhoHangService.CartItem> items = new ArrayList<>();
        items.add(new KhoHangService.CartItem(sp.getMaSP(), sp.getTenSP(), 2, sp.getGiaBan()));

        String error = service.xuatKhoBanHang(items, "Chuyển khoản VietQR", "Lê Anh Duy (Tester)");
        assertNull(error, "Bán hàng thành công không được có lỗi");

        int tonSauKhiBan = sp.getSoLuongTon();
        assertEquals(tonTruocKhiBan - 2, tonSauKhiBan, "Tồn kho phải giảm chính xác bằng số lượng đã bán");

        // Kiểm tra danh sách sản phẩm đã bán có ghi nhận bản ghi mới
        List<KhoHangService.SanPhamDaBan> listDaBan = service.layTatCaSanPhamDaBan();
        assertFalse(listDaBan.isEmpty());
        KhoHangService.SanPhamDaBan donMoi = listDaBan.get(0);
        assertEquals(sp.getMaSP(), donMoi.getMaSP());
        assertEquals(2, donMoi.getSoLuong());
        assertNotNull(donMoi.getThoiGian(), "Thời gian bán phải có ngày giờ");
    }

    @Test
    @DisplayName("Logic CHẶN XUẤT KHO khi vượt quá số lượng tồn kho")
    void testChanXuatKhoKhiVuotTonKho() {
        KhoHangService.SanPhamKho sp = service.timSanPhamTheoMa("SP011");
        assertNotNull(sp);
        int tonKho = sp.getSoLuongTon();

        List<KhoHangService.CartItem> items = new ArrayList<>();
        items.add(new KhoHangService.CartItem(sp.getMaSP(), sp.getTenSP(), tonKho + 99, sp.getGiaBan()));

        String error = service.xuatKhoBanHang(items, "Tiền mặt", "Thu ngân");
        assertNotNull(error, "Phải báo lỗi khi xuất vượt quá tồn kho");
        assertTrue(error.contains("không đủ xuất"), "Nội dung lỗi phải cảnh báo tồn kho");
    }

    @Test
    @DisplayName("Logic TĂNG KHO khi nhập thêm hàng vào kho chứa")
    void testNhapHangVaoKhoTangTonKho() {
        KhoHangService.SanPhamKho sp = service.timSanPhamTheoMa("SP002");
        assertNotNull(sp);
        int tonTruoc = sp.getSoLuongTon();

        boolean ok = service.nhapHangVaoKho(sp.getMaSP(), 20, sp.getGiaBan());
        assertTrue(ok);

        int tonSau = sp.getSoLuongTon();
        assertEquals(tonTruoc + 20, tonSau, "Tồn kho sau khi nhập phải tăng chính xác");
    }

    @Test
    @DisplayName("Logic TĂNG LẠI KHO khi hoàn trả hàng đã bán về kho")
    void testHoanTraHangVeKhoTangLaiTonKho() {
        // Bán 1 món trước
        KhoHangService.SanPhamKho sp = service.timSanPhamTheoMa("SP012");
        assertNotNull(sp);
        int tonBanDau = sp.getSoLuongTon();

        List<KhoHangService.CartItem> items = new ArrayList<>();
        items.add(new KhoHangService.CartItem(sp.getMaSP(), sp.getTenSP(), 3, sp.getGiaBan()));
        service.xuatKhoBanHang(items, "Chuyển khoản VietQR", "Thu ngân");

        assertEquals(tonBanDau - 3, sp.getSoLuongTon());

        // Lấy đơn vừa bán và hoàn trả 2 món
        KhoHangService.SanPhamDaBan donVuaBan = service.layTatCaSanPhamDaBan().get(0);
        boolean hoanTraOk = service.hoanTraHangVeKho(donVuaBan.getMaGiaoDich(), sp.getMaSP(), 2, "Khách trả 2 quả chuối");
        assertTrue(hoanTraOk);

        // Tồn kho phải tăng lại đúng 2 món
        assertEquals(tonBanDau - 3 + 2, sp.getSoLuongTon(), "Tồn kho phải tăng lại đúng số lượng hoàn trả");
        assertTrue(donVuaBan.getTrangThai().contains("Đã hoàn trả"));
    }
}
