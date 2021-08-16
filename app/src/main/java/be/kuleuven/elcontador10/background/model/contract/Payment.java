package be.kuleuven.elcontador10.background.model.contract;

import java.sql.Timestamp;

public class Payment {
    private String id;
    private String title;
    private long amount;
    private Timestamp start;
    private Timestamp end;
    private String frequency;
    private String notes;

    public Payment(String id, String title, long amount, Timestamp start, Timestamp end, String frequency, String notes) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.start = start;
        this.end = end;
        this.frequency = frequency;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
