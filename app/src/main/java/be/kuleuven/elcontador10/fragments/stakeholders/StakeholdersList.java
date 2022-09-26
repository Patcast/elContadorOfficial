package be.kuleuven.elcontador10.fragments.stakeholders;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.background.adapters.StakeholderListRecViewAdapter;
import be.kuleuven.elcontador10.background.Caching;

import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.ViewModel_AllTransactions;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.TransactionNew;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class StakeholdersList extends Fragment {
    private LinearLayout noStakeLayout;
    private StakeholderListRecViewAdapter adapter;
    private MainActivity mainActivity;
    private ViewModel_AllTransactions viewModel_allTransactions;
    private MenuItem menuItem;
    private NavController navController;
    private ViewModel_NewTransaction viewModel_newTransaction;
    private String prevFrag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        viewModel_allTransactions = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        viewModel_newTransaction = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
        prevFrag = StakeholdersListArgs.fromBundle(getArguments()).getPrevFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_micro_acounts, container, false);
        noStakeLayout = view.findViewById(R.id.layoutNoStakeHolder);
        noStakeLayout.setOnClickListener(i->noStakeSelected());
        RecyclerView recyclerMicros = view.findViewById(R.id.recyclerViewAllMicro);
        recyclerMicros.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new StakeholderListRecViewAdapter(viewModel_newTransaction,view,prevFrag);
        recyclerMicros.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        viewModel_allTransactions.requestGroupOFStakeHolders(Caching.INSTANCE.getChosenAccountId());
        viewModel_allTransactions.getStakeholdersList().observe(getViewLifecycleOwner(), i->adapter.setStakeListOnAdapter(i));
        setTopMenu();
    }

    private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            final int menu_search = R.id.menu_search, menu_add_stake = R.id.menu_add_stake, menu_settings = R.id.menu_settings;

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                menu.findItem(menu_search).setVisible(true);
                if (prevFrag == null) {
                    menu.findItem(menu_add_stake).setVisible(true);
                    menu.findItem(menu_settings).setVisible(true);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case menu_search:
                        onSearchClick(menuItem);
                        return true;
                    case menu_settings:
                        navController.navigate(R.id.action_stakeholders_to_accountSettings);
                        return true;
                    case menu_add_stake:
                        addStakeholder();
                        return true;
                    default:
                        return false;
                }
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        Caching.INSTANCE.setChosenMicroAccountId(null);
        if (prevFrag == null) {
            mainActivity.displayBottomNavigationMenu(true);
            mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        } else if (prevFrag.equals(TransactionNew.TAG)) {
            noStakeLayout.setVisibility(View.VISIBLE);
            mainActivity.setHeaderText(getString(R.string.select_a_stakeholder));
        }
    }

    @Override
    public void onStop() {
            super.onStop();
            mainActivity.displayBottomNavigationMenu(false);
            if( menuItem != null) menuItem.collapseActionView();
            noStakeLayout.setVisibility(View.GONE);
    }

    public void onSearchClick(MenuItem item) {
        this.menuItem = item;
        SearchView searchView = (SearchView)  item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                        return false;
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                }
        });

    }

    public void addStakeholder() {
        navController.navigate(R.id.action_allMicroAccounts2_to_newMicroAccount);
    }

    private void noStakeSelected() {
        viewModel_newTransaction.reset();
        navController.popBackStack();
    }
}