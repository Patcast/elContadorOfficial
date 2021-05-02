package be.kuleuven.elcontador10.background.database;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import be.kuleuven.elcontador10.background.CardFormatter;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsDisplayInterface;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;
import be.kuleuven.elcontador10.background.parcels.NewTransactionParcel;
import be.kuleuven.elcontador10.background.interfaces.CardFormatterInterface;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsInterface;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsNewInterface;

public class TransactionsManager {
    private final String all_URL = "https://studev.groept.be/api/a20sd505/getTransactions";
    private final String single_URL = "https://studev.groept.be/api/a20sd505/getTransaction/";

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
    public void getTransactions(TransactionsInterface transactions, FilterTransactionsParcel filter) {
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

                            if (Filter(filter, date, sender.toLowerCase(), receiver.toLowerCase(), type, subtype)) {
                                CardFormatterInterface cardFormatter = new CardFormatter();
                                String[] formatted = cardFormatter.TransactionFormatter(id, date, amount, sender, receiver, type, subtype);

                                titles.add(formatted[0]);
                                descriptions.add(formatted[1]);
                                status.add(formatted[2]);
                                metadata.add(formatted[3]);
                            }
                        }

                        populateRecyclerView(transactions);
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

    private void populateRecyclerView(TransactionsInterface transactions) {
        transactions.populateRecyclerView(titles, descriptions, status, metadata);
    }
}
