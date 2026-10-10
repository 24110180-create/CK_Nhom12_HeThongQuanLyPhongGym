package com.nhom12.gym.util;

import java.awt.Component;
import javax.swing.JOptionPane;

public class DialogHelper {
   public static boolean confirm(Component parent, String message, String title) {
      int choice = JOptionPane.showConfirmDialog(parent, message, title != null ? title : "Xác nhận thao tác", 0, 3);
      return choice == 0;
   }

   public static void showError(Component parent, String errorMsg) {
      JOptionPane.showMessageDialog(parent, "<html><b>❌ LỖI HỆ THỐNG:</b><br>" + errorMsg + "<br><br><i>Vui lòng kiểm tra lại kết nối cơ sở dữ liệu hoặc liên hệ Quản trị viên.</i></html>", "Lỗi Kết Nối / Hệ Thống", 0);
   }

   public static void showSuccess(Component parent, String successMsg) {
      JOptionPane.showMessageDialog(parent, successMsg, "Thành công", 1);
   }

   public static void showWarning(Component parent, String warningMsg) {
      JOptionPane.showMessageDialog(parent, "<html><b>⚠ CẢNH BÁO OFFLINE:</b><br>" + warningMsg + "<br><i>Hệ thống đang lưu dữ liệu tạm vào bộ nhớ đệm (Cache).</i></html>", "Cảnh báo hệ thống", 2);
   }
}
