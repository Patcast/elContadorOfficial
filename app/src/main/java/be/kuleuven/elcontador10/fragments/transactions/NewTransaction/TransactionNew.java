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
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transaction;
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
    NewTransactionViewModel viewModel;
    StakeHolder selectedStakeHolder;
    String idCatSelected;
    String currentPhotoPath;
    StorageReference storageReference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.new_transaction_title));
        mainActivity.displayTopMenu(false);
        return inflater.inflate(R.layout.fragment_transaction_new, container, false);
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(NewTransactionViewModel.class);
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
        Button galleryButton = view.findViewById(R.id.btn_add_pictureFromGallery);
        Button confirmButton = view.findViewById(R.id.btn_confirm_NewTransaction);
        // listeners
        galleryButton.setOnClickListener(v->useGallery());
        txtStakeHolder.setOnClickListener(v -> { navController.navigate(R.id.action_newTransaction_to_chooseStakeHolderDialog); });
        confirmButton.setOnClickListener(v -> confirmTransaction());
        btnAddCategory.setOnClickListener(v-> navController.navigate(R.id.action_newTransaction_to_chooseCategory));
        btnAddPicture.setOnClickListener(v -> askForCamaraPermission());
        txtEmojiCategory.setOnClickListener(v-> navController.navigate(R.id.action_newTransaction_to_chooseCategory));
        setWordCounters();
        accountSelected.setText(Caching.INSTANCE.getAccountName());
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
    }
    private void setCategoryChosen(EmojiCategory emojiCategory){
        if(emojiCategory==null){
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
            txtStakeHolder.setText(R.string.select_an_stakeholder);
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
                navController.popBackStack();
                makeNewTrans();
            }
        }
    }

    private void makeNewTrans(){
        boolean cashOut = radGroup.getCheckedRadioButtonId() == R.id.radio_CashOut;
        String title = txtTitle.getText().toString();
        int amount = Integer.parseInt(txtAmount.getText().toString());
        if(cashOut) amount = amount*-1;
        String idCatFinal=(idCatSelected!=null)?idCatSelected: " ";
        String notes = txtNotes.getText().toString();
        String idStakeHolder =(selectedStakeHolder!=null)?selectedStakeHolder.getId():"";
        Transaction newTrans= new Transaction(title,amount, mainActivity.returnSavedLoggedEmail(), idStakeHolder,idCatFinal,notes);
        newTrans.SendTransaction(newTrans);
    }

    ////// CAMARA

   /* private void AskForCamaraPermission() {
        //Toast.makeText(getContext(), "Camara will open", Toast.LENGTH_SHORT).show();

        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},CAMARA_PERM_CODE);
        }
        else {
            openCamara();
        }
    }

    private void openCamara() {
        Toast.makeText(getContext(), "Camara will open", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMARA_PERM_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamara();
            }
            else{
                Toast.makeText(getContext(), R.string.camara_permission_denied, Toast.LENGTH_SHORT).show();
            }

        }
        Toast.makeText(getContext(), "Camara will open", Toast.LENGTH_SHORT).show();
    }*/

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
            //openCamara();
            dispatchTakePictureIntent();
        }
        else{
            EasyPermissions.requestPermissions(this,getString(R.string.camara_permission_denied),CAMARA_PERM_CODE,perms);
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      /*  if(requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){

        }*/
        if(requestCode==CAMARA_REQUEST_CODE){
            /*Bitmap image = (Bitmap) data.getExtras().get("data");
            btnAddPicture.setImageBitmap(image);*/
            if(resultCode== Activity.RESULT_OK){
                File f = new  File(currentPhotoPath);
                //imageFinal.setImageURI(Uri.fromFile(f));
                galleryAddPic(f);
            }
        }
        if(requestCode==GALLERY_REQUEST_CODE){
            /*Bitmap image = (Bitmap) data.getExtras().get("data");
            btnAddPicture.setImageBitmap(image);*/
            if(resultCode== Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_"+timeStamp+"."+getFileExt(contentUri);
                Log.d("tag","onActivityResult: Gallery Image Uri: "+imageFileName);
                //imageFinal.setImageURI(contentUri);
                uploadImageToFireBase(imageFileName,contentUri);

            }
        }

    }



    private String getFileExt(Uri contentUri) {
        ContentResolver c = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private void galleryAddPic(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
        uploadImageToFireBase(f.getName(),contentUri);

    }

    private void uploadImageToFireBase(String name, Uri contentUri) {
        StorageReference image = storageReference.child("pictures/"+name);
        image.putFile(contentUri).addOnSuccessListener(taskSnapshot -> {
            image.getDownloadUrl().addOnSuccessListener(uri -> {
                        Picasso.get().load(uri).into(imageFinal);

            }
            );
            Toast.makeText(getContext(), getString(R.string.image_uploaded), Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
        });
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

}