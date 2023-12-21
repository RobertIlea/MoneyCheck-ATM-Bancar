package ATM;

public class User {
    private int id_User;
    private String first_name;
    private String last_name;
    private String IBAN;
    private int pin_code;

    public User(){}
    public User(int id_User, String first_name, String last_name, String IBAN, int pin_code){
        this.id_User=id_User;
        this.first_name=first_name;
        this.last_name=last_name;
        this.IBAN=IBAN;
        this.pin_code=pin_code;
    }
    public int getId_User(){
        return id_User;
    }
    public String getFirst_name(){
        return first_name;
    }
    public String getLast_name(){
        return last_name;
    }
    public String getIBAN(){
        return IBAN;
    }
    public int getPin_code(){
        return pin_code;
    }

    public static void addUser(User user){
        String url = "jdbc:sqlite:A:\MoneyCheck - ATM Bancar\MoneyCheck-ATM-Bancar\identifier.sqlite"
    }
    @Override
    public String toString(){
        StringBuilder out = new StringBuilder();
        out.append("User data:\n").append("id: ").append(getId_User());
        out.append("\nFirst name: ").append(getFirst_name());
        out.append("\nLast name: ").append(getLast_name());
        out.append("\nIBAN: ").append(getIBAN());
        out.append("\nPin code: ").append(getPin_code());

        return out.toString();
    }

}
