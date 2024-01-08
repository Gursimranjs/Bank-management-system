package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ForgotPassword extends JFrame implements ActionListener {

    private JTextField emailField, cardNumberField, newPasswordField;
    private JButton resetPasswordButton,cancelButton;

    public ForgotPassword() {
        super("Forgot Password");
        initializeUI();
        resetPasswordButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }

    private void initializeUI() {
        setLayout(null);
        setSize(500, 250);
        setLocationRelativeTo(null);

        JLabel emailLabel = new JLabel("Enter your email:");
        emailLabel.setBounds(50, 30, 150, 20);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(230, 30, 200, 25);
        add(emailField);

        JLabel cardNumberLabel = new JLabel("Enter your card number:");
        cardNumberLabel.setBounds(50, 70, 160, 20);
        add(cardNumberLabel);

        cardNumberField = new JTextField();
        cardNumberField.setBounds(230, 70, 200, 25);
        add(cardNumberField);

        JLabel newPasswordLabel = new JLabel("Enter new password:");
        newPasswordLabel.setBounds(50, 110, 150, 20);
        add(newPasswordLabel);

        newPasswordField = new JTextField();
        newPasswordField.setBounds(230, 110, 200, 25);
        add(newPasswordField);

        resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.setBounds(120, 160, 150, 30);
        add(resetPasswordButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(280, 160, 100, 30);
        add(cancelButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == resetPasswordButton) {
            String email = emailField.getText();
            String cardNumber = cardNumberField.getText();
            String newPassword = newPasswordField.getText();

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banksystem", "root", "")) {
                String query = "SELECT cardNumber FROM openaccount WHERE email=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, email);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String storedCardNumber = resultSet.getString("cardNumber");
                    if (storedCardNumber.equals(cardNumber)) {
                        // Card number matches, update the password
                        String updateQuery = "UPDATE openaccount SET password=? WHERE email=?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, newPassword);
                        updateStatement.setString(2, email);
                        updateStatement.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Password reset successful.");
                    } else {
                        // Card number doesn't match
                        JOptionPane.showMessageDialog(this, "Invalid card number. Please try again.");
                    }
                } else {
                    // Email doesn't exist in the database
                    JOptionPane.showMessageDialog(this, "This account does not exist.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error connecting to the database.");
            }
        } else if (e.getSource() == cancelButton) {
            new Login();
            dispose(); // Close the ForgotPassword window
        }
    }

    public static void main(String[] args) {
        new ForgotPassword();
    }
}

