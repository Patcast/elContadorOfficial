package be.kuleuven.elcontador10.fragments.accounts;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.AccountsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Account;



public class Accounts extends Fragment implements Caching.AccountsObserver, AccountsBottomMenu.AccountsBottomSheetListener {


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
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        startRecyclerView(view);
        checkLogIn(mainActivity.returnSavedLoggedEmail());
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> mainActivity.displayToolBar(true));
        setTopMenu();
    }
       private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                menu.findItem(R.id.menu_bottom_sheet).setVisible(true);
                menu.findItem(R.id.menu_add).setVisible(true);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_bottom_sheet:
                        onBottomSheetClick();
                        return true;
                    case R.id.menu_add:
                        onAddClick();
                        return true;
                    default:
                        return false;
                }
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void startRecyclerView(View view){
        recyclerAccounts = view.findViewById(R.id.recyclerAccounts);
        recyclerAccounts.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new AccountsRecViewAdapter(view);
        recyclerAccounts.setAdapter(adapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkLogIn(String email){
        if(email==null){
            navController.navigate(R.id.action_accounts_to_signIn);
        }
        else{
            Caching.INSTANCE.requestStaticData();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        Caching.INSTANCE.attachAccountsObservers(this, mainActivity.returnSavedLoggedEmail());
        try{
            mainActivity.displayBottomNavigationMenu(false);
        }
        catch(Exception e){}
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



    @Override
    public void onMySettingsClick() {
        bottomSheet.dismiss();
    }

    @Override
    public void onLogOut() {
        bottomSheet.dismiss();
        mainActivity.signOut();
        navController.navigate(R.id.signIn);
    }
    //Menu

    public void onBottomSheetClick() {
        bottomSheet = new AccountsBottomMenu(this);
        bottomSheet.show(getParentFragmentManager(),"AccountsBottomSheet");
    }
    public void onAddClick() {
        navController.navigate(R.id.action_accounts_to_addNewAccount);
    }

}