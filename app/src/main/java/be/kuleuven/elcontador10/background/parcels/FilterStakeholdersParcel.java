package be.kuleuven.elcontador10.background.parcels;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;

import be.kuleuven.elcontador10.R;

public class FilterStakeholdersParcel implements Parcelable {
    private final String name;

    // roles
    private final ArrayList<String> roles;

    // attributes
    private final boolean deleted;

    // sort by
    private final String sortBy;

    public FilterStakeholdersParcel(@Nullable String name, ArrayList<String> roles,
                                    boolean deleted, String sortBy) {
        this.name = name;
        this.roles = roles;
        this.deleted = deleted;
        this.sortBy = sortBy;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected FilterStakeholdersParcel(Parcel in) {
        name = in.readString();
        roles = (ArrayList<String>) in.readArrayList(getClass().getClassLoader());
        deleted = in.readBoolean();
        sortBy = in.readString();
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
        dest.writeBoolean(deleted);
        dest.writeString(sortBy);
    }

    //Getters and Setters

    public String getName() {
        return name;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getSortBy() {
        return sortBy;
    }
}
