package be.kuleuven.elcontador10.fragments.accounts;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.background.adapters.AccountSettingsRecViewAdapter;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.Account;

public class AccountSettings extends Fragment {
    private EditText edTextNewEmail;
    private TextView textEmailWarning;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NavController navController;
    private ViewModel_AccountSettings viewModel;

    private AccountSettingsRecViewAdapter adapter_custom;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
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

        navController = Navigation.findNavController(view);

        edTextNewEmail = view.findViewById(R.id.edTextEmailPart);
        Button btnAddEmail = view.findViewById(R.id.butAddEmail);
        RecyclerView recUsersOfAccount = view.findViewById(R.id.recParticipants);
        TextView textAccountName = view.findViewById(R.id.textAccountName);
        textEmailWarning = view.findViewById(R.id.textEmailWarning);

        recUsersOfAccount.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter_custom = new AccountSettingsRecViewAdapter(mainActivity.returnSavedLoggedEmail());
        recUsersOfAccount.setAdapter(adapter_custom);

        textAccountName.setText(Caching.INSTANCE.getAccountName());
        btnAddEmail.setOnClickListener(v->verifyEmailToAdd());

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_AccountSettings.class);
        viewModel.requestAccountUsers();
        viewModel.getAccount().observe(getViewLifecycleOwner(), this::updateAccount);
        setTopMenu();
    }

    private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                menu.findItem(R.id.menu_delete).setVisible(true);
            }
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.menu_delete:
                        onDeleteClick();
                        return true;
                    default:
                        return false;
                }
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
    @Override
    public void onResume() {
        super.onResume();
        viewModel.getAccount().observe(getViewLifecycleOwner(), this::updateAccount);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onDeleteClick() {
        if (Caching.INSTANCE.checkPermission(mainActivity.returnSavedLoggedEmail())) {
            String accountName = Caching.INSTANCE.getAccountName();

            final LinearLayout layout = new LinearLayout(requireContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            int dp = (int) (getResources().getDisplayMetrics().density * 24 + 0.5f);
            layout.setPadding(dp, 0, dp, 0);

            final EditText confirmText = new EditText(requireContext());
            confirmText.setHint(accountName);

            final TextView failLabel = new TextView(requireContext());
            failLabel.setVisibility(View.INVISIBLE);

            layout.addView(confirmText);
            layout.addView(failLabel);

            final AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.delete_account)
                    .setMessage(getString(R.string.delete_account_prompt, accountName))
                    .setView(layout)
                    .setPositiveButton(R.string.yes, null)
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                Button confirm = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                confirm.setOnClickListener(view1 -> {
                    String prompt = confirmText.getText().toString();

                    if (accountName.equals(prompt)) {
                        failLabel.setVisibility(View.INVISIBLE);
                        onDeleteLastWarning(accountName);
                        dialog.dismiss();
                    } else {
                        failLabel.setVisibility(View.VISIBLE);
                        failLabel.setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_red_warning, null));
                        failLabel.setText(R.string.delete_account_fail_mismatch);
                    }
                });
            });

            dialog.show();
        } else new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_account)
                .setMessage(R.string.not_enough_permission_delete)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }

    private void onDeleteLastWarning(String accountName) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_account)
                .setMessage(getString(R.string.delete_accout_last_warning, accountName))
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> onDeleteConfirm())
                .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }

    private void onDeleteConfirm() {
        db.document("accounts/" + Caching.INSTANCE.getChosenAccountId())
                .delete()
                .addOnSuccessListener(onSuccessListener -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle(R.string.delete_account)
                            .setMessage(R.string.account_deleted)
                            .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                            .create()
                            .show();

                    navController.navigate(R.id.action_accountSettings_to_accounts);
                })
                .addOnFailureListener(error ->
                        new AlertDialog.Builder(requireContext())
                                .setTitle(R.string.delete_account)
                                .setMessage(R.string.delete_account_fail)
                                .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                                .create()
                                .show());
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
            builder.setTitle(getString(R.string.confirm_email))
                    .setMessage(getString(R.string.add_email_question)+"\n\n" + emailToAdd)
                    .setPositiveButton(getString(R.string.yes), (dialog, which) ->addEmail(emailToAdd))
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
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