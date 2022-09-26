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
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

//Todo: Remove mandatory Stakeholder.
public class TransactionNew extends Fragment implements  EasyPermissions.PermissionCallbacks, AdapterView.OnItemSelectedListener {
    RadioGroup radGroup;
    TextView txtWordsCounterTitle,accountSelected,txtEmojiCategory,txtStakeHolder,txtWordsCounterNotes,txtMustHaveAmount,txt_property_selected;
    ImageButton btnAddCategory,btnAddPicture;
    ImageView imageFinal;
    EditText txtAmount,txtTitle,txtNotes;
    MainActivity mainActivity;
    NavController navController;
    ViewModel_NewTransaction viewModel;
    ImageFireBase imageSelected;
    StakeHolder selectedStakeHolder;
    Property selectedProperty;
    String idCatSelected,additional_transaction;
    ConstraintLayout categoryLayout;
    List <String>trans_type = new ArrayList<>();

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

        additional_transaction = mainActivity.getResources().getStringArray(R.array.type_of_cash_transaction)[0];
        Spinner spinner =  view.findViewById(R.id.type_of_cash_transaction);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.type_of_cash_transaction,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

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
        txt_property_selected = view.findViewById(R.id.text_propertySelected);
        txtStakeHolder = view.findViewById(R.id.text_stakeholderSelected);
        txtNotes = view.findViewById(R.id.ed_txt_notes);
        txtWordsCounterNotes = view.findViewById(R.id.text_newTransaction_wordCounter_notes);
        txtMustHaveAmount = view.findViewById(R.id.textView_fillAmount);
        Button confirmButton = view.findViewById(R.id.btn_confirm_NewTransaction);
        categoryLayout= view.findViewById(R.id.layout_addCategory);
        // listeners
        txtStakeHolder.setOnClickListener(v ->goToStakeList());
        txt_property_selected.setOnClickListener(v->lookForProperty());
        confirmButton.setOnClickListener(v -> confirmTransaction());
        btnAddCategory.setOnClickListener(this::onCategory_Clicked);
        btnAddPicture.setOnClickListener(v -> startCamara());
        txtEmojiCategory.setOnClickListener(this::onCategory_Clicked);
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
        viewModel.getChosenImage().observe(getViewLifecycleOwner(),this::setImageChosen);
        viewModel.getChosenProperty().observe(getViewLifecycleOwner(),this::setPropertyChosen);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.reset();
    }
    private void setPropertyChosen(Property property) {
        selectedProperty = property;

        if (property != null) {
            txt_property_selected.setText(property.getName());
            txtStakeHolder.setBackground(null);
            txtStakeHolder.setEnabled(false);
        } else {
            txt_property_selected.setText(R.string.none);
            txtStakeHolder.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.rec_view_gray, null));
            txtStakeHolder.setEnabled(true);
        }
    }

    private void setStakeChosenText(StakeHolder stakeHolder) {
        if(stakeHolder!=null){
            txtStakeHolder.setText(stakeHolder.getName());
            selectedStakeHolder = stakeHolder;
        }
        else{
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
            }
        }
    }

    private void confirmTransaction(){
        String amount = txtAmount.getText().toString() ;


        if ( (txtTitle.getText().toString().length()==0) || (amount.isEmpty()) || (Integer.parseInt(amount) == 0) || (idCatSelected==null) ){
            if (txtTitle.getText().toString().length()==0 ) {
                txtWordsCounterTitle.setText(R.string.this_field_is_requiered);
                txtWordsCounterTitle.setTextColor(getResources().getColor(R.color.light_red_warning));
            }
            if ( amount.isEmpty()||Integer.parseInt(amount) == 0) {
                txtMustHaveAmount.setVisibility(View.VISIBLE);
                txtMustHaveAmount.setTextColor(getResources().getColor(R.color.light_red_warning));
                txtMustHaveAmount.setText(R.string.this_field_is_requiered);
            }
            if(idCatSelected==null){
                categoryLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.missing_new_category,null));
                Toast.makeText(mainActivity, R.string.must_choose_category, Toast.LENGTH_LONG).show();
            }
        }
        else{
            makeNewTrans();
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
                (imageSelected!=null)?imageSelected.getNameOfImage():"",
                trans_type,
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i==0){
            trans_type.clear();
            trans_type.addAll(Arrays.asList(Caching.INSTANCE.TYPE_CASH, null, null, null));
        }
        else if(i==1){
            trans_type.clear();
            trans_type.addAll(Arrays.asList(Caching.INSTANCE.TYPE_CASH, Caching.INSTANCE.TYPE_PAYABLES, null, null));
        }
        else{
            trans_type.clear();
            trans_type.addAll(Arrays.asList(Caching.INSTANCE.TYPE_CASH, null, Caching.INSTANCE.TYPE_RECEIVABLES, null));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}