package be.kuleuven.elcontador10.background.tools;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.model.Interfaces.ViewModelCamaraInterface;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.PicturesBottomMenu;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CamaraSetUp {

    public static final int CAMARA_PERM_CODE = 2901;
    public static final int CAMARA_REQUEST_CODE = 1382;
    public static final int GALLERY_REQUEST_CODE = 3892;
    String currentPhotoPath;
    PicturesBottomMenu bottomSheet;
    // pass in constructor
    Context context;
    Fragment fragment;


    @RequiresApi(api = Build.VERSION_CODES.R)
    public CamaraSetUp(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
        askForCamaraPermission();
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @AfterPermissionGranted(CAMARA_PERM_CODE)
    private void askForCamaraPermission() {
        String[] perms= {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(context,perms)){
            chooseImageOptions();
        }
        else{
            EasyPermissions.requestPermissions(fragment,fragment.getString(R.string.camara_permission_denied),CAMARA_PERM_CODE,perms);
        }
    }

    private void chooseImageOptions() {
        bottomSheet = new PicturesBottomMenu(new PicturesBottomMenu.PicturesBottomSheetListener() {
            @Override
            public void onGalleryClick() {
                useGallery();
                bottomSheet.dismiss();
            }
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onTakePictureClick() {
                dispatchTakePictureIntent();
                bottomSheet.dismiss();
            }
        });
        bottomSheet.show(fragment.getParentFragmentManager(),"PicturesBottomSheet");
    }

    private void useGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(fragment.requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(fragment.requireContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                fragment.startActivityForResult(takePictureIntent, CAMARA_REQUEST_CODE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES); USE if you don't want them to be saved in the gallery.
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onActivityResultForCamara(int requestCode, int resultCode, ViewModelCamaraInterface viewModel, @Nullable Intent data){
        if(requestCode==CAMARA_REQUEST_CODE){
            if(resultCode== Activity.RESULT_OK){
                File f = new  File(currentPhotoPath);
                //imageFinal.setImageURI(Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                fragment.getActivity().sendBroadcast(mediaScanIntent);
                ImageFireBase imageFireBase = new ImageFireBase(f.getName(),contentUri);
                viewModel.selectImage(imageFireBase);

            }
        }
        if(requestCode==GALLERY_REQUEST_CODE){

            if(resultCode== Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_"+timeStamp+"."+getFileExt(contentUri);
                Log.d("tag","onActivityResult: Gallery Image Uri: "+imageFileName);
                ImageFireBase imageFireBase = new ImageFireBase(imageFileName,contentUri);
                viewModel.selectImage(imageFireBase);

            }
        }

    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = fragment.getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

}
