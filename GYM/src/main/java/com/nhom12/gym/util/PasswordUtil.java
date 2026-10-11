package com.nhom12.gym.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Tiện ích Băm và Mã hóa Mật khẩu Chuẩn An Toàn SHA-256 cho Hệ Thống FitLife Gym
 */
public class PasswordUtil {

    /**
     * Băm mật khẩu chuỗi thuần sang mã băm SHA-256 (64 ký tự hex)
     * Ví dụ: "123456" -> "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi băm mật khẩu bằng thuật toán SHA-256", e);
        }
    }

    /**
     * Kiểm tra một chuỗi đã là mã băm SHA-256 chuẩn (64 ký tự hex) hay chưa
     */
    public static boolean isHashed(String password) {
        if (password == null || password.length() != 64) {
            return false;
        }
        return password.matches("^[0-9a-fA-F]{64}$");
    }

    /**
     * Kiểm tra tính hợp lệ của mật khẩu:
     * 1. So khớp mã băm SHA-256
     * 2. Tương thích ngược nếu cơ sở dữ liệu cũ chưa kịp băm
     */
    public static boolean checkPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }
        String hashedInput = hashPassword(plainPassword);
        if (hashedInput.equalsIgnoreCase(storedPassword)) {
            return true;
        }
        // Fallback tương thích dữ liệu cũ
        return plainPassword.equals(storedPassword);
    }

    public static void main(String[] args) {
        String raw = "123456";
        String hash = hashPassword(raw);
        System.out.println("=== KIEM TRA BAM MAT KHAU SHA-256 ===");
        System.out.println("Mat khau goc: " + raw);
        System.out.println("Ma bam SHA-256: " + hash);
        System.out.println("Kiem tra do dai & hop le: " + isHashed(hash));
        System.out.println("Xac thuc dang nhap: " + checkPassword(raw, hash));
    }
}
