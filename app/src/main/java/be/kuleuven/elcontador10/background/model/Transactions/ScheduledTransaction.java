package be.kuleuven.elcontador10.background.model.Transactions;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.background.database.Caching;

public class ScheduledTransaction extends Transaction{


    private long amountPaid;



    private static final String TAG = "scheduledTransaction";

    public ScheduledTransaction(long totalAmount, long amountPaid, Timestamp dueDate, String idOfStakeholder) {
        super("title",(int)amountPaid,idOfStakeholder,"category",dueDate);
        this.amountPaid = amountPaid;

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

    public long getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(long amountPaid) {
        this.amountPaid = amountPaid;
    }


    public void setIdOfStakeholder(String idOfStakeholder) {
        super.setIdOfStakeholder(idOfStakeholder);
    }


}
