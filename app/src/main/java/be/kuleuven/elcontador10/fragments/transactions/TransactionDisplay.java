package be.kuleuven.elcontador10.fragments.transactions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.TransactionsManager;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsDisplayInterface;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;

public class TransactionDisplay extends Fragment implements TransactionsDisplayInterface {
    private MainActivity mainActivity;
    private String id;
    private TransactionsManager manager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(R.string.transactions);
        manager = TransactionsManager.getInstance();

        return inflater.inflate(R.layout.fragment_transaction_display, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            TransactionDisplayArgs args = TransactionDisplayArgs.fromBundle(getArguments());
            id = args.getId();

            manager.getTransaction(this, id);
        }
        catch (Exception e) {
            Toast.makeText(mainActivity, "Nothing to show", Toast.LENGTH_SHORT).show();
        }

        Button delete = requireView().findViewById(R.id.btn_Delete_DisplayTransaction);
        delete.setOnClickListener(this::onDelete_Clicked);
    }

    public void onDelete_Clicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        builder.setTitle("Delete transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes", (dialog, which) -> manager.deleteTransaction(this, id))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void display(Bundle bundle) {
        TextView sender, receiver, amount, category, subcategory, date, notes;

        sender = requireView().findViewById(R.id.txtTransactionDisplaySender);
        receiver = requireView().findViewById(R.id.txtTransactionDisplayReceiver);
        amount = requireView().findViewById(R.id.txtTransactionDisplayAmount);
        category = requireView().findViewById(R.id.txtTransactionDisplayCatgeory);
        subcategory = requireView().findViewById(R.id.txtTransactionDisplaySubcategory);
        date = requireView().findViewById(R.id.txtTransactionDisplayDate);
        notes = requireView().findViewById(R.id.txtTransactionDisplayNotes);
        notes.setMovementMethod(new ScrollingMovementMethod());

        sender.setText(bundle.getString("user"));
        receiver.setText(bundle.getString("stakeholder"));

        category.setText(bundle.getString("category"));
        subcategory.setText(bundle.getString("subcategory"));
        date.setText(bundle.getString("date"));
        notes.setText(bundle.getString("notes"));

        // for amount
        double sum = bundle.getDouble("amount");
        String amount_text = (sum > 0 ? "IN" : "OUT") + " $" + Math.abs(sum);
        amount.setText(amount_text);

    }

    @Override
    public void error(String error) {
        Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();

        FilterTransactionsParcel parcel = new FilterTransactionsParcel("*", "*", "*", null, null);

        NavController navController = Navigation.findNavController(requireView());
        TransactionDisplayDirections.ActionTransactionDisplayToTransactionsSummary action =
                TransactionDisplayDirections.actionTransactionDisplayToTransactionsSummary(parcel);
        navController.navigate(action);
    }

    @Override
    public Context getContext() { return mainActivity; }
}