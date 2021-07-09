package be.kuleuven.elcontador10.background.database;

import android.os.Build;
import android.os.Bundle;

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
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersNewInterface;
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersSummaryInterface;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

public class StakeholdersManager {

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
    public void getStakeholders(StakeholdersSummaryInterface summary, List<StakeHolder> stakeHolders, FilterStakeholdersParcel filter) {
//        RequestQueue requestQueue = Volley.newRequestQueue(summary.getContext());
        List<DataPlaceHolder> data = new ArrayList<>();

        for (StakeHolder stakeholder : stakeHolders) {
            String id = stakeholder.getId();
            String firstName = stakeholder.getName();
            String role = stakeholder.getRole();
            boolean deleted = stakeholder.isDeleted();

            // run object through filter
            if (filter != null && Filter(filter, (firstName ).toLowerCase(), role, deleted)) {
                //data.add(new DataPlaceHolder(id, firstName, role, deleted));
            }
        }

        // sort
        if (filter != null) data = sorter(filter, data);

        // return data
        returnData(summary, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<DataPlaceHolder> sorter(FilterStakeholdersParcel filter, List<DataPlaceHolder> data) {
        List<DataPlaceHolder> sorted;

        if ("Role".equals(filter.getSortBy())) {
            sorted = data.stream()
                    .sorted(Comparator.comparing(DataPlaceHolder::getRole))
                    .collect(Collectors.toList());
        } else {
            sorted = data.stream()
                    .sorted(Comparator.comparing(DataPlaceHolder::getLastName))
                    .collect(Collectors.toList());
        }
        return sorted;
    }

    private boolean Filter(FilterStakeholdersParcel filter, String fullName, String role, boolean deleted) {
        String name = filter.getName().toLowerCase();
        ArrayList<String> roles = filter.getRoles();
        boolean isDeleted = filter.isDeleted();

        if (!isDeleted == deleted) return false; // deleted not matching
        if (!name.equals("*")) if (!fullName.contains(name)) return false; // name not matching
        return roles.contains(role); // role not in list
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void returnData(StakeholdersSummaryInterface summary, List<DataPlaceHolder> data) {
        // initialise formatter
        ArrayList<String[]> formatted = new ArrayList<>();
        CardFormatterInterface formatter = new CardFormatter();

        // send each data to the formatter, while adding to the formatted list
        data.forEach(t -> formatted.add(formatter.StakeholderFormatter(t.getId(), t.getFirstName(), t.getLastName(),
                t.getRole(), t.isDeleted())));

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
        String final_URL = DatabaseURL.INSTANCE.getStakeholder + id;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteStakeholder(StakeholdersDisplayInterface stakeholders, String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(stakeholders.getContext());

        StringRequest request = new StringRequest(Request.Method.POST, DatabaseURL.INSTANCE.deleteStakeholder,
                response -> {
                    stakeholders.delete();
                    //Caching.INSTANCE.setStakeHolders();
                },
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addStakeholder(StakeholdersNewInterface stakeholder, String firstName, String lastName,
                               String role, @Nullable String phoneNo, @Nullable String email, @Nullable String image) {
        RequestQueue requestQueue = Volley.newRequestQueue(stakeholder.getContext());

        StringRequest request = new StringRequest(Request.Method.POST, DatabaseURL.INSTANCE.addStakeholder,
                response -> {
                    stakeholder.addStakeholder();
                    //Caching.INSTANCE.setStakeHolders();
                },
                error -> stakeholder.feedback(error.toString())) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fnam", firstName);
                params.put("lnam", lastName);
                params.put("ro", role);
                params.put("no", phoneNo);
                params.put("em", email);
                params.put("img", image);
                return params;
            }
        };

        requestQueue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void editStakeholder(StakeholdersNewInterface stakeholder, String id, String firstName, String lastName,
                                String role, @Nullable String phoneNo, @Nullable String email, @Nullable String image) {
        RequestQueue requestQueue = Volley.newRequestQueue(stakeholder.getContext());

        StringRequest request = new StringRequest(Request.Method.POST, DatabaseURL.INSTANCE.editStakeholder,
                response -> {
                    stakeholder.editStakeholder();
                    ///Caching.INSTANCE.setStakeHolders();
                },
                error -> {
                    stakeholder.feedback(error.toString());
                    error.printStackTrace();
            }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fnam", firstName);
                params.put("lnam", lastName);
                params.put("ro", role);
                params.put("no", phoneNo);
                params.put("em", email);
                params.put("img", image);
                params.put("id", id);

                return params;
            }
        };

        requestQueue.add(request);
    }

    private static class DataPlaceHolder {
        int id;
        String firstName, lastName, role;
        boolean deleted;

        public DataPlaceHolder(int id, String firstName, String lastName, String role,boolean deleted) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.deleted = deleted;
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

        public boolean isDeleted() {
            return deleted;
        }
    }
}
