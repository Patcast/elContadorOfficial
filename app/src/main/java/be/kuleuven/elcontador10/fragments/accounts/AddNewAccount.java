package be.kuleuven.elcontador10.fragments.accounts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.kuleuven.elcontador10.R;
import be.kuleuven.elcontador10.activities.MainActivity;


public class AddNewAccount extends Fragment {

    MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity =(MainActivity) getActivity();
        mainActivity.displayTopMenu(false);
        mainActivity.setHeaderText(getString(R.string.add_account));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_account, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.displayTopMenu(true);
    }
}