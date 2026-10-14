package com.nhom12.gym.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tiện ích xuất dữ liệu ra tệp CSV có thể mở bằng Microsoft Excel.
 * Sử dụng chuẩn mã hóa UTF-8 BOM (\uFEFF) giúp tiếng Việt có dấu hiển thị hoàn hảo,
 * không bị lỗi font trên Windows và Excel.
 */
public class ExcelExportHelper {

    /**
     * Xuất dữ liệu từ JTable ra tệp CSV
     * @param parentComponent Component cha để hiển thị thông báo
     * @param table Bảng JTable cần xuất
     * @param defaultTitle Tiêu đề gợi ý tên file (ví dụ: "BaoCao_DoanhThu")
     */
    public static void exportTableToExcel(Component parentComponent, JTable table, String defaultTitle) {
        if (table == null || table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parentComponent,
                    "Không có dữ liệu trong bảng để xuất báo cáo!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String suggestedFileName = defaultTitle + "_" + sdf.format(new Date()) + ".csv";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu báo cáo CSV");
        fileChooser.setSelectedFile(new File(suggestedFileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Tệp CSV (*.csv) - mở được bằng Excel", "csv"));

        int userSelection = fileChooser.showSaveDialog(parentComponent);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
            fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
        }

        try (FileOutputStream fos = new FileOutputStream(fileToSave);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {

            // Ghi Byte Order Mark (BOM) UTF-8 để Microsoft Excel nhận diện font Tiếng Việt
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);

            TableModel model = table.getModel();
            int colCount = model.getColumnCount();
            int rowCount = model.getRowCount();

            // 1. Ghi dòng tiêu đề cột
            for (int col = 0; col < colCount; col++) {
                String colName = escapeCsv(table.getColumnName(col));
                bw.write(colName);
                if (col < colCount - 1) {
                    bw.write(",");
                }
            }
            bw.newLine();

            // 2. Ghi từng dòng dữ liệu
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    Object val = model.getValueAt(row, col);
                    String text = (val != null) ? val.toString() : "";
                    bw.write(escapeCsv(text));
                    if (col < colCount - 1) {
                        bw.write(",");
                    }
                }
                bw.newLine();
            }

            bw.flush();

            // Hỏi người dùng có muốn mở tệp CSV vừa xuất không
            int choice = JOptionPane.showConfirmDialog(parentComponent,
                    "<html><body style='width: 320px; font-family: Segoe UI;'>"
                            + "<b style='color: #16a34a; font-size: 14px;'>✓ Xuất tệp CSV thành công!</b><br><br>"
                            + "<b>Đường dẫn:</b> " + fileToSave.getAbsolutePath() + "<br>"
                            + "<b>Tổng số bản ghi:</b> " + rowCount + " dòng.<br><br>"
                            + "Bạn có muốn mở tệp bằng ứng dụng mặc định không?</body></html>",
                    "Thành công", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileToSave);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parentComponent,
                    "Lỗi khi xuất tệp CSV: " + ex.getMessage(),
                    "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String escapeCsv(String input) {
        if (input == null) return "\"\"";
        String escaped = input.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
