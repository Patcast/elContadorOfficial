package be.kuleuven.elcontador10.fragments.transactions;

import android.content.Context;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.RecyclerViewAdapter;
import be.kuleuven.elcontador10.background.database.TransactionsManager;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;
import be.kuleuven.elcontador10.background.interfaces.TransactionsInterface;

public class Transactions extends Fragment implements TransactionsInterface {
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    private TransactionsManager manager;
    private FilterTransactionsParcel filter;
    private NavController navController;

/*
    public Transactions(FilterTransactionsParcel filter) {
        this.filter = filter;
    }

    public Transactions(){
        FilterTransactionsParcel filterNew = new FilterTransactionsParcel("*", "*",
                "*", null, null);
        this.filter = filterNew;
    }

 */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("Transactions");
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get argument from TransactionFilter
        try {
            assert getArguments() != null;
            TransactionsArgs args = TransactionsArgs.fromBundle(getArguments());
            filter = args.getParcelFilter();
        }

        catch (Exception e) {
            filter = new FilterTransactionsParcel("*", "*",
                    "*", null, null);
        }

        ///// Set Navigation for Transactions buttons
        navController = Navigation.findNavController(view);
        FloatingActionButton fabAdd = view.findViewById(R.id.btn_filter_Transaction);
        fabAdd.setOnClickListener(v -> navController.navigate(R.id.action_transactions_summary_to_transactionsFilter));
        FloatingActionButton fabSettings = view.findViewById(R.id.btn_add_Transaction);
        fabSettings.setOnClickListener(v -> navController.navigate(R.id.action_transactions_summary_to_newTransaction));
        ///// End

        recyclerView = getView().findViewById(R.id.TransactionsRecycler);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

               // if (newState == RecyclerView.SCROLL_STATE_DRAGGING) mainActivity.hideButtons();
               // else mainActivity.viewButtons();
            }
        });

        manager = TransactionsManager.getInstance();
        manager.getTransactions(this, filter);
    }

    @Override
    public Context getContext() {
        return mainActivity;
    }

    @Override
    public void populateRecyclerView(ArrayList<String> title, ArrayList<String> description, ArrayList<String> status, ArrayList<String> metadata) {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(title, description, status, metadata, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    @Override
    public void error(String error) {
        Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayTransaction(String id) {
        TransactionsDirections.ActionTransactionsSummaryToTransactionDisplay action =
                TransactionsDirections.actionTransactionsSummaryToTransactionDisplay(id);
        navController.navigate(action);
    }
}