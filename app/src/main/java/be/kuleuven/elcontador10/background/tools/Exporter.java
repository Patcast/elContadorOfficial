package be.kuleuven.elcontador10.background.tools;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.net.URI;

public enum Exporter {
    INSTANCE;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public URI createFile(String title) {

        return null;
    }
}
