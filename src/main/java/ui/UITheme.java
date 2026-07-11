package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public final class UITheme {

    private UITheme() {}

    // Brand / header
    public static final Color PRIMARY = new Color(0x1E, 0x56, 0xA8);      // deep blue
    public static final Color PRIMARY_DARK = new Color(0x14, 0x3D, 0x7A);
    public static final Color BACKGROUND = new Color(0xF4, 0xF6, 0xFA);   // app background
    public static final Color CARD = Color.WHITE;
    public static final Color BORDER = new Color(0xDC, 0xE1, 0xEA);

    // Action button colors
    public static final Color ADD = new Color(0x2E, 0xA0, 0x4B);      // green
    public static final Color UPDATE = new Color(0x22, 0x77, 0xD0);   // blue
    public static final Color DELETE = new Color(0xD9, 0x3A, 0x3A);   // red
    public static final Color CLEAR = new Color(0x8A, 0x93, 0xA3);    // gray
    public static final Color REFRESH = new Color(0x17, 0x9B, 0x9B);  // teal
    public static final Color SEARCH = new Color(0x7A, 0x4F, 0xC4);   // purple

    // Priority badge colors
    public static final Color PRIORITY_HIGH = new Color(0xE0, 0x3E, 0x3E);
    public static final Color PRIORITY_MEDIUM = new Color(0xE8, 0x9A, 0x1C);
    public static final Color PRIORITY_LOW = new Color(0x2E, 0xA0, 0x4B);

    // Status badge colors
    public static final Color STATUS_PENDING = new Color(0x8A, 0x93, 0xA3);
    public static final Color STATUS_IN_PROGRESS = new Color(0x22, 0x77, 0xD0);
    public static final Color STATUS_COMPLETED = new Color(0x2E, 0xA0, 0x4B);

    // Role badge colors (for the admin user-management table)
    public static final Color ROLE_ADMIN = new Color(0x7A, 0x4F, 0xC4);
    public static final Color ROLE_USER = new Color(0x22, 0x77, 0xD0);

    public static Color roleColor(String role) {
        return "ADMIN".equalsIgnoreCase(role) ? ROLE_ADMIN : ROLE_USER;
    }

    public static Color priorityColor(String priority) {
        if (priority == null) return PRIORITY_LOW;
        switch (priority) {
            case "High": return PRIORITY_HIGH;
            case "Medium": return PRIORITY_MEDIUM;
            default: return PRIORITY_LOW;
        }
    }

    public static Color statusColor(String status) {
        if (status == null) return STATUS_PENDING;
        switch (status) {
            case "In Progress": return STATUS_IN_PROGRESS;
            case "Completed": return STATUS_COMPLETED;
            default: return STATUS_PENDING;
        }
    }

    /** Styles a button with a solid color fill and white text. */
    public static void colorize(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
}
