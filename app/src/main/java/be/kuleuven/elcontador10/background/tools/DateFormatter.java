package be.kuleuven.elcontador10.background.tools;


import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

// TODO make it into static methods maybe
public class DateFormatter {
   Timestamp inputDate;
   String code;
   String formattedDate;

    public DateFormatter(Timestamp inputDate, String code) {
        this.inputDate = inputDate;
        this.code = code;
        formattedDate="Error Loading...";
        chooseFormatter();
    }

    private void chooseFormatter() {
        switch (code) {
            case "s":
                getShortDate();
                break;
            case "f":
                getLongDate();
                break;
            case "t":
                getTime();
                break;
            default:
                setFormattedDate("invalid code");
        }
    }
    // eg. 17 Ago
    private void getShortDate(){
        Date date = inputDate.toDate();
        String [] bitsOfDate = date.toString().split(" ");
        StringBuilder shortDate = new StringBuilder();
        shortDate.append(bitsOfDate[2]);
        shortDate.append(" ");
        shortDate.append(bitsOfDate[1]);
        if (Integer.parseInt(bitsOfDate[5]) != Calendar.getInstance().get(Calendar.YEAR)) {
            shortDate.append(" ");
            shortDate.append(bitsOfDate[5]);
        }
        setFormattedDate(shortDate.toString());
    }
    // eg. 17 Ago 2021
    private void getLongDate(){
        String [] bitsOfDate = inputDate.toDate().toString().split(" ");
        StringBuilder shortDate = new StringBuilder();
        shortDate.append(bitsOfDate[2]);
        shortDate.append(" ");
        shortDate.append(bitsOfDate[1]);
        shortDate.append(" ");
        shortDate.append(bitsOfDate[5]);
        setFormattedDate(shortDate.toString());
    }
    // eg. 11:21
    private void getTime(){
        String [] bitsOfDate = inputDate.toDate().toString().split(" ");
        String [] bitsOfTime= bitsOfDate[3].split(":");
        StringBuilder time = new StringBuilder();
        time.append(bitsOfTime[0]);
        time.append(":");
        time.append(bitsOfTime[1]);
        setFormattedDate(time.toString());
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getFormattedDate() {
        return formattedDate;
    }
}
