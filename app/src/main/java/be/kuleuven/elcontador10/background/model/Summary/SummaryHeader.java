package be.kuleuven.elcontador10.background.model.Summary;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import be.kuleuven.elcontador10.background.model.BalanceRecord;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

public class SummaryHeader {

    Map<String,Integer> summaryMap = new HashMap<>();
    private final List<ProcessedTransaction> monthlyListOfProcessedTransactions= new ArrayList<>();
    private final List<ScheduledTransaction> monthlyListOfScheduleTransactions= new ArrayList<>();
    private final List<BalanceRecord> listOfBalanceRecords = new ArrayList<>();
    private BalanceRecord selectedBalanceRecord;
    private final int selectedMonth ;
    private final int selectedYear ;
    private Integer startingBalance;
    private Integer closingBalance;
    private Integer sumOfPayables;
    private Integer sumOfReceivables;
    private Integer scheduleBalance;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public SummaryHeader(List<BalanceRecord> listOfBalanceRecords,List<ProcessedTransaction> monthlyListOfProcessedTransactions, List<ScheduledTransaction> monthlyListOfScheduleTransactions,int selectedMonth, int selectedYear) {
        this.listOfBalanceRecords.addAll(listOfBalanceRecords);
        this.monthlyListOfProcessedTransactions.addAll(monthlyListOfProcessedTransactions);
        this.monthlyListOfScheduleTransactions.addAll(monthlyListOfScheduleTransactions);
        this.selectedMonth = selectedMonth;
        this.selectedYear = selectedYear;
        makeMap();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void makeMap() {
        setSelectedBalanceRecord();
        summaryMap.put("startingBalance",getStartingBalance());
        summaryMap.put("currentBalance",getClosingBalance());
        summaryMap.put("receivables",getSumOfReceivables());
        summaryMap.put("payables",getSumOfPayables());
        summaryMap.put("scheduleBalance",getScheduleBalance());
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

    private void setNetScheduleBalance() {
        scheduleBalance=  (closingBalance != null? closingBalance:0) + getSumOfPayables()+getSumOfReceivables();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setStartingAndClosingBalance(){
        if(selectedBalanceRecord!=null){
            Timestamp currentDate = Timestamp.now();
            int currentMonth =currentDate.toDate().getMonth()+1;
            int currentYear =currentDate.toDate().getYear()+1900;
            if ((selectedYear< currentYear)||(selectedYear== currentYear&&selectedMonth<=currentMonth)){
                startingBalance = (int) selectedBalanceRecord.getStartingBalance();
                closingBalance = monthlyListOfProcessedTransactions
                        .stream()
                        .filter(i-> !i.getIsDeleted())
                        .map(ProcessedTransaction::getTotalAmount)
                        .reduce(startingBalance, Integer::sum);
            }
        }
        else {
            startingBalance = null;
            closingBalance = null;
        }
        setScheduleBalances();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setScheduleBalances(){
        int totalAmountToPay;
        int totalAmountPaid;

            totalAmountToPay = monthlyListOfScheduleTransactions.stream()
                    .map(ScheduledTransaction::getTotalAmount)
                    .filter(totalAmount -> totalAmount < 0)
                    .reduce(0, Integer::sum);
            totalAmountPaid = monthlyListOfScheduleTransactions.stream()
                    .map(ScheduledTransaction::getAmountPaid)
                    .filter(totalAmount -> totalAmount < 0)
                    .reduce(0, Integer::sum);
            sumOfPayables =totalAmountToPay-totalAmountPaid;

            totalAmountToPay = monthlyListOfScheduleTransactions.stream()
                    .map(ScheduledTransaction::getTotalAmount)
                    .filter(totalAmount -> totalAmount > 0)
                    .reduce(0, Integer::sum);
            totalAmountPaid = monthlyListOfScheduleTransactions.stream()
                    .map(ScheduledTransaction::getAmountPaid)
                    .filter(totalAmount -> totalAmount > 0)
                    .reduce(0, Integer::sum);
            sumOfReceivables =totalAmountToPay-totalAmountPaid;
            setNetScheduleBalance();

    }


    public Integer getStartingBalance() {
        return startingBalance;
    }

    public Integer getClosingBalance() {
        return closingBalance;
    }

    public Integer getSumOfPayables() {
        return sumOfPayables;
    }

    public Integer getSumOfReceivables() {
        return sumOfReceivables;
    }

    public Integer getScheduleBalance() {
        return scheduleBalance;
    }

    public Map<String, Integer> getSummaryMap() {
        return summaryMap;
    }
}
