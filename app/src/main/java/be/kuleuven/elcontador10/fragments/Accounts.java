package be.kuleuven.elcontador10.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.AccountsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Account;


public class Accounts extends Fragment  {

    private static final String TAG = "Accounts";
    RecyclerView recyclerAccounts;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Account> accounts = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.accounts));

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerAccounts = view.findViewById(R.id.recyclerAccounts);
        AccountsRecViewAdapter adapter = new AccountsRecViewAdapter(view);
        recyclerAccounts.setAdapter(getAccounts(adapter));
        recyclerAccounts.setLayoutManager(new LinearLayoutManager(this.getContext()));


    }

    private AccountsRecViewAdapter getAccounts(AccountsRecViewAdapter adapter) {

        db.collection("/globalAccounts/"+ Caching.INSTANCE.getGlobalAccountId() +"/accounts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            Account myAccount =  new Account((long)doc.get("balance"),(String)doc.get("name"), doc.getId());
                            accounts.add(myAccount);
                        }
                        adapter.setGreetings(accounts);
                    }
                });
        return adapter;
    }


}