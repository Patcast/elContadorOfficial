package be.kuleuven.elcontador10.background;

import android.content.Context;

import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import be.kuleuven.elcontador10.R;

import be.kuleuven.elcontador10.background.model.Account;

import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.TransactionType;
import be.kuleuven.elcontador10.background.model.User;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.ViewModel_AllTransactions;

public enum Caching {
    INSTANCE;
    public final String TYPE_CASH = "CASH",
            TYPE_PAYABLES = "PAYABLES",
            TYPE_RECEIVABLES = "RECEIVABLES",
            TYPE_PENDING = "PENDING",
            PROPERTY_STAKEHOLDER = "P_STAKE",
            PROPERTY_NEW_T = "P_New_T",
            STAKE_NEW_TRANSACTION = "S_New_T";
    /// interfaces******
    public interface CategoriesObserver{
        void notifyCatObserver(List <EmojiCategory> customCategoriesInput);
    }
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
        void notifyAllTransactionsObserver(List<ProcessedTransaction> allTransactions);
    }
    public interface MicroAccountTransactionObserver{
        void notifyMicroAccountTransactionObserver(List<ProcessedTransaction> transactions);
    }

    ////*********Data
    private final List <Account> accounts = new ArrayList<>();
    private final List<EmojiCategory> customCategories = new ArrayList<>();
    public List <StakeHolder> stakeHolders = new ArrayList<>();
    public List <TransactionType>  transTypes = new ArrayList<>();
    public List <String> roles = new ArrayList<>();
    public List <ProcessedTransaction> transactions = new ArrayList<>();
    public List <ProcessedTransaction> microAccountTransactions = new ArrayList<>();


    ///********** Observers List
    private final List <AccountsObserver> accountsObservers = new ArrayList<>();
    private final List <CategoriesObserver> catObservers = new ArrayList<>();
    private final List <StaticDataObserver> staticDataObservers = new ArrayList<>();
    private final List <StakeholdersObserver> stakeholdersObservers = new ArrayList<>();
    private final List <MicroAccountTransactionObserver> microAccountObservers = new ArrayList<>();
    private final List <AllTransactionsObserver> allTransactionsObservers = new ArrayList<>();


    ///********** Variables

    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Caching";
    ViewModel_AllTransactions viewModel;

    /// ******** Authentication
    private String chosenAccountId;
    private Account chosenAccount;
    private User logInUser;
    private String chosenMicroAccountId;


    ///Attach methods*************************
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void attachCatObserver(CategoriesObserver newObserver){
        catObservers.add(newObserver);
        if(customCategories.size()!=0)newObserver.notifyCatObserver(customCategories);
    }
    public void deAttachCatObserver(CategoriesObserver unWantedObserver){
        if(unWantedObserver!=null)catObservers.remove(unWantedObserver);
    }
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


    public void attachMicroTransactionsObserver(MicroAccountTransactionObserver newObserver) {
        microAccountObservers.add(newObserver);
        if (microAccountTransactions.size() != 0)
            newObserver.notifyMicroAccountTransactionObserver(microAccountTransactions);
    }
    public void deAttachMicroTransactionsObserver(MicroAccountTransactionObserver observer) {
        if (microAccountObservers.size() != 0)
            microAccountObservers.remove(observer);
    }



