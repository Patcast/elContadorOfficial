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

    private ArrayList<String> transactions_titles;
    private ArrayList<String> transactions_descriptions;
    private ArrayList<String> transactions_status;
    private ArrayList<String> transactions_metadata;

    private ArrayList<String> tenants_titles;
    private ArrayList<String> tenants_descriptions;
    private ArrayList<String> tenants_status;
    private ArrayList<String> tenants_metadata;

//    private ArrayList<String> contracts_titles;
//    private ArrayList<String> contracts_descriptions;
//    private ArrayList<String> contracts_status;
//    private ArrayList<String> contracts_metadata;

    private HomepageManager() {
        transactions_titles = new ArrayList<>();
        transactions_descriptions = new ArrayList<>();
        transactions_status = new ArrayList<>();
        transactions_metadata = new ArrayList<>();

        tenants_titles = new ArrayList<>();
        tenants_descriptions = new ArrayList<>();
        tenants_status = new ArrayList<>();
        tenants_metadata = new ArrayList<>();

//        contracts_titles = new ArrayList<>();
//        contracts_descriptions = new ArrayList<>();
//        contracts_status = new ArrayList<>();
//        contracts_metadata = new ArrayList<>();
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
                        transactions_titles.clear();
                        transactions_descriptions.clear();
                        transactions_status.clear();
                        transactions_metadata.clear();

                        transactions_titles.add("WHITE#Recent transactions");
                        transactions_descriptions.add("Here are the 5 most recent transactions:");
                        transactions_status.add(" # "); // needs to be added so that the list index are the same
                                                        // empty status needs to be " # " for the colour formatter
                        transactions_metadata.add("");

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

                                CardFormatterInterface cardFormatter = new CardFormatter();
                                String[] formatted = cardFormatter.TransactionFormatter(id, date, amount, sender, receiver, type, subtype);

                                transactions_titles.add(formatted[0]);
                                transactions_descriptions.add(formatted[1]);
                                transactions_status.add(formatted[2]);
                                transactions_metadata.add(formatted[3]);
                            }
                        }
                        else {
                            transactions_titles.add("None");
                            transactions_descriptions.add("");
                            transactions_status.add(" # ");
                            transactions_metadata.add("");
                        }

                        getInDebtTenants(homepageInterface);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getInDebtTenants(HomepageInterface homepageInterface) {
        RequestQueue requestQueue = Volley.newRequestQueue(homepageInterface.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_Tenants, null,
                response -> {
                    try {
                        tenants_titles.clear();
                        tenants_descriptions.clear();
                        tenants_status.clear();
                        tenants_metadata.clear();

                        tenants_titles.add("WHITE#Stakeholders in debt");
                        tenants_descriptions.add("Here are the stakeholders in debt:");
                        tenants_status.add(" # ");
                        tenants_metadata.add("");

                        if (response.length() != 0) {
                            for (int i = 0; i < response.length() ; i++) {
                                JSONObject object = response.getJSONObject(i);

                                int id = object.getInt("idStakeholders");
                                String firstName = object.getString("firstName");
                                String lastName = object.getString("LastName");
                                double balance = object.getDouble("balance");
                                String role = object.getString("Role");

                                CardFormatterInterface cardFormatter = new CardFormatter();
                                String[] formatted = cardFormatter.StakeholderFormatter(id, firstName, lastName, balance, role);

                                tenants_titles.add(formatted[0]);
                                tenants_descriptions.add(formatted[1]);
                                tenants_status.add(formatted[2]);
                                tenants_metadata.add(formatted[3]);
                            }
                        }
                        else {
                            tenants_titles.add("None");
                            tenants_descriptions.add("");
                            tenants_status.add(" # "); // needs to be added so that the list index are the same
                            tenants_metadata.add("");
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

    /*
    Not required anymore
     */
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void getEndingContracts(HomepageInterface homepageInterface) {
//        LocalDate now = LocalDate.now();
//        LocalDate later = now.plusMonths(1);
//
//        String URL_FINAL = URL_Contracts + now.getYear() + "-" + now.getMonthValue() + "-" + now.getDayOfMonth() + "/" +
//                later.getYear() + "-" + later.getMonthValue() + "-" + later.getDayOfMonth();
//
//        RequestQueue requestQueue = Volley.newRequestQueue(homepageInterface.getContext());
//
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_FINAL, null,
//                response -> {
//                    try {
//                        contracts_titles.clear();
//                        contracts_descriptions.clear();
//                        contracts_status.clear();
//                        contracts_metadata.clear();
//
//                        contracts_titles.add("WHITE#Contracts ending this month");
//                        contracts_descriptions.add("Here are the contracts ending this month:");
//                        contracts_status.add(" # ");
//                        contracts_metadata.add("");
//
//                        if (response.length() != 0) {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject object = response.getJSONObject(i);
//
//                                int idContract = object.getInt("idContracts");
//
//                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                                LocalDate startDate = LocalDate.parse(object.getString("startDate"), formatter);
//                                LocalDate endDate = LocalDate.parse(object.getString("endDate"), formatter);
//
//                                double amount = object.getDouble("amount");
//                                String firstName = object.getString("firstName");
//                                String lastName = object.getString("lastName");
//                                int idPlace = object.getInt("idPlace");
//                                String placeType = object.getString("placeType");
//                                String contractType = object.getString("idContractType");
//
//                                CardFormatterInterface cardFormatter = new CardFormatter();
//                                String[] formatted = cardFormatter.ContractFormatter(idContract, startDate, endDate, amount,
//                                        firstName, lastName, idPlace, placeType, contractType);
//
//                                contracts_titles.add(formatted[0]);
//                                contracts_descriptions.add(formatted[1]);
//                                contracts_status.add(formatted[2]);
//                                contracts_metadata.add(formatted[3]);
//                            }
//                        }
//                        else {
//                            contracts_titles.add("None");
//                            contracts_descriptions.add("");
//                            contracts_status.add(" # "); // needs to be added so that the list index are the same
//                            contracts_metadata.add("");
//                        }
//
//                        pushData(homepageInterface);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        homepageInterface.error(e.toString());
//                    }
//                },
//                error -> {
//                    error.printStackTrace();
//                    homepageInterface.error(error.toString());
//                });
//        requestQueue.add(request);
//    }

    public void pushData(HomepageInterface homepageInterface) {
        // combine all arrays into one
        ArrayList<String> titles = new ArrayList<>();
        titles.addAll(transactions_titles);
        titles.addAll(tenants_titles);
//        titles.addAll(contracts_titles);
        
        ArrayList<String> descriptions = new ArrayList<>();
        descriptions.addAll(transactions_descriptions);
        descriptions.addAll(tenants_descriptions);
//        descriptions.addAll(contracts_descriptions);

        ArrayList<String> status = new ArrayList<>();
        status.addAll(transactions_status);
        status.addAll(tenants_status);
//        status.addAll(contracts_status);
        
        ArrayList<String> metadata = new ArrayList<>();
        metadata.addAll(transactions_metadata);
        metadata.addAll(tenants_metadata);
//        metadata.addAll(contracts_metadata);

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
