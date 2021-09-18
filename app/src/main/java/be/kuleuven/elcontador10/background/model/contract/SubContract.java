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
    private String notes;
    private String registeredBy;

    private static final String TAG = "subContract";

    /**
     * @param title title of payment
     * @param amount amount of payment
     * @param startDate start date for subcontract
     * @param endDate end date for subcontract
     * @param notes notes of the subcontract
     * @param registeredBy email of account
     */
    public SubContract(String title, long amount, Timestamp startDate, Timestamp endDate, String notes, String registeredBy) {
        this.title = title;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        this.registeredBy = registeredBy;
    }

    public SubContract() {}

    public static String newSubContract(SubContract subContract, String contractId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + Caching.INSTANCE.getChosenMicroAccountId() +
                "/contracts/" + contractId + "/subcontracts";

        DocumentReference doc = db.collection(url).document();

        doc.set(subContract);

        return doc.getId();
    }

    // TODO add psv edit/delete

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
