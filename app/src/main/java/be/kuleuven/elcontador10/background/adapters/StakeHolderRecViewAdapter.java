package be.kuleuven.elcontador10.background.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;



public class StakeHolderRecViewAdapter extends RecyclerView.Adapter<StakeHolderRecViewAdapter.ViewHolder> {

    private List<StakeHolder> stakeholdersList = new ArrayList<>();
    private ArrayList<StakeHolder> stakeHoldersOriginal;
    private final View viewFromHostingClass;
    private ViewModel_NewTransaction viewModel;

    public StakeHolderRecViewAdapter(View viewFromHostingClass, ViewModel_NewTransaction viewModel) {
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
    public void onBindViewHolder(@NonNull  StakeHolderRecViewAdapter.ViewHolder holder, int position) {

            holder.textName.setText(stakeholdersList.get(position).getName());
            holder.textRole.setText(String.valueOf(stakeholdersList.get(position).getRole()));
            holder.parent.setOnClickListener(v -> {
               viewModel.selectStakeholder(stakeholdersList.get(position));
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

        private ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent_allMicros);
            textName = itemView.findViewById(R.id.text_Account_name_Micros);
            textRole = itemView.findViewById(R.id.text_micros_role);
        }
    }

    public void setStakeholdersList(List <StakeHolder> stakeholdersList) {
        this.stakeholdersList = stakeholdersList;
        notifyDataSetChanged();
        stakeHoldersOriginal = new ArrayList<>();
        stakeHoldersOriginal.addAll(this.stakeholdersList);

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filter(String txtSearched){
        int lenght = txtSearched.length();
        if(lenght==0){
            stakeholdersList.clear();
            stakeholdersList.addAll(stakeHoldersOriginal);
        }else{
            List<StakeHolder> collection = stakeholdersList
                                                            .stream()
                                                            .filter(i->i.getName().toLowerCase().contains(txtSearched.toLowerCase()))
                                                            .collect(Collectors.toList());
            stakeholdersList.clear();
            stakeholdersList.addAll(collection);

        }

     notifyDataSetChanged();
    }
}
