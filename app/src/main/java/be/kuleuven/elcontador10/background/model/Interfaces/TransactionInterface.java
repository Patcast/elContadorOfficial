package be.kuleuven.elcontador10.background.model.Interfaces;

import com.google.firebase.Timestamp;

public interface TransactionInterface {
    public int getColorPositiveInt();
    public int getColorNegativeInt();
    public int getTotalAmount();
    public String getIdOfStakeInt();
    public String getIdOfTransactionInt();
    public String getIdOfCategoryInt();
    public Timestamp getDate();
    public String getTitle();
    public String getImageInt();
}
