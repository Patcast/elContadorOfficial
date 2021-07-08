package be.kuleuven.elcontador10.background.database;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.model.Account;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.model.TransactionType;
import be.kuleuven.elcontador10.fragments.Accounts;

public enum Caching {
    INSTANCE;
    ////*********Data
    private List <StakeHolder> stakeHolders = new ArrayList<>();
    private List <TransactionType>  transTypes = new ArrayList<>();
    private List <String> roles = new ArrayList<>();
    ///********** Observers List
    private List <CachingObserver> observers = new ArrayList<>();
    private List <Transaction> transactions = new ArrayList<>();
    private List <Account> accounts = new ArrayList<>();
    ///********** Variables
     View view;
     Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Caching";
    public GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    private final String globalAccountId = "IhsNw7w9mkz6bsf6TfZd";

///Attach method

    public void attachCaching(CachingObserver newObserver){
        observers.add(newObserver);
        newObserver.notifyStakeHolders(stakeHolders);
        newObserver.notifyCategories(transTypes);
        newObserver.notifyRoles(roles);

    }
    /// Set Data
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAllData(Context context){
        this.context = context;
        requestStaticData();
        requestStakeHolder();
        observers.forEach(o ->o.notifyStakeHolders(stakeHolders) );
        observers.forEach(o ->o.notifyCategories(transTypes) );
        observers.forEach(o ->o.notifyRoles(roles));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAllObservers(){
        observers.forEach(o ->o.notifyRoles(roles));
        observers.forEach(o ->o.notifyStakeHolders(stakeHolders) );
        observers.forEach(o ->o.notifyCategories(transTypes) );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setStakeHolders (){
        requestStakeHolder();
        observers.forEach(o-> o.notifyStakeHolders(stakeHolders));
    }

    //////// DATA BASE ****************
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestStaticData(){
        final DocumentReference docRef = db.document("/globalAccounts/"+globalAccountId+"/accounts/xI4douRpfSbTs8Ofepee/datos/staticData");
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                (( HashMap<String,String[]> )snapshot.getData().get("categories")).forEach(this::makeTransactionTypes);
                roles.add((String)snapshot.get("roles"));
            } else {
                Log.d(TAG, "Current data: null");
            }
        });

    }

    private void makeTransactionTypes(String k, String[] v) {
        for(String subCat: v){
            TransactionType typeOfTransactions1 = new TransactionType(k,subCat);
            transTypes.add(typeOfTransactions1);
        }
    }

    public void requestStakeHolder(){
        db.collection("/globalAccounts/"+globalAccountId+"/accounts/xI4douRpfSbTs8Ofepee/stakeHolders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            StakeHolder myStakeHolder =  doc.toObject(StakeHolder.class);
                            myStakeHolder.setId( doc.getId());
                            stakeHolders.add(myStakeHolder);
                        }
                    }
                });
    }

    private void requestAllTransactions(){

        db.collection("/globalAccounts/"+globalAccountId+"/accounts/xI4douRpfSbTs8Ofepee/transactions")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            Transaction myTransaction =  doc.toObject(Transaction.class);
                            myTransaction.setId( doc.getId());
                            transactions.add(myTransaction);
                        }
                    }
                });



    }
    private void requestAllAccounts(String globalAccountId){

        db.collection("/globalAccounts/"+globalAccountId+"/accounts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            Account myAccount =  doc.toObject(Account.class);
                            myAccount.setId( doc.getId());
                            accounts.add(myAccount);
                        }
                    }
                });

    }



    public void detach(CachingObserver observer){
        observers.remove(observer);

    }
    public List<TransactionType> getTransTypes() {
        return transTypes;
    }
}
