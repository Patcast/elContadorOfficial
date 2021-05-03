package be.kuleuven.elcontador10.background.interfaces;

import android.content.Context;

public interface SettingsInterface {
    void passwordChanged();
    void error(String string);
    Context getContext();
}
