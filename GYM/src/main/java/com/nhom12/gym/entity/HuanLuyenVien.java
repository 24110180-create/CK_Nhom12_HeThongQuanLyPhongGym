package com.nhom12.gym.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "HuanLuyenVien")
public class HuanLuyenVien {

    @Id
    @Column(name = "MaHLV", length = 30)
    private String maHLV;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "ChuyenMon", length = 100)
    private String chuyenMon;

    @Column(name = "KinhNghiemNam")
    private int kinhNghiemNam;

    @Column(name = "ChiNhanh", length = 50)
    private String chiNhanh;

    @Column(name = "CaLamViec", length = 50)
    private String caLamViec;

    @Column(name = "DanhGiaSao")
    private double danhGiaSao;

    @Column(name = "TrangThai", length = 30)
    private String trangThai; // "Đang làm việc", "Nghỉ phép", "Tạm ngưng"

    public HuanLuyenVien() {
    }

    public HuanLuyenVien(String maHLV, String hoTen, String soDienThoai, String chuyenMon,
                         int kinhNghiemNam, String chiNhanh, String caLamViec, double danhGiaSao, String trangThai) {
        this.maHLV = maHLV;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.chuyenMon = chuyenMon;
        this.kinhNghiemNam = kinhNghiemNam;
        this.chiNhanh = chiNhanh;
        this.caLamViec = caLamViec;
        this.danhGiaSao = danhGiaSao;
        this.trangThai = trangThai;
    }

    public String getMaHLV() {
        return maHLV;
    }

    public void setMaHLV(String maHLV) {
        this.maHLV = maHLV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getChuyenMon() {
        return chuyenMon;
    }

    public void setChuyenMon(String chuyenMon) {
        this.chuyenMon = chuyenMon;
    }

    public int getKinhNghiemNam() {
        return kinhNghiemNam;
    }

    public void setKinhNghiemNam(int kinhNghiemNam) {
        this.kinhNghiemNam = kinhNghiemNam;
    }

    public String getChiNhanh() {
        return chiNhanh;
    }

    public void setChiNhanh(String chiNhanh) {
        this.chiNhanh = chiNhanh;
    }

    public String getCaLamViec() {
        return caLamViec;
    }

    public void setCaLamViec(String caLamViec) {
        this.caLamViec = caLamViec;
    }

    public double getDanhGiaSao() {
        return danhGiaSao;
    }

    public void setDanhGiaSao(double danhGiaSao) {
        this.danhGiaSao = danhGiaSao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return hoTen + " (" + chuyenMon + ")";
    }
}
