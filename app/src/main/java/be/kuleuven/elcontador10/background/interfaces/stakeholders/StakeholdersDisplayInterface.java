package be.kuleuven.elcontador10.background.interfaces.stakeholders;

import android.content.Context;
import android.os.Bundle;

public interface StakeholdersDisplayInterface {
    void display(Bundle bundle);
    void error(String error);
    Context getContext();
}
