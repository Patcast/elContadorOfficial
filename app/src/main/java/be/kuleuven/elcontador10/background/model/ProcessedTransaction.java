package be.kuleuven.elcontador10.background.model;

import android.content.Context;


import android.widget.Toast;

import com.google.firebase.Timestamp;


import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;

public class ProcessedTransaction implements TransactionInterface {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private String title;
    private int totalAmount;
    private String idOfStakeInt;
    private String id;
    private String idOfCategoryInt;
    private String idOfProperty;
    private Timestamp dueDate;
    private String registeredBy;
    private String notes;
    private String imageName;
    private boolean isDeleted;
    private List <String>type = new ArrayList<>();
    private int collectionIndex;
    private int collectionSize;


    public ProcessedTransaction() {
    }
    public ProcessedTransaction(Timestamp dueDate,int collectionIndex) {
        this.dueDate=dueDate;
        this.collectionIndex = collectionIndex;
    }


    public ProcessedTransaction(String title, int amount, String registeredBy, String idOfStakeInt, String idOfCategoryInt, String notes, String imageName,List <String> type_input , int collectionIndex, int collectionSize, String idOfProperty) {
        this.title = title;
        this.totalAmount = amount;
        this.registeredBy = registeredBy;
        this.idOfStakeInt = idOfStakeInt;
        this.idOfCategoryInt = idOfCategoryInt;
        this.idOfProperty = idOfProperty;
        this.dueDate = new Timestamp(new Date());
        this.notes = notes;
        this.imageName = imageName;
        isDeleted = false;
        this.type.addAll(type_input);
        this.collectionIndex = collectionIndex;
        this.collectionSize = collectionSize;
    }
    public  void setFutureTransactionsFields(String title, int amount, String registeredBy, String idOfStakeInt, String idOfCategoryInt, String notes, String imageName,List <String> type_input , String idOfProperty) {
        this.title = title;
        this.totalAmount = amount;
        this.registeredBy = registeredBy;
        this.idOfStakeInt = idOfStakeInt;
        this.idOfCategoryInt = idOfCategoryInt;
        this.idOfProperty = idOfProperty;
        this.notes = notes;
        this.imageName = imageName;
        isDeleted = false;
        this.type.addAll(type_input);

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
            imageName = null; // the idea is to remove all places where image name was stored.
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

        else if(getType().contains("CASH")){
            if(getTotalAmount()<0) return R.color.transaction_processed_positive;
            else return R.color.transaction_processed_negative;
        }
        else{
            if(getType().contains("RECEIVABLES"))return R.color.rec_view_receivable;
            else return R.color.rec_view_payable;
        }

    }
    public int transText(){
        if(getType().contains("CASH")){
            if(getTotalAmount()<0) return (R.string.paid_to);
            else return (R.string.paid_by);
        }
        else{
            if(getType().contains("RECEIVABLES"))return (R.string.receivable_of);
            else return(R.string.payable_of);
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

    public List <String> getType() {
        return type;
    }

    public int getCollectionIndex() {
        return collectionIndex;
    }

    public int getCollectionSize() {
        return collectionSize;
    }

    public String getIdOfProperty() {
        return idOfProperty;
    }

    public String getId() {
        return id;
    }

    public void setCollectionSize(int collectionSize) {
        this.collectionSize = collectionSize;
    }
}



