package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.BalanceRecord;
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Summary.SummaryHeader;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

public class ViewModel_AllTransactions extends ViewModel {
    private static final String TAG = "All Transactions VM";
    //ChosenTypesOfTransactions
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<ProcessedTransaction> monthlyListOfProcessedTransactions = new ArrayList<>();
    private final List<ProcessedTransaction> ListOfDeletedTransactions = new ArrayList<>();
    private final List<ScheduledTransaction> monthlyListOfScheduleTransactions = new ArrayList<>();
    private final List<BalanceRecord> listOfBalanceRecords = new ArrayList<>();
    /// Boolean filter
    private final MutableLiveData<Map<String,Boolean>> booleanFilter = new MutableLiveData<>();
    public LiveData<Map<String,Boolean>> getBooleanFilter() {
        return booleanFilter;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setBooleanFilter(Map<String,Boolean> selectedTypes){
        booleanFilter.setValue(selectedTypes);
    }

    /// Calendar filter
    private final MutableLiveData<Map<String, Integer>> calendarFilter = new MutableLiveData<>();
    public LiveData<Map<String, Integer>> getCalendarFilter() {
        return calendarFilter;
    }
    public void setCalendarFilter(Map<String, Integer> selectedDate){
        calendarFilter.setValue(selectedDate);
    }

    /// List of transactions displayed
    private final MutableLiveData<List<TransactionInterface>> allChosenTransactions = new MutableLiveData<>();
    public LiveData<List<TransactionInterface>> getAllChosenTransactions() {
        return allChosenTransactions;
    }

    /// Map of summary values
    private final MutableLiveData<Map<String, Integer>> mapOfMonthlySummaryValues = new MutableLiveData<>();
    public LiveData<Map<String, Integer>> getMapOfMonthlySummaryValues() {
        return mapOfMonthlySummaryValues;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setMapOfSummary() {
            SummaryHeader header = new SummaryHeader(
                    listOfBalanceRecords,
                    monthlyListOfProcessedTransactions,
                    monthlyListOfScheduleTransactions,
                    calendarFilter.getValue().get("month"),
                    calendarFilter.getValue().get("year"));
            mapOfMonthlySummaryValues.setValue(header.getSummaryMap());
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestBalanceRecords(){
        Query transactionsFromOneAccount = db
                .collection("/accounts/"+Caching.INSTANCE.getChosenAccountId()+"/balanceRecords");
        transactionsFromOneAccount.addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            List<BalanceRecord> listRec = new ArrayList<>();
            for (QueryDocumentSnapshot doc : value) {
                Map<String, Object> myRecordMap =  doc.getData();
                listRec.add(new BalanceRecord((Long)myRecordMap.get("startingBalance"),(Timestamp) myRecordMap.get("date")));
            }
            listOfBalanceRecords.clear();
            listOfBalanceRecords.addAll(listRec);
            setMapOfSummary();
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Timestamp getFirstStartingBalanceTimeStamp(){
        Optional<Timestamp> time =  listOfBalanceRecords
                .stream()
                .sorted(Comparator.comparing(BalanceRecord::getDate))
                .map(i -> i.getDate())
                .findFirst();
        return time.orElse(null);
    }


    /// Querying lists of transactions
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectListOfProcessedTransactions() throws ParseException {

        int month = getCalendarFilter().getValue().get("month");
        int year = getCalendarFilter().getValue().get("year");// this will cause trouble
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateEnd = dateFormat.parse("01/"+(month+1)+"/"+year);
        Timestamp dateSelectedTop = new Timestamp(dateEnd);
        Date dateBottom = dateFormat.parse("01/"+month+"/"+year);
        Timestamp dateSelectedBottom = new Timestamp(dateBottom);

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
                for (QueryDocumentSnapshot doc : value) {
                    ProcessedTransaction myTransaction =  doc.toObject(ProcessedTransaction.class);
                    myTransaction.setId(doc.getId());
                    listTrans.add(myTransaction);
                }
                monthlyListOfProcessedTransactions.clear();
                monthlyListOfProcessedTransactions.addAll(listTrans);

                setListOfTransactions();
            });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectScheduleTransactions() throws ParseException {
        int month = getCalendarFilter().getValue().get("month");
        int year = getCalendarFilter().getValue().get("year");// this will cause trouble
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateEnd = dateFormat.parse("01/"+(month+1)+"/"+year);
        Timestamp dateSelectedTop = new Timestamp(dateEnd);
        //Date dateBottom = dateFormat.parse("01/"+month+"/"+year);
        //Timestamp dateSelectedBottom = new Timestamp(dateBottom);

        db.collectionGroup("scheduledTransactions").
                whereEqualTo("idOfAccount", Caching.INSTANCE.getChosenAccountId()).
                whereEqualTo("completed", false).
               // whereGreaterThanOrEqualTo("dueDate", dateSelectedBottom).
                whereLessThan("dueDate", dateSelectedTop).
                addSnapshotListener((value,e ) -> {

                    if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                     }
                    List<ScheduledTransaction> listTransSchedule = new ArrayList<>();
                    monthlyListOfScheduleTransactions.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        ScheduledTransaction myTransaction =  doc.toObject(ScheduledTransaction.class);
                        myTransaction.setId(doc.getId());
                        myTransaction.setPath(doc.getReference().getPath());
                        listTransSchedule.add(myTransaction);
                    }
                    monthlyListOfScheduleTransactions.addAll(listTransSchedule);
                    Caching.INSTANCE.setScheduledTransactions(listTransSchedule);
                    requestGroupOFStakeHolders(Caching.INSTANCE.getChosenAccountId()); // updates the balance of each stakeholder.
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setInitialData() throws ParseException {
        requestBalanceRecords();
        initialiseCalendarFilter();
        initialiseBooleanFilter();
        selectScheduleTransactions();
        selectListOfProcessedTransactions();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setListOfTransactions(){
        allChosenTransactions.setValue(filterTransactions());
        setMapOfSummary();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<TransactionInterface> filterTransactions() {
        List<TransactionInterface> listAllTransactionsFiltered = new ArrayList<>();
        if(booleanFilter.getValue().get("transaction"))
        {
            listAllTransactionsFiltered.addAll(monthlyListOfProcessedTransactions.stream().filter(i-> !i.getIsDeleted()).collect(Collectors.toList()));
        }
        if(booleanFilter.getValue().get("receivable"))
        {
            listAllTransactionsFiltered.addAll(monthlyListOfScheduleTransactions.stream().filter(i->i.getTotalAmount()>0).collect(Collectors.toList()));
        }
        if(booleanFilter.getValue().get("payable"))
        {
            listAllTransactionsFiltered.addAll(monthlyListOfScheduleTransactions.stream().filter(i->i.getTotalAmount()<0).collect(Collectors.toList()));
        }
        if(booleanFilter.getValue().get("deletedTrans"))
        {
            listAllTransactionsFiltered.addAll(monthlyListOfProcessedTransactions.stream().filter(ProcessedTransaction::getIsDeleted).collect(Collectors.toList()));
        }
       return listAllTransactionsFiltered.stream().
                                                sorted(Comparator.comparing(TransactionInterface::getDueDate).reversed()).
                                                collect(Collectors.toList());
    }

    private void initialiseCalendarFilter() {
        Calendar cal = Calendar.getInstance();
        Map<String,Integer> chosenDateMap = new HashMap<>();
        chosenDateMap.put("month",cal.get(Calendar.MONTH)+1);
        chosenDateMap.put("year",cal.get(Calendar.YEAR));
        setCalendarFilter(chosenDateMap);
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
    public void resetListOfTransactions() {
        monthlyListOfScheduleTransactions.clear();
        monthlyListOfProcessedTransactions.clear();
    }
    ////////////////  QUERY ALL STAKEHOLDERS



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
            updateBalancesStakeHolders(list);
            setStakeholdersList(list);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateBalancesStakeHolders(List<StakeHolder> list) {
            for (StakeHolder stakeholder : list) {
                // get past / now scheduled transactions for the stakeholder
                List <ScheduledTransaction> stakeholderScheduledTransactions =
                        monthlyListOfScheduleTransactions.stream()
                                                .filter(transaction -> transaction.getIdOfStakeInt().equals(stakeholder.getId()))
                                                .filter(transaction -> transaction.getDueDate().getSeconds() <= Timestamp.now().getSeconds())
                                                .collect(Collectors.toList());

                int sum = stakeholderScheduledTransactions.stream()
                        .mapToInt(ScheduledTransaction::getTotalAmount)
                        .sum();

                int paid = stakeholderScheduledTransactions.stream()
                        .mapToInt(ScheduledTransaction::getAmountPaid)
                        .sum();

                stakeholder.setBalance(sum - paid);
            }
    }

    public List<ProcessedTransaction> getMonthlyListOfProcessedTransactions() {
        return monthlyListOfProcessedTransactions;
    }

    public List<ScheduledTransaction> getMonthlyListOfScheduleTransactions() {
        return monthlyListOfScheduleTransactions;
    }

}
