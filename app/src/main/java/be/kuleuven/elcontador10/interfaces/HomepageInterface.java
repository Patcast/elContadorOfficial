package be.kuleuven.elcontador10.interfaces;

import android.content.Context;

import java.util.ArrayList;

public interface HomepageInterface {
    Context getContext();
    void populateRecyclerView(ArrayList<String> title, ArrayList<String> description, ArrayList<String> status, ArrayList<String> metadata);
    void error(String error);
}
