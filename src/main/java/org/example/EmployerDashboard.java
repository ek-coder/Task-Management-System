package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;

public class EmployerDashboard extends JFrame {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField titleField, descriptionField, dueDateField;
    private JComboBox<String> employeeComboBox, priorityComboBox;
    private JButton assignButton, updateButton, deleteButton;
    private int employerId;

    public EmployerDashboard(int employerId) {
        this.employerId = employerId;
        setTitle("Employer Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240)); // Light background color

        add(createTaskInputPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        loadEmployees();
        loadTasks();
    }

    private JPanel createTaskInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE); // White background for the input panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Title:"), gbc);
        titleField = new JTextField(15);
        gbc.gridx = 1; panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Description:"), gbc);
        descriptionField = new JTextField(15);
        gbc.gridx = 1; panel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        dueDateField = new JTextField(15);
        gbc.gridx = 1; panel.add(dueDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Priority:"), gbc);
        priorityComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        gbc.gridx = 1; panel.add(priorityComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Assign to Employee:"), gbc);
        employeeComboBox = new JComboBox<>();
        gbc.gridx = 1; panel.add(employeeComboBox, gbc);

        assignButton = new JButton("Assign Task");
        assignButton.addActionListener(this::assignTask);
        gbc.gridx = 0; gbc.gridy = 5; panel.add(assignButton, gbc);

        updateButton = new JButton("Update Task");
        updateButton.addActionListener(this::updateTask);
        gbc.gridx = 1; panel.add(updateButton, gbc);

        deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(this::deleteTask);
        gbc.gridx = 2; panel.add(deleteButton, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Tasks"));
        panel.setBackground(Color.WHITE); // White background for the table panel

        String[] columnNames = {"ID", "Title", "Description", "Due Date", "Priority", "Status", "Assigned To"};
        tableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setRowHeight(25);
        taskTable.setFillsViewportHeight(true);

        // Load task details into the input fields when a row is selected
        taskTable.getSelectionModel().addListSelectionListener(event -> loadSelectedTaskDetails());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadEmployees() {
        String query = "SELECT * FROM users WHERE role = 'employee'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                employeeComboBox.addItem(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        String query = "SELECT t.*, u.username FROM tasks t JOIN users u ON t.assigned_to = u.id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("description"));
                row.add(rs.getDate("due_date"));
                row.add(rs.getInt("priority"));
                row.add(rs.getString("status"));
                row.add(rs.getString("username"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void assignTask(ActionEvent e) {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String dueDate = dueDateField.getText();
        int priority = Integer.parseInt((String) priorityComboBox.getSelectedItem());
        String assignedTo = (String) employeeComboBox.getSelectedItem();

        String query = "INSERT INTO tasks (title, description, due_date, priority, assigned_to) " +
                "VALUES (?, ?, ?, ?, (SELECT id FROM users WHERE username = ?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, dueDate);
            pstmt.setInt(4, priority);
            pstmt.setString(5, assignedTo);
            pstmt.executeUpdate();
            loadTasks();
            clearFields(); // Clear fields after assignment
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void loadSelectedTaskDetails() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            titleField.setText((String) tableModel.getValueAt(selectedRow, 1));
            descriptionField.setText((String) tableModel.getValueAt(selectedRow, 2));
            dueDateField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            priorityComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
            employeeComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 6));
        }
    }

    private void updateTask(ActionEvent e) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0); // Task ID
        String title = titleField.getText();
        String description = descriptionField.getText();
        String dueDate = dueDateField.getText();
        int priority = Integer.parseInt((String) priorityComboBox.getSelectedItem());
        String assignedTo = (String) employeeComboBox.getSelectedItem();

        String query = "UPDATE tasks SET title = ?, description = ?, due_date = ?, priority = ?, assigned_to = (SELECT id FROM users WHERE username = ?) WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, dueDate);
            pstmt.setInt(4, priority);
            pstmt.setString(5, assignedTo);
            pstmt.setInt(6, taskId);
            pstmt.executeUpdate();
            loadTasks();
            clearFields(); // Clear fields after updating
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void deleteTask(ActionEvent e) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0); // Task ID

        String query = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
            loadTasks();
            clearFields(); // Clear fields after deletion
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void clearFields() {
        titleField.setText("");
        descriptionField.setText("");
        dueDateField.setText("");
        priorityComboBox.setSelectedIndex(0); // Reset priority combo box
        employeeComboBox.setSelectedIndex(0); // Reset employee combo box
        taskTable.clearSelection(); // Clear the selection in the table
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployerDashboard(1).setVisible(true));
    }
}