package com.nhom12.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "LopHoc")
public class LopHoc {

    @Id
    @Column(name = "MaLop", length = 30)
    private String maLop;

    @Column(name = "TenLop", nullable = false, length = 100)
    private String tenLop;

    @Column(name = "BoMon", length = 50)
    private String boMon;

    @Column(name = "HuanLuyenVien", length = 100)
    private String huanLuyenVien;

    @Column(name = "ThoiGian", length = 100)
    private String thoiGian;

    @Column(name = "PhongTap", length = 50)
    private String phongTap;

    @Column(name = "SiSoToiDa")
    private int siSoToiDa;

    @Column(name = "SiSoHienTai")
    private int siSoHienTai;

    public LopHoc() {
    }

    public LopHoc(String maLop, String tenLop, String boMon, String huanLuyenVien, String thoiGian, String phongTap, int siSoToiDa, int siSoHienTai) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.boMon = boMon;
        this.huanLuyenVien = huanLuyenVien;
        this.thoiGian = thoiGian;
        this.phongTap = phongTap;
        this.siSoToiDa = siSoToiDa;
        this.siSoHienTai = siSoHienTai;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public String getBoMon() {
        return boMon;
    }

    public void setBoMon(String boMon) {
        this.boMon = boMon;
    }

    public String getHuanLuyenVien() {
        return huanLuyenVien;
    }

    public void setHuanLuyenVien(String huanLuyenVien) {
        this.huanLuyenVien = huanLuyenVien;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getPhongTap() {
        return phongTap;
    }

    public void setPhongTap(String phongTap) {
        this.phongTap = phongTap;
    }

    public int getSiSoToiDa() {
        return siSoToiDa;
    }

    public void setSiSoToiDa(int siSoToiDa) {
        this.siSoToiDa = siSoToiDa;
    }

    public int getSiSoHienTai() {
        return siSoHienTai;
    }

    public void setSiSoHienTai(int siSoHienTai) {
        this.siSoHienTai = siSoHienTai;
    }
}
