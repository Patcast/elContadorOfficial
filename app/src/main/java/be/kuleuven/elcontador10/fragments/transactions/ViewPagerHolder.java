package be.kuleuven.elcontador10.fragments.transactions;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ViewPagerAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Account;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.fragments.microaccounts.AllMicroAccounts;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions;


public class ViewPagerHolder extends Fragment implements Caching.AccountsObserver {

   ViewPagerAdapter mAdapter;
   ViewPager2 viewPager2;
   MainActivity mainActivity;
    private TextView textBalanceAccount;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.displayToolBar(true);
        String name = Caching.INSTANCE.getAccountName();
        mainActivity.setHeaderText(name);
        mainActivity.displayTabLayout(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager_holder, container, false);
        viewPager2 =view.findViewById(R.id.viewPagerHolder);
        textBalanceAccount = view.findViewById(R.id.textView_allTransactions_balance);
        mAdapter = new ViewPagerAdapter(mainActivity.getSupportFragmentManager(),getLifecycle());
        addFragments(view);
        return view;
    }

    private void addFragments(View view) {
        mAdapter.addFragment(new AllTransactions());
        mAdapter.addFragment(new AllMicroAccounts());
        viewPager2.setAdapter(mAdapter);
        new TabLayoutMediator(mainActivity.getTabLayout(),viewPager2,(t,p)->{
            switch(p){
                case 0:
                    t.setText("Transactions");
                    t.setIcon(R.drawable.icon_transaction);
                    break;
                case 1:
                    t.setText("Micro Accounts");
                    t.setIcon(R.drawable.ic_baseline_people_alt_24);
                    break;

            }
        }).attach();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        mainActivity.displayToolBar(true);
        mainActivity.displayTabLayout(true);
        Caching.INSTANCE.attachAccountsObservers(this,mainActivity.returnSavedLoggedEmail());
    }
    @Override
    public void onStop() {
        super.onStop();
        mainActivity.displayToolBar(true);
        mainActivity.displayTabLayout(false);
        Caching.INSTANCE.deAttachAccountsObservers(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void notifyAccountsObserver(List<Account> accounts) {
        textBalanceAccount.setText(Caching.INSTANCE.getAccountBalance());
    }
}