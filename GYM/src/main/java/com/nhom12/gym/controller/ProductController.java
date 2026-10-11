package com.nhom12.gym.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductController - Điều phối quản lý sản phẩm POS, nước uống, whey, đồ tập
 * (Khớp chuẩn mô hình MVC Controller như trong cấu trúc đồ án yêu cầu)
 */
public class ProductController {

    public static class ProductItem {
        private String maSP;
        private String tenSP;
        private double donGia;
        private int tonKho;

        public ProductItem(String maSP, String tenSP, double donGia, int tonKho) {
            this.maSP = maSP;
            this.tenSP = tenSP;
            this.donGia = donGia;
            this.tonKho = tonKho;
        }

        public String getMaSP() { return maSP; }
        public String getTenSP() { return tenSP; }
        public double getDonGia() { return donGia; }
        public int getTonKho() { return tonKho; }
    }

    public List<ProductItem> layDanhSachSanPham() {
        List<ProductItem> list = new ArrayList<>();
        list.add(new ProductItem("SP01", "Nước khoáng Lavie 500ml", 10000, 150));
        list.add(new ProductItem("SP02", "Nước điện giải Revive 500ml", 15000, 120));
        list.add(new ProductItem("SP03", "Whey Protein Isolate Rule 1", 35000, 80));
        list.add(new ProductItem("SP04", "Găng tay tập Gym Pro", 85000, 30));
        list.add(new ProductItem("SP05", "Khăn cotton thấm mồ hôi FitLife", 30000, 50));
        return list;
    }

    public static void main(String[] args) {
        ProductController controller = new ProductController();
        System.out.println("=== DANH SACH SAN PHAM POS (ProductController) ===");
        for (ProductItem item : controller.layDanhSachSanPham()) {
            System.out.printf("- [%s] %s | Đơn giá: %,.0f VNĐ | Tồn: %d%n",
                    item.getMaSP(), item.getTenSP(), item.getDonGia(), item.getTonKho());
        }
    }
}
