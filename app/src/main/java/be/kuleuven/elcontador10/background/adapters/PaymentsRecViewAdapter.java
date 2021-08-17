package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
import android.icu.number.FormattedNumber;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.NumberFormatter;
import be.kuleuven.elcontador10.background.model.contract.Payment;

public class PaymentsRecViewAdapter extends RecyclerView.Adapter<PaymentsRecViewAdapter.ViewHolder> {
    private List<Payment> payments;
    private final View viewFromHostingClass;
    private Context context;
    private NavController navController;
    private Fragment fragment;

    public PaymentsRecViewAdapter(View viewFromHostingClass, Context context, Fragment fragment) {
        this.viewFromHostingClass = viewFromHostingClass;
        this.context = context;
        payments = new ArrayList<>();
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        navController = Navigation.findNavController(viewFromHostingClass);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view_payments, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = payments.get(position);

        holder.title.setText(payment.getTitle());
        holder.amount.setText(context.getString(R.string.amount) + " " + new NumberFormatter(payment.getAmount()).getFinalNumber());
        // TODO dates
        holder.frequency.setText(context.getString(R.string.frequency) + " " + payment.getFrequency());

        // TODO go to payment fragment
        holder.layout.setOnClickListener(v -> {

        });

        // hide last divider
        if (position + 1 != getItemCount()) holder.divider.setVisibility(View.VISIBLE);
        else holder.divider.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, amount, start, end, frequency;
        private final View divider;
        private final ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.payment_title);
            amount = itemView.findViewById(R.id.payment_amount);
            start = itemView.findViewById(R.id.payment_start);
            end = itemView.findViewById(R.id.payment_end);
            frequency = itemView.findViewById(R.id.payment_frequency);
            layout = itemView.findViewById(R.id.payment_layout);
            divider = itemView.findViewById(R.id.payment_divider);
        }
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
        notifyDataSetChanged();
    }
}
