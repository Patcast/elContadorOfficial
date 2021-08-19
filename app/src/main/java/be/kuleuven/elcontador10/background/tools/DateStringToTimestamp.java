package be.kuleuven.elcontador10.background.tools;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public enum DateStringToTimestamp {
    INSTANCE;

    /** String (dd/MM/yyyy) to timestamp of the beginning of that day at current timezone.
     *
     * @param text String with format dd/MM/yyyy
     * @return Timestamp
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Timestamp date(@NonNull String text) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ZoneId zone = ZoneId.systemDefault();

        LocalDate localDate = LocalDate.parse(text, formatter);

        return new Timestamp(Date.from(localDate.atStartOfDay(zone).toInstant()));
    }
}
