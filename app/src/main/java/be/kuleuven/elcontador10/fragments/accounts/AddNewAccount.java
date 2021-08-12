package be.kuleuven.elcontador10.fragments.accounts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.model.Account;


public class AddNewAccount extends Fragment {
    Button confirmButton;
    EditText edTextName;
    EditText edTextBalance;
    MainActivity mainActivity;
    NavController navController;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity =(MainActivity) getActivity();
        mainActivity.displayTopMenu(false);
        mainActivity.setHeaderText(getString(R.string.add_account));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_add_new_account, container, false);
        confirmButton = view.findViewById(R.id.btn_confirm_NewAccount);
        edTextBalance = view.findViewById(R.id.ed_txt_starting_balance);
        edTextName = view.findViewById(R.id.ed_txt_name_Account);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        confirmButton.setOnClickListener(v ->registerAccount());
    }

    private void registerAccount() {
        String loggedInEmail = mainActivity.returnSavedLoggedEmail();
        if(loggedInEmail!=null){
            ArrayList<String> users= new ArrayList<>();
            users.add(loggedInEmail);
            String nameOfAccount =edTextName.getText().toString();
            if (!(nameOfAccount.isEmpty())){
                String amountText = edTextBalance.getText().toString();
                long amount;
                if(!(amountText.isEmpty())) {
                    amount = Long.parseLong(amountText);
                }
                else{amount =0;}
                Account newAccount = new Account(nameOfAccount,amount,users);
                newAccount.sendNewAccount(newAccount,getContext());
            }
            else{
                Toast.makeText(getContext(),"Please type down a name for the account",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getContext(),"No Logged In email found",Toast.LENGTH_SHORT).show();

        }
        navController.popBackStack();
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.displayTopMenu(true);
    }
}