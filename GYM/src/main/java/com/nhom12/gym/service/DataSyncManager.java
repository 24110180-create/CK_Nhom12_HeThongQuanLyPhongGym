package com.nhom12.gym.service;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trung tâm điều phối đồng bộ dữ liệu thời gian thực toàn hệ thống (System-Wide Real-Time Event Bus).
 * Đảm bảo mọi thao tác Thêm, Sửa, Xóa trên các DAO lập tức kích hoạt cập nhật giao diện
 * trên toàn bộ các Panel đang mở, không bao giờ để xảy ra tình trạng sai lệch hoặc không nhất quán dữ liệu.
 */
public class DataSyncManager {

    private static final Logger LOGGER = Logger.getLogger(DataSyncManager.class.getName());

    public enum DataType {
        HOI_VIEN,    // Thêm, cập nhật, gia hạn, khóa/mở thẻ, xóa hội viên
        HOA_DON,     // Giao dịch thu phí, đăng ký mới, gia hạn gói tập, POS bán hàng
        CHECK_IN,    // Lượt quét mã QR, điểm danh cửa vào
        GOI_TAP,     // Thêm, sửa, xóa gói tập dịch vụ
        LOP_HOC,     // Lớp nhóm, lịch dạy HLV, đăng ký lớp, điểm danh sĩ số
        NHAN_SU,     // Tài khoản nhân sự, phân ca, HLV
        ALL;         // Tái đồng bộ toàn bộ hệ thống

        public boolean isAffecting(DataType target) {
            return this == ALL || this == target;
        }
    }

    public interface SyncListener {
        void onDataChanged(DataType type);
    }

    private static final DataSyncManager INSTANCE = new DataSyncManager();

    public static DataSyncManager getInstance() {
        return INSTANCE;
    }

    private static final List<SyncListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Đăng ký lắng nghe sự kiện thay đổi dữ liệu
     */
    public static void addListener(SyncListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Hủy đăng ký lắng nghe
     */
    public static void removeListener(SyncListener listener) {
        listeners.remove(listener);
    }

    /**
     * Phát tín hiệu thay đổi dữ liệu tới toàn hệ thống.
     * Tự động điều phối chuyển tiếp luồng an toàn vào Event Dispatch Thread (Swing EDT).
     */
    public static void fireDataChanged(DataType type) {
        SwingUtilities.invokeLater(() -> {
            for (SyncListener listener : listeners) {
                try {
                    listener.onDataChanged(type);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Lỗi cập nhật UI khi đồng bộ dữ liệu sự kiện: " + type, e);
                }
            }
        });
    }
}
