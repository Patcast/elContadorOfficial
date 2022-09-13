package be.kuleuven.elcontador10.fragments.stakeholders;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.adapters.TransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.StakeHolder;

public class StakeDetailsList extends Fragment {
    private TransactionsRecViewAdapter recyclerViewAdapter;
    RecyclerView recyclerView;
    StakeholderViewModel viewModel;
    View view;

    List<ProcessedTransaction> transactionList = new ArrayList<>();
    private final StakeHolder selectedStakeHolder;
    private boolean isLoaded =false;
    private final String tabId;

    public StakeDetailsList(StakeHolder selectedStakeHolder, String tabId) {
        this.selectedStakeHolder = selectedStakeHolder;
        this.tabId = tabId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_properties, container, false);
        recyclerView = view.findViewById(R.id.rec_all_properties);
        viewModel = new ViewModelProvider(requireActivity()).get(StakeholderViewModel.class);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewAdapter = new TransactionsRecViewAdapter(view,getContext());
        viewModel.getListOfStakeHolderTrans().observe(getViewLifecycleOwner(), this::updateAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.setAdapter(recyclerViewAdapter);
        viewModel.setSelectedStakeholder(selectedStakeHolder);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadMore() {
        Log.w("Hello","Loading in progress");
        if(!isLoaded){
            Log.w("Hello","Loading in Future");
            recyclerViewAdapter.setAllTransactions(setAdapterFuture());
            isLoaded=true;

        }
        else{
            Log.w("Hello","Removing future");
            recyclerViewAdapter.setAllTransactions(setAdapterNoFuture());
            isLoaded=false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<ProcessedTransaction>  setAdapterNoFuture(){
                 return   transactionList
                            .stream()
                            .filter(t->t.getType().contains(tabId))
                            .filter(t->!t.getType().contains(Caching.INSTANCE.TYPE_PENDING))
                            .collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<ProcessedTransaction>  setAdapterFuture(){
            return  transactionList
                    .stream()
                    .filter(t->t.getType().contains(tabId))
                    .collect(Collectors.toList());

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateAdapter(List<ProcessedTransaction> transactionListFull) {
        transactionList.clear();
        transactionList.addAll(transactionListFull);
        recyclerViewAdapter.setAllTransactions(setAdapterNoFuture());
    }


}