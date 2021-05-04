package be.kuleuven.elcontador10.fragments.stakeholders;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Session2Command;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.Base64Encoder;
import be.kuleuven.elcontador10.background.database.StakeholdersManager;
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersNewInterface;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

public class StakeholderNew extends Fragment implements StakeholdersNewInterface {

    private MainActivity mainActivity;
    private NavController navController;

    private Button picture;

    private String image;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMG = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle("New Stakeholder");

        return inflater.inflate(R.layout.fragment_stakeholder_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // set buttons
        Button cancel = requireView().findViewById(R.id.btn_cancel_NewTransaction);
        Button confirm = requireView().findViewById(R.id.btn_confirm_NewTransaction);
        picture = requireView().findViewById(R.id.btn_picture_NewStakeholder);

        cancel.setOnClickListener(this::onCancelClicked);
        confirm.setOnClickListener(this::onConfirmClicked);
        picture.setOnClickListener(this::onPictureClicked);
    }

    public void onCancelClicked(View view) {
        //TODO get roles from database
        String[] temp_array = getResources().getStringArray(R.array.roles);
        ArrayList<String> roles = new ArrayList<>(Arrays.asList(temp_array));
        FilterStakeholdersParcel filter = new FilterStakeholdersParcel("*", roles, false, false, "Name");

        StakeholderNewDirections.ActionStakeholderNewToStakeholderSummary action =
                StakeholderNewDirections.actionStakeholderNewToStakeholderSummary(filter);
        navController.navigate(action);
    }

    public void onPictureClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        if (image == null) {
            // add image
            builder.setTitle("Add photo")
                    .setMessage("Get a photo from camera or gallery?")
                    .setPositiveButton("Camera", this::Camera)
                    .setNegativeButton("Gallery", this::Gallery)
                    .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            // user want to change picture
            builder.setTitle("Change photo")
                    .setMessage("Do you want to change the photo?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // change image
                        image = null;
                        onPictureClicked(view);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setNeutralButton("Delete", (dialog, which) -> {
                        // dete image
                        image = null;
                        picture.setText("Choose Picture");
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        }
    }

    // confirm button clicked
    public void onConfirmClicked(View view) {
        String firstName = ((TextView) requireView().findViewById(R.id.StakeholderNewFirstName)).getText().toString();
        String lastName = ((TextView) requireView().findViewById(R.id.StakeholderNewLastName)).getText().toString();
        String role = ((Spinner) requireView().findViewById(R.id.StakeholderNewRoles)).getSelectedItem().toString();
        String phoneNo = ((TextView) requireView().findViewById(R.id.StakeholderNewPhoneNumber)).getText().toString();
        String email = ((TextView) requireView().findViewById(R.id.StakeholderNewEmail)).getText().toString();

        if (firstName.equals("") || lastName.equals("")) feedback("Missing input!");
        else {
            if (phoneNo.equals("")) phoneNo = null;
            if (email.equals("")) email = null;

            StakeholdersManager manager = StakeholdersManager.getInstance();
            manager.addStakeholder(this, firstName, lastName, role, phoneNo, email, image);
        }
    }

    // request camera activity
    private void Camera(DialogInterface dialog, int which) {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            feedback("Unable to open camera!");
        }
    }

    // request gallery activity
    private void Gallery(DialogInterface dialog, int which) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    // get images either from gallery or camera
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                image = Base64Encoder.encodeImage(imageBitmap);

                picture.setText("Change Picture");
            } catch (Exception e) { feedback("Error getting image."); }
        } else if (requestCode == RESULT_LOAD_IMG) {
            try {
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mainActivity.getContentResolver(), selectedImage);
                image = Base64Encoder.encodeImage(bitmap);

                picture.setText("Change Picture");
            } catch (Exception e) { feedback("Error getting image."); }
        }
    }

    @Override
    public void addStakeholder() {
        feedback("Stakeholder created.");
        onCancelClicked(getView());
    }

    @Override
    public void feedback(String feedback) {
        Toast.makeText(mainActivity, feedback, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return mainActivity;
    }
}