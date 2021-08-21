package be.kuleuven.elcontador10.fragments.transactions.NewTransaction.Categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import be.kuleuven.elcontador10.R;

public class CategoriesBottomMenu extends BottomSheetDialogFragment {

    public interface CategoriesBottomSheetListener{
        void onAddCategoryClick();
        void onEditClick();
    }

    ConstraintLayout addNewCategoryButton;
    ConstraintLayout EditButton;
    CategoriesBottomSheetListener attachedListener;

    public CategoriesBottomMenu(CategoriesBottomSheetListener attachedListener) {
        this.attachedListener= attachedListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.bottom_menu_categories, container, false);
        addNewCategoryButton = view.findViewById(R.id.bs_categories_addNew);
        EditButton = view.findViewById(R.id.bs_categories_edit);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addNewCategoryButton.setOnClickListener(v -> attachedListener.onAddCategoryClick());
        EditButton.setOnClickListener(v -> attachedListener.onEditClick());
    }



}