package com.nhom12.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(
   name = "TaiKhoan"
)
public class TaiKhoan {
   @Id
   @Column(
      name = "TenDangNhap"
   )
   private String tenDangNhap;
   @Column(
      name = "MatKhau",
      length = 255
   )
   private String matKhau;
   @Column(
      name = "VaiTro"
   )
   private String vaiTro;
   @Column(
      name = "HoTen"
   )
   private String hoTen;

   public TaiKhoan() {
   }

   public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, String hoTen) {
      this.tenDangNhap = tenDangNhap;
      this.matKhau = matKhau;
      this.vaiTro = vaiTro;
      this.hoTen = hoTen;
   }

   public String getTenDangNhap() {
      return this.tenDangNhap;
   }

   public void setTenDangNhap(String tenDangNhap) {
      this.tenDangNhap = tenDangNhap;
   }

   public String getMatKhau() {
      return this.matKhau;
   }

   public void setMatKhau(String matKhau) {
      this.matKhau = matKhau;
   }

   public String getVaiTro() {
      return this.vaiTro;
   }

   public void setVaiTro(String vaiTro) {
      this.vaiTro = vaiTro;
   }

   public String getHoTen() {
      return this.hoTen;
   }

   public void setHoTen(String hoTen) {
      this.hoTen = hoTen;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TaiKhoan)) {
         return false;
      } else {
         TaiKhoan other = (TaiKhoan)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$tenDangNhap = this.getTenDangNhap();
            Object other$tenDangNhap = other.getTenDangNhap();
            if (this$tenDangNhap == null) {
               if (other$tenDangNhap != null) {
                  return false;
               }
            } else if (!this$tenDangNhap.equals(other$tenDangNhap)) {
               return false;
            }

            Object this$matKhau = this.getMatKhau();
            Object other$matKhau = other.getMatKhau();
            if (this$matKhau == null) {
               if (other$matKhau != null) {
                  return false;
               }
            } else if (!this$matKhau.equals(other$matKhau)) {
               return false;
            }

            Object this$vaiTro = this.getVaiTro();
            Object other$vaiTro = other.getVaiTro();
            if (this$vaiTro == null) {
               if (other$vaiTro != null) {
                  return false;
               }
            } else if (!this$vaiTro.equals(other$vaiTro)) {
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

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof TaiKhoan;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $tenDangNhap = this.getTenDangNhap();
      result = result * 59 + ($tenDangNhap == null ? 43 : $tenDangNhap.hashCode());
      Object $matKhau = this.getMatKhau();
      result = result * 59 + ($matKhau == null ? 43 : $matKhau.hashCode());
      Object $vaiTro = this.getVaiTro();
      result = result * 59 + ($vaiTro == null ? 43 : $vaiTro.hashCode());
      Object $hoTen = this.getHoTen();
      result = result * 59 + ($hoTen == null ? 43 : $hoTen.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = this.getTenDangNhap();
      return "TaiKhoan(tenDangNhap=" + var10000 + ", matKhau=" + this.getMatKhau() + ", vaiTro=" + this.getVaiTro() + ", hoTen=" + this.getHoTen() + ")";
   }
}
