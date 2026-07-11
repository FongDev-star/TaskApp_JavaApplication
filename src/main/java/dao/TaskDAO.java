package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Task;

public class TaskDAO {

    Connection conn = DBConnection.getConnection();

    public void insert(Task t) throws SQLException {
        String sql = "INSERT INTO task (task_id, user_id, title, category, priority, status, due_date, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTaskId());
            ps.setInt(2, t.getUserId());
            ps.setString(3, t.getTitle());
            ps.setString(4, t.getCategory());
            ps.setString(5, t.getPriority());
            ps.setString(6, t.getStatus());
            ps.setString(7, t.getDueDate());
            ps.setString(8, t.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(Task t) throws SQLException {
        String sql = "UPDATE task SET task_id=?, user_id=?, title=?, category=?, priority=?, status=?, due_date=?, description=? " +
                "WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTaskId());
            ps.setInt(2, t.getUserId());
            ps.setString(3, t.getTitle());
            ps.setString(4, t.getCategory());
            ps.setString(5, t.getPriority());
            ps.setString(6, t.getStatus());
            ps.setString(7, t.getDueDate());
            ps.setString(8, t.getDescription());
            ps.setInt(9, t.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM task WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Task> getAll(Integer userId) throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = userId == null
                ? "SELECT t.*, u.username AS owner_username FROM task t JOIN users u ON t.user_id = u.id ORDER BY t.id"
                : "SELECT t.*, u.username AS owner_username FROM task t JOIN users u ON t.user_id = u.id WHERE t.user_id = ? ORDER BY t.id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractTask(rs));
                }
            }
        }
        return list;
    }

    public List<Task> search(String key, Integer userId) throws SQLException {
        List<Task> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, u.username AS owner_username FROM task t JOIN users u ON t.user_id = u.id " +
                        "WHERE (t.task_id LIKE ? OR t.title LIKE ? OR t.category LIKE ?)");
        if (userId != null) sql.append(" AND t.user_id = ?");
        sql.append(" ORDER BY t.id");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, "%" + key + "%");
            ps.setString(2, "%" + key + "%");
            ps.setString(3, "%" + key + "%");
            if (userId != null) ps.setInt(4, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractTask(rs));
                }
            }
        }
        return list;
    }

    public boolean exists(String taskId, int userId) throws SQLException {
        String sql = "SELECT 1 FROM task WHERE task_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, taskId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Task extractTask(ResultSet rs) throws SQLException {
        Task t = new Task();
        t.setId(rs.getInt("id"));
        t.setTaskId(rs.getString("task_id"));
        t.setUserId(rs.getInt("user_id"));
        t.setOwnerUsername(rs.getString("owner_username"));
        t.setTitle(rs.getString("title"));
        t.setCategory(rs.getString("category"));
        t.setPriority(rs.getString("priority"));
        t.setStatus(rs.getString("status"));
        t.setDueDate(rs.getString("due_date"));
        t.setDescription(rs.getString("description"));
        return t;
    }
}