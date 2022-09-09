package be.kuleuven.elcontador10.fragments.stakeholders.contracts;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.ViewModelCategory;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.TransactionNewDirections;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class ContractNewSubContract extends Fragment {

    private static final String TAG = "NewFutureTransaction";
    private final int ONE_TIME=0,  CUSTOM=6;
    private NavController navController;

    //views
    private TextView   numOfFutureTransTxt, summaryOfFutureTransactionsTxt, emojiTxt
    , stakeTxt,fillStakeTxt,propertyTxt,fillAmountTxt,wordCountNotesTxt,fillTitleTxt;
    EditText notesTxt,titleTxt,amountTxt;
    private Spinner frequency_spinner;
    private RadioButton payablesButton;
    private Button btnChooseStartingDate, btnConfirm;
    private ImageButton selectCategory;
    StakeHolder selectedStakeHolder;
    Property selectedProperty;

    //variables
    private MainActivity mainActivity;
    private String  idCatSelected;
    private boolean isStartingDatePicked = false;
    private int  collectionSize;
    private ArrayList<ProcessedTransaction> transactions;
    private ViewModel_NewTransaction viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contract_new_sub_contract, container, false);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.new_future_transaction));
        idCatSelected = null;
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        startViews(view);
        setButtonsListeners();
        setSpinners();
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);
        ViewModelCategory viewModelCategory = new ViewModelProvider(requireActivity()).get(ViewModelCategory.class);
        viewModelCategory.getChosenCategory().observe(getViewLifecycleOwner(), this::setChosenCategory);
        setWordCounters();
    }
    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenStakeholder().observe(getViewLifecycleOwner(), this::setStakeChosenText);
        viewModel.getChosenProperty().observe(getViewLifecycleOwner(),this::setPropertyChosen);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.reset();
    }

    private void setPropertyChosen(Property property) {
        if(property!=null){
            propertyTxt.setText(property.getName());
            selectedProperty = property;
        }
        else{
            selectedProperty=null;
            propertyTxt.setText(R.string.none);
        }
    }
    private void setStakeChosenText(StakeHolder stakeHolder) {
        if(stakeHolder!=null){
            stakeTxt.setText(stakeHolder.getName());
            selectedStakeHolder = stakeHolder;
        }
        else{
            selectedStakeHolder = null;
            stakeTxt.setText(R.string.none);
        }
    }

    private void setSpinners() {

        ArrayAdapter<String> frequency_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.frequency));
        frequency_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency_spinner.setAdapter(frequency_adapter);
        frequency_spinner.setOnItemSelectedListener(new FrequencyAdapter());
    }

    private void setWordCounters() {
        new MaxWordsCounter(30,titleTxt,fillTitleTxt,getContext());
        new MaxWordsCounter(100,notesTxt,wordCountNotesTxt,getContext());
        new MaxWordsCounter(8,amountTxt,fillAmountTxt,getContext());
    }
    private void startViews(View view) {
        stakeTxt = view.findViewById(R.id.text_stakeholderSelected);
        fillStakeTxt = view.findViewById(R.id.textView_fillStakeholder);
        propertyTxt = view.findViewById(R.id.text_propertySelected);
        fillAmountTxt = view.findViewById(R.id.textView_fillAmount);
        fillTitleTxt =view.findViewById(R.id.text_fillTitle);
        wordCountNotesTxt = view.findViewById(R.id.text_newTransaction_wordCounter_notes);

        titleTxt = view.findViewById(R.id.payment_new_title);
        amountTxt = view.findViewById(R.id.payment_new_amount);
        numOfFutureTransTxt = view.findViewById(R.id.payment_new_duration);
        numOfFutureTransTxt.addTextChangedListener(new TextChangeWatcher());
        summaryOfFutureTransactionsTxt = view.findViewById(R.id.payment_new_info);
        notesTxt = view.findViewById(R.id.payment_new_notes);
        payablesButton = view.findViewById(R.id.payment_new_out);
        btnChooseStartingDate = view.findViewById(R.id.payment_new_start);
        frequency_spinner = view.findViewById(R.id.payment_new_frequency);
        btnConfirm = view.findViewById(R.id.payment_new_confirm);
        selectCategory = view.findViewById(R.id.imageButton_chooseCategory);
        emojiTxt = view.findViewById(R.id.text_emoji_category);
        propertyTxt.setOnClickListener(v->lookForProperty());
        stakeTxt.setOnClickListener(v -> { navController.navigate(R.id.action_contractNewPayment_to_chooseStakeHolderDialog); });

    }

    private void lookForProperty() {
        ContractNewSubContractDirections.ActionContractNewPaymentToPropertiesList action = ContractNewSubContractDirections.actionContractNewPaymentToPropertiesList(TAG);
        navController.navigate(action);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setButtonsListeners() {
        btnChooseStartingDate.setOnClickListener(this::onChooseStartDateClicked);
        btnConfirm.setOnClickListener(this::onConfirm_Clicked);
        selectCategory.setOnClickListener(this::onSelectCategory_Clicked);
        emojiTxt.setOnClickListener(this::onSelectCategory_Clicked);

    }

    public void onSelectCategory_Clicked(View view) {
        ContractNewSubContractDirections.ActionContractNewPaymentToChooseCategory action =
                ContractNewSubContractDirections.actionContractNewPaymentToChooseCategory(false);
        navController.navigate(action);
    }
    public void setChosenCategory(EmojiCategory emojiCategory) {
        if (emojiCategory == null) {
            idCatSelected = null;
            emojiTxt.setVisibility(View.GONE);
            selectCategory.setVisibility(View.VISIBLE);
        } else {
            idCatSelected = emojiCategory.getId();
            selectCategory.setVisibility(View.GONE);
            emojiTxt.setVisibility(View.VISIBLE);
            emojiTxt.setText(emojiCategory.getIcon());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onChooseStartDateClicked(View view) {
        Calendar calendar = Calendar.getInstance();
        final int year_int = calendar.get(Calendar.YEAR);
        final int month_int = calendar.get(Calendar.MONTH);
        final int day_int = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),android.R.style.Theme_Holo_Dialog_MinWidth,
                (view1, year, month, dayOfMonth) -> {
                    month += 1;
                    String date = ((dayOfMonth < 10)? "0" : "" ) + dayOfMonth + "/" + ((month < 10)? "0" : "") + month + "/" + year;
                    LocalDate dateChosen = DatabaseDatesFunctions.INSTANCE.stringToDate(date);

                    // time chosen before now
                    if (dateChosen.isBefore(LocalDate.now())) {
                        Toast.makeText(getContext(), R.string.date_before_error, Toast.LENGTH_LONG).show();
                        btnChooseStartingDate.setText(R.string.choose);
                        isStartingDatePicked=false;

                    } else {
                        btnChooseStartingDate.setText(date);
                        isStartingDatePicked=true;
                        calculateMultipleFutureTransaction();
                    }
                }, year_int, month_int, day_int);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    public void onConfirm_Clicked(View view) {
        String title_text = titleTxt.getText().toString();
        String amount_text = amountTxt.getText().toString();

        if(noInputErrors(title_text,amount_text)){
            List <String> type_input = (payablesButton.isChecked())? Arrays.asList(null,"PAYABLE", null,"PENDING"):Arrays.asList(null,null, "RECEIVABLES","PENDING");
            String propertyId = (selectedProperty!=null)?selectedProperty.getId():null;
            transactions.forEach(t->t.setFutureTransactionsFields(
                    title_text,
                    Integer.parseInt(amount_text),
                    mainActivity.returnSavedLoggedEmail(),
                    selectedStakeHolder.getId(),
                    idCatSelected,
                    notesTxt.getText().toString(),
                    null,
                    type_input,
                    propertyId
            ));
            transactions.forEach(t->t.sendTransaction(t,getContext()));
            navController.popBackStack();
        }
    }

    private boolean noInputErrors(String title_text,String amount_text) {

        if (title_text.equals("")) {
            fillTitleTxt.setText(R.string.add_title);
            fillTitleTxt.setTextColor(getContext().getResources().getColor(R.color.light_red_warning));
        }
        else if (amount_text.equals("")) {
            fillAmountTxt.setText(R.string.zero_amount);
            fillAmountTxt.setTextColor(getContext().getResources().getColor(R.color.light_red_warning));
        }
        else if (selectedStakeHolder==null) fillStakeTxt.setText(R.string.this_field_is_requiered);
        else if (!isStartingDatePicked) errorOnDates(getString(R.string.select_starting_date));
        else if ( transactions==null||transactions.size()==0 )errorOnDates(getString(R.string.please_fill_in_duration));
        else return true;

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean calculateMultipleFutureTransaction(){

        String start_date = btnChooseStartingDate.getText().toString();
        int frequencyID = frequency_spinner.getSelectedItemPosition();
        String collectionSizeString =numOfFutureTransTxt.getText().toString();
        if ((frequencyID == ONE_TIME ||(frequencyID != CUSTOM && collectionSizeString.length()>0))&& isStartingDatePicked) {
            collectionSize = (collectionSizeString.length()>0)? Integer.parseInt(collectionSizeString):1;

            transactions = DatabaseDatesFunctions.INSTANCE.makeFutureTransactions(start_date, frequencyID,collectionSize);//

            if (transactions != null) {
                String summary =
                        getString(R.string.future_dates)+"\n"
                        + transactions  .stream()
                        .map(e -> DatabaseDatesFunctions.INSTANCE.timestampToString(e.getDueDate()))
                        .collect(Collectors.joining("\n"));
                summaryOfFutureTransactionsTxt.setText(summary);
                summaryOfFutureTransactionsTxt.setTextColor(Color.WHITE);// move for later
                return true;
            } else {
                errorOnDates(getString(R.string.error_period));
                return false;
            }
        }
        return false;
    }
    private void errorOnDates(String errorMsg){
        summaryOfFutureTransactionsTxt.setText(errorMsg);
        summaryOfFutureTransactionsTxt.setTextColor(getContext().getResources().getColor(R.color.light_red_warning));
    }

    private class TextChangeWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void afterTextChanged(Editable editable) {
            calculateMultipleFutureTransaction();
        }
    }

    private class FrequencyAdapter implements AdapterView.OnItemSelectedListener {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(transactions!=null)transactions.clear();
            if (i == 0) { // one time
                numOfFutureTransTxt.setVisibility(View.GONE);
                calculateMultipleFutureTransaction();
            }
            else {
                numOfFutureTransTxt.setVisibility(View.VISIBLE);
                calculateMultipleFutureTransaction();
                numOfFutureTransTxt.setHint(R.string.duration);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Toast.makeText(mainActivity, R.string.select_frequency, Toast.LENGTH_SHORT).show();
        }
    }





}