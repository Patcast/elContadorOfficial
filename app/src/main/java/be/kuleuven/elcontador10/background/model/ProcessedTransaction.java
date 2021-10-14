package be.kuleuven.elcontador10.background.model;

import android.content.Context;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;


import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



import java.util.Date;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;

public class ProcessedTransaction implements TransactionInterface {
    private static final String TAG = "newTransaction";


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private String title;
    private int totalAmount;
    private String idOfStakeInt;
    private String id;
    private String idOfCategoryInt;
    private Timestamp dueDate;
    private String registeredBy;
    private String notes;
    private String imageName;

    public ProcessedTransaction() {
    }

    public ProcessedTransaction(String title, int amount, String registeredBy, String idOfStakeInt, String idOfCategoryInt, String notes, String imageName) {
        this.title = title;
        this.totalAmount = amount;
        this.registeredBy = registeredBy;
        this.idOfStakeInt = idOfStakeInt;
        this.idOfCategoryInt = idOfCategoryInt;
        this.dueDate = new Timestamp(new Date());
        this.notes = notes;
        this.imageName = imageName;
    }


    public void uploadImageToFireBase(ProcessedTransaction newTransactionInput,ImageFireBase ImageSelected,Context context) {
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

        db.collection(urlNewTransactions)
                .add(newTrans)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("id",documentReference.getId());
                })
                .addOnFailureListener(e -> Toast.makeText(context, context.getString(R.string.Transaction_upload_failed), Toast.LENGTH_SHORT).show());
    }
    public void deleteTransaction(){
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";
        db.collection(urlNewTransactions).document(getIdOfTransactionInt())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }


    public void setId(String id) {
        this.id = id;
    }



    public String getRegisteredBy() {
        return registeredBy;
    }
    public String getNotes() {
        return notes;
    }
    @Override
    public int getTotalAmount() {
        return totalAmount;
    }
    @Override
    public String getIdOfStakeInt() {
        return idOfStakeInt;
    }

    @Override
    public String getIdOfCategoryInt() {
        return idOfCategoryInt;
    }

    @Override
    public Timestamp getDueDate() {
        return dueDate;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Exclude
    @Override
    public int getColorInt() {
        int colorPositive = R.color.transaction_processed_positive;
        int colorNegative = R.color.transaction_processed_negative;
        return (totalAmount<0)? colorNegative : colorPositive;
    }
    @Exclude
    @Override
    public String getIdOfTransactionInt() {
        return id;
    }
}



