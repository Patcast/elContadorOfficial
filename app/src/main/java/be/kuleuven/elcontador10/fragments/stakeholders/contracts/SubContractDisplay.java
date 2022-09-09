package be.kuleuven.elcontador10.fragments.stakeholders.contracts;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ScheduledTransactionsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.model.contract.SubContract;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;

public class SubContractDisplay extends Fragment implements Caching.SubContractObserver {

    //views
    private TextView amount, period, title;
    private RecyclerView recyclerView;

    // variables
    private MainActivity mainActivity;
    private ArrayList<ScheduledTransaction> scheduledTransactions;
    private SubContract subContract;
    private String idSubcontract;
    ScheduledTransactionsRecViewAdapter adapter;
    private SubContractViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_contract_subcontract, container, false);

        // set variables
        mainActivity = (MainActivity) requireActivity();
        //mainActivity.modifyVisibilityOfMenuItem(R.id.menu_filter, true);
       // mainActivity.setCurrentMenuClicker(this);

        scheduledTransactions = new ArrayList<>();
        adapter = new ScheduledTransactionsRecViewAdapter(view, getContext());
        viewModel = new ViewModelProvider(mainActivity).get(SubContractViewModel.class);

        // set views
        amount = view.findViewById(R.id.subcontract_amount);
        period = view.findViewById(R.id.subcontract_period);
        title = view.findViewById(R.id.subcontract_title);

        recyclerView = view.findViewById(R.id.scheduledTransactions_rec_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get arguments
        idSubcontract = SubContractDisplayArgs.fromBundle(getArguments()).getSubcontractId();

        Caching.INSTANCE.getSubContract(idSubcontract);
        Caching.INSTANCE.attachSubcontractObserver(this);

        viewModel.getFiltered().observe(getViewLifecycleOwner(), this::changeData);
    }

    public void changeData(ArrayList<ScheduledTransaction> filteredTransactions) {
        adapter.setScheduledTransactions(filteredTransactions);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void notify(SubContract contract, List<ScheduledTransaction> scheduledTransactionList) {
        subContract = contract;
        StakeHolder stakeHolder = Caching.INSTANCE.getChosenStakeHolder();
        mainActivity.setHeaderText(stakeHolder.getName() + " - " + subContract.getTitle());

        scheduledTransactions = (ArrayList<ScheduledTransaction>) scheduledTransactionList;

        if (scheduledTransactions != null)
            viewModel.setRaw(scheduledTransactions);

        amount.setText(new NumberFormatter(subContract.getAmount()).getFinalNumber());

        if (subContract.getStartDate() != null && subContract.getEndDate() != null)
            period.setText(DatabaseDatesFunctions.INSTANCE.timestampToPeriod(subContract.getStartDate(), subContract.getEndDate()));
        else if (subContract.getStartDate() != null)
            period.setText(DatabaseDatesFunctions.INSTANCE.timestampToString(subContract.getStartDate()));
        else
            period.setText("N/A");

        title.setText(contract.getTitle());
    }

    @Override
    public void onStop() {
        super.onStop();
        //mainActivity.modifyVisibilityOfMenuItem(R.id.menu_filter, false);
        mainActivity.setCurrentMenuClicker(null);
    }

    // activity handler functions


    public void onFilterClick() {
        SubContractFilterDialog filterDialog = new SubContractFilterDialog(getViewLifecycleOwner());
        filterDialog.show(getParentFragmentManager(), "SubContractDisplay");
    }


}
