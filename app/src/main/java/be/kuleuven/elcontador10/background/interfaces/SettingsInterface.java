package be.kuleuven.elcontador10.background.interfaces;

import android.content.Context;

import java.util.ArrayList;

public interface SettingsInterface {

    void feedback(String string);
    void populateSpinner(ArrayList<String> ids, ArrayList<String> stakeholders);
    Context getContext();
}
