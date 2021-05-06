package be.kuleuven.elcontador10.fragments.stakeholders;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

import static be.kuleuven.elcontador10.R.id.stakeholder_filter_byDebt;
import static be.kuleuven.elcontador10.R.id.stakeholder_filter_byRole;

public class StakeholderFilter extends Fragment {
    private MainActivity mainActivity;

    private TextView name;

    private CheckBox all_roles;
    private LinearLayout chipGroup;
    private ArrayList<CheckBox> roles;
    private CheckBox inDebt;
    private ArrayList<String> roleNames;
    private int noOfChecked;

    private Button cancel;
    private Button filter;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set view variables
        name = requireView().findViewById(R.id.stakeholder_filter_name);
        all_roles = requireView().findViewById(R.id.stakeholder_filter_all);
        chipGroup = requireView().findViewById(R.id.stakeholder_filter_roles);
        cancel = requireView().findViewById(R.id.btn_cancel_FilterStakeholder);
        filter = requireView().findViewById(R.id.btn_filter_FilterStakeholder);

        // initialise chip group
        // TODO get roles from database instead of local
        String[] temp_array = getResources().getStringArray(R.array.roles);
        roleNames.addAll(Arrays.asList(temp_array));
        setRoles(roleNames);

        // set navigation
        navController = Navigation.findNavController(view);
        cancel.setOnClickListener(this::onClick_Cancel);
        filter.setOnClickListener(this::onClick_Filter);

        // roles CheckBox animation
        all_roles.setOnClickListener(this::onClick_All);
    }

    public void onClick_Cancel(View view) {
        FilterStakeholdersParcel filter = new FilterStakeholdersParcel("*", roleNames, false, false, "Name");
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
        String name_text = name.getText().toString();
        RadioGroup group = requireView().findViewById(R.id.stakeholder_filter_radioGroup);
        String sortBy;

        // get the names of checked CheckBox
        ArrayList<String> categories = roles.stream()
                .filter(CheckBox::isChecked)
                .map(v -> v.getText().toString())
                .collect(Collectors.toCollection(ArrayList::new));

        boolean deleted = ((CheckBox) requireView().findViewById(R.id.stakeholder_filter_deleted)).isChecked();

        final int byDebt = stakeholder_filter_byDebt;
        final int byRole = stakeholder_filter_byRole;

        switch(group.getCheckedRadioButtonId()) {
            case byDebt:
                sortBy = "Debt";
                break;
            case byRole:
                sortBy = "Role";
                break;
            default:
                sortBy = "Name";
        }

        return new FilterStakeholdersParcel(name_text, categories, false, deleted, sortBy);
    }
}