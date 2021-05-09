package be.kuleuven.elcontador10.model;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionType {
    int id;
    String category;
    String subCategory;

    public TransactionType(int id ,String cat,String sub) {
        category =cat;
        subCategory = sub;
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public int getId() {
        return id;
    }
}
