package be.kuleuven.elcontador10.fragments.stakeholders;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
    private String phoneNo;
    private int CALL_PERMISSION = 1;

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

        // initialise views variables
        name = requireView().findViewById(R.id.txtStakeholderDisplayName);
        role = requireView().findViewById(R.id.txtStakeholderDisplayRole);
        phone = requireView().findViewById(R.id.txtStakeholderDisplayPhoneNo);
        email = requireView().findViewById(R.id.txtStakeholderDisplayEmail);
        balance = requireView().findViewById(R.id.txtStakeholderDisplayBalance);
        phoneNo = bundle.getString("phone");

        // set views text
        name.setText(bundle.getString("name"));
        role.setText(bundle.getString("role"));
        phone.setText(phoneNo);
        email.setText(bundle.getString("email"));
        balance.setText(String.format("$%s", bundle.getDouble("balance")));

        // set onClickListeners
        phone.setOnClickListener(this::onPhoneNumber_Clicked);
    }

    public void onPhoneNumber_Clicked(View view) {
        if (ActivityCompat.checkSelfPermission(mainActivity,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //call permission not granted yet
            requestCallPermission();
        }
        else {
            Intent call = new Intent(Intent.ACTION_CALL);
            call.setData(Uri.parse("tel:" + phoneNo));
            startActivity(call);
        }
    }

    private void requestCallPermission() {
        // request permission politely
        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
            new AlertDialog.Builder(mainActivity)
                    .setTitle("Call permission needed")
                    .setMessage("Accept to allow phone calls from the app")
                    .setPositiveButton("Ok", (dialog, which) -> requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
        else {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                error("Permission granted.");
                requestCallPermission();
            } else error("Permission not granted.");
        }
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