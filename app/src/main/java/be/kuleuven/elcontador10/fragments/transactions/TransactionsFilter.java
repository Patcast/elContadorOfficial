package be.kuleuven.elcontador10.fragments.transactions;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Cashing;
import be.kuleuven.elcontador10.background.database.TransactionsManager;
import be.kuleuven.elcontador10.background.interfaces.CashingObserver;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsFilterInterface;
import be.kuleuven.elcontador10.model.StakeHolder;
import be.kuleuven.elcontador10.model.TransactionType;

public class TransactionsFilter extends Fragment implements CashingObserver {
    private MainActivity mainActivity;

    private Spinner spCategory;
    private Spinner spSubCategory;
    private Switch switchFrom;
    private Switch switchTo;
    private AutoCompleteTextView txtStakeHolder;
    private TextView dateFrom;
    private TextView dateTo;

    List<TransactionType> transTypes = new ArrayList<>();
    List<StakeHolder> stakeHolds = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cashing.INSTANCE.attachChasing(this); //Adds Transaction new to the list of observers of Cashing
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle("Filter Transactions");
        return inflater.inflate(R.layout.fragment_transactions_filter, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get transactions type from database
//        TransactionsManager manager = TransactionsManager.getInstance();
//        manager.getTransactionTypes(this);

        // get views
        spCategory = getView().findViewById(R.id.FilterTransCategory);
        spSubCategory = getView().findViewById(R.id.FilterTransSubcategory);
        switchFrom = getView().findViewById(R.id.FilterTransFromSwitch);
        switchTo = getView().findViewById(R.id.FilterTransToSwitch);
        txtStakeHolder = getView().findViewById(R.id.FilterTransName);
        dateFrom = getView().findViewById(R.id.FilterTransFrom);
        dateTo = getView().findViewById(R.id.FilterTransTo);

        setWidgets(view);

        // onClickListeners
        switchFrom.setOnCheckedChangeListener(this::From_onClick);
        switchTo.setOnCheckedChangeListener(this::To_onClick);
        //Set sp_SubCategory after clicking on category
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String catChosen = spCategory.getSelectedItem().toString();
                ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,
                        transTypes.stream()
                                .filter(cat->cat.getCategory().equals(catChosen))
                                .map(TransactionType::getSubCategory)
                                .distinct().collect(Collectors.toList()));
                spSubCategory.setAdapter(adapterSpinner);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set navigation
        final NavController navController = Navigation.findNavController(view);
        /// Navigate to Transaction and sent filerSent object  after pressing filter
        Button filterButton = view.findViewById(R.id.btn_filter_FilterTrans);
        filterButton.setOnClickListener((v)-> {
            TransactionsFilterDirections.ActionTransactionsFilterToTransactionsSummary action =
                    TransactionsFilterDirections.actionTransactionsFilterToTransactionsSummary(getFilter());
            navController.navigate(action);
        });
        Button closeButton = view.findViewById(R.id.btn_cancel_FilterTrans);
        closeButton.setOnClickListener(v -> {
            // default filter
            FilterTransactionsParcel parcel = new FilterTransactionsParcel("*", "*", "*", null, null);

            TransactionsFilterDirections.ActionTransactionsFilterToTransactionsSummary action =
                    TransactionsFilterDirections.actionTransactionsFilterToTransactionsSummary(parcel);
            navController.navigate(action);
        });
    }

   /* @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setCategories( List<TransactionType> types) {

        all_categories = types;
        categories.clear();
        categories.add("All");
        categories.addAll(types.stream().map(TransactionType::getCategory).distinct().collect(Collectors.toList()));
        ArrayAdapter adapter = new ArrayAdapter(mainActivity, android.R.layout.simple_spinner_dropdown_item, categories);
        category.setAdapter(adapter);

        // category onItemSelected listener
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // select subcategory
                if (position != 0) {
                    String category_string = category.getSelectedItem().toString();
                    ArrayList<String> subs = new ArrayList<>();
                    subs.add("All");
                    subs.addAll(all_categories.stream()
                            .filter(cat -> cat.getCategory().equals(category_string))
                            .map(TransactionType::getSubCategory)
                            .distinct()
                            .collect(Collectors.toList()));

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                            subs);
                    subcategory.setAdapter(adapter);
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item,
                            new String[]{"All"});
                    subcategory.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }*/


    /// Set spinner for category & Autofill for Stakeholders
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setWidgets(View view){

        // Implement Categories spinner **********
        ArrayAdapter adapterSpinnerCat = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,
                transTypes.stream()
                        .map(TransactionType::getCategory)
                        .distinct()
                        .collect(Collectors.toList()));
        spCategory.setAdapter(adapterSpinnerCat);

        //Implements auto-fill stakeholder *********
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,
                stakeHolds.stream()
                        .map(StakeHolder::getFullNameId)
                        .distinct()
                        .collect(Collectors.toList()));
        txtStakeHolder.setAdapter(adapter);
    }
    @Override
    public Context getContext() { return mainActivity; }

    private void From_onClick(View view, boolean isChecked) {
        if (isChecked) dateFrom.setEnabled(true);
        else dateFrom.setEnabled(false);
    }

    private void To_onClick(View view, boolean isChecked) {
        if (isChecked) dateTo.setEnabled(true);
        else dateTo.setEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public FilterTransactionsParcel getFilter(){
        String category_string, subcategory_string;

        if (spCategory.getSelectedItemPosition() == 0) category_string = "*";
        else category_string = spCategory.getSelectedItem().toString();

        if (spSubCategory.getSelectedItemPosition() == 0) subcategory_string = "*";
        else subcategory_string = spSubCategory.getSelectedItem().toString();

        String name_text = txtStakeHolder.getText().toString();
        LocalDateTime from = null;
        LocalDateTime to = null;

        if (name_text.equals("")) name_text = "*";

        if (dateFrom.isEnabled()) {
            try {
                String[] date = dateFrom.getText().toString().split("/");

                from = LocalDateTime.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]), 0, 0 ,0);
            } catch (Exception e) {
                Toast.makeText(mainActivity, "Invalid date.", Toast.LENGTH_SHORT).show();
            }
        }

        if (dateTo.isEnabled()) {
            try {
                String[] date = dateTo.getText().toString().split("/");

                to = LocalDateTime.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]), 23, 59, 59);
            } catch (Exception e) {
                Toast.makeText(mainActivity, "Invalid date.", Toast.LENGTH_SHORT).show();
            }
        }

        //mainActivity.setSelectedFragment(new TransactionsSummary(filter), "TransactionsSummary");
        return new FilterTransactionsParcel(category_string, subcategory_string, name_text, from, to);
    }

    @Override
    public void notifyRoles(List<String> roles) {

    }

    @Override
    public void notifyCategories(List<TransactionType> transTypes) {
        this.transTypes.clear();
        this.transTypes.addAll(transTypes);
    }
    @Override
    public void notifyStakeHolders(List<StakeHolder> stakeHolders) {
        stakeHolds.clear();
        stakeHolds.addAll(stakeHolders);
    }
}


