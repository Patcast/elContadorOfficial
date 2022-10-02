package be.kuleuven.elcontador10.fragments.stakeholders;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.Inflater;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ViewPagerAdapter;
import be.kuleuven.elcontador10.background.Caching;

import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.tools.ZoomOutPageTransformer;
import be.kuleuven.elcontador10.fragments.property.PropertiesList;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.ViewModel_AllTransactions;


public class StakeholderViewPageHolder extends Fragment implements ZoomOutPageTransformer.PageChangeListener, MainActivity.FABImplement {
    private NavController navController;
    private ViewPagerAdapter mAdapter;
    private ViewPager2 viewPager;
    private MainActivity mainActivity;
    private StakeholderViewModel viewModel;

    StakeHolder stakeHolder;
    String sumOfTransactions, initialReceivables,initialPayables;
    List<ProcessedTransaction> processedTransactionList = new ArrayList<>();
    private ViewModel_AllTransactions viewModelAllTransactions;

    private StakeDetailsList payables;
    private StakeDetailsList receivables;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.displayToolBar(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_micro_account_view_holder, container, false);
        viewPager = view.findViewById(R.id.viewPagerHolder);
        mAdapter = new ViewPagerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setPageTransformer(new ZoomOutPageTransformer(this));
        viewModel = new ViewModelProvider(requireActivity()).get(StakeholderViewModel.class);
        viewModelAllTransactions = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        stakeHolder = StakeholderViewPageHolderArgs.fromBundle(getArguments()).getStakeHolder();

