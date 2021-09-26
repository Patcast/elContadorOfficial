package be.kuleuven.elcontador10.background.model.Transactions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Date;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ImageFireBase;

public class ProcessedTransaction extends  Transaction {
    private static final String TAG = "newTransaction";
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private String registeredBy;
    private String notes;
    private String imageName;

    public ProcessedTransaction() {
        super();
    }

    public ProcessedTransaction(String title, int amount, String registeredBy, String stakeHolder, String category, String notes, String imageName) {
        super(title,amount,stakeHolder,category);
        this.registeredBy = registeredBy;
        this.notes = notes;
        this.imageName = imageName;
        super.setColor(R.color.processed_transactions);
    }


    public void uploadImageToFireBase(ProcessedTransaction newTransactionInput, ImageFireBase ImageSelected, Context context) {
        StringBuilder saveUrl = new StringBuilder();
        saveUrl.append(Caching.INSTANCE.getChosenAccountId());
        saveUrl.append("/");
        saveUrl.append(ImageSelected.getNameOfImage());
        StorageReference image = storageReference.child(saveUrl.toString());
        image.putFile(ImageSelected.getContentUri()).addOnSuccessListener(taskSnapshot -> {
            newTransactionInput.sendTransaction(newTransactionInput,context);
        }).addOnFailureListener(e -> {
            Toast.makeText(context, context.getString(R.string.Transaction_upload_failed), Toast.LENGTH_SHORT).show();
        });
    }
    public void sendTransaction(ProcessedTransaction newTrans,Context context){
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";

        super.getDb().collection(urlNewTransactions)
                .add(newTrans)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("id",documentReference.getId());
                })
                .addOnFailureListener(e -> Toast.makeText(context, context.getString(R.string.Transaction_upload_failed), Toast.LENGTH_SHORT).show());
    }
    public void deleteTransaction(){
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";
        super.getDb().collection(urlNewTransactions).document(super.getIdOfTransaction())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }


    public String getShortDate(){
        String [] bitsOfDate = super.getDate().toDate().toString().split(" ");
        StringBuilder shortDate = new StringBuilder();
        shortDate.append(bitsOfDate[2]);
        shortDate.append(" ");
        shortDate.append(bitsOfDate[1]);
        return shortDate.toString();
    }


    public String getRegisteredBy() {
        return registeredBy;
    }
    public String getNotes() {
        return notes;
    }

    public String getImageName() {
        return imageName;
    }


}



