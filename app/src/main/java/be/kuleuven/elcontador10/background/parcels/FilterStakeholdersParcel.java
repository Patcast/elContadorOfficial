package be.kuleuven.elcontador10.background.parcels;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class FilterStakeholdersParcel implements Parcelable {
    private String name;

    // roles
    private boolean employee;
    private boolean manager;
    private boolean owner;
    private boolean tenant;

    // attributes
    private boolean inDebt;

    public FilterStakeholdersParcel(@Nullable String name, boolean employee, boolean manager, boolean owner, boolean tenant, boolean inDebt) {
        this.name = name;
        this.employee = employee;
        this.manager = manager;
        this.owner = owner;
        this.tenant = tenant;
        this.inDebt = inDebt;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected FilterStakeholdersParcel(Parcel in) {
        name = in.readString();

        employee = in.readBoolean();
        manager = in.readBoolean();
        owner = in.readBoolean();
        tenant = in.readBoolean();

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

        dest.writeBoolean(employee);
        dest.writeBoolean(manager);
        dest.writeBoolean(owner);
        dest.writeBoolean(tenant);

        dest.writeBoolean(inDebt);
    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmployee() {
        return employee;
    }

    public void setEmployee(boolean employee) {
        this.employee = employee;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isTenant() {
        return tenant;
    }

    public void setTenant(boolean tenant) {
        this.tenant = tenant;
    }

    public boolean isInDebt() {
        return inDebt;
    }

    public void setInDebt(boolean inDebt) {
        this.inDebt = inDebt;
    }
}
