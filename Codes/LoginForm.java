import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginForm {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Login Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);

        // Center the login form on screen
        frame.setLocationRelativeTo(null);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // New "Edit Info" button logic
        JButton editInfoButton = new JButton("Edit Info");

        // Login button logic
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                boolean isValid = true;
                StringBuilder errorMessages = new StringBuilder();

                // Validate email
                if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    errorMessages.append("Invalid email format.\n");
                    isValid = false;
                }

                // Validate password
                if (password.isEmpty()) {
                    errorMessages.append("Password cannot be empty.\n");
                    isValid = false;
                }

                if (isValid) {
                    try {
                        // Connect to the database and check credentials
                        Connection connection = DriverManager.getConnection("jdbc:sqlite:student_db.db");
                        String query = "SELECT * FROM studentinfo WHERE Semail = ? AND Spassword = ?";
                        PreparedStatement stmt = connection.prepareStatement(query);
                        stmt.setString(1, email);
                        stmt.setString(2, password);

                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            // Retrieve user info
                            String name = rs.getString("Sname");
                            String mobile = rs.getString("Smobile");
                            String gender = rs.getString("Sgender");
                            String address = rs.getString("Saddress");
                            String city = rs.getString("Scity");
                            String pincode = rs.getString("Spincode");

                            // Display user info in a new dialog
                            String userInfo = "<html><b>User Info:</b><br>"
                                    + "Name: " + name + "<br>"
                                    + "Mobile: " + mobile + "<br>"
                                    + "Email: " + email + "<br>"
                                    + "Gender: " + gender + "<br>"
                                    + "Address: " + address + "<br>"
                                    + "City: " + city + "<br>"
                                    + "Pincode: " + pincode + "</html>";

                            JOptionPane.showMessageDialog(frame, userInfo, "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                        }
                        connection.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Error connecting to the database. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, errorMessages.toString(), "Validation Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Register button logic (opens Registration Form)
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the login frame and open the registration form
                frame.setVisible(false);
                RegistrationForm.main(new String[]{});  // This will call the main method of the RegistrationForm class
            }
        });
        editInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Edit Info Form directly
                EditInfoForm.main(new String[]{});
                frame.dispose(); // Close the login form
            }
        });


        // Show/Hide password feature
        showPasswordCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0); // Show password
                } else {
                    passwordField.setEchoChar('*'); // Hide password
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
        frame.add(showPasswordCheckBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(registerButton, gbc);

        // Add the Edit Info button to the layout
        gbc.gridx = 1;
        gbc.gridy = 5;
        frame.add(editInfoButton, gbc);

        frame.setVisible(true);
    }
}
