package be.kuleuven.elcontador10.background.database;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import be.kuleuven.elcontador10.background.interfaces.SettingsInterface;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;

public class SettingsManager {
    private final String URL_Check = "https://studev.groept.be/api/a20sd505/LogIn/";
    private static volatile SettingsManager INSTANCE;

    private SettingsManager() {}

    public static SettingsManager getInstance() {
        if (INSTANCE == null) synchronized (SettingsManager.class) { INSTANCE = new SettingsManager(); }

        return INSTANCE;
    }

    public void checkPassword(SettingsInterface settings, StakeholderLoggedIn loggedIn, String currentPassword, String newPassword) {
        String username = loggedIn.getUsername();
        String URL = URL_Check + username + "/" + currentPassword;

        RequestQueue requestQueue = Volley.newRequestQueue(settings.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        if (response.length() != 0) changePassword(settings, newPassword); // password matches
                        else {
                            settings.error("Wrong password.");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        settings.error(e.toString());
                    }
                },
                error -> {
                    error.printStackTrace();
                    settings.error("Error connecting to server! (1)");
                }
        );
        requestQueue.add(request);
    }

    private void changePassword(SettingsInterface settings, String newPassword) {

    }
}
