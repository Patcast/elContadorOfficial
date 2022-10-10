package be.kuleuven.elcontador10.fragments.transactions.Categories;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vdurmont.emoji.EmojiManager;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;

public class CategoryDialog extends DialogFragment {

    private EmojiCategory editingEmoji;
    private EditText edTextName,edTextIcon;
    private TextView textWordCounter,textEmojiRequest,dialogTitle;
    private Button btnDelete, btnConfirm;
    private DialogCategoriesListener listener;

    public interface DialogCategoriesListener{
        void closeDialog();
    }

    public CategoryDialog() { }

    public CategoryDialog(EmojiCategory editingEmojiInput) {
        this.editingEmoji = editingEmojiInput;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialoge_category_editing, container);

        dialogTitle = view.findViewById(R.id.text_categoryDialog_title);
        edTextName = view.findViewById(R.id.editText_category_Name);
        edTextIcon = view.findViewById(R.id.editText_category_Icon);
        textWordCounter = view.findViewById(R.id.text_categories_wordCounter);
        textEmojiRequest = view.findViewById(R.id.text_categories_verify_emoji);
        btnDelete = view.findViewById(R.id.button_category_delete);
        btnConfirm = view.findViewById(R.id.button_category_confirm);

        updateUIEditingMode();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setWordCounter();
        btnConfirm.setOnClickListener(v -> confirmCategory());
        btnDelete.setOnClickListener(v -> deleteCategory());
    }

    private void deleteCategory() {
        if (editingEmoji == null) closeDialog();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.delete_category)
                    .setMessage(R.string.are_you_sure_delete_category)
                    .setPositiveButton(R.string.yes, (dialog, which) -> confirmDelete())
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    private void confirmDelete() {
        if (editingEmoji != null) editingEmoji.deleteCategory();
        closeDialog();
    }

    private void updateUIEditingMode() {
        if (editingEmoji != null){
            edTextName.setText(editingEmoji.getTitle());
            edTextIcon.setText(editingEmoji.getIcon());
            dialogTitle.setText(R.string.editing_custom_category);
        } else {
            dialogTitle.setText(R.string.add_new_cat);
            btnDelete.setText(R.string.dismiss);
        }
    }

    private void closeDialog(){
        if (listener != null){
            listener.closeDialog();
            setListener(null);
        }
        dismiss();
    }

    private void setWordCounter() {
        new MaxWordsCounter(20,edTextName,textWordCounter,getContext());
    }

    private void confirmCategory() {
        String title = edTextName.getText().toString();
        String emoji = edTextIcon.getText().toString();

        if(title.length()!=0){
            if(emoji.length()!=0){
                if (EmojiManager.isEmoji(emoji)){
                    if (editingEmoji == null) {
                        EmojiCategory newEmoji = new EmojiCategory(title,emoji);
                        newEmoji.saveNewCategory(newEmoji);
                    } else {
                        editingEmoji.setIcon(emoji);
                        editingEmoji.setTitle(title);
                        editingEmoji.updateCategory(editingEmoji);
                        editingEmoji=null;
                    }
                    closeDialog();
                } else {
                    textEmojiRequest.setVisibility(View.VISIBLE);
                    textEmojiRequest.setText(R.string.invalid_icon_please_enter_an_emoji);
                }
            } else {
                textEmojiRequest.setVisibility(View.VISIBLE);
                textEmojiRequest.setText(R.string.this_field_is_required);
            }
        } else{
            textWordCounter.setText(R.string.this_field_is_required);
            textWordCounter.setTextColor(getResources().getColor(R.color.light_red_warning));
        }
    }

    public void setListener(DialogCategoriesListener listener) {
        this.listener = listener;
    }
}
