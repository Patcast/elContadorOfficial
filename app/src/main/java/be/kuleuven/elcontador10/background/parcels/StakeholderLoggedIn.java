package be.kuleuven.elcontador10.background.parcels;

import android.os.Parcel;
import android.os.Parcelable;

public class StakeholderLoggedIn implements Parcelable {
    private int id;
    private String firstName;
    private String lastName;
    private String role;
    private String phoneNumber;
    private String email;
    private double balance;
    private String username;

    public static final Parcelable.Creator<StakeholderLoggedIn> CREATOR = new Creator<StakeholderLoggedIn>() {
        @Override
        public StakeholderLoggedIn createFromParcel(Parcel source) {
            return new StakeholderLoggedIn(
                    source.readInt(), source.readString(), source.readString(), source.readString(),
                    source.readString(), source.readString(), source.readDouble(), source.readString());
        }

        @Override
        public StakeholderLoggedIn[] newArray(int size) {
            return new StakeholderLoggedIn[size];
        }
    };

    public StakeholderLoggedIn(int id, String firstName, String lastName, String role,
                               String phoneNumber, String email, double balance, String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.balance = balance;
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(role);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeDouble(balance);
        dest.writeString(username);
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public double getBalance() {
        return balance;
    }

    public String getUsername() {
        return username;
    }
}