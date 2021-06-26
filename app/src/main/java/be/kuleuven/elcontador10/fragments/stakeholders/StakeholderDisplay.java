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
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import be.kuleuven.elcontador10.background.parcels.EditStakeholderParcel;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

public class StakeholderDisplay extends Fragment implements StakeholdersDisplayInterface {
    private MainActivity mainActivity;

    private String id;
    private String phoneNo;
    private String emailAddress;
    private String full_name;
    private String role;
    private String image_text;

    private NavController navController;

    private StakeholdersManager manager;

    private Bitmap img = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle(getString(R.string.stakeholder));

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
        Button delete = requireView().findViewById(R.id.btn_delete_DisplayStakeholder);
        delete.setOnClickListener(this::onDelete_Click);

        Button edit = requireView().findViewById(R.id.btn_edit_DisplayStakeholder);
        edit.setOnClickListener(this::onEdit_Clicked);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        TextView name, roleView, phone, email;
        ImageView image = requireView().findViewById(R.id.imgViewDisplayStakeholder);

        // initialise views variables
        name = requireView().findViewById(R.id.txtStakeholderDisplayName);
        roleView = requireView().findViewById(R.id.txtStakeholderDisplayRole);
        phone = requireView().findViewById(R.id.txtStakeholderDisplayPhoneNo);
        email = requireView().findViewById(R.id.txtStakeholderDisplayEmail);

        // get bundles
        phoneNo = bundle.getString("phone");
        emailAddress = bundle.getString("email");
        image_text = bundle.getString("image");
        full_name = bundle.getString("name");
        role = bundle.getString("role");

        // set views text
        name.setText(full_name);
        roleView.setText(role);
        phone.setText(phoneNo);
        email.setText(emailAddress);

        // set image
        if (!image_text.equals("null") && !image_text.equals("")) {
            img = Base64Encoder.decodeImage(image_text);
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

    public void onEdit_Clicked(View view) {
        EditStakeholderParcel parcel = new EditStakeholderParcel(id, full_name, role, phoneNo, emailAddress, img, image_text);
        StakeholderDisplayDirections.ActionStakeholderDisplayToStakeholderNew action =
                StakeholderDisplayDirections.actionStakeholderDisplayToStakeholderNew();
        action.setEditStakeholder(parcel);
        navController.navigate(action);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void delete() {
        error("Stakeholder deleted");

        ArrayList<String> roles = mainActivity.getRoles();
        // navigate back to summary
        navController.navigate(StakeholderDisplayDirections.actionStakeholderDisplayToStakeholderSummary(
                new FilterStakeholdersParcel("*", roles, false, "Name") // default filter
        ));
    }
}