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



    CheckBox transactions,receivables,payables;
    ViewModel_AllTransactions viewModel;
    HashMap<String,Boolean> typeOfTransactions;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.bottom_menu_all_transactions, container, false);

        transactions = view.findViewById(R.id.check_box_transaction);
        receivables = view.findViewById(R.id.check_box_receivables);
        payables = view.findViewById(R.id.check_box_payables);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_AllTransactions.class);
        viewModel.getChosenTypesOfTransactions().observe(getViewLifecycleOwner(), this::setUi);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUi(HashMap<String, Boolean> currentTransTypesSelected) {
        currentTransTypesSelected.forEach(this::updateUiItems);

    }

    private void updateUiItems(String s, Boolean b) {
       /* switch(s) {
            case "transaction":
                if(b)
                {
                    iconTransaction.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
                }
                else {
                    iconTransaction.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                }
                break;
            case "receivable":
                if(b) iconReceivable.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
                else iconReceivable.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                break;
            case "payable":
                if(b) iconPayable.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
                else iconPayable.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                break;
            default:
                Toast.makeText(getContext(), "error with dialog", Toast.LENGTH_SHORT).show();
        }*/
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            transactions.setOnClickListener(v-> updateUiItems("transaction",true));
            receivables.setOnClickListener(v-> updateUiItems("receivable",true));
            payables.setOnClickListener(v-> updateUiItems("payable",true));
    }



    /////--------------
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.bottom_menu_all_transactions, null);

        builder.setView(dialog)
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