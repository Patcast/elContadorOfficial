package be.kuleuven.elcontador10.fragments.stakeholders;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.Base64Encoder;
import be.kuleuven.elcontador10.background.database.StakeholdersManager;
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersDisplayInterface;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

public class StakeholderDisplay extends Fragment implements StakeholdersDisplayInterface {
    private MainActivity mainActivity;

    private String id;
    private String phoneNo;
    private String emailAddress;
    private String full_name;

    private final int CALL_PERMISSION = 1;
    private final int MESSAGE_PERMISSION = 1;

    private Button delete;
    private NavController navController;

    private StakeholdersManager manager;

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
            assert getArguments() != null;
            StakeholderDisplayArgs args = StakeholderDisplayArgs.fromBundle(getArguments());
            id = args.getId();
        }
        catch (Exception e) {
            error("Nothing to show.");
        }

        // get manager
        manager = StakeholdersManager.getInstance();
        manager.getStakeholder(this, id);

        navController = Navigation.findNavController(view);
        // buttons
        delete = requireView().findViewById(R.id.btn_delete_DisplayStakeholder);
        delete.setOnClickListener(this::onDelete_Click);
    }

    public void onDelete_Click(View view) {
        new AlertDialog.Builder(mainActivity)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete Stakeholder " + full_name + "?")
                .setPositiveButton("Yes", (dialog, which) -> manager.deleteStakeholder(this, id))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void display(Bundle bundle) {
        TextView name, role, phone, email, balance;
        ImageView image = requireView().findViewById(R.id.imgViewDisplayStakeholder);

        // initialise views variables
        name = requireView().findViewById(R.id.txtStakeholderDisplayName);
        role = requireView().findViewById(R.id.txtStakeholderDisplayRole);
        phone = requireView().findViewById(R.id.txtStakeholderDisplayPhoneNo);
        email = requireView().findViewById(R.id.txtStakeholderDisplayEmail);
        balance = requireView().findViewById(R.id.txtStakeholderDisplayBalance);

        // get bundles
        phoneNo = bundle.getString("phone");
        emailAddress = bundle.getString("email");
        String image_text = bundle.getString("image");
        full_name = bundle.getString("name");

        // set views text
        name.setText(full_name);
        role.setText(bundle.getString("role"));
        phone.setText(phoneNo);
        email.setText(emailAddress);
        balance.setText(String.format("$%s", bundle.getDouble("balance")));

        // set image
        if (!image_text.equals("null")) {
            Bitmap img = Base64Encoder.decodeImage(image_text);
            image.setImageBitmap(img);
        }

        // set onClickListeners
        phone.setOnClickListener(this::onPhoneNumber_Clicked);
        email.setOnClickListener(this::onEmail_Clicked);
    }

    // TextBox for number clicked
    public void onPhoneNumber_Clicked(View view) {
        if (!phoneNo.equals("null")) { // phone number should exist
            new AlertDialog.Builder(mainActivity)
                    .setTitle("Phone number")
                    .setMessage("Call or copy number to clipboard?")
                    .setPositiveButton("Call", (dialog, which) -> callNumber())
                    .setNegativeButton("Message", (dialog, which) -> messageNumber())
                    .setNeutralButton("Copy", (dialog, which) -> copyToClipboard(phoneNo))
                    .create()
                    .show();
        } else error("No phone number.");
    }

    // TextBox email clicked
    public void onEmail_Clicked(View view) {
        if (!emailAddress.equals("null")) {
            new AlertDialog.Builder(mainActivity)
                    .setTitle("Email")
                    .setMessage("Email or copy to clipboard?")
                    .setPositiveButton("Email", (dialog, which) -> email())
                    .setNegativeButton("Copy", (dialog, which) -> copyToClipboard(emailAddress))
                    .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else error("No email address.");
    }

    // copy a text to phone's keyboard
    private void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("elContador", text);
        clipboardManager.setPrimaryClip(clip);
        error("Copied to clipboard.");
    }

    // call the phone number
    private void callNumber() {
        try {
            Intent call = new Intent(Intent.ACTION_VIEW);
            call.setData(Uri.parse("tel:" + phoneNo));
            startActivity(call);
        } catch (Exception e) {
            e.printStackTrace();
            error("Cannot open phone application.");
        }
    }

    // message the phone number
    private void messageNumber() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNo)));
        } catch (Exception e) {
            e.printStackTrace();
            error("Cannot open SMS application.");
        }
    }

    private void email() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + emailAddress));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            error("Cannot open mail application.");
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

    @Override
    public void delete() {
        error("Stakeholder deleted");

        //TODO get roles from database
        String[] temp_array = getResources().getStringArray(R.array.roles);
        ArrayList<String> roles = new ArrayList<>(Arrays.asList(temp_array));
        // navigate back to summary
        navController.navigate(StakeholderDisplayDirections.actionStakeholderDisplayToStakeholderSummary(
                new FilterStakeholdersParcel("*", roles, false) // default filter
        ));
    }
}