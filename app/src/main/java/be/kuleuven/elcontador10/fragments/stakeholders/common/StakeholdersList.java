package be.kuleuven.elcontador10.fragments.stakeholders.common;

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
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.StakeholderListRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;

import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.ViewModel_AllTransactions;


public class StakeholdersList extends Fragment implements  MainActivity.TopMenuHandler {
        private StakeholderListRecViewAdapter adapter;
        private MainActivity mainActivity;
        ViewModel_AllTransactions viewModel_allTransactions;
        private MenuItem menuItem;
        private NavController navController;


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                mainActivity = (MainActivity) getActivity();
                assert mainActivity != null;
                mainActivity.setCurrentMenuClicker(this);

                viewModel_allTransactions = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_all_micro_acounts, container, false);
                RecyclerView recyclerMicros = view.findViewById(R.id.recyclerViewAllMicro);
                recyclerMicros.setLayoutManager(new LinearLayoutManager(this.getContext()));
                adapter = new StakeholderListRecViewAdapter(view);
                recyclerMicros.setAdapter(adapter);

                return view;
        }

        @Override
        public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                navController = Navigation.findNavController(view);
                viewModel_allTransactions.getStakeholdersList().observe(getViewLifecycleOwner(), i->adapter.setStakeListOnAdapter(i));
                setTopMenu();
        }
           private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                menu.findItem(R.id.menu_search).setVisible(true);
                menu.findItem(R.id.menu_add_stake).setVisible(true);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_search:
                        onSearchClick(menuItem);
                        return true;
                    case R.id.menu_add_stake:
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
                mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
                Caching.INSTANCE.setChosenMicroAccountId(null);
                mainActivity.displayBottomNavigationMenu(true);
                mainActivity.setCurrentMenuClicker(this);
        }

        @Override
        public void onStop() {
                super.onStop();
                mainActivity.setCurrentMenuClicker(null);
                mainActivity.displayBottomNavigationMenu(false);
                if( menuItem != null) menuItem.collapseActionView();
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

        @Override
        public void onToolbarTitleClick() {
                navController.navigate(R.id.action_stakeholders_to_accountSettings);

        }
}