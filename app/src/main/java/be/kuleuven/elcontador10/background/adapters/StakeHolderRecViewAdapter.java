package be.kuleuven.elcontador10.background.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.viewModels.ChosenStakeViewModel;
import be.kuleuven.elcontador10.fragments.transactions.ChooseStakeHolder;


public class StakeHolderRecViewAdapter extends RecyclerView.Adapter<StakeHolderRecViewAdapter.ViewHolder> {

    private List<StakeHolder> stakeholdersList = new ArrayList<>();
    private final View viewFromHostingClass;
    private ChosenStakeViewModel viewModel;

    public StakeHolderRecViewAdapter(View viewFromHostingClass, ChosenStakeViewModel viewModel) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_accounts,parent,false);
            return new ViewHolder(viewParent);
            }

    @Override
    public void onBindViewHolder(@NonNull  StakeHolderRecViewAdapter.ViewHolder holder, int position) {
            holder.buttonNewTransaction.setVisibility(View.INVISIBLE);
            holder.textName.setText(stakeholdersList.get(position).getName());
            holder.textRole.setText(String.valueOf(stakeholdersList.get(position).getRole()));
           holder.parent.setOnClickListener(v -> {
               viewModel.select(stakeholdersList.get(position));
               NavController navController = Navigation.findNavController(viewFromHostingClass);
               navController.navigate(R.id.action_chooseStakeHolderDialog_to_newTransaction);
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
        private ImageView buttonNewTransaction;
        private ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.RecView_ChooseAc);
            textName = itemView.findViewById(R.id.text_Account_name);
            textRole = itemView.findViewById(R.id.text_Account_balance);
            buttonNewTransaction = itemView.findViewById(R.id.imageAddTransaction);

        }
    }

    public void setStakeholdersList(List <StakeHolder> stakeholdersList) {
        this.stakeholdersList = stakeholdersList;
        notifyDataSetChanged();
    }
}
