package be.kuleuven.elcontador10.fragments.transactions.DisplayTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.Timestamp;

import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.tools.CamaraSetUp;
import be.kuleuven.elcontador10.background.tools.DateFormatter;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TransactionDisplay extends Fragment implements EasyPermissions.PermissionCallbacks {
    private MainActivity mainActivity;
    TextView concerning, registeredBy, account, type, amount, category,emojiCategory, date,time, notes, deletedBy, deletedDate;
    TableRow rowDeletedBy, rowDeletedDate;
    LinearLayout background;
    ProcessedTransaction selectedTrans;
    NavController navController;
    ConstraintLayout layoutAddPhotoIcon;
    CircularProgressIndicator progressIndicator;
    ImageView imViewPhotoIn;
    View view;
    ViewModel_DisplayTransaction viewModel;
    CamaraSetUp camara;

    boolean isLoading;
    boolean isDeleted;

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
            selectedTrans = Caching.INSTANCE.getTransaction(args.getId());
            viewModel.setTransaction(selectedTrans);
            displayInformation();

            mainActivity.setTitle(selectedTrans.getTitle());
            mainActivity.displayStakeHolderDetails(false, "", "", "");
        }
        catch (Exception e) {
            Toast.makeText(mainActivity, "Error Loading the information.", Toast.LENGTH_SHORT).show();
        }
        imViewPhotoIn.setOnClickListener(v->navController.navigate(R.id.action_transactionDisplay_to_displayPhoto2));
        layoutAddPhotoIcon.setOnClickListener(v->startCamara());
        setTopMenu();
    }

    private void setTopMenu(){
       requireActivity().addMenuProvider(new MenuProvider() {
           @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);
               if (selectedTrans.getType().contains(Caching.INSTANCE.TYPE_PENDING))
                   menu.findItem(R.id.menu_execute_transaction).setVisible(true);
               if (!selectedTrans.getIsDeleted())
                   menu.findItem(R.id.menu_delete).setVisible(true);
            }

           @RequiresApi(api = Build.VERSION_CODES.N)
           @Override
           public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
               final int menu_delete = R.id.menu_delete,
                       menu_execute = R.id.menu_execute_transaction;

               switch (menuItem.getItemId()){
                   case menu_delete:
                       onDeleteClick();
                       return true;
                   case menu_execute:
                       onExecuteClick();
                       return true;
                   default:
                       return false;
               }
           }
       }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkIfImageExists();
        viewModel.getChosenBitMap().observe(getViewLifecycleOwner(), i -> setImage(null,i));
        viewModel.getIsLoading().observe(getViewLifecycleOwner(),this::setLoadingBar);
        viewModel.getChosenImage().observe(getViewLifecycleOwner(),b -> setImage(b,null));
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

    private void setImage (ImageFireBase image,Bitmap bitmap){
        if(!hasNullOrEmptyDrawable(imViewPhotoIn)) {
            setUiForPhoto(true);
        }
        else if(image!=null) {
            setUiForPhoto(true);
            imViewPhotoIn.setImageURI(image.getContentUri());
        }
        else if(bitmap!=null) {
            setUiForPhoto(true);
            imViewPhotoIn.setImageBitmap(bitmap);
        }
        else if(!isLoading) {
            setUiForPhoto(false);
        }
    }
    public static boolean hasNullOrEmptyDrawable(ImageView iv)
    {
        Drawable drawable = iv.getDrawable();
        BitmapDrawable bitmapDrawable = drawable instanceof BitmapDrawable ? (BitmapDrawable)drawable : null;

        return bitmapDrawable == null || bitmapDrawable.getBitmap() == null;
    }


    private void setUiForPhoto(boolean photoDownloaded) {
        if (photoDownloaded){
            imViewPhotoIn.setVisibility(View.VISIBLE);
            layoutAddPhotoIcon.setVisibility(View.GONE);
            progressIndicator.setVisibility(View.GONE);
        } else {
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
        background = view.findViewById(R.id.layoutTransactionHolder);
        type = view.findViewById(R.id.textTransTypeDisplay);
        rowDeletedBy = view.findViewById(R.id.rowDeletedBy);
        rowDeletedDate = view.findViewById(R.id.rowDeletedDate);
        deletedBy = view.findViewById(R.id.txtDeletedByDisplay);
        deletedDate = view.findViewById(R.id.txtDeletedDateDisplay);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayInformation() {
        if(selectedTrans==null)Toast.makeText(getContext(),"error getting Transaction",Toast.LENGTH_SHORT).show();
        else {
            NumberFormatter formatter = new NumberFormatter(selectedTrans.getTotalAmount());
            DateFormatter dateFormatter = new DateFormatter(selectedTrans.getDueDate(),"f");
            DateFormatter timeFormatter = new DateFormatter(selectedTrans.getDueDate(),"t");
            amount.setText(formatter.getFinalNumber());

            String concerningText = getText(selectedTrans.transText()) + " " + Caching.INSTANCE.getStakeholderName(selectedTrans.getIdOfStakeInt());
            if (selectedTrans.getIdOfProperty() != null && !selectedTrans.getIdOfProperty().equals(""))
                concerningText += "\n" + Caching.INSTANCE.getPropertyNameFromID(selectedTrans.getIdOfProperty());
            concerning.setText(concerningText);

            background.setBackgroundColor(ContextCompat.getColor(requireContext(), selectedTrans.getColorInt()));

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

            String typeText;
            List<String> types = selectedTrans.getType();

            if (types.contains(Caching.INSTANCE.TYPE_CASH)) {
                typeText = Caching.INSTANCE.TYPE_CASH;

                if (types.contains(Caching.INSTANCE.TYPE_PAYABLES))
                    typeText += " / " + Caching.INSTANCE.TYPE_PAYABLES;
                else if (types.contains(Caching.INSTANCE.TYPE_RECEIVABLES))
                    typeText += " / " + Caching.INSTANCE.TYPE_RECEIVABLES;
            } else if (types.contains(Caching.INSTANCE.TYPE_PENDING)) {
                typeText = Caching.INSTANCE.TYPE_PENDING;

                if (types.contains(Caching.INSTANCE.TYPE_PAYABLES))
                    typeText += " / " + Caching.INSTANCE.TYPE_PAYABLES;
                else if (types.contains(Caching.INSTANCE.TYPE_RECEIVABLES))
                    typeText += " / " + Caching.INSTANCE.TYPE_RECEIVABLES;
            } else if (types.contains(Caching.INSTANCE.TYPE_PAYABLES))
                typeText = Caching.INSTANCE.TYPE_PAYABLES;
            else
                typeText = Caching.INSTANCE.TYPE_RECEIVABLES;

            type.setText(typeText);

            if (selectedTrans.getIsDeleted()) {
                rowDeletedDate.setVisibility(View.VISIBLE);
                rowDeletedBy.setVisibility(View.VISIBLE);

                dateFormatter = new DateFormatter(selectedTrans.getDeletedDate(), "f");
                deletedDate.setText(dateFormatter.getFormattedDate());
                deletedBy.setText(selectedTrans.getDeletedBy());
            }
        }
    }

    private void checkIfImageExists(){
        if (selectedTrans.getImageName()!=null){
            if(selectedTrans.getImageName().length()>0){
                if(viewModel.getChosenBitMap().getValue()==null){
                    viewModel.selectBitMap(selectedTrans.getImageName(),requireContext());
                }
                else imViewPhotoIn.setImageBitmap(viewModel.getChosenBitMap().getValue());
            }
        }
        else setUiForPhoto(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void confirmDelete(){
        navController.popBackStack();
        Timestamp currentDate = Timestamp.now();
        if( selectedTrans.getDueDate().toDate().getMonth() < currentDate.toDate().getMonth()) {
            Toast.makeText(getContext(), R.string.not_delete_past_transactions, Toast.LENGTH_LONG).show();
        }
        else{
            selectedTrans.deleteTransaction(getContext(), mainActivity.returnSavedLoggedEmail());
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onDeleteClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.delete_transaction))
                .setMessage(getString(R.string.sure_delete_transaction))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> confirmDelete())
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void confirmExecute() {
        navController.popBackStack();
        selectedTrans.execute(getContext());
    }

    public void onExecuteClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.execute_pending_transaction))
                .setMessage(getString(R.string.sure_execute_transaction))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> confirmExecute())
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    //
    ////// CAMARA
    /////


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void startCamara(){
        camara = new CamaraSetUp(getContext(),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CamaraSetUp.CAMARA_REQUEST_CODE||requestCode==CamaraSetUp.GALLERY_REQUEST_CODE){
            ImageFireBase photoCreated= camara.onActivityResultForCamara(requestCode,resultCode,viewModel,data);
            if (photoCreated!=null){
                selectedTrans.updateImageFromFireBase(selectedTrans,photoCreated,getContext());
            }
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
}