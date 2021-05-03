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

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.Base64Encoder;
import be.kuleuven.elcontador10.background.database.StakeholdersManager;
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersDisplayInterface;

public class StakeholderDisplay extends Fragment implements StakeholdersDisplayInterface {
    private MainActivity mainActivity;
    private String id;
    private String phoneNo;
    private String emailAddress;
    private int CALL_PERMISSION = 1;
    private Button delete;
    private NavController navController;

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

        // get manager
        StakeholdersManager manager = StakeholdersManager.getInstance();
        manager.getStakeholder(this, id);

        navController = Navigation.findNavController(view);
        // buttons
        delete = requireView().findViewById(R.id.btn_delete_DisplayStakeholder);

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

        // set views text
        name.setText(bundle.getString("name"));
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
                    .setPositiveButton("Call", ((dialog, which) -> callNumber()))
                    .setNegativeButton("Copy", (dialog, which) -> copyToClipboard(phoneNo))
                    .create()
                    .show();
        } else error("No phone number.");
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
        if (ActivityCompat.checkSelfPermission(mainActivity,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //call permission not granted yet
            requestCallPermission();
        } else {
            // call permission granted, goes to call number
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
                    .setMessage("Please accept to allow phone calls from the app.")
                    .setPositiveButton("Ok", (dialog, which) -> requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION);
    }

    // TextBox email clicked
    public void onEmail_Clicked(View view) {
        if (!emailAddress.equals("null")) {
            new AlertDialog.Builder(mainActivity)
                    .setTitle("Email")
                    .setMessage("Email or copy to clipboard?")
                    .setPositiveButton("Email", ((dialog, which) -> email()))
                    .setNegativeButton("Copy", (dialog, which) -> copyToClipboard(emailAddress))
                    .create()
                    .show();
        } else error("No email address.");
    }

    private void email() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + emailAddress));
        startActivity(intent);
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