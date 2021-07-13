package be.kuleuven.elcontador10.background.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Account;


public class AccountsRecViewAdapter extends RecyclerView.Adapter<AccountsRecViewAdapter.ViewHolder> {

    private ArrayList<Account> accounts = new ArrayList<>();
    private final View viewFromHostingClass;
    NavController navController;


    public AccountsRecViewAdapter(View view) {
        viewFromHostingClass= view;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_accounts,parent,false);
        return new ViewHolder(viewParent);
    }

    @Override
    public void onBindViewHolder(@NonNull  AccountsRecViewAdapter.ViewHolder holder, int position) {
        holder.textName.setText(accounts.get(position).getName());
        holder.textBalance.setText(String.valueOf(accounts.get(position).getBalance()));
        holder.parent.setOnClickListener(v -> {
            Caching.INSTANCE.setChosenAccountId(accounts.get(position).getId());
                navController.navigate(R.id.action_home_summary_to_transactions_summary);
            }
        );
        holder.buttonNewTransaction.setOnClickListener(v->{
            Caching.INSTANCE.setChosenAccountId(accounts.get(position).getId());
            navController.navigate(R.id.action_home_summary_to_newTransaction);
        });



    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textName;
        private TextView textBalance;
        private Button buttonNewTransaction;
        private ConstraintLayout parent;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.RecView_ChooseAc);
            textName = itemView.findViewById(R.id.text_Account_name);
            textBalance = itemView.findViewById(R.id.text_Account_balance);
            buttonNewTransaction = itemView.findViewById(R.id.button_AccountNewTrans);

        }
    }

    public void setGreetings(ArrayList<Account> greetings) {
        this.accounts = greetings;
        notifyDataSetChanged();
    }
}
