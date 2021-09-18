package be.kuleuven.elcontador10.background.tools;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;

import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

@RequiresApi(api = Build.VERSION_CODES.O)
public enum DatabaseDatesFunctions {
    INSTANCE;

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

    public LocalDate stringToDate(@NonNull String text) {
        return LocalDate.parse(text, formatter);
    }

    /**
     * Used for standard frequency
     * @param start start date (dd/MM/yyyy)
     * @param frequency how many times the payment repeats (value)
     *                  1 - daily, 2 - weekly, 3 - monthly, 4 - quarterly, 5 - yearly
     * @param duration how many weeks, months...
     * @param unit index of duration_spinner
     *             unit + frequency. 1 - days, 2 - weeks, 3 - months, 4 - quarters, 5 - years
     * @return String start date - end date
     */
    public String getPeriod(String start, int frequency, int duration, int unit) {
        LocalDate startDate = stringToDate(start);
        String end;
        unit += frequency;

        if (unit == 1) {
            LocalDate newDate = startDate.plusDays(duration);
            end = newDate.format(formatter);
        } else if (unit == 2) {
            LocalDate newDate = startDate.plusWeeks(duration);
            end = newDate.format(formatter);
        } else if (unit == 3) {
            LocalDate newDate = startDate.plusMonths(duration);
            end = newDate.format(formatter);
        } else if (unit == 4) {
            LocalDate newDate = startDate.plusMonths(duration * 3L);
            end = newDate.format(formatter);
        } else if (unit == 5) {
            LocalDate newDate = startDate.plusYears(duration);
            end = newDate.format(formatter);
        } else return null;

        return start + " - " + end;
    }

    /**
     * Used for standard frequency
     * Gets a list of dates in a given period with the frequency code
     * @param period String "start - end"
     * @param frequency 1 - daily, 2 - weekly, 3 - monthly, 4 - quarterly, 5 - yearly
     * @return ArrayList of ScheduledTransaction. size() is the amount of payments,
     *          null if last payment is after end date
     */
    public ArrayList<ScheduledTransaction> getScheduledTransactions(String period, int frequency) {
        String[] dates = period.split(" - ");
        LocalDate startDate = stringToDate(dates[0]);
        LocalDate endDate = stringToDate(dates[1]);

        ArrayList<ScheduledTransaction> transactions = new ArrayList<>();
        LocalDate j = startDate;
        int i = 0;

        while (j.isBefore(endDate)) {

            if (frequency == 1)      j = startDate.plusDays((long) i);
            else if (frequency == 2) j = startDate.plusWeeks((long) i);
            else if (frequency == 3) j = startDate.plusMonths((long) i);
            else if (frequency == 4) j = startDate.plusMonths(3L * i);
            else if (frequency == 5) j = startDate.plusYears((long) i);
            else return null;

            // set totalAmount and idOfStakeholder later
            ScheduledTransaction transaction = new ScheduledTransaction(0, 0, localDateToTimestamp(j), null);

            if (j.isEqual(endDate)) break; // successful
            else if (j.isAfter(endDate)) return null; // unsuccessful - period not full
            i++;
            transactions.add(transaction);
        }

        return transactions;
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
        result.add(startDate.format(formatter) + " - " + j.format(formatter));

        return result;
    }
}
