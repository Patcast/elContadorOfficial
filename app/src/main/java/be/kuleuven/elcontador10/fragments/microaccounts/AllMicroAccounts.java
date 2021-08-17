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
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.adapters.AllMicroRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.NewTransactionViewModel;


public class AllMicroAccounts extends Fragment implements Caching.StakeholdersObserver , SearchView.OnQueryTextListener {
        private RecyclerView recyclerMicros;
        private android.widget.SearchView txtSearch;
        private AllMicroRecViewAdapter adapter;
        private List<StakeHolder> microsList = new ArrayList<>();
        private FloatingActionButton addNewMicro;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_all_micro_acounts, container, false);

                txtSearch = view.findViewById(R.id.searchAllMicro);
                txtSearch.setOnQueryTextListener(this);

                recyclerMicros = view.findViewById(R.id.recyclerViewAllMicro);
                recyclerMicros.setLayoutManager(new LinearLayoutManager(this.getContext()));

                NewTransactionViewModel viewModel = new ViewModelProvider(requireActivity()).get(NewTransactionViewModel.class);
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
        }

        @Override
        public void onStart() {
                super.onStart();
                Caching.INSTANCE.attachStakeholdersObservers(this);
                if(microsList.size()>0) adapter.setMicroAccountsList(microsList);
                recyclerMicros.setAdapter(adapter);
        }

        @Override
        public void onStop() {
                super.onStop();
                Caching.INSTANCE.deAttachStakeholdersObservers(this);
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
                navController.navigate(R.id.action_viewPagerHolder_to_newMicroAccount);
        }
}