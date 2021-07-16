package be.kuleuven.elcontador10.fragments.transactions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.StakeHolderRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.viewModels.ChosenStakeViewModel;


public class ChooseStakeHolder extends Fragment implements Caching.StakeholdersObserver {
    private RecyclerView recyclerStakeHolds;
    private StakeHolderRecViewAdapter adapter;
    private List <StakeHolder> stakeHolders = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_stake_holder, container, false);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.select_micro_account));
        recyclerStakeHolds = view.findViewById(R.id.recyclerViewChooseStake);
        recyclerStakeHolds.setLayoutManager(new LinearLayoutManager(this.getContext()));
        ChosenStakeViewModel viewModel = new ViewModelProvider(requireActivity()).get(ChosenStakeViewModel.class);
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
}