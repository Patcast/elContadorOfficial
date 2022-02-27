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

import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.ViewModelCategory;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;
import be.kuleuven.elcontador10.background.model.contract.SubContract;
import be.kuleuven.elcontador10.background.tools.DatabaseDatesFunctions;

public class ContractNewSubContract extends Fragment {
    private NavController navController;

    //views
    private TextView title, amount, custom_frequency, duration, info, notes, emoji;
    private Spinner frequency_spinner, custom_frequency_spinner, duration_spinner;
    private RadioButton in, out;
    private Button start, confirm;
    private LinearLayout duration_layout;
    private ConstraintLayout custom_frequency_layout;
    private ImageButton selectCategory;

    //variables
    private MainActivity mainActivity;
    private String contractId, idCatSelected, period_text, frequency_text;
    private int paymentsLeft;
    private ArrayList<ScheduledTransaction> transactions;
    private ViewModelCategory viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contract_new_sub_contract, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up
        navController = Navigation.findNavController(view);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText("New Payment");
        contractId = ContractNewSubContractArgs.fromBundle(getArguments()).getContractId();
        idCatSelected = null;

        // set views
        title = view.findViewById(R.id.payment_new_title);
        amount = view.findViewById(R.id.payment_new_amount);
        custom_frequency = view.findViewById(R.id.payment_new_customFrequency);
        custom_frequency.addTextChangedListener(new TextChangeWatcher());
        duration = view.findViewById(R.id.payment_new_duration);
        duration.addTextChangedListener(new TextChangeWatcher());
        info = view.findViewById(R.id.payment_new_info);
        notes = view.findViewById(R.id.payment_new_notes);

        duration_layout = view.findViewById(R.id.payment_new_duration_layout);
        custom_frequency_layout = view.findViewById(R.id.payment_new_customFrequencyLayout);

