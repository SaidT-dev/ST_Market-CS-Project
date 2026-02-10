package com.GUI;

import javax.swing.*;
import java.awt.*;

public class StyleUtils {

    public static final Color COLOR_BACKGROUND = new Color(243, 244, 246);
    public static final Color COLOR_SIDEBAR_BG = new Color(30, 35, 48);
    public static final Color COLOR_ACCENT = new Color(59, 130, 246);
    public static final Color COLOR_ACCENT_HOVER = new Color(37, 99, 235);
    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);
    public static final Color COLOR_DANGER = new Color(239, 68, 68);
    public static final Color COLOR_WHITE = Color.WHITE;


    public static final Color COLOR_TEXT_DARK = new Color(17, 24, 39);
    public static final Color COLOR_TEXT_NORMAL = new Color(55, 65, 81);
    public static final Color COLOR_TEXT_GRAY = new Color(107, 114, 128);


    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);


    public static void applyPrimaryButtonStyle(JButton button) {
        button.setBackground(COLOR_ACCENT);
        button.setForeground(COLOR_WHITE);
        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
    }


    public static void applySecondaryButtonStyle(JButton button) {
        button.setBackground(new Color(229, 231, 235));
        button.setForeground(COLOR_TEXT_NORMAL);
        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
    }

    public static void applyDangerButtonStyle(JButton button) {
        button.setBackground(COLOR_DANGER);
        button.setForeground(COLOR_WHITE);
        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty("JButton.buttonType", "roundRect");
    }


    public static void applyPanelHeaderStyle(JLabel label) {
        label.setFont(FONT_TITLE);
        label.setForeground(COLOR_TEXT_DARK);
    }


    public static void applyTableHeaderStyle(JTable table) {
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setForeground(COLOR_TEXT_NORMAL);
        table.getTableHeader().setBackground(COLOR_BACKGROUND);

        Dimension d = table.getTableHeader().getPreferredSize();
        d.height = 40;
        table.getTableHeader().setPreferredSize(d);
    }
}
