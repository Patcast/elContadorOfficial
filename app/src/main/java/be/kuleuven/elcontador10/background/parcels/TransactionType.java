package be.kuleuven.elcontador10.background.parcels;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionType {

    String category;
    String subCategory;

    public TransactionType(String cat,String sub) {
        category =cat;
        subCategory = sub;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }
}
