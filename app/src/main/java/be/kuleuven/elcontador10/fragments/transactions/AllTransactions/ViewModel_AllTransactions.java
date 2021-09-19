package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transaction;

public class ViewModel_AllTransactions extends ViewModel {
    private static final String TAG = "All Transactions VM";
    //ChosenTypesOfTransactions
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<HashMap<String,Boolean>> chosenTypesOfTransactions = new MutableLiveData<>();
    public LiveData<HashMap<String,Boolean>> getChosenTypesOfTransactions() {
        return chosenTypesOfTransactions;
    }
    public void setTypesOfTransactions(HashMap<String,Boolean> selectedTypes){
        chosenTypesOfTransactions.setValue(selectedTypes);
    }


    private final MutableLiveData<List<Transaction>> monthlyListOfTransactions = new MutableLiveData<>();
    public LiveData<List<Transaction>> getMonthlyListOfTransactions() {
        return monthlyListOfTransactions;
    }
    public void selectMonthlyList() {
            String urlGetAccountTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";
            CollectionReference transactionsFromOneAccount = db.
                    collection(urlGetAccountTransactions);
            transactionsFromOneAccount.addSnapshotListener((value, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                List<Transaction> listTrans = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    Transaction myTransaction =  doc.toObject(Transaction.class);
                    myTransaction.setId( doc.getId());
                    listTrans.add(myTransaction);
                }
                monthlyListOfTransactions.setValue(listTrans);
            });
    }





    public void resetAll(){
        chosenTypesOfTransactions.setValue(null);
    }
}
