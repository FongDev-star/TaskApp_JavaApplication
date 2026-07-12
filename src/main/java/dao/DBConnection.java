package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import util.PasswordUtil;

public class DBConnection {
    private static Connection conn;
    private static final String URL = "jdbc:sqlite:tasks.db";

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL);
                initSchema(conn);
                seedDefaultAdmin(conn);
                System.out.println("Database Connected Successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void initSchema(Connection connection) throws SQLException {
        String users = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "salt TEXT NOT NULL," +
                "role TEXT NOT NULL DEFAULT 'USER'" +
                ")";

        String task = "CREATE TABLE IF NOT EXISTS task (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "task_id TEXT," +
                "user_id INTEGER NOT NULL," +
                "title TEXT," +
                "category TEXT," +
                "priority TEXT," +
                "status TEXT," +
                "due_date TEXT," +
                "description TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(id)," +
                "UNIQUE(user_id, task_id)" +
                ")";

        try (Statement st = connection.createStatement()) {
            st.execute(users);
            st.execute(task);
        }
    }
    
    private static void seedDefaultAdmin(Connection connection) throws SQLException {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) AS c FROM users")) {
            if (rs.next() && rs.getInt("c") == 0) {
                String salt = PasswordUtil.generateSalt();
                String hash = PasswordUtil.hash(DEFAULT_ADMIN_PASSWORD, salt);
                String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, 'ADMIN')";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, DEFAULT_ADMIN_USERNAME);
                    ps.setString(2, hash);
                    ps.setString(3, salt);
                    ps.executeUpdate();
                }
                System.out.println("Seeded default admin account -> username: " + DEFAULT_ADMIN_USERNAME
                        + " / password: " + DEFAULT_ADMIN_PASSWORD + " (change this after first login)");
            }
        }
    }
}
