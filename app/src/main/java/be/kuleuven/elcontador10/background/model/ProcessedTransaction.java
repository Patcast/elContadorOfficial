package be.kuleuven.elcontador10.background.model;

import android.content.Context;

import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;


import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;

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
    private boolean isDeleted;

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
        isDeleted = false;
    }

    public void updateImageFromFireBase(ProcessedTransaction newTransactionInput,ImageFireBase ImageSelected,Context context) {
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";
        Map<String, Object> data = new HashMap<>();
        data.put("imageName",ImageSelected.getNameOfImage());
        db.collection(urlNewTransactions).document(newTransactionInput.id)
                .set(data, SetOptions.merge());

        StringBuilder saveUrl = new StringBuilder();
        saveUrl.append(Caching.INSTANCE.getChosenAccountId());
        saveUrl.append("/");
        saveUrl.append(ImageSelected.getNameOfImage());
        StorageReference image = storageReference.child(saveUrl.toString());
        image.putFile(ImageSelected.getContentUri()).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(context,"photo added", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, context.getString(R.string.Transaction_upload_failed), Toast.LENGTH_SHORT).show();
            deleteImageFromFireBase(false,context);
            Toast.makeText(context,"photo failed to upload, please try again", Toast.LENGTH_SHORT).show();
        });
    }

    public void deleteImageFromFireBase(Boolean storedSuccessfully,Context context){
        // remove ImageName from transaction
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";
        Map<String, Object> data = new HashMap<>();
        data.put("imageName","");
        db.collection(urlNewTransactions).document(getIdOfTransactionInt())
                .set(data, SetOptions.merge());
        if(storedSuccessfully) {
            // remove ImageName file from storage
            StringBuilder deleteUrl = new StringBuilder();
            deleteUrl.append(Caching.INSTANCE.getChosenAccountId());
            deleteUrl.append("/");
            deleteUrl.append(getImageName());
            StorageReference desertRef = storageReference.child(deleteUrl.toString());
            desertRef.delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(context,"photo deleted", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(exception -> {
                Toast.makeText(context,"photo failed to delete, please try again", Toast.LENGTH_SHORT).show();
            });
        }
    }
    //Todo: Maybe use similar method to update after the transaction was created.
    public void uploadImageToFireBase(ProcessedTransaction newTransactionInput,ImageFireBase ImageSelected,Context context) {
        StringBuilder saveUrl = new StringBuilder();
        saveUrl.append(Caching.INSTANCE.getChosenAccountId());
        saveUrl.append("/");
        saveUrl.append(ImageSelected.getNameOfImage());
        StorageReference image = storageReference.child(saveUrl.toString());
        image.putFile(ImageSelected.getContentUri()).addOnSuccessListener(taskSnapshot -> {
            if (newTransactionInput!=null) newTransactionInput.sendTransaction(newTransactionInput,context);
            else Toast.makeText(context,"photo added", Toast.LENGTH_SHORT).show();
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
    public void deleteTransaction(Context context){
        /*
        db.collection(urlNewTransactions).document(getIdOfTransactionInt())
                .delete()
                .addOnSuccessListener(aVoid ->{
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    Toast.makeText(context, "Transaction deleted", Toast.LENGTH_LONG).show();

                } )
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));*/
        isDeleted = true;
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";
        db.collection(urlNewTransactions).document(getIdOfTransactionInt())
                .update("isDeleted", true)
                .addOnSuccessListener(aVoid -> successfulDelete(context))
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete transaction.", Toast.LENGTH_SHORT).show());
    }
    public void successfulDelete(Context context){
        Toast.makeText(context, "Transaction deleted.", Toast.LENGTH_SHORT).show();
        //delete picture too here
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
        if(getIsDeleted())return R.color.rec_view_scheduled_completed;
        else{
            int colorPositive = R.color.transaction_processed_positive;
            int colorNegative = R.color.transaction_processed_negative;
            return (totalAmount<0)? colorNegative : colorPositive;
        }

    }

    @Exclude
    @Override
    public String getIdOfTransactionInt() {
        return id;
    }

    @Exclude
    @Override
    public String getAmountToDisplay(){
        NumberFormatter formatter = new NumberFormatter(getTotalAmount());
        return formatter.getFinalNumber();
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }


}



