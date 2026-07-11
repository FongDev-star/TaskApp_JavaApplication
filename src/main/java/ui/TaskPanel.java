package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.TaskDAO;
import dao.UserDAO;
import model.Task;
import model.User;
import util.Validator;

public class TaskPanel extends JPanel {

    private final TaskDAO dao = new TaskDAO();
    private final UserDAO userDAO = new UserDAO();
    private final int currentUserId;
    private final boolean isAdmin;

    private JTextField txtId, txtTitle, txtDueDate, txtSearch;
    private JComboBox<String> cbCategory, cbPriority, cbStatus, cbOwner;
    private JTextArea txtDescription;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblStatus;

    private String[] columns;
    private int colId, colTaskId, colOwner, colTitle, colCategory, colPriority, colStatus, colDueDate;
    private Map<String, Integer> usernameToId = new LinkedHashMap<>();
    private Integer selectedRecordId; 
    public TaskPanel(int currentUserId, boolean isAdmin) {
        this.currentUserId = currentUserId;
        this.isAdmin = isAdmin;

        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(14, 14, 14, 14));
        setBackground(UITheme.BACKGROUND);

        buildColumnLayout();

        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);
        topArea.add(buildFormPanel());
        topArea.add(Box.createVerticalStrut(10));
        topArea.add(buildButtonPanel());
        add(topArea, BorderLayout.NORTH);

        add(buildTablePanel(), BorderLayout.CENTER);

        lblStatus = new JLabel("Ready.");
        lblStatus.setForeground(new Color(0x5A, 0x63, 0x73));
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.PLAIN, 12f));
        lblStatus.setBorder(new EmptyBorder(6, 2, 0, 0));
        add(lblStatus, BorderLayout.SOUTH);

        if (isAdmin) refreshOwnerOptions();
        loadTable();
    }

    private void buildColumnLayout() {
        if (isAdmin) {
            columns = new String[]{"ID", "Task ID", "Owner", "Title", "Category", "Priority", "Status", "Due Date"};
            colId = 0; colTaskId = 1; colOwner = 2; colTitle = 3; colCategory = 4; colPriority = 5; colStatus = 6; colDueDate = 7;
        } else {
            columns = new String[]{"ID", "Task ID", "Title", "Category", "Priority", "Status", "Due Date"};
            colId = 0; colTaskId = 1; colOwner = -1; colTitle = 2; colCategory = 3; colPriority = 4; colStatus = 5; colDueDate = 6;
        }
    }

    private JPanel buildFormPanel() {
        int rows = isAdmin ? 5 : 4;
        JPanel form = new JPanel(new GridLayout(rows, 4, 10, 8));
        form.setBackground(UITheme.CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel formTitle = new JLabel("Task Details");
        formTitle.setFont(formTitle.getFont().deriveFont(Font.BOLD, 14f));
        formTitle.setForeground(UITheme.PRIMARY_DARK);

        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        wrapper.add(formTitle, BorderLayout.NORTH);
        wrapper.add(form, BorderLayout.CENTER);

        txtId = new JTextField();
        txtTitle = new JTextField();
        txtDueDate = new JTextField("YYYY-MM-DD");
        cbCategory = new JComboBox<>(new String[]{"Work", "Personal", "Study", "Urgent"});
        cbPriority = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        cbStatus = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
        txtDescription = new JTextArea(2, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        form.add(new JLabel("Task ID (e.g., T-101):"));
        form.add(txtId);
        form.add(new JLabel("Task Title:"));
        form.add(txtTitle);

        form.add(new JLabel("Category:"));
        form.add(cbCategory);
        form.add(new JLabel("Priority:"));
        form.add(cbPriority);

        form.add(new JLabel("Status:"));
        form.add(cbStatus);
        form.add(new JLabel("Due Date:"));
        form.add(txtDueDate);

        form.add(new JLabel("Description:"));
        form.add(new JScrollPane(txtDescription));

        if (isAdmin) {
            cbOwner = new JComboBox<>();
            form.add(new JLabel("Owner:"));
            form.add(cbOwner);
        } else {
            form.add(new JLabel(""));
            form.add(new JLabel(""));
        }

        return wrapper;
    }

    private JPanel buildButtonPanel() {
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton delBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        JButton refreshBtn = new JButton("Refresh");
        JButton searchBtn = new JButton("Search");

        UITheme.colorize(addBtn, UITheme.ADD);
        UITheme.colorize(updateBtn, UITheme.UPDATE);
        UITheme.colorize(delBtn, UITheme.DELETE);
        UITheme.colorize(clearBtn, UITheme.CLEAR);
        UITheme.colorize(refreshBtn, UITheme.REFRESH);
        UITheme.colorize(searchBtn, UITheme.SEARCH);

        txtSearch = new JTextField(14);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnPanel.setOpaque(false);
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(delBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(new JSeparator(SwingConstants.VERTICAL));
        btnPanel.add(new JLabel("Search:"));
        btnPanel.add(txtSearch);
        btnPanel.add(searchBtn);

        addBtn.addActionListener(e -> onAdd());
        updateBtn.addActionListener(e -> onUpdate());
        delBtn.addActionListener(e -> onDelete());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> { if (isAdmin) refreshOwnerOptions(); loadTable(); });
        searchBtn.addActionListener(e -> onSearch());
        txtSearch.addActionListener(e -> onSearch());

        return btnPanel;
    }

    private JScrollPane buildTablePanel() {
        model = new DefaultTableModel(columns, 0) {
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
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xCF, 0xE0, 0xF7));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(table.getFont().deriveFont(13f));

        table.getColumnModel().getColumn(colPriority).setCellRenderer(new BadgeCellRenderer(UITheme::priorityColor));
        table.getColumnModel().getColumn(colStatus).setCellRenderer(new BadgeCellRenderer(UITheme::statusColor));

        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 12f));
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setReorderingAllowed(false);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelectedRow();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private void refreshOwnerOptions() {
        try {
            usernameToId.clear();
            cbOwner.removeAllItems();
            for (User u : userDAO.getAll()) {
                usernameToId.put(u.getUsername(), u.getId());
                cbOwner.addItem(u.getUsername());
            }
        } catch (SQLException ex) {
            showError("Error loading users: " + ex.getMessage());
        }
    }

    private void fillFormFromSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return;
        int row = table.convertRowIndexToModel(viewRow);

        selectedRecordId = (Integer) model.getValueAt(row, colId);
        txtId.setText(model.getValueAt(row, colTaskId).toString());
        txtTitle.setText(model.getValueAt(row, colTitle).toString());
        cbCategory.setSelectedItem(model.getValueAt(row, colCategory).toString());
        cbPriority.setSelectedItem(model.getValueAt(row, colPriority).toString());
        cbStatus.setSelectedItem(model.getValueAt(row, colStatus).toString());
        txtDueDate.setText(model.getValueAt(row, colDueDate).toString());
        if (isAdmin && colOwner != -1) {
            cbOwner.setSelectedItem(model.getValueAt(row, colOwner).toString());
        }
        txtId.setEditable(false); // lock ID while an existing row is selected
    }

    // Resolves which user_id a task in the form should belong to: the
    // selected Owner (admin mode) or the logged-in user (user mode).
    private int resolveTargetUserId() {
        if (isAdmin && cbOwner.getSelectedItem() != null) {
            return usernameToId.getOrDefault(cbOwner.getSelectedItem().toString(), currentUserId);
        }
        return currentUserId;
    }

    private void onAdd() {
        if (Validator.isEmpty(txtId.getText()) || Validator.isEmpty(txtTitle.getText())) {
            showError("Task ID and Title are required!");
            return;
        }
        if (!Validator.isValidDate(txtDueDate.getText())) {
            showError("Due date must be in YYYY-MM-DD format.");
            return;
        }
        try {
            String id = txtId.getText().trim();
            int targetUserId = resolveTargetUserId();
            if (dao.exists(id, targetUserId)) {
                showError("This owner already has a task with ID \"" + id + "\". Use Update instead, or pick a new ID.");
                return;
            }
            dao.insert(buildTaskFromForm());
            loadTable();
            clearForm();
            setStatus("Task added successfully.");
        } catch (SQLException ex) {
            showError("Error adding task: " + ex.getMessage());
        }
    }

    private void onUpdate() {
        if (selectedRecordId == null) {
            showError("Select a task from the table to update!");
            return;
        }
        if (!Validator.isValidDate(txtDueDate.getText())) {
            showError("Due date must be in YYYY-MM-DD format.");
            return;
        }
        try {
            Task t = buildTaskFromForm();
            t.setId(selectedRecordId);
            dao.update(t);
            loadTable();
            clearForm();
            setStatus("Task updated successfully.");
        } catch (SQLException ex) {
            showError("Error updating task: " + ex.getMessage());
        }
    }

    private void onDelete() {
        if (selectedRecordId == null) {
            showError("Select a task first!");
            return;
        }
        String id = txtId.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete task \"" + id + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dao.delete(selectedRecordId);
            loadTable();
            clearForm();
            setStatus("Task deleted.");
        } catch (SQLException ex) {
            showError("Error deleting task: " + ex.getMessage());
        }
    }

    private void onSearch() {
        try {
            Integer scope = isAdmin ? null : currentUserId;
            List<Task> list = dao.search(txtSearch.getText().trim(), scope);
            populateTable(list);
            setStatus(list.size() + " task(s) found.");
        } catch (SQLException ex) {
            showError("Error searching tasks: " + ex.getMessage());
        }
    }

    private Task buildTaskFromForm() {
        Task t = new Task();
        t.setTaskId(txtId.getText().trim());
        t.setTitle(txtTitle.getText().trim());
        t.setCategory((String) cbCategory.getSelectedItem());
        t.setPriority((String) cbPriority.getSelectedItem());
        t.setStatus((String) cbStatus.getSelectedItem());
        t.setDueDate(txtDueDate.getText().trim());
        t.setDescription(txtDescription.getText().trim());
        t.setUserId(resolveTargetUserId());
        return t;
    }

    private void clearForm() {
        selectedRecordId = null;
        txtId.setText("");
        txtId.setEditable(true);
        txtTitle.setText("");
        cbCategory.setSelectedIndex(0);
        cbPriority.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        txtDueDate.setText("YYYY-MM-DD");
        txtDescription.setText("");
        txtSearch.setText("");
        if (isAdmin && cbOwner.getItemCount() > 0) cbOwner.setSelectedIndex(0);
        table.clearSelection();
    }

    private void loadTable() {
        try {
            Integer scope = isAdmin ? null : currentUserId;
            populateTable(dao.getAll(scope));
            setStatus(model.getRowCount() + " task(s) loaded.");
        } catch (SQLException e) {
            showError("Error loading tasks: " + e.getMessage());
        }
    }

    private void populateTable(List<Task> list) {
        model.setRowCount(0);
        for (Task t : list) {
            List<Object> row = new ArrayList<>();
            row.add(t.getId());
            row.add(t.getTaskId());
            if (isAdmin) row.add(t.getOwnerUsername());
            row.add(t.getTitle());
            row.add(t.getCategory());
            row.add(t.getPriority());
            row.add(t.getStatus());
            row.add(t.getDueDate());
            model.addRow(row.toArray());
        }
    }

    private void setStatus(String message) {
        lblStatus.setText(message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}