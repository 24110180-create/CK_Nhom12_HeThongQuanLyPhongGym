package com.nhom12.gym.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bộ kiểm thử tự động (Unit Test) cho CheckInService
 * Kiểm thử toàn diện 5 Quy Tắc Nghiệp Vụ Kinh Doanh (5 Core Business Rules)
 * của Use Case Lõi "Quét Thẻ & Check-in Hội Viên"
 */
@DisplayName("Kiểm thử CheckInService - 5 Quy Tắc Nghiệp Vụ Cốt Lõi")
public class CheckInServiceTest {

    private CheckInService checkInService;

    @BeforeEach
    void setUp() {
        checkInService = new CheckInService();
    }

    @Test
    @DisplayName("Quy tắc 0: Mã thẻ rỗng hoặc null -> Bị từ chối ngay lập tức")
    void testMaTheRong() {
        CheckInService.CheckInResult resNull = checkInService.thucHienCheckIn(null, "CN01 - Quận 1");
        assertFalse(resNull.success);
        assertEquals("LOCKED", resNull.gateStatus);

        CheckInService.CheckInResult resEmpty = checkInService.thucHienCheckIn("   ", "CN01 - Quận 1");
        assertFalse(resEmpty.success);
        assertEquals("LOCKED", resEmpty.gateStatus);
    }

    @Test
    @DisplayName("Quy tắc 1: Mã thẻ không tồn tại trên hệ thống -> Từ chối mở cửa")
    void testRule1_TheKhongTonTai() {
        CheckInService.CheckInResult res = checkInService.thucHienCheckIn("UNKNOWN_CARD_99999", "CN01 - Quận 1");
        assertFalse(res.success, "Thẻ không có trong hồ sơ phải bị từ chối");
        assertEquals("LOCKED", res.gateStatus);
        assertTrue(res.message.contains("TỪ CHỐI") || res.message.contains("không tồn tại"));
    }

    @Test
    @DisplayName("Quy tắc 2: Thẻ đang bị TẠM KHÓA -> Từ chối mở cửa")
    void testRule2_TheBiKhoa() {
        // 'KHOA' là mã thẻ kiểm thử chuyên biệt cho trạng thái Tạm khóa
        CheckInService.CheckInResult res = checkInService.thucHienCheckIn("KHOA", "CN01 - Quận 1");
        assertFalse(res.success, "Thẻ tạm khóa không được phép vào phòng gym");
        assertEquals("LOCKED", res.gateStatus);
        assertTrue(res.lyDoTuChoi.toLowerCase().contains("khóa") || res.message.toLowerCase().contains("khóa"));
    }

    @Test
    @DisplayName("Quy tắc 3: Gói tập đã HẾT HẠN -> Từ chối mở cửa")
    void testRule3_GoiTapHetHan() {
        // 'LAN' hoặc 'QR003' là thẻ đã hết hạn trong hệ thống
        CheckInService.CheckInResult res = checkInService.thucHienCheckIn("LAN", "CN01 - Quận 1");
        assertFalse(res.success, "Thẻ hết hạn phải bị từ chối");
        assertEquals("LOCKED", res.gateStatus);
        assertTrue(res.lyDoTuChoi.toLowerCase().contains("hết hạn") || res.message.toLowerCase().contains("hết hạn"));
    }

    @Test
    @DisplayName("Quy tắc 4: Sai chi nhánh đăng ký (Thẻ cơ sở đơn lẻ) -> Từ chối mở cửa")
    void testRule4_SaiChiNhanh() {
        // QR002 đăng ký tại 'CN02 - Tân Bình', khi quẹt tại 'CN01 - Quận 1' phải bị từ chối
        CheckInService.CheckInResult res = checkInService.thucHienCheckIn("QR002", "CN01 - Quận 1");
        assertFalse(res.success, "Thẻ cơ sở đơn lẻ quẹt sai chi nhánh phải bị từ chối");
        assertEquals("LOCKED", res.gateStatus);
        assertTrue(res.lyDoTuChoi.toLowerCase().contains("chi nhánh") || res.message.toLowerCase().contains("chi nhánh"));
    }

    @Test
    @DisplayName("Quy tắc 4 (Ngoại lệ VIP): Thẻ VIP Diamond toàn hệ thống -> Được phép vào mọi chi nhánh")
    void testRule4_TheVIPDuocVaoMoiChiNhanh() {
        // VIP007 là khách VIP toàn hệ thống
        CheckInService.CheckInResult res1 = checkInService.thucHienCheckIn("VIP007", "CN01 - Quận 1");
        assertTrue(res1.success, "Hội viên VIP phải được check-in tại CN01");
        assertEquals("OPEN", res1.gateStatus);
        assertTrue(res1.isVipSecret || res1.message.contains("VIP"));
    }

    @Test
    @DisplayName("Quy tắc 5: Chống quét trùng lặp (Anti-duplicate scan trong vòng 2 phút)")
    void testRule5_ChongQuetTrungLap() {
        // Lần 1: Check-in thẻ hợp lệ QR001
        CheckInService.CheckInResult resLan1 = checkInService.thucHienCheckIn("QR001", "CN01 - Quận 1");
        assertTrue(resLan1.success, "Lần quẹt đầu tiên phải thành công");
        assertEquals("OPEN", resLan1.gateStatus);

        // Lần 2: Quét lại ngay lập tức mã QR001
        CheckInService.CheckInResult resLan2 = checkInService.thucHienCheckIn("QR001", "CN01 - Quận 1");
        assertFalse(resLan2.success, "Lần quẹt thứ 2 ngay lập tức phải bị chặn chống quét trùng");
        assertEquals("LOCKED", resLan2.gateStatus);
        assertTrue(resLan2.lyDoTuChoi.contains("quét trùng") || resLan2.message.contains("quét trùng"));
    }

