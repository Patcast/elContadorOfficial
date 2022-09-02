package be.kuleuven.elcontador10.background.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class Account {
    private static final String TAG = "account";
    private long cash;
    private long sumOfReceivables;
    private long sumOfPayables;
    private long equity;
    private String name;
    private ArrayList<String> users;
    private String id;
    private String owner;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Account( String name,long cash, ArrayList<String> users , String owner) {
        this.cash = cash;
        this.equity = cash;
        this.sumOfReceivables= 0;
        sumOfPayables = 0;
        this.name = name;
        this.users = users;
        this.owner = owner;
    }

    public Account() {
    }

    public void sendNewAccount(Account newAccount, Context context){
        db.collection("accounts")
                .add(newAccount)
                .addOnSuccessListener(doc -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + doc.getId());
                    Toast.makeText(context,"Account registered",Toast.LENGTH_SHORT).show();
                    BalanceRecord record = new BalanceRecord(cash,new Timestamp(new Date()));
                    record.sendNewRecord(record, doc.getId());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public String getOwner() {
        return owner;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCash() {
        return cash;
    }

    public String getName() {
        return name;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public long getSumOfReceivables() {
        return sumOfReceivables;
    }

    public long getSumOfPayables() {
        return sumOfPayables;
    }

    public long getEquity() {
        return equity;
    }
}
