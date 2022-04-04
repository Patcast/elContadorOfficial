package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.installations.Utils;

import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.tools.Exporter;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;


public class AllTransactions extends Fragment implements  DatePickerDialog.OnDateSetListener, MainActivity.TopMenuHandler {
    //TODO: update starting balance if transactions are deleted
    private RecyclerView recyclerAllTransactions;
    private TransactionsRecViewAdapter adapter;
    private FloatingActionButton fabNew, fabExport;
    private TextView txtStartingBalance,txCurrentBalance,txtSumOfReceivables,txtSumOfPayables,ScheduleBalance,txtSumOfCashOut, txtSumOfCashIn;
    private Button selectMonth;
    private MainActivity mainActivity;
    private ViewModel_AllTransactions viewModel;
    NavController navController;

    private String selectedMonth;
    private int selectedYear;
    private long startingBalance, cashIn, cashOut, currentBalance, receivables, payables, scheduleBalance;

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
        mainActivity = (MainActivity) requireActivity();
        mainActivity.displayBottomNavigationMenu(true);
        mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        selectMonth = view.findViewById(R.id.btn_selectMonth);
        txtSumOfCashOut = view.findViewById(R.id.text_sumCashOut);
        txtSumOfCashIn = view.findViewById(R.id.text_sumCashIn);
        txtStartingBalance = view.findViewById(R.id.text_startingBalance);
        txCurrentBalance = view.findViewById(R.id.text_currentBalance);
        txtSumOfReceivables = view.findViewById(R.id.text_receivables);
        txtSumOfPayables = view.findViewById(R.id.text_payables);
        ScheduleBalance = view.findViewById(R.id.text_futureBalance);
        fabNew = view.findViewById(R.id.btn_newFAB);
        fabExport = view.findViewById(R.id.btn_exportFAB);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        selectMonth.setOnClickListener(v -> pickDate());
        fabNew.setOnClickListener(v->navController.navigate(R.id.action_allTransactions2_to_newTransaction));
        //fabExport.setOnClickListener(this::onExport_Clicked);

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
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_export,true);

        recyclerAllTransactions.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.displayBottomNavigationMenu(false);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_filter,false);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_export,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSummaryUi(Map<String, Integer> inputsForSummary) {

        NumberFormatter formatter = new NumberFormatter(0);
        String inputSB = "NA";
        if(inputsForSummary.get("startingBalance")!=null){
            startingBalance = inputsForSummary.get("startingBalance");
            formatter.setOriginalNumber(startingBalance);
            inputSB = formatter.getFinalNumber();
        }
        txtStartingBalance.setText(inputSB);

        String inputCI = "NA";
        if(inputsForSummary.get("cashIn")!=null){
            cashIn = inputsForSummary.get("cashIn");
            formatter.setOriginalNumber(cashIn);
            inputCI = formatter.getFinalNumber();
        }
        txtSumOfCashIn.setText(inputCI);

        String inputCO = "NA";
        if(inputsForSummary.get("cashOut")!=null){
            cashOut = inputsForSummary.get("cashOut");
            formatter.setOriginalNumber(cashOut);
            inputCO = formatter.getFinalNumber();
        }
        txtSumOfCashOut.setText(inputCO);

        String inputCB = "NA";
        if(inputsForSummary.get("currentBalance")!=null){
            currentBalance = inputsForSummary.get("currentBalance");
            formatter.setOriginalNumber(currentBalance);
            inputCB = formatter.getFinalNumber();
        }

        receivables = inputsForSummary.get("receivables");
        payables = inputsForSummary.get("payables");
        scheduleBalance = inputsForSummary.get("scheduleBalance");

        txCurrentBalance.setText(inputCB);
        formatter.setOriginalNumber(receivables);
        txtSumOfReceivables.setText(formatter.getFinalNumber());
        formatter.setOriginalNumber(payables);
        txtSumOfPayables.setText(formatter.getFinalNumber());
        formatter.setOriginalNumber(scheduleBalance);
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

        selectedMonth = monthSelected;
        selectedYear = calendarFilter.get("year");
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onExport_Clicked(View view) {
        HashMap<String, Boolean> filter = new HashMap<>();

        filter.put("transaction",true);
        filter.put("receivable",true);
        filter.put("payable",true);
        filter.put("deletedTrans",false);
        viewModel.setBooleanFilter(filter);

        String message = "Export the current month?\n" + selectedMonth + " " + selectedYear;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Yes", this::export)
                .setNegativeButton("No", (dialogInterface, id) -> {})
                .create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void export(DialogInterface dialogInterface, int id) {
        List<ProcessedTransaction> processed = viewModel.getMonthlyListOfProcessedTransactions();
        List<ScheduledTransaction> scheduled = viewModel.getMonthlyListOfScheduleTransactions();

        File file = Exporter.INSTANCE.createFile(selectedMonth + "_" + selectedYear, processed, scheduled,
                startingBalance, cashIn, cashOut, currentBalance, receivables, payables, scheduleBalance);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.ms-excel");
//        intent.putExtra(Intent.EXTRA_EMAIL, "");
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Financial report for " + selectedMonth + "/" + selectedYear);
//        intent.putExtra(Intent.EXTRA_TEXT, "Brought to you by elContador.");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(intent);
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
    public void addStakeholder() {

    }

    @Override
    public void onSearchClick(MenuItem item) {

    }

    @Override
    public void onFilterClick() {
        DialogFilterAllTransactions filterDialog = new DialogFilterAllTransactions(getViewLifecycleOwner());
        filterDialog.show(getParentFragmentManager(),"AccountsBottomSheet");
    }

    @Override
    public void onToolbarTitleClick() {
        navController.navigate(R.id.action_allTransactions2_to_accountSettings);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onExportClick() {
        onExport_Clicked();
    }


}