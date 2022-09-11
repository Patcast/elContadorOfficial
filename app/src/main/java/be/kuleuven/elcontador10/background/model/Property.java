package be.kuleuven.elcontador10.background.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.background.database.Caching;

public class Property  implements Parcelable {
    private static final String TAG = "Add property fragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    private long equity;
    private long cash;
    private long sumOfPayables;
    private long sumOfReceivables;
    private long equityPending;
    private long sumOfPayablesPending;
    private long sumOfReceivablesPending;


    private String name;
    private String id;

    public Property(String name) {
        this.name = name;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Property(Parcel in) {
        this.equity = in.readLong();
        this.cash = in.readLong();
        this.sumOfPayables = in.readLong();
        this.sumOfReceivables = in.readLong();
        this.equityPending = in.readLong();
        this.sumOfPayablesPending = in.readLong();
        this.sumOfReceivablesPending = in.readLong();
        this.name = in.readString();
        this.id = in.readString();
    }
    public Property() {
    }

    public static final Creator<Property> CREATOR = new Creator<Property>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Property createFromParcel(Parcel in) {
            return new Property(in);
        }

        @Override
        public Property[] newArray(int size) {
            return new Property[size];
        }
    };

    public void addProperty(Property newProperty) {
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/properties";

        db.collection(url)
                .add(newProperty)
                .addOnSuccessListener(documentReference -> documentReference.update("id",documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding property document", e));
    }



    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public long calculatePropertySummary(){
        return (sumOfReceivables-sumOfPayables);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(equity);
        parcel.writeLong(sumOfPayables);
        parcel.writeLong(sumOfReceivables);
        parcel.writeLong(cash);
        parcel.writeLong(sumOfPayablesPending);
        parcel.writeLong(sumOfPayablesPending);
        parcel.writeLong(equityPending);

        parcel.writeString(name);
        parcel.writeString(id);
    }
}
