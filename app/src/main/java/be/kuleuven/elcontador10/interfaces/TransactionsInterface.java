package be.kuleuven.elcontador10.interfaces;

import android.content.Context;

import java.util.ArrayList;

public interface TransactionsInterface {
    Context getContext();
    void populateRecyclerView(ArrayList<String> title, ArrayList<String> description, ArrayList<String> status, ArrayList<String> metadata);
    void error(String error);
    void displayTransaction(String id);
}
