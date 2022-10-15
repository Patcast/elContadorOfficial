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
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.tools.DateFormatter;
import be.kuleuven.elcontador10.fragments.property.PropertyViewPageHolderDirections;
import be.kuleuven.elcontador10.fragments.stakeholders.StakeholderViewPageHolderDirections;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.AllTransactionsDirections;

public class TransactionsRecViewAdapter extends  RecyclerView.Adapter<TransactionsRecViewAdapter.ViewHolder> {
    private List<ProcessedTransaction> allTransactions = new ArrayList<>();
    NavController navController;
    View viewFromHostingClass;
    Context context;



    public TransactionsRecViewAdapter(View viewFromHostingClass, Context context) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.context = context;

    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_transactions, parent, false);
        return new ViewHolder(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull TransactionsRecViewAdapter.ViewHolder holder, int position) {
            populateItemRows(holder, position);
    }


    @Override
    public int getItemCount() { return  allTransactions.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textTitle, textAmount, textDate, textNameOfParticipant,
                textPaidBy, txtEmojiCategory;
        private final ConstraintLayout parent;
        private final ImageView camaraIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            camaraIcon = itemView.findViewById(R.id.imageView_camara_icon);
            txtEmojiCategory = itemView.findViewById(R.id.textView_transaction_emoji);
            textTitle = itemView.findViewById(R.id.text_title_allTrans);
            textTitle.setSelected(true);
            parent = itemView.findViewById(R.id.recVew_Item_AllTransactions);
            textAmount = itemView.findViewById(R.id.textAmount);
            textAmount.setSelected(true);
            textDate = itemView.findViewById(R.id.text_date_allTrans);
            textPaidBy = itemView.findViewById(R.id.textPaidBy);
            textNameOfParticipant = itemView.findViewById(R.id.text_nameOfParticipant);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateItemRows(ViewHolder holder, int position) {

        ProcessedTransaction transaction = allTransactions.get(position);
        holder.textPaidBy.setText(transaction.transText());
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
            try {
                AllTransactionsDirections.ActionAllTransactions2ToTransactionDisplay action =
                        AllTransactionsDirections.actionAllTransactions2ToTransactionDisplay(transaction.getIdOfTransactionInt());
                navController.navigate(action);
            } catch (Exception e) { // there must be a better way
                // from MicroAccount ItemViewHolder
                try{
                    StakeholderViewPageHolderDirections.ActionMicroAccountViewPagerHolderToTransactionDisplay action =
                            StakeholderViewPageHolderDirections.actionMicroAccountViewPagerHolderToTransactionDisplay(transaction.getIdOfTransactionInt());
                    navController.navigate(action);
                }catch (Exception t){
                    PropertyViewPageHolderDirections.ActionPropertyViewPageHolderToTransactionDisplay action = PropertyViewPageHolderDirections.actionPropertyViewPageHolderToTransactionDisplay(transaction.getIdOfTransactionInt());
                    navController.navigate(action);
                }

            }
        });

    }

    public void setAllTransactions (List<ProcessedTransaction> newTransactions) {
        this.allTransactions.clear();
        this.allTransactions = newTransactions;
        this.notifyDataSetChanged();
    }
}
