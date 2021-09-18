package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.contract.SubContract;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.fragments.stakeholders.common.StakeholderViewPageHolderDirections;
import be.kuleuven.elcontador10.fragments.stakeholders.contracts.ContractDisplay;
import be.kuleuven.elcontador10.fragments.stakeholders.contracts.ContractDisplayDirections;
import be.kuleuven.elcontador10.fragments.stakeholders.contracts.ContractsList;

public class SubContractsRecViewAdapter extends RecyclerView.Adapter<SubContractsRecViewAdapter.ViewHolder> {
    private List<SubContract> subContracts;
    private final View viewFromHostingClass;
    private Context context;
    private NavController navController;
    private final Fragment fragment;

    public SubContractsRecViewAdapter(View viewFromHostingClass, Context context, Fragment fragment) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.context = context;
        subContracts = new ArrayList<>();
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_sub_contracts, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubContract subContract = subContracts.get(position);

        // set texts
        holder.title.setText(subContract.getTitle());

        long amount = subContract.getAmount();
        String absolute_amount = new NumberFormatter(Math.abs(amount)).getFinalNumber();
        String amount_text = ((amount > 0)? context.getString(R.string.in) : context.getString(R.string.out)) + " " + absolute_amount;
        holder.amount.setText(amount_text);

        if (subContract.getStartDate() != null)
            holder.period.setText(DatabaseDatesFunctions.INSTANCE.timestampToPeriod(subContract.getStartDate(), subContract.getEndDate()));
        else
            holder.period.setText("N/A");

        // TODO go to subcontract fragment
        holder.layout.setOnClickListener(v -> {
            if (fragment instanceof ContractDisplay) {
                ContractDisplayDirections.ActionContractDisplayToSubContractDisplay action =
                        ContractDisplayDirections.actionContractDisplayToSubContractDisplay(subContract.getId());

                navController.navigate(action);
            } else if (fragment instanceof ContractsList) {
                StakeholderViewPageHolderDirections.ActionStakeholderViewPagerHolderToSubContractDisplay action =
                        StakeholderViewPageHolderDirections.actionStakeholderViewPagerHolderToSubContractDisplay(subContract.getId());

                navController.navigate(action);
            }
        });

        // hide last divider
        if (position + 1 == getItemCount()) holder.divider.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return subContracts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, amount, period;
        private final View divider;
        private final ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.payment_title);
            amount = itemView.findViewById(R.id.payment_amount);
            period = itemView.findViewById(R.id.payment_period);
            layout = itemView.findViewById(R.id.payment_layout);
            divider = itemView.findViewById(R.id.payment_divider);
        }
    }

    public void setPayments(List<SubContract> subContracts) {
        this.subContracts = subContracts;
        notifyDataSetChanged();
    }
}
