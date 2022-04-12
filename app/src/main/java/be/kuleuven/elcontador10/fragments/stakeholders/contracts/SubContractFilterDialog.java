package be.kuleuven.elcontador10.fragments.stakeholders.contracts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import be.kuleuven.elcontador10.R;

public class SubContractFilterDialog extends DialogFragment {
    private final LifecycleOwner owner;
    private CheckBox late, future, completed, ignored;
    private SubContractViewModel viewModel;

    public SubContractFilterDialog(LifecycleOwner owner) {
        this.owner = owner;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // inflate view
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_filter_scheduled_transactions, null);

        // checkboxes
        late = view.findViewById(R.id.check_box_late);
        future = view.findViewById(R.id.check_box_future);
        completed = view.findViewById(R.id.check_box_completed);
        ignored = view.findViewById(R.id.check_box_ignored);

        // access view model
        viewModel = new ViewModelProvider(requireActivity()).get(SubContractViewModel.class);
        viewModel.getIsLate().observe(owner, this::updateLate);
        viewModel.getIsFuture().observe(owner, this::updateFuture);
        viewModel.getIsCompleted().observe(owner, this::updateCompleted);
        viewModel.getIsIgnored().observe(owner, this::updateIgnored);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setView(view)
                .setTitle("Displayed information")
                .setPositiveButton("Ok", ((dialogInterface, i) -> onConfirm()))
                .setNegativeButton("Cancel", ((dialogInterface, i) -> this.requireDialog().cancel()));

        return builder.create();
    }

    private void onConfirm() {
        viewModel.setIsLate(late.isChecked());
        viewModel.setIsFuture(future.isChecked());
        viewModel.setIsCompleted(completed.isChecked());
        viewModel.setIsIgnored(ignored.isChecked());

        viewModel.setFiltered();
    }

    private void updateLate(boolean late) {
        this.late.setChecked(late);
    }

    private void updateFuture(boolean future) {
        this.future.setChecked(future);
    }

    private void updateCompleted(boolean completed) {
        this.completed.setChecked(completed);
    }

    private void updateIgnored(boolean ignored) {
        this.ignored.setChecked(ignored);
    }
}
