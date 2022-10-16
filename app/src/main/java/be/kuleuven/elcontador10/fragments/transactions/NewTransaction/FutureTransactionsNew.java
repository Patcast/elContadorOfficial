package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
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

import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;
import be.kuleuven.elcontador10.fragments.property.PropertyListViewModel;

public class FutureTransactionsNew extends Fragment {

    private NavController navController;
    private TextView summaryOfFutureTransactionsTxt, propertyTxt, emojiTxt, stakeTxt, fillStakeTxt,
            fillAmountTxt, wordCountNotesTxt, fillTitleTxt, fillAmountOfFuture;
    EditText numOfFutureTransTxt, notesTxt, titleTxt, amountTxt;
    private Spinner frequency_spinner;
    private RadioButton payablesButton;
    private Button btnChooseStartingDate, btnConfirm;
    private ImageButton selectCategory;
    StakeHolder selectedStakeHolder;
    Property selectedProperty;
    String date=null;
    private MainActivity mainActivity;
    private String  idCatSelected;
    private ArrayList<ProcessedTransaction> transactions;
    private ViewModel_NewTransaction viewModel;

    public static final String TAG = "FutureTransactionsNew";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_future_new, container, false);
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

        if (getArguments() != null) {
            String stakeholderID = FutureTransactionsNewArgs.fromBundle(getArguments()).getIdStakeholder();
            if (stakeholderID != null)
                viewModel.selectStakeholder(Caching.INSTANCE.getStakeHolder(stakeholderID));
            String propertyID = FutureTransactionsNewArgs.fromBundle(getArguments()).getIdProperty();
            if (propertyID != null) {
                PropertyListViewModel viewModelProperty = new ViewModelProvider(requireActivity()).get(PropertyListViewModel.class);
                Property chosenProperty = viewModelProperty.getPropertyFromID(propertyID);
                this.viewModel.selectProperty(chosenProperty);
            }
            getArguments().clear();
        }

        setWordCounters();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenCategory().observe(getViewLifecycleOwner(), this::setChosenCategory);
        viewModel.getChosenStakeholder().observe(getViewLifecycleOwner(), this::setStakeChosenText);
        viewModel.getChosenProperty().observe(getViewLifecycleOwner(), this::setPropertyChosen);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.reset();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setPropertyChosen(Property property) {
        selectedProperty = property;
        if (property != null) {
            propertyTxt.setText(property.getName());
            stakeTxt.setBackground(null);
            stakeTxt.setEnabled(false);
        } else {
            propertyTxt.setText(R.string.none);
            stakeTxt.setBackground(ResourcesCompat.getDrawable(getResources(), R.color.rec_view_gray, null));
            stakeTxt.setEnabled(true);
        }
    }

    private void setStakeChosenText(StakeHolder stakeHolder) {
        if (stakeHolder != null){
            stakeTxt.setText(stakeHolder.getName());
            selectedStakeHolder = stakeHolder;
        }
        else {
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
        new MaxWordsCounter(30, titleTxt, fillTitleTxt, getContext());
        new MaxWordsCounter(100, notesTxt, wordCountNotesTxt, getContext());
        new MaxWordsCounter(8, amountTxt, fillAmountTxt, getContext());
        new MaxWordsCounter(4, numOfFutureTransTxt, fillAmountOfFuture, getContext());
    }
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startViews(View view) {
        //views
        ScrollView scrollView = view.findViewById(R.id.scrollViewFutureNew);

        TextView accountText = view.findViewById(R.id.textView_currentAccount);
        accountText.setText(Caching.INSTANCE.getAccountName());

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
        summaryOfFutureTransactionsTxt.setMovementMethod(new ScrollingMovementMethod());

        scrollView.setOnTouchListener((v, event) -> {
            summaryOfFutureTransactionsTxt.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });

        summaryOfFutureTransactionsTxt.setOnTouchListener((v, event) -> {
            summaryOfFutureTransactionsTxt.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        notesTxt = view.findViewById(R.id.payment_new_notes);
        payablesButton = view.findViewById(R.id.payment_new_out);
        btnChooseStartingDate = view.findViewById(R.id.payment_new_start);
        frequency_spinner = view.findViewById(R.id.payment_new_frequency);
        btnConfirm = view.findViewById(R.id.payment_new_confirm);
        selectCategory = view.findViewById(R.id.imageButton_chooseCategory);
        emojiTxt = view.findViewById(R.id.text_emoji_category);
        propertyTxt.setOnClickListener(v->lookForProperty());
        stakeTxt.setOnClickListener(v -> goToStakeList());

        fillAmountOfFuture = view.findViewById(R.id.textView_fillDuration);
    }

    private void goToStakeList() {
        FutureTransactionsNewDirections.ActionTransactionFutureNewToStakeholders action =
                FutureTransactionsNewDirections.actionTransactionFutureNewToStakeholders();
        action.setPrevFragment(TAG);
        navController.navigate(action);
    }
    private void lookForProperty() {
        FutureTransactionsNewDirections.ActionContractNewPaymentToPropertiesList action =
                FutureTransactionsNewDirections.actionContractNewPaymentToPropertiesList();
        action.setPreviousFragment(Caching.INSTANCE.PROPERTY_NEW_T);
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
        FutureTransactionsNewDirections.ActionContractNewPaymentToChooseCategory action =
                FutureTransactionsNewDirections.actionContractNewPaymentToChooseCategory(false);
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
                    date = ((dayOfMonth < 10)? "0" : "" ) + dayOfMonth + "/" + ((month < 10)? "0" : "") + month + "/" + year;
                    LocalDate dateChosen = DatabaseDatesFunctions.INSTANCE.stringToDate(date);

                    // time chosen before now
                    if (dateChosen.isBefore(LocalDate.now())) {
                        Toast.makeText(getContext(), R.string.date_before_error, Toast.LENGTH_LONG).show();
                        btnChooseStartingDate.setText(R.string.choose);
                        date=null;

                    } else {
                        btnChooseStartingDate.setText(date);
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
            List <String> type_input = (payablesButton.isChecked())?
                    Arrays.asList(null, Caching.INSTANCE.TYPE_PAYABLES, null, Caching.INSTANCE.TYPE_PENDING) :
                    Arrays.asList(null,null, Caching.INSTANCE.TYPE_RECEIVABLES, Caching.INSTANCE.TYPE_PENDING);
            String propertyId = (selectedProperty!=null)? selectedProperty.getId() : "";
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
        boolean valid = true;

        if (title_text.equals("")) {
            fillTitleTxt.setText(R.string.add_title);
            fillTitleTxt.setTextColor(requireContext().getResources().getColor(R.color.light_red_warning));
            valid = false;
        }
        if (amount_text.equals("")) {
            fillAmountTxt.setText(R.string.zero_amount);
            fillAmountTxt.setTextColor(requireContext().getResources().getColor(R.color.light_red_warning));
            valid = false;
        }
        if (selectedStakeHolder == null) {
            fillStakeTxt.setText(R.string.this_field_is_required);
            valid = false;
        }
        if (date == null) {
            errorOnDates(getString(R.string.select_starting_date));
            valid = false;
        }
        if ( transactions == null || transactions.size() == 0 ) {
            errorOnDates(getString(R.string.please_fill_in_duration));
            valid = false;
        }
        if (idCatSelected == null) {
            selectCategory.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.missing_new_category,null));
            Toast.makeText(mainActivity, R.string.must_choose_category, Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateMultipleFutureTransaction(){
        int frequencyID = frequency_spinner.getSelectedItemPosition();
        String collectionSizeString = numOfFutureTransTxt.getText().toString();
        int ONE_TIME = 0;
        int CUSTOM = 6;
        if ((frequencyID == ONE_TIME ||(frequencyID != CUSTOM && collectionSizeString.length() > 0))
                && date != null) {
            int collectionSize = (collectionSizeString.length() > 0) ? Integer.parseInt(collectionSizeString) : 1;

            transactions = DatabaseDatesFunctions.INSTANCE.makeFutureTransactions(date, frequencyID, collectionSize);

            if (transactions != null) {
                String summary =
                        getString(R.string.future_dates)+"\n\n"
                        + transactions.stream()
                        .map(e -> DatabaseDatesFunctions.INSTANCE.timestampToString(e.getDueDate()))
                        .collect(Collectors.joining("\n"));

                summaryOfFutureTransactionsTxt.setText(summary);
                summaryOfFutureTransactionsTxt.setTextColor(Color.WHITE);
            } else {
                errorOnDates(getString(R.string.error_period));
            }
        }
    }
    private void errorOnDates(String errorMsg){
        summaryOfFutureTransactionsTxt.setText(errorMsg);
        summaryOfFutureTransactionsTxt.setTextColor(requireContext().getResources().getColor(R.color.light_red_warning));
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
            int length = numOfFutureTransTxt.getText().toString().length();
            if (length == 0) {
                if (transactions != null)
                    transactions.clear();
                errorOnDates(getString(R.string.please_fill_in_duration));
            } else
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
                fillAmountOfFuture.setVisibility(View.GONE);
                calculateMultipleFutureTransaction();
            }
            else {
                numOfFutureTransTxt.setVisibility(View.VISIBLE);
                fillAmountOfFuture.setVisibility(View.VISIBLE);
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
