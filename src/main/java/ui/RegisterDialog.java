package ui;

import dao.UserDAO;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class RegisterDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword, txtConfirm;
    private JLabel lblMessage;
    private boolean registrationSuccessful = false;

    private final UserDAO userDAO = new UserDAO();

    public RegisterDialog(Dialog owner) {
        super(owner, "Create Account", true);
        setSize(400, 380);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout());
        add(buildForm(), BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(UITheme.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Register a new account");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(UITheme.PRIMARY_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirm = new JPasswordField();
        for (JTextField f : new JTextField[]{txtUsername, txtPassword, txtConfirm}) {
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                    new EmptyBorder(6, 8, 6, 8)));
        }

        lblMessage = new JLabel(" ");
        lblMessage.setForeground(UITheme.DELETE);
        lblMessage.setFont(lblMessage.getFont().deriveFont(12f));
        lblMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton registerBtn = new JButton("Register");
        UITheme.colorize(registerBtn, UITheme.ADD);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        registerBtn.addActionListener(e -> onRegister());

        JButton cancelBtn = new JButton("Cancel");
        UITheme.colorize(cancelBtn, UITheme.CLEAR);
        cancelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cancelBtn.addActionListener(e -> dispose());

        wrapper.add(title);
        wrapper.add(fieldLabel("Username"));
        wrapper.add(txtUsername);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(fieldLabel("Password"));
        wrapper.add(txtPassword);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(fieldLabel("Confirm Password"));
        wrapper.add(txtConfirm);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(lblMessage);
        wrapper.add(Box.createVerticalStrut(8));
        wrapper.add(registerBtn);
        wrapper.add(Box.createVerticalStrut(8));
        wrapper.add(cancelBtn);

        return wrapper;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 12f));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
        return l;
    }

    private void onRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (Validator.isEmpty(username) || Validator.isEmpty(password)) {
            lblMessage.setText("Username and password are required.");
            return;
        }
        if (username.length() < 3) {
            lblMessage.setText("Username must be at least 3 characters.");
            return;
        }
        if (password.length() < 6) {
            lblMessage.setText("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            lblMessage.setText("Passwords do not match.");
            return;
        }
        try {
            boolean created = userDAO.register(username, password);
            if (!created) {
                lblMessage.setText("That username is already taken.");
                return;
            }
            registrationSuccessful = true;
            dispose();
        } catch (SQLException ex) {
            lblMessage.setText("Registration error: " + ex.getMessage());
        }
    }

    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }

    public String getRegisteredUsername() {
        return txtUsername.getText().trim();
    }
}
