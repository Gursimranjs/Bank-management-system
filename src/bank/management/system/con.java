package bank.management.system;

import java.sql.*;

public class con {

    Connection connection;
    Statement statement;

    public con(){
        try{
            // Load the JDBC driver (this might throw an exception if the driver is not found)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banksystem", "root", ""); // Update with your username and password
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
