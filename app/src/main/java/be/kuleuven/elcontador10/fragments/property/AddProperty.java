package be.kuleuven.elcontador10.fragments.property;


import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
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
import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class AddProperty extends Fragment {
    private NavController navController;
    private EditText inputName;
    private TextView inputStakeholder, counterName;

    private ViewModel_NewTransaction viewModelTransaction;
    private StakeHolder chosenStakeholder;
    private Property chosenProperty;
    private MainActivity mainActivity;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_property, container, false);

        mainActivity = (MainActivity) requireActivity();
        inputName = view.findViewById(R.id.text_name_new_property);
        counterName = view.findViewById(R.id.text_counter_new_property);
        inputStakeholder = view.findViewById(R.id.text_stakeholder_new_property);
        inputStakeholder.setOnClickListener(view1 ->
                navController.navigate(R.id.action_addProperty_to_chooseStakeHolderDialog));
        viewModelTransaction = new ViewModelProvider(mainActivity).get(ViewModel_NewTransaction.class);

        try {
            assert getArguments() != null;
            chosenProperty = AddPropertyArgs.fromBundle(getArguments()).getProperty();
            viewModelTransaction.selectStakeholder(Caching.INSTANCE.getStakeHolder(chosenProperty.getStakeholder()));
            getArguments().clear();
        } catch (Exception ignore) { }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        Button confirm = view.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(this::onConfirm_Clicked);

        if (chosenProperty != null) {
            mainActivity.setHeaderText(getString(R.string.edit_property));
            inputName.setText(chosenProperty.getName());
        } else {
            mainActivity.setHeaderText(getString(R.string.add_new_property));
        }

        setTopMenu();
        new MaxWordsCounter(20, inputName, counterName, getContext());
    }

    private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
                if ((chosenProperty != null)) menu.findItem(R.id.menu_delete).setVisible(true);
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

    @Override
    public void onStart() {
        super.onStart();
        viewModelTransaction.getChosenStakeholder().observe(getViewLifecycleOwner(), stakeHolder -> {
            if (stakeHolder == null) inputStakeholder.setText(R.string.none);
            else inputStakeholder.setText(stakeHolder.getName());
            chosenStakeholder = stakeHolder;
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModelTransaction.reset();
    }

    public void onConfirm_Clicked(View view) {
        String name = inputName.getText().toString();

        if (name.isEmpty()) {
            counterName.setText(R.string.warning_new_properties);
            counterName.setTextColor(ResourcesCompat.getColor(getResources(),R.color.light_red_warning,null));
        }
        else {
            if (chosenProperty == null) {
                navController.popBackStack();
                Property newProperty;
                if (chosenStakeholder != null)
                    newProperty = new Property(name, chosenStakeholder.getId());
                else
                    newProperty = new Property(name, null);
                Property.addProperty(newProperty);
            } else {
                navController.popBackStack();
                navController.popBackStack();
                chosenProperty.setName(name);
                if (chosenStakeholder != null)
                    chosenProperty.setStakeholder(chosenStakeholder.getId());
                else
                    chosenProperty.setStakeholder(null);
                Property.editProperty(chosenProperty);
            }
        }
    }

    private void onDelete_Clicked() {
        if (Caching.INSTANCE.checkPermission(mainActivity.returnSavedLoggedEmail()))
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_property_title)
                    .setMessage(R.string.delete_property_message)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        Property.deleteProperty(chosenProperty, getContext());
                        navController.popBackStack();
                        navController.popBackStack();
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
        else
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_property_title)
                    .setMessage(R.string.not_enough_permission_delete)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
    }

}
