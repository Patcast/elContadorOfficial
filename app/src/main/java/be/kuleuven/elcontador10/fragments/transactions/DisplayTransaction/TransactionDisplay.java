package be.kuleuven.elcontador10.fragments.transactions.DisplayTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.Timestamp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.tools.DateFormatter;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.fragments.transactions.AllTransactions.ViewModel_AllTransactions;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.PicturesBottomMenu;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TransactionDisplay extends Fragment implements MainActivity.TopMenuHandler,EasyPermissions.PermissionCallbacks {
    private MainActivity mainActivity;
    TextView concerning, registeredBy,account, amount, category,emojiCategory, date,time, notes;
    ProcessedTransaction selectedTrans;
    NavController navController;
    ConstraintLayout layoutAddPhotoIcon;
    CircularProgressIndicator progressIndicator;
    ImageView imViewPhotoIn;
    View view;
    ViewModel_DisplayTransaction viewModel;
    boolean isLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_DisplayTransaction.class);
        viewModel.reset();
        isLoading=false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        view=inflater.inflate(R.layout.fragment_transaction_display, container, false);

        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        try {
            TransactionDisplayArgs args = TransactionDisplayArgs.fromBundle(getArguments());
            initializeViews(view);
            displayInformation(args.getId());
            mainActivity.setTitle(selectedTrans.getTitle());

        }
        catch (Exception e) {
            Toast.makeText(mainActivity, "Error Loading the information.", Toast.LENGTH_SHORT).show();
        }
        imViewPhotoIn.setOnClickListener(v->navController.navigate(R.id.action_transactionDisplay_to_displayPhoto2));
        layoutAddPhotoIcon.setOnClickListener(v->askForCamaraPermission());
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivity.setCurrentMenuClicker(this);
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_delete,true);
        viewModel.getChosenBitMap().observe(getViewLifecycleOwner(), this::setImage);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(),this::setLoadingBar);
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_delete,false);

    }

    private void setLoadingBar(Boolean aBoolean) {
        if (aBoolean){
            progressIndicator.setVisibility(View.VISIBLE);
            isLoading=true;
            layoutAddPhotoIcon.setVisibility(View.GONE);
        }
        else{
            progressIndicator.setVisibility(View.GONE);
            isLoading=false;
        }
    }

    private void setImage(Bitmap bitmap) {
        if(bitmap!=null) {
            setUiForPhoto(true);
            imViewPhotoIn.setImageBitmap(bitmap);
        }
        else {
            setUiForPhoto(false);

        }
    }


    private void setUiForPhoto(boolean photoDownloaded) {
        if(photoDownloaded){
            imViewPhotoIn.setVisibility(View.VISIBLE);
            layoutAddPhotoIcon.setVisibility(View.GONE);
        }else{
            imViewPhotoIn.setVisibility(View.GONE);
            layoutAddPhotoIcon.setVisibility(View.VISIBLE);
        }
    }

    public void initializeViews (View view) {
        amount = view.findViewById(R.id.textAmount);
        concerning = view.findViewById(R.id.textConcerningDisplay);
        account = view.findViewById(R.id.textAccountChosenDisplay);
        emojiCategory = view.findViewById(R.id.txtCategoryIcon);
        category = view.findViewById(R.id.txtCategoryTitle);
        date = view.findViewById(R.id.txtDateDisplay);
        time = view.findViewById(R.id.txtTimeDisplay);
        registeredBy = view.findViewById(R.id.txtRegisteredByDisplay);
        notes = view.findViewById(R.id.txtNotesDisplay);
        notes.setMovementMethod(new ScrollingMovementMethod());
        layoutAddPhotoIcon = view.findViewById(R.id.layout_addPhoto);
        imViewPhotoIn = view.findViewById(R.id.image_transaction_photoDownloaded);
        progressIndicator = view.findViewById(R.id.progress_indicator_displayTrans);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayInformation(String idOfTransaction) {
        selectedTrans = Caching.INSTANCE.getTransaction(idOfTransaction);
        if(selectedTrans.equals(null))Toast.makeText(getContext(),"error getting Transaction",Toast.LENGTH_SHORT);
        else {
            NumberFormatter formatter = new NumberFormatter(selectedTrans.getTotalAmount());
            DateFormatter dateFormatter = new DateFormatter(selectedTrans.getDueDate(),"f");
            DateFormatter timeFormatter = new DateFormatter(selectedTrans.getDueDate(),"t");
            amount.setText(formatter.getFinalNumber());
            String startPhrase=(formatter.isNegative())? getString(R.string.paid_to): getString(R.string.paid_by);
            String concerningText= startPhrase+" "+Caching.INSTANCE.getStakeholderName(selectedTrans.getIdOfStakeInt());
            concerning.setText(concerningText);
            account.setText(Caching.INSTANCE.getAccountName());

            String emoji =Caching.INSTANCE.getCategoryEmoji(selectedTrans.getIdOfCategoryInt());
            if (emoji.length()==0){
                emojiCategory.setVisibility(View.GONE);
                category.setVisibility(View.GONE);
            }
            else emojiCategory.setText(emoji);
            category.setText(Caching.INSTANCE.getCategoryTitle(selectedTrans.getIdOfCategoryInt()));
            date.setText(dateFormatter.getFormattedDate());
            time.setText(timeFormatter.getFormattedDate());
            registeredBy.setText(selectedTrans.getRegisteredBy());
            notes.setText(selectedTrans.getNotes());
            checkIfImageExists();
        }
    }

    private void checkIfImageExists(){
        if(selectedTrans.getImageName().length()>0){
            if(viewModel.getChosenBitMap().getValue()==null){
                viewModel.selectBitMap(selectedTrans.getImageName(),requireContext());
            }
        }
        else setUiForPhoto(false);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void confirmDelete(){
        navController.popBackStack();
        Timestamp currentDate = Timestamp.now();
        if( selectedTrans.getDueDate().toDate().getMonth()!=currentDate.toDate().getMonth()) {
            Toast.makeText(getContext(), "The transaction belongs to a previous month, so it is not possible to delete it.", Toast.LENGTH_LONG).show();
        }
        else{
            selectedTrans.deleteTransaction(getContext());
        }
    }


    @Override
    public void onBottomSheetClick() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDeleteClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes", (dialog, which) ->confirmDelete())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onEditingClick() {

    }

    @Override
    public void onAddClick() {

    }

    @Override
    public void onSearchClick(SearchView searchView) {

    }

    @Override
    public void onFilterClick() {

    }

    //
    ////// CAMARA
    /////
    public static final int CAMARA_PERM_CODE = 2901;
    public static final int CAMARA_REQUEST_CODE = 1382;
    public static final int GALLERY_REQUEST_CODE = 3892;
    String currentPhotoPath;
    PicturesBottomMenu bottomSheet;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @AfterPermissionGranted(CAMARA_PERM_CODE)
    private void askForCamaraPermission() {
        String[] perms= {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(requireContext(),perms)){
            chooseImageOptions();
        }
        else{
            EasyPermissions.requestPermissions(this,getString(R.string.camara_permission_denied),CAMARA_PERM_CODE,perms);
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
        bottomSheet.show(getParentFragmentManager(),"PicturesBottomSheet");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMARA_REQUEST_CODE){

            if(resultCode== Activity.RESULT_OK){
                File f = new  File(currentPhotoPath);
                imViewPhotoIn.setImageURI(Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);
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
        ContentResolver c = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
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

    static final int REQUEST_TAKE_PHOTO=1;
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMARA_REQUEST_CODE);
            }
        }
    }
    private void useGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}