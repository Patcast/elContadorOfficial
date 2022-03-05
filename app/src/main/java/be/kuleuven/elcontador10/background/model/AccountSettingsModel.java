package be.kuleuven.elcontador10.background.model;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;


import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;

public class AccountSettingsModel {
    private static final String TAG = "accountSettingsItem";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String subjectsEmail;
    private Context context;
    boolean executed=false;

    public AccountSettingsModel(String subjectsEmail, Context context) {
        this.subjectsEmail = subjectsEmail;
        this.context = context;
    }

    public boolean  changeOwner(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_change_owner)
                .setMessage(context.getString(R.string.new_owner_1)+" "+subjectsEmail+" "+context.getString(R.string.new_owner2))
                .setPositiveButton(R.string.yes, (dialog, which) ->executeChangeOfOwner())
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
       return executed;
    }
    private void executeChangeOfOwner(){
        setExecuted(true);
        db.collection("accounts").document(Caching.INSTANCE.getChosenAccountId()).update("owner", subjectsEmail)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }
    public boolean deleteAccountUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm delete user")
                .setMessage(context.getString(R.string.new_owner_1)+" "+subjectsEmail+" "+context.getString(R.string.delete_user2))
                .setPositiveButton(R.string.yes, (dialog, which) ->executeDeleteAccountUser())
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
        
        return isExecuted();
    }


    private void executeDeleteAccountUser(){
        setExecuted(true);
        db.collection("accounts")
                .document(Caching.INSTANCE.getChosenAccountId())
                .update("users", FieldValue.arrayRemove(subjectsEmail))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public boolean isExecuted() {
        return executed;
    }
}
