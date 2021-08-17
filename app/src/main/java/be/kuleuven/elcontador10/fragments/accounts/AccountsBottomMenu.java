package be.kuleuven.elcontador10.fragments.accounts;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import be.kuleuven.elcontador10.R;


public class AccountsBottomMenu extends BottomSheetDialogFragment {

    public interface AccountsBottomSheetListener{
        void onAddAccountClick();
        void onLogOut();
    }


    ConstraintLayout addNewAccountButton;
    ConstraintLayout logOutButton;
    AccountsBottomSheetListener attachedListener;

    public AccountsBottomMenu(AccountsBottomSheetListener attachedListener) {
        this.attachedListener= attachedListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.bottom_menu_accounts, container, false);
        addNewAccountButton = view.findViewById(R.id.bs_account_addNew);
        logOutButton = view.findViewById(R.id.bs_account_log_out);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addNewAccountButton.setOnClickListener(v -> attachedListener.onAddAccountClick());
        logOutButton.setOnClickListener(v -> attachedListener.onLogOut());
    }



}