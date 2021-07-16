package be.kuleuven.elcontador10.fragments.transactions;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.WidgetsCreation;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.interfaces.CreateWidgets;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.TransactionType;

public class TransactionsFilter extends Fragment implements CachingObserver, CreateWidgets {
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
        //Caching.INSTANCE.attachCaching(this); //Adds Transaction new to the list of observers of Caching
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.filter_transactions));
        return inflater.inflate(R.layout.fragment_transactions_filter, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get views
        spCategory = requireView().findViewById(R.id.FilterTransCategory);
        spSubCategory = requireView().findViewById(R.id.FilterTransSubcategory);
        switchFrom = requireView().findViewById(R.id.FilterTransFromSwitch);
        switchTo = requireView().findViewById(R.id.FilterTransToSwitch);
        txtStakeHolder = requireView().findViewById(R.id.FilterTransName);
        dateFrom = requireView().findViewById(R.id.FilterTransFrom);
        dateTo = requireView().findViewById(R.id.FilterTransTo);
        addAutoStake();
        addSpinnerCat();
        addCalendar();

        // onClickListeners
        switchFrom.setOnCheckedChangeListener(this::From_onClick);
        switchTo.setOnCheckedChangeListener(this::To_onClick);
        //Set sp_SubCategory after clicking on category
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String catChosen = spCategory.getSelectedItem().toString();
                addSpinnerSubCat(catChosen);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set navigation
        final NavController navController = Navigation.findNavController(view);
        /// Navigate to Transaction and sent filerSent object  after pressing filter
        Button filterButton = view.findViewById(R.id.btn_filter_FilterTrans);
    /*    filterButton.setOnClickListener((v)-> {
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
        });*/


    }

    @Override
    public Context getContext() { return mainActivity; }

    private void From_onClick(View view, boolean isChecked) {
        dateFrom.setEnabled(isChecked);
    }

    private void To_onClick(View view, boolean isChecked) {
        dateTo.setEnabled(isChecked);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public FilterTransactionsParcel getFilter(){
        String category_string, subcategory_string;

        if (spCategory.getSelectedItemPosition() == 0) category_string = "*";
        else category_string = spCategory.getSelectedItem().toString();

        if (spSubCategory.getSelectedItemPosition() == 0) subcategory_string = "*";
        else subcategory_string = spSubCategory.getSelectedItem().toString();

        String name_text = txtStakeHolder.getText().toString();

        if (!name_text.equals("") && name_text.contains("-") && name_text.contains(" ")) {
            name_text = name_text.split("-")[2];
            name_text = name_text.split(" ")[1];
        }

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

    ///Implementation of CashingObserver

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
    ///Implementation of CashingObserver
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addSpinnerCat() {
        WidgetsCreation.INSTANCE.makeSpinnerCat(mainActivity,spCategory,true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addSpinnerSubCat(String catChosen) {
        WidgetsCreation.INSTANCE.makeSpinnerSubCat(mainActivity,spSubCategory,catChosen,true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addAutoStake() {
        WidgetsCreation.INSTANCE.makeAutoStake(mainActivity,txtStakeHolder,true);

    }

    @Override
    public void addCalendar() {
        WidgetsCreation.INSTANCE.makeCalendarFrom(mainActivity, dateFrom);
        WidgetsCreation.INSTANCE.makeCalendarTo(mainActivity, dateTo);

    }
}


