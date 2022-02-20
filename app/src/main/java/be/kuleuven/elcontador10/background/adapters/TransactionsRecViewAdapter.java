package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.tools.DateFormatter;
import be.kuleuven.elcontador10.fragments.stakeholders.common.StakeholderViewPageHolderDirections;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.AllTransactionsDirections;


public class TransactionsRecViewAdapter extends RecyclerView.Adapter<TransactionsRecViewAdapter.ViewHolder>  {
    private List<TransactionInterface> allTransactions = new ArrayList<>();
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
        TransactionInterface transaction = allTransactions.get(position);
        // show stakeholder
        if(transaction.getTotalAmount()<0)holder.textPaidBy.setText(R.string.paid_to);
        else holder.textPaidBy.setText(R.string.paid_by);
        holder.textNameOfParticipant.setText(Caching.INSTANCE.getStakeholderName(transaction.getIdOfStakeInt()));
        // show Amount
        holder.textAmount.setText(transaction.getAmountToDisplay());
        holder.textAmount.setTextColor(ContextCompat.getColor(context,transaction.getColorInt()));
        // show Date
        DateFormatter dateFormatter = new DateFormatter(transaction.getDueDate(),"s");
        holder.textDate.setText(dateFormatter.getFormattedDate());
        // show title
        holder.textTitle.setText(transaction.getTitle());
        holder.textTitle.setTextColor(ContextCompat.getColor(context, allTransactions.get(position).getColorInt()));
        // show image and emoji
        holder.txtEmojiCategory.setText(Caching.INSTANCE.getCategoryEmoji(allTransactions.get(position).getIdOfCategoryInt()));
        if(!(allTransactions.get(position).getImageName()!= null && allTransactions.get(position).getImageName().length()>0))holder.camaraIcon.setVisibility(View.GONE);
        else holder.camaraIcon.setVisibility(View.VISIBLE);

        holder.parent.setOnClickListener(v -> {
            if (transaction instanceof ProcessedTransaction) {
                try {
                    // from Account ViewHolder
                    ProcessedTransaction castTransaction = (ProcessedTransaction) transaction;
                    if(castTransaction.getIsDeleted()) Toast.makeText(context ,"This transaction is deleted. The details cannot be displayed", Toast.LENGTH_SHORT).show();
                    else{
                        AllTransactionsDirections.ActionAllTransactions2ToTransactionDisplay action = AllTransactionsDirections.actionAllTransactions2ToTransactionDisplay(transaction.getIdOfTransactionInt());
                        navController.navigate(action);
                    }

                } catch (Exception e) {
                    // from MicroAccount ViewHolder
                    StakeholderViewPageHolderDirections.ActionMicroAccountViewPagerHolderToTransactionDisplay action =
                            StakeholderViewPageHolderDirections.actionMicroAccountViewPagerHolderToTransactionDisplay(transaction.getIdOfTransactionInt());
                    navController.navigate(action);
                }
            }
            else if (allTransactions.get(position) instanceof ScheduledTransaction) {
                    AllTransactionsDirections.ActionAllTransactions2ToExecuteScheduledTransaction action =
                            AllTransactionsDirections.actionAllTransactions2ToExecuteScheduledTransaction(transaction.getIdOfTransactionInt());

                    Caching.INSTANCE.setChosenStakeHolder(transaction.getIdOfStakeInt());

                    navController.navigate(action);

                } else Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return allTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textTitle,textAmount,textDate,textNameOfParticipant,textPaidBy,txtEmojiCategory;
        private ConstraintLayout parent;
        private ImageView camaraIcon;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            camaraIcon = itemView.findViewById(R.id.imageView_camara_icon);
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


    public void setAllTransactions (List<TransactionInterface> NewTransactions) {
        this.allTransactions = NewTransactions;
        notifyDataSetChanged();
    }
}
