package be.kuleuven.elcontador10.background.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.viewModels.ChosenStakeViewModel;
import be.kuleuven.elcontador10.fragments.ViewPagerHolderDirections;
import be.kuleuven.elcontador10.fragments.microaccounts.MicroAccountViewPagerHolderArgs;

public class AllMicroRecViewAdapter extends RecyclerView.Adapter<AllMicroRecViewAdapter.ViewHolder> {

    private List<StakeHolder> microAccountsList = new ArrayList<>();
    private ArrayList<StakeHolder> microAccountsOriginal;
    private final View viewFromHostingClass;
    private ChosenStakeViewModel viewModel;

    public AllMicroRecViewAdapter(View viewFromHostingClass, ChosenStakeViewModel viewModel) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_micros,parent,false);
        return new AllMicroRecViewAdapter.ViewHolder(viewParent);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.textName.setText(microAccountsList.get(position).getName());
        holder.textRole.setText(String.valueOf(microAccountsList.get(position).getRole()));
        holder.parent.setOnClickListener(v -> {
                    viewModel.select(microAccountsList.get(position));
                    NavController navController = Navigation.findNavController(viewFromHostingClass);
                    String id = Caching.INSTANCE.getChosenAccountId();
                    ViewPagerHolderDirections.ActionViewPagerHolderToMicroAccountViewPagerHolder action =
                        ViewPagerHolderDirections.actionViewPagerHolderToMicroAccountViewPagerHolder(microAccountsList.get(position), Caching.INSTANCE.getChosenAccountId());
                    navController.navigate(action);
                }
        );
    }


    @Override
    public int getItemCount() {
        return microAccountsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textName;
        private TextView textRole;
        private ImageView buttonNewTransaction;
        private ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent_allMicros);
            textName = itemView.findViewById(R.id.text_Account_name_Micros);
            textRole = itemView.findViewById(R.id.text_micros_role);
        }
    }

    public void setMicroAccountsList(List <StakeHolder> microAccountsList) {
        this.microAccountsList = microAccountsList;
        notifyDataSetChanged();
        microAccountsOriginal = new ArrayList<>();
        microAccountsOriginal.addAll(this.microAccountsList);

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filter(String txtSearched){
        int lenght = txtSearched.length();
        if(lenght==0){
            microAccountsList.clear();
            microAccountsList.addAll(microAccountsOriginal);
        }else{
            List<StakeHolder> collection = microAccountsList
                    .stream()
                    .filter(i->i.getName().toLowerCase().contains(txtSearched.toLowerCase()))
                    .collect(Collectors.toList());
            microAccountsList.clear();
            microAccountsList.addAll(collection);

        }

        notifyDataSetChanged();
    }
}
