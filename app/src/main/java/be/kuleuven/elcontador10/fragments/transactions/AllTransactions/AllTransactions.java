package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.MonthlyRecords;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.tools.Exporter;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;

public class AllTransactions extends Fragment implements DatePickerDialog.OnDateSetListener, MainActivity.FABImplement {

    private RecyclerView recyclerView;
    private TransactionsRecViewAdapter recyclerViewAdapter;

    private TextView txtStartingCash, txCurrentCash,txtSumOfReceivables,txtSumOfPayables, txtEquity,txtSumOfCashOut, txtSumOfCashIn;
    private Button selectMonth;
    private MainActivity mainActivity;
    private ViewModel_AllTransactions viewModelAllTransactions;
    NavController navController;
    private  MonthlyRecords currentMonthlyRecord;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelAllTransactions = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        try {
            viewModelAllTransactions.setInitialData();
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
        txtSumOfCashOut.setSelected(true);
        txtSumOfCashIn = view.findViewById(R.id.text_sumCashIn);
        txtSumOfCashIn.setSelected(true);
        txtStartingCash = view.findViewById(R.id.text_startingBalance);
        txtStartingCash.setSelected(true);
        txCurrentCash = view.findViewById(R.id.text_currentBalance);
        txCurrentCash.setSelected(true);
        txtSumOfReceivables = view.findViewById(R.id.text_receivables);
        txtSumOfReceivables.setSelected(true);
        txtSumOfPayables = view.findViewById(R.id.text_payables);
        txtSumOfPayables.setSelected(true);
        txtEquity = view.findViewById(R.id.text_futureBalance);
        txtEquity.setSelected(true);



        startRecycler(view);

        return view;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        selectMonth.setOnClickListener(v -> pickDate());
        viewModelAllTransactions.getAllChosenTransactions().observe(getViewLifecycleOwner(), i-> recyclerViewAdapter.setAllTransactions(i));
        viewModelAllTransactions.getSelectedMonthlyRecord().observe(getViewLifecycleOwner(), this::updateMonthlyRecordUi);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mainActivity.setFABVisibility(newState != RecyclerView.SCROLL_STATE_DRAGGING);
            }
        });
        setTopMenu();
    }

    private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            final int menu_filter = R.id.menu_filter, menu_export = R.id.menu_export, menu_settings = R.id.menu_settings;

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                menu.findItem(menu_filter).setVisible(true);
                menu.findItem(menu_export).setVisible(true);
                menu.findItem(menu_settings).setVisible(true);
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case menu_filter:
                        onFilterClick();
                        return true;
                    case menu_settings:
                        navController.navigate(R.id.action_allTransactions2_to_accountSettings);
                        return true;
                    case menu_export:
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
        recyclerView.setAdapter(recyclerViewAdapter);
        mainActivity.setFabImplement(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.resetFAB();
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.displayBottomNavigationMenu(false);
        mainActivity.setFabImplement(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateMonthlyRecordUi(MonthlyRecords inputsForSummary) {
        if (inputsForSummary!=null ){
            currentMonthlyRecord =inputsForSummary;
            NumberFormatter formatter = new NumberFormatter(0);
            formatter.setOriginalNumber(inputsForSummary.getStartingCash());
            txtStartingCash.setText(formatter.getFinalNumber());
            formatter.setOriginalNumber(inputsForSummary.getCash());
            txCurrentCash.setText(formatter.getFinalNumber());
            formatter.setOriginalNumber(inputsForSummary.getSumOfReceivables());
            txtSumOfReceivables.setText(formatter.getFinalNumber());
            formatter.setOriginalNumber(inputsForSummary.getSumOfPayables());
            txtSumOfPayables.setText(formatter.getFinalNumber());
            formatter.setOriginalNumber(inputsForSummary.getEquity());
            txtEquity.setText(formatter.getFinalNumber());
            formatter.setOriginalNumber(inputsForSummary.getCashIn());
            txtSumOfCashIn.setText(formatter.getFinalNumber());
            formatter.setOriginalNumber(inputsForSummary.getCashOut());
            txtSumOfCashOut.setText(formatter.getFinalNumber());
            Calendar cal = inputsForSummary.getDate();
            String currentDate = ""+(getResources().getStringArray(R.array.months_list))[cal.get(Calendar.MONTH)]+" "+cal.get(Calendar.YEAR);
            selectMonth.setText(currentDate);
        }
        else{
            String NAString = "NA";
            txtStartingCash.setText(NAString);
            txtSumOfCashIn.setText(NAString);
            txtSumOfCashOut.setText(NAString);
            txCurrentCash.setText(NAString);
            txtSumOfReceivables.setText(NAString);
            txtSumOfPayables.setText(NAString);
            txtEquity.setText(NAString);
        }
    }




    private void pickDate() {
        if(currentMonthlyRecord!=null){
            MonthYearPickerDialog pd = new MonthYearPickerDialog(currentMonthlyRecord.getDate().get(Calendar.MONTH),currentMonthlyRecord.getDate().get(Calendar.YEAR),viewModelAllTransactions.listOfMonthlyRecords);
            pd.setListener(this);
            pd.show(getParentFragmentManager(), "MonthYearPickerDialog");
        }
    }

    private void startRecycler(View view) {
        recyclerView = view.findViewById(R.id.RecViewTransactionsHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewAdapter = new TransactionsRecViewAdapter(view,getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        if(viewModelAllTransactions.findMonthlyRecord(month,year)!=null){
            try {
                viewModelAllTransactions.requestListOfProcessedTransactions(viewModelAllTransactions.findMonthlyRecord(month,year));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else Toast.makeText(mainActivity, "The month is not existing", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onExport_Clicked() {
        String message = getString(R.string.export_prompt, ""+(currentMonthlyRecord.getDate().get(Calendar.MONTH)+1), (currentMonthlyRecord.getDate().get(Calendar.YEAR)));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(R.string.yes, this::saveFileLocation)
                .setNegativeButton(R.string.no, (dialogInterface, id) -> dialogInterface.dismiss())
                .create()
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveFileLocation(DialogInterface dialogInterface, int id) {
        dialogInterface.dismiss();

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.ms-excel");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
        intent.putExtra(Intent.EXTRA_TITLE, (currentMonthlyRecord.getDate().get(Calendar.MONTH)+1) + "_" + (currentMonthlyRecord.getDate().get(Calendar.YEAR)) + ".xls");
        saveIntentLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> saveIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onActivityResult
    );

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void export(Uri uri) {
        List<ProcessedTransaction> processed = viewModelAllTransactions.getMonthlyListOfProcessedTransactions().getValue();

        File file = Exporter.INSTANCE.createFile(mainActivity, uri,
                (currentMonthlyRecord.getDate().get(Calendar.MONTH)+1)  + " " + (currentMonthlyRecord.getDate().get(Calendar.YEAR)), processed, currentMonthlyRecord.getStartingCash(),
                currentMonthlyRecord.getCashIn(), currentMonthlyRecord.getCashOut(), currentMonthlyRecord.getCash(), currentMonthlyRecord.getSumOfReceivables(), currentMonthlyRecord.getSumOfPayables(), currentMonthlyRecord.getEquity());

        if (file == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setMessage(R.string.failed_to_create_file)
                    .setPositiveButton(R.string.ok, ((dialogInterface, i) -> dialogInterface.dismiss()))
                    .create()
                    .show();
            return;
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(R.string.file_ready)
                .setPositiveButton(R.string.send, (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("application/vnd.ms-excel");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.open, (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "*/*");
                    startActivity(intent);
                })
                .setNeutralButton(R.string.cancel,
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }


    public void onFilterClick() {
        DialogFilterAllTransactions filterDialog = new DialogFilterAllTransactions(getViewLifecycleOwner());
        filterDialog.show(getParentFragmentManager(),"AccountsBottomSheet");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    public void onExportClick() {
        onExport_Clicked();
    }


    private void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK &&
                result.getData() != null) {
            Uri uri = result.getData().getData();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                export(uri);
            }
        }
    }

    @Override
    public void onTransactionNewClicked() {
        navController.navigate(R.id.action_allTransactions2_to_newTransaction);
    }

    @Override
    public void onScheduledTransactionNewClicked() {
        navController.navigate(R.id.action_allTransactions2_to_contractNewPayment);
    }
}