package be.kuleuven.elcontador10.fragments.stakeholders.contracts.subcontracts;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.contract.Contract;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.model.contract.SubContract;

public class SubContractDisplay extends Fragment implements Caching.SubContractObserver {

    //views


    // variables
    private MainActivity mainActivity;
    private ArrayList<ScheduledTransaction> scheduledTransactions;
    private SubContract subContract;
    private String idSubcontract;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_contract_subcontract, container, false);

        // set variables
        mainActivity = (MainActivity) getActivity();
        scheduledTransactions = new ArrayList<>();

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
    }

    @Override
    public void notify(SubContract contract, List<ScheduledTransaction> scheduledTransactionList) {
        subContract = contract;
        StakeHolder stakeHolder = Caching.INSTANCE.getChosenStakeHolder();
        mainActivity.setHeaderText(stakeHolder.getName() + " - " + subContract.getTitle());

        scheduledTransactions.clear();
        scheduledTransactions.addAll(scheduledTransactionList);
    }
}
