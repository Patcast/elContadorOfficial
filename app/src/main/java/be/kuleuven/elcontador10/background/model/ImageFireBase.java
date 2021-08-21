package be.kuleuven.elcontador10.background.model;

import android.net.Uri;

public class ImageFireBase {
    String nameOfImage;
    Uri contentUri;

    public ImageFireBase(String name, Uri contentUri) {
        this.nameOfImage = name;
        this.contentUri = contentUri;
    }

    public String getNameOfImage() {
        return nameOfImage;
    }

    public Uri getContentUri() {
        return contentUri;
    }
}
