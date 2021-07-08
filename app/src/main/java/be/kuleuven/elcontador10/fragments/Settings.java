package be.kuleuven.elcontador10.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.SettingsManager;
import be.kuleuven.elcontador10.background.interfaces.SettingsInterface;
import be.kuleuven.elcontador10.background.parcels.StakeholderLoggedIn;

public class Settings extends Fragment implements SettingsInterface {
    private MainActivity mainActivity;
    private StakeholderLoggedIn loggedIn;

    private ArrayList<String> ids;
    private ArrayList<String> nonRegisteredStakeholders;

    private SettingsManager manager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(R.string.settings);
        loggedIn = mainActivity.getLoggedIn();
        manager = SettingsManager.getInstance();

        return  inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manager.findNonRegistered(this);

        Button logout = requireView().findViewById(R.id.btnLogOut);
        logout.setOnClickListener(this::onLogOut_CLicked);

        Button changePassword = requireView().findViewById(R.id.btnChangePassword);
        changePassword.setOnClickListener(this::onChangePassword_Clicked);

        Button add_log_in = requireView().findViewById(R.id.btnRegister);
        add_log_in.setOnClickListener(this::onRegister_Clicked);
    }

    public void onLogOut_CLicked(View view) {
        Toast.makeText(mainActivity, "Logging out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(mainActivity, LogIn.class));
        mainActivity.finish();
    }

    public void onChangePassword_Clicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Change password");

        LinearLayout layout = new LinearLayout(mainActivity);
        layout.setOrientation(LinearLayout.VERTICAL);

        // check for current password
        EditText checkPassword = new EditText(mainActivity);
        checkPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkPassword.setHint("Current password");
        layout.addView(checkPassword);

        // new password
        EditText newPassword = new EditText(mainActivity);
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassword.setHint("New password");
        layout.addView(newPassword);

        // confirm password
        EditText confirmPassword = new EditText(mainActivity);
        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPassword.setHint("Confirm password");
        layout.addView(confirmPassword);

        // set buttons
        builder.setPositiveButton("Ok", (dialog, which) -> {
            String passwordCurrent = checkPassword.getText().toString();
            String passwordNew = newPassword.getText().toString();
            String passwordConfirm = confirmPassword.getText().toString();

            if (passwordConfirm.equals("") || passwordCurrent.equals("") || passwordNew.equals("")) // empty input
                this.feedback("Missing input!");
            else if (passwordNew.equals(passwordConfirm))
                manager.changePassword(this, loggedIn, passwordCurrent, passwordNew);
            else this.feedback("New password does not match."); })

                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // initialise
        builder.setView(layout)
                .create().show();
    }

    public void onRegister_Clicked (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Register");

        LinearLayout layout = new LinearLayout(mainActivity);
        layout.setOrientation(LinearLayout.VERTICAL);

        // TextView
        TextView text = new TextView(mainActivity);
        text.setText(R.string.stakeholder);
        layout.addView(text);

        // Spinner
        Spinner spinner = new Spinner(mainActivity);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, nonRegisteredStakeholders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        layout.addView(spinner);

        // username
        EditText userName = new EditText(mainActivity);
        userName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        userName.setHint("Username");
        layout.addView(userName);

        // new password
        EditText newPassword = new EditText(mainActivity);
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassword.setHint("Password");
        layout.addView(newPassword);

        // confirm password
        EditText confirmPassword = new EditText(mainActivity);
        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPassword.setHint("Confirm password");
        layout.addView(confirmPassword);

        // set buttons
        builder.setPositiveButton("Ok", (dialog, which) -> {
            int position = spinner.getSelectedItemPosition();
            String textName = userName.getText().toString();
            String textPass = newPassword.getText().toString();
            String textConfirm = confirmPassword.getText().toString();

            if (textName.equals("") || textPass.equals("") || textConfirm.equals(""))
                feedback("Missing input!");
            else if (textPass.equals(textConfirm))
                manager.register(this, ids.get(position), textName, textPass);
            else feedback("Passwords does not match!"); })

                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // initialise
        builder.setView(layout)
                .create().show();
    }

    @Override
    public void feedback(String string) {
        Toast.makeText(mainActivity, string, Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getContext() {
        return mainActivity;
    }

    @Override
    public void populateSpinner(ArrayList<String> ids, ArrayList<String> stakeholders) {
        this.ids = ids;
        this.nonRegisteredStakeholders = stakeholders;
    }
}