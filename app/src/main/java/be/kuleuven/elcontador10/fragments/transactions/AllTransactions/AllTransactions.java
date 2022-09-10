package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.tools.Exporter;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.fragments.property.PropertyListViewModel;


public class AllTransactions extends Fragment implements  DatePickerDialog.OnDateSetListener, MainActivity.TopMenuHandler {

    private RecyclerView recyclerAllTransactions;
    private TransactionsRecViewAdapter adapter;
    private FloatingActionButton fabNewTransaction, fabNewFutureTransaction,fabNew;
    private TextView textFabNewTransaction,textFabReceivable;
    private Animation rotateOpen,rotateClose,popOpen,popClose;
    private LinearLayout coverLayout;


    private TextView txtStartingCash, txCurrentCash,txtSumOfReceivables,txtSumOfPayables, txtEquity,txtSumOfCashOut, txtSumOfCashIn;
    private Button selectMonth;
    private MainActivity mainActivity;
    private ViewModel_AllTransactions viewModel;
    NavController navController;

    private String selectedMonth;
    private int selectedYear;
    private long cashAtStart, cashIn, cashOut, cashAtEnd, receivables, payables, equity;
    private boolean isClicked;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        new ViewModelProvider(requireActivity()).get(PropertyListViewModel.class);  // query properties
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

        mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        selectMonth = view.findViewById(R.id.btn_selectMonth);
        txtSumOfCashOut = view.findViewById(R.id.text_sumCashOut);
        txtSumOfCashIn = view.findViewById(R.id.text_sumCashIn);
        txtStartingCash = view.findViewById(R.id.text_startingBalance);
        txCurrentCash = view.findViewById(R.id.text_currentBalance);
        txtSumOfReceivables = view.findViewById(R.id.text_receivables);
        txtSumOfPayables = view.findViewById(R.id.text_payables);
        txtEquity = view.findViewById(R.id.text_futureBalance);
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


        textFabNewTransaction = view.findViewById(R.id.text_fabNewTransaction);
        textFabReceivable = view.findViewById(R.id.text_fabReceivable);
        fabNewTransaction = view.findViewById(R.id.btn_new_TransactionFAB);
        fabNewFutureTransaction = view.findViewById(R.id.btn_new_ReceivableOrPayable);
        fabNew = view.findViewById(R.id.btn_newFAB);
        coverLayout = view.findViewById(R.id.coverLayout);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        selectMonth.setOnClickListener(v -> pickDate());
        //fabNew.setOnClickListener(v->navController.navigate(R.id.action_allTransactions2_to_newTransaction));

        coverLayout.setOnClickListener(v->closeCover());
        fabNew.setOnClickListener(v->fabOpenAnimation());
        fabNewTransaction.setOnClickListener(v->navController.navigate(R.id.action_allTransactions2_to_newTransaction));
        fabNewFutureTransaction.setOnClickListener(v -> navController.navigate(R.id.action_allTransactions2_to_contractNewPayment));
        rotateOpen = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close);
        popOpen= AnimationUtils.loadAnimation(getContext(),R.anim.pop_up_fabs);
        popClose = AnimationUtils.loadAnimation(getContext(),R.anim.pop_down_fabs);
        isClicked= false;

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
        setTopMenu();
    }

    private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                menu.findItem(R.id.menu_filter).setVisible(true);
                menu.findItem(R.id.menu_export).setVisible(true);
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_filter:
                        onFilterClick();
                        return true;
                    case R.id.menu_export:
                        onExportClick();
                        return true;
                    default:
                        return false;
                }
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        mainActivity.displayBottomNavigationMenu(true);
        mainActivity.setCurrentMenuClicker(this);
        recyclerAllTransactions.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.setCurrentMenuClicker(null);
        mainActivity.displayBottomNavigationMenu(false);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSummaryUi(Map<String, Integer> inputsForSummary) {

        NumberFormatter formatter = new NumberFormatter(0);
        String inputSB = "NA";
        if(inputsForSummary.get("startingBalance")!=null){
            cashAtStart = inputsForSummary.get("startingBalance");
            formatter.setOriginalNumber(cashAtStart);
            inputSB = formatter.getFinalNumber();
        }
        txtStartingCash.setText(inputSB);

        String inputCI = "NA";
        if(inputsForSummary.get("cashIn")!=null){
            cashIn = inputsForSummary.get("cashIn");
            formatter.setOriginalNumber(cashIn);
            inputCI = formatter.getFinalNumber();
        }
        txtSumOfCashIn.setText(inputCI);

        String inputCO = "NA";
        if(inputsForSummary.get("cashOut")!=null){
            cashOut =(-inputsForSummary.get("cashOut")) ;
            formatter.setOriginalNumber(cashOut);
            inputCO = formatter.getFinalNumber();
        }
        txtSumOfCashOut.setText(inputCO);




        String inputCB = "NA";
        if(inputsForSummary.get("currentBalance")!=null){
            cashAtEnd = inputsForSummary.get("currentBalance");
            formatter.setOriginalNumber(cashAtEnd);
            inputCB = formatter.getFinalNumber();
        }

        receivables = inputsForSummary.get("receivables");
        payables = inputsForSummary.get("payables");
        equity = inputsForSummary.get("scheduleBalance");

        txCurrentCash.setText(inputCB);
        formatter.setOriginalNumber(inputsForSummary.get("receivables"));
        txtSumOfReceivables.setText(formatter.getFinalNumber());
        formatter.setOriginalNumber(inputsForSummary.get("payables"));
        txtSumOfPayables.setText(formatter.getFinalNumber());
        formatter.setOriginalNumber(inputsForSummary.get("scheduleBalance"));
        txtEquity.setText(formatter.getFinalNumber());
    }

