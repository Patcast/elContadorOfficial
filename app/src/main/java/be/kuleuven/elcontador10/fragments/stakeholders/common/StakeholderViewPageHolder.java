package be.kuleuven.elcontador10.fragments.stakeholders.common;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayoutMediator;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ViewPagerAdapter;
import be.kuleuven.elcontador10.background.database.Caching;

import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.tools.ZoomOutPageTransformer;
import be.kuleuven.elcontador10.fragments.stakeholders.contracts.ContractsList;
import be.kuleuven.elcontador10.fragments.stakeholders.contracts.NewContractDialog;
import be.kuleuven.elcontador10.fragments.stakeholders.transactions.StakeholderTransactionsList;


// move FAB here
public class StakeholderViewPageHolder extends Fragment implements ZoomOutPageTransformer.PageChangeListener {
    private ViewPagerAdapter mAdapter;
    private ViewPager2 viewPager;

    private FloatingActionButton fab;
    private FloatingActionButton newTransaction;
    private FloatingActionButton newPayableReceivable;

    private TextView labelNewTransaction;
    private TextView labelNewPayableReceivable;

    private MainActivity mainActivity;
    private boolean fabClicked;

    private Animation rotateOpen,rotateClose,popOpen,popClose;

    private StakeholderViewModel viewModel;

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

        fab = view.findViewById(R.id.btn_stakeholder_view_holder);
        newTransaction = view.findViewById(R.id.btn_stakeholder_new_transaction);
        newPayableReceivable = view.findViewById(R.id.btn_stakeholder_new_ReceivableOrPayable);

        labelNewTransaction = view.findViewById(R.id.lbl_stakeholder_newTransaction);
        labelNewPayableReceivable = view.findViewById(R.id.lbl_stakeholder_newPayableReceivable);

        viewModel = new ViewModelProvider(requireActivity()).get(StakeholderViewModel.class);

        viewModel.getFabClicked().observe(getViewLifecycleOwner(), item -> {
            fabClicked = item;
            setVisibility();
            setAnimation();
        });

        addFragments(view);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StakeHolder stakeHolder = StakeholderViewPageHolderArgs.fromBundle(getArguments()).getStakeHolder();
        mainActivity.setHeaderText(stakeHolder.getName() + " - " + Caching.INSTANCE.getAccountName());
        mainActivity.displayTabLayout(true);
        Caching.INSTANCE.setChosenStakeHolder(stakeHolder);

        // set details
        String balance = new NumberFormatter(stakeHolder.getBalance()).getFinalNumber();
        mainActivity.displayStakeHolderDetails(true, balance, stakeHolder.getRole());

        Caching.INSTANCE.openMicroAccount(stakeHolder.getId()); // set MicroAccount to caching

        rotateOpen = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close);
        popOpen= AnimationUtils.loadAnimation(getContext(),R.anim.pop_up_fabs);
        popClose = AnimationUtils.loadAnimation(getContext(),R.anim.pop_down_fabs);
    }

    private void addFragments(View view) {
        mAdapter.addFragment(new StakeholderTransactionsList());
        mAdapter.addFragment(new ContractsList());
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
        switch(viewPager.getCurrentItem()){
            case 0:
                // transactions

                if (fabClicked)
                    viewModel.setFabClicked(true);

                fab.setOnClickListener(view -> {
                    setVisibility();
                    setAnimation();
                    fabClicked = !fabClicked;
                    viewModel.setFabClicked(fabClicked);
                });

                break;
            case 1:
                // contracts

                fab.setOnClickListener(view -> {
                    NewContractDialog dialog = new NewContractDialog((MainActivity) getActivity());
                    dialog.show();
                });

                fabClicked = true;
                setVisibility();
                fabClicked = false;

                break;
        }
    }

    private void setVisibility() {
        if (!fabClicked) {
            labelNewPayableReceivable.setVisibility(View.VISIBLE);
            labelNewTransaction.setVisibility(View.VISIBLE);
            newTransaction.setVisibility(View.VISIBLE);
            newPayableReceivable.setVisibility(View.VISIBLE);
        } else {
            labelNewPayableReceivable.setVisibility(View.GONE);
            labelNewTransaction.setVisibility(View.GONE);
            newTransaction.setVisibility(View.GONE);
            newPayableReceivable.setVisibility(View.GONE);
        }
    }

    private void setAnimation() {
        if (!fabClicked) {
            labelNewPayableReceivable.startAnimation(popOpen);
            labelNewTransaction.startAnimation(popOpen);
            newTransaction.startAnimation(popOpen);
            newPayableReceivable.startAnimation(popOpen);

            fab.startAnimation(rotateOpen);
        } else {
            labelNewPayableReceivable.startAnimation(popClose);
            labelNewTransaction.startAnimation(popClose);
            newTransaction.startAnimation(popClose);
            newPayableReceivable.startAnimation(popClose);

            fab.startAnimation(rotateClose);
        }
    }
}