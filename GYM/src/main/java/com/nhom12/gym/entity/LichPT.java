package com.nhom12.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "LichPT")
public class LichPT {

    public static final String DA_DAT = "Đã đặt";
    public static final String HOAN_THANH = "Hoàn thành";
    public static final String DA_HUY = "Đã hủy";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MaHoiVien", nullable = false)
    private int maHoiVien;

    @Column(name = "TenHoiVien", nullable = false, length = 100)
    private String tenHoiVien;

    @Column(name = "SoDienThoaiHoiVien", length = 20)
    private String soDienThoaiHoiVien;

    @Column(name = "MaHLV", nullable = false, length = 30)
    private String maHLV;

    @Column(name = "TenHLV", nullable = false, length = 100)
    private String tenHLV;

    @Column(name = "ChiNhanh", nullable = false, length = 100)
    private String chiNhanh;

    @Column(name = "BatDau", nullable = false)
    private LocalDateTime batDau;

    @Column(name = "ThoiLuongPhut", nullable = false)
    private int thoiLuongPhut;

    @Column(name = "TrangThai", nullable = false, length = 30)
    private String trangThai = DA_DAT;

    @Column(name = "GhiChu", length = 500)
    private String ghiChu;

    @Column(name = "NguoiTao", length = 50)
    private String nguoiTao;

    public LichPT() {
    }

    public LichPT(int maHoiVien, String tenHoiVien, String soDienThoaiHoiVien,
                  String maHLV, String tenHLV, String chiNhanh, LocalDateTime batDau,
                  int thoiLuongPhut, String trangThai, String ghiChu, String nguoiTao) {
        this.maHoiVien = maHoiVien;
        this.tenHoiVien = tenHoiVien;
        this.soDienThoaiHoiVien = soDienThoaiHoiVien;
        this.maHLV = maHLV;
        this.tenHLV = tenHLV;
        this.chiNhanh = chiNhanh;
        this.batDau = batDau;
        this.thoiLuongPhut = thoiLuongPhut;
        this.trangThai = trangThai == null ? DA_DAT : trangThai;
        this.ghiChu = ghiChu;
        this.nguoiTao = nguoiTao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getMaHoiVien() { return maHoiVien; }
    public void setMaHoiVien(int maHoiVien) { this.maHoiVien = maHoiVien; }
    public String getTenHoiVien() { return tenHoiVien; }
    public void setTenHoiVien(String tenHoiVien) { this.tenHoiVien = tenHoiVien; }
    public String getSoDienThoaiHoiVien() { return soDienThoaiHoiVien; }
    public void setSoDienThoaiHoiVien(String soDienThoaiHoiVien) { this.soDienThoaiHoiVien = soDienThoaiHoiVien; }
    public String getMaHLV() { return maHLV; }
    public void setMaHLV(String maHLV) { this.maHLV = maHLV; }
    public String getTenHLV() { return tenHLV; }
    public void setTenHLV(String tenHLV) { this.tenHLV = tenHLV; }
    public String getChiNhanh() { return chiNhanh; }
    public void setChiNhanh(String chiNhanh) { this.chiNhanh = chiNhanh; }
    public LocalDateTime getBatDau() { return batDau; }
    public void setBatDau(LocalDateTime batDau) { this.batDau = batDau; }
    public int getThoiLuongPhut() { return thoiLuongPhut; }
    public void setThoiLuongPhut(int thoiLuongPhut) { this.thoiLuongPhut = thoiLuongPhut; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public String getNguoiTao() { return nguoiTao; }
    public void setNguoiTao(String nguoiTao) { this.nguoiTao = nguoiTao; }
}
