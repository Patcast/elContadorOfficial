package be.kuleuven.elcontador10.fragments.stakeholders;

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
import java.util.Arrays;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.RecyclerViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.database.StakeholdersManager;
import be.kuleuven.elcontador10.background.database.TransactionsManager;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersSummaryInterface;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.TransactionType;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

public class StakeholderSummary extends Fragment implements StakeholdersSummaryInterface, CachingObserver {
    // private variables
    private MainActivity mainActivity;

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabFilter;

    private NavController navController;
    private ArrayList<String> roles= new ArrayList<>();
    private RecyclerViewAdapter recyclerViewAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.stakeholder));
        roles.addAll(Caching.INSTANCE.roles);
        Caching.INSTANCE.attachCaching(this);
        return inflater.inflate(R.layout.fragment_stakeholder_summary, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set views variables
        recyclerView = requireView().findViewById(R.id.TransactionsRecycler);
        fabAdd = requireView().findViewById(R.id.btn_add_Transaction);
        fabFilter = requireView().findViewById(R.id.btn_filter_Transaction);

        // set up recyclerview
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        });

        //set navigation
        navController = Navigation.findNavController(view);
        fabFilter.setOnClickListener(v -> navController.navigate(R.id.action_stakeholderSummary_to_stakeholderFilter));
        fabAdd.setOnClickListener(v -> navController.navigate(R.id.action_stakeholderSummary_to_stakeholderNew));
    }

    @Override
    public void populateRecyclerView(ArrayList<String> title, ArrayList<String> description, ArrayList<String> status, ArrayList<String> metadata) {
        recyclerViewAdapter = new RecyclerViewAdapter(title, description, status, metadata, this);
    }

    @Override
    public void error(String error) {
        Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayStakeholder(String id) {
        StakeholderSummaryDirections.ActionStakeholderSummaryToStakeholderDisplay action =
                StakeholderSummaryDirections.actionStakeholderSummaryToStakeholderDisplay(id);
        navController.navigate(action);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Caching.INSTANCE.detach(this);
    }
//// Implementation of the Interfaces
    @Override
    public void notifyRoles(List<String> roles) {

    }

    @Override
    public void notifyCategories(List<TransactionType> transTypes) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void notifyStakeHolders(List<StakeHolder> stakeHolders) {
        // get arguments
        FilterStakeholdersParcel filter;
        try {
            StakeholderSummaryArgs args = StakeholderSummaryArgs.fromBundle(getArguments());
            filter = args.getFilter();
        } catch (Exception e) {
            filter = new FilterStakeholdersParcel("*", roles, false, "Name");
        }

        StakeholdersManager manager = StakeholdersManager.getInstance();
        manager.getStakeholders(this, stakeHolders, filter);
    }
}