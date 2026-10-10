package com.nhom12.gym.util;

public class Auth {
   public static String tenDangNhap = null;
   public static String vaiTro = null;
   public static String hoTen = null;

   public static boolean isLogin() {
      return tenDangNhap != null;
   }

   public static boolean isManager() {
      return isLogin() && "Chủ".equalsIgnoreCase(vaiTro);
   }

   public static boolean isReceptionist() {
      return isLogin() && "Lễ tân".equalsIgnoreCase(vaiTro);
   }

   public static boolean isTrainer() {
      return isLogin() && "HLV".equalsIgnoreCase(vaiTro);
   }

   public static boolean isMember() {
      return isLogin() && "Hội viên".equalsIgnoreCase(vaiTro);
   }

   public static void clear() {
      tenDangNhap = null;
      vaiTro = null;
      hoTen = null;
   }
}
