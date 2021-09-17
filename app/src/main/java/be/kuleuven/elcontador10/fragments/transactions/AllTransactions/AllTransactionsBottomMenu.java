package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import be.kuleuven.elcontador10.R;

public class AllTransactionsBottomMenu extends BottomSheetDialogFragment {

    public interface AllTransactionBottomSheetListener{
        void onOptionSelected();
    }


    LinearLayout transactions,receivables,payables;
    AllTransactionBottomSheetListener attachedListener;

    public AllTransactionsBottomMenu(AllTransactionBottomSheetListener attachedListener) {
        this.attachedListener= attachedListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.bottom_menu_all_transactions, container, false);
        transactions = view.findViewById(R.id.bs_layout_transaction);
        receivables = view.findViewById(R.id.bs_layout_Receivables);
        payables = view.findViewById(R.id.bs_layout_Payables);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            transactions.setOnClickListener(v -> attachedListener.onOptionSelected());
            receivables.setOnClickListener(v -> attachedListener.onOptionSelected());
            payables.setOnClickListener(v -> attachedListener.onOptionSelected());
    }



}