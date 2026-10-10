package com.nhom12.gym.service;

import com.nhom12.gym.dao.HoiVienDAO;
import com.nhom12.gym.entity.HoiVien;
import java.util.Date;
import java.util.List;

public class HoiVienService {
   private final HoiVienDAO dao = new HoiVienDAO();

   public List<HoiVien> layDanhSachHoiVien() {
      return this.dao.layTatCa();
   }

   public String luuHoiVien(HoiVien hv, boolean isUpdate) {
      if (hv.getHoTen().trim().isEmpty()) {
         return "LỖI: Tên không được để trống!";
      } else if (hv.getMaTheQR().trim().isEmpty()) {
         return "LỖI: Mã QR không được để trống!";
      } else if (hv.getNgayHetHan() != null && hv.getNgayHetHan().before(new Date())) {
         return "CẢNH BÁO: Ngày hết hạn đang set ở quá khứ!";
      } else {
         boolean success = isUpdate ? this.dao.capNhat(hv) : this.dao.them(hv);
         return success ? "THÀNH CÔNG!" : "LỖI: Không thể lưu vào Cơ sở dữ liệu (Có thể trùng mã QR).";
      }
   }

   public boolean xoaHoiVien(int maHoiVien) {
      return this.dao.xoa(maHoiVien);
   }
}
