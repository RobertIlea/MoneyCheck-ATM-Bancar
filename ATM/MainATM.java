package ATM;

public class MainATM {
    public static void main(String [] args){
      //  User user = new User("Marinel", "Dorel", "RO12312442122222", 1234,132132);
        //User.addUser(user);
        /*
        User getUser = User.getUserDataByIban("RO123456789123456789");
        if(getUser != null){
            User.removeUser(getUser);
        } else {
            System.out.println("User not found");
        }
         */
        User u = User.getUserDataByIban("RO12312442122222");
        int money = 500;
        //User.addBalance(u, money);
        User u1 = User.getUserDataByIban("RO123124421");

        int quantity = 4000;
        User.addBalance(u1,quantity);
        User.withdrawBalance(u1,money);

        //int new_pin1 = 2123;
        //User.changePIN(u,new_pin1);

       // int quantity = 100;
        //User.transferBetweenUsers(u1,u,quantity);

    }
}
