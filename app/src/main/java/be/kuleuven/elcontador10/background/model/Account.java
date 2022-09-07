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
    private String name;
    private ArrayList<String> users;
    private String id;
    private String owner;

    private long equity;
    private long cash;
    private long sumOfPayables;
    private long sumOfReceivables;
    private long equityPending;
    private long sumOfPayablesPending;
    private long sumOfReceivablesPending;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Account( String name,long cash, ArrayList<String> users , String owner) {
        this.cash = cash;
        this.equity = cash;
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

    public long getEquity() {
        return equity;
    }

    public long getSumOfPayables() {
        return sumOfPayables;
    }

    public long getSumOfReceivables() {
        return sumOfReceivables;
    }

    public long getEquityPending() {
        return equityPending;
    }

    public long getSumOfPayablesPending() {
        return sumOfPayablesPending;
    }

    public long getSumOfReceivablesPending() {
        return sumOfReceivablesPending;
    }
    public void setEquityPayablesAndReceivables(long sumOfPayables,long sumOfReceivables,  long equity){
        this.equity =equity;
        this.sumOfReceivables = sumOfReceivables;
        this.sumOfPayables = sumOfPayables;
    }
}
