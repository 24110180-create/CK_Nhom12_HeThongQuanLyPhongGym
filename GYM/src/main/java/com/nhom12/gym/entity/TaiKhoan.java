package com.nhom12.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể Tài khoản hệ thống (Phân quyền 5 vai trò: Chủ, Lễ tân, HLV, Quản lý, Hội viên)
 */
@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TenDangNhap", length = 50, nullable = false)
    private String tenDangNhap;

    @Column(name = "MatKhau", length = 255, nullable = false)
    private String matKhau;

    @Column(name = "VaiTro", length = 50, nullable = false)
    private String vaiTro;

    @Column(name = "HoTen", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "ChiNhanh", length = 100)
    private String chiNhanh = "Toàn hệ thống";

    @Column(name = "MaHoiVien")
    private Integer maHoiVien;

    @Column(name = "TrangThaiTaiKhoan", length = 30)
    private String trangThaiTaiKhoan = "Đang hoạt động";

    public TaiKhoan() {
    }

    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, String hoTen) {
        this(tenDangNhap, matKhau, vaiTro, hoTen, "Toàn hệ thống");
    }

    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, String hoTen, String chiNhanh) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.hoTen = hoTen;
        this.chiNhanh = chiNhanh != null ? chiNhanh : "Toàn hệ thống";
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getChiNhanh() {
        return chiNhanh;
    }

    public void setChiNhanh(String chiNhanh) {
        this.chiNhanh = chiNhanh != null ? chiNhanh : "Toàn hệ thống";
    }

    public Integer getMaHoiVien() {
        return maHoiVien;
    }

    public void setMaHoiVien(Integer maHoiVien) {
        this.maHoiVien = maHoiVien;
    }

    public String getTrangThaiTaiKhoan() {
        return trangThaiTaiKhoan == null ? "Đang hoạt động" : trangThaiTaiKhoan;
    }

    public void setTrangThaiTaiKhoan(String trangThaiTaiKhoan) {
        this.trangThaiTaiKhoan = trangThaiTaiKhoan != null ? trangThaiTaiKhoan : "Đang hoạt động";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoan taiKhoan = (TaiKhoan) o;
        return Objects.equals(tenDangNhap, taiKhoan.tenDangNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenDangNhap);
    }

    @Override
    public String toString() {
        // Bảo mật: Không in chuỗi mật khẩu ra log/toString
        return "TaiKhoan{" +
                "tenDangNhap='" + tenDangNhap + '\'' +
                ", vaiTro='" + vaiTro + '\'' +
                ", hoTen='" + hoTen + '\'' +
                '}';
    }
}
