package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dao.UserDAO;
import model.User;
import util.Validator;

public class LoginDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblMessage;
    private User authenticatedUser; // null until a successful login

    private final UserDAO userDAO = new UserDAO();

    public LoginDialog() {
        super((Frame) null, "Task Management System - Login", true);
        setSize(420, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(22, 20, 18, 20));

        JLabel title = new JLabel("Task Management System");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setForeground(new Color(0xD8, 0xE4, 0xF5));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 12f));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    private JPanel buildForm() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(UITheme.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(24, 32, 24, 32));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        styleField(txtUsername);
        styleField(txtPassword);

        lblMessage = new JLabel(" ");
        lblMessage.setForeground(UITheme.DELETE);
        lblMessage.setFont(lblMessage.getFont().deriveFont(12f));
        lblMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = new JButton("Login");
        UITheme.colorize(loginBtn, UITheme.PRIMARY);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginBtn.addActionListener(e -> onLogin());

        JButton registerBtn = new JButton("Create a new account");
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setForeground(UITheme.UPDATE);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> onRegister());

        KeyAdapter enterToLogin = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) onLogin();
            }
        };
        txtUsername.addKeyListener(enterToLogin);
        txtPassword.addKeyListener(enterToLogin);

        wrapper.add(labeled("Username"));
        wrapper.add(txtUsername);
        wrapper.add(Box.createVerticalStrut(14));
        wrapper.add(labeled("Password"));
        wrapper.add(txtPassword);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(lblMessage);
        wrapper.add(Box.createVerticalStrut(8));
        wrapper.add(loginBtn);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(registerBtn);

        return wrapper;
    }

    private JLabel labeled(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 12f));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
        return l;
    }

    private void styleField(JTextField field) {
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
    }

    private void onLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (Validator.isEmpty(username) || Validator.isEmpty(password)) {
            lblMessage.setText("Please enter both username and password.");
            return;
        }
        try {
            User user = userDAO.authenticate(username, password);
            if (user == null) {
                lblMessage.setText("Invalid username or password.");
                txtPassword.setText("");
                return;
            }
            authenticatedUser = user;
            dispose();
        } catch (SQLException ex) {
            lblMessage.setText("Login error: " + ex.getMessage());
        }
    }

    private void onRegister() {
        RegisterDialog register = new RegisterDialog(this);
        register.setVisible(true);
        if (register.isRegistrationSuccessful()) {
            txtUsername.setText(register.getRegisteredUsername());
            txtPassword.setText("");
            lblMessage.setForeground(UITheme.ADD);
            lblMessage.setText("Account created! You can log in now.");
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
