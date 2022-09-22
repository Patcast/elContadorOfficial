package be.kuleuven.elcontador10.fragments.property;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;


import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class AddProperty extends Fragment {
    private NavController navController;
    private TextView inputName, inputStakeholder;

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
        inputName = view.findViewById(R.id.ed_txt_name);
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

        Button delete = view.findViewById(R.id.btn_delete_new_stakeholder);

        if (chosenProperty != null) {
            mainActivity.setHeaderText(getString(R.string.edit_property));
            inputName.setText(chosenProperty.getName());
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(this::onDelete_Clicked);
        } else {
            delete.setVisibility(View.GONE);
            mainActivity.setHeaderText(getString(R.string.add_new_property));
        }
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
            txtWordsCounterTitle.setText(R.string.warning_new_properties);
            txtWordsCounterTitle.setTextColor(ResourcesCompat.getColor(getResources(),R.color.light_red_warning,null));
        }
        else {
            if (chosenProperty == null) {
                navController.popBackStack();
                Property newProperty = new Property(name);
                if (chosenStakeholder != null)
                    newProperty.setStakeholder(chosenStakeholder.getId());
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

    private void onDelete_Clicked(View view) {

    }

}
