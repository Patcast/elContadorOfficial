package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import be.kuleuven.elcontador10.R;

public class DialogFilterAllTransactions extends DialogFragment {


    androidx.lifecycle.LifecycleOwner owner;
    public DialogFilterAllTransactions(androidx.lifecycle.LifecycleOwner owner){
        this.owner = owner;
    }
    CheckBox transactions,receivables,payables;
    ViewModel_AllTransactions viewModel;
    HashMap<String,Boolean> typeOfTransactions;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUi(HashMap<String, Boolean> currentTransTypesSelected) {
        currentTransTypesSelected.forEach(this::updateUiItems);
    }
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
        viewModel.getChosenTypesOfTransactions().observe(owner, this::setUi);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("ok", (dialog1, id) ->onItemSelected())
                .setNegativeButton("cancel", (dialog12, id) ->
                        this.getDialog().cancel());
        builder.setTitle("Displayed information");

        return builder.create();
    }

    private void onItemSelected() {
    }


}