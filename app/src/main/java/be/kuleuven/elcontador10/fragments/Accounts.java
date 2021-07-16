package be.kuleuven.elcontador10.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.AccountsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.model.Account;
import be.kuleuven.elcontador10.background.model.StakeHolder;


public class Accounts extends Fragment implements Caching.AccountsObserver {

    private static final String TAG = "Accounts";
    RecyclerView recyclerAccounts;
    AccountsRecViewAdapter adapter;
    ArrayList<Account> accountsList = new ArrayList<>();
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        Caching.INSTANCE.attachAccountsObservers(this);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.accounts));
        recyclerAccounts = view.findViewById(R.id.recyclerAccounts);
        recyclerAccounts.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new AccountsRecViewAdapter(view);
       if(accountsList.size()>0) adapter.setAccounts(accountsList);
        recyclerAccounts.setAdapter(adapter);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Caching.INSTANCE.attachAccountsObservers(this);
        if(accountsList.size()>0) adapter.setAccounts(accountsList);
        recyclerAccounts.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachAccountsObservers(this);
    }

    @Override
    public void notifyAccountsObserver(List<Account> accounts) {
            accountsList.clear();
            accountsList.addAll(accounts);
            adapter.setAccounts(accountsList);

    }
}