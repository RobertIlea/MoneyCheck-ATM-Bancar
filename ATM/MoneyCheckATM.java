package ATM;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class is used to create an ATM.
 */
public class MoneyCheckATM {

    /**
     * atm_name is the name of the ATM
     */
    private String atm_name;
    /**
     * amount_of_money is the amount of money that the ATM has
     */
    private double amount_of_money;

    /**
     * users is an array list of users that the ATM has
     */
    private ArrayList<User> users;

    /**
     * admin is the admin of the ATM
     */
    private Admin admin;

    /**
     * Constructor without parameters
     */
    public MoneyCheckATM(){
        this.atm_name=null;
        this.amount_of_money=0.0;
        this.users=null;
        this.admin=null;
    }
    /**
     * Constructor with parameters
     * @param atmName name of the ATM
     * @param amount_of_money amount of money that the ATM has
     * @param users array list of users that the ATM has
     * @param admin admin of the ATM
     */
    public MoneyCheckATM(String atmName, double amount_of_money, ArrayList<User> users, Admin admin){
        this.atm_name = atmName;
        this.amount_of_money=amount_of_money;
        this.users=users;
        this.admin=admin;
    }
    /**
     * This method is used to get the name of the ATM
     * @return name of the ATM
     */
    public String getAtm_name(){
        return atm_name;
    }

    /**
     * This method is used to set the name of the ATM
     * @param atm_name name of the ATM
     */
    public void setAtm_name(String atm_name){
        this.atm_name=atm_name;
    }

    /**
     * This method is used to get the amount of money that the ATM has
     * @return amount of money that the ATM has
     */
    public double getAmount_of_money(){
        return amount_of_money;
    }

    /**
     * This method is used to get the users that the ATM has
     * @return users that the ATM has
     */
    public ArrayList<User> getUsers(){
        return users;
    }

    /**
     * This method is used to get the admin of the ATM
     * @return admin of the ATM
     */
    public Admin getAdmin(){
        return admin;
    }

    /**
     * This method is used to set the users that the ATM has
     * @param users amount of money that the ATM has
     */
    public void setUsers(ArrayList<User> users){
        this.users=users;
    }

    /**
     * This method is used to set the admin of the ATM
     * @param admin admin of the ATM
     */
    public void setAdmin(Admin admin){
        this.admin=admin;
    }

    /**
     * This method is used to set the amount of money that the ATM has
     * @param sum amount of money that the ATM has
     */
    public void setAmount_of_money(double sum) {
        this.amount_of_money = sum;
    }

    /**
     * This method is used to retrieve an ATM from database
     * @param name name of the ATM
     */
    public static MoneyCheckATM retrieveAtmFromDatabase(String name) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        MoneyCheckATM ATM = new MoneyCheckATM();
        try (Connection connection = DriverManager.getConnection(url)) {
            String selectQuery = "SELECT a.*, COUNT(u.iban) AS numUsers, u.first_name, u.last_name, u.iban, u.pin_code, u.balance, u.Type, u.mail, u.card_blocked " +
                    "FROM ATM a LEFT JOIN Users u ON a.Atm_name = u.atm " +
                    "WHERE a.Atm_name = ?";


            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, name);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        if (ATM.getAtm_name() == null) { // Retrieve the ATM data only once
                            ATM.setAtm_name(resultSet.getString("Atm_name"));
                            if (resultSet.getString("Atm_sold") != null) {
                                ATM.setAmount_of_money(resultSet.getDouble("Atm_sold"));
                            }
                            int numUsers = resultSet.getInt("numUsers");
                            ATM.setUsers(new ArrayList<>());

                            // Retrieve and set the admin
                            Admin admin = Admin.getAdminDataByEmail(resultSet.getString("Atm_admin"));
                            ATM.setAdmin(admin);

                            // Process user data for the specified number of users
                            boolean cardBlocked = resultSet.getBoolean("card_blocked");
                            String cardBlockedString = cardBlocked ? "true" : "false";
                            for (int i = 0; i < numUsers; i++) {
                                String userFirstName = resultSet.getString("first_name");
                                if (userFirstName != null) {
                                    User user = new User(
                                            userFirstName,
                                            resultSet.getString("last_name"),
                                            resultSet.getString("iban"),
                                            resultSet.getInt("pin_code"),
                                            resultSet.getInt("balance"),
                                            resultSet.getString("Type").equals("Admin") ? FunctionType.Admin : FunctionType.User,
                                            ATM.atm_name,
                                            resultSet.getString("mail"),
                                            cardBlocked // Convert the boolean to a string
                                    );
                                    ATM.getUsers().add(user);
                                    user.setCard_blocked(cardBlockedString); // Set the card_blocked field
                                }

                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching ATM data from the database: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return ATM;
    }

    /**
     * This method is used to add an ATM to database
     * @param ATM the ATM that is going to be added
     */
    public static void addAtmToDatabase(MoneyCheckATM ATM){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try{
            Connection connection = DriverManager.getConnection(url);
            String insertQuery = "INSERT INTO ATM (Atm_name, Atm_admin, Atm_users, Atm_sold) values (?,?,?,?) "; //insert data into ATM table
            try(PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)){ //prepare statement
                preparedStatement.setString(1, ATM.getAtm_name()); //set ATM name
                preparedStatement.setString(2, ATM.getAdmin().getFirst_name()); //set ATM_admin first name
                preparedStatement.setInt(3,ATM.getUsers().size()); //set Atm_users number
                preparedStatement.setDouble(4,ATM.getAmount_of_money()); //set Atm_sold

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is used to remove an ATM from database
     * @param ATM the ATM that is going to be removed
     */
    public static void removeAtmFromDatabase(MoneyCheckATM ATM){
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
        try{
            Connection connection = DriverManager.getConnection(url); //connect to database
            String deleteQuery = "DELETE FROM ATM WHERE Atm_name = ?"; //delete query
            try(PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)){ //trying to delete
                preparedStatement.setString(1, ATM.getAtm_name()); //set atm name that we want to delete
                preparedStatement.executeUpdate(); //execute the deletion
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if the atm exists.
     * @param atmName the name of the atm
     * @return true if the atm exists, false otherwise
     */
    public static boolean atmExists(String atmName) {
        String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";

        try (Connection connection = DriverManager.getConnection(url)) {
            String query = "SELECT COUNT(*) FROM ATM WHERE Atm_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, atmName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Override toString method for the ATM class
     * @return the ATM as a string
     */
    @Override
    public String toString(){
        StringBuilder out = new StringBuilder();
        out.append("ATM Sold is: ").append(getAmount_of_money());
        out.append("\n").append(getAdmin());
        for(User user : users){
            int i=1;
            out.append("\nUser[" + i + "]: ").append(user.toString());
            i++;
        }
        return out.toString();
    }


}
