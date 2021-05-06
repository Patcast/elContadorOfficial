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
    public String[] TransactionFormatter(int id, LocalDateTime date, double amount, String sender, String receiver, String type, String subtype,
                                         boolean deleted) {
        String title = "WHITE#" + type;
        String description;
        String status = "WHITE#";
        String metadata = (deleted ? "deleted" : "Transactions#" + id);

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
            else status += (date.getHour() < 10? "0" : "") + date.getHour() + ":" +
                    (date.getMinute() < 10? "0" : "") + date.getMinute();
        }
        else if (now.getDayOfYear() - 1 == date.getDayOfYear()) { // transaction happened yesterday
            status += "Yesterday at " + (date.getHour() < 10? "0" : "") + date.getHour() + ":"
                    + (date.getMinute() < 10? "0" : "") + date.getMinute();;
        }
        else { // transaction happened days before
            status += (date.getDayOfMonth() < 10? "0" : "") + date.getDayOfMonth() + "/" +
                    (date.getMonthValue() < 10? "0" : "") + date.getMonthValue() + "/" +
                    date.getYear();
        }

        return new String[] {title, description, status, metadata};
    }

    @Override
    public String[] StakeholderFormatter(int id, String firstName, String lastName, double balance, String role, boolean deleted) {
        String title = "WHITE#" + firstName + " " + lastName;
        String description = "Role: " + role;// + "\n" + "Balance: $" + balance;

        String status = "";
//        if (balance < 0) status = "RED#In debt";
//        else if (balance == 0) status = "WHITE#Payment up to date";
//        else status = "GREEN#Reimbursement required";

        String metadata = (deleted? "deleted" : "Stakeholder#" + id);

        return new String[] {title, description, status, metadata};
    }
}
