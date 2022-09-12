package be.kuleuven.elcontador10.background.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.kuleuven.elcontador10.background.database.Caching;


public class SummaryHeader {

    Map<String,Integer> summaryMap = new HashMap<>();
    private final List<ProcessedTransaction> monthlyListOfProcessedTransactions= new ArrayList<>();
    private final List<BalanceRecord> listOfBalanceRecords = new ArrayList<>();
    private BalanceRecord selectedBalanceRecord;
    private final int selectedMonth ;
    private final int selectedYear ;
    private Integer startingCash;
    private Integer closingCash;
    private Integer sumOfPayables;
    private Integer sumOfReceivables;
    private Integer equity;
    private Integer cashOut;
    private Integer cashIn;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public SummaryHeader(List<BalanceRecord> listOfBalanceRecords,List<ProcessedTransaction> monthlyList,int selectedMonth, int selectedYear,Integer sumOfPayables, Integer sumOfReceivables, Integer equity ) {
        this.listOfBalanceRecords.addAll(listOfBalanceRecords);
        this.monthlyListOfProcessedTransactions.addAll(monthlyList);
        this.selectedMonth = selectedMonth;
        this.selectedYear = selectedYear;
        this.sumOfPayables = sumOfPayables;
        this.sumOfReceivables = sumOfReceivables;
        this.equity = equity;
        makeMap();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void makeMap() {
        setSelectedBalanceRecord();
        summaryMap.put("startingBalance", getStartingCash());
        summaryMap.put("cashIn",getCashIn());
        summaryMap.put("cashOut",getCashOut());
        summaryMap.put("currentBalance", getClosingCash());
        summaryMap.put("receivables",getSumOfReceivables());
        summaryMap.put("payables",getSumOfPayables());
        summaryMap.put("scheduleBalance", getEquity());
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setSelectedBalanceRecord(){
        Optional<BalanceRecord> selectedRecord = listOfBalanceRecords
                .stream()
                .filter(i-> i.getDate().toDate().getMonth()+1==selectedMonth)
                .filter(i-> i.getDate().toDate().getYear() + 1900==selectedYear)
                .findFirst();
       selectedBalanceRecord =selectedRecord.orElse(null);
        setStartingAndClosingBalance();

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setStartingAndClosingBalance(){
        if(selectedBalanceRecord!=null){
            Timestamp currentDate = Timestamp.now();
            int currentMonth =currentDate.toDate().getMonth()+1;
            int currentYear =currentDate.toDate().getYear()+1900;
            if ((selectedYear< currentYear)||(selectedYear== currentYear&&selectedMonth<=currentMonth)){
                startingCash = (int) selectedBalanceRecord.getStartingBalance();
                closingCash = monthlyListOfProcessedTransactions
                        .stream()
                        .filter(i-> !i.getIsDeleted())
                        .filter(i->i.getType().contains(Caching.INSTANCE.TYPE_CASH))
                        .map(ProcessedTransaction::getTotalAmount)
                        .reduce(startingCash, Integer::sum);
                setCashInAndOut();// only called if valid
            }
        }
        else {
            startingCash = null;
            closingCash = null;
            cashIn = null;
            cashOut = null;
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setCashInAndOut() {
        cashIn = monthlyListOfProcessedTransactions
                .stream()
                .filter(i-> !i.getIsDeleted())
                .map(ProcessedTransaction::getTotalAmount)
                .filter(totalAmount -> totalAmount >=0)
                .reduce(0, Integer::sum);
        cashOut = monthlyListOfProcessedTransactions
                .stream()
                .filter(i-> !i.getIsDeleted())
                .map(ProcessedTransaction::getTotalAmount)
                .filter(totalAmount -> totalAmount <0)
                .reduce(0, Integer::sum);
    }


    public Integer getStartingCash() {
        return startingCash;
    }

    public Integer getClosingCash() {
        return closingCash;
    }

    public Integer getSumOfPayables() {
        return sumOfPayables;
    }

    public Integer getSumOfReceivables() {
        return sumOfReceivables;
    }

    public Integer getEquity() {
        return equity;
    }

    public Map<String, Integer> getSummaryMap() {
        return summaryMap;
    }

    public Integer getCashOut() {
        return cashOut;
    }

    public Integer getCashIn() {
        return cashIn;
    }
}
