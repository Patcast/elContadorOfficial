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
import com.google.firebase.firestore.CollectionReference;
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
import java.util.Optional;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.model.Account;
import be.kuleuven.elcontador10.background.model.LoggedUser;
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
    private LoggedUser logInUser;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Caching";

    /// ******** Authentication
    public GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    private String chosenAccountId;


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
    public void startApp(LoggedUser user){
        requestAllUserAccounts(user.getEmail());
        setLogInUser(user);
        requestStaticData();
    }

    //// Request data

    //this goes on the click Listener of the account RecView
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openAccountFully(String chosenAccountId){
        setChosenAccountId(chosenAccountId);
        requestStakeHolder(chosenAccountId);
        requestAccountTransactions(chosenAccountId);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openQuickNewTransaction(String chosenAccountId){
        setChosenAccountId(chosenAccountId);
        requestStakeHolder(chosenAccountId);
    }


    //////// DATA BASE ****************
    //OnClick Listener on signIn
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestAllUserAccounts(String email){
        accounts.clear();
        CollectionReference accountsRef = db.collection("accounts");
        accountsRef.whereArrayContains("users", email);
        accountsRef.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            for (QueryDocumentSnapshot doc : value) {
                if (doc.get("name") != null) {
                    Account myAccount =  doc.toObject(Account.class);
                    myAccount.setId( doc.getId());
                    accounts.add(myAccount);
                }
                accountsObservers.forEach(t->t.notifyAccountsObserver(getAccounts()));
            }
        });
        /*db.collection("/globalAccounts/"+globalAccountId+"/accounts").orderBy("name", Query.Direction.ASCENDING)
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
                });*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestStaticData(){
        final DocumentReference docRef = db.document("/staticData/categories");
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                (( HashMap<String,ArrayList<String>> )snapshot.getData().get("categories")).forEach(this::makeTransactionTypes);
                roles.addAll((ArrayList)snapshot.get("roles"));
                staticDataObservers.forEach(staticDataObserver -> staticDataObserver.notifyStaticDataObserver(transTypes,roles));
                System.out.println(transTypes);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestStakeHolder(String chosenAccountId){
        String stakeHoldersUrl = "/accounts/"+chosenAccountId+"/stakeHolders";
        db.collection(stakeHoldersUrl)
                .addSnapshotListener((value, e) -> {
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
                });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestAccountTransactions(String chosenAccountId){

    String urlGetAccountTransactions = "/accounts/"+chosenAccountId+"/transactions";
        db.collection(urlGetAccountTransactions)
                .addSnapshotListener((value, e) -> {
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
                });
    }

//////************** end of db

    public void setChosenAccountId(String chosenAccountId) {
        this.chosenAccountId = chosenAccountId;
    }
    public void setLogInUser(LoggedUser logInUser) {
        this.logInUser = logInUser;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public int getNumberOfAccountObservers(){
        return accountsObservers.size();
    }
    public String getChosenAccountId() {
        return chosenAccountId;
    }
    public String getLogInUserId() {
        return logInUser.getEmail();
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Transaction getTransaction(String idOfTransaction){
        List<Transaction> availableTran = new ArrayList<>(getTransactions());
       Optional<Transaction> possibleTransaction = availableTran.stream()
                            .filter(t->t.getId().equals(idOfTransaction))
                            .findFirst();
       return possibleTransaction.orElse(null);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getAccountName(){
        Optional<String> selectedAccount = getAccounts().stream()
                                                        .filter(a->a.getId().equals(chosenAccountId))
                                                        .map(Account::getName)
                                                        .findFirst();
        return selectedAccount.orElse("account not found");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getStakeholderName(String idStakeholder) {

        Optional<String> possibleName = getStakeHolders().stream()
                .filter(s -> s.getId().equals(idStakeholder))
                .map(StakeHolder::getName)
                .findFirst();
        return possibleName.orElse(context.getString(R.string.error_finding_microAccount));
    }

}
