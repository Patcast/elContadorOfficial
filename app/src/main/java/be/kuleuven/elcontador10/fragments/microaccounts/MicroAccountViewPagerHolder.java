package be.kuleuven.elcontador10.fragments.microaccounts;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MicroAccountViewPagerHolder extends Fragment {
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
        addFragments(view);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StakeHolder stakeHolder = MicroAccountViewPagerHolderArgs.fromBundle(getArguments()).getStakeHolder();
        mainActivity.setHeaderText(stakeHolder.getName());
        mainActivity.displayTabLayout(true);

        chosenAccountId = MicroAccountViewPagerHolderArgs.fromBundle(getArguments()).getAccountID();

        Caching.INSTANCE.openMicroAccount(stakeHolder.getId());
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

        // set account ID to origin
        Caching.INSTANCE.setChosenAccountId(chosenAccountId);
        mainActivity.setHeaderText(Caching.INSTANCE.getAccountName());
        mainActivity.displayTabLayout(false);
        Caching.INSTANCE.setChosenMicroAccountId(null);
    }
}