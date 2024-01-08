package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;

public class Login extends JFrame implements ActionListener {

    private JLabel image1, image2, image3;
    public JLabel label1,label2,label3;
    public JTextField textfieldforemail;
    public JPasswordField Passwordfield;
    public JButton signin, openaccount, forgotpassword;


    public Login() {
        super("Bank Management System");
        initializeUI();
        signin.addActionListener(this);
        openaccount.addActionListener(this);
        forgotpassword.addActionListener(this);


    }

    private void initializeUI() {
        setLayout(null);
        setSize(850, 480);
        setLocation(300, 200);



        label1 = new JLabel("Welcome to National Bank Of Canada");
        label1.setForeground(Color.WHITE);
        label1.setFont(new Font("Avantegarde", Font.BOLD, 19));
        label1.setBounds(250, 120, 600, 40); // Adjusted the width of label1
        add(label1);

        label2 = new JLabel("Email: ");
        label2.setForeground(Color.WHITE);
        label2.setFont(new Font("Ralway", Font.BOLD, 15));
        label2.setBounds(250, 155, 600, 40); // Adjusted the width of label1
        add(label2);

        textfieldforemail= new JTextField(50);
        textfieldforemail.setFont(new Font("Arial",Font.BOLD,14));
        textfieldforemail.setBounds(300,165,230,25);
        add(textfieldforemail);

        label3 = new JLabel("Key: ");
        label3.setForeground(Color.WHITE);
        label3.setFont(new Font("Ralway", Font.BOLD, 15));
        label3.setBounds(250, 190, 600, 40); // Adjusted the width of label1
        add(label3);

        Passwordfield= new JPasswordField(20);
        Passwordfield.setFont(new Font("Arial",Font.BOLD,14));
        Passwordfield.setBounds(300,198,230,25);
        add(Passwordfield);

        signin=new JButton("Sign in");
        signin.setFont(new Font("Arial",Font.BOLD,14));
        signin.setBackground(Color.BLACK);
        signin.setBounds(320,270,200,30);
        signin.addActionListener(this);
        add(signin);

        openaccount=new JButton("Open Account");
        openaccount.setFont(new Font("Arial",Font.BOLD,10));
        openaccount.setBackground(Color.BLACK);
        openaccount.setBounds(320,300,100,30);
        openaccount.addActionListener(this);
        add(openaccount);

        forgotpassword=new JButton("Forgot Pass");
        forgotpassword.setFont(new Font("Arial",Font.BOLD,10));
        forgotpassword.setBackground(Color.BLACK);
        forgotpassword.setBounds(420,300,100,30);
        forgotpassword.addActionListener(this);
        add(forgotpassword);



        addImage("icons/bank.png", 375, 10, 100, 100);
        addImage("icons/card.png", 630, 350, 100, 100);
        addImage("icons/background.png", 0, 0, 900, 480);




        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                adjustImagePositions();
            }
        });

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

    private void adjustImagePositions() {
        int newWidth = getContentPane().getWidth();
        int newHeight = getContentPane().getHeight();

        if (image1 != null) {
            image1.setBounds(newWidth * 375 / 850, newHeight * 10 / 480, 100, 100);
        }

        if (image2 != null) {
            image2.setBounds(newWidth * 630 / 850, newHeight * 370 / 480, 100, 100);
        }

        if (image3 != null) {
            image3.setBounds(0, 0, newWidth, newHeight);
        }
        if (label1 != null) {
            // Adjust the label's position and size based on the new window dimensions
            label1.setBounds(newWidth * 250 / 850, newHeight * 120 / 480, newWidth * 600 / 850, newHeight * 40 / 480);
            label1.setFont(new Font("Avantegarde", Font.BOLD,  19));
        }

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signin) {
            String email = textfieldforemail.getText();
            String password = String.valueOf(Passwordfield.getPassword());

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banksystem", "root", "")) {
                String query = "SELECT * FROM openaccount WHERE email=? AND password=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    // Successful login
                    dispose(); // Close the login window
                    // Open the Dashboard window after successful login
                    new Dashboard(email);
                } else {
                    // Invalid credentials
                    JOptionPane.showMessageDialog(this, "Invalid email or password. Please try again.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error connecting to the database.");
            }
        } else if (e.getSource() == openaccount) {
            new OpenAccount();
            this.dispose();
        } else if (e.getSource() == forgotpassword) {
            // Code for handling forgot password functionality

            this.dispose();
        }
    }




    public static void main(String[] args) {
        new Login();
    }


}
