package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.tools.DateFormatter;
import be.kuleuven.elcontador10.fragments.stakeholders.common.StakeholderViewPageHolderDirections;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.AllTransactionsDirections;


public class TransactionsRecViewAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProcessedTransaction> allTransactions = new ArrayList<>();
    NavController navController;
    View viewFromHostingClass;
    Context context;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public TransactionsRecViewAdapter(View viewFromHostingClass, Context context) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.context = context;

    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_item_all_transactions, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }


    @Override
    public int getItemCount() { return allTransactions == null ? 0 : allTransactions.size(); }
    @Override
    public int getItemViewType(int position) {
        return allTransactions.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textTitle,textAmount,textDate,textNameOfParticipant,textPaidBy,txtEmojiCategory;
        private ConstraintLayout parent;
        private ImageView camaraIcon;

        public ItemViewHolder(@NonNull  View itemView) {
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
    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateItemRows(ItemViewHolder holder, int position) {

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
                StakeholderViewPageHolderDirections.ActionMicroAccountViewPagerHolderToTransactionDisplay action =
                        StakeholderViewPageHolderDirections.actionMicroAccountViewPagerHolderToTransactionDisplay(transaction.getIdOfTransactionInt());
                navController.navigate(action);
            }
        });

    }

    public void setAllTransactions (List<ProcessedTransaction> NewTransactions) {
        this.allTransactions = NewTransactions;
        notifyDataSetChanged();
    }
}
