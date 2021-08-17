package be.kuleuven.elcontador10.background.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import be.kuleuven.elcontador10.R;

import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.NewTransactionViewModel;

public class CategoriesRecViewAdapter extends RecyclerView.Adapter<CategoriesRecViewAdapter.ViewHolder> {

    private List<EmojiCategory> categories = new ArrayList<>();
    private final View viewFromHostingClass;
    private NewTransactionViewModel viewModel;

    public CategoriesRecViewAdapter(View viewFromHostingClass, NewTransactionViewModel viewModel) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_categories,parent,false);
        return new ViewHolder(viewParent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textNameCategory.setText(categories.get(position).getTitle());
        String icon = categories.get(position).getIcon();
        holder.imageIconCategory.setText(icon);
        holder.parent.setOnClickListener(v -> {
            viewModel.selectCategory(categories.get(position));
            NavController navController = Navigation.findNavController(viewFromHostingClass);
            navController.popBackStack();
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textNameCategory;
        private TextView imageIconCategory;
        private ConstraintLayout parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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



}
