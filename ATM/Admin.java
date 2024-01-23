package ATM;

import javax.swing.*;
import java.sql.*;

/**
 * This class is used to create an admin object
 */
public class Admin {

    /**
     * first name of the admin
     */
    private String first_name;

    /**
     * last name of the admin
     */
    private String last_name;
    /**
     * email of the admin
     */
    private String email;

    /**
     * Constructor without parameters
     */
    public Admin(){}

    /**
     * Constructor with parameters
     * @param first_name first name of the admin
     * @param last_name last name of the admin
     * @param email email of the admin
     */
    public Admin(String first_name, String email,String last_name,FunctionType admin){
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    /**
     * This method is used to get the first name of the admin
     * @return first name of the admin
     */
    public String getFirst_name(){
        return first_name;
    }
    /**
     * This method is used to set the first name of the admin
     * @param first_name first name of the admin
     */
    public void setFirst_name(String first_name){
        this.first_name=first_name;
    }
    /**
     * This method is used to get the last name of the admin
     * @return last name of the admin
     */
    public String getLast_name(){
        return last_name;
    }
    /**
     * This method is used to set the last name of the admin
     * @param last_name last name of the admin
     */
    public void setLast_name(String last_name){
        this.last_name=last_name;
    }
    /**
     * This method is used to get the email of the admin
     * @return email of the admin
     */
    public String getEmail(){
        return email;
    }
    /**
     * This method is used to set the email of the admin
     * @param email email of the admin
     */
    public void setEmail(String email){
        this.email=email;
    }
    /**
     * This method is used to get the function type of the admin
     * @return function type of the admin
     */
    public FunctionType getFunctionType(){
        return FunctionType.Admin;
    }

    /**
     * This method is used to add an admin to the database
     * @param admin admin to be added to the database
     */
    public static void addAdminToDatabase(Admin admin) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try (Connection connection = DriverManager.getConnection(url)) {
            String insertQuery = "INSERT INTO Admin(Admin_first_name, Admin_last_name, Admin_email) VALUES(?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, admin.getFirst_name());
                preparedStatement.setString(2, admin.getLast_name());
                preparedStatement.setString(3, admin.getEmail());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is used to remove an admin from the database
     * @param admin admin to be removed from the database
     */
    public static void removeAdminFromDatabase(Admin admin){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try (Connection connection = DriverManager.getConnection(url)) {
            String deleteQuery = "DELETE FROM Admin WHERE Admin_email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, admin.getEmail());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is used to add money to an ATM
     * @param ATM ATM to which the money is added
     * @param quantity quantity of money to be added
     */
    public static void addMoneyToATM(MoneyCheckATM ATM, double quantity) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try (Connection connection = DriverManager.getConnection(url)) {
            // Retrieve the current sold value from the database
            String selectQuery = "SELECT Atm_sold FROM ATM WHERE Atm_name = ?"; // Select the ATM sold value from the database
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) { // Prepare the query
                selectStatement.setString(1, ATM.getAtm_name()); // Set the ATM name
                try (ResultSet resultSet = selectStatement.executeQuery()) { // Execute the query
                    if (resultSet.next()) { // If the ATM is found in the database
                        double currentSold = resultSet.getDouble("Atm_sold"); // Retrieve the current sold value from the database

                        // Update the sold value in the database with the accumulated value
                        String updateQuery = "UPDATE ATM SET Atm_sold = ? WHERE Atm_name = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setDouble(1, currentSold + quantity);
                            updateStatement.setString(2, ATM.getAtm_name());
                            updateStatement.executeUpdate();
                        }

                        // Retrieve the updated sold value from the database and update the MoneyCheckATM object
                        String updatedSoldQuery = "SELECT Atm_sold FROM ATM WHERE Atm_name = ?"; // Select the ATM sold value from the database
                        try (PreparedStatement updatedSoldStatement = connection.prepareStatement(updatedSoldQuery)) { // Prepare the query
                            updatedSoldStatement.setString(1, ATM.getAtm_name()); // Set the ATM name
                            try (ResultSet updatedSoldResultSet = updatedSoldStatement.executeQuery()) { // Execute the query
                                if (updatedSoldResultSet.next()) { // If the ATM is found in the database
                                    double updatedSold = updatedSoldResultSet.getDouble("Atm_sold"); // Retrieve the current sold value from the database
                                    ATM.setAmount_of_money(updatedSold); // Update the MoneyCheckATM object
                                }
                            }
                        }
                    } else {
                        System.out.println("ATM not found in the database.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }

    /**
     * This method is used to get the admin data by email
     * @param email email of the admin
     * @return admin data
     */
    public static Admin getAdminDataByEmail(String email){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        Admin admin = null;
        try (Connection connection = DriverManager.getConnection(url)) {
            String selectQuery = "SELECT * FROM Admin WHERE Admin_email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        admin = new Admin(
                                resultSet.getString("Admin_email"),
                                resultSet.getString("Admin_first_name"),
                                resultSet.getString("Admin_last_name"),
                                FunctionType.Admin
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return admin;
    }

    /**
     * Override the toString method for the Admin class
     * @return admin data
     */
    @Override
    public String toString(){
        StringBuilder out = new StringBuilder();
        out.append("ADMIN DATA:\n").append("First name: ").append(getFirst_name()).append("\nLast name: ").append(getLast_name())
                .append("\nEmail: ").append(getEmail()).append("\n");
        return out.toString();
    }
}
