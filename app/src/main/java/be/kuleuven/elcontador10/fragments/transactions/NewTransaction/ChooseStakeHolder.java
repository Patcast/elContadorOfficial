package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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
import be.kuleuven.elcontador10.background.adapters.StakeHolderRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;


public class ChooseStakeHolder extends Fragment implements Caching.StakeholdersObserver , SearchView.OnQueryTextListener {
    private RecyclerView recyclerStakeHolds;
    private android.widget.SearchView txtSearch;
    private StakeHolderRecViewAdapter adapter;
    private List <StakeHolder> stakeHolders = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_stake_holder, container, false);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.select_micro_account));
        recyclerStakeHolds = view.findViewById(R.id.recyclerViewChooseStake);
        txtSearch = view.findViewById(R.id.searchChosenStake);
        txtSearch.setOnQueryTextListener(this);
        recyclerStakeHolds.setLayoutManager(new LinearLayoutManager(this.getContext()));
        ViewModel_NewTransaction viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
        adapter = new StakeHolderRecViewAdapter(view,viewModel);
        Caching.INSTANCE.attachStakeholdersObservers(this);
        if(stakeHolders.size()>0) adapter.setStakeholdersList(stakeHolders);
        recyclerStakeHolds.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        Caching.INSTANCE.attachStakeholdersObservers(this);
        if(stakeHolders.size()>0) adapter.setStakeholdersList(stakeHolders);
        recyclerStakeHolds.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachStakeholdersObservers(this);
    }
    @Override
    public void notifyStakeholdersObserver(List<StakeHolder> stakeHolders) {
        this.stakeHolders.clear();
        this.stakeHolders.addAll(stakeHolders);
        adapter.setStakeholdersList(this.stakeHolders);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }
}