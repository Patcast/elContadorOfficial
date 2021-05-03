package be.kuleuven.elcontador10.background.database;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.elcontador10.background.interfaces.SettingsInterface;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;

public class SettingsManager {
    private final String URL_Check = "https://studev.groept.be/api/a20sd505/LogIn/";
    private final String URL_Change = "https://studev.groept.be/api/a20sd505/changePassword/";
    private final String URL_NonRegistered = "https://studev.groept.be/api/a20sd505/getNonRegisteredStakeholders";

    private static volatile SettingsManager INSTANCE;

    private SettingsManager() {}

    public static SettingsManager getInstance() {
        if (INSTANCE == null) synchronized (SettingsManager.class) { INSTANCE = new SettingsManager(); }

        return INSTANCE;
    }

    public void changePassword(SettingsInterface settings, StakeholderLoggedIn loggedIn, String currentPassword, String newPassword) {
        String username = loggedIn.getUsername();
        String checkURL = URL_Check + username + "/" + currentPassword;

        RequestQueue requestQueue = Volley.newRequestQueue(settings.getContext());
        StringRequest change = new StringRequest(Request.Method.POST, URL_Change,
                response -> settings.feedback("Password changed."),
                error -> {
                    error.printStackTrace();
                    settings.feedback(error.toString());
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("login", username);
                params.put("pass", newPassword);
                return params;
            }
        };

        JsonArrayRequest check = new JsonArrayRequest(Request.Method.GET, checkURL, null,
                response -> {
                    try {
                        if (response.length() != 0) requestQueue.add(change); // password matches
                        else settings.feedback("Wrong password.");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        settings.feedback(e.toString());
                    }
                },
                error -> {
                    error.printStackTrace();
                    settings.feedback("Error connecting to server! (1)");
                }
        );
        requestQueue.add(check);
    }

    public void findNonRegistered(SettingsInterface settings) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(settings.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_NonRegistered, null,
                response -> {
                    try {
                        ids.clear(); names.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);

                            String id = object.getString("id");
                            String name = object.getString("name");

                            ids.add(id); names.add(name);
                        }

                        settings.populateSpinner(ids, names);
                    } catch (Exception e) {
                        e.printStackTrace();
                        settings.feedback(e.toString());
                    }
                }, error -> {
                    error.printStackTrace();
                    settings.feedback(error.toString());
        });

        requestQueue.add(request);
    }
}
