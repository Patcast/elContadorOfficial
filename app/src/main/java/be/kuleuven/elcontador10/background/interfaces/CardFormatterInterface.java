package be.kuleuven.elcontador10.background.interfaces;

import java.time.LocalDateTime;

public interface CardFormatterInterface {
    String[] TransactionFormatter(int id, LocalDateTime date, double amount, String sender, String receiver,
                                  String type, String subtype, boolean deleted);
    String[] StakeholderFormatter(int id, String firstName, String lastName, double balance, String role, boolean deleted);
}