/*    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onExport_Clicked(View view) {
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
    }*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateDateButtonAndListOfTransactions(Map<String, Integer> calendarFilter) throws ParseException {
        String monthSelected = (getResources().getStringArray(R.array.months_list))[calendarFilter.get("month")-1];
        String monthYear = ""+monthSelected+" "+calendarFilter.get("year");
        selectMonth.setText(monthYear);
        viewModel.resetListOfTransactions();
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
    public void onExport_Clicked() {
        String message = getString(R.string.export_prompt, selectedMonth, selectedYear);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(R.string.yes, this::export)
                .setNegativeButton(R.string.no, (dialogInterface, id) -> dialogInterface.dismiss())
                .create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void export(DialogInterface dialogInterface, int id) {
        dialogInterface.dismiss();

        List<ProcessedTransaction> processed;
        processed = viewModel.getMonthlyListOfProcessedTransactions();

        File file = Exporter.INSTANCE.createFile(selectedMonth + "_" + selectedYear, processed,
                cashAtStart, cashIn, cashOut, cashAtEnd, receivables, payables, equity, this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(R.string.file_ready)
                .setPositiveButton(R.string.send, (dialogInterface1, i) -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("application/vnd.ms-excel");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    startActivity(intent);
                })
                .setNegativeButton(R.string.save, (dialogInterface1, i) -> {
                    // TODO: local save
                })
                .setNeutralButton(R.string.cancel, (dialogInterface1, i) -> dialogInterface1.dismiss())
                .create().show();
    }

    ///******  ANIMATIONS METHODS


    public void closeCover() {
        if(isClicked){
            setAnimation(true);
            setVisibility(true);
            isClicked = false;
        }
    }

    private void fabOpenAnimation() {
        setVisibility(isClicked);
        setAnimation(isClicked);
        isClicked = !isClicked;
    }

    private void setAnimation(boolean addButtonClicked) {
        if(!addButtonClicked){
            coverLayout.setVisibility(View.VISIBLE);
            textFabNewTransaction.startAnimation(popOpen);
            textFabReceivable.startAnimation(popOpen);
            fabNewTransaction.startAnimation(popOpen);
            fabNewFutureTransaction.startAnimation(popOpen);
            fabNew.startAnimation(rotateOpen);
        }
        else{
            coverLayout.setVisibility(View.INVISIBLE);
            textFabNewTransaction.startAnimation(popClose);
            textFabReceivable.startAnimation(popClose);
            fabNewTransaction.startAnimation(popClose);
            fabNewFutureTransaction.startAnimation(popClose);
            fabNew.startAnimation(rotateClose);
        }

    }

    private void setVisibility(boolean addButtonClicked) {
        if(!addButtonClicked){
            textFabReceivable.setVisibility(View.VISIBLE);
            textFabNewTransaction.setVisibility(View.VISIBLE);
            fabNewTransaction.setVisibility(View.VISIBLE);
            fabNewFutureTransaction.setVisibility(View.VISIBLE);
        }
        else{
            textFabNewTransaction.setVisibility(View.INVISIBLE);
            textFabReceivable.setVisibility(View.INVISIBLE);
            fabNewTransaction.setVisibility(View.INVISIBLE);
            fabNewFutureTransaction.setVisibility(View.INVISIBLE);
        }

    }

    ///////*******


    public void onFilterClick() {
        DialogFilterAllTransactions filterDialog = new DialogFilterAllTransactions(getViewLifecycleOwner());
        filterDialog.show(getParentFragmentManager(),"AccountsBottomSheet");
    }

    @Override
    public void onToolbarTitleClick() {
        navController.navigate(R.id.action_allTransactions2_to_accountSettings);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    public void onExportClick() {
        onExport_Clicked();
    }


}