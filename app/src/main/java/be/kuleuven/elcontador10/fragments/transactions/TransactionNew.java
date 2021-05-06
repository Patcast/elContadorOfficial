package be.kuleuven.elcontador10.fragments.transactions;



import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.Transaction;
import be.kuleuven.elcontador10.background.database.JsonArrayRequestWithParams;
import be.kuleuven.elcontador10.background.parcels.TransactionType;


public class TransactionNew extends Fragment {

//// input from UI
    RadioGroup radGroup;
    EditText txtAmount;
    AutoCompleteTextView txtStakeHolder;
    Spinner spCategory;
    Spinner spSubCategory;
    EditText txtNotes;
    ////// Arrays to fill input

    List<TransactionType> typeFullList = new ArrayList<>();
    List<String> categories = new ArrayList<>();
    List<List<String>>  Subcategories = new ArrayList<>();
    ArrayList<String> stakeHolders = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_new, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radGroup = view.findViewById(R.id.radioGroup);
        txtAmount = view.findViewById(R.id.ed_txt_amount);
        spCategory = view.findViewById(R.id.sp_TransCategory);
        spSubCategory = view.findViewById(R.id.sp_TransSubcategory);
        txtNotes = view.findViewById(R.id.ed_txt_notes);
        requestStakeHold(view);
        requestTypeTrans(view);

        //Set sp_SubCategory after clicking on category
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String catChosen = spCategory.getSelectedItem().toString();
                ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,
                        typeFullList.stream()
                                    .filter(cat->cat.getCategory().equals(catChosen))
                                    .map(TransactionType::getSubCategory)
                                    .distinct().collect(Collectors.toList()));
                spSubCategory.setAdapter(adapterSpinner);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /// Navigates to  All Transaction and sends New transaction to db
        final NavController navController = Navigation.findNavController(view);
        Button confirmButton = view.findViewById(R.id.btn_confirm_NewTransaction);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_newTransaction_to_transactions_summary);
                postNewTransaction(view);

            }
        });
    }

    /////////////// Get information from db *******************************************

////  This is to create objects of the class TypeCategory for the spinners and to get the Id of trans from the db
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
                            typeFullList.add(record);
                            }
                    Toast.makeText(getActivity(), "successful ", Toast.LENGTH_LONG).show();
                    setSpinners();
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

/// Set spinner for category
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setSpinners(){

        categories.clear();
        categories.addAll(typeFullList.stream().map(TransactionType::getCategory).distinct().collect(Collectors.toList()));
        ArrayAdapter adapterSpinnerCat = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,categories);
        spCategory.setAdapter(adapterSpinnerCat);
    }

/////////This is to fill up the stakeholders auto-fill with the stakeholders form the db
    public void requestStakeHold(View view){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String requestURL = "https://studev.groept.be/api/a20sd505/getStakeHolderName";
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{

                    for(int i=0; i<response.length();i++){
                        JSONObject recordJson = response.getJSONObject(i);
                       stakeHolders.add(recordJson.getString("name"));
                    }
                    //Set AutocompleteText
                    txtStakeHolder = view.findViewById(R.id.actv_stakeholder);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,stakeHolders);
                    txtStakeHolder.setAdapter(adapter);

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

    /// This method reads the radio groups and returns true is the cash in radio button is selected.
    public boolean transCashIn(RadioGroup radioGroup){
        return radioGroup.getCheckedRadioButtonId() == R.id.radio_CashIn;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Transaction makeNewTrans(){
        boolean cashIn = transCashIn(radGroup);
        double amount = Double.parseDouble(txtAmount.getText().toString());
        //char [] stakeholder =  txtStakeHolder.getText().toString().toCharArray();
        //int idStake = stakeholder[0];
        String stakeholder =  txtStakeHolder.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        String subCategory = spSubCategory.getSelectedItem().toString();
        String notes = txtNotes.getText().toString();
        Optional<TransactionType> searchIdType = typeFullList.stream()
                                                             .filter(cat ->cat.getCategory().equals(category))
                                                             .filter(subCat -> subCat.getSubCategory().equals(subCategory))
                                                             .findFirst();
        int idType= searchIdType.get().getId();
        return new Transaction(cashIn,amount,Character.getNumericValue(stakeholder.charAt(0)),idType,notes);
    }

    ////// Posts the content from the NewTransaction to the db
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void postNewTransaction(View view){
        ///Make HashMap for params
        Transaction newTrans = makeNewTrans();
        Map<String,String> params = new HashMap<>();
        params.put("amount", String.valueOf(newTrans.getAmount()));
        params.put("notes", newTrans.getTxtComments());
        params.put("idpays", newTrans.getStakePays());
        params.put("idrec", newTrans.getStakeReceives());
        params.put("type", newTrans.getIdType());


        // Make Json request
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String RequestURL = "https://studev.groept.be/api/a20sd505/postNewTransaction/";
        JsonArrayRequestWithParams submitRequest = new JsonArrayRequestWithParams (Request.Method.POST, RequestURL, params,  new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(getActivity(), "Transaction placed", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Unable to place the Transaction", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(submitRequest);

    }







}