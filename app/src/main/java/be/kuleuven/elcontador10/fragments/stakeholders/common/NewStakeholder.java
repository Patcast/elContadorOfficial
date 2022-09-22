package be.kuleuven.elcontador10.fragments.stakeholders.common;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
        inputName = view.findViewById(R.id.text_name_new_property);
        inputRole = view.findViewById(R.id.ed_txt_role);
        counterName = view.findViewById(R.id.ed_txt_name_counter);
        counterRole = view.findViewById(R.id.ed_txt_role_counter);
        MainActivity mainActivity = (MainActivity) requireActivity();

        if (stakeHolder != null) {
            inputName.setText(stakeHolder.getName());
            if (stakeHolder.getRole() != null) inputRole.setText(stakeHolder.getRole());
            mainActivity.setHeaderText(getString(R.string.stakeholder_settings));
        }
        else{
            mainActivity.setHeaderText(getString(R.string.new_stake));
        }
        setWordCounters();
        Button confirm = view.findViewById(R.id.btn_confirm_NewMicro);
        confirm.setOnClickListener(this::onConfirm_Clicked);
        setTopMenu();
    }
    private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                if ((stakeHolder != null)) menu.findItem(R.id.menu_delete).setVisible(true);
            }
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                final int menu_delete = R.id.menu_delete;
                if (menuItem.getItemId() == menu_delete) {
                    onDelete_Clicked();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void onConfirm_Clicked(View view) {
        String name = inputName.getText().toString();
        String role = inputRole.getText().toString();

        if (name.isEmpty()) {
            counterName.setText(R.string.this_field_is_requiered);
            counterName.setTextColor(getResources().getColor(R.color.light_red_warning));
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

    private void onDelete_Clicked() {
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