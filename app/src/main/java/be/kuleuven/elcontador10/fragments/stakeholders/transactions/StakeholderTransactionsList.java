package be.kuleuven.elcontador10.fragments.stakeholders.transactions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Transaction;

public class StakeholderTransactionsList extends Fragment implements Caching.MicroAccountTransactionObserver {
    private RecyclerView recyclerView;
    private TransactionsRecViewAdapter adapter;
    private ArrayList<Transaction> transactions;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_micro_account_transactions, container, false);

        transactions = new ArrayList<>();

        fab = view.findViewById(R.id.btn_new_TransactionFAB);
        fab.setOnClickListener(this::onFAB_Clicked);

        recyclerView = view.findViewById(R.id.RecViewTransactionsHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new TransactionsRecViewAdapter(view, getContext());
        Caching.INSTANCE.attachMicroTransactionsObserver(this);
        if (transactions.size() > 0) adapter.setAllTransactions(transactions);

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) fab .setVisibility(View.GONE);
                else fab.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Caching.INSTANCE.attachMicroTransactionsObserver(this);
        if (transactions.size()>0) adapter.setAllTransactions(transactions);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachMicroTransactionsObserver(this);
        transactions.clear();
    }

    @Override
    public void notifyMicroAccountTransactionObserver(List<Transaction> transactions) {
        this.transactions.clear();
        this.transactions.addAll(transactions);
        adapter.setAllTransactions(this.transactions);
    }

    public void onFAB_Clicked(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_microAccountViewPagerHolder_to_newTransaction);
    }
}