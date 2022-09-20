package be.kuleuven.elcontador10.fragments.stakeholders.common;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;

public class NewStakeholder extends Fragment {
    private NavController navController;
    private StakeHolder stakeHolder;
    private EditText inputName, inputRole;
    private TextView counterName, counterRole;

    private MainActivity mainActivity;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            String stakeholderID = NewStakeholderArgs.fromBundle(getArguments()).getIdStakeholder();
            if (stakeholderID != null) {
                stakeHolder = Caching.INSTANCE.getStakeHolder(stakeholderID);
            }
        } catch (Exception e) {
            stakeHolder = null;
        }

        mainActivity = (MainActivity) requireActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_microaccount, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialise background
        navController = Navigation.findNavController(view);

        // initialise view
        inputName = view.findViewById(R.id.ed_txt_name);
        inputRole = view.findViewById(R.id.ed_txt_role);
        counterName = view.findViewById(R.id.ed_txt_name_counter);
        counterRole = view.findViewById(R.id.ed_txt_role_counter);

        Button delete = view.findViewById(R.id.btn_delete_NewMicro);

        if (stakeHolder != null) {
            inputName.setText(stakeHolder.getName());
            if (stakeHolder.getRole() != null) {
                inputRole.setText(stakeHolder.getRole());
            }

            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(this::onDelete_Clicked);
        } else delete.setVisibility(View.GONE);

        setWordCounters();

        Button confirm = view.findViewById(R.id.btn_confirm_NewMicro);
        confirm.setOnClickListener(this::onConfirm_Clicked);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.new_stake));
    }

    private void onConfirm_Clicked(View view) {
        String name = inputName.getText().toString();
        String role = inputRole.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "The new stakeholder must have a name.", Toast.LENGTH_LONG).show();
        }
        else {
            navController.popBackStack();
            if (stakeHolder == null) {
                StakeHolder account = new StakeHolder(name, role);
                account.addAccount();
            } else {
                navController.popBackStack();
                stakeHolder.setName(name);
                stakeHolder.setRole(role);
                stakeHolder.editAccount();
            }
        }
    }

    private void onDelete_Clicked(View view) {
        if (Caching.INSTANCE.checkPermission(mainActivity.returnSavedLoggedEmail()))
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_stakeholder_title)
                    .setMessage(R.string.delete_stakeholder_message)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        stakeHolder.delete(getContext());
                        navController.popBackStack();
                        navController.popBackStack();
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
        else
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_stakeholder_title)
                    .setMessage(R.string.not_enough_permission_delete)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
    }

    public void setWordCounters() {
        new MaxWordsCounter(20, inputName, counterName, getContext());
        new MaxWordsCounter(100, inputRole, counterRole, getContext());
    }
}