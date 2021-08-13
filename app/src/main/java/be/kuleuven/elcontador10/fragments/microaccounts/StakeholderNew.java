package be.kuleuven.elcontador10.fragments.microaccounts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.Base64Encoder;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.database.StakeholdersManager;
import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersNewInterface;
import be.kuleuven.elcontador10.background.parcels.EditStakeholderParcel;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

public class StakeholderNew extends Fragment implements StakeholdersNewInterface {

    private MainActivity mainActivity;
    private NavController navController;
    private Button image_button;
    private ImageView image_preview;
    private Spinner roles;
    private TextView firstName, lastName, phoneNo, email;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMG = 2;

    private String image_string;
    private ArrayList<String> roles_array= new ArrayList<>();
    private String id;
    private boolean edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) requireActivity();

        return inflater.inflate(R.layout.fragment_stakeholder_new, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // set buttons and views
        Button cancel = requireView().findViewById(R.id.btn_cancel_NewTransaction);
        Button confirm = requireView().findViewById(R.id.btn_confirm_NewTransaction);
        image_button = requireView().findViewById(R.id.btn_picture_NewStakeholder);
        image_preview = requireView().findViewById(R.id.StakeholderNewImageView);
        roles = requireView().findViewById(R.id.StakeholderNewRoles);
        firstName = requireView().findViewById(R.id.StakeholderNewFirstName);
        lastName = requireView().findViewById(R.id.StakeholderNewLastName);
        phoneNo = requireView().findViewById(R.id.StakeholderNewPhoneNumber);
        email = requireView().findViewById(R.id.StakeholderNewEmail);

        // onClick listeners
        cancel.setOnClickListener(this::onCancelClicked);
        confirm.setOnClickListener(this::onConfirmClicked);
        image_button.setOnClickListener(this::onPictureClicked);

        // set up spinner
        roles_array.addAll(Caching.INSTANCE.roles);
        ArrayAdapter adapter = new ArrayAdapter(mainActivity, android.R.layout.simple_spinner_dropdown_item, roles_array);
        roles.setAdapter(adapter);

        // set up arguments
        EditStakeholderParcel parcel = StakeholderNewArgs.fromBundle(getArguments()).getEditStakeholder();

        if (parcel != null) {
            mainActivity.setTitle("Edit Stakeholder");
            edit = true;

            displayEditValues(parcel);
        }
        else {
            mainActivity.setTitle("New Stakeholder");
            edit = false;
        }
    }

    public void displayEditValues(EditStakeholderParcel parcel) {
        id = parcel.getId();
        String[] full_name = parcel.getName().split(" ", 2);
        firstName.setText(full_name[0]);
        lastName.setText(full_name[1]);
        int position = roles_array.indexOf(parcel.getRole());
        roles.setSelection(position);
        phoneNo.setText(parcel.getPhoneNo());
        email.setText(parcel.getEmail());

        if (parcel.getImage() != null) {
            image_preview.setImageBitmap(parcel.getImage());
            image_string = parcel.getImage_string();
            image_button.setText(R.string.change_pic);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onCancelClicked(View view) {
        if (!edit) { // go back to stakeholder summary
            ArrayList<String> roles = new ArrayList<>(Caching.INSTANCE.roles);
            FilterStakeholdersParcel filter = new FilterStakeholdersParcel("*", roles, false, "Name");

            StakeholderNewDirections.ActionStakeholderNewToStakeholderSummary action =
                    StakeholderNewDirections.actionStakeholderNewToStakeholderSummary(filter);
            navController.navigate(action);
        } else { // go back to the stakeholder that was edited
            StakeholderNewDirections.ActionStakeholderNewToStakeholderDisplay action =
                    StakeholderNewDirections.actionStakeholderNewToStakeholderDisplay(id);
            navController.navigate(action);
        }
    }

    public void onPictureClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        if (image_string == null) {
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
                        image_string = null;
                        onPictureClicked(view);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setNeutralButton("Delete", (dialog, which) -> {
                        // delete image
                        image_string = null;
                        image_button.setText(R.string.choose_picture);
                        image_preview.setImageResource(R.drawable.icon_stakeholder);
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        }
    }

    // confirm button clicked
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onConfirmClicked(View view) {
        String first_Name = firstName.getText().toString();
        String last_Name = lastName.getText().toString();
        String role = roles.getSelectedItem().toString();
        String phone_No = phoneNo.getText().toString();
        String emailString = email.getText().toString();
        if (image_string == null) image_string = "";
        StakeholdersManager manager = StakeholdersManager.getInstance();

        if (first_Name.equals("") || last_Name.equals("")) feedback("Missing input!");
        else {
            if (edit)
                manager.editStakeholder(this, id, first_Name, last_Name, role, phone_No, emailString, image_string);
            else
                manager.addStakeholder(this, first_Name, last_Name, role, phone_No, emailString, image_string);
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
    }

    // get images either from gallery or camera
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                image_string = Base64Encoder.encodeImage(imageBitmap);

                image_button.setText(R.string.change_pic);
                image_preview.setImageBitmap(imageBitmap);
            } catch (Exception e) { feedback("Error getting image."); }
        } else if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mainActivity.getContentResolver(), selectedImage);
                image_string = Base64Encoder.encodeImage(bitmap);

                image_button.setText(R.string.change_pic);
                image_preview.setImageBitmap(bitmap);
            } catch (Exception e) { feedback("Error getting image."); }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addStakeholder() {
        feedback("Stakeholder created.");
        onCancelClicked(getView());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void editStakeholder() {
        feedback("Stakeholder edited.");
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