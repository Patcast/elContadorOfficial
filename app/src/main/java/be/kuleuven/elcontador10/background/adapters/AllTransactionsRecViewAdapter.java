package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Account;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactionsDirections;

public class AllTransactionsRecViewAdapter extends RecyclerView.Adapter<AllTransactionsRecViewAdapter.ViewHolder>  {
    private List<Transaction> allTransactions = new ArrayList<>();
    NavController navController;
    View viewFromHostingClass;
    Context context;

    public AllTransactionsRecViewAdapter(View viewFromHostingClass, Context context) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.context = context;

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_transactions,parent,false);
        return new ViewHolder(viewParent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String id = allTransactions.get(position).getStakeHolder();
        holder.textName.setText(Caching.INSTANCE.getStakeholderName(id));
        long amount = allTransactions.get(position).getAmount();
        StringBuilder amountText = new StringBuilder();
        if(amount<0){
            holder.textAmount.setTextColor(Color.parseColor("#ffc7c7"));
            amount = amount *-1;
            amountText.append("- $" );
        }
        else{
            amountText.append("  $" );
        }
        amountText.append(amount);
        holder.textAmount.setText(amountText);
        holder.parent.setOnClickListener(v->{
            AllTransactionsDirections.ActionAllTransactionsToTransactionDisplay action = AllTransactionsDirections.actionAllTransactionsToTransactionDisplay(id);
            navController.navigate(action);
        });
    }

    @Override
    public int getItemCount() {
        return allTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textName;
        private TextView textAmount;
        private ConstraintLayout parent;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.recVew_Item_AllTransactions);
            textName = itemView.findViewById(R.id.textMicroAccount);
            textAmount = itemView.findViewById(R.id.textAmount);
        }
    }


    public void setAllTransactions (List<Transaction> NewTransactions) {
        this.allTransactions = NewTransactions;
        notifyDataSetChanged();
    }
}
