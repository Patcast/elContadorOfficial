package be.kuleuven.elcontador10.fragments.transactions;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;


public class AllTransactions extends Fragment implements Caching.AllTransactionsObserver {

    private RecyclerView recyclerAllTransactions;
    private TransactionsRecViewAdapter adapter;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();
    FloatingActionButton fabNewTransaction;
    MainActivity mainActivity;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_transactions, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.displayBottomNavigationMenu(true);
        mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        fabNewTransaction = view.findViewById(R.id.btn_new_TransactionFAB);
        startRecycler(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabNewTransaction.setOnClickListener(this::onFAB_Clicked);
        recyclerAllTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) fabNewTransaction .setVisibility(View.GONE);
                else fabNewTransaction.setVisibility(View.VISIBLE);
            }
        });
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
}