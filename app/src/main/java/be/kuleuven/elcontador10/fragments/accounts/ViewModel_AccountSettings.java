package be.kuleuven.elcontador10.fragments.accounts;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.Account;


public class ViewModel_AccountSettings extends ViewModel {

    private static final String TAG = "accountViewModel";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Account> account = new MutableLiveData<>();
    public LiveData<Account> getAccount() {
        return account;
    }
    public void setAccountUsersList(Account accountInput){
        account.setValue(accountInput);
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestAccountUsers(){
        final DocumentReference docRef = db.collection("accounts").document(Caching.INSTANCE.getChosenAccountId());
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Account account = snapshot.toObject(Account.class);
                setAccountUsersList(account);
                Log.d(TAG, "Current data: " + snapshot.getData());
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

}