package com.nhom12.gym.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "HoaDon")
public class HoaDon {

    @Id
    @Column(name = "MaHoaDon", length = 30)
    private String maHoaDon;

    @Column(name = "MaHoiVien")
    private int maHoiVien;

    @Column(name = "TenHoiVien", length = 100)
    private String tenHoiVien;

    @Column(name = "LoaiGiaoDich", length = 50)
    private String loaiGiaoDich;

    @Column(name = "TongTien")
    private double tongTien;

    @Column(name = "GiamGia")
    private double giamGia;

    @Column(name = "ThanhTien")
    private double thanhTien;

    @Column(name = "HinhThucThanhToan", length = 50)
    private String hinhThucThanhToan; // "Tiền mặt", "VietQR Chuyển khoản"

    @Column(name = "NguoiLap", length = 50)
    private String nguoiLap;

    @Column(name = "ChiNhanh", length = 50)
    private String chiNhanh;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NgayLap")
    private Date ngayLap;

    @Column(name = "TrangThai", length = 30)
    private String trangThai; // "Đã thanh toán", "Đã hủy"

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    public HoaDon() {
        this.ngayLap = new Date();
    }

    public HoaDon(String maHoaDon, int maHoiVien, String tenHoiVien, String loaiGiaoDich,
                  double tongTien, double giamGia, double thanhTien, String hinhThucThanhToan,
                  String nguoiLap, String chiNhanh, Date ngayLap, String trangThai, String ghiChu) {
        this.maHoaDon = maHoaDon;
        this.maHoiVien = maHoiVien;
        this.tenHoiVien = tenHoiVien;
        this.loaiGiaoDich = loaiGiaoDich;
        this.tongTien = tongTien;
        this.giamGia = giamGia;
        this.thanhTien = thanhTien;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.nguoiLap = nguoiLap;
        this.chiNhanh = chiNhanh;
        this.ngayLap = ngayLap != null ? ngayLap : new Date();
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
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

    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public String getNguoiLap() {
        return nguoiLap;
    }

    public void setNguoiLap(String nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public String getChiNhanh() {
        return chiNhanh;
    }

    public void setChiNhanh(String chiNhanh) {
        this.chiNhanh = chiNhanh;
    }

    public Date getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(Date ngayLap) {
        this.ngayLap = ngayLap;
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
}
