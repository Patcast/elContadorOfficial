package be.kuleuven.elcontador10.fragments.transactions;



import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.Transaction;
import be.kuleuven.elcontador10.background.parcels.TransactionType;


public class TransactionNew extends Fragment {

    private static final String[] stakeHolders = new String[]{"Carlos","Mauricio","Tomas","Juan","Patricio","Alexandria","Yonathan"};
    RadioGroup radGroup;
    EditText txtAmount;
    AutoCompleteTextView txtStakeHolder;
    Spinner spCategory;
    Spinner spSubCategory;
    EditText txtNotes;
    List<Map<String,String>> typeRecords;
    List<String> categories = new ArrayList<>();
    List<TransactionType> typeList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_new, container, false);
        requestTypeTrans(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        typeRecords = new ArrayList<>();

        radGroup = view.findViewById(R.id.radioGroup);
        txtAmount = view.findViewById(R.id.ed_txt_amount);
        spCategory = view.findViewById(R.id.sp_TransCategory);
        spSubCategory = view.findViewById(R.id.sp_TransSubcategory);
        txtNotes = view.findViewById(R.id.ed_txt_notes);
        requestTypeTrans(view);

        //Set AutocompleteText
        txtStakeHolder = view.findViewById(R.id.actv_stakeholder);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,stakeHolders);
        txtStakeHolder.setAdapter(adapter);

        //Set sp_SubCategory
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String catChosen = spCategory.getSelectedItem().toString();
                ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, Transaction.chooseSubCat(catChosen));
                spSubCategory.setAdapter(adapterSpinner);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /// This method reads the radio groups and returns true is the cash in radio button is selected.
    public boolean transCashIn(RadioGroup radioGroup){
        return radioGroup.getCheckedRadioButtonId() == R.id.radio_CashIn;
    }


    public void requestTypeTrans(View view){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String requestURL = "https://studev.groept.be/api/a20sd505/getTransactionTypes";
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONArray response) {
                try{

                        for(int i=0; i<response.length();i++){
                        JSONObject recordJson = response.getJSONObject(i);
                            TransactionType record = new TransactionType(recordJson.getInt("idTransactionType"),recordJson.getString("type"),recordJson.getString("subType"));
                            typeList.add(record);
                            }
                    Toast.makeText(getActivity(), "successful ", Toast.LENGTH_LONG).show();
                    lateSettings();
                }
                catch(JSONException e){
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Unable upload TypesTable", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(submitRequest);
    }

    private Transaction makeNewTrans(){
        boolean cashIn = transCashIn(radGroup);
        double amount = Double.parseDouble(txtAmount.getText().toString());
        String stakeholder =  txtStakeHolder.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        String subCategory = spSubCategory.getSelectedItem().toString();
        String notes = txtNotes.getText().toString();

        return new Transaction(cashIn,amount,stakeholder,category,subCategory,notes);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void lateSettings(){

        categories.clear();
        categories.addAll(typeList.stream().map(TransactionType::getCategory).distinct().collect(Collectors.toList()));
        /// Set sp_Category
        ArrayAdapter adapterSpinnerCat = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,categories);
        spCategory.setAdapter(adapterSpinnerCat);

    }


}