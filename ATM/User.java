package ATM;

import java.sql.*;

public class User {
    private int id_User;
    private String first_name;
    private String last_name;
    private String IBAN;
    private int pin_code;
    private double balance;
    private boolean card_blocked;

    public User() {
    }

    public User(String first_name, String last_name, String IBAN, int pin_code, double balance, FunctionType User) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.IBAN = IBAN;
        this.pin_code = pin_code;
        this.balance = balance;
    }

    public int getId_User() {
        return id_User;
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
    public void setCard_blocked(boolean b) {
        this.card_blocked=b;
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
                                resultSet.getString("Type").equals("Admin") ? FunctionType.Admin : FunctionType.User // if type = admin then admin else user

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
    public static User addUser(User user) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try {
            Connection connection = DriverManager.getConnection(url);   // connect to database
            String insertQuery = "INSERT INTO Users (first_name, last_name, iban, pin_code, balance, Type) VALUES (?, ?, ?, ?, ?, ?)"; // insert query

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) { // prepare statement
                preparedStatement.setString(1, user.getFirst_name()); // set first name
                preparedStatement.setString(2, user.getLast_name()); // set last name
                preparedStatement.setString(3, user.getIBAN()); // set iban
                preparedStatement.setInt(4, user.getPin_code());  // set pin code
                preparedStatement.setDouble(5, user.getBalance()); // set balance
                preparedStatement.setString(6, user.getType().toString()); // set type

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
                        // Set other user properties based on the retrieved data
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained."); // if no id obtained throw exception
                    }
                }
            }
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
    public static void removeUser(User user) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try {
            Connection connection = DriverManager.getConnection(url); // connect to database
            String deleteQuery = "DELETE FROM Users WHERE iban = ?"; // delete query


            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) { // prepare statement
                preparedStatement.setString(1, user.getIBAN()); // set iban
                preparedStatement.executeUpdate(); // execute update
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error at deleting the user from database!!", e);
        }
    }
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
        out.append("\nType: ").append(FunctionType.User).append("\n");
        return out.toString();
    }


}


