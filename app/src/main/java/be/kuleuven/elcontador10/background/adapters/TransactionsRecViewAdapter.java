package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.fragments.ViewPagerHolderDirections;
import be.kuleuven.elcontador10.fragments.microaccounts.MicroAccountViewPagerHolder;
import be.kuleuven.elcontador10.fragments.microaccounts.MicroAccountViewPagerHolderDirections;
import be.kuleuven.elcontador10.fragments.transactions.ViewPagerHolderDirections;


public class TransactionsRecViewAdapter extends RecyclerView.Adapter<TransactionsRecViewAdapter.ViewHolder>  {
    private List<Transaction> allTransactions = new ArrayList<>();
    NavController navController;
    View viewFromHostingClass;
    Context context;
    public TransactionsRecViewAdapter(View viewFromHostingClass, Context context) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String idStakeholder = allTransactions.get(position).getStakeHolder();
        String idOfTransaction = allTransactions.get(position).getId();
        String stakeName = Caching.INSTANCE.getStakeholderName(idStakeholder);
        NumberFormatter formatter = new NumberFormatter(allTransactions.get(position).getAmount());
        if(formatter.isNegative())holder.textAmount.setTextColor(ContextCompat.getColor(context, R.color.rec_view_negative_amount));
        if(formatter.isNegative())holder.textPaidBy.setText(R.string.paid_to);
        holder.textNameOfParticipant.setText(stakeName);
        holder.textAmount.setText(formatter.getFinalNumber());
        holder.textDate.setText(allTransactions.get(position).getShortDate());
        holder.textTitle.setText(allTransactions.get(position).getTitle());
        holder.txtEmojiCategory.setText(Caching.INSTANCE.getCategoryEmoji(allTransactions.get(position).getCategory()));
        holder.parent.setOnClickListener(v->{
            try {
                // from Account ViewHolder
                ViewPagerHolderDirections.ActionViewPagerHolderToTransactionDisplay action = ViewPagerHolderDirections.actionViewPagerHolderToTransactionDisplay(idOfTransaction);
                navController.navigate(action);
            } catch (Exception e) {
                // from MicroAccount ViewHolder
                MicroAccountViewPagerHolderDirections.ActionMicroAccountViewPagerHolderToTransactionDisplay action =
                        MicroAccountViewPagerHolderDirections.actionMicroAccountViewPagerHolderToTransactionDisplay(idOfTransaction);
                navController.navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textTitle,textAmount,textDate,textNameOfParticipant,textPaidBy,txtEmojiCategory;
        private ConstraintLayout parent;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            txtEmojiCategory = itemView.findViewById(R.id.textView_transaction_emoji);
            textTitle = itemView.findViewById(R.id.text_title_allTrans);
            parent = itemView.findViewById(R.id.recVew_Item_AllTransactions);
            textTitle = itemView.findViewById(R.id.text_title_allTrans);
            textAmount = itemView.findViewById(R.id.textAmount);
            textDate = itemView.findViewById(R.id.text_date_allTrans);
            textPaidBy = itemView.findViewById(R.id.textPaidBy);
            textNameOfParticipant = itemView.findViewById(R.id.text_nameOfParticipant);

        }
    }


    public void setAllTransactions (List<Transaction> NewTransactions) {
        this.allTransactions = NewTransactions;
        notifyDataSetChanged();
    }
}
