package ui;

import dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class ResetPasswordDialog extends JDialog {

    private JPasswordField txtPassword, txtConfirm;
    private JLabel lblMessage;
    private boolean success = false;

    private final UserDAO userDAO = new UserDAO();
    private final int userId;

    public ResetPasswordDialog(Frame owner, int userId, String username) {
        super(owner, "Reset Password - " + username, true);
        this.userId = userId;
        setSize(360, 280);
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

        txtPassword = new JPasswordField();
        txtConfirm = new JPasswordField();
        for (JTextField f : new JTextField[]{txtPassword, txtConfirm}) {
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

        JButton saveBtn = new JButton("Reset Password");
        UITheme.colorize(saveBtn, UITheme.UPDATE);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        saveBtn.addActionListener(e -> onSave());

        JButton cancelBtn = new JButton("Cancel");
        UITheme.colorize(cancelBtn, UITheme.CLEAR);
        cancelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cancelBtn.addActionListener(e -> dispose());

        JLabel newPwLabel = new JLabel("New Password");
        newPwLabel.setFont(newPwLabel.getFont().deriveFont(Font.BOLD, 12f));
        newPwLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        newPwLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(confirmLabel.getFont().deriveFont(Font.BOLD, 12f));
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

        wrapper.add(newPwLabel);
        wrapper.add(txtPassword);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(confirmLabel);
        wrapper.add(txtConfirm);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(lblMessage);
        wrapper.add(Box.createVerticalStrut(8));
        wrapper.add(saveBtn);
        wrapper.add(Box.createVerticalStrut(8));
        wrapper.add(cancelBtn);
        return wrapper;
    }

    private void onSave() {
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (password.length() < 6) {
            lblMessage.setText("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            lblMessage.setText("Passwords do not match.");
            return;
        }
        try {
            userDAO.resetPassword(userId, password);
            success = true;
            dispose();
        } catch (SQLException ex) {
            lblMessage.setText("Error: " + ex.getMessage());
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
