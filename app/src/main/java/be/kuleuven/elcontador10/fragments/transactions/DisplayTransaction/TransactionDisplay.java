package be.kuleuven.elcontador10.fragments.transactions.DisplayTransaction;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.progressindicator.CircularProgressIndicator;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.tools.DateFormatter;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;

public class TransactionDisplay extends Fragment  {
    private MainActivity mainActivity;
    TextView concerning, registeredBy, idText ,account, amount, category,emojiCategory, date,time, notes;
    ProcessedTransaction selectedTrans;
    NavController navController;
    ConstraintLayout layoutAddPhotoIcon;
    CircularProgressIndicator progressIndicator;
    ImageView imViewPhotoIn;
    View view;
    ViewModel_DisplayTransaction viewModel;
    boolean isLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_DisplayTransaction.class);
        viewModel.reset();
        isLoading=false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.transaction_display));
        view=inflater.inflate(R.layout.fragment_transaction_display, container, false);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        try {
            TransactionDisplayArgs args = TransactionDisplayArgs.fromBundle(getArguments());
            initializeViews(view);
            displayInformation(args.getId());
        }
        catch (Exception e) {
            Toast.makeText(mainActivity, "Error Loading the information.", Toast.LENGTH_SHORT).show();
        }
        Button delete = requireView().findViewById(R.id.buttonDeleteTransaction);
        delete.setOnClickListener(this::onDelete_Clicked);
        imViewPhotoIn.setOnClickListener(v->navController.navigate(R.id.action_transactionDisplay_to_displayPhoto2));
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_delete,true);
        viewModel.getChosenBitMap().observe(getViewLifecycleOwner(), this::setImage);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(),this::setLoadingBar);
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.modifyVisibilityOfMenuItem(R.id.menu_delete,false);

    }

    private void setLoadingBar(Boolean aBoolean) {
        if (aBoolean){
            progressIndicator.setVisibility(View.VISIBLE);
            isLoading=true;
            layoutAddPhotoIcon.setVisibility(View.GONE);
        }
        else{
            progressIndicator.setVisibility(View.GONE);
            isLoading=false;
        }
    }

    private void setImage(Bitmap bitmap) {
        if(bitmap!=null) {
            setUiForPhoto(true);
            imViewPhotoIn.setImageBitmap(bitmap);
        }
        else {
            setUiForPhoto(false);

        }
    }


    private void setUiForPhoto(boolean photoDownloaded) {
        if(photoDownloaded){
            imViewPhotoIn.setVisibility(View.VISIBLE);
            layoutAddPhotoIcon.setVisibility(View.GONE);
        }else{
            imViewPhotoIn.setVisibility(View.GONE);
            layoutAddPhotoIcon.setVisibility(View.VISIBLE);
        }
    }

    public void initializeViews (View view) {
        amount = view.findViewById(R.id.textAmount);
        concerning = view.findViewById(R.id.textConcerningDisplay);
        account = view.findViewById(R.id.textAccountChosenDisplay);
        idText = view.findViewById(R.id.txtIdTransactionDISPLAY);
        emojiCategory = view.findViewById(R.id.txtCategoryIcon);
        category = view.findViewById(R.id.txtCategoryTitle);
        date = view.findViewById(R.id.txtDateDisplay);
        time = view.findViewById(R.id.txtTimeDisplay);
        registeredBy = view.findViewById(R.id.txtRegisteredByDisplay);
        notes = view.findViewById(R.id.txtNotesDisplay);
        notes.setMovementMethod(new ScrollingMovementMethod());
        layoutAddPhotoIcon = view.findViewById(R.id.layout_addPhoto);
        imViewPhotoIn = view.findViewById(R.id.image_transaction_photoDownloaded);
        progressIndicator = view.findViewById(R.id.progress_indicator_displayTrans);


    }


    private void onDelete_Clicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Delete transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes", (dialog, which) ->confirmDelete())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void confirmDelete(){
        navController.popBackStack();
        selectedTrans.deleteTransaction();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayInformation(String idOfTransaction) throws InterruptedException {
        selectedTrans = Caching.INSTANCE.getTransaction(idOfTransaction);
        if(selectedTrans.equals(null))Toast.makeText(getContext(),"error getting Transaction",Toast.LENGTH_SHORT);
        else {
            NumberFormatter formatter = new NumberFormatter(selectedTrans.getTotalAmount());
            DateFormatter dateFormatter = new DateFormatter(selectedTrans.getDueDate(),"f");
            DateFormatter timeFormatter = new DateFormatter(selectedTrans.getDueDate(),"t");
            amount.setText(formatter.getFinalNumber());
            String startPhrase=(formatter.isNegative())? getString(R.string.paid_to): getString(R.string.paid_by);
            String concerningText= startPhrase+" "+Caching.INSTANCE.getStakeholderName(selectedTrans.getIdOfStakeInt());
            concerning.setText(concerningText);
            account.setText(Caching.INSTANCE.getAccountName());
            idText.setText(selectedTrans.getIdOfTransactionInt());
            String emoji =Caching.INSTANCE.getCategoryEmoji(selectedTrans.getIdOfCategoryInt());
            if (emoji.length()==0){
                emojiCategory.setVisibility(View.GONE);
                category.setVisibility(View.GONE);
            }
            else emojiCategory.setText(emoji);
            category.setText(Caching.INSTANCE.getCategoryTitle(selectedTrans.getIdOfCategoryInt()));
            date.setText(dateFormatter.getFormattedDate());
            time.setText(timeFormatter.getFormattedDate());
            registeredBy.setText(selectedTrans.getRegisteredBy());
            notes.setText(selectedTrans.getNotes());
            if(selectedTrans.getImageName().length()>0){
                if(viewModel.getChosenBitMap().getValue()==null){
                    viewModel.selectBitMap(selectedTrans.getImageName(),requireContext());
                }
            }
            else setUiForPhoto(false);
        }
    }



}