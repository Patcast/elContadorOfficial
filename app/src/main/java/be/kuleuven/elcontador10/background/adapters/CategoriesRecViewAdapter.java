package be.kuleuven.elcontador10.background.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;


import be.kuleuven.elcontador10.R;

import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.CategoryDialog;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.NewTransactionViewModel;

public class CategoriesRecViewAdapter extends RecyclerView.Adapter<CategoriesRecViewAdapter.ViewHolder> {

    private final List<EmojiCategory> categories = new ArrayList<>();
    private final View viewFromHostingClass;
    private final NewTransactionViewModel viewModel;
    private boolean editMode;
    private Fragment hostFragment;

    public CategoriesRecViewAdapter(View viewFromHostingClass, NewTransactionViewModel viewModel,Fragment hostFragment) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.viewModel = viewModel;
        this.hostFragment = hostFragment;
        editMode = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_categories,parent,false);
        return new ViewHolder(viewParent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (editMode) holder.editingSwitch.setVisibility(View.VISIBLE);
        else holder.editingSwitch.setVisibility(View.GONE);
        holder.textNameCategory.setText(categories.get(position).getTitle());
        String icon = categories.get(position).getIcon();
        holder.imageIconCategory.setText(icon);
        holder.parent.setOnClickListener(v -> {
            viewModel.selectCategory(categories.get(position));
            NavController navController = Navigation.findNavController(viewFromHostingClass);
            navController.popBackStack();
        });
        holder.editingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                CategoryDialog dialog =new CategoryDialog(categories.get(position));
                dialog.setListener((CategoryDialog.DialogCategoriesListener) hostFragment);
                dialog.show(hostFragment.getParentFragmentManager(),"Category Dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private final SwitchMaterial editingSwitch;
        private final TextView textNameCategory;
        private final TextView imageIconCategory;
        private final ConstraintLayout parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editingSwitch = itemView.findViewById(R.id.switch_category);
            parent = itemView.findViewById(R.id.recView_item_categories);
            textNameCategory = itemView.findViewById(R.id.textView_category_name);
            imageIconCategory = itemView.findViewById(R.id.imageButton_category_icon);
        }
    }

    public void setDefCategories(List <EmojiCategory> categoriesListInput) {
        categories.clear();
        this.categories.addAll(categoriesListInput);
        notifyDataSetChanged();

    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        notifyDataSetChanged();
    }
}
