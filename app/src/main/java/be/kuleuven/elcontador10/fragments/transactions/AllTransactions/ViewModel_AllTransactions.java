package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.MonthlyRecords;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.StakeHolder;

public class ViewModel_AllTransactions extends ViewModel {
    private static final String TAG = "All Transactions VM";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setInitialData() throws ParseException {
        initialiseCalendarFilter();
        initialiseBooleanFilter();
        requestGroupOFStakeHolders(Caching.INSTANCE.getChosenAccountId());
    }



    /// Querying lists of transactions
    private final MutableLiveData<List<ProcessedTransaction>> monthlyListOfProcessedTransactions = new MutableLiveData<>();
    public void resetListOfTransactions(){
        monthlyListOfProcessedTransactions.setValue(null);
    }
    public LiveData<List<ProcessedTransaction>> getMonthlyListOfProcessedTransactions() {
        return monthlyListOfProcessedTransactions;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestListOfProcessedTransactions(MonthlyRecords inputMonthlyRecord) throws ParseException {

        if(inputMonthlyRecord!=null){
            Calendar cal = inputMonthlyRecord.getDate();

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date dateBottom = dateFormat.parse("01/"+(cal.get(Calendar.MONTH)+1)+"/"+(cal.get(Calendar.YEAR)));
            assert dateBottom != null;
            Timestamp dateSelectedBottom = new Timestamp(dateBottom);
            cal.add(Calendar.MONTH,1);
            Date dateEnd = dateFormat.parse("01/"+(cal.get(Calendar.MONTH)+1)+"/"+(cal.get(Calendar.YEAR)));
            assert dateEnd != null;
            Timestamp dateSelectedTop = new Timestamp(dateEnd);
            String urlGetAccountTransactions = "/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/transactions";
            Query transactionsFromOneAccount = db.
                    collection(urlGetAccountTransactions).
                    whereGreaterThanOrEqualTo("dueDate", dateSelectedBottom).
                    whereLessThan("dueDate", dateSelectedTop);
            transactionsFromOneAccount.addSnapshotListener((value, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                List<ProcessedTransaction> listTrans = new ArrayList<>();
                assert value != null;
                for (QueryDocumentSnapshot doc : value) {
                    ProcessedTransaction myTransaction =  doc.toObject(ProcessedTransaction.class);
                    myTransaction.setId(doc.getId());
                    listTrans.add(myTransaction);
                }
                if(monthlyListOfProcessedTransactions.getValue()!=null)monthlyListOfProcessedTransactions.getValue().clear();
                monthlyListOfProcessedTransactions.setValue(listTrans);
                inputMonthlyRecord.setCashInAndOut(listTrans);
                cal.add(Calendar.MONTH,-1);
                try {
                    setSelectedMonthlyRecord(inputMonthlyRecord);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                setAllChosenTransactions();
            });
        }
    }

    /// List of transactions displayed
    private final MutableLiveData<List<ProcessedTransaction>> allChosenTransactions = new MutableLiveData<>();
    public LiveData<List<ProcessedTransaction>> getAllChosenTransactions() {
        return allChosenTransactions;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAllChosenTransactions(){
        allChosenTransactions.setValue(filterTransactions());
    }





    /// Boolean filter
    @RequiresApi(api = Build.VERSION_CODES.N)
    private final MutableLiveData<Map<String,Boolean>> booleanFilter = new MutableLiveData<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    public LiveData<Map<String,Boolean>> getBooleanFilter() {
        return booleanFilter;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setBooleanFilter(Map<String,Boolean> selectedTypes){
        booleanFilter.setValue(selectedTypes);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialiseBooleanFilter() {
        HashMap<String, Boolean> transTypes = new HashMap<>();
        transTypes.put("transaction",true);
        transTypes.put("receivable",false);
        transTypes.put("payable",false);
        transTypes.put("deletedTrans",false);
        setBooleanFilter(transTypes);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<ProcessedTransaction> filterTransactions() {
        List<ProcessedTransaction> listAllTransactionsFiltered = new ArrayList<>();
        if (booleanFilter.getValue() != null) {
            if (Boolean.TRUE.equals(booleanFilter.getValue().get("transaction")))
                listAllTransactionsFiltered.addAll(
                        Objects.requireNonNull(
                                        monthlyListOfProcessedTransactions.getValue())
                                .stream()
                                .filter(i -> !i.getIsDeleted())
                                .filter(i -> i.getType().contains(Caching.INSTANCE.TYPE_CASH))
                                .collect(Collectors.toList())
                );
            if (Boolean.TRUE.equals(booleanFilter.getValue().get("receivable")))
                listAllTransactionsFiltered.addAll(
                        Objects.requireNonNull(
                                        monthlyListOfProcessedTransactions.getValue())
                                .stream()
                                .filter(i -> !i.getIsDeleted())
                                .filter(i -> i.getType().contains(Caching.INSTANCE.TYPE_RECEIVABLES))
                                .collect(Collectors.toList())
                );
            if (Boolean.TRUE.equals(booleanFilter.getValue().get("payable")))
                listAllTransactionsFiltered.addAll(
                        Objects.requireNonNull(
                                monthlyListOfProcessedTransactions.getValue())
                                .stream()
                                .filter(i -> !i.getIsDeleted())
                                .filter(i -> i.getType().contains(Caching.INSTANCE.TYPE_PAYABLES))
                                .collect(Collectors.toList())
                );
            if (Boolean.TRUE.equals(booleanFilter.getValue().get("deletedTrans")))
                listAllTransactionsFiltered.addAll(
                        Objects.requireNonNull(
                                        monthlyListOfProcessedTransactions.getValue())
                                .stream()
                                .filter(ProcessedTransaction::getIsDeleted)
                                .collect(Collectors.toList())
                );
        }
        return listAllTransactionsFiltered
                .stream()
                .sorted(Comparator.comparing(ProcessedTransaction::getDueDate).reversed())
                .collect(Collectors.toList());
    }

    // STAKEHOLDERS
    private final MutableLiveData<List<StakeHolder>> stakeholderList = new MutableLiveData<>();
    public LiveData<List<StakeHolder>> getStakeholdersList() {
        return stakeholderList;
    }
    public void setStakeholdersList(List<StakeHolder> inputStakeholders){
        stakeholderList.setValue(inputStakeholders);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestGroupOFStakeHolders(String chosenAccountId){
        String stakeHoldersUrl = "/accounts/"+chosenAccountId+"/stakeHolders";
        Query getStakeHolders = db.collection(stakeHoldersUrl)
                .orderBy("name", Query.Direction.ASCENDING);

        getStakeHolders.addSnapshotListener((value, e) -> {
            if (e != null) {
                return;
            }
            List<StakeHolder> list = new ArrayList<>();
            assert value != null;
            for (QueryDocumentSnapshot doc : value) {
                StakeHolder myStakeHolder =  doc.toObject(StakeHolder.class);
                myStakeHolder.setId(doc.getId());
                list.add(myStakeHolder);
            }
            setStakeholdersList(list);
            Caching.INSTANCE.setStakeholderList(list);
        });
    }

    ///////// CALENDAR RELATED ////////////***************


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialiseCalendarFilter() {
       resetSelectedMonthlyRecord();
       requestMonthlyRecords();
    }
    public final List<MonthlyRecords> listOfMonthlyRecords = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestMonthlyRecords(){
        Query monthlyRecordsFromOneAccount = db
                .collection("/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/monthlyRecords");
        monthlyRecordsFromOneAccount.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            assert value != null;
            listOfMonthlyRecords.clear();
            for (QueryDocumentSnapshot doc : value) {
                MonthlyRecords record = doc.toObject(MonthlyRecords.class);
                record.setDate();
                listOfMonthlyRecords.add(record);
            }
            try {
                if(selectedMonthlyRecord.getValue()!=null) setSelectedMonthlyRecord(listOfMonthlyRecords.stream().filter(m->m.getId().equals((selectedMonthlyRecord.getValue()).getId())).findFirst().orElse(null));
                else requestListOfProcessedTransactions(getLatestMonthlyRecord());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        });
    }

    private final MutableLiveData<MonthlyRecords> selectedMonthlyRecord = new MutableLiveData<>();
    public LiveData<MonthlyRecords> getSelectedMonthlyRecord() {
        return selectedMonthlyRecord;
    }
    public void resetSelectedMonthlyRecord(){
        selectedMonthlyRecord.setValue(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private MonthlyRecords getLatestMonthlyRecord(){
        Optional<MonthlyRecords> monthlyRecordsOptional = listOfMonthlyRecords.stream().max(Comparator.comparing(MonthlyRecords::getDate));
        return monthlyRecordsOptional.orElse(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setSelectedMonthlyRecord(MonthlyRecords monthlyRecord) throws ParseException { ///
        if(monthlyListOfProcessedTransactions.getValue()!=null){
           selectedMonthlyRecord.setValue(monthlyRecord);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MonthlyRecords findMonthlyRecord(int month, int year){
        Optional<MonthlyRecords> selectedRecord = listOfMonthlyRecords
                .stream()
                .filter(i-> i.getDate().get(Calendar.MONTH) == month)
                .filter(i-> i.getDate().get(Calendar.YEAR) == year)
                .findFirst();
        return selectedRecord.orElse(null);

    }















}
