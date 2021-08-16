package be.kuleuven.elcontador10.fragments.transactions;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.CategoriesRecViewAdapter;
import be.kuleuven.elcontador10.background.adapters.StakeHolderRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.TransactionType;
import be.kuleuven.elcontador10.background.viewModels.NewTransactionViewModel;

public class ChooseCategory extends Fragment implements Caching.StaticDataObserver {
    private RecyclerView recyclerCategories;

    private CategoriesRecViewAdapter adapter;
    private List<String> defCategories = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_stake_holder, container, false);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.choose_category));
        recyclerCategories = view.findViewById(R.id.recyclerViewChooseStake);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this.getContext()));
        NewTransactionViewModel viewModel = new ViewModelProvider(requireActivity()).get(NewTransactionViewModel.class);
        adapter = new CategoriesRecViewAdapter(view,viewModel);
        Caching.INSTANCE.attachStaticDataObservers(this);
        if(defCategories.size()>0) adapter.setDefCategories(defCategories);
        recyclerCategories.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Caching.INSTANCE.attachStaticDataObservers(this);
        if(defCategories.size()>0) adapter.setDefCategories(defCategories);
        recyclerCategories.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachStaticDataObserver(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void notifyStaticDataObserver(List<TransactionType> transTypes, List<String> roles) {
        this.defCategories.clear();

        this.defCategories.addAll( transTypes.stream()
                .map(TransactionType::getSubCategory)
                .collect(Collectors.toList()));
        adapter.setDefCategories(defCategories);
    }
}