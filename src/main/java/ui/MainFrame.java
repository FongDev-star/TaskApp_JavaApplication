package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.User;

public class MainFrame extends JFrame {

    public MainFrame(User user, Runnable onLogout) {
        setTitle("Task Management System - " + user.getUsername());
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 620));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BACKGROUND);

        add(buildHeader(user, onLogout), BorderLayout.NORTH);
        add(user.isAdmin() ? new AdminDashboard(user) : new UserDashboard(user), BorderLayout.CENTER);
    }

    private JPanel buildHeader(User user, Runnable onLogout) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        String label = user.isAdmin() ? "Admin Dashboard" : "Task Management System";
        JLabel title = new JLabel(label);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        JLabel welcome = new JLabel("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
        welcome.setForeground(new Color(0xD8, 0xE4, 0xF5));
        welcome.setFont(welcome.getFont().deriveFont(Font.PLAIN, 13f));

        JButton logoutBtn = new JButton("Logout");
        UITheme.colorize(logoutBtn, UITheme.DELETE);
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                onLogout.run();
            }
        });

        right.add(welcome);
        right.add(logoutBtn);
        header.add(right, BorderLayout.EAST);

        return header;
    }
}