///Other Methods*************************


    //this goes on the click Listener of the account RecView
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openAccountFully(String chosenAccountId){
        setChosenAccountId(chosenAccountId);
        requestAccountTransactions(chosenAccountId);
        requestCustomCategories();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openQuickNewTransaction(String chosenAccountId){
        setChosenAccountId(chosenAccountId);
        requestCustomCategories();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openMicroAccount(String microAccountId) {
        setChosenMicroAccountId(microAccountId);
        requestMicroAccountTransactions(chosenAccountId, microAccountId);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public  void startApp(){
        requestStaticData();
    }
    public void signOut(){
         customCategories.clear();
         stakeHolders.clear();
         transTypes.clear();
         accounts.clear();
         transactions.clear();
         roles.clear();
         logInUser = null;
         chosenAccountId = null;
         chosenAccount = null;
    }

    //////// DATA BASE ****************

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestCustomCategories() {
         db.collection("/accounts/"+chosenAccountId+"/customCategories")
                 .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                     if (value != null) {
                         customCategories.clear();
                         for (QueryDocumentSnapshot doc : value) {
                             if (doc.get("title") != null) {
                                 EmojiCategory myCategory = doc.toObject(EmojiCategory.class);
                                 myCategory.setId(doc.getId());
                                 customCategories.add(myCategory);
                             }
                         }
                         catObservers.forEach(t -> t.notifyCatObserver(customCategories));
                     }
                });
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestAllUserAccounts(String email){
        startApp();
        db.collection("accounts").
                whereArrayContains("users", email).
                addSnapshotListener((value, e) -> {

                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    accounts.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("name") != null) {
                            Account myAccount =  doc.toObject(Account.class);
                            myAccount.setId(doc.getId());
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
    public void setStakeholderList(List<StakeHolder> stakeholderList){
        if (stakeholderList != null) {
            this.stakeHolders.clear();
            this.stakeHolders.addAll(stakeholderList);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestAccountTransactions(String chosenAccountId){
        String urlGetAccountTransactions = "/accounts/"+chosenAccountId+"/transactions";
        CollectionReference transactionsFromOneAccount = db.collection(urlGetAccountTransactions);
        transactionsFromOneAccount.addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    transactions.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        ProcessedTransaction myTransaction =  doc.toObject(ProcessedTransaction.class);
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
                        ProcessedTransaction myTransaction = doc.toObject(ProcessedTransaction.class);
                        //myTransaction.setId(doc.getId());//new transactions don't need it.
                        microAccountTransactions.add(myTransaction);
                    }

                    System.out.println(getStakeholderName(microAccountId));
                    microAccountObservers.forEach(t -> t.notifyMicroAccountTransactionObserver(microAccountTransactions));
                });
    }

//////************** end of db

    // getters


    public Account getChosenAccount() {
        return chosenAccount;
    }

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

    public List<ProcessedTransaction> getTransactions() {
        return transactions;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ProcessedTransaction getTransaction(String idOfTransaction){
        List<ProcessedTransaction> availableTran = new ArrayList<>(getTransactions());
       Optional<ProcessedTransaction> possibleTransaction = availableTran.stream()
                            .filter(t->t.getIdOfTransactionInt().equals(idOfTransaction))
                            .findFirst();
       return possibleTransaction.orElse(null);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getAccountName(){
        Optional<String> selectedAccount = getAccounts().stream()
                                                        .filter(a->a.getId().equals(chosenAccountId))
                                                        .map(Account::getName)
                                                        .findFirst();
        return selectedAccount.orElse(context.getString(R.string.error_loading));
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getStakeholderName(String idStakeholder) {

        Optional<String> possibleName = getStakeHolders().stream()
                .filter(s -> s.getId().equals(idStakeholder))
                .map(StakeHolder::getName)
                .findFirst();
        return possibleName.orElse(context.getString(R.string.not_recorded));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getCategoryEmoji(String idCategory) {
        List<EmojiCategory> emojiCategoriesCombo = new ArrayList<>(customCategories);
        Optional<String> possibleEmoji = emojiCategoriesCombo.stream()
                .filter(s -> s.getId().equals(idCategory))
                .map(EmojiCategory::getIcon)
                .findFirst();
        return possibleEmoji.orElse("");
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getCategoryTitle(String idCategory) {
        List<EmojiCategory> emojiCategoriesCombo = new ArrayList<>(customCategories);
        Optional<String> possibleEmoji = emojiCategoriesCombo.stream()
                .filter(s -> s.getId().equals(idCategory))
                .map(EmojiCategory::getTitle)
                .findFirst();
        return possibleEmoji.orElse("");
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public StakeHolder getStakeHolder(String id) {
        if (id != null)
            return stakeHolders
                    .stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        else return null;
    }




    // setters

    public void setChosenAccount(Account chosenAccount) {
        this.chosenAccount = chosenAccount;
    }

    public void setChosenStakeHolder(StakeHolder chosenStakeHolder) {
        /// ******** Stakeholders Variables
    }


    private final HashMap<String, String> propertyList = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setPropertyList(List<Property> properties) {
        propertyList.clear();
        properties.forEach( property -> propertyList.put(property.getId(), property.getName()));
    }

    public String getPropertyNameFromID(String id) {
        if (id!=null&&!id.equals(""))
            return propertyList.get(id);
        else
            return "N/A";
    }



    public boolean checkPermission(String loggedInEmail) {
        return getChosenAccount().getOwner().equals(loggedInEmail);
    }
}
