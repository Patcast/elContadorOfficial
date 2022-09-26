package be.kuleuven.elcontador10.background.adapters;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.fragments.property.PropertiesListDirections;
import be.kuleuven.elcontador10.fragments.stakeholders.StakeholderViewPageHolderDirections;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class PropertiesListRecViewAdapter extends RecyclerView.Adapter<PropertiesListRecViewAdapter.ViewHolder> implements Filterable {
    private final List<Property> propertyList = new ArrayList<>();
    private final List<Property> propertyListFull = new ArrayList<>();
    private final View viewFromHostingClass;
    private final String prevTAG;
    private NavController navController;
    private final ViewModel_NewTransaction viewModel;

    public PropertiesListRecViewAdapter(View viewFromHostingClass, String TAG, ViewModel_NewTransaction viewModel) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.viewModel= viewModel;
        prevTAG = TAG;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_micros,parent,false);
        navController = Navigation.findNavController(viewFromHostingClass);
        return new PropertiesListRecViewAdapter.ViewHolder(viewParent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property property = propertyList.get(position);
        holder.textName.setText(property.getName());

        long receivables = property.getSumOfReceivables();
        if (receivables != 0) {
            NumberFormatter formatter = new NumberFormatter(receivables);
            String formatted = formatter.getFinalNumber();
            holder.textReceivables.setText(formatted);
        }
        else holder.textReceivables.setVisibility(View.GONE);

        long payables = property.getSumOfPayables();
        if (payables != 0) {
            NumberFormatter formatter = new NumberFormatter(payables);
            String formatted = formatter.getFinalNumber();
            holder.textViewPayables.setText(formatted);
        }
        else holder.textViewPayables.setVisibility(View.GONE);


        //NAVIGATION
        if (prevTAG == null)
            holder.parent.setOnClickListener(view -> {
                PropertiesListDirections.ActionPropertiesListToPropertyViewPageHolder action =
                        PropertiesListDirections.actionPropertiesListToPropertyViewPageHolder(property);
                navController.navigate(action);
            });
        else if (prevTAG.equals(Caching.INSTANCE.PROPERTY_STAKEHOLDER))
            holder.parent.setOnClickListener(view -> {
                StakeholderViewPageHolderDirections.ActionStakeholderViewPagerHolderToPropertyViewPageHolder action =
                        StakeholderViewPageHolderDirections.actionStakeholderViewPagerHolderToPropertyViewPageHolder(property);
                navController.navigate(action);
            });
        else {
            holder.parent.setOnClickListener(v -> {
                viewModel.selectProperty(property);
                navController.popBackStack();
            });
        }

        if (property.getStakeholder() == null)
            holder.textRole.setText(R.string.vacant);
        else
            holder.textRole.setText(Caching.INSTANCE.getStakeholderName(property.getStakeholder()));
    }
    @Override
    public int getItemCount() {
        return propertyList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textName, textReceivables,textViewPayables, textRole;

        private final ConstraintLayout parent;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                parent = itemView.findViewById(R.id.parent_allMicros);
                textName = itemView.findViewById(R.id.text_Account_name_Micros);
                textName.setSelected(true);
                textRole = itemView.findViewById(R.id.text_micros_role);
                textReceivables = itemView.findViewById(R.id.text_micros_balance);
                textReceivables.setSelected(true);
                textViewPayables = itemView.findViewById(R.id.textViewPayables);
                textViewPayables.setSelected(true);
            }
    }
        /////------------

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setPropertyListOnAdapter(@Nullable List <Property> propertiesListInput) {
        propertyListFull.clear();
        propertyList.clear();
        if (propertiesListInput != null) {
            if (prevTAG != null && prevTAG.equals(Caching.INSTANCE.PROPERTY_NEW_T)) {
                this.propertyList.addAll(propertiesListInput
                        .stream()
                        .filter(property -> !property.isDeleted())
                        .filter(property -> property.getStakeholder() != null)
                        .sorted(Comparator.comparing(Property::getName))
                        .collect(Collectors.toList()));
                this.propertyListFull.addAll(propertiesListInput
                        .stream()
                        .filter(property -> !property.isDeleted())
                        .filter(property -> property.getStakeholder() != null)
                        .sorted(Comparator.comparing(Property::getName))
                        .collect(Collectors.toList()));
            } else {
                this.propertyList.addAll(propertiesListInput
                        .stream()
                        .filter(property -> !property.isDeleted())
                        .sorted(Comparator.comparing(Property::getName))
                        .collect(Collectors.toList()));
                this.propertyListFull.addAll(propertiesListInput
                        .stream()
                        .filter(property -> !property.isDeleted())
                        .sorted(Comparator.comparing(Property::getName))
                        .collect(Collectors.toList()));
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
            return filter;
        }

    private final PropertyFilter filter = new PropertyFilter();

    public class PropertyFilter extends Filter {
        String vacant;

        public void setVacant(String vacant) {
            this.vacant = vacant;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Property> collectionFiltered = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                collectionFiltered.addAll(propertyListFull);
            }
            else {
                String search = constraint.toString().toLowerCase();


                collectionFiltered.addAll(propertyListFull
                        .stream()
                        .filter(i -> i.getName().toLowerCase().contains(search) ||
                                (i.getStakeholder() != null && Caching.INSTANCE.getStakeholderName(i.getStakeholder()).toLowerCase().contains(search)) ||
                                (i.getStakeholder() == null && vacant.toLowerCase().contains(search))
                        )
                        .collect(Collectors.toList())
                );
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = collectionFiltered;
            return filterResults;
        }

        @Override
        protected void publishResults (CharSequence constraint, FilterResults results) {
            propertyList.clear();
            if (results.values != null)
                propertyList.addAll((Collection<? extends Property>) results.values);
            notifyDataSetChanged();
        }
    };
}
