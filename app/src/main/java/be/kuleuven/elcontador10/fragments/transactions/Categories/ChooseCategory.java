package be.kuleuven.elcontador10.fragments.transactions.Categories;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.background.adapters.CategoriesRecViewAdapter;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class ChooseCategory extends Fragment implements Caching.CategoriesObserver {
//Todo: Delete BottomSheet class and layout
    private ConstraintLayout addCustomCat;
    private ViewModel_NewTransaction viewModel;
    private RecyclerView recyclerCategories_custom;
    private CategoriesRecViewAdapter adapter_custom;
    private final List<EmojiCategory> customCategories = new ArrayList<>();
    MainActivity mainActivity;
    View view;
    private boolean isOwner;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_category, container, false);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.choose_category));
        addCustomCat = view.findViewById(R.id.layout_addCategory);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
        startCustomRecycler(view);

        isOwner = mainActivity.returnSavedLoggedEmail().equals(Caching.INSTANCE.getChosenAccount().getOwner());

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startCustomRecycler(View view) {
        recyclerCategories_custom = view.findViewById(R.id.recView_categories_custom);
        recyclerCategories_custom.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter_custom = new CategoriesRecViewAdapter(view, viewModel, this, isOwner);
        Caching.INSTANCE.attachCatObserver(this);
        if(customCategories.size()>0) adapter_custom.setDefCategories(customCategories);
        recyclerCategories_custom.setAdapter(adapter_custom);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // only allow owners to add
        if (isOwner)
            addCustomCat.setOnClickListener(v->startDialogForAdding());
        else
            addCustomCat.setVisibility(View.GONE);
    }
    private void startDialogForAdding() {
        NavController nav = Navigation.findNavController(view);
        ChooseCategoryDirections.ActionChooseCategoryToCategorySettings act = ChooseCategoryDirections.actionChooseCategoryToCategorySettings(null);
        nav.navigate(act);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        Caching.INSTANCE.attachCatObserver(this);
        if(customCategories.size()>0) adapter_custom.setDefCategories(customCategories);
        recyclerCategories_custom.setAdapter(adapter_custom);
    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachCatObserver(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void notifyCatObserver(List<EmojiCategory> customCategoriesInput) {
        customCategories.clear();
        customCategories.addAll(customCategoriesInput.stream().filter(c->!c.getIsDeleted()).collect(Collectors.toList()));
        adapter_custom.setDefCategories(customCategories);
    }

}