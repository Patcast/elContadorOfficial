package be.kuleuven.elcontador10.fragments.microaccounts;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.contract.Contract;

public class NewContractDialog extends Dialog {
    private MainActivity activity;
    private Button confirm, cancel;
    private TextView title, notes;

    private Contract contract;
    private boolean editing;

    public NewContractDialog(MainActivity activity) {
        super(activity);
        this.activity = activity;
        this.editing = false;
    }

    public NewContractDialog(MainActivity activity, Contract contract) {
        super(activity);
        this.activity = activity;
        this.editing = true;
        this.contract = contract;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_contract_new);

        confirm = findViewById(R.id.contract_new_add);
        cancel = findViewById(R.id.contract_new_cancel);

        confirm.setOnClickListener(this::onConfirm_Clicked);
        cancel.setOnClickListener(this::onCancel_Clicked);

        title = findViewById(R.id.contract_new_title);
        notes = findViewById(R.id.contract_new_notes);

        if (editing) {
            title.setText(contract.getTitle());
            notes.setText(contract.getNotes());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onConfirm_Clicked(View view) {
        String title_text = title.getText().toString();

        if (!title_text.equals("")) {
            String notes_text = notes.getText().toString();
            if (editing) {
                contract.setTitle(title_text);
                contract.setNotes(notes_text);

                Toast.makeText(activity, "Contract edited", Toast.LENGTH_SHORT).show();
            } else {
                Contract newContract = new Contract(title_text, activity.returnSavedLoggedEmail(), notes_text);
                newContract.setMicroAccount(Caching.INSTANCE.getChosenMicroAccountId());
                Contract.newContract(newContract);

                Toast.makeText(activity, "New contract made", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        } else {
            // TODO change to resource string
            Toast.makeText(activity, "Missing title!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCancel_Clicked(View view) {
        dismiss();
    }
}
