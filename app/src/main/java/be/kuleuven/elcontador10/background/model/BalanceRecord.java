package be.kuleuven.elcontador10.background.model;

import com.google.firebase.Timestamp;

public class BalanceRecord {
    private Long startingBalance;

    private Timestamp date;

    public BalanceRecord() {
    }

    public BalanceRecord(Long startingBalance, Timestamp date) {
        this.startingBalance = startingBalance;
        this.date = date;
    }

    public long getStartingBalance() {
        return startingBalance;
    }

    public Timestamp getDate() {
        return date;
    }
}
