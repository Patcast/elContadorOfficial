package be.kuleuven.elcontador10.fragments.property;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;




import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.PropertiesListRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class PropertiesList extends Fragment implements  MainActivity.TopMenuHandler {
    private PropertiesListRecViewAdapter adapter;
    private MainActivity mainActivity;
    PropertyListViewModel viewModelPropertiesList;
    private ViewModel_NewTransaction viewModel;
    private MenuItem menuItem;
    private NavController navController;
    private String prevTAG;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.setCurrentMenuClicker(this);
        viewModelPropertiesList = new ViewModelProvider(requireActivity()).get(PropertyListViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_properties, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rec_all_properties);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        try{
            prevTAG = PropertiesListArgs.fromBundle(getArguments()).getPreviousFragment();
            adapter = new PropertiesListRecViewAdapter(view,prevTAG,viewModel);

        }catch (Exception e){
            adapter = new PropertiesListRecViewAdapter(view);
        }

        recyclerView.setAdapter(adapter);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        viewModelPropertiesList.getListOfProperties().observe(getViewLifecycleOwner(), i->adapter.setPropertyListOnAdapter(i));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,true);
        if(prevTAG==null) {
            mainActivity.modifyVisibilityOfMenuItem(R.id.menu_add_property,true);
            mainActivity.displayBottomNavigationMenu(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,false);

        mainActivity.displayBottomNavigationMenu(false);
        if(prevTAG==null) {
            mainActivity.modifyVisibilityOfMenuItem(R.id.menu_add_property,false);
            mainActivity.displayBottomNavigationMenu(false);
        }
        if( menuItem != null) menuItem.collapseActionView();
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
    public void onExportClick() {

    }
    @Override
    public void addStakeholder() {

    }

    @Override
    public void addProperty() {
        navController.navigate(R.id.action_propertiesList_to_addProperty);

    }


    @Override
    public void onToolbarTitleClick() {
        if(prevTAG==null)navController.navigate(R.id.action_propertiesList_to_accountSettings);
    }
}