package com.nhom12.gym.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Thực thể Nhật Ký Hệ Thống (NhatKyHeThong)
 * Phục vụ kiểm toán thao tác (Audit Log), chống thất thoát dữ liệu và ghi vết người dùng.
 */
@Entity
@Table(name = "NhatKyHeThong")
public class NhatKyHeThong implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNhatKy")
    private int maNhatKy;

    @Column(name = "TenDangNhap", length = 50, nullable = false)
    private String tenDangNhap;

    @Column(name = "VaiTro", length = 50)
    private String vaiTro;

    @Column(name = "HanhDong", length = 100, nullable = false)
    private String hanhDong;

    @Column(name = "ChiTiet", length = 500)
    private String chiTiet;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ThoiGian")
    private Date thoiGian;

    public NhatKyHeThong() {
        this.thoiGian = new Date();
    }

    public NhatKyHeThong(String tenDangNhap, String vaiTro, String hanhDong, String chiTiet) {
        this.tenDangNhap = (tenDangNhap != null && !tenDangNhap.trim().isEmpty()) ? tenDangNhap.trim() : "he_thong";
        this.vaiTro = vaiTro;
        this.hanhDong = hanhDong;
        this.chiTiet = chiTiet;
        this.thoiGian = new Date();
    }

    public NhatKyHeThong(int maNhatKy, String tenDangNhap, String vaiTro, String hanhDong, String chiTiet, Date thoiGian) {
        this.maNhatKy = maNhatKy;
        this.tenDangNhap = tenDangNhap;
        this.vaiTro = vaiTro;
        this.hanhDong = hanhDong;
        this.chiTiet = chiTiet;
        this.thoiGian = thoiGian != null ? thoiGian : new Date();
    }

    public int getMaNhatKy() {
        return maNhatKy;
    }

    public void setMaNhatKy(int maNhatKy) {
        this.maNhatKy = maNhatKy;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(String hanhDong) {
        this.hanhDong = hanhDong;
    }

    public String getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(String chiTiet) {
        this.chiTiet = chiTiet;
    }

    public Date getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Date thoiGian) {
        this.thoiGian = thoiGian;
    }
}
