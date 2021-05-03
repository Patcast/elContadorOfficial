package be.kuleuven.elcontador10.background.database;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.elcontador10.background.CardFormatter;
import be.kuleuven.elcontador10.background.interfaces.CardFormatterInterface;
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersDisplayInterface;
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersSummaryInterface;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;
import be.kuleuven.elcontador10.fragments.stakeholders.StakeholderSummary;

public class StakeholdersManager {
    private final String all_URL = "https://studev.groept.be/api/a20sd505/getStakeholders";
    private final String single_URL = "https://studev.groept.be/api/a20sd505/getStakeholder/";
    private final String delete_URL = "https://studev.groept.be/api/a20sd505/deleteStakeholder/";

    private static volatile  StakeholdersManager INSTANCE = null;

    private final ArrayList<String> titles;
    private final ArrayList<String> descriptions;
    private final ArrayList<String> status;
    private final ArrayList<String> metadata;

    private StakeholdersManager() {
        titles = new ArrayList<>();
        descriptions = new ArrayList<>();
        status = new ArrayList<>();
        metadata = new ArrayList<>();
    }

    public static StakeholdersManager getInstance() {
        if (INSTANCE == null) {
            synchronized (StakeholdersManager.class) {
                if (INSTANCE == null) INSTANCE = new StakeholdersManager();
            }
        }

        return INSTANCE;
    }

    public void getStakeholders(StakeholdersSummaryInterface summary, FilterStakeholdersParcel filter) {
        RequestQueue requestQueue = Volley.newRequestQueue(summary.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, all_URL, null,
                response -> {
                    try {
                        titles.clear();
                        descriptions.clear();
                        status.clear();
                        metadata.clear();

                        for (int i = 0; i < response.length() ; i++) {
                            JSONObject object = response.getJSONObject(i);

                            int id = object.getInt("idStakeholders");
                            String firstName = object.getString("firstName");
                            String lastName = object.getString("LastName");
                            double balance = object.getDouble("balance");
                            String role = object.getString("Role");

                            if (Filter(filter, (lastName + " " + balance).toLowerCase(), balance, role)) {
                                CardFormatterInterface cardFormatter = new CardFormatter();
                                String[] formatted = cardFormatter.StakeholderFormatter(id, firstName, lastName, balance, role);

                                titles.add(formatted[0]);
                                descriptions.add(formatted[1]);
                                status.add(formatted[2]);
                                metadata.add(formatted[3]);
                            }
                        }

                        summary.populateRecyclerView(titles, descriptions, status, metadata);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        summary.error(e.toString());
                    }
                },
                error -> {
                    error.printStackTrace();
                    summary.error(error.toString());
                });
        requestQueue.add(request);
    }

    private boolean Filter(FilterStakeholdersParcel filter, String fullName, double balance, String role) {
        String name = filter.getName().toLowerCase();
        ArrayList<String> roles = filter.getRoles();
        boolean debt = filter.isInDebt();

        if (!name.equals("*")) if (!fullName.contains(name)) return false; // name not matching
        if (!roles.contains(role)) return false; // role not in list
        if (debt && balance >= 0) return false; // not in debt

        return true;
    }

    public void getStakeholder(StakeholdersDisplayInterface stakeholder, String id) {
        String final_URL = single_URL + id;
        RequestQueue requestQueue = Volley.newRequestQueue(stakeholder.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, final_URL, null,
                response ->  {
                    try {
                        Bundle bundle = new Bundle();
                        JSONObject object = response.getJSONObject(0);

                        bundle.putString("name", object.getString("name"));
                        bundle.putString("role", object.getString("Role"));
                        bundle.putString("phone", object.getString("phoneNumber"));
                        bundle.putString("email", object.getString("email"));
                        bundle.putDouble("balance", object.getDouble("balance"));
                        bundle.putString("image", object.getString("image"));

                        stakeholder.display(bundle);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        stakeholder.error(e.toString());
                    }
                },
                error -> {
                    error.printStackTrace();
                    stakeholder.error(error.toString());
                });

        requestQueue.add(request);
    }

    public void deleteStakeholder(StakeholdersDisplayInterface stakeholders, String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(stakeholders.getContext());

        StringRequest request = new StringRequest(Request.Method.POST, delete_URL,
                response -> stakeholders.delete(),
                error -> stakeholders.error("Unable to delete")) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        requestQueue.add(request);
    }
}
