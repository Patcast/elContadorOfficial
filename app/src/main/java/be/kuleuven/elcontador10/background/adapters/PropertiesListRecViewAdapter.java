package be.kuleuven.elcontador10.background.adapters;

import android.os.Build;
import android.util.Log;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class PropertiesListRecViewAdapter extends RecyclerView.Adapter<PropertiesListRecViewAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "RV Properties List:" ;
    private final List<Property> propertyList = new ArrayList<>();
    private final List<Property> propertyListFull = new ArrayList<>();
    private final View viewFromHostingClass;
    private String prevTAG;
    NavController navController;
    private ViewModel_NewTransaction viewModel;

    public PropertiesListRecViewAdapter(View viewFromHostingClass, String TAG, ViewModel_NewTransaction viewModel) {
            this.viewFromHostingClass = viewFromHostingClass;
            this.viewModel= viewModel;
            prevTAG = TAG;
    }

    public PropertiesListRecViewAdapter(View viewFromHostingClass) {
        this.viewFromHostingClass = viewFromHostingClass;
        prevTAG=null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_micros,parent,false);
        navController = Navigation.findNavController(viewFromHostingClass);
        return new PropertiesListRecViewAdapter.ViewHolder(viewParent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property property = propertyList.get(position);
        holder.textName.setText(property.getName());
        holder.textInfo.setText("");

        if(prevTAG==null){
            long balance = property.calculatePropertySummary();
            if (balance != 0) {
                holder.textBalance.setVisibility(View.VISIBLE);
                NumberFormatter formatter = new NumberFormatter(balance);
                String formatted = formatter.getFinalNumber();
                holder.textBalance.setText(formatted);
                if (balance > 0) holder.textBalance.setTextColor(viewFromHostingClass.getContext().getColor(R.color.transaction_processed_positive));
                else holder.textBalance.setTextColor(viewFromHostingClass.getContext().getColor(R.color.rec_view_negative_amount));

            }
            else holder.textBalance.setVisibility(View.GONE); // hide 0 balance

        }
        else  {
            Log.w(TAG,"Moving to transaction new");
            holder.textBalance.setVisibility(View.GONE);
            holder.parent.setOnClickListener(v -> {
                viewModel.selectProperty(property);
                navController.popBackStack();
            }
            );
        }

    }
    @Override
    public int getItemCount() {
        return propertyList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView textName;
            private TextView textInfo;
            private TextView textBalance;
            private ConstraintLayout parent;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                parent = itemView.findViewById(R.id.parent_allMicros);
                textName = itemView.findViewById(R.id.text_Account_name_Micros);
                textInfo = itemView.findViewById(R.id.text_micros_role);
                textBalance = itemView.findViewById(R.id.text_micros_balance);
            }
    }
        /////------------

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setPropertyListOnAdapter(List <Property> propertiesListInput) {
            propertyListFull.clear();
            propertyList.clear();
            this.propertyList.addAll(propertiesListInput.stream().sorted(Comparator.comparing(Property::getName)).collect(Collectors.toList()));
            propertyListFull.addAll(propertiesListInput.stream().sorted(Comparator.comparing(Property::getName)).collect(Collectors.toList()));

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
                List<Property> collectionFiltered = new ArrayList<>();
                if (constraint.toString().isEmpty()) {
                    collectionFiltered.addAll(propertyListFull);
                }
                else {
                    collectionFiltered
                            .addAll(propertyListFull
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
                propertyList.clear();
                propertyList.addAll((Collection<? extends Property>) results.values);
                notifyDataSetChanged();
            }};
}
