package be.kuleuven.elcontador10.fragments.property;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;


import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.tools.MaxWordsCounter;
import be.kuleuven.elcontador10.fragments.transactions.NewTransaction.ViewModel_NewTransaction;

public class AddProperty extends Fragment {
    private NavController navController;
    private EditText inputName;
    private TextView txtStakeHolder,txtWordsCounterTitle;
    StakeHolder selectedStakeHolder;
    ViewModel_NewTransaction viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_property, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        inputName = view.findViewById(R.id.ed_txt_name);
        txtWordsCounterTitle = view.findViewById(R.id.text_newTransaction_wordCounter);
        txtStakeHolder = view.findViewById(R.id.text_stakeholderSelected);
        txtStakeHolder.setOnClickListener(v -> { navController.navigate(R.id.action_addProperty_to_chooseStakeHolderDialog); });
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel_NewTransaction.class);

        new MaxWordsCounter(30,inputName,txtWordsCounterTitle,getContext());
        Button confirm = view.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(this::onConfirm_Clicked);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setHeaderText(getString(R.string.add_new_property));
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getChosenStakeholder().observe(getViewLifecycleOwner(), this::setStakeChosenText);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.reset();
    }

    public void onConfirm_Clicked(View view) {
        String name = inputName.getText().toString();

        if (name.isEmpty()) {
            txtWordsCounterTitle.setText(R.string.warning_new_properties);
            txtWordsCounterTitle.setTextColor(ResourcesCompat.getColor(getResources(),R.color.light_red_warning,null));
        }
        else {
            navController.popBackStack();
            Property newProperty = (selectedStakeHolder==null)? new Property(name,""):new Property(name, selectedStakeHolder.getId());
            newProperty.addProperty(newProperty);
        }
    }
    private void setStakeChosenText(StakeHolder stakeHolder) {
        if(stakeHolder!=null){
            txtStakeHolder.setText(stakeHolder.getName());
            selectedStakeHolder = stakeHolder;
        }
        else{
            txtStakeHolder.setText(R.string.none);
        }
    }


}
