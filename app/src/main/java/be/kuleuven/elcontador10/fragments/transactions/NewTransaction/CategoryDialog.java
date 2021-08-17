package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vanniktech.emoji.EmojiUtils;

import java.util.regex.Matcher;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;

public class CategoryDialog extends DialogFragment {
    EditText edTextName,edTextIcon;
    TextView textWordCounter,textEmojiRequest;
    Button btnDelete,btnConfirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialoge_category_editing,null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edTextName = view.findViewById(R.id.editText_category_Name);
        edTextIcon = view.findViewById(R.id.editText_category_Icon);
        textWordCounter = view.findViewById(R.id.text_categories_wordCounter);
        textEmojiRequest = view.findViewById(R.id.text_categories_verify_emoji);
        btnDelete = view.findViewById(R.id.button_category_delete);
        btnConfirm = view.findViewById(R.id.button_category_confirm);
        setWordCounter();

        btnConfirm.setOnClickListener(v -> confirmCategory());
    }
    private void setWordCounter() {
        new MaxWordsCounter(20,edTextName,textWordCounter,getContext());
    }
    private void confirmCategory() {
        String title = edTextName.getText().toString();
        String emoji = edTextIcon.getText().toString();
        if (EmojiUtils.isOnlyEmojis(emoji)) {
            EmojiCategory newEmoji = new EmojiCategory(title,emoji);
            Toast.makeText(getContext(),"emoji",Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(getContext(),"No emoji",Toast.LENGTH_SHORT).show();
       /* String htmlifiedText = EmojiUtils.isOnlyEmojis(emoji);
// regex to identify html entitities in htmlified text
        Matcher matcher = htmlEntityPattern.matcher(htmlifiedText);

        while (matcher.find()) {
            String emojiCode = matcher.group();
            if (isEmoji(emojiCode)) {

                emojis.add(EmojiUtils.getEmoji(emojiCode).getEmoji());
            }
        }*/
    }




}
