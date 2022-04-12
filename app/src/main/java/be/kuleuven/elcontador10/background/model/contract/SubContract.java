package be.kuleuven.elcontador10.background.model.contract;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

import be.kuleuven.elcontador10.background.database.Caching;

public class SubContract {
    private String id;
    private String title;
    private long amount;
    private Timestamp startDate;
    private Timestamp endDate;
    private String registeredBy;

    private static final String TAG = "subContract";

    /**
     * @param title title of payment
     * @param amount amount of payment
     * @param startDate start date for subcontract
     * @param endDate end date for subcontract
     * @param registeredBy email of account
     */
    public SubContract(String title, long amount, Timestamp startDate, Timestamp endDate, String registeredBy) {
        this.title = title;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registeredBy = registeredBy;
    }

    public SubContract() {}

    // database functions

    public static String newSubContract(SubContract subContract, String contractId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + Caching.INSTANCE.getChosenMicroAccountId() +
                "/contracts/" + contractId + "/subcontracts";

        DocumentReference doc = db.collection(url).document();

        doc.set(subContract);

        return doc.getId();
    }

    // TODO add psv edit/delete

    // setters & getters

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

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }
}
