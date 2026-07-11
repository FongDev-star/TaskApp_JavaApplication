package ui;

import dao.UserDAO;
import model.User;
import util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class AddUserDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JLabel lblMessage;
    private boolean created = false;

    private final UserDAO userDAO = new UserDAO();

    public AddUserDialog(Frame owner) {
        super(owner, "Add User", true);
        setSize(380, 340);
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
        wrapper.setBorder(new EmptyBorder(22, 26, 22, 26));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cbRole = new JComboBox<>(new String[]{User.ROLE_USER, User.ROLE_ADMIN});
        for (JComponent f : new JComponent[]{txtUsername, txtPassword, cbRole}) {
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        }
        txtUsername.setBorder(fieldBorder());
        txtPassword.setBorder(fieldBorder());

        lblMessage = new JLabel(" ");
        lblMessage.setForeground(UITheme.DELETE);
        lblMessage.setFont(lblMessage.getFont().deriveFont(12f));
        lblMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton createBtn = new JButton("Create User");
        UITheme.colorize(createBtn, UITheme.ADD);
        createBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        createBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        createBtn.addActionListener(e -> onCreate());

        JButton cancelBtn = new JButton("Cancel");
        UITheme.colorize(cancelBtn, UITheme.CLEAR);
        cancelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cancelBtn.addActionListener(e -> dispose());

        wrapper.add(label("Username"));
        wrapper.add(txtUsername);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(label("Password"));
        wrapper.add(txtPassword);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(label("Role"));
        wrapper.add(cbRole);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(lblMessage);
        wrapper.add(Box.createVerticalStrut(8));
        wrapper.add(createBtn);
        wrapper.add(Box.createVerticalStrut(8));
        wrapper.add(cancelBtn);
        return wrapper;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 12f));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
        return l;
    }

    private javax.swing.border.Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8));
    }

    private void onCreate() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role = (String) cbRole.getSelectedItem();

        if (Validator.isEmpty(username) || username.length() < 3) {
            lblMessage.setText("Username must be at least 3 characters.");
            return;
        }
        if (password.length() < 6) {
            lblMessage.setText("Password must be at least 6 characters.");
            return;
        }
        try {
            boolean ok = userDAO.createUser(username, password, role);
            if (!ok) {
                lblMessage.setText("That username is already taken.");
                return;
            }
            created = true;
            dispose();
        } catch (SQLException ex) {
            lblMessage.setText("Error: " + ex.getMessage());
        }
    }

    public boolean isCreated() {
        return created;
    }
}
