package com.nhom12.gym.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Thực thể Hội viên VIP đặc quyền (HoiVienVipDacQuyen)
 * Lưu trữ danh sách hội viên áp dụng chính sách ưu đãi nội bộ bảo mật theo vai trò.
 */
@Entity
@Table(name = "HoiVienVipDacQuyen")
public class HoiVienVipDacQuyen implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MaThe", length = 30)
    private String maThe;

    @Column(name = "HoTen", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "SoDienThoai", length = 15, nullable = false)
    private String soDienThoai;

    @Column(name = "MoTaNoiBo", length = 200)
    private String moTaNoiBo;

    @Column(name = "TiLeGiam", columnDefinition = "DECIMAL(5,2)")
    private double tiLeGiam = 50.0;

    @Column(name = "GhiChuBaoMat", length = 200)
    private String ghiChuBaoMat;

    public HoiVienVipDacQuyen() {
    }

    public HoiVienVipDacQuyen(String maThe, String hoTen, String soDienThoai, String moTaNoiBo, double tiLeGiam, String ghiChuBaoMat) {
        this.maThe = maThe;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.moTaNoiBo = moTaNoiBo;
        this.tiLeGiam = tiLeGiam;
        this.ghiChuBaoMat = ghiChuBaoMat;
    }

    public String getMaThe() {
        return maThe;
    }

    public void setMaThe(String maThe) {
        this.maThe = maThe;
    }

    public String getMaTheQR() {
        return maThe;
    }

    public void setMaTheQR(String maTheQR) {
        this.maThe = maTheQR;
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

    public String getMoTaNoiBo() {
        return moTaNoiBo;
    }

    public void setMoTaNoiBo(String moTaNoiBo) {
        this.moTaNoiBo = moTaNoiBo;
    }

    public String getMoiQuanHe() {
        return moTaNoiBo;
    }

    public void setMoiQuanHe(String moiQuanHe) {
        this.moTaNoiBo = moiQuanHe;
    }

    public double getTiLeGiam() {
        return tiLeGiam;
    }

    public void setTiLeGiam(double tiLeGiam) {
        this.tiLeGiam = tiLeGiam;
    }

    public String getGhiChuBaoMat() {
        return ghiChuBaoMat;
    }

    public void setGhiChuBaoMat(String ghiChuBaoMat) {
        this.ghiChuBaoMat = ghiChuBaoMat;
    }
}
