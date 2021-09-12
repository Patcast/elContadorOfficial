package be.kuleuven.elcontador10.fragments.microaccounts;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.AllMicroRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;


public class AllMicroAccounts extends Fragment implements Caching.StakeholdersObserver , androidx.appcompat.widget.SearchView.OnQueryTextListener, MainActivity.TopMenuHandler {
        private RecyclerView recyclerMicros;
        private AllMicroRecViewAdapter adapter;
        private final List<StakeHolder> microsList = new ArrayList<>();
        MainActivity mainActivity;
        FloatingActionButton addNewMicro;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                mainActivity = (MainActivity) getActivity();
                mainActivity.setCurrentMenuClicker(this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_all_micro_acounts, container, false);
                recyclerMicros = view.findViewById(R.id.recyclerViewAllMicro);
                recyclerMicros.setLayoutManager(new LinearLayoutManager(this.getContext()));
                ViewModel_NewTransaction viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
                adapter = new AllMicroRecViewAdapter(view,viewModel);
                Caching.INSTANCE.attachStakeholdersObservers(this);
                if(microsList.size()>0) adapter.setMicroAccountsList(microsList);
                recyclerMicros.setAdapter(adapter);
                addNewMicro = view.findViewById(R.id.btn_new_MicroFAB);
                addNewMicro.setOnClickListener(this::onFAB_Clicked);
                return view;
        }

        @Override
        public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                mainActivity.displayBottomNavigationMenu(true);
        }

        @Override
        public void onStart() {
                super.onStart();
                Caching.INSTANCE.setChosenMicroAccountId(null);
                Caching.INSTANCE.attachStakeholdersObservers(this);
                if(microsList.size()>0) adapter.setMicroAccountsList(microsList);
                recyclerMicros.setAdapter(adapter);
        }

        @Override
        public void onStop() {
                super.onStop();
                Caching.INSTANCE.deAttachStakeholdersObservers(this);
                mainActivity.displayBottomNavigationMenu(false);
        }

        @Override
        public void notifyStakeholdersObserver(List<StakeHolder> stakeHolders) {
                this.microsList.clear();
                this.microsList.addAll(stakeHolders);
                adapter.setMicroAccountsList(this.microsList);
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
        public void onSearchClick(androidx.appcompat.widget.SearchView searchView) {
                searchView.setOnQueryTextListener((androidx.appcompat.widget.SearchView.OnQueryTextListener)this);
        }

        @Override
        public void onFilterClick() {

        }
}