package be.kuleuven.elcontador10.background.model.contract;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

import be.kuleuven.elcontador10.background.database.Caching;

public class Contract {

    // local variables
    private String id;
    private String title;
    private String microAccount;
    private String registeredBy;
    private Timestamp registerDate;
    private String notes;
    private ArrayList<Payment> payments;

    // firebase
    private static final String TAG = "contract";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Contract(String title, String registeredBy, String notes) {
        this.title = title;
        this.registeredBy = registeredBy;
        this.registerDate = new Timestamp(new Date()); // now
        this.notes = notes;
    }

    public Contract() {}

    // database
    public static void newContract(Contract contract) {
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + contract.getMicroAccount() + "/contracts";

        db.collection(url)
                .add(contract)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static void editContract(Contract contract) {
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + contract.getMicroAccount() + "/contracts/" +
                contract.getId();

        db.document(url)
                .set(contract)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot edited with ID: " + contract.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error editing document", e));
    }


    // setters and getters
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

    @Exclude
    public String getMicroAccount() {
        return microAccount;
    }

    public void setMicroAccount(String microAccount) {
        this.microAccount = microAccount;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public Timestamp getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Timestamp registerDate) {
        this.registerDate = registerDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Exclude
    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }
}
