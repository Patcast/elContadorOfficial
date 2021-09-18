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

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.contract.Contract;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.model.contract.SubContract;

public class SubContractDisplay extends Fragment {

    // variables
    private MainActivity mainActivity;
    private ArrayList<ScheduledTransaction> scheduledTransactions;
    private SubContract subContract;
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_contract_subcontract, container, false);

        // set variables
        mainActivity = (MainActivity) getActivity();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get arguments
        id = SubContractDisplayArgs.fromBundle(getArguments()).getId();

        StakeHolder stakeHolder = Caching.INSTANCE.getChosenStakeHolder();
        Contract contract = Caching.INSTANCE.getChosenContract();
        subContract = contract.getSubContractFromId(id);
        Caching.INSTANCE.setChosenSubContract(subContract);

        mainActivity.setHeaderText(stakeHolder.getName() + " - " + subContract.getTitle());
    }
}
