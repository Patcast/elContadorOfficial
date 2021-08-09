package be.kuleuven.elcontador10.background.database;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
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
    private static volatile SettingsManager INSTANCE;

    private SettingsManager() {}

    public static SettingsManager getInstance() {
        if (INSTANCE == null) synchronized (SettingsManager.class) { INSTANCE = new SettingsManager(); }

        return INSTANCE;
    }

    public void changePassword(SettingsInterface settings, StakeholderLoggedIn loggedIn, String currentPassword, String newPassword) {
        String username = loggedIn.getUsername();
        String checkURL = DatabaseURL.INSTANCE.checkLogIn + username + "/" + currentPassword;

        RequestQueue requestQueue = Volley.newRequestQueue(settings.getContext());
        StringRequest change = new StringRequest(Request.Method.POST, DatabaseURL.INSTANCE.changePassword,
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

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, DatabaseURL.INSTANCE.getNonRegistered, null,
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

    public void register(SettingsInterface settings, String id, String username, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(settings.getContext());
        String checkURL = DatabaseURL.INSTANCE.checkUsernameList + username;

        StringRequest update = new StringRequest(Request.Method.POST, DatabaseURL.INSTANCE.updateUsername,
                response -> settings.feedback("LoggedUser added"),
                error -> {
                    error.printStackTrace();
                    settings.feedback(error.toString());
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", username);
                params.put("id", id);
                return params;
            }
        };

        StringRequest add = new StringRequest(Request.Method.POST, DatabaseURL.INSTANCE.registerUsername,
                response -> requestQueue.add(update),
                error -> {
                   error.printStackTrace();
                   settings.feedback(error.toString());
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", username);
                params.put("pass", password);
                return params;
            }
        };

        JsonArrayRequest check = new JsonArrayRequest(Request.Method.GET, checkURL, null,
                response -> {
                    try {
                        if (response.length() == 0) requestQueue.add(add);
                        else settings.feedback("Username already exists!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        settings.feedback(e.toString());
                    }
                }, error -> {
                    error.printStackTrace();
                    settings.feedback(error.toString());
        });

        requestQueue.add(check);
    }
}
