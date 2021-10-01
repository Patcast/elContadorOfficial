package be.kuleuven.elcontador10.background.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import be.kuleuven.elcontador10.background.database.Caching;

public class Account {
    private static final String TAG = "account";
    private long balance;
    private String name;
    private ArrayList<String> users;
    private String id;
    private Map<String,Integer> mapOfStaringBalances;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Account( String name,long balance, ArrayList<String> users) {
        this.balance = balance;
        this.name = name;
        this.users = users;
    }

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

    public void sendNewAccount(Account newAccount, Context context){
        db.collection("accounts")
                .add(newAccount)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(context,"Account registered",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
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

    public ArrayList<String> getUsers() {
        return users;
    }

    public Map<String, Integer> getMapOfStaringBalances() {
        return mapOfStaringBalances;
    }
}
