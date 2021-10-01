package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

import be.kuleuven.elcontador10.R;

public class MonthYearPickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2030;
    private static final int MIN_YEAR = 2021;
    private DatePickerDialog.OnDateSetListener listener;
    private ViewModel_AllTransactions viewModel;
    private final int month;
    private final int year;

    MonthYearPickerDialog(int month, int year){
        this.month= month;
        this.year= year;
    }
    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_year_picker, null);
        final NumberPicker monthPicker = dialog.findViewById(R.id.picker_month);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(getResources().getStringArray(R.array.months_list));

        final NumberPicker yearPicker = dialog.findViewById(R.id.picker_year);

        //monthPicker.setValue(cal.get(Calendar.MONTH)+1);
        monthPicker.setValue(month);


        //int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);
        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton("ok", (dialog1, id) ->
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0))
                .setNegativeButton("cancel", (dialog12, id) ->
                        MonthYearPickerDialog.this.getDialog().cancel());
        builder.setTitle("Select a Month");
        return builder.create();
    }
}