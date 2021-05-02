package be.kuleuven.elcontador10.fragments.transactions;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.TransactionsManager;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsDisplayInterface;

public class TransactionDisplay extends Fragment implements TransactionsDisplayInterface {
    private MainActivity mainActivity;
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("Transaction");

        return inflater.inflate(R.layout.fragment_transaction_display, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            TransactionDisplayArgs args = TransactionDisplayArgs.fromBundle(getArguments());
            id = args.getId();

            TransactionsManager manager = TransactionsManager.getInstance();
            manager.getTransaction(this, id);
        }
        catch (Exception e) {
            Toast.makeText(mainActivity, "Nothing to show", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void display(Bundle bundle) {
        TextView sender, receiver, amount, category, subcategory, date, notes;

        sender = getView().findViewById(R.id.txtTransactionDisplaySender);
        receiver = getView().findViewById(R.id.txtTransactionDisplayReceiver);
        amount = getView().findViewById(R.id.txtTransactionDisplayAmount);
        category = getView().findViewById(R.id.txtTransactionDisplayCatgeory);
        subcategory = getView().findViewById(R.id.txtTransactionDisplaySubcategory);
        date = getView().findViewById(R.id.txtTransactionDisplayDate);
        notes = getView().findViewById(R.id.txtTransactionDisplayNotes);

        sender.setText(bundle.getString("sender"));
        receiver.setText(bundle.getString("receiver"));
        amount.setText(bundle.getString("amount"));
        category.setText(bundle.getString("category"));
        subcategory.setText(bundle.getString("subcategory"));
        date.setText(bundle.getString("date"));
        notes.setText(bundle.getString("notes"));
    }

    @Override
    public void error(String error) {
        Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() { return mainActivity; }
}