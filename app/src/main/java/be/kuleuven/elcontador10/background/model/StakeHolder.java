package be.kuleuven.elcontador10.background.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class StakeHolder implements Parcelable {
    private String id;
    private long balance;
    private String name;
    private String role;
    private boolean deleted;
    private String email;
    private int phoneNumber;
    private String idOfGlobalAccount;
    private boolean authorized;

    public StakeHolder(long balance, String name, String role, boolean deleted, String email, int phoneNumber, String idOfGlobalAccount, boolean authorized) {
        this.balance = balance;
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
        this.balance = in.readLong();
        this.name = in.readString();
        this.role = in.readString();
        this.deleted = in.readBoolean();
        this.email = in.readString();
        this.phoneNumber = in.readInt();
        this.idOfGlobalAccount = in.readString();
        this.authorized = in.readBoolean();
    }

    public StakeHolder() {    }

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
        parcel.writeDouble(balance);
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

    public long getBalance() {
        return balance;
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

}
