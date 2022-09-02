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


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.model.Interfaces.TransactionInterface;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.tools.CamaraSetUp;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

//Todo: Remove mandatory Stakeholder.
public class TransactionNew extends Fragment implements  EasyPermissions.PermissionCallbacks, AdapterView.OnItemSelectedListener {
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
    String additional_transaction;
    List <String>trans_type = new ArrayList<>();

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

        additional_transaction = mainActivity.getResources().getStringArray(R.array.type_of_cash_transaction)[0];
        Spinner spinner = (Spinner) view.findViewById(R.id.type_of_cash_transaction);
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
        txtStakeHolder = view.findViewById(R.id.text_stakeholderSelected);
        txtNotes = view.findViewById(R.id.ed_txt_notes);
        txtWordsCounterNotes = view.findViewById(R.id.text_newTransaction_wordCounter_notes);
        txtMustHaveAmount = view.findViewById(R.id.textView_fillAmount);
        Button confirmButton = view.findViewById(R.id.btn_confirm_NewTransaction);
        // listeners
        txtStakeHolder.setOnClickListener(v -> { navController.navigate(R.id.action_newTransaction_to_chooseStakeHolderDialog); });
        confirmButton.setOnClickListener(v -> confirmTransaction());
        btnAddCategory.setOnClickListener(this::onCategory_Clicked);
        btnAddPicture.setOnClickListener(v -> startCamara());
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
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_search,false);
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
            txtStakeHolder.setText(R.string.none);
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
                (imageSelected!=null)?imageSelected.getNameOfImage():"",
                trans_type,
                1,
                1
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
            trans_type.addAll(Arrays.asList("CASH",null,null));
        }
        else if(i==1){
            trans_type.clear();
            trans_type.addAll(Arrays.asList("CASH","PAYABLES",null));
        }
        else{
            trans_type.clear();
            trans_type.addAll(Arrays.asList("CASH",null,"RECEIVABLES"));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}