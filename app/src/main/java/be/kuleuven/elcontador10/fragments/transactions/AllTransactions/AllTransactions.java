package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;


import java.text.ParseException;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.tools.MonthYearPickerDialog;


public class AllTransactions extends Fragment implements  DatePickerDialog.OnDateSetListener, MainActivity.TopMenuHandler {

    private RecyclerView recyclerAllTransactions;
    private TransactionsRecViewAdapter adapter;
    private FloatingActionButton fabNewTransaction,fabPayableOrReceivable,fabNew;
    private TextView textFabNewTransaction,textFabReceivable;
    private LinearLayout coverLayout;
    private Button selectMonth;
    boolean isClicked;
    private MainActivity mainActivity;
    private Animation rotateOpen,rotateClose,popOpen,popClose;
    private ViewModel_AllTransactions viewModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_transactions, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.displayBottomNavigationMenu(true);
        mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        selectMonth = view.findViewById(R.id.btn_selectMonth);
        coverLayout = view.findViewById(R.id.coverLayout);
        textFabNewTransaction = view.findViewById(R.id.text_fabNewTransaction);
        textFabReceivable = view.findViewById(R.id.text_fabReceivable);
        fabNewTransaction = view.findViewById(R.id.btn_new_TransactionFAB);
        fabPayableOrReceivable = view.findViewById(R.id.btn_new_ReceivableOrPayable);
        fabNew = view.findViewById(R.id.btn_newFAB);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        viewModel.setTypesOfTransactions(makeMapOfTransTypes());
        viewModel.getChosenTypesOfTransactions().observe(getViewLifecycleOwner(), i ->updateTransactionTypesDisplayed());
        viewModel.getMonthlyListOfTransactions().observe(getViewLifecycleOwner(),i->updateListOfTransactions(i));
        startRecycler(view);
        return view;
    }

    private void updateListOfTransactions(List<Transaction> listUpdated) {
        adapter.setAllTransactions(listUpdated);
    }


    private HashMap<String, Boolean> makeMapOfTransTypes() {
        HashMap<String, Boolean> transTypes = new HashMap<>();
        transTypes.put("transaction",true);
        transTypes.put("receivable",false);
        transTypes.put("payable",false);
        return transTypes;
    }
    private void updateTransactionTypesDisplayed() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectMonth.setOnClickListener(v -> pickDate());
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
        try {
            Calendar cal = Calendar.getInstance();
            viewModel.selectMonthlyList(cal.get(Calendar.MONTH)+1,cal.get(Calendar.YEAR));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
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
        recyclerAllTransactions.setAdapter(adapter);
    }


    public void onFAB_Clicked(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_allTransactions2_to_newTransaction);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        try {
            viewModel.selectMonthlyList(month,year);
            String monthSelected = (getResources().getStringArray(R.array.months_list))[month-1];
            String monthYear = ""+monthSelected+" "+year;
            selectMonth.setText(monthYear);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "error with dates", Toast.LENGTH_SHORT).show();
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