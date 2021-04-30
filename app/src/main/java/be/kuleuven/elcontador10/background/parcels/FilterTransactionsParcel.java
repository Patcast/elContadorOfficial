package be.kuleuven.elcontador10.background.parcels;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;

public class FilterTransactionsParcel implements Parcelable {
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

    protected FilterTransactionsParcel(Parcel in) {
        category = in.readString();
        subcategory = in.readString();
        name = in.readString();
    }

    public static final Creator<FilterTransactionsParcel> CREATOR = new Creator<FilterTransactionsParcel>() {
        @Override
        public FilterTransactionsParcel createFromParcel(Parcel in) {
            return new FilterTransactionsParcel(in);
        }

        @Override
        public FilterTransactionsParcel[] newArray(int size) {
            return new FilterTransactionsParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(subcategory);
        dest.writeString(name);
    }

    //Getters and Setters
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
