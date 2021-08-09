package be.kuleuven.elcontador10.background.adapters;


import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Account;


public class AccountsRecViewAdapter extends RecyclerView.Adapter<AccountsRecViewAdapter.ViewHolder>  {

    private  ArrayList<Account> accounts = new ArrayList<>();
    private final View viewFromHostingClass;
    NavController navController;


    public AccountsRecViewAdapter(View view) { viewFromHostingClass= view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_accounts,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull  AccountsRecViewAdapter.ViewHolder holder, int position) {
        holder.textName.setText(accounts.get(position).getName());
        long amount = accounts.get(position).getBalance();
        StringBuilder amountText = new StringBuilder();
        amountText.append("$" );
        amountText.append(amount);
        holder.textBalance.setText(amountText);

        holder.parent.setOnClickListener(v -> {
            String chosenAccount = accounts.get(position).getId();
            Caching.INSTANCE.openAccountFully(chosenAccount);
            navController.navigate(R.id.action_accounts_to_viewPagerHolder);
            }
        );

        holder.buttonNewTransaction.setOnClickListener(v->{
            String chosenAccount = accounts.get(position).getId();
            Caching.INSTANCE.openQuickNewTransaction(chosenAccount);
            navController.navigate(R.id.action_accounts_to_newTransaction);
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textName;
        private TextView textBalance;
        private ImageButton buttonNewTransaction;
        private ConstraintLayout parent;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.RecView_ChooseAc);
            textName = itemView.findViewById(R.id.text_Account_name);
            textBalance = itemView.findViewById(R.id.text_Account_balance);
            buttonNewTransaction = itemView.findViewById(R.id.imageAddTransaction);

        }
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts= accounts;
        notifyDataSetChanged();
    }
}
