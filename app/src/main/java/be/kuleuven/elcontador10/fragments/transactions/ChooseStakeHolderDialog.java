package be.kuleuven.elcontador10.fragments.transactions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.background.adapters.StakeHolderRecViewAdapter;
import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.StakeHolder;


public class ChooseStakeHolderDialog extends Fragment {
    private static final String TAG = "ChooseStakeHolderDialog";
    public OnStakeHolderSelected receiverFragment;

    public interface OnStakeHolderSelected{
        void sendInput(StakeHolder input);
    }

    //Dialog myDialog = getDialog();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_stake_holder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerStakeHolds = view.findViewById(R.id.recyclerViewChooseStake);
        StakeHolderRecViewAdapter adapter = new StakeHolderRecViewAdapter(view,receiverFragment);
        ArrayList<StakeHolder> stakeHolders = new ArrayList<>(Caching.INSTANCE.getStakeHolders());
        adapter.setStakeholdersList(stakeHolders);
        recyclerStakeHolds.setAdapter(adapter);
        recyclerStakeHolds.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try{
            receiverFragment= (OnStakeHolderSelected) requireActivity().getSupportFragmentManager().findFragmentById(R.id.newTransaction) ;
        }
        catch (ClassCastException e){
            Log.e(TAG,"onAttach: CastExeption: "+ e.getMessage());
        }

    }
}