package be.kuleuven.elcontador10.fragments.property;
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
import be.kuleuven.elcontador10.background.adapters.PropertiesListRecViewAdapter;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class PropertiesList extends Fragment {
    private PropertiesListRecViewAdapter adapter;
    private MainActivity mainActivity;
    PropertyListViewModel viewModelPropertiesList;
    private MenuItem menuItem;
    private NavController navController;
    private String prevTAG;
    private StakeHolder stakeHolder;
    private ViewModel_NewTransaction viewModel_newTransaction;

    public PropertiesList(String prevTAG, StakeHolder stakeHolder) {
        this.prevTAG = prevTAG;
        this.stakeHolder = stakeHolder;
    }

    public PropertiesList() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        prevTAG =PropertiesListArgs.fromBundle(getArguments()).getPreviousFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_properties, container, false);


        viewModel_newTransaction = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
        viewModelPropertiesList = new ViewModelProvider(requireActivity()).get(PropertyListViewModel.class);
        viewModelPropertiesList.requestListOfProperties();

        RecyclerView recyclerView = view.findViewById(R.id.rec_all_properties);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        if(prevTAG == null) adapter = new PropertiesListRecViewAdapter(view);
        else adapter = new PropertiesListRecViewAdapter(view, prevTAG, viewModel_newTransaction);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        LinearLayout noPropertyItem = view.findViewById(R.id.layoutNoProperty);
        noPropertyItem.setVisibility(View.GONE);
        if(prevTAG==null||prevTAG.equals(Caching.INSTANCE.PROPERTY_NEW_T)){
            viewModelPropertiesList.getListOfProperties().observe(getViewLifecycleOwner(), properties -> adapter.setPropertyListOnAdapter(properties));
            setTopMenu();
            if (prevTAG!=null){
                noPropertyItem.setVisibility(View.VISIBLE);
                noPropertyItem.setOnClickListener(c->onNoPropertySelected());
            }
        }
        else{
            viewModelPropertiesList.requestStakeholderProperties(stakeHolder);
            viewModelPropertiesList.getStakeholderProperties().observe(getViewLifecycleOwner(), properties -> adapter.setPropertyListOnAdapter(properties));
        }
    }

    private void onNoPropertySelected() {
        viewModel_newTransaction.resetChosenProperty();
        navController.popBackStack();
    }

    private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            final int menu_search = R.id.menu_search,menu_share = R.id.menu_share,menu_add_property = R.id.menu_add_property;

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                menu.findItem(R.id.menu_search).setVisible(true);
                menu.findItem(menu_share).setVisible(true);
               if (prevTAG == null ) menu.findItem(menu_add_property).setVisible(true);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case menu_search:
                        onSearchClick(menuItem);
                        return true;
                    case menu_share:
                        navController.navigate(R.id.action_propertiesList_to_accountSettings);
                        return true;
                    case menu_add_property:
                        addProperty();
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
        if (prevTAG == null || prevTAG.equals(Caching.INSTANCE.PROPERTY_NEW_T)){
            mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
            if (prevTAG == null) {
                mainActivity.displayBottomNavigationMenu(true);
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (prevTAG == null || prevTAG.equals(Caching.INSTANCE.PROPERTY_NEW_T)){
            if (prevTAG == null) {
                mainActivity.displayBottomNavigationMenu(false);
            }
            if (menuItem != null) menuItem.collapseActionView();
        }
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
                PropertiesListRecViewAdapter.PropertyFilter filter =
                        (PropertiesListRecViewAdapter.PropertyFilter) adapter.getFilter();
                filter.setVacant(getString(R.string.vacant));
                filter.filter(newText);
                return false;
            }
        });

    }

    public void addProperty() {
        navController.navigate(R.id.action_propertiesList_to_addProperty);
    }

}