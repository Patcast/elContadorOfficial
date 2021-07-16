package be.kuleuven.elcontador10.fragments.transactions;


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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.RecyclerViewAdapter;
import be.kuleuven.elcontador10.background.database.TransactionsManager;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsSummaryInterface;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;

public class TransactionsSummary extends Fragment implements TransactionsSummaryInterface {
    private RecyclerView recyclerView;
    private TransactionsManager manager;
    private FilterTransactionsParcel filter;
    private NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get argument from TransactionFilter
        try {
            assert getArguments() != null;
          /*  TransactionsSummaryArgs args = TransactionsSummaryArgs.fromBundle(getArguments());
            filter = args.getParcelFilter();*/
        }

        catch (Exception e) {
            filter = new FilterTransactionsParcel("*", "*",
                    "*", null, null);
        }

        ///// Set Navigation for TransactionsSummary buttons
        navController = Navigation.findNavController(view);
    /*    FloatingActionButton fabFilter = view.findViewById(R.id.btn_filter_Transaction);
        fabFilter.setOnClickListener(v -> navController.navigate(R.id.action_transactions_summary_to_transactionsFilter));
        FloatingActionButton fabAdd = view.findViewById(R.id.btn_add_Transaction);
        fabAdd.setOnClickListener(v -> navController.navigate(R.id.action_transactions_summary_to_newTransaction));*/
        ///// End

        recyclerView = getView().findViewById(R.id.TransactionsRecycler);
       /* recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    fabFilter.setVisibility(View.GONE);
                    fabAdd.setVisibility(View.GONE);
                }
                else {
                    fabFilter.setVisibility(View.VISIBLE);
                    fabAdd.setVisibility(View.VISIBLE);
                }
            }
        });*/

        manager = TransactionsManager.getInstance();
        manager.getTransactions(this, filter);
    }



    @Override
    public void populateRecyclerView(ArrayList<String> title, ArrayList<String> description, ArrayList<String> status,
                                     ArrayList<String> metadata, double subtotal) {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(title, description, status, metadata, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        TextView subtotal_text = requireView().findViewById(R.id.lblSubtotal);
        String subtotalString = getString(R.string.subtotal, subtotal);
        subtotal_text.setText(subtotalString);
    }

    @Override
    public void error(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayTransaction(String id) {
    /*    TransactionsSummaryDirections.ActionTransactionsSummaryToTransactionDisplay action =
                TransactionsSummaryDirections.actionTransactionsSummaryToTransactionDisplay(id);
        navController.navigate(action);*/
    }
}