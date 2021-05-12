package be.kuleuven.elcontador10.background.database;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.LogIn;
import be.kuleuven.elcontador10.background.interfaces.CashingObserver;
import be.kuleuven.elcontador10.background.interfaces.LogInInterface;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;
import be.kuleuven.elcontador10.fragments.transactions.TransactionNew;
import be.kuleuven.elcontador10.model.StakeHolder;
import be.kuleuven.elcontador10.model.TransactionType;

public enum Cashing {
    INSTANCE;
    ////*********Data
    private List <StakeHolder> stakeHolders = new ArrayList<>();
    private List <TransactionType>  transTypes = new ArrayList<>();
    private List <String> roles = new ArrayList<>();
    ///********** Observers List
    private List <CashingObserver> observers = new ArrayList<>();
    ///********** Variables
     View view;
     Context context;


    ///Attach method

    public void attachChasing(CashingObserver newObserver){
        observers.add(newObserver);
        newObserver.notifyStakeHolders(stakeHolders);
        newObserver.notifyCategories(transTypes);
        newObserver.notifyRoles(roles);

    }
    /// Set Data
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAllData(Context context){
        this.context = context;
        requestTypeTrans();
        requestStakeHold();
        requestRoles();
        observers.forEach(o ->o.notifyStakeHolders(stakeHolders) );
        observers.forEach(o ->o.notifyCategories(transTypes) );
        observers.forEach(o ->o.notifyRoles(roles));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAllObservers(){
        observers.forEach(o ->o.notifyStakeHolders(stakeHolders) );
        observers.forEach(o ->o.notifyCategories(transTypes) );
        observers.forEach(o ->o.notifyRoles(roles));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setStakeHolders (){
        requestStakeHold();
        observers.forEach(o-> o.notifyStakeHolders(stakeHolders));
    }

    //////// DATA BASE ****************
    public void requestTypeTrans(){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, DataBaseURL.INSTANCE.getTranType, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONArray response) {
                try{
                    transTypes.clear();
                    for(int i=0; i<response.length();i++){
                        JSONObject recordJson = response.getJSONObject(i);
                        TransactionType record = new TransactionType(recordJson.getInt("idTransactionType"),recordJson.getString("type"),recordJson.getString("subType"));
                        transTypes.add(record);
                    }
                   // setSpinners();
                }
                catch(JSONException e){
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Unable upload TypesTable", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(submitRequest);
    }
    public void requestStakeHold(){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, DataBaseURL.INSTANCE.getStakeHolder, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    stakeHolders.clear();
                    for(int i=0; i<response.length();i++){
                        JSONObject recordJson = response.getJSONObject(i);
                        StakeHolder record = new StakeHolder(recordJson.getInt("idStakeholders")
                                                            ,recordJson.getString("firstName")
                                                            ,recordJson.getString("LastName")
                                                            ,recordJson.getString("Role")
                                                            ,(recordJson.getInt("deleted"))>= 1);
                        stakeHolders.add(record);
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Unable upload StakeHolderTable", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);
    }
    public void requestRoles() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, DataBaseURL.INSTANCE.getRoles, null,
                response -> {
                    try {
                        roles.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject recordJson = response.getJSONObject(i);
                            roles.add(recordJson.getString("Role"));
                        }

                       // manager.onLoginSucceed(username, loggedIn, roles);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //manager.onLoginFailed(e.toString());
                    }
                },
                //error -> manager.onLoginFailed(error.toString()));
                error -> Toast.makeText(context, "Unable upload StakeHolderTable", Toast.LENGTH_LONG).show());

        requestQueue.add(request);
    }
}
