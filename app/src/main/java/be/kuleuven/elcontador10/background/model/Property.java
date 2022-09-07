package be.kuleuven.elcontador10.background.model;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.background.database.Caching;

public class Property {
    private static final String TAG = "Add property fragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    private long equity;
    private long cash;
    private long sumOfPayables;
    private long sumOfReceivables;
    private long equityPending;
    private long sumOfPayablesPending;
    private long sumOfReceivablesPending;


    private String name;
    private String id;

    public Property(String name) {
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



    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getEquity() {
        return equity;
    }

    public long getCash() {
        return cash;
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
    public long getSummary(){
        return (sumOfReceivables-sumOfPayables);
    }
}
