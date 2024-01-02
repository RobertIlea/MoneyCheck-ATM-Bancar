package ATM;

import java.util.ArrayList;

public class MainATM {
    /**
     * This method is used to block a card
     * @param user
     */
    public static void blockCard(User user){
        user.setCard_blocked(true);
    }

    /**
     * This method is used to unblock a card
     * @param user
     */
    public static void unblockCard(User user){
        user.setCard_blocked(false);
    }
    public static void main(String[] args) {
        MoneyCheckATM ATM = new MoneyCheckATM();
        ArrayList <User> users = new ArrayList<>();
        Admin admin = new Admin("Robert-Ioan", "Ilea", "admin@atm.ro", FunctionType.Admin);
        User user1 = new User("Marinel", "Dorel", "RO1231244212332222112", 1234, 13213332,FunctionType.User);
        User user2 = new User("Dorel", "Andrei", "RO1233121111332121", 3212, 3333333,FunctionType.User);
        users.add(user1);
        users.add(user2);
        ATM.setAtm_name("MoneyCheck");
        ATM.setAdmin(admin);
        ATM.setUsers(users);
        //MoneyCheckATM.addAtmToDatabase(ATM);
        //MoneyCheckATM.removeAtmFromDatabase(ATM);
        //Admin.addMoneyToATM(ATM,2500.20);
        User.withdrawBalance(user1,5000, ATM);
    }
}