        addFragments();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mainActivity.setHeaderText(stakeHolder.getName());
        Caching.INSTANCE.setChosenStakeHolder(stakeHolder);
        viewModel.setSelectedStakeholder(stakeHolder);
        Caching.INSTANCE.openMicroAccount(stakeHolder.getId()); // set MicroAccount to caching
        viewModel.getListOfStakeHolderTrans().observe(getViewLifecycleOwner(), this::updateSummaryWithTransactions);
        viewModelAllTransactions.getStakeholdersList().observe(getViewLifecycleOwner(), this::updateSummaryWithStakeholder);
        setTopMenu();
    }

    private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                menu.findItem(R.id.menu_settings).setVisible(true);
                menu.findItem(R.id.menu_upload_future).setVisible(true);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                final int menu_settings = R.id.menu_settings;
                final int menu_future = R.id.menu_upload_future;

                switch (menuItem.getItemId()){
                    case menu_settings:
                        editStakeholder();
                        return true;
                    case menu_future:
                        loadMore();
                        return true;
                    default:
                        return false;
                }
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSummaryWithStakeholder(List<StakeHolder> s) {
        Optional<StakeHolder> matchingObject = s.stream().
                filter(p -> p.getId().equals(stakeHolder.getId())).
                findFirst();
        matchingObject.ifPresent(this::setStakeHolder);
        if(processedTransactionList.size()>0)updateSummaryWithTransactions(processedTransactionList);
        mainActivity.displayStakeHolderDetails(true, sumOfTransactions,initialReceivables,initialPayables,null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSummaryWithTransactions(List<ProcessedTransaction> transactionList) {
        processedTransactionList.clear();
        processedTransactionList.addAll(transactionList);
        int currentMonth = Timestamp.now().toDate().getMonth();
        NumberFormatter formatter = new NumberFormatter(0);
        formatter.setOriginalNumber(transactionList
                .stream()
                .filter(i -> !i.getIsDeleted())
                .filter(t -> !t.getType().contains(Caching.INSTANCE.TYPE_PENDING))
                .filter(t -> t.getType().contains(Caching.INSTANCE.TYPE_CASH))
                .filter(t -> t.getDueDate().toDate().getMonth() == currentMonth)
                .map(ProcessedTransaction::getTotalAmount)
                .reduce(0, Integer::sum)
        );
        sumOfTransactions = formatter.getFinalNumber();
        formatter.setOriginalNumber(stakeHolder.getSumOfReceivables());
        initialReceivables = formatter.getFinalNumber();
        formatter.setOriginalNumber(stakeHolder.getSumOfPayables());
        initialPayables = formatter.getFinalNumber();
        initialPayables =formatter.getFinalNumber();
        mainActivity.displayStakeHolderDetails(true,sumOfTransactions ,initialReceivables,initialPayables, null);
    }

    private void editStakeholder() {
        StakeholderViewPageHolderDirections.ActionStakeholderViewPagerHolderToNewMicroAccount action =
                StakeholderViewPageHolderDirections.actionStakeholderViewPagerHolderToNewMicroAccount(stakeHolder.getId());
        navController.navigate(action);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addFragments() {
        StakeDetailsList cash = new StakeDetailsList(stakeHolder, Caching.INSTANCE.TYPE_CASH);
        receivables = new StakeDetailsList(stakeHolder, Caching.INSTANCE.TYPE_RECEIVABLES);
        payables = new StakeDetailsList(stakeHolder, Caching.INSTANCE.TYPE_PAYABLES);
        PropertiesList property = new PropertiesList(stakeHolder, Caching.INSTANCE.PROPERTY_STAKEHOLDER);

        mAdapter.addFragment(cash);
        mAdapter.addFragment(receivables);
        mAdapter.addFragment(payables);
        mAdapter.addFragment(property);

        viewPager.setAdapter(mAdapter);
        new TabLayoutMediator(mainActivity.getTabLayout(), viewPager, (t, p) -> {
            switch (p) {
                case 0:
                    t.setText(R.string.transactions);
                    t.setIcon(R.drawable.ic_baseline_attach_money_24);
                    break;
                case 1:
                    t.setText(R.string.receivables);
                    t.setIcon(R.drawable.icon_contracts);
                    break;
                case 2:
                    t.setText(R.string.payables);
                    t.setIcon(R.drawable.icon_contracts);
                    break;
                case 3:
                    t.setText(R.string.properties);
                    t.setIcon(R.drawable.ic_baseline_business_24);
            }
        }).attach();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        mainActivity.displayStakeHolderDetails(true, sumOfTransactions,initialReceivables,initialPayables,null);
        mainActivity.displayToolBar(true);
        mainActivity.displayTabLayout(true);
        mainActivity.setFabImplement(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.resetFAB();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStop() {
        super.onStop();

        mainActivity.displayTabLayout(false);
        mainActivity.displayStakeHolderDetails(false);
        mainActivity.setFabImplement(null);

        viewPager.removeAllViews();
        mAdapter.deleteFragments();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadMore() {
        List<Fragment> fragments = getChildFragmentManager().getFragments();

        if ((viewPager.getCurrentItem() == 1 || viewPager.getCurrentItem() == 2) &&
                fragments.get(viewPager.getCurrentItem()) instanceof StakeDetailsList) {
            ((StakeDetailsList) fragments.get(viewPager.getCurrentItem())).loadMore();
        } else Toast.makeText(mainActivity, R.string.no_future, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPageChange() {
    }

    public void setStakeHolder(StakeHolder stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public void onTransactionNewClicked() {
        StakeholderViewPageHolderDirections.ActionMicroAccountViewPagerHolderToNewTransaction action =
                StakeholderViewPageHolderDirections.actionMicroAccountViewPagerHolderToNewTransaction();
        action.setIdStakeholder(stakeHolder.getId());
        navController.navigate(action);
    }

    @Override
    public void onScheduledTransactionNewClicked() {
        StakeholderViewPageHolderDirections.ActionStakeholderViewPagerHolderToTransactionFutureNew action =
                StakeholderViewPageHolderDirections.actionStakeholderViewPagerHolderToTransactionFutureNew();
        action.setIdStakeholder(stakeHolder.getId());
        navController.navigate(action);
    }
}