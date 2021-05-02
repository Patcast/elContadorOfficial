package be.kuleuven.elcontador10.background;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import be.kuleuven.elcontador10.background.interfaces.CardFormatterInterface;

public class CardFormatter implements CardFormatterInterface {

    /**
    * Takes in all necessary data to output Title, Description, Status, Metadata in that order
    * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String[] TransactionFormatter(int id, LocalDateTime date, double amount, String sender, String receiver, String type, String subtype) {
        String title = "WHITE#" + type;
        String description;
        String status = "WHITE#";
        String metadata = "TransactionsSummary#" + id;

        if (subtype.equals("null")) {
            if (receiver.equals("null"))
                description = sender + " has paid $" + amount + " for " + type + ".";
            else
                description = sender + " has sent $" + amount + " to " + receiver + " for " + type + ".";
        }
        else {
            if (receiver.equals("null"))
                description = sender + " has paid $" + amount + " for " + subtype + ".";
            else
                description = sender + " has paid $" + amount + " to " + receiver + " for " + subtype + ".";
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.getDayOfYear() == date.getDayOfYear()) {// transaction happened today
            Duration duration = Duration.between(date, now);

            if (duration.toHours() < 1) {
                if (duration.toMinutes() < 1) status += "Few moments ago";
                else status += duration.toMinutes() + " minutes ago";
            }
            else status += date.getHour() + ":" + date.getMinute();
        }
        else if (now.getDayOfYear() - 1 == date.getDayOfYear()) { // transaction happened yesterday
            status += "Yesterday";
        }
        else { // transaction happened days before
            status += date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
        }

        return new String[] {title, description, status, metadata};
    }

    @Override
    public String[] TenantFormatter(int id, String firstName, String lastName, double balance) {
        String title = "WHITE#" + firstName + " " + lastName;
        String description = "Balance: $" + balance;

        String status;
        if (balance < 0) status = "RED#In debt";
        else if (balance == 0) status = "WHITE#Payment up to date";
        else status = "GREEN#Reimbursement required";

        String metadata = "Tenants#" + id;

        return new String[] {title, description, status, metadata};
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String[] ContractFormatter(int id, LocalDate startDate, LocalDate endDate, double amount, String firstName, String lastName, int idPlace, String placeType, String contractType) {
        String title = "WHITE#" + placeType + " " + idPlace;
        String description = "Tenant: " + firstName + " " + lastName + "\n" +
                "Type: " + contractType + "\n" +
                "Amount: $" + amount;
        String start = startDate.getDayOfMonth() + "/" + startDate.getMonthValue() + "/" + startDate.getYear();
        String end = endDate.getDayOfMonth() + "/" + endDate.getMonthValue() + "/" + endDate.getYear();
        String status = "WHITE#" + start + " - " + end;
        String metadata = "Contracts#" + id;
        return new String[] {title, description, status, metadata};
    }
}
