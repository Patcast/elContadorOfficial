package be.kuleuven.elcontador10.background.model.Interfaces;

import com.google.firebase.Timestamp;

public interface TransactionInterface {
    public int getColorInt();
    public int getTotalAmount();
    public String getIdOfStakeInt();
    public String getIdOfTransactionInt();
    public String getIdOfCategoryInt();
    public Timestamp getDueDate();
    public String getTitle();
    public String getImageName();
}
