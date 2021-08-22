package be.kuleuven.elcontador10.fragments.transactions.DisplayTransaction;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.tools.DateFormatter;
import be.kuleuven.elcontador10.background.tools.NumberFormatter;
import be.kuleuven.elcontador10.background.model.Transaction;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class TransactionDisplay extends Fragment  {
    private MainActivity mainActivity;
    TextView concerning, registeredBy, idText ,account, amount, category,emojiCategory, date,time, notes;
    Transaction selectedTrans;
    NavController navController;
    ConstraintLayout layoutAddPhotoIcon;
    LinearLayout layoutDownloadPhotoHolder;
    ImageView imViewPhotoIn;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ViewModel_DisplayTransaction viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.transaction_display));
        return inflater.inflate(R.layout.fragment_transaction_display, container, false);
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
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_DisplayTransaction.class);
        Button delete = requireView().findViewById(R.id.buttonDeleteTransaction);
        delete.setOnClickListener(this::onDelete_Clicked);
        layoutDownloadPhotoHolder.setOnClickListener(v->navController.navigate(R.id.action_transactionDisplay_to_displayPhoto2));
    }

    private void openPhoto(String imageName) {
        StringBuilder downloadUrl = new StringBuilder();
        downloadUrl.append(Caching.INSTANCE.getChosenAccountId());
        downloadUrl.append("/");
        downloadUrl.append(imageName);
        storage.getReference().child(downloadUrl.toString()).getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(imViewPhotoIn);
            setUiForPhoto(true);
            viewModel.selectUri(uri);
        }).addOnFailureListener(exception -> {
            Toast.makeText(getContext(), "Error loading photo.", Toast.LENGTH_SHORT).show();
            setUiForPhoto(false);
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.reset();
    }

    private void setUiForPhoto(boolean photoDownloaded) {
        if(photoDownloaded){
            layoutDownloadPhotoHolder.setVisibility(View.VISIBLE);
            layoutAddPhotoIcon.setVisibility(View.GONE);
        }else{
            layoutDownloadPhotoHolder.setVisibility(View.GONE);
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
        layoutDownloadPhotoHolder = view.findViewById(R.id.layout_photoDownloaded_holder);
        imViewPhotoIn = view.findViewById(R.id.image_transaction_photoDownloaded);

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
    private void displayInformation(String idOfTransaction) {
        selectedTrans = Caching.INSTANCE.getTransaction(idOfTransaction);
        if(selectedTrans.equals(null))Toast.makeText(getContext(),"error getting Transaction",Toast.LENGTH_SHORT);
        else {
            NumberFormatter formatter = new NumberFormatter(selectedTrans.getAmount());
            DateFormatter dateFormatter = new DateFormatter(selectedTrans.getDate(),"f");
            DateFormatter timeFormatter = new DateFormatter(selectedTrans.getDate(),"t");

            amount.setText(formatter.getFinalNumber());
            String startPhrase=(formatter.isNegative())? getString(R.string.paid_to): getString(R.string.paid_by);
            String concerningText= startPhrase+" "+Caching.INSTANCE.getStakeholderName(selectedTrans.getStakeHolder());
            concerning.setText(concerningText);
            account.setText(Caching.INSTANCE.getAccountName());
            idText.setText(selectedTrans.getId());
            String emoji =Caching.INSTANCE.getCategoryEmoji(selectedTrans.getCategory());
            if (emoji.length()==0){
                emojiCategory.setVisibility(View.GONE);
                category.setVisibility(View.GONE);
            }
            else emojiCategory.setText(emoji);
            category.setText(Caching.INSTANCE.getCategoryTitle(selectedTrans.getCategory()));
            date.setText(dateFormatter.getFormattedDate());
            time.setText(timeFormatter.getFormattedDate());
            registeredBy.setText(selectedTrans.getRegisteredBy());
            notes.setText(selectedTrans.getNotes());
            if(selectedTrans.getImageName().length()>0)openPhoto(selectedTrans.getImageName());
            else setUiForPhoto(false);

        }
    }


}