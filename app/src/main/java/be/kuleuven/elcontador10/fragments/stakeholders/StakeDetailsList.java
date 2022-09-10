package be.kuleuven.elcontador10.fragments.stakeholders;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
    private TransactionsRecViewAdapter recyclerViewAdapter;
    RecyclerView recyclerView;
    StakeholderViewModel viewModel;
    View view;
    private String tabId;
    List<ProcessedTransaction> transactionList = new ArrayList<>();
    List<ProcessedTransaction> transactionListFuture = new ArrayList<>();
    List<ProcessedTransaction> transactionListFull = new ArrayList<>();
    boolean isLoading = false;

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
        recyclerView = view.findViewById(R.id.rec_all_properties);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(StakeholderViewModel.class);
        recyclerViewAdapter = new TransactionsRecViewAdapter(view,getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        initScrollListener();
        return view;
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    //if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == transactionList.size() - 1) {
                    if (linearLayoutManager != null && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }
    private void loadMore() {
        if(transactionListFuture.size()>0){
            transactionList.add(0,null);
            recyclerViewAdapter.notifyItemInserted(0);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                transactionList.remove(0);
                recyclerViewAdapter.notifyItemRemoved(0);
                transactionList.addAll(0,transactionListFuture);
                transactionListFuture.clear();
                recyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }, 1000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getListOfStakeHolderTrans().observe(getViewLifecycleOwner(), i->updateAdapter(i));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateAdapter(List<ProcessedTransaction> i) {
        transactionListFull.clear();
        transactionListFull.addAll(i);
        prepareListsForAdapter(transactionListFull);
        recyclerViewAdapter.setAllTransactions(transactionList);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void prepareListsForAdapter(List<ProcessedTransaction> transactionListFull) {
        transactionList.clear();
        transactionListFuture.clear();
        if(tabId.equals(Caching.INSTANCE.TYPE_CASH)){

            transactionList.addAll(
                    transactionListFull
                            .stream()
                            .filter(t->t.getType().contains(tabId))
                            .filter(t->!t.getType().contains(Caching.INSTANCE.TYPE_PENDING))
                            .collect(Collectors.toList())
            );
            transactionListFuture.addAll(
                    transactionListFull
                            .stream()
                            .filter(t->t.getType().contains(tabId))
                            .filter(t->t.getType().contains(Caching.INSTANCE.TYPE_PENDING))
                            .collect(Collectors.toList())
            );
        }
        else{

            transactionList.addAll(
                    transactionListFull
                            .stream()
                            .filter(t->!t.getType().contains(Caching.INSTANCE.TYPE_CASH))
                            .filter(t->t.getType().contains(tabId))
                            .filter(t->!t.getType().contains(Caching.INSTANCE.TYPE_PENDING))
                            .collect(Collectors.toList())
            );
            transactionListFuture.addAll(
                    transactionListFull
                            .stream()
                            .filter(t->!t.getType().contains(Caching.INSTANCE.TYPE_CASH))
                            .filter(t->t.getType().contains(tabId))
                            .filter(t->t.getType().contains(Caching.INSTANCE.TYPE_PENDING))
                            .collect(Collectors.toList())
            );
        }
    }


}