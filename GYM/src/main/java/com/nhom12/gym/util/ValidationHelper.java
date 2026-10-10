package com.nhom12.gym.util;

import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Lớp tiện ích kiểm tra dữ liệu đầu vào (Validation Helper) cho các Form và Panel Swing.
 */
public class ValidationHelper {

    private static final Border ERROR_BORDER = BorderFactory.createLineBorder(new Color(239, 68, 68), 1);
    private static final Border DEFAULT_BORDER = BorderFactory.createLineBorder(new Color(203, 213, 225), 1);

    /**
     * Kiểm tra nhanh chuỗi rỗng của 1 ô nhập JTextField:
     * - Cắt khoảng trắng trim() và kiểm tra isEmpty()
     * - Bật thông báo JOptionPane.showMessageDialog chuẩn cú pháp WARNING_MESSAGE
     * - Tự động focus con trỏ vào ô bị trống
     *
     * @param parent    Component cha (this / JFrame / JPanel)
     * @param field     Ô nhập dữ liệu JTextField
     * @param tenTruong Tên trường hiển thị trong thông báo lỗi (vd: "Họ và tên")
     * @return true nếu hợp lệ (không rỗng), false nếu rỗng
     */
    public static boolean checkRong(Component parent, JTextField field, String tenTruong) {
        if (field == null || field.getText().trim().isEmpty()) {
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

    /**
     * Kiểm tra nhanh chuỗi rỗng không kèm giao diện (trả về boolean).
     */
    public static boolean isRong(JTextField field) {
        return field == null || field.getText().trim().isEmpty();
    }

    /**
     * Kiểm tra nhanh chuỗi rỗng theo giá trị String.
     */
    public static boolean isRong(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Kiểm tra rỗng hàng loạt nhiều JTextField cùng lúc.
     * Dừng lại và cảnh báo ngay tại ô trống đầu tiên tìm thấy.
     *
     * @param parent Component cha
     * @param fields Mảng các JTextField cần kiểm tra
     * @param labels Mảng tên hiển thị tương ứng
     * @return true nếu tất cả đều không rỗng, false nếu có ít nhất 1 ô rỗng
     */
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

    /**
     * Biểu thức chính quy (Regex) chuẩn số điện thoại di động Việt Nam:
     * - Bắt đầu bằng 0
     * - Đầu số thuộc các nhà mạng: 03, 05, 07, 08, 09 (Viettel, Mobi, Vina, Vietnamobile, Wintel/Itelecom)
     * - Theo sau đúng 8 chữ số (tổng cộng đúng 10 số)
     */
    public static final String REGEX_SO_DIEN_THOAI_VN = "^0(3|5|7|8|9)[0-9]{8}$";

    /**
     * Kiểm tra số điện thoại có hợp lệ theo chuẩn mạng viễn thông Việt Nam hay không.
     */
    public static boolean isSoDienThoaiHopLe(String phone) {
        if (phone == null) return false;
        return phone.trim().matches(REGEX_SO_DIEN_THOAI_VN);
    }

    /**
     * Kiểm tra số điện thoại trong JTextField kèm thông báo lỗi và focus:
     * - Báo lỗi nếu để trống
     * - Báo lỗi nếu không đúng định dạng nhà mạng Việt Nam
     *
     * @param parent Component cha
     * @param field  Ô JTextField nhập số điện thoại
     * @return true nếu hợp lệ, false nếu lỗi
     */
    public static boolean checkSoDienThoai(Component parent, JTextField field) {
        if (field == null) return false;
        String phone = field.getText().trim();

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(
                parent,
                "Số điện thoại không được để trống!",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE
            );
            field.requestFocus();
            return false;
        }

        if (!phone.matches(REGEX_SO_DIEN_THOAI_VN)) {
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
     * Kiểm tra ngày sinh trong JTextField:
     * - Báo lỗi nếu để trống
     * - Bọc try-catch bắt ParseException với setLenient(false) để phát hiện ngày sai hoặc không tồn tại
     * - Chặn ngày sinh trong tương lai
     * - Kiểm tra nghiệp vụ độ tuổi tối thiểu (vd: 15 tuổi)
     *
     * @param parent           Component cha
     * @param field            Ô JTextField nhập ngày sinh
     * @param doTuoiToiThieu   Độ tuổi tối thiểu quy định (vd: 15)
     * @return true nếu hợp lệ, false nếu lỗi
     */
    public static boolean checkNgaySinh(Component parent, JTextField field, int doTuoiToiThieu) {
        if (field == null) return false;
        String birthStr = field.getText().trim();

        if (birthStr.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Ngày sinh không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            field.requestFocus();
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            Date birthDate = sdf.parse(birthStr);
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
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(
                parent,
                "Ngày sinh không hợp lệ hoặc không tồn tại trên lịch (ví dụ: 32/13/2000)!\nVui lòng nhập đúng định dạng chuẩn dd/MM/yyyy (ví dụ: 15/05/2000).",
                "Lỗi Định Dạng Ngày Sinh",
                JOptionPane.WARNING_MESSAGE
            );
            field.requestFocus();
            return false;
        }

        return true;
    }
}
