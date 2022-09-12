package be.kuleuven.elcontador10.fragments.stakeholders.common;

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
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;

public class NewStakeholder extends Fragment {
    private NavController navController;
    private StakeHolder stakeHolder;
    private TextView inputName, inputRole;

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

        if (stakeHolder != null) {
            inputName.setText(stakeHolder.getName());
            if (stakeHolder.getRole() != null)
                inputRole.setText(stakeHolder.getRole());
        }

        Button confirm = view.findViewById(R.id.btn_confirm_NewMicro);
        confirm.setOnClickListener(this::onConfirm_Clicked);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.new_stake));
    }

    public void onConfirm_Clicked(View view) {
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
                stakeHolder.setName(name);
                stakeHolder.setRole(role);
                stakeHolder.editAccount();
            }
        }
    }
}