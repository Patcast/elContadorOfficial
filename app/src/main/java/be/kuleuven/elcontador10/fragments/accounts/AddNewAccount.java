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
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.background.model.Account;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;


public class AddNewAccount extends Fragment {
    //Todo: Add Notes for the account (for example to add the address)
    private Button confirmButton;
    private EditText edTextName, edTextBalance;
    private TextView counterName;
    private MainActivity mainActivity;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.add_account));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_add_new_account, container, false);

        confirmButton = view.findViewById(R.id.btn_confirm_NewAccount);

        edTextName = view.findViewById(R.id.ed_txt_name_Account);
        edTextBalance = view.findViewById(R.id.ed_txt_starting_balance);

        counterName = view.findViewById(R.id.txt_account_counter);
        TextView counterBalance = view.findViewById(R.id.txt_balance_counter);

        new MaxWordsCounter(20, edTextName, counterName, requireContext());
        new MaxWordsCounter(8, edTextBalance, counterBalance, requireContext());

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
        if (loggedInEmail != null) {
            ArrayList<String> users= new ArrayList<>();
            users.add(loggedInEmail);
            String nameOfAccount =edTextName.getText().toString();
            if (!(nameOfAccount.isEmpty())){
                String amountText = edTextBalance.getText().toString();
                long amount = 0;
                if(!(amountText.isEmpty())) amount = Long.parseLong(amountText);
                Account newAccount = new Account(nameOfAccount,amount,users, mainActivity.returnSavedLoggedEmail());
                newAccount.sendNewAccount(newAccount,getContext());
                navController.popBackStack();
            } else {
                counterName.setTextColor(getResources().getColor(R.color.light_red_warning));
                counterName.setText(R.string.account_name_missing);
            }
        }
        else Toast.makeText(getContext(), R.string.no_logged_in_email, Toast.LENGTH_SHORT).show();
    }


}