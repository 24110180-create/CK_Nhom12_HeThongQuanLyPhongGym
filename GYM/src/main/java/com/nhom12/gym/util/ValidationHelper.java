package com.nhom12.gym.util;

import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Lớp tiện ích kiểm tra dữ liệu đầu vào (Validation Helper)
 * Tách biệt hoàn toàn Business Validation Logic (nhận String thuần, độc lập UI)
 * và Swing UI Validation (nhận JTextField, hiển thị JOptionPane)
 */
public class ValidationHelper {

    /**
     * Regex chuẩn số điện thoại di động Việt Nam (10 số, đầu 03, 05, 07, 08, 09)
     */
    public static final String REGEX_SO_DIEN_THOAI_VN = "^0(3|5|7|8|9)[0-9]{8}$";

    /**
     * Regex chuẩn địa chỉ Email theo tiêu chuẩn RFC 5322
     */
    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    private static final Pattern PATTERN_PHONE = Pattern.compile(REGEX_SO_DIEN_THOAI_VN);
    private static final Pattern PATTERN_EMAIL = Pattern.compile(REGEX_EMAIL);

    // =========================================================================
    // 1. CÁC HÀM VALIDATION THUẦN (PURE BUSINESS LOGIC - KHÔNG PHỤ THUỘC SWING)
    // =========================================================================

    /**
     * Kiểm tra chuỗi có rỗng hoặc null hay không
     */
    public static boolean isRong(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Kiểm tra số điện thoại có hợp lệ theo chuẩn mạng viễn thông Việt Nam
     */
    public static boolean isSoDienThoaiHopLe(String phone) {
        if (phone == null) return false;
        return PATTERN_PHONE.matcher(phone.trim()).matches();
    }

    /**
     * Alias ngắn gọn cho isSoDienThoaiHopLe
     */
    public static boolean isSoDienThoai(String phone) {
        return isSoDienThoaiHopLe(phone);
    }

    /**
     * Kiểm tra định dạng địa chỉ Email hợp lệ
     */
    public static boolean isEmail(String email) {
        if (email == null) return false;
        return PATTERN_EMAIL.matcher(email.trim()).matches();
    }

    /**
     * Kiểm tra một chuỗi có phải số nguyên dương (> 0) hay không
     */
    public static boolean isSoNguyenDuong(String text) {
        if (isRong(text)) return false;
        try {
            long val = Long.parseLong(text.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Kiểm tra một chuỗi có phải số thực dương (>= 0) hay không
     */
    public static boolean isSoThucDuong(String text) {
        if (isRong(text)) return false;
        try {
            double val = Double.parseDouble(text.trim());
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Kiểm tra tính hợp lệ của chuỗi ngày tháng theo mẫu quy định (nghiêm ngặt leniency = false)
     */
    public static boolean isNgayHopLe(String dateStr, String pattern) {
        if (isRong(dateStr) || isRong(pattern)) return false;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            Date parsed = sdf.parse(dateStr.trim());
            return parsed != null;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Parse chuỗi ngày sang đối tượng Date an toàn
     */
    public static Date parseNgay(String dateStr, String pattern) {
        if (isRong(dateStr) || isRong(pattern)) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            return sdf.parse(dateStr.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Tính tuổi chính xác dựa trên ngày sinh và ngày hiện tại
     */
    public static int tinhTuoi(Date birthDate) {
        if (birthDate == null) return 0;
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(birthDate);
        Calendar nowCal = Calendar.getInstance();
        int age = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
        if (nowCal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    /**
     * Kiểm tra hội viên có đủ tuổi quy định tối thiểu hay không
     */
    public static boolean isDuTuoi(Date birthDate, int doTuoiToiThieu) {
        if (birthDate == null) return false;
        if (birthDate.after(new Date())) return false; // Ngày sinh tương lai
        return tinhTuoi(birthDate) >= doTuoiToiThieu;
    }

    // =========================================================================
    // 2. CÁC HÀM VALIDATION CHO GIAO DIỆN SWING (TƯƠNG THÍCH NGƯỢC HOÀN TOÀN)
    // =========================================================================

    public static boolean isRong(JTextField field) {
        return field == null || isRong(field.getText());
    }

    public static boolean checkRong(Component parent, JTextField field, String tenTruong) {
        if (field == null || isRong(field.getText())) {
            JOptionPane.showMessageDialog(
                parent,
                "Vui lòng không để trống " + tenTruong + "!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE
            );
            if (field != null) {
                field.requestFocus();
            }
            return false;
        }
        return true;
    }

    public static boolean checkRongHangLoat(Component parent, JTextField[] fields, String[] labels) {
        if (fields == null || labels == null) return true;
        for (int i = 0; i < fields.length; i++) {
            if (i < labels.length) {
                if (!checkRong(parent, fields[i], labels[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkSoDienThoai(Component parent, JTextField field) {
        if (field == null) return false;
        String phone = field.getText().trim();

        if (isRong(phone)) {
            JOptionPane.showMessageDialog(
                parent,
                "Số điện thoại không được để trống!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE
            );
            field.requestFocus();
            return false;
        }

        if (!isSoDienThoaiHopLe(phone)) {
            JOptionPane.showMessageDialog(
                parent,
                "Số điện thoại không hợp lệ!\nVui lòng nhập đúng 10 chữ số bắt đầu bằng các đầu số Việt Nam (03, 05, 07, 08, 09).",
                "Lỗi Định Dạng",
                JOptionPane.WARNING_MESSAGE
            );
            field.requestFocus();
            return false;
        }

        return true;
    }

    public static boolean checkNgaySinh(Component parent, JTextField field, int doTuoiToiThieu) {
        if (field == null) return false;
        String birthStr = field.getText().trim();

        if (isRong(birthStr)) {
            JOptionPane.showMessageDialog(parent, "Ngày sinh không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            field.requestFocus();
            return false;
        }

        Date birthDate = parseNgay(birthStr, "dd/MM/yyyy");
        if (birthDate == null) {
            JOptionPane.showMessageDialog(
                parent,
                "Ngày sinh không hợp lệ hoặc không tồn tại trên lịch (ví dụ: 32/13/2000)!\nVui lòng nhập đúng định dạng chuẩn dd/MM/yyyy (ví dụ: 15/05/2000).",
                "Lỗi Định Dạng Ngày Sinh",
                JOptionPane.WARNING_MESSAGE
            );
            field.requestFocus();
            return false;
        }

        if (birthDate.after(new Date())) {
            JOptionPane.showMessageDialog(parent, "Ngày sinh không thể lớn hơn ngày hiện tại!", "Sai Ngày Sinh", JOptionPane.WARNING_MESSAGE);
            field.requestFocus();
            return false;
        }

        int age = tinhTuoi(birthDate);
        if (age < doTuoiToiThieu) {
            JOptionPane.showMessageDialog(
                parent,
                "Hội viên chưa đủ tuổi!\nTheo quy định của phòng gym, hội viên phải từ " + doTuoiToiThieu + " tuổi trở lên mới được tạo hồ sơ (Độ tuổi hiện tại: " + age + " tuổi).",
                "Quy Định Độ Tuổi",
                JOptionPane.WARNING_MESSAGE
            );
            field.requestFocus();
            return false;
        }

        return true;
    }
}
