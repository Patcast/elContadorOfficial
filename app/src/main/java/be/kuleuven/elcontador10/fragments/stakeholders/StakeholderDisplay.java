package be.kuleuven.elcontador10.fragments.stakeholders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.StakeholdersManager;
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersDisplayInterface;

public class StakeholderDisplay extends Fragment implements StakeholdersDisplayInterface {
    private MainActivity mainActivity;
    private String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("Stakeholder");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stakeholder_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            StakeholderDisplayArgs args = StakeholderDisplayArgs.fromBundle(getArguments());
            id = args.getId();
        }
        catch (Exception e) {
            Toast.makeText(mainActivity, "Nothing to show", Toast.LENGTH_SHORT).show();
        }

        StakeholdersManager manager = StakeholdersManager.getInstance();
        manager.getStakeholder(this, id);

    }

    @Override
    public void display(Bundle bundle) {
        TextView name, role, phone, email, balance;

        name = requireView().findViewById(R.id.txtStakeholderDisplayName);
        role = requireView().findViewById(R.id.txtStakeholderDisplayRole);
        phone = requireView().findViewById(R.id.txtStakeholderDisplayPhoneNo);
        email = requireView().findViewById(R.id.txtStakeholderDisplayEmail);
        balance = requireView().findViewById(R.id.txtStakeholderDisplayBalance);

        name.setText(bundle.getString("name"));
        role.setText(bundle.getString("role"));
        phone.setText(bundle.getString("phone"));
        email.setText(bundle.getString("email"));
        balance.setText("$"+bundle.getDouble("balance"));
    }

    @Override
    public void error(String error) {
        Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public Context getContext() {
        return mainActivity;
    }
}