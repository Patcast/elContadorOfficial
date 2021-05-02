package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.interfaces.HomepageInterface;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsInterface;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> TitleArray, DescriptionArray, StatusArray, MetadataArray;
    private Fragment fragment;
    private Context context;

    /*
    * Take in the arrays and context
    * */
    public RecyclerViewAdapter(ArrayList<String> titleArray, ArrayList<String> descriptionArray,
                               ArrayList<String> statusArray, ArrayList<String> metadataArray, Fragment fragment) {
        TitleArray = titleArray;
        DescriptionArray = descriptionArray;
        StatusArray = statusArray;
        MetadataArray = metadataArray;
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview_row, parent, false);
        return new ViewHolder(view);
    }

    /*
    * Set the text for each CardView from the array
    * */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] text_title; // COLOUR|TEXT
        String[] text_status;

        if (TitleArray.get(position).contains("#")) {
            text_title = TitleArray.get(position).split("#");
            text_status = StatusArray.get(position).split("#");
        }
        else {
            text_title = new String[] {"WHITE", TitleArray.get(position)};
            text_status = new String[] {"WHITE", TitleArray.get(position)};
        }

        holder.title.setText(text_title[1]);
        holder.description.setText(DescriptionArray.get(position));
        holder.status.setText(text_status[1]);
        holder.metadata.setText(MetadataArray.get(position));

        //TODO add more colours
        //for title colour
        switch (text_title[0]) {
            default:
                holder.title.setTextColor(context.getResources().getColor(R.color.white));
                break;
        }

        //for status colour
        switch (text_status[0]) {
            case "RED":
                holder.status.setTextColor(context.getResources().getColor(R.color.contador_red));
                break;
            case "GREEN":
                holder.status.setTextColor(context.getResources().getColor(R.color.green));
                break;
            default:
                holder.status.setTextColor(context.getResources().getColor(R.color.white));
        }

        if (!MetadataArray.get(position).equals("")) {
            holder.layout.setOnClickListener(v -> onClick(v, position));
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.contador_bg));
            holder.divider.setVisibility(View.INVISIBLE);
        }
    }

    //TODO implement onclick listener
    private void onClick(View view, int position) {
        if (MetadataArray.get(position).contains("#")) {
            String[] array = MetadataArray.get(position).split("#");
            switch(array[0]) {
                case "TransactionsSummary":
                    if (fragment instanceof TransactionsInterface) {
                        TransactionsInterface transactions = (TransactionsInterface) fragment;
                        transactions.displayTransaction(array[1]);
                    }
                    else if (fragment instanceof HomepageInterface) {
                        HomepageInterface home = (HomepageInterface) fragment;
                        home.displayTransaction(array[1]);
                    }
                    break;
                default:
                    Toast.makeText(context, "Nothing to show.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return TitleArray.size();
    }

    /*
    * Find IDs from the CardView row
    * */
    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView title, description, status, metadata;
        protected LinearLayout layout;
        protected CardView cardView;
        protected View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.labelRowTitle);
            description = itemView.findViewById(R.id.labelRowDescription);
            status = itemView.findViewById(R.id.labelRowStatus);
            metadata = itemView.findViewById(R.id.labelRowMetadata);
            layout = itemView.findViewById(R.id.cardview_layout);
            cardView = itemView.findViewById(R.id.cardBackground);
            divider = itemView.findViewById(R.id.cardDivider);
        }
    }
}
