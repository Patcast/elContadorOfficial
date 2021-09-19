package be.kuleuven.elcontador10.background.model.contract;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.background.database.Caching;

public class ScheduledTransaction {
    private String id;
    private long totalAmount;
    private long amountPaid;
    private Timestamp dueDate;
    private String idOfStakeholder;
    private String title;

    private static final String TAG = "scheduledTransaction";

    public ScheduledTransaction(long totalAmount, long amountPaid, Timestamp dueDate, String idOfStakeholder) {
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.dueDate = dueDate;
        this.idOfStakeholder = idOfStakeholder;
    }

    // for Firebase
    public ScheduledTransaction() {}

    // database
    // TODO database functions
    public static void newScheduledTransaction(ScheduledTransaction transaction, String contractId, String subContractId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + Caching.INSTANCE.getChosenMicroAccountId() +
                "/contracts/" + contractId + "/subcontracts/" + subContractId + "/scheduledTransactions";

        db.collection(url)
                .add(transaction)
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

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(long amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public String getIdOfStakeholder() {
        return idOfStakeholder;
    }

    public void setIdOfStakeholder(String idOfStakeholder) {
        this.idOfStakeholder = idOfStakeholder;
    }

    @Exclude
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
