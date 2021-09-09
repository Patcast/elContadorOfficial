package be.kuleuven.elcontador10.background.model.contract;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class ScheduledTransaction {
    private String id;
    private long totalAmount;
    private long amountPaid;
    private Timestamp dueDate;
    private String idOfStakeholder;

    public ScheduledTransaction(String id, long totalAmount, long amountPaid, Timestamp dueDate, String idOfStakeholder) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.dueDate = dueDate;
        this.idOfStakeholder = idOfStakeholder;
    }

    // for Firebase
    public ScheduledTransaction() {}

    // database
    // TODO database functions

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
}
