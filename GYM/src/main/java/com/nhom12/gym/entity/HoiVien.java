package com.nhom12.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Thực thể Hội viên phòng tập (Hỗ trợ mã thẻ QR định danh và phân quyền chi nhánh)
 */
@Entity
@Table(name = "HoiVien")
public class HoiVien implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHoiVien")
    private int maHoiVien;

    @Column(name = "MaTheQR", unique = true, nullable = false, length = 50)
    private String maTheQR;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "ChiNhanhDangKy", length = 100)
    private String chiNhanhDangKy;

    @Column(name = "NgayHetHan", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ngayHetHan;

    @Column(name = "TrangThai", length = 30)
    private String trangThai;

    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "GoiTapDangKy", length = 100)
    private String goiTapDangKy;

    @Column(name = "NgayDangKy")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayDangKy;

    @Column(name = "SoTienDaThanhToan")
    private Double soTienDaThanhToan;

    @Column(name = "HinhThucThanhToan", length = 50)
    private String hinhThucThanhToan;

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

    public HoiVien(int maHoiVien, String maTheQR, String hoTen, String soDienThoai, String chiNhanhDangKy,
                   String goiTapDangKy, Date ngayDangKy, Date ngayHetHan, Double soTienDaThanhToan,
                   String hinhThucThanhToan, String trangThai) {
        this.maHoiVien = maHoiVien;
        this.maTheQR = maTheQR;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.chiNhanhDangKy = chiNhanhDangKy;
        this.goiTapDangKy = goiTapDangKy;
        this.ngayDangKy = ngayDangKy;
        this.ngayHetHan = ngayHetHan;
        this.soTienDaThanhToan = soTienDaThanhToan;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.trangThai = trangThai;
    }

    public int getMaHoiVien() {
        return maHoiVien;
    }

    public void setMaHoiVien(int maHoiVien) {
        this.maHoiVien = maHoiVien;
    }

    public String getMaTheQR() {
        return maTheQR;
    }

    public void setMaTheQR(String maTheQR) {
        this.maTheQR = maTheQR;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getChiNhanhDangKy() {
        return chiNhanhDangKy;
    }

    public void setChiNhanhDangKy(String chiNhanhDangKy) {
        this.chiNhanhDangKy = chiNhanhDangKy;
    }

    public Date getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(Date ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getGoiTapDangKy() {
        return goiTapDangKy;
    }

    public void setGoiTapDangKy(String goiTapDangKy) {
        this.goiTapDangKy = goiTapDangKy;
    }

    public Date getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(Date ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public Double getSoTienDaThanhToan() {
        return soTienDaThanhToan != null ? soTienDaThanhToan : 0.0;
    }

    public void setSoTienDaThanhToan(Double soTienDaThanhToan) {
        this.soTienDaThanhToan = soTienDaThanhToan;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoiVien hoiVien = (HoiVien) o;
        if (maHoiVien > 0 && hoiVien.maHoiVien > 0) {
            return maHoiVien == hoiVien.maHoiVien;
        }
        return Objects.equals(maTheQR, hoiVien.maTheQR);
    }

    @Override
    public int hashCode() {
        return maHoiVien > 0 ? Objects.hash(maHoiVien) : Objects.hash(maTheQR);
    }

    @Override
    public String toString() {
        return "HoiVien{" +
                "maHoiVien=" + maHoiVien +
                ", maTheQR='" + maTheQR + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", goiTapDangKy='" + goiTapDangKy + '\'' +
                ", ngayDangKy=" + ngayDangKy +
                ", soTienDaThanhToan=" + soTienDaThanhToan +
                ", hinhThucThanhToan='" + hinhThucThanhToan + '\'' +
                ", chiNhanhDangKy='" + chiNhanhDangKy + '\'' +
                ", ngayHetHan=" + ngayHetHan +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
