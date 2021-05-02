package be.kuleuven.elcontador10.background.parcels;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class FilterStakeholdersParcel implements Parcelable {
    private String name;

    // roles
    private ArrayList<String> roles;

    // attributes
    private boolean inDebt;

    public FilterStakeholdersParcel(@Nullable String name, ArrayList<String> roles, boolean inDebt) {
        this.name = name;
        this.roles = roles;
        this.inDebt = inDebt;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected FilterStakeholdersParcel(Parcel in) {
        name = in.readString();
        roles = (ArrayList<String>) in.readArrayList(getClass().getClassLoader());
        inDebt = in.readBoolean();
    }

    public static final Creator<FilterStakeholdersParcel> CREATOR = new Creator<FilterStakeholdersParcel>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public FilterStakeholdersParcel createFromParcel(Parcel source) {
            return new FilterStakeholdersParcel(source);
        }

        @Override
        public FilterStakeholdersParcel[] newArray(int size) {
            return new FilterStakeholdersParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(roles);
        dest.writeBoolean(inDebt);
    }

    //Getters and Setters

    public String getName() {
        return name;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public boolean isInDebt() {
        return inDebt;
    }
}
