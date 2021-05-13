package be.kuleuven.elcontador10.background.parcels;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class EditStakeholderParcel implements Parcelable {

    String id, name, role, phoneNo, email, image_string;
    Bitmap image;

    public EditStakeholderParcel(String id, String name, String role, String phoneNo, String email, Bitmap image, String image_string) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.phoneNo = phoneNo;
        this.email = email;
        this.image = image;
        this.image_string = image_string;
    }

    protected EditStakeholderParcel(Parcel in) {
        id = in.readString();
        name = in.readString();
        role = in.readString();
        phoneNo = in.readString();
        email = in.readString();
    }

    public static final Creator<EditStakeholderParcel> CREATOR = new Creator<EditStakeholderParcel>() {
        @Override
        public EditStakeholderParcel createFromParcel(Parcel source) {
            return new EditStakeholderParcel(source);
        }

        @Override
        public EditStakeholderParcel[] newArray(int size) {
            return new EditStakeholderParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(role);
        dest.writeString(phoneNo);
        dest.writeString(email);
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

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getImage_string() {
        return image_string;
    }
}
