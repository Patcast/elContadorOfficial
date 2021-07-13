package be.kuleuven.elcontador10.background.model;

public class Account {
    private long balance;
    private String name;
    private String id;

    public Account() {
    }

    public Account(long balance, String name, String id) {
        this.balance = balance;
        this.name = name;
        this.id = id;
    }

    public Account(int balance, String name) {
        this.balance = balance;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
