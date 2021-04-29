package be.kuleuven.elcontador10.fragments.transactions;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalDateTime;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.CategorySpinnerAdapter;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;
import be.kuleuven.elcontador10.interfaces.TransactionsFilterInterface;

public class TransactionsFilter extends Fragment implements TransactionsFilterInterface {
    private MainActivity mainActivity;

    private Spinner category;
    private Spinner subcategory;
    private Switch switchFrom;
    private Switch switchTo;
    private TextView name;
    private TextView dateFrom;
    private TextView dateTo;
    private Button filter;
    private Button cancel;

    private String category_string = "*";
    private String subcategory_string = "*";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_transactions_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        category = getView().findViewById(R.id.FilterTransCategory);
        subcategory = getView().findViewById(R.id.FilterTransSubcategory);
        switchFrom = getView().findViewById(R.id.FilterTransFromSwitch);
        switchTo = getView().findViewById(R.id.FilterTransToSwitch);
        name = getView().findViewById(R.id.FilterTransName);
        dateFrom = getView().findViewById(R.id.FilterTransFrom);
        dateTo = getView().findViewById(R.id.FilterTransTo);
        filter = getView().findViewById(R.id.FilterTransFilter);
        cancel = getView().findViewById(R.id.FilterTransCancel);

        switchFrom.setOnCheckedChangeListener(this::From_onClick);
        switchTo.setOnCheckedChangeListener(this::To_onClick);
        cancel.setOnClickListener(this::Cancel_OnClick);
        filter.setOnClickListener(this::Filter_OnClick);

        mainActivity.hideButtons();

        ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(mainActivity, R.array.category_items, android.R.layout.simple_spinner_item);
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(category_adapter);
        category.setOnItemSelectedListener(new CategorySpinnerAdapter(mainActivity, this));

        subcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) subcategory_string = "*";
                else {
                    subcategory_string =  parent.getItemAtPosition(position).toString();
                    System.out.println(subcategory_string);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                subcategory_string = "*";
            }
        });
    }

    @Override
    public void category_All() {
        category_string = "*";
        subcategory_string = "*";
        subcategory.setAdapter(null);
        subcategory.setVisibility(View.INVISIBLE);
    }

    @Override
    public void category_Rent() {
        category_string = "Rent";
        subcategory.setVisibility(View.VISIBLE);

        ArrayAdapter<CharSequence> subcategory_adapter = ArrayAdapter.createFromResource(mainActivity, R.array.rent_subcategory_items, android.R.layout.simple_spinner_item);
        subcategory.setAdapter(subcategory_adapter);
    }

    @Override
    public void category_Others() {
        category_string = "Others";
        subcategory_string = "*";
        subcategory.setAdapter(null);
        subcategory.setVisibility(View.INVISIBLE);
    }

    @Override
    public void category_Salary() {
        category_string = "Salary";
        subcategory.setVisibility(View.VISIBLE);

        ArrayAdapter<CharSequence> subcategory_adapter = ArrayAdapter.createFromResource(mainActivity, R.array.salary_subcategory_items, android.R.layout.simple_spinner_item);
        subcategory.setAdapter(subcategory_adapter);
    }

    @Override
    public void category_Toilets() {
        category_string = "Toilets";
        subcategory.setVisibility(View.VISIBLE);

        ArrayAdapter<CharSequence> subcategory_adapter = ArrayAdapter.createFromResource(mainActivity, R.array.toilets_subcategory_items, android.R.layout.simple_spinner_item);
        subcategory.setAdapter(subcategory_adapter);
    }

    @Override
    public void category_Purchases() {
        category_string = "Purchases";
        subcategory.setVisibility(View.VISIBLE);

        ArrayAdapter<CharSequence> subcategory_adapter = ArrayAdapter.createFromResource(mainActivity, R.array.purchases_subcategory_items, android.R.layout.simple_spinner_item);
        subcategory.setAdapter(subcategory_adapter);
    }

    @Override
    public void category_Deposits() {
        category_string = "Deposits";
        subcategory.setVisibility(View.VISIBLE);

        ArrayAdapter<CharSequence> subcategory_adapter = ArrayAdapter.createFromResource(mainActivity, R.array.deposits_subcategory_items, android.R.layout.simple_spinner_item);
        subcategory.setAdapter(subcategory_adapter);
    }

    private void From_onClick(View view, boolean isChecked) {
        if (isChecked) dateFrom.setEnabled(true);
        else dateFrom.setEnabled(false);
    }

    private void To_onClick(View view, boolean isChecked) {
        if (isChecked) dateTo.setEnabled(true);
        else dateTo.setEnabled(false);
    }

    public void Cancel_OnClick(View view) {
        FilterTransactionsParcel filter = new FilterTransactionsParcel("*", "*", "*", null, null);
        mainActivity.setSelectedFragment(new Transactions(filter), "Transactions");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Filter_OnClick(View view) {
        String name_text = name.getText().toString();
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

        FilterTransactionsParcel filter = new FilterTransactionsParcel(category_string, subcategory_string, name_text, from, to);
        mainActivity.setSelectedFragment(new Transactions(filter), "Transactions");
    }
}