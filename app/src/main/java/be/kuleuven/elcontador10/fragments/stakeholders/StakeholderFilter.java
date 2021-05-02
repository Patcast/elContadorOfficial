package be.kuleuven.elcontador10.fragments.stakeholders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

public class StakeholderFilter extends Fragment {
    private MainActivity mainActivity;

    private TextView name;
    private CheckBox all_roles;
    private LinearLayout chipGroup;
    private ArrayList<CheckBox> roles;
    private CheckBox inDebt;
    private Button cancel;
    private Button filter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle("Filter Stakeholder");
        roles = new ArrayList<>();
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
        inDebt = requireView().findViewById(R.id.stakeholder_filter_debt);
        cancel = requireView().findViewById(R.id.btn_cancel_FilterStakeholder);
        filter = requireView().findViewById(R.id.btn_filter_FilterStakeholder);

        // initialise chip group
        String[] roleNames = getResources().getStringArray(R.array.roles);
        setRoles(roleNames);
    }

    private void setRoles(String[] role_names) {
        for (String tagName : role_names) {
            CheckBox chip = new CheckBox(getContext());
            chip.setText(tagName);

            roles.add(chip);
            chipGroup.addView(chip);
        }
    }

    public FilterStakeholdersParcel getFilter() {
        String name_text = name.getText().toString();
        ArrayList<String> categories = new ArrayList<>(5);
        boolean debt = inDebt.isChecked();

        return new FilterStakeholdersParcel(name_text, categories, debt);
    }
}