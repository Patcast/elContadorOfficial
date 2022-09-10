package be.kuleuven.elcontador10.fragments.stakeholders;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    List<ProcessedTransaction> transactionListDisplayed = new ArrayList<>();
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

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                Log.w("Details", "On scroll");
                if (!isLoading) {
                    Log.w("Details", "On scroll is not loading");

                    if (linearLayoutManager != null && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        Log.w("Details", "On scroll is TOP");
                        loadMore();
                        isLoading = true;
                    }
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

               /* LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                Log.w("Details", "On scroll");
                if (!isLoading) {
                    Log.w("Details", "On scroll is not loading");

                    if (linearLayoutManager != null && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        Log.w("Details", "On scroll is TOP");
                        loadMore();
                        isLoading = true;
                    }
                }*/
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        isLoading=false;
    }

    private void loadMore() {
        if(transactionListDisplayed.size()<transactionList.size()+transactionListFuture.size()){
            Log.w("Details", "LLLLLLLoading");
            transactionListDisplayed.add(0,null);
            recyclerViewAdapter.notifyItemInserted(0);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                transactionListDisplayed.remove(0);
                recyclerViewAdapter.notifyItemRemoved(0);
                transactionListDisplayed.addAll(0,transactionListFuture);
                recyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }, 2000);
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
        transactionListDisplayed.clear();
        prepareListsForAdapter(i);
        transactionListDisplayed.addAll(transactionList);
        recyclerViewAdapter.setAllTransactions(transactionListDisplayed);

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