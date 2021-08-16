package be.kuleuven.elcontador10.background.model.contract;

import java.util.ArrayList;

public class Contract {
    private String id;
    private String title;
    private String notes;
    private ArrayList<Payment> payments;

    public Contract(String id, String title, String notes) {
        this.id = id;
        this.title = title;
        this.notes = notes;
        this.payments = new ArrayList<>();
    }

    public Contract() {}

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }
}
