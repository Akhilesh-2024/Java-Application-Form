import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentDatabaseUI extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton saveButton, deleteButton, backButton, viewDeletedButton;
    private JTextField[] editFields;
    private int selectedRow = -1;
    private String[] columnNames = {"Sname", "Smobile", "Semail", "Spassword", "Sgender", "Saddress", "Scity", "Spincode", "id"};

    public StudentDatabaseUI() {
        setTitle("Student Information");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table setup
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Control panel for editing
        JPanel controlPanel = new JPanel(new GridLayout(10, 2));
        editFields = new JTextField[columnNames.length - 1]; // Exclude id from editable fields
        for (int i = 0; i < columnNames.length - 1; i++) { // Do not include id
            controlPanel.add(new JLabel(columnNames[i] + ":"));
            editFields[i] = new JTextField(20);
            controlPanel.add(editFields[i]);
        }
        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete");
        backButton = new JButton("Back"); // Back button
        viewDeletedButton = new JButton("View Deleted"); // View Deleted button
        controlPanel.add(saveButton);
        controlPanel.add(deleteButton);
        controlPanel.add(backButton);
        controlPanel.add(viewDeletedButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Load data from database
        loadData();

        // Add table selection listener
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                selectRow();
            }
        });

        // Save button action
        saveButton.addActionListener(e -> saveData());

        // Delete button action
        deleteButton.addActionListener(e -> deleteData());

        // Back button action
        backButton.addActionListener(e -> goBackToLogin());

        // View Deleted button action
        viewDeletedButton.addActionListener(e -> viewDeletedData());
    }

    private void loadData() {
        String url = "jdbc:sqlite:student_db.db";
        String query = "SELECT * FROM studentinfo";

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Set column headers
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // Populate table with data
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectRow() {
        selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            for (int i = 0; i < columnNames.length - 1; i++) { // Skip id column
                editFields[i].setText(table.getValueAt(selectedRow, i).toString());
            }
        }
    }

    private void saveData() {
        if (selectedRow != -1) {
            String[] newValues = new String[columnNames.length - 1]; // Exclude id
            for (int i = 0; i < columnNames.length - 1; i++) {
                newValues[i] = editFields[i].getText();
                table.setValueAt(newValues[i], selectedRow, i); // Update table UI
            }
            String id = table.getValueAt(selectedRow, columnNames.length - 1).toString();
            updateDatabase(id, newValues);
        }
    }

    private void updateDatabase(String id, String[] newValues) {
        String url = "jdbc:sqlite:student_db.db";
        String query = "UPDATE studentinfo SET Sname=?, Smobile=?, Semail=?, Spassword=?, Sgender=?, Saddress=?, Scity=?, Spincode=? WHERE id=?";

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(query);

            for (int i = 0; i < newValues.length; i++) {
                pstmt.setString(i + 1, newValues[i]);
            }
            pstmt.setString(newValues.length + 1, id);

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();
            conn.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Record Updated Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteData() {
        if (selectedRow != -1) {
            // Ensure the selected row has a valid id
            String id = table.getValueAt(selectedRow, columnNames.length - 1).toString();
    
            // Checking if the id is valid  
            if (id != null && !id.isEmpty()) {
                String url = "jdbc:sqlite:student_db.db";
                String query = "DELETE FROM studentinfo WHERE id = ?";
    
                try {
                    Class.forName("org.sqlite.JDBC");
                    Connection conn = DriverManager.getConnection(url);
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, id);
    
                    // Execute the delete operation
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        // Remove the row from the table UI
                        tableModel.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Record Deleted Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "No record found with this ID", "Delete Error", JOptionPane.ERROR_MESSAGE);
                    }
                    pstmt.close();
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No valid record selected for deletion", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a record to delete", "Delete Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void viewDeletedData() {
        String url = "jdbc:sqlite:student_db.db";
        String query = "SELECT * FROM studentinfo_backup";  // Query to fetch deleted records
    
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
    
            // Create a new table model for the backup data
            DefaultTableModel backupTableModel = new DefaultTableModel();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
    
            // Add columns to the backup table model
            for (int i = 1; i <= columnCount; i++) {
                backupTableModel.addColumn(metaData.getColumnName(i));
            }
    
            // Populate table with deleted data
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                backupTableModel.addRow(row);
            }
    
            // Create a new table to show the backup data
            JTable backupTable = new JTable(backupTableModel);
            JScrollPane backupScrollPane = new JScrollPane(backupTable);
    
            // Display the backup data in a new frame
            JFrame backupFrame = new JFrame("Deleted Student Records");
            backupFrame.setSize(800, 500);
            backupFrame.add(backupScrollPane);
            backupFrame.setLocationRelativeTo(null);
            backupFrame.setVisible(true);
    
            rs.close();
            stmt.close();
            conn.close();
    
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void goBackToLogin() {
        this.dispose(); // Close current window
        SwingUtilities.invokeLater(() -> EditInfoForm.main(new String[]{}));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDatabaseUI().setVisible(true));
    }
}
