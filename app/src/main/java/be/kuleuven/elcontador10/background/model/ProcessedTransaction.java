package be.kuleuven.elcontador10.background.model;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private String stakeHolder;
    private String id;
    private String category;
    private Timestamp date;
    private String registeredBy;
    private String notes;
    private String imageName;
    private final int colorPositive = R.color.transaction_processed_positive;
    private final int colorNegative = R.color.transaction_processed_negative;

    public ProcessedTransaction() {
    }

    public ProcessedTransaction(String title, int amount, String registeredBy, String stakeHolder, String category, String notes, String imageName) {
        this.title = title;
        this.totalAmount = amount;
        this.registeredBy = registeredBy;
        this.stakeHolder = stakeHolder;
        this.category = category;
        this.date = new Timestamp(new Date());
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
        db.collection(urlNewTransactions).document(getId())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
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

    public String getNotes() {
        return notes;
    }
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getImageName() {
        return imageName;
    }


    @Override
    public int getColorPositiveInt() {
        return colorPositive;
    }

    @Override
    public int getColorNegativeInt() {
        return colorNegative;
    }

    @Override
    public int getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String getIdOfStakeInt() {
        return stakeHolder;
    }

    @Override
    public String getIdOfTransactionInt() {
        return id;
    }

    @Override
    public String getIdOfCategoryInt() {
        return category;
    }

    @Override
    public Timestamp getDate() {
        return date;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getImageInt() {
        return imageName;
    }

}



