package be.kuleuven.elcontador10.background.database;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.elcontador10.background.CardFormatter;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsDisplayInterface;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsFilterInterface;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsSummaryInterface;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;
import be.kuleuven.elcontador10.background.interfaces.CardFormatterInterface;
import be.kuleuven.elcontador10.background.model.TransactionType;


public class TransactionsManager {
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

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, DatabaseURL.INSTANCE.getTransactions, null,
                response -> {
                    try {
                        titles.clear();
                        descriptions.clear();
                        status.clear();
                        metadata.clear();
                        double subtotal = 0;

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
                            boolean deleted = object.getString("deleted").equals("1");

                            if (Filter(filter, date, user.toLowerCase(), stakeholder.toLowerCase(), type, subtype)) {
                                CardFormatterInterface cardFormatter = new CardFormatter();
                                String[] formatted = cardFormatter.TransactionFormatter(id, date, amount, user, stakeholder, type, subtype, deleted);

                                titles.add(formatted[0]);
                                descriptions.add(formatted[1]);
                                status.add(formatted[2]);
                                metadata.add(formatted[3]);

                                if (!deleted) subtotal += amount;
                            }
                        }

                        transactions.populateRecyclerView(titles, descriptions, status, metadata, subtotal);
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
    private boolean Filter(FilterTransactionsParcel filter, LocalDateTime timestamp, String maker, String stakeholder, String type, String subtype) {
        String category = filter.getCategory();
        String subcategory = filter.getSubcategory();
        String name = filter.getName().toLowerCase();
        LocalDateTime from = filter.getFrom();
        LocalDateTime to = filter.getTo();

        if (!category.equals("*") && !type.equals(category)) return false; // category doesn't match
        if (!subcategory.equals("*") && !subtype.equals(subcategory)) return false; // subcategory doesn't match

        if (!name.equals("*")) if (!maker.contains(name) && !stakeholder.contains(name)) return false; // name not in maker or stakeholder

        if (from != null) if (timestamp.isBefore(from)) return  false; // transaction happened before given period
        if (to != null) return !timestamp.isAfter(to); // transaction happened after given period

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getTransaction(TransactionsDisplayInterface displayInterface, String id) {
        String final_URL = DatabaseURL.INSTANCE.getTransaction + id;
        RequestQueue requestQueue = Volley.newRequestQueue(displayInterface.getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, final_URL, null,
                response -> {
                    try {
                        Bundle bundle = new Bundle();
                        JSONObject object = response.getJSONObject(0);

                        bundle.putString("user", object.getString("userName"));
                        bundle.putString("stakeholder", object.getString("stakeholderName"));
                        bundle.putDouble("amount", object.getDouble("amount"));
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

    public void deleteTransaction(TransactionsDisplayInterface display, String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(display.getContext());

        StringRequest request = new StringRequest(Request.Method.POST, DatabaseURL.INSTANCE.deleteTransaction,
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

/*    public void addNewTransaction(Transaction newTrans, Context useContext){
            ///Make HashMap for params
            Map<String,String> params = new HashMap<>();
            params.put("amount", String.valueOf(newTrans.getAmount()));
            params.put("notes", newTrans.getTxtComments());
            params.put("iduser", String.valueOf(newTrans.getRegisteredBy()));
            params.put("idstakeholder", newTrans.getStakeHolder());
            params.put("type", newTrans.getCategory());
            // Make Json request
            RequestQueue requestQueue = Volley.newRequestQueue(useContext);
            JsonArrayRequestWithParams submitRequest = new JsonArrayRequestWithParams (Request.Method.POST, DatabaseURL.INSTANCE.addTransaction, params,
                    response -> Toast.makeText(useContext, "Transaction placed", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(useContext, error.toString(), Toast.LENGTH_LONG).show());
            requestQueue.add(submitRequest);

    }*/

    private class JsonArrayRequestWithParams extends JsonArrayRequest {
        private Map<String, String> params;
        public JsonArrayRequestWithParams(String url, Response.Listener<JSONArray> listener, @Nullable Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        public JsonArrayRequestWithParams(int method, String url, @Nullable JSONArray jsonRequest, Response.Listener<JSONArray> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        public JsonArrayRequestWithParams(int method, String url, @Nullable Map<String, String> params, Response.Listener<JSONArray> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, null, listener, errorListener);
            this.params = params;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return this.params;
        }

        @Override
        public byte[] getBody() {
            StringBuilder stringBodyBuilder = new StringBuilder();
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    stringBodyBuilder.append("\r\n" + "--98379387434"+ "\r\n");
                    stringBodyBuilder.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n\r\n");
                    stringBodyBuilder.append(params.get(key));
                }
            }
            return stringBodyBuilder.toString().getBytes();
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            final Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "multipart/form-data;boundary=98379387434");
            return headers;
        }
    }

}