        custom_frequency_spinner = view.findViewById(R.id.payment_new_customFrequencySpinner);
        ArrayAdapter<String> custom_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.frequency_names));
        custom_frequency_spinner.setAdapter(custom_adapter);
        custom_frequency_spinner.setOnItemSelectedListener(new CustomAdapter());

        duration_spinner = view.findViewById(R.id.payment_new_durationSpinner);
        duration_spinner.setOnItemSelectedListener(new DurationAdapter());

        frequency_spinner = view.findViewById(R.id.payment_new_frequency);
        ArrayAdapter<String> frequency_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.frequency));
        frequency_spinner.setAdapter(frequency_adapter);
        frequency_spinner.setOnItemSelectedListener(new FrequencyAdapter());

        in = view.findViewById(R.id.payment_new_in);
        out = view.findViewById(R.id.payment_new_out);

        start = view.findViewById(R.id.payment_new_start);
        start.setOnClickListener(this::onStart_Clicked);

        confirm = view.findViewById(R.id.payment_new_confirm);
        confirm.setOnClickListener(this::onConfirm_Clicked);

        selectCategory = view.findViewById(R.id.imageButton_chooseCategory);
        selectCategory.setOnClickListener(this::onSelectCategory_Clicked);

        emoji = view.findViewById(R.id.text_emoji_category);
        emoji.setOnClickListener(this::onSelectCategory_Clicked);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelCategory.class);
        viewModel.getChosenCategory().observe(getViewLifecycleOwner(), this::setChosenCategory);
    }

    public void onSelectCategory_Clicked(View view) {
        ContractNewSubContractDirections.ActionContractNewPaymentToChooseCategory action =
                ContractNewSubContractDirections.actionContractNewPaymentToChooseCategory(false);

        navController.navigate(action);
    }

    public void setChosenCategory(EmojiCategory emojiCategory) {
        if (emojiCategory == null) {
            idCatSelected = null;
            emoji.setVisibility(View.GONE);
            selectCategory.setVisibility(View.VISIBLE);
        } else {
            idCatSelected = emojiCategory.getId();
            selectCategory.setVisibility(View.GONE);
            emoji.setVisibility(View.VISIBLE);
            emoji.setText(emojiCategory.getIcon());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onConfirm_Clicked(View view) {
        String title_text = title.getText().toString();
        String amount_text = amount.getText().toString();

        if (title_text.equals("") || amount_text.equals("")) { // everything selected
            Toast.makeText(mainActivity, R.string.zero_amount, Toast.LENGTH_SHORT).show();
        }
        else if (info.getCurrentTextColor() == Color.RED && duration_layout.getVisibility() == View.VISIBLE) { // error visible
            Toast.makeText(mainActivity, R.string.check_errors, Toast.LENGTH_SHORT).show();
        } else {
            int amount_value = Integer.parseInt(amount_text);
            if (out.isChecked()) amount_value = - amount_value;

            Timestamp startDate;
            Timestamp endDate;

            String start_text = start.getText().toString();
            startDate = DatabaseDatesFunctions.INSTANCE.stringToTimestamp(start_text); // initial payment at the start of contract

            int frequency_value = frequency_spinner.getSelectedItemPosition();

            if (frequency_value == 0) {
                endDate = null;

                period_text = "N/A";
                paymentsLeft = 0;
            } else {
                endDate = DatabaseDatesFunctions.INSTANCE.stringToTimestamp(period_text.split(" - ")[1]);

                if (paymentsLeft == 0) {
                    Toast.makeText(mainActivity, R.string.error_no_payments_can_be_made, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            String note_text = notes.getText().toString();

            // create sub contract
            SubContract newSubContract = new SubContract(title_text, amount_value, startDate, endDate, note_text, mainActivity.returnSavedLoggedEmail());
            String subContractId = SubContract.newSubContract(newSubContract, contractId);

            // create scheduled payments
            if (subContractId != null) {
                if (frequency_value == 0) {
                    ScheduledTransaction transaction = new ScheduledTransaction(amount_value, 0,
                            startDate, Caching.INSTANCE.getChosenMicroAccountId());

                    transaction.setTitle(title_text);
                    transaction.setCategory(idCatSelected);

                    ScheduledTransaction.newScheduledTransaction(transaction, contractId, subContractId);
                } else {
                    // final copies of variables for foreach
                    final int final_amount = amount_value;
                    final String final_ID = Caching.INSTANCE.getChosenMicroAccountId();

                    transactions.forEach(e -> e.setTotalAmount(final_amount));
                    transactions.forEach(e -> e.setIdOfStakeholder(final_ID));
                    transactions.forEach(e -> e.setTitle(title_text));
                    transactions.forEach(e -> e.setCategory(idCatSelected));

                    // add all scheduled transactions
                    transactions.forEach(e -> ScheduledTransaction.newScheduledTransaction(e, contractId, subContractId));
                }
            }

            navController.popBackStack();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onStart_Clicked(View view) {
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
                        Toast.makeText(getContext(), R.string.date_before_error, Toast.LENGTH_SHORT).show();
                        start.setText("N/A");
                    } else {
                        start.setText(date);
                        calculateDates();
                    }
                }, year_int, month_int, day_int);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void calculateDates() {
        if (frequency_spinner.getSelectedItemPosition() != 0) { // not one time
            String string = duration.getText().toString();

            if (string.length() == 0) {
                info.setTextColor(Color.RED);
                info.setText(R.string.please_fill_in_duration);
            } else {
                String start_date = start.getText().toString();

                if (!start_date.equals("N/A")) {
                    info.setTextColor(Color.WHITE);
                    int frequencyID = frequency_spinner.getSelectedItemPosition();
                    int durationValue = Integer.parseInt(duration.getText().toString());

                    if (frequency_spinner.getSelectedItemPosition() != 6) { // not custom
                        int durationUnit = duration_spinner.getSelectedItemPosition();

                        period_text = DatabaseDatesFunctions.INSTANCE.getPeriod(start_date, frequencyID, durationValue, durationUnit);

                        transactions = DatabaseDatesFunctions.INSTANCE.getScheduledTransactions(period_text, frequencyID);

                        if (transactions != null) {
                            paymentsLeft = transactions.size();

                            String info_text = period_text + "\n\nPayment dates:\n" +
                                    transactions.stream()
                                            .map(e -> DatabaseDatesFunctions.INSTANCE.timestampToString(e.getDueDate()))
                                            .collect(Collectors.joining("\n"));

                            info.setText(info_text);
                        } else {
                            info.setText(R.string.error_period);
                            info.setTextColor(Color.RED);
                        }
                    } else { // custom
                        String frequency_value = custom_frequency.getText().toString();
                        int frequency_unit = custom_frequency_spinner.getSelectedItemPosition();

                        if (!frequency_value.equals("")) {
                            frequency_text = frequency_value + " - " + frequency_unit;

                            LinkedList<String> data = DatabaseDatesFunctions.INSTANCE.customPeriod(start_date, durationValue,
                                    frequency_text);

                            if (data != null) {
                                period_text = data.getLast();
                                paymentsLeft = data.size() - 1;

                                String info_text = period_text + "\n\nPayment dates:\n" +
                                        data.stream()
                                                .limit(data.size() - 1) // last one not a payment
                                                .map(String::toString)
                                                .collect(Collectors.joining("\n"));

                                transactions = DatabaseDatesFunctions.INSTANCE.textToTransaction(data);
                                info.setText(info_text);
                            } else {
                                info.setText(R.string.error_period);
                                info.setTextColor(Color.RED);
                            }
                        } else {
                            info.setText(R.string.zero_amount);
                            info.setTextColor(Color.RED);
                        }
                    }
                } else {
                    info.setText(R.string.select_starting_date);
                    info.setTextColor(Color.RED);
                }
            }
        } else { // one time
            if (start.getText().toString().equals("N/A")) {
                info.setText(R.string.select_starting_date);
                info.setTextColor(Color.RED);
            } else {
                String info_text = "Payment date:\n" +
                        start.getText().toString();

                info.setText(info_text);
                info.setTextColor(Color.WHITE);
            }
        }
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
            calculateDates();
        }
    }

    private class FrequencyAdapter implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 0) { // one time
                custom_frequency_layout.setVisibility(View.GONE);
                duration_layout.setVisibility(View.GONE);
                frequency_text = "0";
            }
            else {
                duration_layout.setVisibility(View.VISIBLE);

                if (i == 6) { // custom
                    custom_frequency_layout.setVisibility(View.VISIBLE);


                    duration.setHint(R.string.number_of_payments);
                    duration_spinner.setVisibility(View.GONE);
                }
                else { // standard
                    custom_frequency_layout.setVisibility(View.GONE);
                    frequency_text = String.valueOf(i);

                    List<String> names = Arrays.asList(getResources().getStringArray(R.array.frequency_names));
                    names = names.subList(i - 1, names.size());
                    ArrayAdapter<String> duration_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                            names);
                    duration_spinner.setAdapter(duration_adapter);
                    duration_spinner.setVisibility(View.VISIBLE);

                    duration.setHint(R.string.duration);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Toast.makeText(mainActivity, R.string.select_frequency, Toast.LENGTH_SHORT).show();
        }
    }

    private class CustomAdapter implements  AdapterView.OnItemSelectedListener {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            calculateDates();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Toast.makeText(mainActivity, R.string.select_frequency, Toast.LENGTH_SHORT).show();
        }
    }

    private class DurationAdapter implements  AdapterView.OnItemSelectedListener {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            calculateDates();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Toast.makeText(mainActivity, R.string.select_frequency, Toast.LENGTH_SHORT).show();
        }
    }
}