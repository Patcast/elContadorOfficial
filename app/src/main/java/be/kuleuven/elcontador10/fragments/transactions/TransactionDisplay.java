package be.kuleuven.elcontador10.fragments.transactions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.interfaces.TransactionsDisplayInterface;

public class TransactionDisplay extends Fragment implements TransactionsDisplayInterface {
    private MainActivity mainActivity;
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("Transaction");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            assert getArguments() != null;
            TransactionDisplayArgs args = TransactionDisplayArgs.fromBundle(getArguments());
            id = args.getId();
        }
        catch (Exception e) {
            Toast.makeText(mainActivity, "Nothing to show", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void display(Bundle bundle) {

    }
}