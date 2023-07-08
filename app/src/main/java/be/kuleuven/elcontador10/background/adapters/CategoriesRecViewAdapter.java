package be.kuleuven.elcontador10.background.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.R;

import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.fragments.transactions.Categories.CategorySettings;
import be.kuleuven.elcontador10.fragments.transactions.Categories.ChooseCategoryDirections;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class CategoriesRecViewAdapter extends RecyclerView.Adapter<CategoriesRecViewAdapter.ViewHolder> {

    private final List<EmojiCategory> categories = new ArrayList<>();
    private final View viewFromHostingClass;
    private final ViewModel_NewTransaction viewModel;
    private final Fragment hostFragment;
    private final boolean isOwner;

    public CategoriesRecViewAdapter(View viewFromHostingClass, ViewModel_NewTransaction viewModel, Fragment hostFragment, boolean isOwner) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.viewModel = viewModel;
        this.hostFragment = hostFragment;
        this.isOwner = isOwner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_categories,parent,false);
        return new ViewHolder(viewParent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmojiCategory category = categories.get(position);

        holder.textNameCategory.setText(category.getTitle());

        StringBuilder desc = new StringBuilder();

        Context context = hostFragment.getContext();
        assert (context != null);

        if (category.isCashIn()) desc.append(context.getString(R.string.in));
        else desc.append(context.getString(R.string.out));

        category.getType().forEach(string -> {
            if (string != null) {
                desc.append(" - ");
                if (string.equals(Caching.INSTANCE.TYPE_CASH))
                    desc.append(context.getString(R.string.cash));
                else if (string.equals(Caching.INSTANCE.TYPE_PAYABLES))
                    desc.append(context.getString(R.string.payables));
                else if (string.equals(Caching.INSTANCE.TYPE_RECEIVABLES))
                    desc.append(context.getString(R.string.receivables));
            }
        });

        holder.textDescriptionCategory.setText(desc.toString());

        String icon = category.getIcon();
        holder.imageIconCategory.setText(icon);

        holder.parent.setOnClickListener(v -> {
            viewModel.selectCategory(category);
            NavController navController = Navigation.findNavController(viewFromHostingClass);
            navController.popBackStack();
        });

        if (isOwner) {
            holder.editButton.setOnClickListener(v -> {
                NavController nav = Navigation.findNavController(viewFromHostingClass);
                ChooseCategoryDirections.ActionChooseCategoryToCategorySettings act = ChooseCategoryDirections.actionChooseCategoryToCategorySettings(category.getId());
                nav.navigate(act);
            });
        }
        else {
            holder.editButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textNameCategory;
        private final TextView textDescriptionCategory;
        private final TextView imageIconCategory;
        private final ConstraintLayout parent;
        private final ImageButton editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editButton = itemView.findViewById(R.id.btn_edit_category);
            parent = itemView.findViewById(R.id.recView_item_categories);
            textNameCategory = itemView.findViewById(R.id.textView_category_name);
            textDescriptionCategory = itemView.findViewById(R.id.textView_category_description);
            imageIconCategory = itemView.findViewById(R.id.imageButton_category_icon);
        }
    }

    public void setDefCategories(List <EmojiCategory> categoriesListInput) {
        categories.clear();
        this.categories.addAll(categoriesListInput);
        notifyDataSetChanged();
    }

    public void setEditMode() {
        notifyDataSetChanged();
    }
}
