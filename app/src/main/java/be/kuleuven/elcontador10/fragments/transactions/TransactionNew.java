package be.kuleuven.elcontador10.fragments.transactions;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.WidgetsCreation;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.viewModels.ChosenStakeViewModel;
//Todo: Improvement of Categories and programming limit words for notes and title. Also remove mandatory Stakeholder.
public class TransactionNew extends Fragment {
    private static final String TAG = "TransactionNew";
    RadioGroup radGroup;
    EditText txtTitle;
    EditText txtAmount;
    TextView txtStakeHolder;
    Spinner spCategory;
    Spinner spSubCategory;
    EditText txtNotes;
    MainActivity mainActivity;
    NavController navController;
    ChosenStakeViewModel viewModel;
    StakeHolder selectedStakeHolder;

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
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ChosenStakeViewModel.class);
        navController = Navigation.findNavController(view);
///      Initialize views
        radGroup = view.findViewById(R.id.radioGroup);
        txtTitle = view.findViewById(R.id.text_newTransaction_title);
        txtAmount = view.findViewById(R.id.ed_txt_amount);
        txtStakeHolder = view.findViewById(R.id.text_stakeholderSelected);
        /*spCategory = view.findViewById(R.id.sp_TransCategory);
        spSubCategory = view.findViewById(R.id.sp_TransSubcategory);*/
        txtNotes = view.findViewById(R.id.ed_txt_notes);
        Button confirmButton = view.findViewById(R.id.btn_confirm_NewTransaction);
      /*  WidgetsCreation.INSTANCE.makeSpinnerCat(mainActivity,spCategory,false);

////      Set sp_SubCategory after clicking on category
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                WidgetsCreation.INSTANCE.makeSpinnerSubCat(mainActivity,spSubCategory,spCategory.getSelectedItem().toString(),false);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        txtStakeHolder.setOnClickListener(v -> { navController.navigate(R.id.action_newTransaction_to_chooseStakeHolderDialog); });
        confirmButton.setOnClickListener(v -> confirmTransaction());
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenStakeholder().observe(getViewLifecycleOwner(), this::setStakeChosenText);
    }
    @Override
    public void onStop() {
        super.onStop();
        viewModel.reset();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.reset();
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
            Toast.makeText(getActivity(), R.string.zero_amount, Toast.LENGTH_LONG).show();
        }
        else{
            if (selectedStakeHolder==null ) {
                Toast.makeText(getActivity(), R.string.select_an_stakeholder, Toast.LENGTH_LONG).show();
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
        String category = spCategory.getSelectedItem().toString();
        String subCategory = spSubCategory.getSelectedItem().toString();
        String notes = txtNotes.getText().toString();
        Transaction newTrans= new Transaction(title,amount, mainActivity.returnSavedLoggedEmail(), selectedStakeHolder.getId(),category,subCategory,notes);
        newTrans.SendTransaction(newTrans);
    }








}