package be.kuleuven.elcontador10.background.interfaces.transactions;

import android.content.Context;

import java.util.ArrayList;

import be.kuleuven.elcontador10.background.parcels.TransactionType;

public interface TransactionsFilterInterface {
    void setCategories(ArrayList<TransactionType> types);
    Context getContext();
}
