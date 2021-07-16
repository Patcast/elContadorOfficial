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

import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.AllTransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.adapters.StakeHolderRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.viewModels.ChosenStakeViewModel;


public class AllTransactions extends Fragment  {

    private RecyclerView recyclerAllTransactions;
    private AllTransactionsRecViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.transactions));
        View view = inflater.inflate(R.layout.fragment_all_transactions, container, false);
        recyclerAllTransactions = view.findViewById(R.id.RecViewTransactionsHolder);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AllTransactionsRecViewAdapter(view,getContext());
        recyclerAllTransactions.setAdapter(adapter);
        recyclerAllTransactions.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

}