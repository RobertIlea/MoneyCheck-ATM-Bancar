package ATM;

public class MainATM {
    public static void main(String [] args){
        User user = new User("Marinel", "Dorel", "RO12312442122222", 1234,132132);
        //User.addUser(user);
        User getUser = User.getUserDataByIban("RO123456789123456789");
        if(getUser != null){
            User.removeUser(getUser);
        } else {
            System.out.println("User not found");
        }

    }
}
