package be.kuleuven.elcontador10.background.parcels;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;

public class FilterTransactionsParcel {
    private String category;
    private String subcategory;
    private String name;
    private LocalDateTime from;
    private LocalDateTime to;

    public FilterTransactionsParcel(String category, String subcategory, String name, @Nullable LocalDateTime from, @Nullable LocalDateTime to) {
        this.category = category; // if * all
        this.subcategory = subcategory;
        this.name = name;
        this.from = from; // nullable
        this.to = to;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }
}
