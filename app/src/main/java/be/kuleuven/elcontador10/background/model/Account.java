package be.kuleuven.elcontador10.background.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import be.kuleuven.elcontador10.background.database.Caching;

public class Account {
    private static final String TAG = "account";
    private long balance;
    private String name;
    private ArrayList<String> users;
    private String id;
    private String owner;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Account( String name,long balance, ArrayList<String> users , String owner) {
        this.balance = balance;
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
                    BalanceRecord record = new BalanceRecord(balance,new Timestamp(new Date()));
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

    public long getBalance() {
        return balance;
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

}
