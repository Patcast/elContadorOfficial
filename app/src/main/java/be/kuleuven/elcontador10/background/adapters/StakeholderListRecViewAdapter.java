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
import be.kuleuven.elcontador10.fragments.stakeholders.StakeholdersListDirections;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;


public class StakeholderListRecViewAdapter extends RecyclerView.Adapter<StakeholderListRecViewAdapter.ViewHolder> implements Filterable {

    private final List<StakeHolder> stakeholdersList = new ArrayList<>();
    private final ArrayList<StakeHolder> stakeHoldersFull = new ArrayList<>();
    private final ViewModel_NewTransaction viewModel;
    private final View viewFromHostingClass;
    private final String prevFrag;

    public StakeholderListRecViewAdapter(ViewModel_NewTransaction viewModel, View viewFromHostingClass, String prevFrag) {
        this.viewModel = viewModel;
        this.viewFromHostingClass = viewFromHostingClass;
        this.prevFrag = prevFrag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_micros,parent,false);
        return new StakeholderListRecViewAdapter.ViewHolder(viewParent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        StakeHolder stake = stakeholdersList.get(position);
        holder.textName.setText(stake.getName());

        long balance = stake.getSumOfReceivables() - stake.getSumOfPayables();
        if (balance != 0) {
            holder.textBalance.setVisibility(View.VISIBLE);
            NumberFormatter formatter = new NumberFormatter(balance);
            String formatted = formatter.getFinalNumber();
            holder.textBalance.setText(formatted);
        } else
            holder.textBalance.setVisibility(View.GONE);

        if (stake.getRole() != null && !stake.getRole().equals("")) {
            holder.textRole.setVisibility(View.VISIBLE);
            holder.textRole.setText(stake.getRole());
        } else
            holder.textRole.setVisibility(View.GONE);

        holder.parent.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(viewFromHostingClass);

            if(prevFrag == null){
                        StakeholdersListDirections.ActionStakeholdersToStakeholder action  = StakeholdersListDirections.actionStakeholdersToStakeholder(stakeholdersList.get(position));
                        navController.navigate(action);
            } else {
                viewModel.selectStakeholder(stake);
                navController.popBackStack();
            }
        });
    }


    @Override
    public int getItemCount() {
        return stakeholdersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textName, textBalance, textRole;
        private final ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent_allMicros);
            textName = itemView.findViewById(R.id.text_Account_name_Micros);
            textName.setSelected(true);
            textRole = itemView.findViewById(R.id.text_micros_role);
            textBalance = itemView.findViewById(R.id.text_micros_balance);
            textBalance.setSelected(true);
        }
    }
    /////------------

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setStakeListOnAdapter(List <StakeHolder> stakeholdersListInput) {
        stakeHoldersFull.clear();
        stakeholdersList.clear();
        List<StakeHolder> stakeHolders = stakeholdersListInput.stream()
                .filter(e -> !e.isDeleted())
                .collect(Collectors.toList());
        this.stakeholdersList.addAll(stakeHolders);
        stakeHoldersFull.addAll(stakeHolders);
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
                collectionFiltered.addAll(
                        stakeHoldersFull
                                .stream()
                                .filter(i -> i.getName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                        (i.getRole() != null &&
                                                i.getRole().toLowerCase().contains(constraint.toString().toLowerCase())))
                                .collect(Collectors.toList())
                );
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = collectionFiltered;
            return filterResults;
        }

        @Override
        protected void publishResults (CharSequence constraint, FilterResults results){
            stakeholdersList.clear();
            if (results.values != null)
                stakeholdersList.addAll((Collection<? extends StakeHolder>) results.values);
            notifyDataSetChanged();
        }};
}
