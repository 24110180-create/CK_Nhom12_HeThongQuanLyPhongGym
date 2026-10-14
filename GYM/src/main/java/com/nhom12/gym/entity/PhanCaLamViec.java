package com.nhom12.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(name = "PhanCaLamViec", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"NgayLam", "MaNhanVien", "CaLam"})
})
public class PhanCaLamViec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NgayLam", nullable = false)
    private LocalDate ngayLam;

    @Column(name = "MaNhanVien", nullable = false, length = 60)
    private String maNhanVien;

    @Column(name = "TenNhanVien", nullable = false, length = 100)
    private String tenNhanVien;

    @Column(name = "VaiTro", nullable = false, length = 30)
    private String vaiTro;

    @Column(name = "ChiNhanh", nullable = false, length = 100)
    private String chiNhanh;

    @Column(name = "CaLam", nullable = false, length = 20)
    private String caLam;

    public PhanCaLamViec() {
    }

    public PhanCaLamViec(LocalDate ngayLam, String maNhanVien, String tenNhanVien,
                         String vaiTro, String chiNhanh, String caLam) {
        this.ngayLam = ngayLam;
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.vaiTro = vaiTro;
        this.chiNhanh = chiNhanh;
        this.caLam = caLam;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getNgayLam() { return ngayLam; }
    public void setNgayLam(LocalDate ngayLam) { this.ngayLam = ngayLam; }
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    public String getChiNhanh() { return chiNhanh; }
    public void setChiNhanh(String chiNhanh) { this.chiNhanh = chiNhanh; }
    public String getCaLam() { return caLam; }
    public void setCaLam(String caLam) { this.caLam = caLam; }
}
