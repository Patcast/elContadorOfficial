package be.kuleuven.elcontador10.background.model;



import java.util.HashMap;

public class User {
    String name;
    String email;
    HashMap<String,String> accounts;

    public User() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, String> getAccounts() {
        return accounts;
    }

}
