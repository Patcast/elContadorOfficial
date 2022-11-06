package be.kuleuven.elcontador10.fragments.transactions.Categories;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.vdurmont.emoji.EmojiManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.kuleuven.elcontador10.MainActivity;
import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.Caching;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;

public class CategorySettings extends Fragment implements AdapterView.OnItemSelectedListener {

    private EmojiCategory editingEmoji;
    private EditText edTextName,edTextIcon;
    private TextView textWordCounter,textEmojiRequest;
    private Button  btnConfirm;
    private RadioButton radCashIn;
    private RadioGroup radioGroup;
    private NavController navController;
    private final List<String> trans_type = new ArrayList<>();
    private Spinner spinner;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialoge_category_editing, container,false);

        edTextName = view.findViewById(R.id.editText_category_Name);
        edTextIcon = view.findViewById(R.id.editText_category_Icon);
        textWordCounter = view.findViewById(R.id.text_categories_wordCounter);
        textEmojiRequest = view.findViewById(R.id.text_categories_verify_emoji);
        btnConfirm = view.findViewById(R.id.button_category_confirm);
        radCashIn = view.findViewById(R.id.radio_CashIn);
        radioGroup= view.findViewById(R.id.radioGroup);
        CategorySettingsArgs args = CategorySettingsArgs.fromBundle(getArguments());
        spinner =  view.findViewById(R.id.type_of_cash_transaction);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.type_of_cash_transaction,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        updateUIEditingMode(args.getIdOfCategory());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        setWordCounter();



        btnConfirm.setOnClickListener(v -> confirmCategory());
        setTopMenu();
    }
    private void setTopMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_three_buttons_menu, menu);

                if (editingEmoji != null ){
                    menu.findItem(R.id.menu_delete).setVisible(true);
                }
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_delete) {
                    deleteCategory();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void deleteCategory() {
        if (editingEmoji == null) navController.popBackStack();
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
        if (editingEmoji != null) EmojiCategory.deleteCategory(editingEmoji);
        navController.popBackStack();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUIEditingMode(String id) {
        MainActivity mainActivity = (MainActivity) requireActivity();
        if (id != null){
            editingEmoji = Caching.INSTANCE.getEmojiCategory(id);
            if (editingEmoji != null){
                edTextName.setText(editingEmoji.getTitle());
                edTextIcon.setText(editingEmoji.getIcon());
                if (!editingEmoji.isCashIn()){
                    radioGroup.check(R.id.radio_CashOut);
                } else
                    radioGroup.check(R.id.radio_CashIn);
                mainActivity.setHeaderText(getString(R.string.editing_custom_category));
                if(editingEmoji.getType().contains(Caching.INSTANCE.TYPE_PAYABLES))  spinner.setSelection(1);
                else if(editingEmoji.getType().contains(Caching.INSTANCE.TYPE_RECEIVABLES)) spinner.setSelection(2);
                else spinner.setSelection(0);

            }
        } else {
            mainActivity.setHeaderText(getString(R.string.add_new_cat));
        }
    }



    private void setWordCounter() {
        new MaxWordsCounter(20,edTextName,textWordCounter,getContext());
    }

    private void confirmCategory() {
        boolean isValid = true;
        String title = edTextName.getText().toString();
        String emoji = edTextIcon.getText().toString();
        if(title.length()==0){
            textWordCounter.setText(R.string.this_field_is_required);
            textWordCounter.setTextColor(getResources().getColor(R.color.light_red_warning));
            isValid = false;
        }
        if(emoji.length()==0) {
            textEmojiRequest.setVisibility(View.VISIBLE);
            textEmojiRequest.setText(R.string.this_field_is_required);
            isValid = false;
        }

        if (!EmojiManager.isEmoji(emoji)) {
            textEmojiRequest.setVisibility(View.VISIBLE);
            textEmojiRequest.setText(R.string.invalid_icon_please_enter_an_emoji);
            isValid = false;
        }

        if(isValid){
          makeCategory(title,emoji);
        }
    }
    public void makeCategory(String title, String emoji){
        EmojiCategory newEmoji = new EmojiCategory(emoji, title, radCashIn.isChecked(), trans_type);
        if (editingEmoji == null) {
            EmojiCategory.saveNewCategory(newEmoji);
        } else {
            newEmoji.setId(editingEmoji.getId());
            EmojiCategory.updateCategory(newEmoji);
            editingEmoji = null;
        }
        navController.popBackStack();
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            trans_type.clear();
            trans_type.addAll(Arrays.asList(Caching.INSTANCE.TYPE_CASH, null, null, null));
        } else if (i == 1) {
            trans_type.clear();
            trans_type.addAll(Arrays.asList(Caching.INSTANCE.TYPE_CASH, Caching.INSTANCE.TYPE_PAYABLES, null, null));
        } else {
            trans_type.clear();
            trans_type.addAll(Arrays.asList(Caching.INSTANCE.TYPE_CASH, null, Caching.INSTANCE.TYPE_RECEIVABLES, null));
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
