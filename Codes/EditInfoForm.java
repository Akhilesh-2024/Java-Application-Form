import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EditInfoForm {
    
    public static void main(String[] args) {
        // Step 1: Initial login form to authenticate the user
        JFrame frame = new JFrame("Student Login Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Student Login");
        JButton adminLoginButton = new JButton("Admin Login");

        // Student Login button logic
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                // Validate email and password
                if (email.isEmpty() || !email.matches("^[a-zA0-9._%+-]+@[a-z0-9.-]+\\.com$")) {
                    JOptionPane.showMessageDialog(frame, "Invalid email format.", "Validation Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Password cannot be empty.", "Validation Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Step 2: Authenticate the user by checking the credentials
                    Connection connection = DriverManager.getConnection("jdbc:sqlite:student_db.db");
                    String query = "SELECT * FROM studentinfo WHERE Semail = ? AND Spassword = ?";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setString(1, email);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        // Step 3: Show the Edit User Information Form
                        new EditUserInfoForm(rs); // Pass user data to EditUserInfoForm
                        frame.dispose(); // Close login form
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                    connection.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Admin Login button logic
        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Hardcoded admin credentials
                String adminUsername = "admin";
                String adminPassword = "admin123";

                String username = JOptionPane.showInputDialog(frame, "Enter Admin Username:");
                String password = JOptionPane.showInputDialog(frame, "Enter Admin Password:");

                if (adminUsername.equals(username) && adminPassword.equals(password)) {
                    // Admin login successful, show the Student Database UI
                    StudentDatabaseUI demo = new StudentDatabaseUI();
                    demo.setVisible(true);
                    frame.dispose(); // Close login form
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Admin credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout for the form
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(emailLabel, gbc);

        gbc.gridx = 1;
        frame.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(passwordLabel, gbc);

        gbc.gridx = 1;
        frame.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(adminLoginButton, gbc);

        frame.setVisible(true);
    }

    public void setVisible(boolean b) {
        throw new UnsupportedOperationException("Unimplemented method 'setVisible'");
    }
}

// Admin Page to be shown after successful Admin login

class EditUserInfoForm {

    private JFrame editFrame;
    private JTextField nameField, mobileField, emailField, pincodeField;
    private JTextArea addressField;
    private JComboBox<String> cityComboBox;
    private JRadioButton maleButton, femaleButton, otherButton;
    private ButtonGroup genderGroup;
    private String userEmail;

    public EditUserInfoForm(ResultSet rs) {
        // Step 4: Initialize the Edit User Info Form
        try {
            userEmail = rs.getString("Semail");

            editFrame = new JFrame("Edit User Information");
            editFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            editFrame.setSize(500, 600);
            editFrame.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;

            // Create fields for editing user info
            JLabel nameLabel = new JLabel("Name:");
            nameField = new JTextField(20);
            nameField.setText(rs.getString("Sname"));

            JLabel mobileLabel = new JLabel("Mobile No:");
            mobileField = new JTextField(15);
            mobileField.setText(rs.getString("Smobile"));

            JLabel emailLabel = new JLabel("Email:");
            emailField = new JTextField(20);
            emailField.setText(rs.getString("Semail"));
            emailField.setEditable(false); // Prevent email change

            JLabel genderLabel = new JLabel("Gender:");
            maleButton = new JRadioButton("Male");
            femaleButton = new JRadioButton("Female");
            otherButton = new JRadioButton("Other");

            genderGroup = new ButtonGroup();
            genderGroup.add(maleButton);
            genderGroup.add(femaleButton);
            genderGroup.add(otherButton);

            String gender = rs.getString("Sgender");
            if ("Male".equals(gender)) {
                maleButton.setSelected(true);
            } else if ("Female".equals(gender)) {
                femaleButton.setSelected(true);
            } else {
                otherButton.setSelected(true);
            }

            // Layout the buttons with a small gap
            gbc.gridx = 1;
            gbc.gridy = 3;
            editFrame.add(maleButton, gbc);

            gbc.gridx = 2; // Shift female button next to male button
            editFrame.add(femaleButton, gbc);

            gbc.gridx = 3; // Shift other button next to female button
            editFrame.add(otherButton, gbc);

            JLabel addressLabel = new JLabel("Address:");
            addressField = new JTextArea(3, 20);
            addressField.setLineWrap(true);
            addressField.setWrapStyleWord(true);
            addressField.setText(rs.getString("Saddress"));
            JScrollPane addressScrollPane = new JScrollPane(addressField);

            JLabel cityLabel = new JLabel("City:");
            String[] cities = {"Select City", "New York", "Los Angeles", "Chicago"};
            cityComboBox = new JComboBox<>(cities);
            cityComboBox.setSelectedItem(rs.getString("Scity"));

            JLabel pincodeLabel = new JLabel("Pincode:");
            pincodeField = new JTextField(10);
            pincodeField.setText(rs.getString("Spincode"));

            // Update button
            JButton updateButton = new JButton("Update Info");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateUserInfo(); // Call update method
                }
            });

            // Layout for the form
            gbc.gridx = 0;
            gbc.gridy = 0;
            editFrame.add(nameLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            editFrame.add(mobileLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(mobileField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            editFrame.add(emailLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(emailField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            editFrame.add(genderLabel, gbc);

            gbc.gridx = 1;
            editFrame.add(maleButton, gbc);

            gbc.gridx = 2; 
            editFrame.add(femaleButton, gbc);

            gbc.gridx = 3; 
            editFrame.add(otherButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            editFrame.add(addressLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(addressScrollPane, gbc);

            gbc.gridx = 0;
            gbc.gridy = 5;
            editFrame.add(cityLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(cityComboBox, gbc);

            gbc.gridx = 0;
            gbc.gridy = 6;
            editFrame.add(pincodeLabel, gbc);
            gbc.gridx = 1;
            editFrame.add(pincodeField, gbc);

            gbc.gridx = 1;
            gbc.gridy = 7;
            editFrame.add(updateButton, gbc);

            editFrame.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching user data: " + e.getMessage());
        }
    }

    public EditUserInfoForm(String mockEmail, String mockName, String mockMobile, String mockGender, String mockAddress,
            String mockCity, String mockPincode) {
    }

    // Method to update user information in the database
    private void updateUserInfo() {
        // Get updated data from the form
        String name = nameField.getText();
        String mobile = mobileField.getText();
        String address = addressField.getText();
        String city = (String) cityComboBox.getSelectedItem();
        String pincode = pincodeField.getText();
        String gender = maleButton.isSelected() ? "Male" : femaleButton.isSelected() ? "Female" : "Other";

        // Validate data
        if (name.isEmpty() || mobile.isEmpty() || address.isEmpty() || city.equals("Select City") || pincode.isEmpty()) {
            JOptionPane.showMessageDialog(editFrame, "Please fill all fields correctly.", "Validation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update the database with the new values
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:student_db.db");
            String query = "UPDATE studentinfo SET Sname = ?, Smobile = ?, Saddress = ?, Scity = ?, Spincode = ?, Sgender = ? WHERE Semail = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, mobile);
            stmt.setString(3, address);
            stmt.setString(4, city);
            stmt.setString(5, pincode);
            stmt.setString(6, gender);
            stmt.setString(7, userEmail);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(editFrame, "User information updated successfully.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(editFrame, "Failed to update user information.", "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
            connection.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(editFrame, "Error updating user info: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
