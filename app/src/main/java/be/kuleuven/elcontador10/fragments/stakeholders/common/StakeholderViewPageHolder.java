package be.kuleuven.elcontador10.fragments.stakeholders.common;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ViewPagerAdapter;
import be.kuleuven.elcontador10.background.database.Caching;

import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.tools.ZoomOutPageTransformer;
import be.kuleuven.elcontador10.fragments.stakeholders.StakeDetailsList;
import be.kuleuven.elcontador10.fragments.stakeholders.StakeholderViewModel;
import be.kuleuven.elcontador10.fragments.stakeholders.contracts.ContractsList;


// move FAB here
public class StakeholderViewPageHolder extends Fragment implements ZoomOutPageTransformer.PageChangeListener {
    private ViewPagerAdapter mAdapter;
    private ViewPager2 viewPager;
    private MainActivity mainActivity;
    private StakeholderViewModel viewModel;
    StakeHolder stakeHolder;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.displayToolBar(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_micro_account_view_holder, container, false);
        viewPager = view.findViewById(R.id.viewPagerHolder);
        mAdapter = new ViewPagerAdapter(mainActivity.getSupportFragmentManager(), getLifecycle());
        viewPager.setPageTransformer(new ZoomOutPageTransformer(this));
        viewModel = new ViewModelProvider(requireActivity()).get(StakeholderViewModel.class);

        addFragments();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stakeHolder = StakeholderViewPageHolderArgs.fromBundle(getArguments()).getStakeHolder();
        mainActivity.setHeaderText(stakeHolder.getName() + " - " + Caching.INSTANCE.getAccountName());
        Caching.INSTANCE.setChosenStakeHolder(stakeHolder);
        viewModel.setSelectedStakeholder(stakeHolder);
        Caching.INSTANCE.openMicroAccount(stakeHolder.getId()); // set MicroAccount to caching
    }

    private void addFragments() {
        mAdapter.addFragment(new StakeDetailsList(Caching.INSTANCE.TYPE_CASH));
        mAdapter.addFragment(new StakeDetailsList(Caching.INSTANCE.TYPE_RECEIVABLES));
        mAdapter.addFragment(new StakeDetailsList(Caching.INSTANCE.TYPE_PAYABLES));
        viewPager.setAdapter(mAdapter);

        new TabLayoutMediator(mainActivity.getTabLayout(), viewPager, (t, p) -> {
            switch (p) {
                case 0:
                    t.setText(R.string.transactions);
                    t.setIcon(R.drawable.icon_transaction);
                    break;
                case 1:
                    t.setText(R.string.receivables);
                    t.setIcon(R.drawable.icon_contracts);
                    break;
                case 2:
                    t.setText(R.string.payables);
                    t.setIcon(R.drawable.icon_contracts);
                    break;
            }
        }).attach();
    }

    @Override
    public void onStart() {
        super.onStart();
        // set details
        String balance = new NumberFormatter(stakeHolder.getEquity()).getFinalNumber();
        mainActivity.displayStakeHolderDetails(true, balance, stakeHolder.getRole());
        mainActivity.displayToolBar(true);
        mainActivity.displayTabLayout(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStop() {
        super.onStop();

        mainActivity.displayTabLayout(false);
        mainActivity.displayStakeholderDetails(false);
    }

    @Override
    public void onPageChange() {
        switch(viewPager.getCurrentItem()){
            case 0:

                break;
            default:
                break;
        }
    }
}