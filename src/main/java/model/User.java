package model;

public class User {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    private int id;
    private String username;
    private String passwordHash;
    private String salt;
    private String role;

    public User() {
    }

    public User(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }
}
