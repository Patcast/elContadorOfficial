package be.kuleuven.elcontador10.fragments.transactions;

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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.AccountsRecViewAdapter;
import be.kuleuven.elcontador10.background.adapters.AllTransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.adapters.StakeHolderRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.viewModels.ChosenStakeViewModel;


public class AllTransactions extends Fragment implements Caching.AllTransactionsObserver {

    private RecyclerView recyclerAllTransactions;
    private AllTransactionsRecViewAdapter adapter;
    ArrayList<Transaction> transactionArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.transactions));
        View view = inflater.inflate(R.layout.fragment_all_transactions, container, false);
        recyclerAllTransactions = view.findViewById(R.id.RecViewTransactionsHolder);
        recyclerAllTransactions.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new AllTransactionsRecViewAdapter(view,getContext());
        Caching.INSTANCE.attachAllTransactionsObserver(this);
        if(transactionArrayList.size()>0) adapter.setAllTransactions(transactionArrayList);
        recyclerAllTransactions.setAdapter(adapter);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Caching.INSTANCE.attachAllTransactionsObserver(this);
        if(transactionArrayList.size()>0) adapter.setAllTransactions(transactionArrayList);
        recyclerAllTransactions.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachAllTransactionsObserver(this);
    }

    @Override
    public void notifyAllTransactionsObserver(List<Transaction> allTransactions) {
        transactionArrayList.clear();
        transactionArrayList.addAll(allTransactions);
        adapter.setAllTransactions(transactionArrayList);

    }
}