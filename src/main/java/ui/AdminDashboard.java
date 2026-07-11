package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JPanel {
    public AdminDashboard(User adminUser) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 13f));
        tabs.addTab("Manage Users", new UserManagementPanel(adminUser));
        tabs.addTab("All Tasks", new TaskPanel(adminUser.getId(), true));

        add(tabs, BorderLayout.CENTER);
    }
}
