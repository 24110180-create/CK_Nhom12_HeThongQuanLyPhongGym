package com.nhom12.gym.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DangKyLop")
public class DangKyLop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "MaLop", length = 30)
    private String maLop;

    @Column(name = "MaHoiVien")
    private int maHoiVien;

    @Column(name = "TenHoiVien", length = 100)
    private String tenHoiVien;

    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NgayDangKy")
    private Date ngayDangKy;

    @Column(name = "TrangThaiDiemDanh", length = 50)
    private String trangThaiDiemDanh; // "Đã điểm danh", "Chưa điểm danh", "Vắng mặt"

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    public DangKyLop() {
        this.ngayDangKy = new Date();
        this.trangThaiDiemDanh = "Chưa điểm danh";
    }

    public DangKyLop(int id, String maLop, int maHoiVien, String tenHoiVien, String soDienThoai,
                     Date ngayDangKy, String trangThaiDiemDanh, String ghiChu) {
        this.id = id;
        this.maLop = maLop;
        this.maHoiVien = maHoiVien;
        this.tenHoiVien = tenHoiVien;
        this.soDienThoai = soDienThoai;
        this.ngayDangKy = ngayDangKy != null ? ngayDangKy : new Date();
        this.trangThaiDiemDanh = trangThaiDiemDanh != null ? trangThaiDiemDanh : "Chưa điểm danh";
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public int getMaHoiVien() {
        return maHoiVien;
    }

    public void setMaHoiVien(int maHoiVien) {
        this.maHoiVien = maHoiVien;
    }

    public String getTenHoiVien() {
        return tenHoiVien;
    }

    public void setTenHoiVien(String tenHoiVien) {
        this.tenHoiVien = tenHoiVien;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public Date getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(Date ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public String getTrangThaiDiemDanh() {
        return trangThaiDiemDanh;
    }

    public void setTrangThaiDiemDanh(String trangThaiDiemDanh) {
        this.trangThaiDiemDanh = trangThaiDiemDanh;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
