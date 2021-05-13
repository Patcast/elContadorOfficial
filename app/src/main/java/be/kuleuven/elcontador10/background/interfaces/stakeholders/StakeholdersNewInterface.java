package be.kuleuven.elcontador10.background.interfaces.stakeholders;

import android.content.Context;

public interface StakeholdersNewInterface {
    void addStakeholder();
    void editStakeholder();
    void feedback(String feedback);
    Context getContext();
}
