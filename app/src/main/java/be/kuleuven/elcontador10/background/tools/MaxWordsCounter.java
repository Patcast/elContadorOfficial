package be.kuleuven.elcontador10.background.tools;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import be.kuleuven.elcontador10.R;

public class MaxWordsCounter {

    public MaxWordsCounter(int maxWord, EditText inputEdText, TextView wordCounterText,Context inputContext) {
        setWordCounter(maxWord, inputEdText, wordCounterText, inputContext);
        int count = inputEdText.getText().toString().length();
        String init = count + "/" + maxWord;
        wordCounterText.setText(init);
    }

    private void setWordCounter(int maxWords,EditText writingEdText, TextView displayText, Context context){
        writingEdText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = writingEdText.getText().toString();
                writingEdText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxWords) });
                String counter = text.length()+"/"+ maxWords;
                displayText.setText(counter);
                if (text.length() >= maxWords){
                    displayText.setTextColor(context.getResources().getColor(R.color.light_red_warning));
                } else{
                    displayText.setTextColor(context.getResources().getColor(R.color.light_grey));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


}
