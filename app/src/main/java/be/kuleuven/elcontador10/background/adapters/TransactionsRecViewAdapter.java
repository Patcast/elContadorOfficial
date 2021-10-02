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
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;
import be.kuleuven.elcontador10.background.tools.DateFormatter;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
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
        String idStakeholder = allTransactions.get(position).getIdOfStakeInt();
        String idOfTransaction = allTransactions.get(position).getIdOfTransactionInt();
        String stakeName = Caching.INSTANCE.getStakeholderName(idStakeholder);
        int amountInput = allTransactions.get(position).getTotalAmount();
        NumberFormatter formatter = new NumberFormatter(amountInput);
        if(formatter.isNegative())holder.textAmount.setTextColor(ContextCompat.getColor(context, R.color.rec_view_negative_amount));
        if(formatter.isNegative())holder.textPaidBy.setText(R.string.paid_to);
        holder.textNameOfParticipant.setText(stakeName);
        holder.textAmount.setText(formatter.getFinalNumber());
        DateFormatter dateFormatter = new DateFormatter(allTransactions.get(position).getDate(),"s");
        holder.textDate.setText(dateFormatter.getFormattedDate());
        holder.textTitle.setText(allTransactions.get(position).getTitle());
        holder.txtEmojiCategory.setText(Caching.INSTANCE.getCategoryEmoji(allTransactions.get(position).getIdOfCategoryInt()));
        if(!(allTransactions.get(position).getImageName()!= null && allTransactions.get(position).getImageName().length()>0))holder.camaraIcon.setVisibility(View.GONE);
        else holder.camaraIcon.setVisibility(View.VISIBLE);
        holder.parent.setOnClickListener(v->{
            try {
                // from Account ViewHolder
                AllTransactionsDirections.ActionAllTransactions2ToTransactionDisplay action = AllTransactionsDirections.actionAllTransactions2ToTransactionDisplay(idOfTransaction);
                navController.navigate(action);
            } catch (Exception e) {
                // from MicroAccount ViewHolder
                StakeholderViewPageHolderDirections.ActionMicroAccountViewPagerHolderToTransactionDisplay action =
                        StakeholderViewPageHolderDirections.actionMicroAccountViewPagerHolderToTransactionDisplay(idOfTransaction);
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
