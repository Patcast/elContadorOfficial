package be.kuleuven.elcontador10.background.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;



public class ChooseStakeHolderRecViewAdapter extends RecyclerView.Adapter<ChooseStakeHolderRecViewAdapter.ViewHolder> implements Filterable {

    private List<StakeHolder> stakeholdersList = new ArrayList<>();
    private ArrayList<StakeHolder> stakeHoldersFull = new ArrayList<>();
    private final View viewFromHostingClass;
    private final ViewModel_NewTransaction viewModel;

    public ChooseStakeHolderRecViewAdapter(View viewFromHostingClass, ViewModel_NewTransaction viewModel) {
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
    public void onBindViewHolder(@NonNull  ChooseStakeHolderRecViewAdapter.ViewHolder holder, int position) {
        StakeHolder stakeHolder =stakeholdersList.get(position);
        holder.textName.setText(stakeHolder.getName());
        holder.textRole.setText(String.valueOf(stakeHolder.getRole()));
        holder.txtBalance.setVisibility(View.GONE);
        holder.parent.setOnClickListener(v -> {
               viewModel.selectStakeholder(stakeHolder);
               NavController navController = Navigation.findNavController(viewFromHostingClass);
               navController.navigate(R.id.action_chooseStakeHolderDialog_to_newTransaction);
            });
    }

    @Override
    public int getItemCount() {
            return stakeholdersList.size();
            }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textName;
        private final TextView textRole;
        private final TextView txtBalance;
        private final ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent_allMicros);
            textName = itemView.findViewById(R.id.text_Account_name_Micros);
            textRole = itemView.findViewById(R.id.text_micros_role);
            txtBalance = itemView.findViewById(R.id.text_micros_balance);

        }
    }

    public void setStakeholdersList(List <StakeHolder> stakeholdersListInput) {
        stakeHoldersFull.clear();
        stakeholdersList.clear();
        this.stakeholdersList.addAll(stakeholdersListInput);
        stakeHoldersFull.addAll(stakeholdersListInput);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<StakeHolder> collectionFiltered = new ArrayList<>();
                if (constraint.toString().isEmpty()) {
                    collectionFiltered.addAll(stakeHoldersFull);
                }
                else {
                    collectionFiltered
                            .addAll(stakeHoldersFull
                            .stream()
                            .filter(i -> i.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                            .collect(Collectors.toList()));
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = collectionFiltered;
                return filterResults;
            }
            @Override
            protected void publishResults (CharSequence constraint, FilterResults results){
                stakeholdersList.clear();
                stakeholdersList.addAll((Collection<? extends StakeHolder>) results.values);
                notifyDataSetChanged();
            }};




}
