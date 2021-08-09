package be.kuleuven.elcontador10.background.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.type.DateTime;

import java.util.Date;

import be.kuleuven.elcontador10.background.database.Caching;

public class Transaction {
    private static final String TAG = "newTransaction";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String urlForCollectionTransactions;
    private int amount;
    private String stakeHolder;
    private String id;
    private String category;
    private String subCategory;
    private Timestamp date;
    private String registeredBy;
    private String notes;
    private boolean deleted;

    public Transaction() {
    }

    public Transaction( int amount, String registeredBy, String stakeHolder, String category, String subCategory, String notes) {

        this.amount = amount;
        this.registeredBy = registeredBy;
        this.stakeHolder = stakeHolder;
        this.category = category;
        this.subCategory = subCategory;
        this.date = new Timestamp(new Date());
        this.deleted = false;
        this.notes = notes;
    }
    public void SendTransaction(Transaction newTrans){
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";

        db.collection(urlNewTransactions)
                .add(newTrans)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
    public void deleteTransaction(){
        db.collection(urlForCollectionTransactions).document(getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }




    public int getAmount() {
        return amount;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public String getStakeHolder() {
        return stakeHolder;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public Timestamp getDate() {
        return date;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getNotes() {
        return notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}



