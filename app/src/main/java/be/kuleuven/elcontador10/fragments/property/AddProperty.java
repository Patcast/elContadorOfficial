package be.kuleuven.elcontador10.fragments.property;


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
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.model.MicroAccount;
import be.kuleuven.elcontador10.background.model.Property;

public class AddProperty extends Fragment {
    private NavController navController;

    private TextView inputName;

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_property, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        inputName = view.findViewById(R.id.ed_txt_name);


        Button confirm = view.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(this::onConfirm_Clicked);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setHeaderText(getString(R.string.add_new_property));
    }

    public void onConfirm_Clicked(View view) {
        String name = inputName.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.warning_new_properties), Toast.LENGTH_LONG).show();
        }
        else {
            navController.popBackStack();
            Property newProperty = new Property(name);
            newProperty.addProperty(newProperty);
        }
    }

}
