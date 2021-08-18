package be.kuleuven.elcontador10.background.model.contract;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Date;

import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;

public class Payment {
    private String id;
    private String title;
    private long amount;
    private Timestamp start;
    private Timestamp end;
    private int frequency;
    private String notes;
    private String registeredBy;

    private static final String TAG = "payment";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Payment(String title, long amount, Timestamp start, Timestamp end, int frequency, String notes, String registeredBy) {
        this.title = title;
        this.amount = amount;
        this.start = start;
        this.end = end;
        this.frequency = frequency;
        this.notes = notes;
        this.registeredBy = registeredBy;
    }

    public Payment() {}

    public static void newPayment(Payment payment, String contractId, String microAccountId) {
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + microAccountId +
                "/contracts/" + contractId + "/payments";

        db.collection(url)
                .add(payment)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }
}
