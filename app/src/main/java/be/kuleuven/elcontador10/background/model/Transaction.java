package be.kuleuven.elcontador10.background.model;

import com.google.type.DateTime;

public class Transaction {
    private double amount;
    private String registeredBy;
    private String stakeHolder;
    private String txtComments;
    private DateTime date;
    private boolean deleted;
    private String notes;
    private String category;
    private String subCategory;
    private String id;

    public Transaction() {
    }

    public Transaction(double amount, String registeredBy, String stakeHolder, String txtComments, DateTime date, boolean deleted, String notes, String category, String subCategory, String id) {
        this.amount = amount;
        this.registeredBy = registeredBy;
        this.stakeHolder = stakeHolder;
        this.txtComments = txtComments;
        this.date = date;
        this.deleted = deleted;
        this.notes = notes;
        this.category = category;
        this.subCategory = subCategory;
        this.id = id;
    }

    public Transaction(double amount, String registeredBy, String stakeHolder, String txtComments, DateTime date, boolean deleted, String notes, String category, String subCategory) {
        this.amount = amount;
        this.registeredBy = registeredBy;
        this.stakeHolder = stakeHolder;
        this.txtComments = txtComments;
        this.date = date;
        this.deleted = deleted;
        this.notes = notes;
        this.category = category;
        this.subCategory = subCategory;
    }

    public double getAmount() {
        return amount;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public String getStakeHolder() {
        return stakeHolder;
    }

    public String getTxtComments() {
        return txtComments;
    }

    public DateTime getDate() {
        return date;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getNotes() {
        return notes;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}



