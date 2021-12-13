package be.kuleuven.elcontador10.fragments.stakeholders.common.AllStakeholders;

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

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.StakeholderListRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

// TODO make search bar at the top of list
public class StakeholdersList extends Fragment implements  MainActivity.TopMenuHandler {
        private StakeholderListRecViewAdapter adapter;
        MainActivity mainActivity;
        FloatingActionButton addNewMicro;
        ViewModel_AllStakeholders viewModelAllStakes;
        private MenuItem menuItem;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                mainActivity = (MainActivity) getActivity();
                assert mainActivity != null;
                mainActivity.setCurrentMenuClicker(this);
                viewModelAllStakes = new ViewModelProvider(requireActivity()).get(ViewModel_AllStakeholders.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_all_micro_acounts, container, false);
                RecyclerView recyclerMicros = view.findViewById(R.id.recyclerViewAllMicro);
                recyclerMicros.setLayoutManager(new LinearLayoutManager(this.getContext()));
                ViewModel_NewTransaction viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
                adapter = new StakeholderListRecViewAdapter(view,viewModel);
                recyclerMicros.setAdapter(adapter);
                addNewMicro = view.findViewById(R.id.btn_new_MicroFAB);
                addNewMicro.setOnClickListener(this::onFAB_Clicked);
                return view;
        }

        @Override
        public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                viewModelAllStakes.getStakeholdersList().observe(getViewLifecycleOwner(), i->adapter.setStakeListOnAdapter(i));
        }


        @Override
        public void onStart() {
                super.onStart();
                Caching.INSTANCE.setChosenMicroAccountId(null);
                mainActivity.displayBottomNavigationMenu(true);
                mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,true);
        }

        @Override
        public void onStop() {
                super.onStop();
                mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,false);
                mainActivity.displayBottomNavigationMenu(false);
                if( menuItem != null) menuItem.collapseActionView();
        }

        public void onFAB_Clicked(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_allMicroAccounts2_to_newMicroAccount);
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
}