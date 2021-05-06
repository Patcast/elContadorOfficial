package be.kuleuven.elcontador10.background.database;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import be.kuleuven.elcontador10.background.CardFormatter;
import be.kuleuven.elcontador10.background.interfaces.CardFormatterInterface;
import be.kuleuven.elcontador10.background.interfaces.HomepageInterface;

public class HomepageManager {
    private final String URL_Transactions = "https://studev.groept.be/api/a20sd505/homepageTransactions";
    private final String URL_Tenants = "https://studev.groept.be/api/a20sd505/inDebtTenants";

    private static volatile HomepageManager INSTANCE = null;

    private ArrayList<String> titles;
    private ArrayList<String> descriptions;
    private ArrayList<String> status;
    private ArrayList<String> metadata;

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
    public void getRecentTransactions(HomepageInterface homepageInterface) {
        RequestQueue requestQueue = Volley.newRequestQueue(homepageInterface.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_Transactions, null,
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
                                String sender = object.getString("sender");
                                String receiver = object.getString("receiver");
                                String type = object.getString("type");
                                String subtype = object.getString("subType");
                                boolean deleted = object.getString("deleted").equals("1");

                                if (!deleted) { // don't want deleted transactions on the home screen
                                    CardFormatterInterface cardFormatter = new CardFormatter();
                                    String[] formatted = cardFormatter.TransactionFormatter(id, date, amount, sender, receiver, type, subtype, false);

                                    titles.add(formatted[0]);
                                    descriptions.add(formatted[1]);
                                    status.add(formatted[2]);
                                    metadata.add(formatted[3]);
                                }
                            }
                        }
                        else {
                            titles.add("None");
                            descriptions.add("");
                            status.add(" # ");
                            metadata.add("");
                        }

                        pushData(homepageInterface);
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

    public void pushData(HomepageInterface homepageInterface) {
        //send back to UI
        homepageInterface.populateRecyclerView(titles, descriptions, status, metadata);
    }

    public void getBudget(HomepageInterface home) {
        final String URL_Budget = "https://studev.groept.be/api/a20sd505/getBudget";

        RequestQueue requestQueue = Volley.newRequestQueue(home.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_Budget, null,
                response -> {
                    try {
                        home.displayBudget(response.getJSONObject(0).getDouble("balance"));
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
