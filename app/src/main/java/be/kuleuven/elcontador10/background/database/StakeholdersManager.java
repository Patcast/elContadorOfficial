package be.kuleuven.elcontador10.background.database;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private StakeholdersManager() { }

    public static StakeholdersManager getInstance() {
        if (INSTANCE == null) {
            synchronized (StakeholdersManager.class) {
                if (INSTANCE == null) INSTANCE = new StakeholdersManager();
            }
        }

        return INSTANCE;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getStakeholders(StakeholdersSummaryInterface summary, FilterStakeholdersParcel filter) {
        RequestQueue requestQueue = Volley.newRequestQueue(summary.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, all_URL, null,
                response -> {
                    try {
                        List<DataPlaceHolder> data = new ArrayList<>();

                        for (int i = 0; i < response.length() ; i++) {
                            JSONObject object = response.getJSONObject(i);

                            // get all data
                            int id = object.getInt("idStakeholders");
                            String firstName = object.getString("firstName");
                            String lastName = object.getString("LastName");
                            double balance = object.getDouble("balance");
                            String role = object.getString("Role");
                            boolean deleted = object.getString("deleted").equals("1");

                            // run object through filter
                            if (Filter(filter, (firstName + " " + lastName).toLowerCase(), balance, role, deleted)) {
                                data.add(new DataPlaceHolder(id, firstName, lastName, role, balance));
                            }
                        }

                        // sort
                        data = sorter(filter, data);

                        // return data
                        returnData(summary, data);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<DataPlaceHolder> sorter(FilterStakeholdersParcel filter, List<DataPlaceHolder> data) {
        List<DataPlaceHolder> sorted;

        switch (filter.getSortBy()) {
            case "Debt":
                sorted = data.stream()
                        .sorted(Comparator.comparing(DataPlaceHolder::getBalance))
                        .collect(Collectors.toList());
                break;
            case "Role":
                sorted = data.stream()
                        .sorted(Comparator.comparing(DataPlaceHolder::getRole))
                        .collect(Collectors.toList());
                break;
            default:
                sorted = data.stream()
                        .sorted(Comparator.comparing(DataPlaceHolder::getLastName))
                        .collect(Collectors.toList());
        }
        return sorted;
    }

    private boolean Filter(FilterStakeholdersParcel filter, String fullName, double balance, String role, boolean deleted) {
        String name = filter.getName().toLowerCase();
        ArrayList<String> roles = filter.getRoles();
        boolean debt = filter.isInDebt();
        boolean isDeleted = filter.isDeleted();

        if (!isDeleted == deleted) return false; // deleted not matching
        if (!name.equals("*")) if (!fullName.contains(name)) return false; // name not matching
        if (!roles.contains(role)) return false; // role not in list
        return !debt || !(balance >= 0); // not in debt
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void returnData(StakeholdersSummaryInterface summary, List<DataPlaceHolder> data) {
        // initialise formatter
        ArrayList<String[]> formatted = new ArrayList<>();
        CardFormatterInterface formatter = new CardFormatter();

        // send each data to the formatter, while adding to the formatted list
        data.forEach(t -> formatted.add(formatter.StakeholderFormatter(t.getId(), t.getFirstName(), t.getLastName(),
                t.getBalance(), t.getRole())));

        ArrayList<String> titles = new ArrayList<>(), descriptions = new ArrayList<>(), status = new ArrayList<>(), metadata = new ArrayList<>();

        // add the formatted Strings to the corresponding ArrayList
        // 0: titles, 1: descriptions, 2: status, 3: metadata
        formatted.forEach(t -> titles.add(t[0]));
        formatted.forEach(t -> descriptions.add(t[1]));
        formatted.forEach(t -> status.add(t[2]));
        formatted.forEach(t -> metadata.add(t[3]));

        // send back to interface
        summary.populateRecyclerView(titles, descriptions, status, metadata);
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

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private static class DataPlaceHolder {
        int id;
        String firstName, lastName, role;
        double balance;

        public DataPlaceHolder(int id, String firstName, String lastName, String role, double balance) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.balance = balance;
        }

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getRole() {
            return role;
        }

        public double getBalance() {
            return balance;
        }
    }
}
