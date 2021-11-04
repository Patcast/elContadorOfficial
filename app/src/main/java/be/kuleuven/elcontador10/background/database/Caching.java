package be.kuleuven.elcontador10.background.database;

import android.content.Context;

import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.kuleuven.elcontador10.R;

import be.kuleuven.elcontador10.background.model.Account;

import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.TransactionType;
import be.kuleuven.elcontador10.background.model.User;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.model.contract.SubContract;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.model.contract.Contract;

public enum Caching {
    INSTANCE;

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
    public interface MicroAccountContractObserver {
        void notifyMicroAccountContractsObserver(List<Contract> contracts);
    }

    public interface SubContractObserver {
        void notify(SubContract contract, List<ScheduledTransaction> scheduledTransactions);
    }




    ////*********Data
    private final List <Account> accounts = new ArrayList<>();
    private final List<EmojiCategory> defaultCategories = new ArrayList<>();
    private final List<EmojiCategory> customCategories = new ArrayList<>();
    public List <StakeHolder> stakeHolders = new ArrayList<>();
    public List <TransactionType>  transTypes = new ArrayList<>();
    public List <String> roles = new ArrayList<>();
    public List <ProcessedTransaction> transactions = new ArrayList<>();
    public List <ProcessedTransaction> microAccountTransactions = new ArrayList<>();
    public List <Contract> microAccountContracts = new ArrayList<>();
    public List <ScheduledTransaction> scheduledTransactions = new ArrayList<>();

    ///********** Observers List
    private final List <AccountsObserver> accountsObservers = new ArrayList<>();
    private final List <CategoriesObserver> catObservers = new ArrayList<>();
    private final List <StaticDataObserver> staticDataObservers = new ArrayList<>();
    private final List <StakeholdersObserver> stakeholdersObservers = new ArrayList<>();
    private final List <MicroAccountTransactionObserver> microAccountObservers = new ArrayList<>();
    private final List <AllTransactionsObserver> allTransactionsObservers = new ArrayList<>();
    private final List <MicroAccountContractObserver> microAccountContractObservers = new ArrayList<>();
    private final List <SubContractObserver> subContractObservers = new ArrayList<>();

    ///********** Variables

    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Caching";

    /// ******** Authentication
    private String chosenAccountId;
    private User logInUser;
    private String chosenMicroAccountId;

    /// ******** Stakeholders Variables
    private StakeHolder chosenStakeHolder;
    private Contract chosenContract;
    private SubContract chosenSubContract;

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
        if (allTransactionsObservers.size() != 0)
            allTransactionsObservers.remove(unWantedObserver);
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

    public void attachMicroContractObserver(MicroAccountContractObserver observer) {
        microAccountContractObservers.add(observer);
        if (microAccountContracts.size() != 0)
            observer.notifyMicroAccountContractsObserver(microAccountContracts);
    }

    public void deAttachMicroContractObserver(MicroAccountContractObserver observer) {
        if (microAccountContractObservers.size() != 0)
            microAccountContractObservers.remove(observer);
    }

    public void attachSubcontractObserver(SubContractObserver observer) {
        subContractObservers.add(observer);
        if (chosenSubContract != null && scheduledTransactions != null)
            observer.notify(chosenSubContract, scheduledTransactions);
    }

    public void detachSubcontractObserver(SubContractObserver observer) {
        subContractObservers.remove(observer);
    }

///Other Methods*************************


