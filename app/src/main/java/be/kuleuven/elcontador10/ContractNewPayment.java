package be.kuleuven.elcontador10;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;

import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.WidgetsCreation;
import be.kuleuven.elcontador10.background.model.contract.Payment;
import be.kuleuven.elcontador10.background.tools.DateStringToTimestamp;

public class ContractNewPayment extends Fragment {
    private NavController navController;

    //views
    private TextView title, amount, notes;
    private Spinner frequency;
    private RadioButton in, out;
    private Button start, end, confirm;

    //variables
    private MainActivity mainActivity;
    private String contractId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contract_new_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up
        navController = Navigation.findNavController(view);
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText("New Payment");
        contractId = ContractNewPaymentArgs.fromBundle(getArguments()).getContractId();

        // set views
        title = view.findViewById(R.id.payment_new_title);
        amount = view.findViewById(R.id.payment_new_amount);
        notes = view.findViewById(R.id.payment_new_notes);

        frequency = view.findViewById(R.id.payment_new_frequency);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.frequency));
        frequency.setAdapter(adapter);
        frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    // one-time
                    start.setEnabled(false);
                    start.setText("N/A");
                    end.setEnabled(false);
                    end.setText("N/A");
                } else {
                    start.setEnabled(true);
                    end.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mainActivity, "Please select frequency", Toast.LENGTH_SHORT).show();
            }
        });

        in = view.findViewById(R.id.payment_new_in);
        out = view.findViewById(R.id.payment_new_out);

        start = view.findViewById(R.id.payment_new_start);
        WidgetsCreation.INSTANCE.attachCalendarButton(getContext(), start);
        end = view.findViewById(R.id.payment_new_end);
        WidgetsCreation.INSTANCE.attachCalendarButton(getContext(), end);

        confirm = view.findViewById(R.id.payment_new_confirm);
        confirm.setOnClickListener(this::onConfirm_Clicked);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onConfirm_Clicked(View view) {
        String title_text = title.getText().toString();
        String amount_text = amount.getText().toString();

        if (title_text.equals("") && amount_text.equals("") && !frequency.isSelected()) {
            Toast.makeText(mainActivity, R.string.zero_amount, Toast.LENGTH_SHORT).show();
        } else {
            long amount_value = Long.parseLong(amount_text);
            if (out.isChecked()) amount_value = - amount_value;

            Timestamp start_value, end_value;
            int frequency_value = frequency.getSelectedItemPosition();

            if (frequency_value == 0) {
                start_value = null;
                end_value = null;
            } else {
                start_value = DateStringToTimestamp.INSTANCE.date(start.getText().toString());
                end_value = DateStringToTimestamp.INSTANCE.date(end.getText().toString());
            }

            Payment payment = new Payment(title_text, amount_value, start_value, end_value, frequency_value,
                    notes.getText().toString(), mainActivity.returnSavedLoggedEmail());

            Payment.newPayment(payment, contractId);

            navController.popBackStack();
        }
    }
}