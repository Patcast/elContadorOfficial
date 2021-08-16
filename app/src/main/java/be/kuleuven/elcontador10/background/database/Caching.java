package be.kuleuven.elcontador10.background.database;

import android.content.Context;

import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;


import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.core.OrderBy;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import be.kuleuven.elcontador10.R;

import be.kuleuven.elcontador10.background.model.Account;

import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.model.TransactionType;
import be.kuleuven.elcontador10.background.model.User;

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
    public interface MicroAccountTransactionObserver{
        void notifyMicroAccountTransactionObserver(List<Transaction> transactions);
    }

    private final List <Account> accounts = new ArrayList<>();
    private final List <AccountsObserver> accountsObservers = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void attachAccountsObservers(AccountsObserver newObserver, String email){
        if(email!=null){
            accountsObservers.add(newObserver);
            if(accounts.size()==0) requestAllUserAccounts(email);
            else newObserver.notifyAccountsObserver(accounts);
        }
    }
    public void deAttachAccountsObservers(AccountsObserver unWantedObserver){
        if (unWantedObserver!=null){
            accountsObservers.remove(unWantedObserver);
        }
    }


    ////*********Data
    public List <StakeHolder> stakeHolders = new ArrayList<>();
    public List <TransactionType>  transTypes = new ArrayList<>();
    public List <String> roles = new ArrayList<>();
    public List <Transaction> transactions = new ArrayList<>();
    public List <Transaction> microAccountTransactions = new ArrayList<>();

    ///********** Observers List
    private final List <StaticDataObserver> staticDataObservers = new ArrayList<>();
    private final List <StakeholdersObserver> stakeholdersObservers = new ArrayList<>();
    private final List <MicroAccountTransactionObserver> microAccountObservers = new ArrayList<>();
    private final List <AllTransactionsObserver> allTransactionsObservers = new ArrayList<>();

    ///********** Variables

    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Caching";

    /// ******** Authentication
    private String chosenAccountId;
    private User logInUser;
    private String chosenMicroAccountId;

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

    public void attachAllTransactionsObserver(AllTransactionsObserver newObserver){
        allTransactionsObservers.add(newObserver);
        if(transactions.size()!=0){
            newObserver.notifyAllTransactionsObserver( transactions);
        }

    }
    public void deAttachAllTransactionsObserver(AllTransactionsObserver unWantedObserver){
        allTransactionsObservers.remove(unWantedObserver);
    }

    public void attachMicroTransactionsObserver(MicroAccountTransactionObserver newObserver) {
        microAccountObservers.add(newObserver);
        if (microAccountTransactions.size() != 0)
            newObserver.notifyMicroAccountTransactionObserver(microAccountTransactions);
    }

    public void deAttachMicroTransactionsObserver(MicroAccountTransactionObserver observer) {
        microAccountObservers.remove(observer);
    }

///Other Methods*************************


    //this goes on the click Listener of the account RecView
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openAccountFully(String chosenAccountId){
        setChosenAccountId(chosenAccountId);
        requestGroupOFStakeHolders(chosenAccountId);
        requestAccountTransactions(chosenAccountId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openQuickNewTransaction(String chosenAccountId){
        setChosenAccountId(chosenAccountId);
        requestGroupOFStakeHolders(chosenAccountId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openMicroAccount(String microAccountId) {
        setChosenMicroAccountId(microAccountId);
        requestMicroAccountTransactions(chosenAccountId, microAccountId);
    }

    public void signOut(){
     stakeHolders.clear();
     transTypes.clear();
     accounts.clear();
     transactions.clear();
     roles.clear();
     logInUser = null;
     chosenAccountId = null;
    }

    //////// DATA BASE ****************
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestAllUserAccounts(String email){
        db.collection("accounts").
                whereArrayContains("users", email).
                orderBy("name").
                addSnapshotListener((value, e) -> {

                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    accounts.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("name") != null) {
                            Account myAccount =  doc.toObject(Account.class);
                            myAccount.setId( doc.getId());
                            accounts.add(myAccount);
                        }
                    }
                    accountsObservers.forEach(t->t.notifyAccountsObserver(getAccounts()));
                });
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
    public void requestGroupOFStakeHolders(String chosenAccountId){
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
    public void requestAccountTransactions(String chosenAccountId){

    String urlGetAccountTransactions = "/accounts/"+chosenAccountId+"/transactions";
        db.collection(urlGetAccountTransactions).
                orderBy("date", Query.Direction.DESCENDING).
                addSnapshotListener((value, e) -> {
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
    User requestedUser;
    public User requestUser(String userEmail){

        db.collection("users").whereEqualTo("email",userEmail).
        addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            for (QueryDocumentSnapshot doc : value) {
                if (doc.get("email") != null) {
                    requestedUser =  doc.toObject(User.class);
                }
            }
        });
        return requestedUser;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestMicroAccountTransactions(String chosenAccountId, String microAccountId) {
        String url = "/accounts/" + chosenAccountId + "/transactions";

        db.collection(url)
                .whereEqualTo("stakeHolder", microAccountId)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed", e);
                        return;
                    }

                    microAccountTransactions.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        Transaction myTransaction = doc.toObject(Transaction.class);
                        myTransaction.setId(doc.getId());
                        microAccountTransactions.add(myTransaction);
                    }

                    System.out.println(getStakeholderName(microAccountId));
                    microAccountObservers.forEach(t -> t.notifyMicroAccountTransactionObserver(microAccountTransactions));
                });
    }

//////************** end of db

    public void setChosenAccountId(String chosenAccountId) {
        this.chosenAccountId = chosenAccountId;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setChosenMicroAccountId(String chosenMicroAccountId) {
        this.chosenMicroAccountId = chosenMicroAccountId;
    }

    public String getChosenAccountId() {
        return chosenAccountId;
    }

    public String getLogInUserId() {
        return logInUser.getEmail();
    }

    public String getChosenMicroAccountId() {
        return chosenMicroAccountId;
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
