package be.kuleuven.elcontador10.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CardFormatterInterface {
    String[] TransactionFormatter(int id, LocalDateTime date, double amount, String sender, String receiver,
                                  String type, String subtype);
    String[] TenantFormatter(int id, String firstName, String lastName, double balance);
    String[] ContractFormatter(int id, LocalDate startDate, LocalDate endDate, double amount, String firstName,
                               String lastName, int idPlace, String placeType, String contractType);
}
