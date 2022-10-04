package be.kuleuven.elcontador10.background.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.Exclude;
import java.util.Calendar;
import java.util.List;

import be.kuleuven.elcontador10.background.Caching;

public class MonthlyRecords {
    private String id;
    private long equity;
    private long cash;
    private long sumOfPayables;
    private long sumOfReceivables;
    private long equityPending;
    private long sumOfPayablesPending;
    private long sumOfReceivablesPending;
    private long startingCash;
    private Integer cashOut;
    private Integer cashIn;

    private Calendar date;

    public MonthlyRecords() {
    }


    public String getId() {
        return id;
    }

    public long getEquity() {
        return equity;
    }

    public long getCash() {
        return cash;
    }

    public long getSumOfPayables() {
        return sumOfPayables;
    }

    public long getSumOfReceivables() {
        return sumOfReceivables;
    }

    public long getEquityPending() {
        return equityPending;
    }

    public long getSumOfPayablesPending() {
        return sumOfPayablesPending;
    }

    public long getSumOfReceivablesPending() {
        return sumOfReceivablesPending;
    }

    public long getStartingCash() {
        return startingCash;
    }

    @Exclude
    public Calendar getDate() {
        return date;
    }
    @Exclude
    public Integer getCashOut() {
        return cashOut;
    }
    @Exclude
    public Integer getCashIn() {
        return cashIn;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setDate() {
        date= Calendar.getInstance();
        date.set(Integer.parseInt(id.substring(0,4)),Integer.parseInt(id.substring(4,6)),1);
        setCashInAndOut(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setCashInAndOut (List<ProcessedTransaction> monthlyListOfProcessedTransactions) {
        if(monthlyListOfProcessedTransactions!=null){
            cashIn = monthlyListOfProcessedTransactions
                    .stream()
                    .filter(i-> !i.getIsDeleted())
                    .filter(i->i.getType().contains(Caching.INSTANCE.TYPE_CASH))
                    .map(ProcessedTransaction::getTotalAmount)
                    .filter(totalAmount -> totalAmount >=0)
                    .reduce(0, Integer::sum);
            cashOut = monthlyListOfProcessedTransactions
                    .stream()
                    .filter(i-> !i.getIsDeleted())
                    .filter(i->i.getType().contains(Caching.INSTANCE.TYPE_CASH))
                    .map(ProcessedTransaction::getTotalAmount)
                    .filter(totalAmount -> totalAmount <0)
                    .reduce(0, Integer::sum);
        }
        else{
            cashIn=0;
            cashOut=0;
        }

    }


}
