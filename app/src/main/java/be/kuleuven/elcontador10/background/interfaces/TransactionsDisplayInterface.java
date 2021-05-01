package be.kuleuven.elcontador10.background.interfaces;

import android.content.Context;
import android.os.Bundle;

public interface TransactionsDisplayInterface {
    void display(Bundle bundle);
    void error(String error);
    Context getContext();
}
