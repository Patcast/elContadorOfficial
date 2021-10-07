package be.kuleuven.elcontador10.background.model.contract;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;

public class ScheduledTransaction implements TransactionInterface {
    private String id;
    private long totalAmount;
    private long amountPaid;
    private Timestamp dueDate;
    private String idOfStakeholder;
    private String title;
    private String category;
    private String idOfAccount;

    private static final String TAG = "scheduledTransaction";

    public ScheduledTransaction(long totalAmount, long amountPaid, Timestamp dueDate, String idOfStakeholder) {
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.dueDate = dueDate;
        this.idOfStakeholder = idOfStakeholder;
        this.idOfAccount = Caching.INSTANCE.getChosenAccountId();
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

    public static void updateScheduledTransaction(ScheduledTransaction transaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + Caching.INSTANCE.getChosenMicroAccountId() +
                "/contracts/" + Caching.INSTANCE.getChosenContract().getId() + "/subcontracts/" + Caching.INSTANCE.getChosenSubContract().getId() +
                "/scheduledTransactions/" + transaction.getId();

        db.document(url)
                .set(transaction)
                .addOnSuccessListener(unused -> Log.d(TAG, "Document updated: " + transaction.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public String getIdOfStakeholder() {
        return idOfStakeholder;
    }

    public void setIdOfStakeholder(String idOfStakeholder) {
        this.idOfStakeholder = idOfStakeholder;
    }

    @Override
    public int getColorInt() {
        return 0;
    }

    @Override
    public int getTotalAmount() {
        return (int)totalAmount;
    }

    @Override
    public String getIdOfStakeInt() {
        return idOfStakeholder;
    }

    @Override
    public String getIdOfTransactionInt() {
        return id;
    }

    @Override
    public String getIdOfCategoryInt() {
        return category;
    }

    @Override
    public Timestamp getDueDate() {
        return dueDate;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getImageName() {
        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIdOfAccount() {
        return idOfAccount;
    }

    public void setIdOfAccount(String idOfAccount) {
        this.idOfAccount = idOfAccount;
    }

    public void pay(long amount) {
        amountPaid += amount;
    }
}
