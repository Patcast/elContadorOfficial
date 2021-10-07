package be.kuleuven.elcontador10.fragments.transactions.scheduledTransaction;

import android.os.Build;
import android.os.Bundle;
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

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;

public class ExecuteScheduledTransaction extends Fragment {

    private TextView selectStakeholder, selectTransaction, amountTotal, amountPaid, amountLeft;
    private EditText increase;
    private Button confirm;

    // variables
    private MainActivity mainActivity;
    private String transactionID;
    private ScheduledTransaction transaction;
    private StakeHolder stakeholder;
    private ExecuteScheduledViewModel viewModel;

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

        increase = view.findViewById(R.id.execute_amount);

        confirm = view.findViewById(R.id.execute_confirm);

        viewModel = new ViewModelProvider(mainActivity).get(ExecuteScheduledViewModel.class);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

                    setTexts();
                } else
                    Toast.makeText(mainActivity, "Error loading data!", Toast.LENGTH_SHORT).show();
            }
        }

//        Toast.makeText(mainActivity, id, Toast.LENGTH_SHORT).show();
    }

    public void setTexts() {
        String total = getResources().getString(R.string.total_amount_to_pay) + new NumberFormatter(transaction.getTotalAmount()).getFinalNumber();
        String paid = getResources().getString(R.string.amount_paid) + new NumberFormatter(transaction.getAmountPaid()).getFinalNumber();
        String left = getResources().getString(R.string.amount_left) +
                new NumberFormatter(transaction.getTotalAmount() - transaction.getAmountPaid()).getFinalNumber();

        amountTotal.setText(total);
        amountPaid.setText(paid);
        amountLeft.setText(left);
    }
}