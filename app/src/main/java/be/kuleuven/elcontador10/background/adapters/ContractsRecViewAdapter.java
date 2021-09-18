package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.contract.Contract;
import be.kuleuven.elcontador10.background.model.contract.SubContract;
import be.kuleuven.elcontador10.fragments.stakeholders.common.StakeholderViewPageHolderDirections;

public class ContractsRecViewAdapter extends RecyclerView.Adapter<ContractsRecViewAdapter.ViewHolder> {
    private List<Contract> contracts;
    private final View viewFromHostingClass;
    private final Context context;
    private NavController navController;
    private Fragment fragment;

    public ContractsRecViewAdapter(View view, Context context, Fragment fragment) {
        viewFromHostingClass = view;
        this.context = context;
        contracts = new ArrayList<>();
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_contracts, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contract contract = contracts.get(position);

        holder.title.setText(contract.getTitle());

        holder.expand.setOnClickListener(v -> {
            if (contract.getSubContracts().size() != 0) {
                if (holder.payments.getVisibility() == View.GONE) {
                    holder.expand.setBackground(context.getDrawable(R.drawable.icon_compress));
                    holder.payments.setVisibility(View.VISIBLE);

                    // set up Payments recycler view
                    holder.payments.setLayoutManager(new LinearLayoutManager(context));
                    ArrayList<SubContract> subContracts = contract.getSubContracts();
                    SubContractsRecViewAdapter adapter = new SubContractsRecViewAdapter(viewFromHostingClass, context, fragment);
                    adapter.setPayments(subContracts);
                    holder.payments.setAdapter(adapter);

                    holder.divider.setVisibility(View.VISIBLE);
                } else {
                    holder.expand.setBackground(context.getDrawable(R.drawable.icon_expand));
                    holder.payments.setVisibility(View.GONE);
                    holder.divider.setVisibility(View.GONE);
                }
            }
        });

        holder.layout.setOnClickListener(v -> {
            StakeholderViewPageHolderDirections.ActionMicroAccountViewPagerHolderToContractDisplay action =
                    StakeholderViewPageHolderDirections.actionMicroAccountViewPagerHolderToContractDisplay(contract.getId());
            navController.navigate(action);
        });
    }

    @Override
    public int getItemCount() {
        return contracts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final Button expand;
        private final RecyclerView payments;
        private final ConstraintLayout layout;
        private final View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.contract_title);
            expand = itemView.findViewById(R.id.contract_expand);
            payments = itemView.findViewById(R.id.contract_recyclerview);
            layout = itemView.findViewById(R.id.contract_layout);
            divider = itemView.findViewById(R.id.contract_divider);
        }
    }

    public void setContracts (List<Contract> contracts) {
        this.contracts = contracts;
        notifyDataSetChanged();
    }
}