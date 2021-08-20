package be.kuleuven.elcontador10.background.model.contract;


import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.background.database.Caching;

public class Payment {
    private String id;
    private String title;
    private long amount;
    private Timestamp nextPaymentDate;
    private int paymentsLeft;
    private String period;
    private String frequency;
    private String notes;
    private String registeredBy;

    private static final String TAG = "payment";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     *
      * @param title title of payment
     * @param amount amount of payment
     * @param nextPaymentDate next date when balance changes
     * @param paymentsLeft how many payments left
     * @param period start date - end date
     * @param frequency how many times the payment repeats (value-unit)
     *                  unit: 0 - days, 1 - weeks, 2 - months, 3 - quarters, 4 - years
     *                  without unit: 1 - daily, 2 - weekly, 3 - monthly, 4 - quarterly, 5 - yearly
     * @param notes notes
     * @param registeredBy email of account registered
     */
    public Payment(String title, long amount, Timestamp nextPaymentDate, int paymentsLeft, String period, String frequency, String notes, String registeredBy) {
        this.title = title;
        this.amount = amount;
        this.nextPaymentDate = nextPaymentDate;
        this.paymentsLeft = paymentsLeft;
        this.period = period;
        this.frequency = frequency;
        this.notes = notes;
        this.registeredBy = registeredBy;
    }

    public Payment() {}

    public static void newPayment(Payment payment, String contractId) {
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + Caching.INSTANCE.getChosenMicroAccountId() +
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

    public Timestamp getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(Timestamp nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public int getPaymentsLeft() {
        return paymentsLeft;
    }

    public void setPaymentsLeft(int paymentsLeft) {
        this.paymentsLeft = paymentsLeft;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     *
     * @return how many times the payment repeats (value-unit)
     *         unit: 0 - days, 1 - weeks, 2 - months, 3 - quarters, 4 - years
     *         without unit: 1 - daily, 2 - weekly, 3 - monthly, 4 - quarterly, 5 - yearly
     */
    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
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
