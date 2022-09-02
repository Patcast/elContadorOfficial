package be.kuleuven.elcontador10.background.model;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.background.database.Caching;

public class Property {
    private static final String TAG = "Add property fragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private long balance;
    private long pendingBalance;
    private String name;
    private String id;

    public Property(String name) {
        this.balance = 0;
        this.pendingBalance = 0;
        this.name = name;
    }

    public Property() {
    }
    public void addProperty(Property newProperty) {
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/properties";

        db.collection(url)
                .add(newProperty)
                .addOnSuccessListener(documentReference -> documentReference.update("id",documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding property document", e));
    }

    public long getBalance() {
        return balance;
    }

    public long getPendingBalance() {
        return pendingBalance;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
