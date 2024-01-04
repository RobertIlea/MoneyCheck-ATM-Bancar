package ATM;

import java.util.ArrayList;

public class MainATM {
    /**
     * This method is used to block a card
     * @param user
     */
    public static void blockCard(User user){
        user.setCard_blocked(String.valueOf(true));
    }

    /**
     * This method is used to unblock a card
     * @param user
     */
    public static void unblockCard(User user){
        user.setCard_blocked(String.valueOf(false));
    }

    /**
     * This method is used to generate a random IBAN
     * @return
     */
    public static String randomIban(){
        String iban = "RO";
        for(int i=0;i<22;i++){
            int random = (int)(Math.random()*10);
            iban+=random;
        }
        return iban;
    }

    /**
     * This method is used to generate a random PIN code
     * @return
     */
    public static Integer randomPinCode(){
        int pin_code = (int)(Math.random()*10000);
        return pin_code;
    }

    /**
     * This method is used to generate an email
     * @param first_name
     * @param last_name
     * @return
     */
    public static String generateEmail(String first_name, String last_name){
        first_name = first_name.toLowerCase();
        last_name = last_name.toLowerCase();
        String email = first_name+"."+last_name+"@atm.com";
        return email;
    }
    public static void main(String[] args) {
        MoneyCheckATM ATM = new MoneyCheckATM();
        ATM.setAtm_name("MoneyCheck");
        ArrayList <User> users = new ArrayList<>();
        Admin admin = new Admin("Robert-Ioan", "Ilea", "admin@atm.ro", FunctionType.Admin);
        //User user1 = new User("Marinel", "Dorel", "RO1231244212332222112", 1234, 13213332,FunctionType.User,"MoneyCheck","dorel.marinel@yahoo.com");
        //User user2 = new User("Dorel", "Andrei", "RO1233121111332121", 3212, 3333333,FunctionType.User,"MoneyCheck","andrei.dorel@gmail.com");
        //users.add(user1);
        //users.add(user2);
        //Admin.addAdminToDatabase(admin);
        ATM.setAdmin(admin);
        ATM.setUsers(users);
        //MoneyCheckATM.addAtmToDatabase(ATM);
        //MoneyCheckATM.removeAtmFromDatabase(ATM);
        //Admin.addMoneyToATM(ATM,2500.20);
        //User.withdrawBalance(user1,5000, ATM);
        //User user3 = User.getUserDataByIban("RO9724427038429189244227");
        //User.removeUser(user3);
        for(User user : users){
            System.out.println(user);
        }
        //User user1 = User.getUserDataByIban("RO0564859812021772860988");
       // User.removeUser(user1,ATM);
    }
}
