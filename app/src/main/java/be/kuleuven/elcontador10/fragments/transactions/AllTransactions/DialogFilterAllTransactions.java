package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.elcontador10.R;

public class DialogFilterAllTransactions extends DialogFragment {

    CheckBox transactions,receivables,payables;
    ViewModel_AllTransactions viewModel;
    Map<String,Boolean> typeOfTransactions;
    androidx.lifecycle.LifecycleOwner owner;
    public DialogFilterAllTransactions(androidx.lifecycle.LifecycleOwner owner){
        this.owner = owner;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUi(Map<String, Boolean> currentTransTypesSelected) {
        typeOfTransactions = currentTransTypesSelected;
        currentTransTypesSelected.forEach(this::updateUiItems);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUiItems(String s, Boolean b) {
       switch(s) {
            case "transaction":
                transactions.setChecked(b);
                break;
            case "receivable":
                receivables.setChecked(b);
                break;
            case "payable":
                payables.setChecked(b);
                break;
            default:
                Toast.makeText(getContext(), "error with dialog", Toast.LENGTH_SHORT).show();
        }
    }
    /////--------------
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bottom_menu_all_transactions, null);
        transactions = view.findViewById(R.id.check_box_transaction);
        receivables = view.findViewById(R.id.check_box_receivables);
        payables = view.findViewById(R.id.check_box_payables);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        viewModel.getBooleanFilter().observe(owner, this::setUi);
        builder.setView(view)
                .setPositiveButton("ok", (dialog1, id) -> onConfirmSelection())
                .setNegativeButton("cancel", (dialog12, id) ->
                        this.getDialog().cancel());
        builder.setTitle("Displayed information");

        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onConfirmSelection() {
        typeOfTransactions.forEach(this::updateList);
        viewModel.setBooleanFilter(typeOfTransactions);
        viewModel.setListOfTransactions();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateList(String s, Boolean aBoolean) {
        switch(s) {
            case "transaction":
                typeOfTransactions.replace(s,transactions.isChecked());

                break;
            case "receivable":
                typeOfTransactions.replace(s,receivables.isChecked());

                break;
            case "payable":
                typeOfTransactions.replace(s,payables.isChecked());
                break;
            default:
                Toast.makeText(getContext(), "error with dialog", Toast.LENGTH_SHORT).show();
        }
    }

}