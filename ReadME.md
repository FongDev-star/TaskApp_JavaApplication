# Task Management System

A Java Swing desktop application for personal task tracking, with account registration, login, and role-based access — regular users manage their own tasks, while an admin manages user accounts and can view/reassign tasks across everyone.

Built with Java Swing (UI), SQLite via JDBC (storage), and Maven (build).

## Features

- **Authentication** — self-service registration and login; passwords are salted and hashed with SHA-256, never stored in plain text
- **Role-based dashboards** — regular `USER` accounts see only their own tasks; `ADMIN` accounts get a separate dashboard
- **Task management** — create, update, delete, and search tasks (Task ID, title, category, priority, status, due date, description)
- **Admin user management** — add users, reset passwords, toggle roles between USER/ADMIN, delete accounts
- **Admin task oversight** — view every user's tasks in one table with an Owner column, and reassign a task to any user
- **Color-coded UI** — priority, status, and role are shown as colored badges; action buttons follow a consistent color convention (green = add, blue = update, red = delete, etc.)

## Prerequisites

- JDK 17 or later
- Apache Maven
- Internet access on first build (to download the `sqlite-jdbc` dependency and Maven plugins)

## Getting Started

```bash
cd TaskManagementSystem
mvn compile
mvn exec:java
```

On first launch, the app creates a SQLite database at `~/.TaskManagementSystem/tasks.db` and seeds a default administrator account:

| Username | Password  |
|----------|-----------|
| `admin`  | `admin123` |

**Change this password** after your first login, using the Reset Password action in the admin dashboard (Manage Users tab).

Anyone else can create their own account from the login screen via **Create a new account** — self-registered accounts are always created with the `USER` role.

## Project Structure

```
TaskManagementSystem/
├── src/
│   ├── model/       Task.java, User.java
│   ├── dao/         DBConnection.java, TaskDAO.java, UserDAO.java
│   ├── util/        Validator.java, PasswordUtil.java
│   └── ui/          Main.java, MainFrame.java, LoginDialog.java,
│                     RegisterDialog.java, UserDashboard.java,
│                     AdminDashboard.java, UserManagementPanel.java,
│                     TaskPanel.java, AddUserDialog.java,
│                     ResetPasswordDialog.java, UITheme.java,
│                     BadgeCellRenderer.java
├── database/schema.sql
└── pom.xml
```

The code follows an MVC + DAO structure: `model` classes are plain data holders, `dao` classes are the only classes that touch SQL, and `ui` classes handle presentation and call into the DAO layer.

## Database

Two tables, auto-created on first run (see `database/schema.sql`):

- **`users`** — `id`, `username` (unique), `password_hash`, `salt`, `role` (`USER` or `ADMIN`)
- **`task`** — `id`, `task_id` (unique), `user_id` (FK → `users.id`), `title`, `category`, `priority`, `status`, `due_date`, `description`

Each task belongs to exactly one user. Deleting a user also deletes their tasks.

The database file lives at a fixed path (`~/.TaskManagementSystem/tasks.db`) rather than a path relative to the working directory, so it's always the same file regardless of whether you run the app from a terminal, an IDE, or a packaged jar.

## Usage

**As a regular user:** log in, then use the form at the top of the dashboard to add a task, click a row in the table to load it into the form for editing, or use Search to filter by Task ID, title, or category.

**As an admin:** the dashboard has two tabs —
- *Manage Users*: add accounts, reset passwords, toggle roles, delete accounts
- *All Tasks*: same task form/table as the user view, but scoped to everyone, with an Owner column and an Owner picker in the form for assigning/reassigning tasks

## Building a Runnable Jar

```bash
mvn package
java -jar target/TaskManagementSystem.jar
```

(`mvn package` uses the shade plugin configured in `pom.xml` to bundle the `sqlite-jdbc` dependency into the jar.)

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| UI | Java Swing (cross-platform Look & Feel) |
| Persistence | SQLite via `org.xerial:sqlite-jdbc` |
| Build | Apache Maven |
| Password hashing | Salted SHA-256 |

## Notes

- This is a coursework-scale project; SHA-256 password hashing is adequate here but isn't what you'd use in a production system (bcrypt/argon2 would be the real choice there).
- No networked multi-client access — this is a single-machine desktop app backed by a local SQLite file.