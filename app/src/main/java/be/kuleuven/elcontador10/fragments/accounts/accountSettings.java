package be.kuleuven.elcontador10.fragments.accounts;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.ViewModelCategory;
import be.kuleuven.elcontador10.background.adapters.AccountSettingsRecViewAdapter;
import be.kuleuven.elcontador10.background.adapters.CategoriesRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Account;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;


public class accountSettings extends Fragment {


    EditText edTextNewEmail;
    TextView textEmailWarning;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recUsersOfAccount;
    private AccountSettingsRecViewAdapter adapter_custom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setHeaderText(getString(R.string.account_settings));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_settings, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edTextNewEmail = view.findViewById(R.id.edTextEmailPart);
        Button btnAddEmail = view.findViewById(R.id.butAddEmail);
        recUsersOfAccount = view.findViewById(R.id.recParticipants);
        TextView textAccountName = view.findViewById(R.id.textAccountName);
        textEmailWarning = view.findViewById(R.id.textEmailWarning);


        recUsersOfAccount.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter_custom = new AccountSettingsRecViewAdapter();
        recUsersOfAccount.setAdapter(adapter_custom);

        textAccountName.setText(Caching.INSTANCE.getAccountName());
        btnAddEmail.setOnClickListener(v->verifyEmailToAdd());

        ViewModel_AccountSettings viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_AccountSettings.class);
        viewModel.requestAccountUsers();
        viewModel.getAccount().observe(getViewLifecycleOwner(), v->updateAccount(v));
    }

    private void updateAccount(Account account) {
        adapter_custom.setUsers(account.getUsers(),account.getOwner());
    }


    private void verifyEmailToAdd() {
        String emailToAdd = edTextNewEmail.getText().toString();
        if(emailToAdd.isEmpty()){
            textEmailWarning.setVisibility(View.VISIBLE);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirm email")
                    .setMessage("Do you want to add this email to the account?\n\n" + emailToAdd)
                    .setPositiveButton("Yes", (dialog, which) ->addEmail(emailToAdd))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }
    private void addEmail(String emailToAdd){
        textEmailWarning.setVisibility(View.GONE);
        db.collection("accounts").document(Caching.INSTANCE.getChosenAccountId()).update("users", FieldValue.arrayUnion(emailToAdd));
        edTextNewEmail.setText("");
    }


}