package be.kuleuven.elcontador10.background.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.background.database.Caching;

public class StakeHolder implements Parcelable {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "microAccount";

    private String id;
    private String name;
    private String role;
    private boolean deleted;
    private String email;
    private int phoneNumber;
    private String idOfGlobalAccount;
    private boolean authorized;
    private long equity;
    private long cash;
    private long sumOfPayables;
    private long sumOfReceivables;
    private long equityPending;
    private long sumOfPayablesPending;
    private long sumOfReceivablesPending;




    public StakeHolder(long balance, String name, String role, boolean deleted, String email, int phoneNumber, String idOfGlobalAccount, boolean authorized) {
        this.equity = balance;
        this.name = name;
        this.role = role;
        this.deleted = deleted;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.idOfGlobalAccount = idOfGlobalAccount;
        this.authorized = authorized;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected StakeHolder(Parcel in) {
        this.equity = in.readLong();
        this.name = in.readString();
        this.role = in.readString();
        this.deleted = in.readBoolean();
        this.email = in.readString();
        this.phoneNumber = in.readInt();
        this.idOfGlobalAccount = in.readString();
        this.authorized = in.readBoolean();
    }

    public StakeHolder(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public StakeHolder() {    }

    public void addAccount() {
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders";

        db.collection(url)
                .add(this)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public void editAccount() {
        String url = "/accounts/" + Caching.INSTANCE.getChosenAccountId() + "/stakeHolders/" + getId();

        db.document(url)
                .update("name", getName(), "role", getRole())
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Document edited with ID: " + getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static final Creator<StakeHolder> CREATOR = new Creator<StakeHolder>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public StakeHolder createFromParcel(Parcel parcel) {
            return new StakeHolder(parcel);
        }

        @Override
        public StakeHolder[] newArray(int i) {
            return new StakeHolder[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(equity);
        parcel.writeString(name);
        parcel.writeString(role);
        parcel.writeBoolean(deleted);
        parcel.writeString(email);
        parcel.writeInt(phoneNumber);
        parcel.writeString(idOfGlobalAccount);
        parcel.writeBoolean(authorized);
    }

    public String getIdOfGlobalAccount() {
        return idOfGlobalAccount;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public String getId() {
        return id;
    }



    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getEmail() {
        return email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setEquity(int equity) {
        this.equity = equity;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
