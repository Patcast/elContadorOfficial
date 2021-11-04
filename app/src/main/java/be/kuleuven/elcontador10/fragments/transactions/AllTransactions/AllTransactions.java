package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;


import java.text.ParseException;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;


public class AllTransactions extends Fragment implements  DatePickerDialog.OnDateSetListener, MainActivity.TopMenuHandler {
    //TODO: update starting balance if transactions are deleted
    private RecyclerView recyclerAllTransactions;
    private TransactionsRecViewAdapter adapter;
    private FloatingActionButton fabNew;
    private TextView txtStartingBalance,txCurrentBalance,txtSumOfReceivables,txtSumOfPayables,ScheduleBalance;
    private Button selectMonth;
    private MainActivity mainActivity;
    private ViewModel_AllTransactions viewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        try {
            viewModel.setInitialData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_transactions, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.displayBottomNavigationMenu(true);
        mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        selectMonth = view.findViewById(R.id.btn_selectMonth);
        txtStartingBalance = view.findViewById(R.id.text_startingBalance);
        txCurrentBalance = view.findViewById(R.id.text_currentBalance);
        txtSumOfReceivables = view.findViewById(R.id.text_receivables);
        txtSumOfPayables = view.findViewById(R.id.text_payables);
        ScheduleBalance = view.findViewById(R.id.text_futureBalance);
        fabNew = view.findViewById(R.id.btn_newFAB);
        viewModel.getCalendarFilter().observe(getViewLifecycleOwner(), i-> {
            try {
                updateDateButtonAndListOfTransactions(i);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        viewModel.getAllChosenTransactions().observe(getViewLifecycleOwner(), i->adapter.setAllTransactions(i));
        viewModel.getMapOfMonthlySummaryValues().observe(getViewLifecycleOwner(), this::updateSummaryUi);
        startRecycler(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectMonth.setOnClickListener(v -> pickDate());
        fabNew.setOnClickListener(this::onFAB_Clicked);

        recyclerAllTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    fabNew.setVisibility(View.INVISIBLE);
                }
                else fabNew.setVisibility(View.VISIBLE);
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        mainActivity.setCurrentMenuClicker(this);
        mainActivity.displayBottomNavigationMenu(true);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_filter,true);
        recyclerAllTransactions.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.displayBottomNavigationMenu(false);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_filter,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSummaryUi(Map<String, Integer> inputsForSummary) {

        NumberFormatter formatter = new NumberFormatter(0);
        String inputSB = "NA";
        if(inputsForSummary.get("startingBalance")!=null){
            formatter.setOriginalNumber(inputsForSummary.get("startingBalance"));
            inputSB = formatter.getFinalNumber();
        }
        txtStartingBalance.setText(inputSB);
        String inputCB = "NA";
        if(inputsForSummary.get("currentBalance")!=null){
            formatter.setOriginalNumber(inputsForSummary.get("currentBalance"));
            inputCB = formatter.getFinalNumber();
        }
        txCurrentBalance.setText(inputCB);
        formatter.setOriginalNumber(inputsForSummary.get("receivables"));
        txtSumOfReceivables.setText(formatter.getFinalNumber());
        formatter.setOriginalNumber(inputsForSummary.get("payables"));
        txtSumOfPayables.setText(formatter.getFinalNumber());
        formatter.setOriginalNumber(inputsForSummary.get("scheduleBalance"));
        ScheduleBalance.setText(formatter.getFinalNumber());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateDateButtonAndListOfTransactions(Map<String, Integer> calendarFilter) throws ParseException {
        String monthSelected = (getResources().getStringArray(R.array.months_list))[calendarFilter.get("month")-1];
        String monthYear = ""+monthSelected+" "+calendarFilter.get("year");
        selectMonth.setText(monthYear);
        viewModel.resetListOfTransactions();
        viewModel.selectScheduleTransactions();
        viewModel.selectListOfProcessedTransactions();

    }


    private void pickDate() {
        int month = viewModel.getCalendarFilter().getValue().get("month");
        int year = viewModel.getCalendarFilter().getValue().get("year");
        MonthYearPickerDialog pd = new MonthYearPickerDialog(month,year);
        pd.setListener(this);
        pd.show(getParentFragmentManager(), "MonthYearPickerDialog");
    }

    private void startRecycler(View view) {
        recyclerAllTransactions = view.findViewById(R.id.RecViewTransactionsHolder);
        recyclerAllTransactions.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new TransactionsRecViewAdapter(view,getContext());
        recyclerAllTransactions.setAdapter(adapter);
    }


    public void onFAB_Clicked(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_allTransactions2_to_newTransaction);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Timestamp latestStartingBalance = viewModel.getFirstStartingBalanceTimeStamp();
        if (latestStartingBalance !=null){
            int lastMonth = latestStartingBalance.toDate().getMonth()+1;
            int lastYear = latestStartingBalance.toDate().getYear()+1900;
            if(year <lastYear || (year == lastYear) && month < lastMonth){
                Toast.makeText(getContext(), "Invalid date! The latest Balance Summary available is for "+(getResources().getStringArray(R.array.months_list))[lastMonth-1]+" / "+lastYear, Toast.LENGTH_LONG).show();
            }
            else {
                Map<String,Integer> chosenDateMap = new HashMap<>();
                chosenDateMap.put("month",month);
                chosenDateMap.put("year",year);
                viewModel.setCalendarFilter(chosenDateMap);
            }
        }
        else {
            Toast.makeText(getContext(), "Error Loading Transactions, please try again.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBottomSheetClick() {
    }

    @Override
    public void onDeleteClick() {

    }

    @Override
    public void onEditingClick() {

    }

    @Override
    public void onAddClick() {

    }

    @Override
    public void onSearchClick(SearchView searchView) {

    }

    @Override
    public void onFilterClick() {
        DialogFilterAllTransactions filterDialog = new DialogFilterAllTransactions(getViewLifecycleOwner());
        filterDialog.show(getParentFragmentManager(),"AccountsBottomSheet");
    }


}