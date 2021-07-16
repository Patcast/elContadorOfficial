package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
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

public class AllTransactionsRecViewAdapter extends RecyclerView.Adapter<AllTransactionsRecViewAdapter.ViewHolder> implements Caching.AllTransactionsObserver {
    private List<Transaction> allTransactions = new ArrayList<>();
    NavController navController;
    View viewFromHostingClass;
    Context context;

    public AllTransactionsRecViewAdapter(View viewFromHostingClass, Context context) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.context = context;
        Caching.INSTANCE.attachAllTransactionsObserver(this);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_transactions,parent,false);
        return new AllTransactionsRecViewAdapter.ViewHolder(viewParent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
       Optional possibleName = Caching.INSTANCE.getStakeHolders().stream()
                                                                    .filter(s->s.getId().equals(allTransactions.get(position).getId()))
                                                                    .map(StakeHolder::getName)
                                                                    .findFirst();
        String nameMicroAccount  = (possibleName.isPresent())?(String) possibleName.get(): context.getString(R.string.error_finding_microAccount);
        holder.textAmount.setText(nameMicroAccount);
        holder.textAmount.setText(String.valueOf(allTransactions.get(position).getAmount()));
       holder.parent.setOnClickListener(v-> Toast.makeText(context,"This will be ready soon",Toast.LENGTH_SHORT));
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
            textName = itemView.findViewById(R.id.text_Account_name);
            textAmount = itemView.findViewById(R.id.text_Account_balance);
        }
    }
    @Override
    public void notifyAllTransactionsObserver(List<Transaction> allTransactions) {
        setAllTransactions(allTransactions);
    }

    public void setAllTransactions (List<Transaction> NewTransactions) {
        this.allTransactions.clear();
        this.allTransactions = NewTransactions;
        notifyDataSetChanged();
    }
}
