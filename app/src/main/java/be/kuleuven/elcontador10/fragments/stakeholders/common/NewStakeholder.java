package be.kuleuven.elcontador10.fragments.stakeholders.common;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.MicroAccount;

public class NewStakeholder extends Fragment {
    private NavController navController;

    private TextView inputName;
    private Spinner inputRole;
    private Button confirm;

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

        inputRole = view.findViewById(R.id.sp_MicroRole);
        makeSpinnerRole();

        confirm = view.findViewById(R.id.btn_confirm_NewMicro);
        confirm.setOnClickListener(this::onConfirm_Clicked);
    }

    public void onConfirm_Clicked(View view) {
        String name = inputName.getText().toString();
        String role = inputRole.getSelectedItem().toString();

        if (name.isEmpty()) {
            Toast.makeText(getActivity(), R.string.zero_amount, Toast.LENGTH_LONG).show();
        }
        else {
            navController.popBackStack();

            MicroAccount account = new MicroAccount(name, role);
            account.addAccount(account);
        }
    }

    public void makeSpinnerRole() {
        List<String> roles = new ArrayList<>((Arrays.asList(this.getResources().getStringArray(R.array.roles))));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, roles);
        inputRole.setAdapter(adapter);
    }
}