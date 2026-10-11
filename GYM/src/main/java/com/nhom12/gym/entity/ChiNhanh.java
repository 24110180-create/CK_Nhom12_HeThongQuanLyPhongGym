package com.nhom12.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Thực thể Chi Nhánh (Cốt lõi cho mô hình Chuỗi Phòng Gym Đa Chi Nhánh FitLife Gym)
 * Quản lý thông tin từng cơ sở, quản lý phụ trách, sức chứa và liên kết dữ liệu nghiệp vụ
 */
@Entity
@Table(name = "ChiNhanh")
public class ChiNhanh implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MaChiNhanh", length = 30, nullable = false)
    private String maChiNhanh; // VD: 'CN01', 'CN02'

    @Column(name = "TenChiNhanh", length = 100, nullable = false)
    private String tenChiNhanh; // VD: 'FitLife Flagship - Quận 1'

    @Column(name = "DiaChi", length = 255, nullable = false)
    private String diaChi;

    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "QuanLyPhuTrach", length = 100)
    private String quanLyPhuTrach;

    @Column(name = "SucChuaToiDa")
    private Integer sucChuaToiDa;

    @Column(name = "TrangThai", length = 30)
    private String trangThai; // 'Đang hoạt động', 'Bảo trì', 'Tạm ngưng'

    public ChiNhanh() {
    }

    public ChiNhanh(String maChiNhanh, String tenChiNhanh, String diaChi, String soDienThoai,
                    String quanLyPhuTrach, Integer sucChuaToiDa, String trangThai) {
        this.maChiNhanh = maChiNhanh;
        this.tenChiNhanh = tenChiNhanh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.quanLyPhuTrach = quanLyPhuTrach;
        this.sucChuaToiDa = sucChuaToiDa;
        this.trangThai = trangThai;
    }

    public String getMaChiNhanh() {
        return maChiNhanh;
    }

    public void setMaChiNhanh(String maChiNhanh) {
        this.maChiNhanh = maChiNhanh;
    }

    public String getTenChiNhanh() {
        return tenChiNhanh;
    }

    public void setTenChiNhanh(String tenChiNhanh) {
        this.tenChiNhanh = tenChiNhanh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getQuanLyPhuTrach() {
        return quanLyPhuTrach;
    }

    public void setQuanLyPhuTrach(String quanLyPhuTrach) {
        this.quanLyPhuTrach = quanLyPhuTrach;
    }

    public Integer getSucChuaToiDa() {
        return sucChuaToiDa;
    }

    public void setSucChuaToiDa(Integer sucChuaToiDa) {
        this.sucChuaToiDa = sucChuaToiDa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maChiNhanh + " - " + tenChiNhanh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiNhanh chiNhanh = (ChiNhanh) o;
        return Objects.equals(maChiNhanh, chiNhanh.maChiNhanh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChiNhanh);
    }
}
