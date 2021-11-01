package be.kuleuven.elcontador10.background.model;

import android.util.Log;

import com.google.firebase.Timestamp;

import com.google.firebase.firestore.FirebaseFirestore;


public class BalanceRecord {
    private static final String TAG = "model balance record";
    private Long startingBalance;

    private Timestamp date;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public BalanceRecord() {
    }

    public BalanceRecord(Long startingBalance, Timestamp date) {
        this.startingBalance = startingBalance;
        this.date = date;
    }

    public void sendNewRecord(BalanceRecord record, String idOfNewAccount) {
        db.collection("/accounts/"+idOfNewAccount+"/balanceRecords")
                .add(record)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public long getStartingBalance() {
        return startingBalance;
    }

    public Timestamp getDate() {
        return date;
    }


}
