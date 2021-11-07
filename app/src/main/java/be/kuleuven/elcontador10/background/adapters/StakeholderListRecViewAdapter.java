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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.fragments.stakeholders.common.AllStakeholders.StakeholdersListDirections;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;



public class StakeholderListRecViewAdapter extends RecyclerView.Adapter<StakeholderListRecViewAdapter.ViewHolder> implements Filterable {

    private final List<StakeHolder> stakeholdersList = new ArrayList<>();
    private final ArrayList<StakeHolder> stakeHoldersFull = new ArrayList<>();
    private final View viewFromHostingClass;
    private final ViewModel_NewTransaction viewModel;

    public StakeholderListRecViewAdapter(View viewFromHostingClass, ViewModel_NewTransaction viewModel) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_micros,parent,false);
        return new StakeholderListRecViewAdapter.ViewHolder(viewParent);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        StakeHolder stake = stakeholdersList.get(position);
        holder.textName.setText(stake.getName());
        holder.textRole.setText(String.valueOf(stake.getRole()));
        NumberFormatter formatter = new NumberFormatter(stake.getBalance());
        String formatted = formatter.getFinalNumber();
        holder.textBalance.setText(formatted);
        holder.parent.setOnClickListener(v -> {
                    viewModel.selectStakeholder(stakeholdersList.get(position));
                    NavController navController = Navigation.findNavController(viewFromHostingClass);
                    StakeholdersListDirections.ActionStakeholdersToStakeholder action  = StakeholdersListDirections.actionStakeholdersToStakeholder(stakeholdersList.get(position));
                    navController.navigate(action);
                }
        );
    }


    @Override
    public int getItemCount() {
        return stakeholdersList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textName;
        private TextView textRole;
        private TextView textBalance;
        private ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent_allMicros);
            textName = itemView.findViewById(R.id.text_Account_name_Micros);
            textRole = itemView.findViewById(R.id.text_micros_role);
            textBalance = itemView.findViewById(R.id.text_micros_balance);
        }
    }

    public void setStakeListOnAdapter(List <StakeHolder> stakeholdersListInput) {
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
