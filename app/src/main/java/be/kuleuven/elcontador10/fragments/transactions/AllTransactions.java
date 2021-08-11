package be.kuleuven.elcontador10.fragments.transactions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    FloatingActionButton fabNewTransaction;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_transactions, container, false);

        fabNewTransaction = view.findViewById(R.id.btn_new_TransactionFAB);
        fabNewTransaction.setOnClickListener(this::onFAB_Clicked);

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

        recyclerAllTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) fabNewTransaction .setVisibility(View.GONE);
                else fabNewTransaction.setVisibility(View.VISIBLE);
            }
        });
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

    public void onFAB_Clicked(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_viewPagerHolder_to_newTransaction2);
    }
}