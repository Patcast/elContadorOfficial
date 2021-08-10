package be.kuleuven.elcontador10.fragments.accounts;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.AccountsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.model.Account;
import be.kuleuven.elcontador10.background.model.StakeHolder;


public class Accounts extends Fragment implements Caching.AccountsObserver, MainActivity.MenuClicker, AccountsBottomMenu.AccountsBottomSheetListener {

    //Todo: Accounts are repeating when opening fragment after logging in and bottom sheet is not disappearing.
    private static final String TAG = "Accounts";
    RecyclerView recyclerAccounts;
    AccountsRecViewAdapter adapter;
    MainActivity mainActivity;
    ArrayList<Account> accountsList = new ArrayList<>();
    View view;
    NavController navController;
    AccountsBottomMenu bottomSheet;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.accounts));
        mainActivity.setCurrentMenuClicker(this);
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        String email= mainActivity.returnSavedLoggedEmail();
        logInRequired(email==null,email);
        recyclerAccounts = view.findViewById(R.id.recyclerAccounts);
        recyclerAccounts.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new AccountsRecViewAdapter(view);
        Caching.INSTANCE.attachAccountsObservers(this);
        if(accountsList.size()>0) adapter.setAccounts(accountsList);
        recyclerAccounts.setAdapter(adapter);
        System.out.println(Thread.getAllStackTraces());

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void logInRequired(boolean required, String email){
        if(required){
            navController.navigate(R.id.signIn);
            accountsList.clear();
        }
        else{
            if(accountsList.size()==0) Caching.INSTANCE.requestAllUserAccounts(email);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if(Caching.INSTANCE.getNumberOfAccountObservers() ==0){
            Caching.INSTANCE.attachAccountsObservers(this);
            if(accountsList.size()>0) adapter.setAccounts(accountsList);
            recyclerAccounts.setAdapter(adapter);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> mainActivity.displayToolBar(true));
    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachAccountsObservers(this);
        mainActivity.displayToolBar(false);
    }

    @Override
    public void notifyAccountsObserver(List<Account> accounts) {
            accountsList.clear();
            accountsList.addAll(accounts);
            adapter.setAccounts(accountsList);

    }

    @Override
    public void onBottomSheetClick() {
        bottomSheet = new AccountsBottomMenu(this);
        bottomSheet.show(getParentFragmentManager(),"AccountsBottomSheet");
    }

    @Override
    public void onAddAccountClick() {
        bottomSheet.dismiss();
        navController.navigate(R.id.action_accounts_to_addNewAccount);
    }

    @Override
    public void onLogOut() {
        bottomSheet.dismiss();
        mainActivity.deleteSavedLoggedEmail();
        navController.navigate(R.id.action_accounts_to_signIn);
    }
}