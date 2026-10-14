package com.nhom12.gym.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * TRUNG TÂM QUẢN TRỊ & TIẾP NHẬN HỘI VIÊN (MEMBER HUB)
 * Hợp nhất thông minh giữa "Danh Sách & Tra Cứu Hội Viên" và "Tiếp Nhận & Đăng Ký Mới",
 * loại bỏ hoàn toàn mâu thuẫn và trùng lặp trên giao diện điều hành.
 */
public class QuanLyHoiVienHubPanel extends JPanel {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsPanel = new JPanel(cardLayout);

    private static final String CARD_LIST = "CARD_LIST";
    private static final String CARD_REGISTER = "CARD_REGISTER";
    private static final String CARD_EXPIRING = "CARD_EXPIRING";

    private QuanLyHoiVienPanel listPanel;
    private DangKyHoiVienPanel registerPanel;
    private HoiVienSapHetHanPanel expiringPanel;

    private JButton btnTabList;
    private JButton btnTabRegister;
    private JButton btnTabExpiring;

    public QuanLyHoiVienHubPanel() {
        this(0);
    }

    public QuanLyHoiVienHubPanel(int initialTab) {
        setLayout(new BorderLayout(0, 8));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(10, 14, 12, 14));

        initHubUI();
        selectTab(initialTab);
    }

    private void initHubUI() {
        // --- 1. TOP NAVIGATION TAB BAR (HIỆN ĐẠI, TINH TẾ) ---
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 4, 6, 4));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("TRUNG TÂM QUẢN TRỊ & HỒ SƠ HỘI VIÊN FITLIFE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Hồ sơ hội viên chuỗi cơ sở • Tra cứu thẻ QR, cảnh báo tái ký & tiếp nhận đăng ký mới");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        topBar.add(titlePanel, BorderLayout.WEST);

        // Tab Switcher Buttons (Pill Segmented Control)
        JPanel tabSwitcher = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        tabSwitcher.setOpaque(false);

        btnTabList = createTabButton("Danh Sách & Tra Cứu Hội Viên", true);
        btnTabRegister = createTabButton("+ Tiếp Nhận & Đăng Ký Mới", false);
        btnTabExpiring = createTabButton("⚠️ Hội Viên Sắp Hết Hạn & Tái Ký", false);

        btnTabList.addActionListener(e -> selectTab(0));
        btnTabRegister.addActionListener(e -> selectTab(1));
        btnTabExpiring.addActionListener(e -> selectTab(2));

        tabSwitcher.add(btnTabList);
        tabSwitcher.add(btnTabRegister);
        tabSwitcher.add(btnTabExpiring);
        topBar.add(tabSwitcher, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // --- 2. CARDS CONTAINER ---
        listPanel = new QuanLyHoiVienPanel(() -> selectTab(1));
        registerPanel = new DangKyHoiVienPanel(
                () -> selectTab(0),
                () -> {
                    listPanel.loadDataToTable();
                    selectTab(0);
                }
        );
        expiringPanel = new HoiVienSapHetHanPanel();

        cardsPanel.setOpaque(false);
        cardsPanel.add(listPanel, CARD_LIST);
        cardsPanel.add(registerPanel, CARD_REGISTER);
        cardsPanel.add(expiringPanel, CARD_EXPIRING);

        add(cardsPanel, BorderLayout.CENTER);
    }

    private JButton createTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 35));
        updateTabButtonStyle(btn, active);
        return btn;
    }

    private void updateTabButtonStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(new Color(37, 99, 235));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(29, 78, 216), 1),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(71, 85, 105));
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    new EmptyBorder(6, 12, 6, 12)
            ));
        }
    }

    public void selectTab(int index) {
        if (index == 1) {
            cardLayout.show(cardsPanel, CARD_REGISTER);
            updateTabButtonStyle(btnTabList, false);
            updateTabButtonStyle(btnTabRegister, true);
            updateTabButtonStyle(btnTabExpiring, false);
        } else if (index == 2) {
            cardLayout.show(cardsPanel, CARD_EXPIRING);
            updateTabButtonStyle(btnTabList, false);
            updateTabButtonStyle(btnTabRegister, false);
            updateTabButtonStyle(btnTabExpiring, true);
            if (expiringPanel != null) {
                expiringPanel.loadData();
            }
        } else {
            cardLayout.show(cardsPanel, CARD_LIST);
            updateTabButtonStyle(btnTabList, true);
            updateTabButtonStyle(btnTabRegister, false);
            updateTabButtonStyle(btnTabExpiring, false);
            if (listPanel != null) {
                listPanel.loadDataToTable();
            }
        }
    }
}
