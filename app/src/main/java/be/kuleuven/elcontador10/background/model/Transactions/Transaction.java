package be.kuleuven.elcontador10.background.model.Transactions;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;


public class Transaction {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String title;
    private int amount;
    private String idOfStakeholder;
    private String idOfTransaction;
    private String category;
    private Timestamp date;
    private int color;


    public Transaction(){}
    public Transaction(String title, int amount, String stakeHolder, String category) {
        this.title = title;
        this.amount = amount;
        this.idOfStakeholder = stakeHolder;
        this.category = category;
        this.date = new Timestamp(new Date());
    }
    public Transaction(String title, int amount, String stakeHolder, String category,Timestamp date) {
        this.title = title;
        this.amount = amount;
        this.idOfStakeholder = stakeHolder;
        this.category = category;
        this.date = date;
    }



    public FirebaseFirestore getDb() {
        return db;
    }

    public String getTitle() {
        return title;
    }

    public int getAmount() {
        return amount;
    }

    public String getIdOfStakeholder() {
        return idOfStakeholder;
    }

    public String getIdOfTransaction() {
        return idOfTransaction;
    }

    public String getCategory() {
        return category;
    }

    public Timestamp getDate() {
        return date;
    }
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setIdOfStakeholder(String idOfStakeholder) {
        this.idOfStakeholder = idOfStakeholder;
    }

    public void setIdOfTransaction(String idOfTransaction) {
        this.idOfTransaction = idOfTransaction;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
