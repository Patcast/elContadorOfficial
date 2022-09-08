package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ChooseStakeHolderRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.ViewModel_AllTransactions;


public class ChooseStakeHolder extends Fragment implements MainActivity.TopMenuHandler{
    private ConstraintLayout noStakeLayout;
    private ChooseStakeHolderRecViewAdapter adapter;
    private ViewModel_NewTransaction viewModel;
    private NavController navController;
    private MainActivity mainActivity;
    private MenuItem menuItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_stake_holder, container, false);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.select_a_stakeholder));
        mainActivity.setCurrentMenuClicker(this);
        RecyclerView recyclerStakeHolds = view.findViewById(R.id.recyclerViewChooseStake);
        noStakeLayout = view.findViewById(R.id.layoutNoStakeHolder);
        recyclerStakeHolds.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new ChooseStakeHolderRecViewAdapter(view,viewModel);
        recyclerStakeHolds.setAdapter(adapter);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noStakeLayout.setOnClickListener(i->noStakeSelected());
        navController = Navigation.findNavController(view);
        ViewModel_AllTransactions viewModel_allTransactions = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        viewModel_allTransactions.requestGroupOFStakeHolders(Caching.INSTANCE.getChosenAccountId());
        viewModel_allTransactions.getStakeholdersList().observe(getViewLifecycleOwner(), i->adapter.setStakeholdersList(i));
    }

    @Override
    public void onStart(){
        super.onStart();
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,true);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_add,false);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_bottom_sheet,false);
    }

    @Override
    public void onStop() {
        super.onStop();

        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,false);
        if( menuItem != null) menuItem.collapseActionView();
    }

    private void noStakeSelected() {
        viewModel.reset();
        navController.popBackStack();
    }

    @Override
    public void onBottomSheetClick() {

    }

    @Override
    public void onDeleteClick() {

    }

    @Override
    public void onEditingClick() {

    }

    @Override
    public void onAddClick() {

    }

    @Override
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

    @Override
    public void onFilterClick() {

    }

    @Override
    public void onToolbarTitleClick() {

    }
    @Override
    public void onExportClick() {

    }
    @Override
    public void addStakeholder() {

    }

    @Override
    public void addProperty() {

    }
}