package ATM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


import static ATM.MainATM.*;

public class MoneyCheck extends JFrame {
    private JButton adminButton;
    private JButton userButton;
    private MoneyCheckATM ATM;
    private JButton addMoneyButton;
    private JButton addUserButton;
    private JButton deleteUserButton;
    private ArrayList<User> users;
    private JButton blockCardButton;

    public MoneyCheck() {
        setTitle("ATM MoneyCheck");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
    }
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 1)); // 2 rows, 1 column

        adminButton = new JButton("Admin");
        userButton = new JButton("User");

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAdminPanel();
            }
        });

        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserPanel();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2)); // 1 row, 2 columns
        buttonPanel.add(adminButton);
        buttonPanel.add(userButton);

        mainPanel.add(buttonPanel);

        add(mainPanel);
    }
    private void addUserToATM(){
        String user_first_name = JOptionPane.showInputDialog("Enter user first name:");
        if (user_first_name == null || user_first_name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid first name. Please enter a valid name.");
            return;
        }
        String user_last_name = JOptionPane.showInputDialog("Enter user last name:");
        if (user_last_name == null || user_last_name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid last name. Please enter a valid name.");
            return;
        }
        String user_iban = randomIban();
        int user_pin_code = randomPinCode();
        double user_balance = 0;
        FunctionType user_function = FunctionType.User;
        String atm_name_that_user_belongs = JOptionPane.showInputDialog("Enter the name of the atm in which you want to add the user.");
        if(atm_name_that_user_belongs == null || atm_name_that_user_belongs.isEmpty()){
            JOptionPane.showMessageDialog(this,"Invalid input! Please enter a valid atm name;");
            return;
        }
        String user_email = generateEmail(user_first_name, user_last_name);
        boolean card_blocked = false;
        try{
            String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";

            User new_user = new User(user_first_name, user_last_name, user_iban, user_pin_code, user_balance, user_function, atm_name_that_user_belongs, user_email,card_blocked);

            // modify the number of users in the ATM
            ATM = MoneyCheckATM.retrieveAtmFromDatabase(atm_name_that_user_belongs);
            users = ATM.getUsers();
            User.addUser(new_user,users);
            ATM.setUsers(users);

            try{
                Connection connection = DriverManager.getConnection(url);
                String updateQuery = "UPDATE ATM SET Atm_users = ? WHERE Atm_name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setInt(1,users.size());
                preparedStatement.setString(2,atm_name_that_user_belongs);
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                throw new RuntimeException();
            }

        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error at adding the user");
        }

    }
    private void addMoneyToATM(){
        String atmName = JOptionPane.showInputDialog("Enter the name of the ATM:");
        if (atmName == null || atmName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid ATM name. Please enter a valid name.");
            return;
        }
        ATM = MoneyCheckATM.retrieveAtmFromDatabase(atmName);

        String quantityStr = JOptionPane.showInputDialog("Enter the quantity of money to add:");
        if (quantityStr != null && !quantityStr.isEmpty()) {
            try {
                double quantity = Double.parseDouble(quantityStr);
                if(quantity < 0){
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than 0!");
                    return;
                }
                Admin.addMoneyToATM(ATM, quantity);
                JOptionPane.showMessageDialog(this, "Money added to ATM: " + quantity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
        }
    }
    private void deleteUser(){
        // TODO: it works but needs some adjustments
        String user_iban = JOptionPane.showInputDialog("Enter user IBAN:");
        if(user_iban == null || user_iban.isEmpty()){
            JOptionPane.showMessageDialog(this, "Invalid IBAN");
        }
        try{
            User user = User.getUserDataByIban(user_iban);
            String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
            ATM = MoneyCheckATM.retrieveAtmFromDatabase(user.getAtm_name());
            User.removeUser(user,ATM);
            users=ATM.getUsers();
            users.remove(user);
            ATM.setUsers(users);
            int size = ATM.getUsers().size() - 1;

            try{
                Connection connection = DriverManager.getConnection(url);
                String updateQuery = "UPDATE ATM SET Atm_users = ? WHERE Atm_name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setInt(1,size);
                preparedStatement.setString(2,user.getAtm_name());
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                throw new RuntimeException();
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error at removing the user");
        }
    }
    private void blockCard() {
        String userIban = JOptionPane.showInputDialog("Enter user IBAN:");
        if (userIban == null || userIban.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid IBAN");
            return;
        }
        try {
            User user = User.getUserDataByIban(userIban);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "User not found!");
                return;
            }
            if (user.isCard_blocked()) {
                JOptionPane.showMessageDialog(this, "Card is already blocked!");
                return;
            }
            String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
            try (Connection connection = DriverManager.getConnection(url)) {
                String updateQuery = "UPDATE Users SET card_blocked = ? WHERE iban = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setBoolean(1, true);
                    preparedStatement.setString(2, user.getIBAN());
                    int rowsUpdated = preparedStatement.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Card successfully blocked for user: " + user.getIBAN());
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to block the card for user: " + user.getIBAN());
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "SQL error updating card_blocked status: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error blocking the card: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openAdminPanel() {
        // Code to open the admin panel or perform admin-specific actions
        JPanel adminPanel = new JPanel(new GridLayout(2, 1)); // 2 rows, 1 column
        JPanel aditionalPanel = new JPanel(new GridLayout(2, 2)); // 2 row, 2 columns
        addMoneyButton = new JButton("Add money to ATM");
        addUserButton = new JButton("Add user to an ATM");
        deleteUserButton = new JButton("Remove user");
        blockCardButton = new JButton("Block card");
        addMoneyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMoneyToATM();
            }
        });
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUserToATM();
            }
        });
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });
        blockCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blockCard();
            }
        });
        aditionalPanel.add(addMoneyButton);
        aditionalPanel.add(addUserButton);
        aditionalPanel.add(deleteUserButton);
        aditionalPanel.add(blockCardButton);
        adminPanel.add(aditionalPanel);

        getContentPane().removeAll();
        getContentPane().add(adminPanel);
        revalidate();
        repaint();

    }



    private void openUserPanel() {
        // Code to open the user panel or perform user-specific actions
        JOptionPane.showMessageDialog(this, "User Panel");
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MoneyCheck atmGUI = new MoneyCheck();
                atmGUI.setVisible(true);
            }
        });
    }
}
