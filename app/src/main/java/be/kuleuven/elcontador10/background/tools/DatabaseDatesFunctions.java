package be.kuleuven.elcontador10.background.tools;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;

import be.kuleuven.elcontador10.background.model.ProcessedTransaction;

@RequiresApi(api = Build.VERSION_CODES.O)
public enum DatabaseDatesFunctions {
    INSTANCE;
    public final int ONE_TIME=0, DAILY=1, WEEKLY=2, TWO_WEEKS = 3, MONTHLY=4,YEARLY=5, CUSTOM=6;


    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ZoneId zone = ZoneId.systemDefault();

    /**
     * String (dd/MM/yyyy) to timestamp of the beginning of that day at current timezone.
     * @param text String with format dd/MM/yyyy
     * @return Timestamp
     */
    public Timestamp stringToTimestamp(@NonNull String text) {
        return localDateToTimestamp(stringToDate(text));
    }

    public Timestamp localDateToTimestamp(LocalDate date) {
        return new Timestamp(Date.from(date.atStartOfDay(zone).toInstant()));
    }

    public String timestampToPeriod(Timestamp start, Timestamp end) {
        return timestampToString(start) + " - " + timestampToString(end);
    }

    public String timestampToString(Timestamp date) {
        LocalDate localDate = date.toDate().toInstant().atZone(zone).toLocalDate();
        return  localDate.format(formatter);
    }

    public String timestampToStringDetailed(Timestamp date) {
        LocalDateTime localDateTime = date.toDate().toInstant().atZone(zone).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss"));
    }

    public LocalDate stringToDate(@NonNull String text) {
        return LocalDate.parse(text, formatter);
    }


    public ArrayList<ProcessedTransaction> makeFutureTransactions(String startDateString, int frequency, int collectionSize) {
        LocalDate startDate = stringToDate(startDateString);
        ArrayList<ProcessedTransaction> futureTransactions = new ArrayList<>();
        LocalDate date_future_trans = startDate;

        for(int i = 0; i <collectionSize;i++){
            switch (frequency){
                case ONE_TIME:
                    break;
                case DAILY:
                    date_future_trans = startDate.plusDays(i);
                    break;
                case WEEKLY:
                    date_future_trans = startDate.plusWeeks(i);
                    break;
                case TWO_WEEKS:
                    date_future_trans = startDate.plusWeeks(2L * i);
                    break;
                case MONTHLY:
                    date_future_trans = startDate.plusMonths(i);
                    break;

                case YEARLY:
                    date_future_trans = startDate.plusYears(i);
                    break;
                default:
                    return null;
            }
            ProcessedTransaction transaction = new ProcessedTransaction(localDateToTimestamp(date_future_trans),i);
            futureTransactions.add(transaction);

        }
        futureTransactions.forEach(t->t.setCollectionSize(futureTransactions.size()));
        return futureTransactions;
    }

    /**
     * Used for custom frequency
     * @param start start date (dd/MM/yyyy)
     * @param nrOfPayments number of payments
     * @param frequencyCode value-unit
     *                      unit: 0 - days, 1 - weeks, 2 - months, 3 - quarters, 4 - years
     * @return first: first-second last: payment dates, last: start date - end date
     */
    public LinkedList<String> customPeriod(String start, int nrOfPayments, String frequencyCode) {
        String[] split = frequencyCode.split(" - ");
        int value = Integer.parseInt(split[0]);
        int unit = Integer.parseInt(split[1]);

        LocalDate startDate = LocalDate.parse(start, formatter);
        LinkedList<String> result = new LinkedList<>();
        LocalDate j = startDate;

        for (int i = 0; i <= nrOfPayments; i++) {
            if (unit == 0)      j = startDate.plusDays((long) i * value);
            else if (unit == 1) j = startDate.plusWeeks((long) i * value);
            else if (unit == 2) j = startDate.plusMonths((long) i * value);
            else if (unit == 3) j = startDate.plusMonths((long) i * value * 3);
            else if (unit == 4) j = startDate.plusYears((long) i * value);
            else return null;

            result.add("Payment " + (i + 1) + ": " + j.format(formatter));
        }

        result.removeLast();
        result.add(startDate.format(formatter) + " - " + j.format(formatter)); // period text

        return result;
    }



}
