package ATM;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to test the ATM application.
 */
public class Tests {

    /**
     * This method is used to test the addUser method
     */
    @Test
    public void testAddUser() {
        ArrayList<User> users = new ArrayList<>();
        User user = new User("Adriana", "Test123", "123456789221", 1234, 100.0, FunctionType.User, "MoneyCheck", "adriana.Test123@example.com", false);
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

    /**
     * This method is used to test the removeUser method
     */
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

    /**
     * This method is used to test the addBalance method
     */
    @Test
    public void testAddBalance() {
        User user = new User("Alice", "Smith", "1111222233334444", 5678, 300.0, FunctionType.User, "MoneyCheck", "alice.smith@example.com", false);
        double initialBalance = user.getBalance();
        double depositAmount = 50.0;

        User.addBalance(user, depositAmount);

        assertEquals(initialBalance + depositAmount, user.getBalance());
    }

    /**
     * This method is used to test the withdrawBalance method
     */
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

    /**
     * This method is used to test the changePIN method
     */
    @Test
    public void testChangePIN() {
        User user = new User("Eva", "Williams", "5555666677778888", 4321, 1000.0, FunctionType.User, "MoneyCheck", "eva.williams@example.com", false);
        int newPin = 9999;

        User.changePIN(user, newPin);

        assertEquals(newPin, user.getPin_code());
    }

    /**
     * This method is used to test the transferBetweenUsers method
     */
    @Test
    public void testTransferBetweenUsers() {
        User user1 = new User("Sender", "Smith", "1111222233334444", 1234, 1000.0, FunctionType.User, "MoneyCheck", "sender@example.com", false);
        User user2 = new User("Receiver", "Johnson", "4444333322221111", 5678, 500.0, FunctionType.User, "MoneyCheck", "receiver@example.com", false);
        double transferAmount = 200.0;

        User.transferBetweenUsers(user1, user2, transferAmount);

        assertEquals(800.0, user1.getBalance());
        assertEquals(700.0, user2.getBalance());
    }

    /**
     * This method is used to test the getUserDataByIBAN method
     */
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

    /**
     * This method is used to test the insertTransaction method
     */
    @Test
    public void testInsertTransaction() {
        String senderIban = "1111222233334444";
        String receiverIban = "4444333322221111";
        double amount = 150.0;
        TransactionType transactionType = TransactionType.Transfer;

        User.insertTransaction(senderIban, receiverIban, amount, transactionType);
    }

    /**
     * This method is used to test the addMoneyToATM method
     */
    @Test
    public void addMoneyToAtm() {
        MoneyCheckATM atm = MoneyCheckATM.retrieveAtmFromDatabase("MoneyCheck");

        double amount = 50.0;
        double initialAmount = atm.getAmount_of_money();
        Admin.addMoneyToATM(atm, amount);

        assertEquals(initialAmount+amount, atm.getAmount_of_money());
    }

    /**
     * This method is used to test if the ATM exists
     */
    @Test
    public void testIfAtmExists(){
        assertTrue(MoneyCheckATM.atmExists("MoneyCheck"));
    }

    /**
     * This method is used to test if the ATM doesn't exist (negative test)
     */
    @Test
    public void testIfAtmExists2(){
        assertFalse(MoneyCheckATM.atmExists("MoneyCheck2"));
    }

    /**
     * This method is used to test the blockCard method
     */
    @Test
    public void testBlockCard() {
        User user = new User("John", "Doe", "123456789", 1234, 100.0, FunctionType.User, "MoneyCheck", "john.doe@example.com", false);
        MainATM.blockCard(user);
        assertTrue(user.isCard_blocked());
    }

    /**
     * This method is used to test the unblockCard method
     */
    @Test
    public void testUnblockCard() {
        User user = new User("John", "Doe", "123456789", 1234, 100.0, FunctionType.User, "MoneyCheck", "john.doe@example.com", true);
        MainATM.unblockCard(user);
        assertFalse(user.isCard_blocked());
    }

    /**
     * This method is used to test the randomIban method
     */
    @Test
    public void testRandomIban(){
        String iban = MainATM.randomIban();
        assertEquals(24, iban.length());
    }

    /**
     * This method is used to test the randomPinCode method
     */
    @Test
    public void testRandomPinCode(){
        int pin_code = MainATM.randomPinCode();
        assertTrue(pin_code>=0 && pin_code<=9999);
    }

    /**
     * This method is used to test the generateEmail method
     */
    @Test
    public void testGenerateEmail() {
        String email = MainATM.generateEmail("John", "Doe");
        assertEquals("john.doe@atm.com", email);
    }

    /**
     * This method is used to test the addAdminToDatabase method
     */
    @Test
    public void testAddAdminToDatabase() {
        Admin admin = new Admin("John", "Doe", "john.doe@admin.com", FunctionType.Admin);
        Admin.addAdminToDatabase(admin);
    }

    /**
     * This method is used to test the removeAdminFromDatabase method
     */
    @Test
    public void testRemoveAdminFromDatabase() {
        Admin admin = new Admin("John", "Doe", "john.doe@admin.com", FunctionType.Admin);
        Admin.removeAdminFromDatabase(admin);
    }

    /**
     * This method is used to test the generateAdminEmail method
     */
    @Test
    public void testGenerateAdminEmail() {
        String email = MainATM.generateAdminEmail("John", "Doe");
        assertEquals("john.doe@admin.com", email);
    }

    /**
     * This method is used to test the addAtmToDatabase method
     */
    @Test
    public void testAddAtmToDatabase() {
        MoneyCheckATM atm = new MoneyCheckATM("test", 1000.0, new ArrayList<>(), new Admin("John", "Doe", "john.doe@admin.com", FunctionType.Admin));
        MoneyCheckATM.addAtmToDatabase(atm);
    }

    /**
     * This method is used to test the removeAtmFromDatabase method
     */
    @Test
    public void testRemoveAtmFromDatabase() {
        MoneyCheckATM atm = new MoneyCheckATM("test", 1000.0, new ArrayList<>(), new Admin("John", "Doe", "john.doe@admin.com", FunctionType.Admin));
        MoneyCheckATM.removeAtmFromDatabase(atm);
    }


}
