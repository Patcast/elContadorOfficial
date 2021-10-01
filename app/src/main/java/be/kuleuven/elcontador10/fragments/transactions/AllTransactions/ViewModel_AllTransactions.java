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
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

public class ViewModel_AllTransactions extends ViewModel {
    private static final String TAG = "All Transactions VM";
    //ChosenTypesOfTransactions
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<ProcessedTransaction> monthlyListOfProcessedTransactions = new ArrayList<>();
    private final List<ScheduledTransaction> monthlyListOfScheduleTransactions = new ArrayList<>();

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
        Map<String,Integer>  summaryMap = new HashMap<>();
        int startingBalance = Caching.INSTANCE.getStartingBalance(calendarFilter.getValue().get("month"),calendarFilter.getValue().get("year"));
        summaryMap.put("startingBalance",startingBalance);
        int currentBalance = monthlyListOfProcessedTransactions.stream()
                .map(ProcessedTransaction::getTotalAmount)
                .filter(totalAmount -> totalAmount >0)
                .reduce(startingBalance, Integer::sum);
        summaryMap.put("currentBalance",currentBalance);
        summaryMap.put("receivables",monthlyListOfScheduleTransactions.stream()
                .map(ScheduledTransaction::getTotalAmount)
                .filter(totalAmount -> totalAmount >0)
                .reduce(0, Integer::sum));
        summaryMap.put("payables",monthlyListOfScheduleTransactions.stream()
                .map(ScheduledTransaction::getTotalAmount)
                .filter(totalAmount -> totalAmount <0)
                .reduce(0, Integer::sum));
        summaryMap.put("scheduleBalance",monthlyListOfScheduleTransactions.stream()
                .map(ScheduledTransaction::getTotalAmount)
                .reduce(currentBalance, Integer::sum));
        mapOfMonthlySummaryValues.setValue(summaryMap);
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
                    whereGreaterThanOrEqualTo("date", dateSelectedBottom).
                    whereLessThan("date", dateSelectedTop);
            transactionsFromOneAccount.addSnapshotListener((value, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                List<ProcessedTransaction> listTrans = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    ProcessedTransaction myTransaction =  doc.toObject(ProcessedTransaction.class);
                    myTransaction.setId( doc.getId());
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
        Date dateBottom = dateFormat.parse("01/"+month+"/"+year);
        Timestamp dateSelectedBottom = new Timestamp(dateBottom);

        db.collectionGroup("scheduledTransactions").
                whereEqualTo("idOfAccount", Caching.INSTANCE.getChosenAccountId()).
                whereGreaterThanOrEqualTo("date", dateSelectedBottom).
                whereLessThan("date", dateSelectedTop).
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
                        listTransSchedule.add(myTransaction);
                    }
                    monthlyListOfScheduleTransactions.addAll(listTransSchedule);
                    setListOfTransactions();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setInitialData() throws ParseException {
        initialiseCalendarFilter();
        initialiseBooleanFilter();
        selectScheduleTransactions();
        selectListOfProcessedTransactions( );
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
            listAllTransactionsFiltered.addAll(monthlyListOfProcessedTransactions);
        }
        if(booleanFilter.getValue().get("receivable"))
        {
            listAllTransactionsFiltered.addAll(monthlyListOfScheduleTransactions.stream().filter(i->i.getTotalAmount()>0).collect(Collectors.toList()));
        }
        if(booleanFilter.getValue().get("payable"))
        {
            listAllTransactionsFiltered.addAll(monthlyListOfScheduleTransactions.stream().filter(i->i.getTotalAmount()<0).collect(Collectors.toList()));
        }

       return listAllTransactionsFiltered.stream().
                                                sorted(Comparator.comparing(TransactionInterface::getDate)).
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
        setBooleanFilter(transTypes);
    }
    public void resetListOfTransactions() {
        monthlyListOfScheduleTransactions.clear();
        monthlyListOfProcessedTransactions.clear();
    }
}
