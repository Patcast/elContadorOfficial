package be.kuleuven.elcontador10.fragments.transactions;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.Transaction;

public class TransactionNew extends Fragment {

    private static final String[] stakeHolders = new String[]{"Carlos","Mauricio","Tomas","Juan","Patricio","Alexandria","Yonathan"};
    RadioGroup radGroup;
    EditText txtAmount;
    AutoCompleteTextView txtStakeHolder;
    Spinner spCategory;
    Spinner spSubCategory;
    EditText txtNotes;
    MainActivity mainActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("New Transaction");

        return inflater.inflate(R.layout.fragment_transaction_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radGroup = view.findViewById(R.id.radioGroup);
        txtAmount = view.findViewById(R.id.ed_txt_amount);
        spCategory = view.findViewById(R.id.sp_TransCategory);
        spSubCategory = view.findViewById(R.id.sp_TransSubcategory);
        txtNotes = getActivity().findViewById(R.id.ed_txt_notes);
        //Set AutocompleteText
        txtStakeHolder = view.findViewById(R.id.actv_stakeholder);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,stakeHolders);
        txtStakeHolder.setAdapter(adapter);

        //Set sp_SubCategory
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String catChosen = spCategory.getSelectedItem().toString();
                ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, Transaction.chooseSubCat(catChosen));
                spSubCategory.setAdapter(adapterSpinner);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /// This method reads the radio groups and returns true is the cash in radio button is selected.
    public boolean transCashIn(RadioGroup radioGroup){
        return radioGroup.getCheckedRadioButtonId() == R.id.radio_CashIn;
    }
    private Transaction makeNewTrans(){
        boolean cashIn = transCashIn(radGroup);
        double amount = Double.parseDouble(txtAmount.getText().toString());
        String stakeholder =  txtStakeHolder.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        String subCategory = spSubCategory.getSelectedItem().toString();
        String notes = txtNotes.getText().toString();

        return new Transaction(cashIn,amount,stakeholder,category,subCategory,notes);


    }

}