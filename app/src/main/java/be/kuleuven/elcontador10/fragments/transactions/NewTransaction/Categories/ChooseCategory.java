package be.kuleuven.elcontador10.fragments.transactions.NewTransaction.Categories;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
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


import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.ViewModelCategory;
import be.kuleuven.elcontador10.background.adapters.CategoriesRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class ChooseCategory extends Fragment implements Caching.CategoriesObserver, MainActivity.TopMenuHandler, CategoryDialog.DialogCategoriesListener {
//Todo: Delete BottomSheet class and layout
    private ConstraintLayout noCategoryItem,addCustomCat;
    private ViewModelCategory viewModel;
    private RecyclerView recyclerCategories_custom;
    private CategoriesRecViewAdapter adapter_custom;
    private final List<EmojiCategory> customCategories = new ArrayList<>();
    MainActivity mainActivity;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_category, container, false);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.choose_category));
        mainActivity.setCurrentMenuClicker(this);
        noCategoryItem = view.findViewById(R.id.choose_noCat);
        addCustomCat = view.findViewById(R.id.layout_addCategory);

        boolean isNewTransaction = ChooseCategoryArgs.fromBundle(getArguments()).getNewTransaction();

        if (isNewTransaction)
            viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
        else
            viewModel = new ViewModelProvider(requireActivity()).get(ViewModelCategory.class);
        startDefaultRecViews(view);
        startCustomRecycler(view);
        return view;

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startDefaultRecViews(View view){
        RecyclerView recyclerCategories_default = view.findViewById(R.id.recView_categories_default);
        recyclerCategories_default.setLayoutManager(new LinearLayoutManager(this.getContext()));
        CategoriesRecViewAdapter adapter_default = new CategoriesRecViewAdapter(view, viewModel, this);
        recyclerCategories_default.setAdapter(adapter_default);
        adapter_default.setDefCategories(Caching.INSTANCE.getDefaultCategories());
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startCustomRecycler(View view) {
        recyclerCategories_custom = view.findViewById(R.id.recView_categories_custom);
        recyclerCategories_custom.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter_custom = new CategoriesRecViewAdapter(view,viewModel,this);
        Caching.INSTANCE.attachCatObserver(this);
        if(customCategories.size()>0) adapter_custom.setDefCategories(customCategories);
        recyclerCategories_custom.setAdapter(adapter_custom);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        noCategoryItem.setOnClickListener(v->closeWithNoCategory(navController));
        addCustomCat.setOnClickListener(v->startDialogForAdding());
    }
    private void startDialogForAdding() {
        CategoryDialog dialog =new CategoryDialog();
        dialog.show(getParentFragmentManager(),"Category Dialog");
    }

    private void closeWithNoCategory(NavController navController ) {
        viewModel.resetCategory();
        navController.popBackStack();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_edit,true);
        Caching.INSTANCE.attachCatObserver(this);
        if(customCategories.size()>0) adapter_custom.setDefCategories(customCategories);
        recyclerCategories_custom.setAdapter(adapter_custom);
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_edit,false);
        Caching.INSTANCE.deAttachCatObserver(this);
    }


    @Override
    public void notifyCatObserver(List<EmojiCategory> customCategoriesInput) {
        customCategories.clear();
        customCategories.addAll(customCategoriesInput);
        adapter_custom.setDefCategories(customCategories);
    }





    @Override
    public void closeDialog() {
        adapter_custom.setEditMode(false);
    }

//////              MenuHandlerSettings
    @Override
    public void onBottomSheetClick() {

    }

    @Override
    public void onEditingClick() {
        adapter_custom.setEditMode(true);
    }
    @Override
    public void onDeleteClick() {

    }


    @Override
    public void onAddClick() {

    }

    @Override
    public void onSearchClick(SearchView searchView) {

    }

    @Override
    public void onFilterClick() {

    }

}