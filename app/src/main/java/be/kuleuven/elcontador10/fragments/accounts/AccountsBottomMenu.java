package be.kuleuven.elcontador10.fragments.accounts;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;


public class AccountsBottomMenu extends BottomSheetDialogFragment {
    ConstraintLayout addNewAccountButton;
    ConstraintLayout logOutButton;


    public AccountsBottomMenu(AccountsBottomSheetListener attachedListener) {
        this.attachedListener= attachedListener;
    }

    AccountsBottomSheetListener attachedListener;
    public interface AccountsBottomSheetListener{
        void onAddAccountClick();
        void onLogOut();
    }

   /* @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try{
            attachedListener = (AccountsBottomSheetListener) getParentFragment();
        }
        catch(ClassCastException e){
            throw new ClassCastException(context.toString()+ "must implement AccountsBottomSheetListener ");
        }

    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_accounts_bottom_menu, container, false);
        addNewAccountButton = view.findViewById(R.id.bs_account_addNew);
        logOutButton = view.findViewById(R.id.bs_account_log_out);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addNewAccountButton.setOnClickListener(v -> attachedListener.onAddAccountClick());
        logOutButton.setOnClickListener(v -> attachedListener.onLogOut());
    }

}