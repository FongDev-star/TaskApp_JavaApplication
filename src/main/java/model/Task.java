package model;

public class Task {
    private int id;
    private String taskId;
    private int userId;              // owner of this task
    private String ownerUsername;    // transient, popux lated only for admin "all tasks" view
    private String title;
    private String category;
    private String priority;
    private String status;
    private String dueDate;
    private String description;

    public Task() {
    }

    public Task(String taskId, String title, String category, String priority,
                String status, String dueDate, String description) {
        this.taskId = taskId;
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
        this.description = description;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
