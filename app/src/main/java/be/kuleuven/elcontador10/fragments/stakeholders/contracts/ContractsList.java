package be.kuleuven.elcontador10.fragments.stakeholders.contracts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ContractsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.contract.Contract;

public class ContractsList extends Fragment implements Caching.MicroAccountContractObserver {
    private RecyclerView recyclerView;
    private ContractsRecViewAdapter adapter;
    private List<Contract> contracts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_micro_account_contracts, container, false);

        contracts = new ArrayList<>();

        recyclerView = view.findViewById(R.id.RecViewContracts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ContractsRecViewAdapter(view, getContext(), this);
        Caching.INSTANCE.attachMicroContractObserver(this);
        if (contracts.size() > 0) adapter.setContracts(contracts);

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachMicroContractObserver(this);
        contracts.clear();
    }

    @Override
    public void notifyMicroAccountContractsObserver(List<Contract> contracts) {
        this.contracts = contracts;
        adapter.setContracts(contracts);
    }
}