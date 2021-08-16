package be.kuleuven.elcontador10.background.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.viewModels.NewTransactionViewModel;

public class CategoriesRecViewAdapter extends RecyclerView.Adapter<CategoriesRecViewAdapter.ViewHolder> {

    private List<String> categories = new ArrayList<>();
    private final View viewFromHostingClass;
    private NewTransactionViewModel viewModel;

    public CategoriesRecViewAdapter(View viewFromHostingClass, NewTransactionViewModel viewModel) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_micros,parent,false);
        return new ViewHolder(viewParent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textNameCategory.setText(categories.get(position));
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
        private ImageButton imageIconCategory;
        private ConstraintLayout parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.recView_item_categories);
            textNameCategory = itemView.findViewById(R.id.textView_category_name);
            imageIconCategory = itemView.findViewById(R.id.imageButton_category_icon);
        }
    }

    public void setDefCategories(List <String> categoriesListInput) {
        categories.clear();
        this.categories.addAll(categoriesListInput);
        notifyDataSetChanged();

    }



}
