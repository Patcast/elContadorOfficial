package be.kuleuven.elcontador10.background.database;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import be.kuleuven.elcontador10.background.CardFormatter;
import be.kuleuven.elcontador10.background.interfaces.CardFormatterInterface;
import be.kuleuven.elcontador10.background.interfaces.HomepageInterface;

public class HomepageManager {
    private static volatile HomepageManager INSTANCE = null;

    private final ArrayList<String> titles;
    private final ArrayList<String> descriptions;
    private final ArrayList<String> status;
    private final ArrayList<String> metadata;

    private HomepageManager() {
        titles = new ArrayList<>();
        descriptions = new ArrayList<>();
        status = new ArrayList<>();
        metadata = new ArrayList<>();
    }

    public static HomepageManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HomepageManager.class) {
                if (INSTANCE == null) INSTANCE = new HomepageManager();
            }
        }

        return INSTANCE;
    }


    // get data from the server
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getRecentTransactions(@NotNull HomepageInterface homepageInterface) {
        RequestQueue requestQueue = Volley.newRequestQueue(homepageInterface.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, DatabaseURL.INSTANCE.homepageTransaction, null,
                response -> {
                    try {
                        // clear everything first
                        titles.clear();
                        descriptions.clear();
                        status.clear();
                        metadata.clear();

                        titles.add("WHITE#Recent transactions");
                        descriptions.add("Here are the 5 most recent transactions:");
                        status.add(" # "); // needs to be added so that the list index are the same
                                           // empty status needs to be " # " for the colour formatter
                        metadata.add("");

                        if (response.length() != 0) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);

                                int id = object.getInt("idTransactions");

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDateTime date = LocalDateTime.parse(object.getString("date"), formatter);

                                double amount = object.getDouble("amount");
                                String user = object.getString("userName");
                                String stakeholder = object.getString("stakeholderName");
                                String type = object.getString("type");
                                String subtype = object.getString("subType");

                                CardFormatterInterface cardFormatter = new CardFormatter();
                                String[] formatted = cardFormatter.TransactionFormatter(id, date, amount, user, stakeholder, type, subtype, false);

                                titles.add(formatted[0]);
                                descriptions.add(formatted[1]);
                                status.add(formatted[2]);
                                metadata.add(formatted[3]);
                            }
                        }
                        else {
                            titles.add("None");
                            descriptions.add("");
                            status.add(" # ");
                            metadata.add("");
                        }

                        homepageInterface.populateRecyclerView(titles, descriptions, status, metadata);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        homepageInterface.error(e.toString());
                    }
                },
                error -> {
                    error.printStackTrace();
                    homepageInterface.error(error.toString());
                });
        requestQueue.add(request);
    }

    public void getBudget(@NotNull HomepageInterface home) {
        RequestQueue requestQueue = Volley.newRequestQueue(home.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, DatabaseURL.INSTANCE.getBudget, null,
                response -> {
                    try {
                        home.displayBudget(response.getJSONObject(0).getDouble("budget"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        home.error(e.toString());
                    }
                },
                error -> {
                    home.error(error.toString());
                    error.printStackTrace();
        });

        requestQueue.add(request);
    }
}
