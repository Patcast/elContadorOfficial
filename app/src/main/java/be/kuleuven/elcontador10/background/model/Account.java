package be.kuleuven.elcontador10.background.model;

public class Account {
    private int balance;
    private String name;
    private String id;

    public Account() {
    }

    public Account(int balance, String name) {
        this.balance = balance;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
