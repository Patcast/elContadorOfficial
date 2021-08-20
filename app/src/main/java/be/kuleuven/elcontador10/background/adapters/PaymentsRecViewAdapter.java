package be.kuleuven.elcontador10.background.adapters;

import android.content.Context;
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
import be.kuleuven.elcontador10.background.model.contract.Payment;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;
import be.kuleuven.elcontador10.background.tools.DateFormatter;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = payments.get(position);

        // set texts
        holder.title.setText(payment.getTitle());

        long amount = payment.getAmount();
        String absolute_amount = new NumberFormatter(Math.abs(amount)).getFinalNumber();
        String amount_text = ((amount > 0)? context.getString(R.string.in) : context.getString(R.string.out)) + " " + absolute_amount;
        holder.amount.setText(amount_text);

        holder.period.setText(payment.getPeriod());

        String frequency_text = context.getString(R.string.frequency) + ": " + DatabaseDatesFunctions.INSTANCE.frequencyDecoder(payment.getFrequency(), context);
        holder.frequency.setText(frequency_text);

        String next_string = context.getString(R.string.next_payment) + " " +
                ((payment.getNextPaymentDate() == null)? "N/A" :  new DateFormatter(payment.getNextPaymentDate(), "f").getFormattedDate());
        holder.next.setText(next_string);

        String paymentsLeft = context.getString(R.string.payments_left) + " " + payment.getPaymentsLeft();
        holder.left.setText(paymentsLeft);

        // TODO go to payment fragment
        holder.layout.setOnClickListener(v -> {

        });

        // hide last divider
        if (position + 1 == getItemCount()) holder.divider.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, amount, period, next, frequency, left;
        private final View divider;
        private final ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.payment_title);
            amount = itemView.findViewById(R.id.payment_amount);
            period = itemView.findViewById(R.id.payment_period);
            next = itemView.findViewById(R.id.payment_nextPayment);
            frequency = itemView.findViewById(R.id.payment_frequency);
            left = itemView.findViewById(R.id.payment_paymentsLeft);
            layout = itemView.findViewById(R.id.payment_layout);
            divider = itemView.findViewById(R.id.payment_divider);
        }
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
        notifyDataSetChanged();
    }
}
