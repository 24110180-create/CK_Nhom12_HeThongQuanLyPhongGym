package com.nhom12.gym.service;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service quản lý phân quyền truy cập hệ thống theo vai trò (Role-Based Access Control - RBAC)
 * Lưu trữ cấu hình bền vững, hỗ trợ thêm/sửa/xóa nhóm quyền và cập nhật thời gian thực lên Dashboard.
 */
public class PermissionService {

    private static final PermissionService INSTANCE = new PermissionService();

    public static final String PERM_CHECKIN = "checkin";
    public static final String PERM_HOIVIEN = "hoivien";
    public static final String PERM_POS = "pos";
    public static final String PERM_LOPHOC = "lophoc";
    public static final String PERM_GOITAP = "goitap";
    public static final String PERM_BAOCAO = "baocao";
    public static final String PERM_NHANSU = "nhansu";
    public static final String PERM_XOA = "xoa";

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

        // 1. Quản trị viên (Chủ phòng tập) - Full 8 quyền
        Set<String> adminPerms = new HashSet<>(Arrays.asList(
                PERM_CHECKIN, PERM_HOIVIEN, PERM_POS, PERM_LOPHOC,
                PERM_GOITAP, PERM_BAOCAO, PERM_NHANSU, PERM_XOA
        ));
        rolePermissions.put("Quản trị viên (Chủ phòng tập)", adminPerms);

        // 2. Quản lý Chi nhánh
        Set<String> managerPerms = new HashSet<>(Arrays.asList(
                PERM_CHECKIN, PERM_HOIVIEN, PERM_POS, PERM_LOPHOC,
                PERM_GOITAP, PERM_BAOCAO, PERM_NHANSU
        ));
        rolePermissions.put("Quản lý Chi nhánh", managerPerms);

        // 3. Lễ tân Ca Sáng
        Set<String> ltMorningPerms = new HashSet<>(Arrays.asList(
                PERM_CHECKIN, PERM_HOIVIEN, PERM_POS, PERM_LOPHOC, PERM_GOITAP
        ));
        rolePermissions.put("Lễ tân Ca Sáng", ltMorningPerms);

        // 4. Lễ tân Ca Chiều
        Set<String> ltAfternoonPerms = new HashSet<>(Arrays.asList(
                PERM_CHECKIN, PERM_HOIVIEN, PERM_POS, PERM_LOPHOC, PERM_GOITAP
        ));
        rolePermissions.put("Lễ tân Ca Chiều", ltAfternoonPerms);

        // 5. Huấn Luyện Viên (HLV)
        Set<String> hlvPerms = new HashSet<>(Arrays.asList(
                PERM_CHECKIN, PERM_LOPHOC
        ));
        rolePermissions.put("Huấn Luyện Viên (HLV)", hlvPerms);

    }

    private String normalizeRole(String role) {
        if (role == null) return "Quản trị viên (Chủ phòng tập)";
        String r = role.trim();
        for (String k : rolePermissions.keySet()) {
            if (k.equalsIgnoreCase(r)) return k;
        }
        if (r.equalsIgnoreCase("Chủ") || r.toLowerCase().contains("chủ phòng") || r.toLowerCase().contains("admin")) {
            return "Quản trị viên (Chủ phòng tập)";
        }
        if (r.equalsIgnoreCase("Quản lý") || r.toLowerCase().contains("quản lý")) {
            return "Quản lý Chi nhánh";
        }
        if (r.equalsIgnoreCase("Lễ tân") || r.toLowerCase().contains("lễ tân")) {
            return "Lễ tân Ca Sáng";
        }
        if (r.equalsIgnoreCase("HLV") || r.toLowerCase().contains("huấn luyện")) {
            return "Huấn Luyện Viên (HLV)";
        }
        return r;
    }

    public synchronized boolean hasPermission(String role, String permKey) {
        if (permKey == null) return true;
        String normalized = normalizeRole(role);
        Set<String> perms = rolePermissions.get(normalized);
        if (perms == null) {
            // Mặc định vai trò không xác định hoặc Chủ luôn cho phép
            return "Quản trị viên (Chủ phòng tập)".equals(normalized);
        }
        return perms.contains(permKey);
    }

    public synchronized Set<String> getPermissions(String role) {
        String normalized = normalizeRole(role);
        Set<String> perms = rolePermissions.get(normalized);
        if (perms == null) return new HashSet<>();
        return new HashSet<>(perms);
    }

    public synchronized void setPermissions(String role, Set<String> perms) {
        String normalized = normalizeRole(role);
        rolePermissions.put(normalized, new HashSet<>(perms));
        luuRaFile();
        notifyChange();
    }

    public synchronized void addRole(String roleName, Set<String> initialPerms) {
        if (roleName == null || roleName.trim().isEmpty()) return;
        rolePermissions.put(roleName.trim(), initialPerms != null ? new HashSet<>(initialPerms) : new HashSet<>());
        luuRaFile();
        notifyChange();
    }

    public synchronized void removeRole(String roleName) {
        if (roleName == null) return;
        rolePermissions.remove(roleName.trim());
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
        if (listener != null) {
            changeListeners.add(listener);
        }
    }

    public void removeChangeListener(Runnable listener) {
        changeListeners.remove(listener);
    }

    private void notifyChange() {
        for (Runnable r : changeListeners) {
            try {
                r.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void luuRaFile() {
        Properties props = new Properties();
        for (Map.Entry<String, Set<String>> entry : rolePermissions.entrySet()) {
            props.setProperty(entry.getKey(), String.join(",", entry.getValue()));
        }
        try (OutputStream os = new FileOutputStream(configFile)) {
            props.store(os, "FitLife Gym Role-Based Access Control Permissions");
        } catch (Exception ignored) {
        }
    }

    private synchronized void taiTuFile() {
        if (!configFile.exists()) return;
        try (InputStream is = new FileInputStream(configFile)) {
            Properties props = new Properties();
            props.load(is);
            for (String key : props.stringPropertyNames()) {
                String val = props.getProperty(key);
                if (val != null) {
                    Set<String> perms = new HashSet<>();
                    for (String p : val.split(",")) {
                        if (!p.trim().isEmpty()) perms.add(p.trim());
                    }
                    rolePermissions.put(key, perms);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
