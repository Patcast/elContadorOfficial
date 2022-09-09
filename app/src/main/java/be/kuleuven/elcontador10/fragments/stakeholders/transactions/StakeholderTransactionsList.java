package be.kuleuven.elcontador10.fragments.stakeholders.transactions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.fragments.stakeholders.StakeholderViewModel;

public class StakeholderTransactionsList extends Fragment implements Caching.MicroAccountTransactionObserver {
    private RecyclerView recyclerView;
    private LinearLayout coverLayout;
    private TransactionsRecViewAdapter adapter;
    private List<ProcessedTransaction> transactions;

    private StakeholderViewModel viewModel;
    private boolean fabClicked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_micro_account_transactions, container, false);

        transactions = new ArrayList<>();

        recyclerView = view.findViewById(R.id.RecViewTransactionsHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        coverLayout = view.findViewById(R.id.coverLayout);
        coverLayout.setOnClickListener(this::on_Cover_Clicked);

        adapter = new TransactionsRecViewAdapter(view, getContext());
        Caching.INSTANCE.attachMicroTransactionsObserver(this);
        if (transactions.size() > 0) adapter.setAllTransactions(transactions);

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(StakeholderViewModel.class);
/*
        viewModel.getFabClicked().observe(getViewLifecycleOwner(), item -> {
            fabClicked = item;

            if (!fabClicked) coverLayout.setVisibility(View.VISIBLE);
            else coverLayout.setVisibility(View.GONE);
        });
*/
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
    public void notifyMicroAccountTransactionObserver(List<ProcessedTransaction> transactions) {
        this.transactions.clear();
        this.transactions.addAll(transactions);
        adapter.setAllTransactions(this.transactions);
    }

    public void on_Cover_Clicked(View view) {
        if (!fabClicked) coverLayout.setVisibility(View.VISIBLE);
        else coverLayout.setVisibility(View.GONE);

        fabClicked = !fabClicked;

        //viewModel.setFabClicked(fabClicked);
    }
}