package be.kuleuven.elcontador10.background.database;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

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
import com.google.firebase.firestore.Query;
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
    /// interfaces******
    public interface StaticDataObserver {
        void notifyStaticDataObserver( List <TransactionType> transTypes,  List <String> roles);
    }
    public interface StakeholdersObserver{
        void notifyStakeholdersObserver(List <StakeHolder> stakeHolders);
    }
    public interface AccountsObserver {
        void notifyAccountsObserver(List<Account> accounts);
    }
    public interface AllTransactionsObserver{
        void notifyAllTransactionsObserver(List<Transaction> allTransactions);
    }

    ////*********Data
    public List <StakeHolder> stakeHolders = new ArrayList<>();
    public List <TransactionType>  transTypes = new ArrayList<>();
    public List <String> roles = new ArrayList<>();
    public List <Transaction> transactions = new ArrayList<>();
    private final List <Account> accounts = new ArrayList<>();


    ///********** Observers List
    private final List <StaticDataObserver> staticDataObservers = new ArrayList<>();
    private final List <StakeholdersObserver> stakeholdersObservers = new ArrayList<>();
    private final List <AccountsObserver> accountsObservers = new ArrayList<>();
    private final List <AllTransactionsObserver> allTransactionsObservers = new ArrayList<>();
    ///********** Variables
    View view;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Caching";
    /// ******** Authentication
    public GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    private  String globalAccountId;
    private String chosenAccountId;
    private String logInUserId;

///Attach methods*************************
    public void attachStaticDataObservers(StaticDataObserver newObserver){
        staticDataObservers.add(newObserver);
        newObserver.notifyStaticDataObserver( transTypes,roles);
    }
    public void deAttachStaticDataObserver(StaticDataObserver newObserver){
        staticDataObservers.remove(newObserver);
    }
    public void attachStakeholdersObservers(StakeholdersObserver newObserver){
        stakeholdersObservers.add(newObserver);
        if(stakeHolders.size()!=0){
            newObserver.notifyStakeholdersObserver( stakeHolders); }
    }
    public void deAttachStakeholdersObservers(StakeholdersObserver newObserver){
        stakeholdersObservers.remove(newObserver);
    }
    public void attachAccountsObservers(AccountsObserver newObserver){
        accountsObservers.add(newObserver);
        if(accounts.size()!=0){
        newObserver.notifyAccountsObserver( accounts);}
    }
    public void deAttachAccountsObservers(AccountsObserver unWantedObserver){
        accountsObservers.remove(unWantedObserver);
    }
    public void attachAllTransactionsObserver(AllTransactionsObserver newObserver){
        allTransactionsObservers.add(newObserver);
        if(transactions.size()!=0){
            newObserver.notifyAllTransactionsObserver( transactions);
        }

    }
    public void deAttachAllTransactionsObserver(AllTransactionsObserver unWantedObserver){
        allTransactionsObservers.remove(unWantedObserver);
    }


///Other Methods*************************

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startApp(String globalAccountId,String userId){
        requestAllAccounts(globalAccountId);
        requestStaticData(globalAccountId);
        setGlobalAccountId(globalAccountId);
        setLogInUserId(userId);
        requestStakeHolder(chosenAccountId);
    }

    //// Request data

    //this goes on the click Listener of the account RecView
    public void openAccount(String chosenAccountId){
        setChosenAccountId(chosenAccountId);
        requestAllTransactions(chosenAccountId);
    }



    //////// DATA BASE ****************

    //OnClick Listener on signIn
    public void requestAllAccounts(String globalAccountId){

        db.collection("/globalAccounts/"+globalAccountId+"/accounts").orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        accounts.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Account myAccount =  doc.toObject(Account.class);
                            myAccount.setId( doc.getId());
                            accounts.add(myAccount);
                        }
                        accountsObservers.forEach(o -> o.notifyAccountsObserver(accounts));
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestStaticData(String globalAccountId){
        final DocumentReference docRef = db.document("/globalAccounts/"+globalAccountId);
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                (( HashMap<String,ArrayList<String>> )snapshot.getData().get("categories")).forEach(this::makeTransactionTypes);
                roles.addAll((ArrayList)snapshot.get("roles"));
                System.out.println(roles);
                staticDataObservers.forEach(staticDataObserver -> staticDataObserver.notifyStaticDataObserver(transTypes,roles));

            } else {
                Log.d(TAG, "Current data: null");
            }
        });


    }

    private void makeTransactionTypes(String k, ArrayList<String> v) {
        for(String subCat: v){
            TransactionType typeOfTransactions1 = new TransactionType(k,subCat);
            transTypes.add(typeOfTransactions1);
        }
    }

    public void requestStakeHolder(String chosenAccountId){
        db.collection("/globalAccounts/"+globalAccountId+"/stakeHolders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        stakeHolders.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            StakeHolder myStakeHolder =  doc.toObject(StakeHolder.class);
                            myStakeHolder.setId( doc.getId());
                            stakeHolders.add(myStakeHolder);
                        }
                        stakeholdersObservers.forEach(s->s.notifyStakeholdersObserver(getStakeHolders()));
                    }
                });
    }

    private void requestAllTransactions(String chosenAccountId){

        db.collection("/globalAccounts/"+globalAccountId+"/accounts/"+chosenAccountId+"/transactions")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        transactions.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Transaction myTransaction =  doc.toObject(Transaction.class);
                            myTransaction.setId( doc.getId());
                            transactions.add(myTransaction);
                        }
                        allTransactionsObservers.forEach(t->t.notifyAllTransactionsObserver(getTransactions()));
                    }
                });
    }

//////************** end of db

    public void setChosenAccountId(String chosenAccountId) {
        this.chosenAccountId = chosenAccountId;
    }

    public String getChosenAccountId() {
        return chosenAccountId;
    }

    public String getLogInUserId() {
        return logInUserId;
    }

    public void setLogInUserId(String logInUserId) {
        this.logInUserId = logInUserId;
    }

    public String getGlobalAccountId() {
        return globalAccountId;
    }

    public void setGlobalAccountId(String globalAccountId) {
        this.globalAccountId = globalAccountId;
    }

    public List<StakeHolder> getStakeHolders() {
        return stakeHolders;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Account> getAccounts() {
        return accounts;
    }


}
