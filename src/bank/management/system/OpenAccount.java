package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.UUID;

class OpenAccount extends JFrame implements ActionListener {

    private JLabel label;
    private JTextField fullNameField, emailField, initialDepositField, passwordField, addressField, phoneNumberField, governmentIdField;
    private JButton createAccountButton, cancelButton;

    public OpenAccount() {
        super("Open Account");
        initializeUI();
    }

    private void initializeUI() {
        setLayout(null);
        setSize(850, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Customized colors and fonts
        Color backgroundColor = new Color(220, 220, 220);
        Color buttonColor = new Color(65, 105, 225);
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        getContentPane().setBackground(backgroundColor);

        label = new JLabel("Open a New Account");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setBounds(300, 110, 300, 30);
        add(label);

        String[] accountTypes = {"Savings Account", "Checking Account", "Business Account"};
        JComboBox<String> accountTypeComboBox = new JComboBox<>(accountTypes);
        accountTypeComboBox.setBounds(330, 155, 200, 25);
        add(accountTypeComboBox);

        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setBounds(50, 200, 80, 20);
        add(fullNameLabel);

        fullNameField = new JTextField();
        fullNameField.setBounds(150, 200, 200, 25);
        add(fullNameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(440, 200, 80, 20);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(550, 200, 200, 25);
        add(emailField);



        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 230, 80, 20);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 230, 200, 25);
        add(passwordField);

        JLabel phoneNumberLabel = new JLabel("Phone :");
        phoneNumberLabel.setBounds(440, 230, 100, 20);
        add(phoneNumberLabel);

        phoneNumberField = new JTextField();
        phoneNumberField.setBounds(550, 230, 200, 25);
        add(phoneNumberField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(50, 260, 80, 20);
        add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(150, 260, 200, 25);
        add(addressField);

        JLabel initialDepositLabel = new JLabel("Initial Deposit ($):");
        initialDepositLabel.setBounds(440, 260, 100, 20);
        add(initialDepositLabel);

        initialDepositField = new JTextField();
        initialDepositField.setBounds(550, 260, 200, 25);
        add(initialDepositField);

        JLabel govIdLabel = new JLabel("Government ID:");
        govIdLabel.setBounds(50, 290, 100, 20);
        add(govIdLabel);

        governmentIdField = new JTextField();
        governmentIdField.setBounds(150, 290, 200, 25);
        add(governmentIdField);









        createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(300, 360, 140, 30);
        createAccountButton.addActionListener(this);
        add(createAccountButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(475, 360, 100, 30);
        cancelButton.addActionListener(this);
        add(cancelButton);

        addImage("icons/bank.png", 375, 10, 100, 100);
        addImage("icons/card.png", 630, 450, 100, 100);
        addImage("icons/background.png", 0, 0, 900, 600);

        setVisible(true);
    }


    private void addImage(String imagePath, int x, int y, int width, int height) {
        ImageIcon originalImageIcon = new ImageIcon(ClassLoader.getSystemResource(imagePath));
        Image scaledImage = originalImageIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
        JLabel image = new JLabel(scaledImageIcon);
        image.setBounds(x, y, width, height);
        add(image);

        if (imagePath.equals("icons/bank.png")) {
            JLabel image1 = image;
        } else if (imagePath.equals("icons/card.png")) {
            JLabel image2 = image;
        } else if (imagePath.equals("icons/background.png")) {
            JLabel image3 = image;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createAccountButton) {
            // Check if all fields are filled
            if (fullNameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                    passwordField.getText().isEmpty() || initialDepositField.getText().isEmpty() ||
                    addressField.getText().isEmpty() || phoneNumberField.getText().isEmpty() ||
                    governmentIdField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            } else {
                try {
                    con databaseConnection = new con();

                    String fullName = fullNameField.getText();
                    String email = emailField.getText();
                    String password = passwordField.getText();
                    double initialDeposit = Double.parseDouble(initialDepositField.getText());
                    String address = addressField.getText();
                    String phoneNumber = phoneNumberField.getText();
                    String governmentIdNumber = governmentIdField.getText();

                    String accountId = generateAccountId(); // Implement this method to generate a unique account ID
                    String cardNumber = generateCardNumber(); // Implement this method to generate a unique card number

                    // Insert data into the account table
                    String accountInsertQuery = "INSERT INTO openaccount (accountType, fullName, email, password, initialDeposit, address, phoneNumber, accountId, cardNumber, govtId) " +
                            "VALUES ('Savings Account', '" + fullName + "', '" + email + "', '" + password + "', " + initialDeposit + ", '" + address + "', '" + phoneNumber + "', '" + accountId + "', '" + cardNumber + "', '" + governmentIdNumber + "')";
                    databaseConnection.statement.executeUpdate(accountInsertQuery);

                    JOptionPane.showMessageDialog(this, "Account Created Successfully!\nAccount ID: " + accountId + "\nCard Number: " + cardNumber);

                    dispose();
                    Login l1 = new Login();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to Create Account. Please check the entered data.");
                }
            }
        } else if (e.getSource() == cancelButton) {
            this.dispose();
            new Login();

        }
    }


    public static String generateAccountId() {
        // Generate a random 6-digit number as the account ID
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    public static String generateCardNumber() {
        // Use UUID to create a random unique card number
        UUID uuid = UUID.randomUUID();
        String cardNumber = uuid.toString().replaceAll("-", "").substring(0, 16);
        return cardNumber.toUpperCase(); // Assuming the card number needs to be in uppercase
    }



        public static void main (String[]args){


    }



}