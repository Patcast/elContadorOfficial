package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

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
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.TransactionType;

public class ChooseCategory extends Fragment implements Caching.DefCategoriesObserver {
    private RecyclerView recyclerCategories;

    private CategoriesRecViewAdapter adapter;
    private List<EmojiCategory> defCategories = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_category, container, false);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.choose_category));
        recyclerCategories = view.findViewById(R.id.recView_categories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this.getContext()));
        NewTransactionViewModel viewModel = new ViewModelProvider(requireActivity()).get(NewTransactionViewModel.class);
        adapter = new CategoriesRecViewAdapter(view,viewModel);
        Caching.INSTANCE.attachDefCatObserver(this);
        if(defCategories.size()>0) adapter.setDefCategories(defCategories);
        recyclerCategories.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        if(!(Caching.INSTANCE.getDefCatObservers().contains(this))) Caching.INSTANCE.attachDefCatObserver(this);
        if(defCategories.size()>0) adapter.setDefCategories(defCategories);
        recyclerCategories.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachDefCatObserver(this);
    }

    @Override
    public void notifyDefCatObserver(List<EmojiCategory> categoriesInput) {
        defCategories.clear();
        defCategories.addAll(categoriesInput);
        adapter.setDefCategories(defCategories);
    }
}