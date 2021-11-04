package be.kuleuven.elcontador10.background.model.contract;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.R;
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
    private String path;
    private int color;
    private ScheduledTransactionStatus status;

    public enum ScheduledTransactionStatus {
        LATE, FUTURE, COMPLETED
    }

    private static final String TAG = "scheduledTransaction";

    public ScheduledTransaction(long totalAmount, long amountPaid, Timestamp dueDate, String idOfStakeholder) {
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.dueDate = dueDate;
        this.idOfStakeholder = idOfStakeholder;
        this.idOfAccount = Caching.INSTANCE.getChosenAccountId();

        setColor();
    }

    // for Firebase

    public ScheduledTransaction() {}
    // database

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

        String url = transaction.getPath();

        db.document(url)
                .set(transaction)
                .addOnSuccessListener(unused -> Log.d(TAG, "Document updated: " + transaction.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    // setters and getters

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

    @Exclude
    @Override
    public int getColorInt() {
        return color;
    }

    public void setColor() {
        if (amountPaid == totalAmount) { // completed
            status = ScheduledTransactionStatus.COMPLETED;
            color = R.color.rec_view_scheduled_completed;
        } else if (dueDate.getSeconds() > Timestamp.now().getSeconds()) { // future
            status = ScheduledTransactionStatus.FUTURE;
            color = R.color.rec_view_scheduled_future;
        } else { // late
            status = ScheduledTransactionStatus.LATE;
            color = R.color.rec_view_scheduled_late;
        }
    }

    @Exclude
    public ScheduledTransactionStatus getStatus() {
        return status;
    }

    @Override
    public int getTotalAmount() {
        return (int)totalAmount;
    }

    @Exclude
    @Override
    public String getIdOfStakeInt() {
        return idOfStakeholder;
    }

    @Exclude
    @Override
    public String getIdOfTransactionInt() {
        return id;
    }

    @Exclude
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

    // for updating data
    @Exclude
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
