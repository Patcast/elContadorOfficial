package be.kuleuven.elcontador10.background.database;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;
import be.kuleuven.elcontador10.background.interfaces.LogInInterface;

public class AccountManager {

    private static  volatile AccountManager INSTANCE = null;

    private AccountManager() {}

    public static AccountManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AccountManager.class) {
                if (INSTANCE == null) INSTANCE = new AccountManager();
            }
        }

        return INSTANCE;
    }

    public void Authenticate(LogInInterface manager, String username, String password) {
        String URL = DatabaseURL.INSTANCE.checkLogIn + username + "/" + password;
        RequestQueue requestQueue = Volley.newRequestQueue(manager.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
            response -> {
                try {
                    if (response.length() != 0) {
                        String login_name = response.getJSONObject(0).getString("username");
                        findStakeHolderUsername(manager, login_name);
                    }
                    else {
                        manager.onLoginFailed("Wrong username / password.");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    manager.onLoginFailed(e.toString());
                }
            },
            error -> {
                error.printStackTrace();
                manager.onLoginFailed("Error connecting to server! (1)");
            }
        );
        requestQueue.add(request);
    }

    public void findStakeHolderUsername(LogInInterface manager, String username) {
        String URL = DatabaseURL.INSTANCE.URL_findStakeholderUsername + username;
        RequestQueue requestQueue = Volley.newRequestQueue(manager.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        if (response.length() != 0) {
                            JSONObject object = response.getJSONObject(0);
                            int id = object.getInt("idStakeholders");
                            String firstName = object.getString("firstName");
                            String lastName = object.getString("LastName");
                            String role = object.getString("Role");
                            String phoneNumber = object.getString("phoneNumber");
                            String email = object.getString("email");
                            boolean deleted = object.getString("deleted").equals("1");

                            if (!deleted) {
                                StakeholderLoggedIn loggedIn = new StakeholderLoggedIn(
                                        id, firstName, lastName, role, phoneNumber, email, username);

                               // getStakeholderRoles(manager, username, loggedIn);
                                manager.onLoginSucceed(username, loggedIn);
                            } else {
                                manager.onLoginFailed("Stakeholder has been deleted.");
                            }
                        }
                        else {
                            manager.onLoginFailed("Stakeholder does not exist.");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        manager.onLoginFailed(e.toString());
                    }
                },
                error -> {
                    error.printStackTrace();
                    manager.onLoginFailed("Error connecting to server!");
                }
                );
        requestQueue.add(request);
    }

}
