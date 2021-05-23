package be.kuleuven.elcontador10.fragments.stakeholders;

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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.WidgetsCreation;
import be.kuleuven.elcontador10.background.interfaces.CreateWidgets;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

import static be.kuleuven.elcontador10.R.id.stakeholder_filter_byRole;

public class StakeholderFilter extends Fragment implements CreateWidgets {
    private MainActivity mainActivity;

    private AutoCompleteTextView txtStakeHolder;

    private CheckBox all_roles;
    private LinearLayout chipGroup;
    private ArrayList<CheckBox> roles;
    private ArrayList<String> roleNames;
    private int noOfChecked;

    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle("Filter Stakeholder");
        roles = new ArrayList<>();
        roleNames = new ArrayList<>();
        noOfChecked = 0;
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stakeholder_filter, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set view variables
        txtStakeHolder = requireView().findViewById(R.id.stakeholder_filter_name);
        all_roles = requireView().findViewById(R.id.stakeholder_filter_all);
        chipGroup = requireView().findViewById(R.id.stakeholder_filter_roles);
        Button cancel = requireView().findViewById(R.id.btn_cancel_FilterStakeholder);
        Button filter = requireView().findViewById(R.id.btn_filter_FilterStakeholder);

        // initialise chip group
        roleNames = mainActivity.getRoles();
        setRoles(roleNames);

        // set navigation
        navController = Navigation.findNavController(view);
        cancel.setOnClickListener(this::onClick_Cancel);
        filter.setOnClickListener(this::onClick_Filter);

        // roles CheckBox animation
        all_roles.setOnClickListener(this::onClick_All);

        addAutoStake();
    }


    public void onClick_Cancel(View view) {
        FilterStakeholdersParcel filter = new FilterStakeholdersParcel("*", roleNames, false, "Name");
        StakeholderFilterDirections.ActionStakeholderFilterToStakeholderSummary action =
                StakeholderFilterDirections.actionStakeholderFilterToStakeholderSummary(filter);
        navController.navigate(action);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick_Filter(View view) {
        StakeholderFilterDirections.ActionStakeholderFilterToStakeholderSummary action =
                StakeholderFilterDirections.actionStakeholderFilterToStakeholderSummary(getFilter());
        navController.navigate(action);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick_All(View view) {
        if (all_roles.isChecked()) roles.forEach(v -> v.setChecked(true));
        else roles.forEach(v -> v.setChecked(false));
    }

    public void setRoles(ArrayList<String> role_names) {
        for (String tagName : role_names) {
            CheckBox chip = new CheckBox(getContext());
            chip.setText(tagName);
            chip.setChecked(true);
            chip.setOnCheckedChangeListener(this::onClick_Role);

            noOfChecked++;
            roles.add(chip);
            chipGroup.addView(chip);
        }
    }

    public void onClick_Role(View view, boolean checked) {
        if (checked) noOfChecked++;
        else noOfChecked--;

        all_roles.setChecked(noOfChecked == roles.size()); // sets all roles: true if all checkboxes are checked
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public FilterStakeholdersParcel getFilter() {
        String name_text = txtStakeHolder.getText().toString();
        if (!name_text.equals("") && name_text.contains("-") && name_text.contains(" ")) {
            name_text = name_text.split("-")[2];
            name_text = name_text.split(" ")[1];
        }
        String sortBy;

        // get the names of checked CheckBox
        ArrayList<String> categories = roles.stream()
                .filter(CheckBox::isChecked)
                .map(v -> v.getText().toString())
                .collect(Collectors.toCollection(ArrayList::new));

        boolean deleted = ((CheckBox) requireView().findViewById(R.id.stakeholder_filter_deleted)).isChecked();

        RadioButton byRole = requireView().findViewById(stakeholder_filter_byRole);

        if (byRole.isChecked()) sortBy = "Role";
        else sortBy = "Name";

        return new FilterStakeholdersParcel(name_text, categories, deleted, sortBy);
    }

    @Override
    public void addSpinnerCat() {

    }

    @Override
    public void addSpinnerSubCat(String catChosen) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addAutoStake() {
        WidgetsCreation.INSTANCE.makeAutoStake(mainActivity,txtStakeHolder,false);

    }

    @Override
    public void addCalendar() {

    }
}