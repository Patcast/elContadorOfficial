package be.kuleuven.elcontador10.background.model;

import com.google.firebase.Timestamp;

public class BalanceRecord {
    private Long startingBalance;
    private Long closingBalance;
    private Timestamp date;

    public BalanceRecord() {
    }

    public BalanceRecord(Long startingBalance, Long closingBalance, Timestamp date) {
        this.startingBalance = startingBalance;
        this.closingBalance = closingBalance;
        this.date = date;
    }

    public long getStartingBalance() {
        return startingBalance;
    }

    public long getClosingBalance() {
        return closingBalance;
    }

    public Timestamp getDate() {
        return date;
    }
}
