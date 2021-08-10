package be.kuleuven.elcontador10.background.model;



import java.util.HashMap;

public class LoggedUser {
    String name;
    String email;
    HashMap<String,String> accounts;

    public LoggedUser() {
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


    @Override
    public String toString() {
        return "LoggedUser{" +
                "Name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
