import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("unused")
public class RegistrationForm {
    private static JLabel photoPreviewLabel = new JLabel("No Photo Selected");
    private static BufferedImage selectedImage = null;

    // Validation helper method
    public static boolean isPasswordStrong(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*\\d.*");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Registration Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 750);  
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel photoLabel = new JLabel("Upload Photo:");
        JButton photoButton = new JButton("Choose Photo");

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);

        JLabel mobileLabel = new JLabel("Mobile No:");
        JTextField mobileField = new JTextField(15);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JLabel genderLabel = new JLabel("Gender:");
        JRadioButton maleButton = new JRadioButton("Male");
        JRadioButton femaleButton = new JRadioButton("Female");
        JRadioButton otherButton = new JRadioButton("Other");

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderGroup.add(otherButton);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        genderPanel.add(otherButton);

        JLabel addressLabel = new JLabel("Address:");
        JTextArea addressField = new JTextArea(3, 20);
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        JScrollPane addressScrollPane = new JScrollPane(addressField);

        JLabel cityLabel = new JLabel("City:");
        String[] cities = {"Select City", "New York", "Los Angeles", "Chicago", "Houston"};
        JComboBox<String> cityComboBox = new JComboBox<>(cities);

        JLabel pincodeLabel = new JLabel("Pincode:");
        JTextField pincodeField = new JTextField(10);

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");

        // Disable the Register button by default
        registerButton.setEnabled(true);

        // Photo upload logic
        photoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
                
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        selectedImage = ImageIO.read(selectedFile);
                        ImageIcon icon = new ImageIcon(selectedImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                        photoPreviewLabel.setIcon(icon);
                        photoPreviewLabel.setText(""); 
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error loading image.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Mobile field limit to 10 digits (KeyListener)
        mobileField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (mobileField.getText().length() >= 10) {
                    e.consume(); // Prevent further input if length exceeds 10
                }
            }
        });

        // Pincode field limit to 6 digits (KeyListener)
        pincodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (pincodeField.getText().length() >= 6) {
                    e.consume(); // Prevent further input if length exceeds 6
                }
            }
        });

        // Register button logic
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String mobile = mobileField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String gender = maleButton.isSelected() ? "Male" :
                                femaleButton.isSelected() ? "Female" :
                                otherButton.isSelected() ? "Other" : "Not selected";
                String address = addressField.getText();
                String city = (String) cityComboBox.getSelectedItem();
                String pincode = pincodeField.getText();

                boolean isValid = true;
                StringBuilder errorMessages = new StringBuilder();

                if (name.isEmpty()) {
                    errorMessages.append("Name cannot be empty.\n");
                    isValid = false;
                }

                if (mobile.isEmpty() || mobile.length() != 10 || !mobile.matches("\\d{10}")) {
                    errorMessages.append("Mobile number must be exactly 10 digits.\n");
                    isValid = false;
                }

                if (email.isEmpty() || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                    errorMessages.append("Invalid email format.\n");
                    isValid = false;
                }

                if (password.isEmpty() || !isPasswordStrong(password)) {
                    errorMessages.append("Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, and a number.\n");
                    isValid = false;
                }

                if (address.isEmpty()) {
                    errorMessages.append("Address cannot be empty.\n");
                    isValid = false;
                }

                if (city.equals("Select City")) {
                    errorMessages.append("Please select a city.\n");
                    isValid = false;
                }

                if (pincode.isEmpty() || pincode.length() != 6 || !pincode.matches("\\d{6}")) {
                    errorMessages.append("Pincode must be exactly 6 digits.\n");
                    isValid = false;
                }

                if (isValid) {
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:sqlite:student_db.db");
                        String query = "INSERT INTO studentinfo (Sname, Smobile, Semail, Spassword, Sgender, Saddress, Scity, Spincode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement stmt = connection.prepareStatement(query);
                        stmt.setString(1, name);
                        stmt.setString(2, mobile);
                        stmt.setString(3, email);
                        stmt.setString(4, password);
                        stmt.setString(5, gender);
                        stmt.setString(6, address);
                        stmt.setString(7, city);
                        stmt.setString(8, pincode);
                        stmt.executeUpdate();
                        connection.close();

                        JOptionPane.showMessageDialog(frame, "Registration Successful!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, errorMessages.toString(), "Validation Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Login button logic (opens Login Form)
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                LoginForm.main(new String[]{});  // Opens the Login Form
            }
        });

        // Layout for the form
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(photoLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(photoButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        frame.add(photoPreviewLabel, gbc);  // Photo preview

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        frame.add(nameLabel, gbc);

        gbc.gridx = 1;
        frame.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(mobileLabel, gbc);

        gbc.gridx = 1;
        frame.add(mobileField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(emailLabel, gbc);

        gbc.gridx = 1;
        frame.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        frame.add(passwordLabel, gbc);

        gbc.gridx = 1;
        frame.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        frame.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        frame.add(genderPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        frame.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        frame.add(addressScrollPane, gbc);  // Address field

        gbc.gridx = 0;
        gbc.gridy = 8;
        frame.add(cityLabel, gbc);

        gbc.gridx = 1;
        frame.add(cityComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        frame.add(pincodeLabel, gbc);

        gbc.gridx = 1;
        frame.add(pincodeField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 10;
        frame.add(registerButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 11;
        frame.add(loginButton, gbc);  // Login button

        frame.setVisible(true);
    }
}
