package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.tools.CamaraSetUp;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;
import be.kuleuven.elcontador10.fragments.property.PropertyListViewModel;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

//Todo: Remove mandatory Stakeholder.
public class TransactionNew extends Fragment implements  EasyPermissions.PermissionCallbacks {

    private TextView txtWordsCounterTitle, txtEmojiCategory, txtWordsCounterNotes,
            txtMustHaveAmount, txtPropertySelected, txtStakeHolder, txtStakeholderNotSelected;
    private ImageButton btnAddCategory, btnAddPicture;
    private ImageView imageFinal;
    private EditText txtAmount,txtTitle,txtNotes;
    private ConstraintLayout categoryLayout;

    private MainActivity mainActivity;
    private NavController navController;

    private ViewModel_NewTransaction viewModel;
    private ImageFireBase imageSelected;
    private StakeHolder selectedStakeHolder;
    private Property selectedProperty;
    private String idCatSelected;

    public static final String TAG  = "TransactionNew";

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
        TextView accountSelected = view.findViewById(R.id.textView_currentAccount);
        btnAddCategory = view.findViewById(R.id.imageButton_chooseCategory);
        txtEmojiCategory = view.findViewById(R.id.text_emoji_category);
        txtTitle = view.findViewById(R.id.text_newTransaction_title);
        txtWordsCounterTitle = view.findViewById(R.id.text_newTransaction_wordCounter);
        txtAmount = view.findViewById(R.id.ed_txt_amount);
        txtPropertySelected = view.findViewById(R.id.text_propertySelected);
        txtStakeHolder = view.findViewById(R.id.text_stakeholderSelected);
        txtNotes = view.findViewById(R.id.ed_txt_notes);
        txtWordsCounterNotes = view.findViewById(R.id.text_newTransaction_wordCounter_notes);
        txtMustHaveAmount = view.findViewById(R.id.textView_fillAmount);
        txtStakeholderNotSelected = view.findViewById(R.id.textView_stakeholderEmpty);
        Button confirmButton = view.findViewById(R.id.btn_confirm_NewTransaction);
        categoryLayout= view.findViewById(R.id.layout_addCategory);
        // listeners
        txtStakeHolder.setOnClickListener(v ->goToStakeList());
        txtPropertySelected.setOnClickListener(v->lookForProperty());
        confirmButton.setOnClickListener(v -> confirmTransaction());
        btnAddCategory.setOnClickListener(this::onCategory_Clicked);
        btnAddPicture.setOnClickListener(v -> startCamara());
        txtEmojiCategory.setOnClickListener(this::onCategory_Clicked);

        if (getArguments() != null) {
            String stakeholderID = FutureTransactionsNewArgs.fromBundle(getArguments()).getIdStakeholder();
            if (stakeholderID != null)
                viewModel.selectStakeholder(Caching.INSTANCE.getStakeHolder(stakeholderID));
            String propertyID = FutureTransactionsNewArgs.fromBundle(getArguments()).getIdProperty();
            if (propertyID != null) {
                PropertyListViewModel viewModel = new ViewModelProvider(requireActivity()).get(PropertyListViewModel.class);
                Property chosenProperty = viewModel.getPropertyFromID(propertyID);
                this.viewModel.selectProperty(chosenProperty);
            }
            getArguments().clear(); //
        }

