package ATM;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    @Test
    public void testAddUser() {
        ArrayList<User> users = new ArrayList<>();
        User user = new User("John", "Doe", "123456789", 1234, 100.0, FunctionType.User, "MoneyCheck", "john.doe@example.com", false);
        User addedUser = User.addUser(user, users);
        assertEquals(user.getFirst_name(), addedUser.getFirst_name());
        assertEquals(user.getLast_name(), addedUser.getLast_name());
        assertEquals(user.getIBAN(), addedUser.getIBAN());
        assertEquals(user.getPin_code(), addedUser.getPin_code());
        assertEquals(user.getBalance(), addedUser.getBalance());
        assertEquals(user.getType(), addedUser.getType());
        assertEquals(user.getAtm_name(), addedUser.getAtm_name());
        assertEquals(user.getMail(), addedUser.getMail());
        assertEquals(user.isCard_blocked(), addedUser.isCard_blocked());
    }

    @Test
    public void testRemoveUser() {
        MoneyCheckATM atm = new MoneyCheckATM();
        ArrayList<User> users = new ArrayList<>();
        User user = new User("Jane", "Doe", "987654321", 4321, 200.0, FunctionType.User, "MoneyCheck", "jane.doe@example.com", false);
        users.add(user);
        atm.setUsers(users);

        User.removeUser(user, atm);

        assertTrue(atm.getUsers().isEmpty());
    }

    @Test
    public void testAddBalance() {
        User user = new User("Alice", "Smith", "1111222233334444", 5678, 300.0, FunctionType.User, "MoneyCheck", "alice.smith@example.com", false);
        double initialBalance = user.getBalance();
        double depositAmount = 50.0;

        User.addBalance(user, depositAmount);

        assertEquals(initialBalance + depositAmount, user.getBalance());
    }

    @Test
    public void testWithdrawBalance() {
        User user = new User("Bob", "Johnson", "4444333322221111", 8765, 400.0, FunctionType.User, "MoneyCheck", "bob.johnson@test.com", false);
        double initialBalance = user.getBalance();
        double withdrawAmount = 50.0;
        MoneyCheckATM moneyCheck = new MoneyCheckATM();
        moneyCheck.setAmount_of_money(initialBalance);

        assertEquals(initialBalance, user.getBalance());
        assertEquals(initialBalance, moneyCheck.getAmount_of_money());

        User.withdrawBalance(user, withdrawAmount, moneyCheck);

        double expectedUserBalance = initialBalance - withdrawAmount;
        double expectedAtmBalance = initialBalance - withdrawAmount;

        assertEquals(expectedUserBalance, user.getBalance());
        assertEquals(expectedAtmBalance, moneyCheck.getAmount_of_money());
    }

    @Test
    public void testChangePIN() {
        User user = new User("Eva", "Williams", "5555666677778888", 4321, 1000.0, FunctionType.User, "MoneyCheck", "eva.williams@example.com", false);
        int newPin = 9999;

        User.changePIN(user, newPin);

        assertEquals(newPin, user.getPin_code());
    }

    @Test
    public void testTransferBetweenUsers() {
        User user1 = new User("Sender", "Smith", "1111222233334444", 1234, 1000.0, FunctionType.User, "MoneyCheck", "sender@example.com", false);
        User user2 = new User("Receiver", "Johnson", "4444333322221111", 5678, 500.0, FunctionType.User, "MoneyCheck", "receiver@example.com", false);
        double transferAmount = 200.0;

        User.transferBetweenUsers(user1, user2, transferAmount);

        assertEquals(800.0, user1.getBalance());
        assertEquals(700.0, user2.getBalance());
    }

    @Test
    public void testGetUserDataByIban() {
        ArrayList<User> users = new ArrayList<>();
        User user = new User("John", "Doe", "123456789", 1234, 100.0, FunctionType.User, "MoneyCheck", "john.doe@example.com", false);
        users.add(user);

        User retrievedUser = User.getUserDataByIban("123456789");

        assertNotNull(retrievedUser);
        assertEquals("John", retrievedUser.getFirst_name());
        assertEquals("Doe", retrievedUser.getLast_name());
        assertEquals("123456789", retrievedUser.getIBAN());
        assertEquals(1234, retrievedUser.getPin_code());
        assertEquals(100.0, retrievedUser.getBalance());
        assertEquals(FunctionType.User, retrievedUser.getType());
        assertEquals("MoneyCheck", retrievedUser.getAtm_name());
        assertEquals("john.doe@example.com", retrievedUser.getMail());
        assertFalse(retrievedUser.isCard_blocked());
    }

    @Test
    public void testInsertTransaction() {
        String senderIban = "1111222233334444";
        String receiverIban = "4444333322221111";
        double amount = 150.0;
        TransactionType transactionType = TransactionType.Transfer;

        User.insertTransaction(senderIban, receiverIban, amount, transactionType);
    }
}
