package be.kuleuven.elcontador10.fragments.transactions;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.WidgetsCreation;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.database.TransactionsManager;
import be.kuleuven.elcontador10.background.interfaces.CachingObserver;
import be.kuleuven.elcontador10.background.interfaces.CreateWidgets;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.model.TransactionType;
import be.kuleuven.elcontador10.background.viewModels.ChosenStakeViewModel;

public class TransactionNew extends Fragment implements CachingObserver, CreateWidgets {
    private static final String TAG = "TransactionNew";
//// input from UI
    RadioGroup radGroup;
    EditText txtAmount;
    TextView txtStakeHolder;
    Spinner spCategory;
    Spinner spSubCategory;
    EditText txtNotes;
    MainActivity mainActivity;
    NavController navController;
    ChosenStakeViewModel viewModel;
    StakeHolder selectedStakeHolder;
    ////// Arrays to fill input
    List<TransactionType> transTypes = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.new_transaction_title));
        return inflater.inflate(R.layout.fragment_transaction_new, container, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ChosenStakeViewModel.class);
        radGroup = view.findViewById(R.id.radioGroup);
        txtAmount = view.findViewById(R.id.ed_txt_amount);
        txtStakeHolder = view.findViewById(R.id.text_stakeholderSelected);
        spCategory = view.findViewById(R.id.sp_TransCategory);
        spSubCategory = view.findViewById(R.id.sp_TransSubcategory);
        txtNotes = view.findViewById(R.id.ed_txt_notes);
        Button confirmButton = view.findViewById(R.id.btn_confirm_NewTransaction);
        /// Set spinner for category & Autofill for Stakeholders
        navController = Navigation.findNavController(view);
        addSpinnerCat();
        addAutoStake();

        //Set sp_SubCategory after clicking on category
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addSpinnerSubCat(spCategory.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void confirmTransaction(){

            // here we check that the user added a certain amount.
            String amount =  txtAmount.getText().toString();
            if ( amount.isEmpty()) {
                Toast.makeText(getActivity(), R.string.zero_amount, Toast.LENGTH_LONG).show();
            }
            else{
                if ( Double.parseDouble(amount) == 0 ) {
                    Toast.makeText(getActivity(), R.string.zero_amount, Toast.LENGTH_LONG).show();
                }
                else{
                    navController.navigate(R.id.action_newTransaction_to_transactions_summary);
                    TransactionsManager manager = TransactionsManager.getInstance();
                    manager.addNewTransaction(makeNewTrans(),mainActivity);
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    private Transaction makeNewTrans(){
        boolean cashIn = radGroup.getCheckedRadioButtonId() == R.id.radio_CashIn;
        double amount = Double.parseDouble(txtAmount.getText().toString());
        String stakeholder =  txtStakeHolder.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        String subCategory = spSubCategory.getSelectedItem().toString();
        String notes = txtNotes.getText().toString();
        Optional<TransactionType> searchIdType = transTypes.stream()
                                                             .filter(cat ->cat.getCategory().equals(category))
                                                             .filter(subCat -> subCat.getSubCategory().equals(subCategory))
                                                             .findFirst();

        return new Transaction();
    }


    //IMPLEMENTATION of INTERFACES **********


     @Override
    public void notifyRoles(List<String> roles) {
    }

    @Override
    public void notifyCategories(List<TransactionType> transTypes) {
        this.transTypes.clear();
        this.transTypes.addAll(transTypes);
    }
    @Override
    public void notifyStakeHolders(List<StakeHolder> stakeHolders) {

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addSpinnerCat() {
        WidgetsCreation.INSTANCE.makeSpinnerCat(mainActivity,spCategory,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addSpinnerSubCat(String catChosen) {
        WidgetsCreation.INSTANCE.makeSpinnerSubCat(mainActivity,spSubCategory,catChosen,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addAutoStake() {
        //WidgetsCreation.INSTANCE.makeAutoStake(mainActivity,txtStakeHolder,false);

    }

    @Override
    public void addCalendar() {

    }

}