        setWordCounters();
        accountSelected.setText(Caching.INSTANCE.getAccountName());
    }

    private void goToStakeList() {
        TransactionNewDirections.ActionNewTransactionToStakeholders action =
                TransactionNewDirections.actionNewTransactionToStakeholders();
        action.setPrevFragment(TAG);
        navController.navigate(action);
    }


    private void lookForProperty() {
        TransactionNewDirections.ActionNewTransactionToPropertiesList action =
                TransactionNewDirections.actionNewTransactionToPropertiesList();
        action.setPreviousFragment(Caching.INSTANCE.PROPERTY_NEW_T);
        navController.navigate(action);
    }

    public void onCategory_Clicked(View view) {
        TransactionNewDirections.ActionNewTransactionToChooseCategory action =
                TransactionNewDirections.actionNewTransactionToChooseCategory(true);

        navController.navigate(action);
    }

    private void setWordCounters() {
        new MaxWordsCounter(30,txtTitle,txtWordsCounterTitle,getContext());
        new MaxWordsCounter(100,txtNotes,txtWordsCounterNotes,getContext());
        new MaxWordsCounter(8,txtAmount,txtMustHaveAmount,getContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenStakeholder().observe(getViewLifecycleOwner(), this::setStakeChosenText);
        viewModel.getChosenCategory().observe(getViewLifecycleOwner(), this::setCategoryChosen);
        viewModel.getChosenImage().observe(getViewLifecycleOwner(), this::setImageChosen);
        viewModel.getChosenProperty().observe(getViewLifecycleOwner(), this::setPropertyChosen);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.reset();
    }

    private void setPropertyChosen(Property property) {
        selectedProperty = property;

        if (property != null) {
            txtPropertySelected.setText(property.getName());
            txtStakeHolder.setBackground(null);
            txtStakeHolder.setEnabled(false);
        } else {
            txtPropertySelected.setText(R.string.none);
            txtStakeHolder.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.rec_view_gray, null));
            txtStakeHolder.setEnabled(true);
        }
    }

    private void setStakeChosenText(StakeHolder stakeHolder) {
        selectedStakeHolder = stakeHolder;
        if (stakeHolder != null) {
            txtStakeholderNotSelected.setVisibility(View.GONE);
            txtStakeHolder.setText(stakeHolder.getName());
        } else {
            txtStakeHolder.setText(R.string.none);
        }
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
                categoryLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.new_account_category_outline,null));
                idCatSelected= emojiCategory.getId();
                btnAddCategory.setVisibility(View.GONE);
                txtEmojiCategory.setVisibility(View.VISIBLE);
                txtEmojiCategory.setText(emojiCategory.getIcon());
                if(!emojiCategory.requiresStakeHolderChosen() || emojiCategory.requiresStakeHolderChosen()&&selectedStakeHolder!=null) txtStakeholderNotSelected.setVisibility(View.GONE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void confirmTransaction(){
        String amount = txtAmount.getText().toString() ;
        boolean valid = true;

        if (txtTitle.getText().toString().length() == 0) {
            txtWordsCounterTitle.setText(R.string.this_field_is_required);
            txtWordsCounterTitle.setTextColor(getResources().getColor(R.color.light_red_warning));
            valid = false;
        }
        if (amount.isEmpty()||Integer.parseInt(amount) == 0) {
            txtMustHaveAmount.setVisibility(View.VISIBLE);
            txtMustHaveAmount.setTextColor(getResources().getColor(R.color.light_red_warning));
            txtMustHaveAmount.setText(R.string.this_field_is_required);
            valid = false;
        }
        if (idCatSelected == null) {
            categoryLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.missing_new_category,null));
            Toast.makeText(mainActivity, R.string.must_choose_category, Toast.LENGTH_LONG).show();
            valid = false;

        }
        else {
            EmojiCategory emojiCategory = Caching.INSTANCE.getEmojiCategory(idCatSelected);
            if(emojiCategory.requiresStakeHolderChosen() && (emojiCategory.requiresStakeHolderChosen()&&selectedStakeHolder==null)) {
                txtStakeholderNotSelected.setVisibility(View.VISIBLE);
                txtStakeholderNotSelected.setTextColor(getResources().getColor(R.color.light_red_warning));
                txtStakeholderNotSelected.setText(R.string.this_field_is_required);
                Toast.makeText(mainActivity, getText(R.string.category_needs_stakeholder), Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        if (valid) makeNewTrans();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void makeNewTrans(){
        EmojiCategory category = Caching.INSTANCE.getEmojiCategory(idCatSelected);
        ProcessedTransaction newTransaction = new ProcessedTransaction(
                txtTitle.getText().toString(),
                (category.isCashIn())?Integer.parseInt(txtAmount.getText().toString()):Integer.parseInt(txtAmount.getText().toString())*-1,
                mainActivity.returnSavedLoggedEmail(),
                (selectedStakeHolder!=null)?selectedStakeHolder.getId():"",
                idCatSelected,
                txtNotes.getText().toString(),
                (imageSelected!=null)?imageSelected.getNameOfImage():"",
                category.getType(),
                1,
                1,
                (selectedProperty!=null)?selectedProperty.getId():""
                );
        if (imageSelected!=null)newTransaction.uploadImageToFireBase(newTransaction,imageSelected,requireContext());
        else newTransaction.sendTransaction(newTransaction,requireContext());
        navController.popBackStack();
    }

    ////// CAMARA
    CamaraSetUp camara;

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
            camara.onActivityResultForCamara(requestCode,resultCode,viewModel,data);
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