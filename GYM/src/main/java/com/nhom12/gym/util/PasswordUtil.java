package com.nhom12.gym.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Tiện ích Băm và Mã hóa Mật khẩu Chuẩn An Toàn BCrypt & SHA-256 cho Hệ Thống FitLife Gym
 */
public class PasswordUtil {

    /**
     * Băm mật khẩu sang chuỗi BCrypt chuẩn công nghiệp (độ dài ~60 ký tự, Work factor = 12)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        try {
            return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        } catch (Throwable t) {
            // Fallback an toàn sang SHA-256
            return hashSha256(plainPassword);
        }
    }

    /**
     * Băm mật khẩu theo chuẩn SHA-256 (64 hex characters)
     */
    public static String hashSha256(String plainPassword) {
        if (plainPassword == null) return null;
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
            throw new RuntimeException("Lỗi băm SHA-256: " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra một chuỗi đã là mã băm (BCrypt hoặc SHA-256) hay chưa
     */
    public static boolean isHashed(String password) {
        if (password == null) {
            return false;
        }
        // BCrypt định dạng $2a$ / $2b$ / $2y$ độ dài 60
        if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) {
            return password.length() == 60;
        }
        // SHA-256 độ dài 64 hex
        return password.length() == 64 && password.matches("^[0-9a-fA-F]{64}$");
    }

    /**
     * Xác thực mật khẩu:
     * 1. Đối soát mã băm BCrypt
     * 2. Đối soát mã băm SHA-256 (nếu tài khoản dùng SHA-256 cũ)
     * 3. Đối soát mật khẩu thô (nếu tài khoản chưa kịp băm)
     */
    public static boolean checkPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }

        // 1. Kiểm tra khớp BCrypt
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(plainPassword, storedPassword);
            } catch (Throwable ignored) {
            }
        }

        // 2. Kiểm tra khớp SHA-256
        String sha = hashSha256(plainPassword);
        if (sha.equalsIgnoreCase(storedPassword)) {
            return true;
        }

        // 3. Khớp chuỗi thô (tương thích ngược)
        return plainPassword.equals(storedPassword);
    }
}
