package be.kuleuven.elcontador10.fragments.property;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class AddProperty extends Fragment {
    private NavController navController;
    private TextView inputName, inputStakeholder;

    private ViewModel_NewTransaction viewModel;
    private StakeHolder chosenStakeholder;

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_property, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);

        navController = Navigation.findNavController(view);
        inputName = view.findViewById(R.id.ed_txt_name);
        inputStakeholder = view.findViewById(R.id.text_stakeholder_new_property);
        inputStakeholder.setOnClickListener(view1 ->
                navController.navigate(R.id.action_addProperty_to_chooseStakeHolderDialog));

        Button confirm = view.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(this::onConfirm_Clicked);

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.add_new_property));
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenStakeholder().observe(getViewLifecycleOwner(), stakeHolder -> {
            if (stakeHolder == null) inputStakeholder.setText(R.string.none);
            else {
                inputStakeholder.setText(stakeHolder.getName());
                chosenStakeholder = stakeHolder;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.reset();
    }

    public void onConfirm_Clicked(View view) {
        String name = inputName.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.warning_new_properties), Toast.LENGTH_LONG).show();
        }
        else {
            navController.popBackStack();
            Property newProperty = new Property(name);
            if (chosenStakeholder != null)
                newProperty.setStakeholder(chosenStakeholder.getId());
            Property.addProperty(newProperty);
        }
    }

}
