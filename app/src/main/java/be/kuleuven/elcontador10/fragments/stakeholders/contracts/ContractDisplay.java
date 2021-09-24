package be.kuleuven.elcontador10.fragments.stakeholders.contracts;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.adapters.SubContractsRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.contract.Contract;
import be.kuleuven.elcontador10.background.model.contract.SubContract;
import be.kuleuven.elcontador10.background.tools.DateFormatter;

public class ContractDisplay extends Fragment implements Caching.MicroAccountContractObserver {
    // views
    private TextView title, microAccountName, accountName, registeredBy, registeredDate, notes;
    private RecyclerView paymentsView;
    private FloatingActionButton add_btn, edit_btn, delete_btn;

    // adapters
    private SubContractsRecViewAdapter adapter;

    // variables
    private List<SubContract> subContractList;
    private Contract contract;
    private String contractId;
    private MainActivity mainActivity;
    private NavController navController;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contract_display, container, false);

        // set up variables
        subContractList = new ArrayList<>();
        mainActivity = (MainActivity) getActivity();

        // find views
        title = view.findViewById(R.id.contract_display_title);
        microAccountName = view.findViewById(R.id.contract_microaccount);
        accountName = view.findViewById(R.id.contract_account);
        registeredBy = view.findViewById(R.id.contract_registeredBy);
        registeredDate = view.findViewById(R.id.contract_registeredDate);
        notes = view.findViewById(R.id.contract_notes);

        paymentsView = view.findViewById(R.id.contract_payments_recView);
        paymentsView.setLayoutManager(new LinearLayoutManager(getContext()));

        add_btn = view.findViewById(R.id.contract_btn_add);
        edit_btn = view.findViewById(R.id.contract_btn_edit);
        delete_btn = view.findViewById(R.id.contract_btn_delete);

        // set onClickListeners
        add_btn.setOnClickListener(this::onAdd_Clicked);
        edit_btn.setOnClickListener(this::onEdit_Clicked);
        delete_btn.setOnClickListener(this::onDelete_Clicked);

        // adapters
        adapter = new SubContractsRecViewAdapter(view, getContext(), this);
        paymentsView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get argument and attach to caching
        contractId = ContractDisplayArgs.fromBundle(getArguments()).getContractId();
        Caching.INSTANCE.attachMicroContractObserver(this);

        // set activity header
        mainActivity.displayToolBar(true);
        mainActivity.displayTabLayout(false);

        navController = Navigation.findNavController(view);
    }

    @Override
    public void onStop() {
        super.onStop();
        Caching.INSTANCE.deAttachMicroContractObserver(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void notifyMicroAccountContractsObserver(List<Contract> contracts) {
        contract = Caching.INSTANCE.getContractFromId(contractId);
        if (contract != null) {
            Caching.INSTANCE.setChosenContract(contract);
            String name = Caching.INSTANCE.getStakeholderName(contract.getMicroAccount());

            // set up header
            mainActivity.setHeaderText(name + " - " + contract.getTitle());

            // set up texts
            title.setText(contract.getTitle());
            microAccountName.setText(name);
            accountName.setText(Caching.INSTANCE.getAccountName());
            registeredBy.setText(contract.getRegisteredBy());

            String date = new DateFormatter(contract.getRegisterDate(), "f").getFormattedDate();
            registeredDate.setText(date);
            notes.setText(contract.getNotes());

            // set up recycler view
            subContractList = contract.getSubContracts();
            adapter.setPayments(subContractList);
        } else Toast.makeText(getContext(), "Error getting contract id = " + contractId, Toast.LENGTH_LONG).show();
    }

    // TODO make buttons go somewhere
    public void onAdd_Clicked(View view) {
        ContractDisplayDirections.ActionContractDisplayToContractNewPayment action =
                ContractDisplayDirections.actionContractDisplayToContractNewPayment(contractId);
        navController.navigate(action);
    }

    public void onEdit_Clicked(View view) {
        NewContractDialog dialog = new NewContractDialog((MainActivity) getActivity(), contract);
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onDelete_Clicked(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);

        //TODO use string resources
        dialog.setTitle("Delete contract")
                .setMessage("Are you sure about deleting contract " + contract.getTitle() + " from micro account "
                    + Caching.INSTANCE.getStakeholderName(contract.getMicroAccount()) + "?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    Contract.deleteContract(contract);
                    Toast.makeText(mainActivity, "Contract deleted", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                    navController.popBackStack();
                }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .create().show();
    }
}