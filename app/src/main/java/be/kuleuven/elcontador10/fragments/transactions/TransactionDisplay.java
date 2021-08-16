package be.kuleuven.elcontador10.fragments.transactions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.database.TransactionsManager;
import be.kuleuven.elcontador10.background.interfaces.transactions.TransactionsDisplayInterface;
import be.kuleuven.elcontador10.background.model.NumberFormatter;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.parcels.FilterTransactionsParcel;

public class TransactionDisplay extends Fragment  {
    private MainActivity mainActivity;
    TextView concerning, registeredBy, idText ,account, amount, category, subcategory, date, notes;
    Transaction selectedTrans;
    NavController navController;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.transaction_display));


        return inflater.inflate(R.layout.fragment_transaction_display, container, false);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        try {
            TransactionDisplayArgs args = TransactionDisplayArgs.fromBundle(getArguments());
            String idOfTransaction = args.getId();
            initializeViews(view);
            displayInformation(idOfTransaction);

        }
        catch (Exception e) {
            Toast.makeText(mainActivity, "Nothing to show", Toast.LENGTH_SHORT).show();
        }

        Button delete = requireView().findViewById(R.id.buttonDeleteTransaction);
        delete.setOnClickListener(this::onDelete_Clicked);
    }



    private void onDelete_Clicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Delete transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes", (dialog, which) ->confirmDelete())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void confirmDelete(){
        navController.popBackStack();
        selectedTrans.deleteTransaction();

    }



    public void initializeViews (View view) {
        amount = view.findViewById(R.id.textAmount);
        concerning = view.findViewById(R.id.textConcerningDisplay);
        account = view.findViewById(R.id.textAccountChosenDisplay);
        idText = view.findViewById(R.id.txtIdTransactionDISPLAY);
        category = view.findViewById(R.id.txtCategoryDisplay);
        subcategory = view.findViewById(R.id.txtSubCategoryDisplay);
        date = view.findViewById(R.id.txtDateDisplay);
        registeredBy = view.findViewById(R.id.txtRegisteredByDisplay);
        notes = view.findViewById(R.id.txtNotesDisplay);
        notes.setMovementMethod(new ScrollingMovementMethod());
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayInformation(String idOfTransaction) {
        selectedTrans = Caching.INSTANCE.getTransaction(idOfTransaction);
        if(selectedTrans.equals(null))Toast.makeText(getContext(),"error getting Transaction",Toast.LENGTH_SHORT);
        else {

            NumberFormatter formatter = new NumberFormatter(selectedTrans.getAmount());
            if(formatter.isNegative())amount.setTextColor(Color.parseColor("#ffc7c7"));
            amount.setText(formatter.getFinalNumber());
            concerning.setText(Caching.INSTANCE.getStakeholderName(selectedTrans.getStakeHolder()));
            account.setText(Caching.INSTANCE.getAccountName());
            idText.setText(selectedTrans.getId());
            category.setText(selectedTrans.getCategory());
            subcategory.setText(selectedTrans.getSubCategory());
            date.setText(String.valueOf(selectedTrans.getDate().toDate()));
            registeredBy.setText(selectedTrans.getRegisteredBy());
            notes.setText(selectedTrans.getNotes());
        }
    }


}