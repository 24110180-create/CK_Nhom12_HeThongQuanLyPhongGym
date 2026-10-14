package com.nhom12.gym.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PermissionService {
    private static final Logger LOGGER = Logger.getLogger(PermissionService.class.getName());
    private static final PermissionService INSTANCE = new PermissionService();

    public static final String PERM_CHECKIN = "checkin";
    public static final String PERM_HOIVIEN = "hoivien";
    public static final String PERM_POS = "pos";
    public static final String PERM_LOPHOC = "lophoc";
    public static final String PERM_GOITAP = "goitap";
    public static final String PERM_BAOCAO = "baocao";
    public static final String PERM_NHANSU = "nhansu";
    public static final String PERM_XOA = "xoa";
    public static final String PERM_SHIFTS = "shifts";
    public static final String PERM_MEMBER_ACCOUNTS = "member_accounts";
    public static final String PERM_STAFF_ACCOUNTS = "staff_accounts";
    public static final String PERM_PT_MANAGE = "pt_manage";
    public static final String PERM_PT_OWN = "pt_own";
    public static final String PERM_INBODY = "inbody";
    public static final String PERM_KHO = "kho";
    public static final String PERM_SYSTEM = "system";

    private final Map<String, Set<String>> rolePermissions = new LinkedHashMap<>();
    private final List<Runnable> changeListeners = new CopyOnWriteArrayList<>();
    private final File configFile = new File("fitlife_permissions.properties");

    private PermissionService() {
        khoiTaoMacDinh();
        taiTuFile();
    }

    public static PermissionService getInstance() {
        return INSTANCE;
    }

    private void khoiTaoMacDinh() {
        rolePermissions.clear();
        rolePermissions.put("Chủ", setOf(
                PERM_CHECKIN, PERM_HOIVIEN, PERM_POS, PERM_LOPHOC, PERM_GOITAP,
                PERM_BAOCAO, PERM_NHANSU, PERM_XOA, PERM_SHIFTS, PERM_MEMBER_ACCOUNTS,
                PERM_STAFF_ACCOUNTS, PERM_PT_MANAGE, PERM_PT_OWN, PERM_INBODY, PERM_KHO, PERM_SYSTEM));
        rolePermissions.put("Quản lý", setOf(
                PERM_CHECKIN, PERM_HOIVIEN, PERM_POS, PERM_LOPHOC, PERM_GOITAP,
                PERM_BAOCAO, PERM_NHANSU, PERM_SHIFTS, PERM_MEMBER_ACCOUNTS,
                PERM_PT_MANAGE, PERM_PT_OWN, PERM_INBODY, PERM_KHO));
        rolePermissions.put("Lễ tân", setOf(
                PERM_CHECKIN, PERM_HOIVIEN, PERM_POS, PERM_LOPHOC, PERM_GOITAP,
                PERM_MEMBER_ACCOUNTS, PERM_PT_MANAGE));
        rolePermissions.put("HLV", setOf(PERM_LOPHOC, PERM_PT_OWN, PERM_INBODY));
        rolePermissions.put("Hội viên", new HashSet<>());
    }

    private Set<String> setOf(String... permissions) {
        return new LinkedHashSet<>(Arrays.asList(permissions));
    }

    private String normalizeRole(String role) {
        if (role == null) return null;
        String value = role.trim();
        for (String knownRole : rolePermissions.keySet()) {
            if (knownRole.equalsIgnoreCase(value)) return knownRole;
        }
        if (value.equalsIgnoreCase("Quản trị viên (Chủ phòng tập)")
                || value.toLowerCase().contains("chủ phòng") || value.equalsIgnoreCase("admin")) {
            return "Chủ";
        }
        if (value.equalsIgnoreCase("Quản lý Chi nhánh")) return "Quản lý";
        if (value.toLowerCase().startsWith("lễ tân ca")) return "Lễ tân";
        if (value.equalsIgnoreCase("Huấn Luyện Viên (HLV)")) return "HLV";
        return value;
    }

    public synchronized boolean hasPermission(String role, String permKey) {
        if (permKey == null || permKey.trim().isEmpty()) return false;
        Set<String> permissions = rolePermissions.get(normalizeRole(role));
        return permissions != null && permissions.contains(permKey);
    }

    public synchronized boolean hasAccessToModule(String role, String moduleTitle) {
        String permission = permissionForModule(moduleTitle);
        return permission != null && hasPermission(role, permission);
    }

    private String permissionForModule(String title) {
        if (title == null) return null;
        String normalized = title.toLowerCase();
        if (normalized.contains("tài khoản nhân sự") || normalized.contains("phân quyền")) return PERM_STAFF_ACCOUNTS;
        if (normalized.contains("tài khoản app") || normalized.contains("tài khoản hội viên")) return PERM_MEMBER_ACCOUNTS;
        if (normalized.contains("lịch pt được giao") || normalized.contains("sổ tay tiến độ pt")
                || normalized.contains("hồ sơ học viên phụ trách")) return PERM_PT_OWN;
        if (normalized.contains("lịch huấn luyện pt")) return PERM_PT_MANAGE;
        if (normalized.contains("lịch làm việc") || normalized.contains("phân ca")) return PERM_SHIFTS;
        if (normalized.contains("quét thẻ") || normalized.contains("check-in")) return PERM_CHECKIN;
        if (normalized.contains("quản lý hội viên") || normalized.contains("gia hạn gói")) return PERM_HOIVIEN;
        if (normalized.contains("lớp học")) return PERM_LOPHOC;
        if (normalized.contains("pos") || normalized.contains("bán hàng")) return PERM_POS;
        if (normalized.contains("gói tập") || normalized.contains("khuyến mãi")) return PERM_GOITAP;
        if (normalized.contains("nhân sự") || normalized.contains("bảng lương")) return PERM_NHANSU;
        if (normalized.contains("kho hàng")) return PERM_KHO;
        if (normalized.contains("inbody")) return PERM_INBODY;
        if (normalized.contains("báo cáo") || normalized.contains("phân tích")
                || normalized.contains("giao dịch") || normalized.contains("trang chủ")) return PERM_BAOCAO;
        if (normalized.contains("cài đặt")) return PERM_SYSTEM;
        return null;
    }

    public synchronized Set<String> getPermissions(String role) {
        Set<String> permissions = rolePermissions.get(normalizeRole(role));
        return permissions == null ? new HashSet<>() : new HashSet<>(permissions);
    }

    public synchronized void setPermissions(String role, Set<String> permissions) {
        String normalized = normalizeRole(role);
        if (!rolePermissions.containsKey(normalized) || permissions == null) return;
        rolePermissions.put(normalized, new HashSet<>(permissions));
        luuRaFile();
        notifyChange();
    }

    public synchronized void addRole(String roleName, Set<String> initialPermissions) {
        if (roleName == null || roleName.trim().isEmpty()) return;
        rolePermissions.put(roleName.trim(), initialPermissions == null
                ? new HashSet<>() : new HashSet<>(initialPermissions));
        luuRaFile();
        notifyChange();
    }

    public synchronized void removeRole(String roleName) {
        String normalized = normalizeRole(roleName);
        if (normalized == null || Arrays.asList("Chủ", "Quản lý", "Lễ tân", "HLV", "Hội viên").contains(normalized)) return;
        rolePermissions.remove(normalized);
        luuRaFile();
        notifyChange();
    }

    public synchronized List<String> getAllRoles() {
        return new ArrayList<>(rolePermissions.keySet());
    }

    public synchronized void resetToDefault() {
        khoiTaoMacDinh();
        luuRaFile();
        notifyChange();
    }

    public void addChangeListener(Runnable listener) {
        if (listener != null) changeListeners.add(listener);
    }

    public void removeChangeListener(Runnable listener) {
        changeListeners.remove(listener);
    }

    private void notifyChange() {
        for (Runnable listener : changeListeners) {
            try {
                listener.run();
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING, "Không thể thông báo cập nhật quyền", e);
            }
        }
    }

    private synchronized void luuRaFile() {
        Properties properties = new Properties();
        for (Map.Entry<String, Set<String>> entry : rolePermissions.entrySet()) {
            properties.setProperty(entry.getKey(), String.join(",", entry.getValue()));
        }
        try (OutputStream output = new FileOutputStream(configFile)) {
            properties.store(output, "FitLife Gym Role-Based Access Control Permissions");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể lưu cấu hình phân quyền", e);
        }
    }

    private synchronized void taiTuFile() {
        if (!configFile.exists()) return;
        try (InputStream input = new FileInputStream(configFile)) {
            Properties properties = new Properties();
            properties.load(input);
            for (String key : properties.stringPropertyNames()) {
                String role = normalizeRole(key);
                if (!rolePermissions.containsKey(role)) continue;
                Set<String> permissions = new HashSet<>();
                String value = properties.getProperty(key);
                if (value != null) {
                    for (String permission : value.split(",")) {
                        if (!permission.trim().isEmpty()) permissions.add(permission.trim());
                    }
                }
                rolePermissions.put(role, permissions);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể đọc cấu hình phân quyền; sử dụng quyền mặc định an toàn", e);
        }
    }
}
