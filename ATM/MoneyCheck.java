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

/**
 * This class is used to create a GUI for the ATM application.
 */
public class MoneyCheck extends JFrame {
    private JButton adminButton;
    private JButton userButton;
    private MoneyCheckATM ATM;
    private JButton addMoneyButton;
    private JButton addUserButton;
    private JButton deleteUserButton;
    private ArrayList<User> users;
    private JButton blockCardButton;
    private JButton unBlockCardButton;
    private JTextField email;
    private JTextField pincode;
    private JTextField adminPassword;
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
                openAdminLoginPanel();
            }
        });

        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserLoginPanel();
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
            if(!MoneyCheckATM.atmExists(atm_name_that_user_belongs)){
                JOptionPane.showMessageDialog(this, "ATM with name " + atm_name_that_user_belongs + " does not exist.");
                return;
            }
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
            JOptionPane.showMessageDialog(this,"User added successfully.");
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
        MoneyCheckATM atm = MoneyCheckATM.retrieveAtmFromDatabase(atmName);
        if(atm == null || atm.getAtm_name() == null || atm.getAtm_name().isEmpty()){
            JOptionPane.showMessageDialog(this, "ATM not found!");
            return;
        }
        String quantityStr = JOptionPane.showInputDialog("Enter the quantity of money to add:");
        if (quantityStr != null && !quantityStr.isEmpty()) {
            try {
                double quantity = Double.parseDouble(quantityStr);
                if(quantity < 0){
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than 0!");
                    return;
                }
                Admin.addMoneyToATM(atm, quantity);
                JOptionPane.showMessageDialog(this, "Money added to ATM: " + quantity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
        }
    }

    private void deleteUser(){
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
    private void unBlockCard(){
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
            if (!user.isCard_blocked()) {
                JOptionPane.showMessageDialog(this, "Card is already unblocked!");
                return;
            }
            String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
            try (Connection connection = DriverManager.getConnection(url)) {
                String updateQuery = "UPDATE Users SET card_blocked = ? WHERE iban = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setBoolean(1, false);
                    preparedStatement.setString(2, user.getIBAN());
                    int rowsUpdated = preparedStatement.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Card successfully unblocked for user: " + user.getIBAN());
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to unblock the card for user: " + user.getIBAN());
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "SQL error updating card_blocked status: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error unblocking the card: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void deleteMyAccount(User user) {
        try {
            User userData = User.getUserDataByEmail(user.getMail());
            if (userData != null) {
                int confirmResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete your account?", "Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirmResult == JOptionPane.YES_OPTION) {
                    String userATM = user.getAtm_name();
                    MoneyCheckATM atm = MoneyCheckATM.retrieveAtmFromDatabase(userATM);

                    if (userATM != null && !userATM.isEmpty()) {
                        User.removeUser(userData, atm);
                        users = atm.getUsers();
                        users.remove(userData);
                        atm.setUsers(users);
                        int size = atm.getUsers().size() - 1;

                        try {
                            String url = "jdbc:sqlite:A:/MoneyCheck - ATM Bancar/MoneyCheck-ATM-Bancar/identifier.sqlite";
                            Connection connection = DriverManager.getConnection(url);
                            String updateQuery = "UPDATE ATM SET Atm_users = ? WHERE Atm_name = ?";
                            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                            preparedStatement.setInt(1, size);
                            preparedStatement.setString(2, userATM);
                            preparedStatement.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        JOptionPane.showMessageDialog(this, "Account deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error at deleting your account");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Account deletion canceled.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting your account.");
        }
    }
    private void openUserPanel(User user){
        JPanel userOptionsPanel = new JPanel(new GridLayout(3, 2));
        JButton addBalanceButton = new JButton("Add Balance");
        JButton viewBalanceButton = new JButton("View Balance");
        JButton deleteUserButton = new JButton("Delete account");
        JButton withdrawMoneyButton = new JButton("Withdraw");
        JButton changePinButton = new JButton("Modify pin code");
        JButton transferButton = new JButton("Transfer money");
        JButton blockCardButton = new JButton("Block card");
        JButton goToMainPage = new JButton("Back");
        addBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amount = JOptionPane.showInputDialog("Enter the amount of money you want to add.");
                if (amount != null && !amount.isEmpty()) {
                    User.addBalance(user, Double.parseDouble(amount));
                    JOptionPane.showMessageDialog(MoneyCheck.this, "Balance added successfully!");
                } else {
                    JOptionPane.showMessageDialog(MoneyCheck.this, "Invalid amount. Please enter a valid number.");
                }
            }
        });
        viewBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double balance = user.getBalance();
                JOptionPane.showMessageDialog(MoneyCheck.this, "Your current balance: " + balance);
            }
        });
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMyAccount(user);
                goToMainPage();
            }
        });
        withdrawMoneyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amount = JOptionPane.showInputDialog("Enter the amount of money you want to withdraw");
                if(amount == null || amount.isEmpty()){
                    JOptionPane.showMessageDialog(MoneyCheck.this,"Invalid amount. Please enter a valid number");
                    return;
                }
                else{
                    String atm_name = user.getAtm_name();
                    try{
                        MoneyCheckATM atm = MoneyCheckATM.retrieveAtmFromDatabase(atm_name);
                        if(Double.parseDouble(amount) > atm.getAmount_of_money()){
                            JOptionPane.showMessageDialog(MoneyCheck.this,"Not enough money in the " + atm.getAtm_name() + " ATM");
                            return;
                        }
                        if(Double.parseDouble(amount) > user.getBalance()){
                            JOptionPane.showMessageDialog(MoneyCheck.this,"You don't have enough money in account.");
                            return;
                        }
                        User.withdrawBalance(user,Double.parseDouble(amount),atm);
                        JOptionPane.showMessageDialog(MoneyCheck.this, "You withdraw " + Double.parseDouble(amount) +" successfully!");
                    }catch (Exception a){
                        JOptionPane.showMessageDialog(MoneyCheck.this, "Error at withdraw the amount.");
                    }
                }
            }
        });
        changePinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pin = JOptionPane.showInputDialog("Enter the new pin");
                if(pin == null || pin.isEmpty()){
                    JOptionPane.showMessageDialog(MoneyCheck.this,"Please insert a valid pin.");
                    return;
                }else{
                    try{
                        if(pin.length() != 4){
                            JOptionPane.showMessageDialog(MoneyCheck.this,"Pin code must have 4 digits.");
                            return;
                        }
                        User.changePIN(user,Integer.parseInt(pin));
                        JOptionPane.showMessageDialog(MoneyCheck.this,"Pin updated successfully.");
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(MoneyCheck.this, "Error at trying to modify your pin code.");
                    }

                }
            }
        });
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String receiverIban = JOptionPane.showInputDialog("Enter the iban you want to send.");
                if(receiverIban == null || receiverIban.isEmpty()){
                    JOptionPane.showMessageDialog(MoneyCheck.this,"The iban doesn't exists");
                    return;
                }
                String amount = JOptionPane.showInputDialog("Enter the amount of money you want to send.");
                if(amount == null || amount.isEmpty()){
                    JOptionPane.showMessageDialog(MoneyCheck.this,"Enter a valid amount of money.");
                    return;
                }
                try{
                    User receiverUser = User.getUserDataByIban(receiverIban);
                    if(Double.parseDouble(amount) > user.getBalance()){
                        JOptionPane.showMessageDialog(MoneyCheck.this, "You don't have enough money in your account.");
                        return;
                    }
                    User.transferBetweenUsers(user,receiverUser,Double.parseDouble(amount));
                    JOptionPane.showMessageDialog(MoneyCheck.this, "Transfer has been successfully processed.");
                }catch (Exception e1){
                    JOptionPane.showMessageDialog(MoneyCheck.this, "Error at trying to transfer to another user.");
                }
            }
        });
        blockCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pin = JOptionPane.showInputDialog("Enter your pin code.");
                if(pin == null || pin.isEmpty()){
                    JOptionPane.showMessageDialog(MoneyCheck.this,"Please insert a valid pin.");
                    return;
                }
                try{
                    if(Integer.parseInt(pin) != user.getPin_code()){
                        JOptionPane.showMessageDialog(MoneyCheck.this,"Wrong pin code.");
                        return;
                    }
                    if(user.isCard_blocked()){
                        JOptionPane.showMessageDialog(MoneyCheck.this,"Your card is already blocked.");
                        return;
                    }
                    User.blockCard(user);
                    JOptionPane.showMessageDialog(MoneyCheck.this,"Your card has been blocked.");
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(MoneyCheck.this, "Error at trying to block your card.");
                }
                goToMainPage();
            }
        });
        goToMainPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToMainPage();
            }
        });

        userOptionsPanel.add(deleteUserButton);
        userOptionsPanel.add(addBalanceButton);
        userOptionsPanel.add(viewBalanceButton);
        userOptionsPanel.add(withdrawMoneyButton);
        userOptionsPanel.add(changePinButton);
        userOptionsPanel.add(transferButton);
        userOptionsPanel.add(blockCardButton);
        userOptionsPanel.add(goToMainPage);

        getContentPane().removeAll();
        getContentPane().add(userOptionsPanel);
        revalidate();
        repaint();
    }
    private void openAdminPanel(Admin admin){
        JPanel adminPanel = new JPanel(new GridLayout(1, 1));
        JPanel aditionalPanel = new JPanel(new GridLayout(3, 3));
        JButton goToMainPageButton = new JButton("Back");
        addMoneyButton = new JButton("Add money to ATM");
        addUserButton = new JButton("Add user to an ATM");
        deleteUserButton = new JButton("Remove user");
        blockCardButton = new JButton("Block card");
        unBlockCardButton = new JButton("Unblock card");
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
        unBlockCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unBlockCard();
            }
        });
        goToMainPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToMainPage();
            }
        });

        aditionalPanel.add(addMoneyButton);
        aditionalPanel.add(addUserButton);
        aditionalPanel.add(deleteUserButton);
        aditionalPanel.add(blockCardButton);
        aditionalPanel.add(unBlockCardButton);
        aditionalPanel.add(unBlockCardButton);
        aditionalPanel.add(goToMainPageButton);
        adminPanel.add(aditionalPanel);

        getContentPane().removeAll();
        getContentPane().add(adminPanel);
        revalidate();
        repaint();

    }

    private void loginUser() {
        String userEmail = email.getText();
        String pinCodeStr = pincode.getText();
        if (userEmail == null || userEmail.isEmpty() || pinCodeStr == null || pinCodeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid Email or PIN Code. Please enter valid information.");
            return;
        }
        try {
            int pinCode = Integer.parseInt(pinCodeStr);
            User user = User.getUserDataByEmail(userEmail);

            if (user != null) {
                if (!user.isCard_blocked()) {
                    if (user.getPin_code() == pinCode) {
                        // Successful login
                        openUserPanel(user);
                    } else {
                        // Invalid PIN code
                        JOptionPane.showMessageDialog(this, "Invalid PIN Code. Please enter a valid PIN Code.");
                    }
                } else {
                    // Card is blocked
                    JOptionPane.showMessageDialog(this, "Card is blocked.");
                }
            } else {
                // User not found
                JOptionPane.showMessageDialog(this, "User not found. Please enter a valid email.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid PIN Code. Please enter a valid number.");
        }
    }
    private void loginAdmin(){
        String adminEmail = email.getText();
        String adminPass = adminPassword.getText();

        if(adminEmail.isEmpty() || adminPass == null || adminPass.isEmpty()){
            JOptionPane.showMessageDialog(this,"Invalid email or password. Please insert valid information");
            return;
        }
        try{
            if(!adminPass.equals("admin")){
                JOptionPane.showMessageDialog(this,"Wrong password.");
                return;
            }
            Admin admin = Admin.getAdminDataByEmail(adminEmail);
            if(admin != null){
                openAdminPanel(admin);
            }
            else{
                JOptionPane.showMessageDialog(this,"Admin not found.");
                return;
            }
        }catch (Exception ex){
            JOptionPane.showMessageDialog(this, "Error at trying to login as an admin.");
        }
    }
    private void goToMainPage(){
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(adminButton);
        buttonPanel.add(userButton);
        mainPanel.add(buttonPanel);

        getContentPane().removeAll();
        getContentPane().add(mainPanel);
        revalidate();
        repaint();
    }
    private void openAdminLoginPanel() {
        JPanel adminPanel = new JPanel(new GridLayout(2, 2));
        JPanel aditionalAdminPanel = new JPanel(new GridLayout(3,2));

        email = new JTextField();
        adminPassword = new JTextField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginAdmin();
            }
        });

        JButton goToMainPageButton = new JButton("Back");
        goToMainPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToMainPage();
            }
        });

        aditionalAdminPanel.add(new JLabel("Email:"));
        aditionalAdminPanel.add(email);

        aditionalAdminPanel.add(new JLabel("Password:"));
        aditionalAdminPanel.add(adminPassword);
        aditionalAdminPanel.add(loginButton);
        aditionalAdminPanel.add(goToMainPageButton);
        adminPanel.add(aditionalAdminPanel);


        getContentPane().removeAll();
        getContentPane().add(adminPanel);
        revalidate();
        repaint();
    }
    private void openUserLoginPanel() {
        JPanel userPanel = new JPanel(new GridLayout(2, 2));
        JPanel aditionalPanel = new JPanel(new GridLayout(3,2));

        email = new JTextField();
        pincode = new JTextField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });

        JButton goToMainPageButton = new JButton("Back");
        goToMainPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToMainPage();
            }
        });

        aditionalPanel.add(new JLabel("Email:"));
        aditionalPanel.add(email);

        aditionalPanel.add(new JLabel("PIN Code:"));
        aditionalPanel.add(pincode);
        aditionalPanel.add(loginButton);
        aditionalPanel.add(goToMainPageButton);
        userPanel.add(aditionalPanel);


        getContentPane().removeAll();
        getContentPane().add(userPanel);
        revalidate();
        repaint();
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
