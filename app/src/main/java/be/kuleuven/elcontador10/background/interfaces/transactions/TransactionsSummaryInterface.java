package be.kuleuven.elcontador10.background.interfaces.transactions;

import android.content.Context;

import java.util.ArrayList;

public interface TransactionsSummaryInterface {
    Context getContext();
    void populateRecyclerView(ArrayList<String> title, ArrayList<String> description, ArrayList<String> status, ArrayList<String> metadata);
    void error(String error);
    void displayTransaction(String id);
}
