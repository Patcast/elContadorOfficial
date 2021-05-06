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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.elcontador10.background.CardFormatter;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsDisplayInterface;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsFilterInterface;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsSummaryInterface;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;
import be.kuleuven.elcontador10.background.parcels.NewTransactionParcel;
import be.kuleuven.elcontador10.background.interfaces.CardFormatterInterface;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsNewInterface;
import be.kuleuven.elcontador10.background.parcels.TransactionType;

public class TransactionsManager {
    private final String all_URL = "https://studev.groept.be/api/a20sd505/getTransactions";
    private final String single_URL = "https://studev.groept.be/api/a20sd505/getTransaction/";
    private final String type_URL = "https://studev.groept.be/api/a20sd505/getTransactionTypes";
    private final String delete_URL = "https://studev.groept.be/api/a20sd505/deleteTransaction/";

    private static volatile TransactionsManager INSTANCE = null;

    private final ArrayList<String> titles;
    private final ArrayList<String> descriptions;
    private final ArrayList<String> status;
    private final ArrayList<String> metadata;

    private TransactionsManager() {
        titles = new ArrayList<>();
        descriptions = new ArrayList<>();
        status = new ArrayList<>();
        metadata = new ArrayList<>();
    }

    public static TransactionsManager getInstance() {
        if (INSTANCE == null) {
            synchronized (TransactionsManager.class) {
                if (INSTANCE == null) INSTANCE = new TransactionsManager();
            }
        }

        return INSTANCE;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getTransactions(TransactionsSummaryInterface transactions, FilterTransactionsParcel filter) {
        RequestQueue requestQueue = Volley.newRequestQueue(transactions.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, all_URL, null,
                response -> {
                    try {
                        titles.clear();
                        descriptions.clear();
                        status.clear();
                        metadata.clear();

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

                            if (Filter(filter, date, sender.toLowerCase(), receiver.toLowerCase(), type, subtype)) {
                                CardFormatterInterface cardFormatter = new CardFormatter();
                                String[] formatted = cardFormatter.TransactionFormatter(id, date, amount, sender, receiver, type, subtype, deleted);

                                titles.add(formatted[0]);
                                descriptions.add(formatted[1]);
                                status.add(formatted[2]);
                                metadata.add(formatted[3]);
                            }
                        }

                        transactions.populateRecyclerView(titles, descriptions, status, metadata);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        transactions.error(e.toString());
                    }
                },
                error -> {
                    error.printStackTrace();
                    transactions.error(error.toString());
                });
        requestQueue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean Filter(FilterTransactionsParcel filter, LocalDateTime timestamp, String sender, String receiver, String type, String subtype) {
        String category = filter.getCategory();
        String subcategory = filter.getSubcategory();
        String name = filter.getName().toLowerCase();
        LocalDateTime from = filter.getFrom();
        LocalDateTime to = filter.getTo();

        if (!category.equals("*") && !type.equals(category)) return false; // category doesn't match
        if (!subcategory.equals("*") && !subtype.equals(subcategory)) return false; // subcategory doesn't match

        if (!name.equals("*")) if (!sender.contains(name) && !receiver.contains(name)) return false; // name not in sender or receiver

        if (from != null) if (timestamp.isBefore(from)) return  false; // transaction happened before given period
        if (to != null) return !timestamp.isAfter(to); // transaction happened after given period

        return true;
    }

    public void addTransactions(TransactionsNewInterface newInterface, NewTransactionParcel parcel) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getTransaction(TransactionsDisplayInterface displayInterface, String id) {
        String final_URL = single_URL + id;
        RequestQueue requestQueue = Volley.newRequestQueue(displayInterface.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, final_URL, null,
                response -> {
                    try {
                        Bundle bundle = new Bundle();
                        JSONObject object = response.getJSONObject(0);

                        bundle.putString("sender", object.getString("sender"));
                        bundle.putString("receiver", object.getString("receiver"));
                        bundle.putString("amount", object.getString("amount"));
                        bundle.putString("category", object.getString("type"));
                        bundle.putString("subcategory", object.getString("subType"));
                        bundle.putString("notes", object.getString("notes"));

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime date = LocalDateTime.parse(object.getString("date"), formatter);
                        String date_text = date.getDayOfWeek().toString() + " " + date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear() +
                                " " + date.getHour() + ":" + date.getMinute() + ":" + date.getSecond();
                        bundle.putString("date", date_text);

                        displayInterface.display(bundle);

                    } catch (Exception e) {
                        e.printStackTrace();
                        displayInterface.error(e.toString());
                    }
                },
                error -> {
                    error.printStackTrace();
                    displayInterface.error(error.toString());
                });
        requestQueue.add(request);
    }

    public void getTransactionTypes(TransactionsFilterInterface filterInterface) {
        RequestQueue requestQueue = Volley.newRequestQueue(filterInterface.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, type_URL, null,
                response -> {
                    try {
                        ArrayList<TransactionType> types = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);
                            TransactionType type = new TransactionType(object.getInt("idTransactionType"),
                                    object.getString("type"), object.getString("subType"));
                            types.add(type);
                        }

                        filterInterface.setCategories(types);
                    } catch (JSONException e) { e.printStackTrace(); }
                }, Throwable::printStackTrace);

        requestQueue.add(request);
    }

    public void deleteTransaction(TransactionsDisplayInterface display, String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(display.getContext());

        StringRequest request = new StringRequest(Request.Method.POST, delete_URL,
                response -> display.error("Transaction has been deleted"),
                error -> display.error(error.toString())) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        requestQueue.add(request);
    }
}
