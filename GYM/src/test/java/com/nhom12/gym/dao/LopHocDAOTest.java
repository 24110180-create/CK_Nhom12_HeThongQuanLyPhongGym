package com.nhom12.gym.dao;

import com.nhom12.gym.entity.DangKyLop;
import com.nhom12.gym.entity.LopHoc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử nghiệp vụ Lớp Học, Ghi Danh & Điểm Danh (HLV)")
class LopHocDAOTest {

    private LopHocDAO lopHocDAO;
    private DangKyLopDAO dangKyLopDAO;

    @BeforeEach
    void setUp() {
        lopHocDAO = new LopHocDAO();
        dangKyLopDAO = new DangKyLopDAO();
    }

    @Test
    @DisplayName("Lấy tất cả danh sách lớp học và đồng bộ sĩ số chuẩn xác")
    void testLayTatCaDongBoSiSo() {
        List<LopHoc> list = lopHocDAO.layTatCa();
        assertNotNull(list);
        assertFalse(list.isEmpty());

        for (LopHoc lh : list) {
            int actualHV = lopHocDAO.layDanhSachHocVienCuaLop(lh.getMaLop()).size();
            assertEquals(actualHV, lh.getSiSoHienTai(),
                    "Sĩ số lớp " + lh.getMaLop() + " phải đồng bộ chính xác với số lượng học viên thực tế");
        }
    }

    @Test
    @DisplayName("Thêm mới, tìm kiếm và xóa lớp học")
    void testThemVaXoaLopHoc() {
        String testMaLop = "TEST_" + System.currentTimeMillis();
        lopHocDAO.xoa(testMaLop);

        LopHoc lopMoi = new LopHoc(testMaLop, "Pilates Test Group", "Pilates", "HLV Test", "07:00 - 08:30 (T2-T4)", "Phong Test", 25, 0);

        boolean themOk = lopHocDAO.them(lopMoi);
        assertTrue(themOk, "Thêm lớp học mới phải thành công");

        LopHoc timLop = lopHocDAO.timTheoMa(testMaLop);
        assertNotNull(timLop, "Phải tìm thấy lớp vừa thêm");
        assertEquals("Pilates Test Group", timLop.getTenLop());

        // Cập nhật
        timLop.setTenLop("Pilates Advanced Group");
        boolean updateOk = lopHocDAO.capNhat(timLop);
        assertTrue(updateOk, "Cập nhật lớp học phải thành công");

        // Xóa
        boolean xoaOk = lopHocDAO.xoa(testMaLop);
        assertTrue(xoaOk, "Xóa lớp học phải thành công");
        assertNull(lopHocDAO.timTheoMa(testMaLop), "Sau khi xóa, tìm kiếm lớp phải trả về null");
    }

    @Test
    @DisplayName("Ghi danh học viên vào lớp và tự động tăng sĩ số")
    void testGhiDanhVaDongBoSiSo() {
        String testMaLop = "TEST_DK_" + System.currentTimeMillis();
        lopHocDAO.xoa(testMaLop);

        LopHoc lopMoi = new LopHoc(testMaLop, "Yoga Test Group", "Yoga", "HLV Thu Thao", "08:00 - 09:00", "Studio 1", 20, 0);
        lopHocDAO.them(lopMoi);

        int siSoBanDau = lopHocDAO.timTheoMa(testMaLop).getSiSoHienTai();

        // Ghi danh học viên 1
        DangKyLop dk1 = new DangKyLop(0, testMaLop, 9999, "Nguyen Van Test", "0911222333", new Date(), "Chưa điểm danh", "Test note");
        boolean dkOk = lopHocDAO.dangKyVaoLop(dk1);
        assertTrue(dkOk, "Ghi danh học viên vào lớp phải thành công");

        // Kiểm tra sĩ số tăng 1
        LopHoc lopSauDK = lopHocDAO.timTheoMa(testMaLop);
        assertEquals(siSoBanDau + 1, lopSauDK.getSiSoHienTai(), "Sĩ số phải tăng lên 1 sau khi ghi danh");

        // Chặn ghi danh trùng học viên
        DangKyLop dkTrung = new DangKyLop(0, testMaLop, 9999, "Nguyen Van Test", "0911222333", new Date(), "Chưa điểm danh", "");
        boolean dkTrungOk = lopHocDAO.dangKyVaoLop(dkTrung);
        assertFalse(dkTrungOk, "Không được phép ghi danh trùng học viên trong cùng một lớp");

        // Điểm danh có mặt
        List<DangKyLop> danhSachHV = lopHocDAO.layDanhSachHocVienCuaLop(testMaLop);
        assertFalse(danhSachHV.isEmpty());
        DangKyLop hvDaThem = danhSachHV.get(danhSachHV.size() - 1);

        boolean diemDanhOk = lopHocDAO.capNhatDiemDanh(hvDaThem.getId(), "Đã điểm danh");
        assertTrue(diemDanhOk, "Cập nhật điểm danh có mặt phải thành công");

        // Điểm danh tất cả
        lopHocDAO.diemDanhTatCa(testMaLop, "Đã điểm danh");

        // Hủy đăng ký và kiểm tra sĩ số giảm
        boolean huyOk = lopHocDAO.huyDangKy(hvDaThem.getId(), testMaLop);
        assertTrue(huyOk, "Hủy đăng ký học viên khỏi lớp phải thành công");

        LopHoc lopSauHuy = lopHocDAO.timTheoMa(testMaLop);
        assertEquals(siSoBanDau, lopSauHuy.getSiSoHienTai(), "Sĩ số phải giảm về ban đầu sau khi hủy đăng ký");

        // Dọn dẹp
        lopHocDAO.xoa(testMaLop);
        lopHocDAO.xoa("TEST_LH99");
        lopHocDAO.xoa("TEST_LH88");
    }
}
