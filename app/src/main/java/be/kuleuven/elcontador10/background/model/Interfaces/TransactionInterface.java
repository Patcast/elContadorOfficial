package be.kuleuven.elcontador10.background.model.Interfaces;

import com.google.firebase.Timestamp;

public interface TransactionInterface {

    int getColorInt();
    int getTotalAmount();
    String getIdOfStakeInt();
    String getIdOfTransactionInt();
    String getIdOfCategoryInt();
    Timestamp getDueDate();
    String getTitle();
    String getImageName();
    String getAmountToDisplay();
}
