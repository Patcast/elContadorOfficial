package be.kuleuven.elcontador10.background.model.contract;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import be.kuleuven.elcontador10.background.database.Caching;

public class Contract {

    // local variables
    private String id;
    private String title;
    private String microAccount;
    private String registeredBy;
    private Timestamp registerDate;
    private String notes;
    private String propertyID;
    private ArrayList<SubContract> subContracts;

    // firebase
    private static final String TAG = "contract";

    // TODO implement propertyID
    public Contract(String title, String registeredBy, String notes) {
        this.title = title;
        this.registeredBy = registeredBy;
        this.registerDate = new Timestamp(new Date()); // now
        this.notes = notes;
        this.propertyID = null;
    }

    public Contract() {}

    // database
    public static void newContract(Contract contract) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + contract.getMicroAccount() + "/contracts";

        db.collection(url)
                .add(contract)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static void editContract(Contract contract) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + contract.getMicroAccount() + "/contracts/" +
                contract.getId();

        db.document(url)
                .set(contract)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot edited with ID: " + contract.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error editing document", e));
    }

    public static void deleteContract(Contract contract) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + contract.getMicroAccount() + "/contracts/" +
                contract.getId();

        db.document(url)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
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
    public ArrayList<SubContract> getSubContracts() {
        return subContracts;
    }

    public void setSubContracts(ArrayList<SubContract> subContracts) {
        this.subContracts = subContracts;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    // functions
    @RequiresApi(api = Build.VERSION_CODES.N)
    public SubContract getSubContractFromId(String id) {
        Optional<SubContract> subContract = subContracts.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();

        return subContract.orElse(null);
    }
}
