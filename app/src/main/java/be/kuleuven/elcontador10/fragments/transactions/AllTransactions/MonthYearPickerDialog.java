package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.MonthlyRecords;

public class MonthYearPickerDialog extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private final int month, year;
    private final List<MonthlyRecords> listOfRecords ;
    private final List<String> monthList = new ArrayList<>();
    List<String> monthListSelected = new ArrayList<>();
    int oldSpan = 0;


    public MonthYearPickerDialog(int month, int year, List<MonthlyRecords> listOfRecords) {
        this.month = month;
        this.year = year;
        this.listOfRecords = listOfRecords;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.month_year_picker, null);
        final NumberPicker monthPicker = dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = dialog.findViewById(R.id.picker_year);
        monthList.clear();
        monthList.addAll(Arrays.asList(getResources().getStringArray(R.array.months_list)));

        monthPicker.setMinValue(0);
        setMonthNumberPicker(getMinMonth(year),getMaxMonth(year),monthPicker);
        yearPicker.setMinValue(getMinYear());
        yearPicker.setMaxValue(getMaxYear());
        yearPicker.setValue(year);
        yearPicker.setOnValueChangedListener((numberPicker, oldVal, newVal) -> {
            setMonthNumberPicker(getMinMonth(newVal),getMaxMonth(newVal),monthPicker);
        });

        builder.setView(dialog)
                // Add buttons
                .setPositiveButton(R.string.ok, (dialog1, id) ->
                        listener.onDateSet(null, yearPicker.getValue(), getTrueMonth(monthPicker.getValue()), 0))
                .setNegativeButton(R.string.cancel, (dialog12, id) ->
                        MonthYearPickerDialog.this.getDialog().cancel());
        builder.setTitle(R.string.select_month);
        return builder.create();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getMaxYear(){
        Optional<MonthlyRecords> monthlyRecordsOptional = listOfRecords.stream().max(Comparator.comparing(MonthlyRecords::getDate));
        return monthlyRecordsOptional.map(monthlyRecords -> monthlyRecords.getDate().get(Calendar.YEAR)).orElse(0);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getMinYear(){
        Optional<MonthlyRecords> monthlyRecordsOptional = listOfRecords.stream().min(Comparator.comparing(MonthlyRecords::getDate));
        return monthlyRecordsOptional.map(monthlyRecords -> monthlyRecords.getDate().get(Calendar.YEAR)).orElse(0);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getMinMonth(int selectedYear){
        Optional<MonthlyRecords> monthlyRecordsOptional = listOfRecords.stream().filter(r->r.getDate().get(Calendar.YEAR)==selectedYear).min(Comparator.comparing(MonthlyRecords::getDate));
        return monthlyRecordsOptional.map(monthlyRecords -> monthlyRecords.getDate().get(Calendar.MONTH)+1).orElse(0);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getMaxMonth(int selectedYear){
        Optional<MonthlyRecords> monthlyRecordsOptional = listOfRecords.stream().filter(r->r.getDate().get(Calendar.YEAR)==selectedYear).max(Comparator.comparing(MonthlyRecords::getDate));
        return monthlyRecordsOptional.map(monthlyRecords -> monthlyRecords.getDate().get(Calendar.MONTH)+1).orElse(0);
    }
    private void setMonthNumberPicker(int minMonth, int maxMonth, NumberPicker numberPicker){
        monthListSelected.clear();
        monthListSelected.addAll(monthList.subList(minMonth-1,maxMonth));
        String[] myArray = new String[monthListSelected.size()];
        monthListSelected.toArray(myArray);
        int newSpan =myArray.length;
        if(oldSpan == 0){
            numberPicker.setMaxValue(newSpan-1);
            numberPicker.setDisplayedValues(myArray);
            oldSpan = newSpan;
            numberPicker.setValue(monthListSelected.indexOf(monthList.get(month)));
        }else if(newSpan > oldSpan){
            numberPicker.setDisplayedValues(myArray);
            numberPicker.setMaxValue(newSpan-1);
            oldSpan = newSpan;
        }else{
            numberPicker.setMaxValue(newSpan-1);
            numberPicker.setDisplayedValues(myArray);
            oldSpan = newSpan;
        }
    }
    private int getTrueMonth(int monthPickerValue){

        if(monthPickerValue>=monthListSelected.size()) return 0;
        else return monthList.indexOf(monthListSelected.get(monthPickerValue));
    }

}