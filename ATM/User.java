package ATM;

import java.sql.*;
import java.util.ArrayList;

public class User {
    private int id_User;
    private String first_name;
    private String last_name;
    private String IBAN;
    private int pin_code;
    private double balance;
    private boolean card_blocked;
    private String atm_name;
    private String mail;

    public User() {
    }

    public User(String first_name, String last_name, String IBAN, int pin_code, double balance, FunctionType User, String atm_name, String mail, boolean card_blocked) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.IBAN = IBAN;
        this.pin_code = pin_code;
        this.balance = balance;
        this.atm_name = atm_name;
        this.mail = mail;
        this.card_blocked = false;
    }

    public int getId_User() {
        return id_User;
    }
    public String getMail(){
        return mail;
    }
    public void setMail(String mail){
        this.mail=mail;
    }
    public String getAtm_name(){
        return atm_name;
    }
    public void setAtm_name(String atm_name){
        this.atm_name=atm_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getIBAN() {
        return IBAN;
    }

    public int getPin_code() {
        return pin_code;
    }

    public double getBalance() {
        return balance;
    }

    private void setBalance(double balance) {
        this.balance = balance;
    }
    public void setPin_code(int pin_code){
        this.pin_code = pin_code;
    }
    private void setId_User(int anInt) {
        this.id_User = anInt;
    }
    public void setCard_blocked(String card_blocked){
        this.card_blocked = Boolean.parseBoolean(card_blocked);
    }
    public boolean isCard_blocked(){
        return card_blocked;
    }
    public FunctionType getType(){
        return FunctionType.User;
    }

    /**
     * Get user data by IBAN
     *
     * @param iban
     * @return
     */
    public static User getUserDataByIban(String iban) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        User user = null;

        try (Connection connection = DriverManager.getConnection(url)) {
            String selectQuery = "SELECT * FROM Users WHERE iban = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, iban);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // User found, create a User object with the retrieved data
                        user = new User(
                                //resultSet.getInt("id_User"),
                                resultSet.getString("first_name"),
                                resultSet.getString("last_name"),
                                resultSet.getString("iban"),
                                resultSet.getInt("pin_code"),
                                resultSet.getInt("balance"),
                                resultSet.getString("Type").equals("Admin") ? FunctionType.Admin : FunctionType.User, // if type = admin then admin else user
                                resultSet.getString("atm"),
                                resultSet.getString("mail"),
                                resultSet.getBoolean("card_blocked")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user data from the database", e);
        }

        return user;
    }

    /**
     * Method to retrieve the user data by their email.
     * @param email
     * @return
     */
    public static User getUserDataByEmail(String email){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        User user = null;

        try (Connection connection = DriverManager.getConnection(url)) {
            String selectQuery = "SELECT * FROM Users WHERE mail = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // User found, create a User object with the retrieved data
                        user = new User(
                                //resultSet.getInt("id_User"),
                                resultSet.getString("first_name"),
                                resultSet.getString("last_name"),
                                resultSet.getString("iban"),
                                resultSet.getInt("pin_code"),
                                resultSet.getInt("balance"),
                                resultSet.getString("Type").equals("Admin") ? FunctionType.Admin : FunctionType.User, // if type = admin then admin else user
                                resultSet.getString("atm"),
                                resultSet.getString("mail"),
                                resultSet.getBoolean("card_blocked")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user data from the database", e);
        }

        return user;
    }

    /**
     * Adds a user to the database
     *
     * @param user
     * @return
     */
    public static User addUser(User user, ArrayList<User> users) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try {
            Connection connection = DriverManager.getConnection(url);   // connect to database
            String insertQuery = "INSERT INTO Users (first_name, last_name, iban, pin_code, balance, Type, atm, mail, card_blocked) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"; // insert query

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) { // prepare statement
                preparedStatement.setString(1, user.getFirst_name()); // set first name
                preparedStatement.setString(2, user.getLast_name()); // set last name
                preparedStatement.setString(3, user.getIBAN()); // set iban
                preparedStatement.setInt(4, user.getPin_code());  // set pin code
                preparedStatement.setDouble(5, user.getBalance()); // set balance
                preparedStatement.setString(6, user.getType().toString()); // set type
                preparedStatement.setString(7, user.getAtm_name()); //set atm_name
                preparedStatement.setString(8, user.getMail()); //set mail
                preparedStatement.setBoolean(9, user.isCard_blocked()); //set card_blocked

                int affectedRows = preparedStatement.executeUpdate(); // execute update
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected."); // if no rows affected throw exception
                }
            }

            // Manually fetch the user's data using a SELECT query and set the ID
            String selectQuery = "SELECT * FROM Users WHERE iban = ?"; // select query
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) { // prepare statement
                selectStatement.setString(1, user.getIBAN()); // set iban

                try (ResultSet resultSet = selectStatement.executeQuery()) { // execute query
                    if (resultSet.next()) { // if user found
                        user.setId_User(resultSet.getInt("id_User")); // set id
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained."); // if no id obtained throw exception
                    }
                }
            }
            users.add(user);
        } catch (SQLException e) {
            throw new RuntimeException("Error at adding a user to the database!!", e);
        }

        return user;
    }

    /**
     * Delete a user from the database
     *
     * @param user
     */
    public static void removeUser(User user, MoneyCheckATM atm) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try (Connection connection = DriverManager.getConnection(url)) {
            String deleteQuery = "DELETE FROM Users WHERE iban = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, user.getIBAN());
                preparedStatement.executeUpdate();
            }

            // Update the number of users in the ATM table
            String updateQuery = "UPDATE ATM SET Atm_users = ? WHERE Atm_name = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, atm.getUsers().size());
                updateStatement.setString(2, atm.getAtm_name());
                updateStatement.executeUpdate();
            }

            atm.getUsers().remove(user);
        } catch (SQLException e) {
            throw new RuntimeException("Error at deleting the user from database!!", e);
        }
    }

    /**
     * Insert a transaction into the database
     *
     * @param user1_iban
     * @param user2_iban
     * @param amount
     * @param transactionType
     */ 
    public static void insertTransaction(String user1_iban, String user2_iban, double amount, TransactionType transactionType){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try{
            Connection connection = DriverManager.getConnection(url); // connect to database
            String insertQuery = "INSERT INTO Transactions (sender_iban, receiver_iban, amount, transaction_type) VALUES (?, ?, ?, ?)"; // insert query

            try(PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)){ // prepare statement
                preparedStatement.setString(1,user1_iban); // set sender iban
                preparedStatement.setString(2,user2_iban);  // set receiver iban
                preparedStatement.setDouble(3,amount); // set amount
                preparedStatement.setString(4, String.valueOf(transactionType)); // set transaction type

                preparedStatement.executeUpdate(); // execute update
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add balance to a user
     *
     * @param user
     * @param quantity
     */
    public static void addBalance(User user, double quantity) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        if(quantity < 0){
            System.out.println("Quantity must be positive");
            return;
        }
        if(user.isCard_blocked()){
            System.out.println("Card is blocked");
            return;
        }
        try {
            Connection connection = DriverManager.getConnection(url);
            String updateQuery = "UPDATE Users SET balance = ? WHERE iban = ?"; // update balance

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) { // prepare statement
                preparedStatement.setDouble(1, user.getBalance() + quantity); // set new balance
                preparedStatement.setString(2, user.getIBAN()); // where iban = user.getIBAN()

                preparedStatement.executeUpdate(); // execute update
            }
            user.setBalance(user.getBalance() + quantity); // update user balance
            insertTransaction(user.getIBAN(), user.getIBAN(),quantity, TransactionType.Deposit); // insert transaction
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Withdraw money from users account.
     *
     * @param user
     * @param quantity
     */
    public static void withdrawBalance(User user, double quantity, MoneyCheckATM atm) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        if(quantity < 0){
            System.out.println("Quantity must be positive");
            return;
        }
        if(user.isCard_blocked()){
            System.out.println("Card is blocked");
            return;
        }
        try{
            Connection connection = DriverManager.getConnection(url);
            String selectQuery = "SELECT Atm_sold FROM ATM WHERE Atm_name = ?"; // Select the ATM sold value from the database
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) { // Prepare the query
                selectStatement.setString(1, atm.getAtm_name()); // Set the ATM name
                try (ResultSet resultSet = selectStatement.executeQuery()) { // Execute the query
                    if (resultSet.next()) { // If the ATM is found in the database
                        double currentSold = resultSet.getDouble("Atm_sold"); // Retrieve the current sold value from the database
                        if(quantity > currentSold){
                            System.out.println("Not enough money in ATM");
                            return;
                        }
                        atm.setAmount_of_money(currentSold - quantity); // Update the MoneyCheckATM object
                        // Update the ATM_sold value in the database
                        String updateAtmQuery = "UPDATE ATM SET Atm_sold = ? WHERE Atm_name = ?";
                        try (PreparedStatement updateAtmStatement = connection.prepareStatement(updateAtmQuery)) {
                            updateAtmStatement.setDouble(1, currentSold - quantity);
                            updateAtmStatement.setString(2, atm.getAtm_name());
                            updateAtmStatement.executeUpdate();
                        }
                    } else {
                        System.out.println("ATM not found in the database.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = DriverManager.getConnection(url)) { // connect to database
            String updateQuery = "UPDATE Users SET Balance = ? WHERE Iban = ?"; // update balance

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setDouble(1, user.getBalance() - quantity); // set new balance
                preparedStatement.setString(2, user.getIBAN()); // where iban = user.getIBAN()

                preparedStatement.executeUpdate(); // execute update
            }
            user.setBalance(user.getBalance() - quantity); // update user balance
            atm.setAmount_of_money(atm.getAmount_of_money() - quantity); // update ATM money
            insertTransaction(user.getIBAN(),user.getIBAN(),quantity,TransactionType.Withdraw); // insert transaction
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Method to change user's PIN.
     * @param user
     * @param newpin
     */
    public static void changePIN(User user, int newpin){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        if(user.isCard_blocked()){
            System.out.println("Card is blocked");
            return;
        }
        try{
            Connection connection = DriverManager.getConnection(url); // connect to database
            String updateQuery = "UPDATE Users SET Pin_code = ? WHERE Iban = ?"; // update pin code

            try(PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)){ // prepare statement
                preparedStatement.setInt(1, newpin); // set new pin code
                preparedStatement.setString(2,user.getIBAN()); // where iban = user.getIBAN()

                preparedStatement.executeUpdate(); // execute update
            }

            user.setPin_code(newpin); // update user pin code
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method used for transfer between two users.
     * @param user1
     * @param user2
     * @param quantity
     */
    public static void transferBetweenUsers(User user1, User user2, double quantity){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        if(user1.isCard_blocked()) {
            System.out.println(user1.getFirst_name() + " card is blocked");
            return;
        }
        if(user2.isCard_blocked()) {
            System.out.println(user2.getFirst_name() + " card is blocked");
            return;
        }
        try{
            // For user1 which is the one who send the money
            Connection connection = DriverManager.getConnection(url); // connect to database
            String updateQuery = "UPDATE Users SET Balance = ? WHERE Iban = ?"; // update balance

            try(PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)){ // prepare statement
                preparedStatement.setDouble(1,user1.getBalance() - quantity); // set new balance
                preparedStatement.setString(2,user1.getIBAN()); // where iban = user1.getIBAN()

                preparedStatement.executeUpdate(); // execute update
            }
            user1.setBalance(user1.getBalance() - quantity); // update user1 balance

            // For user2 which is the one who receive the money
            String updateQuery2 = "Update Users SET Balance = ? WHERE Iban = ?"; // update balance
            try(PreparedStatement preparedStatement = connection.prepareStatement(updateQuery2)){ // prepare statement
                preparedStatement.setDouble(1, user2.getBalance() + quantity); // set new balance
                preparedStatement.setString(2,user2.getIBAN()); // where iban = user2.getIBAN()

                preparedStatement.executeUpdate(); // execute update
            }
            user2.setBalance(user2.getBalance() + quantity); // update user2 balance
            insertTransaction(user1.getIBAN(), user2.getIBAN(), quantity, TransactionType.Transfer); // insert transaction
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Override toString method
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("User data:\n");
        out.append("\nFirst name: ").append(getFirst_name());
        out.append("\nLast name: ").append(getLast_name());
        out.append("\nIBAN: ").append(getIBAN());
        out.append("\nPin code: ").append(getPin_code());
        out.append("\nBalance: ").append(getBalance());
        out.append("\nType: ").append(FunctionType.User);
        out.append("\nCard blocked: ").append(isCard_blocked() ? "Yes" : "No");
        out.append("\nATM name: ").append(getAtm_name());
        out.append("\nMail: ").append(getMail());
        return out.toString();
    }


}


