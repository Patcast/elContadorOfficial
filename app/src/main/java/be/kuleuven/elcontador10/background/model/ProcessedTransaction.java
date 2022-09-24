package be.kuleuven.elcontador10.background.model;

import android.app.AlertDialog;
import android.content.Context;

import com.google.firebase.Timestamp;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;
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
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;

public class ProcessedTransaction {


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
    private final List <String> type = new ArrayList<>();
    private int collectionIndex;
    private int collectionSize;
    private Timestamp deletedDate;
    private String deletedBy;

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

    public void sendTransaction(ProcessedTransaction newTrans, Context context){
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";

        db.collection(urlNewTransactions)
                .add(newTrans)
                .addOnSuccessListener(documentReference -> documentReference.update("id",documentReference.getId()))
                .addOnFailureListener(e ->
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.add_transaction)
                                .setMessage(R.string.Transaction_upload_failed)
                                .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                .create()
                                .show()
                );
    }
    public void deleteTransaction(Context context, String deletedBy, String reason){
        isDeleted = true;
        deletedDate = Timestamp.now();
        notes = notes +
                ((notes.equals("")) ? "" : " \n") +
                context.getString(R.string.reason_to_delete) + " \n" +
                reason;
        this.deletedBy = deletedBy;
        String urlNewTransactions = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/transactions";
        db.collection(urlNewTransactions).document(getIdOfTransactionInt())
                .update("isDeleted", true,
                        "deletedDate", deletedDate,
                        "deletedBy", deletedBy,
                        "notes", notes)
                .addOnSuccessListener(aVoid ->
                        new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.delete_transaction))
                                .setMessage(context.getString(R.string.transaction_deleted))
                                .setPositiveButton(context.getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                                .create()
                                .show()
                )
                .addOnFailureListener(e ->
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.delete_transaction)
                                .setMessage(R.string.failed_delete_transaction)
                                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                                .create()
                                .show()
                );
    }

    public void execute(Context context) {
        if (type.contains(Caching.INSTANCE.TYPE_PENDING)) {
            type.remove(Caching.INSTANCE.TYPE_PENDING);
            String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";

            final Map<String, Object> removePending = new HashMap<>();
            removePending.put("type", FieldValue.arrayRemove(Caching.INSTANCE.TYPE_PENDING));

            db.collection(urlNewTransactions).document(getIdOfTransactionInt())
                    .update(removePending)
                    .addOnSuccessListener(e ->
                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.execute_pending_transaction)
                                    .setMessage(R.string.transaction_executed)
                                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                    .create()
                                    .show()
                    )
                    .addOnFailureListener(e ->
                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.execute_pending_transaction)
                                    .setMessage(R.string.failed_execute_transaction)
                                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                    .create()
                                    .show()
                    );
        }
    }

    public void updateImageFromFireBase(ProcessedTransaction newTransactionInput,ImageFireBase ImageSelected,Context context) {
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";

        Map<String, Object> data = new HashMap<>();
        data.put("imageName",ImageSelected.getNameOfImage());

        db.collection(urlNewTransactions).document(newTransactionInput.id)
                .set(data, SetOptions.merge());

        String saveUrl = Caching.INSTANCE.getChosenAccountId() +
                "/" +
                ImageSelected.getNameOfImage();

        storageReference.child(saveUrl)
                .putFile(ImageSelected.getContentUri())
                .addOnSuccessListener(taskSnapshot ->
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.photo_added)
                                .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                .create()
                                .show())
                .addOnFailureListener(e -> {
                        new AlertDialog.Builder(context)
                                .setTitle(context.getString(R.string.add_transaction))
                                .setMessage(context.getString(R.string.Transaction_upload_failed))
                                .setPositiveButton(context.getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                        deleteImageFromFireBase(false,context);
                });
    }

    public void deleteImageFromFireBase(Boolean storedSuccessfully,Context context){
        // remove ImageName from transaction
        String urlNewTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";
        Map<String, Object> data = new HashMap<>();
        data.put("imageName","");
        db.collection(urlNewTransactions).document(getIdOfTransactionInt())
                .set(data, SetOptions.merge());
        if (storedSuccessfully) {
            // remove ImageName file from storage
            String deleteUrl = Caching.INSTANCE.getChosenAccountId() +
                    "/" +
                    getImageName();
            storageReference.child(deleteUrl)
                    .delete()
                    .addOnFailureListener(exception ->
                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.delete_photo_title)
                                    .setMessage(R.string.photo_delete_fail)
                                    .setPositiveButton(context.getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                                    .create()
                                    .show()
                    );
            imageName = null; // the idea is to remove all places where image name was stored.
        }
    }
    //Todo: Maybe use similar method to update after the transaction was created.
    public void uploadImageToFireBase(ProcessedTransaction newTransactionInput,ImageFireBase ImageSelected,Context context) {
        String saveUrl = Caching.INSTANCE.getChosenAccountId() +
                "/" +
                ImageSelected.getNameOfImage();
        StorageReference image = storageReference.child(saveUrl);
        image.putFile(ImageSelected.getContentUri())
                .addOnSuccessListener(taskSnapshot -> {
                    if (newTransactionInput != null)
                        newTransactionInput.sendTransaction(newTransactionInput,context);
                })
                .addOnFailureListener(e ->
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.image_upload)
                                .setMessage(R.string.Transaction_upload_failed)
                                .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                .create()
                                .show()
                );
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


    public int getTotalAmount() {
        return totalAmount;
    }
    public String getIdOfStakeInt() {
        return idOfStakeInt;
    }
    public String getIdOfCategoryInt() {
        return idOfCategoryInt;
    }
    public Timestamp getDueDate() {
        return dueDate;
    }

    public String getTitle() {
        return title;
    }

    public String getImageName() {
        return imageName;
    }
    @Exclude
    public int getColorInt() {
        if(getIsDeleted())return R.color.rec_view_scheduled_completed;

        else if(getType().contains(Caching.INSTANCE.TYPE_CASH)){
            if(getTotalAmount()<0) return R.color.transaction_processed_positive;
            else return R.color.transaction_processed_negative;
        }
        else if(getType().contains(Caching.INSTANCE.TYPE_PENDING)){
            return R.color.rec_view_receivable_future;
        }
        else{
            if(getType().contains(Caching.INSTANCE.TYPE_PAYABLES))return R.color.transaction_Schedule_payables;
            else return R.color.transaction_Schedule_receivables;
        }

    }
    public int transText(){
        if(getType().contains(Caching.INSTANCE.TYPE_CASH)){
            if(getTotalAmount()<0) return (R.string.paid_to);
            else return (R.string.paid_by);
        }
        else if(getType().contains(Caching.INSTANCE.TYPE_PENDING)) {
             return(R.string.f_transaction_for);
        }
        else {
            if(getType().contains("RECEIVABLES"))return (R.string.receivable_of);
            else return(R.string.payable_of);
        }
    }

    @Exclude
    public String getIdOfTransactionInt() {
        return id;
    }

    @Exclude
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

    public Timestamp getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Timestamp deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
}