    @Test
    @DisplayName("Quy tắc kiểm tra hiệu năng: Thời gian phản hồi kiểm soát cửa < 200ms")
    void testHieuNangPhanHoiNhanh() {
        CheckInService.CheckInResult res = checkInService.thucHienCheckIn("TUAN", "CN01 - Quận 1");
        assertTrue(res.responseTimeMs < 200, "Thời gian xử lý check-in phải dưới 200ms để không gây nghẽn flap barrier (thực tế: " + res.responseTimeMs + "ms)");
    }

    @Test
    @DisplayName("Yêu Cầu Ẩn 3: Vé lẻ 1 ngày (Guest Pass) - Mở cổng 1 lần duy nhất và chặn vào chui lần 2")
    void testYeuCauAn3_VeLeMotNgay_GuestPass() {
        CheckInService.GuestPassTicket ticket = checkInService.banVeLeNgay("Khách Kiểm Thử", "0909123456", "CN01 - Cơ sở 1 (Quận 1)", 80000);
        assertNotNull(ticket);
        assertTrue(ticket.ticketCode.startsWith("PASS-"));

        // Lần 1: Quét mã vé lẻ hợp lệ -> Cửa Flap Barrier mở
        CheckInService.CheckInResult res1 = checkInService.thucHienCheckIn(ticket.ticketCode, "CN01 - Cơ sở 1 (Quận 1)");
        assertTrue(res1.success, "Vé lẻ 1 ngày hợp lệ phải mở cửa được");
        assertEquals("OPEN", res1.gateStatus);

        // Lần 2: Quét lại mã vé lẻ đã dùng -> Phải bị từ chối chống bán chui / vào chui
        CheckInService.CheckInResult res2 = checkInService.thucHienCheckIn(ticket.ticketCode, "CN01 - Cơ sở 1 (Quận 1)");
        assertFalse(res2.success, "Vé lẻ đã sử dụng không được phép mở cửa lần thứ 2");
        assertEquals("LOCKED", res2.gateStatus);
        assertTrue(res2.lyDoTuChoi.contains("đã sử dụng") || res2.message.contains("ĐÃ SỬ DỤNG"));
    }

    @Test
    @DisplayName("Yêu Cầu Ẩn 2: Wifi CN02 yếu - Đệm ngoại tuyến OfflineSyncBuffer và Auto-Sync khi có mạng")
    void testYeuCauAn2_OfflineBufferVaAutoSync() {
        // Mô phỏng mất mạng tại CN02 (Tân Bình)
        checkInService.setNetworkOnline(false);
        assertFalse(checkInService.isNetworkOnline());

        int initialBuffer = checkInService.getOfflineBufferSize();
        CheckInService.CheckInResult res = checkInService.thucHienCheckIn("TUAN", "CN02 - Cơ sở 2 (Tân Bình)");
        assertTrue(res.success, "Chế độ offline vẫn phải check-in thành công");
        assertEquals(initialBuffer + 1, checkInService.getOfflineBufferSize(), "Phải đệm vào OfflineSyncBuffer");

        // Khôi phục mạng và tự động đồng bộ (Auto-Sync)
        checkInService.setNetworkOnline(true);
        int synced = checkInService.dongBoOfflineLenHeThong();
        assertTrue(synced >= 1, "Phải đồng bộ thành công các bản ghi trong hàng đợi");
        assertEquals(0, checkInService.getOfflineBufferSize(), "Hàng đợi đệm phải được xóa sạch sau khi sync");
    }

    @Test
    @DisplayName("Yêu Cầu Ẩn 1: VIP 50% Anh Đức - Lễ tân hoàn toàn không thấy giá gốc & không thấy chiết khấu 50%")
    void testYeuCauAn1_VipPolicyService_BaoMatGiaThuc() {
        VipPolicyService vipService = VipPolicyService.getInstance();
        double giaGoc = 6000000; // Gói 12 tháng 6.000.000 đ

        // Lễ tân tra cứu khách VIP của Anh Đức (Đỗ Minh Trí - VIP007)
        VipPolicyService.ThongTinGiaHienThi infoLeTan = vipService.tinhGiaTheoPhanQuyen("Lễ tân", "0909998877", "Đỗ Minh Trí", "VIP007", giaGoc);
        assertTrue(infoLeTan.isVipDacQuyen);
        assertEquals(3000000, infoLeTan.soTienThucThu, "Số tiền thực thu phải là 50% (3 triệu)");
        assertEquals(3000000, infoLeTan.giaGoc, "Giá gốc hiển thị cho Lễ tân phải bị tráo thành giá thực thu để giấu giá thực!");
        assertEquals(0, infoLeTan.soTienGiam, "Số tiền giảm hiển thị cho Lễ tân phải bằng 0 để giấu dòng chiết khấu");
        assertFalse(infoLeTan.choPhepNhanVienThayGiaGoc, "Phải chặn quyền xem giá gốc đối với Lễ tân");

        // Chủ phòng tập (Anh Đức) tra cứu
        VipPolicyService.ThongTinGiaHienThi infoChu = vipService.tinhGiaTheoPhanQuyen("Chủ", "0909998877", "Đỗ Minh Trí", "VIP007", giaGoc);
        assertTrue(infoChu.isVipDacQuyen);
        assertEquals(6000000, infoChu.giaGoc, "Chủ phải thấy đầy đủ giá niêm yết gốc 6 triệu");
        assertEquals(3000000, infoChu.soTienGiam, "Chủ phải thấy rõ chiết khấu 3 triệu");
        assertEquals(3000000, infoChu.soTienThucThu, "Chủ phải thấy rõ số tiền thực thu");
        assertTrue(infoChu.choPhepNhanVienThayGiaGoc);
    }
}
