package ATM;

import java.net.ConnectException;
import java.sql.*;

public class User {
    private int id_User;
    private String first_name;
    private String last_name;
    private String IBAN;
    private int pin_code;
    private int balance;

    public User() {
    }

    public User(String first_name, String last_name, String IBAN, int pin_code, int balance) {
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

    public int getBalance() {
        return balance;
    }

    private void setBalance(int balance) {
        this.balance = balance;
    }
    public void setPin_code(int pin_code){
        this.pin_code = pin_code;
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
                                resultSet.getInt("balance")

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
     */
    public static void addUser(User user) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try {
            Connection connection = DriverManager.getConnection(url);
            String insertQuery = "INSERT INTO Users (first_name, last_name, iban, pin_code) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, user.getFirst_name());
                preparedStatement.setString(2, user.getLast_name());
                preparedStatement.setString(3, user.getIBAN());
                preparedStatement.setInt(4, user.getPin_code());
                preparedStatement.setInt(5, user.getBalance());

                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error at adding a user to the database!!", e);
        }
    }

    /**
     * Delete a user from the database
     *
     * @param user
     */
    public static void removeUser(User user) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try {
            Connection connection = DriverManager.getConnection(url);
            String deleteQuery = "DELETE FROM Users WHERE iban = ?";


            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, user.getIBAN());
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error at deleting the user from database!!", e);
        }
    }
    public static void insertTransaction(String user1_iban, String user2_iban, int amount, TransactionType transactionType){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try{
            Connection connection = DriverManager.getConnection(url);
            String insertQuery = "INSERT INTO Transactions (sender_iban, receiver_iban, amount, transaction_type) VALUES (?, ?, ?, ?)";

            try(PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)){
                preparedStatement.setString(1,user1_iban);
                preparedStatement.setString(2,user2_iban);
                preparedStatement.setInt(3,amount);
                preparedStatement.setString(4, String.valueOf(transactionType));

                preparedStatement.executeUpdate();
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
    public static void addBalance(User user, int quantity) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try {
            Connection connection = DriverManager.getConnection(url);
            String updateQuery = "UPDATE Users SET balance = ? WHERE iban = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, user.getBalance() + quantity);
                preparedStatement.setString(2, user.getIBAN());

                preparedStatement.executeUpdate();
            }
            user.setBalance(user.getBalance() + quantity);
            insertTransaction(user.getIBAN(), user.getIBAN(),quantity, TransactionType.Deposit);
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
    public static void withdrawBalance(User user, int quantity) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try (Connection connection = DriverManager.getConnection(url)) {
            String updateQuery = "UPDATE Users SET Balance = ? WHERE Iban = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, user.getBalance() - quantity);
                preparedStatement.setString(2, user.getIBAN());

                preparedStatement.executeUpdate();
            }
            user.setBalance(user.getBalance() - quantity);
            insertTransaction(user.getIBAN(),user.getIBAN(),quantity,TransactionType.Withdraw);
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
        try{
            Connection connection = DriverManager.getConnection(url);
            String updateQuery = "UPDATE Users SET Pin_code = ? WHERE Iban = ?";

            try(PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)){
                preparedStatement.setInt(1, newpin);
                preparedStatement.setString(2,user.getIBAN());

                preparedStatement.executeUpdate();
            }

            user.setPin_code(newpin);
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
    public static void transferBetweenUsers(User user1, User user2, int quantity){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try{
            // For user1 which is the one who send the money
            Connection connection = DriverManager.getConnection(url);
            String updateQuery = "UPDATE Users SET Balance = ? WHERE Iban = ?";

            try(PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)){
                preparedStatement.setInt(1,user1.getBalance() - quantity);
                preparedStatement.setString(2,user1.getIBAN());

                preparedStatement.executeUpdate();
            }
            user1.setBalance(user1.getBalance() - quantity);

            // For user2 which is the one who receive the money
            String updateQuery2 = "Update Users SET Balance = ? WHERE Iban = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(updateQuery2)){
                preparedStatement.setInt(1, user2.getBalance() + quantity);
                preparedStatement.setString(2,user2.getIBAN());

                preparedStatement.executeUpdate();
            }
            user2.setBalance(user2.getBalance() + quantity);

            insertTransaction(user1.getIBAN(), user2.getIBAN(), quantity, TransactionType.Transfer);
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

        return out.toString();
    }
}


