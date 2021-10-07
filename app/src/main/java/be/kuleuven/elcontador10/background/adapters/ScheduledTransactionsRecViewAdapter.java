package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.graphics.Color;
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

import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.model.contract.SubContract;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.fragments.stakeholders.contracts.SubContractDisplayArgs;
import be.kuleuven.elcontador10.fragments.stakeholders.contracts.SubContractDisplayDirections;

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
        View viewParent = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_scheduled_payments,parent,false);
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
        if(formatterTotal.isNegative()) holder.textPaidBy.setText(R.string.paid_by);

        String amount = formatterPaid.getFinalNumber() + "/" + formatterTotal.getFinalNumber();
        holder.textAmount.setText(amount);
        if (transaction.getDueDate() != null)
            holder.textDate.setText(DatabaseDatesFunctions.INSTANCE.timestampToString(transaction.getDueDate()));
        else
            holder.textDate.setText("N/A");
        holder.textTitle.setText(transaction.getTitle());

        // setting colours
        if (Math.abs(transaction.getAmountPaid()) >= Math.abs(transaction.getTotalAmount())) { // paid
            holder.textTitle.setTextColor(Color.GRAY);
            holder.textAmount.setTextColor(Color.GRAY);
        } else if (transaction.getDueDate().getSeconds() < Timestamp.now().getSeconds()) { // unpaid and late
            holder.textTitle.setTextColor(Color.RED);
            holder.textAmount.setTextColor(Color.RED);
        }

        if (transaction.getCategory() != null)
            holder.txtEmojiCategory.setText(Caching.INSTANCE.getCategoryEmoji(transaction.getCategory()));

        //TODO categories

        // TODO go somewhere

        holder.parent.setOnClickListener(view -> {
            SubContractDisplayDirections.ActionSubContractDisplayToExecuteScheduledTransaction action =
                SubContractDisplayDirections.actionSubContractDisplayToExecuteScheduledTransaction(idOfTransaction);

            navController.navigate(action);
        });

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

    public class ViewHolder extends RecyclerView.ViewHolder {
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
