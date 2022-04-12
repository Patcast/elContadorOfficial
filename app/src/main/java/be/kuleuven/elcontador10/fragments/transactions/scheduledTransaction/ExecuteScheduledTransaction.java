package be.kuleuven.elcontador10.fragments.transactions.scheduledTransaction;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;

public class ExecuteScheduledTransaction extends Fragment {

    // views
    private TextView selectStakeholder, selectTransaction, amountTotal, amountPaid, amountLeft, warning, payableOrReceivable;
    private EditText increase;

    // variables
    private MainActivity mainActivity;
    private String transactionID;
    private ScheduledTransaction transaction;
    private StakeHolder stakeholder;
    private ExecuteScheduledViewModel viewModel;
    private NavController navController;
    private long left;
    private boolean isReceivable;
    private Button ignore;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.execute_scheduled_transaction));

        View view = inflater.inflate(R.layout.fragment_execute_scheduled_transaction, container, false);

        selectStakeholder = view.findViewById(R.id.execute_choose_stakeholder);
        selectTransaction = view.findViewById(R.id.execute_choose_transaction);
        amountTotal = view.findViewById(R.id.execute_amount_total);
        amountPaid = view.findViewById(R.id.execute_amount_paid);
        amountLeft = view.findViewById(R.id.execute_amount_left);
        warning = view.findViewById(R.id.execute_warning);
        payableOrReceivable = view.findViewById(R.id.execute_PayOrReceive);

        increase = view.findViewById(R.id.execute_amount);
        increase.addTextChangedListener(new CustomTextWatcher());

        Button confirm = view.findViewById(R.id.execute_confirm);
        confirm.setOnClickListener(this::onConfirm_Clicked);

        ignore = view.findViewById(R.id.execute_ignore);
        ignore.setOnClickListener(this::onIgnore_Clicked);

        viewModel = new ViewModelProvider(mainActivity).get(ExecuteScheduledViewModel.class);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        transactionID = ExecuteScheduledTransactionArgs.fromBundle(getArguments()).getId();
        stakeholder = Caching.INSTANCE.getChosenStakeHolder();

        if (stakeholder != null) {
            selectStakeholder.setText(stakeholder.getName());
            viewModel.setChosenStakeholder(stakeholder);

            if (transactionID != null) {
                transaction = Caching.INSTANCE.getScheduledTransactionFromId(transactionID);

                if (transaction != null) {
                    viewModel.setChosenTransaction(transaction);

                    String text;

                    if (transaction.getDueDate() != null)
                        text = transaction.getTitle() + " - " + DatabaseDatesFunctions.INSTANCE.timestampToString(transaction.getDueDate());
                    else
                        text = transaction.getTitle();

                    selectTransaction.setText(text);

                    if (transaction.isIgnored())
                        ignore.setText(R.string.un_ignore);

                    setTexts();
                } else {
                    Toast.makeText(mainActivity, "Error loading data!", Toast.LENGTH_SHORT).show();
                    navController.popBackStack();
                }
            }
            else setNullTexts();
        }
        else setNullTexts();

//        Toast.makeText(mainActivity, id, Toast.LENGTH_SHORT).show();
    }

    public void setTexts() {
        String total = getResources().getString(R.string.total_amount_to_pay) + new NumberFormatter(Math.abs(transaction.getTotalAmount())).getFinalNumber();
        String paid = getResources().getString(R.string.amount_paid) + new NumberFormatter(Math.abs(transaction.getAmountPaid())).getFinalNumber();

        this.left = Math.abs(transaction.getTotalAmount()) - Math.abs(transaction.getAmountPaid());
        String left = getResources().getString(R.string.amount_left) + new NumberFormatter(this.left).getFinalNumber();

        isReceivable = transaction.getTotalAmount() > 0;

        amountTotal.setText(total);
        amountPaid.setText(paid);
        amountLeft.setText(left);

        if (isReceivable) payableOrReceivable.setText(R.string.receivables);
        else payableOrReceivable.setText(R.string.payables);

    }

    public void setNullTexts() {
        String total = getResources().getString(R.string.total_amount_to_pay) + "N/A";
        String paid = getResources().getString(R.string.amount_paid) + "N/A";
        String left = getResources().getString(R.string.amount_left) + "N/A";

        amountTotal.setText(total);
        amountPaid.setText(paid);
        amountLeft.setText(left);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onConfirm_Clicked(View view) {
        String pay_text = increase.getText().toString();

        if (pay_text.equals(""))
            Toast.makeText(mainActivity, R.string.input_amount_to_pay, Toast.LENGTH_SHORT).show();
        else if (warning.getVisibility() == View.VISIBLE) {
            Toast.makeText(mainActivity, R.string.warning_amount_paid_will_be_larger_than_amount_to_pay, Toast.LENGTH_SHORT).show();
        } else if (transaction.isIgnored()) {
            Toast.makeText(mainActivity, R.string.unable_execute_ignored, Toast.LENGTH_SHORT).show();
        } else {
            int toPay = Integer.parseInt(pay_text);

            if (!isReceivable) toPay = -toPay; // is a payable, so negative

            transaction.pay(toPay);

            if (transaction.getAmountPaid() == transaction.getTotalAmount()) transaction.setCompleted(true);

            ScheduledTransaction.updateScheduledTransaction(transaction);

            // create new transaction

            String notes = String.format("Generated transaction of %s of stakeholder %s by %s at %s", transaction.getTitle(),
                    Caching.INSTANCE.getStakeholderName(transaction.getIdOfStakeholder()), Caching.INSTANCE.getAccountName(),
                    DatabaseDatesFunctions.INSTANCE.timestampToString(Timestamp.now()));

            ProcessedTransaction processedTransaction = new ProcessedTransaction(transaction.getTitle(), toPay, mainActivity.returnSavedLoggedEmail(),
                    transaction.getIdOfStakeholder(), transaction.getCategory(), notes, transaction.getImageName());

            processedTransaction.sendTransaction(processedTransaction, getContext());

            navController.popBackStack();
        }
    }

    public void onIgnore_Clicked(View view) {
        int title = (transaction.isIgnored())? R.string.uningnore_transaction_title : R.string.ignore_transaction_title;
        int message = (transaction.isIgnored())? R.string.unignore_transaction_message : R.string.ignore_transaction_message;

        AlertDialog dialog = new AlertDialog.Builder(mainActivity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    transaction.toggleIgnore();
                    if (transaction.isIgnored())
                        navController.popBackStack();
                    else { // now not ignored
                        ignore.setText(R.string.ignore);
                    }
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();

        dialog.show();
    }

    private class CustomTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String text = charSequence.toString();

            if (!text.equals("")) {
                int inter = Integer.parseInt(text);

                if (inter > left) warning.setVisibility(View.VISIBLE);
                else warning.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}