package be.kuleuven.elcontador10.fragments.transactions;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.tools.MonthYearPickerDialog;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;


public class AllTransactions extends Fragment implements Caching.AllTransactionsObserver, DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerAllTransactions;
    private TransactionsRecViewAdapter adapter;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    FloatingActionButton fabNewTransaction;
    FloatingActionButton fabPayableOrReceivable;
    FloatingActionButton fabNew;
    TextView textFabNewTransaction;
    TextView textFabReceivable;
    LinearLayout coverLayout;
    ConstraintLayout mainContainer;
    Button selectMonth;

    int yearSelected;
    int monthSelected;
    boolean isClicked;


    MainActivity mainActivity;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation popOpen;
    private Animation popClose;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_transactions, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.displayBottomNavigationMenu(true);
        mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        selectMonth = view.findViewById(R.id.btn_selectMonth);
        mainContainer = view.findViewById(R.id.main_container);
        coverLayout = view.findViewById(R.id.coverLayout);
        textFabNewTransaction = view.findViewById(R.id.text_fabNewTransaction);
        textFabReceivable = view.findViewById(R.id.text_fabReceivable);
        fabNewTransaction = view.findViewById(R.id.btn_new_TransactionFAB);
        fabPayableOrReceivable = view.findViewById(R.id.btn_new_ReceivableOrPayable);
        fabNew = view.findViewById(R.id.btn_newFAB);
        startRecycler(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectMonth.setOnClickListener(v->createMonthPicker());
        coverLayout.setOnClickListener(v->closeCover());
        fabNew.setOnClickListener(v->fabOpenAnimation());
        fabNewTransaction.setOnClickListener(this::onFAB_Clicked);
        fabPayableOrReceivable.setOnClickListener(v -> Toast.makeText(getContext(), "Payables Or Receivables", Toast.LENGTH_SHORT).show());
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

        rotateOpen = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close);
        popOpen= AnimationUtils.loadAnimation(getContext(),R.anim.pop_up_fabs);
        popClose = AnimationUtils.loadAnimation(getContext(),R.anim.pop_down_fabs);
        isClicked= false;
    }

    private void pickDate() {
        MonthYearPickerDialog pd = new MonthYearPickerDialog();
        pd.setListener(this);
        pd.show(getParentFragmentManager(), "MonthYearPickerDialog");
    }

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
            fabPayableOrReceivable.startAnimation(popOpen);
            fabNew.startAnimation(rotateOpen);
        }
        else{
            coverLayout.setVisibility(View.INVISIBLE);
            textFabNewTransaction.startAnimation(popClose);
            textFabReceivable.startAnimation(popClose);
            fabNewTransaction.startAnimation(popClose);
            fabPayableOrReceivable.startAnimation(popClose);
            fabNew.startAnimation(rotateClose);
        }

    }

    private void setVisibility(boolean addButtonClicked) {
        if(!addButtonClicked){
            textFabReceivable.setVisibility(View.VISIBLE);
            textFabNewTransaction.setVisibility(View.VISIBLE);
            fabNewTransaction.setVisibility(View.VISIBLE);
            fabPayableOrReceivable.setVisibility(View.VISIBLE);
        }
        else{
            textFabNewTransaction.setVisibility(View.INVISIBLE);
            textFabReceivable.setVisibility(View.INVISIBLE);
            fabNewTransaction.setVisibility(View.INVISIBLE);
            fabPayableOrReceivable.setVisibility(View.INVISIBLE);
        }

    }

    private void startRecycler(View view) {
        recyclerAllTransactions = view.findViewById(R.id.RecViewTransactionsHolder);
        recyclerAllTransactions.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new TransactionsRecViewAdapter(view,getContext());
        Caching.INSTANCE.attachAllTransactionsObserver(this);
        if(transactionArrayList.size()>0) adapter.setAllTransactions(transactionArrayList);
        recyclerAllTransactions.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        mainActivity.displayBottomNavigationMenu(true);
        Caching.INSTANCE.attachAllTransactionsObserver(this);
        if(transactionArrayList.size()>0) adapter.setAllTransactions(transactionArrayList);
        recyclerAllTransactions.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachAllTransactionsObserver(this);
        mainActivity.displayBottomNavigationMenu(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void notifyAllTransactionsObserver(List<Transaction> allTransactions) {
        transactionArrayList.clear();
        transactionArrayList.addAll(allTransactions);
        adapter.setAllTransactions(transactionArrayList);
    }

    public void onFAB_Clicked(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_allTransactions2_to_newTransaction);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Toast.makeText(getContext(), ""+year+"/"+month, Toast.LENGTH_SHORT).show();
    }

    private void createMonthPicker(){

        Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set(2010, 0, 1); // Set minimum date to show in dialog
        long minDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

        calendar.clear();
        calendar.set(2018, 11, 31); // Set maximum date to show in dialog
        long maxDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

// Create instance with date ranges values
        MonthYearPickerDialogFragment dialogFragment =  MonthYearPickerDialogFragment
                .getInstance(monthSelected, yearSelected, minDate, maxDate);

        dialogFragment.show(getParentFragmentManager(), null);

        dialogFragment.show(getParentFragmentManager(), "MonthYearPicker");

        dialogFragment.setOnDateSetListener((year, monthOfYear) -> {
            Toast.makeText(getContext(), ""+monthOfYear+"/"+year, Toast.LENGTH_SHORT).show();
        });

    }


}