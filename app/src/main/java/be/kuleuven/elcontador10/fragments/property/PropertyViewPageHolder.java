package be.kuleuven.elcontador10.fragments.property;

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
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.ViewPagerAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.tools.ZoomOutPageTransformer;
import be.kuleuven.elcontador10.fragments.stakeholders.StakeDetailsList;
import be.kuleuven.elcontador10.fragments.stakeholders.StakeholderViewModel;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.ViewModel_AllTransactions;

public class PropertyViewPageHolder extends Fragment implements ZoomOutPageTransformer.PageChangeListener {
        private ViewPagerAdapter mAdapter;
        private ViewPager2 viewPager;
        private MainActivity mainActivity;
        private PropertyListViewModel viewModel;
        Property property;
        String sumOfTransactions, initialReceivables,initialPayables;
        List<ProcessedTransaction> processedTransactionList = new ArrayList<>();
        private ViewModel_AllTransactions viewModelAllTransactions;



        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mainActivity = (MainActivity) requireActivity();
            mainActivity.displayToolBar(true);
            property = PropertyViewPageHolderArgs.fromBundle(getArguments()).getProperty();

        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_micro_account_view_holder, container, false);
            viewPager = view.findViewById(R.id.viewPagerHolder);
            mAdapter = new ViewPagerAdapter(mainActivity.getSupportFragmentManager(), getLifecycle());
            viewPager.setPageTransformer(new ZoomOutPageTransformer(this));
            viewModel = new ViewModelProvider(requireActivity()).get(PropertyListViewModel.class);
            viewModelAllTransactions = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);

            return view;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mainActivity.setHeaderText(property.getName());
            addFragments();

            viewModel.getListOfProperties().observe(getViewLifecycleOwner(), this::updateSummaryWithProperty);
            viewModelAllTransactions.getMonthlyListOfProcessedTransactions().observe(getViewLifecycleOwner(), this::updateSummaryWithTransactions);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void updateSummaryWithProperty(List<Property> s) {
            Optional<Property> matchingObject = s.stream().
                    filter(p -> p.getId().equals(property.getId())).
                    findFirst();
            matchingObject.ifPresent(this::setProperty);
            if(processedTransactionList.size()>0)updateSummaryWithTransactions(processedTransactionList);
        }



        @RequiresApi(api = Build.VERSION_CODES.N)
        private void updateSummaryWithTransactions(List<ProcessedTransaction> transactionList) {
            processedTransactionList.clear();
            processedTransactionList.addAll(transactionList);
            int currentMonth = Timestamp.now().toDate().getMonth();
            NumberFormatter formatter = new NumberFormatter(0);
            formatter.setOriginalNumber(transactionList
                    .stream()
                    .filter(t->t.getId().equals(property.getId()))
                    .filter(i-> !i.getIsDeleted())
                    .filter(t->!t.getType().contains(Caching.INSTANCE.TYPE_PENDING))
                    .filter(t->t.getType().contains(Caching.INSTANCE.TYPE_CASH))
                    .filter(t->t.getDueDate().toDate().getMonth()==currentMonth)
                    .map(ProcessedTransaction::getTotalAmount)
                    .reduce(0, Integer::sum)
            );
            sumOfTransactions = formatter.getFinalNumber();
            formatter.setOriginalNumber(property.getSumOfReceivables());
            initialReceivables = formatter.getFinalNumber();
            formatter.setOriginalNumber(property.getSumOfPayables());
            initialPayables = formatter.getFinalNumber();
            initialPayables =formatter.getFinalNumber();
            mainActivity.displayStakeHolderDetails(true,sumOfTransactions ,initialReceivables,initialPayables);
        }



        private void addFragments() {
            mAdapter.addFragment(new StakeDetailsList(null,property,Caching.INSTANCE.TYPE_CASH));
            mAdapter.addFragment(new StakeDetailsList(null,property,Caching.INSTANCE.TYPE_RECEIVABLES));
            mAdapter.addFragment(new StakeDetailsList(null,property,Caching.INSTANCE.TYPE_PAYABLES));
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
                }
            }).attach();
        }

        @Override
        public void onStart() {
            super.onStart();
            mainActivity.displayStakeHolderDetails(true, sumOfTransactions,initialReceivables,initialPayables);
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
        }

        public void setProperty(Property property) {
            this.property = property;
        }
}

