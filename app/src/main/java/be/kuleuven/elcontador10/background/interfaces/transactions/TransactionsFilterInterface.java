package be.kuleuven.elcontador10.background.interfaces.transactions;

import android.content.Context;

import java.util.List;


import be.kuleuven.elcontador10.background.model.TransactionType;

public interface TransactionsFilterInterface {
    void setCategories(List<TransactionType> types);
    Context getContext();
}
