package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class EmployeeTaskView extends JFrame {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextArea queryField;
    private int employeeId;

    public EmployeeTaskView(int employeeId) {
        this.employeeId = employeeId;
        setTitle("Employee Task View");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createTablePanel(), BorderLayout.CENTER);
        add(createQueryPanel(), BorderLayout.SOUTH);
        loadTasks();
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Tasks"));

        String[] columnNames = {"ID", "Title", "Description", "Due Date", "Priority", "Status", "Comments"};
        tableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQueryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Submit Query/Comment"));

        queryField = new JTextArea(4, 50);
        queryField.setLineWrap(true);
        queryField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(queryField);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button to submit comment
        JButton submitButton = new JButton("Submit Comment");
        submitButton.addActionListener(e -> submitComment());
        panel.add(submitButton, BorderLayout.SOUTH);

        // Button to mark as completed
        JButton markCompletedButton = new JButton("Mark as Completed");
        markCompletedButton.addActionListener(e -> markAsCompleted());
        panel.add(markCompletedButton, BorderLayout.EAST);

        return panel;
    }

    private void loadTasks() {
        String query = "SELECT * FROM tasks WHERE assigned_to = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("description"));
                row.add(rs.getDate("due_date"));
                row.add(rs.getInt("priority"));
                row.add(rs.getString("status"));
                row.add(rs.getString("comments"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void submitComment() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to submit a comment.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0); // Get task ID
        String comment = queryField.getText();

        String updateQuery = "UPDATE tasks SET comments = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, comment);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Comment submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTasks(); // Reload the tasks to show the updated comments
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void markAsCompleted() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to mark as completed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0); // Get task ID
        String updateQuery = "UPDATE tasks SET status = 'completed' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Task marked as completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTasks(); // Reload tasks to show the updated status
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeTaskView view = new EmployeeTaskView(1); // Pass employee ID as needed
            view.setVisible(true);
        });
    }
}