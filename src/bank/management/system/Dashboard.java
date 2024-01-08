package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Dashboard extends JFrame implements ActionListener {

    private String userEmail;
    private String userName;
    private Connection connection;
    private Statement statement;
    private JPanel panel;
    private JButton depositButton;
    private JButton withdrawButton, accountdetails;
    private JTextArea historyTextArea;
    private JLabel image1, image2, image3;
    private JLabel welcomeLabel;
    private JButton balanceButton;

    public Dashboard(String userEmail) {
        super("National Bank Of Canada");
        this.userEmail = userEmail;


        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banksystem", "root", ""); // Update with your username and password
            statement = connection.createStatement();

            String query = "SELECT fullName FROM openaccount WHERE email = '" + userEmail + "'";
            System.out.println("Query: " + query); // Print the query to check its correctness

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                userName = resultSet.getString("fullName");
                System.out.println("Fetched Name: " + userName); // Print the retrieved name
            } else {
                System.out.println("No name found for email: " + userEmail); // Notify if no name is found
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace for debugging
        }

        initializeUI();
    }

    private void initializeUI() {

        setLayout(null);
        setSize(850, 480);
        setLocation(300, 200);



        welcomeLabel = new JLabel("Welcome, " + userName);
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setBounds(325, 120, 600, 40);
        add(welcomeLabel);

        balanceButton = new JButton("Balance: $0.00");
        balanceButton.setOpaque(true);
        balanceButton.setForeground(Color.BLACK);
        balanceButton.setFont(new Font("Arial", Font.BOLD, 16));

        balanceButton.setBounds(85, 160, 200, 100);
        balanceButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Set cursor to hand when hovering

// Handle the click event for the balance button
        balanceButton.addActionListener(e -> showTransactionHistoryDialog(userEmail));

        add(balanceButton);

// Fetch and display balance
        double balance = fetchBalance(userEmail);
        balanceButton.setText("Balance: $" + balance);

        depositButton = new JButton("Deposit");
        depositButton.setPreferredSize(new Dimension(120, 40));
        depositButton.setBounds(325,160,200,100);
        depositButton.setFont(new Font("Arial", Font.BOLD, 16));

        depositButton.addActionListener(this);
        add(depositButton);

        withdrawButton = new JButton("Withdraw");
        withdrawButton.setPreferredSize(new Dimension(120, 40));
        withdrawButton.setBounds(565,160,200,100);
        withdrawButton.setFont(new Font("Arial", Font.BOLD, 16));
        withdrawButton.addActionListener(this);
        add(withdrawButton);



        addImage("icons/bank.png", 375, 10, 100, 100);
        addImage("icons/card.png", 630, 350, 100, 100);
        addImage("icons/background.png", 0, 0, 900, 480);



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
            image1 = image;
        } else if (imagePath.equals("icons/card.png")) {
            image2 = image;
        } else if (imagePath.equals("icons/background.png")) {
            image3 = image;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == balanceButton) {
            // Show deposit and withdrawal history dialog
            showTransactionHistoryDialog(userEmail);
        } else if (e.getSource() == depositButton) {
            double amount = getAmountFromUser("Enter deposit amount:");
            performDeposit(userEmail, amount);
        } else if (e.getSource() == withdrawButton) {
            double amount = getAmountFromUser("Enter withdrawal amount:");
            performWithdrawal(userEmail, amount);
        }
    }
    private void showTransactionHistoryDialog(String userEmail) {
        JFrame historyFrame = new JFrame("Transaction History");
        historyFrame.setSize(400, 300);
        historyFrame.setLocationRelativeTo(this);

        JTextArea historyTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        scrollPane.setBounds(50, 50, 300, 200);

        // Fetch deposit and withdrawal history from the database and set to historyTextArea
        String depositHistory = fetchDepositHistory(userEmail);
        String withdrawalHistory = fetchWithdrawalHistory(userEmail);
        historyTextArea.setText("Deposit History:\n" + depositHistory + "\n\nWithdrawal History:\n" + withdrawalHistory);

        historyFrame.add(scrollPane);
        historyFrame.setVisible(true);
    }

    // Add methods to fetch deposit and withdrawal history
    private String fetchDepositHistory(String userEmail) {
        StringBuilder depositHistory = new StringBuilder();
        try {
            String depositQuery = "SELECT * FROM deposit_history WHERE accountId = (SELECT id FROM openaccount WHERE email = '" + userEmail + "')";
            ResultSet depositResultSet = statement.executeQuery(depositQuery);
            while (depositResultSet.next()) {
                // Append deposit history details to StringBuilder
                depositHistory.append(depositResultSet.getString("amount"))
                        .append(" deposited at ")
                        .append(depositResultSet.getTimestamp("timestamp"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return depositHistory.toString();
    }

    private String fetchWithdrawalHistory(String userEmail) {
        StringBuilder withdrawalHistory = new StringBuilder();
        try {
            String withdrawalQuery = "SELECT * FROM withdrawal_history WHERE accountId = (SELECT id FROM openaccount WHERE email = '" + userEmail + "')";
            ResultSet withdrawalResultSet = statement.executeQuery(withdrawalQuery);
            while (withdrawalResultSet.next()) {
                // Append withdrawal history details to StringBuilder
                withdrawalHistory.append(withdrawalResultSet.getString("amount"))
                        .append(" withdrawn at ")
                        .append(withdrawalResultSet.getTimestamp("timestamp"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return withdrawalHistory.toString();
    }

    private double getAmountFromUser(String message) {
        String input = JOptionPane.showInputDialog(this, message);
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered!");
            return 0.0;
        }
    }
    private double fetchBalance(String userEmail) {
        double balance = 0.0;
        try {
            String balanceQuery = "SELECT initialDeposit FROM openaccount WHERE email = '" + userEmail + "'";
            ResultSet balanceResultSet = statement.executeQuery(balanceQuery);
            if (balanceResultSet.next()) {
                balance = balanceResultSet.getDouble("initialDeposit");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }
    private void performDeposit(String userEmail, double amount) {
        try {
            // Update the account balance in openaccount table
            String updateBalanceQuery = "UPDATE openaccount SET initialDeposit = initialDeposit + " + amount + " WHERE email = '" + userEmail + "'";
            statement.executeUpdate(updateBalanceQuery);

            // Log the deposit in the deposit history table
            String insertDepositQuery = "INSERT INTO deposit_history (accountId, amount) SELECT id, " + amount + " FROM openaccount WHERE email = '" + userEmail + "'";
            statement.executeUpdate(insertDepositQuery);

            JOptionPane.showMessageDialog(this, "Deposit successful!");

            // Fetch and display updated balance
            double updatedBalance = fetchBalance(userEmail);
            balanceButton.setText("Balance: $" + updatedBalance); // Update balance label
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to deposit amount.");
        }
    }

    private void performWithdrawal(String userEmail, double amount) {
        try {
            // Check if the withdrawal amount is valid and sufficient
            ResultSet resultSet = statement.executeQuery("SELECT initialDeposit FROM openaccount WHERE email = '" + userEmail + "'");
            if (resultSet.next()) {
                double balance = resultSet.getDouble("initialDeposit");
                if (balance >= amount) {
                    // Update the account balance in openaccount table
                    String updateBalanceQuery = "UPDATE openaccount SET initialDeposit = initialDeposit - " + amount + " WHERE email = '" + userEmail + "'";
                    statement.executeUpdate(updateBalanceQuery);

                    // Log the withdrawal in the withdrawal history table
                    String insertWithdrawalQuery = "INSERT INTO withdrawal_history (accountId, amount) SELECT id, " + amount + " FROM openaccount WHERE email = '" + userEmail + "'";
                    statement.executeUpdate(insertWithdrawalQuery);

                    JOptionPane.showMessageDialog(this, "Withdrawal successful!");

                    // Fetch and display updated balance
                    double updatedBalance = fetchBalance(userEmail);
                    balanceButton.setText("Balance: $" + updatedBalance); // Update balance label
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to withdraw amount.");
        }
    }

    public static void main(String[] args) {
//        new Dashboard("gursimranjs03@gmail.com");
    }
}
