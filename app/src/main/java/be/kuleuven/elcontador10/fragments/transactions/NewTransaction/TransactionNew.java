package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;

//Todo: Improvement of Categories and programming limit words for notes and title. Also remove mandatory Stakeholder.
public class TransactionNew extends Fragment {
    private static final String TAG = "TransactionNew";
    RadioGroup radGroup;
    EditText txtTitle;
    TextView txtWordsCounterTitle;
    ImageButton btnAddCategory;
    TextView txtEmojiCategory;
    EditText txtAmount;
    TextView txtStakeHolder;
    EditText txtNotes;
    TextView txtWordsCounterNotes;
    MainActivity mainActivity;
    NavController navController;
    NewTransactionViewModel viewModel;
    StakeHolder selectedStakeHolder;
    String idCatSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setHeaderText(getString(R.string.new_transaction_title));
        mainActivity.displayTopMenu(false);
        return inflater.inflate(R.layout.fragment_transaction_new, container, false);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(NewTransactionViewModel.class);
        navController = Navigation.findNavController(view);

///      Initialize views
        radGroup = view.findViewById(R.id.radioGroup);
        btnAddCategory = view.findViewById(R.id.imageButton_chooseCategory);
        txtEmojiCategory = view.findViewById(R.id.text_emoji_category);
        txtTitle = view.findViewById(R.id.text_newTransaction_title);
        txtWordsCounterTitle = view.findViewById(R.id.text_newTransaction_wordCounter);
        txtAmount = view.findViewById(R.id.ed_txt_amount);
        txtStakeHolder = view.findViewById(R.id.text_stakeholderSelected);
        txtNotes = view.findViewById(R.id.ed_txt_notes);
        txtWordsCounterNotes = view.findViewById(R.id.text_newTransaction_wordCounter_notes);
        Button confirmButton = view.findViewById(R.id.btn_confirm_NewTransaction);
        // listeners
        txtStakeHolder.setOnClickListener(v -> { navController.navigate(R.id.action_newTransaction_to_chooseStakeHolderDialog); });
        confirmButton.setOnClickListener(v -> confirmTransaction());
        btnAddCategory.setOnClickListener(v-> navController.navigate(R.id.action_newTransaction_to_chooseCategory));
        txtEmojiCategory.setOnClickListener(v-> navController.navigate(R.id.action_newTransaction_to_chooseCategory));
        setWordCounters();
    }

    private void setWordCounters() {
        new MaxWordsCounter(30,txtTitle,txtWordsCounterTitle,getContext());
        new MaxWordsCounter(100,txtNotes,txtWordsCounterNotes,getContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenStakeholder().observe(getViewLifecycleOwner(), this::setStakeChosenText);
        viewModel.getChosenCategory().observe(getViewLifecycleOwner(), this::setCategoryChosen);
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.reset();
        viewModel.resetCategory();
    }
    private void setCategoryChosen(EmojiCategory emojiCategory){
        if(emojiCategory==null){
            txtEmojiCategory.setVisibility(View.GONE);
            btnAddCategory.setVisibility(View.VISIBLE);
        }
        else{
            if(emojiCategory.getIcon()!=null){
                idCatSelected= emojiCategory.getId();
                btnAddCategory.setVisibility(View.GONE);
                txtEmojiCategory.setVisibility(View.VISIBLE);
                txtEmojiCategory.setText(emojiCategory.getIcon());
            }
        }
    }
    private void setStakeChosenText(StakeHolder stakeHolder) {
        if(stakeHolder!=null){
            txtStakeHolder.setText(stakeHolder.getName());
            selectedStakeHolder = stakeHolder;
        }
        else{
            txtStakeHolder.setText(R.string.select_an_stakeholder);
        }
    }
    private void confirmTransaction(){

        String amount = txtAmount.getText().toString() ;
        if ( amount.isEmpty()||Integer.parseInt(amount) == 0) {
            Toast.makeText(getActivity(), R.string.zero_amount, Toast.LENGTH_LONG).show();
        }
        else{
            if (selectedStakeHolder==null ) {
                Toast.makeText(getActivity(), R.string.select_an_stakeholder, Toast.LENGTH_LONG).show();
            }
            else{
                navController.popBackStack();
                makeNewTrans();
            }
        }
    }

    private void makeNewTrans(){
        boolean cashOut = radGroup.getCheckedRadioButtonId() == R.id.radio_CashOut;
        String title = txtTitle.getText().toString();
        int amount = Integer.parseInt(txtAmount.getText().toString());
        if(cashOut) amount = amount*-1;
        String idCatFinal=(idCatSelected!=null)?idCatSelected: " ";
        String notes = txtNotes.getText().toString();
        Transaction newTrans= new Transaction(title,amount, mainActivity.returnSavedLoggedEmail(), selectedStakeHolder.getId(),idCatFinal,notes);
        newTrans.SendTransaction(newTrans);
    }








}