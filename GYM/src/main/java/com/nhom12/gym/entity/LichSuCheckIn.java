package com.nhom12.gym.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "LichSuCheckIn")
public class LichSuCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "MaHoiVien")
    private int maHoiVien;

    @Column(name = "TenHoiVien", length = 100)
    private String tenHoiVien;

    @Column(name = "MaTheQR", length = 50)
    private String maTheQR;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ThoiGian")
    private Date thoiGian;

    @Column(name = "ChiNhanh", length = 50)
    private String chiNhanh;

    @Column(name = "CuaRaVao", length = 50)
    private String cuaRaVao;

    @Column(name = "PhuongThuc", length = 30)
    private String phuongThuc;

    @Column(name = "TrangThai", length = 30)
    private String trangThai; // "Hợp lệ", "Từ chối"

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    @Column(name = "ThoiGianPhanHoiMs")
    private long thoiGianPhanHoiMs;

    public LichSuCheckIn() {
        this.thoiGian = new Date();
    }

    public LichSuCheckIn(int id, int maHoiVien, String tenHoiVien, String maTheQR, Date thoiGian,
                         String chiNhanh, String cuaRaVao, String phuongThuc, String trangThai,
                         String ghiChu, long thoiGianPhanHoiMs) {
        this.id = id;
        this.maHoiVien = maHoiVien;
        this.tenHoiVien = tenHoiVien;
        this.maTheQR = maTheQR;
        this.thoiGian = thoiGian != null ? thoiGian : new Date();
        this.chiNhanh = chiNhanh;
        this.cuaRaVao = cuaRaVao;
        this.phuongThuc = phuongThuc;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.thoiGianPhanHoiMs = thoiGianPhanHoiMs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMaTheQR() {
        return maTheQR;
    }

    public void setMaTheQR(String maTheQR) {
        this.maTheQR = maTheQR;
    }

    public Date getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Date thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getChiNhanh() {
        return chiNhanh;
    }

    public void setChiNhanh(String chiNhanh) {
        this.chiNhanh = chiNhanh;
    }

    public String getCuaRaVao() {
        return cuaRaVao;
    }

    public void setCuaRaVao(String cuaRaVao) {
        this.cuaRaVao = cuaRaVao;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public long getThoiGianPhanHoiMs() {
        return thoiGianPhanHoiMs;
    }

    public void setThoiGianPhanHoiMs(long thoiGianPhanHoiMs) {
        this.thoiGianPhanHoiMs = thoiGianPhanHoiMs;
    }
}
