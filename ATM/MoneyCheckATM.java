package ATM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MoneyCheckATM {
    private String atm_name;
    private double amount_of_money;
    private ArrayList<User> users;
    private Admin admin;
    public MoneyCheckATM(){}

    public MoneyCheckATM(String atmName, double amount_of_money, ArrayList<User> users, Admin admin){
        this.atm_name = atmName;
        this.amount_of_money=amount_of_money;
        this.users=users;
        this.admin=admin;
    }
    public String getAtm_name(){
        return atm_name;
    }
    public void setAtm_name(String atm_name){
        this.atm_name=atm_name;
    }
    public double getAmount_of_money(){
        return amount_of_money;
    }
    public ArrayList<User> getUsers(){
        return users;
    }
    public Admin getAdmin(){
        return admin;
    }

    public void setUsers(ArrayList<User> users){
        this.users=users;
    }
    public void setAdmin(Admin admin){
        this.admin=admin;
    }
    public void setAmount_of_money(double sum) {
        this.amount_of_money = sum;
    }

    /**
     * This method is used to add an ATM to database
     * @param ATM
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
     * @param ATM
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
