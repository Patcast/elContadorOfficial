package be.kuleuven.elcontador10.fragments.microaccounts;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ViewPagerAdapter;
import be.kuleuven.elcontador10.background.database.Caching;

import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.tools.ZoomOutPageTransformer;

public class MicroAccountViewPagerHolder extends Fragment implements ZoomOutPageTransformer.PageChangeListener {
    private ViewPagerAdapter mAdapter;

    private ViewPager2 viewPager;

    private MainActivity mainActivity;
    private String chosenAccountId;

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

        addFragments(view);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StakeHolder stakeHolder = MicroAccountViewPagerHolderArgs.fromBundle(getArguments()).getStakeHolder();
        mainActivity.setHeaderText(stakeHolder.getName() + " - " + Caching.INSTANCE.getAccountName());
        mainActivity.displayTabLayout(true);
        Caching.INSTANCE.setChosenStakeHolder(stakeHolder);

        chosenAccountId = Caching.INSTANCE.getChosenAccountId();

        // set details
        String balance = new NumberFormatter(stakeHolder.getBalance()).getFinalNumber();
        mainActivity.displayStakeHolderDetails(true, balance, stakeHolder.getRole());


        Caching.INSTANCE.openMicroAccount(stakeHolder.getId()); // set MicroAccount to caching
    }

    private void addFragments(View view) {
        mAdapter.addFragment(new MicroAccountTransactions());
        mAdapter.addFragment(new MicroAccountContracts());
        viewPager.setAdapter(mAdapter);

        new TabLayoutMediator(mainActivity.getTabLayout(), viewPager, (t, p) -> {
            switch (p) {
                case 0:
                    t.setText("Transactions");
                    t.setIcon(R.drawable.icon_transaction);
                    break;
                case 1:
                    t.setText("Contracts");
                    t.setIcon(R.drawable.icon_contracts);
                    break;
            }
        }).attach();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStop() {
        super.onStop();

        mainActivity.displayToolBar(true);
        mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        mainActivity.displayTabLayout(false);
        mainActivity.displayStakeholderDetails(false);
    }

    @Override
    public void onPageChange() {
//        switch(viewPager.getCurrentItem()){
//            case 0:
//
//                break;
//            case 1:
//
//                break;
//        }
    }
}