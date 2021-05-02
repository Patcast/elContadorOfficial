package be.kuleuven.elcontador10.fragments.stakeholders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;

public class StakeholderFilter extends Fragment {
    private MainActivity mainActivity;

    private TextView name;
    private Chip all_roles;
    private ChipGroup roles;
    private Chip inDebt;
    private Button cancel;
    private Button filter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle("Filter Stakeholder");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stakeholder_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set view variables
        name = requireView().findViewById(R.id.stakeholder_filter_name);
        all_roles = requireView().findViewById(R.id.stakeholder_filter_all);
        roles = requireView().findViewById(R.id.stakeholder_filter_group);
        inDebt = requireView().findViewById(R.id.stakeholder_filter_indebt);
        cancel = requireView().findViewById(R.id.btn_cancel_FilterStakeholder);
        filter = requireView().findViewById(R.id.btn_filter_FilterStakeholder);
    }
}