package be.kuleuven.elcontador10.background.interfaces;

import java.time.LocalDateTime;

public interface CardFormatterInterface {
    String[] TransactionFormatter(int id, LocalDateTime date, double amount, String user, String stakeholder,
                                  String type, String subtype, boolean deleted);
    String[] StakeholderFormatter(int id, String firstName, String lastName, String role, boolean deleted);
}
