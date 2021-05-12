package be.kuleuven.elcontador10.background.interfaces;

import android.content.Context;

import java.util.ArrayList;

import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;

public interface LogInInterface {

    Context getContext();
    void onLoginSucceed(String username, StakeholderLoggedIn loggedIn);
    void onLoginFailed(String reason);
}
