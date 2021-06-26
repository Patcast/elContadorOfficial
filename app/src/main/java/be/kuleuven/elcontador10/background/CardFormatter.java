package be.kuleuven.elcontador10.background;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Duration;
import java.time.LocalDateTime;


import be.kuleuven.elcontador10.background.interfaces.CardFormatterInterface;

public class CardFormatter  implements CardFormatterInterface {

    /**
    * Takes in all necessary data to output Title, Description, Status, Metadata in that order
    * */


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String[] TransactionFormatter(int id, LocalDateTime date, double amount, String user, String stakeholder, String type, String subtype,
                                         boolean deleted) {
        String title = (amount > 0 ? "GREEN#Ingreso" : "RED#Egreso");
        String description = user +
                (amount > 0 ? " ha depositado " : " ha retirado ") + "$" + Math.abs(amount) +
                (amount > 0 ? " de " : " para ") + // maker + amount
                (stakeholder.equals("Not Specified") ? "" : stakeholder + "'s ") + // stakeholder
                ".\nConcepto:  " +
                subtype.toLowerCase() + // type
                (type.equals(subtype) ? "" : " " + type.toLowerCase()) + "."; // remove repeating types and subtypes
        String status = "WHITE#";
        String metadata = (deleted ? "deleted" : "Transactions#" + id);

        LocalDateTime now = LocalDateTime.now();

        if (now.getDayOfYear() == date.getDayOfYear()) {// transaction happened today
            Duration duration = Duration.between(date, now);

            if (duration.toHours() < 1) {
                if (duration.toMinutes() < 1) status += " hace unos momentos ";
                else status += duration.toMinutes() + " hace unos minutos ";
            }
            else status += (date.getHour() < 10? "0" : "") + date.getHour() + ":" +
                    (date.getMinute() < 10? "0" : "") + date.getMinute();
        }
        else if (now.getDayOfYear() - 1 == date.getDayOfYear()) { // transaction happened yesterday
            status += " Ayer a las "+ (date.getHour() < 10? "0" : "") + date.getHour() + ":"
                    + (date.getMinute() < 10? "0" : "") + date.getMinute();
        }
        else { // transaction happened days before
            status += (date.getDayOfMonth() < 10? "0" : "") + date.getDayOfMonth() + "/" +
                    (date.getMonthValue() < 10? "0" : "") + date.getMonthValue() + "/" +
                    date.getYear();
        }

        return new String[] {title, description, status, metadata};
    }

    @Override
    public String[] StakeholderFormatter(int id, String firstName, String lastName, String role, boolean deleted) {
        String title = "WHITE#" + firstName + " " + lastName;
        String description = " Rol: "+ role;

        String status = "";

        String metadata = (deleted? "deleted" : "Stakeholder#" + id);

        return new String[] {title, description, status, metadata};
    }
}
