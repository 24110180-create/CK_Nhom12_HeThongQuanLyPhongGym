package com.nhom12.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GoiTap")
public class GoiTap {

    @Id
    @Column(name = "MaGoiTap", length = 30)
    private String maGoiTap;

    @Column(name = "TenGoiTap", nullable = false, length = 100)
    private String tenGoiTap;

    @Column(name = "ThoiHanThang")
    private int thoiHanThang;

    @Column(name = "GiaTien")
    private double giaTien;

    @Column(name = "PhamVi", length = 100)
    private String phamVi;

    @Column(name = "MoTa", length = 255)
    private String moTa;

    @Column(name = "TrangThai", length = 50)
    private String trangThai;

    public GoiTap() {
    }

    public GoiTap(String maGoiTap, String tenGoiTap, int thoiHanThang, double giaTien, String phamVi, String moTa, String trangThai) {
        this.maGoiTap = maGoiTap;
        this.tenGoiTap = tenGoiTap;
        this.thoiHanThang = thoiHanThang;
        this.giaTien = giaTien;
        this.phamVi = phamVi;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public String getMaGoiTap() {
        return maGoiTap;
    }

    public void setMaGoiTap(String maGoiTap) {
        this.maGoiTap = maGoiTap;
    }

    public String getTenGoiTap() {
        return tenGoiTap;
    }

    public void setTenGoiTap(String tenGoiTap) {
        this.tenGoiTap = tenGoiTap;
    }

    public int getThoiHanThang() {
        return thoiHanThang;
    }

    public void setThoiHanThang(int thoiHanThang) {
        this.thoiHanThang = thoiHanThang;
    }

    public double getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(double giaTien) {
        this.giaTien = giaTien;
    }

    public String getPhamVi() {
        return phamVi;
    }

    public void setPhamVi(String phamVi) {
        this.phamVi = phamVi;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
