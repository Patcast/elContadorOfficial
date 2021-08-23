package be.kuleuven.elcontador10.fragments.accounts;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ViewPagerAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Account;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.tools.ZoomOutPageTransformer;
import be.kuleuven.elcontador10.fragments.microaccounts.AllMicroAccounts;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions;


public class ViewPagerHolderAccount extends Fragment implements Caching.AccountsObserver, ZoomOutPageTransformer.PageChangeListener, MainActivity.TopMenuHandler {

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
        addFragments();
        viewPager2.setPageTransformer(new ZoomOutPageTransformer(this));
        return view;
    }

    private void addFragments() {
        mAdapter.addFragment(new AllTransactions());
        mAdapter.addFragment(new AllMicroAccounts());
        viewPager2.setAdapter(mAdapter);
        new TabLayoutMediator(mainActivity.getTabLayout(),viewPager2,(t,p)->{
            switch(p){
                case 0:
                    t.setText(getString(R.string.transactions));
                    t.setIcon(R.drawable.icon_transaction);
                    break;
                case 1:
                    t.setText(getString(R.string.stakeholders));
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
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_filter,false);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,false);
        Caching.INSTANCE.deAttachAccountsObservers(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void notifyAccountsObserver(List<Account> accounts) {
        textBalanceAccount.setText(Caching.INSTANCE.getAccountBalance());
    }


    @Override
    public void onPageChange() {
        switch(viewPager2.getCurrentItem()){
            case 0:
                mainActivity.modifyVisibilityOfMenuItem(R.id.menu_filter,true);
                mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,false);
                break;
            case 1:
                mainActivity.modifyVisibilityOfMenuItem(R.id.menu_filter,false);
                mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,true);
                break;
        }
    }

    ///// TopMenuHandler Details
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
    public void onSearchClick(SearchView searchView) {

    }

    @Override
    public void onFilterClick() {
        Toast.makeText(requireContext(), "Filter coming soon...", Toast.LENGTH_SHORT).show();
    }
}