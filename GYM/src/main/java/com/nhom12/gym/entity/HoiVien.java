package com.nhom12.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(
   name = "HoiVien"
)
public class HoiVien {
   @Id
   @GeneratedValue(
      strategy = GenerationType.IDENTITY
   )
   @Column(
      name = "MaHoiVien"
   )
   private int maHoiVien;
   @Column(
      name = "MaTheQR",
      unique = true
   )
   private String maTheQR;
   @Column(
      name = "HoTen"
   )
   private String hoTen;
   @Column(
      name = "ChiNhanhDangKy"
   )
   private String chiNhanhDangKy;
   @Column(
      name = "NgayHetHan"
   )
   @Temporal(TemporalType.DATE)
   private Date ngayHetHan;
   @Column(
      name = "TrangThai"
   )
   private String trangThai;

   public HoiVien() {
   }

   public HoiVien(int maHoiVien, String maTheQR, String hoTen, String chiNhanhDangKy, Date ngayHetHan, String trangThai) {
      this.maHoiVien = maHoiVien;
      this.maTheQR = maTheQR;
      this.hoTen = hoTen;
      this.chiNhanhDangKy = chiNhanhDangKy;
      this.ngayHetHan = ngayHetHan;
      this.trangThai = trangThai;
   }

   public int getMaHoiVien() {
      return this.maHoiVien;
   }

   public void setMaHoiVien(int maHoiVien) {
      this.maHoiVien = maHoiVien;
   }

   public String getMaTheQR() {
      return this.maTheQR;
   }

   public void setMaTheQR(String maTheQR) {
      this.maTheQR = maTheQR;
   }

   public String getHoTen() {
      return this.hoTen;
   }

   public void setHoTen(String hoTen) {
      this.hoTen = hoTen;
   }

   public String getChiNhanhDangKy() {
      return this.chiNhanhDangKy;
   }

   public void setChiNhanhDangKy(String chiNhanhDangKy) {
      this.chiNhanhDangKy = chiNhanhDangKy;
   }

   public Date getNgayHetHan() {
      return this.ngayHetHan;
   }

   public void setNgayHetHan(Date ngayHetHan) {
      this.ngayHetHan = ngayHetHan;
   }

   public String getTrangThai() {
      return this.trangThai;
   }

   public void setTrangThai(String trangThai) {
      this.trangThai = trangThai;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof HoiVien)) {
         return false;
      } else {
         HoiVien other = (HoiVien)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (this.getMaHoiVien() != other.getMaHoiVien()) {
            return false;
         } else {
            Object this$maTheQR = this.getMaTheQR();
            Object other$maTheQR = other.getMaTheQR();
            if (this$maTheQR == null) {
               if (other$maTheQR != null) {
                  return false;
               }
            } else if (!this$maTheQR.equals(other$maTheQR)) {
               return false;
            }

            Object this$hoTen = this.getHoTen();
            Object other$hoTen = other.getHoTen();
            if (this$hoTen == null) {
               if (other$hoTen != null) {
                  return false;
               }
            } else if (!this$hoTen.equals(other$hoTen)) {
               return false;
            }

            Object this$chiNhanhDangKy = this.getChiNhanhDangKy();
            Object other$chiNhanhDangKy = other.getChiNhanhDangKy();
            if (this$chiNhanhDangKy == null) {
               if (other$chiNhanhDangKy != null) {
                  return false;
               }
            } else if (!this$chiNhanhDangKy.equals(other$chiNhanhDangKy)) {
               return false;
            }

            Object this$ngayHetHan = this.getNgayHetHan();
            Object other$ngayHetHan = other.getNgayHetHan();
            if (this$ngayHetHan == null) {
               if (other$ngayHetHan != null) {
                  return false;
               }
            } else if (!this$ngayHetHan.equals(other$ngayHetHan)) {
               return false;
            }

            Object this$trangThai = this.getTrangThai();
            Object other$trangThai = other.getTrangThai();
            if (this$trangThai == null) {
               if (other$trangThai != null) {
                  return false;
               }
            } else if (!this$trangThai.equals(other$trangThai)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof HoiVien;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getMaHoiVien();
      Object $maTheQR = this.getMaTheQR();
      result = result * 59 + ($maTheQR == null ? 43 : $maTheQR.hashCode());
      Object $hoTen = this.getHoTen();
      result = result * 59 + ($hoTen == null ? 43 : $hoTen.hashCode());
      Object $chiNhanhDangKy = this.getChiNhanhDangKy();
      result = result * 59 + ($chiNhanhDangKy == null ? 43 : $chiNhanhDangKy.hashCode());
      Object $ngayHetHan = this.getNgayHetHan();
      result = result * 59 + ($ngayHetHan == null ? 43 : $ngayHetHan.hashCode());
      Object $trangThai = this.getTrangThai();
      result = result * 59 + ($trangThai == null ? 43 : $trangThai.hashCode());
      return result;
   }

   public String toString() {
      int var10000 = this.getMaHoiVien();
      return "HoiVien(maHoiVien=" + var10000 + ", maTheQR=" + this.getMaTheQR() + ", hoTen=" + this.getHoTen() + ", chiNhanhDangKy=" + this.getChiNhanhDangKy() + ", ngayHetHan=" + String.valueOf(this.getNgayHetHan()) + ", trangThai=" + this.getTrangThai() + ")";
   }
}
