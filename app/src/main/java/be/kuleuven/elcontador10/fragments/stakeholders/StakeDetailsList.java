package be.kuleuven.elcontador10.fragments.stakeholders;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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

public class StakeDetailsList extends Fragment {
    private TransactionsRecViewAdapter adapter;
    StakeholderViewModel viewModel;
    View view;
    private String tabId;
    List<ProcessedTransaction> transactionList = new ArrayList<>();

    public StakeDetailsList(String tabId){
        this.tabId=tabId;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_properties, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rec_all_properties);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(StakeholderViewModel.class);
        adapter = new TransactionsRecViewAdapter(view,getContext());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getListOfStakeHolderTrans().observe(getViewLifecycleOwner(), i->updateAdapter(i));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateAdapter(List<ProcessedTransaction> i) {
        transactionList.clear();
        transactionList.addAll(i);
        if(tabId.equals(Caching.INSTANCE.TYPE_CASH)){
            adapter.setAllTransactions(
                    transactionList.stream().filter(t->t.getType().contains(tabId)).collect(Collectors.toList())
            );
        }
        else{
            adapter.setAllTransactions(
                    transactionList.stream().filter(t->!t.getType().contains(Caching.INSTANCE.TYPE_CASH)).filter(t->t.getType().contains(tabId)).collect(Collectors.toList())
            );
        }

    }


}