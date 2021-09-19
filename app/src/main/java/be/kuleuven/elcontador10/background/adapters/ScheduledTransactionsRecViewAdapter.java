package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.fragments.stakeholders.common.StakeholderViewPageHolderDirections;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.AllTransactionsDirections;

public class ScheduledTransactionsRecViewAdapter extends RecyclerView.Adapter<ScheduledTransactionsRecViewAdapter.ViewHolder> {
    private List<ScheduledTransaction> allTransactions = new ArrayList<>();
    NavController navController;
    View viewFromHostingClass;
    Context context;
    public ScheduledTransactionsRecViewAdapter(View viewFromHostingClass, Context context) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.context = context;

    }
    @NonNull
    @NotNull
    @Override
    public ScheduledTransactionsRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_transactions,parent,false);
        return new ScheduledTransactionsRecViewAdapter.ViewHolder(viewParent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduledTransaction transaction = allTransactions.get(position);
        
        String idStakeholder = transaction.getIdOfStakeholder();
        String idOfTransaction = transaction.getId();
        String stakeName = Caching.INSTANCE.getStakeholderName(idStakeholder);
        holder.textNameOfParticipant.setText(stakeName);

        NumberFormatter formatterPaid = new NumberFormatter(transaction.getAmountPaid());
        NumberFormatter formatterTotal = new NumberFormatter(transaction.getTotalAmount());
        if(formatterTotal.isNegative()) {
            holder.textAmount.setTextColor(ContextCompat.getColor(context, R.color.rec_view_negative_amount));
            holder.textPaidBy.setText(R.string.paid_to);
        }

        String amount = formatterPaid.getFinalNumber() + "/" + formatterTotal.getFinalNumber();
        holder.textAmount.setText(amount);
        holder.textDate.setText(DatabaseDatesFunctions.INSTANCE.timestampToString(transaction.getDueDate()));
        holder.textTitle.setText(transaction.getTitle());

        //TODO categories
        
//        holder.parent.setOnClickListener(v->{
//            try {
//                // from Account ViewHolder
//                AllTransactionsDirections.ActionAllTransactions2ToTransactionDisplay action = AllTransactionsDirections.actionAllTransactions2ToTransactionDisplay(idOfTransaction);
//                navController.navigate(action);
//            } catch (Exception e) {
//                // from MicroAccount ViewHolder
//                StakeholderViewPageHolderDirections.ActionMicroAccountViewPagerHolderToTransactionDisplay action =
//                        StakeholderViewPageHolderDirections.actionMicroAccountViewPagerHolderToTransactionDisplay(idOfTransaction);
//                navController.navigate(action);
//            }
//        });
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
            txtEmojiCategory = itemView.findViewById(R.id.textView_scheduledTransaction_emoji);
            textTitle = itemView.findViewById(R.id.text_title_allSchTrans);
            parent = itemView.findViewById(R.id.recVew_Item_AllScheduledTransactions);
            textTitle = itemView.findViewById(R.id.text_title_allSchTrans);
            textAmount = itemView.findViewById(R.id.textAmount);
            textDate = itemView.findViewById(R.id.text_date_allSchTrans);
            textPaidBy = itemView.findViewById(R.id.textPaidBy);
            textNameOfParticipant = itemView.findViewById(R.id.text_nameOfParticipant);

        }
    }

    public void setScheduledTransactions (List<ScheduledTransaction> newTransactions) {
        this.allTransactions = newTransactions;
        notifyDataSetChanged();
    }
}
