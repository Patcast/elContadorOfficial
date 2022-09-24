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


import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.background.adapters.PropertiesListRecViewAdapter;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class PropertiesList extends Fragment implements  MainActivity.TopMenuHandler {
    private PropertiesListRecViewAdapter adapter;
    private MainActivity mainActivity;
    PropertyListViewModel viewModelPropertiesList;
    private ViewModel_NewTransaction viewModel;
    private MenuItem menuItem;
    private NavController navController;
    private String prevTAG;


    public PropertiesList(String prevTAG) {
        this.prevTAG = prevTAG;
    }

    public PropertiesList() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_properties, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rec_all_properties);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mainActivity = (MainActivity) requireActivity();
        viewModelPropertiesList = new ViewModelProvider(requireActivity()).get(PropertyListViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
        viewModelPropertiesList.requestListOfProperties();
        try {
            prevTAG = PropertiesListArgs.fromBundle(getArguments()).getPreviousFragment();
        }
        catch(Exception e){}

         if(prevTAG==null)adapter = new PropertiesListRecViewAdapter(view);
         else {
            adapter = new PropertiesListRecViewAdapter(view,prevTAG,viewModel);
         }



        recyclerView.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (prevTAG == null || prevTAG.equals(Caching.INSTANCE.PROPERTY_NEW_T)){
            navController = Navigation.findNavController(view);
            viewModelPropertiesList.getListOfProperties().observe(getViewLifecycleOwner(), i->adapter.setPropertyListOnAdapter(i));
            setTopMenu();
        }
    }
       private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                menu.findItem(R.id.menu_search).setVisible(true);
               if (prevTAG == null ) menu.findItem(R.id.menu_add_property).setVisible(true);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.menu_search:
                        onSearchClick(menuItem);
                        return true;
                    case R.id.menu_add_property:
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
            mainActivity.setCurrentMenuClicker(this);
            mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
            if(prevTAG==null) {
                mainActivity.displayBottomNavigationMenu(true);
            }
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        if (prevTAG == null || prevTAG.equals(Caching.INSTANCE.PROPERTY_NEW_T)){
            mainActivity.setCurrentMenuClicker(null);
            if(prevTAG==null) {
                mainActivity.displayBottomNavigationMenu(false);
            }
            if( menuItem != null) menuItem.collapseActionView();
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


    @Override
    public void onToolbarTitleClick() {
        if(prevTAG==null)navController.navigate(R.id.action_propertiesList_to_accountSettings);
    }
}