package dao;

import model.User;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    Connection conn = DBConnection.getConnection();

    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Registers a new account with the USER role. Returns false if the username is taken. */
    public boolean register(String username, String plainPassword) throws SQLException {
        if (existsByUsername(username)) {
            return false;
        }
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(plainPassword, salt);
        String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.setString(4, User.ROLE_USER);
            ps.executeUpdate();
        }
        return true;
    }

    /** Creates a user with an explicit role. Used by the admin dashboard. Returns false if taken. */
    public boolean createUser(String username, String plainPassword, String role) throws SQLException {
        if (existsByUsername(username)) {
            return false;
        }
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(plainPassword, salt);
        String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.setString(4, role);
            ps.executeUpdate();
        }
        return true;
    }

    /** Returns the authenticated User, or null if the username/password doesn't match. */
    public User authenticate(String username, String plainPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = extractUser(rs);
                return PasswordUtil.verify(plainPassword, u.getSalt(), u.getPasswordHash()) ? u : null;
            }
        }
    }

    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users ORDER BY id")) {
            while (rs.next()) {
                list.add(extractUser(rs));
            }
        }
        return list;
    }

    public void updateRole(int userId, String newRole) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET role=? WHERE id=?")) {
            ps.setString(1, newRole);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void resetPassword(int userId, String newPlainPassword) throws SQLException {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hash(newPlainPassword, salt);
        try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET password_hash=?, salt=? WHERE id=?")) {
            ps.setString(1, hash);
            ps.setString(2, salt);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    /** Deletes the user and cascades to their tasks (SQLite doesn't enforce FKs by default). */
    public void delete(int userId) throws SQLException {
        try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM task WHERE user_id=?")) {
            ps1.setInt(1, userId);
            ps1.executeUpdate();
        }
        try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps2.setInt(1, userId);
            ps2.executeUpdate();
        }
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setSalt(rs.getString("salt"));
        u.setRole(rs.getString("role"));
        return u;
    }
}
