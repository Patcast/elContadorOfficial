package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transactions.ProcessedTransaction;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

//Todo: Improvement of Categories and programming limit words for notes and title. Also remove mandatory Stakeholder.
public class TransactionNew extends Fragment implements EasyPermissions.PermissionCallbacks{
    public static final int CAMARA_PERM_CODE = 2901;
    private static final String TAG = "TransactionNew";
    public static final int CAMARA_REQUEST_CODE = 1382;
    public static final int GALLERY_REQUEST_CODE = 3892;

    RadioGroup radGroup;
    TextView txtWordsCounterTitle,accountSelected,txtEmojiCategory,txtStakeHolder,txtWordsCounterNotes,txtMustHaveAmount;
    ImageButton btnAddCategory,btnAddPicture;
    ImageView imageFinal;
    EditText txtAmount,txtTitle,txtNotes;
    MainActivity mainActivity;
    NavController navController;
    ViewModel_NewTransaction viewModel;
    ImageFireBase imageSelected;
    StakeHolder selectedStakeHolder;
    String idCatSelected;
    String currentPhotoPath;
    PicturesBottomMenu bottomSheet;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.new_transaction_title));
        return inflater.inflate(R.layout.fragment_transaction_new, container, false);
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
        navController = Navigation.findNavController(view);

///      Initialize views
        imageFinal =view.findViewById(R.id.imageView_final);
        btnAddPicture =view.findViewById(R.id.imageButton_addPicture);
        accountSelected = view.findViewById(R.id.textView_currentAccount);
        radGroup = view.findViewById(R.id.radioGroup);
        btnAddCategory = view.findViewById(R.id.imageButton_chooseCategory);
        txtEmojiCategory = view.findViewById(R.id.text_emoji_category);
        txtTitle = view.findViewById(R.id.text_newTransaction_title);
        txtWordsCounterTitle = view.findViewById(R.id.text_newTransaction_wordCounter);
        txtAmount = view.findViewById(R.id.ed_txt_amount);
        txtStakeHolder = view.findViewById(R.id.text_stakeholderSelected);
        txtNotes = view.findViewById(R.id.ed_txt_notes);
        txtWordsCounterNotes = view.findViewById(R.id.text_newTransaction_wordCounter_notes);
        txtMustHaveAmount = view.findViewById(R.id.textView_fillAmount);
        Button confirmButton = view.findViewById(R.id.btn_confirm_NewTransaction);
        // listeners
        txtStakeHolder.setOnClickListener(v -> { navController.navigate(R.id.action_newTransaction_to_chooseStakeHolderDialog); });
        confirmButton.setOnClickListener(v -> confirmTransaction());
        btnAddCategory.setOnClickListener(this::onCategory_Clicked);
        btnAddPicture.setOnClickListener(v -> askForCamaraPermission());
        txtEmojiCategory.setOnClickListener(this::onCategory_Clicked);
        setWordCounters();
        accountSelected.setText(Caching.INSTANCE.getAccountName());
    }

    public void onCategory_Clicked(View view) {
        TransactionNewDirections.ActionNewTransactionToChooseCategory action =
                TransactionNewDirections.actionNewTransactionToChooseCategory(true);

        navController.navigate(action);
    }

    private void setWordCounters() {
        new MaxWordsCounter(30,txtTitle,txtWordsCounterTitle,getContext());
        new MaxWordsCounter(100,txtNotes,txtWordsCounterNotes,getContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenStakeholder().observe(getViewLifecycleOwner(), this::setStakeChosenText);
        viewModel.getChosenCategory().observe(getViewLifecycleOwner(), this::setCategoryChosen);
        viewModel.getChosenImage().observe(getViewLifecycleOwner(),this::setImageChosen);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.reset();
        viewModel.resetCategory();
        viewModel.resetImage();
    }

    private void setImageChosen(ImageFireBase imageFireBase) {
        if(imageFireBase==null){
            imageFinal.setVisibility(View.GONE);
            btnAddPicture.setVisibility(View.VISIBLE);
        }
        else{
            btnAddPicture.setVisibility(View.GONE);
            imageFinal.setVisibility(View.VISIBLE);
            this.imageSelected = imageFireBase;
            imageFinal.setImageURI(this.imageSelected.getContentUri());
        }
    }
    private void setCategoryChosen(EmojiCategory emojiCategory){
        if(emojiCategory==null){
            idCatSelected= null;
            txtEmojiCategory.setVisibility(View.GONE);
            btnAddCategory.setVisibility(View.VISIBLE);
        }
        else{
            if(emojiCategory.getIcon()!=null){
                idCatSelected= emojiCategory.getId();
                btnAddCategory.setVisibility(View.GONE);
                txtEmojiCategory.setVisibility(View.VISIBLE);
                txtEmojiCategory.setText(emojiCategory.getIcon());
            }
        }
    }
    private void setStakeChosenText(StakeHolder stakeHolder) {
        if(stakeHolder!=null){
            txtStakeHolder.setText(stakeHolder.getName());
            selectedStakeHolder = stakeHolder;
        }
        else{
            txtStakeHolder.setText(R.string.select_a_stakeholder);
        }
    }
    private void confirmTransaction(){

        String amount = txtAmount.getText().toString() ;
        if ( amount.isEmpty()||Integer.parseInt(amount) == 0) {
            txtMustHaveAmount.setVisibility(View.VISIBLE);
            txtMustHaveAmount.setText(R.string.this_field_is_requiered);
        }
        else{
            txtMustHaveAmount.setVisibility(View.GONE);
            if (txtTitle.getText().toString().length()==0 ) {
                txtWordsCounterTitle.setText(R.string.this_field_is_requiered);
                txtWordsCounterTitle.setTextColor(getResources().getColor(R.color.light_red_warning));
            }
            else{
                makeNewTrans();
            }
        }
    }

    private void makeNewTrans(){

        ProcessedTransaction newTransaction = new ProcessedTransaction(
                txtTitle.getText().toString(),
                (radGroup.getCheckedRadioButtonId() == R.id.radio_CashOut)?Integer.parseInt(txtAmount.getText().toString())*-1:Integer.parseInt(txtAmount.getText().toString()),
                mainActivity.returnSavedLoggedEmail(),
                (selectedStakeHolder!=null)?selectedStakeHolder.getId():"",
                (idCatSelected!=null)?idCatSelected:"",
                txtNotes.getText().toString(),
                (imageSelected!=null)?imageSelected.getNameOfImage():""
                );
        if (imageSelected!=null)newTransaction.uploadImageToFireBase(newTransaction,imageSelected,requireContext());
        else newTransaction.sendTransaction(newTransaction,requireContext());
        navController.popBackStack();
    }

    ////// CAMARA


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
                imageFinal.setImageURI(Uri.fromFile(f));
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