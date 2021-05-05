package be.kuleuven.elcontador10.background.database;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class JsonArrayRequestWithParams extends JsonArrayRequest {
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