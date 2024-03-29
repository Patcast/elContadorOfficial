package be.kuleuven.elcontador10.background.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.FirestoreGrpc;

import be.kuleuven.elcontador10.background.database.Caching;

// TODO Combine with StakeHolder
// TODO rename all microaccount to StakeHolder

public class MicroAccount {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "microAccount";

    private String name;
    private String role;
    private long balance;

    public MicroAccount(String name, String role) {
        this.name = name;
        this.role = role;
        this.balance = 0;
    }

    public void addAccount(MicroAccount account) {
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders";

        db.collection(url)
                .add(account)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
