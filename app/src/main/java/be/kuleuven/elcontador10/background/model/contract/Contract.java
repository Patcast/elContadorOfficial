package be.kuleuven.elcontador10.background.model.contract;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Contract {
    private String id;
    private String title;
    private String microAccount;
    private String registeredBy;
    private Timestamp registerDate;
    private String notes;
    private ArrayList<Payment> payments;

    public Contract(String title, String registeredBy, String notes) {
        this.title = title;
        this.registeredBy = registeredBy;
        this.registerDate = new Timestamp(new Date()); // now
        this.notes = notes;
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

    public String getMicroAccount() {
        return microAccount;
    }

    public void setMicroAccount(String microAccount) {
        this.microAccount = microAccount;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public Timestamp getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Timestamp registerDate) {
        this.registerDate = registerDate;
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
