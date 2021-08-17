package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

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
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.CategoriesRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.TransactionType;
import be.kuleuven.elcontador10.fragments.accounts.AccountsBottomMenu;

public class ChooseCategory extends Fragment implements Caching.CategoriesObserver, MainActivity.MenuClicker, CategoriesBottomMenu.CategoriesBottomSheetListener {
    private CategoriesBottomMenu bottomSheet;
    private ConstraintLayout noCategoryItem;
    private NewTransactionViewModel viewModel;
    private RecyclerView recyclerCategories_default;
    private RecyclerView recyclerCategories_custom;
    private CategoriesRecViewAdapter adapter_custom;
    private CategoriesRecViewAdapter adapter_default;
    private final List<EmojiCategory> defCategories = new ArrayList<>();
    private final List<EmojiCategory> customCategories = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_category, container, false);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.choose_category));
        mainActivity.displayTopMenu(true);
        mainActivity.setCurrentMenuClicker(this);
        noCategoryItem = view.findViewById(R.id.choose_noCat);
        Caching.INSTANCE.attachCatObserver(this);
        startRecViews(view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        noCategoryItem.setOnClickListener(v->closeWithNoCategory(navController));
    }
    private void startRecViews(View view){
        recyclerCategories_custom = view.findViewById(R.id.recView_categories_custom);
        recyclerCategories_custom.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerCategories_default = view.findViewById(R.id.recView_categories_default);
        recyclerCategories_default.setLayoutManager(new LinearLayoutManager(this.getContext()));
        viewModel = new ViewModelProvider(requireActivity()).get(NewTransactionViewModel.class);
        adapter_default = new CategoriesRecViewAdapter(view,viewModel);
        adapter_custom = new CategoriesRecViewAdapter(view,viewModel);
        if(defCategories.size()>0){
            adapter_default.setDefCategories(defCategories);
            adapter_custom.setDefCategories(customCategories);
        }
        recyclerCategories_default.setAdapter(adapter_default);
        recyclerCategories_custom.setAdapter(adapter_custom);
    }

    private void closeWithNoCategory(NavController navController ) {
        viewModel.resetCategory();
        navController.popBackStack();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        if(!(Caching.INSTANCE.getDefCatObservers().contains(this))) Caching.INSTANCE.attachCatObserver(this);
        if(defCategories.size()>0){
            adapter_default.setDefCategories(defCategories);
            adapter_custom.setDefCategories(defCategories);
        }
        recyclerCategories_default.setAdapter(adapter_default);
        recyclerCategories_custom.setAdapter(adapter_custom);
    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachCatObserver(this);
    }


    @Override
    public void notifyCatObserver(List<EmojiCategory> defCategoriesInput, List<EmojiCategory> customCategoriesInput) {
        defCategories.clear();
        defCategories.addAll(defCategoriesInput);

        customCategories.clear();
        customCategories.addAll(customCategoriesInput);
        adapter_default.setDefCategories(defCategories);
        adapter_custom.setDefCategories(customCategories);
    }

    @Override
    public void onBottomSheetClick() {
        bottomSheet = new CategoriesBottomMenu(this);
        bottomSheet.show(getParentFragmentManager(),"CategoriesBottomSheet");

    }

    @Override
    public void onAddCategoryClick() {
        bottomSheet.dismiss();
        CategoryDialog dialog =new CategoryDialog();
        dialog.show(getParentFragmentManager(),"Category Dialog");
    }

    @Override
    public void onEditClick() {
        bottomSheet.dismiss();
        CategoryDialog dialog =new CategoryDialog();
        dialog.show(getParentFragmentManager(),"Category Dialog");
    }
}