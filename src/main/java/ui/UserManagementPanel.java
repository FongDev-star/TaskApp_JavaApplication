package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.UserDAO;
import model.User;

public class UserManagementPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private final User currentUser;

    private JTable table;
    private DefaultTableModel model;
    private JLabel lblStatus;

    private static final String[] COLUMNS = {"ID", "Username", "Role"};

    public UserManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(14, 14, 14, 14));
        setBackground(UITheme.BACKGROUND);

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        lblStatus = new JLabel("Ready.");
        lblStatus.setForeground(new Color(0x5A, 0x63, 0x73));
        lblStatus.setFont(lblStatus.getFont().deriveFont(12f));
        lblStatus.setBorder(new EmptyBorder(6, 2, 0, 0));
        add(lblStatus, BorderLayout.SOUTH);

        loadUsers();
    }

    private JPanel buildToolbar() {
        JButton addBtn = new JButton("Add User");
        JButton resetBtn = new JButton("Reset Password");
        JButton roleBtn = new JButton("Toggle Role");
        JButton deleteBtn = new JButton("Delete User");
        JButton refreshBtn = new JButton("Refresh");

        UITheme.colorize(addBtn, UITheme.ADD);
        UITheme.colorize(resetBtn, UITheme.UPDATE);
        UITheme.colorize(roleBtn, UITheme.SEARCH);
        UITheme.colorize(deleteBtn, UITheme.DELETE);
        UITheme.colorize(refreshBtn, UITheme.REFRESH);

        addBtn.addActionListener(e -> onAddUser());
        resetBtn.addActionListener(e -> onResetPassword());
        roleBtn.addActionListener(e -> onToggleRole());
        deleteBtn.addActionListener(e -> onDeleteUser());
        refreshBtn.addActionListener(e -> loadUsers());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panel.setOpaque(false);
        panel.add(addBtn);
        panel.add(resetBtn);
        panel.add(roleBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        return panel;
    }

    private JScrollPane buildTable() {
        model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF2, 0xF5, 0xFA));
                }
                return c;
            }
        };
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xCF, 0xE0, 0xF7));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(table.getFont().deriveFont(13f));
        table.getColumnModel().getColumn(2).setCellRenderer(new BadgeCellRenderer(UITheme::roleColor));

        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 12f));
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private void onAddUser() {
        AddUserDialog dialog = new AddUserDialog(getOwnerFrame());
        dialog.setVisible(true);
        if (dialog.isCreated()) {
            loadUsers();
            setStatus("User created.");
        }
    }

    private void onResetPassword() {
        User selected = getSelectedUser();
        if (selected == null) {
            showError("Select a user first.");
            return;
        }
        ResetPasswordDialog dialog = new ResetPasswordDialog(getOwnerFrame(), selected.getId(), selected.getUsername());
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            setStatus("Password reset for " + selected.getUsername() + ".");
        }
    }

    private void onToggleRole() {
        User selected = getSelectedUser();
        if (selected == null) {
            showError("Select a user first.");
            return;
        }
        if (selected.getId() == currentUser.getId()) {
            showError("You can't change your own role.");
            return;
        }
        String newRole = selected.isAdmin() ? User.ROLE_USER : User.ROLE_ADMIN;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Change " + selected.getUsername() + "'s role to " + newRole + "?",
                "Confirm Role Change", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            userDAO.updateRole(selected.getId(), newRole);
            loadUsers();
            setStatus("Role updated for " + selected.getUsername() + ".");
        } catch (SQLException ex) {
            showError("Error updating role: " + ex.getMessage());
        }
    }

    private void onDeleteUser() {
        User selected = getSelectedUser();
        if (selected == null) {
            showError("Select a user first.");
            return;
        }
        if (selected.getId() == currentUser.getId()) {
            showError("You can't delete your own account while logged in.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user \"" + selected.getUsername() + "\"? This also deletes all of their tasks.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            userDAO.delete(selected.getId());
            loadUsers();
            setStatus("User deleted.");
        } catch (SQLException ex) {
            showError("Error deleting user: " + ex.getMessage());
        }
    }

    private User getSelectedUser() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        int id = (int) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        String role = (String) model.getValueAt(row, 2);
        return new User(id, username, role);
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAll();
            model.setRowCount(0);
            for (User u : users) {
                model.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole()});
            }
            setStatus(users.size() + " user(s) loaded.");
        } catch (SQLException ex) {
            showError("Error loading users: " + ex.getMessage());
        }
    }

    private Frame getOwnerFrame() {
        Window w = SwingUtilities.getWindowAncestor(this);
        return (w instanceof Frame) ? (Frame) w : null;
    }

    private void setStatus(String message) {
        lblStatus.setText(message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}