    //this goes on the click Listener of the account RecView
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openAccountFully(String chosenAccountId){
        setChosenAccountId(chosenAccountId);
        requestGroupOFStakeHolders(chosenAccountId);
        requestAccountTransactions(chosenAccountId);
        requestCustomCategories();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openQuickNewTransaction(String chosenAccountId){
        setChosenAccountId(chosenAccountId);
        requestGroupOFStakeHolders(chosenAccountId);
        requestCustomCategories();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openMicroAccount(String microAccountId) {
        setChosenMicroAccountId(microAccountId);
        requestMicroAccountTransactions(chosenAccountId, microAccountId);
        requestMicroAccountContracts(chosenAccountId, microAccountId);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public  void startApp(){
        requestStaticData();
        requestDefaultCategories();
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
    }

    //////// DATA BASE ****************

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestCustomCategories() {
        db.collection("/accounts/"+chosenAccountId+"/customCategories").
                addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    customCategories.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("title") != null) {
                            EmojiCategory myCategory =  doc.toObject(EmojiCategory.class);
                            myCategory.setId(doc.getId());
                            customCategories.add(myCategory);
                        }
                    }
                    catObservers.forEach(t->t.notifyCatObserver(customCategories));
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestDefaultCategories() {
        db.collection("defaultCategories").
                addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    defaultCategories.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("title") != null) {
                            EmojiCategory myCategory =  doc.toObject(EmojiCategory.class);
                            myCategory.setId(doc.getId());
                            defaultCategories.add(myCategory);
                        }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestMicroAccountContracts(String chosenAccountId, String microAccountId) {
        String url = "/accounts/" + chosenAccountId + "/stakeHolders/" + microAccountId + "/contracts";

        db.collection(url)
                .addSnapshotListener((value, e) -> {
                   if (e != null) {
                       Log.w(TAG, "Listen failed", e);
                       return;
                   }

                   microAccountContracts.clear();

                   for (QueryDocumentSnapshot doc : value) {
                       Contract myContract = doc.toObject(Contract.class);

                       myContract.setId(doc.getId());
                       myContract.setMicroAccount(microAccountId);
                       myContract.setSubContracts(getMicroAccountSubContracts(chosenAccountId, microAccountId, doc.getId()));

                       microAccountContracts.add(myContract);
                   }

                   microAccountContractObservers.forEach(t -> t.notifyMicroAccountContractsObserver(microAccountContracts));
                });
    }

    private ArrayList<SubContract> getMicroAccountSubContracts(String chosenAccountId, String microAccountId, String contractId) {
        String url = "/accounts/" + chosenAccountId + "/stakeHolders/" + microAccountId + "/contracts/" + contractId + "/subcontracts";
        ArrayList<SubContract> subContracts = new ArrayList<>();

        db.collection(url)
                .addSnapshotListener((value, e) -> {
                   if (e!= null) {
                       Log.w(TAG, "Listen failed", e);
                       return;
                   }

                   for (QueryDocumentSnapshot doc : value) {
                       SubContract mySubContract = doc.toObject(SubContract.class);
                       mySubContract.setId(doc.getId());
                       subContracts.add(mySubContract);
                   }
                });

        return subContracts;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getSubContract(String subcontract) {
        String url = "/accounts/" + chosenAccountId + "/stakeHolders/" + chosenMicroAccountId +
                "/contracts/" + chosenContract.getId() + "/subcontracts/" + subcontract;

        db.document(url)
                .addSnapshotListener((value, error) -> {
                    if (error != null && value == null) {
                        Log.w(TAG, "Listen failed", error);
                        return;
                    }

                    chosenSubContract = value.toObject(SubContract.class);
                    chosenSubContract.setId(value.getId());

                    getScheduledTransactions(url);
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getScheduledTransactions(String subContractURL) {
        String url = subContractURL + "/scheduledTransactions";

        Query query = db.collection(url)
                .orderBy("dueDate", Query.Direction.ASCENDING);

        query.addSnapshotListener((value, error) -> {
                    if (error!= null && value == null) {
                        Log.w(TAG, "Listen failed", error);
                        return;
                    }

                    scheduledTransactions.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        ScheduledTransaction transaction = doc.toObject(ScheduledTransaction.class);
                        transaction.setId(doc.getId());
                        transaction.setPath(doc.getReference().getPath());
                        transaction.setColor();
                        scheduledTransactions.add(transaction);
                    }

                    subContractObservers.forEach(e -> e.notify(chosenSubContract, scheduledTransactions));
                });
    }

    public void setScheduledTransactions(List<ScheduledTransaction> transactions) {
        scheduledTransactions.clear();
        scheduledTransactions.addAll(transactions);
    }

//////************** end of db

    // getters

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
    public String getAccountBalance(){
        Optional<String> selectedAccount = getAccounts().stream()
                .filter(a->a.getId().equals(chosenAccountId))
                .map(Account::getBalance)
                .map(this::formatNumber)
                .findFirst();
        return selectedAccount.orElse(context.getString(R.string.error_loading));
    }
    private String formatNumber(Long inputNumber){
        NumberFormatter formatter = new NumberFormatter(inputNumber);
        return formatter.getFinalNumber();
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
    public List<EmojiCategory> getDefaultCategories() {
        return defaultCategories;
    }
    public List<CategoriesObserver> getCatObservers() {
        return catObservers;
    }

    public Contract getContractFromId(String id) {
        for (Contract contract : microAccountContracts) {
            if (contract.getId().equals(id)) return contract;
        }
        Log.w(TAG, "Contract not found", new IllegalArgumentException());
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getCategoryEmoji(String idCategory) {
        List<EmojiCategory> emojiCategoriesCombo = new ArrayList<>(customCategories);
        emojiCategoriesCombo.addAll(defaultCategories);
        Optional<String> possibleEmoji = emojiCategoriesCombo.stream()
                .filter(s -> s.getId().equals(idCategory))
                .map(EmojiCategory::getIcon)
                .findFirst();
        return possibleEmoji.orElse("");
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getCategoryTitle(String idCategory) {
        List<EmojiCategory> emojiCategoriesCombo = new ArrayList<>(customCategories);
        emojiCategoriesCombo.addAll(defaultCategories);
        Optional<String> possibleEmoji = emojiCategoriesCombo.stream()
                .filter(s -> s.getId().equals(idCategory))
                .map(EmojiCategory::getTitle)
                .findFirst();
        return possibleEmoji.orElse("");
    }

    public StakeHolder getChosenStakeHolder() {
        return chosenStakeHolder;
    }

    public Contract getChosenContract() {
        return chosenContract;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public SubContract getSubContractFromID(String id) {
        Optional<SubContract> subContract = chosenContract.getSubContracts().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();

        return subContract.orElse(null);
    }

    public SubContract getChosenSubContract() {
        return chosenSubContract;
    }

    // setters

    public void setChosenStakeHolder(StakeHolder chosenStakeHolder) {
        this.chosenStakeHolder = chosenStakeHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setChosenStakeHolder(String chosenStakeHolderID) {
        chosenStakeHolder = stakeHolders.stream()
                .filter(t -> t.getId().equals(chosenStakeHolderID))
                .findFirst().orElse(null);
    }

    public void setChosenContract(Contract chosenContract) {
        this.chosenContract = chosenContract;
    }

    public void setChosenSubContract(SubContract chosenSubContract) {
        this.chosenSubContract = chosenSubContract;
    }
   /* @RequiresApi(api = Build.VERSION_CODES.N)
    public int getStartingBalances(int month, int year){
        Optional<Map<String,Integer>> mapOptional = getAccounts().stream()
                .filter(a->a.getId().equals(chosenAccountId))
                .map(Account::getMapOfStaringBalances)
                .findFirst();
        if(mapOptional.isPresent()){
          return  mapOptional.get().getOrDefault((""+month+"/"+year),0);
        }
        return 0;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getLatestStartingBalance(){
        Optional<Map<String,Integer>> mapOptional = getAccounts().stream()
                .filter(a->a.getId().equals(chosenAccountId))
                .map(Account::getMapOfStaringBalances)
                .findFirst();
        if(mapOptional.isPresent()){
            return  mapOptional.get().keySet().toArray(new String[0])[0];
        }
        return "[error loading latest period]";

    }*/

    public ScheduledTransaction getScheduledTransactionFromId(String id) {
        for (ScheduledTransaction scheduledTransaction : scheduledTransactions) {
            if (scheduledTransaction.getId().equals(id)) return scheduledTransaction;
        }
        return null;
    }
}
